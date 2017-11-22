package com.databazoo.minerhealth.healthcheck;

import com.databazoo.components.UIConstants;
import com.databazoo.minerhealth.config.Config;
import com.databazoo.minerhealth.executable.Executable;

/**
 * nVidia implementation of health check driver.
 *
 * @author boris
 */
class HealthCheckNvidia extends HealthCheckBase {

    /**
     * Individual driver implementation requirement.
     */
    @Override
    public void check() {
        if (Config.isFanControl()) {
            runFanCheck();
        }

        if (UIConstants.isWindows()) {
            throw new IllegalStateException("Windows not supported yet.");
        } else {
            Executable exec = new Executable("nvidia-smi", "|", "grep", "GeForce", "|", "wc", "-l").exec();
            if (exec.getResultCode() == 0) {
                temperature = Double.parseDouble(exec.getOutputStd());
            } else {
                throw new IllegalStateException("Reading temperature failed.");
            }
        }
    }

    private void runFanCheck() {
        for (int i = 0; i < HealthCheck.getClaymore().getGpuCount(); i++) {
            // TODO
        }
    }

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
            return new String[] { "nvidia-smi", "|", "grep", "GeForce", "|", "wc", "-l" };
        }
    }
}
