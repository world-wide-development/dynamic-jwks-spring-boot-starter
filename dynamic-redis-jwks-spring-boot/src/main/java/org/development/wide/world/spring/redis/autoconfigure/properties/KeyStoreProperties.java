package org.development.wide.world.spring.redis.autoconfigure.properties;

import org.development.wide.world.spring.jwks.property.KeyStoreInternalProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.lang.NonNull;

@ConfigurationProperties("dynamic-jwks.key-store")
public record KeyStoreProperties(
        @DefaultValue("authorization.certificate") String alias,
        @DefaultValue("Xi,#l#NpZr.v:=;kQd0n/'E1#qlNrH") CharSequence password
) {

    @NonNull
    public KeyStoreInternalProperties convertToInternal() {
        return KeyStoreInternalProperties.builder()
                .password(this.password())
                .alias(this.alias())
                .build();
    }

}
