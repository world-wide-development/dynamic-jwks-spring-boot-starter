package org.development.wide.world.spring.jwks.internal;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.property.BCCertificateInternalProperties;
import org.development.wide.world.spring.jwks.spi.CertificateIssuer;
import org.development.wide.world.spring.jwks.spi.CertificateService;
import org.springframework.lang.NonNull;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;

/**
 * BouncyCastle-based implementation fo the {@link CertificateIssuer}
 *
 * @see CertificateIssuer
 */
public class BouncyCastleCertificateIssuer implements CertificateIssuer {

    public static final String X500_CN_PREFIX = "CN=";
    public static final String SHA_512_WITH_RSA = "SHA512withRSA";

    private final CertificateService certificateService;
    private final BCCertificateInternalProperties properties;

    public BouncyCastleCertificateIssuer(final CertificateService certificateService,
                                         final BCCertificateInternalProperties properties) {
        this.certificateService = certificateService;
        this.properties = properties;
    }

    @Override
    public CertificateData issueOne() {
        final KeyPair keyPair = certificateService.generateKeyPair();
        final BigInteger serial = certificateService.generateSerialFromUuid();
        final ContentSigner contentSigner = certificateService.instantiateContentSigner(keyPair, SHA_512_WITH_RSA);
        final X509v3CertificateBuilder x509v3CertificateBuilder = this.instantiateX509CertificateBuilder(keyPair, serial);
        final X509CertificateHolder x509CertificateHolder = x509v3CertificateBuilder.build(contentSigner);
        final X509Certificate x509Certificate = certificateService.convertCertificate(x509CertificateHolder);
        final String hexSerial = certificateService.convertBigIntToHexDecimalString(serial);
        return CertificateData.builder()
                .x509Certificates(Collections.emptyList())
                .x509Certificate(x509Certificate)
                .privateKey(keyPair.getPrivate())
                .serialNumber(hexSerial)
                .build();
    }

    /* Private methods */
    @NonNull
    private X509v3CertificateBuilder instantiateX509CertificateBuilder(@NonNull final KeyPair keyPair,
                                                                       @NonNull final BigInteger serial) {
        final Instant now = Instant.now();
        final Date notBefore = Date.from(now);
        final Date notAfter = Date.from(now.plus(properties.certificateTtl()));
        final X500Name issuer = new X500Name(X500_CN_PREFIX.concat(properties.issuer()));
        final X500Name subject = new X500Name(X500_CN_PREFIX.concat(properties.subject()));
        return new JcaX509v3CertificateBuilder(issuer, serial, notBefore, notAfter, subject, keyPair.getPublic());
    }

}
