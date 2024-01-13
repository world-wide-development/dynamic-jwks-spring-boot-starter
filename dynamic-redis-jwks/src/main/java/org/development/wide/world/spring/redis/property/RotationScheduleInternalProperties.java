package org.development.wide.world.spring.redis.property;

import org.springframework.lang.NonNull;

import java.time.Duration;

public record RotationScheduleInternalProperties(
        Boolean enabled,
        Duration interval
) {

    /* Builder */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Boolean enabled;
        private Duration interval;

        public Builder enabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder interval(Duration interval) {
            this.interval = interval;
            return this;
        }

        @NonNull
        public RotationScheduleInternalProperties build() {
            return new RotationScheduleInternalProperties(enabled, interval);
        }

    }

}
