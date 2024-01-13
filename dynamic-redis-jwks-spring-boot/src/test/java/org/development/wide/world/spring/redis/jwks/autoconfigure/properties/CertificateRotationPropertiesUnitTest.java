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
class CertificateRotationPropertiesUnitTest extends BaseUnitTest {

    @Mock
    RotationRetryProperties rotationRetryProperties;
    @Mock
    RotationScheduleProperties rotationScheduleProperties;

    @Test
    void testInstantiationSuccess() {
        // Given
        final Duration givenRotateBefore = Duration.ofSeconds(70);
        BDDMockito.given(rotationScheduleProperties.interval()).willReturn(Duration.ofMinutes(1));
        // Expect
        Assertions.assertDoesNotThrow(() -> new CertificateRotationProperties(
                givenRotateBefore,
                rotationRetryProperties,
                rotationScheduleProperties,
                "given-rotation-lock-key"
        ));
        // And
        BDDMockito.then(rotationScheduleProperties).should().interval();
    }

    @Test
    void testInstantiationThrowsRotateBeforeMustBeGreaterThenScheduleIntervalException() {
        // Given
        final Duration givenRotateBefore = Duration.ofSeconds(69);
        BDDMockito.given(rotationScheduleProperties.interval()).willReturn(Duration.ofMinutes(1));
        // Expect
        final IllegalStateException exception = Assertions
                .assertThrows(IllegalStateException.class, () -> new CertificateRotationProperties(
                        givenRotateBefore,
                        rotationRetryProperties,
                        rotationScheduleProperties,
                        "given-rotation-lock-key"
                ));
        // Then
        Assertions.assertEquals("rotateBefore must be greater then schedule interval", exception.getMessage());
        // And
        BDDMockito.then(rotationScheduleProperties).should().interval();
    }

}
