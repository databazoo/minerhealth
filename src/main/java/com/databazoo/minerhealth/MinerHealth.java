
package com.databazoo.minerhealth;

import java.util.logging.Logger;

import com.databazoo.minerhealth.config.Config;

/**
 * Main class
 *
 * @author bobus
 */
public class MinerHealth {

    public static final Logger LOGGER = Logger.getLogger(MinerHealth.class.getName());

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

        LOGGER.info("Config initialized");
    }
}
