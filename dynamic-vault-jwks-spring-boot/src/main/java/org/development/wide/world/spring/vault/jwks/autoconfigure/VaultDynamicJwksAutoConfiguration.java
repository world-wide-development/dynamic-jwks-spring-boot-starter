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

/**
 * Autoconfigure HashiCorp Vault based implementation of the dynamic JWKS
 *
 * @see VaultDynamicJwkSet
 */
@AutoConfiguration
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(matchIfMissing = true, name = {"dynamic-jwks.enabled"})
@EnableConfigurationProperties({KeyStoreProperties.class, DynamicJwksProperties.class})
public class VaultDynamicJwksAutoConfiguration {

    /**
     * Instantiates {@link VaultDynamicJwkSet} bean
     *
     * @param certificateRotator required dependency of {@link JwksCertificateRotator} type
     * @return {@code JWKSource<SecurityContext>}
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({JwksCertificateRotator.class})
    public JWKSource<SecurityContext> jwkSource(final JwksCertificateRotator certificateRotator) {
        return new VaultDynamicJwkSet(certificateRotator);
    }

    /**
     * Lazy configuration for {@link JwksCertificateRotator}
     * Instantiates all the required for injection to {@link JwksCertificateRotator} beans
     *
     * @see JwksCertificateRotator
     */
    @Configuration(proxyBeanMethods = false)
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
         * @param properties required dependency of {@link KeyStoreProperties} type
         * @return {@code InternalKeyStore}
         */
        @Bean
        @ConditionalOnMissingBean
        public InternalKeyStore internalKeyStore(final KeyStoreProperties properties) {
            return new InternalKeyStore(properties);
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
         * Instantiates {@link KeyStoreKeeper} bean
         *
         * @param vaultTemplate    required dependency of {@link VaultTemplate} type
         * @param properties       required dependency of {@link DynamicJwksProperties} type
         * @param keyStoreTemplate required dependency of {@link KeyStoreTemplate} type
         * @return {@code KeyStoreKeeper}
         */
        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnBean({VaultTemplate.class, KeyStoreTemplate.class})
        public KeyStoreKeeper keyStoreKeeper(final VaultTemplate vaultTemplate,
                                             final DynamicJwksProperties properties,
                                             final KeyStoreTemplate keyStoreTemplate) {
            return new VaultKeyStoreKeeper(vaultTemplate, properties, keyStoreTemplate);
        }

        /**
         * Instantiates {@link CertificateIssuer} bean
         *
         * @param vaultTemplate required dependency of {@link VaultTemplate} type
         * @param properties    required dependency of {@link DynamicJwksProperties} type
         * @return {@code CertificateIssuer}
         */
        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnBean({VaultTemplate.class})
        public CertificateIssuer certificateIssuer(final VaultTemplate vaultTemplate,
                                                   final DynamicJwksProperties properties) {
            return new VaultCertificateIssuer(vaultTemplate, properties);
        }

        /**
         * Instantiates {@link JwksCertificateRotator} bean
         *
         * @param keyStoreKeeper    required dependency of {@link KeyStoreKeeper} type
         * @param jwkSetConverter   required dependency of {@link JwkSetConverter} type
         * @param properties        required dependency of {@link DynamicJwksProperties} type
         * @param certificateIssuer required dependency of {@link CertificateIssuer} type
         * @return {@code JwksCertificateRotator}
         */
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
