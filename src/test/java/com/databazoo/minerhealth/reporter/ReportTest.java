package com.databazoo.minerhealth.reporter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReportTest {
	@Test
	public void send() throws Exception {
		assertEquals("OK", Report.up("c107de54-40ef-43a4-99e3-acb5828c18ad", "TEST_RIG", 5, 66, 75, 94.3, 7).send());
		assertEquals("OK", Report.start("c107de54-40ef-43a4-99e3-acb5828c18ad", "TEST_RIG").send());
	}

}