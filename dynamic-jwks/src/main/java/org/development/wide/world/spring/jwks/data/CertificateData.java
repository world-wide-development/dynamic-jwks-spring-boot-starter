package org.development.wide.world.spring.jwks.data;

import org.development.wide.world.spring.jwks.util.CertificateUtils;
import org.springframework.lang.NonNull;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

import static java.util.Optional.ofNullable;

public record CertificateData(
        Integer version,
        String serialNumber,
        PrivateKey privateKey,
        X509Certificate x509Certificate,
        List<X509Certificate> x509Certificates
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

    public Builder toBuilder() {
        return new Builder()
                .x509Certificates(this.x509Certificates())
                .x509Certificate(this.x509Certificate())
                .serialNumber(this.serialNumber())
                .privateKey(this.privateKey())
                .version(this.version());
    }

    public static final class Builder {

        private Integer version;
        private String serialNumber;
        private PrivateKey privateKey;
        private X509Certificate x509Certificate;
        private List<X509Certificate> x509Certificates;

        public Builder version(Integer version) {
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

        public Builder x509Certificates(List<X509Certificate> x509Certificates) {
            this.x509Certificates = x509Certificates;
            return this;
        }

        @NonNull
        public CertificateData build() {
            return new CertificateData(version, serialNumber, privateKey, x509Certificate, x509Certificates);
        }

    }

}
