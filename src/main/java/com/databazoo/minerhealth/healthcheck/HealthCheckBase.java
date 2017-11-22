package com.databazoo.minerhealth.healthcheck;

import java.util.Arrays;
import java.util.List;

import com.databazoo.minerhealth.executable.Executable;

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

    double temperature;

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
     * Check if driver can be used by getting GPU count.
     * This value is also stored to the Claymore health check instance.
     *
     * @return can driver be used?
     */
    @Override
    public boolean isSuitable() {
        Executable exec = new Executable(countGPUsQuery()).exec();
        int gpuCount = 0;
        boolean result = exec.getResultCode() == 0 &&
                exec.getOutputStd().matches("[0-9\\-]+") &&
                (gpuCount = Integer.parseInt(exec.getOutputStd())) > 0;
        if (gpuCount > 0) {
            HealthCheck.getClaymore().setGpuCount(gpuCount);
        }
        return result;
    }

    /**
     * Get command line arguments for detection of available GPUs.
     *
     * @return command line arguments
     */
    abstract String[] countGPUsQuery();

    /**
     * Get detected temperature.
     *
     * @return temperature as provided by the driver
     */
    public double getTemperature() {
        return temperature;
    }
}
