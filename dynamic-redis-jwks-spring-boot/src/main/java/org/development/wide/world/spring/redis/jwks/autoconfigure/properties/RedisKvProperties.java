package org.development.wide.world.spring.redis.jwks.autoconfigure.properties;

import org.development.wide.world.spring.redis.property.RedisKvInternalProperties;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("dynamic-jwks.redis-storage.kv")
public record RedisKvProperties(
        @DefaultValue("authorization.certificate") String certificateKey
) {
    @NonNull
    public RedisKvInternalProperties convertToInternal() {
        return RedisKvInternalProperties.builder()
                .certificateKey(this.certificateKey())
                .build();
    }

}
