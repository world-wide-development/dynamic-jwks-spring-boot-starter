package org.development.wide.world.spring.vault.jwks.autoconfigure.properties;

import org.development.wide.world.spring.vault.jwks.property.VaultVersionedKvInternalProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.lang.NonNull;

@ConfigurationProperties("dynamic-jwks.vault.versioned-kv")
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
