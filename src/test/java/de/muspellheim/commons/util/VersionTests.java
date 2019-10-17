/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.util;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class VersionTests {

    @Test
    void construct() {
        // When
        Version version = Version.of(1, 2, 3);

        // Then
        assertAll(
            () -> assertEquals(1, version.getMajor(), "Major"),
            () -> assertEquals(2, version.getMinor(), "Minor"),
            () -> assertEquals(3, version.getPatch(), "Patch")
        );
    }

    @Test
    void parse() {
        // When
        Version completeVersion = Version.parse("1.2.3-alpha+001");

        // Then
        assertAll(
            () -> assertEquals(1, completeVersion.getMajor(), "Major"),
            () -> assertEquals(2, completeVersion.getMinor(), "Minor"),
            () -> assertEquals(3, completeVersion.getPatch(), "Patch"),
            () -> assertEquals("alpha", completeVersion.getPreRelease(), "Pre-released"),
            () -> assertEquals("001", completeVersion.getBuildMetadata(), "Build metadata"),
            () -> assertEquals("1.2.3 alpha (001)", completeVersion.toString(), "String")
        );
    }

    @Test
    void semanticVersioning() {
        assertEquals("1.2.3", Version.parse("1.2.3").toString(), "Common");
        assertEquals("1.2.3 alpha", Version.parse("1.2.3-alpha").toString(), "Only pre-released");
        assertEquals("1.2.3 (001)", Version.parse("1.2.3+001").toString(), "Only build metadata");
    }

    @Test
    void textIsNotAVersion() {
        // When
        Exception e = assertThrows(IllegalArgumentException.class, () -> Version.parse("1.2"));

        // Then
        assertEquals("text is not a version", e.getMessage());
    }

    @Test
    void compare() {
        Version a = Version.of(1, 0, 0);
        Version a1 = Version.of(1, 0, 0);
        Version b = Version.of(2, 0, 0);
        Version c = Version.of(2, 1, 0);
        Version d = Version.of(2, 1, 1);

        assertAll(
            () -> assertEquals(0, a.compareTo(a1), "1.0.0 = 1.0.0"),
            () -> assertTrue(a.compareTo(b) < 0, "1.0.0 < 2.0.0"),
            () -> assertTrue(b.compareTo(c) < 0, "2.0.0 < 2.1.0"),
            () -> assertTrue(c.compareTo(d) < 0, "2.1.0 < 2.1.1")
        );
    }

    @Test
    void compareWithNull() {
        // Given
        Version v = Version.of(1, 0, 0);

        // When
        assertThrows(NullPointerException.class, () -> v.compareTo(null));
    }

    @Test
    void withoutPreReleaseIsNewer() {
        Version a = Version.parse("1.0.0-alpha");
        Version b = Version.parse("1.0.0");

        assertTrue(a.compareTo(b) < 0, "1.0.0-alpha < 1.0.0");
    }

    @Test
    void comparePreReleases() {
        Version a = Version.parse("1.0.0-alpha");
        Version b = Version.parse("1.0.0-alpha.1");
        Version c = Version.parse("1.0.0-alpha.beta");
        Version d = Version.parse("1.0.0-beta");
        Version e = Version.parse("1.0.0-beta.2");
        Version f = Version.parse("1.0.0-beta.11");
        Version g = Version.parse("1.0.0-rc.1");
        Version h = Version.parse("1.0.0");

        assertAll(
            () -> assertTrue(a.compareTo(b) < 0, "1.0.0-alpha < 1.0.0-alpha.1"),
            () -> assertTrue(b.compareTo(c) < 0, "1.0.0-alpha.1 < 1.0.0-alpha.beta"),
            () -> assertTrue(c.compareTo(d) < 0, "1.0.0-alpha.beta < 1.0.0-beta"),
            () -> assertTrue(d.compareTo(e) < 0, "1.0.0-beta < 1.0.0-beta.2"),
            () -> assertTrue(e.compareTo(f) < 0, "1.0.0-beta.2 < 1.0.0-beta.11"),
            () -> assertTrue(f.compareTo(g) < 0, "1.0.0-beta.11 < 1.0.0-rc.1"),
            () -> assertTrue(g.compareTo(h) < 0, "1.0.0-rc.1 < 1.0.0")
        );
    }

}
