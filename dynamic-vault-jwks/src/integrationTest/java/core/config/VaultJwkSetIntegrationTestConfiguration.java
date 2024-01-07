package core.config;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.development.wide.world.spring.jwks.internal.*;
import org.development.wide.world.spring.jwks.property.KeyStoreInternalProperties;
import org.development.wide.world.spring.jwks.spi.*;
import org.development.wide.world.spring.jwks.template.KeyStoreTemplate;
import org.development.wide.world.spring.vault.jwks.internal.RetryableVaultJwksCertificateRotator;
import org.development.wide.world.spring.vault.jwks.internal.VaultCertificateDataConverter;
import org.development.wide.world.spring.vault.jwks.internal.VaultCertificateIssuer;
import org.development.wide.world.spring.vault.jwks.internal.VaultCertificateRepository;
import org.development.wide.world.spring.vault.jwks.property.DynamicVaultJwksInternalProperties;
import org.development.wide.world.spring.vault.jwks.property.VaultPkiInternalProperties;
import org.development.wide.world.spring.vault.jwks.property.VaultVersionedKvInternalProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.core.VaultVersionedKeyValueOperations;

import java.time.Duration;

@TestConfiguration(proxyBeanMethods = false)
public class VaultJwkSetIntegrationTestConfiguration {

    public static final KeyStoreInternalProperties KEY_STORE_PROPERTIES = KeyStoreInternalProperties.builder()
            .password("integration-test-password")
            .alias("integration-test-alias")
            .build();
    public static final VaultPkiInternalProperties PKI_INTERNAL_PROPERTIES = VaultPkiInternalProperties.builder()
            .certificateCommonName("authorization.certificate")
            .certificateTtl(Duration.ofMinutes(1))
            .roleName("jwks")
            .rootPath("pki")
            .build();
    public static final VaultVersionedKvInternalProperties KV_INTERNAL_PROPERTIES = VaultVersionedKvInternalProperties.builder()
            .certificatePath("authorization.certificate")
            .rootPath("secret")
            .build();
    public static final DynamicVaultJwksInternalProperties JWKS_INTERNAL_PROPERTIES = DynamicVaultJwksInternalProperties.builder()
            .versionedKv(KV_INTERNAL_PROPERTIES)
            .certificateRotationRetries(3)
            .pki(PKI_INTERNAL_PROPERTIES)
            .enabled(Boolean.TRUE)
            .build();

    @Bean
    public JwkSetConverter jwkSetConverter() {
        return new JwkSetConverter();
    }

    @Bean
    public InternalKeyStore internalKeyStore() {
        return new InternalKeyStore(KEY_STORE_PROPERTIES);
    }

    @Bean
    public VaultCertificateDataConverter vaultCertificateDataConverter() {
        return new VaultCertificateDataConverter();
    }

    @Bean
    public KeyStoreTemplate keyStoreTemplate(final InternalKeyStore internalKeyStore) {
        return new KeyStoreTemplate(internalKeyStore);
    }

    @Bean
    public CertificateIssuer certificateIssuer(@NonNull final VaultTemplate vaultTemplate,
                                               @NonNull final VaultCertificateDataConverter converter) {
        return new VaultCertificateIssuer(vaultTemplate, PKI_INTERNAL_PROPERTIES, converter);
    }

    @Bean
    public CertificateRepository certificateRepository(@NonNull final VaultTemplate vaultTemplate,
                                                       @NonNull final KeyStoreTemplate keyStoreTemplate) {
        final String path = KV_INTERNAL_PROPERTIES.rootPath();
        final VaultVersionedKeyValueOperations keyValueOperations = vaultTemplate.opsForVersionedKeyValue(path);
        return new VaultCertificateRepository(keyStoreTemplate, keyValueOperations);
    }

    @Bean
    public JwkSetDataHolder jwkSetDataHolder(@NonNull final RetryableJwksCertificateRotator retryableJwksCertificateRotator) {
        return new AtomicJwkSetDataHolder(retryableJwksCertificateRotator);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(@NonNull final JwkSetDataHolder jwkSetDataHolder) {
        return new DynamicJwkSet(jwkSetDataHolder);
    }

    @Bean
    public JwksCertificateRotator jwksCertificateRotator(@NonNull final JwkSetConverter jwkSetConverter,
                                                         @NonNull final CertificateIssuer certificateIssuer,
                                                         @NonNull final CertificateRepository certificateRepository) {
        return new DefaultJwksCertificateRotator(jwkSetConverter, certificateIssuer, certificateRepository);
    }

    @Bean
    public RetryableJwksCertificateRotator retryableJwksCertificateRotator(@NonNull final JwksCertificateRotator certificateRotator) {
        return new RetryableVaultJwksCertificateRotator(certificateRotator, JWKS_INTERNAL_PROPERTIES);
    }

}
