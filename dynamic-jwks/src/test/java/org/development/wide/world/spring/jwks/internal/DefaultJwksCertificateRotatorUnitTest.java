package org.development.wide.world.spring.jwks.internal;

import ch.qos.logback.classic.Level;
import core.base.BaseUnitTest;
import core.utils.LogbackUtils;
import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.data.CertificateRotationData;
import org.development.wide.world.spring.jwks.data.JwkSetData;
import org.development.wide.world.spring.jwks.property.BCCertificateInternalProperties;
import org.development.wide.world.spring.jwks.spi.CertificateIssuer;
import org.development.wide.world.spring.jwks.spi.CertificateRepository;
import org.development.wide.world.spring.jwks.spi.CertificateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.lang.NonNull;

import java.time.Duration;
import java.util.Optional;

@ExtendWith({MockitoExtension.class})
class DefaultJwksCertificateRotatorUnitTest extends BaseUnitTest {

    @Mock
    CertificateRepository certificateRepository;
    @Spy
    JwkSetConverter jwkSetConverter = new JwkSetConverter();
    @Spy
    CertificateService certificateService = new DefaultCertificateService();
    @Spy
    BCCertificateInternalProperties certificateProperties = BCCertificateInternalProperties.builder()
            .certificateTtl(Duration.ofDays(30))
            .subject("Given Subject")
            .issuer("Given Issuer")
            .build();
    @Spy
    CertificateIssuer certificateIssuer = new BouncyCastleCertificateIssuer(certificateService, certificateProperties);

    @InjectMocks
    DefaultJwksCertificateRotator certificateRotator;

    @BeforeEach
    void setUpEach() {
        LogbackUtils.changeLoggingLevel(Level.TRACE, DefaultJwksCertificateRotator.class);
    }

    @Test
    void testRotateValidCertificate() {
        // Given
        final int givenVersion = 3;
        final String givenKey = "given-key";
        final CertificateRotationData givenRotationData = CertificateRotationData.builder()
                .key(givenKey)
                .build();
        final CertificateData givenCertificateData = CertificateIssuerTestWrapper.of(Duration.ofDays(30)).issueOne()
                .toBuilder()
                .version(givenVersion)
                .build();
        BDDMockito.given(certificateRepository.findOne(givenKey)).willReturn(Optional.of(givenCertificateData));
        // When
        final JwkSetData result = certificateRotator.rotate(function -> function.apply(givenRotationData));
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.jwkSet());
        Assertions.assertEquals(givenCertificateData, result.certificateData());
        Assertions.assertNotNull(result.jwkSet().getKeyByKeyId(givenCertificateData.serialNumber()));
        // And
        BDDMockito.then(jwkSetConverter).should().convert(givenCertificateData);
        BDDMockito.then(certificateRepository).should().findOne(givenKey);
        BDDMockito.then(certificateIssuer).should(BDDMockito.never()).issueOne();
        BDDMockito.then(certificateRepository).should(BDDMockito.never()).saveOne(givenKey, givenCertificateData);
    }

    @Test
    void testRotateInvalidCertificate() {
        // Given
        final int givenVersion = 3;
        final String givenKey = "given-key";
        final CertificateRotationData givenRotationData = CertificateRotationData.builder()
                .key(givenKey)
                .build();
        final CertificateData givenCertificateData = CertificateIssuerTestWrapper.of(Duration.ZERO).issueOne()
                .toBuilder()
                .version(givenVersion)
                .build();
        BDDMockito.clearInvocations(certificateIssuer);
        ArgumentCaptor<CertificateData> rotatedCertificateData = ArgumentCaptor.forClass(CertificateData.class);
        BDDMockito.given(certificateRepository.findOne(givenKey)).willReturn(Optional.of(givenCertificateData));
        BDDMockito.given(certificateRepository.saveOne(BDDMockito.eq(givenKey), rotatedCertificateData.capture()))
                .willAnswer(invocation -> rotatedCertificateData.getValue());
        // When
        final JwkSetData result = certificateRotator.rotate(function -> function.apply(givenRotationData));
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.jwkSet());
        Assertions.assertEquals(rotatedCertificateData.getValue(), result.certificateData());
        Assertions.assertNotNull(result.jwkSet().getKeyByKeyId(rotatedCertificateData.getValue().serialNumber()));
        // And
        BDDMockito.then(jwkSetConverter).should().convert(rotatedCertificateData.getValue());
        BDDMockito.then(certificateRepository).should().findOne(givenKey);
        BDDMockito.then(certificateIssuer).should().issueOne();
        BDDMockito.then(certificateRepository).should().saveOne(givenKey, rotatedCertificateData.getValue());
    }

    static class CertificateIssuerTestWrapper {

        private final BouncyCastleCertificateIssuer issuer;

        private CertificateIssuerTestWrapper(final BouncyCastleCertificateIssuer issuer) {
            this.issuer = issuer;
        }

        @NonNull
        static CertificateIssuerTestWrapper of(final Duration certificateTtl) {
            final CertificateService certificateService = new DefaultCertificateService();
            final BCCertificateInternalProperties certificateProperties = BCCertificateInternalProperties.builder()
                    .certificateTtl(certificateTtl)
                    .subject("Given Subject")
                    .issuer("Given Issuer")
                    .build();
            final var issuer = new BouncyCastleCertificateIssuer(certificateService, certificateProperties);
            return new CertificateIssuerTestWrapper(issuer);
        }

        public CertificateData issueOne() {
            return issuer.issueOne();
        }

    }

}
