/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.util;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.With;

/**
 * A version according to <a href="https://semver.org">Semantic Versioning</a>.
 *
 * <p>Pre release and build metadata are optional and can be <code>null</code>.
 */
@Value
@RequiredArgsConstructor(staticName = "of")
public class Version implements Comparable<Version> {

  private static final String VERSION = "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)";
  private static final String PRE_RELEASE =
      "(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)"
          + "(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?";
  public static final String BUILD_METADATA = "(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$";
  private static final Pattern PATTERN = Pattern.compile(VERSION + PRE_RELEASE + BUILD_METADATA);

  /**
   * A new major version indicates incombatible changes.
   *
   * @return the major version of this version
   */
  int major;

  /**
   * A new minor version indicates combatible changes.
   *
   * @return the minor version of this version
   */
  int minor;

  /**
   * A new patch version indicates changes are only bugfixes.
   *
   * @return the patch version of this version
   */
  int patch;

  /**
   * Mark a pre-release.
   *
   * <p>A version without pre-release is newer than with.
   *
   * @return the pre-release of this version
   */
  @With String preRelease;

  /**
   * Additional build metadata.
   *
   * <p>Ignored when compare.
   *
   * @return the build metadata of this version
   */
  @With String buildMetadata;

  /**
   * Creates a simple version.
   *
   * @param major the major version
   * @param minor the minor version
   * @param patch the patch version
   * @return the version
   */
  public static Version of(int major, int minor, int patch) {
    return new Version(major, minor, patch, null, null);
  }

  /**
   * Parse text as version string.
   *
   * @param text a version string
   * @return the version
   * @throws IllegalArgumentException if text is not a version string
   */
  public static Version parse(@NonNull String text) {
    Matcher matcher = PATTERN.matcher(text);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("text is not a version string: " + text);
    }

    return new Version(
        Integer.parseInt(matcher.group(1)),
        Integer.parseInt(matcher.group(2)),
        Integer.parseInt(matcher.group(3)),
        matcher.group(4),
        matcher.group(5));
  }

  @Override
  public String toString() {
    return major
        + "."
        + minor
        + "."
        + patch
        + (preRelease != null ? " " + preRelease : "")
        + (buildMetadata != null ? " (" + buildMetadata + ")" : "");
  }

  @Override
  public int compareTo(@NonNull Version o) {
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

    if (Objects.equals(preRelease, o.preRelease)) {
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
