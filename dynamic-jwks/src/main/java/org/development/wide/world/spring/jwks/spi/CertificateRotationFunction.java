package org.development.wide.world.spring.jwks.spi;

import org.development.wide.world.spring.jwks.data.CertificateData;
import org.development.wide.world.spring.jwks.data.CertificateRotationData;

import java.util.function.Function;

public interface CertificateRotationFunction extends Function<CertificateRotationData, CertificateData> {
}
