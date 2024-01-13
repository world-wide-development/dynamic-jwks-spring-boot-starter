package org.development.wide.world.spring.redis.internal;

import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.data.KeyStoreSource;
import org.development.wide.world.spring.jwks.internal.InternalKeyStore;
import org.development.wide.world.spring.jwks.spi.CertificateRepository;
import org.development.wide.world.spring.jwks.template.KeyStoreTemplate;
import org.development.wide.world.spring.redis.data.VersionedKeyStoreSource;
import org.development.wide.world.spring.redis.exception.RedisOperationException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;

import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * Redis-based implementation of the {@link CertificateRepository}
 *
 * @see CertificateRepository
 */
public class RedisCertificateRepository implements CertificateRepository {

    private final KeyStoreTemplate keyStoreTemplate;
    private final RedisTemplate<String, VersionedKeyStoreSource> redisTemplate;
    private final HashOperations<String, String, VersionedKeyStoreSource> valueOperations;

    public RedisCertificateRepository(@NonNull final KeyStoreTemplate keyStoreTemplate,
                                      @NonNull final RedisTemplate<String, VersionedKeyStoreSource> redisTemplate) {
        this(keyStoreTemplate, redisTemplate, redisTemplate.opsForHash());
    }

    public RedisCertificateRepository(@NonNull final KeyStoreTemplate keyStoreTemplate,
                                      @NonNull final RedisTemplate<String, VersionedKeyStoreSource> redisTemplate,
                                      @NonNull final HashOperations<String, String, VersionedKeyStoreSource> valueOperations) {
        this.redisTemplate = redisTemplate;
        this.valueOperations = valueOperations;
        this.keyStoreTemplate = keyStoreTemplate;
    }

    /**
     * @see CertificateRepository#findOne(String)
     */
    @Override
    public Optional<CertificateData> findOne(@NonNull final String key) {
        return ofNullable(valueOperations.get(key, key)).map(versionedKeyStoreSource -> {
            final KeyStoreSource keyStoreSource = versionedKeyStoreSource.keyStoreSource();
            final InternalKeyStore internalKeyStore = keyStoreTemplate.reloadFromSource(keyStoreSource);
            final X509Certificate x509Certificate = internalKeyStore.getX509Certificate();
            return CertificateData.builder()
                    .x509Certificates(Collections.singletonList(x509Certificate))
                    .privateKey(internalKeyStore.getPrivateKey())
                    .serialNumber(keyStoreSource.serialNumber())
                    .version(versionedKeyStoreSource.version())
                    .x509Certificate(x509Certificate)
                    .build();
        });
    }

    /**
     * @see CertificateRepository#saveOne(String, CertificateData)
     */
    @Override
    public CertificateData saveOne(@NonNull final String key, @NonNull final CertificateData certificateData) {
        final var casCallback = CompareVersionedKeyStoreSourceAndSetCallback.of(key, certificateData, keyStoreTemplate);
        final Boolean executionResultStatus = redisTemplate.execute(casCallback);
        if (Boolean.FALSE.equals(executionResultStatus)) {
            throw new RedisOperationException("Unsuccessful Compare And Set Redis operation");
        }
        return this.findOne(key)
                .orElseThrow(() -> new IllegalStateException("Certificate data cannot be null"));
    }

}
