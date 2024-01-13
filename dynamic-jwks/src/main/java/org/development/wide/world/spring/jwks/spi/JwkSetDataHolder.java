package org.development.wide.world.spring.jwks.spi;

import org.development.wide.world.spring.jwks.data.JwkSetData;

import java.time.Duration;

public interface JwkSetDataHolder {

    JwkSetData getActual();

    JwkSetData rotateInAdvanceIfAny(Duration rotateBefore);

}
