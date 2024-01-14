package org.development.wide.world.spring.redis.internal;

import core.base.BaseUnitTest;
import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.data.KeyStoreSource;
import org.development.wide.world.spring.jwks.template.KeyStoreTemplate;
import org.development.wide.world.spring.redis.data.VersionedKeyStoreSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

@ExtendWith({MockitoExtension.class})
@SuppressWarnings({"ResultOfMethodCallIgnored"})
class CompareVersionedKeyStoreSourceAndSetCallbackUnitTest extends BaseUnitTest {

    @Mock
    X509Certificate x509Certificate;
    @Mock
    KeyStoreTemplate keyStoreTemplate;
    @Mock
    VersionedKeyStoreSource versionedKeyStoreSource;
    @Mock
    RedisOperations<String, VersionedKeyStoreSource> redisOperations;
    @Mock
    ValueOperations<String, VersionedKeyStoreSource> valueOperations;

    Integer version = 3;
    String key = "given-key";

    CompareVersionedKeyStoreSourceAndSetCallback callback;

    @BeforeEach
    void setUpEach() {
        final CertificateData certificateData = CertificateData.builder()
                .privateKey(BDDMockito.mock(PrivateKey.class))
                .x509Certificates(List.of(x509Certificate))
                .serialNumber("given-serial-number")
                .x509Certificate(x509Certificate)
                .version(version)
                .build();
        callback = CompareVersionedKeyStoreSourceAndSetCallback.of(key, certificateData, keyStoreTemplate);
    }

    @Test
    void testExecuteReturnsTrue() {
        // Given
        final KeyStoreSource givenKeyStoreSource = KeyStoreSource.builder()
                .keyStoreSources("given-store-sources".getBytes(StandardCharsets.UTF_8))
                .serialNumber("given-serial-number")
                .build();
        BDDMockito.given(redisOperations.opsForValue()).willReturn(valueOperations);
        BDDMockito.given(valueOperations.get(key)).willReturn(versionedKeyStoreSource);
        BDDMockito.given(versionedKeyStoreSource.version()).willReturn(version);
        BDDMockito.given(keyStoreTemplate.saveCertificate(BDDMockito.any())).willReturn(givenKeyStoreSource);
        BDDMockito.given(redisOperations.exec()).willReturn(List.of(Boolean.TRUE));
        // When
        final Boolean result = callback.execute(redisOperations);
        // Then
        Assertions.assertEquals(Boolean.TRUE, result);
        // And
        BDDMockito.then(redisOperations).should().watch(key);
        BDDMockito.then(redisOperations).should().opsForValue();
        BDDMockito.then(valueOperations).should().get(key);
        BDDMockito.then(versionedKeyStoreSource).should().version();
        BDDMockito.then(redisOperations).should().multi();
        BDDMockito.then(keyStoreTemplate).should().saveCertificate(BDDMockito.any());
        BDDMockito.then(redisOperations).should().exec();
        BDDMockito.then(redisOperations).should(BDDMockito.never()).unwatch();
    }

    @Test
    void testExecuteReturnsFalseSinceWrongLastVersion() {
        // Given
        final Integer wrongLastVersion = 4;
        BDDMockito.given(redisOperations.opsForValue()).willReturn(valueOperations);
        BDDMockito.given(valueOperations.get(key)).willReturn(versionedKeyStoreSource);
        BDDMockito.given(versionedKeyStoreSource.version()).willReturn(wrongLastVersion);
        // When
        final Boolean result = callback.execute(redisOperations);
        // Then
        Assertions.assertEquals(Boolean.FALSE, result);
        // And
        BDDMockito.then(redisOperations).should().watch(key);
        BDDMockito.then(redisOperations).should().opsForValue();
        BDDMockito.then(valueOperations).should().get(key);
        BDDMockito.then(versionedKeyStoreSource).should().version();
        BDDMockito.then(redisOperations).should(BDDMockito.never()).multi();
        BDDMockito.then(keyStoreTemplate).should(BDDMockito.never()).saveCertificate(BDDMockito.any());
        BDDMockito.then(redisOperations).should(BDDMockito.never()).exec();
        BDDMockito.then(redisOperations).should().unwatch();
    }

    @Test
    void testExecuteReturnsFalseSinceEmptyOperationResults() {
        // Given
        final KeyStoreSource givenKeyStoreSource = KeyStoreSource.builder()
                .keyStoreSources("given-store-sources".getBytes(StandardCharsets.UTF_8))
                .serialNumber("given-serial-number")
                .build();
        BDDMockito.given(redisOperations.opsForValue()).willReturn(valueOperations);
        BDDMockito.given(valueOperations.get(key)).willReturn(versionedKeyStoreSource);
        BDDMockito.given(versionedKeyStoreSource.version()).willReturn(version);
        BDDMockito.given(keyStoreTemplate.saveCertificate(BDDMockito.any())).willReturn(givenKeyStoreSource);
        BDDMockito.given(redisOperations.exec()).willReturn(Collections.emptyList());
        // When
        final Boolean result = callback.execute(redisOperations);
        // Then
        Assertions.assertEquals(Boolean.FALSE, result);
        // And
        BDDMockito.then(redisOperations).should().watch(key);
        BDDMockito.then(redisOperations).should().opsForValue();
        BDDMockito.then(valueOperations).should().get(key);
        BDDMockito.then(versionedKeyStoreSource).should().version();
        BDDMockito.then(redisOperations).should().multi();
        BDDMockito.then(keyStoreTemplate).should().saveCertificate(BDDMockito.any());
        BDDMockito.then(redisOperations).should().exec();
        BDDMockito.then(redisOperations).should().unwatch();
    }

}
