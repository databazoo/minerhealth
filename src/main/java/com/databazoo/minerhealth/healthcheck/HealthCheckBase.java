package com.databazoo.minerhealth.healthcheck;

/**
 * Sub-frame of all health check drivers. Provides a cache for suitable driver instance, etc.
 *
 * @author boris
 */
abstract class HealthCheckBase implements HealthCheck {

    /**
     * Cache for suitable driver instance
     */
    private static HealthCheck cachedDriver;

    /**
     * Get HealthCheck driver instance
     */
    public static synchronized HealthCheck getCachedDriver() {
        return cachedDriver;
    }

    /**
     * Select a suitable HealthCheck driver from available implementations
     *
     * @return a HealthCheck driver instance
     */
    public static synchronized HealthCheck findSuitableDriver() {
        throw new IllegalStateException("No driver detected. You should be running AMD or nVidia original drivers.");
    }
}
