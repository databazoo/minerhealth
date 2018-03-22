package com.databazoo.minerhealth.healthcheck;

import com.databazoo.minerhealth.config.Config;
import org.junit.Test;

import java.util.regex.Matcher;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

public class HealthCheckBaseTest {


    @Test
    public void getOptimumRPM() throws Exception {
        HealthCheckBase driver = new HealthCheckBaseImpl();

        assertEquals(0, driver.getOptimumRPM(40));
        assertEquals(30, driver.getOptimumRPM(55));
        assertEquals(55, driver.getOptimumRPM(60));
        assertEquals(100, driver.getOptimumRPM(70));
    }

    @Test
    public void getOptimumRpmModified() throws Exception {
        Config.setFan100Temp(63);
        HealthCheckBase driver = new HealthCheckBaseImpl();

        assertEquals(0, driver.getOptimumRPM(40));
        assertEquals(30, driver.getOptimumRPM(55));
        assertEquals(73, driver.getOptimumRPM(60));
        assertEquals(100, driver.getOptimumRPM(70));
    }

    private static class HealthCheckBaseImpl extends HealthCheckBase {

        @Override
        String[] getTemperatureQuery() {
            return new String[0];
        }

        @Override
        String[] setFanSpeedQuery(int gpuNumber, Integer rpm) {
            return new String[0];
        }

        @Override
        Matcher getTempMatcher(String line) {
            return null;
        }
    }
}