package org.development.wide.world.spring.vault.jwks.internal;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.development.wide.world.spring.vault.jwks.spi.VaultJwksCertificateRotator;
import org.development.wide.world.spring.vault.jwks.data.VaultJwkSetHolder;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class VaultDynamicJwkSet implements JWKSource<SecurityContext> {

    private final VaultJwksCertificateRotator certificateRotator;

    private final AtomicReference<VaultJwkSetHolder> jwkSetHolderAtomicReference = new AtomicReference<>();

    public VaultDynamicJwkSet(final VaultJwksCertificateRotator certificateRotator) {
        this.certificateRotator = certificateRotator;
    }

    @Override
    public List<JWK> get(@NonNull final JWKSelector jwkSelector, final SecurityContext context) {
        final VaultJwkSetHolder vaultJwkSetHolder = jwkSetHolderAtomicReference.updateAndGet(jwkSetHolder -> {
            if (Objects.nonNull(jwkSetHolder) && (jwkSetHolder.checkCertificateValidity())) {
                return jwkSetHolder;
            }
            return certificateRotator.rotate();
        });
        final JWKSet jwkSet = vaultJwkSetHolder.jwkSet();
        return jwkSelector.select(jwkSet);
    }

}
