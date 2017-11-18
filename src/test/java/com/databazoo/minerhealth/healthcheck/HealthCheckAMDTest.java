package com.databazoo.minerhealth.healthcheck;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HealthCheckAMDTest {

	@Test
	public void isSuitable() throws Exception {
		assertTrue(new HealthCheckAmdImpl(5).isSuitable());
		assertFalse(new HealthCheckAmdImpl(0).isSuitable());
	}

	private class HealthCheckAmdImpl extends HealthCheckAMD {

		private final int gpuCount;

		HealthCheckAmdImpl(int gpuCount) {
			this.gpuCount = gpuCount;
		}

		/**
		 * Get command line arguments for detection of available GPUs.
		 *
		 * @return command line arguments
		 */
		@Override
		String[] countGPUsQuery() {
			return new String[] {"echo", String.valueOf(gpuCount)};
		}
	}

}
