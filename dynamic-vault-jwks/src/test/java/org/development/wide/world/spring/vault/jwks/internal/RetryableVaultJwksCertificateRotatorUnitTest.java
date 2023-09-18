package org.development.wide.world.spring.vault.jwks.internal;

import core.base.BaseUnitTest;
import org.development.wide.world.spring.jwks.internal.DefaultJwksCertificateRotator;
import org.development.wide.world.spring.jwks.internal.JwkSetConverter;
import org.development.wide.world.spring.jwks.spi.CertificateIssuer;
import org.development.wide.world.spring.jwks.spi.CertificateRepository;
import org.development.wide.world.spring.jwks.spi.JwksCertificateRotator;
import org.development.wide.world.spring.jwks.spi.RetryableJwksCertificateRotator;
import org.development.wide.world.spring.vault.jwks.property.DynamicVaultJwksInternalProperties;
import org.development.wide.world.spring.vault.jwks.property.VaultPkiInternalProperties;
import org.development.wide.world.spring.vault.jwks.property.VaultVersionedKvInternalProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Duration;
import java.util.Optional;

@SpringJUnitConfig({
        RetryableVaultJwksCertificateRotatorUnitTest.UnitTestConfiguration.class
})
class RetryableVaultJwksCertificateRotatorUnitTest extends BaseUnitTest {

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

    @Autowired
    JwkSetConverter jwkSetConverter;

    @Mock
    CertificateIssuer certificateIssuer;
    @Mock
    CertificateRepository certificateRepository;

    RetryableJwksCertificateRotator certificateRotator;

    @BeforeEach
    void setUpEach() {
        final JwksCertificateRotator jwksCertificateRotator = new DefaultJwksCertificateRotator(
                jwkSetConverter,
                certificateIssuer,
                certificateRepository
        );
        this.certificateRotator = new RetryableVaultJwksCertificateRotator(
                jwksCertificateRotator,
                JWKS_INTERNAL_PROPERTIES
        );
    }

    @Test
    void testRotate() {
        // When
        Mockito.when(certificateRepository.findOne(Mockito.anyString()))
                .thenReturn(Optional.of(VaultJwksCertificateRotatorUnitTestData.extractExpiredKeyStoreData()));
        Mockito.when(certificateIssuer.issueOne())
                .thenReturn(VaultJwksCertificateRotatorUnitTestData.extractExpiredKeyStoreData());
        Mockito.when(certificateRepository.saveOne(Mockito.anyString(), Mockito.any()))
                .thenReturn(VaultJwksCertificateRotatorUnitTestData.extractExpiredKeyStoreData());
        // Then
        Assertions.assertDoesNotThrow(() -> certificateRotator.rotate());
        // Verify
        Mockito.verify(certificateRepository, Mockito.times(1))
                .findOne(Mockito.anyString());
        Mockito.verify(certificateIssuer, Mockito.times(1))
                .issueOne();
        Mockito.verify(certificateRepository, Mockito.times(1))
                .saveOne(Mockito.anyString(), Mockito.any());
    }

    @Configuration(proxyBeanMethods = false)
    static class UnitTestConfiguration {

        @Bean
        public JwkSetConverter jwkSetConverter() {
            return new JwkSetConverter();
        }

    }

}
