package org.development.wide.world.spring.vault.jwks.internal;

import org.development.wide.world.spring.jwks.data.JwkSetData;
import org.development.wide.world.spring.jwks.spi.JwksCertificateRotator;
import org.development.wide.world.spring.jwks.spi.RetryableJwksCertificateRotator;
import org.development.wide.world.spring.vault.jwks.property.DynamicVaultJwksInternalProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.vault.VaultException;

/**
 * Vault-based implementation of the {@link RetryableJwksCertificateRotator}
 *
 * @see RetryableJwksCertificateRotator
 * @see DynamicVaultJwksInternalProperties
 */
public class RetryableVaultJwksCertificateRotator implements RetryableJwksCertificateRotator {

    private static final Logger logger = LoggerFactory.getLogger(RetryableVaultJwksCertificateRotator.class);

    private final JwksCertificateRotator certificateRotator;
    private final DynamicVaultJwksInternalProperties properties;
    private final RetryTemplate certificateRotationRetryTemplate;

    public RetryableVaultJwksCertificateRotator(@NonNull final JwksCertificateRotator certificateRotator,
                                                @NonNull final DynamicVaultJwksInternalProperties properties) {
        this.certificateRotator = certificateRotator;
        this.properties = properties;
        this.certificateRotationRetryTemplate = RetryTemplate.builder()
                .maxAttempts(properties.certificateRotationRetries())
                .retryOn(VaultException.class)
                .build();
    }

    /**
     * @see RetryableJwksCertificateRotator#rotate()
     */
    @Override
    public JwkSetData rotate() {
        final String certificatePath = properties.versionedKv().certificatePath();
        return certificateRotator.rotate(rotationFunction -> certificateRotationRetryTemplate.execute(context -> {
            if (logger.isDebugEnabled()) {
                logger.debug("Tray to rotate certificate");
            }
            return rotationFunction.apply(certificatePath);
        }));
    }

}
