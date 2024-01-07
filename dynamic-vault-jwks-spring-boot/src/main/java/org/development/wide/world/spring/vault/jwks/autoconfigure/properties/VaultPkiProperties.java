package org.development.wide.world.spring.vault.jwks.autoconfigure.properties;

import org.development.wide.world.spring.vault.jwks.property.VaultPkiInternalProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.lang.NonNull;

import java.time.Duration;

@ConfigurationProperties("dynamic-jwks.vault-storage.pki")
public record VaultPkiProperties(
        @DefaultValue("pki") String rootPath,
        @DefaultValue("jwks") String roleName,
        @DefaultValue("1m") Duration certificateTtl,
        @DefaultValue("authorization.certificate") String certificateCommonName
) {

    @NonNull
    public VaultPkiInternalProperties convertToInternal() {
        return VaultPkiInternalProperties.builder()
                .certificateCommonName(this.certificateCommonName())
                .certificateTtl(this.certificateTtl())
                .roleName(this.roleName())
                .rootPath(this.rootPath())
                .build();
    }

}
