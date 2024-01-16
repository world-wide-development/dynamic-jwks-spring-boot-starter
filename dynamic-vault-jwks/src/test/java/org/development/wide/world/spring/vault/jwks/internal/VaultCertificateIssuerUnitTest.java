package org.development.wide.world.spring.vault.jwks.internal;

import core.base.BaseUnitTest;
import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.vault.jwks.property.VaultPkiInternalProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.vault.core.VaultPkiOperations;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.CertificateBundle;
import org.springframework.vault.support.VaultCertificateResponse;

import java.time.Duration;

@ExtendWith({MockitoExtension.class})
@SuppressWarnings({"ResultOfMethodCallIgnored"})
class VaultCertificateIssuerUnitTest extends BaseUnitTest {

    @Mock
    VaultTemplate vaultTemplate;
    @Mock
    VaultPkiOperations pkiOperations;
    @Mock
    CertificateBundle certificateBundle;
    @Mock
    VaultCertificateDataConverter converter;
    @Mock
    VaultCertificateResponse vaultCertificateResponse;
    @Spy
    VaultPkiInternalProperties properties = VaultPkiInternalProperties.builder()
            .certificateCommonName("given-certificate-common-name")
            .certificateTtl(Duration.ofDays(3))
            .roleName("given-role-name")
            .rootPath("given-root-path")
            .build();

    VaultCertificateIssuer vaultCertificateIssuer;

    @BeforeEach
    void setUpEach() {
        vaultCertificateIssuer = new VaultCertificateIssuer(pkiOperations, properties, converter);
    }

    @Test
    void instantiateWithVaultTemplate() {
        // Given
        BDDMockito.given(vaultTemplate.opsForPki(properties.rootPath())).willReturn(pkiOperations);
        // Expect
        Assertions.assertDoesNotThrow(() -> new VaultCertificateIssuer(vaultTemplate, properties, converter));
        // And
        BDDMockito.then(vaultTemplate).should().opsForPki(properties.rootPath());
    }

    @Test
    void testIssueOneSuccess() {
        // Given
        final String givenRoleName = properties.roleName();
        final CertificateData givenCertificateData = CertificateData.builder()
                .serialNumber("given-serial-number")
                .version(3)
                .build();
        BDDMockito.given(pkiOperations.issueCertificate(BDDMockito.eq(givenRoleName), BDDMockito.any()))
                .willReturn(vaultCertificateResponse);
        BDDMockito.given(vaultCertificateResponse.getData()).willReturn(certificateBundle);
        BDDMockito.given(converter.convert(certificateBundle)).willReturn(givenCertificateData);
        // When
        final CertificateData result = vaultCertificateIssuer.issueOne();
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(givenCertificateData, result);
        // And
        BDDMockito.then(pkiOperations).should().issueCertificate(BDDMockito.eq(givenRoleName), BDDMockito.any());
        BDDMockito.then(vaultCertificateResponse).should().getData();
        BDDMockito.then(converter).should().convert(certificateBundle);
    }

    @Test
    void testIssueOneThrowsCertificateBundleCannotBeNullException() {
        // Given
        final String givenRoleName = properties.roleName();
        BDDMockito.given(pkiOperations.issueCertificate(BDDMockito.eq(givenRoleName), BDDMockito.any()))
                .willReturn(vaultCertificateResponse);
        BDDMockito.given(vaultCertificateResponse.getData()).willReturn(null);
        // Expect
        final IllegalStateException exception = Assertions
                .assertThrows(IllegalStateException.class, () -> vaultCertificateIssuer.issueOne());
        Assertions.assertEquals("Certificate bundle cannot be null", exception.getMessage());
        // And
        BDDMockito.then(pkiOperations).should().issueCertificate(BDDMockito.eq(givenRoleName), BDDMockito.any());
        BDDMockito.then(vaultCertificateResponse).should().getData();
        BDDMockito.then(converter).should(BDDMockito.never()).convert(certificateBundle);
    }

}
