
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

    private static final Config INSTANCE = new Config();

    public static final String APP_VERSION = UIConstants.getAppVersion();
    public static final String APP_NAME_BASE = UIConstants.getProperty("app.name");
    public static final String APP_DEFAULT_URL = UIConstants.getProperty("app.url");
    public static final String APP_COPYRIGHT = UIConstants.getProperty("app.copyright");

    private static final int EXPECTED_ARGS = 4;

    private String machineName;
    private File logDir;
    private boolean fanControl;
    private boolean remoteReboot;

    public static String getMachineName() {
        return INSTANCE.machineName;
    }

    public static File getLogDir() {
        return INSTANCE.logDir;
    }

    public static boolean isFanControl() {
        return INSTANCE.fanControl;
    }

    public static boolean isRemoteReboot() {
        return INSTANCE.remoteReboot;
    }

    public static String getConfigFileName() {
        return "minerhealth." + (UIConstants.isWindows() ? "bat" : "sh");
    }

    void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    void setLogDir(File logDir) {
        this.logDir = logDir;
    }

    void setFanControl(boolean fanControl) {
        this.fanControl = fanControl;
    }

    void setRemoteReboot(boolean remoteReboot) {
        this.remoteReboot = remoteReboot;
    }

    /**
     * Read command line args and do basic validation.
     *
     * This runs exclusively from {@link com.databazoo.minerhealth.MinerHealth#main(String[])}, so access synchronization is skipped.
     *
     * @param args command line arguments
     */
    public static void init(String[] args) {
        UIConstants.PROPERTIES = null;

        if (args.length != EXPECTED_ARGS) {
            throw new IllegalArgumentException("Expected " + EXPECTED_ARGS + " arguments, but received " + args.length);
        }

        INSTANCE.machineName = args[0];
        INSTANCE.logDir = new File(args[1]);
        INSTANCE.fanControl = getBoolean(args[2]);
        INSTANCE.remoteReboot = getBoolean(args[3]);

        if (!INSTANCE.logDir.exists()) {
            throw new IllegalArgumentException("Folder " + INSTANCE.logDir.getAbsolutePath() + " does not exist. " +
                    "Please set 'logDir' variable properly in " + getConfigFileName());
        }

        LOGGER.info(INSTANCE.toString());
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
