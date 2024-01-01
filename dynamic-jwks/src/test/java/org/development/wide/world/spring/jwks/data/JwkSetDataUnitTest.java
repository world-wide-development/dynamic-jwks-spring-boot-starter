package org.development.wide.world.spring.jwks.data;

import com.nimbusds.jose.jwk.JWKSet;
import core.base.BaseUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class JwkSetDataUnitTest extends BaseUnitTest {

    @Mock
    JWKSet jwkSet;
    @Mock
    CertificateData certificateData;

    JwkSetData jwkSetData;

    @BeforeEach
    void setUpEach() {
        this.jwkSetData = JwkSetData.builder()
                .certificateData(certificateData)
                .jwkSet(jwkSet)
                .build();
    }

    @Test
    void testCheckCertificateValidityTrueResult() {
        // Given
        BDDMockito.given(certificateData.checkCertificateValidity()).willReturn(Boolean.TRUE);
        // When
        final boolean result = jwkSetData.checkCertificateValidity();
        // Then
        Assertions.assertTrue(result);
        // And
        BDDMockito.then(certificateData).should().checkCertificateValidity();
    }

    @Test
    void testCheckCertificateValidityFalseResult() {
        // Given
        BDDMockito.given(certificateData.checkCertificateValidity()).willReturn(Boolean.FALSE);
        // When
        final boolean result = jwkSetData.checkCertificateValidity();
        // Then
        Assertions.assertFalse(result);
        // And
        BDDMockito.then(certificateData).should().checkCertificateValidity();
    }

}
