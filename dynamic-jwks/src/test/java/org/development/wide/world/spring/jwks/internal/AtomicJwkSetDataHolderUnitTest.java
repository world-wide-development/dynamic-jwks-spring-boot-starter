package org.development.wide.world.spring.jwks.internal;

import ch.qos.logback.classic.Level;
import com.nimbusds.jose.jwk.JWKSet;
import core.base.BaseUnitTest;
import core.utils.LogbackUtils;
import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.data.JwkSetData;
import org.development.wide.world.spring.jwks.spi.RetryableJwksCertificateRotator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

@ExtendWith({MockitoExtension.class})
class AtomicJwkSetDataHolderUnitTest extends BaseUnitTest {

    @Mock
    JWKSet jwkSet;
    @Mock
    CertificateData certificateData;
    @Mock
    RetryableJwksCertificateRotator certificateRotator;
    @Mock
    AtomicReference<JwkSetData> jwkSetHolderAtomicReference;
    @Captor
    ArgumentCaptor<UnaryOperator<JwkSetData>> jwkSetDataOperatorCaptor;

    @InjectMocks
    AtomicJwkSetDataHolder jwkSetDataHolder;

    @BeforeEach
    void setUpEach() {
        LogbackUtils.changeLoggingLevel(Level.INFO, AtomicJwkSetDataHolder.class);
    }

    @Test
    void testOneArgumentConstructor() {
        // Expect
        Assertions.assertDoesNotThrow(() -> new AtomicJwkSetDataHolder(certificateRotator));
    }

    @Test
    void testGetActualValidCertificate() {
        // Given
        final Duration givenRotateBefore = Duration.ZERO;
        final JwkSetData givenJwkSetData = JwkSetData.builder()
                .certificateData(certificateData)
                .jwkSet(jwkSet)
                .build();
        BDDMockito.given(jwkSetHolderAtomicReference.updateAndGet(jwkSetDataOperatorCaptor.capture()))
                .willReturn(givenJwkSetData);
        BDDMockito.given(certificateData.checkCertificateValidity(givenRotateBefore)).willReturn(Boolean.TRUE);
        // When
        final JwkSetData result = jwkSetDataHolder.getActual();
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(givenJwkSetData, result);
        Assertions.assertEquals(givenJwkSetData, jwkSetDataOperatorCaptor.getValue().apply(givenJwkSetData));
        // And
        BDDMockito.then(jwkSetHolderAtomicReference).should().updateAndGet(jwkSetDataOperatorCaptor.capture());
        BDDMockito.then(certificateData).should().checkCertificateValidity(givenRotateBefore);
    }

    @Test
    void testGetActualValidCertificateWithTrace() {
        // Set up
        LogbackUtils.changeLoggingLevel(Level.TRACE, AtomicJwkSetDataHolder.class);
        // Given
        final Duration givenRotateBefore = Duration.ZERO;
        final JwkSetData givenJwkSetData = JwkSetData.builder()
                .certificateData(certificateData)
                .jwkSet(jwkSet)
                .build();
        BDDMockito.given(jwkSetHolderAtomicReference.updateAndGet(jwkSetDataOperatorCaptor.capture()))
                .willReturn(givenJwkSetData);
        BDDMockito.given(certificateData.checkCertificateValidity(givenRotateBefore)).willReturn(Boolean.TRUE);
        // When
        final JwkSetData result = jwkSetDataHolder.getActual();
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(givenJwkSetData, result);
        Assertions.assertEquals(givenJwkSetData, jwkSetDataOperatorCaptor.getValue().apply(givenJwkSetData));
        // And
        BDDMockito.then(jwkSetHolderAtomicReference).should().updateAndGet(jwkSetDataOperatorCaptor.capture());
        BDDMockito.then(certificateData).should().checkCertificateValidity(givenRotateBefore);
    }

    @Test
    void testGetActualInvalidCertificate() {
        // Set up
        LogbackUtils.changeLoggingLevel(Level.TRACE, AtomicJwkSetDataHolder.class);
        // Given
        final Duration givenRotateBefore = Duration.ZERO;
        final JwkSetData givenJwkSetData = JwkSetData.builder()
                .certificateData(certificateData)
                .jwkSet(jwkSet)
                .build();
        BDDMockito.given(jwkSetHolderAtomicReference.updateAndGet(jwkSetDataOperatorCaptor.capture()))
                .willReturn(givenJwkSetData);
        BDDMockito.given(certificateData.checkCertificateValidity(givenRotateBefore)).willReturn(Boolean.FALSE);
        BDDMockito.given(certificateRotator.rotate()).willReturn(givenJwkSetData);
        // When
        final JwkSetData result = jwkSetDataHolder.getActual();
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(givenJwkSetData, result);
        Assertions.assertEquals(givenJwkSetData, jwkSetDataOperatorCaptor.getValue().apply(givenJwkSetData));
        // And
        BDDMockito.then(jwkSetHolderAtomicReference).should().updateAndGet(jwkSetDataOperatorCaptor.capture());
        BDDMockito.then(certificateData).should().checkCertificateValidity(givenRotateBefore);
        BDDMockito.then(certificateRotator).should().rotate();
    }

    @Test
    void testGetActualNullJwkSetData() {
        // Set up
        LogbackUtils.changeLoggingLevel(Level.TRACE, AtomicJwkSetDataHolder.class);
        // Given
        final JwkSetData givenJwkSetData = JwkSetData.builder()
                .certificateData(certificateData)
                .jwkSet(jwkSet)
                .build();
        BDDMockito.given(jwkSetHolderAtomicReference.updateAndGet(jwkSetDataOperatorCaptor.capture()))
                .willReturn(givenJwkSetData);
        BDDMockito.given(certificateRotator.rotate()).willReturn(givenJwkSetData);
        // When
        final JwkSetData result = jwkSetDataHolder.getActual();
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(givenJwkSetData, result);
        Assertions.assertEquals(givenJwkSetData, jwkSetDataOperatorCaptor.getValue().apply(null));
        // And
        BDDMockito.then(jwkSetHolderAtomicReference).should().updateAndGet(jwkSetDataOperatorCaptor.capture());
        BDDMockito.then(certificateRotator).should().rotate();
    }

    @Test
    void testRotateInAdvanceIfAnyValidCertificate() {
        // Given
        final Duration givenRotateBefore = Duration.ofDays(3);
        final JwkSetData givenJwkSetData = JwkSetData.builder()
                .certificateData(certificateData)
                .jwkSet(jwkSet)
                .build();
        BDDMockito.given(jwkSetHolderAtomicReference.updateAndGet(jwkSetDataOperatorCaptor.capture()))
                .willReturn(givenJwkSetData);
        BDDMockito.given(certificateData.checkCertificateValidity(givenRotateBefore)).willReturn(Boolean.TRUE);
        // When
        final JwkSetData result = jwkSetDataHolder.rotateInAdvanceIfAny(givenRotateBefore);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(givenJwkSetData, result);
        Assertions.assertEquals(givenJwkSetData, jwkSetDataOperatorCaptor.getValue().apply(givenJwkSetData));
        // And
        BDDMockito.then(jwkSetHolderAtomicReference).should().updateAndGet(jwkSetDataOperatorCaptor.capture());
        BDDMockito.then(certificateData).should().checkCertificateValidity(givenRotateBefore);
    }

    @Test
    void testRotateInAdvanceIfAnyInvalidCertificate() {
        // Given
        final Duration givenRotateBefore = Duration.ofDays(3);
        final JwkSetData givenJwkSetData = JwkSetData.builder()
                .certificateData(certificateData)
                .jwkSet(jwkSet)
                .build();
        BDDMockito.given(jwkSetHolderAtomicReference.updateAndGet(jwkSetDataOperatorCaptor.capture()))
                .willReturn(givenJwkSetData);
        BDDMockito.given(certificateData.checkCertificateValidity(givenRotateBefore)).willReturn(Boolean.FALSE);
        BDDMockito.given(certificateRotator.rotate()).willReturn(givenJwkSetData);
        // When
        final JwkSetData result = jwkSetDataHolder.rotateInAdvanceIfAny(givenRotateBefore);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(givenJwkSetData, result);
        Assertions.assertEquals(givenJwkSetData, jwkSetDataOperatorCaptor.getValue().apply(givenJwkSetData));
        // And
        BDDMockito.then(jwkSetHolderAtomicReference).should().updateAndGet(jwkSetDataOperatorCaptor.capture());
        BDDMockito.then(certificateData).should().checkCertificateValidity(givenRotateBefore);
        BDDMockito.then(certificateRotator).should().rotate();
    }

    @Test
    void testRotateInAdvanceIfAnyInvalidCertificateWithTrace() {
        // Set up
        LogbackUtils.changeLoggingLevel(Level.TRACE, AtomicJwkSetDataHolder.class);
        // Given
        final Duration givenRotateBefore = Duration.ofDays(3);
        final JwkSetData givenJwkSetData = JwkSetData.builder()
                .certificateData(certificateData)
                .jwkSet(jwkSet)
                .build();
        BDDMockito.given(jwkSetHolderAtomicReference.updateAndGet(jwkSetDataOperatorCaptor.capture()))
                .willReturn(givenJwkSetData);
        BDDMockito.given(certificateData.checkCertificateValidity(givenRotateBefore)).willReturn(Boolean.FALSE);
        BDDMockito.given(certificateRotator.rotate()).willReturn(givenJwkSetData);
        // When
        final JwkSetData result = jwkSetDataHolder.rotateInAdvanceIfAny(givenRotateBefore);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(givenJwkSetData, result);
        Assertions.assertEquals(givenJwkSetData, jwkSetDataOperatorCaptor.getValue().apply(givenJwkSetData));
        // And
        BDDMockito.then(jwkSetHolderAtomicReference).should().updateAndGet(jwkSetDataOperatorCaptor.capture());
        BDDMockito.then(certificateData).should().checkCertificateValidity(givenRotateBefore);
        BDDMockito.then(certificateRotator).should().rotate();
    }

}
