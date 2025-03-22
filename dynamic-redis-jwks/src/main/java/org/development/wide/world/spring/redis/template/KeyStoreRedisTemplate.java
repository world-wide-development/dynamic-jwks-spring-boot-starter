package org.development.wide.world.spring.redis.template;

import org.development.wide.world.spring.redis.data.VersionedKeyStoreSource;
import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Redis template, designed especially for {@link VersionedKeyStoreSource}
 *
 * @see RedisTemplate
 */
public class KeyStoreRedisTemplate extends RedisTemplate<String, VersionedKeyStoreSource> {

    public KeyStoreRedisTemplate(final RedisConnectionFactory connectionFactory) {
        setKeySerializer(RedisSerializer.string());
        setValueSerializer(RedisSerializer.json());
        setHashKeySerializer(RedisSerializer.string());
        setHashValueSerializer(RedisSerializer.json());
        setConnectionFactory(connectionFactory);
        afterPropertiesSet();
    }

    @NonNull
    public static KeyStoreRedisTemplate of(@NonNull final RedisConnectionFactory connectionFactory) {
        return new KeyStoreRedisTemplate(connectionFactory);
    }

}
