package org.development.wide.world.spring.vault.jwks.internal;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.development.wide.world.spring.vault.jwks.data.KeyStoreData;
import org.development.wide.world.spring.vault.jwks.util.JwkSetUtils;
import org.springframework.lang.NonNull;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * Converter designed to convert to {@link JWKSet}
 *
 * @see JWKSet
 */
public class JwkSetConverter {

    public JWKSet convert(@NonNull final KeyStoreData keyStoreData) {
        final X509Certificate x509Certificate = keyStoreData.x509Certificate();

        final RSAKey parsedRsakey = JwkSetUtils.parseRsaKey(x509Certificate);
        final PrivateKey privateKey = keyStoreData.privateKey();
        final String serialNumber = keyStoreData.serialNumber();
        final RSAKey rsaKey = new RSAKey.Builder(parsedRsakey)
                .privateKey(privateKey)
                .keyID(serialNumber)
                .build();
        return new JWKSet(rsaKey);
    }

}
