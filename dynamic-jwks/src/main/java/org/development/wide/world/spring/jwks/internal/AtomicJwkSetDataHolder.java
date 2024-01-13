package org.development.wide.world.spring.jwks.internal;

import org.development.wide.world.spring.jwks.data.JwkSetData;
import org.development.wide.world.spring.jwks.spi.JwkSetDataHolder;
import org.development.wide.world.spring.jwks.spi.RetryableJwksCertificateRotator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicJwkSetDataHolder implements JwkSetDataHolder {

    private static final Logger logger = LoggerFactory.getLogger(AtomicJwkSetDataHolder.class);

    private final RetryableJwksCertificateRotator certificateRotator;
    private final AtomicReference<JwkSetData> jwkSetHolderAtomicReference;

    public AtomicJwkSetDataHolder(final RetryableJwksCertificateRotator certificateRotator) {
        this(certificateRotator, new AtomicReference<>());
    }

    public AtomicJwkSetDataHolder(final RetryableJwksCertificateRotator certificateRotator,
                                  final AtomicReference<JwkSetData> jwkSetHolderAtomicReference) {
        Assert.notNull(certificateRotator, "certificateRotator cannot be null");
        Assert.notNull(jwkSetHolderAtomicReference, "jwkSetHolderAtomicReference cannot be null");
        this.certificateRotator = certificateRotator;
        this.jwkSetHolderAtomicReference = jwkSetHolderAtomicReference;
    }

    @Override
    public JwkSetData getActual() {
        return rotateInAdvanceIfAny(Duration.ZERO);
    }

    @Override
    public JwkSetData rotateInAdvanceIfAny(final Duration rotateBefore) {
        return jwkSetHolderAtomicReference.updateAndGet(jwkSetData -> {
            if (Objects.nonNull(jwkSetData) && jwkSetData.checkCertificateValidity(rotateBefore)) {
                if (logger.isTraceEnabled()) {
                    logger.trace("JWK Set still fresh, rotation is not necessary");
                }
                return jwkSetData;
            }
            if (logger.isTraceEnabled()) {
                logger.trace("JWK Set expired, rotating it out");
            }
            return certificateRotator.rotate();
        });
    }

}
