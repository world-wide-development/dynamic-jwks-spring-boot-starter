package org.development.wide.world.spring.vault.jwks.data;

import org.development.wide.world.spring.vault.jwks.util.CertificateUtils;
import org.springframework.lang.NonNull;
import org.springframework.vault.support.Versioned.Version;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import static java.util.Optional.ofNullable;

public record KeyStoreData(
        Version version,
        String serialNumber,
        PrivateKey privateKey,
        X509Certificate x509Certificate
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

        private Version version;
        private String serialNumber;
        private PrivateKey privateKey;
        private X509Certificate x509Certificate;

        public Builder version(Version version) {
            this.version = version;
            return this;
        }

        public Builder serialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
            return this;
        }

        public Builder privateKey(PrivateKey privateKey) {
            this.privateKey = privateKey;
            return this;
        }

        public Builder x509Certificate(X509Certificate x509Certificate) {
            this.x509Certificate = x509Certificate;
            return this;
        }

        @NonNull
        public KeyStoreData build() {
            return new KeyStoreData(version, serialNumber, privateKey, x509Certificate);
        }

    }

}
