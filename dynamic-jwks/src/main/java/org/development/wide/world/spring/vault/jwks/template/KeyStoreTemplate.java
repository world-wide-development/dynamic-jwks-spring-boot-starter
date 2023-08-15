package org.development.wide.world.spring.vault.jwks.template;

import org.development.wide.world.spring.vault.jwks.data.KeyStoreSource;
import org.development.wide.world.spring.vault.jwks.internal.InternalKeyStore;
import org.development.wide.world.spring.vault.jwks.util.KeyFactoryUtils;
import org.springframework.lang.NonNull;
import org.springframework.vault.support.CertificateBundle;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.KeySpec;
import java.util.stream.Stream;

public class KeyStoreTemplate {

    private final InternalKeyStore internalKeyStore;

    public KeyStoreTemplate(final InternalKeyStore internalKeyStore) {
        this.internalKeyStore = internalKeyStore;
    }

    public InternalKeyStore reloadFromSource(@NonNull final KeyStoreSource keyStoreSource) {
        final byte[] sources = keyStoreSource.keyStoreSources();
        return internalKeyStore.reloadFromByteArray(sources);
    }

    public KeyStoreSource saveCertificate(@NonNull final CertificateBundle certificateBundle) {
        return saveCertificate(Boolean.TRUE, certificateBundle);
    }

    public KeyStoreSource saveCertificate(final boolean includeChain,
                                          @NonNull final CertificateBundle certificateBundle) {
        final KeySpec privateKeySpec = certificateBundle.getPrivateKeySpec();
        final PrivateKey privateKey = KeyFactoryUtils.extractPrivateKey(privateKeySpec);
        final Certificate[] chain = extractCertificatesChain(includeChain, certificateBundle);
        final byte[] keyStoreSources = internalKeyStore.saveCertificate(privateKey, chain)
                .serializeToByteArray();
        final String serialNumber = certificateBundle.getSerialNumber();
        return KeyStoreSource.builder()
                .keyStoreSources(keyStoreSources)
                .serialNumber(serialNumber)
                .build();
    }

    /* Private methods */
    @NonNull
    private X509Certificate[] extractCertificatesChain(final boolean includeChain,
                                                       final @NonNull CertificateBundle certificateBundle) {
        return Stream.concat(
                Stream.of(certificateBundle.getX509Certificate()),
                streamIssuerCertificates(includeChain, certificateBundle)
        ).toArray(X509Certificate[]::new);
    }

    private Stream<X509Certificate> streamIssuerCertificates(final boolean includeChain,
                                                             @NonNull final CertificateBundle certificateBundle) {
        if (includeChain) {
            return certificateBundle.getX509IssuerCertificates().stream();
        }
        return Stream.of(certificateBundle.getX509IssuerCertificate());
    }

}
