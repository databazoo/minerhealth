package com.databazoo.minerhealth.healthcheck;

import java.util.Timer;
import java.util.TimerTask;

import com.databazoo.minerhealth.config.Config;

public interface HealthCheck {

    void check();

    static void runChecks() {
        new Timer().schedule(new TimerTask() {

            @Override public void run() {
                getDriver().check();
            }
        }, Config.getReportInterval() * 1000);
    }

    static HealthCheck getDriver() {
        HealthCheck driver = HealthCheckBase.getCachedDriver();
        if (driver != null) {
            return driver;
        } else {
            return HealthCheckBase.findSuitableDriver();
        }
    }
}
