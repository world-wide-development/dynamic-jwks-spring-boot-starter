package org.development.wide.world.spring.redis.internal;

import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.data.KeyStoreSource;
import org.development.wide.world.spring.jwks.template.KeyStoreTemplate;
import org.development.wide.world.spring.redis.data.VersionedKeyStoreSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * Extension of the {@link SessionCallback} with the possibility
 * to perform "Compare And Set" operation for {@link VersionedKeyStoreSource}
 *
 * @see SessionCallback
 */
public class CompareVersionedKeyStoreSourceAndSetCallback implements SessionCallback<Boolean> {

    private static final Logger logger = LoggerFactory.getLogger(CompareVersionedKeyStoreSourceAndSetCallback.class);

    private final String key;
    private final CertificateData certificateData;
    private final KeyStoreTemplate keyStoreTemplate;

    public CompareVersionedKeyStoreSourceAndSetCallback(final String key,
                                                        final CertificateData certificateData,
                                                        final KeyStoreTemplate keyStoreTemplate) {
        this.key = key;
        this.certificateData = certificateData;
        this.keyStoreTemplate = keyStoreTemplate;
    }

    @NonNull
    public static CompareVersionedKeyStoreSourceAndSetCallback of(final String key,
                                                                  final CertificateData certificateData,
                                                                  final KeyStoreTemplate keyStoreTemplate) {
        return new CompareVersionedKeyStoreSourceAndSetCallback(key, certificateData, keyStoreTemplate);
    }

    /**
     * @see SessionCallback#execute(RedisOperations)
     */
    @Override
    @SuppressWarnings({"unchecked"})
    public <K, V> Boolean execute(@NonNull final RedisOperations<K, V> operations) throws DataAccessException {
        final K typeSafeKey = (K) key;
        operations.watch(typeSafeKey);
        final HashOperations<K, K, V> valueOperations = operations.opsForHash();
        final Optional<Integer> optionalVersion = ofNullable(valueOperations.get(typeSafeKey, key))
                .map(VersionedKeyStoreSource.class::cast)
                .map(VersionedKeyStoreSource::version);
        if (optionalVersion.map(version -> version.equals(certificateData.version())).orElse(Boolean.TRUE)) {
            operations.multi();
            final Integer freshVersion = certificateData.version() + 1;
            final CertificateData freshCertificateData = certificateData.toBuilder()
                    .version(freshVersion)
                    .build();
            final KeyStoreSource freshKeyStoreSource = keyStoreTemplate.saveCertificate(freshCertificateData);
            final VersionedKeyStoreSource freshVersionedKeyStoreSource = VersionedKeyStoreSource.builder()
                    .keyStoreSource(freshKeyStoreSource)
                    .version(freshVersion)
                    .build();
            valueOperations.put(typeSafeKey, typeSafeKey, (V) freshVersionedKeyStoreSource);
            final List<Object> operationResults = operations.exec();
            if (!operationResults.isEmpty()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Save certificate, version {}", freshVersion);
                }
                return Boolean.TRUE;
            }
        }
        operations.unwatch();
        return Boolean.FALSE;
    }

}
