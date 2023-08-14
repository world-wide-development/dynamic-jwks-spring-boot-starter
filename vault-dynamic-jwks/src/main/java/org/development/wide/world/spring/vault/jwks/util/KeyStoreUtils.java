package org.development.wide.world.spring.vault.jwks.util;

import org.springframework.lang.NonNull;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public final class KeyStoreUtils {

    public static final String DEFAULT_KEY_STORE_TYPE = KeyStore.getDefaultType();

    private KeyStoreUtils() {
        // Suppresses default constructor
    }

    public static PrivateKey getPrivateKey(final String alias,
                                           final char[] password,
                                           @NonNull final KeyStore keyStore) {
        try {
            return (PrivateKey) keyStore.getKey(alias, password);
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new IllegalStateException("Unable to get private key from key store", e);
        }
    }

    @NonNull
    public static KeyStore getDefaultInstance() {
        try {
            return KeyStore.getInstance(DEFAULT_KEY_STORE_TYPE);
        } catch (KeyStoreException e) {
            throw new IllegalStateException("Unable instantiate key store with default type", e);
        }
    }

    @NonNull
    public static KeyStore initialize(@NonNull final KeyStore keyStore, @NonNull final char[] password) {
        try {
            keyStore.load(() -> new KeyStore.PasswordProtection(password));
            return keyStore;
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new IllegalStateException("Unable initially load key store", e);
        }
    }

    public static PrivateKey getPrivateKey(final String alias,
                                           final CharSequence password,
                                           @NonNull final KeyStore keyStore) {
        final char[] passwordCharArray = CharSequenceUtils.toCharArray(password);
        return getPrivateKey(alias, passwordCharArray, keyStore);
    }

}
