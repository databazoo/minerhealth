package com.databazoo.minerhealth.healthcheck;

import com.databazoo.components.UIConstants;
import com.databazoo.minerhealth.executable.Executable;

public class HealthCheckClaymore {

    private int gpuCount;
    private double performance;

    public int getGpuCount() {
        return gpuCount;
    }

    void setGpuCount(int gpuCount) {
        this.gpuCount = gpuCount;
    }

    public double getPerformance() {
        return performance;
    }

    public double getPerformancePerGPU() {
        return performance / gpuCount * 1.0;
    }

    void check() {
        Executable exec = new Executable(countTotalOutputQuery()).exec();
        if (exec.getResultCode() == 0) {
            performance = Double.parseDouble(exec.getOutputStd());
        } else {
            throw new IllegalStateException("Reading Claymore log failed.");
        }
    }

    /**
     * Get command line arguments for detection of total output.
     *
     * @return command line arguments
     */
    private String[] countTotalOutputQuery() {
        if (UIConstants.isWindows()) {
            throw new IllegalStateException("Windows not supported yet.");
        } else {
            return new String[] { "/bin/sh", "-c", "ls -rt . | tail -1 | xargs grep Total | tail -1 | perl -pe 's/^.*?Total.*?(\\d+(\\.\\d+)?).*$/\\1/;'"};
        }
    }
}
