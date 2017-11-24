package com.databazoo.minerhealth.healthcheck;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.databazoo.minerhealth.MinerHealth;
import com.databazoo.minerhealth.config.Config;

/**
 * Read the latest Claymore log to detect the total output.
 */
public class HealthCheckClaymore {

    private static final Pattern OUTPUT_REGEX = Pattern.compile(".*?Total Speed.*?(\\d+(\\.\\d+)?).*");

    private int gpuCount;
    private double performance;

    public int getGpuCount() {
        return gpuCount;
    }

    void setGpuCount(int gpuCount) {
        this.gpuCount = gpuCount;
    }

    public double getPerformance() {
        return performance;
    }

    public double getPerformancePerGPU() {
        return performance / gpuCount * 1.0;
    }

    void check() {
        try {
            Path path = getLastLogPath();
            long seconds = getSecondsFromModification(path);
            if (seconds > 300) {
                performance = 0;
                MinerHealth.LOGGER.warning("Logfile " + path + " has not been modified for " + seconds + " seconds. Ignoring reported output.");
            } else {
                MinerHealth.LOGGER.warning("Reading " + path + " last modified " + seconds + " seconds ago.");
                performance = getPerformanceFromFile(path.toFile());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Reading Claymore log failed.", e);
        }

    }

    Path getLastLogPath() throws IOException {
        long timeMillis = System.currentTimeMillis();
        Optional<Path> lastFilePath = Files.list(Config.getLogDir().toPath())
                .filter(f -> !Files.isDirectory(f))
                .max((f1, f2) -> {
                    MinerHealth.LOGGER.info("Path " + f1 + " last modified " + (timeMillis - f1.toFile().lastModified()) + " seconds ago.");
                    return (int) (timeMillis - f1.toFile().lastModified());
                });

        if (!lastFilePath.isPresent()) {
            throw new IllegalStateException("No Claymore log available.");
        } else {
            return lastFilePath.get();
        }
    }

    private double getPerformanceFromFile(File file) throws IOException {
        double totalValue = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Total Speed")) {
                    Matcher matcher = OUTPUT_REGEX.matcher(line);
                    if (matcher.find()) {
                        totalValue = Double.parseDouble(matcher.replaceFirst("$1"));
                    }
                }
            }
        }
        return totalValue;
    }

    private long getSecondsFromModification(Path path) throws IOException {
        BasicFileAttributes basicAttribs = Files.readAttributes(path, BasicFileAttributes.class);
        return (System.currentTimeMillis() - basicAttribs.lastModifiedTime().to(TimeUnit.MILLISECONDS)) / 1000;
    }
}
