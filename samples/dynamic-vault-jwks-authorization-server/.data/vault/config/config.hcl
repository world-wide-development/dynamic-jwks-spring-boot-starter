ui = true

api_addr = "0.0.0.0:8200"
cluster_addr  = "0.0.0.0:8201"

storage "file" {
  path = "/vault/data"
}

listener "tcp" {
  tls_disable = 1
  address     = "0.0.0.0:8200"
}

# Enable PKI secrets engine
path "pki/*" {
  capabilities = ["create", "read", "update", "delete", "list"]
}

# Configure the PKI backend
path "pki/config/ca" {
  capabilities = ["create", "read", "update", "delete"]
}

# Create the root certificate
path "pki/root/generate/internal" {
  capabilities = ["create"]
}

# Configure the root certificate
path "pki/config/urls" {
  capabilities = ["create", "read", "update", "delete"]
}

# Add role
path "pki/roles/*" {
  capabilities = ["create", "read", "update", "delete"]
}

# Enable KV secrets engine
path "sys/mounts/*" {
  capabilities = ["create", "read", "update", "delete", "list"]
}
