package org.development.wide.world.spring.vault.jwks.internal;

import com.nimbusds.jose.jwk.JWKSet;
import org.development.wide.world.spring.vault.jwks.data.JwkSetData;
import org.development.wide.world.spring.vault.jwks.data.KeyStoreData;
import org.development.wide.world.spring.vault.jwks.property.DynamicJwksProperties;
import org.development.wide.world.spring.vault.jwks.spi.CertificateIssuer;
import org.development.wide.world.spring.vault.jwks.spi.JwksCertificateRotator;
import org.development.wide.world.spring.vault.jwks.spi.KeyStoreKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.vault.VaultException;
import org.springframework.vault.support.CertificateBundle;
import org.springframework.vault.support.Versioned.Version;

public class VaultJwksCertificateRotator implements JwksCertificateRotator {

    private static final Logger LOGGER = LoggerFactory.getLogger(VaultJwksCertificateRotator.class);

    private final KeyStoreKeeper keyStoreKeeper;
    private final JwkSetConverter jwkSetConverter;
    private final DynamicJwksProperties properties;
    private final CertificateIssuer certificateIssuer;
    private final RetryTemplate certificateRotationRetryTemplate;

    public VaultJwksCertificateRotator(@NonNull final KeyStoreKeeper keyStoreKeeper,
                                       @NonNull final JwkSetConverter jwkSetConverter,
                                       @NonNull final DynamicJwksProperties properties,
                                       @NonNull final CertificateIssuer certificateIssuer) {
        this.properties = properties;
        this.keyStoreKeeper = keyStoreKeeper;
        this.jwkSetConverter = jwkSetConverter;
        this.certificateIssuer = certificateIssuer;
        this.certificateRotationRetryTemplate = RetryTemplate.builder()
                .maxAttempts(properties.certificateRotationRetries())
                .retryOn(VaultException.class)
                .build();
    }

    @Override
    public JwkSetData rotate() {
        final KeyStoreData keyStoreData = certificateRotationRetryTemplate.execute(context -> {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Tray to rotate certificate");
            }
            return rotateKeyStoreData();
        });
        final JWKSet jwkSet = jwkSetConverter.convert(keyStoreData);
        return JwkSetData.builder()
                .keyStoreData(keyStoreData)
                .jwkSet(jwkSet)
                .build();
    }

    /* Private methods */
    private KeyStoreData rotateKeyStoreData() {
        final String certificatePath = properties.certificatePath();
        return keyStoreKeeper.findOne(certificatePath).map(keyStoreData -> {
            if (keyStoreData.checkCertificateValidity()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Certificate form Versioned KV storage is valid and will be used");
                }
                return keyStoreData;
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Certificate from Versioned KV storage is invalid and will be immediately rotated");
            }
            final Version lastVersion = keyStoreData.version();
            return rotateVersionedKeyStoreData(lastVersion);
        }).orElseGet(() -> rotateVersionedKeyStoreData(Version.unversioned()));
    }

    private KeyStoreData rotateVersionedKeyStoreData(@NonNull final Version version) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Rotate certificate {}", version);
        }
        final String path = properties.certificatePath();
        final CertificateBundle certificateBundle = certificateIssuer.issueOne();
        return keyStoreKeeper.saveOne(path, version, certificateBundle);
    }

}
