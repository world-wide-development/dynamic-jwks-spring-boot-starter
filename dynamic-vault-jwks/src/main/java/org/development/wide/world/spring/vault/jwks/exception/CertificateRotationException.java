package org.development.wide.world.spring.vault.jwks.exception;

public class CertificateRotationException extends RuntimeException {

    public CertificateRotationException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
