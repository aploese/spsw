package de.ibapl.jnrheader;

import java.util.Arrays;
import java.util.logging.Logger;

public final class Defined {

	private final static Logger LOG = Logger.getLogger(Defined.class.getCanonicalName());
	
	public final static Defined DEFINED = new Defined();
	public final static Defined NOT_DEFINED = null;
	
	public final static boolean isDefined(Defined value) {
		return value == DEFINED;
	}

	public final static boolean isDefined(Byte value) {
		return value != null;
	}

	public final static boolean isDefined(byte value) {
		LOG.warning("Called  isDefined(byte) from " + Arrays.toString(new Exception().getStackTrace()));
		return true;
	}

	public final static boolean isDefined(Short value) {
		return value != null;
	}

	public final static boolean isDefined(short value) {
		LOG.warning("Called  isDefined(short) from " + Arrays.toString(new Exception().getStackTrace()));
		return true;
	}

	public final static boolean isDefined(Integer value) {
		return value != null;
	}

	public final static boolean isDefined(int value) {
		LOG.warning("Called  isDefined(int) from " + Arrays.toString(new Exception().getStackTrace()));
		return true;
	}

	public final static boolean isDefined(Long value) {
		return value != null;
	}

	public final static boolean isDefined(long value) {
		LOG.warning("Called  isDefined(long) from " + Arrays.toString(new Exception().getStackTrace()));
		return true;
	}

	public final static boolean isDefined(Float value) {
		return value != null;
	}

	public final static boolean isDefined(float value) {
		LOG.warning("Called  isDefined(float) from " + Arrays.toString(new Exception().getStackTrace()));
		return true;
	}

	public final static boolean isDefined(Double value) {
		return value != null;
	}

	public final static boolean isDefined(double value) {
		LOG.warning("Called  isDefined(double) from " + Arrays.toString(new Exception().getStackTrace()));
		return true;
	}

	private Defined() {
		
	}
	
	public String toString() {
		return "defined";
	}
}
