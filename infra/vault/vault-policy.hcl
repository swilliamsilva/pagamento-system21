# ========================================================
# Política: vault-policy.hcl
# Descrição: Permite leitura dos segredos do sistema de pagamento
# ========================================================

path "secret/data/pagamento/*" {
  capabilities = ["read", "list"]
}
