package org.development.wide.world.spring.jwks.internal;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.*;
import core.base.BaseUnitTest;
import core.utils.JwkSetTestDataUtils;
import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.data.JwkSetData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith({MockitoExtension.class})
class DynamicJwkSetUnitTest extends BaseUnitTest {

    @Mock
    AtomicJwkSetDataHolder jwkSetDataHolder;

    @InjectMocks
    DynamicJwkSet dynamicJwkSet;

    @Test
    void testGetForJwks() {
        /* Given */
        final List<JWK> givenJwkKeys = List.of(
                JwkSetTestDataUtils.issueJwk(),
                JwkSetTestDataUtils.issueJwk()
        );
        final JWKSet givenJwkSet = new JWKSet(givenJwkKeys);
        final JWKMatcher jwkMatcher = new JWKMatcher.Builder()
                .build();
        final JwkSetData givenJwkSetData = JwkSetData.builder()
                .certificateData(CertificateData.builder().build())
                .jwkSet(givenJwkSet)
                .build();
        final JWKSelector jwkSelector = new JWKSelector(jwkMatcher);
        BDDMockito.given(jwkSetDataHolder.getActual()).willReturn(givenJwkSetData);
        /* When */
        final List<JWK> result = dynamicJwkSet.get(jwkSelector, null);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertIterableEquals(givenJwkKeys, result);
        /* And */
        BDDMockito.then(jwkSetDataHolder).should().getActual();
    }

    @Test
    void testGetForJwksWithAlgorithm() {
        /* Given */
        final List<JWK> givenJwkKeys = List.of(
                JwkSetTestDataUtils.issueJwk(),
                JwkSetTestDataUtils.issueJwk()
        );
        final JWKSet givenJwkSet = new JWKSet(givenJwkKeys);
        final JWKMatcher jwkMatcher = new JWKMatcher.Builder()
                .algorithm(JWSAlgorithm.RS256)
                .build();
        final JwkSetData givenJwkSetData = JwkSetData.builder()
                .certificateData(CertificateData.builder().build())
                .jwkSet(givenJwkSet)
                .build();
        final JWKSelector jwkSelector = new JWKSelector(jwkMatcher);
        BDDMockito.given(jwkSetDataHolder.getActual()).willReturn(givenJwkSetData);
        /* When */
        final List<JWK> result = dynamicJwkSet.get(jwkSelector, null);
        // Then
        Assertions.assertNotNull(result);
        /* And */
        BDDMockito.then(jwkSetDataHolder).should().getActual();
    }

    @Test
    void testGetForJwksWithAlgorithmAndKeyType() {
        /* Given */
        final List<JWK> givenJwkKeys = List.of(
                JwkSetTestDataUtils.issueJwk(),
                JwkSetTestDataUtils.issueJwk()
        );
        final JWKSet givenJwkSet = new JWKSet(givenJwkKeys);
        final JWKMatcher jwkMatcher = new JWKMatcher.Builder()
                .algorithm(JWSAlgorithm.RS256)
                .keyType(KeyType.RSA)
                .build();
        final JwkSetData givenJwkSetData = JwkSetData.builder()
                .certificateData(CertificateData.builder().build())
                .jwkSet(givenJwkSet)
                .build();
        final JWKSelector jwkSelector = new JWKSelector(jwkMatcher);
        BDDMockito.given(jwkSetDataHolder.getActual()).willReturn(givenJwkSetData);
        /* When */
        final List<JWK> result = dynamicJwkSet.get(jwkSelector, null);
        // Then
        Assertions.assertNotNull(result);
        /* And */
        BDDMockito.then(jwkSetDataHolder).should().getActual();
    }

    @Test
    void testGetForJwtEncoder() {
        /* Given */
        final List<JWK> givenJwkKeys = List.of(
                JwkSetTestDataUtils.issueJwk(),
                JwkSetTestDataUtils.issueJwk()
        );
        final JWKSet givenJwkSet = new JWKSet(givenJwkKeys);
        final JWKMatcher jwkMatcher = new JWKMatcher.Builder()
                .algorithms(JWSAlgorithm.RS256, null)
                .keyUses(KeyUse.SIGNATURE, null)
                .keyType(KeyType.RSA)
                .build();
        final JwkSetData givenJwkSetData = JwkSetData.builder()
                .certificateData(CertificateData.builder().build())
                .jwkSet(givenJwkSet)
                .build();
        final JWKSelector jwkSelector = new JWKSelector(jwkMatcher);
        BDDMockito.given(jwkSetDataHolder.getActual()).willReturn(givenJwkSetData);
        /* When */
        final List<JWK> result = dynamicJwkSet.get(jwkSelector, null);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertIterableEquals(List.of(givenJwkKeys.get(0)), result);
        /* And */
        BDDMockito.then(jwkSetDataHolder).should().getActual();
    }

}
