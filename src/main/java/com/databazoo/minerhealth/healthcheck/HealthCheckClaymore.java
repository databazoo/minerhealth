package com.databazoo.minerhealth.healthcheck;

public class HealthCheckClaymore implements HealthCheck {

    private int gpuCount;
    private double performance;

    public int getGpuCount() {
        return gpuCount;
    }

    public void setGpuCount(int gpuCount) {
        this.gpuCount = gpuCount;
    }

    public double getPerformance() {
        return performance;
    }

    public double getPerformancePerGPU() {
        return performance / gpuCount * 1.0;
    }

    @Override public boolean isSuitable() {
        return true;
    }

    @Override public void check() {
        // TODO
        performance = 0;
    }

    /**
     * Update fan speed (if allowed). Individual driver implementation requirement.
     */
    @Override
    public void updateFans() {

    }

    /**
     * Get detected temperature.
     *
     * @return temperature as provided by the driver
     */
    @Override
    public int getTemperature() {
        return 0;
    }
}
