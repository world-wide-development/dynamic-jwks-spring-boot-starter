package org.development.wide.world.spring.jwks.spi;

import org.development.wide.world.spring.jwks.data.JwkSetData;

import java.time.Duration;

/**
 * Represents a {@link JwkSetData} state storage contract.
 */
public interface JwkSetDataHolder {

    /**
     * Provides the ability to get a valid certificate
     *
     * @return valid and unexpired {@code JwkSetData}
     */
    JwkSetData getActual();

    /**
     * Provides the ability to rotate a certificate
     * or get a current one if it is still valid.
     *
     * @param rotateBefore determines how early to rotate
     * @return valid and unexpired {@code JwkSetData}
     */
    JwkSetData rotateInAdvanceIfAny(Duration rotateBefore);

}
