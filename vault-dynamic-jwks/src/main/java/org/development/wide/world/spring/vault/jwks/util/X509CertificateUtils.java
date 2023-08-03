package org.development.wide.world.spring.vault.jwks.util;

import org.springframework.lang.NonNull;

import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Date;

/**
 * A set of utilities for interacting with the x509 certificate
 *
 * @see X509Certificate
 */
public final class X509CertificateUtils {

    private X509CertificateUtils() {
        // Suppresses default constructor
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
