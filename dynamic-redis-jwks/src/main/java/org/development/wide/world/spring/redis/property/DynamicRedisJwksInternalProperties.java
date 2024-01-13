package org.development.wide.world.spring.redis.property;

import org.springframework.lang.NonNull;

public record DynamicRedisJwksInternalProperties(
        Boolean enabled,
        RedisKvInternalProperties kv,
        CertificateRotationInternalProperties certificateRotation
) {

    /* Builder */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Boolean enabled;
        private RedisKvInternalProperties kv;
        private CertificateRotationInternalProperties certificateRotation;

        public Builder enabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder kv(RedisKvInternalProperties kv) {
            this.kv = kv;
            return this;
        }

        public Builder certificateRotation(CertificateRotationInternalProperties certificateRotation) {
            this.certificateRotation = certificateRotation;
            return this;
        }

        @NonNull
        public DynamicRedisJwksInternalProperties build() {
            return new DynamicRedisJwksInternalProperties(enabled, kv, certificateRotation);
        }

    }

}
