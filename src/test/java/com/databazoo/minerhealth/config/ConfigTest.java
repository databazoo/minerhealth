package com.databazoo.minerhealth.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigTest {

    @Test(expected = IllegalArgumentException.class)
    public void initArgsInvalid1() {
        Config.INSTANCE.init(new String[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void initArgsInvalid2() {
        Config.INSTANCE.init(new String[] {"", "", "", "", "", ""});
    }

    @Test
    public void initArgs() {
        Config.INSTANCE.init(new String[] {"R1", "./", "1", "true"});

        assertEquals(Config.INSTANCE.getMachineName(), "R1");
        assertEquals(Config.INSTANCE.getLogDir().getName(), ".");
        assertTrue(Config.INSTANCE.isFanControl());
        assertTrue(Config.INSTANCE.isRemoteReboot());

        Config.INSTANCE.init(new String[] {"R2", "..", "yes", "0"});

        assertEquals(Config.INSTANCE.getMachineName(), "R2");
        assertEquals(Config.INSTANCE.getLogDir().getName(), "..");
        assertTrue(Config.INSTANCE.isFanControl());
        assertFalse(Config.INSTANCE.isRemoteReboot());
    }
}