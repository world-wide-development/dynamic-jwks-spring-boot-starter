package org.development.wide.world.spring.redis.internal;

import core.base.BaseUnitTest;
import org.development.wide.world.spring.jwks.data.CertificateData;
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

@SuppressWarnings({"unused"})
@ExtendWith({MockitoExtension.class})
class RedisCertificateRepositoryUnitTest extends BaseUnitTest {

    @Mock
    KeyStoreTemplate keyStoreTemplate;
    @Mock
    KeyStoreRedisTemplate redisTemplate;
    @Mock
    ValueOperations<String, VersionedKeyStoreSource> valueOperations;

    @InjectMocks
    RedisCertificateRepository repository;

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

}
