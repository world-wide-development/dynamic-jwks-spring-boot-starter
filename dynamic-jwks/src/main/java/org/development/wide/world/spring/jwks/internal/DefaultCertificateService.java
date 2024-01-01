package org.development.wide.world.spring.jwks.internal;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.development.wide.world.spring.jwks.spi.CertificateService;
import org.development.wide.world.spring.jwks.util.CertificateUtils;
import org.development.wide.world.spring.jwks.util.KeyPairUtils;
import org.springframework.lang.NonNull;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class DefaultCertificateService implements CertificateService {

    @Override
    public KeyPair generateKeyPair() {
        return KeyPairUtils.generate2048RsaKeyPair();
    }

    @Override
    public BigInteger generateSerialFromUuid() {
        return CertificateUtils.generateSerialFromUuid();
    }

    @Override
    public String convertBigIntToHexDecimalString(final BigInteger bigInteger) {
        return CertificateUtils.bigIntToHexDecimalString(bigInteger);
    }

    @Override
    public X509Certificate convertCertificate(final X509CertificateHolder x509CertificateHolder) {
        try {
            return new JcaX509CertificateConverter().getCertificate(x509CertificateHolder);
        } catch (CertificateException e) {
            throw new IllegalStateException("Unable to issue a certificate", e);
        }
    }

    @Override
    public ContentSigner instantiateContentSigner(@NonNull final KeyPair keyPair, final String signatureAlgorithm) {
        try {
            return new JcaContentSignerBuilder(signatureAlgorithm).build(keyPair.getPrivate());
        } catch (OperatorCreationException e) {
            throw new IllegalStateException("Unable to build content signer", e);
        }
    }

}
