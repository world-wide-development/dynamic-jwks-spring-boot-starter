package org.development.wide.world.spring.jwks.property;

import org.springframework.lang.NonNull;

import java.time.Duration;

public record BCCertificateInternalProperties(
        String issuer,
        String subject,
        Duration certificateTtl
) {

    /* Builder */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String issuer;
        private String subject;
        private Duration certificateTtl;

        public Builder issuer(String issuer) {
            this.issuer = issuer;
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder certificateTtl(Duration certificateTtl) {
            this.certificateTtl = certificateTtl;
            return this;
        }

        @NonNull
        public BCCertificateInternalProperties build() {
            return new BCCertificateInternalProperties(issuer, subject, certificateTtl);
        }

    }

}
