package org.development.wide.world.spring.vault.jwks.internal;

import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.data.KeyStoreSource;
import org.development.wide.world.spring.jwks.internal.InternalKeyStore;
import org.development.wide.world.spring.jwks.spi.CertificateRepository;
import org.development.wide.world.spring.jwks.template.KeyStoreTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.vault.core.VaultVersionedKeyValueOperations;
import org.springframework.vault.support.Versioned;
import org.springframework.vault.support.Versioned.Metadata;
import org.springframework.vault.support.Versioned.Version;

import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * Vault-based implementation fo the {@link CertificateRepository}
 *
 * @see CertificateRepository
 */
public class VaultCertificateRepository implements CertificateRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(VaultCertificateRepository.class);

    private final KeyStoreTemplate keyStoreTemplate;
    private final VaultVersionedKeyValueOperations keyValueOperations;

    public VaultCertificateRepository(@NonNull final KeyStoreTemplate keyStoreTemplate,
                                      @NonNull final VaultVersionedKeyValueOperations keyValueOperations) {
        this.keyStoreTemplate = keyStoreTemplate;
        this.keyValueOperations = keyValueOperations;
    }

    /**
     * @see CertificateRepository#findOne(String)
     */
    @Override
    public Optional<CertificateData> findOne(final String key) {
        return ofNullable(keyValueOperations.get(key, KeyStoreSource.class)).flatMap(versionedKeyStoreSource -> {
            final KeyStoreSource keyStoreSource = versionedKeyStoreSource.getData();
            if (keyStoreSource == null) {
                return Optional.empty();
            }
            final InternalKeyStore internalKeyStore = keyStoreTemplate.reloadFromSource(keyStoreSource);
            final CertificateData certificateData = CertificateData.builder()
                    .version(versionedKeyStoreSource.getVersion().getVersion())
                    .x509Certificate(internalKeyStore.getX509Certificate())
                    .privateKey(internalKeyStore.getPrivateKey())
                    .serialNumber(keyStoreSource.serialNumber())
                    .build();
            return Optional.of(certificateData);
        });
    }

    /**
     * @see CertificateRepository#saveOne(String, CertificateData)
     */
    @Override
    public CertificateData saveOne(final String key, @NonNull final CertificateData certificateData) {
        final Version keyVersion = Version.from(certificateData.version());
        final KeyStoreSource keyStoreSource = keyStoreTemplate.saveCertificate(certificateData);
        final Versioned<KeyStoreSource> versionedKeyStoreSource = Versioned.create(keyStoreSource, keyVersion);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("The new certificate has been issued {}", versionedKeyStoreSource.getVersion());
        }
        final Metadata metadata = keyValueOperations.put(key, versionedKeyStoreSource);
        if (LOGGER.isDebugEnabled()) {
            if (metadata.isDeleted()) {
                LOGGER.debug("Certificate was deleted");
            }
            if (metadata.isDestroyed()) {
                LOGGER.debug("Certificate was destroyed");
            }
            LOGGER.debug("Put certificate to versioned KV storage {}", metadata);
        }
        return findOne(key)
                .orElseThrow(() -> new IllegalArgumentException("Key store data cannot be null"));
    }

}
