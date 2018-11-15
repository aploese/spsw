package de.ibapl.jnrheader;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Is this more precisely glibc compilant ???
 * @author aploese
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD})
public @interface LINUX {

}
