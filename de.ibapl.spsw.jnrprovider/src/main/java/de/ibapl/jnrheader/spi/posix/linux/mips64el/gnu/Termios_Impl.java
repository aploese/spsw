package de.ibapl.jnrheader.spi.posix.linux.mips64el.gnu;

import de.ibapl.jnrheader.Defined;
import de.ibapl.jnrheader.spi.posix.linux.Termios_Generic;

public class Termios_Impl extends Termios_Generic {
	public static final Defined _HAVE_STRUCT_TERMIOS_C_ISPEED = Defined.NOT_DEFINED;
	public static final Defined _HAVE_STRUCT_TERMIOS_C_OSPEED = Defined.NOT_DEFINED;

	@Override
	protected Defined _HAVE_STRUCT_TERMIOS_C_ISPEED() {
		return Termios_Impl._HAVE_STRUCT_TERMIOS_C_ISPEED;
	}

	@Override
	protected Defined _HAVE_STRUCT_TERMIOS_C_OSPEED() {
		return Termios_Impl._HAVE_STRUCT_TERMIOS_C_OSPEED;
	}

}
