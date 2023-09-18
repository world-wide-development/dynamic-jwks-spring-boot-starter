package org.development.wide.world.spring.jwks.util;

import org.springframework.lang.NonNull;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.security.spec.KeySpec;
import java.time.Instant;
import java.util.Date;
import java.util.HexFormat;
import java.util.UUID;

/**
 * A set of utilities for certificate interaction
 *
 * @see KeySpec
 * @see PrivateKey
 * @see X509Certificate
 */
public final class CertificateUtils {

    public static final String DASH = "-";
    public static final String EMPTY = "";
    public static final int BIG_INTEGER_RADIX = 16;
    public static final String HEX_FORMAT_DELIMITER = ":";

    private CertificateUtils() {
        // Suppresses default constructor
    }

    @NonNull
    public static BigInteger generateSerialFromUuid() {
        final String uuid = UUID.randomUUID().toString();
        return new BigInteger(uuid.replace(DASH, EMPTY), BIG_INTEGER_RADIX);
    }

    @NonNull
    public static String bigIntToHexDecimalString(@NonNull final BigInteger bigInteger) {
        final HexFormat hexFormat = HexFormat.ofDelimiter(HEX_FORMAT_DELIMITER);
        return hexFormat.formatHex(bigInteger.toByteArray());
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
