package org.development.wide.world.spring.redis.data;

import org.development.wide.world.spring.jwks.data.KeyStoreSource;
import org.springframework.lang.NonNull;

import java.io.Serial;
import java.io.Serializable;

public record VersionedKeyStoreSource(
        Integer version,
        KeyStoreSource keyStoreSource
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 42L;

    /* Builder */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Integer version;
        private KeyStoreSource keyStoreSource;

        public Builder version(Integer version) {
            this.version = version;
            return this;
        }

        public Builder keyStoreSource(KeyStoreSource keyStoreSource) {
            this.keyStoreSource = keyStoreSource;
            return this;
        }

        @NonNull
        public VersionedKeyStoreSource build() {
            return new VersionedKeyStoreSource(version, keyStoreSource);
        }

    }

}
