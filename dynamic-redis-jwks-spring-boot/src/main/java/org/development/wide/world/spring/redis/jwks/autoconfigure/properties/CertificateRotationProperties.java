package org.development.wide.world.spring.redis.jwks.autoconfigure.properties;

import org.development.wide.world.spring.redis.property.CertificateRotationInternalProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.lang.NonNull;

import java.time.Duration;

@EnableConfigurationProperties({
        RotationRetryProperties.class,
        RotationScheduleProperties.class
})
@ConfigurationProperties("dynamic-jwks.redis-storage.certificate-rotation")
public record CertificateRotationProperties(
        @DefaultValue("20m") Duration rotateBefore,
        @NestedConfigurationProperty
        @DefaultValue RotationRetryProperties retry,
        @NestedConfigurationProperty
        @DefaultValue RotationScheduleProperties schedule,
        @DefaultValue("auth-cert-rotation-lock-key") String rotationLockKey
) {

    @NonNull
    public CertificateRotationInternalProperties convertToInternal() {
        return CertificateRotationInternalProperties.builder()
                .schedule(this.schedule().convertToInternal())
                .retry(this.retry().convertToInternal())
                .rotationLockKey(this.rotationLockKey())
                .rotateBefore(this.rotateBefore())
                .build();
    }

}
