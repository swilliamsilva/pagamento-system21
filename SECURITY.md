# Segurança no pagamento-system21

Este documento descreve a estratégia de segurança aplicada no sistema distribuído de pagamentos, com foco em autenticação, autorização, criptografia e boas práticas em ambiente produtivo.

---

## 🔐 Mecanismo de Autenticação

### 1. Fluxo de Login (JWT)

```
[Client] → POST /auth/login → AuthController → AuthService
                                 ↓
                       Gera token JWT (AuthResponse)
                                 ↓
                           Retorna token ao client
```

### 2. Validação via Filtro de Segurança (Spring Security)

* Token JWT enviado no header Authorization:
  `Authorization: Bearer <token>`
* O filtro intercepta requisições e valida:

  * Assinatura
  * Expiração
  * Permissões (roles)

---

## 🧾 Estrutura do JWT

```json
{
  "sub": "william.silva",
  "roles": ["USER", "ADMIN"],
  "iat": 1718793652,
  "exp": 1718797252
}
```

* `sub`: Identificador do usuário
* `roles`: Permissões associadas
* `iat` / `exp`: Tempo de emissão e expiração

---

## 🛡️ Camadas de Segurança

| Camada                | Proteção                                               |
| --------------------- | ------------------------------------------------------ |
| **API Gateway**       | Filtro global de autenticação e roteamento seguro      |
| **Auth Service**      | Geração e verificação de token                         |
| **Services internos** | Rejeitam chamadas sem token válido                     |
| **Transport Layer**   | HTTPS obrigatório entre serviços (SSL/TLS)             |
| **Vault**             | Armazenamento seguro de segredos (tokens, credenciais) |

---

## 📁 Arquivos e Configurações Relevantes

* `SecurityConfig.java` → Configuração do filtro de autenticação
* `JwtTokenProvider.java` → Geração e validação do token
* `application.yml` → Tempo de expiração, segredos
* `vault-policy.hcl` → Política de acesso segura ao Vault

---

## 🧪 Testes de Segurança

1. Login válido → deve retornar JWT
2. Token inválido → deve retornar 401
3. Expiração → token expirado rejeitado
4. Acesso sem token → bloqueado
5. Token com role errada → 403 Forbidden

---

## ✅ Boas Práticas

* Segredos externos em Vault (não comitados no Git)
* Rotação periódica de chaves JWT
* CORS restritivo no API Gateway
* CSRF desativado apenas para APIs REST
* Habilitação de logs de segurança (Spring Security Debug)

---

## 📌 Exemplos de Headers

```http
POST /auth/login
Content-Type: application/json

{
  "username": "will",
  "password": "senha123"
}
```

```http
GET /api/pagamento
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR...
```

---

**Autor:** William Silva
**Email:** [williamsilva.codigo@gmail.com](mailto:williamsilva.codigo@gmail.com)
