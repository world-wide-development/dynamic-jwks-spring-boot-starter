package org.development.wide.world.spring.jwks.data;

import core.base.BaseUnitTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class KeyStoreSourceUnitTest extends BaseUnitTest {

    @Test
    void testToString() {
        // Given
        final byte[] givenKeyStoreSources = {1, 22, 35};
        final KeyStoreSource givenKeyStoreSource = KeyStoreSource.builder()
                .keyStoreSources(givenKeyStoreSources)
                .serialNumber("serial-number")
                .build();
        // When
        final String result = givenKeyStoreSource.toString();
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isBlank());
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        final var verifier = EqualsVerifier.simple()
                .forClass(KeyStoreSource.class);
        // Expect
        verifier.verify();
    }

}
