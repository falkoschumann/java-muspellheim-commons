/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {

    @Test
    void testSomeLibraryMethod() {
        Library classUnderTest = new Library();
        assertTrue(classUnderTest.someLibraryMethod(), "someLibraryMethod should return 'true'");
    }

}
