# DEPLOYMENT.md - pagamento-system21

Este documento descreve como realizar o deploy dos microserviços do sistema `pagamento-system21` utilizando Docker, Docker Compose e Kubernetes.

---

##  Docker

Cada microserviço possui seu `Dockerfile` dedicado localizado na pasta:

```
deployment/docker/Dockerfile-[nome-do-serviço]
```

### Exemplo: Dockerfile do `auth-service`

```dockerfile
FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Comandos úteis:

```bash
# Build manual de um serviço
cd auth-service
./mvnw clean package

# Build da imagem Docker
docker build -f deployment/docker/Dockerfile-auth -t pagamento/auth-service:latest .

# Executar o container
docker run -p 8081:8080 pagamento/auth-service:latest
```

---

##  Docker Compose

Arquivo localizado em: `deployment/docker/docker-compose.yml`

### Executar todos os serviços localmente:

```bash
docker-compose -f deployment/docker/docker-compose.yml up --build
```

### Estrutura do `docker-compose.yml`

```yaml
version: '3.8'
services:
  api-gateway:
    build:
      context: .
      dockerfile: deployment/docker/Dockerfile-gateway
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker

  auth-service:
    build:
      context: .
      dockerfile: deployment/docker/Dockerfile-auth
    environment:
      SPRING_PROFILES_ACTIVE: docker

  # Outros serviços seguem padrão semelhante
```

---

##  Kubernetes

### Estrutura básica do manifesto `auth-deployment.yaml`

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: auth-service
spec:
  replicas: 2
  selector:
    matchLabels:
      app: auth-service
  template:
    metadata:
      labels:
        app: auth-service
    spec:
      containers:
        - name: auth-service
          image: pagamento/auth-service:latest
          ports:
            - containerPort: 8080
```

### Comandos úteis com `kubectl`

```bash
# Criar recursos no cluster
kubectl apply -f deployment/k8s/auth-deployment.yaml

# Verificar status
kubectl get pods
kubectl get services

# Logs
kubectl logs -f <nome-do-pod>
```

---

##  Secrets e ConfigMaps

Localizados em:

* `deployment/k8s/secrets.yaml`
* `deployment/k8s/configmap.yaml`

Usados para injetar configurações e segredos seguros nos pods.

---

##  Integração com CI/CD

Workflows localizados em `.github/workflows/`

Exemplo:

```yaml
name: Auth Service CI/CD
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
```

---

##  Documentos Relacionados

* `SECURITY.md`: configuração de certificados TLS
* `AWS-INTEGRATION.md`: integração com S3/SNS
* `FLOW.md`: fluxo de execução entre serviços

---

**Autor:** William Silva
**Email:** [williamsilva.codigo@gmail.com](mailto:williamsilva.codigo@gmail.com)
