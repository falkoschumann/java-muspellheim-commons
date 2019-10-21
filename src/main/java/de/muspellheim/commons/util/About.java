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

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(About.class.getName());

    /**
     * The title of an application or a library.
     *
     * @return the title.
     */
    @NonNull
    String title;

    /**
     * The version of an application or a library.
     *
     * @return the version.
     */
    @NonNull
    Version version;

    /**
     * The copyright of an application or a library.
     *
     * @return the copryright.
     */
    @NonNull
    String copyright;

    /**
     * The rights of an application or a library.
     *
     * @return the rights.
     */
    @NonNull
    String rights;

    /**
     * Create about information by reading title, version and vendor from a package.
     *
     * @param appType class from an application or libray for reading package implementation information.
     * @return the about information.
     */
    public static About of(Class<?> appType) {
        Package p = appType.getPackage();
        return new About(
            p.getImplementationTitle(),
            Version.parse(p.getImplementationVersion()),
            MessageFormat.format(RESOURCE_BUNDLE.getString("about.copyright"), LocalDate.now().getYear(), p.getImplementationVendor()),
            RESOURCE_BUNDLE.getString("about.rights")
        );
    }

    /**
     * Create about information by reading title, version and vendor from a package.
     *
     * @param appType       class from an application or libray for reading package implementation information.
     * @param copyrightYear copyright year can not read from a package.
     * @return the about information.
     */
    public static About of(Class<?> appType, String copyrightYear) {
        Package p = appType.getPackage();
        return new About(
            p.getImplementationTitle(),
            Version.parse(p.getImplementationVersion()),
            MessageFormat.format(RESOURCE_BUNDLE.getString("about.copyright"), copyrightYear, p.getImplementationVendor()),
            RESOURCE_BUNDLE.getString("about.rights")
        );
    }

    /**
     * Get version as user readable text.
     *
     * @return user readable text of version.
     */
    public String getVersionText() {
        return MessageFormat.format(RESOURCE_BUNDLE.getString("about.version"), version);
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