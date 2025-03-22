package org.development.wide.world.spring.jwks.data;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;

public record CertificateRotationData(
        String key,
        Duration rotateBefore
) {

    /* Builder */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        @Nullable
        private String key;
        @Nullable
        private Duration rotateBefore;

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder rotateBefore(Duration rotateBefore) {
            this.rotateBefore = rotateBefore;
            return this;
        }

        @NonNull
        public CertificateRotationData build() {
            return new CertificateRotationData(key, rotateBefore);
        }

    }

}
