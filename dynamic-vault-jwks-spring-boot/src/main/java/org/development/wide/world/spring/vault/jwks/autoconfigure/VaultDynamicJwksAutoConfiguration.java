package org.development.wide.world.spring.vault.jwks.autoconfigure;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.development.wide.world.spring.vault.jwks.internal.*;
import org.development.wide.world.spring.vault.jwks.property.DynamicJwksProperties;
import org.development.wide.world.spring.vault.jwks.property.KeyStoreProperties;
import org.development.wide.world.spring.vault.jwks.spi.CertificateIssuer;
import org.development.wide.world.spring.vault.jwks.spi.JwksCertificateRotator;
import org.development.wide.world.spring.vault.jwks.spi.KeyStoreKeeper;
import org.development.wide.world.spring.vault.jwks.template.KeyStoreTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.core.VaultTemplate;

@AutoConfiguration
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "vault-dynamic-jwks", name = "enabled")
@EnableConfigurationProperties({KeyStoreProperties.class, DynamicJwksProperties.class})
public class VaultDynamicJwksAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({JwksCertificateRotator.class})
    public JWKSource<SecurityContext> jwkSource(final JwksCertificateRotator certificateRotator) {
        return new VaultDynamicJwkSet(certificateRotator);
    }

    @Configuration(proxyBeanMethods = false)
    static class JwksCertificateRotatorConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public JwkSetConverter jwkSetConverter() {
            return new JwkSetConverter();
        }

        @Bean
        @ConditionalOnMissingBean
        public InternalKeyStore internalKeyStore(final KeyStoreProperties properties) {
            return new InternalKeyStore(properties);
        }

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnBean({InternalKeyStore.class})
        public KeyStoreTemplate keyStoreTemplate(final InternalKeyStore internalKeyStore) {
            return new KeyStoreTemplate(internalKeyStore);
        }

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnBean({VaultTemplate.class, KeyStoreTemplate.class})
        public KeyStoreKeeper keyStoreKeeper(final VaultTemplate vaultTemplate,
                                             final DynamicJwksProperties properties,
                                             final KeyStoreTemplate keyStoreTemplate) {
            return new VaultKeyStoreKeeper(vaultTemplate, properties, keyStoreTemplate);
        }

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnBean({VaultTemplate.class})
        public CertificateIssuer certificateIssuer(final VaultTemplate vaultTemplate,
                                                   final DynamicJwksProperties properties) {
            return new VaultCertificateIssuer(vaultTemplate, properties);
        }

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnBean({KeyStoreKeeper.class, JwkSetConverter.class, CertificateIssuer.class})
        public JwksCertificateRotator vaultJwksCertificateRotator(final KeyStoreKeeper keyStoreKeeper,
                                                                  final JwkSetConverter jwkSetConverter,
                                                                  final DynamicJwksProperties properties,
                                                                  final CertificateIssuer certificateIssuer) {
            return new VaultJwksCertificateRotator(keyStoreKeeper, jwkSetConverter, properties, certificateIssuer);
        }

    }

}
