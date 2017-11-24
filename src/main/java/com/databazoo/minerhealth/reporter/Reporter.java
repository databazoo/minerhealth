package com.databazoo.minerhealth.reporter;

import com.databazoo.components.UIConstants;
import com.databazoo.minerhealth.MinerHealth;
import com.databazoo.minerhealth.config.Config;
import com.databazoo.minerhealth.executable.Executable;
import com.databazoo.minerhealth.healthcheck.HealthCheck;
import com.databazoo.minerhealth.healthcheck.HealthCheckClaymore;

/**
 * Sends data to server and validates responses.
 */
public class Reporter {

    private int temperature;
    private double performance;
    private double performancePerGPU;
    private int temperatureRecheckAttempt;
    private int performanceRecheckAttempt;

    /**
     * The usual "All up" report with all metrics included.
     *
     * If there is no validation on server (like performance and temperature criteria) this method checks the values locally with
     * {@link #validateMetrics(double, double, double)}.
     *
     * @param driver HealthCheck driver that completed the last check
     * @param claymore Claymore log parser that completed the last check
     */
    public void reportToServer(HealthCheck driver, HealthCheckClaymore claymore) {
        int gpuCount = claymore.getGpuCount();
        temperature = driver.getTemperature();
        performance = claymore.getPerformance();
        performancePerGPU = claymore.getPerformancePerGPU();

        boolean responseOK = validateResponse(
                Report.up(Config.getClientID(), Config.getMachineName(),
                        gpuCount,
                        temperature,
                        performance
                ).send()
        );

        boolean metricsOK = validateMetrics(temperature, performance, performancePerGPU);

        if (responseOK && metricsOK) {
            resetAttempts();
        }
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
     * @param temperature detected temperature
     * @param performance detected performance
     * @param performancePerGPU detected performance per GPU
     * @return is all valid?
     */
    boolean validateMetrics(double temperature, double performance, double performancePerGPU) {
        if (Config.getMinTemp() > 0 && temperature < Config.getMinTemp()) {
            overheat();
        } else if (Config.getMaxTemp() > 0 && temperature > Config.getMaxTemp()) {
            overheat();
        } else if (Config.getMinPerformance() > 0 && performance < Config.getMinPerformance()) {
            underperform();
        } else if (Config.getMinPerformancePerGPU() > 0 && performancePerGPU < Config.getMinPerformancePerGPU()) {
            underperform();
        } else {
            return true;
        }
        return false;
    }

    /**
     * Check for actions in response and parse config if sent.
     *
     * @param response the whole response string
     * @return is all valid?
     */
    boolean validateResponse(String response) {
        if (response.equalsIgnoreCase("REMOTE_RESTART") && Config.isRemoteReboot()) {
            restart();
        } else if (response.equalsIgnoreCase("OVERHEAT")) {
            overheat();
        } else if (response.equalsIgnoreCase("UNDERPERFORM")) {
            underperform();
        } else if (response.startsWith("CONFIG")) {
            config(response.split("\\s+"));
            return true;
        } else {
            return true;
        }
        return false;
    }

    private void resetAttempts() {
        temperatureRecheckAttempt = 0;
        performanceRecheckAttempt = 0;
    }

    private void overheat() {
        temperatureRecheckAttempt++;
        if (temperatureRecheckAttempt > Config.getRecheckAttemptsLimit()) {
            MinerHealth.LOGGER.warning("Temperature limit breached at " + temperature + ". Will now reboot.");
            restart();
        } else {
            MinerHealth.LOGGER.warning("Temperature limit breached at " + temperature + ". Will recheck (attempt " + temperatureRecheckAttempt + ").");
        }
    }

    private void underperform() {
        performanceRecheckAttempt++;
        if (performanceRecheckAttempt > Config.getRecheckAttemptsLimit()) {
            MinerHealth.LOGGER.warning("Performance limit breached at " + performance + " (" + performancePerGPU + " per GPU). Will now reboot.");
            restart();
        } else {
            MinerHealth.LOGGER.warning("Performance limit breached at " + performance + " (" + performancePerGPU + " per GPU). Will recheck (attempt " + performanceRecheckAttempt + ").");
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
    void restart() {
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
