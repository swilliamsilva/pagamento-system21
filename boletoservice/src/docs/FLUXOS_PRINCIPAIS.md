##
# Executar com perfil JPA (padrão)
mvn spring-boot:run

# Executar com perfil MongoDB
mvn spring-boot:run -Dspring-boot.run.profiles=mongo

# Executar com configuração de produção
mvn spring-boot:run -Dspring-boot.run.profiles=prod


## Fluxos Principais do Boleto Service

### 1. Emissão de Boleto
1. Recebe DTO com dados do boleto
2. Valida dados obrigatórios e regras de negócio
3. Gera códigos (barras, linha digitável, QR Code)
4. Persiste boleto com status EMITIDO
5. Retorna boleto emitido

### 2. Pagamento de Boleto
1. Recebe notificação de pagamento do gateway
2. Busca boleto por ID
3. Valida se boleto está apto para pagamento
4. Atualiza data de pagamento e status para PAGO
5. Envia notificação de confirmação

### 3. Cancelamento de Boleto
1. Recebe solicitação de cancelamento
2. Busca boleto por ID
3. Valida se boleto pode ser cancelado
4. Atualiza status para CANCELADO
5. Persiste motivo de cancelamento

### 4. Reemissão de Boleto
1. Recebe solicitação de reemissão
2. Busca boleto original por ID
3. Valida se boleto pode ser reemitido
4. Gera novo boleto com novos códigos e datas
5. Atualiza contador de reemissoes do original
6. Persiste novo boleto com status EMITIDO
7. Relaciona novo boleto com o original

### Estados do Boleto
```mermaid
stateDiagram-v2
    [*] --> EMITIDO
    EMITIDO --> PAGO: Pagamento confirmado
    EMITIDO --> VENCIDO: Após data de vencimento
    EMITIDO --> CANCELADO: Cancelamento solicitado
    EMITIDO --> REEMITIDO: Solicitação de reemissão
    VENCIDO --> REEMITIDO: Solicitação de reemissão
    REEMITIDO --> EMITIDO: Novo boleto emitido
    
    
## Fluxograma
sequenceDiagram
    participant Cliente
    participant Controller
    participant Service
    participant Factory
    participant Repository
    Cliente->>Controller: POST /boletos (BoletoRequestDTO)
    Controller->>Service: emitirBoleto(dto)
    Service->>Factory: criarBoleto(dto)
    Factory->>Service: Boleto
    Service->>Repository: salvar(boleto)
    Repository-->>Service: Boleto salvo
    Service-->>Controller: BoletoResponseDTO
    Controller-->>Cliente: 201 Created
    
###
    
### Implementação de Geração de Códigos

**BoletoCalculos.java**:
```java
package com.pagamento.boleto.domain.service;

import com.pagamento.boleto.domain.model.Boleto;
import org.springframework.stereotype.Component;

@Component
public class BoletoCalculos {
    
    public String gerarCodigoBarras(Boleto boleto) {
        // Implementação real usando algoritmo de geração de código de barras
        return "34191.11111 11111.111111 11111.111111 1 99990000001500";
    }
    
    public String gerarLinhaDigitavel(String codigoBarras) {
        // Conversão de código de barras para linha digitável
        return codigoBarras.replaceAll("[^0-9]", "");
    }
    
    public String gerarQRCode(Boleto boleto) {
        // Geração de payload PIX
        return "00020126580014BR.GOV.BCB.PIX0136123e4567-e12b-12d1-a456-426655440000520400005303986540515.005802BR5913Fulano de Tal6008BRASILIA62070503***6304";
    }
    
    public byte[] gerarPDF(Boleto boleto) {
        // Implementação real de geração de PDF
        return new byte[0]; // Retorno simulado
    }
}

