package org.development.wide.world.spring.vault.jwks.util;

import com.nimbusds.jose.Algorithm;
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

    /**
     * Extracts {@link JWKSet} from given {@link CertificateBundle}
     *
     * @param certificateBundle source
     * @return {@code JWKSet}
     */
    @NonNull
    public static JWKSet extract(@NonNull final CertificateBundle certificateBundle) {
        final X509Certificate x509Certificate = certificateBundle.getX509Certificate();
        final KeySpec privateKeySpec = certificateBundle.getPrivateKeySpec();
        final String privateKeyType = certificateBundle.getPrivateKeyType();
        final RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) x509Certificate.getPublicKey())
                .privateKey(CertificateUtils.generatePrivateKey(privateKeySpec, privateKeyType))
                .keyID(certificateBundle.getSerialNumber())
                .algorithm(Algorithm.parse(privateKeyType))
                .build();
        return new JWKSet(rsaKey);
    }

}
