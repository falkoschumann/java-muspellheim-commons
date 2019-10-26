/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.time;

import java.time.*;

import lombok.*;

@Value
@RequiredArgsConstructor(staticName = "of")
@SuppressWarnings("checkstyle:VisibilityModifier")
public class Interval {

    @NonNull @With Instant start;
    @NonNull @With Instant end;

}
