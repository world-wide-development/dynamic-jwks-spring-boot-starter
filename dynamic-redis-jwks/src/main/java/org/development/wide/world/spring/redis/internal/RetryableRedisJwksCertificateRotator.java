package org.development.wide.world.spring.redis.internal;

import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.data.CertificateRotationData;
import org.development.wide.world.spring.jwks.data.JwkSetData;
import org.development.wide.world.spring.jwks.spi.CertificateRotationFunction;
import org.development.wide.world.spring.jwks.spi.JwksCertificateRotator;
import org.development.wide.world.spring.jwks.spi.RetryableJwksCertificateRotator;
import org.development.wide.world.spring.redis.exception.RedisOperationException;
import org.development.wide.world.spring.redis.property.DynamicRedisJwksInternalProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.retry.support.RetryTemplate;

import java.time.Duration;

/**
 * Vault-based implementation of the {@link RetryableJwksCertificateRotator}
 *
 * @see RetryableJwksCertificateRotator
 */
public class RetryableRedisJwksCertificateRotator implements RetryableJwksCertificateRotator {

    private static final Logger logger = LoggerFactory.getLogger(RetryableRedisJwksCertificateRotator.class);

    private final JwksCertificateRotator jwksRotator;
    private final RetryTemplate rotationRetryTemplate;
    private final DynamicRedisJwksInternalProperties properties;

    public RetryableRedisJwksCertificateRotator(@NonNull final JwksCertificateRotator jwksRotator,
                                                @NonNull final DynamicRedisJwksInternalProperties properties) {
        this.properties = properties;
        this.jwksRotator = jwksRotator;
        this.rotationRetryTemplate = RetryTemplate.builder()
                .fixedBackoff(properties.certificateRotation().retry().fixedBackoff())
                .maxAttempts(properties.certificateRotation().retry().maxAttempts())
                .retryOn(RedisOperationException.class)
                .build();
    }

    /**
     * @see RetryableJwksCertificateRotator#rotate()
     */
    @Override
    public JwkSetData rotate() {
        return jwksRotator.rotate(this::rotateCertificateWithRetry);
    }

    /* Private methods */
    private CertificateData rotateCertificateWithRetry(@NonNull final CertificateRotationFunction function) {
        return rotationRetryTemplate.execute(context -> rotateCertificate(function));
    }

    private CertificateData rotateCertificate(@NonNull final CertificateRotationFunction certificateRotationFunction) {
        final String certificateKey = properties.kv().certificateKey();
        final Duration rotateBefore = properties.certificateRotation().rotateBefore();
        if (logger.isDebugEnabled()) {
            logger.debug("Tray to rotate certificate");
        }
        final CertificateRotationData rotationData = CertificateRotationData.builder()
                .rotateBefore(rotateBefore)
                .key(certificateKey)
                .build();
        return certificateRotationFunction.apply(rotationData);
    }

}
