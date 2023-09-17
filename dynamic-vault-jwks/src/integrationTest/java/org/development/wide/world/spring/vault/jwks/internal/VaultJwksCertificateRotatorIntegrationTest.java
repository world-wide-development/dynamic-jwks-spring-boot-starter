package org.development.wide.world.spring.vault.jwks.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.base.BaseIntegrationTest;
import core.config.VaultJwkSetIntegrationTestConfiguration;
import org.development.wide.world.spring.jwks.spi.JwksCertificateRotator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.vault.config.VaultAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootTest(classes = {
        VaultAutoConfiguration.class,
        JwksCertificateRotator.class,
        JacksonAutoConfiguration.class
})
@Import({VaultJwkSetIntegrationTestConfiguration.class})
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class VaultJwksCertificateRotatorIntegrationTest extends BaseIntegrationTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JwksCertificateRotator certificateRotator;

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
