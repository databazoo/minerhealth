package com.databazoo.minerhealth.healthcheck;

import com.databazoo.minerhealth.executable.Executable;

import java.util.Arrays;
import java.util.List;

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

    private static List<HealthCheck> availableDrivers;

    /**
     * Get HealthCheck driver instance
     */
    static synchronized HealthCheck getCachedDriver() {
        return cachedDriver;
    }

    /**
     * Select a suitable HealthCheck driver from available implementations
     *
     * @return a HealthCheck driver instance
     */
    static synchronized HealthCheck findSuitableDriver() {
        for (HealthCheck driver : availableDrivers) {
            if (driver.isSuitable()) {
                cachedDriver = driver;
                return driver;
            }
        }
        throw new IllegalStateException("No driver detected. You should be running AMD or nVidia original drivers.");
    }

    static void addDrivers(HealthCheck... drivers) {
        availableDrivers = Arrays.asList(drivers);
    }

    /**
     * Check if driver can be used.
     *
     * @return can driver be used?
     */
    @Override
    public boolean isSuitable() {
        Executable exec = new Executable(countGPUsQuery()).exec();
        return exec.getResultCode() == 0 &&
                exec.getOutputStd().matches("[0-9\\-]+") &&
                Integer.parseInt(exec.getOutputStd()) > 0;
    }

    /**
     * Get command line arguments for detection of available GPUs.
     *
     * @return command line arguments
     */
    abstract String[] countGPUsQuery();
}
