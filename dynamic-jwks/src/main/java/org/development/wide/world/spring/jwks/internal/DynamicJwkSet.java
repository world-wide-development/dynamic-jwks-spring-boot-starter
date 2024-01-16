package org.development.wide.world.spring.jwks.internal;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.development.wide.world.spring.jwks.data.JwkSetData;
import org.development.wide.world.spring.jwks.spi.JwkSetDataHolder;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * Implementation of the {@link JWKSource}.
 * <br>
 * Designed to provide the ability to automatically rotate certificates.
 *
 * @see JWKSource<SecurityContext>
 * @see JwkSetDataHolder
 * @see JwkSetData
 */
public class DynamicJwkSet implements JWKSource<SecurityContext> {

    private final JwkSetDataHolder jwkSetDataHolder;

    public DynamicJwkSet(final JwkSetDataHolder jwkSetDataHolder) {
        this.jwkSetDataHolder = jwkSetDataHolder;
    }

    /**
     * Provides the list of the {@link JWK}s with valid certificates.
     * Automatically issue a new certificate in case of the current certificate expiration.
     * <p>
     * After certificate rotation, attempts to retain at least two keys,
     * the last one and the previous one, to cover the rotation period for all clients.
     *
     * @see JWKSource#get(JWKSelector, SecurityContext)
     */
    @Override
    public List<JWK> get(@NonNull final JWKSelector jwkSelector, final SecurityContext context) {
        final JwkSetData jwkSetData = jwkSetDataHolder.getActual();
        final JWKSet jwkSet = jwkSetData.jwkSet();
        final JWKMatcher jwkSelectorMatcher = jwkSelector.getMatcher();
        if (this.isSelectionForJwtEncoder(jwkSelectorMatcher)) {
            return jwkSelector.select(jwkSet).stream()
                    .findFirst()
                    .stream()
                    .toList();
        }
        return jwkSelector.select(jwkSet);
    }

    /* Private methods */

    /**
     * Signing/Encoding a JWT: get is invoked provided a JWKSelector that contains a JWK Matcher that
     * has the algorithm defined (recommended: RS256) and key use of sig (meaning that this key will be
     * used to for signature purposes).
     * When get is invoked in this context, it expects a list of JWKs, but the list has to be of unique JWKs based on
     * algorithm (only one RSA, only one EC, etc.).
     * So in your implementation, if you have multiple active signing keys of the same algorithm, pick one at
     * [random|round-robin|etc.] -- when the library asks for a key for signing, it only really cares about getting a
     * key, which one you provide doesn't matter, and it is up to your implementation.
     * After a key has been provided, its keyId (i.e., kid JOSE header) is appended to the JWT for later identification
     * in the verification stage, this is done under the hood.
     * <p>
     * <a href="https://github.com/spring-projects/spring-authorization-server/issues/447#issuecomment-934625999">
     * Link to Git Hub
     * </a>
     *
     * @param jwkSelectorMatcher given JWK matcher
     * @return {@code true} if selector contains attributes for JWK selection
     */
    private boolean isSelectionForJwtEncoder(@NonNull final JWKMatcher jwkSelectorMatcher) {
        return Objects.nonNull(jwkSelectorMatcher.getAlgorithms())
               && Objects.nonNull(jwkSelectorMatcher.getKeyTypes())
               && Objects.nonNull(jwkSelectorMatcher.getKeyUses());
    }

}
