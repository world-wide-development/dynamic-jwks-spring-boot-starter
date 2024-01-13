package org.development.wide.world.spring.redis.internal;

import core.base.BaseIntegrationTest;
import core.config.RedisJwkSetIntegrationTestConfiguration;
import org.development.wide.world.spring.jwks.spi.RetryableJwksCertificateRotator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import({RedisJwkSetIntegrationTestConfiguration.class})
class RetryableRedisJwksCertificateRotatorIntegrationTest extends BaseIntegrationTest {

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
