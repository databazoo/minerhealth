package com.databazoo.minerhealth.healthcheck;

import com.databazoo.minerhealth.config.Config;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Health Check interface. The entry point to the whole health check implementation is {@link #runChecks()}.
 *
 * @author boris
 */
public interface HealthCheck {

    /**
     * Check if driver can be used.
     *
     * @return can driver be used?
     */
    boolean isSuitable();

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
            HealthCheckBase.addDrivers(
                    new HealthCheckAMD(),
                    new HealthCheckNvidia()
            );
            return HealthCheckBase.findSuitableDriver();
        }
    }
}
