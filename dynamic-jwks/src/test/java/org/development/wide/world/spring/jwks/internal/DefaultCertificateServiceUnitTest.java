package org.development.wide.world.spring.jwks.internal;

import core.base.BaseUnitTest;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.cert.X509Certificate;

@ExtendWith({MockitoExtension.class})
@SuppressWarnings({"ResultOfMethodCallIgnored"})
class DefaultCertificateServiceUnitTest extends BaseUnitTest {

    @Mock
    KeyPair keyPair;
    @Mock
    X509CertificateHolder x509CertificateHolder;

    DefaultCertificateService service;

    @BeforeEach
    void setUpEach() {
        this.service = new DefaultCertificateService();
    }

    @Test
    void testGenerateSerialFormUuid() {
        // When
        final BigInteger result = service.generateSerialFromUuid();
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertNotEquals(BigInteger.ZERO, result);
    }

    @Test
    void testConvertBigIntToHexDecimalString() {
        // Given
        final BigInteger givenBigInteger = service.generateSerialFromUuid();
        // When
        final String result = service.convertBigIntToHexDecimalString(givenBigInteger);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isBlank());
    }

    @Test
    void testConvertCertificateSuccess() throws IOException {
        // Given
        final byte[] certificateEncoded = """
                -----BEGIN CERTIFICATE-----
                MIICtDCCAh2gAwIBAgIBADANBgkqhkiG9w0BAQ0FADB2MQswCQYDVQQGEwJ1czEN
                MAsGA1UECAwERGVtbzENMAsGA1UECgwERGVtbzENMAsGA1UEAwwERGVtbzENMAsG
                A1UEBwwERGVtbzENMAsGA1UECwwERGVtbzEcMBoGCSqGSIb3DQEJARYNZGVtb0Bt
                YWlsLmNvbTAgFw0yMzEyMjkyMDE4MjBaGA8zMDIzMDUwMTIwMTgyMFowdjELMAkG
                A1UEBhMCdXMxDTALBgNVBAgMBERlbW8xDTALBgNVBAoMBERlbW8xDTALBgNVBAMM
                BERlbW8xDTALBgNVBAcMBERlbW8xDTALBgNVBAsMBERlbW8xHDAaBgkqhkiG9w0B
                CQEWDWRlbW9AbWFpbC5jb20wgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBANQJ
                d2IaPIIEVUBd1YI6gBduFqs7BrE3/HoUhslM8QK5Nh/61oHv5syu2ieHpi0SekXm
                cWzz2y8J/+jZHlQJ7V3W67XCujg3kZ44X+DAR7OH4lAlr9sAHtBaQ/hf+ZEoT+Hw
                CnCL4wTOF2EwiQ5pJHjcJ74lPz/jLHA+x0jBIl19AgMBAAGjUDBOMB0GA1UdDgQW
                BBQ1tHONC9UoPPZ2Fpv6HOlnzw4W6DAfBgNVHSMEGDAWgBQ1tHONC9UoPPZ2Fpv6
                HOlnzw4W6DAMBgNVHRMEBTADAQH/MA0GCSqGSIb3DQEBDQUAA4GBACczdPQ8+Icz
                uYXBlpQ5RXMi3Hzox7+ktdOPGh4afY4NQNT/6nv2hZuyfHaYLpa68NC77vK4P7Bs
                IdKXWRTkukswKlnBc50skifQzjXMfucs8kmuoQfio1wahSkpDEQPNujyHRRgYNMG
                LV5DGAmKIk6zkM3knkekNK8XFXXrKpQh
                -----END CERTIFICATE-----
                """.getBytes(StandardCharsets.UTF_8);
        BDDMockito.given(x509CertificateHolder.getEncoded()).willReturn(certificateEncoded);
        // When
        final X509Certificate result = service.convertCertificate(x509CertificateHolder);
        // Then
        Assertions.assertNotNull(result);
        // And
        BDDMockito.then(x509CertificateHolder).should().getEncoded();
    }

    @Test
    void testConvertCertificateThrowUnableToIssueCertificateException() throws IOException {
        // Given
        final byte[] certificateEncoded = "Invalid certificate".getBytes(StandardCharsets.UTF_8);
        BDDMockito.given(x509CertificateHolder.getEncoded()).willReturn(certificateEncoded);
        // Expect
        final IllegalStateException exception = Assertions
                .assertThrows(IllegalStateException.class, () -> service.convertCertificate(x509CertificateHolder));
        Assertions.assertEquals("Unable to issue a certificate", exception.getMessage());
        // And
        BDDMockito.then(x509CertificateHolder).should().getEncoded();
    }

    @Test
    void testInstantiateContentSignerSuccess() {
        // Given
        final String givenAlgorithm = "SHA512withRSA";
        final KeyPair givenKeyPair = service.generateKeyPair();
        // When
        final ContentSigner result = service.instantiateContentSigner(givenKeyPair, givenAlgorithm);
        // Then
        Assertions.assertNotNull(result);
    }

    @Test
    void testInstantiateContentSignerThrowUnableToBuildContentSignerException() {
        // Given
        final String givenAlgorithm = "SHA512withRSA";
        // Expect
        final IllegalStateException exception = Assertions
                .assertThrows(IllegalStateException.class, () -> service.instantiateContentSigner(keyPair, givenAlgorithm));
        Assertions.assertEquals("Unable to build content signer", exception.getMessage());
        // And
        BDDMockito.then(keyPair).should().getPrivate();
    }

}
