/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.sql;

import org.junit.jupiter.api.*;

@Tag("postgres")
class ConstructorResultSetMapperTests extends BaseResultSetMapperTests {

    @BeforeEach
    void setUp() {
        super.setUp();
        mapper = new ConstructorResultSetMapper<>(ExampleEntity.class);
    }

}
