/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.util;

import java.text.*;
import java.time.*;
import java.util.*;

import lombok.*;

/**
 * Collect information about an application or a libray.
 */
@Value
@RequiredArgsConstructor(staticName = "of")
@SuppressWarnings("checkstyle:VisibilityModifier")
public class About {

    private static final String ABOUT_VERSION = "about.version";
    private static final String ABOUT_COPYRIGHT = "about.copyright";
    private static final String ABOUT_RIGHTS = "about.rights";

    @Getter(AccessLevel.PRIVATE)
    ResourceBundle resourceBundle = ResourceBundle.getBundle(About.class.getName());

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
     * <p>
     * Use current year as copyright year and a default rights.
     *
     * @param appType class from an application or libray for reading package implementation information
     * @return the about information
     */
    public static About of(Class<?> appType) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(About.class.getName());
        Package p = appType.getPackage();
        return new About(
            p.getImplementationTitle(),
            Version.parse(p.getImplementationVersion()),
            MessageFormat.format(resourceBundle.getString(ABOUT_COPYRIGHT), LocalDate.now().getYear(), p.getImplementationVendor()),
            resourceBundle.getString(ABOUT_RIGHTS)
        );
    }

    /**
     * Create about information by reading title, version and vendor from a package.
     * <p>
     * Use a default rights.
     *
     * @param appType       class from an application or libray for reading package implementation information
     * @param copyrightYear copyright year can not read from a package
     * @return the about information
     * @deprecated use {@link #of(Class, int)}
     */
    @Deprecated
    public static About of(Class<?> appType, String copyrightYear) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(About.class.getName());
        Package p = appType.getPackage();
        return new About(
            p.getImplementationTitle(),
            Version.parse(p.getImplementationVersion()),
            MessageFormat.format(resourceBundle.getString(ABOUT_COPYRIGHT), copyrightYear, p.getImplementationVendor()),
            resourceBundle.getString(ABOUT_RIGHTS)
        );
    }

    /**
     * Create about information by reading title, version and vendor from a package.
     * <p>
     * Use a default rights.
     *
     * @param appType       class from an application or libray for reading package implementation information
     * @param copyrightYear copyright year can not read from a package
     * @return the about information
     */
    public static About of(Class<?> appType, int copyrightYear) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(About.class.getName());
        Package p = appType.getPackage();
        return new About(
            p.getImplementationTitle(),
            Version.parse(p.getImplementationVersion()),
            MessageFormat.format(resourceBundle.getString(ABOUT_COPYRIGHT), copyrightYear, p.getImplementationVendor()),
            resourceBundle.getString(ABOUT_RIGHTS)
        );
    }

    /**
     * Create about information with specified title, version, copyright holder and copyright year.
     * <p>
     * Use a default rights.
     *
     * @param title           the title
     * @param version         the version
     * @param copyrightYear   the copyright year
     * @param copyrightHolder the copyright holder
     * @return the about information
     */
    public static About of(String title, Version version, int copyrightYear, String copyrightHolder) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(About.class.getName());
        return new About(
            title,
            version,
            MessageFormat.format(resourceBundle.getString(ABOUT_COPYRIGHT), copyrightYear, copyrightHolder),
            resourceBundle.getString(ABOUT_RIGHTS)
        );
    }

    /**
     * Get version as user readable text.
     *
     * @return user readable text of version
     */
    public String getVersionText() {
        return MessageFormat.format(resourceBundle.getString(ABOUT_VERSION), version);
    }

    /**
     * Print about information to standard out.
     */
    public void print() {
        System.out.println(title);
        System.out.println(getVersionText());
        System.out.println(copyright);
        System.out.println(rights);
    }

}
