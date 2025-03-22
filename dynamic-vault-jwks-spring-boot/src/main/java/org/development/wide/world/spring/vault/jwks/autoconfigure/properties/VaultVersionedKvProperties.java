package org.development.wide.world.spring.vault.jwks.autoconfigure.properties;

import org.development.wide.world.spring.vault.jwks.property.VaultVersionedKvInternalProperties;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("dynamic-jwks.vault-storage.versioned-kv")
public record VaultVersionedKvProperties(
        @DefaultValue("secret") String rootPath,
        @DefaultValue("authorization.certificate") String certificatePath
) {

    @NonNull
    public VaultVersionedKvInternalProperties convertToInternal() {
        return VaultVersionedKvInternalProperties.builder()
                .certificatePath(this.certificatePath())
                .rootPath(this.rootPath())
                .build();
    }

}
