package com.databazoo.minerhealth.reporter;

import com.databazoo.minerhealth.healthcheck.HealthCheck;
import com.databazoo.minerhealth.healthcheck.HealthCheckClaymore;

public class Reporter {

    public void reportToServer(HealthCheck driver, HealthCheckClaymore claymore) {
        final int gpuCount = claymore.getGpuCount();
        final double temperature = driver.getTemperature();
        final double performance = claymore.getPerformance();
        final double performancePerGPU = claymore.getPerformancePerGPU();

        // TODO: send and receive
    }
}
