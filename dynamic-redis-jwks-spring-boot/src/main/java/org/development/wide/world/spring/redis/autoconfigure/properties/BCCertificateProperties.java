package org.development.wide.world.spring.redis.autoconfigure.properties;

import org.development.wide.world.spring.jwks.property.BCCertificateInternalProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.lang.NonNull;

import java.time.Duration;

@ConfigurationProperties("dynamic-jwks.bc-certificate")
public record BCCertificateProperties(
        @DefaultValue("6h") Duration certificateTtl,
        @DefaultValue("WorldWideDevelopmentIssuer") String issuer,
        @DefaultValue("WorldWideDevelopmentSubject") String subject
) {

    @NonNull
    public BCCertificateInternalProperties convertToInternal() {
        return BCCertificateInternalProperties.builder()
                .certificateTtl(this.certificateTtl())
                .subject(this.subject())
                .issuer(this.issuer())
                .build();
    }

}
