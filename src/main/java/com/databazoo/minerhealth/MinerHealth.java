
package com.databazoo.minerhealth;

import com.databazoo.minerhealth.config.Config;
import com.databazoo.tools.Dbg;

/**
 * Main class
 *
 * @author bobus
 */
public class MinerHealth {

    /**
     * main() method
     *
     * @param args command-line params
     */
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new Dbg.UncaughtExceptionHandler());
        new MinerHealth().start();
    }

    /**
     * Run application
     */
    private void start() {
        Config.init();

        Dbg.info("Config initialized");
    }
}
