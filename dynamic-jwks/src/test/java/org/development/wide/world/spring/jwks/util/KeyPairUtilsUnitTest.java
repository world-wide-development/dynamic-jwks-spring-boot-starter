package org.development.wide.world.spring.jwks.util;

import core.base.BaseUnitTest;
import org.development.wide.world.spring.jwks.util.KeyPairUtils.KeyFactoryManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateKeySpec;

@ExtendWith({MockitoExtension.class})
class KeyPairUtilsUnitTest extends BaseUnitTest {

    @Test
    void testInstantiationException() throws Exception {
        // Given
        final Constructor<KeyPairUtils> givenConstructor = KeyPairUtils.class.getDeclaredConstructor();
        // Expect
        Assertions.assertThrows(IllegalAccessException.class, givenConstructor::newInstance);
        givenConstructor.setAccessible(Boolean.TRUE);
        Assertions.assertDoesNotThrow(() -> givenConstructor.newInstance());
    }

    @Test
    void testExtractPrivateKeyEcSuccess() {
        // Given
        final KeyPair keyPair = KeyPairUtils.generate256EcKeyPair();
        final KeySpec givenSpec = KeyFactoryManager.EC_KEY_FACTORY.extractKeySpec(keyPair.getPrivate());
        // When
        final PrivateKey result = KeyPairUtils.extractPrivateKey(givenSpec);
        // Then
        Assertions.assertNotNull(result);
    }

    @Test
    void testExtractPrivateKeyRsaSuccess() {
        // Given
        final KeyPair keyPair = KeyPairUtils.generate2048RsaKeyPair();
        final KeySpec givenSpec = KeyFactoryManager.RSA_KEY_FACTORY.extractKeySpec(keyPair.getPrivate());
        // When
        final PrivateKey result = KeyPairUtils.extractPrivateKey(givenSpec);
        // Then
        Assertions.assertNotNull(result);
    }

    @Test
    void testExtractPrivateKeyThrowsCannotExtractPrivateKeyFromKeySpecException() {
        // Given
        final KeySpec givenSpec = BDDMockito.mock(RSAPrivateKeySpec.class);
        // Expect
        final IllegalStateException exception = Assertions
                .assertThrows(IllegalStateException.class, () -> KeyPairUtils.extractPrivateKey(givenSpec));
        Assertions.assertEquals("Cannot extract private key from key spec", exception.getMessage());
    }

    @Test
    void testExtractKeySpecThrowsCannotExtractKeySpecException() {
        // Given
        final Key key = BDDMockito.mock(Key.class);
        // Expect
        final IllegalStateException exception = Assertions
                .assertThrows(IllegalStateException.class, () -> KeyFactoryManager.EC_KEY_FACTORY.extractKeySpec(key));
        Assertions.assertEquals("Cannot extract key spec", exception.getMessage());
    }

    @Test
    void testInstantiateFactoryThrowsNoAlgorithmAvailableException() {
        // Given
        final String givenAlgorithm = "wrong-algorithm";
        // Expect
        final IllegalStateException exception = Assertions
                .assertThrows(IllegalStateException.class, () -> KeyPairUtils.instantiateFactory(givenAlgorithm));
        Assertions.assertEquals("No %s algorithm available".formatted(givenAlgorithm), exception.getMessage());
    }

    @Test
    void testInstantiateGeneratorThrowsNoAlgorithmAvailableException() {
        // Given
        final String givenAlgorithm = "wrong-algorithm";
        // Expect
        final IllegalStateException exception = Assertions
                .assertThrows(IllegalStateException.class, () -> KeyPairUtils.instantiateGenerator(givenAlgorithm));
        Assertions.assertEquals("No %s algorithm available".formatted(givenAlgorithm), exception.getMessage());
    }

}
