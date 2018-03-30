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
		public final speed_t c_ispeed = new speed_t(); /* input speed */
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

	}

	public interface Functions {
		// speed_t cfgetispeed(const struct termios *termios_p);
		// speed_t cfgetospeed(const struct termios *termios_p);
		// int cfsetispeed(struct termios *termios_p, speed_t speed);
		// int cfsetospeed(struct termios *termios_p, speed_t speed);
		int tcdrain(int fildes);

		// int tcflow(int fildes, int action);
		// int tcflush(int fildes, int queue_selector);
		int tcgetattr(int fildes, @Out @Transient Termios termios_p);

		// pid_t tcgetsid(int fildes);
		int tcsendbreak(int fildes, int duration);
		int tcsetattr(int fildes, int optional_actions, Termios termios_p);
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
