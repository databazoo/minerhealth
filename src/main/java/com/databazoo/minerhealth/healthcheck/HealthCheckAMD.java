package com.databazoo.minerhealth.healthcheck;

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
		// TODO
		return new String[] {};
	}
}
