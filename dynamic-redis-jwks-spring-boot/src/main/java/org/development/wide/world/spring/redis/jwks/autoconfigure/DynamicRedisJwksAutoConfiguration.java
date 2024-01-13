package org.development.wide.world.spring.redis.jwks.autoconfigure;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.development.wide.world.spring.jwks.internal.*;
import org.development.wide.world.spring.jwks.property.BCCertificateInternalProperties;
import org.development.wide.world.spring.jwks.property.KeyStoreInternalProperties;
import org.development.wide.world.spring.jwks.spi.*;
import org.development.wide.world.spring.jwks.template.KeyStoreTemplate;
import org.development.wide.world.spring.redis.data.VersionedKeyStoreSource;
import org.development.wide.world.spring.redis.internal.RedisCertificateRepository;
import org.development.wide.world.spring.redis.internal.RedisCertificateRotationTask;
import org.development.wide.world.spring.redis.internal.RetryableRedisJwksCertificateRotator;
import org.development.wide.world.spring.redis.jwks.autoconfigure.properties.*;
import org.development.wide.world.spring.redis.property.CertificateRotationInternalProperties;
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
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
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
        DynamicJwksProperties.class,
        BCCertificateProperties.class,
        DynamicRedisJwksProperties.class,
        RotationScheduleProperties.class,
        CertificateRotationProperties.class
})
@Configuration(proxyBeanMethods = false)
@AutoConfiguration(
        after = {UserDetailsServiceAutoConfiguration.class},
        before = {OAuth2AuthorizationServerJwtAutoConfiguration.class}
)
@ConditionalOnProperty(matchIfMissing = true, name = {"dynamic-jwks.redis-storage.enabled"})
public class DynamicRedisJwksAutoConfiguration {

    /**
     * Instantiates {@link DynamicJwkSet} bean
     *
     * @param jwkSetDataHolder required dependency of {@link JwkSetDataHolder} type
     * @return {@code JWKSource<SecurityContext>}
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({JwkSetDataHolder.class})
    public JWKSource<SecurityContext> jwkSource(final JwkSetDataHolder jwkSetDataHolder) {
        return new DynamicJwkSet(jwkSetDataHolder);
    }

    /**
     * Lazy configuration for {@link SchedulingConfigurer}
     * Instantiates all the beans required for injection to {@link CertificateRotationTask}
     *
     * @see CertificateRotationTask
     * @see SchedulingConfigurer
     */
    @EnableScheduling
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(
            matchIfMissing = true,
            name = {
                    "dynamic-jwks.redis-storage.enabled",
                    "dynamic-jwks.redis-storage.certificate-rotation.schedule.enabled"
            }
    )
    public static class JwksCertificateRotationScheduleConfiguration {

        /**
         * Conditionally instantiates {@link LockRegistry} bean
         *
         * @param redisConnectionFactory required dependency of {@link RedisConnectionFactory} type
         * @param rotationProperties     required dependency of {@link CertificateRotationProperties} type
         * @return {@code LockRegistry}
         */
        @Bean
        @ConditionalOnMissingBean
        public LockRegistry lockRegistry(final RedisConnectionFactory redisConnectionFactory,
                                         final CertificateRotationProperties rotationProperties) {
            Assert.notNull(rotationProperties, "Certificate rotation properties cannot be null");
            final String lockKey = rotationProperties.rotationLockKey();
            return new RedisLockRegistry(redisConnectionFactory, lockKey);
        }

        /**
         * Conditionally instantiates {@link CertificateRotationTask} bean
         *
         * @param lockRegistry     required dependency of {@link LockRegistry} type
         * @param jwkSetDataHolder required dependency of {@link JwkSetDataHolder} type
         * @param properties       required dependency of {@link CertificateRotationProperties} type
         * @return {@code CertificateRotationTask}
         */
        @Bean
        @ConditionalOnMissingBean
        public CertificateRotationTask certificateRotationTask(final LockRegistry lockRegistry,
                                                               final JwkSetDataHolder jwkSetDataHolder,
                                                               final CertificateRotationProperties properties) {
            Assert.notNull(lockRegistry, "Lock registry cannot be null");
            Assert.notNull(jwkSetDataHolder, "JWKS data holder cannot be null");
            Assert.notNull(properties, "Certificate rotation properties cannot be null");
            final CertificateRotationInternalProperties rotationProperties = properties.convertToInternal();
            return new RedisCertificateRotationTask(lockRegistry, jwkSetDataHolder, rotationProperties);
        }

        /**
         * Conditionally instantiates {@link SchedulingConfigurer} bean
         *
         * @param rotationTask               required dependency of {@link CertificateRotationTask} type
         * @param rotationScheduleProperties required dependency of {@link RotationScheduleProperties} type
         * @return {@code SchedulingConfigurer}
         */
        @Bean
        @ConditionalOnMissingBean
        public SchedulingConfigurer schedulingConfigurer(final CertificateRotationTask rotationTask,
                                                         final RotationScheduleProperties rotationScheduleProperties) {
            return taskRegistrar -> taskRegistrar.addFixedRateTask(rotationTask, rotationScheduleProperties.interval());
        }

    }

    /**
     * Lazy configuration for {@link JwkSetDataHolder}
     * Instantiates all the required for injection to {@link JwkSetDataHolder} beans
     *
     * @see JwkSetDataHolder
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(matchIfMissing = true, name = {"dynamic-jwks.redis-storage.enabled"})
    public static class JwkSetDataHolderConfiguration {

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
         * Instantiates {@link CertificateService} bean
         *
         * @return {@code CertificateService}
         */
        @Bean
        @ConditionalOnMissingBean
        public CertificateService certificateService() {
            return new DefaultCertificateService();
        }

        /**
         * Instantiates {@link InternalKeyStore} bean
         *
         * @param properties required dependency of {@link KeyStoreProperties} type
         * @return {@code InternalKeyStore}
         */
        @Bean
        @ConditionalOnMissingBean
        public InternalKeyStore internalKeyStore(final KeyStoreProperties properties) {
            Assert.notNull(properties, "Key store properties cannot be null");
            final KeyStoreInternalProperties internalProperties = properties.convertToInternal();
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
        public CertificateIssuer certificateIssuer(final BCCertificateProperties bcProperties,
                                                   final CertificateService certificateService) {
            Assert.notNull(bcProperties, "bcProperties cannot be null");
            Assert.notNull(certificateService, "certificateService cannot be null");
            final BCCertificateInternalProperties internalBcProperties = bcProperties.convertToInternal();
            return new BouncyCastleCertificateIssuer(certificateService, internalBcProperties);
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
            Assert.notNull(properties, "Dynamic redis JWKS properties cannot be null");
            Assert.notNull(jwksCertificateRotator, "JWKS certificate rotator cannot be null");
            final DynamicRedisJwksInternalProperties internalProperties = properties.convertToInternal();
            return new RetryableRedisJwksCertificateRotator(jwksCertificateRotator, internalProperties);
        }

        /**
         * Conditionally instantiates {@link JwkSetDataHolder} bean
         *
         * @param retryableJwksCertificateRotator required dependency of {@link RetryableJwksCertificateRotator} type
         * @return {@code JwkSetDataHolder}
         */
        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnBean({RetryableJwksCertificateRotator.class})
        public JwkSetDataHolder jwkSetDataHolder(@NonNull final RetryableJwksCertificateRotator retryableJwksCertificateRotator) {
            return new AtomicJwkSetDataHolder(retryableJwksCertificateRotator);
        }

        /**
         * Conditionally instantiates {@link RedisTemplate} bean
         *
         * @param connectionFactory required dependency of {@link RedisConnectionFactory} type
         * @return {@code RedisTemplate<String, VersionedKeyStoreSource>}
         */
        @Bean
        @ConditionalOnMissingBean
        public RedisTemplate<String, VersionedKeyStoreSource> versionedKeyStoreSourceRedisTemplate(@NonNull final RedisConnectionFactory connectionFactory) {
            final RedisTemplate<String, VersionedKeyStoreSource> versionedKeyStoreSourceRedisTemplate = new RedisTemplate<>();
            versionedKeyStoreSourceRedisTemplate.setConnectionFactory(connectionFactory);
            return versionedKeyStoreSourceRedisTemplate;
        }

    }

}
