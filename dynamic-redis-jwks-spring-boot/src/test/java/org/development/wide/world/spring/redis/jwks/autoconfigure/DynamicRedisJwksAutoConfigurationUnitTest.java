package org.development.wide.world.spring.redis.jwks.autoconfigure;

import com.nimbusds.jose.jwk.source.JWKSource;
import core.base.BaseUnitTest;
import org.development.wide.world.spring.jwks.internal.InternalKeyStore;
import org.development.wide.world.spring.jwks.internal.JwkSetConverter;
import org.development.wide.world.spring.jwks.spi.*;
import org.development.wide.world.spring.jwks.template.KeyStoreTemplate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
class DynamicRedisJwksAutoConfigurationUnitTest extends BaseUnitTest {

    final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DynamicRedisJwksAutoConfiguration.class));

    @Test
    void testThePresenceOfTheConfiguredBeans() {
        final ApplicationContextRunner applicationContextRunner = this.contextRunner
                .withUserConfiguration(UnitTestUserConfiguration.class)
                .withPropertyValues("dynamic-jwks.redis.enabled=on");
        applicationContextRunner.run(context -> {
            assertThat(context).hasSingleBean(RetryableJwksCertificateRotator.class);
            assertThat(context).hasSingleBean(JwksCertificateRotator.class);
            assertThat(context).hasSingleBean(CertificateRepository.class);
            assertThat(context).hasSingleBean(CertificateService.class);
            assertThat(context).hasSingleBean(CertificateIssuer.class);
            assertThat(context).hasSingleBean(KeyStoreTemplate.class);
            assertThat(context).hasSingleBean(InternalKeyStore.class);
            assertThat(context).hasSingleBean(JwkSetConverter.class);
            assertThat(context).hasSingleBean(RedisTemplate.class);
            assertThat(context).hasSingleBean(JWKSource.class);
        });
    }

    @Configuration(proxyBeanMethods = false)
    static class UnitTestUserConfiguration {

        @Bean
        RedisConnectionFactory redisConnectionFactory() {
            return Mockito.mock(RedisConnectionFactory.class);
        }

    }

}
