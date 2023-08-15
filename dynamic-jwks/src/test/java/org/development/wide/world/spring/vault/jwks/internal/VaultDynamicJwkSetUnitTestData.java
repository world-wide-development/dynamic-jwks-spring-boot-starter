package org.development.wide.world.spring.vault.jwks.internal;

import org.development.wide.world.spring.vault.jwks.data.JwkSetData;
import org.development.wide.world.spring.vault.jwks.data.KeyStoreData;
import org.development.wide.world.spring.vault.jwks.spi.VaultJwksCertificateRotatorUnitTestData;
import org.springframework.lang.NonNull;

public final class VaultDynamicJwkSetUnitTestData {

    private VaultDynamicJwkSetUnitTestData() {
        // Suppresses default constructor
    }

    @NonNull
    public static JwkSetData extractVaultJwkSetData() {
        final KeyStoreData keyStoreData = VaultJwksCertificateRotatorUnitTestData.extractExpiredKeyStoreData();
        final JwkSetConverter jwkSetConverter = new JwkSetConverter();
        return JwkSetData.builder()
                .jwkSet(jwkSetConverter.convert(keyStoreData))
                .keyStoreData(keyStoreData)
                .build();
    }

}
