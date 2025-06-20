#!/bin/bash
# ========================================================
# Script: vault-init.sh
# Descrição: Inicializa o Vault e imprime os tokens de acesso.
# Uso: ./vault-init.sh
# ========================================================

echo "Iniciando HashiCorp Vault..."

vault operator init -key-shares=1 -key-threshold=1 > vault-keys.txt

UNSEAL_KEY=$(grep 'Unseal Key 1:' vault-keys.txt | awk '{print $NF}')
ROOT_TOKEN=$(grep 'Initial Root Token:' vault-keys.txt | awk '{print $NF}')

echo "Fazendo unseal do Vault..."
vault operator unseal $UNSEAL_KEY

echo "Logando com root token..."
vault login $ROOT_TOKEN

echo "Vault inicializado e autenticado"
