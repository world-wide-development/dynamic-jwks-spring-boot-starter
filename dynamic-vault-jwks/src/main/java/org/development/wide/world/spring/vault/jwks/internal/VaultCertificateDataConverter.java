package org.development.wide.world.spring.vault.jwks.internal;

import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.spi.CertificateDataConverter;
import org.development.wide.world.spring.jwks.util.KeyPairUtils;
import org.springframework.lang.NonNull;
import org.springframework.vault.support.CertificateBundle;

import java.security.PrivateKey;
import java.security.spec.KeySpec;

/**
 * Implementation fo the {@link CertificateDataConverter}
 * <br>
 * Converts {@link CertificateBundle} to {@link CertificateData}
 *
 * @see CertificateDataConverter<CertificateBundle>
 */
public class VaultCertificateDataConverter implements CertificateDataConverter<CertificateBundle> {

    /**
     * @see CertificateDataConverter#convert(Object)
     */
    @Override
    public CertificateData convert(@NonNull final CertificateBundle source) {
        final KeySpec privateKeySpec = source.getPrivateKeySpec();
        final PrivateKey privateKey = KeyPairUtils.extractPrivateKey(privateKeySpec);
        return CertificateData.builder()
                .x509Certificates(source.getX509IssuerCertificates())
                .x509Certificate(source.getX509Certificate())
                .serialNumber(source.getSerialNumber())
                .privateKey(privateKey)
                .build();
    }

}
