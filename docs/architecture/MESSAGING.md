# Arquitetura de Mensageria - pagamento-system21

Este documento descreve a estratégia de comunicação assíncrona entre os serviços do sistema `pagamento-system21` utilizando **Apache Kafka** e **AWS SNS**.

---

## 🎯 Objetivo

Garantir integração desacoplada entre microserviços com:

* Alta disponibilidade e tolerância a falhas
* Processamento assíncrono de eventos
* Escalabilidade horizontal

---

## 📦 Componentes

| Componente                     | Descrição                                  |
| ------------------------------ | ------------------------------------------ |
| `KafkaProducerConfig.java`     | Configuração do produtor Kafka             |
| `KafkaConsumerConfig.java`     | Configuração do consumidor Kafka           |
| `KafkaTopics.java`             | Definição centralizada dos tópicos Kafka   |
| `KafkaNotificacaoAdapter.java` | Envia eventos de notificação ao Kafka      |
| `SnsService.java`              | Serviço para envio de mensagens ao AWS SNS |
| `SnsClientConfig.java`         | Bean de configuração do cliente SNS        |

---

## 🔄 Fluxo de Eventos

### Exemplo: Fluxo de Pagamento Boleto

```
[PaymentService] --(evento: boleto.criado)--> [Kafka] --> [BoletoService]
                                      ↓
                        [NotificacaoService] via KafkaNotificacaoAdapter
```

---

## ☁️ Integração com AWS SNS

Utilizado principalmente para notificações externas ou integração com outros domínios.

### Exemplo

```java
snsService.publishMessage("arn:aws:sns:us-east-1:123456789:pagamento-notify", "BOLETO_PAGO");
```

---

## 🧪 Testes e Diagnóstico

* Utilize o `kafka-console-consumer.sh` e `kafka-console-producer.sh` para testes locais
* Simule falhas e verifique o comportamento (ex: dead-letter topics)

---

## 📜 Tópicos Kafka Utilizados

| Tópico              | Finalidade                         |
| ------------------- | ---------------------------------- |
| `boleto.criado`     | Envio de novo boleto ao serviço    |
| `boleto.pago`       | Confirmação de pagamento de boleto |
| `notificacao.email` | Disparo de notificações por e-mail |
| `pix.gerado`        | Evento de criação de chave Pix     |
| `cartao.transacao`  | Transação processada com cartão    |

Todos os nomes estão definidos em `KafkaTopics.java` para evitar hardcoded.

---

## 🔐 Segurança

* Autenticação via credenciais de acesso para AWS SNS (definidas no `AwsCredentialsConfig.java`)
* Para Kafka, recomenda-se:

  * TLS
  * ACLs baseadas em tópico
  * SASL (SCRAM ou OAUTH)

---

## 📁 Localização dos Arquivos

```
common/src/main/java/com/pagamento/common/messaging/KafkaProducerConfig.java
common/src/main/java/com/pagamento/common/messaging/KafkaConsumerConfig.java
common/src/main/java/com/pagamento/common/messaging/KafkaTopics.java
boleto-service/src/main/java/.../KafkaNotificacaoAdapter.java
cloud-aws/src/main/java/com/pagamento/aws/sns/SnsService.java
```

---

## ✅ Boas Práticas

* Sempre definir partições para escalabilidade
* Separar tópicos por domínio
* Incluir versionamento no payload (ex: `"v1": {...}`)
* Documentar contratos de eventos (event schemas)

---

**Autor:** William Silva
**Email:** [williamsilva.codigo@gmail.com](mailto:williamsilva.codigo@gmail.com)
