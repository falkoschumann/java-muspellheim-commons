/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.time;

import java.time.*;
import java.time.format.*;
import javax.annotation.*;

import lombok.*;

/**
 * Interval between two times based on {@link LocalDateTime}.
 * <p>
 * Intervals are inclusive of the start and exclusive of the end.
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("checkstyle:VisibilityModifier")
public class DateTimeInterval {

    // TODO TimeInterval
    // TODO DateInterval
    // TODO Interval (Instant)

    @NonNull @With LocalDateTime start;
    @NonNull @With LocalDateTime end;

    /**
     * Obtains an instance of {@code DateTimeInterval} from a start and end.
     *
     * @param startInclusive the start inclusive, not null
     * @param endExclusive   the end exclusive, not null
     * @return the date-time interval, not null
     */
    public static DateTimeInterval of(LocalDateTime startInclusive, LocalDateTime endExclusive) {
        if (!startInclusive.isBefore(endExclusive)) {
            throw new IllegalArgumentException("start must be before end");
        }

        return new DateTimeInterval(startInclusive, endExclusive);
    }

    /**
     * Obtains an instance of {@code DateTimeInterval} from a text string such as {@code 2007-12-03T10:15:30}.
     * <p>
     * The string must represent a valid interval and is parsed using
     * {@link java.time.format.DateTimeFormatter#ISO_LOCAL_DATE_TIME}.
     *
     * @param text the text to parse such as "2019-08-16T17:02/2019-09-09T08:50", not null
     * @return the parsed local date-time interval, not null
     * @throws DateTimeParseException if the text cannot be parsed
     */
    public static DateTimeInterval parse(@Nonnull CharSequence text) {
        String[] s = text.toString().split("/");
        if (s.length != 2) {
            throw new DateTimeParseException("interval must separate start and end with /", text, 0);
        }
        LocalDateTime start = LocalDateTime.parse(s[0]);
        LocalDateTime end = LocalDateTime.parse(s[1]);
        return of(start, end);
    }

    /**
     * Checks if this interval is before the specified date-time.
     *
     * @param time the time to compare to, not null
     * @return true if this interval is before the specified time
     */
    public boolean isBefore(LocalDateTime time) {
        return end.isBefore(time);
    }

    /**
     * Checks if this interval is before the specified interval.
     *
     * @param other the interval to compare to, not null
     * @return true if this interval is before the specified interval
     */
    public boolean isBefore(DateTimeInterval other) {
        return end.isBefore(other.start) || end.equals(other.start);
    }

    /**
     * Checks if this interval is after the specified date-time.
     *
     * @param time the time to compare to, not null
     * @return true if this interval is after the specified time
     */
    public boolean isAfter(LocalDateTime time) {
        return start.isAfter(time);
    }

    /**
     * Checks if this interval is after the specified interval.
     *
     * @param other the interval to compare to, not null
     * @return true if this interval is after the specified interval
     */
    public boolean isAfter(DateTimeInterval other) {
        return start.isAfter(other.end) || start.equals(other.end);
    }

    /**
     * Checks if this interval contains the specified time.
     *
     * @param time the time to compare to, not null
     * @return true if this interval contains the specified time
     */
    public boolean contains(LocalDateTime time) {
        return start.equals(time) || (start.isBefore(time) && time.isBefore(end));
    }

    /**
     * Checks if this interval contains the specified interval.
     *
     * @param other the interval to compare to, not null
     * @return true if this interval contains the specified interval
     */
    public boolean contains(DateTimeInterval other) {
        return (start.isBefore(other.start) || start.equals(other.start)) && (end.isAfter(other.end) || end.equals(other.end));
    }

    /**
     * Checks if this interval abuts the specified interval.
     *
     * @param other the interval to compare to, not null
     * @return true if this interval abuts the specified interval
     */
    public boolean abuts(DateTimeInterval other) {
        return start.equals(other.end) || end.equals(other.start);
    }

    /**
     * Obtains the gap between this and an other interval.
     *
     * @param other the interval to compare to, not null
     * @return the interval between this an the other interval or null if there is no gap
     */
    public DateTimeInterval gap(DateTimeInterval other) {
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
    public boolean overlaps(DateTimeInterval other) {
        return equals(other) || contains(other) || overlapsStartOf(other) || overlapsEndOf(other);
    }

    private boolean overlapsStartOf(DateTimeInterval other) {
        return other.start.isBefore(end) && end.isBefore(other.end);
    }

    private boolean overlapsEndOf(DateTimeInterval other) {
        return start.isBefore(other.end) && other.end.isBefore(end);
    }

    /**
     * Obtains the overlap between this and an other interval.
     *
     * @param other the interval to compare to, not null
     * @return the overlapping interval or null if there is no overlap
     */
    public DateTimeInterval overlap(DateTimeInterval other) {
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
