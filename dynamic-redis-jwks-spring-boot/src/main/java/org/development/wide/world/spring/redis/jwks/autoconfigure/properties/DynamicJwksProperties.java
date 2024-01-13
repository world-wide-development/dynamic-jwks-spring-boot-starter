package org.development.wide.world.spring.redis.jwks.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.lang.NonNull;

import java.time.Duration;

@EnableConfigurationProperties({
        KeyStoreProperties.class,
        BCCertificateProperties.class,
        DynamicRedisJwksProperties.class
})
@ConfigurationProperties("dynamic-jwks")
public record DynamicJwksProperties(
        @NestedConfigurationProperty
        @DefaultValue KeyStoreProperties keyStore,
        @NestedConfigurationProperty
        @DefaultValue BCCertificateProperties bcCertificate,
        @NestedConfigurationProperty
        @DefaultValue DynamicRedisJwksProperties redisStorage
) {

    public DynamicJwksProperties {
        this.validateCertificateTtlProperty(bcCertificate, redisStorage);
    }

    /* Private methods */
    private void validateCertificateTtlProperty(@NonNull final BCCertificateProperties bcCertificate,
                                                @NonNull final DynamicRedisJwksProperties redisStorage) {
        final Duration certificateTtl = bcCertificate.certificateTtl();
        final Duration rotateBefore = redisStorage.certificateRotation().rotateBefore();
        if (certificateTtl.compareTo(rotateBefore) <= 0) {
            throw new IllegalStateException("certificateTtl property must be greater then rotateBefore");
        }
    }

}
