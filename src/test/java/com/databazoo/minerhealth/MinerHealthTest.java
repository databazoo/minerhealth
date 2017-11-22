package com.databazoo.minerhealth;

import org.junit.Test;

public class MinerHealthTest {

    @Test
    public void smokeTest() throws Exception {
        MinerHealth.main(new String[] { "TEST_RIG", "./", "1", "1", "15" });
        Thread.sleep(100);
    }
}