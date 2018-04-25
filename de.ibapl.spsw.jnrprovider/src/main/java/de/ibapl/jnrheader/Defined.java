package de.ibapl.jnrheader;

public final class Defined {

	public final static Defined DEFINED = new Defined();
	public final static Defined NOT_DEFINED = null;
	
	public final static boolean isDefined(Defined value) {
		return value == DEFINED;
	}

	private Defined() {
		
	}
	
	public String toString() {
		return "defined";
	}
}
