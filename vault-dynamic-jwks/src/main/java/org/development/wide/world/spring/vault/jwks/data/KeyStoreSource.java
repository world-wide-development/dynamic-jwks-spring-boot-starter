package org.development.wide.world.spring.vault.jwks.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.Objects;

public record KeyStoreSource(
        @JsonProperty("serialNumber") String serialNumber,
        @JsonProperty("keyStoreSources") byte[] keyStoreSources
) {

    /* Equals and Hash Code */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KeyStoreSource that = (KeyStoreSource) o;
        return Objects.equals(serialNumber, that.serialNumber)
               && Arrays.equals(keyStoreSources, that.keyStoreSources);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hash(serialNumber) + Arrays.hashCode(keyStoreSources);
    }

    /* To String */
    @Override
    @NonNull
    public String toString() {
        return "KeyStoreSource{" +
               "serialNumber='" + serialNumber + '\'' +
               ", keyStoreSources=" + Arrays.toString(keyStoreSources) +
               '}';
    }

    /* Builder */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String serialNumber;
        private byte[] keyStoreSources;

        public Builder serialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
            return this;
        }

        public Builder keyStoreSources(byte[] keyStoreSources) {
            this.keyStoreSources = keyStoreSources;
            return this;
        }

        @NonNull
        public KeyStoreSource build() {
            return new KeyStoreSource(this.serialNumber, this.keyStoreSources);
        }

    }

}
