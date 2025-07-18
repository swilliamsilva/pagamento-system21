Documentação para Suporte Técnico
1. Configurações Essenciais

    Porta do Serviço:
    yaml

server.port: 8083

    Ajuste necessário em produção: Modificar via variável de ambiente SERVER_PORT

Context Path:
yaml

    server.servlet.context-path: /pix

        Todos os endpoints acessíveis via http://host:8083/pix/**

2. Integração com Payment Service
yaml

payment:
  service:
    url: http://paymentservice:8080
    timeout: 5000
    retry:
      max-attempts: 3
      backoff: 500

    Timeout: 5 segundos para conexão/resposta

    Retry Policy:

        3 tentativas com intervalo de 500ms

        Problema comum: Aumentar timeout se ocorrerem ReadTimeoutException

3. Configuração DynamoDB
yaml

aws:
  dynamodb:
    endpoint: ${AWS_DYNAMODB_ENDPOINT:http://localhost:8000}
    region: ${AWS_REGION:sa-east-1}

    Ambiente Local:

        Usa LocalStack (default: http://localhost:8000)

        Setar variáveis:
        bash

    AWS_ACCESS_KEY=test
    AWS_SECRET_KEY=test

Produção:

    Remover endpoint para usar AWS real

    Setar variáveis reais:
    bash

        AWS_REGION=us-east-1
        AWS_ACCESS_KEY=AKIA...
        AWS_SECRET_KEY=...

4. Circuit Breaker (Resiliência)
yaml

resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        failureRateThreshold: 50
        waitDurationInOpenState: 10s

    Comportamento:

        Abre circuito após 50% de falhas em 10 chamadas

        Permanece aberto por 10 segundos

        Monitoramento: Acessar /actuator/health para status

5. Configuração JWT
yaml

jwt:
  secret: ${JWT_SECRET:default-secret}
  expiration: 3600000 # 1 hora

    Produção OBRIGATÓRIO:

        Setar variável JWT_SECRET com chave complexa

        Exemplo de geração:
        bash

        openssl rand -base64 32

6. Variáveis de Ambiente Críticas
Variável	Default	Descrição
AWS_REGION	sa-east-1	Região AWS
AWS_ACCESS_KEY	test	Access Key AWS
AWS_SECRET_KEY	test	Secret Key AWS
JWT_SECRET	default-secret	Chave para validação JWT
PAYMENT_SERVICE_URL	http://paymentservice:8080	URL do Payment Service
7. Solução de Problemas Comuns

Problema: Timeout ao chamar Payment Service
Solução:
yaml

# Aumentar timeouts:
payment:
  service:
    timeout: 10000 # 10 segundos

feign:
  client:
    config:
      default:
        readTimeout: 30000

Problema: Erros 401 - Autenticação
Verificar:

    Header Authorization: Bearer <token> presente

    Mesmo JWT_SECRET usado no API Gateway

    Token expirado (default: 1 hora)

Problema: Circuit Breaker aberto
Ações:

    Verificar logs do Payment Service

    Acessar /actuator/health para status

    Esperar 10 segundos (tempo de abertura)

    Reduzir carga se necessário

8. Health Checks

    Endpoint: http://localhost:8083/pix/actuator/health

    Componentes verificados:

        Conexão com DynamoDB

        Status do Circuit Breaker

        Disponibilidade do Payment Service

9. Melhores Práticas Produção

    Secret Management:

        Usar AWS Secrets Manager ou Kubernetes Secrets

        Nunca comitar valores reais no YAML

    High Availability:
    yaml

payment:
  service:
    url: http://payment-service-lb:8080

Logging:
yaml

logging:
  level:
    root: WARN # Produção
    com.pagamento: INFO

Monitoramento:

    Prometheus: /actuator/prometheus

    Métricas chave:

        resilience4j_circuitbreaker_state

        http_server_requests_seconds