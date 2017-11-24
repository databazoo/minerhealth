package com.databazoo.minerhealth.healthcheck;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import com.databazoo.minerhealth.config.Config;
import com.databazoo.minerhealth.executable.Executable;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class HealthCheckClaymoreTest {

	@Before
	public void setUp() throws Exception {
		Config.setLogDir(new File(new File(new File("target"), "test-classes"), "logs"));
        Executable exec = new Executable("/bin/sh", "-c", "touch -t 2901011200 1503689981_log.txt && touch -t 1701011200 1503607098_log.txt").exec();
		if(exec.getResultCode() != 0) {
			throw new IllegalStateException("Could not touch required logfile.");
		}

		HealthCheckNvidia nvDriver = new HealthCheckNvidia();
		nvDriver.sourceLin = "cat ../nvidia-smi.out";
		nvDriver.isSuitable();
	}

	@Test
	public void check() throws Exception {
		HealthCheckClaymore driver = HealthCheck.CLAYMORE;
		driver.check();
		assertEquals(5, driver.getGpuCount());
		assertEquals(70.438, driver.getPerformance());
		assertEquals(14.0876, driver.getPerformancePerGPU());
	}

	@Test
	public void checkOldLog() throws Exception {
		HealthCheckClaymore driver = new HealthCheckClaymoreImpl();
		driver.check();
		assertEquals(0.0, driver.getPerformance());
	}

	private class HealthCheckClaymoreImpl extends HealthCheckClaymore {

		@Override
		Path getLastLogPath() throws IOException {
			return new File(Config.getLogDir(), "1503607098_log.txt").toPath();
		}
	}
}