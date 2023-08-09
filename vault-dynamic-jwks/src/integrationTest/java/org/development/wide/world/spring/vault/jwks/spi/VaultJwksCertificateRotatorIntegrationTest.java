package org.development.wide.world.spring.vault.jwks.spi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.base.BaseIntegrationTest;
import core.config.VaultJwkSetIntegrationTestConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.vault.config.VaultAutoConfiguration;
import org.springframework.context.annotation.Import;

import static org.development.wide.world.spring.vault.jwks.spi.VaultJwksCertificateConstants.FIRST_CERTIFICATE_BUNDLE;

@SpringBootTest(classes = {
        VaultAutoConfiguration.class,
        JacksonAutoConfiguration.class,
        VaultJwksCertificateRotator.class
})
@Import({VaultJwkSetIntegrationTestConfiguration.class})
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class VaultJwksCertificateRotatorIntegrationTest extends BaseIntegrationTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    VaultJwksCertificateRotator certificateRotator;

    @Test
    void testRotate() {
        Assertions.assertDoesNotThrow(() -> certificateRotator.rotate());
    }

    @Test
    void testDoubleRotate() {
        Assertions.assertDoesNotThrow(() -> certificateRotator.rotate());
        Assertions.assertDoesNotThrow(() -> certificateRotator.rotate());
    }

    @Test
    void testCertificateBundleJacksonSerialization() throws JsonProcessingException {
        final String certificateBundleJsonString = objectMapper.writeValueAsString(FIRST_CERTIFICATE_BUNDLE);
        Assertions.assertNotNull(certificateBundleJsonString);
    }

}
