package org.development.wide.world.spring.jwks.internal;

import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWKSecurityContext;
import com.nimbusds.jose.proc.SecurityContext;
import core.base.BaseUnitTest;
import org.development.wide.world.spring.jwks.data.JwkSetData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith({MockitoExtension.class})
class DynamicJwkSetUnitTest extends BaseUnitTest {

    @Mock
    JwkSetData jwkSetData;
    @Mock
    AtomicJwkSetDataHolder jwkSetDataHolder;

    JWKSource<SecurityContext> jwkSource;

    @BeforeEach
    void setUpEach() {
        this.jwkSource = new DynamicJwkSet(jwkSetDataHolder);
    }

    @Test
    void testGet() {
        /* Given */
        final JWKMatcher jwkMatcher = new JWKMatcher.Builder().build();
        final JWKSelector jwkSelector = new JWKSelector(jwkMatcher);
        final JWKSecurityContext jwkSecurityContext = new JWKSecurityContext(List.of());
        BDDMockito.given(jwkSetDataHolder.getActual()).willReturn(jwkSetData);
        /* Expect */
        Assertions.assertDoesNotThrow(() -> jwkSource.get(jwkSelector, jwkSecurityContext));
        /* And */
        BDDMockito.then(jwkSetDataHolder).should().getActual();
    }

}
