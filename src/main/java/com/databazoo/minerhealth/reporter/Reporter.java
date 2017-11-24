package com.databazoo.minerhealth.reporter;

import com.databazoo.components.UIConstants;
import com.databazoo.minerhealth.config.Config;
import com.databazoo.minerhealth.executable.Executable;
import com.databazoo.minerhealth.healthcheck.HealthCheck;
import com.databazoo.minerhealth.healthcheck.HealthCheckClaymore;

/**
 * Sends data to server and validates responses.
 */
public class Reporter {

    /**
     * The usual "All up" report with all metrics included.
     *
     * If there is no validation on server (like performance and temperature criteria) this method checks the values locally with
     * {@link #validateMetrics(int, double, double, double)}.
     *
     * @param driver HealthCheck driver that completed the last check
     * @param claymore Claymore log parser that completed the last check
     */
    public void reportToServer(HealthCheck driver, HealthCheckClaymore claymore) {
        final int gpuCount = claymore.getGpuCount();
        final double temperature = driver.getTemperature();
        final double performance = claymore.getPerformance();
        final double performancePerGPU = claymore.getPerformancePerGPU();

        validateResponse(
                Report.up(Config.getClientID(), Config.getMachineName(),
                        gpuCount,
                        temperature,
                        performance
                ).send()
        );

        validateMetrics(gpuCount, temperature, performance, performancePerGPU);
    }

    /**
     * Reports the machine start (well, actually the application start).
     */
    public void reportStart() {
        Report
                .start(Config.getClientID(), Config.getMachineName())
                .send();
    }

    /**
     * Manual validation of metrics in case we did not receive OVERHEAT or UNDERPERFORM from server directly.
     *
     * @param gpuCount detected count of GPUs
     * @param temperature detected temperature
     * @param performance detected performance
     * @param performancePerGPU detected performance per GPU
     */
    private void validateMetrics(int gpuCount, double temperature, double performance, double performancePerGPU) {
    }

    /**
     * Check for actions in response and parse config if sent.
     *
     * @param response the whole response string
     */
    private void validateResponse(String response) {
        if (response.equalsIgnoreCase("REMOTE_RESTART") && Config.isRemoteReboot()) {
            restart();
        } else if (response.equalsIgnoreCase("OVERHEAT") || response.equalsIgnoreCase("UNDERPERFORM")) {
            restart();
        } else if (response.startsWith("CONFIG")) {
            config(response.split("\\s+"));
        }
    }

    /**
     * Parse config constants, pretty much KEY-VALUE pairs for known keys. Everything else is ignored.
     *
     * @param args individual arguments
     */
    private void config(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
            case "minTemp":
                i++;
                if (i < args.length) {
                    Config.setMinTemp(Double.parseDouble(args[i]));
                }
                break;
            case "maxTemp":
                i++;
                if (i < args.length) {
                    Config.setMaxTemp(Double.parseDouble(args[i]));
                }
                break;
            case "minPerformance":
                i++;
                if (i < args.length) {
                    Config.setMinPerformance(Double.parseDouble(args[i]));
                }
                break;
            case "minPerformancePerGPU":
                i++;
                if (i < args.length) {
                    Config.setMinPerformancePerGPU(Double.parseDouble(args[i]));
                }
                break;
            }
        }
    }

    /**
     * Report and perform a reboot.
     */
    private void restart() {
        Report
                .restart(Config.getClientID(), Config.getMachineName())
                .send();
        if (UIConstants.isWindows()) {
            throw new IllegalStateException("Windows not supported yet.");
        } else {
            if (new Executable("reboot").exec().getResultCode() != 0) {
                throw new IllegalStateException("Could not reboot. Check permissions on `reboot` command.");
            }
        }
    }
}
