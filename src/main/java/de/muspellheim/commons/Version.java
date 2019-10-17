/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons;

import java.util.*;
import java.util.regex.*;

import lombok.*;

/**
 * A version according to <a href="https://semver.org">Semantic Versioning</a>.
 * <p>
 * Pre release and build metadata are optional and can be null.
 */
@Value
@RequiredArgsConstructor(staticName = "of")
@SuppressWarnings("checkstyle:VisibilityModifier")
public class Version {

    // TODO Implement Comparable

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

}
