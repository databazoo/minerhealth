package com.databazoo.minerhealth.reporter;

import com.databazoo.minerhealth.config.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

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
        return new Report(clientID, machineName, "up", gpuCount, temperature, performance);
    }

    /**
     * Generate "Started up" report.
     *
     * @param clientID client ID
     * @param machineName machine name
     * @return report instance
     */
    static Report start(String clientID, String machineName) {
        return new Report(clientID, machineName, "start");
    }

    /**
     * Generate "Restarting" report.
     *
     * @param clientID client ID
     * @param machineName machine name
     * @return report instance
     */
    static Report restart(String clientID, String machineName) {
        return new Report(clientID, machineName, "reboot");
    }

    private final String clientID;
    private final String machineName;
    private final String action;
    private int gpuCount;
    private double temperature;
    private double performance;

    public Report(String clientID, String machineName, String action, int gpuCount, double temperature, double performance) {
        this.clientID = clientID;
        this.machineName = machineName;
        this.action = action;
        this.gpuCount = gpuCount;
        this.temperature = temperature;
        this.performance = performance;
    }

    private Report(String clientID, String machineName, String action) {
        this.clientID = clientID;
        this.machineName = machineName;
        this.action = action;
    }

    /**
     * Send the report.
     *
     * @return response constant
     */
    String send() {
        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(getUrl().openConnection().getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if(response.length() > 0) {
                    response.append("\n");
                }
                response.append(inputLine);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Server reporting failed.", e);
        }
        return response.toString();
    }

    private URL getUrl() throws MalformedURLException {
        StringBuilder restUrl = new StringBuilder(Config.APP_REST_URL)
                .append("?").append("clientID").append("=").append(clientID)
                .append("&").append("machine").append("=").append(machineName)
                .append("&").append("action").append("=").append(action);
        if (gpuCount > 0) {
            restUrl.append("&").append("gpus").append("=").append(gpuCount);
        }
        if (temperature > 0) {
            restUrl.append("&").append("temp").append("=").append(temperature);
        }
        if (performance > 0) {
            restUrl.append("&").append("output").append("=").append(performance);
        }
        return new URL(restUrl.toString());
    }
}
