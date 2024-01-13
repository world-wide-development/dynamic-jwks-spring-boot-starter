package org.development.wide.world.spring.jwks.internal;

import com.nimbusds.jose.jwk.JWKSet;
import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.data.CertificateRotationData;
import org.development.wide.world.spring.jwks.data.JwkSetData;
import org.development.wide.world.spring.jwks.spi.CertificateIssuer;
import org.development.wide.world.spring.jwks.spi.CertificateRepository;
import org.development.wide.world.spring.jwks.spi.JwkSetRotationFunction;
import org.development.wide.world.spring.jwks.spi.JwksCertificateRotator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.time.Duration;

import static java.util.Optional.ofNullable;

/**
 * Default implementation of the {@link JwksCertificateRotator}
 *
 * @see JwkSetConverter
 * @see CertificateIssuer
 * @see CertificateRepository
 */
public class DefaultJwksCertificateRotator implements JwksCertificateRotator {

    public static final Integer INITIAL_VERSION = 0;

    private static final Logger logger = LoggerFactory.getLogger(DefaultJwksCertificateRotator.class);

    private final JwkSetConverter jwkSetConverter;
    private final CertificateIssuer certificateIssuer;
    private final CertificateRepository certificateRepository;

    public DefaultJwksCertificateRotator(@NonNull final JwkSetConverter jwkSetConverter,
                                         @NonNull final CertificateIssuer certificateIssuer,
                                         @NonNull final CertificateRepository certificateRepository) {
        this.jwkSetConverter = jwkSetConverter;
        this.certificateIssuer = certificateIssuer;
        this.certificateRepository = certificateRepository;
    }

    /**
     * @see JwksCertificateRotator#rotate(JwkSetRotationFunction)
     */
    @Override
    public JwkSetData rotate(@NonNull final JwkSetRotationFunction rotationFn) {
        final CertificateData certificateData = rotationFn.apply(this::rotateCertificateData);
        final JWKSet jwkSet = jwkSetConverter.convert(certificateData);
        return JwkSetData.builder()
                .certificateData(certificateData)
                .jwkSet(jwkSet)
                .build();
    }

    /* Private methods */
    private CertificateData rotateCertificateData(@NonNull final CertificateRotationData rotationData) {
        final String key = rotationData.key();
        final Duration rotateBefore = ofNullable(rotationData.rotateBefore())
                .orElse(Duration.ZERO);
        return certificateRepository.findOne(key).map(certificateData -> {
            if (certificateData.checkCertificateValidity(rotateBefore)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Certificate is valid and will be used");
                }
                return certificateData;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Certificate is invalid and will be immediately rotated");
            }
            final Integer lastVersion = certificateData.version();
            return rotateVersionedCertificateData(key, lastVersion);
        }).orElseGet(() -> rotateVersionedCertificateData(key, INITIAL_VERSION));
    }

    private CertificateData rotateVersionedCertificateData(@NonNull final String key, @NonNull final Integer version) {
        final CertificateData certificateData = certificateIssuer.issueOne().toBuilder()
                .version(version)
                .build();
        return certificateRepository.saveOne(key, certificateData);
    }

}
