package org.development.wide.world.spring.redis.jwks.autoconfigure.properties;

import org.development.wide.world.spring.redis.property.DynamicRedisJwksInternalProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.lang.NonNull;

import java.time.Duration;

@ConfigurationProperties("dynamic-jwks.redis")
@EnableConfigurationProperties({RedisKvProperties.class})
public record DynamicRedisJwksProperties(
        @NestedConfigurationProperty
        @DefaultValue RedisKvProperties kv,
        @DefaultValue("false") Boolean enabled,
        @DefaultValue("3") Integer certificateRotationRetries,
        @DefaultValue("1s") Duration certificateRotationRetryFixedBackoff
) {

    @NonNull
    public DynamicRedisJwksInternalProperties convertToInternal() {
        return DynamicRedisJwksInternalProperties.builder()
                .certificateRotationRetryFixedBackoff(this.certificateRotationRetryFixedBackoff())
                .certificateRotationRetries(this.certificateRotationRetries())
                .kv(this.kv().convertToInternal())
                .enabled(this.enabled())
                .build();
    }

}
