package org.development.wide.world.spring.redis.property;

import org.springframework.lang.NonNull;

import java.time.Duration;

public record CertificateRotationInternalProperties(
        Duration rotateBefore,
        String rotationLockKey,
        RotationRetryInternalProperties retry,
        RotationScheduleInternalProperties schedule
) {

    /* Builder */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Duration rotateBefore;
        private String rotationLockKey;
        private RotationRetryInternalProperties retry;
        private RotationScheduleInternalProperties schedule;

        public Builder rotateBefore(Duration rotateBefore) {
            this.rotateBefore = rotateBefore;
            return this;
        }

        public Builder rotationLockKey(String rotationLockKey) {
            this.rotationLockKey = rotationLockKey;
            return this;
        }

        public Builder retry(RotationRetryInternalProperties retry) {
            this.retry = retry;
            return this;
        }

        public Builder schedule(RotationScheduleInternalProperties schedule) {
            this.schedule = schedule;
            return this;
        }

        @NonNull
        public CertificateRotationInternalProperties build() {
            return new CertificateRotationInternalProperties(rotateBefore, rotationLockKey, retry, schedule);
        }

    }

}
