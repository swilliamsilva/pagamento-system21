# Observabilidade e Monitoramento - pagamento-system21

Este documento descreve os mecanismos de monitoramento, rastreamento e métricas aplicados ao sistema distribuído `pagamento-system21`, com foco em visibilidade, saúde e desempenho.

---

## 🎯 Objetivo

Garantir visibilidade completa da execução dos serviços através de:

* Logs estruturados (com contexto)
* Métricas de aplicação (Micrometer)
* Tracing distribuído (OpenTelemetry)
* Indicadores de saúde customizados

---

## 📦 Componentes Envolvidos

| Componente                   | Finalidade                                    |
| ---------------------------- | --------------------------------------------- |
| `LoggingAspect.java`         | Logs automáticos de entrada/saída dos métodos |
| `TracingConfig.java`         | Configuração do OpenTelemetry / Sleuth        |
| `MetricsConfig.java`         | Exposição de métricas via Micrometer          |
| `ReadinessProbe.java`        | Health check para readiness                   |
| `LivenessProbe.java`         | Health check para liveness                    |
| `BoletoHealthIndicator.java` | Validação da saúde do serviço de boletos      |

---

## 📈 Métricas

### 🔧 Ferramentas

* **Micrometer**: biblioteca de métricas no Spring Boot
* **Prometheus**: coleta e armazenamento das métricas
* **Grafana**: dashboards para visualização

### 📊 Exemplos de Métricas

| Métrica                              | Descrição                          |
| ------------------------------------ | ---------------------------------- |
| `http.server.requests`               | Contagem de requisições HTTP       |
| `jvm.memory.used`                    | Memória usada pela JVM             |
| `process.cpu.usage`                  | Uso da CPU do processo             |
| `custom.boleto.pagamentos.efetuados` | Total de boletos pagos registrados |

---

## 🔍 Tracing (Rastreamento)

### Tecnologias

* **OpenTelemetry**: padronização de trace
* **Jaeger / Zipkin**: visualização de chamadas entre serviços

### Exemplo de Trace

```
[API Gateway] → [Auth Service] → [Payment Service] → [Boleto Service]
```

Cada trecho da requisição recebe um **traceId** único propagado entre os serviços via headers:

```http
traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01
```

---

## 🩺 Health Checks

Expostos via **Spring Boot Actuator**:

* `/actuator/health` → inclui probes de:

  * Banco de dados
  * Kafka / Mensageria
  * Serviços internos
  * Serviços externos (Asaas)

---

## 🚀 Configurações Sugeridas

```yaml
management:
  endpoints:
    web:
      exposure:
        include: "health,metrics,prometheus"
  metrics:
    export:
      prometheus:
        enabled: true
```

---

## ✅ Boas Práticas

* TraceId nos logs: `log.info("traceId={}", traceId)`
* Cada serviço define health indicators próprios
* Dashboards configurados no Grafana por serviço
* Alarmes definidos em cima das métricas-chave

---

## 📁 Referências

* `common/observability/LoggingAspect.java`
* `common/observability/TracingConfig.java`
* `common/observability/MetricsConfig.java`
* `common/health/*`

---

**Autor:** William Silva
**Email:** [williamsilva.codigo@gmail.com](mailto:williamsilva.codigo@gmail.com)
