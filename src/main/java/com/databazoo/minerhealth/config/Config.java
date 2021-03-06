
package com.databazoo.minerhealth.config;

import java.io.File;

import com.databazoo.components.UIConstants;

import static com.databazoo.minerhealth.MinerHealth.LOGGER;

/**
 * Global application config.
 *
 * @author boris
 */
public class Config {

    private static final Config INSTANCE = new Config();

    public static final String APP_VERSION = UIConstants.getAppVersion();
    public static final String APP_NAME_BASE = UIConstants.getProperty("app.name");
    public static final String APP_REST_URL = UIConstants.getProperty("app.rest.url");
    public static final String APP_COPYRIGHT = UIConstants.getProperty("app.copyright");

    private static final int EXPECTED_ARGS = 7;
    private static final int MIN_INTERVAL = 5;

    private String clientID;
    private String machineName;
    private File logDir;
    private boolean fanControl;
    private boolean remoteReboot;
    private int reportInterval;
    private int recheckAttemptsLimit = 6;

    private double minTemp = 0;
    private double maxTemp = 999;
    private double minPerformance = 0;
    private double minPerformancePerGPU = 0;

    private int fan30Temp = 55;
    private int fan100Temp = 69;

    public static String getClientID() {
        return INSTANCE.clientID;
    }

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

    public static int getReportInterval() {
        return INSTANCE.reportInterval;
    }

    public static int getRecheckAttemptsLimit() {
        return INSTANCE.recheckAttemptsLimit;
    }

    public static String getConfigFileName() {
        return "minerhealth." + (UIConstants.isWindows() ? "bat" : "sh");
    }

    public static void setClientID(String clientID) {
        INSTANCE.clientID = clientID;
    }

    public static void setMachineName(String machineName) {
        INSTANCE.machineName = machineName;
    }

    public static void setLogDir(File logDir) {
        INSTANCE.logDir = logDir;
    }

    public static void setFanControl(boolean fanControl) {
        INSTANCE.fanControl = fanControl;
    }

    public static void setRemoteReboot(boolean remoteReboot) {
        INSTANCE.remoteReboot = remoteReboot;
    }

    public static void setReportInterval(int reportInterval) {
        INSTANCE.reportInterval = reportInterval;
    }

    public static void setRecheckAttemptsLimit(int recheckAttemptsLimit) {
        INSTANCE.recheckAttemptsLimit = recheckAttemptsLimit;
    }

    public static double getMinTemp() {
        return INSTANCE.minTemp;
    }

    public static void setMinTemp(double minTemp) {
        INSTANCE.minTemp = minTemp;
    }

    public static double getMaxTemp() {
        return INSTANCE.maxTemp;
    }

    public static void setMaxTemp(double maxTemp) {
        INSTANCE.maxTemp = maxTemp;
    }

    public static double getMinPerformance() {
        return INSTANCE.minPerformance;
    }

    public static void setMinPerformance(double minPerformance) {
        INSTANCE.minPerformance = minPerformance;
    }

    public static double getMinPerformancePerGPU() {
        return INSTANCE.minPerformancePerGPU;
    }

    public static void setMinPerformancePerGPU(double minPerformancePerGPU) {
        INSTANCE.minPerformancePerGPU = minPerformancePerGPU;
    }

    public static int getFan30Temp() {
        return INSTANCE.fan30Temp;
    }

    public static void setFan30Temp(int fan30Temp) {
        INSTANCE.fan30Temp = fan30Temp;
    }

    public static int getFan100Temp() {
        return INSTANCE.fan100Temp;
    }

    public static void setFan100Temp(int fan100Temp) {
        INSTANCE.fan100Temp = fan100Temp;
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

        setClientID(args[0]);
        setMachineName(args[1]);
        setLogDir(new File(args[2]));
        setFanControl(getBoolean(args[3]));
        setRemoteReboot(getBoolean(args[4]));
        setReportInterval(Integer.parseInt(args[5]));
        setRecheckAttemptsLimit(Integer.parseInt(args[6]));

        if (!getLogDir().exists()) {
            throw new IllegalArgumentException("Folder " + getLogDir().getAbsolutePath() + " does not exist. " +
                    "Please set 'logDir' variable properly in " + getConfigFileName());
        }

        if (getReportInterval() < MIN_INTERVAL) {
            throw new IllegalArgumentException("Report interval can not be less than " + MIN_INTERVAL + ". " +
                    "Please set 'reportInterval' variable properly in " + getConfigFileName());
        }

        LOGGER.info(INSTANCE.toString());
    }

    /**
     * Read boolean parameters.
     *
     * @param arg true for 1, yes, true
     * @return boolean value
     */
    private static boolean getBoolean(String arg) {
        return arg.equalsIgnoreCase("1") || arg.equalsIgnoreCase("yes") || arg.equalsIgnoreCase("true");
    }

    /**
     * Info dump.
     *
     * @return info string
     */
    @Override public String toString() {
        return APP_NAME_BASE + " v" + APP_VERSION + " {" +
                "\n\tclientID = " + getClientID() +
                "\n\tmachineName = " + machineName +
                "\n\tlogDir = " + logDir.getAbsolutePath() +
                "\n\tfanControl = " + fanControl +
                "\n\tremoteReboot = " + remoteReboot +
                "\n\treportInterval = " + reportInterval +
                "\n\treportURL = " + APP_REST_URL +
                "\n}";
    }
}
