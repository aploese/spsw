package de.ibapl.jnrheader;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jnr.ffi.Platform.OS;

@Documented
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface DefinedByOS {

	OS[] value();
}
