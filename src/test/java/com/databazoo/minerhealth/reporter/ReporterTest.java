package com.databazoo.minerhealth.reporter;

import com.databazoo.minerhealth.config.Config;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReporterTest {

	@Test
	public void validateMetrics() throws Exception {
		Config.setMaxTemp(75);
		Config.setMinTemp(45);
		Config.setMinPerformance(75);
		Config.setMinPerformancePerGPU(15);

		Reporter reporter = new ReporterImpl();
		assertTrue(reporter.validateMetrics(70, 100, 20));
		assertFalse(reporter.validateMetrics(20, 100, 20));
		assertFalse(reporter.validateMetrics(100, 100, 20));
		assertFalse(reporter.validateMetrics(70, 70, 20));
		assertFalse(reporter.validateMetrics(70, 100, 14.5));
	}

	@Test
	public void validateResponse() throws Exception {
		Config.setRemoteReboot(true);

		Reporter reporter = new ReporterImpl();
		assertFalse(reporter.validateResponse("REMOTE_RESTART"));
		assertFalse(reporter.validateResponse("OVERHEAT"));
		assertFalse(reporter.validateResponse("UNDERPERFORM"));
		assertTrue(reporter.validateResponse("CONFIG"));
		assertTrue(reporter.validateResponse("OK"));
	}

	@Test
	public void validateResponseReboot() throws Exception {
		Config.setRemoteReboot(true);

		ReporterImpl reporter = new ReporterImpl();
		assertFalse(reporter.validateResponse("REMOTE_RESTART"));
		assertEquals(1, reporter.restartCalled);

		assertFalse(reporter.validateResponse("OVERHEAT"));
		assertEquals(1, reporter.restartCalled);
		assertFalse(reporter.validateResponse("OVERHEAT"));
		assertEquals(1, reporter.restartCalled);
		assertFalse(reporter.validateResponse("OVERHEAT"));
		assertEquals(1, reporter.restartCalled);
		assertFalse(reporter.validateResponse("OVERHEAT"));
		assertEquals(1, reporter.restartCalled);
		assertFalse(reporter.validateResponse("OVERHEAT"));
		assertEquals(1, reporter.restartCalled);
		assertFalse(reporter.validateResponse("OVERHEAT"));
		assertEquals(1, reporter.restartCalled);
		assertFalse(reporter.validateResponse("OVERHEAT"));
		assertEquals(2, reporter.restartCalled);
	}

	@Test
	public void config() throws Exception {
		Reporter reporter = new ReporterImpl();
		assertTrue(reporter.validateResponse("CONFIG minTemp 10 maxTemp 20 minPerformance 30 minPerformancePerGPU 40"));
		assertEquals(10, Config.getMinTemp(), 0.001);
		assertEquals(20, Config.getMaxTemp(), 0.001);
		assertEquals(30, Config.getMinPerformance(), 0.001);
		assertEquals(40, Config.getMinPerformancePerGPU(), 0.001);
	}

	private class ReporterImpl extends Reporter {

		private int restartCalled;

		/**
		 * Report and perform a reboot.
		 */
		@Override
		void restart() {
			restartCalled++;
		}
	}

}