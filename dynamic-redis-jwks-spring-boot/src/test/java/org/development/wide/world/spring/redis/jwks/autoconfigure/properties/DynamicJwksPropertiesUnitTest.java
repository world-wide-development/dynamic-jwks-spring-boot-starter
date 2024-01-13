package org.development.wide.world.spring.redis.jwks.autoconfigure.properties;

import core.base.BaseUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

@ExtendWith({MockitoExtension.class})
@SuppressWarnings({"ResultOfMethodCallIgnored"})
class DynamicJwksPropertiesUnitTest extends BaseUnitTest {

    @Mock
    KeyStoreProperties keyStoreProperties;
    @Mock
    BCCertificateProperties bcCertificateProperties;
    @Mock
    DynamicRedisJwksProperties dynamicRedisJwksProperties;
    @Mock
    CertificateRotationProperties certificateRotationProperties;

    @Test
    void testInstantiationSuccessTtlHours() {
        // Given
        BDDMockito.given(bcCertificateProperties.certificateTtl()).willReturn(Duration.ofHours(6));
        BDDMockito.given(dynamicRedisJwksProperties.certificateRotation()).willReturn(certificateRotationProperties);
        BDDMockito.given(certificateRotationProperties.rotateBefore()).willReturn(Duration.ofMinutes(20));
        // Expect
        Assertions.assertDoesNotThrow(() -> new DynamicJwksProperties(
                keyStoreProperties,
                bcCertificateProperties,
                dynamicRedisJwksProperties
        ));
        // And
        BDDMockito.then(bcCertificateProperties).should().certificateTtl();
        BDDMockito.then(dynamicRedisJwksProperties).should().certificateRotation();
        BDDMockito.then(certificateRotationProperties).should().rotateBefore();
    }

    @Test
    void testInstantiationSuccessTtlSeconds() {
        // Given
        BDDMockito.given(bcCertificateProperties.certificateTtl()).willReturn(Duration.ofSeconds(121));
        BDDMockito.given(dynamicRedisJwksProperties.certificateRotation()).willReturn(certificateRotationProperties);
        BDDMockito.given(certificateRotationProperties.rotateBefore()).willReturn(Duration.ofMinutes(2));
        // Expect
        Assertions.assertDoesNotThrow(() -> new DynamicJwksProperties(
                keyStoreProperties,
                bcCertificateProperties,
                dynamicRedisJwksProperties
        ));
        // And
        BDDMockito.then(bcCertificateProperties).should().certificateTtl();
        BDDMockito.then(dynamicRedisJwksProperties).should().certificateRotation();
        BDDMockito.then(certificateRotationProperties).should().rotateBefore();
    }

    @Test
    void testInstantiationThrowsCertificateTtlPropertyMustBeGreaterThenRotateBeforeExceptionTtlLess() {
        // Given
        BDDMockito.given(bcCertificateProperties.certificateTtl()).willReturn(Duration.ofSeconds(119));
        BDDMockito.given(dynamicRedisJwksProperties.certificateRotation()).willReturn(certificateRotationProperties);
        BDDMockito.given(certificateRotationProperties.rotateBefore()).willReturn(Duration.ofMinutes(2));
        // Expect
        final IllegalStateException exception = Assertions
                .assertThrows(IllegalStateException.class, () -> new DynamicJwksProperties(
                        keyStoreProperties,
                        bcCertificateProperties,
                        dynamicRedisJwksProperties
                ));
        Assertions.assertEquals("certificateTtl property must be greater then rotateBefore", exception.getMessage());
        // And
        BDDMockito.then(bcCertificateProperties).should().certificateTtl();
        BDDMockito.then(dynamicRedisJwksProperties).should().certificateRotation();
        BDDMockito.then(certificateRotationProperties).should().rotateBefore();
    }

    @Test
    void testInstantiationThrowsCertificateTtlPropertyMustBeGreaterThenRotateBeforeExceptionTtlEquals() {
        // Given
        BDDMockito.given(bcCertificateProperties.certificateTtl()).willReturn(Duration.ofSeconds(120));
        BDDMockito.given(dynamicRedisJwksProperties.certificateRotation()).willReturn(certificateRotationProperties);
        BDDMockito.given(certificateRotationProperties.rotateBefore()).willReturn(Duration.ofMinutes(2));
        // Expect
        final IllegalStateException exception = Assertions
                .assertThrows(IllegalStateException.class, () -> new DynamicJwksProperties(
                        keyStoreProperties,
                        bcCertificateProperties,
                        dynamicRedisJwksProperties
                ));
        Assertions.assertEquals("certificateTtl property must be greater then rotateBefore", exception.getMessage());
        // And
        BDDMockito.then(bcCertificateProperties).should().certificateTtl();
        BDDMockito.then(dynamicRedisJwksProperties).should().certificateRotation();
        BDDMockito.then(certificateRotationProperties).should().rotateBefore();
    }

}
