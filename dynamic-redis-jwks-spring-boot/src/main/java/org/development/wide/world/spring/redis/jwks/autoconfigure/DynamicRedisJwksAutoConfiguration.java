package org.development.wide.world.spring.redis.jwks.autoconfigure;

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
import org.development.wide.world.spring.redis.jwks.autoconfigure.properties.BCCertificateProperties;
import org.development.wide.world.spring.redis.jwks.autoconfigure.properties.DynamicRedisJwksProperties;
import org.development.wide.world.spring.redis.jwks.autoconfigure.properties.KeyStoreProperties;
import org.development.wide.world.spring.redis.jwks.autoconfigure.properties.RedisKvProperties;
import org.development.wide.world.spring.redis.property.DynamicRedisJwksInternalProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.server.servlet.OAuth2AuthorizationServerJwtAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * Autoconfigure Redis-based implementation of the dynamic JWKS
 *
 * @see DynamicJwkSet
 */
@ConditionalOnClass({
        RedisTemplate.class
})
@EnableConfigurationProperties({
        RedisKvProperties.class,
        KeyStoreProperties.class,
        BCCertificateProperties.class,
        DynamicRedisJwksProperties.class
})
@Configuration(proxyBeanMethods = false)
@AutoConfiguration(
        after = {UserDetailsServiceAutoConfiguration.class},
        before = {OAuth2AuthorizationServerJwtAutoConfiguration.class}
)
@ConditionalOnProperty(matchIfMissing = true, name = {"dynamic-jwks.redis.enabled"})
public class DynamicRedisJwksAutoConfiguration {

    /**
     * Instantiates {@link DynamicJwkSet} bean
     *
     * @param certificateRotator required dependency of {@link RetryableJwksCertificateRotator} type
     * @return {@code JWKSource<SecurityContext>}
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({RetryableJwksCertificateRotator.class})
    public JWKSource<SecurityContext> jwkSource(final RetryableJwksCertificateRotator certificateRotator) {
        return new DynamicJwkSet(certificateRotator);
    }

    /**
     * Lazy configuration for {@link RetryableJwksCertificateRotator}
     * Instantiates all the required for injection to {@link RetryableJwksCertificateRotator} beans
     *
     * @see RetryableJwksCertificateRotator
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(matchIfMissing = true, name = {"dynamic-jwks.redis.enabled"})
    public static class JwksCertificateRotatorConfiguration {

        /**
         * Instantiates {@link JwkSetConverter} bean
         *
         * @return {@code JwkSetConverter}
         */
        @Bean
        @ConditionalOnMissingBean
        public JwkSetConverter jwkSetConverter() {
            return new JwkSetConverter();
        }

        /**
         * Instantiates {@link InternalKeyStore} bean
         *
         * @param keyStoreProperties required dependency of {@link KeyStoreProperties} type
         * @return {@code InternalKeyStore}
         */
        @Bean
        @ConditionalOnMissingBean
        public InternalKeyStore internalKeyStore(final KeyStoreProperties keyStoreProperties) {
            Assert.notNull(keyStoreProperties, "keyStoreProperties cannot be null");
            final KeyStoreInternalProperties internalProperties = keyStoreProperties.convertToInternal();
            return new InternalKeyStore(internalProperties);
        }

        /**
         * Instantiates {@link KeyStoreTemplate} bean
         *
         * @param internalKeyStore required dependency of {@link InternalKeyStore} type
         * @return {@code KeyStoreTemplate}
         */
        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnBean({InternalKeyStore.class})
        public KeyStoreTemplate keyStoreTemplate(final InternalKeyStore internalKeyStore) {
            return new KeyStoreTemplate(internalKeyStore);
        }

        /**
         * Instantiates {@link CertificateIssuer} bean
         *
         * @param bcProperties required dependency of {@link BCCertificateProperties} type
         * @return {@code CertificateIssuer}
         */
        @Bean
        @ConditionalOnMissingBean
        public CertificateIssuer certificateIssuer(final BCCertificateProperties bcProperties) {
            Assert.notNull(bcProperties, "bcProperties cannot be null");
            final BCCertificateInternalProperties internalBcProperties = bcProperties.convertToInternal();
            return new BouncyCastleCertificateIssuer(internalBcProperties);
        }

        /**
         * Instantiates {@link CertificateRepository} bean
         *
         * @param keyStoreTemplate required dependency of {@link KeyStoreTemplate} type
         * @param redisTemplate    required dependency of {@link RedisTemplate} type
         * @return {@code CertificateRepository}
         */
        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnBean({KeyStoreTemplate.class})
        public CertificateRepository certificateRepository(final KeyStoreTemplate keyStoreTemplate,
                                                           final RedisTemplate<String, VersionedKeyStoreSource> redisTemplate) {
            Assert.notNull(keyStoreTemplate, "keyStoreTemplate cannot be null");
            Assert.notNull(redisTemplate, "redisTemplate cannot be null");
            return new RedisCertificateRepository(keyStoreTemplate, redisTemplate);
        }

        /**
         * Instantiates {@link JwksCertificateRotator} bean
         *
         * @param jwkSetConverter       required dependency of {@link JwkSetConverter} type
         * @param certificateIssuer     required dependency of {@link CertificateIssuer} type
         * @param certificateRepository required dependency of {@link CertificateRepository} type
         * @return {@code JwksCertificateRotator}
         */
        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnBean({JwkSetConverter.class, CertificateIssuer.class, CertificateRepository.class})
        public JwksCertificateRotator jwksCertificateRotator(@NonNull final JwkSetConverter jwkSetConverter,
                                                             @NonNull final CertificateIssuer certificateIssuer,
                                                             @NonNull final CertificateRepository certificateRepository) {
            return new DefaultJwksCertificateRotator(jwkSetConverter, certificateIssuer, certificateRepository);
        }

        /**
         * Instantiates {@link RetryableJwksCertificateRotator} bean
         *
         * @param properties             required dependency of {@link DynamicRedisJwksProperties} type
         * @param jwksCertificateRotator required dependency of {@link JwksCertificateRotator} type
         * @return {@code RetryableJwksCertificateRotator}
         */
        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnBean({JwksCertificateRotator.class})
        public RetryableJwksCertificateRotator retryableJwksCertificateRotator(final DynamicRedisJwksProperties properties,
                                                                               final JwksCertificateRotator jwksCertificateRotator) {
            Assert.notNull(properties, "properties cannot be null");
            Assert.notNull(jwksCertificateRotator, "jwksCertificateRotator cannot be null");
            final DynamicRedisJwksInternalProperties internalProperties = properties.convertToInternal();
            return new RetryableRedisJwksCertificateRotator(jwksCertificateRotator, internalProperties);
        }

        @Bean
        public RedisTemplate<String, VersionedKeyStoreSource> versionedKeyStoreSourceRedisTemplate(@NonNull final RedisConnectionFactory connectionFactory) {
            final RedisTemplate<String, VersionedKeyStoreSource> versionedKeyStoreSourceRedisTemplate = new RedisTemplate<>();
            versionedKeyStoreSourceRedisTemplate.setConnectionFactory(connectionFactory);
            return versionedKeyStoreSourceRedisTemplate;
        }

    }

}
