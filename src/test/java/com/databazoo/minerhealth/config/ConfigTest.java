package com.databazoo.minerhealth.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigTest {

    @Test(expected = IllegalArgumentException.class)
    public void initArgsInvalid1() {
        Config.init(new String[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void initArgsInvalid2() {
        Config.init(new String[] {"", "", "", "", "", ""});
    }

    @Test
    public void initArgs() {
        Config.init(new String[] { "c107de54-40ef-43a4-99e3-acb5828c18ad", "R1", "./", "1", "true", "15" });

        assertEquals(Config.getClientID(), "c107de54-40ef-43a4-99e3-acb5828c18ad");
        assertEquals(Config.getMachineName(), "R1");
        assertEquals(Config.getLogDir().getName(), ".");
        assertTrue(Config.isFanControl());
        assertTrue(Config.isRemoteReboot());

        Config.init(new String[] { "c107de54-40ef-43a4-99e3-acb5828c18ad", "R2", "..", "yes", "0", "15" });

        assertEquals(Config.getClientID(), "c107de54-40ef-43a4-99e3-acb5828c18ad");
        assertEquals(Config.getMachineName(), "R2");
        assertEquals(Config.getLogDir().getName(), "..");
        assertTrue(Config.isFanControl());
        assertFalse(Config.isRemoteReboot());
    }
}