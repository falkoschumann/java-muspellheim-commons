/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.time;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class LocalDateIntervalTests {

  @Test
  void created() {
    // When
    LocalDate start = LocalDate.of(2019, 10, 25);
    LocalDate end = LocalDate.of(2019, 12, 24);
    LocalDateInterval interval = LocalDateInterval.of(start, end);

    // Then
    assertEquals(start, interval.getStart(), "start");
    assertEquals(end, interval.getEnd(), "end");
  }

  @Test
  void createdEmpty() {
    // When
    LocalDate start = LocalDate.of(2019, 10, 25);
    LocalDate end = LocalDate.of(2019, 10, 25);
    LocalDateInterval interval = LocalDateInterval.of(start, end);

    // Then
    assertEquals(start, interval.getStart(), "start");
    assertEquals(end, interval.getEnd(), "end");
  }

  @Test
  void createInvalid() {
    // When
    LocalDate start = LocalDate.of(2019, 10, 25);
    LocalDate end = LocalDate.of(2019, 12, 24);

    // Then
    assertThrows(
        IllegalArgumentException.class, () -> LocalDateInterval.of(end, start), "start after end");
  }

  @Test
  void duration() {
    LocalDate start = LocalDate.of(2019, 10, 25);
    LocalDate end = LocalDate.of(2019, 12, 24);
    LocalDateInterval interval = LocalDateInterval.of(start, end);

    // When
    Period period = interval.duration();

    // Then
    assertEquals(Period.ofMonths(1).plusDays(29), period);
  }

  @Test
  void parsed() {
    // When
    LocalDateInterval parsed = LocalDateInterval.parse("2019-08-16/2019-09-09");

    // Then
    LocalDate start = LocalDate.of(2019, 8, 16);
    LocalDate end = LocalDate.of(2019, 9, 9);
    LocalDateInterval interval = LocalDateInterval.of(start, end);
    assertEquals(interval, parsed);
  }

  @Test
  void yesterday() {
    // Given
    Instant fixed = LocalDateTime.of(2019, 10, 25, 18, 2).toInstant(ZoneOffset.UTC);
    Clock clock = Clock.fixed(fixed, ZoneId.systemDefault());

    // When
    LocalDateInterval yesterday = LocalDateInterval.yesterday(clock);

    // Then
    LocalDate start = LocalDate.of(2019, 10, 24);
    LocalDate end = LocalDate.of(2019, 10, 24);
    LocalDateInterval interval = LocalDateInterval.of(start, end);
    assertEquals(interval, yesterday);
  }

  @Test
  void lastDays() {
    // Given
    Instant fixed = LocalDateTime.of(2019, 10, 25, 18, 2).toInstant(ZoneOffset.UTC);
    Clock clock = Clock.fixed(fixed, ZoneId.systemDefault());

    // When
    LocalDateInterval lastDays = LocalDateInterval.lastDays(10, clock);

    // Then
    LocalDate start = LocalDate.of(2019, 10, 15);
    LocalDate end = LocalDate.of(2019, 10, 25);
    LocalDateInterval interval = LocalDateInterval.of(start, end);
    assertEquals(interval, lastDays);
  }

  @Test
  void today() {
    // Given
    Instant fixed = LocalDateTime.of(2019, 10, 25, 18, 2).toInstant(ZoneOffset.UTC);
    Clock clock = Clock.fixed(fixed, ZoneId.systemDefault());

    // When
    LocalDateInterval yesterday = LocalDateInterval.today(clock);

    // Then
    LocalDate start = LocalDate.of(2019, 10, 25);
    LocalDate end = LocalDate.of(2019, 10, 25);
    LocalDateInterval interval = LocalDateInterval.of(start, end);
    assertEquals(interval, yesterday);
  }

  @Test
  void tomorrow() {
    // Given
    Instant fixed = LocalDateTime.of(2019, 10, 25, 18, 2).toInstant(ZoneOffset.UTC);
    Clock clock = Clock.fixed(fixed, ZoneId.systemDefault());

    // When
    LocalDateInterval yesterday = LocalDateInterval.tomorrow(clock);

    // Then
    LocalDate start = LocalDate.of(2019, 10, 26);
    LocalDate end = LocalDate.of(2019, 10, 26);
    LocalDateInterval interval = LocalDateInterval.of(start, end);
    assertEquals(interval, yesterday);
  }

  @Test
  void nextDays() {
    // Given
    Instant fixed = LocalDateTime.of(2019, 10, 25, 18, 2).toInstant(ZoneOffset.UTC);
    Clock clock = Clock.fixed(fixed, ZoneId.systemDefault());

    // When
    LocalDateInterval nextDays = LocalDateInterval.nextDays(7, clock);

    // Then
    LocalDate start = LocalDate.of(2019, 10, 25);
    LocalDate end = LocalDate.of(2019, 11, 1);
    LocalDateInterval interval = LocalDateInterval.of(start, end);
    assertEquals(interval, nextDays);
  }

  @Test
  void testToString() {
    // Given
    LocalDate start = LocalDate.of(2019, 8, 16);
    LocalDate end = LocalDate.of(2019, 9, 9);
    LocalDateInterval interval = LocalDateInterval.of(start, end);

    // When
    String s = interval.toString();

    // Then
    assertEquals("2019-08-16/2019-09-09", s);
  }

  @ParameterizedTest
  @MethodSource("compareWithOtherProvider")
  void compareWithOther(
      String testTitle,
      LocalDateInterval interval,
      LocalDateInterval other,
      boolean equals,
      boolean isBefore,
      boolean isAfter,
      boolean contains,
      boolean abuts,
      LocalDateInterval gap,
      boolean overlaps,
      LocalDateInterval overlap) {
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
    LocalDate before1 = LocalDate.of(2019, 2, 16);
    LocalDate before2 = LocalDate.of(2019, 3, 9);
    LocalDate start = LocalDate.of(2019, 8, 16);
    LocalDate within1 = LocalDate.of(2019, 8, 20);
    LocalDate within2 = LocalDate.of(2019, 9, 1);
    LocalDate end = LocalDate.of(2019, 9, 9);
    LocalDate after1 = LocalDate.of(2019, 9, 16);
    LocalDate after2 = LocalDate.of(2019, 10, 9);

    LocalDateInterval interval = LocalDateInterval.of(start, end);

    // arguments: test title, interval, other, equals, isBefore, isAfter, contains, abuts, gap,
    // overlaps, overlap
    return Stream.of(
        arguments(
            "interval is equal other ",
            interval,
            LocalDateInterval.of(start, end),
            true,
            false,
            false,
            true,
            false,
            null,
            true,
            LocalDateInterval.of(start, end)),
        arguments(
            "interval is after other",
            interval,
            LocalDateInterval.of(before1, before2),
            false,
            false,
            true,
            false,
            false,
            LocalDateInterval.of(before2, start),
            false,
            null),
        arguments(
            "intervals start abuts others end",
            interval,
            LocalDateInterval.of(before2, start),
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
            LocalDateInterval.of(before1, within2),
            false,
            false,
            false,
            false,
            false,
            null,
            true,
            LocalDateInterval.of(start, within2)),
        arguments(
            "intervals start abuts others start",
            interval,
            LocalDateInterval.of(start, within1),
            false,
            false,
            false,
            true,
            false,
            null,
            true,
            LocalDateInterval.of(start, within1)),
        arguments(
            "interval contains other",
            interval,
            LocalDateInterval.of(within1, within2),
            false,
            false,
            false,
            true,
            false,
            null,
            true,
            LocalDateInterval.of(within1, within2)),
        arguments(
            "intervals end abuts others end",
            interval,
            LocalDateInterval.of(within1, end),
            false,
            false,
            false,
            true,
            false,
            null,
            true,
            LocalDateInterval.of(within1, end)),
        arguments(
            "intervals end overlaps other",
            interval,
            LocalDateInterval.of(within2, after1),
            false,
            false,
            false,
            false,
            false,
            null,
            true,
            LocalDateInterval.of(within2, end)),
        arguments(
            "intervals end abuts others start",
            interval,
            LocalDateInterval.of(end, after1),
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
            LocalDateInterval.of(after1, after2),
            false,
            true,
            false,
            false,
            false,
            LocalDateInterval.of(end, after1),
            false,
            null));
  }

  @ParameterizedTest
  @MethodSource("compareWithTimeProvider")
  void compareWithTime(
      String testTitle,
      LocalDateInterval interval,
      LocalDate time,
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
    LocalDate before = LocalDate.of(2019, 7, 8);
    LocalDate start = LocalDate.of(2019, 8, 16);
    LocalDate within = LocalDate.of(2019, 8, 20);
    LocalDate end = LocalDate.of(2019, 9, 9);
    LocalDate after = LocalDate.of(2019, 11, 11);

    LocalDateInterval interval = LocalDateInterval.of(start, end);

    // arguments: test title, interval, time, isBefore, isAfter, contains
    return Stream.of(
        arguments("interval is after time", interval, before, false, true, false),
        arguments("start of interval is time", interval, start, false, false, true),
        arguments("interval contains time", interval, within, false, false, true),
        arguments("end of interval is time", interval, end, false, false, false),
        arguments("interval is before time", interval, after, true, false, false));
  }
}
