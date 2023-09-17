package org.development.wide.world.spring.jwks.template;

import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.data.KeyStoreSource;
import org.development.wide.world.spring.jwks.internal.InternalKeyStore;
import org.springframework.lang.NonNull;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.stream.Stream;

public class KeyStoreTemplate {

    private final InternalKeyStore internalKeyStore;

    public KeyStoreTemplate(final InternalKeyStore internalKeyStore) {
        this.internalKeyStore = internalKeyStore;
    }

    public KeyStoreSource saveCertificate(@NonNull final CertificateData certificateData) {
        return saveCertificate(Boolean.TRUE, certificateData);
    }

    public KeyStoreSource saveCertificate(final boolean includeChain,
                                          @NonNull final CertificateData certificateData) {
        final PrivateKey privateKey = certificateData.privateKey();
        final Certificate[] chain = extractCertificatesChain(includeChain, certificateData);
        final byte[] keyStoreSources = internalKeyStore.saveCertificate(privateKey, chain)
                .serializeToByteArray();
        final String serialNumber = certificateData.serialNumber();
        return KeyStoreSource.builder()
                .keyStoreSources(keyStoreSources)
                .serialNumber(serialNumber)
                .build();
    }

    public InternalKeyStore reloadFromSource(@NonNull final KeyStoreSource keyStoreSource) {
        final byte[] sources = keyStoreSource.keyStoreSources();
        return internalKeyStore.reloadFromByteArray(sources);
    }

    /* Private methods */
    @NonNull
    private X509Certificate[] extractCertificatesChain(final boolean includeChain,
                                                       final @NonNull CertificateData certificateData) {
        return Stream.concat(
                Stream.of(certificateData.x509Certificate()),
                streamIssuerCertificates(includeChain, certificateData)
        ).toArray(X509Certificate[]::new);
    }

    private Stream<X509Certificate> streamIssuerCertificates(final boolean includeChain,
                                                             @NonNull final CertificateData certificateData) {
        if (includeChain) {
            return certificateData.x509Certificates().stream();
        }
        return Stream.of(certificateData.x509Certificate());
    }

}
