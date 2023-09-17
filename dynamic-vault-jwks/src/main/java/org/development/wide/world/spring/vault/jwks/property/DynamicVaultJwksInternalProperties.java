package org.development.wide.world.spring.vault.jwks.property;

import org.springframework.lang.NonNull;

public record DynamicVaultJwksInternalProperties(
        Boolean enabled,
        VaultPkiInternalProperties pki,
        Integer certificateRotationRetries,
        VaultVersionedKvInternalProperties versionedKv
) {

    /* Builder */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Boolean enabled;
        private VaultPkiInternalProperties pki;
        private Integer certificateRotationRetries;
        private VaultVersionedKvInternalProperties versionedKv;

        public Builder enabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder pki(VaultPkiInternalProperties pki) {
            this.pki = pki;
            return this;
        }

        public Builder versionedKv(VaultVersionedKvInternalProperties versionedKv) {
            this.versionedKv = versionedKv;
            return this;
        }

        public Builder certificateRotationRetries(Integer certificateRotationRetries) {
            this.certificateRotationRetries = certificateRotationRetries;
            return this;
        }

        @NonNull
        public DynamicVaultJwksInternalProperties build() {
            return new DynamicVaultJwksInternalProperties(enabled, pki, certificateRotationRetries, versionedKv);
        }

    }

}
