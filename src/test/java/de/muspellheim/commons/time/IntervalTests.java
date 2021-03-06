/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.time;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class IntervalTests {

  @Test
  void created() {
    // When
    Instant start = LocalDateTime.of(2019, 10, 25, 18, 2).toInstant(ZoneOffset.UTC);
    Instant end = LocalDateTime.of(2019, 12, 24, 18, 0).toInstant(ZoneOffset.UTC);
    Interval interval = Interval.of(start, end);

    // Then
    assertEquals(start, interval.getStart(), "start");
    assertEquals(end, interval.getEnd(), "end");
  }

  @Test
  void createdEmpty() {
    // When
    Instant start = LocalDateTime.of(2019, 10, 25, 18, 2).toInstant(ZoneOffset.UTC);
    Instant end = LocalDateTime.of(2019, 10, 25, 18, 2).toInstant(ZoneOffset.UTC);
    Interval interval = Interval.of(start, end);

    // Then
    assertEquals(start, interval.getStart(), "start");
    assertEquals(end, interval.getEnd(), "end");
  }

  @Test
  void createInvalid() {
    // When
    Instant start = LocalDateTime.of(2019, 10, 25, 18, 2).toInstant(ZoneOffset.UTC);
    Instant end = LocalDateTime.of(2019, 12, 24, 18, 0).toInstant(ZoneOffset.UTC);

    // Then
    assertThrows(IllegalArgumentException.class, () -> Interval.of(end, start), "start after end");
  }

  @Test
  void duration() {
    Instant start = LocalDateTime.of(2019, 10, 25, 18, 2).toInstant(ZoneOffset.UTC);
    Instant end = LocalDateTime.of(2019, 12, 24, 18, 0).toInstant(ZoneOffset.UTC);
    Interval interval = Interval.of(start, end);

    // When
    Duration duration = interval.duration();

    // Then
    assertEquals(Duration.ofDays(59).plusHours(23).plusMinutes(58), duration);
  }

  @Test
  void parsed() {
    // When
    Interval parsed = Interval.parse("2019-08-16T17:02:34Z/2019-09-09T08:50:58Z");

    // Then
    Instant start = LocalDateTime.of(2019, 8, 16, 17, 2, 34).toInstant(ZoneOffset.UTC);
    Instant end = LocalDateTime.of(2019, 9, 9, 8, 50, 58).toInstant(ZoneOffset.UTC);
    Interval interval = Interval.of(start, end);
    assertEquals(interval, parsed);
  }

  @Test
  void testToString() {
    // Given
    Instant start = LocalDateTime.of(2019, 8, 16, 17, 2, 34).toInstant(ZoneOffset.UTC);
    Instant end = LocalDateTime.of(2019, 9, 9, 8, 50, 58).toInstant(ZoneOffset.UTC);
    Interval interval = Interval.of(start, end);

    // When
    String s = interval.toString();

    // Then
    assertEquals("2019-08-16T17:02:34Z/2019-09-09T08:50:58Z", s);
  }

  @ParameterizedTest
  @MethodSource("compareWithOtherProvider")
  void compareWithOther(
      String testTitle,
      Interval interval,
      Interval other,
      boolean equals,
      boolean isBefore,
      boolean isAfter,
      boolean contains,
      boolean abuts,
      Interval gap,
      boolean overlaps,
      Interval overlap) {
    assertAll(
        testTitle,
        () -> assertEquals(equals, interval.equals(other), "equals"),
        () -> assertEquals(isBefore, interval.isBefore(other), "isBefore"),
        () -> assertEquals(isAfter, interval.isAfter(other), "isAfter"),
        () -> assertEquals(contains, interval.contains(other), "contains"),
        () -> assertEquals(abuts, interval.abuts(other), "abuts"),
        () -> assertEquals(gap, interval.gap(other), "gap"),
        () -> assertEquals(overlaps, interval.overlaps(other), "overlaps"),
        () -> assertEquals(overlap, interval.overlap(other), "overlap"));
  }

  static Stream<Arguments> compareWithOtherProvider() {
    Instant before1 = LocalDateTime.of(2019, 2, 16, 17, 2).toInstant(ZoneOffset.UTC);
    Instant before2 = LocalDateTime.of(2019, 3, 9, 8, 50).toInstant(ZoneOffset.UTC);
    Instant start = LocalDateTime.of(2019, 8, 16, 17, 2).toInstant(ZoneOffset.UTC);
    Instant within1 = LocalDateTime.of(2019, 8, 20, 17, 2).toInstant(ZoneOffset.UTC);
    Instant within2 = LocalDateTime.of(2019, 9, 1, 8, 50).toInstant(ZoneOffset.UTC);
    Instant end = LocalDateTime.of(2019, 9, 9, 8, 50).toInstant(ZoneOffset.UTC);
    Instant after1 = LocalDateTime.of(2019, 9, 16, 8, 50).toInstant(ZoneOffset.UTC);
    Instant after2 = LocalDateTime.of(2019, 10, 9, 8, 50).toInstant(ZoneOffset.UTC);

    Interval interval = Interval.of(start, end);

    // arguments: test title, interval, other, equals, isBefore, isAfter, contains, abuts, gap,
    // overlaps, overlap
    return Stream.of(
        arguments(
            "interval is equal other ",
            interval,
            Interval.of(start, end),
            true,
            false,
            false,
            true,
            false,
            null,
            true,
            Interval.of(start, end)),
        arguments(
            "interval is after other",
            interval,
            Interval.of(before1, before2),
            false,
            false,
            true,
            false,
            false,
            Interval.of(before2, start),
            false,
            null),
        arguments(
            "intervals start abuts others end",
            interval,
            Interval.of(before2, start),
            false,
            false,
            true,
            false,
            true,
            null,
            false,
            null),
        arguments(
            "interval start overlaps other",
            interval,
            Interval.of(before1, within2),
            false,
            false,
            false,
            false,
            false,
            null,
            true,
            Interval.of(start, within2)),
        arguments(
            "intervals start abuts others start",
            interval,
            Interval.of(start, within1),
            false,
            false,
            false,
            true,
            false,
            null,
            true,
            Interval.of(start, within1)),
        arguments(
            "interval contains other",
            interval,
            Interval.of(within1, within2),
            false,
            false,
            false,
            true,
            false,
            null,
            true,
            Interval.of(within1, within2)),
        arguments(
            "intervals end abuts others end",
            interval,
            Interval.of(within1, end),
            false,
            false,
            false,
            true,
            false,
            null,
            true,
            Interval.of(within1, end)),
        arguments(
            "intervals end overlaps other",
            interval,
            Interval.of(within2, after1),
            false,
            false,
            false,
            false,
            false,
            null,
            true,
            Interval.of(within2, end)),
        arguments(
            "intervals end abuts others start",
            interval,
            Interval.of(end, after1),
            false,
            true,
            false,
            false,
            true,
            null,
            false,
            null),
        arguments(
            "interval is before other",
            interval,
            Interval.of(after1, after2),
            false,
            true,
            false,
            false,
            false,
            Interval.of(end, after1),
            false,
            null));
  }

  @ParameterizedTest
  @MethodSource("compareWithTimeProvider")
  void compareWithTime(
      String testTitle,
      Interval interval,
      Instant time,
      boolean isBefore,
      boolean isAfter,
      boolean contains) {
    assertAll(
        testTitle,
        () -> assertEquals(isBefore, interval.isBefore(time), "isBefore"),
        () -> assertEquals(isAfter, interval.isAfter(time), "isAfter"),
        () -> assertEquals(contains, interval.contains(time), "contains"));
  }

  static Stream<Arguments> compareWithTimeProvider() {
    Instant before = LocalDateTime.of(2019, 7, 8, 10, 32).toInstant(ZoneOffset.UTC);
    Instant start = LocalDateTime.of(2019, 8, 16, 17, 2).toInstant(ZoneOffset.UTC);
    Instant within = LocalDateTime.of(2019, 8, 20, 8, 43).toInstant(ZoneOffset.UTC);
    Instant end = LocalDateTime.of(2019, 9, 9, 8, 50).toInstant(ZoneOffset.UTC);
    Instant after = LocalDateTime.of(2019, 11, 11, 11, 11).toInstant(ZoneOffset.UTC);

    Interval interval = Interval.of(start, end);

    // arguments: test title, interval, time, isBefore, isAfter, contains
    return Stream.of(
        arguments("interval is after time", interval, before, false, true, false),
        arguments("start of interval is time", interval, start, false, false, true),
        arguments("interval contains time", interval, within, false, false, true),
        arguments("end of interval is time", interval, end, false, false, false),
        arguments("interval is before time", interval, after, true, false, false));
  }
}
