/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.time;

import java.time.*;
import java.util.stream.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

class LocalDateTimeIntervalTests {

    @Test
    void created() {
        // When
        LocalDateTime start = LocalDateTime.of(2019, 10, 25, 18, 2);
        LocalDateTime end = LocalDateTime.of(2019, 12, 24, 18, 0);
        LocalDateTimeInterval interval = LocalDateTimeInterval.of(start, end);

        // Then
        assertEquals(start, interval.getStart(), "start");
        assertEquals(end, interval.getEnd(), "end");
    }

    @Test
    void createInvalid() {
        // When
        LocalDateTime start = LocalDateTime.of(2019, 10, 25, 18, 2);
        LocalDateTime end = LocalDateTime.of(2019, 12, 24, 18, 0);

        // Then
        assertThrows(IllegalArgumentException.class, () -> LocalDateTimeInterval.of(end, start), "start after end");
        assertThrows(IllegalArgumentException.class, () -> LocalDateTimeInterval.of(start, start), "start equals end");
    }

    @Test
    void duration() {
        LocalDateTime start = LocalDateTime.of(2019, 10, 25, 18, 2);
        LocalDateTime end = LocalDateTime.of(2019, 12, 24, 18, 0);
        LocalDateTimeInterval interval = LocalDateTimeInterval.of(start, end);

        // When
        Duration duration = interval.duration();

        // Then
        assertEquals(Duration.ofDays(59).plusHours(23).plusMinutes(58), duration);
    }

    @Test
    void parsed() {
        // When
        LocalDateTimeInterval parsed = LocalDateTimeInterval.parse("2019-08-16T17:02/2019-09-09T08:50");

        // Then
        LocalDateTime start = LocalDateTime.of(2019, 8, 16, 17, 2);
        LocalDateTime end = LocalDateTime.of(2019, 9, 9, 8, 50);
        LocalDateTimeInterval interval = LocalDateTimeInterval.of(start, end);
        assertEquals(interval, parsed);
    }

    @ParameterizedTest
    @MethodSource("compareWithOtherProvider")
    void compareWithOther(String testTitle,
                          LocalDateTimeInterval interval,
                          LocalDateTimeInterval other,
                          boolean equals,
                          boolean isBefore,
                          boolean isAfter,
                          boolean contains,
                          boolean abuts,
                          LocalDateTimeInterval gap,
                          boolean overlaps,
                          LocalDateTimeInterval overlap) {
        assertAll(testTitle,
            () -> assertEquals(equals, interval.equals(other), "equals"),
            () -> assertEquals(isBefore, interval.isBefore(other), "isBefore"),
            () -> assertEquals(isAfter, interval.isAfter(other), "isAfter"),
            () -> assertEquals(contains, interval.contains(other), "contains"),
            () -> assertEquals(abuts, interval.abuts(other), "abuts"),
            () -> assertEquals(gap, interval.gap(other), "gap"),
            () -> assertEquals(overlaps, interval.overlaps(other), "overlaps"),
            () -> assertEquals(overlap, interval.overlap(other), "overlap")
        );
    }

    static Stream<Arguments> compareWithOtherProvider() {
        LocalDateTime before1 = LocalDateTime.of(2019, 2, 16, 17, 2);
        LocalDateTime before2 = LocalDateTime.of(2019, 3, 9, 8, 50);
        LocalDateTime start = LocalDateTime.of(2019, 8, 16, 17, 2);
        LocalDateTime within1 = LocalDateTime.of(2019, 8, 20, 17, 2);
        LocalDateTime within2 = LocalDateTime.of(2019, 9, 1, 8, 50);
        LocalDateTime end = LocalDateTime.of(2019, 9, 9, 8, 50);
        LocalDateTime after1 = LocalDateTime.of(2019, 9, 16, 8, 50);
        LocalDateTime after2 = LocalDateTime.of(2019, 10, 9, 8, 50);

        LocalDateTimeInterval interval = LocalDateTimeInterval.of(start, end);

        // arguments: test title, interval, other, equals, isBefore, isAfter, contains, abuts, gap, overlaps, overlap
        return Stream.of(
            arguments("interval is equal other ",
                interval, LocalDateTimeInterval.of(start, end),
                true, false, false, true, false, null, true, LocalDateTimeInterval.of(start, end)),
            arguments("interval is after other",
                interval, LocalDateTimeInterval.of(before1, before2),
                false, false, true, false, false, LocalDateTimeInterval.of(before2, start), false, null),
            arguments("intervals start abuts others end",
                interval, LocalDateTimeInterval.of(before2, start),
                false, false, true, false, true, null, false, null),
            arguments("interval start overlaps other",
                interval, LocalDateTimeInterval.of(before1, within2),
                false, false, false, false, false, null, true, LocalDateTimeInterval.of(start, within2)
            ),
            arguments("intervals start abuts others start",
                interval, LocalDateTimeInterval.of(start, within1),
                false, false, false, true, false, null, true, LocalDateTimeInterval.of(start, within1)
            ),
            arguments("interval contains other",
                interval, LocalDateTimeInterval.of(within1, within2),
                false, false, false, true, false, null, true, LocalDateTimeInterval.of(within1, within2)
            ),
            arguments("intervals end abuts others end",
                interval, LocalDateTimeInterval.of(within1, end),
                false, false, false, true, false, null, true, LocalDateTimeInterval.of(within1, end)
            ),
            arguments("intervals end overlaps other",
                interval, LocalDateTimeInterval.of(within2, after1),
                false, false, false, false, false, null, true, LocalDateTimeInterval.of(within2, end)
            ),
            arguments("intervals end abuts others start",
                interval, LocalDateTimeInterval.of(end, after1),
                false, true, false, false, true, null, false, null
            ),
            arguments("interval is before other",
                interval, LocalDateTimeInterval.of(after1, after2),
                false, true, false, false, false, LocalDateTimeInterval.of(end, after1), false, null
            )
        );
    }

    @ParameterizedTest
    @MethodSource("compareWithTimeProvider")
    void compareWithTime(String testTitle,
                         LocalDateTimeInterval interval,
                         LocalDateTime time,
                         boolean isBefore,
                         boolean isAfter,
                         boolean contains) {
        assertAll(testTitle,
            () -> assertEquals(isBefore, interval.isBefore(time), "isBefore"),
            () -> assertEquals(isAfter, interval.isAfter(time), "isAfter"),
            () -> assertEquals(contains, interval.contains(time), "contains")
        );
    }

    static Stream<Arguments> compareWithTimeProvider() {
        LocalDateTime before = LocalDateTime.of(2019, 7, 8, 10, 32);
        LocalDateTime start = LocalDateTime.of(2019, 8, 16, 17, 2);
        LocalDateTime within = LocalDateTime.of(2019, 8, 20, 8, 43);
        LocalDateTime end = LocalDateTime.of(2019, 9, 9, 8, 50);
        LocalDateTime after = LocalDateTime.of(2019, 11, 11, 11, 11);

        LocalDateTimeInterval interval = LocalDateTimeInterval.of(start, end);

        // arguments: test title, interval, time, isBefore, isAfter, contains
        return Stream.of(
            arguments("interval is after time",
                interval, before,
                false, true, false
            ),
            arguments("start of interval is time",
                interval, start,
                false, false, true
            ),
            arguments("interval contains time",
                interval, within,
                false, false, true
            ),
            arguments("end of interval is time",
                interval, end,
                false, false, false
            ),
            arguments("interval is before time",
                interval, after,
                true, false, false
            )
        );
    }

}
