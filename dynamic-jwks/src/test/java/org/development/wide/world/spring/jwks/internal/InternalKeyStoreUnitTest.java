package org.development.wide.world.spring.jwks.internal;

import core.base.BaseUnitTest;
import org.development.wide.world.spring.jwks.property.KeyStoreInternalProperties;
import org.development.wide.world.spring.jwks.util.KeyStoreUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

@ExtendWith({MockitoExtension.class})
class InternalKeyStoreUnitTest extends BaseUnitTest {

    @Mock
    PrivateKey privateKey;
    @Mock
    X509Certificate certificate;
    @Spy
    KeyStore keyStore = KeyStoreUtils.getDefaultInstance();
    @Spy
    KeyStoreInternalProperties properties = KeyStoreInternalProperties.builder()
            .password("given-password")
            .alias("given-alias")
            .build();

    @InjectMocks
    InternalKeyStore internalKeyStore;

    String alias = properties.alias();
    char[] password = "given-password".toCharArray();

    @Test
    void testInstantiationWithDefaultKeyStore() {
        // Expect
        Assertions.assertDoesNotThrow(() -> new InternalKeyStore(properties));
    }

    @Test
    void testGetPrivateKeySuccess() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
        // Given
        BDDMockito.given(keyStore.getKey(alias, password)).willReturn(privateKey);
        // When
        final PrivateKey result = internalKeyStore.getPrivateKey();
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(privateKey, result);
        // And
        BDDMockito.then(keyStore).should().getKey(alias, password);
    }

    @Test
    void testGetPrivateKeyThrowsUnableToSaveCertificateToKeyStoreException() throws Exception {
        // Given
        BDDMockito.given(keyStore.getKey(alias, password)).willThrow(KeyStoreException.class);
        // Expect
        final IllegalStateException exception = Assertions
                .assertThrows(IllegalStateException.class, () -> internalKeyStore.getPrivateKey());
        Assertions.assertEquals("Unable to get key from key store", exception.getMessage());
        // And
        BDDMockito.then(keyStore).should().getKey(alias, password);
    }

    @Test
    void testGetX509CertificateSuccess() throws Exception {
        // Given
        BDDMockito.given(keyStore.getCertificate(alias)).willReturn(certificate);
        // When
        final X509Certificate result = internalKeyStore.getX509Certificate();
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(certificate, result);
        // And
        BDDMockito.then(keyStore).should().getCertificate(alias);
    }

    @Test
    void testGetX509CertificateThrowsUnableToGetCertificateFormKeyStoreException() throws Exception {
        // Given
        BDDMockito.given(keyStore.getCertificate(alias)).willThrow(KeyStoreException.class);
        // Expect
        final IllegalStateException exception = Assertions
                .assertThrows(IllegalStateException.class, () -> internalKeyStore.getX509Certificate());
        Assertions.assertEquals("Unable to get certificate from key store", exception.getMessage());
        // And
        BDDMockito.then(keyStore).should().getCertificate(alias);
    }

    @Test
    void testReloadFromByteArraySuccess() throws Exception {
        // Given
        final byte[] givenSources = "Given key store sources".getBytes(StandardCharsets.UTF_8);
        BDDMockito.willDoNothing().given(keyStore).load(BDDMockito.any(), BDDMockito.eq(password));
        // When
        final InternalKeyStore result = internalKeyStore.reloadFromByteArray(givenSources);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(internalKeyStore, result);
        // And
        BDDMockito.then(keyStore).should().load(BDDMockito.any(), BDDMockito.eq(password));
    }

    @Test
    void testReloadFromByteArrayThrowsUnableToLoadKeyStoreFromInputStreamException() throws Exception {
        // Given
        final byte[] givenSources = "Given key store sources".getBytes(StandardCharsets.UTF_8);
        // Expect
        final IllegalStateException exception = Assertions
                .assertThrows(IllegalStateException.class, () -> internalKeyStore.reloadFromByteArray(givenSources));
        Assertions.assertEquals("Unable to load key store from input stream", exception.getMessage());
        // And
        BDDMockito.then(keyStore).should().load(BDDMockito.any(), BDDMockito.eq(password));
    }

    @Test
    void testSerializeToByteArraySuccess() throws Exception {
        // Given
        BDDMockito.willDoNothing().given(keyStore).store(BDDMockito.any(), BDDMockito.eq(password));
        // Expect
        Assertions.assertDoesNotThrow(() -> internalKeyStore.serializeToByteArray());
        // And
        BDDMockito.then(keyStore).should().store(BDDMockito.any(), BDDMockito.eq(password));
    }

    @Test
    void testSerializeToByteArrayThrowsUnableToSerializeKeyStoreToOutputStreamException() throws Exception {
        // Given
        BDDMockito.willThrow(KeyStoreException.class).given(keyStore).store(BDDMockito.any(), BDDMockito.eq(password));
        // Expect
        final IllegalStateException exception = Assertions
                .assertThrows(IllegalStateException.class, () -> internalKeyStore.serializeToByteArray());
        Assertions.assertEquals("Unable to serialize key store to output stream", exception.getMessage());
        // And
        BDDMockito.then(keyStore).should().store(BDDMockito.any(), BDDMockito.eq(password));
    }

    @Test
    void testSaveCertificateSuccess() throws Exception {
        // Given
        final Certificate[] givenChain = {certificate};
        BDDMockito.willDoNothing().given(keyStore).setKeyEntry(alias, privateKey, password, givenChain);
        // When
        final InternalKeyStore result = internalKeyStore.saveCertificate(privateKey, givenChain);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(internalKeyStore, result);
        // And
        BDDMockito.then(keyStore).should().setKeyEntry(alias, privateKey, password, givenChain);
    }

    @Test
    void testSaveCertificateThrowsUnableToSaveCertificateToKeyStoreException() throws Exception {
        // Given
        final Certificate[] givenChain = {certificate};
        BDDMockito.willThrow(KeyStoreException.class)
                .given(keyStore).setKeyEntry(alias, privateKey, password, givenChain);
        // Expect
        final IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> internalKeyStore.saveCertificate(privateKey, givenChain)
        );
        Assertions.assertEquals("Unable to save certificate to key store", exception.getMessage());
        // And
        BDDMockito.then(keyStore).should().setKeyEntry(alias, privateKey, password, givenChain);
    }

}
