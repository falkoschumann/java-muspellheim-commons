/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.time;

import java.time.*;
import java.time.format.*;

import lombok.*;

/**
 * Interval between two dates based on {@link LocalDate}.
 * <p>
 * Intervals are inclusive of the start and exclusive of the end.
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("checkstyle:VisibilityModifier")
public class LocalDateInterval {

    /**
     * The start of this interval is inclusive.
     *
     * @return the start of this interval
     */
    @NonNull @With LocalDate start;

    /**
     * The end of this interval is inclusive.
     *
     * @return the end of this interval
     */
    @NonNull @With LocalDate end;

    /**
     * Obtains an instance of {@code DateInterval} from a start and end.
     *
     * @param startInclusive the start inclusive, not null
     * @param endExclusive   the end exclusive, not null
     * @return the date interval, not null
     */
    public static LocalDateInterval of(LocalDate startInclusive, LocalDate endExclusive) {
        if (!startInclusive.isBefore(endExclusive)) {
            throw new IllegalArgumentException("start must be before end");
        }

        return new LocalDateInterval(startInclusive, endExclusive);
    }

    /**
     * Obtains an instance of {@code DateInterval} from a text string such as {@code 2007-12-03T10:15:30}.
     * <p>
     * The string must represent a valid interval and is parsed using
     * {@link DateTimeFormatter#ISO_LOCAL_DATE_TIME}.
     *
     * @param text the text to parse such as "2019-08-16T17:02/2019-09-09T08:50", not null
     * @return the parsed local date interval, not null
     * @throws DateTimeParseException if the text cannot be parsed
     */
    public static LocalDateInterval parse(@NonNull CharSequence text) {
        String[] s = text.toString().split("/");
        if (s.length != 2) {
            throw new DateTimeParseException("interval must separate start and end with /", text, 0);
        }
        LocalDate start = LocalDate.parse(s[0]);
        LocalDate end = LocalDate.parse(s[1]);
        return of(start, end);
    }

    /**
     * Checks if this interval is before the specified date.
     *
     * @param date the date to compare to, not null
     * @return true if this interval is before the specified date
     */
    public boolean isBefore(LocalDate date) {
        return end.isBefore(date);
    }

    /**
     * Checks if this interval is before the specified interval.
     *
     * @param other the interval to compare to, not null
     * @return true if this interval is before the specified interval
     */
    public boolean isBefore(LocalDateInterval other) {
        return end.isBefore(other.start) || end.equals(other.start);
    }

    /**
     * Checks if this interval is after the specified date.
     *
     * @param date the date to compare to, not null
     * @return true if this interval is after the specified date
     */
    public boolean isAfter(LocalDate date) {
        return start.isAfter(date);
    }

    /**
     * Checks if this interval is after the specified interval.
     *
     * @param other the interval to compare to, not null
     * @return true if this interval is after the specified interval
     */
    public boolean isAfter(LocalDateInterval other) {
        return start.isAfter(other.end) || start.equals(other.end);
    }

    /**
     * Checks if this interval contains the specified date.
     *
     * @param date the date to compare to, not null
     * @return true if this interval contains the specified date
     */
    public boolean contains(LocalDate date) {
        return start.equals(date) || (start.isBefore(date) && date.isBefore(end));
    }

    /**
     * Checks if this interval contains the specified interval.
     *
     * @param other the interval to compare to, not null
     * @return true if this interval contains the specified interval
     */
    public boolean contains(LocalDateInterval other) {
        return (start.isBefore(other.start) || start.equals(other.start)) && (end.isAfter(other.end) || end.equals(other.end));
    }

    /**
     * Checks if this interval abuts the specified interval.
     *
     * @param other the interval to compare to, not null
     * @return true if this interval abuts the specified interval
     */
    public boolean abuts(LocalDateInterval other) {
        return start.equals(other.end) || end.equals(other.start);
    }

    /**
     * Obtains the gap between this and an other interval.
     *
     * @param other the interval to compare to, not null
     * @return the interval between this an the other interval or null if there is no gap
     */
    public LocalDateInterval gap(LocalDateInterval other) {
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
    public boolean overlaps(LocalDateInterval other) {
        return equals(other) || contains(other) || overlapsStartOf(other) || overlapsEndOf(other);
    }

    private boolean overlapsStartOf(LocalDateInterval other) {
        return other.start.isBefore(end) && end.isBefore(other.end);
    }

    private boolean overlapsEndOf(LocalDateInterval other) {
        return start.isBefore(other.end) && other.end.isBefore(end);
    }

    /**
     * Obtains the overlap between this and an other interval.
     *
     * @param other the interval to compare to, not null
     * @return the overlapping interval or null if there is no overlap
     */
    public LocalDateInterval overlap(LocalDateInterval other) {
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
     * Obtains the period of this interval.
     *
     * @return the period.
     */
    public Period duration() {
        return Period.between(start, end);
    }

}
