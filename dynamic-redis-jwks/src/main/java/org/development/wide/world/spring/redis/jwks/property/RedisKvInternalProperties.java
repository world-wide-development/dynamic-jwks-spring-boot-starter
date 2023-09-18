package org.development.wide.world.spring.redis.jwks.property;

import org.springframework.lang.NonNull;

public record RedisKvInternalProperties(
        String certificateKey
) {

    /* Builder */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String certificateKey;

        public Builder certificateKey(String certificateKey) {
            this.certificateKey = certificateKey;
            return this;
        }

        @NonNull
        public RedisKvInternalProperties build() {
            return new RedisKvInternalProperties(certificateKey);
        }

    }

}
