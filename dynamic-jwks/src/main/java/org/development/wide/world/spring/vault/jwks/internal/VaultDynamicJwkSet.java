package org.development.wide.world.spring.vault.jwks.internal;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.development.wide.world.spring.vault.jwks.data.JwkSetData;
import org.development.wide.world.spring.vault.jwks.spi.JwksCertificateRotator;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Implementation of the {@link JWKSource}.
 * <br>
 * Designed to provide the ability automatically rotate certificates.
 *
 * @see JWKSource<SecurityContext>
 * @see JwksCertificateRotator
 * @see JwkSetData
 */
public class VaultDynamicJwkSet implements JWKSource<SecurityContext> {

    private final JwksCertificateRotator certificateRotator;

    private final AtomicReference<JwkSetData> jwkSetHolderAtomicReference = new AtomicReference<>();

    public VaultDynamicJwkSet(final JwksCertificateRotator certificateRotator) {
        this.certificateRotator = certificateRotator;
    }

    /**
     * Provides the list of the {@link JWK}s with valid certificates.
     * Automatically issues new certificate in case of the current certificate expiration
     *
     * @see JWKSource#get(JWKSelector, SecurityContext)
     */
    @Override
    public List<JWK> get(@NonNull final JWKSelector jwkSelector, final SecurityContext context) {
        final JwkSetData jwkSetData = jwkSetHolderAtomicReference.updateAndGet(jwkSetHolder -> {
            if (Objects.nonNull(jwkSetHolder) && jwkSetHolder.checkCertificateValidity()) {
                return jwkSetHolder;
            }
            return certificateRotator.rotate();
        });
        final JWKSet jwkSet = jwkSetData.jwkSet();
        return jwkSelector.select(jwkSet);
    }

}
