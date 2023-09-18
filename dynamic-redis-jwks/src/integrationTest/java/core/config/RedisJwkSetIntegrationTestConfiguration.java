package core.config;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.development.wide.world.spring.jwks.internal.*;
import org.development.wide.world.spring.jwks.property.BCCertificateInternalProperties;
import org.development.wide.world.spring.jwks.property.KeyStoreInternalProperties;
import org.development.wide.world.spring.jwks.spi.CertificateIssuer;
import org.development.wide.world.spring.jwks.spi.CertificateRepository;
import org.development.wide.world.spring.jwks.spi.JwksCertificateRotator;
import org.development.wide.world.spring.jwks.spi.RetryableJwksCertificateRotator;
import org.development.wide.world.spring.jwks.template.KeyStoreTemplate;
import org.development.wide.world.spring.redis.data.VersionedKeyStoreSource;
import org.development.wide.world.spring.redis.internal.RedisCertificateRepository;
import org.development.wide.world.spring.redis.internal.RetryableRedisJwksCertificateRotator;
import org.development.wide.world.spring.redis.property.DynamicRedisJwksInternalProperties;
import org.development.wide.world.spring.redis.property.RedisKvInternalProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;

import java.time.Duration;

@TestConfiguration(proxyBeanMethods = false)
public class RedisJwkSetIntegrationTestConfiguration {

    static final DynamicRedisJwksInternalProperties REDIS_JWKS_PROPERTIES = DynamicRedisJwksInternalProperties.builder()
            .kv(RedisKvInternalProperties.builder().certificateKey("authorization.certificate").build())
            .certificateRotationRetryFixedBackoff(Duration.ofMillis(500))
            .certificateRotationRetries(10)
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
    public CertificateIssuer certificateIssuer() {
        return new BouncyCastleCertificateIssuer(CERTIFICATE_PROPERTIES);
    }

    @Bean
    public KeyStoreTemplate keyStoreTemplate(@NonNull final InternalKeyStore internalKeyStore) {
        return new KeyStoreTemplate(internalKeyStore);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(@NonNull final RetryableJwksCertificateRotator certificateRotator) {
        return new DynamicJwkSet(certificateRotator);
    }

    @Bean
    public JwksCertificateRotator jwksCertificateRotator(@NonNull final JwkSetConverter jwkSetConverter,
                                                         @NonNull final CertificateIssuer certificateIssuer,
                                                         @NonNull final CertificateRepository certificateRepository) {
        return new DefaultJwksCertificateRotator(jwkSetConverter, certificateIssuer, certificateRepository);
    }

    @Bean
    public CertificateRepository certificateRepository(@NonNull final KeyStoreTemplate keyStoreTemplate,
                                                       @NonNull final RedisTemplate<String, VersionedKeyStoreSource> redisTemplate) {
        return new RedisCertificateRepository(keyStoreTemplate, redisTemplate);
    }

    @Bean
    public RetryableJwksCertificateRotator retryableJwksCertificateRotator(@NonNull final JwksCertificateRotator certificateRotator) {
        return new RetryableRedisJwksCertificateRotator(certificateRotator, REDIS_JWKS_PROPERTIES);
    }

    @Bean
    public RedisTemplate<String, VersionedKeyStoreSource> versionedKeyStoreSourceRedisTemplate(@NonNull final RedisConnectionFactory connectionFactory) {
        final RedisTemplate<String, VersionedKeyStoreSource> versionedKeyStoreSourceRedisTemplate = new RedisTemplate<>();
        versionedKeyStoreSourceRedisTemplate.setConnectionFactory(connectionFactory);
        return versionedKeyStoreSourceRedisTemplate;
    }

}
