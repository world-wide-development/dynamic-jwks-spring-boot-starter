package core.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.testcontainers.containers.GenericContainer;

@TestConfiguration(proxyBeanMethods = false)
@EnableConfigurationProperties({RedisProperties.class})
public class RedisTestcontainersConfiguration {

    @Bean
    @ServiceConnection(name = "redis")
    public GenericContainer<?> redisContainer(@NonNull final RedisProperties properties) {
        final String redis7Alpine = "redis:7-alpine";
        try (final var redisContainer = new GenericContainer<>(redis7Alpine)) {
            return redisContainer.withExposedPorts(properties.getPort());
        }
    }

}
