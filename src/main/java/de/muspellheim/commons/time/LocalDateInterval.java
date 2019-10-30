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
        if (startInclusive.isAfter(endExclusive)) {
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
     * Obtains a instance of {@code DateInterval} for yesterday.
     *
     * @return the yesterdays interval
     */
    public static LocalDateInterval yesterday() {
        return yesterday(Clock.systemDefaultZone());
    }

    /**
     * Obtains a instance of {@code DateInterval} for yesterday.
     *
     * @param clock the clock to use
     * @return the yesterdays interval
     */
    public static LocalDateInterval yesterday(Clock clock) {
        LocalDate yesterday = LocalDate.now(clock).minusDays(1);
        return of(yesterday, yesterday);
    }

    /**
     * Obtains a instance of {@code DateInterval} for today.
     *
     * @return the todays interval
     */
    public static LocalDateInterval today() {
        return tomorrow(Clock.systemDefaultZone());
    }

    /**
     * Obtains a instance of {@code DateInterval} for today.
     *
     * @param clock the clock to use
     * @return the todays interval
     */
    public static LocalDateInterval today(Clock clock) {
        LocalDate today = LocalDate.now(clock);
        return of(today, today);
    }

    /**
     * Obtains a instance of {@code DateInterval} for tomorrow.
     *
     * @return the tomorrows interval
     */
    public static LocalDateInterval tomorrow() {
        return tomorrow(Clock.systemDefaultZone());
    }

    /**
     * Obtains a instance of {@code DateInterval} for tomorrow.
     *
     * @param clock the clock to use
     * @return the tomorrows interval
     */
    public static LocalDateInterval tomorrow(Clock clock) {
        LocalDate tomorrow = LocalDate.now(clock).plusDays(1);
        return of(tomorrow, tomorrow);
    }

    /**
     * Obtains a instance of {@code DateInterval} for the next days.
     *
     * @param days the number of next days
     * @return the next days interval
     */
    public static LocalDateInterval nextDays(int days) {
        return nextDays(days, Clock.systemDefaultZone());
    }

    /**
     * Obtains a instance of {@code DateInterval} for the next days.
     *
     * @param days  the number of next days
     * @param clock the clock to use
     * @return the next days interval
     */
    public static LocalDateInterval nextDays(int days, Clock clock) {
        LocalDate d = LocalDate.now(clock);
        if (days >= 0) {
            return of(d, d.plusDays(days));
        } else {
            return of(d.minusDays(-days), d);
        }
    }

    /**
     * Obtains a instance of {@code DateInterval} for the last days.
     *
     * @param days the number of last days
     * @return the last days interval
     */
    public static LocalDateInterval lastDays(int days) {
        return lastDays(days, Clock.systemDefaultZone());
    }

    /**
     * Obtains a instance of {@code DateInterval} for the last days.
     *
     * @param days  the number of last days
     * @param clock the clock to use
     * @return the last days interval
     */
    public static LocalDateInterval lastDays(int days, Clock clock) {
        return nextDays(-days, clock);
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
