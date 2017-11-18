package com.databazoo.minerhealth.healthcheck;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HealthCheckNvidiaTest {

	@Test
	public void isSuitable() throws Exception {
		assertTrue(new HealthCheckNvidiaImpl(5).isSuitable());
		assertFalse(new HealthCheckNvidiaImpl(0).isSuitable());
	}

	private class HealthCheckNvidiaImpl extends HealthCheckNvidia {

		private final int gpuCount;

		HealthCheckNvidiaImpl(int gpuCount) {
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
