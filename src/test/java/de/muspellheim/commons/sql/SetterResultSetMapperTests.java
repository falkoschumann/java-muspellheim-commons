/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.sql;

import org.junit.jupiter.api.*;

@Tag("postgres")
class SetterResultSetMapperTests extends BaseResultSetMapperTests {

    @BeforeEach
    void setUp() {
        super.setUp();
        mapper = new SetterResultSetMapper<>(ExampleEntity.class);
    }

}
