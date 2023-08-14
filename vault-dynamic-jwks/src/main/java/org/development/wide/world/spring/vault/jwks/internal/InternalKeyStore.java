package org.development.wide.world.spring.vault.jwks.internal;

import org.development.wide.world.spring.vault.jwks.property.KeyStoreProperties;
import org.development.wide.world.spring.vault.jwks.util.CharSequenceUtils;
import org.development.wide.world.spring.vault.jwks.util.KeyStoreUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class InternalKeyStore {

    private final String alias;
    private final char[] password;
    private final KeyStore keyStore;

    public InternalKeyStore(@NonNull final KeyStoreProperties properties) {
        this(KeyStoreUtils.getDefaultInstance(), properties);
    }

    public InternalKeyStore(@NonNull final KeyStore keyStore,
                            @NonNull final KeyStoreProperties properties) {
        Assert.notNull(keyStore, "keyStore cannot be null");
        Assert.notNull(properties, "properties cannot be null");
        final char[] passwordCharArray = CharSequenceUtils.toCharArray(properties.password());
        this.keyStore = KeyStoreUtils.initialize(keyStore, passwordCharArray);
        this.password = passwordCharArray;
        this.alias = properties.alias();
    }

    public KeyStore getKeyStore() {
        return this.keyStore;
    }

    private Key getKey() {
        try {
            return this.keyStore.getKey(this.alias, this.password);
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new IllegalStateException("Unable to get key from key store", e);
        }
    }

    public PrivateKey getPrivateKey() {
        return (PrivateKey) getKey();
    }

    public Certificate getCertificate() {
        try {
            return this.keyStore.getCertificate(this.alias);
        } catch (KeyStoreException e) {
            throw new IllegalStateException("Unable to get certificate fro key store", e);
        }
    }

    public X509Certificate getX509Certificate() {
        return (X509Certificate) getCertificate();
    }

    public InternalKeyStore reloadFromByteArray(@NonNull final byte[] sources) {
        final var byteArrayInputStream = new ByteArrayInputStream(sources);
        return reloadFromInputStream(byteArrayInputStream);
    }

    public InternalKeyStore reloadFromInputStream(@NonNull final InputStream inputStream) {
        try (inputStream) {
            this.keyStore.load(inputStream, this.password);
            return this;
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new IllegalStateException("Unable to load key store from input stream", e);
        }
    }

    public byte[] serializeToByteArray() {
        return serializeToByteArrayOutputStream()
                .toByteArray();
    }

    public ByteArrayOutputStream serializeToByteArrayOutputStream() {
        final var byteArrayOutputStream = new ByteArrayOutputStream();
        return serializeToByteArrayOutputStream(byteArrayOutputStream);
    }

    public ByteArrayOutputStream serializeToByteArrayOutputStream(
            @NonNull final ByteArrayOutputStream outputStream) {
        try (outputStream) {
            this.keyStore.store(outputStream, this.password);
            return outputStream;
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to serialize key store to output stream", e);
        }
    }

    public InternalKeyStore saveCertificate(@NonNull final PrivateKey privateKey,
                                            @NonNull final Certificate[] chain) {
        try {
            this.keyStore.setKeyEntry(this.alias, privateKey, this.password, chain);
            return this;
        } catch (KeyStoreException e) {
            throw new IllegalStateException("Unable to save certificate to key store", e);
        }
    }

}
