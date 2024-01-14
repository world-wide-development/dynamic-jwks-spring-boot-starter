package org.development.wide.world.spring.vault.jwks.internal;

import ch.qos.logback.classic.Level;
import core.base.BaseUnitTest;
import core.utils.LogbackUtils;
import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.data.KeyStoreSource;
import org.development.wide.world.spring.jwks.internal.InternalKeyStore;
import org.development.wide.world.spring.jwks.template.KeyStoreTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.vault.core.VaultVersionedKeyValueOperations;
import org.springframework.vault.support.Versioned;

import java.nio.charset.StandardCharsets;

@ExtendWith({MockitoExtension.class})
class VaultCertificateRepositoryUnitTest extends BaseUnitTest {

    @Mock
    Versioned.Metadata metadata;
    @Mock
    InternalKeyStore internalKeyStore;
    @Mock
    KeyStoreTemplate keyStoreTemplate;
    @Mock
    VaultVersionedKeyValueOperations keyValueOperations;

    @InjectMocks
    VaultCertificateRepository repository;

    @BeforeEach
    void setUpEach() {
        LogbackUtils.changeLoggingLevel(Level.INFO, VaultCertificateRepository.class);
    }

    @Test
    void testFindOneSuccess() {
        // Given
        final int givenVersion = 3;
        final String givenKey = "given-key";
        final String givenSerialNumber = "given-serial-number";
        final Class<KeyStoreSource> keyStoreSourceClass = KeyStoreSource.class;
        final KeyStoreSource givenKeyStoreSource = KeyStoreSource.builder()
                .keyStoreSources("given-key-store-sources".getBytes(StandardCharsets.UTF_8))
                .serialNumber(givenSerialNumber)
                .build();
        BDDMockito.given(keyValueOperations.get(givenKey, keyStoreSourceClass))
                .willReturn(Versioned.create(givenKeyStoreSource, Versioned.Version.from(givenVersion)));
        BDDMockito.given(keyStoreTemplate.reloadFromSource(givenKeyStoreSource)).willReturn(internalKeyStore);
        // When
        final CertificateData result = repository.findOne(givenKey).orElse(null);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.version());
        Assertions.assertEquals(givenVersion, result.version());
        Assertions.assertEquals(givenSerialNumber, result.serialNumber());
        // And
        BDDMockito.then(keyValueOperations).should().get(givenKey, keyStoreSourceClass);
        BDDMockito.then(keyStoreTemplate).should().reloadFromSource(givenKeyStoreSource);
    }

    @Test
    void testFindOneOptionalEmpty() {
        // Given
        final int givenVersion = 3;
        final String givenKey = "given-key";
        final Class<KeyStoreSource> keyStoreSourceClass = KeyStoreSource.class;
        BDDMockito.given(keyValueOperations.get(givenKey, keyStoreSourceClass))
                .willReturn(Versioned.create(null, Versioned.Version.from(givenVersion)));
        // When
        final CertificateData result = repository.findOne(givenKey).orElse(null);
        // Then
        Assertions.assertNull(result);
        // And
        BDDMockito.then(keyValueOperations).should().get(givenKey, keyStoreSourceClass);
        BDDMockito.then(keyStoreTemplate).should(BDDMockito.never()).reloadFromSource(BDDMockito.any());
    }

    @Test
    void testSaveOneSuccess() {
        // Given
        final int givenVersion = 3;
        final String givenKey = "given-key";
        final String givenSerialNumber = "given-serial-number";
        final CertificateData givenCertificateData = CertificateData.builder()
                .serialNumber(givenSerialNumber)
                .version(givenVersion)
                .build();
        final Class<KeyStoreSource> keyStoreSourceClass = KeyStoreSource.class;
        final KeyStoreSource givenKeyStoreSource = KeyStoreSource.builder()
                .keyStoreSources("given-key-store-sources".getBytes(StandardCharsets.UTF_8))
                .serialNumber(givenSerialNumber)
                .build();
        BDDMockito.given(keyStoreTemplate.saveCertificate(givenCertificateData)).willReturn(givenKeyStoreSource);
        BDDMockito.given(keyValueOperations.put(BDDMockito.eq(givenKey), BDDMockito.any())).willReturn(metadata);
        BDDMockito.given(keyValueOperations.get(givenKey, keyStoreSourceClass))
                .willReturn(Versioned.create(givenKeyStoreSource, Versioned.Version.from(givenVersion)));
        BDDMockito.given(keyStoreTemplate.reloadFromSource(givenKeyStoreSource)).willReturn(internalKeyStore);
        // When
        final CertificateData result = repository.saveOne(givenKey, givenCertificateData);
        // Then
        Assertions.assertNotNull(result);
        // And
        BDDMockito.then(keyStoreTemplate).should().saveCertificate(givenCertificateData);
        BDDMockito.then(keyValueOperations).should().put(BDDMockito.eq(givenKey), BDDMockito.any());
        BDDMockito.then(metadata).should(BDDMockito.never()).isDeleted();
        BDDMockito.then(metadata).should(BDDMockito.never()).isDeleted();
        BDDMockito.then(keyValueOperations).should().get(givenKey, keyStoreSourceClass);
        BDDMockito.then(keyStoreTemplate).should().reloadFromSource(givenKeyStoreSource);
    }

    @Test
    void testSaveOneThrowsCertificateDataCannotBeNullException() {
        // Given
        final int givenVersion = 3;
        final String givenKey = "given-key";
        final String givenSerialNumber = "given-serial-number";
        final CertificateData givenCertificateData = CertificateData.builder()
                .serialNumber(givenSerialNumber)
                .version(givenVersion)
                .build();
        final Class<KeyStoreSource> keyStoreSourceClass = KeyStoreSource.class;
        final KeyStoreSource givenKeyStoreSource = KeyStoreSource.builder()
                .keyStoreSources("given-key-store-sources".getBytes(StandardCharsets.UTF_8))
                .serialNumber(givenSerialNumber)
                .build();
        BDDMockito.given(keyStoreTemplate.saveCertificate(givenCertificateData)).willReturn(givenKeyStoreSource);
        BDDMockito.given(keyValueOperations.put(BDDMockito.eq(givenKey), BDDMockito.any())).willReturn(metadata);
        BDDMockito.given(keyValueOperations.get(givenKey, keyStoreSourceClass))
                .willReturn(Versioned.create(null, Versioned.Version.from(givenVersion)));
        // Expect
        final IllegalStateException exception = Assertions
                .assertThrows(IllegalStateException.class, () -> repository.saveOne(givenKey, givenCertificateData));
        Assertions.assertEquals("Certificate data cannot be null", exception.getMessage());
        // And
        BDDMockito.then(keyStoreTemplate).should().saveCertificate(givenCertificateData);
        BDDMockito.then(keyValueOperations).should().put(BDDMockito.eq(givenKey), BDDMockito.any());
        BDDMockito.then(metadata).should(BDDMockito.never()).isDeleted();
        BDDMockito.then(metadata).should(BDDMockito.never()).isDeleted();
        BDDMockito.then(keyValueOperations).should().get(givenKey, keyStoreSourceClass);
    }

    @Test
    void testSaveOneSuccessWithDebug() {
        // Set up
        LogbackUtils.changeLoggingLevel(Level.DEBUG, VaultCertificateRepository.class);
        // Given
        final int givenVersion = 3;
        final String givenKey = "given-key";
        final String givenSerialNumber = "given-serial-number";
        final CertificateData givenCertificateData = CertificateData.builder()
                .serialNumber(givenSerialNumber)
                .version(givenVersion)
                .build();
        final Class<KeyStoreSource> keyStoreSourceClass = KeyStoreSource.class;
        final KeyStoreSource givenKeyStoreSource = KeyStoreSource.builder()
                .keyStoreSources("given-key-store-sources".getBytes(StandardCharsets.UTF_8))
                .serialNumber(givenSerialNumber)
                .build();
        BDDMockito.given(keyStoreTemplate.saveCertificate(givenCertificateData)).willReturn(givenKeyStoreSource);
        BDDMockito.given(keyValueOperations.put(BDDMockito.eq(givenKey), BDDMockito.any())).willReturn(metadata);
        BDDMockito.given(keyValueOperations.get(givenKey, keyStoreSourceClass))
                .willReturn(Versioned.create(givenKeyStoreSource, Versioned.Version.from(givenVersion)));
        BDDMockito.given(keyStoreTemplate.reloadFromSource(givenKeyStoreSource)).willReturn(internalKeyStore);
        // When
        final CertificateData result = repository.saveOne(givenKey, givenCertificateData);
        // Then
        Assertions.assertNotNull(result);
        // And
        BDDMockito.then(keyStoreTemplate).should().saveCertificate(givenCertificateData);
        BDDMockito.then(keyValueOperations).should().put(BDDMockito.eq(givenKey), BDDMockito.any());
        BDDMockito.then(metadata).should().isDeleted();
        BDDMockito.then(metadata).should().isDeleted();
        BDDMockito.then(keyValueOperations).should().get(givenKey, keyStoreSourceClass);
        BDDMockito.then(keyStoreTemplate).should().reloadFromSource(givenKeyStoreSource);
    }

    @Test
    void testSaveOneSuccessWithDebugDeletedAndDestroyed() {
        // Set up
        LogbackUtils.changeLoggingLevel(Level.DEBUG, VaultCertificateRepository.class);
        // Given
        final int givenVersion = 3;
        final String givenKey = "given-key";
        final String givenSerialNumber = "given-serial-number";
        final CertificateData givenCertificateData = CertificateData.builder()
                .serialNumber(givenSerialNumber)
                .version(givenVersion)
                .build();
        final Class<KeyStoreSource> keyStoreSourceClass = KeyStoreSource.class;
        final KeyStoreSource givenKeyStoreSource = KeyStoreSource.builder()
                .keyStoreSources("given-key-store-sources".getBytes(StandardCharsets.UTF_8))
                .serialNumber(givenSerialNumber)
                .build();
        BDDMockito.given(keyStoreTemplate.saveCertificate(givenCertificateData)).willReturn(givenKeyStoreSource);
        BDDMockito.given(keyValueOperations.put(BDDMockito.eq(givenKey), BDDMockito.any())).willReturn(metadata);
        BDDMockito.given(metadata.isDeleted()).willReturn(Boolean.TRUE);
        BDDMockito.given(metadata.isDestroyed()).willReturn(Boolean.TRUE);
        BDDMockito.given(keyValueOperations.get(givenKey, keyStoreSourceClass))
                .willReturn(Versioned.create(givenKeyStoreSource, Versioned.Version.from(givenVersion)));
        BDDMockito.given(keyStoreTemplate.reloadFromSource(givenKeyStoreSource)).willReturn(internalKeyStore);
        // When
        final CertificateData result = repository.saveOne(givenKey, givenCertificateData);
        // Then
        Assertions.assertNotNull(result);
        // And
        BDDMockito.then(keyStoreTemplate).should().saveCertificate(givenCertificateData);
        BDDMockito.then(keyValueOperations).should().put(BDDMockito.eq(givenKey), BDDMockito.any());
        BDDMockito.then(metadata).should().isDeleted();
        BDDMockito.then(metadata).should().isDeleted();
        BDDMockito.then(keyValueOperations).should().get(givenKey, keyStoreSourceClass);
        BDDMockito.then(keyStoreTemplate).should().reloadFromSource(givenKeyStoreSource);
    }

}
