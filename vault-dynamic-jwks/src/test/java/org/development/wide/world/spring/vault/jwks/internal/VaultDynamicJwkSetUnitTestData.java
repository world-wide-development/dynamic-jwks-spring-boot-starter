package org.development.wide.world.spring.vault.jwks.internal;

import org.development.wide.world.spring.vault.jwks.data.VaultJwkSetData;
import org.development.wide.world.spring.vault.jwks.util.JwkSetUtils;
import org.springframework.lang.NonNull;
import org.springframework.vault.support.CertificateBundle;
import org.springframework.vault.support.Versioned;

import static java.util.Optional.ofNullable;
import static org.development.wide.world.spring.vault.jwks.spi.VaultJwksCertificateRotatorUnitTestData.extractExpiredVersionedCertificateBundle;

public final class VaultDynamicJwkSetUnitTestData {

    private VaultDynamicJwkSetUnitTestData() {
        // Suppresses default constructor
    }

    @NonNull
    public static VaultJwkSetData extractVaultJwkSetData() {
        final Versioned<CertificateBundle> versionedCertificateBundle = extractExpiredVersionedCertificateBundle();
        final CertificateBundle certificateBundle = ofNullable(versionedCertificateBundle.getData()).orElseThrow();
        return VaultJwkSetData.builder()
                .x509Certificate(certificateBundle.getX509Certificate())
                .versionedCertificateBundle(versionedCertificateBundle)
                .jwkSet(JwkSetUtils.extract(certificateBundle))
                .build();
    }

}
