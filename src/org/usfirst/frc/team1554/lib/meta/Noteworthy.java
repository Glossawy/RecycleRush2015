package org.usfirst.frc.team1554.lib.meta;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * A simple developer annotation that indicates that there is REALLY something of note that a comment simply cannot express.
 *
 * @author Matthew
 */
@Retention(RetentionPolicy.SOURCE)
@Target({TYPE, METHOD, TYPE_USE, CONSTRUCTOR, PACKAGE, ANNOTATION_TYPE, FIELD, LOCAL_VARIABLE})
public @interface Noteworthy {

    String value();

}
