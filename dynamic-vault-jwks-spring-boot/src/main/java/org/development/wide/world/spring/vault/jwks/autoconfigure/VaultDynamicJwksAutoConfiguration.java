package org.development.wide.world.spring.vault.jwks.autoconfigure;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.development.wide.world.spring.jwks.internal.DynamicJwkSet;
import org.development.wide.world.spring.jwks.internal.InternalKeyStore;
import org.development.wide.world.spring.jwks.internal.JwkSetConverter;
import org.development.wide.world.spring.jwks.property.KeyStoreInternalProperties;
import org.development.wide.world.spring.jwks.spi.CertificateIssuer;
import org.development.wide.world.spring.jwks.spi.CertificateRepository;
import org.development.wide.world.spring.jwks.spi.JwksCertificateRotator;
import org.development.wide.world.spring.jwks.template.KeyStoreTemplate;
import org.development.wide.world.spring.vault.jwks.autoconfigure.properties.DynamicVaultJwksProperties;
import org.development.wide.world.spring.vault.jwks.autoconfigure.properties.KeyStoreProperties;
import org.development.wide.world.spring.vault.jwks.autoconfigure.properties.VaultPkiProperties;
import org.development.wide.world.spring.vault.jwks.autoconfigure.properties.VaultVersionedKvProperties;
import org.development.wide.world.spring.vault.jwks.internal.VaultCertificateDataConverter;
import org.development.wide.world.spring.vault.jwks.internal.VaultCertificateIssuer;
import org.development.wide.world.spring.vault.jwks.internal.VaultCertificateRepository;
import org.development.wide.world.spring.vault.jwks.internal.VaultJwksCertificateRotator;
import org.development.wide.world.spring.vault.jwks.property.DynamicVaultJwksInternalProperties;
import org.development.wide.world.spring.vault.jwks.property.VaultPkiInternalProperties;
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
import org.springframework.util.Assert;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.core.VaultVersionedKeyValueOperations;

/**
 * Autoconfigure HashiCorp Vault-based implementation of the dynamic JWKS
 *
 * @see DynamicJwkSet
 */
@ConditionalOnClass({
        VaultTemplate.class
})
@EnableConfigurationProperties({
        KeyStoreProperties.class,
        VaultPkiProperties.class,
        DynamicVaultJwksProperties.class,
        VaultVersionedKvProperties.class
})
@Configuration(proxyBeanMethods = false)
@AutoConfiguration(
        after = {UserDetailsServiceAutoConfiguration.class},
        before = {OAuth2AuthorizationServerJwtAutoConfiguration.class}
)
@ConditionalOnProperty(matchIfMissing = true, name = {"dynamic-jwks.vault.enabled"})
public class VaultDynamicJwksAutoConfiguration {

    /**
     * Instantiates {@link DynamicJwkSet} bean
     *
     * @param certificateRotator required dependency of {@link JwksCertificateRotator} type
     * @return {@code JWKSource<SecurityContext>}
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({JwksCertificateRotator.class})
    public JWKSource<SecurityContext> jwkSource(final JwksCertificateRotator certificateRotator) {
        return new DynamicJwkSet(certificateRotator);
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
         * Instantiates {@link VaultCertificateDataConverter} bean
         *
         * @return {@code VaultCertificateDataConverter}
         */
        @Bean
        @ConditionalOnMissingBean
        public VaultCertificateDataConverter vaultCertificateDataConverter() {
            return new VaultCertificateDataConverter();
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
         * Instantiates {@link CertificateRepository} bean
         *
         * @param vaultTemplate    required dependency of {@link VaultTemplate} type
         * @param keyStoreTemplate required dependency of {@link KeyStoreTemplate} type
         * @param kvProperties     required dependency of {@link VaultVersionedKvProperties} type
         * @return {@code CertificateRepository}
         */
        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnBean({KeyStoreTemplate.class})
        public CertificateRepository keyStoreKeeper(final VaultTemplate vaultTemplate,
                                                    final KeyStoreTemplate keyStoreTemplate,
                                                    final VaultVersionedKvProperties kvProperties) {
            Assert.notNull(keyStoreTemplate, "keyStoreTemplate cannot be null");
            Assert.notNull(vaultTemplate, "vaultTemplate cannot be null");
            Assert.notNull(kvProperties, "kvProperties cannot be null");
            final String path = kvProperties.rootPath();
            final VaultVersionedKeyValueOperations keyValueOperations = vaultTemplate.opsForVersionedKeyValue(path);
            return new VaultCertificateRepository(keyStoreTemplate, keyValueOperations);
        }

        /**
         * Instantiates {@link CertificateIssuer} bean
         *
         * @param vaultTemplate required dependency of {@link VaultTemplate} type
         * @param pkiProperties required dependency of {@link VaultPkiProperties} type
         * @param converter     required dependency of {@link VaultCertificateDataConverter} type
         * @return {@code CertificateIssuer}
         */
        @Bean
        @ConditionalOnMissingBean
        public CertificateIssuer certificateIssuer(final VaultTemplate vaultTemplate,
                                                   final VaultPkiProperties pkiProperties,
                                                   final VaultCertificateDataConverter converter) {
            Assert.notNull(vaultTemplate, "vaultTemplate cannot be null");
            Assert.notNull(pkiProperties, "pkiProperties cannot be null");
            Assert.notNull(converter, "converter cannot be null");
            final VaultPkiInternalProperties internalProperties = pkiProperties.convertToInternal();
            return new VaultCertificateIssuer(vaultTemplate, internalProperties, converter);
        }

        /**
         * Instantiates {@link JwksCertificateRotator} bean
         *
         * @param jwkSetConverter       required dependency of {@link JwkSetConverter} type
         * @param certificateIssuer     required dependency of {@link CertificateIssuer} type
         * @param properties            required dependency of {@link DynamicVaultJwksProperties} type
         * @param certificateRepository required dependency of {@link CertificateRepository} type
         * @return {@code JwksCertificateRotator}
         */
        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnBean({CertificateRepository.class, JwkSetConverter.class, CertificateIssuer.class})
        public JwksCertificateRotator vaultJwksCertificateRotator(final JwkSetConverter jwkSetConverter,
                                                                  final CertificateIssuer certificateIssuer,
                                                                  final DynamicVaultJwksProperties properties,
                                                                  final CertificateRepository certificateRepository) {
            Assert.notNull(properties, "properties cannot be null");
            final DynamicVaultJwksInternalProperties internalProperties = properties.convertToInternal();
            return new VaultJwksCertificateRotator(jwkSetConverter, certificateIssuer, certificateRepository, internalProperties);
        }

    }

}
