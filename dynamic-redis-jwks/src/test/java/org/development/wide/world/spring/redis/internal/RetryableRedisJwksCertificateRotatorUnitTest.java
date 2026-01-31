package org.development.wide.world.spring.redis.internal;

import ch.qos.logback.classic.Level;
import core.base.BaseUnitTest;
import core.utils.LogbackUtils;
import org.assertj.core.api.BDDAssertions;
import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.data.JwkSetData;
import org.development.wide.world.spring.jwks.spi.JwkSetRotationFunction;
import org.development.wide.world.spring.jwks.spi.JwksCertificateRotator;
import org.development.wide.world.spring.redis.exception.CertificateRotationException;
import org.development.wide.world.spring.redis.property.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.retry.RetryException;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.core.retry.Retryable;

import java.time.Duration;

@ExtendWith({MockitoExtension.class})
@SuppressWarnings({"ResultOfMethodCallIgnored"})
class RetryableRedisJwksCertificateRotatorUnitTest extends BaseUnitTest {

    final RotationRetryInternalProperties rotationRetryInternalProperties = RotationRetryInternalProperties.builder()
            .fixedBackoff(Duration.ofMinutes(3))
            .maxAttempts(1)
            .build();
    final RotationScheduleInternalProperties rotationScheduleProperties = RotationScheduleInternalProperties.builder()
            .interval(Duration.ZERO)
            .enabled(Boolean.FALSE)
            .build();
    final CertificateRotationInternalProperties rotationProperties = CertificateRotationInternalProperties.builder()
            .rotationLockKey("given-rotation-lock-key")
            .retry(rotationRetryInternalProperties)
            .schedule(rotationScheduleProperties)
            .rotateBefore(Duration.ofMinutes(3))
            .build();
    @Spy
    final DynamicRedisJwksInternalProperties properties = DynamicRedisJwksInternalProperties.builder()
            .kv(RedisKvInternalProperties.builder().certificateKey("given-certificate-key").build())
            .certificateRotation(rotationProperties)
            .enabled(Boolean.TRUE)
            .build();

    @Mock
    JwksCertificateRotator jwksRotator;
    @Mock
    RetryTemplate rotationRetryTemplate;

    @InjectMocks
    RetryableRedisJwksCertificateRotator retryableCertificateRotator;

    @Captor
    ArgumentCaptor<Retryable<CertificateData>> retryableArgumentCaptor;
    @Captor
    ArgumentCaptor<JwkSetRotationFunction> jwksRotationFnArgumentCaptor;

    @BeforeEach
    void setUpEach() {
        LogbackUtils.changeLoggingLevel(Level.INFO, RetryableRedisJwksCertificateRotator.class);
    }

    @Test
    void testRotateSuccess() throws Throwable {
        // Given
        final CertificateData givenCertificateData = CertificateData.builder()
                .build();
        final JwkSetData givenJwkSetData = JwkSetData.builder()
                .build();
        BDDMockito.given(jwksRotator.rotate(jwksRotationFnArgumentCaptor.capture()))
                .willReturn(givenJwkSetData);
        BDDMockito.given(rotationRetryTemplate.execute(retryableArgumentCaptor.capture()))
                .willReturn(givenCertificateData);
        // When
        final JwkSetData rotationResult = retryableCertificateRotator.rotate();
        final CertificateData functionResult = jwksRotationFnArgumentCaptor.getValue()
                .apply(_ -> givenCertificateData);
        final CertificateData retryResult = retryableArgumentCaptor.getValue()
                .execute();
        // Then
        BDDAssertions.then(rotationResult).isNotNull();
        BDDAssertions.then(functionResult).isNotNull();
        BDDAssertions.then(retryResult).isNotNull();
        // And
        BDDMockito.then(rotationRetryTemplate).should().execute(retryableArgumentCaptor.capture());
        BDDMockito.then(jwksRotator).should().rotate(jwksRotationFnArgumentCaptor.capture());
        BDDMockito.then(properties).should().certificateRotation();
        BDDMockito.then(properties).should().kv();
    }

    @Test
    void testRotateSuccessWithDebug() throws Throwable {
        // Set up
        LogbackUtils.changeLoggingLevel(Level.DEBUG, RetryableRedisJwksCertificateRotator.class);
        // Given
        final CertificateData givenCertificateData = CertificateData.builder()
                .build();
        final JwkSetData givenJwkSetData = JwkSetData.builder()
                .build();
        BDDMockito.given(jwksRotator.rotate(jwksRotationFnArgumentCaptor.capture()))
                .willReturn(givenJwkSetData);
        BDDMockito.given(rotationRetryTemplate.execute(retryableArgumentCaptor.capture()))
                .willReturn(givenCertificateData);
        // When
        final JwkSetData rotationResult = retryableCertificateRotator.rotate();
        final CertificateData functionResult = jwksRotationFnArgumentCaptor.getValue()
                .apply(_ -> givenCertificateData);
        final CertificateData retryResult = retryableArgumentCaptor.getValue()
                .execute();
        // Then
        BDDAssertions.then(rotationResult).isNotNull();
        BDDAssertions.then(functionResult).isNotNull();
        BDDAssertions.then(retryResult).isNotNull();
        // And
        BDDMockito.then(rotationRetryTemplate).should().execute(retryableArgumentCaptor.capture());
        BDDMockito.then(jwksRotator).should().rotate(jwksRotationFnArgumentCaptor.capture());
        BDDMockito.then(properties).should().certificateRotation();
        BDDMockito.then(properties).should().kv();
    }

    @Test
    void testRotateThrowsCertificateRotationException() throws Throwable {
        // Given
        final CertificateData givenCertificateData = CertificateData.builder()
                .build();
        final JwkSetData givenJwkSetData = JwkSetData.builder()
                .build();
        BDDMockito.given(jwksRotator.rotate(jwksRotationFnArgumentCaptor.capture()))
                .willReturn(givenJwkSetData);
        BDDMockito.given(rotationRetryTemplate.execute(retryableArgumentCaptor.capture()))
                .willThrow(RetryException.class);
        // Then
        BDDAssertions.thenExceptionOfType(CertificateRotationException.class)
                .isThrownBy(() -> {
                    retryableCertificateRotator.rotate();
                    final CertificateData functionResult = jwksRotationFnArgumentCaptor.getValue()
                            .apply(_ -> givenCertificateData);
                    BDDAssertions.then(functionResult).isNotNull();
                })
                .withMessage("On retry a certificate rotation exception")
                .withCauseInstanceOf(RetryException.class);
        // And
        BDDMockito.then(rotationRetryTemplate).should().execute(retryableArgumentCaptor.capture());
        BDDMockito.then(jwksRotator).should().rotate(jwksRotationFnArgumentCaptor.capture());
        BDDMockito.then(properties).should(BDDMockito.never()).certificateRotation();
        BDDMockito.then(properties).should(BDDMockito.never()).kv();
    }

}
