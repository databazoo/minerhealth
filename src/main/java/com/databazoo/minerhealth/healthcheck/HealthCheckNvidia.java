package com.databazoo.minerhealth.healthcheck;

import com.databazoo.components.UIConstants;

/**
 * nVidia implementation of health check driver.
 *
 * @author boris
 */
class HealthCheckNvidia extends HealthCheckBase {

    private static final String TEMPERATURES_LIN = " | grep -A1 GeForce | grep '%' | perl -pe 's/^.*?\\%.*?(\\d+).*$/\\1/;'";
    String sourceLin = "nvidia-smi";

    /**
     * Get command line arguments for detection of available GPUs.
     *
     * @return command line arguments
     */
    @Override
    String[] countGPUsQuery() {
        if (UIConstants.isWindows()) {
            throw new IllegalStateException("Windows not supported yet.");
        } else {
            return new String[] { "/bin/sh", "-c", sourceLin + " | grep GeForce | wc -l" };
        }
    }

    /**
     * Get command line arguments for detection of temperature.
     *
     * @return command line arguments
     */
    @Override
    String[] countTemperatureQuery() {
        if (UIConstants.isWindows()) {
            throw new IllegalStateException("Windows not supported yet.");
        } else {
            return new String[] { "/bin/sh", "-c", sourceLin + TEMPERATURES_LIN + " | sort | tail -1"};
        }
    }

    /**
     * Get command line arguments for detection of temperature.
     *
     * @param gpuNumber zero-based GPU number
     * @return command line arguments
     */
    @Override
    String[] countTemperatureQuery(int gpuNumber) {
        if (UIConstants.isWindows()) {
            throw new IllegalStateException("Windows not supported yet.");
        } else {
            return new String[] { "/bin/sh", "-c", sourceLin + TEMPERATURES_LIN + " | head -" + (gpuNumber + 1) + " | tail -1"};
        }
    }

    /**
     * Get command line arguments to set fan speed.
     *
     * @param gpuNumber zero-based GPU number
     * @param rpm       desired fan output (percent)
     * @return command line arguments
     */
    @Override
    String[] setFanSpeedQuery(int gpuNumber, Integer rpm) {
        if (UIConstants.isWindows()) {
            throw new IllegalStateException("Windows not supported yet.");
        } else {
            return new String[] { "/bin/sh", "-c", "nvidia-settings -a [gpu:" + gpuNumber + "]/GPUFanControlState=1 -a [fan:" + gpuNumber + "]/GPUTargetFanSpeed=" + rpm};
        }
    }
}
