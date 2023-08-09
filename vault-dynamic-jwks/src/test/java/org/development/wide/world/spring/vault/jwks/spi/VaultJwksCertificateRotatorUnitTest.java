package org.development.wide.world.spring.vault.jwks.spi;

import core.base.BaseUnitTest;
import org.development.wide.world.spring.vault.jwks.internal.DefaultVaultJwksCertificateRotator;
import org.development.wide.world.spring.vault.jwks.property.VaultDynamicJwksProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.vault.core.VaultPkiOperations;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.core.VaultVersionedKeyValueOperations;
import org.springframework.vault.support.CertificateBundle;

@SpringJUnitConfig({
        VaultJwksCertificateRotatorUnitTest.UnitTestConfiguration.class
})
class VaultJwksCertificateRotatorUnitTest extends BaseUnitTest {

    @Autowired
    VaultDynamicJwksProperties properties;

    VaultJwksCertificateRotator certificateRotator;

    @Mock
    VaultTemplate vaultTemplate;
    @Mock
    VaultPkiOperations pkiOperations;
    @Mock
    VaultVersionedKeyValueOperations keyValueOperations;

    @BeforeEach
    void setUpEach() {
        Mockito.when(vaultTemplate.opsForPki(Mockito.any()))
                .thenReturn(pkiOperations);
        Mockito.when(vaultTemplate.opsForVersionedKeyValue(Mockito.any()))
                .thenReturn(keyValueOperations);
        this.certificateRotator = new DefaultVaultJwksCertificateRotator(vaultTemplate, properties);
    }

    @Test
    void testRotate() {
        // When
        Mockito.when(keyValueOperations.get(Mockito.anyString(), Mockito.eq(CertificateBundle.class)))
                .thenReturn(VaultJwksCertificateRotatorUnitTestData.extractExpiredVersionedCertificateBundle());
        Mockito.when(pkiOperations.issueCertificate(Mockito.anyString(), Mockito.any()))
                .thenReturn(VaultJwksCertificateRotatorUnitTestData.extractExpiredVaultCertificateResponse());
        Mockito.when(keyValueOperations.put(Mockito.anyString(), Mockito.any()))
                .thenReturn(VaultJwksCertificateRotatorUnitTestData.extractVersionedMetadata());
        Mockito.when(keyValueOperations.get(Mockito.anyString(), Mockito.eq(CertificateBundle.class)))
                .thenReturn(VaultJwksCertificateRotatorUnitTestData.extractExpiredVersionedCertificateBundle());
        // Then
        Assertions.assertDoesNotThrow(() -> certificateRotator.rotate());
        // Verify
        Mockito.verify(keyValueOperations, Mockito.times(2))
                .get(Mockito.anyString(), Mockito.eq(CertificateBundle.class));
        Mockito.verify(pkiOperations, Mockito.times(1))
                .issueCertificate(Mockito.anyString(), Mockito.any());
        Mockito.verify(keyValueOperations, Mockito.times(1))
                .put(Mockito.anyString(), Mockito.any());
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties({VaultDynamicJwksProperties.class})
    static class UnitTestConfiguration {
    }

}
