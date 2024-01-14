package org.development.wide.world.spring.redis.internal;

import ch.qos.logback.classic.Level;
import core.base.BaseUnitTest;
import core.utils.LogbackUtils;
import org.development.wide.world.spring.jwks.data.JwkSetData;
import org.development.wide.world.spring.jwks.spi.JwkSetDataHolder;
import org.development.wide.world.spring.redis.property.CertificateRotationInternalProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.integration.util.CheckedCallable;

import java.time.Duration;

@ExtendWith({MockitoExtension.class})
@SuppressWarnings({"ResultOfMethodCallIgnored"})
class RedisCertificateRotationTaskUnitTest extends BaseUnitTest {

    @Mock
    LockRegistry lockRegistry;
    @Mock
    JwkSetDataHolder jwkSetDataHolder;
    @Mock
    CertificateRotationInternalProperties properties;
    @Captor
    ArgumentCaptor<CheckedCallable<JwkSetData, RuntimeException>> checkedCallableCaptor;

    @InjectMocks
    RedisCertificateRotationTask rotationTask;

    @BeforeEach
    void setUpEach() {
        LogbackUtils.changeLoggingLevel(Level.INFO, RedisCertificateRotationTask.class);
    }

    @Test
    void testRunSuccess() throws Exception {
        // Set up
        LogbackUtils.changeLoggingLevel(Level.TRACE, RedisCertificateRotationTask.class);
        // Given
        final String givenLockKey = "given-lock-key";
        final Duration givenRotateBefore = Duration.ofMinutes(2);
        final JwkSetData givenJwkSetData = JwkSetData.builder().build();
        BDDMockito.given(properties.rotationLockKey()).willReturn(givenLockKey);
        BDDMockito.given(properties.rotateBefore()).willReturn(givenRotateBefore);
        BDDMockito.willReturn(givenJwkSetData).given(lockRegistry)
                .executeLocked(BDDMockito.eq(givenLockKey), checkedCallableCaptor.capture());
        BDDMockito.given(jwkSetDataHolder.rotateInAdvanceIfAny(givenRotateBefore)).willReturn(givenJwkSetData);
        // Expect
        Assertions.assertDoesNotThrow(() -> rotationTask.run());
        Assertions.assertFalse(Thread.currentThread().isInterrupted());
        Assertions.assertEquals(givenJwkSetData, checkedCallableCaptor.getValue().call());
        // And
        BDDMockito.then(properties).should().rotateBefore();
        BDDMockito.then(properties).should().rotationLockKey();
        BDDMockito.then(lockRegistry).should()
                .executeLocked(BDDMockito.eq(givenLockKey), checkedCallableCaptor.capture());
        BDDMockito.then(jwkSetDataHolder).should().rotateInAdvanceIfAny(givenRotateBefore);
    }

    @Test
    void testRunThrowsInterruptedException() throws Exception {
        // Set up
        LogbackUtils.changeLoggingLevel(Level.TRACE, RedisCertificateRotationTask.class);
        // Given
        final String givenLockKey = "given-lock-key";
        final Duration givenRotateBefore = Duration.ofMinutes(2);
        final JwkSetData givenJwkSetData = JwkSetData.builder().build();
        BDDMockito.given(properties.rotationLockKey()).willReturn(givenLockKey);
        BDDMockito.given(properties.rotateBefore()).willReturn(givenRotateBefore);
        BDDMockito.willThrow(InterruptedException.class).given(lockRegistry)
                .executeLocked(BDDMockito.eq(givenLockKey), checkedCallableCaptor.capture());
        BDDMockito.given(jwkSetDataHolder.rotateInAdvanceIfAny(givenRotateBefore)).willReturn(givenJwkSetData);
        // Expect
        Assertions.assertDoesNotThrow(() -> rotationTask.run());
        Assertions.assertTrue(Thread.currentThread().isInterrupted());
        Assertions.assertEquals(givenJwkSetData, checkedCallableCaptor.getValue().call());
        // And
        BDDMockito.then(properties).should().rotateBefore();
        BDDMockito.then(properties).should().rotationLockKey();
        BDDMockito.then(lockRegistry).should()
                .executeLocked(BDDMockito.eq(givenLockKey), checkedCallableCaptor.capture());
        BDDMockito.then(jwkSetDataHolder).should().rotateInAdvanceIfAny(givenRotateBefore);
    }

}
