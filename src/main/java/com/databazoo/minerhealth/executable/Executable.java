package com.databazoo.minerhealth.executable;

import com.databazoo.minerhealth.MinerHealth;
import com.databazoo.minerhealth.config.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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

	public Executable exec() {
		try {
			final Process p = Runtime.getRuntime().exec(args, new String[0], Config.getLogDir());

			CountDownLatch latch = new CountDownLatch(2);
			THREAD_POOL.execute(() -> {
				String s;
				StringBuilder outputSB = new StringBuilder();
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
				try {
					while ((s = input.readLine()) != null) {
						//MinerHealth.LOGGER.info(s);
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
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				try {
					while ((s = input.readLine()) != null) {
						MinerHealth.LOGGER.warning(s);
						outputSB.append(s);
					}
				} catch (IOException ex) {
					MinerHealth.LOGGER.severe("errin failed: " + ex);
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
		}
		catch (Exception ex) {
			MinerHealth.LOGGER.severe("Call "+Arrays.toString(args)+" failed: " + ex);
			resultCode = -1;
			return this;
		}
	}
}
