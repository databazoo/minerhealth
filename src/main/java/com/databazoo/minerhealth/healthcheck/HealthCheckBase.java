package com.databazoo.minerhealth.healthcheck;

abstract class HealthCheckBase implements HealthCheck {

    private static HealthCheck cachedDriver;

    public static synchronized HealthCheck getCachedDriver() {
        return cachedDriver;
    }

    public static synchronized HealthCheck findSuitableDriver() {
        throw new IllegalStateException("No driver detected. You should be running AMD or nVidia original drivers.");
    }
}
