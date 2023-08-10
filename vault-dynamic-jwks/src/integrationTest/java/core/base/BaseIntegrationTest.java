package core.base;

import org.springframework.lang.NonNull;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.vault.VaultContainer;

@Testcontainers
public abstract class BaseIntegrationTest {

    static final String VAULT_TOKEN = "vault-test-token";
    @SuppressWarnings("resource")
    static final VaultContainer<?> vaultContainer = new VaultContainer<>("hashicorp/vault")
            .withInitCommand("secrets enable pki")
            .withInitCommand("write pki/root/generate/internal common_name='root.certificate' ttl=87600h")
            .withInitCommand("write pki/roles/jwks allow_any_name=true max_ttl=72h")
            .withInitCommand("secrets enable -path=vault-dynamic-jwks-spring-boot-starter kv-v2")
            .withVaultToken(VAULT_TOKEN)
            .withReuse(Boolean.TRUE);

    static {
        vaultContainer.start();
    }

    @DynamicPropertySource
    static void setup(@NonNull final DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.vault.token", () -> VAULT_TOKEN);
        registry.add("spring.cloud.vault.uri", vaultContainer::getHttpHostAddress);
    }

}
