/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.util;

import java.util.*;
import java.util.regex.*;

import lombok.*;

/**
 * A version according to <a href="https://semver.org">Semantic Versioning</a>.
 * <p>
 * Pre release and build metadata are optional and can be <code>null</code>.
 */
@Value
@RequiredArgsConstructor(staticName = "of")
@SuppressWarnings("checkstyle:VisibilityModifier")
public class Version implements Comparable<Version> {

    private static final Pattern PATTERN = Pattern.compile("^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)"
        + "(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?"
        + "(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$");

    int major;

    int minor;

    int patch;

    @With
    String preRelease;

    @With
    String buildMetadata;

    public static Version of(int major, int minor, int patch) {
        return new Version(major, minor, patch, null, null);
    }

    public static Version parse(String text) {
        Objects.requireNonNull(text, "text");
        Matcher matcher = PATTERN.matcher(text);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("text is not a version");
        }

        return new Version(
            Integer.parseInt(matcher.group(1)),
            Integer.parseInt(matcher.group(2)),
            Integer.parseInt(matcher.group(3)),
            matcher.group(4),
            matcher.group(5)
        );
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch
            + (preRelease != null ? " " + preRelease : "")
            + (buildMetadata != null ? " (" + buildMetadata + ")" : "");
    }

    @Override
    public int compareTo(Version o) {
        Objects.requireNonNull(o, "o");

        if (this == o) {
            return 0;
        }

        int result = Integer.compare(major, o.major);
        if (result != 0) {
            return result;
        }

        result = Integer.compare(minor, o.minor);
        if (result != 0) {
            return result;
        }

        result = Integer.compare(patch, o.patch);
        if (result != 0) {
            return result;
        }

        if (preRelease == o.preRelease) {
            return 0;
        }
        if (preRelease == null) {
            return 1;
        }
        if (o.preRelease == null) {
            return -1;
        }
        return comparePreRelease(preRelease, o.preRelease);
    }

    private static int comparePreRelease(String pr1, String pr2) {
        int result;
        String[] pr1a = pr1.split("\\.");
        String[] pr2a = pr2.split("\\.");
        int lim = Math.min(pr1a.length, pr2a.length);
        for (int i = 0; i < lim; i++) {
            String a = pr1a[i];
            String b = pr2a[i];
            try {
                int a1 = Integer.parseInt(a);
                int b1 = Integer.parseInt(b);
                result = Integer.compare(a1, b1);
                if (result != 0) {
                    return result;
                }
            } catch (NumberFormatException ignored) {
                result = a.compareTo(b);
                if (result != 0) {
                    return result;
                }
            }
        }
        return pr1a.length - pr2a.length;
    }

}
