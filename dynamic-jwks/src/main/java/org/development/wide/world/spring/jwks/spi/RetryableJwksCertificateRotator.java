package org.development.wide.world.spring.jwks.spi;

import org.development.wide.world.spring.jwks.data.JwkSetData;

/**
 * The main contract defining certificate rotation mechanisms
 * for dynamic JWKS, with a certain ability to retry rotation, in case if something went wrong
 *
 * @see JwkSetData
 */
public interface RetryableJwksCertificateRotator {

    /**
     * Performs rotation of the certificate in case of expiry of its validity period
     *
     * @return {@code JwkSetData} with fresh certificate
     */
    JwkSetData rotate();

}
