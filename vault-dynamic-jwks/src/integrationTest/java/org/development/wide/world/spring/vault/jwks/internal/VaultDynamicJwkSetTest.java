package org.development.wide.world.spring.vault.jwks.internal;

import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWKSecurityContext;
import com.nimbusds.jose.proc.SecurityContext;
import core.base.BaseIntegrationTest;
import core.config.VaultJwkSetIntegrationTestConfiguration;
import org.development.wide.world.spring.vault.jwks.spi.VaultJwksCertificateRotator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.vault.config.VaultAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.util.List;

@SpringBootTest(classes = {
        JWKSource.class,
        VaultAutoConfiguration.class,
        JacksonAutoConfiguration.class,
        VaultJwksCertificateRotator.class
})
@Import({VaultJwkSetIntegrationTestConfiguration.class})
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class VaultDynamicJwkSetTest extends BaseIntegrationTest {

    @Autowired
    JWKSource<SecurityContext> jwkSource;

    @Test
    void testGet() {
        final JWKMatcher jwkMatcher = new JWKMatcher.Builder().build();
        final JWKSelector jwkSelector = new JWKSelector(jwkMatcher);
        final JWKSecurityContext jwkSecurityContext = new JWKSecurityContext(List.of());
        Assertions.assertDoesNotThrow(() -> jwkSource.get(jwkSelector, jwkSecurityContext));
    }

}
