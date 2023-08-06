package core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.development.wide.world.spring.vault.jwks.internal.DefaultVaultJwksCertificateRotator;
import org.development.wide.world.spring.vault.jwks.internal.VaultDynamicJwkSet;
import org.development.wide.world.spring.vault.jwks.jackson.CertificateBundleJacksonMixIn;
import org.development.wide.world.spring.vault.jwks.property.VaultDynamicJwksProperties;
import org.development.wide.world.spring.vault.jwks.spi.VaultJwksCertificateRotator;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.vault.client.RestTemplateCustomizer;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.CertificateBundle;

import java.util.List;

@TestConfiguration
@EnableConfigurationProperties({VaultDynamicJwksProperties.class})
@SuppressWarnings({"SpringJavaInjectionPointsAutowiringInspection"})
public class VaultJwkSetIntegrationTestConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder -> {
            // Customize CertificateBundle serialization
            jacksonObjectMapperBuilder.mixIn(CertificateBundle.class, CertificateBundleJacksonMixIn.class);
        };
    }

    @Bean
    public RestTemplateCustomizer restTemplateCustomizer(@NonNull final ObjectMapper objectMapper) {
        return restTemplate -> {
            final List<HttpMessageConverter<?>> messageConverters = List.of(
                    new MappingJackson2HttpMessageConverter(objectMapper)
            );
            restTemplate.setMessageConverters(messageConverters);
        };
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(final VaultJwksCertificateRotator certificateRotator) {
        return new VaultDynamicJwkSet(certificateRotator);
    }

    @Bean
    public VaultJwksCertificateRotator vaultJwksCertificateRotator(@NonNull final VaultTemplate vaultTemplate,
                                                                   @NonNull final VaultDynamicJwksProperties properties) {
        return new DefaultVaultJwksCertificateRotator(vaultTemplate, properties);
    }

}
