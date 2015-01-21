package org.usfirst.frc.team1554.lib.meta;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.TYPE_USE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A simple developer annotation that indicates that there is REALLY something of
 * note that a comment simply cannot express.
 * 
 * @author Matthew
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ TYPE, METHOD, TYPE_USE, CONSTRUCTOR, PACKAGE, ANNOTATION_TYPE, FIELD, LOCAL_VARIABLE })
public @interface Noteworthy {

	String value();

}
