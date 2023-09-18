package org.development.wide.world.spring.redis.jwks.internal;

import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.data.KeyStoreSource;
import org.development.wide.world.spring.jwks.internal.InternalKeyStore;
import org.development.wide.world.spring.jwks.spi.CertificateRepository;
import org.development.wide.world.spring.jwks.template.KeyStoreTemplate;
import org.development.wide.world.spring.redis.jwks.data.VersionedKeyStoreSource;
import org.development.wide.world.spring.redis.jwks.exception.RedisOperationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.lang.NonNull;

import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class RedisCertificateRepository implements CertificateRepository {

    private final KeyStoreTemplate keyStoreTemplate;
    private final RedisTemplate<String, VersionedKeyStoreSource> redisTemplate;
    private final ValueOperations<String, VersionedKeyStoreSource> valueOperations;

    public RedisCertificateRepository(@NonNull final KeyStoreTemplate keyStoreTemplate,
                                      @NonNull final RedisTemplate<String, VersionedKeyStoreSource> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.keyStoreTemplate = keyStoreTemplate;
        this.valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public Optional<CertificateData> findOne(@NonNull final String key) {
        return ofNullable(valueOperations.get(key)).map(versionedKeyStoreSource -> {
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
