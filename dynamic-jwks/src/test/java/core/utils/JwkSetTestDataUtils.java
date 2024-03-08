package core.utils;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.internal.BouncyCastleCertificateIssuer;
import org.development.wide.world.spring.jwks.internal.DefaultCertificateService;
import org.development.wide.world.spring.jwks.internal.JwkSetConverter;
import org.development.wide.world.spring.jwks.property.BCCertificateInternalProperties;
import org.development.wide.world.spring.jwks.spi.CertificateIssuer;
import org.development.wide.world.spring.jwks.spi.CertificateService;

import java.time.Duration;

public interface JwkSetTestDataUtils {

    JwkSetConverter jwkSetConverter = new JwkSetConverter();
    CertificateService certificateService = new DefaultCertificateService();
    BCCertificateInternalProperties bcProperties = BCCertificateInternalProperties.builder()
            .certificateTtl(Duration.ofHours(6))
            .subject("GivenSubject")
            .issuer("GivenIssuer")
            .build();
    CertificateIssuer certificateIssuer = new BouncyCastleCertificateIssuer(certificateService, bcProperties);

    static JWK issueJwk() {
        return issueJwkSet().getKeys().getFirst();
    }

    static JWKSet issueJwkSet() {
        final CertificateData certificateData = certificateIssuer.issueOne();
        return jwkSetConverter.convert(certificateData);
    }

}
