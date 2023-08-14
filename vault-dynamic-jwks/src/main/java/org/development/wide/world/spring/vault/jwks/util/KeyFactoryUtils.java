package org.development.wide.world.spring.vault.jwks.util;

import org.springframework.lang.NonNull;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.util.Arrays;
import java.util.NoSuchElementException;

public final class KeyFactoryUtils {

    public static final String EC = "EC";
    public static final String RSA = "RSA";

    private KeyFactoryUtils() {
        // Suppresses default constructor
    }

    @NonNull
    public static KeyFactory getInstance(final String algorithm) {
        try {
            return KeyFactory.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No %s algorithm available".formatted(algorithm), e);
        }
    }

    public static PrivateKey extractPrivateKey(final KeySpec keySpec) {
        final KeyFactory keyFactory = KeyFactoryManager.instantiateByKeySpec(keySpec);
        try {
            return keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new IllegalStateException("Cannot extract private key from key spec", e);
        }
    }

    public enum KeyFactoryManager {

        EC_KEY_FACTORY(KeyFactoryUtils.getInstance(EC), PKCS8EncodedKeySpec.class),
        RSA_KEY_FACTORY(KeyFactoryUtils.getInstance(RSA), RSAPrivateKeySpec.class);

        private final KeyFactory keyFactory;
        private final Class<? extends KeySpec> keySpecClass;

        KeyFactoryManager(final KeyFactory keyFactory,
                          final Class<? extends KeySpec> keySpecClass) {
            this.keyFactory = keyFactory;
            this.keySpecClass = keySpecClass;
        }

        public static KeyFactory instantiateByKeySpec(@NonNull final KeySpec keySpec) {
            return instantiateByKeySpec(keySpec.getClass());
        }

        public static KeyFactory instantiateByKeySpec(final Class<? extends KeySpec> keySpecClass) {
            return Arrays.stream(values())
                    .filter(manager -> manager.keySpecClass().isAssignableFrom(keySpecClass))
                    .map(KeyFactoryManager::keyFactory)
                    .findAny()
                    .orElseThrow(() -> new NoSuchElementException("KeyFactory by %s not found".formatted(keySpecClass)));
        }

        public KeyFactory keyFactory() {
            return this.keyFactory;
        }

        public Class<? extends KeySpec> keySpecClass() {
            return this.keySpecClass;
        }

    }

}
