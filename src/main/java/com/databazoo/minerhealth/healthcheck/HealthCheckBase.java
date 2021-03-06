package com.databazoo.minerhealth.healthcheck;

import com.databazoo.minerhealth.MinerHealth;
import com.databazoo.minerhealth.config.Config;
import com.databazoo.minerhealth.executable.Executable;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;

/**
 * Sub-frame of all health check drivers. Provides a cache for suitable driver instance, etc.
 *
 * @author boris
 */
abstract class HealthCheckBase implements HealthCheck {

    private static final int MIN_RPM = 30;

    /**
     * Cache for suitable driver instance
     */
    private static HealthCheck cachedDriver;

    private static List<HealthCheck> availableDrivers;

    private int temperature;
    private int gpuCount;
    private int fanRPM;

    /**
     * Get HealthCheck driver instance
     */
    static synchronized HealthCheck getCachedDriver() {
        return cachedDriver;
    }

    /**
     * Select a suitable HealthCheck driver from available implementations
     *
     * @return a HealthCheck driver instance
     */
    static synchronized HealthCheck findSuitableDriver() {
        for (HealthCheck driver : availableDrivers) {
            if (driver.isSuitable()) {
                cachedDriver = driver;
                return driver;
            }
        }
        throw new IllegalStateException("No driver detected. You should be running AMD or nVidia original drivers.");
    }

    /**
     * Saves the driver list. This is defined externally to prevent cyclic class reference.
     *
     * @param drivers driver list
     */
    static void addDrivers(HealthCheck... drivers) {
        availableDrivers = Arrays.asList(drivers);
    }

    /**
     * Check if driver can be used by getting GPU count.
     * This value is also stored to the Claymore health check instance.
     *
     * @return can driver be used?
     */
    @Override
    public boolean isSuitable() {
        Executable exec = new Executable(getTemperatureQuery()).exec();
        if (exec.getResultCode() == 0) {
            gpuCount = 0;
            HealthCheck.getClaymore().setGpuCount(10000);
            readTemperatures(exec, (currentGpu, gpuTemp) -> gpuCount++);
            HealthCheck.getClaymore().setGpuCount(gpuCount);
            return gpuCount > 0;
        } else {
            return false;
        }
    }

    /**
     * Check system temperature. Individual driver implementation requirement.
     */
    @Override
    public void check() {
        Executable exec = new Executable(getTemperatureQuery()).exec();
        if (exec.getResultCode() == 0) {
            temperature = 0;
            readTemperatures(exec, (currentGpu, gpuTemp) -> {
                if (gpuTemp > temperature) {
                    temperature = gpuTemp;
                }
            });
        } else {
            throw new IllegalStateException("Reading highest temperature failed.");
        }
    }

    /**
     * Update fan speed (if allowed). Individual driver implementation requirement.
     */
    @Override
    public void updateFans() {
        Executable exec = new Executable(getTemperatureQuery()).exec();
        if (exec.getResultCode() == 0) {
            fanRPM = 0;
            int[] tempValues = new int[HealthCheck.getClaymore().getGpuCount()];
            readTemperatures(exec, (currentGpu, gpuTemp) -> tempValues[currentGpu] = gpuTemp);

            for (int i = 0; i < tempValues.length; i++) {
                int temp = tempValues[i];
                int optimumRPM = getOptimumRPM(temp);

                // Make sure low temperature is not a mistake
                if(temp > 10) {
                    setFanSpeed(i, optimumRPM);
                }
            }
        } else {
            MinerHealth.LOGGER.severe("Reading temperature failed.");
        }
    }

    int getOptimumRPM(int temp) {
        if (temp < Config.getFan30Temp()) {
            return 0;
        } else if (temp >= Config.getFan100Temp()) {
            return 100;
        } else {
            double rpmPerDegree = (100.0 - MIN_RPM) / (Config.getFan100Temp() - Config.getFan30Temp());
            double degreesAboveMin = temp - Config.getFan30Temp();
            return (int) (rpmPerDegree * degreesAboveMin) + MIN_RPM;
        }
    }

    /**
     * Read lines from executed query and try to match it with tempMatcher. If a match is found notify the consumer of values.
     *
     * @param exec executed query
     * @param consumer consumer for current GPU number (zero-based) and temperature value
     */
    private void readTemperatures(Executable exec, BiConsumer<Integer, Integer> consumer) {
        String[] lines = exec.getOutputStd().split("\n");
        int currentGpu = 0;
        for (String line : lines) {
            if (currentGpu >= HealthCheck.getClaymore().getGpuCount()) {
                break;
            }
            Matcher matcher = getTempMatcher(line);
            if (matcher.find()) {
                consumer.accept(currentGpu, Integer.parseInt(matcher.replaceFirst("$1")));
                currentGpu++;
            }
        }
    }

    /**
     * Set fan speed. Also, store max fan RPM for reporting.
     *
     * @param gpuNumber GPU number (zero-based)
     * @param rpm 0-100%
     */
    void setFanSpeed(int gpuNumber, Integer rpm) {
        if (rpm > fanRPM) {
            fanRPM = rpm;
        }
        Executable exec = new Executable(setFanSpeedQuery(gpuNumber, rpm)).exec();
        if (exec.getResultCode() != 0) {
            throw new IllegalStateException("Setting fan speed failed for GPU " + gpuNumber);
        }
    }

    /**
     * Get command line arguments for detection of temperature.
     *
     * @return command line arguments
     */
    abstract String[] getTemperatureQuery();

    /**
     * Get command line arguments to set fan speed.
     *
     * @param gpuNumber zero-based GPU number
     * @param rpm desired fan output (percent)
     * @return command line arguments
     */
    abstract String[] setFanSpeedQuery(int gpuNumber, Integer rpm);

    /**
     * Get regexp matcher for temperature lines.
     *
     * Contract: match whole string with {@link Matcher#find()} and provide the temperature value in $1 for replacement.
     *
     * @param line individual line to match
     * @return instance of matcher
     */
    abstract Matcher getTempMatcher(String line);

    /**
     * Get detected temperature.
     *
     * @return temperature as provided by the driver
     */
    @Override
    public int getTemperature() {
        return temperature;
    }

    /**
     * Get fan RPM.
     *
     * @return highest fan RPM (percent)
     */
    @Override
    public int getFanRPM() {
        return fanRPM;
    }
}
