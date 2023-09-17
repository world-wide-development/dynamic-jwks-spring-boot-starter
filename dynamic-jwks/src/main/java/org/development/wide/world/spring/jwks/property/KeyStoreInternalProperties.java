package org.development.wide.world.spring.jwks.property;

import org.springframework.lang.NonNull;

public record KeyStoreInternalProperties(
        String alias,
        CharSequence password
) {

    /* Builder */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String alias;
        private CharSequence password;

        public Builder alias(String alias) {
            this.alias = alias;
            return this;
        }

        public Builder password(CharSequence password) {
            this.password = password;
            return this;
        }

        @NonNull
        public KeyStoreInternalProperties build() {
            return new KeyStoreInternalProperties(alias, password);
        }

    }

}
