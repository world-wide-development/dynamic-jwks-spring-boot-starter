package org.development.wide.world.spring.jwks.util;

import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.lang.NonNull;

/**
 * A set of utilities for {@link CharSequence} interaction
 *
 * @see JWKSet
 */
public final class CharSequenceUtils {

    private CharSequenceUtils() {
        // Suppresses default constructor
    }

    @NonNull
    public static char[] toCharArray(final CharSequence charSequence) {
        if (charSequence == null || charSequence.isEmpty()) {
            return new char[0];
        }
        final char[] charArray = new char[charSequence.length()];
        for (int i = 0; i < charSequence.length(); i++) {
            charArray[i] = charSequence.charAt(i);
        }
        return charArray;
    }

}
