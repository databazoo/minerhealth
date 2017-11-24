package com.databazoo.minerhealth.healthcheck;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;

import com.databazoo.minerhealth.MinerHealth;
import com.databazoo.minerhealth.executable.Executable;

/**
 * Sub-frame of all health check drivers. Provides a cache for suitable driver instance, etc.
 *
 * @author boris
 */
abstract class HealthCheckBase implements HealthCheck {

    private static final Map<Integer, Integer> TEMPERATURE_RPM_MAP = new LinkedHashMap<>();
    private static final Integer TEMPERATURE_HIGHEST = 69;
    private static final Integer TEMPERATURE_LOWEST = 55;

    static {
        TEMPERATURE_RPM_MAP.put(TEMPERATURE_HIGHEST, 100);
        TEMPERATURE_RPM_MAP.put(68, 95);
        TEMPERATURE_RPM_MAP.put(67, 90);
        TEMPERATURE_RPM_MAP.put(66, 85);
        TEMPERATURE_RPM_MAP.put(65, 80);
        TEMPERATURE_RPM_MAP.put(64, 75);
        TEMPERATURE_RPM_MAP.put(63, 70);
        TEMPERATURE_RPM_MAP.put(62, 65);
        TEMPERATURE_RPM_MAP.put(61, 60);
        TEMPERATURE_RPM_MAP.put(60, 55);
        TEMPERATURE_RPM_MAP.put(59, 50);
        TEMPERATURE_RPM_MAP.put(58, 45);
        TEMPERATURE_RPM_MAP.put(57, 40);
        TEMPERATURE_RPM_MAP.put(56, 35);
        TEMPERATURE_RPM_MAP.put(TEMPERATURE_LOWEST, 30);
    }

    /**
     * Cache for suitable driver instance
     */
    private static HealthCheck cachedDriver;

    private static List<HealthCheck> availableDrivers;

    private int temperature;
    private int gpuCount;

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
            int[] tempValues = new int[HealthCheck.getClaymore().getGpuCount()];
            readTemperatures(exec, (currentGpu, gpuTemp) -> tempValues[currentGpu] = gpuTemp);

            for (int i = 0; i < tempValues.length; i++) {
                int temp = tempValues[i];
                if (temp > TEMPERATURE_HIGHEST) {
                    setFanSpeed(i, TEMPERATURE_RPM_MAP.get(TEMPERATURE_HIGHEST));
                } else if (temp < TEMPERATURE_LOWEST) {
                    if(temp > 10) {
                        setFanSpeed(i, 0);
                    }
                } else {
                    setFanSpeed(i, TEMPERATURE_RPM_MAP.get(temp));
                }
            }
        } else {
            MinerHealth.LOGGER.severe("Reading temperature failed.");
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
     * Set fan speed.
     *
     * @param gpuNumber GPU number (zero-based)
     * @param rpm 0-100%
     */
    private void setFanSpeed(int gpuNumber, Integer rpm) {
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
}
