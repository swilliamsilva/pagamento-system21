# Arquitetura Hexagonal para Boleto Service

## Camadas:
1. **Aplicação**:
   - Controllers
   - DTOs
   - Mappers

2. **Domínio**:
   - Entidades e Value Objects
   - Serviços de domínio
   - Portas (interfaces)
   - Regras de negócio

3. **Infraestrutura**:
   - Adaptadores para bancos de dados
   - Adaptadores para gateways externos
   - Adaptadores para mensageria
   - Configurações

## Fluxo de Pagamento de Boleto:
1. Requisição chega no `BoletoController`
2. Chamada para `BoletoService` no domínio
3. Domínio executa validações e cálculos
4. Uso de portas para:
   - Persistência (`BoletoRepositoryPort`)
   - Gateway de pagamento (`AsaasGatewayPort`)
   - Notificação (`NotificacaoPort`)
5. Retorno de `BoletoResponseDTO`