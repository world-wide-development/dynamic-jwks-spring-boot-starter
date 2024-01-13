package org.development.wide.world.spring.jwks.data;

import core.base.BaseUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class CertificateDataUnitTest extends BaseUnitTest {

    CertificateData certificateData;

    @BeforeEach
    void setUpEach() {
        certificateData = CertificateData.builder()
                .build();
    }

    @Test
    void testCheckCertificateValidityReturnsFalse() {
        // When
        final boolean result = certificateData.checkCertificateValidity();
        // Then
        Assertions.assertFalse(result);
    }

}
