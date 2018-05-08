package de.ibapl.jnrheader;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD})
public @interface POSIX {

	Marker value() default Marker.ISO_C;

	public enum Marker {
		ISO_C,
		XSI;
	}

}
