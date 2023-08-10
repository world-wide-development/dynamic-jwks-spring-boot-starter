package org.development.wide.world.spring.vault.jwks.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.development.wide.world.spring.vault.jwks.jackson.CertificateBundleJacksonMixIn;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.vault.client.RestTemplateCustomizer;
import org.springframework.vault.support.CertificateBundle;

import java.util.List;

@AutoConfiguration
@ConditionalOnProperty(prefix = "vault-dynamic-jwks", name = "enabled")
public class VaultDynamicJwksMappingAutoConfiguration {

    @Bean
    @ConditionalOnBean({Jackson2ObjectMapperBuilder.class})
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder
                .mixIn(CertificateBundle.class, CertificateBundleJacksonMixIn.class);
    }

    @Bean
    @ConditionalOnBean({ObjectMapper.class})
    public RestTemplateCustomizer restTemplateCustomizer(@NonNull final ObjectMapper objectMapper) {
        return restTemplate -> {
            final List<HttpMessageConverter<?>> messageConverters = List.of(
                    new MappingJackson2HttpMessageConverter(objectMapper)
            );
            restTemplate.setMessageConverters(messageConverters);
        };
    }

}
