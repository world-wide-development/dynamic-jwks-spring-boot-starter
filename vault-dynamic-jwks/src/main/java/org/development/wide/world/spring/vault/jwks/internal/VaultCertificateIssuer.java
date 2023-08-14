package org.development.wide.world.spring.vault.jwks.internal;

import org.development.wide.world.spring.vault.jwks.property.VaultDynamicJwksProperties;
import org.development.wide.world.spring.vault.jwks.spi.CertificateIssuer;
import org.springframework.lang.NonNull;
import org.springframework.vault.core.VaultPkiOperations;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.CertificateBundle;
import org.springframework.vault.support.VaultCertificateRequest;
import org.springframework.vault.support.VaultResponseSupport;

import java.util.Optional;

public class VaultCertificateIssuer implements CertificateIssuer {

    private final VaultPkiOperations pkiOperations;
    private final VaultDynamicJwksProperties properties;

    public VaultCertificateIssuer(@NonNull final VaultTemplate vaultTemplate,
                                  @NonNull final VaultDynamicJwksProperties properties) {
        this.properties = properties;
        this.pkiOperations = vaultTemplate.opsForPki(properties.pkiPath());
    }

    @Override
    public CertificateBundle issueOne() {
        final VaultCertificateRequest request = VaultCertificateRequest.builder()
                .withAltName(properties.pkiCertificateCommonName())
                .commonName(properties.pkiCertificateCommonName())
                .ttl(properties.pkiCertificateTtl())
                .build();
        return Optional.of(pkiOperations.issueCertificate(properties.pkiRoleName(), request))
                .map(VaultResponseSupport::getData)
                .orElseThrow(() -> new IllegalStateException("Certificate bundle cannot be null"));
    }

}
