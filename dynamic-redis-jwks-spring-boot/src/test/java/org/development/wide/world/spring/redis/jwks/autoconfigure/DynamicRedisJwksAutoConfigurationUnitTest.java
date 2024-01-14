package org.development.wide.world.spring.redis.jwks.autoconfigure;

import com.nimbusds.jose.jwk.source.JWKSource;
import core.base.BaseUnitTest;
import org.development.wide.world.spring.jwks.internal.InternalKeyStore;
import org.development.wide.world.spring.jwks.internal.JwkSetConverter;
import org.development.wide.world.spring.jwks.spi.*;
import org.development.wide.world.spring.jwks.template.KeyStoreTemplate;
import org.development.wide.world.spring.redis.jwks.autoconfigure.DynamicRedisJwksAutoConfiguration.JwkSetDataHolderConfiguration;
import org.development.wide.world.spring.redis.jwks.autoconfigure.DynamicRedisJwksAutoConfiguration.JwksCertificateRotationScheduleConfiguration;
import org.development.wide.world.spring.redis.template.KeyStoreRedisTemplate;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
class DynamicRedisJwksAutoConfigurationUnitTest extends BaseUnitTest {

    final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DynamicRedisJwksAutoConfiguration.class));

    @Test
    void testThePresenceOfTheConfiguredBeansFirst() {
        // Given
        final ApplicationContextRunner applicationContextRunner = this.contextRunner
                .withPropertyValues("dynamic-jwks.key-store.password=password")
                .withPropertyValues("dynamic-jwks.redis-storage.enabled=true")
                .withUserConfiguration(UnitTestFirstConfiguration.class);
        // Expect
        applicationContextRunner.run(context -> {
            assertThat(context).hasSingleBean(JwksCertificateRotationScheduleConfiguration.class);
            assertThat(context).hasSingleBean(RetryableJwksCertificateRotator.class);
            assertThat(context).hasSingleBean(JwkSetDataHolderConfiguration.class);
            assertThat(context).hasSingleBean(CertificateRotationTask.class);
            assertThat(context).hasSingleBean(JwksCertificateRotator.class);
            assertThat(context).hasSingleBean(CertificateRepository.class);
            assertThat(context).hasSingleBean(KeyStoreRedisTemplate.class);
            assertThat(context).hasSingleBean(SchedulingConfigurer.class);
            assertThat(context).hasSingleBean(CertificateService.class);
            assertThat(context).hasSingleBean(CertificateIssuer.class);
            assertThat(context).hasSingleBean(KeyStoreTemplate.class);
            assertThat(context).hasSingleBean(InternalKeyStore.class);
            assertThat(context).hasSingleBean(JwkSetConverter.class);
            assertThat(context).hasSingleBean(LockRegistry.class);
            assertThat(context).hasSingleBean(JWKSource.class);
        });
    }

    @Test
    void testThePresenceOfTheConfiguredBeansSecond() {
        // Given
        final ApplicationContextRunner applicationContextRunner = this.contextRunner
                .withPropertyValues("dynamic-jwks.key-store.password=password")
                .withPropertyValues("dynamic-jwks.redis-storage.enabled=true")
                .withUserConfiguration(UnitTestSecondConfiguration.class);
        // Expect
        applicationContextRunner.run(context -> {
            assertThat(context).hasSingleBean(JwksCertificateRotationScheduleConfiguration.class);
            assertThat(context).hasSingleBean(RetryableJwksCertificateRotator.class);
            assertThat(context).hasSingleBean(JwkSetDataHolderConfiguration.class);
            assertThat(context).hasSingleBean(CertificateRotationTask.class);
            assertThat(context).hasSingleBean(JwksCertificateRotator.class);
            assertThat(context).hasSingleBean(CertificateRepository.class);
            assertThat(context).hasSingleBean(KeyStoreRedisTemplate.class);
            assertThat(context).hasSingleBean(SchedulingConfigurer.class);
            assertThat(context).hasSingleBean(CertificateService.class);
            assertThat(context).hasSingleBean(CertificateIssuer.class);
            assertThat(context).hasSingleBean(KeyStoreTemplate.class);
            assertThat(context).hasSingleBean(InternalKeyStore.class);
            assertThat(context).hasSingleBean(JwkSetConverter.class);
            assertThat(context).hasSingleBean(LockRegistry.class);
            assertThat(context).hasSingleBean(JWKSource.class);
        });
    }

    @Test
    void testTheAbsenceOfTheConfiguredBeans() {
        // Given
        final ApplicationContextRunner applicationContextRunner = this.contextRunner
                .withPropertyValues("dynamic-jwks.redis-storage.enabled=false")
                .withUserConfiguration(UnitTestFirstConfiguration.class);
        // Expect
        applicationContextRunner.run(context -> {
            assertThat(context).doesNotHaveBean(JwksCertificateRotationScheduleConfiguration.class);
            assertThat(context).doesNotHaveBean(RetryableJwksCertificateRotator.class);
            assertThat(context).doesNotHaveBean(JwkSetDataHolderConfiguration.class);
            assertThat(context).doesNotHaveBean(CertificateRotationTask.class);
            assertThat(context).doesNotHaveBean(JwksCertificateRotator.class);
            assertThat(context).doesNotHaveBean(CertificateRepository.class);
            assertThat(context).doesNotHaveBean(KeyStoreRedisTemplate.class);
            assertThat(context).doesNotHaveBean(SchedulingConfigurer.class);
            assertThat(context).doesNotHaveBean(CertificateService.class);
            assertThat(context).doesNotHaveBean(CertificateIssuer.class);
            assertThat(context).doesNotHaveBean(KeyStoreTemplate.class);
            assertThat(context).doesNotHaveBean(InternalKeyStore.class);
            assertThat(context).doesNotHaveBean(JwkSetConverter.class);
            assertThat(context).doesNotHaveBean(LockRegistry.class);
            assertThat(context).doesNotHaveBean(JWKSource.class);
        });
    }

    @Test
    void testThePresenceOfTheConfiguredBeansWithoutScheduling() {
        // Given
        final ApplicationContextRunner applicationContextRunner = this.contextRunner
                .withPropertyValues("dynamic-jwks.redis-storage.certificate-rotation.schedule.enabled=false")
                .withPropertyValues("dynamic-jwks.key-store.password=password")
                .withPropertyValues("dynamic-jwks.redis-storage.enabled=true")
                .withUserConfiguration(UnitTestFirstConfiguration.class);
        // Expect
        applicationContextRunner.run(context -> {
            assertThat(context).doesNotHaveBean(JwksCertificateRotationScheduleConfiguration.class);
            assertThat(context).doesNotHaveBean(CertificateRotationTask.class);
            assertThat(context).doesNotHaveBean(SchedulingConfigurer.class);
            assertThat(context).doesNotHaveBean(LockRegistry.class);
            assertThat(context).hasSingleBean(RetryableJwksCertificateRotator.class);
            assertThat(context).hasSingleBean(JwkSetDataHolderConfiguration.class);
            assertThat(context).hasSingleBean(JwksCertificateRotator.class);
            assertThat(context).hasSingleBean(CertificateRepository.class);
            assertThat(context).hasSingleBean(KeyStoreRedisTemplate.class);
            assertThat(context).hasSingleBean(CertificateService.class);
            assertThat(context).hasSingleBean(CertificateIssuer.class);
            assertThat(context).hasSingleBean(KeyStoreTemplate.class);
            assertThat(context).hasSingleBean(InternalKeyStore.class);
            assertThat(context).hasSingleBean(JwkSetConverter.class);
            assertThat(context).hasSingleBean(JWKSource.class);
        });
    }

    @Configuration(proxyBeanMethods = false)
    static class UnitTestFirstConfiguration {

        @Bean
        RedisConnectionFactory redisConnectionFactory() {
            return BDDMockito.mock(RedisConnectionFactory.class);
        }

        @Bean
        @ConditionalOnBean({RedisConnectionFactory.class})
        @ConditionalOnProperty(
                matchIfMissing = true,
                name = {
                        "dynamic-jwks.redis-storage.enabled",
                        "dynamic-jwks.redis-storage.certificate-rotation.schedule.enabled"
                }
        )
        LockRegistry lockRegistry() {
            return BDDMockito.mock(RedisLockRegistry.class);
        }

    }

    @Configuration(proxyBeanMethods = false)
    static class UnitTestSecondConfiguration {

        @Bean
        RedisConnectionFactory redisConnectionFactory() {
            return BDDMockito.mock(RedisConnectionFactory.class);
        }

        @Bean
        @ConditionalOnBean({RedisConnectionFactory.class})
        @ConditionalOnProperty(
                matchIfMissing = true,
                name = {
                        "dynamic-jwks.redis-storage.enabled",
                        "dynamic-jwks.redis-storage.certificate-rotation.schedule.enabled"
                }
        )
        CertificateRotationTask certificateRotationTask() {
            return BDDMockito.mock(CertificateRotationTask.class);
        }

    }

}
