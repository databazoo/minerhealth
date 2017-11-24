package com.databazoo.minerhealth.healthcheck;

import java.io.File;

import com.databazoo.components.UIConstants;
import com.databazoo.minerhealth.config.Config;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HealthCheckNvidiaTest {

    @Before
    public void setUp() throws Exception {
        Config.setLogDir(new File(new File(new File("target"), "test-classes"), "logs"));
    }

    @Test
    public void smokeTest() throws Exception {
        new HealthCheckNvidia().isSuitable();
    }

    @Test
    public void isSuitable() throws Exception {
        assertTrue(new HealthCheckNvidiaImpl(5).isSuitable());
        assertFalse(new HealthCheckNvidiaImpl(0).isSuitable());
    }

    @Test
    public void check() throws Exception {
        HealthCheckNvidiaImpl driver = new HealthCheckNvidiaImpl();
        driver.sourceLin = "cat ../nvidia-smi.out";
        driver.isSuitable();
        driver.check();
        assertEquals(72, driver.getTemperature());
    }

    @Test
    public void updateFans() throws Exception {
        HealthCheckNvidiaImpl driver = new HealthCheckNvidiaImpl();
        driver.sourceLin = "cat ../nvidia-smi.out";
        driver.isSuitable();
        driver.updateFans();
        assertEquals(5, driver.setFanSpeedQueryCalled);
    }

    private class HealthCheckNvidiaImpl extends HealthCheckNvidia {

        private Integer gpuCount;
        private int setFanSpeedQueryCalled = 0;

        HealthCheckNvidiaImpl() {
        }

        HealthCheckNvidiaImpl(int gpuCount) {
            this.gpuCount = gpuCount;
        }

        /**
         * Get command line arguments for detection of available GPUs.
         *
         * @return command line arguments
         */
        @Override
        String[] getTemperatureQuery() {
            if (gpuCount != null) {
                StringBuilder gpuLines = new StringBuilder("'");
                for (int i = 0; i < gpuCount; i++) {
                    gpuLines.append("| 55%   61C  \n");
                }
                gpuLines.append("'");
                return new String[] { "echo", gpuLines.toString() };
            } else {
                return super.getTemperatureQuery();
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
			setFanSpeedQueryCalled++;
			if (UIConstants.isWindows()) {
				throw new IllegalStateException("Windows not supported yet.");
			} else {
				return new String[] { "/bin/sh", "-c", "echo OK"};
			}
		}
	}

}
