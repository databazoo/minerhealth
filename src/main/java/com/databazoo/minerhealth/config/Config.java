
package com.databazoo.minerhealth.config;

import com.databazoo.components.UIConstants;

/**
 * Global application config.
 *
 * @author bobus
 */
public class Config {
	public final static String APP_VERSION = UIConstants.getAppVersion();
	public static final String APP_NAME_BASE = UIConstants.getProperty("app.name");
	public static final String APP_DEFAULT_URL = UIConstants.getProperty("app.url");
	public static final String APP_COPYRIGHT = UIConstants.getProperty("app.copyright");

	/**
	 * Check password, licence, app name and new version.
	 */
	public static void init() {
		UIConstants.PROPERTIES = null;
	}
}
