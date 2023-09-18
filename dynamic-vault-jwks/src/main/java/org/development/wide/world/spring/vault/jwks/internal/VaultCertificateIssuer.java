package org.development.wide.world.spring.vault.jwks.internal;

import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.spi.CertificateIssuer;
import org.development.wide.world.spring.vault.jwks.property.VaultPkiInternalProperties;
import org.springframework.lang.NonNull;
import org.springframework.vault.core.VaultPkiOperations;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultCertificateRequest;
import org.springframework.vault.support.VaultResponseSupport;

import java.util.Optional;

public class VaultCertificateIssuer implements CertificateIssuer {

    private final VaultPkiOperations pkiOperations;
    private final VaultPkiInternalProperties properties;
    private final VaultCertificateDataConverter converter;

    public VaultCertificateIssuer(@NonNull final VaultTemplate vaultTemplate,
                                  @NonNull final VaultPkiInternalProperties properties,
                                  @NonNull final VaultCertificateDataConverter converter) {
        this.converter = converter;
        this.properties = properties;
        this.pkiOperations = vaultTemplate.opsForPki(properties.rootPath());
    }

    @Override
    public CertificateData issueOne() {
        final VaultCertificateRequest request = VaultCertificateRequest.builder()
                .withAltName(properties.certificateCommonName())
                .commonName(properties.certificateCommonName())
                .ttl(properties.certificateTtl())
                .build();
        return Optional.of(pkiOperations.issueCertificate(properties.roleName(), request))
                .map(VaultResponseSupport::getData)
                .map(converter::convert)
                .orElseThrow(() -> new IllegalStateException("Certificate bundle cannot be null"));
    }

}
