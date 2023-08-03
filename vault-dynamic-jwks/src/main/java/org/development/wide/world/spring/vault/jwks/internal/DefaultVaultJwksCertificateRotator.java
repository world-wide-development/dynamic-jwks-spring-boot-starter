package org.development.wide.world.spring.vault.jwks.internal;

import com.nimbusds.jose.jwk.JWKSet;
import org.development.wide.world.spring.vault.jwks.data.VaultJwkSetHolder;
import org.development.wide.world.spring.vault.jwks.spi.VaultJwksCertificateRotator;
import org.development.wide.world.spring.vault.jwks.util.X509CertificateUtils;
import org.development.wide.world.spring.vault.jwks.property.VaultDynamicJwksProperties;
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
import java.text.ParseException;
import java.time.Duration;

import static java.util.Optional.ofNullable;

public class DefaultVaultJwksCertificateRotator implements VaultJwksCertificateRotator {

    public static final String CERTIFICATE_BUNDLE_CANNOT_BE_NULL_MSG = "Certificate bundle cannot be null";
    public static final String VERSIONED_CERTIFICATE_BUNDLE_CANNOT_BE_NULL_MSG = "Versioned certificate bundle cannot be null";

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultVaultJwksCertificateRotator.class);

    private final Duration timeToLive;
    private final VaultPkiOperations pkiOperations;
    private final VaultDynamicJwksProperties properties;
    private final VaultVersionedKeyValueOperations keyValueOperations;

    public DefaultVaultJwksCertificateRotator(@NonNull final VaultTemplate vaultTemplate,
                                              @NonNull final VaultDynamicJwksProperties properties) {
        this.properties = properties;
        this.pkiOperations = vaultTemplate.opsForPki(properties.pkiPath());
        this.timeToLive = properties.pkiCertificateTtl().plus(Duration.ofMinutes(1));
        this.keyValueOperations = vaultTemplate.opsForVersionedKeyValue(properties.versionedKeyValuePath());
    }

    @Override
    public VaultJwkSetHolder rotate() {
        final Versioned<CertificateBundle> versionedCertificateBundle = rotateVersionedCertificateBundle();
        final CertificateBundle certificateBundle = ofNullable(versionedCertificateBundle.getData())
                .orElseThrow(() -> new UnsupportedOperationException(CERTIFICATE_BUNDLE_CANNOT_BE_NULL_MSG));
        final X509Certificate x509Certificate = certificateBundle.getX509Certificate();
        final JWKSet jwkSet = extractJwkSet(certificateBundle);
        return VaultJwkSetHolder.builder()
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
                return versionedCertificateBundle;
            }
            final Version lastVersion = versionedCertificateBundle.getVersion();
            final int nextVersionNumber = lastVersion.getVersion() + 1;
            final Version nextVersion = Version.from(nextVersionNumber);
            return saveVersionedCertificateBundle(nextVersion);
        }).orElseGet(() -> saveVersionedCertificateBundle(Version.unversioned()));
    }

    private JWKSet parseEncodedCertificate(@NonNull final String encodedCertificate) {
        try {
            return JWKSet.parse(encodedCertificate);
        } catch (ParseException e) {
            throw new UnsupportedOperationException("Can't parse encoded certificate", e);
        }
    }

    private JWKSet extractJwkSet(@NonNull final CertificateBundle certificateBundle) {
        final String encodedCertificate = certificateBundle.getCertificate();
        return parseEncodedCertificate(encodedCertificate);
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

    private Versioned<CertificateBundle> saveVersionedCertificateBundle(@NonNull final Version nextVersion) {
        final CertificateBundle certificateBundle = issueCertificateBundle(timeToLive);
        final Versioned<CertificateBundle> versionedCertificateBundle = Versioned.create(certificateBundle, nextVersion);
        final Metadata metadata = keyValueOperations.put(properties.certificatePath(), versionedCertificateBundle);
        if (metadata.isDeleted() && LOGGER.isDebugEnabled()) {
            LOGGER.debug("Authorization certificate was deleted");
        }
        if (metadata.isDestroyed() && LOGGER.isDebugEnabled()) {
            LOGGER.debug("Authorization certificate was destroyed");
        }
        return ofNullable(keyValueOperations.get(properties.certificatePath(), CertificateBundle.class))
                .orElseThrow(() -> new IllegalArgumentException(VERSIONED_CERTIFICATE_BUNDLE_CANNOT_BE_NULL_MSG));
    }

    private boolean checkCertificateBundleValidity(@NonNull final Versioned<CertificateBundle> versionedCertificateBundle) {
        return ofNullable(versionedCertificateBundle.getData())
                .map(Certificate::getX509Certificate)
                .map(X509CertificateUtils::checkValidity)
                .orElse(Boolean.FALSE);
    }

}
