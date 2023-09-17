package org.development.wide.world.spring.vault.jwks.internal;

import com.nimbusds.jose.jwk.JWKSet;
import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.data.JwkSetData;
import org.development.wide.world.spring.jwks.internal.JwkSetConverter;
import org.development.wide.world.spring.jwks.spi.CertificateIssuer;
import org.development.wide.world.spring.jwks.spi.CertificateRepository;
import org.development.wide.world.spring.jwks.spi.JwksCertificateRotator;
import org.development.wide.world.spring.vault.jwks.property.DynamicVaultJwksInternalProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.vault.VaultException;

/**
 * Vault-based implementation of the {@link JwksCertificateRotator}
 *
 * @see JwkSetConverter
 * @see CertificateIssuer
 * @see CertificateRepository
 * @see JwksCertificateRotator
 * @see DynamicVaultJwksInternalProperties
 */
public class VaultJwksCertificateRotator implements JwksCertificateRotator {

    public static final Integer INITIAL_VERSION = 0;

    private static final Logger LOGGER = LoggerFactory.getLogger(VaultJwksCertificateRotator.class);

    private final JwkSetConverter jwkSetConverter;
    private final CertificateIssuer certificateIssuer;
    private final CertificateRepository certificateRepository;
    private final DynamicVaultJwksInternalProperties properties;
    private final RetryTemplate certificateRotationRetryTemplate;

    public VaultJwksCertificateRotator(@NonNull final JwkSetConverter jwkSetConverter,
                                       @NonNull final CertificateIssuer certificateIssuer,
                                       @NonNull final CertificateRepository certificateRepository,
                                       @NonNull final DynamicVaultJwksInternalProperties properties) {
        this.properties = properties;
        this.jwkSetConverter = jwkSetConverter;
        this.certificateIssuer = certificateIssuer;
        this.certificateRepository = certificateRepository;
        this.certificateRotationRetryTemplate = RetryTemplate.builder()
                .maxAttempts(properties.certificateRotationRetries())
                .retryOn(VaultException.class)
                .build();
    }

    /**
     * @see JwksCertificateRotator#rotate()
     */
    @Override
    public JwkSetData rotate() {
        final CertificateData certificateData = certificateRotationRetryTemplate.execute(context -> {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Tray to rotate certificate");
            }
            return rotateCertificateData();
        });
        final JWKSet jwkSet = jwkSetConverter.convert(certificateData);
        return JwkSetData.builder()
                .keyStoreData(certificateData)
                .jwkSet(jwkSet)
                .build();
    }

    /* Private methods */
    private CertificateData rotateCertificateData() {
        final String certificatePath = properties.versionedKv().certificatePath();
        return certificateRepository.findOne(certificatePath).map(certificateData -> {
            if (certificateData.checkCertificateValidity()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Certificate form Versioned KV storage is valid and will be used");
                }
                return certificateData;
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Certificate from Versioned KV storage is invalid and will be immediately rotated");
            }
            final Integer lastVersion = certificateData.version();
            return rotateVersionedCertificateData(lastVersion);
        }).orElseGet(() -> rotateVersionedCertificateData(INITIAL_VERSION));
    }

    private CertificateData rotateVersionedCertificateData(@NonNull final Integer version) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Rotate certificate {}", version);
        }
        final String certificatePath = properties.versionedKv().certificatePath();
        final CertificateData certificateData = certificateIssuer.issueOne().toBuilder()
                .version(version)
                .build();
        return certificateRepository.saveOne(certificatePath, certificateData);
    }

}
