package org.development.wide.world.spring.vault.jwks.util;

import org.springframework.lang.NonNull;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.time.Instant;
import java.util.Date;

/**
 * A set of utilities for certificate interaction
 *
 * @see KeySpec
 * @see PrivateKey
 * @see X509Certificate
 */
public final class CertificateUtils {

    private CertificateUtils() {
        // Suppresses default constructor
    }

    /**
     * Generates {@link PrivateKey} from given {@link KeySpec} according to given algorithm
     *
     * @param keySpec   key spec for private key generation
     * @param algorithm algorithm of the target private key
     * @return {@code  PrivateKey}
     * @throws UnsupportedOperationException in case of wrong algorithm or invalid key speck
     */
    @SuppressWarnings("unused")
    public static PrivateKey generatePrivateKey(final KeySpec keySpec, final String algorithm) {
        try {
            final KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new UnsupportedOperationException("Cant generate private key", e);
        }
    }

    /**
     * Checks that the given date is within the certificate's validity period<br>
     * In other words, this determines whether the certificate would be valid at the given instant<br>
     * Uses {@link Instant#now()} under the hood, for certificate time validation
     *
     * @param certificate X509 Certificate to validate
     * @return {@code boolean ture} if certificate is valid
     * @see #checkValidity(Instant, X509Certificate)
     */
    public static boolean checkValidity(@NonNull final X509Certificate certificate) {
        final Instant now = Instant.now();
        return checkValidity(now, certificate);
    }

    /**
     * Checks that the given date is within the certificate's validity period<br>
     * In other words, this determines whether the certificate would be valid at the given instant
     *
     * @param instant     the Instant to check against
     * @param certificate X509 Certificate to validate
     * @return {@code boolean ture} if certificate is valid
     * @see #checkValidity(X509Certificate)
     */
    public static boolean checkValidity(@NonNull final Instant instant, @NonNull final X509Certificate certificate) {
        try {
            certificate.checkValidity(Date.from(instant));
            return true;
        } catch (CertificateExpiredException | CertificateNotYetValidException e) {
            return false;
        }
    }

}