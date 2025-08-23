package com.pagamento.pix.controller;

import com.pagamento.pix.application.dto.*;
import com.pagamento.pix.application.mapper.PixMapper;
import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.domain.ports.in.PixServicePort;
import com.pagamento.pix.exception.PixNotFoundException;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/pix")
@Validated
@Tag(name = "PIX Controller", description = "Endpoints para gerenciamento de transações PIX")
public class PixController {

    private static final Logger logger = LoggerFactory.getLogger(PixController.class);
    
    private final PixServicePort pixServicePort;
    private final PixMapper pixMapper;

    public PixController(PixServicePort pixServicePort, PixMapper pixMapper) {
        this.pixServicePort = pixServicePort;
        this.pixMapper = pixMapper;
    }

    // Endpoint para criar transação PIX
    @Operation(summary = "Cria uma nova transação PIX", 
               description = "Processa uma transação PIX após validar os dados de entrada e as regras de negócio")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transação PIX processada com sucesso",
                     content = @Content(schema = @Schema(implementation = PixResponseDTO.class),
                     examples = @ExampleObject(value = """
                         {
                           "id": "PIX-12345",
                           "chaveDestino": "destino@email.com",
                           "valor": 150.99,
                           "dataTransacao": "2023-10-25T15:30:45.123",
                           "status": "PROCESSADO",
                           "bacenId": "BID-54321",
                           "nomeRecebedor": "Empresa XYZ"
                         }
                         """))),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "422", description = "Regras de negócio violadas"),
        @ApiResponse(responseCode = "429", description = "Limite de requisições excedido"),
        @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping
    @RateLimiter(name = "pixCreationRateLimit")
    @Audited
    public ResponseEntity<PixResponseDTO> createPix(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados da transação PIX",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = PixRequestDTO.class),
                    examples = @ExampleObject(value = """
                        {
                          "solicitacaoId": "SOL-12345",
                          "chaveDestino": "destino@email.com",
                          "valor": 150.99,
                          "tipo": "EMAIL",
                          "documentoPagador": "12345678909",
                          "nomePagador": "João Silva"
                        }
                        """)
                )
            )
            @Valid @RequestBody PixRequestDTO request) {
        
        final String transactionId = request.getSolicitacaoId() != null ? 
                                    request.getSolicitacaoId() : 
                                    "REQ-" + System.currentTimeMillis();
        
        logger.info("[ID: {}] Recebendo solicitação PIX", transactionId);
        
        // Mapear para domínio e validar
        Pix pix = pixMapper.toDomain(request).toBuilder().id(transactionId).build();
        
        // Processar transação
        Pix processedPix = pixServicePort.processarPix(pix);
        PixResponseDTO response = PixMapper.toResponseDTO(processedPix);
        
        logger.info("[ID: {}] Transação PIX processada. Status: {}", 
                   transactionId, response.getStatus());
        
        return ResponseEntity.ok(response);
    }

    // Endpoint para estornar transação
    @Operation(summary = "Estornar uma transação PIX", 
               description = "Inicia o processo de estorno para uma transação PIX previamente processada")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estorno iniciado com sucesso",
                     content = @Content(schema = @Schema(implementation = PixResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Transação não encontrada"),
        @ApiResponse(responseCode = "422", description = "Estorno não permitido para o status atual"),
        @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping("/estorno")
    @RateLimiter(name = "pixRefundRateLimit")
    @Audited
    public ResponseEntity<PixResponseDTO> estornarPix(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados para estorno",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = EstornoRequestDTO.class),
                    examples = @ExampleObject(value = """
                        {
                          "pixId": "PIX-12345",
                          "motivo": "Pagamento duplicado"
                        }
                        """)
                )
            )
            @Valid @RequestBody EstornoRequestDTO request) {
        
        final String logId = "EST-" + (request.getPixId() != null ? request.getPixId() : System.currentTimeMillis());
        logger.info("[ID: {}] Recebendo solicitação de estorno", logId);
        
        Pix pix = pixServicePort.obterPorId(request.getPixId());
        
        if (pix == null) {
            logger.warn("[ID: {}] Transação não encontrada: {}", logId, request.getPixId());
            throw new PixNotFoundException("Transação PIX não encontrada com ID: " + request.getPixId());
        }
        
        pixServicePort.estornarPix(pix, request.getMotivo());
        PixResponseDTO response = PixMapper.toResponseDTO(pix);
        
        logger.info("[ID: {}] Estorno iniciado para transação: {}", logId, request.getPixId());
        return ResponseEntity.ok(response);
    }

    // Endpoint para consultar transação
    @Operation(summary = "Consultar transação PIX", 
               description = "Obtém os detalhes de uma transação PIX pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transação encontrada",
                     content = @Content(schema = @Schema(implementation = PixResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Transação não encontrada"),
        @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PixResponseDTO> consultarPix(
            @Parameter(description = "ID da transação PIX", example = "PIX-12345")
            @PathVariable String id) {
        
        logger.info("[CONSULTA] Buscando transação: {}", id);
        
        Pix pix = pixServicePort.obterPorId(id);
        
        if (pix == null) {
            logger.warn("[CONSULTA] Transação não encontrada: {}", id);
            throw new PixNotFoundException("Transação PIX não encontrada com ID: " + id);
        }
        
        PixResponseDTO response = PixMapper.toResponseDTO(pix);
        logger.info("[CONSULTA] Transação encontrada: {}", id);
        return ResponseEntity.ok(response);
    }

    // Endpoint para pesquisa avançada
    @Operation(summary = "Pesquisar transações PIX", 
               description = "Busca transações com filtros avançados e paginação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transações encontradas",
                     content = @Content(schema = @Schema(implementation = PixSearchResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping
    public ResponseEntity<PixSearchResponseDTO> pesquisarPix(
            @Parameter(description = "CPF/CNPJ do pagador", example = "12345678909")
            @RequestParam(required = false) String documentoPagador,
            
            @Parameter(description = "Chave PIX de destino", example = "destino@email.com")
            @RequestParam(required = false) String chaveDestino,
            
            @Parameter(description = "Status da transação", example = "PROCESSADO")
            @RequestParam(required = false) String status,
            
            @Parameter(description = "Data inicial (yyyy-MM-dd)", example = "2023-10-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            
            @Parameter(description = "Data final (yyyy-MM-dd)", example = "2023-10-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            
            @Parameter(hidden = true) Pageable pageable) {
        
        logger.info("[PESQUISA] Iniciando pesquisa com filtros");
        
        Page<Pix> result = pixServicePort.pesquisar(
            documentoPagador, 
            chaveDestino, 
            status, 
            dataInicio, 
            dataFim, 
            pageable
        );
        
        PixSearchResponseDTO response = new PixSearchResponseDTO(
            result.getContent().stream().map(PixMapper::toResponseDTO).toList(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements()
        );
        
        logger.info("[PESQUISA] {} transações encontradas", response.getTotalElements());
        return ResponseEntity.ok(response);
    }
}


