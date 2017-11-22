package com.databazoo.minerhealth.healthcheck;

import java.util.*;

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
        Executable exec = new Executable(countGPUsQuery()).exec();
        int gpuCount = 0;
        boolean result = exec.getResultCode() == 0 &&
                exec.getOutputStd().matches("[0-9\\-]+") &&
                (gpuCount = Integer.parseInt(exec.getOutputStd())) > 0;
        if (gpuCount > 0) {
            HealthCheck.getClaymore().setGpuCount(gpuCount);
        }
        return result;
    }

    /**
     * Individual driver implementation requirement.
     */
    @Override
    public void check() {
        Executable exec = new Executable(countTemperatureQuery()).exec();
        if (exec.getResultCode() == 0) {
            temperature = Integer.parseInt(exec.getOutputStd());
        } else {
            throw new IllegalStateException("Reading highest temperature failed.");
        }
    }

    @Override
    public void updateFans() {
        for (int i = 0; i < HealthCheck.getClaymore().getGpuCount(); i++) {
            Executable exec = new Executable(countTemperatureQuery(i)).exec();
            if (exec.getResultCode() == 0) {
                int temp = Integer.parseInt(exec.getOutputStd());
                if (temp > TEMPERATURE_HIGHEST) {
                    setFanSpeed(i, TEMPERATURE_RPM_MAP.get(TEMPERATURE_HIGHEST));
                } else if (temp < TEMPERATURE_LOWEST) {
                    if(temp > 10) {
                        setFanSpeed(i, 0);
                    }
                } else {
                    setFanSpeed(i, TEMPERATURE_RPM_MAP.get(temp));
                }
            } else {
                MinerHealth.LOGGER.severe("Reading temperature failed for GPU " + i);
            }
        }
    }

    private void setFanSpeed(int gpuNumber, Integer rpm) {
        Executable exec = new Executable(setFanSpeedQuery(gpuNumber, rpm)).exec();
        if (exec.getResultCode() != 0) {
            throw new IllegalStateException("Setting fan speed failed for GPU " + gpuNumber);
        }
    }

    /**
     * Get command line arguments for detection of available GPUs.
     *
     * @return command line arguments
     */
    abstract String[] countGPUsQuery();

    /**
     * Get command line arguments for detection of temperature.
     *
     * @return command line arguments
     */
    abstract String[] countTemperatureQuery();

    /**
     * Get command line arguments for detection of temperature.
     *
     * @param gpuNumber zero-based GPU number
     * @return command line arguments
     */
    abstract String[] countTemperatureQuery(int gpuNumber);

    /**
     * Get command line arguments to set fan speed.
     *
     * @param gpuNumber zero-based GPU number
     * @param rpm desired fan output (percent)
     * @return command line arguments
     */
    abstract String[] setFanSpeedQuery(int gpuNumber, Integer rpm);

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
