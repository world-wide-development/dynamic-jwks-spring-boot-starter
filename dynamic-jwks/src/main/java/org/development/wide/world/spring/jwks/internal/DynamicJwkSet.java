package org.development.wide.world.spring.jwks.internal;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.development.wide.world.spring.jwks.data.JwkSetData;
import org.development.wide.world.spring.jwks.spi.JwkSetDataHolder;
import org.development.wide.world.spring.jwks.spi.RetryableJwksCertificateRotator;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * Implementation of the {@link JWKSource}.
 * <br>
 * Designed to provide the ability to automatically rotate certificates.
 *
 * @see RetryableJwksCertificateRotator
 * @see JWKSource<SecurityContext>
 * @see JwkSetData
 */
public class DynamicJwkSet implements JWKSource<SecurityContext> {

    private final JwkSetDataHolder jwkSetDataHolder;

    public DynamicJwkSet(final JwkSetDataHolder jwkSetDataHolder) {
        this.jwkSetDataHolder = jwkSetDataHolder;
    }

    /**
     * Provides the list of the {@link JWK}s with valid certificates.
     * Automatically issue a new certificate in case of the current certificate expiration
     *
     * @see JWKSource#get(JWKSelector, SecurityContext)
     */
    @Override
    public List<JWK> get(@NonNull final JWKSelector jwkSelector, final SecurityContext context) {
        final JwkSetData jwkSetData = jwkSetDataHolder.getActual();
        final JWKSet jwkSet = jwkSetData.jwkSet();
        return jwkSelector.select(jwkSet);
    }

}
