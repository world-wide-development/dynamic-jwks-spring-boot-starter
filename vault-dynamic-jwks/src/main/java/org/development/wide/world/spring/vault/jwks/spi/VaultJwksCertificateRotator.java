package org.development.wide.world.spring.vault.jwks.spi;

import org.development.wide.world.spring.vault.jwks.data.VaultJwkSetHolder;

public interface VaultJwksCertificateRotator {

    VaultJwkSetHolder rotate();

}
