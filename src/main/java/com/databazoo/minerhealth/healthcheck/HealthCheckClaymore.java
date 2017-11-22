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

    @Override public double getTemperature() {
        return 0;
    }
}
