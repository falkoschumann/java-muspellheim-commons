/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.util;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/** Collect information about an application or a libray. */
@Value
@RequiredArgsConstructor(staticName = "of")
public class About {

  @Getter(AccessLevel.PRIVATE)
  ResourceBundle bundle = ResourceBundle.getBundle(About.class.getName());

  /**
   * The title of an application or a library.
   *
   * @return the title
   */
  @NonNull String title;

  /**
   * The version of an application or a library.
   *
   * @return the version
   */
  @NonNull Version version;

  /**
   * The copyright of an application or a library.
   *
   * @return the copryright
   */
  @NonNull String copyright;

  /**
   * The rights of an application or a library.
   *
   * @return the rights
   */
  @NonNull String rights;

  /**
   * Create about information by reading title, version and vendor from a package.
   *
   * <p>Use current year as copyright year and a default rights.
   *
   * @param appType class from an application or libray for reading package implementation
   *     information
   * @return the about information
   */
  public static About of(Class<?> appType) {
    return of(appType, LocalDate.now().getYear());
  }

  /**
   * Create about information by reading title, version and vendor from a package.
   *
   * <p>Use a default rights.
   *
   * @param appType class from an application or libray for reading package implementation
   *     information
   * @param copyrightYear copyright year can not read from a package
   * @return the about information
   */
  public static About of(Class<?> appType, int copyrightYear) {
    Package p = appType.getPackage();
    return of(
        p.getImplementationTitle(),
        Version.parse(p.getImplementationVersion()),
        copyrightYear,
        p.getImplementationVendor());
  }

  /**
   * Create about information with specified title, version, copyright holder and copyright year.
   *
   * <p>Use a default rights.
   *
   * @param title the title
   * @param version the version
   * @param copyrightYear the copyright year
   * @param copyrightHolder the copyright holder
   * @return the about information
   */
  public static About of(String title, Version version, int copyrightYear, String copyrightHolder) {
    ResourceBundle resourceBundle = ResourceBundle.getBundle(About.class.getName());
    return new About(
        title,
        version,
        MessageFormat.format(
            resourceBundle.getString("about.copyright"),
            String.valueOf(copyrightYear),
            copyrightHolder),
        resourceBundle.getString("about.rights"));
  }

  /**
   * Get version as user readable text.
   *
   * @return user readable text of version
   */
  public String getVersionText() {
    return MessageFormat.format(bundle.getString("about.version"), version);
  }

  /** Print about information to standard out. */
  public void print() {
    System.out.println(title);
    System.out.println(getVersionText());
    System.out.println(copyright);
    System.out.println(rights);
  }
}
