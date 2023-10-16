package org.development.wide.world.spring.jwks.internal;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.development.wide.world.spring.jwks.data.JwkSetData;
import org.development.wide.world.spring.jwks.spi.RetryableJwksCertificateRotator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

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

    private static final Logger logger = LoggerFactory.getLogger(DynamicJwkSet.class);

    private final RetryableJwksCertificateRotator certificateRotator;
    private final AtomicReference<JwkSetData> jwkSetHolderAtomicReference;

    public DynamicJwkSet(final RetryableJwksCertificateRotator certificateRotator) {
        this(certificateRotator, new AtomicReference<>());
    }

    public DynamicJwkSet(final RetryableJwksCertificateRotator certificateRotator,
                         final AtomicReference<JwkSetData> jwkSetHolderAtomicReference) {
        Assert.notNull(certificateRotator, "certificateRotator cannot be null");
        Assert.notNull(jwkSetHolderAtomicReference, "jwkSetHolderAtomicReference cannot be null");
        this.jwkSetHolderAtomicReference = jwkSetHolderAtomicReference;
        this.certificateRotator = certificateRotator;
    }

    /**
     * Provides the list of the {@link JWK}s with valid certificates.
     * Automatically issuer new certificate in case of the current certificate expiration
     *
     * @see JWKSource#get(JWKSelector, SecurityContext)
     */
    @Override
    public List<JWK> get(@NonNull final JWKSelector jwkSelector, final SecurityContext context) {
        final JwkSetData jwkSetData = jwkSetHolderAtomicReference.updateAndGet(jwkSetHolder -> {
            if (Objects.nonNull(jwkSetHolder) && jwkSetHolder.checkCertificateValidity()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("JWK Set still fresh, rotation is not necessary");
                }
                return jwkSetHolder;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("JWK Set expired, rotating it out");
            }
            return certificateRotator.rotate();
        });
        final JWKSet jwkSet = jwkSetData.jwkSet();
        return jwkSelector.select(jwkSet);
    }

}
