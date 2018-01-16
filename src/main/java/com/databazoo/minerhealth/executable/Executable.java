package com.databazoo.minerhealth.executable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.databazoo.minerhealth.MinerHealth;
import com.databazoo.minerhealth.config.Config;

/**
 * Wrapper for exec()
 *
 * @author boris
 */
public class Executable {

	private static final Executor THREAD_POOL = Executors.newCachedThreadPool();

	private final String[] args;
	private final StringBuilder outputStd = new StringBuilder();
	private final StringBuilder outputErr = new StringBuilder();
	private int resultCode;

	public Executable(String... args) {
		this.args = args;
	}

	public String[] getArgs() {
		return args;
	}

	public String getOutputStd() {
		return outputStd.toString();
	}

	public String getOutputErr() {
		return outputErr.toString();
	}

	public int getResultCode() {
		return resultCode;
	}

    /**
     * Run the commands. Retrieves stdin and stderr as strings.
     *
     * @return this
     */
    public Executable exec() {
		Process p = null;
        try {
			Process finalP = p = Runtime.getRuntime().exec(args, new String[0], Config.getLogDir());

            CountDownLatch latch = new CountDownLatch(2);
			THREAD_POOL.execute(() -> {
                String s;
                StringBuilder outputSB = new StringBuilder();
                try (BufferedReader input = new BufferedReader(new InputStreamReader(finalP.getInputStream()))) {
                    while ((s = input.readLine()) != null) {
                        if (outputSB.length() > 0) {
                            outputSB.append('\n');
                        }
                        outputSB.append(s);
                    }
                } catch (IOException ex) {
                    MinerHealth.LOGGER.severe("stdin failed: " + ex.getMessage());
                }
                outputStd.append(outputSB.toString());
                latch.countDown();
            });
            THREAD_POOL.execute(() -> {
                String s;
                StringBuilder outputSB = new StringBuilder();
                try (BufferedReader input = new BufferedReader(new InputStreamReader(finalP.getErrorStream()))) {
                    while ((s = input.readLine()) != null) {
                        MinerHealth.LOGGER.warning(s);
                        if (outputSB.length() > 0) {
                            outputSB.append('\n');
                        }
                        outputSB.append(s);
                    }
                } catch (IOException ex) {
                    MinerHealth.LOGGER.severe("stderr failed: " + ex);
                }
                outputErr.append(outputSB.toString());
                latch.countDown();
            });

            // Wait for process to end
            resultCode = p.waitFor();
            //MinerHealth.LOGGER.info("exit code: "+ resultCode);

            // Wait for threads to collect all data
            latch.await();

            return this;

        } catch (Exception ex) {
            MinerHealth.LOGGER.severe("Call " + Arrays.toString(args) + " failed: " + ex);
            resultCode = -1;
            return this;

        } finally {
        	if (p != null) {
				p.destroy();
			}
		}
	}
}
