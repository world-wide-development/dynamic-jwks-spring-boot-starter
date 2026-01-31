package org.development.wide.world.spring.vault.jwks.internal;

import ch.qos.logback.classic.Level;
import core.base.BaseUnitTest;
import core.utils.LogbackUtils;
import org.assertj.core.api.BDDAssertions;
import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.data.JwkSetData;
import org.development.wide.world.spring.jwks.spi.JwkSetRotationFunction;
import org.development.wide.world.spring.jwks.spi.JwksCertificateRotator;
import org.development.wide.world.spring.vault.jwks.exception.CertificateRotationException;
import org.development.wide.world.spring.vault.jwks.property.DynamicVaultJwksInternalProperties;
import org.development.wide.world.spring.vault.jwks.property.VaultPkiInternalProperties;
import org.development.wide.world.spring.vault.jwks.property.VaultVersionedKvInternalProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.retry.RetryException;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.core.retry.Retryable;

import java.time.Duration;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"ResultOfMethodCallIgnored"})
class RetryableVaultJwksCertificateRotatorUnitTest extends BaseUnitTest {

    final VaultVersionedKvInternalProperties kvInternalProperties = VaultVersionedKvInternalProperties.builder()
            .certificatePath("authorization.certificate")
            .rootPath("secret")
            .build();
    final VaultPkiInternalProperties pkiInternalProperties = VaultPkiInternalProperties.builder()
            .certificateCommonName("authorization.certificate")
            .certificateTtl(Duration.ofMinutes(1))
            .roleName("jwks")
            .rootPath("pki")
            .build();
    @Spy
    final DynamicVaultJwksInternalProperties properties = DynamicVaultJwksInternalProperties.builder()
            .versionedKv(kvInternalProperties)
            .certificateRotationRetries(3)
            .pki(pkiInternalProperties)
            .enabled(Boolean.TRUE)
            .build();

    @Mock
    JwksCertificateRotator jwksRotator;
    @Mock
    RetryTemplate rotationRetryTemplate;

    @InjectMocks
    RetryableVaultJwksCertificateRotator retryableCertificateRotator;

    @Captor
    ArgumentCaptor<Retryable<CertificateData>> retryableArgumentCaptor;
    @Captor
    ArgumentCaptor<JwkSetRotationFunction> jwksRotationFnArgumentCaptor;

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
        BDDMockito.then(properties).should().versionedKv();
    }

    @Test
    void testRotateSuccessWithDebug() throws Throwable {
        // Set up
        LogbackUtils.changeLoggingLevel(Level.DEBUG, RetryableVaultJwksCertificateRotator.class);
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
        BDDMockito.then(properties).should().versionedKv();
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
        BDDMockito.then(properties).should(BDDMockito.never()).versionedKv();
    }
}
