package org.development.wide.world.spring.vault.jwks.internal;

import org.development.wide.world.spring.vault.jwks.data.KeyStoreData;
import org.development.wide.world.spring.vault.jwks.data.KeyStoreSource;
import org.development.wide.world.spring.vault.jwks.property.DynamicJwksProperties;
import org.development.wide.world.spring.vault.jwks.spi.KeyStoreKeeper;
import org.development.wide.world.spring.vault.jwks.template.KeyStoreTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.core.VaultVersionedKeyValueOperations;
import org.springframework.vault.support.CertificateBundle;
import org.springframework.vault.support.Versioned;
import org.springframework.vault.support.Versioned.Metadata;
import org.springframework.vault.support.Versioned.Version;

import java.util.Optional;

import static java.util.Optional.ofNullable;

public class VaultKeyStoreKeeper implements KeyStoreKeeper {

    private static final Logger LOGGER = LoggerFactory.getLogger(VaultKeyStoreKeeper.class);

    private final DynamicJwksProperties properties;
    private final KeyStoreTemplate keyStoreTemplate;
    private final VaultVersionedKeyValueOperations keyValueOperations;

    public VaultKeyStoreKeeper(@NonNull final VaultTemplate vaultTemplate,
                               @NonNull final DynamicJwksProperties properties,
                               @NonNull final KeyStoreTemplate keyStoreTemplate) {
        this.properties = properties;
        this.keyStoreTemplate = keyStoreTemplate;
        this.keyValueOperations = vaultTemplate.opsForVersionedKeyValue(properties.versionedKeyValuePath());
    }

    @Override
    public Optional<KeyStoreData> findOne(final String path) {
        return ofNullable(keyValueOperations.get(path, KeyStoreSource.class)).flatMap(versionedKeyStoreSource -> {
            final KeyStoreSource keyStoreSource = versionedKeyStoreSource.getData();
            if (keyStoreSource == null) {
                return Optional.empty();
            }
            final InternalKeyStore internalKeyStore = keyStoreTemplate.reloadFromSource(keyStoreSource);
            final KeyStoreData keyStoreData = KeyStoreData.builder()
                    .x509Certificate(internalKeyStore.getX509Certificate())
                    .version(versionedKeyStoreSource.getVersion())
                    .privateKey(internalKeyStore.getPrivateKey())
                    .serialNumber(keyStoreSource.serialNumber())
                    .build();
            return Optional.of(keyStoreData);
        });
    }

    @Override
    public KeyStoreData saveOne(final String path, final Version version, final CertificateBundle certificateBundle) {
        final KeyStoreSource keyStoreSource = keyStoreTemplate.saveCertificate(certificateBundle);
        final Versioned<KeyStoreSource> versionedKeyStoreSource = Versioned.create(keyStoreSource, version);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("The new certificate has been issued {}", versionedKeyStoreSource.getVersion());
        }
        final Metadata metadata = keyValueOperations.put(properties.certificatePath(), versionedKeyStoreSource);
        if (LOGGER.isDebugEnabled()) {
            if (metadata.isDeleted()) {
                LOGGER.debug("Certificate was deleted");
            }
            if (metadata.isDestroyed()) {
                LOGGER.debug("Certificate was destroyed");
            }
            LOGGER.debug("Put certificate to versioned KV storage {}", metadata);
        }
        return findOne(properties.certificatePath())
                .orElseThrow(() -> new IllegalArgumentException("Key store data cannot be null"));
    }

}
