package com.databazoo.minerhealth.reporter;

/**
 * Report instance and static generation methods API.
 */
class Report {

    /**
     * Generate "All up" report.
     *
     * @param clientID client ID
     * @param machineName machine name
     * @param gpuCount detected count of GPUs
     * @param temperature detected temperature
     * @param performance detected performance
     * @return report instance
     */
    static Report up(String clientID, String machineName, int gpuCount, double temperature, double performance) {
        return new Report();
    }

    /**
     * Generate "Started up" report.
     *
     * @param clientID client ID
     * @param machineName machine name
     * @return report instance
     */
    static Report start(String clientID, String machineName) {
        return new Report();
    }

    /**
     * Generate "Restarting" report.
     *
     * @param clientID client ID
     * @param machineName machine name
     * @return report instance
     */
    static Report restart(String clientID, String machineName) {
        return new Report();
    }

    private Report() {
    }

    /**
     * Send the report.
     *
     * @return response constant
     */
    String send() {
        // TODO: send and receive
        return "";
    }
}
