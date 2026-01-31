package org.development.wide.world.spring.redis.internal;

import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.data.CertificateRotationData;
import org.development.wide.world.spring.jwks.data.JwkSetData;
import org.development.wide.world.spring.jwks.spi.CertificateRotationFunction;
import org.development.wide.world.spring.jwks.spi.JwksCertificateRotator;
import org.development.wide.world.spring.jwks.spi.RetryableJwksCertificateRotator;
import org.development.wide.world.spring.redis.exception.CertificateRotationException;
import org.development.wide.world.spring.redis.exception.RedisOperationException;
import org.development.wide.world.spring.redis.property.DynamicRedisJwksInternalProperties;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.retry.RetryException;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;

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
        final RetryPolicy rotationRetryPolicy = RetryPolicy.builder()
                .maxRetries(properties.certificateRotation().retry().maxAttempts())
                .delay(properties.certificateRotation().retry().fixedBackoff())
                .includes(RedisOperationException.class)
                .build();
        this.rotationRetryTemplate = new RetryTemplate(rotationRetryPolicy);
    }

    /**
     * @see RetryableJwksCertificateRotator#rotate()
     */
    @Override
    public JwkSetData rotate() {
        return this.jwksRotator.rotate(this::rotateCertificateWithRetry);
    }

    /* Private methods */
    private CertificateData rotateCertificateWithRetry(@NonNull final CertificateRotationFunction function) {
        try {
            return this.rotationRetryTemplate.execute(() -> rotateCertificate(function));
        } catch (RetryException e) {
            throw new CertificateRotationException("On retry a certificate rotation exception", e);
        }
    }

    private CertificateData rotateCertificate(@NonNull final CertificateRotationFunction certificateRotationFunction) {
        final String certificateKey = this.properties.kv().certificateKey();
        final Duration rotateBefore = this.properties.certificateRotation().rotateBefore();
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
