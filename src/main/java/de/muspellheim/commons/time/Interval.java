/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.time;

import java.time.*;
import java.time.format.*;

import lombok.*;

/**
 * Interval between two timestamps based on {@link Instant}.
 * <p>
 * Intervals are inclusive of the start and exclusive of the end.
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("checkstyle:VisibilityModifier")
public class Interval {

    /**
     * The start of this interval is inclusive.
     *
     * @return the start of this interval
     */
    @NonNull @With Instant start;

    /**
     * The end of this interval is inclusive.
     *
     * @return the end of this interval
     */
    @NonNull @With Instant end;

    /**
     * Obtains an instance of {@code Interval} from a start and end.
     *
     * @param startInclusive the start inclusive, not null
     * @param endExclusive   the end exclusive, not null
     * @return the interval, not null
     */
    public static Interval of(Instant startInclusive, Instant endExclusive) {
        if (startInclusive.isAfter(endExclusive)) {
            throw new IllegalArgumentException("start must be before end");
        }

        return new Interval(startInclusive, endExclusive);
    }

    /**
     * Obtains an instance of {@code Interval} from a text string such as {@code 2019-08-16T17:02:34Z/2019-09-09T08:50:58Z}.
     * <p>
     * The string must represent a valid interval and is parsed using
     * {@link DateTimeFormatter#ISO_INSTANT}.
     *
     * @param text the text to parse such as "2019-08-16T17:02:34Z/2019-09-09T08:50:58Z", not null
     * @return the parsed local interval, not null
     * @throws DateTimeParseException if the text cannot be parsed
     */
    public static Interval parse(@NonNull CharSequence text) {
        String[] s = text.toString().split("/");
        if (s.length != 2) {
            throw new DateTimeParseException("interval must separate start and end with /", text, 0);
        }
        Instant start = Instant.parse(s[0]);
        Instant end = Instant.parse(s[1]);
        return of(start, end);
    }

    /**
     * Checks if this interval is before the specified interval.
     *
     * @param timestamp the timestamp to compare to, not null
     * @return true if this interval is before the specified time
     */
    public boolean isBefore(Instant timestamp) {
        return end.isBefore(timestamp);
    }

    /**
     * Checks if this interval is before the specified interval.
     *
     * @param other the interval to compare to, not null
     * @return true if this interval is before the specified interval
     */
    public boolean isBefore(Interval other) {
        return end.isBefore(other.start) || end.equals(other.start);
    }

    /**
     * Checks if this interval is after the specified interval.
     *
     * @param timestamp the timestamp to compare to, not null
     * @return true if this interval is after the specified time
     */
    public boolean isAfter(Instant timestamp) {
        return start.isAfter(timestamp);
    }

    /**
     * Checks if this interval is after the specified interval.
     *
     * @param other the interval to compare to, not null
     * @return true if this interval is after the specified interval
     */
    public boolean isAfter(Interval other) {
        return start.isAfter(other.end) || start.equals(other.end);
    }

    /**
     * Checks if this interval contains the specified time.
     *
     * @param timestamp the timestamp to compare to, not null
     * @return true if this interval contains the specified time
     */
    public boolean contains(Instant timestamp) {
        return start.equals(timestamp) || (start.isBefore(timestamp) && timestamp.isBefore(end));
    }

    /**
     * Checks if this interval contains the specified interval.
     *
     * @param other the interval to compare to, not null
     * @return true if this interval contains the specified interval
     */
    public boolean contains(Interval other) {
        return (start.isBefore(other.start) || start.equals(other.start)) && (end.isAfter(other.end) || end.equals(other.end));
    }

    /**
     * Checks if this interval abuts the specified interval.
     *
     * @param other the interval to compare to, not null
     * @return true if this interval abuts the specified interval
     */
    public boolean abuts(Interval other) {
        return start.equals(other.end) || end.equals(other.start);
    }

    /**
     * Obtains the gap between this and an other interval.
     *
     * @param other the interval to compare to, not null
     * @return the interval between this an the other interval or null if there is no gap
     */
    public Interval gap(Interval other) {
        if (isBefore(other) && !end.equals(other.start)) {
            return of(end, other.start);
        } else if (isAfter(other) && !start.equals(other.end)) {
            return of(other.end, start);
        } else {
            return null;
        }
    }

    /**
     * Checks if this interval overlaps the specified interval.
     *
     * @param other the interval to compare to, not null
     * @return true if this interval overlaps the specified interval
     */
    public boolean overlaps(Interval other) {
        return equals(other) || contains(other) || overlapsStartOf(other) || overlapsEndOf(other);
    }

    private boolean overlapsStartOf(Interval other) {
        return other.start.isBefore(end) && end.isBefore(other.end);
    }

    private boolean overlapsEndOf(Interval other) {
        return start.isBefore(other.end) && other.end.isBefore(end);
    }

    /**
     * Obtains the overlap between this and an other interval.
     *
     * @param other the interval to compare to, not null
     * @return the overlapping interval or null if there is no overlap
     */
    public Interval overlap(Interval other) {
        if (equals(other)) {
            return this;
        }

        if (!overlaps(other)) {
            return null;
        }

        if (contains(other)) {
            return other;
        }

        if (other.start.isBefore(start)) {
            return of(start, other.end);
        } else {
            return of(other.start, end);
        }
    }

    /**
     * Obtains the duration of this interval.
     *
     * @return the duration.
     */
    public Duration duration() {
        return Duration.between(start, end);
    }

}
