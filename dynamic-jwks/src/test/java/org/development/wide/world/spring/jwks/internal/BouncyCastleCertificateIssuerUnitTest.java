package org.development.wide.world.spring.jwks.internal;

import core.base.BaseUnitTest;
import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.property.BCCertificateInternalProperties;
import org.development.wide.world.spring.jwks.spi.CertificateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

@ExtendWith({MockitoExtension.class})
@SuppressWarnings({"ResultOfMethodCallIgnored"})
class BouncyCastleCertificateIssuerUnitTest extends BaseUnitTest {

    @Spy
    CertificateService certificateService = new DefaultCertificateService();
    @Spy
    BCCertificateInternalProperties certificateProperties = BCCertificateInternalProperties.builder()
            .certificateTtl(Duration.ofMinutes(1))
            .subject("TestSubject")
            .issuer("TestIssuer")
            .build();

    @InjectMocks
    BouncyCastleCertificateIssuer certificateIssuer;

    @Test
    void testIssueOne() {
        // When
        final CertificateData certificateData = certificateIssuer.issueOne();
        // Then
        Assertions.assertNotNull(certificateData);
        // And
        BDDMockito.then(certificateService).should().generateKeyPair();
        BDDMockito.then(certificateService).should().generateSerialFromUuid();
        BDDMockito.then(certificateService).should().instantiateContentSigner(BDDMockito.any(), BDDMockito.anyString());
        BDDMockito.then(certificateProperties).should().certificateTtl();
        BDDMockito.then(certificateProperties).should().subject();
        BDDMockito.then(certificateProperties).should().issuer();
        BDDMockito.then(certificateService).should().convertCertificate(BDDMockito.any());
        BDDMockito.then(certificateService).should().convertBigIntToHexDecimalString(BDDMockito.any());
    }

}
