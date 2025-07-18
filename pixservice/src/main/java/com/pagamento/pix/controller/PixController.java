package com.pagamento.pix.controller;

import com.pagamento.pix.application.dto.*;
import com.pagamento.pix.application.mapper.PixMapper;
import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.service.PixService;
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
import org.springframework.http.HttpStatus;
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
    
    private final PixService pixService;
    private final PixMapper pixMapper;

    public PixController(PixService pixService, PixMapper pixMapper) {
        this.pixService = pixService;
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
    @RateLimiter(name = "pixCreationRateLimit") // Integração com Resilience4j
    @Audited(action = Audited.Action.CREATE) // Auditoria com Spring Data Envers
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
        
        try {
            // Mapear para domínio e validar
            Pix pix = pixMapper.toDomain(request);
            pix.setId(transactionId);
            
            if (!pix.isValid()) {
                logger.warn("[ID: {}] Dados do PIX inválidos", transactionId);
                return ResponseEntity.unprocessableEntity().build();
            }
            
            // Processar transação
            Pix processedPix = pixService.processarPix(pix);
            PixResponseDTO response = pixMapper.toResponseDTO(processedPix);
            
            logger.info("[ID: {}] Transação PIX processada. Status: {}", 
                       transactionId, response.getStatus());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("[ID: {}] Requisição inválida: {}", transactionId, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("[ID: {}] Erro ao processar PIX: {}", transactionId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
    @Audited(action = Audited.Action.UPDATE)
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
        
        try {
            Pix pix = pixService.obterPorId(request.getPixId());
            
            if (pix == null) {
                logger.warn("[ID: {}] Transação não encontrada: {}", logId, request.getPixId());
                return ResponseEntity.notFound().build();
            }
            
            pixService.estornarPix(pix, request.getMotivo());
            PixResponseDTO response = pixMapper.toResponseDTO(pix);
            
            logger.info("[ID: {}] Estorno iniciado para transação: {}", logId, request.getPixId());
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            logger.warn("[ID: {}] Estorno não permitido: {}", logId, e.getMessage());
            return ResponseEntity.unprocessableEntity().build();
        } catch (Exception e) {
            logger.error("[ID: {}] Erro ao iniciar estorno: {}", logId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
        
        try {
            Pix pix = pixService.obterPorId(id);
            
            if (pix == null) {
                logger.warn("[CONSULTA] Transação não encontrada: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            PixResponseDTO response = pixMapper.toResponseDTO(pix);
            logger.info("[CONSULTA] Transação encontrada: {}", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("[CONSULTA] Erro ao buscar transação {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
        
        try {
            Page<Pix> result = pixService.pesquisar(
                documentoPagador, 
                chaveDestino, 
                status, 
                dataInicio, 
                dataFim, 
                pageable
            );
            
            Page<PixResponseDTO> dtoPage = result.map(pixMapper::toResponseDTO);
            PixSearchResponseDTO response = new PixSearchResponseDTO(
                dtoPage.getContent(),
                dtoPage.getNumber(),
                dtoPage.getSize(),
                dtoPage.getTotalElements()
            );
            
            logger.info("[PESQUISA] {} transações encontradas", response.getTotalElements());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("[PESQUISA] Erro na pesquisa: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}