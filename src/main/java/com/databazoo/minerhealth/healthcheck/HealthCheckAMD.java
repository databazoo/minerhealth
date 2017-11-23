package com.databazoo.minerhealth.healthcheck;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.databazoo.components.UIConstants;

/**
 * AMD implementation of health check driver.
 *
 * @author boris
 */
class HealthCheckAMD extends HealthCheckBase {

	private static final Pattern TEMP_REGEX = Pattern.compile(".*?%.*?(\\d+).*");

	/**
	 * Get regexp matcher for temperature lines.
	 *
	 * Contract: match whole string with {@link Matcher#find()} and provide the temperature value in $1 for replacement.
	 *
	 * @param line individual line to match
	 * @return instance of matcher
	 */
	@Override Matcher getTempMatcher(String line) {
		return TEMP_REGEX.matcher(line);
	}

	/**
	 * Get command line arguments for detection of temperature.
	 *
	 * @return command line arguments
	 */
	@Override
	String[] getTemperatureQuery() {
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
			throw new IllegalStateException("Fan speed control is not available for AMD.");
		}
	}
}
