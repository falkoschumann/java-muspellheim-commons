/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.util;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationTests {

    @AfterEach
    void resetSystemProperties() {
        System.clearProperty("var1");
        System.clearProperty("var2");
        System.clearProperty("var3");
        System.clearProperty("var4");
    }

    @Test
    void defaultsLoaded() throws Exception {
        // Given
        Configuration config = new Configuration();
        config.loadDefaults("config-test");

        // When
        String var1 = config.getString("var1");
        int var2 = config.getInt("var2");
        boolean var3 = config.getBoolean("var3");
        double var4 = config.getDouble("var4");

        // Then
        assertAll(
            () -> assertEquals("foo", var1, "string"),
            () -> assertEquals(42, var2, "int"),
            () -> assertTrue(var3, "boolean"),
            () -> assertEquals(0.815, var4, "double")
        );
    }

    @Test
    void defaultsNotExists() throws Exception {
        // Given
        Configuration config = new Configuration();

        // When
        assertThrows(ConfigurationException.class, () -> config.loadDefaults("file-not-exists"));
    }

    @Test
    void getWithDefault() throws Exception {
        // Given
        Configuration config = new Configuration();

        // When
        String var1 = config.getString("var1", "foo");
        int var2 = config.getInt("var2", 42);
        boolean var3 = config.getBoolean("var3", true);
        double var4 = config.getDouble("var4", 0.815);

        // Then
        assertAll(
            () -> assertEquals("foo", var1, "string"),
            () -> assertEquals(42, var2, "int"),
            () -> assertTrue(var3, "boolean"),
            () -> assertEquals(0.815, var4, "double")
        );
    }

    @Test
    void systemProperties() throws Exception {
        // Given
        System.setProperty("var1", "foo");
        System.setProperty("var2", "42");
        System.setProperty("var3", "true");
        System.setProperty("var4", "0.815");
        Configuration config = new Configuration();

        // When
        String var1 = config.getString("var1");
        int var2 = config.getInt("var2");
        boolean var3 = config.getBoolean("var3");
        double var4 = config.getDouble("var4");

        // Then
        assertAll(
            () -> assertEquals("foo", var1, "string"),
            () -> assertEquals(42, var2, "int"),
            () -> assertTrue(var3, "boolean"),
            () -> assertEquals(0.815, var4, "double")
        );
    }

    @Test
    void priority() throws Exception {
        // Given
        System.setProperty("var1", "foobar");
        System.setProperty("var3", "false");
        Configuration config = new Configuration();
        config.loadDefaults("config-test");

        // When
        String var1 = config.getString("var1", "bar");
        int var2 = config.getInt("var2", 13);
        boolean var3 = config.getBoolean("var3");

        // Then
        assertAll(
            () -> assertEquals("foobar", var1, "default < property < system property"),
            () -> assertEquals(42, var2, "default < property"),
            () -> assertFalse(var3, "property < system property")
        );
    }

    @Test
    void notConfigured() {
        // Given
        Configuration config = new Configuration();

        // When
        assertAll(
            () -> assertThrows(NotConfiguredException.class, () -> config.getString("foo"), "string"),
            () -> assertThrows(NotConfiguredException.class, () -> config.getInt("foo"), "int"),
            () -> assertThrows(NotConfiguredException.class, () -> config.getBoolean("foo"), "boolean"),
            () -> assertThrows(NotConfiguredException.class, () -> config.getDouble("foo"), "double")
        );
    }

    @Test
    void notANumber() throws Exception {
        // Given
        Configuration config = new Configuration();
        config.loadDefaults("config-test");

        // When
        assertAll(
            () -> assertThrows(ConfigurationException.class, () -> config.getInt("var1"), "int"),
            () -> assertThrows(ConfigurationException.class, () -> config.getInt("var1", 42), "int"),
            () -> assertThrows(ConfigurationException.class, () -> config.getDouble("var1"), "double"),
            () -> assertThrows(ConfigurationException.class, () -> config.getDouble("var1", 1.0), "double")
        );
    }

}
