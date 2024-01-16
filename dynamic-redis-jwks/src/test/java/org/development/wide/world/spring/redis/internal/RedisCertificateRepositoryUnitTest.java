package org.development.wide.world.spring.redis.internal;

import core.base.BaseUnitTest;
import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.data.KeyStoreSource;
import org.development.wide.world.spring.jwks.internal.InternalKeyStore;
import org.development.wide.world.spring.jwks.template.KeyStoreTemplate;
import org.development.wide.world.spring.redis.data.VersionedKeyStoreSource;
import org.development.wide.world.spring.redis.exception.RedisOperationException;
import org.development.wide.world.spring.redis.template.KeyStoreRedisTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ValueOperations;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

@ExtendWith({MockitoExtension.class})
class RedisCertificateRepositoryUnitTest extends BaseUnitTest {

    @Mock
    PrivateKey privateKey;
    @Mock
    X509Certificate x509Certificate;
    @Mock
    KeyStoreTemplate keyStoreTemplate;
    @Mock
    InternalKeyStore internalKeyStore;
    @Mock
    KeyStoreRedisTemplate redisTemplate;
    @Mock
    ValueOperations<String, VersionedKeyStoreSource> valueOperations;

    @InjectMocks
    RedisCertificateRepository repository;

    @Test
    void testSaveOneSuccess() {
        // Given
        final int givenVersion = 3;
        final String givenKey = "given-key";
        final String givenSerialNumber = "given-serial-number";
        final CertificateData givenCertificateData = CertificateData.builder()
                .x509Certificates(List.of(x509Certificate))
                .x509Certificate(x509Certificate)
                .serialNumber(givenSerialNumber)
                .privateKey(privateKey)
                .version(givenVersion)
                .build();
        final KeyStoreSource gveinKeyStoreSource = KeyStoreSource.builder()
                .keyStoreSources("given-key-store-sources".getBytes(StandardCharsets.UTF_8))
                .serialNumber(givenSerialNumber)
                .build();
        final VersionedKeyStoreSource givenVersionedKeyStoreSource = VersionedKeyStoreSource.builder()
                .keyStoreSource(gveinKeyStoreSource)
                .version(givenVersion)
                .build();
        BDDMockito.given(redisTemplate.execute(BDDMockito.any(CompareVersionedKeyStoreSourceAndSetCallback.class)))
                .willReturn(Boolean.TRUE);
        BDDMockito.given(valueOperations.get(givenKey)).willReturn(givenVersionedKeyStoreSource);
        BDDMockito.given(keyStoreTemplate.reloadFromSource(gveinKeyStoreSource)).willReturn(internalKeyStore);
        BDDMockito.given(internalKeyStore.getX509Certificate()).willReturn(x509Certificate);
        BDDMockito.given(internalKeyStore.getPrivateKey()).willReturn(privateKey);
        // When
        final CertificateData result = repository.saveOne(givenKey, givenCertificateData);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(givenVersion, result.version());
        Assertions.assertEquals(privateKey, result.privateKey());
        Assertions.assertEquals(givenSerialNumber, result.serialNumber());
        Assertions.assertEquals(x509Certificate, result.x509Certificate());
        // And
        BDDMockito.then(redisTemplate).should()
                .execute(BDDMockito.any(CompareVersionedKeyStoreSourceAndSetCallback.class));
        BDDMockito.then(valueOperations).should().get(givenKey);
        BDDMockito.then(keyStoreTemplate).should().reloadFromSource(gveinKeyStoreSource);
        BDDMockito.then(internalKeyStore).should().getX509Certificate();
        BDDMockito.then(internalKeyStore).should().getPrivateKey();
    }

    @Test
    void testSaveOneThrowsUnsuccessfulCompareAndSetRedisOperationException() {
        // Given
        final String givenKey = "given-key";
        final CertificateData givenCertificateData = CertificateData.builder().build();
        BDDMockito.given(redisTemplate.execute(BDDMockito.any(CompareVersionedKeyStoreSourceAndSetCallback.class)))
                .willReturn(Boolean.FALSE);
        // Expect
        final RedisOperationException exception = Assertions
                .assertThrows(RedisOperationException.class, () -> repository.saveOne(givenKey, givenCertificateData));
        Assertions.assertEquals("Unsuccessful Compare And Set Redis operation", exception.getMessage());
        // And
        BDDMockito.then(redisTemplate).should()
                .execute(BDDMockito.any(CompareVersionedKeyStoreSourceAndSetCallback.class));
    }

    @Test
    void testSaveOneThrowsCertificateDataCannotBeNullException() {
        // Given
        final String givenKey = "given-key";
        final CertificateData givenCertificateData = CertificateData.builder().build();
        BDDMockito.given(redisTemplate.execute(BDDMockito.any(CompareVersionedKeyStoreSourceAndSetCallback.class)))
                .willReturn(Boolean.TRUE);
        BDDMockito.given(valueOperations.get(givenKey)).willReturn(null);
        // Expect
        final IllegalStateException exception = Assertions
                .assertThrows(IllegalStateException.class, () -> repository.saveOne(givenKey, givenCertificateData));
        Assertions.assertEquals("Certificate data cannot be null", exception.getMessage());
        // And
        BDDMockito.then(redisTemplate).should()
                .execute(BDDMockito.any(CompareVersionedKeyStoreSourceAndSetCallback.class));
        BDDMockito.then(valueOperations).should().get(givenKey);
    }

    @Test
    void testInstantiationThroughTwoArgumentConstructor() {
        // Given
        BDDMockito.given(redisTemplate.opsForValue()).willReturn(valueOperations);
        // Expect
        Assertions.assertDoesNotThrow(() -> new RedisCertificateRepository(keyStoreTemplate, redisTemplate));
        // And
        BDDMockito.then(redisTemplate).should().opsForValue();
    }

}
