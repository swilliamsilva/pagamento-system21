# OBSERVABILITY.md - pagamento-system21

Este documento apresenta a estratégia de observabilidade implementada no projeto `pagamento-system21`, incluindo logs, métricas, rastreamento distribuído e pontos de integração com ferramentas externas.

---

##  Objetivos da Observabilidade

* Diagnóstico de problemas em tempo real
* Análise de performance e gargalos
* Visibilidade de ponta a ponta no fluxo de pagamentos

---

##  Localização dos Arquivos

```
common/
├── observability/
│   ├── LoggingAspect.java
│   ├── MetricsConfig.java
│   └── TracingConfig.java
```

---

##  Logging com AspectJ (AOP)

**Classe:** `LoggingAspect`

Intercepta chamadas de métodos de serviços e registra:

* Nome da classe e método
* Parâmetros de entrada
* Tempo de execução
* Resultado ou exceção

**Exemplo de log:**

```
INFO  [PagamentoService] -> Executando: criarPagamento(dto=...)
INFO  [PagamentoService] <- Tempo: 120ms | Resultado: OK
```

---

##  Métricas com Micrometer

**Classe:** `MetricsConfig`

Exposição automática de métricas de:

* JVM (heap, GC, threads)
* HTTP (tempo de resposta, status codes)
* Customizadas por contador/timer

**Endpoint:** `GET /actuator/metrics`

**Exemplo:**

```json
{
  "name": "http.server.requests",
  "measurements": [...],
  "availableTags": ["uri", "method"]
}
```

---

##  Rastreamento com Spring Sleuth / OpenTelemetry

**Classe:** `TracingConfig`

Permite o rastreamento distribuído entre microserviços usando:

* Trace ID compartilhado
* Exportadores (Zipkin, Jaeger, Prometheus)

**Exemplo de headers propagados:**

```
x-b3-traceid: 4b7e58...
x-b3-spanid: 7d47fc...
```

---

## Endpoints Ativados no Actuator

| Endpoint            | Descrição                       |
| ------------------- | ------------------------------- |
| `/actuator/health`  | Health check                    |
| `/actuator/metrics` | Exposição de métricas           |
| `/actuator/loggers` | Configuração dinâmica de log    |
| `/actuator/trace`   | (se ativo) Trace de requisições |

---

##  Segurança nos Endpoints

Use perfis ou configuração via `application.yml` para proteger endpoints sensíveis:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, metrics
```

---

##  Integração com Ferramentas

| Ferramenta    | Função     | Status    |
| ------------- | ---------- | --------- |
| ELK / Loki    | Logs       | Planejado |
| Prometheus    | Métricas   | Em uso    |
| Grafana       | Dashboards | Em uso    |
| Jaeger/Zipkin | Tracing    | Opcional  |

---

##  Referências

* `ACTUATOR`: [https://docs.spring.io/spring-boot/docs/current/actuator-api/html](https://docs.spring.io/spring-boot/docs/current/actuator-api/html)
* `MICROMETER`: [https://micrometer.io](https://micrometer.io)
* `SLEUTH`: [https://spring.io/projects/spring-cloud-sleuth](https://spring.io/projects/spring-cloud-sleuth)

---

**Autor:** William Silva
**Email:** [williamsilva.codigo@gmail.com](mailto:williamsilva.codigo@gmail.com)
