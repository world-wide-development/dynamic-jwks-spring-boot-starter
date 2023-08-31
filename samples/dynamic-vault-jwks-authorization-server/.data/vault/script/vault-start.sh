#!/bin/sh

echo "Start Vault"
vault server -dev

echo "Configure PKI secrets engine"
vault secrets enable -path=pki pki
vault write pki/root/generate/internal common_name="My Root CA" ttl=87600h

vault write pki/config/urls issuing_certificates="http://127.0.0.1:8200/v1/pki/ca" crl_distribution_points="http://127.0.0.1:8200/v1/pki/crl"

echo "Enable and configure the custom KV secrets engine"
vault secrets enable -path=my-custom-kv kv
