package org.development.wide.world.spring.vault.jwks.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.security.cert.X509Certificate;
import java.security.spec.KeySpec;
import java.util.List;

@SuppressWarnings({"unused", "java:S5993"})
public abstract class CertificateBundleJacksonMixIn {

    @JsonCreator
    public CertificateBundleJacksonMixIn(@JsonProperty("private_key") String privateKey,
                                         @JsonProperty("ca_chain") List<String> caChain,
                                         @JsonProperty("certificate") String certificate,
                                         @JsonProperty("serial_number") String serialNumber,
                                         @JsonProperty("issuing_ca") String issuingCaCertificate,
                                         @JsonProperty("private_key_type") String privateKeyType) {
    }

    @JsonIgnore
    public abstract KeySpec getPrivateKeySpec();

    @JsonIgnore
    public abstract String getRequiredPrivateKeyType();

    @JsonIgnore
    public abstract X509Certificate getX509Certificate();

    @JsonIgnore
    public abstract X509Certificate getX509IssuerCertificate();

    @JsonIgnore
    public abstract List<X509Certificate> getX509IssuerCertificates();

}
