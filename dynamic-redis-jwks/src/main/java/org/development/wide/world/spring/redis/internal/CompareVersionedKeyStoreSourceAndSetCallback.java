package org.development.wide.world.spring.redis.internal;

import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.data.KeyStoreSource;
import org.development.wide.world.spring.jwks.template.KeyStoreTemplate;
import org.development.wide.world.spring.redis.data.VersionedKeyStoreSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;
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
        final K concreteKey = (K) key;
        operations.watch(concreteKey);
        final ValueOperations<K, V> valueOperations = operations.opsForValue();
        final Optional<Integer> optionalVersion = ofNullable(valueOperations.get(concreteKey))
                .map(VersionedKeyStoreSource.class::cast)
                .map(VersionedKeyStoreSource::version);
        final Integer lastVersion = certificateData.version();
        if (optionalVersion.map(version -> version.equals(lastVersion)).orElse(Boolean.TRUE)) {
            operations.multi();
            final Integer nextVersion = lastVersion + 1;
            final CertificateData nextCertificateData = certificateData.toBuilder()
                    .version(nextVersion)
                    .build();
            final KeyStoreSource nextKeyStoreSource = keyStoreTemplate.saveCertificate(nextCertificateData);
            final V freshVersionedKeyStoreSource = (V) VersionedKeyStoreSource.builder()
                    .keyStoreSource(nextKeyStoreSource)
                    .version(nextVersion)
                    .build();
            valueOperations.set(concreteKey, freshVersionedKeyStoreSource);
            final List<Object> operationResults = operations.exec();
            if (!operationResults.isEmpty()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Save certificate, version {}", nextVersion);
                }
                return Boolean.TRUE;
            }
        }
        operations.unwatch();
        return Boolean.FALSE;
    }

}
