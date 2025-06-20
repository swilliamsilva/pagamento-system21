# Arquitetura Hexagonal - Visão Geral

Este documento apresenta a arquitetura hexagonal adotada no sistema **pagamento-system21**, com separação clara de responsabilidades entre camadas.

---

## Objetivo

Promover:

* Separação de responsabilidades
* Testabilidade
* Flexibilidade para troca de infraestrutura
* Coesão entre domínio e negócio

---

## Camadas da Arquitetura Hexagonal

```
┌─────────────────────────────────────┐
│        Interfaces de Entrada        │ ← Controllers / REST API
└─────────────────────────────────────┘
               ↓ (DTOs)
┌─────────────────────────────────────┐
│       Aplicação (Mapper + DTO)      │
└─────────────────────────────────────┘
               ↓
┌─────────────────────────────────────┐
│       Domínio (Entidades + Regra)   │
│  - Serviços de domínio              │
│  - Validações                       │
│  - Cálculos                         │
│  - Portas (interfaces)             │
└─────────────────────────────────────┘
               ↓ (Interfaces)
┌─────────────────────────────────────┐
│       Infraestrutura (Adapters)     │
│  - Repositórios                     │
│  - Mensageria (Kafka, SNS)         │
│  - Integrações externas (Asaas)    │
└─────────────────────────────────────┘
```

---

## Fluxo Típico de Requisição

1. **Usuário** envia requisição HTTP → `Controller`
2. Controller converte DTO em domínio via `Mapper`
3. Invoca `Service` no domínio com lógica principal
4. `Service` usa portas para:

   * Persistência (`RepositoryPort`)
   * Notificação (`NotificacaoPort`)
   * Integrações externas (`GatewayPort`)
5. Adapters implementam as portas e fazem a comunicação com infraestrutura
6. Serviço de domínio retorna resultado → Controller envia resposta HTTP

---

## Padrões Utilizados

* **DDD**: Separação clara de domínio e aplicação
* **Ports & Adapters**: Interface orientada ao domínio
* **TDD-friendly**: Domínio testável sem dependências externas
* **Imutabilidade via records (DTOs)**

---

## Vantagens da Arquitetura

* Flexível para mudar infraestrutura (Ex: Kafka → SNS)
* Clareza na responsabilidade das classes
* Redução de acoplamento
* Adoção fácil de CI/CD e observabilidade por camadas

---

## Estrutura por Serviço (Exemplo: Boleto)

```
boleto-service/
├── application/
│   ├── BoletoController.java
│   ├── dto/
│   └── mapper/
├── domain/
│   ├── model/
│   ├── service/
│   └── ports/
├── infrastructure/
│   ├── adapters/
│   └── config/
└── BoletoApplication.java
```

---

## Considerações Finais

Esta arquitetura permite escalar os serviços de pagamento de forma independente, segura e com qualidade.

**Próximos documentos recomendados:**

* `CLASS-FLOW.md`: Fluxo detalhado de classes por serviço
* `SECURITY.md`: Detalhamento do fluxo JWT
* `MONITORING.md`: Observabilidade e health checks

---

**Autor:** William Silva
**Email:** [williamsilva.codigo@gmail.com](mailto:williamsilva.codigo@gmail.com)
