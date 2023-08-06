package org.development.wide.world.spring.vault.jwks.spi;

import org.development.wide.world.spring.vault.jwks.data.VaultJwkSetData;

public interface VaultJwksCertificateRotator {

    VaultJwkSetData rotate();

}
