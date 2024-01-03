package org.development.wide.world.spring.jwks.util;

import core.base.BaseUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;

@ExtendWith({MockitoExtension.class})
class KeyStoreUtilsUnitTest extends BaseUnitTest {

    @Mock
    KeyStore keyStore;
    @Mock
    PrivateKey privateKey;

    @Test
    void testInstantiationException() throws Exception {
        // Given
        final Constructor<KeyStoreUtils> givenConstructor = KeyStoreUtils.class.getDeclaredConstructor();
        // Expect
        Assertions.assertThrows(IllegalAccessException.class, givenConstructor::newInstance);
        givenConstructor.setAccessible(Boolean.TRUE);
        Assertions.assertDoesNotThrow(() -> givenConstructor.newInstance());
    }

    @Test
    void testGetPrivateKeySuccess() throws Exception {
        // Given
        final String givenAlias = "given-alias";
        final char[] givenPassword = CharSequenceUtils.toCharArray("given-password");
        BDDMockito.given(keyStore.getKey(givenAlias, givenPassword)).willReturn(privateKey);
        // When
        final PrivateKey result = KeyStoreUtils.getPrivateKey(givenAlias, givenPassword, keyStore);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(privateKey, result);
        // And
        BDDMockito.then(keyStore).should().getKey(givenAlias, givenPassword);
    }

    @Test
    void testGetPrivateKeyWithCharSequencePasswordSuccess() throws Exception {
        // Given
        final String givenAlias = "given-alias";
        final String givenPassword = "given-password";
        final char[] givenPasswordArray = CharSequenceUtils.toCharArray(givenPassword);
        BDDMockito.given(keyStore.getKey(givenAlias, givenPasswordArray)).willReturn(privateKey);
        // When
        final PrivateKey result = KeyStoreUtils.getPrivateKey(givenAlias, givenPassword, keyStore);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(privateKey, result);
        // And
        BDDMockito.then(keyStore).should().getKey(givenAlias, givenPasswordArray);
    }

    @Test
    void testGetPrivateKeyThrowsUnableToGetPrivateKeyFormKeyStoreException() throws Exception {
        // Given
        final String givenAlias = "given-alias";
        final char[] givenPassword = CharSequenceUtils.toCharArray("given-password");
        BDDMockito.given(keyStore.getKey(givenAlias, givenPassword)).willThrow(KeyStoreException.class);
        // Expect
        final IllegalStateException exception = Assertions
                .assertThrows(IllegalStateException.class, () -> KeyStoreUtils.getPrivateKey(givenAlias, givenPassword, keyStore));
        Assertions.assertEquals("Unable to get private key from key store", exception.getMessage());
        // And
        BDDMockito.then(keyStore).should().getKey(givenAlias, givenPassword);
    }

    @Test
    void testGetInstanceThrowsUnableInstantiateKeyStoreWithDefaultTypeException() {
        // Given
        final String givenType = "wrong-type";
        // Expect
        final IllegalStateException exception = Assertions
                .assertThrows(IllegalStateException.class, () -> KeyStoreUtils.getInstance(givenType));
        Assertions.assertEquals("Unable instantiate key store with default type", exception.getMessage());
    }

    @Test
    void textInitializeThrowsUnableInitiallyLoadKeyStoreException() throws Exception {
        // Given
        final char[] givenPassword = CharSequenceUtils.toCharArray("given-password");
        BDDMockito.willThrow(IOException.class).given(keyStore).load(BDDMockito.any());
        // Expect
        final IllegalStateException exception = Assertions
                .assertThrows(IllegalStateException.class, () -> KeyStoreUtils.initialize(keyStore, givenPassword));
        Assertions.assertEquals("Unable initially load key store", exception.getMessage());
        // And
        BDDMockito.then(keyStore).should().load(BDDMockito.any());
    }

}
