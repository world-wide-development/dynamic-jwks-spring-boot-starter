package org.development.wide.world.spring.redis.property;

import org.springframework.lang.NonNull;

import java.time.Duration;

public record RotationRetryInternalProperties(
        Integer maxAttempts,
        Duration fixedBackoff
) {

    /* Builder */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Integer maxAttempts;
        private Duration fixedBackoff;

        public Builder maxAttempts(Integer maxAttempts) {
            this.maxAttempts = maxAttempts;
            return this;
        }

        public Builder fixedBackoff(Duration fixedBackoff) {
            this.fixedBackoff = fixedBackoff;
            return this;
        }

        @NonNull
        public RotationRetryInternalProperties build() {
            return new RotationRetryInternalProperties(maxAttempts, fixedBackoff);
        }

    }

}
