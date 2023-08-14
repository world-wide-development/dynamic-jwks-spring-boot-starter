package org.development.wide.world.spring.vault.jwks.spi;

import core.base.BaseUnitTest;
import org.development.wide.world.spring.vault.jwks.internal.JwkSetConverter;
import org.development.wide.world.spring.vault.jwks.internal.VaultJwksCertificateRotator;
import org.development.wide.world.spring.vault.jwks.property.VaultDynamicJwksProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

@SpringJUnitConfig({
        VaultJwksCertificateRotatorUnitTest.UnitTestConfiguration.class
})
class VaultJwksCertificateRotatorUnitTest extends BaseUnitTest {

    @Autowired
    JwkSetConverter jwkSetConverter;
    @Autowired
    VaultDynamicJwksProperties vaultJwksProperties;

    @Mock
    CertificateIssuer certificateIssuer;
    @Mock
    KeyStoreKeeper keyStoreKeeper;

    JwksCertificateRotator certificateRotator;

    @BeforeEach
    void setUpEach() {
        this.certificateRotator = new VaultJwksCertificateRotator(
                keyStoreKeeper,
                jwkSetConverter,
                certificateIssuer,
                vaultJwksProperties
        );
    }

    @Test
    void testRotate() {
        // When
        Mockito.when(keyStoreKeeper.findOne(Mockito.anyString()))
                .thenReturn(Optional.of(VaultJwksCertificateRotatorUnitTestData.extractExpiredKeyStoreData()));
        Mockito.when(certificateIssuer.issueOne())
                .thenReturn(VaultJwksCertificateRotatorUnitTestData.extractExpiredCertificateBundle());
        Mockito.when(keyStoreKeeper.saveOne(Mockito.anyString(), Mockito.any(), Mockito.any()))
                .thenReturn(VaultJwksCertificateRotatorUnitTestData.extractExpiredKeyStoreData());
        // Then
        Assertions.assertDoesNotThrow(() -> certificateRotator.rotate());
        // Verify
        Mockito.verify(keyStoreKeeper, Mockito.times(1))
                .findOne(Mockito.anyString());
        Mockito.verify(certificateIssuer, Mockito.times(1))
                .issueOne();
        Mockito.verify(keyStoreKeeper, Mockito.times(1))
                .saveOne(Mockito.anyString(), Mockito.any(), Mockito.any());
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties({VaultDynamicJwksProperties.class})
    static class UnitTestConfiguration {

        @Bean
        public JwkSetConverter jwkSetConverter() {
            return new JwkSetConverter();
        }

    }

}
