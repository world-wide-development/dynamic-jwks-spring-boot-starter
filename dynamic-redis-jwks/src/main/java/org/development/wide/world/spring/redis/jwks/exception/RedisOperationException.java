package org.development.wide.world.spring.redis.jwks.exception;

public class RedisOperationException extends RuntimeException{

    public static final String DEFAULT_MESSAGE = "Unable to perform Redis operation";

    @SuppressWarnings("unused")
    public RedisOperationException() {
        super(DEFAULT_MESSAGE);
    }

    public RedisOperationException(String message) {
        super(message);
    }

}
