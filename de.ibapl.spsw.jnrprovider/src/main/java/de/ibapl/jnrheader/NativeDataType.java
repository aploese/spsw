package de.ibapl.jnrheader;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(value = { ElementType.ANNOTATION_TYPE, ElementType.FIELD })
public @interface NativeDataType {
	String value();
}
