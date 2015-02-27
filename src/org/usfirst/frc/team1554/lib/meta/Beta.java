package org.usfirst.frc.team1554.lib.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Developer Annotation. Marks the target as in development and
 * volatile. Staying is not guaranteed, nor is usability.
 *
 * @author Matthew
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({TYPE, METHOD, CONSTRUCTOR, PACKAGE, ANNOTATION_TYPE})
public @interface Beta {
}
