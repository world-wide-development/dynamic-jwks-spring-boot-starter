package org.development.wide.world.spring.redis.internal;

import ch.qos.logback.classic.Level;
import core.base.BaseUnitTest;
import core.utils.LogbackUtils;
import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.data.JwkSetData;
import org.development.wide.world.spring.jwks.spi.JwkSetRotationFunction;
import org.development.wide.world.spring.jwks.spi.JwksCertificateRotator;
import org.development.wide.world.spring.redis.property.CertificateRotationInternalProperties;
import org.development.wide.world.spring.redis.property.DynamicRedisJwksInternalProperties;
import org.development.wide.world.spring.redis.property.RedisKvInternalProperties;
import org.development.wide.world.spring.redis.property.RotationRetryInternalProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

@ExtendWith({MockitoExtension.class})
class RetryableRedisJwksCertificateRotatorUnitTest extends BaseUnitTest {

    RotationRetryInternalProperties rotationRetryInternalProperties = RotationRetryInternalProperties.builder()
            .fixedBackoff(Duration.ofMinutes(3))
            .maxAttempts(1)
            .build();
    CertificateRotationInternalProperties rotationProperties = CertificateRotationInternalProperties.builder()
            .retry(rotationRetryInternalProperties)
            .rotateBefore(Duration.ofMinutes(3))
            .build();
    @Spy
    @SuppressWarnings({"unused"})
    DynamicRedisJwksInternalProperties properties = DynamicRedisJwksInternalProperties.builder()
            .kv(RedisKvInternalProperties.builder().certificateKey("given-certificate-key").build())
            .certificateRotation(rotationProperties)
            .build();
    @Mock
    JwksCertificateRotator certificateRotator;

    @InjectMocks
    RetryableRedisJwksCertificateRotator retryableCertificateRotator;

    @Captor
    ArgumentCaptor<JwkSetRotationFunction> jwksRotationFnArgumentCaptor;

    @BeforeEach
    void setUpEach() {
        LogbackUtils.changeLoggingLevel(Level.INFO, RetryableRedisJwksCertificateRotator.class);
    }

    @Test
    void testRotateSuccess() {
        // Given
        final CertificateData givenCertificateData = CertificateData.builder()
                .build();
        final JwkSetData givenJwkSetData = JwkSetData.builder()
                .build();
        BDDMockito.given(certificateRotator.rotate(jwksRotationFnArgumentCaptor.capture()))
                .willReturn(givenJwkSetData);
        // When
        final JwkSetData rotationResult = retryableCertificateRotator.rotate();
        final CertificateData functionResult = jwksRotationFnArgumentCaptor.getValue()
                .apply(rotationData -> givenCertificateData);
        // Then
        Assertions.assertNotNull(rotationResult);
        Assertions.assertNotNull(functionResult);
        // And
        BDDMockito.then(certificateRotator).should().rotate(jwksRotationFnArgumentCaptor.capture());
    }

    @Test
    void testRotateSuccessWithDebug() {
        // Set up
        LogbackUtils.changeLoggingLevel(Level.DEBUG, RetryableRedisJwksCertificateRotator.class);
        // Given
        final CertificateData givenCertificateData = CertificateData.builder()
                .build();
        final JwkSetData givenJwkSetData = JwkSetData.builder()
                .build();
        BDDMockito.given(certificateRotator.rotate(jwksRotationFnArgumentCaptor.capture()))
                .willReturn(givenJwkSetData);
        // When
        final JwkSetData rotationResult = retryableCertificateRotator.rotate();
        final CertificateData functionResult = jwksRotationFnArgumentCaptor.getValue()
                .apply(rotationData -> givenCertificateData);
        // Then
        Assertions.assertNotNull(rotationResult);
        Assertions.assertNotNull(functionResult);
        // And
        BDDMockito.then(certificateRotator).should().rotate(jwksRotationFnArgumentCaptor.capture());
    }

}
