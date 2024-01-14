package core.config;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.development.wide.world.spring.jwks.internal.*;
import org.development.wide.world.spring.jwks.property.BCCertificateInternalProperties;
import org.development.wide.world.spring.jwks.property.KeyStoreInternalProperties;
import org.development.wide.world.spring.jwks.spi.*;
import org.development.wide.world.spring.jwks.template.KeyStoreTemplate;
import org.development.wide.world.spring.redis.internal.RedisCertificateRepository;
import org.development.wide.world.spring.redis.internal.RedisCertificateRotationTask;
import org.development.wide.world.spring.redis.internal.RetryableRedisJwksCertificateRotator;
import org.development.wide.world.spring.redis.property.*;
import org.development.wide.world.spring.redis.template.KeyStoreRedisTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.lang.NonNull;

import java.time.Duration;

@TestConfiguration(proxyBeanMethods = false)
public class RedisJwkSetIntegrationTestConfiguration {

    static final RotationScheduleInternalProperties ROTATION_SCHEDULE_PROPERTIES = RotationScheduleInternalProperties.builder()
            .interval(Duration.ofMillis(900))
            .enabled(Boolean.FALSE)
            .build();
    static final RotationRetryInternalProperties ROTATION_RETRY_PROPERTIES = RotationRetryInternalProperties.builder()
            .fixedBackoff(Duration.ofMillis(500))
            .maxAttempts(10)
            .build();
    static final CertificateRotationInternalProperties CERTIFICATE_ROTATION_PROPERTIES = CertificateRotationInternalProperties.builder()
            .rotationLockKey("given-rotation-lock-key")
            .schedule(ROTATION_SCHEDULE_PROPERTIES)
            .rotateBefore(Duration.ofSeconds(1))
            .retry(ROTATION_RETRY_PROPERTIES)
            .build();
    static final DynamicRedisJwksInternalProperties REDIS_JWKS_PROPERTIES = DynamicRedisJwksInternalProperties.builder()
            .kv(RedisKvInternalProperties.builder().certificateKey("authorization.certificate").build())
            .certificateRotation(CERTIFICATE_ROTATION_PROPERTIES)
            .enabled(Boolean.TRUE)
            .build();
    static final BCCertificateInternalProperties CERTIFICATE_PROPERTIES = BCCertificateInternalProperties.builder()
            .certificateTtl(Duration.ofMinutes(2))
            .subject("TestSubject")
            .issuer("TestIssuer")
            .build();
    static final KeyStoreInternalProperties KEY_STORE_PROPERTIES = KeyStoreInternalProperties.builder()
            .password("integration-test-password")
            .alias("integration-test-alias")
            .build();

    @Bean
    public JwkSetConverter jwkSetConverter() {
        return new JwkSetConverter();
    }

    @Bean
    public InternalKeyStore internalKeyStore() {
        return new InternalKeyStore(KEY_STORE_PROPERTIES);
    }

    @Bean
    public CertificateService certificateService() {
        return new DefaultCertificateService();
    }

    @Bean
    public LockRegistry lockRegistry(final RedisConnectionFactory redisConnectionFactory) {
        final String lockKey = CERTIFICATE_ROTATION_PROPERTIES.rotationLockKey();
        return new RedisLockRegistry(redisConnectionFactory, lockKey);
    }

    @Bean
    public CertificateIssuer certificateIssuer(final CertificateService certificateService) {
        return new BouncyCastleCertificateIssuer(certificateService, CERTIFICATE_PROPERTIES);
    }

    @Bean
    public KeyStoreTemplate keyStoreTemplate(@NonNull final InternalKeyStore internalKeyStore) {
        return new KeyStoreTemplate(internalKeyStore);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(@NonNull final JwkSetDataHolder jwkSetDataHolder) {
        return new DynamicJwkSet(jwkSetDataHolder);
    }

    @Bean
    public CertificateRepository certificateRepository(@NonNull final KeyStoreTemplate keyStoreTemplate,
                                                       @NonNull final KeyStoreRedisTemplate redisTemplate) {
        return new RedisCertificateRepository(keyStoreTemplate, redisTemplate);
    }

    @Bean
    public RedisCertificateRotationTask redisCertificateRotationTask(final LockRegistry lockRegistry,
                                                                     final JwkSetDataHolder jwkSetDataHolder) {
        return new RedisCertificateRotationTask(lockRegistry, jwkSetDataHolder, CERTIFICATE_ROTATION_PROPERTIES);
    }

    @Bean
    public KeyStoreRedisTemplate keyStoreRedisTemplate(@NonNull final RedisConnectionFactory connectionFactory) {
        return KeyStoreRedisTemplate.of(connectionFactory);
    }

    @Bean
    public JwksCertificateRotator jwksCertificateRotator(@NonNull final JwkSetConverter jwkSetConverter,
                                                         @NonNull final CertificateIssuer certificateIssuer,
                                                         @NonNull final CertificateRepository certificateRepository) {
        return new DefaultJwksCertificateRotator(jwkSetConverter, certificateIssuer, certificateRepository);
    }

    @Bean
    public JwkSetDataHolder jwkSetDataHolder(@NonNull final RetryableJwksCertificateRotator retryableJwksCertificateRotator) {
        return new AtomicJwkSetDataHolder(retryableJwksCertificateRotator);
    }

    @Bean
    public RetryableJwksCertificateRotator retryableJwksCertificateRotator(@NonNull final JwksCertificateRotator certificateRotator) {
        return new RetryableRedisJwksCertificateRotator(certificateRotator, REDIS_JWKS_PROPERTIES);
    }

}
