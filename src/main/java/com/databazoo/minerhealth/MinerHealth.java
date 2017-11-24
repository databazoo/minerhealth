
package com.databazoo.minerhealth;

import java.util.logging.Logger;

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
}
