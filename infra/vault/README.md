Como cadastrar segredos no Vault

vault kv put secret/pagamento/auth-service jwt.secret=mysupersecretkey
vault kv put secret/pagamento/auth-service jwt.expiration=3600


Como usar:

    Suba o Vault com Docker ou K8s

docker run --cap-add=IPC_LOCK -e 'VAULT_DEV_ROOT_TOKEN_ID=root' -e 'VAULT_DEV_LISTEN_ADDRESS=0.0.0.0:8200' -p 8200:8200 hashicorp/vault

Inicialize o Vault (apenas 1 vez)

cd infra/vault
chmod +x vault-init.sh
./vault-init.sh

Crie a política e associe ao token

vault policy write pagamento-policy vault-policy.hcl

Adicione segredos
Exemplo:

vault kv put secret/pagamento/auth-service jwt-secret=mysecret123

========

 Integração com Spring Boot (após)

Mais adiante, os serviços Java poderão ler os segredos com Spring Cloud Vault:

spring:
  cloud:
    vault:
      uri: http://localhost:8200
      token: s.xxxxxxxx
      kv:
        enabled: true
        backend: secret
        default-context: pagamento

