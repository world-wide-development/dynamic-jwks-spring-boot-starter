package org.development.wide.world.spring.vault.jwks.util;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.lang.NonNull;
import org.springframework.vault.support.CertificateBundle;

import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.KeySpec;

/**
 * A set of utilities for JWKSet interaction
 *
 * @see JWKSet
 * @see CertificateBundle
 */
public final class JwkSetUtils {

    private JwkSetUtils() {
        // Suppresses default constructor
    }

    public static RSAKey parseRsaKey(X509Certificate x509Certificate) {
        try {
            return RSAKey.parse(x509Certificate);
        } catch (JOSEException e) {
            throw new IllegalStateException("Unable to parse RSA key", e);
        }
    }

    /**
     * Extracts {@link JWKSet} from given {@link CertificateBundle}
     *
     * @param source certificate bundle source
     * @return {@code JWKSet}
     */
    @NonNull
    public static JWKSet extract(@NonNull final CertificateBundle source) {
        final X509Certificate x509Certificate = source.getX509Certificate();
        final KeySpec privateKeySpec = source.getPrivateKeySpec();
        final String privateKeyType = source.getPrivateKeyType();
        final RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) x509Certificate.getPublicKey())
                .privateKey(CertificateUtils.generatePrivateKey(privateKeySpec, privateKeyType))
                .algorithm(Algorithm.parse(privateKeyType))
                .keyID(source.getSerialNumber())
                .build();
        return new JWKSet(rsaKey);
    }

}
