package core.config;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.development.wide.world.spring.vault.jwks.internal.*;
import org.development.wide.world.spring.vault.jwks.property.DynamicJwksProperties;
import org.development.wide.world.spring.vault.jwks.property.KeyStoreProperties;
import org.development.wide.world.spring.vault.jwks.spi.CertificateIssuer;
import org.development.wide.world.spring.vault.jwks.spi.JwksCertificateRotator;
import org.development.wide.world.spring.vault.jwks.spi.KeyStoreKeeper;
import org.development.wide.world.spring.vault.jwks.template.KeyStoreTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.vault.core.VaultTemplate;

@TestConfiguration
@EnableConfigurationProperties({
        KeyStoreProperties.class,
        DynamicJwksProperties.class
})
public class VaultJwkSetIntegrationTestConfiguration {

    @Bean
    public JwkSetConverter jwkSetConverter() {
        return new JwkSetConverter();
    }

    @Bean
    public InternalKeyStore internalKeyStore(final KeyStoreProperties properties) {
        return new InternalKeyStore(properties);
    }

    @Bean
    public KeyStoreTemplate keyStoreTemplate(final InternalKeyStore internalKeyStore) {
        return new KeyStoreTemplate(internalKeyStore);
    }

    @Bean
    public CertificateIssuer certificateIssuer(final VaultTemplate vaultTemplate,
                                               final DynamicJwksProperties properties) {
        return new VaultCertificateIssuer(vaultTemplate, properties);
    }

    @Bean
    public KeyStoreKeeper certificateKeyStoreKeeper(final VaultTemplate vaultTemplate,
                                                    final DynamicJwksProperties properties,
                                                    final KeyStoreTemplate keyStoreTemplate) {
        return new VaultKeyStoreKeeper(vaultTemplate, properties, keyStoreTemplate);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(final JwksCertificateRotator certificateRotator) {
        return new VaultDynamicJwkSet(certificateRotator);
    }

    @Bean
    public JwksCertificateRotator vaultJwksCertificateRotator(final KeyStoreKeeper keyStoreKeeper,
                                                              final JwkSetConverter jwkSetConverter,
                                                              final DynamicJwksProperties properties,
                                                              final CertificateIssuer certificateIssuer) {
        return new VaultJwksCertificateRotator(keyStoreKeeper, jwkSetConverter, properties, certificateIssuer);
    }

}
