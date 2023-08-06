package org.development.wide.world.spring.vault.jwks.data;

import com.nimbusds.jose.jwk.JWKSet;
import org.development.wide.world.spring.vault.jwks.util.CertificateUtils;
import org.springframework.lang.NonNull;
import org.springframework.vault.support.CertificateBundle;
import org.springframework.vault.support.Versioned;

import java.security.cert.X509Certificate;

import static java.util.Optional.ofNullable;

public record VaultJwkSetData(
        JWKSet jwkSet,
        X509Certificate x509Certificate,
        Versioned<CertificateBundle> versionedCertificateBundle
) {

    public boolean checkCertificateValidity() {
        return ofNullable(x509Certificate())
                .map(CertificateUtils::checkValidity)
                .orElse(Boolean.FALSE);
    }

    /* Builder */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private JWKSet jwkSet;
        private X509Certificate x509Certificate;
        private Versioned<CertificateBundle> versionedCertificateBundle;

        public Builder jwkSet(JWKSet jwkSet) {
            this.jwkSet = jwkSet;
            return this;
        }

        public Builder x509Certificate(X509Certificate x509Certificate) {
            this.x509Certificate = x509Certificate;
            return this;
        }

        public Builder versionedCertificateBundle(Versioned<CertificateBundle> versionedCertificateBundle) {
            this.versionedCertificateBundle = versionedCertificateBundle;
            return this;
        }

        @NonNull
        public VaultJwkSetData build() {
            return new VaultJwkSetData(this.jwkSet, this.x509Certificate, this.versionedCertificateBundle);
        }

    }

}
