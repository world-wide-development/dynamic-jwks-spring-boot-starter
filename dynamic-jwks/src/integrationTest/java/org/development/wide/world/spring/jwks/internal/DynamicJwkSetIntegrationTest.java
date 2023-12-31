package org.development.wide.world.spring.jwks.internal;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWKSecurityContext;
import com.nimbusds.jose.proc.SecurityContext;
import core.base.BaseIntegrationTest;
import org.development.wide.world.spring.jwks.data.JwkSetData;
import org.development.wide.world.spring.jwks.spi.RetryableJwksCertificateRotator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@SpringJUnitConfig
class DynamicJwkSetIntegrationTest extends BaseIntegrationTest {

    @Mock
    JWKSet jwkSet;
    @Mock
    JwkSetData jwkSetData;
    @MockBean
    RetryableJwksCertificateRotator certificateRotator;

    JWKSource<SecurityContext> jwkSource;

    @BeforeAll
    static void setUpAll() {
        if (LoggerFactory.getLogger(DynamicJwkSet.class) instanceof Logger logbackLogger) {
            logbackLogger.setLevel(Level.DEBUG);
        }
    }

    @Test
    void testGet() {
        /* Init */
        this.jwkSource = new DynamicJwkSet(certificateRotator);
        /* Given */
        final JWKMatcher jwkMatcher = new JWKMatcher.Builder().build();
        final JWKSelector jwkSelector = new JWKSelector(jwkMatcher);
        final JWKSecurityContext jwkSecurityContext = new JWKSecurityContext(List.of());
        /* When */
        Mockito.when(certificateRotator.rotate()).thenReturn(jwkSetData);
        /* Then */
        Assertions.assertDoesNotThrow(() -> jwkSource.get(jwkSelector, jwkSecurityContext));
        /* Verify */
        Mockito.verify(certificateRotator, Mockito.times(1))
                .rotate();
    }

    @Test
    void testGetValidJwkSource() {
        /* Init */
        this.jwkSource = new DynamicJwkSet(certificateRotator, new AtomicReference<>(jwkSetData));
        /* Given */
        final JWKMatcher jwkMatcher = new JWKMatcher.Builder().build();
        final JWKSelector jwkSelector = new JWKSelector(jwkMatcher);
        final JWKSecurityContext jwkSecurityContext = new JWKSecurityContext(List.of());
        /* When */
        Mockito.when(jwkSetData.checkCertificateValidity()).thenReturn(Boolean.TRUE);
        Mockito.when(jwkSetData.jwkSet()).thenReturn(jwkSet);
        /* Then */
        Assertions.assertDoesNotThrow(() -> jwkSource.get(jwkSelector, jwkSecurityContext));
        /* Verify */
        Mockito.verify(jwkSetData, Mockito.times(1))
                .checkCertificateValidity();
        Mockito.verify(jwkSetData, Mockito.times(1))
                .jwkSet();
    }

    @Test
    void testGetWithRotation() {
        /* Init */
        this.jwkSource = new DynamicJwkSet(certificateRotator, new AtomicReference<>(jwkSetData));
        /* Given */
        final JWKMatcher jwkMatcher = new JWKMatcher.Builder().build();
        final JWKSelector jwkSelector = new JWKSelector(jwkMatcher);
        final JWKSecurityContext jwkSecurityContext = new JWKSecurityContext(List.of());
        /* When */
        Mockito.when(jwkSetData.checkCertificateValidity()).thenReturn(Boolean.FALSE);
        Mockito.when(certificateRotator.rotate()).thenReturn(jwkSetData);
        Mockito.when(jwkSetData.jwkSet()).thenReturn(jwkSet);
        /* Then */
        Assertions.assertDoesNotThrow(() -> jwkSource.get(jwkSelector, jwkSecurityContext));
        /* Verify */
        Mockito.verify(jwkSetData, Mockito.times(1))
                .checkCertificateValidity();
        Mockito.verify(certificateRotator, Mockito.times(1))
                .rotate();
        Mockito.verify(jwkSetData, Mockito.times(1))
                .jwkSet();
    }

}
