package com.databazoo.minerhealth.healthcheck;

import com.databazoo.minerhealth.config.Config;
import com.databazoo.minerhealth.executable.Executable;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static junit.framework.Assert.assertEquals;

//@Ignore("For some reason fails on CircleCI")
public class HealthCheckClaymoreTest {

	@Before
	public void setUp() throws Exception {
		Config.setLogDir(new File(new File(new File("target"), "test-classes"), "logs"));
		Thread.sleep(2000);
		Executable exec = new Executable("/bin/sh", "-c", "touch 1503689981_log.txt").exec();
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

}