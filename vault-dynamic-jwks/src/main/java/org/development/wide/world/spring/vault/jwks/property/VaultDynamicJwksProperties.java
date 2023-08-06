package org.development.wide.world.spring.vault.jwks.property;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

@ConfigurationProperties("vault-dynamic-jwks")
public record VaultDynamicJwksProperties(
        @DefaultValue("pki") String pkiPath,
        @DefaultValue("false") Boolean enabled,
        @DefaultValue("jwks") String pkiRoleName,
        @DefaultValue("1s") Duration pkiCertificateTtl,
        @DefaultValue("authorization.certificate") String certificatePath,
        @DefaultValue("authorization.certificate") String pkiCertificateCommonName,
        @DefaultValue("vault-dynamic-jwks-spring-boot-starter") String versionedKeyValuePath
) {
}
