/*-
 * #%L
 * SPSW API
 * %%
 * Copyright (C) 2009 - 2018 Arne Plöse
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */
package de.ibapl.spsw.jnr;

import jnr.ffi.LibraryLoader;
import jnr.ffi.Runtime;
import jnr.ffi.Struct;
import jnr.ffi.Struct.pid_t;
import jnr.ffi.annotations.Out;
import jnr.ffi.annotations.Transient;

@Wrapper("termios.h")
public class Termios_H {

	// Wrap the interface
	public interface NativeFunctions {
		int cfgetispeed(Termios termios);

		int cfgetospeed(Termios termios);

		int cfsetispeed(@Out @Transient Termios termios, int speed);

		int cfsetospeed(@Out @Transient Termios termios, int speed);

		// TODO POSIX ??? set both in and out
		int cfsetspeed(@Out @Transient Termios termios, int speed);

		int tcdrain(int fildes);

		int tcflow(int fildes, int action);

		int tcflush(int fildes, int queue_selector);

		int tcgetattr(int fildes, @Out @Transient Termios termios);

		pid_t tcgetsid(int fildes);

		int tcsendbreak(int fildes, int duration);

		int tcsetattr(int fildes, int optional_actions, Termios termios);
	}

	public final class Termios extends Struct {
		public final tcflag_t c_iflag = new tcflag_t(); /* input mode flags */
		public final tcflag_t c_oflag = new tcflag_t(); /* output mode flags */
		public final tcflag_t c_cflag = new tcflag_t(); /* control mode flags */
		public final tcflag_t c_lflag = new tcflag_t(); /* local mode flags */
		public final cc_t c_line = new cc_t(); /* line discipline */
		public final cc_t[] c_cc = createCc_t(); /* control characters */

		// This is termios2 ??? - but linux except mips does define this.
		/**
		 * Use this only if {@link TermiosFlags#._HAVE_STRUCT_TERMIOS_C_ISPEED} is
		 * defined.
		 * 
		 */
		public final speed_t c_ispeed = _HAVE_STRUCT_TERMIOS_C_ISPEED ? new speed_t() : null; /* input speed */
		/**
		 * Use this only if {@link TermiosFlags#._HAVE_STRUCT_TERMIOS_C_OSPEED} is
		 * defined.
		 * 
		 */
		public final speed_t c_ospeed = _HAVE_STRUCT_TERMIOS_C_OSPEED ? new speed_t() : null; /* output speed */

		private Termios(Runtime runtime) {
			super(runtime);
		}

		private cc_t[] createCc_t() {
			final cc_t[] result = new cc_t[NCCS];
			for (int i = 0; i < NCCS; i++) {
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
			for (int i = 0; i < NCCS; i++) {
				java.lang.String c_ccName = java.lang.String.valueOf(i);
				if (VINTR != null && VINTR == i) {
					c_ccName = "VINTR";
				} else if (VQUIT != null && VQUIT == i) {
					c_ccName = "VQUIT";
				} else if (VERASE != null && VERASE == i) {
					c_ccName = "VERASE";
				} else if (VKILL != null && VKILL == i) {
					c_ccName = "VKILL";
				} else if (VEOF != null && VEOF == i) {
					c_ccName = "VEOF";
				} else if (VTIME != null && VTIME == i) {
					c_ccName = "VTIME";
				} else if (VMIN != null && VMIN == i) {
					c_ccName = "VMIN";
				} else if (VSWTC != null && VSWTC == i) {
					c_ccName = "VSWTC";
				} else if (VSTART != null && VSTART == i) {
					c_ccName = "VSTART";
				} else if (VSTOP != null && VSTOP == i) {
					c_ccName = "VSTOP";
				} else if (VSUSP != null && VSUSP == i) {
					c_ccName = "VSUSP";
				} else if (VEOL != null && VEOL == i) {
					c_ccName = "VEOL";
				} else if (VREPRINT != null && VREPRINT == i) {
					c_ccName = "VREPRINT";
				} else if (VDISCARD != null && VDISCARD == i) {
					c_ccName = "VDISCARD";
				} else if (VWERASE != null && VWERASE == i) {
					c_ccName = "VWERASE";
				} else if (VLNEXT != null && VLNEXT == i) {
					c_ccName = "VLNEXT";
				} else if (VEOL2 != null && VEOL2 == i) {
					c_ccName = "VEOL2";
				}
				sb.append(java.lang.String.format(c_ccFormatString, c_ccName, c_cc[i].byteValue()));
			}

			if (_HAVE_STRUCT_TERMIOS_C_ISPEED) {
				sb.append(java.lang.String.format(formatString, "c_ispeed", c_ispeed.intValue()));
			} else {
				sb.append("    c_ispeed not defined\n");
			}
			if (_HAVE_STRUCT_TERMIOS_C_OSPEED) {
				sb.append(java.lang.String.format(formatString, "c_ospeed", c_ospeed.intValue()));
			} else {
				sb.append("    c_ospeed not defined\n");
			}

			sb.append("}\n");
			return sb.toString();
		}
	}

	@Defines("termios.h")
	public interface TermiosDefines extends JnrDefines {
		@Mandatory()
		short __MAX_BAUD();

		@IsDefined()
		boolean _HAVE_STRUCT_TERMIOS_C_ISPEED();

		@IsDefined()
		boolean _HAVE_STRUCT_TERMIOS_C_OSPEED();

		@Mandatory()
		short B0();

		@Optional()
		short B1000000();

		@Mandatory()
		short B110();

		@Optional()
		short B115200();

		@Optional()
		short B1152000();

		@Mandatory()
		short B1200();

		@Mandatory()
		short B134();

		@Mandatory()
		short B150();

		@Optional()
		short B1500000();

		@Mandatory()
		short B1800();

		@Mandatory()
		short B19200();

		@Mandatory()
		short B200();

		@Optional()
		short B2000000();

		@Optional()
		short B230400();

		@Mandatory()
		short B2400();

		@Optional()
		short B2500000();

		@Mandatory()
		short B300();

		@Optional()
		short B3000000();

		@Optional()
		short B3500000();

		@Mandatory()
		short B38400();

		@Optional()
		short B4000000();

		@Optional()
		short B460800();

		@Mandatory()
		short B4800();

		@Mandatory()
		short B50();

		@Optional()
		short B500000();

		@Optional()
		short B57600();

		@Optional()
		short B576000();

		@Mandatory()
		short B600();

		@Mandatory()
		short B75();

		@Optional()
		short B921600();

		@Mandatory()
		short B9600();

		@Mandatory()
		short BRKINT();

		@Mandatory()
		short BS0();

		@Mandatory()
		short BS1();

		@Mandatory()
		short BSDLY();

		@Mandatory()
		short CBAUD();

		@Mandatory()
		short CBAUDEX();

		@Mandatory()
		int CIBAUD();

		@Mandatory()
		short CLOCAL();

		@Optional()
		Integer CMSPAR();

		@Mandatory()
		short CR0();

		@Mandatory()
		short CR1();

		@Mandatory()
		short CR2();

		@Mandatory()
		short CR3();

		@Mandatory()
		short CRDLY();

		@Mandatory()
		short CREAD();

		@Mandatory()
		int CRTSCTS();

		@Mandatory()
		short CS5();

		@Mandatory()
		short CS6();

		@Mandatory()
		short CS7();

		@Mandatory()
		short CS8();

		@Mandatory()
		short CSIZE();

		@Mandatory()
		short CSTOPB();

		@Mandatory()
		short ECHO();

		@Mandatory()
		short ECHOCTL();

		@Mandatory()
		short ECHOE();

		@Mandatory()
		short ECHOK();

		@Mandatory()
		short ECHOKE();

		@Mandatory()
		short ECHONL();

		@Mandatory()
		short ECHOPRT();

		@Mandatory()
		short EXTA();

		@Mandatory()
		short EXTB();

		@Mandatory()
		int EXTPROC();

		@Mandatory()
		short FF0();

		@Mandatory()
		short FF1();

		@Mandatory()
		short FFDLY();

		@Mandatory()
		short FLUSHO();

		@Mandatory()
		short HUPCL();

		@Mandatory()
		short ICANON();

		@Mandatory()
		short ICRNL();

		@Mandatory()
		short IEXTEN();

		@Mandatory()
		short IGNBRK();

		@Mandatory()
		short IGNCR();

		@Mandatory()
		short IGNPAR();

		@Mandatory()
		short IMAXBEL();

		@Mandatory()
		short INLCR();

		@Mandatory()
		short INPCK();

		@Mandatory()
		short ISIG();

		@Mandatory()
		short ISTRIP();

		@Mandatory()
		short IUCLC();

		@Mandatory()
		short IUTF8();

		@Mandatory()
		short IXANY();

		@Mandatory()
		short IXOFF();

		@Mandatory()
		short IXON();

		@Mandatory()
		short NCCS();

		@Mandatory()
		short NL0();

		@Mandatory()
		short NL1();

		@Mandatory()
		short NLDLY();

		@Mandatory()
		short NOFLSH();

		@Mandatory()
		short OCRNL();

		@Mandatory()
		short OFDEL();

		@Mandatory()
		short OFILL();

		@Mandatory()
		short OLCUC();

		@Mandatory()
		short ONLCR();

		@Mandatory()
		short ONLRET();

		@Mandatory()
		short ONOCR();

		@Mandatory()
		short OPOST();

		@Mandatory()
		short PARENB();

		@Optional()
		Short PAREXT();
		// @Prototype("switch(value && CBAUD) {case B0: return 50; case B50: return 50;
		// default throw IllegalArgumentException(); end;")
		// int toBaudrate(int value);

		@Mandatory()
		short PARMRK();

		@Mandatory()
		short PARODD();

		@Mandatory()
		short PENDIN();

		@Mandatory()
		short TAB0();

		@Mandatory()
		short TAB1();

		@Mandatory()
		short TAB2();

		@Mandatory()
		short TAB3();

		@Mandatory()
		short TABDLY();

		@Mandatory()
		short TCIFLUSH();

		@Mandatory()
		short TCIOFF();

		@Mandatory()
		short TCIOFLUSH();

		@Mandatory()
		short TCION();

		@Mandatory()
		short TCOFLUSH();

		@Mandatory
		short TCOOFF();

		@Mandatory()
		short TCOON();

		@Mandatory()
		short TCSADRAIN();

		@Mandatory()
		short TCSAFLUSH();

		@Mandatory()
		short TCSANOW();

		@Mandatory()
		short TOSTOP();

		@Optional()
		Short VDISCARD();

		@Optional()
		Short VEOF();

		@Optional()
		Short VEOL();

		@Optional()
		Short VEOL2();

		@Optional()
		Short VERASE();

		@Optional()
		Short VINTR();

		@Optional()
		Short VKILL();

		@Optional()
		Short VLNEXT();

		@Optional()
		Short VMIN();

		@Optional()
		Short VQUIT();

		@Optional()
		Short VREPRINT();

		@Optional()
		Short VSTART();

		@Optional()
		Short VSTOP();

		@Optional()
		Short VSUSP();

		@Optional()
		Short VSWTC();

		@Optional()
		Short VSWTCH();

		@Mandatory()
		short VT0();

		@Mandatory()
		short VT1();

		@Mandatory()
		short VTDLY();

		@Optional()
		Short VTIME();

		@Optional()
		Short VWERASE();

		@Mandatory()
		short XCASE();

		@Mandatory()
		short XTABS();
	}

	final public short NCCS;
	final public boolean _HAVE_STRUCT_TERMIOS_C_ISPEED;
	final public boolean _HAVE_STRUCT_TERMIOS_C_OSPEED;
	final public Short VINTR;
	final public Short VQUIT;
	final public Short VERASE;
	final public Short VKILL;
	final public Short VEOF;
	final public Short VTIME;
	final public Short VMIN;
	final public Short VSWTC;
	final public Short VSWTCH;
	final public Short VSTART;
	final public Short VSTOP;
	final public Short VSUSP;
	final public Short VEOL;
	final public Short VREPRINT;
	final public Short VDISCARD;
	final public Short VWERASE;
	final public Short VLNEXT;
	final public Short VEOL2;
	final public short IGNBRK;
	final public short BRKINT;
	final public short IGNPAR;
	final public short PARMRK;
	final public short INPCK;
	final public short ISTRIP;
	final public short INLCR;
	final public short IGNCR;
	final public short ICRNL;
	final public short IUCLC;
	final public short IXON;
	final public short IXANY;
	final public short IXOFF;
	final public short IMAXBEL;
	final public short IUTF8;
	final public short OPOST;
	final public short OLCUC;
	final public short ONLCR;
	final public short OCRNL;
	final public short ONOCR;
	final public short ONLRET;
	final public short OFILL;
	final public short OFDEL;
	final public short NLDLY;
	final public short NL0;
	final public short NL1;
	final public short CRDLY;
	final public short CR0;
	final public short CR1;
	final public short CR2;
	final public short CR3;
	final public short TABDLY;
	final public short TAB0;
	final public short TAB1;
	final public short TAB2;
	final public short TAB3;
	final public short BSDLY;
	final public short BS0;
	final public short BS1;
	final public short FFDLY;
	final public short FF0;
	final public short FF1;
	final public short VTDLY;
	final public short VT0;
	final public short VT1;
	final public short XTABS;
	final public short CBAUD;
	final public short B0;
	final public short B50;
	final public short B75;
	final public short B110;
	final public short B134;
	final public short B150;
	final public short B200;
	final public short B300;
	final public short B600;
	final public short B1200;
	final public short B1800;
	final public short B2400;
	final public short B4800;
	final public short B9600;
	final public short B19200;
	final public short B38400;
	final public short EXTA;
	final public short EXTB;
	final public short CSIZE;
	final public short CS5;
	final public short CS6;
	final public short CS7;
	final public short CS8;
	final public short CSTOPB;
	final public short CREAD;
	final public short PARENB;
	final public short PARODD;
	final public short HUPCL;
	final public short CLOCAL;
	final public short CBAUDEX;
	final public Short B57600;
	final public Short B115200;
	final public Short B230400;
	final public Short B460800;
	final public Short B500000;
	final public Short B576000;
	final public Short B921600;
	final public Short B1000000;
	final public Short B1152000;
	final public Short B1500000;
	final public Short B2000000;
	final public Short B2500000;
	final public Short B3000000;
	final public Short B3500000;
	final public Short B4000000;
	final public short __MAX_BAUD;
	final public int CIBAUD;
	final public Integer CMSPAR;
	final public int CRTSCTS;
	final public short ISIG;
	final public short ICANON;
	final public short XCASE;
	final public short ECHO;
	final public short ECHOE;
	final public short ECHOK;
	final public short ECHONL;
	final public short NOFLSH;
	final public short TOSTOP;
	final public short ECHOCTL;
	final public short ECHOPRT;
	final public short ECHOKE;
	final public short FLUSHO;
	final public short PENDIN;
	final public short IEXTEN;
	final public int EXTPROC;
	final public short TCOOFF;
	final public short TCOON;
	final public short TCIOFF;
	final public short TCION;
	final public short TCIFLUSH;
	final public short TCOFLUSH;
	final public short TCIOFLUSH;
	final public short TCSANOW;
	final public short TCSADRAIN;
	final public short TCSAFLUSH;
	final public Short PAREXT;

	final private NativeFunctions nativeFunctions;

	public Termios_H() {
		nativeFunctions = LibraryLoader.create(NativeFunctions.class).load("c");
		final TermiosDefines termiosDefines = JnrDefines.getInstance(TermiosDefines.class);
		NCCS = termiosDefines.NCCS();
		_HAVE_STRUCT_TERMIOS_C_ISPEED = termiosDefines._HAVE_STRUCT_TERMIOS_C_ISPEED();
		_HAVE_STRUCT_TERMIOS_C_OSPEED = termiosDefines._HAVE_STRUCT_TERMIOS_C_OSPEED();
		VINTR = termiosDefines.VINTR();
		VQUIT = termiosDefines.VQUIT();
		VERASE = termiosDefines.VERASE();
		VKILL = termiosDefines.VKILL();
		VEOF = termiosDefines.VEOF();
		VTIME = termiosDefines.VTIME();
		VMIN = termiosDefines.VMIN();
		VSWTC = termiosDefines.VSWTC();
		VSWTCH = termiosDefines.VSWTCH();
		VSTART = termiosDefines.VSTART();
		VSTOP = termiosDefines.VSTOP();
		VSUSP = termiosDefines.VSUSP();
		VEOL = termiosDefines.VEOL();
		VREPRINT = termiosDefines.VREPRINT();
		VDISCARD = termiosDefines.VDISCARD();
		VWERASE = termiosDefines.VWERASE();
		VLNEXT = termiosDefines.VLNEXT();
		VEOL2 = termiosDefines.VEOL2();
		IGNBRK = termiosDefines.IGNBRK();
		BRKINT = termiosDefines.BRKINT();
		IGNPAR = termiosDefines.IGNPAR();
		PARMRK = termiosDefines.PARMRK();
		INPCK = termiosDefines.INPCK();
		ISTRIP = termiosDefines.ISTRIP();
		INLCR = termiosDefines.INLCR();
		IGNCR = termiosDefines.IGNCR();
		ICRNL = termiosDefines.ICRNL();
		IUCLC = termiosDefines.IUCLC();
		IXON = termiosDefines.IXON();
		IXANY = termiosDefines.IXANY();
		IXOFF = termiosDefines.IXOFF();
		IMAXBEL = termiosDefines.IMAXBEL();
		IUTF8 = termiosDefines.IUTF8();
		OPOST = termiosDefines.OPOST();
		OLCUC = termiosDefines.OLCUC();
		ONLCR = termiosDefines.ONLCR();
		OCRNL = termiosDefines.OCRNL();
		ONOCR = termiosDefines.ONOCR();
		ONLRET = termiosDefines.ONLRET();
		OFILL = termiosDefines.OFILL();
		OFDEL = termiosDefines.OFDEL();
		NLDLY = termiosDefines.NLDLY();
		NL0 = termiosDefines.NL0();
		NL1 = termiosDefines.NL1();
		CRDLY = termiosDefines.CRDLY();
		CR0 = termiosDefines.CR0();
		CR1 = termiosDefines.CR1();
		CR2 = termiosDefines.CR2();
		CR3 = termiosDefines.CR3();
		TABDLY = termiosDefines.TABDLY();
		TAB0 = termiosDefines.TAB0();
		TAB1 = termiosDefines.TAB1();
		TAB2 = termiosDefines.TAB2();
		TAB3 = termiosDefines.TAB3();
		BSDLY = termiosDefines.BSDLY();
		BS0 = termiosDefines.BS0();
		BS1 = termiosDefines.BS1();
		FFDLY = termiosDefines.FFDLY();
		FF0 = termiosDefines.FF0();
		FF1 = termiosDefines.FF1();
		VTDLY = termiosDefines.VTDLY();
		VT0 = termiosDefines.VT0();
		VT1 = termiosDefines.VT1();
		XTABS = termiosDefines.XTABS();
		CBAUD = termiosDefines.CBAUD();
		B0 = termiosDefines.B0();
		B50 = termiosDefines.B50();
		B75 = termiosDefines.B75();
		B110 = termiosDefines.B110();
		B134 = termiosDefines.B134();
		B150 = termiosDefines.B150();
		B200 = termiosDefines.B200();
		B300 = termiosDefines.B300();
		B600 = termiosDefines.B600();
		B1200 = termiosDefines.B1200();
		B1800 = termiosDefines.B1800();
		B2400 = termiosDefines.B2400();
		B4800 = termiosDefines.B4800();
		B9600 = termiosDefines.B9600();
		B19200 = termiosDefines.B19200();
		B38400 = termiosDefines.B38400();
		EXTA = termiosDefines.EXTA();
		EXTB = termiosDefines.EXTB();
		CSIZE = termiosDefines.CSIZE();
		CS5 = termiosDefines.CS5();
		CS6 = termiosDefines.CS6();
		CS7 = termiosDefines.CS7();
		CS8 = termiosDefines.CS8();
		CSTOPB = termiosDefines.CSTOPB();
		CREAD = termiosDefines.CREAD();
		PARENB = termiosDefines.PARENB();
		PARODD = termiosDefines.PARODD();
		HUPCL = termiosDefines.HUPCL();
		CLOCAL = termiosDefines.CLOCAL();
		CBAUDEX = termiosDefines.CBAUDEX();
		B57600 = termiosDefines.B57600();
		B115200 = termiosDefines.B115200();
		B230400 = termiosDefines.B230400();
		B460800 = termiosDefines.B460800();
		B500000 = termiosDefines.B500000();
		B576000 = termiosDefines.B576000();
		B921600 = termiosDefines.B921600();
		B1000000 = termiosDefines.B1000000();
		B1152000 = termiosDefines.B1152000();
		B1500000 = termiosDefines.B1500000();
		B2000000 = termiosDefines.B2000000();
		B2500000 = termiosDefines.B2500000();
		B3000000 = termiosDefines.B3000000();
		B3500000 = termiosDefines.B3500000();
		B4000000 = termiosDefines.B4000000();
		__MAX_BAUD = termiosDefines.__MAX_BAUD();
		CIBAUD = termiosDefines.CIBAUD();
		CMSPAR = termiosDefines.CMSPAR();
		CRTSCTS = termiosDefines.CRTSCTS();
		ISIG = termiosDefines.ISIG();
		ICANON = termiosDefines.ICANON();
		XCASE = termiosDefines.XCASE();
		ECHO = termiosDefines.ECHO();
		ECHOE = termiosDefines.ECHOE();
		ECHOK = termiosDefines.ECHOK();
		ECHONL = termiosDefines.ECHONL();
		NOFLSH = termiosDefines.NOFLSH();
		TOSTOP = termiosDefines.TOSTOP();
		ECHOCTL = termiosDefines.ECHOCTL();
		ECHOPRT = termiosDefines.ECHOPRT();
		ECHOKE = termiosDefines.ECHOKE();
		FLUSHO = termiosDefines.FLUSHO();
		PENDIN = termiosDefines.PENDIN();
		IEXTEN = termiosDefines.IEXTEN();
		EXTPROC = termiosDefines.EXTPROC();
		TCOOFF = termiosDefines.TCOOFF();
		TCOON = termiosDefines.TCOON();
		TCIOFF = termiosDefines.TCIOFF();
		TCION = termiosDefines.TCION();
		TCIFLUSH = termiosDefines.TCIFLUSH();
		TCOFLUSH = termiosDefines.TCOFLUSH();
		TCIOFLUSH = termiosDefines.TCIOFLUSH();
		TCSANOW = termiosDefines.TCSANOW();
		TCSADRAIN = termiosDefines.TCSADRAIN();
		TCSAFLUSH = termiosDefines.TCSAFLUSH();
		PAREXT = termiosDefines.PAREXT();
	}

	public int cfgetispeed(Termios termios) {
		return nativeFunctions.cfgetispeed(termios);
	}

	public int cfgetospeed(Termios termios) {
		return nativeFunctions.cfgetospeed(termios);
	}

	public int cfsetispeed(@Out @Transient Termios termios, int speed) {
		return nativeFunctions.cfsetispeed(null, speed);
	}

	public int cfsetospeed(@Out @Transient Termios termios, int speed) {
		return nativeFunctions.cfsetospeed(termios, speed);
	}

	// TODO POSIX ??? set both in and out
	public int cfsetspeed(@Out @Transient Termios termios, int speed) {
		return nativeFunctions.cfsetspeed(termios, speed);
	}

	/**
	 * Returns a new struct termios.
	 * 
	 * @return
	 */
	public Termios createTermios() {
		return new Termios(Runtime.getRuntime(nativeFunctions));
	}

	public int tcdrain(int fildes) {
		return nativeFunctions.tcdrain(fildes);
	}

	public int tcflow(int fildes, int action) {
		return nativeFunctions.tcflow(fildes, action);
	}

	public int tcflush(int fildes, int queue_selector) {
		return nativeFunctions.tcflush(fildes, queue_selector);
	}

	public int tcgetattr(int fildes, @Out @Transient Termios termios) {
		return nativeFunctions.tcgetattr(fildes, termios);
	}

	public pid_t tcgetsid(int fildes) {
		return nativeFunctions.tcgetsid(fildes);
	}

	public int tcsendbreak(int fildes, int duration) {
		return nativeFunctions.tcsendbreak(fildes, duration);
	}

	public int tcsetattr(int fildes, int optional_actions, Termios termios) {
		return nativeFunctions.tcsetattr(fildes, optional_actions, termios);
	}

}