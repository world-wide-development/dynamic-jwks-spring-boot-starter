package org.development.wide.world.spring.jwks.data;

import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.lang.NonNull;

import static java.util.Optional.ofNullable;

public record JwkSetData(
        JWKSet jwkSet,
        CertificateData certificateData
) {

    public boolean checkCertificateValidity() {
        return ofNullable(certificateData())
                .map(CertificateData::checkCertificateValidity)
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

        public Builder keyStoreData(CertificateData certificateData) {
            this.certificateData = certificateData;
            return this;
        }

        @NonNull
        public JwkSetData build() {
            return new JwkSetData(this.jwkSet, this.certificateData);
        }

    }

}
