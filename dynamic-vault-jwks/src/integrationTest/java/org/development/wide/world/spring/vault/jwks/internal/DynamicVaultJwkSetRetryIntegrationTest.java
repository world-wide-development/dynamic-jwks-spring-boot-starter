package org.development.wide.world.spring.vault.jwks.internal;

import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWKSecurityContext;
import com.nimbusds.jose.proc.SecurityContext;
import core.base.BaseIntegrationTest;
import core.config.VaultJwkSetIntegrationTestConfiguration;
import org.development.wide.world.spring.jwks.spi.RetryableJwksCertificateRotator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.vault.config.VaultAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@SpringBootTest(
        classes = {
                VaultAutoConfiguration.class,
                TaskExecutionAutoConfiguration.class,
                RetryableJwksCertificateRotator.class,
                VaultJwkSetIntegrationTestConfiguration.class
        }
)
@Import({VaultJwkSetIntegrationTestConfiguration.class})
class DynamicVaultJwkSetRetryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    JWKSource<SecurityContext> jwkSource;
    @Autowired
    ThreadPoolTaskExecutor applicationTaskExecutor;

    @Test
    void testGetInParallel() throws InterruptedException {
        final int threadsAmount = 10;
        final CountDownLatch countDownLatch = new CountDownLatch(threadsAmount);
        final JWKMatcher jwkMatcher = new JWKMatcher.Builder()
                .build();
        final JWKSelector jwkSelector = new JWKSelector(jwkMatcher);
        final JWKSecurityContext jwkSecurityContext = new JWKSecurityContext(List.of());
        for (int i = 0; i < threadsAmount; i++) {
            applicationTaskExecutor.execute(() -> {
                Assertions.assertDoesNotThrow(() -> jwkSource.get(jwkSelector, jwkSecurityContext));
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
    }

}
