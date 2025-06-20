# INTEGRATION.md - Integrações Externas no pagamento-system21

Este documento descreve os pontos de integração externa no projeto `pagamento-system21`, incluindo gateways de pagamento, serviços AWS, mensageria e autenticação.

---

## Visão Geral

O sistema está preparado para se comunicar com serviços externos por:

* HTTP REST (com Feign ou WebClient)
* Mensageria assíncrona (Kafka, SNS)
* SDKs diretos (AWS SDK)

---

## Autenticação Externa

**Serviço:** `auth-service`

* Endpoint: `/auth/login`
* Entrada: `AuthRequest`
* Saída: `AuthResponse` com token JWT
* Usado por: todos os clientes e microserviços

---

##  Integração com Asaas (Gateway de Pagamentos)

**Adaptador:** `AsaasGatewayAdapter`

* Protocolo: HTTP REST
* Métodos: POST boleto, GET status, DELETE cancelamento
* Autenticação: Header `access_token`
* Serviço associado: `boleto-service`

**Exemplo:**

```java
WebClient.builder()
  .baseUrl("https://sandbox.asaas.com/api/v3")
  .defaultHeader("access_token", "{token}")
  .build();
```

---

## ☁️ Integração com AWS

### S3 - Armazenamento

* Classe: `S3Service`
* Operações: upload/download, URL pré-assinada
* Usado por: exportações, relatórios

### SNS - Notificações

* Classe: `SnsService`
* Operação: envio de mensagens para tópicos
* Usado por: eventos de pagamento, alertas

**Configuração:** `application-dev.yml` ou `application-prod.yml`

---

## Mensageria Assíncrona

### Kafka

* Tópicos: `pagamento.criado`, `boleto.processado`, `pix.recebido`
* Produtores/Consumidores no módulo `common.messaging`

**Exemplo:**

```java
kafkaTemplate.send(KafkaTopics.PAGAMENTO_CRIADO, payload);
```

### SNS/SQS (alternativa ou híbrido)

* Em fase de planejamento para redundância

---

## Comunicação entre microserviços

* REST tradicional com OpenAPI entre serviços
* Possibilidade de uso futuro de Service Mesh

---

## Estrutura dos Adaptadores de Integração

```
/infrastructure/adapters/gateway/
├── AsaasGatewayAdapter.java
├── KafkaNotificacaoAdapter.java
└── AwsS3Adapter.java (futuro)
```

---

## Testes de Integração

* Mocks com `WireMock` ou `MockWebServer`
* Tests de contrato com `Spring Cloud Contract`
* Testes reais em ambiente sandbox (Asaas, AWS LocalStack)

---

## Referências

* Asaas Docs: [https://docs.asaas.com/api](https://docs.asaas.com/api)
* AWS SDK v2: [https://docs.aws.amazon.com/sdk-for-java/latest](https://docs.aws.amazon.com/sdk-for-java/latest)
* Spring Cloud Stream / Kafka: [https://spring.io/projects/spring-cloud-stream](https://spring.io/projects/spring-cloud-stream)

---

**Autor:** William Silva
**Email:** [williamsilva.codigo@gmail.com](mailto:williamsilva.codigo@gmail.com)
