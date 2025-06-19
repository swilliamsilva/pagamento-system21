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