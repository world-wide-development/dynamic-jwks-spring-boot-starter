package org.development.wide.world.spring.vault.jwks.spi;

import org.development.wide.world.spring.vault.jwks.data.JwkSetData;

public interface JwksCertificateRotator {

    JwkSetData rotate();

}
