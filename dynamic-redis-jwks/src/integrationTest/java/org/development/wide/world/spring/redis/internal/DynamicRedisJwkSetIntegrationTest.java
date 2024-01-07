package org.development.wide.world.spring.redis.internal;

import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWKSecurityContext;
import com.nimbusds.jose.proc.SecurityContext;
import core.base.BaseIntegrationTest;
import core.config.RedisJwkSetIntegrationTestConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@SpringBootTest
@Import({RedisJwkSetIntegrationTestConfiguration.class})
class DynamicRedisJwkSetIntegrationTest extends BaseIntegrationTest {

    @Autowired
    JWKSource<SecurityContext> jwkSource;

    @Test
    void testGet() {
        final JWKMatcher jwkMatcher = new JWKMatcher.Builder().build();
        final JWKSelector jwkSelector = new JWKSelector(jwkMatcher);
        final JWKSecurityContext jwkSecurityContext = new JWKSecurityContext(List.of());
        Assertions.assertDoesNotThrow(() -> jwkSource.get(jwkSelector, jwkSecurityContext));
    }

    @Test
    void testDoubleGet() {
        final JWKMatcher jwkMatcher = new JWKMatcher.Builder().build();
        final JWKSelector jwkSelector = new JWKSelector(jwkMatcher);
        final JWKSecurityContext jwkSecurityContext = new JWKSecurityContext(List.of());
        Assertions.assertDoesNotThrow(() -> jwkSource.get(jwkSelector, jwkSecurityContext));
        Assertions.assertDoesNotThrow(() -> jwkSource.get(jwkSelector, jwkSecurityContext));
    }

}
