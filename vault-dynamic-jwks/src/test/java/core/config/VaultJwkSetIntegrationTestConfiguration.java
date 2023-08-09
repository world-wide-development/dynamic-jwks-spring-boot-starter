package core.config;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.development.wide.world.spring.vault.jwks.internal.DefaultVaultJwksCertificateRotator;
import org.development.wide.world.spring.vault.jwks.internal.VaultDynamicJwkSet;
import org.development.wide.world.spring.vault.jwks.property.VaultDynamicJwksProperties;
import org.development.wide.world.spring.vault.jwks.spi.VaultJwksCertificateRotator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.vault.core.VaultTemplate;

@TestConfiguration
@EnableConfigurationProperties({VaultDynamicJwksProperties.class})
@SuppressWarnings({"SpringJavaInjectionPointsAutowiringInspection"})
public class VaultJwkSetIntegrationTestConfiguration {

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
