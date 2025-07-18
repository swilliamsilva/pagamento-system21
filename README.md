# Sistema de Pagamentos com Arquitetura Hexagonal

![build-all](https://github.com/swilliamsilva/pagamento-system21/actions/workflows/build-all.yml/badge.svg)
![ci](https://github.com/swilliamsilva/pagamento-system21/actions/workflows/ci.yml/badge.svg)

## Status dos Módulos

| Serviço              | Status |
|----------------------|--------|
| API Gateway          | ![apigateway](https://github.com/swilliamsilva/pagamento-system21/actions/workflows/apigateway-ci.yml/badge.svg) |
| Auth Service         | ![authservice](https://github.com/swilliamsilva/pagamento-system21/actions/workflows/authservice-ci.yml/badge.svg) |
| Payment Service      | ![paymentservice](https://github.com/swilliamsilva/pagamento-system21/actions/workflows/paymentservice-ci.yml/badge.svg) |
| Boleto Service       | ![boletoservice](https://github.com/swilliamsilva/pagamento-system21/actions/workflows/boletoservice-ci.yml/badge.svg) |
| Pix Service          | ![pixservice](https://github.com/swilliamsilva/pagamento-system21/actions/workflows/pixservice-ci.yml/badge.svg) |
| Card Service         | ![cardservice](https://github.com/swilliamsilva/pagamento-system21/actions/workflows/cardservice-ci.yml/badge.svg) |
| Asaas Integration    | ![asaasintegration](https://github.com/swilliamsilva/pagamento-system21/actions/workflows/asaasintegration-ci.yml/badge.svg) |
| Cloud AWS Support    | ![cloudaws](https://github.com/swilliamsilva/pagamento-system21/actions/workflows/cloudaws-ci.yml/badge.svg) |

---


# Sistema de Pagamentos com Arquitetura Hexagonal

## Visão Geral
Sistema de pagamentos distribuído com:
- Java 21 e Spring Boot 3
- Arquitetura hexagonal para serviços de pagamento
- Integração com AWS (S3, SNS)
- Comunicação entre serviços via eventos
- Infraestrutura como código

## Serviços
1. **API Gateway**: Roteamento e segurança
2. **Auth Service**: Autenticação JWT
3. **Payment Orchestrator**: Orquestração de pagamentos
4. **Boleto Service**: Implementação hexagonal
5. **Pix Service**: Processamento de transações PIX
6. **Card Service**: Processamento de cartões

## Como Executar
```bash
docker-compose -f deployment/docker/docker-compose.yml up --build
```

## Arquitetura Hexagonal
![Hexagonal Architecture Diagram](docs/architecture/hexagonal-diagram.png)


# Sistema de Pagamentos - pagamento-system21

Projeto de sistema de pagamentos baseado em microserviços, utilizando arquitetura hexagonal, Java 21 e Spring Boot 3.

## Visão Geral - DETALHADA

* Linguagem: Java 21
* Framework: Spring Boot 3
* Arquitetura: Hexagonal (Ports & Adapters)
* Comunicação: Kafka (Mensageria), REST
* Banco de dados: MongoDB, Cassandra, DynamoDB, PostgreSQL (dependendo do módulo)
* Integração com AWS: S3, SNS
* Observabilidade: Micrometers, Logs, Health Checks customizados
* TDD e cobertura de testes unitários e de integração

---

## Como Executar o Projeto

Requisitos:

* JDK 21
* Docker e Docker Compose
* MongoDB, Cassandra e DynamoDB configurados via containers (ou LocalStack)

### Subir ambiente completo

```bash
docker-compose -f deployment/docker/docker-compose.yml up --build
```

> Os serviços estarão disponíveis em:
>
> * Gateway: [http://localhost:8080](http://localhost:8080)
> * Auth: [http://localhost:8081](http://localhost:8081)
> * Payment: [http://localhost:8082](http://localhost:8082)
> * Boleto: [http://localhost:8083](http://localhost:8083)
> * Pix: [http://localhost:8084](http://localhost:8084)
> * Cartão: [http://localhost:8085](http://localhost:8085)

---

##  Testes

### Estrutura de Testes:

* `tests/` (fora dos módulos)

  * `tests/auth-service`: Testes unitários de controller e service
  * `tests/payment-service`: Testes de integração
  * `tests/gateway`: Testes de roteamento
  * `tests/boleto-service`: Testes de domínio e infraestrutura

### Como Executar os Testes:

```bash
./mvnw test
```

> Para rodar um teste específico:

```bash
./mvnw -Dtest=NomeDaClasseTest test
```

---

##  Documentação por Módulo

* [HEXAGONAL-ARCH.md](docs/architecture/HEXAGONAL-ARCH.md): Detalhes da arquitetura do BoletoService
* [SECURITY.md](docs/security/SECURITY.md): JWT, filtros e configuração de autenticação
* [OBSERVABILITY.md](docs/observability/MONITORING.md): Logs, metrics, traces
* [FLOW.md](docs/architecture/CLASS-FLOW.md): Diagrama textual com os fluxos entre as classes por camada

---

## Swagger UI (se configurado)

Cada serviço pode possuir um endpoint do Swagger em:

```
http://localhost:{porta}/swagger-ui/index.html
```

---

##  Fluxo de Chamadas (exemplo Boleto)

1. **Entrada REST** via `BoletoController`
2. Mapeamento do DTO para Domínio via `BoletoMapper`
3. Execução de regras em `BoletoService`, `BoletoValidation`, `BoletoCalculos`
4. Uso das portas:

   * `BoletoRepositoryPort` → `BoletoRepositoryAdapter`
   * `AsaasGatewayPort` → `AsaasGatewayAdapter`
   * `NotificacaoPort` → `KafkaNotificacaoAdapter`
5. Retorno da resposta formatada em DTO

---

##  Manutenção e Extensão

* Para adicionar novo meio de pagamento:

  * Criar módulo novo com 3 camadas (application, domain, infrastructure)
  * Implementar interfaces de porta (hexagonal)
  * Registrar os beans no contexto

* Para criar novos endpoints:

  * Criar DTO
  * Implementar controller e mapper
  * Estender serviço de domínio

---

##  Autor

William Silva
[Email](mailto:williamsilva.codigo@gmail.com) | 
[LinkedIn](https://linkedin.com/in/william-silva-20315993) 
|Projeto Gerado via Script Python Automatizado

---

> © 2025 - Todos os direitos reservados - Sistema de Pagamento com Java 21 & Arquitetura Hexagonal
