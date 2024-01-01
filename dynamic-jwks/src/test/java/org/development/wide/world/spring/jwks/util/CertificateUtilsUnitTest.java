package org.development.wide.world.spring.jwks.util;

import core.base.BaseUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;

@ExtendWith({MockitoExtension.class})
class CertificateUtilsUnitTest extends BaseUnitTest {

    @Test
    void testInstantiationException() throws Exception {
        // Given
        final Constructor<CertificateUtils> givenConstructor = CertificateUtils.class.getDeclaredConstructor();
        // Expect
        Assertions.assertThrows(IllegalAccessException.class, givenConstructor::newInstance);
        givenConstructor.setAccessible(Boolean.TRUE);
        Assertions.assertDoesNotThrow(() -> givenConstructor.newInstance());
    }

}
