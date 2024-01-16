package org.development.wide.world.spring.redis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/resources")
public class DynamicRedisJwksResourceController {

    @GetMapping
    public String getResource() {
        final UUID uuid = UUID.randomUUID();
        return "Dynamic Redis JWKS Resource %s".formatted(uuid);
    }

}
