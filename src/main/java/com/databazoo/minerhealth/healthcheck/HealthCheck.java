package com.databazoo.minerhealth.healthcheck;

import java.util.Timer;
import java.util.TimerTask;

import com.databazoo.minerhealth.MinerHealth;
import com.databazoo.minerhealth.config.Config;
import com.databazoo.minerhealth.reporter.Reporter;

/**
 * Health Check interface. The entry point to the whole health check implementation is {@link #runChecks()}.
 *
 * @author boris
 */
public interface HealthCheck {

    HealthCheckClaymore CLAYMORE = new HealthCheckClaymore();
    Reporter REPORTER = new Reporter();

    /**
     * Check if driver can be used.
     *
     * @return can driver be used?
     */
    boolean isSuitable();

    /**
     * Check system temperature. Individual driver implementation requirement.
     */
    void check();

    /**
     * Update fan speed (if allowed). Individual driver implementation requirement.
     */
    void updateFans();

    /**
     * Get detected temperature.
     *
     * @return temperature as provided by the driver
     */
    int getTemperature();

    /**
     * The entry point to the whole health check implementation
     */
    static void runChecks() {
        new Thread(() -> getReporter().reportStart()).start();

        new Timer("Check Timer").schedule(new TimerTask() {

            @Override public void run() {
                try {
                    final HealthCheck driver = getDriver();
                    final HealthCheckClaymore claymore = getClaymore();

                    if (Config.isFanControl()) {
                        driver.updateFans();
                    }
                    driver.check();
                    claymore.check();

                    getReporter().reportToServer(driver, claymore);
                } catch (Exception ex) {
                    StringBuilder out = new StringBuilder("Checks failed: ").append(ex.getMessage());
                    for(StackTraceElement elem : ex.getStackTrace()){
                        out.append("\n").append(elem.toString());
                    }
                    MinerHealth.LOGGER.severe(out.toString());
                }
            }
        }, 0, Config.getReportInterval() * 1000);
    }

    static Reporter getReporter() {
        return REPORTER;
    }

    static HealthCheckClaymore getClaymore() {
        return CLAYMORE;
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
