package de.ibapl.jnrheader.spi.linux.x86_64.gnu;

import de.ibapl.jnrheader.Defined;
import de.ibapl.jnrheader.spi.linux.Termios_Lib;

public class Termios_Impl extends Termios_Lib {
	public static final Defined _HAVE_STRUCT_TERMIOS_C_ISPEED = Defined.DEFINED;
	public static final Defined _HAVE_STRUCT_TERMIOS_C_OSPEED = Defined.DEFINED;

	@Override
	protected Defined _HAVE_STRUCT_TERMIOS_C_ISPEED() {
		return Termios_Impl._HAVE_STRUCT_TERMIOS_C_ISPEED;
	}

	@Override
	protected Defined _HAVE_STRUCT_TERMIOS_C_OSPEED() {
		return Termios_Impl._HAVE_STRUCT_TERMIOS_C_OSPEED;
	}

}
