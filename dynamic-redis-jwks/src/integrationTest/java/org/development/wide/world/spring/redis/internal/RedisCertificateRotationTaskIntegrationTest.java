package org.development.wide.world.spring.redis.internal;

import core.base.BaseIntegrationTest;
import core.config.RedisJwkSetIntegrationTestConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.CountDownLatch;

@SpringBootTest
@Import({RedisJwkSetIntegrationTestConfiguration.class})
class RedisCertificateRotationTaskIntegrationTest extends BaseIntegrationTest {

    @Autowired
    RedisCertificateRotationTask rotationTask;
    @Autowired
    ThreadPoolTaskExecutor applicationTaskExecutor;

    @Test
    void testRunByMultipleThreads() throws InterruptedException {
        // Given
        final int threadsAmount = 10;
        final CountDownLatch countDownLatch = new CountDownLatch(threadsAmount);
        // Expect
        for (int i = 0; i < threadsAmount; i++) {
            applicationTaskExecutor.execute(() -> {
                Assertions.assertDoesNotThrow(() -> rotationTask.run());
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
    }

}
