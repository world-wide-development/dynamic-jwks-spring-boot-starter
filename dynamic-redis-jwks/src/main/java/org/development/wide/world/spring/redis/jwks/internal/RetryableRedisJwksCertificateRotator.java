package org.development.wide.world.spring.redis.jwks.internal;

import org.development.wide.world.spring.jwks.data.JwkSetData;
import org.development.wide.world.spring.jwks.spi.JwksCertificateRotator;
import org.development.wide.world.spring.jwks.spi.RetryableJwksCertificateRotator;
import org.development.wide.world.spring.redis.jwks.exception.RedisOperationException;
import org.development.wide.world.spring.redis.jwks.property.DynamicRedisJwksInternalProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.retry.support.RetryTemplate;

/**
 * Vault-based implementation of the {@link RetryableJwksCertificateRotator}
 *
 * @see RetryableJwksCertificateRotator
 */
public class RetryableRedisJwksCertificateRotator implements RetryableJwksCertificateRotator {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetryableRedisJwksCertificateRotator.class);

    private final JwksCertificateRotator certificateRotator;
    private final DynamicRedisJwksInternalProperties properties;
    private final RetryTemplate certificateRotationRetryTemplate;

    public RetryableRedisJwksCertificateRotator(@NonNull final JwksCertificateRotator certificateRotator,
                                                @NonNull final DynamicRedisJwksInternalProperties properties) {
        this.properties = properties;
        this.certificateRotator = certificateRotator;
        this.certificateRotationRetryTemplate = RetryTemplate.builder()
                .fixedBackoff(properties.certificateRotationRetryFixedBackoff())
                .maxAttempts(properties.certificateRotationRetries())
                .retryOn(RedisOperationException.class)
                .build();
    }

    /**
     * @see RetryableJwksCertificateRotator#rotate()
     */
    @Override
    public JwkSetData rotate() {
        final String certificateKey = properties.kv().certificateKey();
        return certificateRotator.rotate(rotationFunction -> certificateRotationRetryTemplate.execute(context -> {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Tray to rotate certificate");
            }
            return rotationFunction.apply(certificateKey);
        }));
    }

}
