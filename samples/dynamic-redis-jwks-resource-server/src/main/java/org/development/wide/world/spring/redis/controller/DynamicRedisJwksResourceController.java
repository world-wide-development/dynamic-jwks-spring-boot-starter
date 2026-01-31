package org.development.wide.world.spring.redis.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/resources")
public class DynamicRedisJwksResourceController {

    private static final Logger log = LoggerFactory.getLogger(DynamicRedisJwksResourceController.class);

    @GetMapping
    public String getResource() {
        final UUID uuid = UUID.randomUUID();
        log.info("Retrieving resource with UUID: {}", uuid);
        return "Dynamic Redis JWKS Resource %s".formatted(uuid);
    }

}
