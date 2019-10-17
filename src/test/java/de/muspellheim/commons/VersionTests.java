/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons;

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

}
