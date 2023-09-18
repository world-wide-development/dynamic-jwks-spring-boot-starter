package org.development.wide.world.spring.vault.jwks.internal;

import core.base.BaseIntegrationTest;
import core.config.VaultJwkSetIntegrationTestConfiguration;
import org.development.wide.world.spring.jwks.spi.RetryableJwksCertificateRotator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.vault.config.VaultAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootTest(classes = {
        VaultAutoConfiguration.class,
        RetryableJwksCertificateRotator.class
})
@Import({VaultJwkSetIntegrationTestConfiguration.class})
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class RetryableVaultJwksCertificateRotatorIntegrationTest extends BaseIntegrationTest {

    @Autowired
    RetryableJwksCertificateRotator certificateRotator;

    @Test
    void testRotate() {
        Assertions.assertDoesNotThrow(() -> certificateRotator.rotate());
    }

    @Test
    void testDoubleRotate() {
        Assertions.assertDoesNotThrow(() -> certificateRotator.rotate());
        Assertions.assertDoesNotThrow(() -> certificateRotator.rotate());
    }

}
