package org.development.wide.world.spring.jwks.spi;

import org.development.wide.world.spring.jwks.data.CertificateData;

/**
 * Designed for {@link CertificateData} conversion
 *
 * @param <S> source Type
 * @see CertificateData
 */
public interface CertificateDataConverter<S> {

    /**
     * Converts source into {@link CertificateData}
     *
     * @param source for conversion
     * @return {@code CertificateData}
     */
    CertificateData convert(S source);

}
