# Integração com AWS - pagamento-system21

Este documento apresenta a arquitetura de integração com serviços AWS dentro do sistema `pagamento-system21`, incluindo S3 (armazenamento), SNS (mensageria) e autenticação via credenciais.

---

## Objetivo

* Armazenamento de arquivos (S3)
* Notificações assíncronas (SNS)
* Compatibilidade com ambientes locais e nuvem

---

## Serviços AWS Utilizados

| Serviço | Finalidade                                                |
| ------- | --------------------------------------------------------- |
| S3      | Armazenamento de arquivos de pagamento, comprovantes, etc |
| SNS     | Envio de mensagens para notificações externas             |
| IAM     | Controle de acesso e autenticação via credenciais         |

---

##  Arquivos e Classes

| Caminho                                                                      | Descrição                           |
| ---------------------------------------------------------------------------- | ----------------------------------- |
| `cloud-aws/src/main/java/com/pagamento/aws/s3/S3Service.java`                | Serviço para upload/download via S3 |
| `cloud-aws/src/main/java/com/pagamento/aws/s3/S3ClientConfig.java`           | Bean configurador do cliente S3     |
| `cloud-aws/src/main/java/com/pagamento/aws/sns/SnsService.java`              | Serviço para envio de mensagens SNS |
| `cloud-aws/src/main/java/com/pagamento/aws/sns/SnsClientConfig.java`         | Bean de configuração do SNS         |
| `cloud-aws/src/main/java/com/pagamento/aws/config/AwsCredentialsConfig.java` | Configuração de credenciais         |

---

##  Configuração de Credenciais

```yaml
# application-prod.yml
aws:
  region: us-east-1
  accessKeyId: ${AWS_ACCESS_KEY_ID}
  secretKey: ${AWS_SECRET_ACCESS_KEY}
  s3:
    endpoint: https://s3.amazonaws.com
  sns:
    endpoint: https://sns.amazonaws.com
```

Para ambiente local (`dev`, `test`), as credenciais padrão "test" são usadas via profile:

```java
@Bean
@Profile("dev | test")
public AwsCredentialsProvider localAwsCredentialsProvider() {
    return StaticCredentialsProvider.create(
        AwsBasicCredentials.create("test", "test")
    );
}
```

---

##  Testando com LocalStack

```yaml
# application-dev.yml
aws:
  region: us-east-1
  s3:
    endpoint: http://localhost:4566
  sns:
    endpoint: http://localhost:4566
```

Executar com Docker:

```bash
docker run -d -p 4566:4566 localstack/localstack
```

---

## Exemplos de Uso

### Upload S3

```java
s3Service.uploadFile("bucket-arquivos", "boleto123.pdf", bytes, "application/pdf");
```

### Publicação SNS

```java
snsService.publishMessage("arn:aws:sns:us-east-1:123456789:pagamento-notify", "BOLETO_PAGO");
```

---

## Considerações

* Use variáveis de ambiente para dados sensíveis
* Configure buckets SNS/S3 com políticas mínimas de permissão
* Utilize versionamento e TTL nos buckets se necessário
* Para ambientes produtivos, integre com AWS Vault, Secrets Manager ou sistemas como HashiCorp Vault

---

**Autor:** William Silva
**Email:** [williamsilva.codigo@gmail.com](mailto:williamsilva.codigo@gmail.com)
