package org.development.wide.world.spring.vault.jwks.property;

import org.springframework.lang.NonNull;

import java.time.Duration;

public record VaultPkiInternalProperties(
        String rootPath,
        String roleName,
        Duration certificateTtl,
        String certificateCommonName
) {

    /* Builder */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String rootPath;
        private String roleName;
        private Duration certificateTtl;
        private String certificateCommonName;

        public Builder rootPath(String rootPath) {
            this.rootPath = rootPath;
            return this;
        }

        public Builder roleName(String roleName) {
            this.roleName = roleName;
            return this;
        }

        public Builder certificateTtl(Duration certificateTtl) {
            this.certificateTtl = certificateTtl;
            return this;
        }

        public Builder certificateCommonName(String certificateCommonName) {
            this.certificateCommonName = certificateCommonName;
            return this;
        }

        @NonNull
        public VaultPkiInternalProperties build() {
            return new VaultPkiInternalProperties(rootPath, roleName, certificateTtl, certificateCommonName);
        }

    }

}
