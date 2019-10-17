/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.util;

import java.lang.annotation.*;

/**
 * Marks a public method as only public for testing.
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface TestSeam {
}
