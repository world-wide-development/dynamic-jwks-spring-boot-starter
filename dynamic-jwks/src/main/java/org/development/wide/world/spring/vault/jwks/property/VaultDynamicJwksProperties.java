package org.development.wide.world.spring.vault.jwks.property;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

@ConfigurationProperties("vault-dynamic-jwks")
@EnableConfigurationProperties({KeyStoreProperties.class})
public record VaultDynamicJwksProperties(
        @DefaultValue("pki") String pkiPath,
        @DefaultValue("false") Boolean enabled,
        @DefaultValue("jwks") String pkiRoleName,
        @NestedConfigurationProperty
        @DefaultValue KeyStoreProperties keyStore,
        @DefaultValue("1s") Duration pkiCertificateTtl,
        @DefaultValue("dynamic-jwks") String versionedKeyValuePath,
        @DefaultValue("authorization.certificate") String certificatePath,
        @DefaultValue("authorization.certificate") String pkiCertificateCommonName
) {
}
