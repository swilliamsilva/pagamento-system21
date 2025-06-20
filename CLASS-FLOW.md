# Fluxo de Classes - BoletoService (Hexagonal)

Este documento apresenta o fluxo de chamadas e responsabilidades entre as classes do serviço de boleto, baseado na arquitetura hexagonal adotada no projeto **pagamento-system21**.

## 1. Entrada: Requisição HTTP

* **Classe:** `BoletoController`
* **Local:** `boleto-service.application`
* **Responsabilidade:**

  * Recebe requisições REST (ex: POST `/api/boleto`)
  * Valida entrada básica com `@Valid`
  * Mapeia `BoletoRequestDTO` → domínio
  * Chama `BoletoService` (domínio)

## 2. Aplicação para Domínio

* **Classe:** `BoletoMapper`
* **Responsabilidade:**

  * Conversão bidirecional entre DTOs (`BoletoRequestDTO`, `BoletoResponseDTO`) e a entidade `Boleto`

## 3. Núcleo de Negócio: Domínio

### 3.1. Serviço Principal

* **Classe:** `BoletoService`
* **Local:** `boleto-service.domain.service`
* **Responsabilidade:**

  * Orquestra o fluxo principal de pagamento
  * Valida regras com `BoletoValidation`
  * Calcula juros com `BoletoCalculos`
  * Salva no repositório via `BoletoRepositoryPort`
  * Envia eventos via `NotificacaoPort`

### 3.2. Validação

* **Classe:** `BoletoValidation`
* **Responsabilidade:**

  * Validação de campos obrigatórios, vencimento, CPF etc.

### 3.3. Cálculos

* **Classe:** `BoletoCalculos`
* **Responsabilidade:**

  * Cálculo de valor final com juros, descontos e multa

## 4. Portas de Saída (Domínio)

Interfaces localizadas em `boleto-service.domain.ports`:

* `BoletoRepositoryPort`
* `AsaasGatewayPort`
* `NotificacaoPort`

## 5. Adapters (Infraestrutura)

### 5.1. Banco de Dados

* **Classe:** `BoletoRepositoryAdapter`
* **Implementa:** `BoletoRepositoryPort`
* **Responsabilidade:**

  * Persistência de boletos (MongoDB, JPA, etc.)

### 5.2. Integração com Asaas

* **Classe:** `AsaasGatewayAdapter`
* **Implementa:** `AsaasGatewayPort`
* **Responsabilidade:**

  * Envia dados para geração de boletos reais

### 5.3. Notificação via Kafka

* **Classe:** `KafkaNotificacaoAdapter`
* **Implementa:** `NotificacaoPort`
* **Responsabilidade:**

  * Publica evento com status de boleto

## 6. Saída: Resposta HTTP

* **Classe:** `BoletoController`
* **Responsabilidade:**

  * Recebe `BoletoResponseDTO` do domínio
  * Retorna `200 OK` com os dados do boleto criado ou `400/500` em caso de erro

---

## Resumo Visual

```text
HTTP → BoletoController
          ↓
   BoletoMapper
          ↓
     BoletoService
    ↙     ↓      ↘
Validation Cálculos  (Ports: Repository, Gateway, Notificação)
            ↓           ↓
      Adapters → MongoDB, Asaas, Kafka
          ↓
   BoletoResponseDTO → HTTP Response
```

---

## Próximos Documentos Recomendados

* `HEXAGONAL-ARCH.md`: Estrutura geral de arquitetura
* `SECURITY.md`: Fluxo de autenticação JWT
* `MONITORING.md`: Health checks, métricas e logs

---

**Autor:** William Silva
**Email:** [williamsilva.codigo@gmail.com](mailto:williamsilva.codigo@gmail.com)
