package org.development.wide.world.spring.vault.jwks.autoconfigure.properties;

import org.development.wide.world.spring.vault.jwks.property.DynamicVaultJwksInternalProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.lang.NonNull;

/**
 * Represent root namespace of the Dynamic Vault JWKS properties
 *
 * @see VaultPkiProperties
 * @see VaultVersionedKvProperties
 * @see ConfigurationProperties
 * @see EnableConfigurationProperties
 */
@EnableConfigurationProperties({
        VaultPkiProperties.class,
        VaultVersionedKvProperties.class
})
@ConfigurationProperties("dynamic-jwks.vault")
public record DynamicVaultJwksProperties(
        @NestedConfigurationProperty
        @DefaultValue VaultPkiProperties pki,
        @DefaultValue("false") Boolean enabled,
        @NestedConfigurationProperty
        @DefaultValue VaultVersionedKvProperties versionedKv,
        @DefaultValue("3") Integer certificateRotationRetries
) {

    @NonNull
    public DynamicVaultJwksInternalProperties convertToInternal() {
        return DynamicVaultJwksInternalProperties.builder()
                .certificateRotationRetries(this.certificateRotationRetries())
                .versionedKv(this.versionedKv().convertToInternal())
                .pki(this.pki().convertToInternal())
                .enabled(this.enabled())
                .build();
    }

}
