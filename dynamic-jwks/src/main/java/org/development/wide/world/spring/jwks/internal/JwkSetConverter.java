package org.development.wide.world.spring.jwks.internal;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.development.wide.world.spring.jwks.data.CertificateData;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;

/**
 * Converter designed to convert to {@link JWKSet}
 *
 * @see JWKSet
 */
public class JwkSetConverter {

    /**
     * Converts {@link CertificateData} to {@link JWKSet}
     *
     * @param certificateData source
     * @return {@code JWKSet}
     */
    public JWKSet convert(final CertificateData certificateData) {
        if (certificateData == null) {
            return null;
        }
        final X509Certificate x509Certificate = certificateData.x509Certificate();
        final RSAPublicKey publicKey = (RSAPublicKey) x509Certificate.getPublicKey();
        final PrivateKey privateKey = certificateData.privateKey();
        final String serialNumber = certificateData.serialNumber();
        final RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(serialNumber)
                .build();
        return new JWKSet(rsaKey);
    }

}
