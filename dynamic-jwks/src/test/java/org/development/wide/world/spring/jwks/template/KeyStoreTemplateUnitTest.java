package org.development.wide.world.spring.jwks.template;

import core.base.BaseUnitTest;
import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.data.KeyStoreSource;
import org.development.wide.world.spring.jwks.internal.InternalKeyStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

@ExtendWith({MockitoExtension.class})
class KeyStoreTemplateUnitTest extends BaseUnitTest {

    @Mock
    PrivateKey privateKey;
    @Mock
    X509Certificate x509Certificate;
    @Mock
    InternalKeyStore internalKeyStore;

    @InjectMocks
    KeyStoreTemplate keyStoreTemplate;

    @Test
    void testReloadFromSource() {
        // Given
        final byte[] givenKeyStoreSources = "Key Store Sources".getBytes(StandardCharsets.UTF_8);
        final KeyStoreSource givenKeyStoreSource = KeyStoreSource.builder()
                .keyStoreSources(givenKeyStoreSources)
                .serialNumber("given-serial-number")
                .build();
        BDDMockito.given(internalKeyStore.reloadFromByteArray(givenKeyStoreSources)).willReturn(internalKeyStore);
        // When
        final InternalKeyStore result = keyStoreTemplate.reloadFromSource(givenKeyStoreSource);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(internalKeyStore, result);
        // And
        BDDMockito.then(internalKeyStore).should().reloadFromByteArray(givenKeyStoreSources);
    }

    @Test
    void testSaveCertificateWithChainSuccess() {
        // Given
        final int givenVersion = 5;
        final String givenSerialNumber = "given-serial-number";
        final CertificateData givenCertificateData = CertificateData.builder()
                .x509Certificates(List.of(x509Certificate, x509Certificate))
                .x509Certificate(x509Certificate)
                .serialNumber(givenSerialNumber)
                .privateKey(privateKey)
                .version(givenVersion)
                .build();
        final byte[] givenKeyStoreSources = "Key Store Sources".getBytes(StandardCharsets.UTF_8);
        BDDMockito.given(internalKeyStore.saveCertificate(BDDMockito.eq(privateKey), BDDMockito.any()))
                .willReturn(internalKeyStore);
        BDDMockito.given(internalKeyStore.serializeToByteArray()).willReturn(givenKeyStoreSources);
        // When
        final KeyStoreSource result = keyStoreTemplate.saveCertificate(givenCertificateData);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(givenSerialNumber, result.serialNumber());
        Assertions.assertEquals(givenKeyStoreSources, result.keyStoreSources());
        // And
        BDDMockito.then(internalKeyStore).should().saveCertificate(BDDMockito.eq(privateKey), BDDMockito.any());
        BDDMockito.then(internalKeyStore).should().serializeToByteArray();
    }

    @Test
    void testSaveCertificateWithoutChainSuccess() {
        // Given
        final int givenVersion = 5;
        final String givenSerialNumber = "given-serial-number";
        final CertificateData givenCertificateData = CertificateData.builder()
                .x509Certificates(List.of(x509Certificate, x509Certificate))
                .x509Certificate(x509Certificate)
                .serialNumber(givenSerialNumber)
                .privateKey(privateKey)
                .version(givenVersion)
                .build();
        final byte[] givenKeyStoreSources = "Key Store Sources".getBytes(StandardCharsets.UTF_8);
        BDDMockito.given(internalKeyStore.saveCertificate(BDDMockito.eq(privateKey), BDDMockito.any()))
                .willReturn(internalKeyStore);
        BDDMockito.given(internalKeyStore.serializeToByteArray()).willReturn(givenKeyStoreSources);
        // When
        final KeyStoreSource result = keyStoreTemplate.saveCertificate(Boolean.FALSE, givenCertificateData);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(givenSerialNumber, result.serialNumber());
        Assertions.assertEquals(givenKeyStoreSources, result.keyStoreSources());
        // And
        BDDMockito.then(internalKeyStore).should().saveCertificate(BDDMockito.eq(privateKey), BDDMockito.any());
        BDDMockito.then(internalKeyStore).should().serializeToByteArray();
    }

}
