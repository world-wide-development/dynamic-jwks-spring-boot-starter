package org.development.wide.world.spring.vault.jwks.spi;

import org.springframework.vault.support.CertificateBundle;

public interface CertificateIssuer {

    CertificateBundle issueOne();

}
