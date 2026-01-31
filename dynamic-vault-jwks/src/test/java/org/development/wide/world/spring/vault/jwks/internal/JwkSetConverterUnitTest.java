package org.development.wide.world.spring.vault.jwks.internal;

import com.nimbusds.jose.jwk.JWKSet;
import core.base.BaseUnitTest;
import org.assertj.core.api.BDDAssertions;
import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.internal.JwkSetConverter;
import org.junit.jupiter.api.Test;

import static org.development.wide.world.spring.vault.jwks.internal.VaultJwksCertificateRotatorUnitTestData.extractExpiredKeyStoreData;

class JwkSetConverterUnitTest extends BaseUnitTest {

    final JwkSetConverter converter = new JwkSetConverter();

    @Test
    void testConvertSuccess() {
        // Given
        final CertificateData givenCertificateData = extractExpiredKeyStoreData();
        // When
        final JWKSet result = converter.convert(givenCertificateData);
        // Then
        BDDAssertions.then(result).isNotNull();
    }

    @Test
    void testConvertNull() {
        // When
        final JWKSet result = converter.convert(null);
        // Then
        BDDAssertions.then(result).isNull();
    }

}
