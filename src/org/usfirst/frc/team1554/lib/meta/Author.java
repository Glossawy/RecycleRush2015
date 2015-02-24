package org.usfirst.frc.team1554.lib.meta;

import java.lang.annotation.*;

/**
 * Developer Annotation. Provides Author and Contact information.
 *
 * @author Matthew
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Author {

    String name();

    String msg();

}
