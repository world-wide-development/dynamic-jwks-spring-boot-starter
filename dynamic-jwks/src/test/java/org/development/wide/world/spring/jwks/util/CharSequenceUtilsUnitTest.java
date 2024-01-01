package org.development.wide.world.spring.jwks.util;

import core.base.BaseUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;

@ExtendWith({MockitoExtension.class})
class CharSequenceUtilsUnitTest extends BaseUnitTest {

    @Test
    void testInstantiationException() throws Exception {
        // Given
        final Constructor<CharSequenceUtils> givenConstructor = CharSequenceUtils.class.getDeclaredConstructor();
        // Expect
        Assertions.assertThrows(IllegalAccessException.class, givenConstructor::newInstance);
        givenConstructor.setAccessible(Boolean.TRUE);
        Assertions.assertDoesNotThrow(() -> givenConstructor.newInstance());
    }

    @Test
    void testToCharArrayEmptyCharSequenceProduceEmptyCharArray() {
        // Given
        final char[] expectedResult = {};
        final CharSequence givenCharSequence = "";
        // When
        final char[] result = CharSequenceUtils.toCharArray(givenCharSequence);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertArrayEquals(expectedResult, result);
    }

    @Test
    void testToCharArrayNullCharSequenceProduceEmptyCharArray() {
        // Given
        final char[] expectedResult = {};
        // When
        final char[] result = CharSequenceUtils.toCharArray(null);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertArrayEquals(expectedResult, result);
    }

}
