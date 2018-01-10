
package com.databazoo.minerhealth;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.databazoo.minerhealth.config.Config;
import com.databazoo.minerhealth.healthcheck.HealthCheck;

/**
 * Main class
 *
 * @author boris
 */
public class MinerHealth {

    public static final Logger LOGGER = Logger.getLogger(MinerHealth.class.getName());

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$-6s %5$s%6$s%n");
        try {
            FileHandler fileTxt = new FileHandler(new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".log");
            fileTxt.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileTxt);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            e.printStackTrace();
        }
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandlerImpl());
    }

    /**
     * main() method
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new MinerHealth().start(args);
    }

    /**
     * Run application
     *
     * @param args command line arguments
     */
    private void start(String[] args) {
        Config.init(args);
        HealthCheck.runChecks();
    }

    private static class UncaughtExceptionHandlerImpl implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            StringBuilder out = new StringBuilder();
            out.append("Exception in thread ").append(thread.getName()).append(": ").append(ex.getMessage());
            for(StackTraceElement elem : ex.getStackTrace()){
                out.append("\n").append(elem.toString());
            }
            LOGGER.severe(out.toString());
        }
    }
}
