package org.development.wide.world.spring.jwks.internal;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.property.BCCertificateInternalProperties;
import org.development.wide.world.spring.jwks.spi.CertificateIssuer;
import org.development.wide.world.spring.jwks.util.CertificateUtils;
import org.development.wide.world.spring.jwks.util.KeyPairUtils;
import org.springframework.lang.NonNull;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.cert.CertificateException;
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

    private final BCCertificateInternalProperties properties;

    public BouncyCastleCertificateIssuer(final BCCertificateInternalProperties properties) {
        this.properties = properties;
    }

    @Override
    public CertificateData issueOne() {
        final KeyPair keyPair = KeyPairUtils.generate2048RsaKeyPair();
        final BigInteger serial = CertificateUtils.generateSerialFromUuid();
        final ContentSigner contentSigner = this.instantiateContentSigner(keyPair);
        final X509v3CertificateBuilder x509v3CertificateBuilder = this.instantiateX509CertificateBuilder(keyPair, serial);
        final X509CertificateHolder x509CertificateHolder = x509v3CertificateBuilder.build(contentSigner);
        final X509Certificate x509Certificate = this.convertCertificate(x509CertificateHolder);
        final String hexSerial = CertificateUtils.bigIntToHexDecimalString(serial);
        return CertificateData.builder()
                .x509Certificates(Collections.emptyList())
                .x509Certificate(x509Certificate)
                .privateKey(keyPair.getPrivate())
                .serialNumber(hexSerial)
                .build();
    }

    /* Private methods */
    private ContentSigner instantiateContentSigner(@NonNull final KeyPair keyPair) {
        try {
            return new JcaContentSignerBuilder(SHA_512_WITH_RSA).build(keyPair.getPrivate());
        } catch (OperatorCreationException e) {
            throw new IllegalStateException("Unable to build content signer", e);
        }
    }

    private X509Certificate convertCertificate(final X509CertificateHolder x509CertificateHolder) {
        try {
            return new JcaX509CertificateConverter().getCertificate(x509CertificateHolder);
        } catch (CertificateException e) {
            throw new IllegalStateException("Unable to issue a certificate", e);
        }
    }

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
