package com.databazoo.minerhealth.healthcheck;

import java.io.File;

import com.databazoo.minerhealth.config.Config;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HealthCheckAMDTest {

	@Before
	public void setUp() throws Exception {
		Config.setLogDir(new File(new File(new File("target"), "test-classes"), "logs"));
	}

	@Test
	public void smokeTest() throws Exception {
		new HealthCheckAMD().isSuitable();
	}

	@Test
	public void isSuitable() throws Exception {
		assertTrue(new HealthCheckAmdImpl(5).isSuitable());
		assertFalse(new HealthCheckAmdImpl(0).isSuitable());
	}

	private class HealthCheckAmdImpl extends HealthCheckAMD {

		private Integer gpuCount;

		HealthCheckAmdImpl(int gpuCount) {
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
	}

}
