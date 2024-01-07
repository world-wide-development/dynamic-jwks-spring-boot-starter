package org.development.wide.world.spring.jwks.spi;

import org.development.wide.world.spring.jwks.data.CertificateData;

import java.util.function.Function;

public interface JwkSetRotationFunction extends Function<CertificateRotationFunction, CertificateData> {
}
