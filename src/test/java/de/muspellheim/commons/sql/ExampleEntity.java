/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.sql;

import java.time.*;

import lombok.*;

@Data
@AllArgsConstructor
public class ExampleEntity {

    String familyName;
    int age;
    boolean validPLZEntry;
    LocalDateTime dayOfBirth;

    public ExampleEntity() {
    }

}
