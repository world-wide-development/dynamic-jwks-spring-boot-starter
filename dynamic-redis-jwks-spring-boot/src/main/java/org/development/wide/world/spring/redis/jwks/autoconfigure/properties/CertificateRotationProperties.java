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

    public static final int SCHEDULE_INTERVAL_RESERVE_SECONDS = 10;

    public CertificateRotationProperties {
        this.validateRotateBeforeProperty(rotateBefore, schedule);
    }

    @NonNull
    public CertificateRotationInternalProperties convertToInternal() {
        return CertificateRotationInternalProperties.builder()
                .schedule(this.schedule().convertToInternal())
                .retry(this.retry().convertToInternal())
                .rotationLockKey(this.rotationLockKey())
                .rotateBefore(this.rotateBefore())
                .build();
    }

    /* Private methods */
    private void validateRotateBeforeProperty(@NonNull final Duration rotateBefore,
                                              @NonNull final RotationScheduleProperties schedule) {
        final Duration fallbackInterval = schedule.interval()
                .plusSeconds(SCHEDULE_INTERVAL_RESERVE_SECONDS);
        if (rotateBefore.compareTo(fallbackInterval) < 0) {
            throw new IllegalStateException("rotateBefore must be greater then schedule interval");
        }
    }

}
