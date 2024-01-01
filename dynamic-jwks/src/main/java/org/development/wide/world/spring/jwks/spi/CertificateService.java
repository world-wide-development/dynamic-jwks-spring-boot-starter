package org.development.wide.world.spring.jwks.spi;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.cert.X509Certificate;

public interface CertificateService {

    KeyPair generateKeyPair();

    BigInteger generateSerialFromUuid();

    String convertBigIntToHexDecimalString(BigInteger bigInteger);

    X509Certificate convertCertificate(X509CertificateHolder x509CertificateHolder);

    ContentSigner instantiateContentSigner(KeyPair keyPair, String signatureAlgorithm);
}
