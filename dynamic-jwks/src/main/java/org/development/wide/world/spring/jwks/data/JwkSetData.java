package org.development.wide.world.spring.jwks.data;

import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.lang.NonNull;

import java.time.Duration;

import static java.util.Optional.ofNullable;

public record JwkSetData(
        JWKSet jwkSet,
        CertificateData certificateData
) {

    public boolean checkCertificateValidity() {
        return ofNullable(this.certificateData())
                .map(CertificateData::checkCertificateValidity)
                .orElse(Boolean.FALSE);
    }

    public boolean checkCertificateValidity(final Duration validBefore) {
        return ofNullable(this.certificateData())
                .map(certificateData -> certificateData.checkCertificateValidity(validBefore))
                .orElse(Boolean.FALSE);
    }

    /* Builder */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private JWKSet jwkSet;
        private CertificateData certificateData;

        public Builder jwkSet(JWKSet jwkSet) {
            this.jwkSet = jwkSet;
            return this;
        }

        public Builder certificateData(CertificateData certificateData) {
            this.certificateData = certificateData;
            return this;
        }

        @NonNull
        public JwkSetData build() {
            return new JwkSetData(this.jwkSet, this.certificateData);
        }

    }

}
