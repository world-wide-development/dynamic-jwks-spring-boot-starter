package org.development.wide.world.spring.redis.jwks.autoconfigure.properties;

import org.development.wide.world.spring.redis.jwks.property.RedisKvInternalProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.lang.NonNull;

@ConfigurationProperties("dynamic-jwks.redis.pki")
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
