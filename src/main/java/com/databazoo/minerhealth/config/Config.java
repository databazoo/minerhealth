
package com.databazoo.minerhealth.config;

import java.io.File;

import com.databazoo.components.UIConstants;

import static com.databazoo.minerhealth.MinerHealth.LOGGER;

/**
 * Global application config.
 *
 * @author bobus
 */
public class Config {

    public static final Config INSTANCE = new Config();

    public final static String APP_VERSION = UIConstants.getAppVersion();
    public static final String APP_NAME_BASE = UIConstants.getProperty("app.name");
    public static final String APP_DEFAULT_URL = UIConstants.getProperty("app.url");
    public static final String APP_COPYRIGHT = UIConstants.getProperty("app.copyright");

    private static final int EXPECTED_ARGS = 4;

    private String machineName;
    private File logDir;
    private boolean fanControl;
    private boolean remoteReboot;

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public File getLogDir() {
        return logDir;
    }

    public void setLogDir(File logDir) {
        this.logDir = logDir;
    }

    public boolean isFanControl() {
        return fanControl;
    }

    public void setFanControl(boolean fanControl) {
        this.fanControl = fanControl;
    }

    public boolean isRemoteReboot() {
        return remoteReboot;
    }

    public void setRemoteReboot(boolean remoteReboot) {
        this.remoteReboot = remoteReboot;
    }

    /**
     * Check password, licence, app name and new version.
     *
     * @param args command line arguments
     */
    public void init(String[] args) {
        UIConstants.PROPERTIES = null;

        if (args.length != EXPECTED_ARGS) {
            throw new IllegalArgumentException("Expected " + EXPECTED_ARGS + " arguments, but received " + args.length);
        }

        machineName = args[0];
        logDir = new File(args[1]);
        fanControl = getBoolean(args[2]);
        remoteReboot = getBoolean(args[3]);

        if (!logDir.exists()) {
            throw new IllegalArgumentException("Folder " + logDir.getAbsolutePath() + " does not exist");
        }

        LOGGER.info(toString());
    }

    private static boolean getBoolean(String arg) {
        return arg.equalsIgnoreCase("1") || arg.equalsIgnoreCase("yes") || arg.equalsIgnoreCase("true");
    }

    @Override public String toString() {
        return "Config { " +
                "machineName='" + machineName + '\'' +
                ", logDir=" + logDir.getAbsolutePath() +
                ", fanControl=" + fanControl +
                ", remoteReboot=" + remoteReboot +
                " }";
    }
}
