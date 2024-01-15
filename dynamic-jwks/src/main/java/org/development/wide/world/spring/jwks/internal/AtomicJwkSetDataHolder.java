package org.development.wide.world.spring.jwks.internal;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import org.development.wide.world.spring.jwks.data.JwkSetData;
import org.development.wide.world.spring.jwks.spi.JwkSetDataHolder;
import org.development.wide.world.spring.jwks.spi.RetryableJwksCertificateRotator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.List;
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
        return jwkSetHolderAtomicReference.updateAndGet(lastJwkSetData -> {
            if (Objects.isNull(lastJwkSetData)) {
                return certificateRotator.rotate();
            }
            if (lastJwkSetData.checkCertificateValidity(rotateBefore)) {
                if (logger.isTraceEnabled()) {
                    logger.trace("JWK Set still fresh, rotation is not necessary");
                }
                return lastJwkSetData;
            }
            if (logger.isTraceEnabled()) {
                logger.trace("JWK Set expired, rotating it out");
            }
            final JwkSetData newJwkSetData = certificateRotator.rotate();
            final JWKSet nextJwkSet = this.composeJwkSets(newJwkSetData, lastJwkSetData);
            return JwkSetData.builder()
                    .certificateData(newJwkSetData.certificateData())
                    .jwkSet(nextJwkSet)
                    .build();
        });
    }

    /* Private methods */
    @NonNull
    private JWKSet composeJwkSets(@NonNull final JwkSetData newJwkSetData, @NonNull final JwkSetData lastJwkSetData) {
        final JWK newJwkKey = newJwkSetData.jwkSet().getKeys().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("New JWK Set keys must have at lest one key"));
        final JWK lastJwkKey = lastJwkSetData.jwkSet().getKeys().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Last JWK Set keys must have at lest one key"));
        final List<JWK> nextJwkKeys = List.of(newJwkKey, lastJwkKey);
        return new JWKSet(nextJwkKeys);
    }

}
