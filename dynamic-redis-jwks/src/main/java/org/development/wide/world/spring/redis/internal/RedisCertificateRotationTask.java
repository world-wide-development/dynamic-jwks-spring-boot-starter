package org.development.wide.world.spring.redis.internal;

import org.development.wide.world.spring.jwks.spi.CertificateRotationTask;
import org.development.wide.world.spring.jwks.spi.JwkSetDataHolder;
import org.development.wide.world.spring.redis.property.CertificateRotationInternalProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.support.locks.LockRegistry;

import java.time.Duration;

public class RedisCertificateRotationTask implements CertificateRotationTask {

    private static final Logger logger = LoggerFactory.getLogger(RedisCertificateRotationTask.class);

    private final LockRegistry lockRegistry;
    private final JwkSetDataHolder jwkSetDataHolder;
    private final CertificateRotationInternalProperties rotationProperties;

    public RedisCertificateRotationTask(final LockRegistry lockRegistry,
                                        final JwkSetDataHolder jwkSetDataHolder,
                                        final CertificateRotationInternalProperties rotationProperties) {
        this.lockRegistry = lockRegistry;
        this.jwkSetDataHolder = jwkSetDataHolder;
        this.rotationProperties = rotationProperties;
    }

    @Override
    public void run() {
        final String lockKey = rotationProperties.rotationLockKey();
        final Duration rotateBefore = rotationProperties.rotateBefore();
        if (logger.isTraceEnabled()) {
            logger.trace("Run JWKS certificate rotation task {} {}", lockKey, rotateBefore);
        }
        try {
            lockRegistry.executeLocked(lockKey, () -> jwkSetDataHolder.rotateInAdvanceIfAny(rotateBefore));
        } catch (Exception e) {
            logger.error("Periodic JWKS certificate rotation failure", e);
            Thread.currentThread().interrupt();
        }
    }

}
