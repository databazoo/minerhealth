package com.databazoo.minerhealth.reporter;

import org.junit.Test;

import static org.junit.Assert.*;

public class ReportTest {
	@Test
	public void send() throws Exception {
		assertEquals("OK", Report.up("testClientID", "TEST_RIG", 5, 66, 94.3).send());
		assertEquals("OK", Report.start("testClientID", "TEST_RIG").send());
	}

}