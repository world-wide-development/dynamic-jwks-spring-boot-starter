package org.development.wide.world.spring.redis.jwks.autoconfigure.properties;

import org.development.wide.world.spring.redis.property.RotationScheduleInternalProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.lang.NonNull;

import java.time.Duration;

@ConfigurationProperties("dynamic-jwks.redis-storage.certificate-rotation.schedule")
public record RotationScheduleProperties(
        @DefaultValue("true") Boolean enabled,
        @DefaultValue("20m") Duration interval
) {

    @NonNull
    public RotationScheduleInternalProperties convertToInternal() {
        return RotationScheduleInternalProperties.builder()
                .interval(this.interval())
                .enabled(this.enabled())
                .build();
    }

}
