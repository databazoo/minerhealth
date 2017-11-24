package com.databazoo.minerhealth;

import org.junit.Test;

public class MinerHealthTest {

    @Test
    public void smokeTest() throws Exception {
        MinerHealth.main(new String[] { "c107de54-40ef-43a4-99e3-acb5828c18ad", "TEST_RIG", "./", "1", "1", "15", "6" });
        Thread.sleep(100);
    }
}