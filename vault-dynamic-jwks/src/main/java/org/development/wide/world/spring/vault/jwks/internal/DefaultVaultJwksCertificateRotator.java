package org.development.wide.world.spring.vault.jwks.internal;

import com.nimbusds.jose.jwk.JWKSet;
import org.development.wide.world.spring.vault.jwks.data.VaultJwkSetData;
import org.development.wide.world.spring.vault.jwks.property.VaultDynamicJwksProperties;
import org.development.wide.world.spring.vault.jwks.spi.VaultJwksCertificateRotator;
import org.development.wide.world.spring.vault.jwks.util.CertificateUtils;
import org.development.wide.world.spring.vault.jwks.util.JwkSetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.vault.core.VaultPkiOperations;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.core.VaultVersionedKeyValueOperations;
import org.springframework.vault.support.*;
import org.springframework.vault.support.Versioned.Metadata;
import org.springframework.vault.support.Versioned.Version;

import java.security.cert.X509Certificate;
import java.time.Duration;

import static java.util.Optional.ofNullable;

public class DefaultVaultJwksCertificateRotator implements VaultJwksCertificateRotator {

    public static final String CERTIFICATE_BUNDLE_CANNOT_BE_NULL_MSG = "Certificate bundle cannot be null";
    public static final String VERSIONED_CERTIFICATE_BUNDLE_CANNOT_BE_NULL_MSG = "Versioned certificate bundle cannot be null";

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultVaultJwksCertificateRotator.class);

    private final VaultPkiOperations pkiOperations;
    private final VaultDynamicJwksProperties properties;
    private final VaultVersionedKeyValueOperations keyValueOperations;

    public DefaultVaultJwksCertificateRotator(@NonNull final VaultTemplate vaultTemplate,
                                              @NonNull final VaultDynamicJwksProperties properties) {
        this.properties = properties;
        this.pkiOperations = vaultTemplate.opsForPki(properties.pkiPath());
        this.keyValueOperations = vaultTemplate.opsForVersionedKeyValue(properties.versionedKeyValuePath());
    }

    @Override
    public VaultJwkSetData rotate() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Certificate rotation process started");
        }
        final Versioned<CertificateBundle> versionedCertificateBundle = rotateVersionedCertificateBundle();
        final CertificateBundle certificateBundle = ofNullable(versionedCertificateBundle.getData())
                .orElseThrow(() -> new UnsupportedOperationException(CERTIFICATE_BUNDLE_CANNOT_BE_NULL_MSG));
        final X509Certificate x509Certificate = certificateBundle.getX509Certificate();
        final JWKSet jwkSet = JwkSetUtils.extract(certificateBundle);
        return VaultJwkSetData.builder()
                .versionedCertificateBundle(versionedCertificateBundle)
                .x509Certificate(x509Certificate)
                .jwkSet(jwkSet)
                .build();
    }

    /* Private methods */
    private Versioned<CertificateBundle> rotateVersionedCertificateBundle() {
        final String certificatePath = properties.certificatePath();
        final Class<CertificateBundle> type = CertificateBundle.class;
        return ofNullable(keyValueOperations.get(certificatePath, type)).map(versionedCertificateBundle -> {
            if (checkCertificateBundleValidity(versionedCertificateBundle)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Certificate form Versioned KV storage is valid and will be used");
                }
                return versionedCertificateBundle;
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Certificate from Versioned KV storage is invalid and will be immediately rotated");
            }
            final Version lastVersion = versionedCertificateBundle.getVersion();
//            final Version nextVersion = Version.from(1);
            return rotateVersionedCertificateBundle(lastVersion);
        }).orElseGet(() -> rotateVersionedCertificateBundle(Version.unversioned()));
    }

    private CertificateBundle issueCertificateBundle(@NonNull final Duration timeToLive) {
        final VaultCertificateResponse vaultCertificateResponse = issueCertificate(timeToLive);
        return ofNullable(vaultCertificateResponse.getData())
                .orElseThrow(() -> new UnsupportedOperationException(CERTIFICATE_BUNDLE_CANNOT_BE_NULL_MSG));
    }

    @NonNull
    private VaultCertificateResponse issueCertificate(@NonNull final Duration timeToLive) {
        final VaultCertificateRequest request = VaultCertificateRequest.builder()
                .withAltName(properties.pkiCertificateCommonName())
                .commonName(properties.pkiCertificateCommonName())
                .ttl(timeToLive)
                .build();
        return pkiOperations.issueCertificate(properties.pkiRoleName(), request);
    }

    private Versioned<CertificateBundle> rotateVersionedCertificateBundle(@NonNull final Version version) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Rotate certificate {}", version);
        }
        final CertificateBundle certificateBundle = issueCertificateBundle(properties.pkiCertificateTtl());
        final Versioned<CertificateBundle> versionedCertificateBundle = Versioned.create(certificateBundle, version);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("The new certificate has been issued {}", versionedCertificateBundle.getVersion());
        }
        final Metadata metadata = keyValueOperations.put(properties.certificatePath(), versionedCertificateBundle);
        if (LOGGER.isDebugEnabled()) {
            if (metadata.isDeleted()) {
                LOGGER.debug("Certificate was deleted");
            }
            if (metadata.isDestroyed()) {
                LOGGER.debug("Certificate was destroyed");
            }
            LOGGER.debug("Put certificate to versioned KV storage {}", metadata);
        }
        return ofNullable(keyValueOperations.get(properties.certificatePath(), CertificateBundle.class))
                .orElseThrow(() -> new IllegalArgumentException(VERSIONED_CERTIFICATE_BUNDLE_CANNOT_BE_NULL_MSG));
    }

    private boolean checkCertificateBundleValidity(@NonNull final Versioned<CertificateBundle> versionedCertificateBundle) {
        return ofNullable(versionedCertificateBundle.getData())
                .map(Certificate::getX509Certificate)
                .map(CertificateUtils::checkValidity)
                .orElse(Boolean.FALSE);
    }

}
