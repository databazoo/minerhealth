
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
     * @param args command-line params
     */
    public static void main(String[] args) {
        new MinerHealth().start();
    }

    /**
     * Run application
     */
    private void start() {
        Config.init();

        LOGGER.info("Config initialized");
    }
}
