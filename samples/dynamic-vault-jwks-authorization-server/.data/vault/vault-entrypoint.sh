#!/bin/sh

startVaultServer() {
  vault server -dev
}

waitForVaultServer() {
  echo "Wait for Vault to be ready"
  while ! vault status >/dev/null 2>&1; do
    echo "Vault server has not started yet waiting for 2 seconds"
    sleep 2
  done
}

configurePkiSecretsEngine() {
  echo "Configure PKI secrets engine"
  vault secrets enable -path=pki pki
  vault write pki/root/generate/internal common_name="root.certificate" ttl=87600h
  vault write pki/roles/jwks allow_any_name=true max_ttl=72h
}

customizeVaultServer() {
  waitForVaultServer
  configurePkiSecretsEngine
}

customizeVaultServer &
startVaultServer
