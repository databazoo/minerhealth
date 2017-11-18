package com.databazoo.minerhealth.healthcheck;

/**
 * nVidia implementation of health check driver.
 *
 * @author boris
 */
class HealthCheckNvidia extends HealthCheckBase {

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
		// TODO
		return new String[] {};
	}
}
