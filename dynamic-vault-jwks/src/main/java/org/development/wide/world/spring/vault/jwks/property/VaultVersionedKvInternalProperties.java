package org.development.wide.world.spring.vault.jwks.property;

import org.springframework.lang.NonNull;

public record VaultVersionedKvInternalProperties(
        String rootPath,
        String certificatePath
) {

    /* Builder */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String rootPath;
        private String certificatePath;

        public Builder rootPath(String rootPath) {
            this.rootPath = rootPath;
            return this;
        }

        public Builder certificatePath(String certificatePath) {
            this.certificatePath = certificatePath;
            return this;
        }

        @NonNull
        public VaultVersionedKvInternalProperties build() {
            return new VaultVersionedKvInternalProperties(rootPath, certificatePath);
        }

    }

}
