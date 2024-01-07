package org.development.wide.world.spring.jwks.data;

import org.springframework.lang.NonNull;

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

        private String key;
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
