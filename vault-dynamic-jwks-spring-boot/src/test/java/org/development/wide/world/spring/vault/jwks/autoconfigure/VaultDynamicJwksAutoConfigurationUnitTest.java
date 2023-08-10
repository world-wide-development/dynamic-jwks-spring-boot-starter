package org.development.wide.world.spring.vault.jwks.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.source.JWKSource;
import core.base.BaseUnitTest;
import org.development.wide.world.spring.vault.jwks.spi.VaultJwksCertificateRotator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.vault.client.RestTemplateCustomizer;
import org.springframework.vault.core.VaultTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
class VaultDynamicJwksAutoConfigurationUnitTest extends BaseUnitTest {

    final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(VaultDynamicJwksAutoConfiguration.class));

    @Test
    void testTePresenceOfTheConfiguredBeans() {
        final ApplicationContextRunner applicationContextRunner = this.contextRunner
                .withPropertyValues("vault-dynamic-jwks.enabled=on")
                .withUserConfiguration(UnitTestUserConfiguration.class);
        applicationContextRunner.run(context -> {
            assertThat(context).hasSingleBean(Jackson2ObjectMapperBuilderCustomizer.class);
            assertThat(context).hasSingleBean(VaultJwksCertificateRotator.class);
            assertThat(context).hasSingleBean(RestTemplateCustomizer.class);
            assertThat(context).hasSingleBean(JWKSource.class);
        });
    }

    @Configuration(proxyBeanMethods = false)
    static class UnitTestUserConfiguration {

        @Bean
        ObjectMapper objectMapper() {
            return Mockito.mock(ObjectMapper.class);
        }

        @Bean
        VaultTemplate vaultTemplate() {
            return Mockito.mock(VaultTemplate.class);
        }

    }

}
