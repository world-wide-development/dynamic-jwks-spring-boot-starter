package core.base;

import core.config.RedisTestcontainersConfiguration;
import org.springframework.context.annotation.Import;

@Import({RedisTestcontainersConfiguration.class})
public abstract class BaseIntegrationTest {
}
