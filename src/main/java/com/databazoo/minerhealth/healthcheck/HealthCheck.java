package com.databazoo.minerhealth.healthcheck;

import java.util.Timer;
import java.util.TimerTask;

import com.databazoo.minerhealth.config.Config;

/**
 * Health Check interface. The entry point to the whole health check implementation is {@link #runChecks()}.
 *
 * @author boris
 */
public interface HealthCheck {

    /**
     * Individual driver implementation requirement.
     */
    void check();

    /**
     * The entry point to the whole health check implementation
     */
    static void runChecks() {
        new Timer().schedule(new TimerTask() {

            @Override public void run() {
                getDriver().check();
            }
        }, Config.getReportInterval() * 1000);
    }

    /**
     * Get the cached driver or select a suitable one from available implementations.
     *
     * @return a HealthCheck driver instance
     */
    static HealthCheck getDriver() {
        HealthCheck driver = HealthCheckBase.getCachedDriver();
        if (driver != null) {
            return driver;
        } else {
            return HealthCheckBase.findSuitableDriver();
        }
    }
}
