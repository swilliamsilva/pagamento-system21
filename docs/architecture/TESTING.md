# TESTING.md - pagamento-system21

Este documento descreve a estratégia de testes adotada no projeto `pagamento-system21`, incluindo tipos de testes, ferramentas utilizadas e exemplos de execução.

---

##  Tipos de Testes

| Tipo                | Finalidade                                  | Ferramenta            |
| ------------------- | ------------------------------------------- | --------------------- |
| Unitário            | Testar métodos isoladamente                 | JUnit 5 / Mockito     |
| Integração          | Verificar integração entre classes/módulos  | Spring Test           |
| Contrato (opcional) | Validar contratos entre produtor/consumidor | Spring Cloud Contract |
| End-to-End (futuro) | Testes simulando fluxo real do sistema      | Postman/Newman        |

---

##  Estrutura de Pastas

Os testes estão organizados fora dos módulos de produção:

```
tests/
├── auth-service/
│   ├── AuthControllerTest.java
│   └── AuthServiceTest.java
├── payment-service/
│   └── PaymentServiceIntegrationTest.java
├── boleto-service/
│   ├── domain/
│   │   ├── BoletoServiceTest.java
│   │   └── BoletoValidationTest.java
│   └── infrastructure/
│       └── AsaasGatewayAdapterTest.java
└── gateway/
    └── GatewayRouteTest.java
```

---

##  Ferramentas Utilizadas

* **JUnit 5**: framework principal de testes
* **Mockito**: para mocks e stubs
* **Spring Boot Test**: para testes de integração com contexto real
* **Testcontainers (opcional)**: simulação de banco de dados real em Docker

---

##  Execução dos Testes

### Maven:

```bash
# Executar todos os testes
mvn clean test

# Executar testes de um módulo específico
cd tests/auth-service
mvn test
```

### IDE:

* Você pode rodar testes diretamente por IDEs como IntelliJ ou VSCode com JUnit configurado

---

##  Exemplo: AuthServiceTest

```java
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Test
    void deveAutenticarComSucesso() {
        User user = new User("william", "senha123");
        when(userRepository.findByUsername("william")).thenReturn(Optional.of(user));

        AuthResponse response = authService.authenticate("william", "senha123");

        assertNotNull(response);
        assertTrue(response.token().startsWith("Bearer "));
    }
}
```

---

##  Boas Práticas

* Cobrir cenários positivos e negativos
* Nomear testes com clareza
* Utilizar `@BeforeEach` para setup comum
* Evitar lógica complexa dentro dos testes

---

##  Documentos Relacionados

* `FLOW.md`: fluxo completo de chamadas entre serviços
* `DEPLOYMENT.md`: como subir os serviços para testes reais
* `SECURITY.md`: como testar endpoints seguros com JWT

---

**Autor:** William Silva
**Email:** [williamsilva.codigo@gmail.com](mailto:williamsilva.codigo@gmail.com)
