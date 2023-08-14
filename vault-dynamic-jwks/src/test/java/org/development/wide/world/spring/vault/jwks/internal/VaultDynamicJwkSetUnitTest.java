package org.development.wide.world.spring.vault.jwks.internal;

import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWKSecurityContext;
import com.nimbusds.jose.proc.SecurityContext;
import core.base.BaseUnitTest;
import org.development.wide.world.spring.vault.jwks.property.VaultDynamicJwksProperties;
import org.development.wide.world.spring.vault.jwks.spi.JwksCertificateRotator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

@SpringJUnitConfig({
        VaultDynamicJwkSetUnitTest.UnitTestConfiguration.class
})
class VaultDynamicJwkSetUnitTest extends BaseUnitTest {

    JWKSource<SecurityContext> jwkSource;

    @MockBean
    JwksCertificateRotator certificateRotator;

    @BeforeEach
    void setUpEach() {
        this.jwkSource = new VaultDynamicJwkSet(certificateRotator);
    }

    @Test
    void testGet() {
        /* Given */
        final JWKMatcher jwkMatcher = new JWKMatcher.Builder().build();
        final JWKSelector jwkSelector = new JWKSelector(jwkMatcher);
        final JWKSecurityContext jwkSecurityContext = new JWKSecurityContext(List.of());
        /* When */
        Mockito.when(certificateRotator.rotate())
                .thenReturn(VaultDynamicJwkSetUnitTestData.extractVaultJwkSetData());
        /* Then */
        Assertions.assertDoesNotThrow(() -> jwkSource.get(jwkSelector, jwkSecurityContext));
        /* Verify */
        Mockito.verify(certificateRotator, Mockito.times(1))
                .rotate();
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties({VaultDynamicJwksProperties.class})
    static class UnitTestConfiguration {
    }

}
