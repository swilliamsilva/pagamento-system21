# Vault policies for payment system
path "secret/data/pagamento/*" {
  capabilities = ["read", "list"]
}
