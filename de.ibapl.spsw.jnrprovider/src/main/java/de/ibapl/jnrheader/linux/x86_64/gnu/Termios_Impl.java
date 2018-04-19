package de.ibapl.jnrheader.linux.x86_64.gnu;

import de.ibapl.jnrheader.linux.Termios_Lib;

public class Termios_Impl extends Termios_Lib {
	public static final boolean _HAVE_STRUCT_TERMIOS_C_ISPEED = true;
	public static final boolean _HAVE_STRUCT_TERMIOS_C_OSPEED = true;

	@Override
	protected boolean _HAVE_STRUCT_TERMIOS_C_ISPEED() {
		return Termios_Impl._HAVE_STRUCT_TERMIOS_C_ISPEED;
	}

	@Override
	protected boolean _HAVE_STRUCT_TERMIOS_C_OSPEED() {
		return Termios_Impl._HAVE_STRUCT_TERMIOS_C_OSPEED;
	}

}
