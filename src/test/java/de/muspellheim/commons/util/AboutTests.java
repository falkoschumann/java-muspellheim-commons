/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.util;

import java.util.*;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class AboutTests {

    @Test
    void created() {
        // Given
        Locale.setDefault(Locale.GERMAN);

        // When
        About about = About.of("My App", Version.of(1, 0, 0), 2019, "ACME Ltd.");

        // Then
        assertAll(
            () -> assertEquals("My App", about.getTitle()),
            () -> assertEquals("Version 1.0.0", about.getVersionText()),
            () -> assertEquals("Copyright © 2019 ACME Ltd.", about.getCopyright()),
            () -> assertEquals("Alle Rechte vorbehalten.", about.getRights())
        );
    }

    @Test
    void loadFromManifest() {
        // Given
        Locale.setDefault(Locale.GERMAN);

        // When
        About about = About.of(Test.class, 2019);

        // Then
        assertAll(
            () -> assertEquals("junit-jupiter-api", about.getTitle()),
            () -> assertEquals("Version 5.5.2", about.getVersionText()),
            () -> assertEquals("Copyright © 2019 junit.org", about.getCopyright()),
            () -> assertEquals("Alle Rechte vorbehalten.", about.getRights())
        );
    }

}
