package org.development.wide.world.spring.redis.jwks.autoconfigure.properties;

import org.development.wide.world.spring.redis.property.RotationRetryInternalProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.lang.NonNull;

import java.time.Duration;

@ConfigurationProperties("dynamic-jwks.redis-storage.certificate-rotation.retry")
public record RotationRetryProperties(
        @DefaultValue("3") Integer maxAttempts,
        @DefaultValue("1s") Duration fixedBackoff
) {

    @NonNull
    public RotationRetryInternalProperties convertToInternal() {
        return RotationRetryInternalProperties.builder()
                .fixedBackoff(this.fixedBackoff())
                .maxAttempts(this.maxAttempts())
                .build();
    }

}
