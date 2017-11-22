package com.databazoo.minerhealth.healthcheck;

import com.databazoo.components.UIConstants;

/**
 * AMD implementation of health check driver.
 *
 * @author boris
 */
class HealthCheckAMD extends HealthCheckBase {

	/**
	 * Individual driver implementation requirement.
	 */
	@Override
	public void check() {
		// TODO
	}

	/**
	 * Get command line arguments for detection of available GPUs.
	 *
	 * @return command line arguments
	 */
	@Override
	String[] countGPUsQuery() {
		if (UIConstants.isWindows()) {
			throw new IllegalStateException("Windows not supported yet.");
		} else {
			return new String[]{"echo", "0"};
		}
	}
}
