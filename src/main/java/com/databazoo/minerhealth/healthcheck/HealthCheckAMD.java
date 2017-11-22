package com.databazoo.minerhealth.healthcheck;

import com.databazoo.components.UIConstants;

/**
 * AMD implementation of health check driver.
 *
 * @author boris
 */
class HealthCheckAMD extends HealthCheckBase {

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

	/**
	 * Get command line arguments for detection of temperature.
	 *
	 * @return command line arguments
	 */
	@Override
	String[] countTemperatureQuery() {
		if (UIConstants.isWindows()) {
			throw new IllegalStateException("Windows not supported yet.");
		} else {
			return new String[]{"echo", "0"};
		}
	}

	/**
	 * Get command line arguments for detection of temperature.
	 *
	 * @param gpuNumber zero-based GPU number
	 * @return command line arguments
	 */
	@Override
	String[] countTemperatureQuery(int gpuNumber) {
		if (UIConstants.isWindows()) {
			throw new IllegalStateException("Windows not supported yet.");
		} else {
			return new String[]{"echo", "0"};
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
		if (UIConstants.isWindows()) {
			throw new IllegalStateException("Windows not supported yet.");
		} else {
			return new String[]{};
		}
	}
}
