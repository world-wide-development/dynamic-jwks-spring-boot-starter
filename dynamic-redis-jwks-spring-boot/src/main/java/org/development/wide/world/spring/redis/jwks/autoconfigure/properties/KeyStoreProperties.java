package org.development.wide.world.spring.redis.jwks.autoconfigure.properties;

import jakarta.validation.constraints.NotBlank;
import org.development.wide.world.spring.jwks.property.KeyStoreInternalProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("dynamic-jwks.key-store")
public record KeyStoreProperties(
        @NotBlank CharSequence password,
        @NotBlank @DefaultValue("authorization.certificate") String alias
) {

    @NonNull
    public KeyStoreInternalProperties convertToInternal() {
        return KeyStoreInternalProperties.builder()
                .password(this.password())
                .alias(this.alias())
                .build();
    }

}
