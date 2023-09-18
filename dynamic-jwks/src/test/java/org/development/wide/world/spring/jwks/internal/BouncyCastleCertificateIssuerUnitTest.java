package org.development.wide.world.spring.jwks.internal;

import core.base.BaseUnitTest;
import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.property.BCCertificateInternalProperties;
import org.development.wide.world.spring.jwks.spi.CertificateIssuer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

class BouncyCastleCertificateIssuerUnitTest extends BaseUnitTest {

    static final BCCertificateInternalProperties CERTIFICATE_PROPERTIES = BCCertificateInternalProperties.builder()
            .certificateTtl(Duration.ofMinutes(1))
            .subject("TestSubject")
            .issuer("TestIssuer")
            .build();

    CertificateIssuer certificateIssuer = new BouncyCastleCertificateIssuer(CERTIFICATE_PROPERTIES);

    @Test
    void issueOne() {
        final CertificateData certificateData = certificateIssuer.issueOne();
        Assertions.assertNotNull(certificateData);
    }

}
