/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.sql;

import java.time.*;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("checkstyle:VisibilityModifier")
public class ExampleEntity {

    String familyName;
    int age;
    boolean validPLZEntry;
    LocalDateTime dayOfBirth;

}
