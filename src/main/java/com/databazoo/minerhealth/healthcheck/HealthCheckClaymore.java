package com.databazoo.minerhealth.healthcheck;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.databazoo.minerhealth.MinerHealth;
import com.databazoo.minerhealth.config.Config;

/**
 * Read the latest Claymore log to detect the total output.
 */
public class HealthCheckClaymore {

    private static final Pattern OUTPUT_REGEX = Pattern.compile(".*?Total Speed.*?(\\d+(\\.\\d+)?).*?Total Shares.*?(\\d+).*");

    private int gpuCount;
    private double performance;
    private int shares;

    /**
     * Get detected GPU count.
     *
     * @return detected GPU count
     */
    public int getGpuCount() {
        return gpuCount;
    }

    /**
     * Set detected GPU count.
     *
     * @param gpuCount detected GPU count
     */
    void setGpuCount(int gpuCount) {
        this.gpuCount = gpuCount;
    }

    /**
     * Get detected performance.
     *
     * @return detected performance
     */
    public double getPerformance() {
        return performance;
    }

    /**
     * Get detected shares.
     *
     * @return detected shares
     */
    public int getShares() {
        return shares;
    }

    /**
     * Get detected performance per GPU.
     *
     * @return detected performance per GPU
     */
    public double getPerformancePerGPU() {
        return performance / gpuCount * 1.0;
    }

    /**
     * Find latest log and fetch total output out of it.
     */
    void check() {
        try {
            Path path = getLastLogPath();
            long seconds = getSecondsFromModification(path);
            if (seconds > 300) {
                performance = 0;
                MinerHealth.LOGGER.warning("Logfile " + path + " has not been modified for " + seconds + " seconds. Ignoring reported output.");
            } else {
                MinerHealth.LOGGER.info("Reading " + path + " last modified " + seconds + " seconds ago.");
                getPerformanceFromFile(path.toFile());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Reading Claymore log failed.", e);
        }

    }

    /**
     * Find the latest file in {@link Config#logDir}.
     *
     * @return the latest file
     * @throws IOException in case of IO error
     */
    Path getLastLogPath() throws IOException {
        try (Stream<Path> list = Files.list(Config.getLogDir().toPath())) {
            Optional<Path> lastFilePath = list
                    .filter(f -> !Files.isDirectory(f))
                    .max(Comparator.comparingLong(f -> f.toFile().lastModified()));

            if (!lastFilePath.isPresent()) {
                throw new IllegalStateException("No Claymore log available.");
            } else {
                return lastFilePath.get();
            }
        }
    }

    /**
     * Parse given Claymore log.
     *
     * @param file given log
     * @throws IOException in case of IO error
     */
    private void getPerformanceFromFile(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Total Speed")) {
                    Matcher matcher = OUTPUT_REGEX.matcher(line);
                    if (matcher.find()) {
                        performance = Double.parseDouble(matcher.replaceFirst("$1"));
                        shares = Integer.parseInt(matcher.replaceFirst("$3"));
                    }
                }
            }
        }
    }

    /**
     * Calculate now()-modification in seconds.
     *
     * @param path given file
     * @return last modified
     * @throws IOException in case of IO error
     */
    private long getSecondsFromModification(Path path) throws IOException {
        BasicFileAttributes basicAttribs = Files.readAttributes(path, BasicFileAttributes.class);
        return (System.currentTimeMillis() - basicAttribs.lastModifiedTime().to(TimeUnit.MILLISECONDS)) / 1000;
    }
}
