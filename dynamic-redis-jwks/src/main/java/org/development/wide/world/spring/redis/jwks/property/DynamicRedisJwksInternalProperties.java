package org.development.wide.world.spring.redis.jwks.property;

import org.springframework.lang.NonNull;

import java.time.Duration;

public record DynamicRedisJwksInternalProperties(
        Boolean enabled,
        RedisKvInternalProperties kv,
        Integer certificateRotationRetries,
        Duration certificateRotationRetryFixedBackoff
) {

    /* Builder */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Boolean enabled;
        private RedisKvInternalProperties kv;
        private Integer certificateRotationRetries;
        private Duration certificateRotationRetryFixedBackoff;

        public Builder enabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder kv(RedisKvInternalProperties kv) {
            this.kv = kv;
            return this;
        }

        public Builder certificateRotationRetries(Integer certificateRotationRetries) {
            this.certificateRotationRetries = certificateRotationRetries;
            return this;
        }

        public Builder certificateRotationRetryFixedBackoff(Duration certificateRotationRetryFixedBackoff) {
            this.certificateRotationRetryFixedBackoff = certificateRotationRetryFixedBackoff;
            return this;
        }

        @NonNull
        public DynamicRedisJwksInternalProperties build() {
            return new DynamicRedisJwksInternalProperties(enabled, kv, certificateRotationRetries, certificateRotationRetryFixedBackoff);
        }

    }

}
