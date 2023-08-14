package org.development.wide.world.spring.vault.jwks.spi;

import org.development.wide.world.spring.vault.jwks.data.KeyStoreData;
import org.development.wide.world.spring.vault.jwks.util.KeyStoreUtils;
import org.springframework.lang.NonNull;
import org.springframework.vault.support.CertificateBundle;
import org.springframework.vault.support.VaultCertificateResponse;
import org.springframework.vault.support.Versioned;
import org.springframework.vault.support.Versioned.Metadata;

import java.security.KeyStore;
import java.time.Instant;
import java.util.Collections;

public final class VaultJwksCertificateRotatorUnitTestData {

    private VaultJwksCertificateRotatorUnitTestData() {
        // Suppresses default constructor
    }

    @NonNull
    public static KeyStoreData extractExpiredKeyStoreData() {
        final String alias = "expired.key.store";
        final CharSequence password = "password";
        final CertificateBundle certificateBundle = extractExpiredCertificateBundle();
        final KeyStore keyStore = certificateBundle.createKeyStore(alias, password);
        return KeyStoreData.builder()
                .privateKey(KeyStoreUtils.getPrivateKey(alias, password, keyStore))
                .x509Certificate(certificateBundle.getX509Certificate())
                .serialNumber(certificateBundle.getSerialNumber())
                .version(Versioned.Version.from(1))
                .build();
    }

    @NonNull
    public static Metadata extractVersionedMetadata() {
        return Metadata.builder()
                .version(Versioned.Version.from(2))
                .destroyed(Boolean.FALSE)
                .createdAt(Instant.now())
                .build();
    }

    @NonNull
    public static CertificateBundle extractExpiredCertificateBundle() {
        return CertificateBundle.of(
                "49:b5:69:de:22:fa:f8:c0:dc:1b:ea:66:6a:d0:16:34:ba:d5:fd:50",
                "MIIDZjCCAk6gAwIBAgIUSbVp3iL6+MDcG+pmatAWNLrV/VAwDQYJKoZIhvcNAQE" +
                "LBQAwGzEZMBcGA1UEAxMQcm9vdC5jZXJ0aWZpY2F0ZTAeFw0yMzA4MDYxMzIwNT" +
                "laFw0yMzA4MDYxMzUyMjlaMCQxIjAgBgNVBAMTGWF1dGhvcml6YXRpb24uY2Vyd" +
                "GlmaWNhdGUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDI1mTXVmy7" +
                "p0PTTqJ/phlFjxvCb73FW/0KbV5IGN3iADhqMipiTL+CvooyZVnlMr6qkxMEP6p" +
                "aS+tbM60DhLlAFsmE369yaLR/9ayEPeN6QKbfqos4J1f/GjN3RPdOIyszT1f3lc" +
                "CKmBY5j4OdFAIPPa63neLe6ERIwjyzPrM44G/11AtGjfEPe3f7uVIqfuzDhIEoy" +
                "mxtUwxrdft5HZUNJni3s7kz6O+2mdOPaug5MZUBB3oZc055sbXgbgYHP85ItKGE" +
                "Bql31fsj2yFUF1yVWSbMtiVnt14hIDtQTcbZaJfqNeIH3//bC0S/oU0VnQsW9BT" +
                "8A3h27UHf+Moqp4T3AgMBAAGjgZgwgZUwDgYDVR0PAQH/BAQDAgOoMB0GA1UdJQ" +
                "QWMBQGCCsGAQUFBwMBBggrBgEFBQcDAjAdBgNVHQ4EFgQUKfd7A3G37BEGw402+" +
                "jTB2Fz6sw4wHwYDVR0jBBgwFoAUx/8Bb/OA17v442gyLFR44jx0nr8wJAYDVR0R" +
                "BB0wG4IZYXV0aG9yaXphdGlvbi5jZXJ0aWZpY2F0ZTANBgkqhkiG9w0BAQsFAAO" +
                "CAQEAgYyarRtplBl2MdJx98U7F8HEqz22Ib3/f+CqxzOqlvuEaBhBAoo6CUYzyX" +
                "jI+jNxce9Ng2g1ZktHMqH71tmKLt9L/9KG6o8CM3FLRBIzKpRWHmnIUJeIu4pEo" +
                "p0Pp5JTKA1tHd8ShUDjVI2pnRuPnIyyGmtOur9yQJaMwLvObjeQv8wf7SH+gy8Z" +
                "LTtXSezI2P2CFMZK55uDRJYgX7fq6gF3y4CYlOlb+k69KbP2i6VyxTB+5TpXZTm" +
                "yM0bZoBawaMgXHKCrs6PKVzrWQi7tt64r8v30t6kZUY7llLYu5t/wr03OskpVwg" +
                "SGK2Sp0fFjBKtVbHsU6VuFosdSakow1w==",
                "MIIDRTCCAi2gAwIBAgIUNQqEw22NNytqBhjZA7dSexoqhw8wDQYJKoZIhvcNAQEL" +
                "BQAwGzEZMBcGA1UEAxMQcm9vdC5jZXJ0aWZpY2F0ZTAeFw0yMzA4MDYxMzIwNTZa" +
                "Fw0yMzA5MDcxMzIxMjZaMBsxGTAXBgNVBAMTEHJvb3QuY2VydGlmaWNhdGUwggEi" +
                "MA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC+SJ+aQICj2wYy2VVnYD40lFip" +
                "JxbliGWqktFe73n2kJGS91K32Se99Sucn+0DYsn18/XYNm5WHTKoGRmem6qJs0UP" +
                "0HHeTPZvfZoLqHcxWPPup8Om/WgO0Bmp2nQsPhRxKj4mzmdl7hHjcyBNx50Dl/rS" +
                "p4kqn9uqHdrjD/iYKLWS3c6cDtO469/wPorm29gXMeyokk4AC1kk26RvxAzjhzE8" +
                "BRzm3xo1iHLszZQBSWF58d3xcwgqNhEWn+ikYuf+v9z007b6U2npRuiULtP7y+eL" +
                "3h4ixlqNWGg1Gj8eDp+NFRQtZiSm/rGHuIpZ1dWH39wXytd98o5s/QlMObmLAgMB" +
                "AAGjgYAwfjAOBgNVHQ8BAf8EBAMCAQYwDwYDVR0TAQH/BAUwAwEB/zAdBgNVHQ4E" +
                "FgQUx/8Bb/OA17v442gyLFR44jx0nr8wHwYDVR0jBBgwFoAUx/8Bb/OA17v442gy" +
                "LFR44jx0nr8wGwYDVR0RBBQwEoIQcm9vdC5jZXJ0aWZpY2F0ZTANBgkqhkiG9w0B" +
                "AQsFAAOCAQEAiPc0tuWB6wQVouZpP5p1hbM+y9pUcZU3nf4bwdunnKCF5usoOCi8" +
                "E/0COVz4K2vR3oz1jWHHIkBrGnCes6XL6x2OmJmcETTSJohXIqG1uY04rrNTEJvb" +
                "X2PpyixBa6MESxFa09Xj529Uzlt1oE0GDM29daCcaMgms0HRCnDGaRHblX85PAZj" +
                "dg8OUZDywuF5BMeB+voBPTNk14bbffB23o1+NdJcA7c79flyvGFoHqGGj1gZ5Uh5" +
                "eF5hGpMXT5fXS/TXyyCA9ilQiL5+KGSBqQ/W/DJqt57soi1gyWMDYvbNbGMpEa+S" +
                "6EgQAhyZVQzdL2YUh2McRI4GeTWQNcJu3Q==",
                "MIIEpAIBAAKCAQEAyNZk11Zsu6dD006if6YZRY8bwm+9xVv9Cm1eSBjd4gA4ajIq" +
                "Yky/gr6KMmVZ5TK+qpMTBD+qWkvrWzOtA4S5QBbJhN+vcmi0f/WshD3jekCm36qL" +
                "OCdX/xozd0T3TiMrM09X95XAipgWOY+DnRQCDz2ut53i3uhESMI8sz6zOOBv9dQL" +
                "Ro3xD3t3+7lSKn7sw4SBKMpsbVMMa3X7eR2VDSZ4t7O5M+jvtpnTj2roOTGVAQd6" +
                "GXNOebG14G4GBz/OSLShhAapd9X7I9shVBdclVkmzLYlZ7deISA7UE3G2WiX6jXi" +
                "B9//2wtEv6FNFZ0LFvQU/AN4du1B3/jKKqeE9wIDAQABAoIBAFpzlH8XQWlSb82f" +
                "QLhUylT1mv767HESeOhVUX0PFf9PMhlB9qzG/AmaXwtLci0zqYORMaNcORDp2Fn+" +
                "8BEBmZ0vphrd01qnpYNr1gLJDMZmj8F0QbdMoOkXl85DlU3vsOku9uNe4pSI4pmR" +
                "9Sitdi//C81OonraCMbsFAJ2XqliP0aJIqffiQvyNo+PObW2Oj1g20nuo03vB7o5" +
                "llMbJprE67IvwJa6/BE9eI23CsISXMje0YziRZ/jtTB2cTlra+264s1NByRpk0uu" +
                "wyFmvV3ut5BSAk9A8fphW8GsPPtgVgfvJAa5sCSJ/t4iCwSO1pVOBfO9m8deiFiV" +
                "nNqsDYECgYEA34cSR5HPBXf6HhaNeB0H19KQcXM8a7LgqE/kKC/+P1gaCI/OV3qq" +
                "kXqu1BnEIAul+ydzzYfowE3Rk2anQL8UHl2Gc/WSJMH/Mw5uH7ADR0FfeF1hBU3a" +
                "7MemSes61CHtnALb/xknQ/6Bg+2EVC8jnYJ8A9aV69dVjr4JGwyYa6cCgYEA5gN7" +
                "gC9DO0lHZFtspbozz/mQqPV6LrEPOVH92s7Z7xGLW8x8EHNx5dACXY3e6UPygq59" +
                "1MfEnBloQsYAEaR7tlyRSpfJ/4vABoUwX424S9MxqbmYwQf3SNV5nLetnxM7agRh" +
                "CJfM+D4lUm0mBGVUqS5udqSMDHV0hgErRjQDBjECgYEAlzF8ys40cqWBl+J3VFyG" +
                "lpRFwYdJwitfTFmloQ09c0k7arkiwAcn4nlSUgMPpwFaBvTbnpUkeRbqAEL6NJBK" +
                "onNuY3cioBxcawOCt4pN3V+5nOfEnVrZDznIH82toBlG6DUC65zu803t+veof2zX" +
                "MU42Zj46cCjaJVAjRpmRa5cCgYEAst7gauXWPcOVvkiOrC6qXkCwq4QCmU7eDPBj" +
                "Hoaf2hdlrWTO0ihE2beyLzjFsddCPzgc6JzVm6LyfoLlyNKF1mIUJtL1pkHyed+X" +
                "p6dZO7YoN9n6HZrpRf2lDieU4FEfcimnn/wUzLPfaEMzPlXiyZIvGxqJQqMQk6lT" +
                "Eswd0fECgYBQbxy7TWsqI6Oo3t/yytdjtovbKPSv5z+cyVq6t9qFM4koF/d4GWa9" +
                "7NhndEiJhe8wEG3+PzLOI94w3M5N0D7mZ9CwIGKQ4vK8j+Hu0Uvlxekjsaq6LvoY" +
                "BKA3bqg3hgALy7M1jfZizOIwYerGiN0zP/2dX5Me451VB3nrIDhkHg==",
                "rsa"
        );
    }

    @NonNull
    public static Versioned<CertificateBundle> extractExpiredVersionedCertificateBundle() {
        return Versioned.create(extractExpiredCertificateBundle(), Versioned.Version.from(1));
    }

    @NonNull
    public static VaultCertificateResponse extractExpiredVaultCertificateResponse() {
        final CertificateBundle certificateBundle = extractExpiredCertificateBundle();
        return extractVaultCertificateResponse(certificateBundle);
    }

    @NonNull
    public static VaultCertificateResponse extractVaultCertificateResponse(final CertificateBundle certificateBundle) {
        final VaultCertificateResponse certificateResponse = new VaultCertificateResponse();
        certificateResponse.setMetadata(Collections.emptyMap());
        certificateResponse.setAuth(Collections.emptyMap());
        certificateResponse.setData(certificateBundle);
        return certificateResponse;
    }

}
