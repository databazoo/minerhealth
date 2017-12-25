package com.databazoo.components;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Properties;

/**
 * UI Constants
 */
public class UIConstants {
    public static final Graphics2D GRAPHICS = (Graphics2D) new BufferedImage(5,5,BufferedImage.TYPE_INT_ARGB).getGraphics();

    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final String APP_VERSION = "app.version";

    public static final boolean DEBUG = getProperty("app.debug") != null && getProperty("app.debug").equalsIgnoreCase("true");

    public static volatile Properties PROPERTIES;

    private static volatile String versionWithEnvironment;

    /**
     * Check platform.
     *
     * @return is Windows platform?
     */
    public static boolean isWindows() {
        return OS.contains("win");
    }

    /**
     * Check platform.
     *
     * @return is MacOS platform?
     */
    public static boolean isMac() {
        return OS.contains("mac");
    }

    public static String getProperty(String property) {
        if (PROPERTIES == null) {
            try {
                PROPERTIES = new Properties();
                PROPERTIES.load(UIConstants.class.getResource("/app.properties").openStream());
            } catch (IOException e) {
                throw new IllegalStateException("Could not load properties", e);
            }
        }
        return PROPERTIES.getProperty(property);
    }

    public static String getAppVersion() {
        return getProperty(APP_VERSION);
    }

    /**
     * Get app version with environment.
     *
     * @return string
     */
    public static String getVersionWithEnvironment() {
        return versionWithEnvironment == null ? "uninitialized application on " + getJREVersion() : versionWithEnvironment;
    }

    public static void setVersionWithEnvironment(String versionWithEnvironment) {
        UIConstants.versionWithEnvironment = versionWithEnvironment;
    }

    /**
     * Get username and OS.
     *
     * @return string
     */
    public static String getUsernameWithOS() {
        return System.getProperty("user.name") + "." + OS;
    }

    /**
     * Get JRE version with bitness.
     *
     * @return string
     */
    private static String getJREVersion() {
        return System.getProperty("java.version") + " " + (System.getProperty("os.arch").contains("64") ? "64bit" : "32bit");
    }
}
