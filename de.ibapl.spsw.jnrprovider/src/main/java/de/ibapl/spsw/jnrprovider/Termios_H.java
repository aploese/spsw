package de.ibapl.spsw.jnrprovider;

import jnr.constants.platform.TermiosFlags;
import jnr.ffi.Runtime;
import jnr.ffi.Struct;
import jnr.ffi.annotations.Out;
import jnr.ffi.annotations.Transient;

public interface Termios_H {

	public static final class Termios extends Struct {
		public final tcflag_t c_iflag = new tcflag_t(); /* input mode flags */
		public final tcflag_t c_oflag = new tcflag_t(); /* output mode flags */
		public final tcflag_t c_cflag = new tcflag_t(); /* control mode flags */
		public final tcflag_t c_lflag = new tcflag_t(); /* local mode flags */
		public final cc_t c_line = new cc_t(); /* line discipline */
		public final cc_t[] c_cc = createCc_t(); /* control characters */

		// This is termios2 but linux except mips does define this.
		/**
		 * Use this only if {@link TermiosFlags#._HAVE_STRUCT_TERMIOS_C_ISPEED} is
		 * defined.
		 * 
		 */
		public final speed_t c_ispeed = new speed_t(); /* input speed */
		/**
		 * Use this only if {@link TermiosFlags#._HAVE_STRUCT_TERMIOS_C_OSPEED} is
		 * defined.
		 * 
		 */
		public final speed_t c_ospeed = new speed_t(); /* output speed */

		public Termios(Runtime runtime) {
			super(runtime);
		}

		private cc_t[] createCc_t() {
			final int size = TermiosFlags.NCCS.intValue();
			final cc_t[] result = new cc_t[size];
			for (int i = 0; i < size; i++) {
				result[i] = new cc_t();
			}
			return result;
		}

		public java.lang.String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(getClass().getSimpleName()).append(" { \n");
			final java.lang.String formatString = "    %s = 0x%08x\n";
			sb.append(java.lang.String.format(formatString, "c_iflag", c_iflag.intValue()));
			sb.append(java.lang.String.format(formatString, "c_oflag", c_oflag.intValue()));
			sb.append(java.lang.String.format(formatString, "c_cflag", c_cflag.intValue()));
			sb.append(java.lang.String.format(formatString, "c_lflag", c_lflag.intValue()));
			sb.append(java.lang.String.format(formatString, "c_line", c_line.intValue()));
			final java.lang.String c_ccFormatString = "    c_cc[%s] = 0x%02x\n";
			for (int i = 0; i < TermiosFlags.NCCS.intValue(); i++) {
				java.lang.String c_ccName = java.lang.String.valueOf(i);
				if (TermiosFlags.VINTR.defined() && TermiosFlags.VINTR.intValue() == i) {
					c_ccName = "VINTR";
				} else if (TermiosFlags.VQUIT.defined() && TermiosFlags.VQUIT.intValue() == i) {
					c_ccName = "VQUIT";
				} else if (TermiosFlags.VERASE.defined() && TermiosFlags.VERASE.intValue() == i) {
					c_ccName = "VERASE";
				} else if (TermiosFlags.VKILL.defined() && TermiosFlags.VKILL.intValue() == i) {
					c_ccName = "VKILL";
				} else if (TermiosFlags.VEOF.defined() && TermiosFlags.VEOF.intValue() == i) {
					c_ccName = "VEOF";
				} else if (TermiosFlags.VTIME.defined() && TermiosFlags.VTIME.intValue() == i) {
					c_ccName = "VTIME";
				} else if (TermiosFlags.VMIN.defined() && TermiosFlags.VMIN.intValue() == i) {
					c_ccName = "VMIN";
				} else if (TermiosFlags.VSWTC.defined() && TermiosFlags.VSWTC.intValue() == i) {
					c_ccName = "VSWTC";
				} else if (TermiosFlags.VSTART.defined() && TermiosFlags.VSTART.intValue() == i) {
					c_ccName = "VSTART";
				} else if (TermiosFlags.VSTOP.defined() && TermiosFlags.VSTOP.intValue() == i) {
					c_ccName = "VSTOP";
				} else if (TermiosFlags.VSUSP.defined() && TermiosFlags.VSUSP.intValue() == i) {
					c_ccName = "VSUSP";
				} else if (TermiosFlags.VEOL.defined() && TermiosFlags.VEOL.intValue() == i) {
					c_ccName = "VEOL";
				} else if (TermiosFlags.VREPRINT.defined() && TermiosFlags.VREPRINT.intValue() == i) {
					c_ccName = "VREPRINT";
				} else if (TermiosFlags.VDISCARD.defined() && TermiosFlags.VDISCARD.intValue() == i) {
					c_ccName = "VDISCARD";
				} else if (TermiosFlags.VWERASE.defined() && TermiosFlags.VWERASE.intValue() == i) {
					c_ccName = "VWERASE";
				} else if (TermiosFlags.VLNEXT.defined() && TermiosFlags.VLNEXT.intValue() == i) {
					c_ccName = "VLNEXT";
				} else if (TermiosFlags.VEOL2.defined() && TermiosFlags.VEOL2.intValue() == i) {
					c_ccName = "VEOL2";
				}
				sb.append(java.lang.String.format(c_ccFormatString, c_ccName, c_cc[i].byteValue()));
			}

			if (TermiosFlags._HAVE_STRUCT_TERMIOS_C_ISPEED.defined()) {
				sb.append(java.lang.String.format(formatString, "c_ispeed", c_ispeed.intValue()));
			} else {
				sb.append("    c_ispeed not defined\n");
			}
			if (TermiosFlags._HAVE_STRUCT_TERMIOS_C_OSPEED.defined()) {
				sb.append(java.lang.String.format(formatString, "c_ospeed", c_ospeed.intValue()));
			} else {
				sb.append("    c_ospeed not defined\n");
			}

			sb.append("}\n");
			return sb.toString();
		}
	}

	public interface Functions {
		int cfgetispeed(Termios termios);

		int cfgetospeed(Termios termios);

		int cfsetispeed(@Out @Transient Termios termios, int speed);

		int cfsetospeed(@Out @Transient Termios termios, int speed);

		// TODO POSIX ??? set both in and out
		int cfsetspeed(@Out @Transient Termios termios, int speed);

		int tcdrain(int fildes);

		// int tcflow(int fildes, int action);
		// int tcflush(int fildes, int queue_selector);
		int tcgetattr(int fildes, @Out @Transient Termios termios);

		// pid_t tcgetsid(int fildes);
		int tcsendbreak(int fildes, int duration);

		int tcsetattr(int fildes, int optional_actions, Termios termios);
	}

	/*
	 * Model after this ... int gettimeofday(struct timeval *restrict tp, void
	 * *restrict tzp);
	 * 
	 * public int gettimeofday(@Out @Transient Timeval tv, Pointer unused);
	 * 
	 * 
	 * public static final class Timeval extends Struct { public final time_t tv_sec
	 * = new time_t(); public final SignedLong tv_usec = new SignedLong();
	 * 
	 * public Timeval(Runtime runtime) { super(runtime); } }
	 * 
	 * 
	 * 
	 */
}
