package org.development.wide.world.spring.vault.jwks.autoconfigure;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.development.wide.world.spring.vault.jwks.internal.DefaultVaultJwksCertificateRotator;
import org.development.wide.world.spring.vault.jwks.internal.VaultDynamicJwkSet;
import org.development.wide.world.spring.vault.jwks.property.VaultDynamicJwksProperties;
import org.development.wide.world.spring.vault.jwks.spi.VaultJwksCertificateRotator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.vault.core.VaultTemplate;

@AutoConfiguration
@ConditionalOnBean({VaultTemplate.class})
@EnableConfigurationProperties({VaultDynamicJwksProperties.class})
@ConditionalOnProperty(prefix = "vault-dynamic-jwks", name = "enabled")
public class VaultDynamicJwksAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JWKSource<SecurityContext> jwkSource(final VaultJwksCertificateRotator certificateRotator) {
        return new VaultDynamicJwkSet(certificateRotator);
    }

    @Bean
    @ConditionalOnMissingBean
    public VaultJwksCertificateRotator vaultJwksCertificateRotator(final VaultTemplate vaultTemplate,
                                                                   final VaultDynamicJwksProperties properties) {
        return new DefaultVaultJwksCertificateRotator(vaultTemplate, properties);
    }

}
