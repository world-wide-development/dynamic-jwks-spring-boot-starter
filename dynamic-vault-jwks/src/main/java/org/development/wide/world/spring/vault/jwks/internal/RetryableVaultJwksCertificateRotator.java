package org.development.wide.world.spring.vault.jwks.internal;

import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.data.CertificateRotationData;
import org.development.wide.world.spring.jwks.data.JwkSetData;
import org.development.wide.world.spring.jwks.spi.CertificateRotationFunction;
import org.development.wide.world.spring.jwks.spi.JwksCertificateRotator;
import org.development.wide.world.spring.jwks.spi.RetryableJwksCertificateRotator;
import org.development.wide.world.spring.vault.jwks.exception.CertificateRotationException;
import org.development.wide.world.spring.vault.jwks.property.DynamicVaultJwksInternalProperties;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.retry.RetryException;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.vault.VaultException;

/**
 * Vault-based implementation of the {@link RetryableJwksCertificateRotator}
 *
 * @see RetryableJwksCertificateRotator
 * @see DynamicVaultJwksInternalProperties
 */
public class RetryableVaultJwksCertificateRotator implements RetryableJwksCertificateRotator {

    private static final Logger logger = LoggerFactory.getLogger(RetryableVaultJwksCertificateRotator.class);

    private final JwksCertificateRotator jwksRotator;
    private final RetryTemplate rotationRetryTemplate;
    private final DynamicVaultJwksInternalProperties properties;

    public RetryableVaultJwksCertificateRotator(@NonNull final JwksCertificateRotator jwksRotator,
                                                @NonNull final DynamicVaultJwksInternalProperties properties) {
        final RetryPolicy rotationRetryPolicy = RetryPolicy.builder()
                .maxRetries(properties.certificateRotationRetries())
                .includes(VaultException.class)
                .build();
        final RetryTemplate retryTemplate = new RetryTemplate(rotationRetryPolicy);
        this(jwksRotator, retryTemplate, properties);
    }

    public RetryableVaultJwksCertificateRotator(@NonNull final JwksCertificateRotator jwksRotator,
                                                @NonNull final RetryTemplate rotationRetryTemplate,
                                                @NonNull final DynamicVaultJwksInternalProperties properties) {
        this.properties = properties;
        this.jwksRotator = jwksRotator;
        this.rotationRetryTemplate = rotationRetryTemplate;
    }

    /**
     * @see RetryableJwksCertificateRotator#rotate()
     */
    @Override
    public JwkSetData rotate() {
        return jwksRotator.rotate(this::rotateCertificateWithRetry);
    }

    /* Private methods */
    private CertificateData rotateCertificate(@NonNull final CertificateRotationFunction function) {
        final String certificateKey = properties.versionedKv().certificatePath();
        if (logger.isDebugEnabled()) {
            logger.debug("Tray to rotate certificate");
        }
        final CertificateRotationData rotationData = CertificateRotationData.builder()
                .key(certificateKey)
                .build();
        return function.apply(rotationData);
    }

    private CertificateData rotateCertificateWithRetry(@NonNull final CertificateRotationFunction function) {
        try {
            return this.rotationRetryTemplate.execute(() -> rotateCertificate(function));
        } catch (RetryException e) {
            throw new CertificateRotationException("On retry a certificate rotation exception", e);
        }
    }

}
