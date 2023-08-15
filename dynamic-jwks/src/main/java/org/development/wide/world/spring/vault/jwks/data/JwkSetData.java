package org.development.wide.world.spring.vault.jwks.data;

import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.lang.NonNull;

import static java.util.Optional.ofNullable;

public record JwkSetData(
        JWKSet jwkSet,
        KeyStoreData keyStoreData
) {

    public boolean checkCertificateValidity() {
        return ofNullable(keyStoreData())
                .map(KeyStoreData::checkCertificateValidity)
                .orElse(Boolean.FALSE);
    }

    /* Builder */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private JWKSet jwkSet;
        private KeyStoreData keyStoreData;

        public Builder jwkSet(JWKSet jwkSet) {
            this.jwkSet = jwkSet;
            return this;
        }

        public Builder keyStoreData(KeyStoreData keyStoreData) {
            this.keyStoreData = keyStoreData;
            return this;
        }

        @NonNull
        public JwkSetData build() {
            return new JwkSetData(this.jwkSet, this.keyStoreData);
        }

    }

}
