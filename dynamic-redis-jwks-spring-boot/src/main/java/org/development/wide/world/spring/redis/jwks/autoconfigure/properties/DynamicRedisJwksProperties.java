package org.development.wide.world.spring.redis.jwks.autoconfigure.properties;

import org.development.wide.world.spring.redis.property.DynamicRedisJwksInternalProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.lang.NonNull;

@EnableConfigurationProperties({
        RedisKvProperties.class,
        CertificateRotationProperties.class
})
@ConfigurationProperties("dynamic-jwks.redis-storage")
public record DynamicRedisJwksProperties(
        @NestedConfigurationProperty
        @DefaultValue RedisKvProperties kv,
        @DefaultValue("false") Boolean enabled,
        @NestedConfigurationProperty
        @DefaultValue CertificateRotationProperties certificateRotation
) {

    @NonNull
    public DynamicRedisJwksInternalProperties convertToInternal() {
        return DynamicRedisJwksInternalProperties.builder()
                .certificateRotation(this.certificateRotation().convertToInternal())
                .kv(this.kv().convertToInternal())
                .enabled(this.enabled())
                .build();
    }

}
