package org.development.wide.world.spring.jwks.spi;

import org.development.wide.world.spring.jwks.data.CertificateData;

import java.util.Optional;

/**
 * The main contract defining the possibilities of interaction with the certificate repository
 *
 * @see CertificateData
 */
public interface CertificateRepository {

    /**
     * Searches for certificate data by given key
     *
     * @param key certificate identity
     * @return {@code Optional<CertificateData>} or {@link Optional#empty()}
     */
    Optional<CertificateData> findOne(String key);

    /**
     * Saves certificate
     *
     * @param key certificate identity
     * @param certificateData certificate data
     * @return {@code CertificateData}
     */
    CertificateData saveOne(String key, CertificateData certificateData);

}
