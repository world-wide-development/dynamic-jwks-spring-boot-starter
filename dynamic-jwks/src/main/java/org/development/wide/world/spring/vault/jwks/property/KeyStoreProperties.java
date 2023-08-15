package org.development.wide.world.spring.vault.jwks.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("vault-dynamic-jwks.key-store")
public record KeyStoreProperties(
        @DefaultValue("authorization.certificate") String alias,
        @DefaultValue("Xi,#l#NpZr.v:=;kQd0n/'E1#qlNrH") CharSequence password
) {
}
