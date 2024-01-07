package org.development.wide.world.spring.jwks.spi;

import org.development.wide.world.spring.jwks.data.JwkSetData;

/**
 * The main contract defining certificate rotation mechanisms for dynamic JWKS
 *
 * @see JwkSetData
 */
public interface JwksCertificateRotator {

    /**
     * Performs rotation of the certificate in case of expiry of its validity period
     *
     * @return {@code JwkSetData} with fresh certificate
     */
    JwkSetData rotate(JwkSetRotationFunction rotationFn);

}
