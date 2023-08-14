package org.development.wide.world.spring.vault.jwks.spi;

import org.development.wide.world.spring.vault.jwks.data.KeyStoreData;
import org.springframework.vault.support.CertificateBundle;
import org.springframework.vault.support.Versioned.Version;

import java.util.Optional;

public interface KeyStoreKeeper {

    Optional<KeyStoreData> findOne(String path);

    KeyStoreData saveOne(String path, Version version, CertificateBundle certificateBundle);

}
