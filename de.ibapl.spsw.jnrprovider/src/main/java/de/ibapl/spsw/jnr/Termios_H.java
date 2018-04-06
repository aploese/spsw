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
import jnr.ffi.annotations.Out;
import jnr.ffi.annotations.Transient;

@Wrapper("termios.h")
public class Termios_H {

	@Defines("termios.h")
	public interface TermiosDefines extends JnrDefines {
		@Mandatory()
		short NCCS();
		@IsDefined()
		boolean _HAVE_STRUCT_TERMIOS_C_ISPEED();
		@IsDefined()
		boolean _HAVE_STRUCT_TERMIOS_C_OSPEED();
		@Optional()
		Short VINTR();
		@Optional()
		Short VQUIT();
		@Optional()
		Short VERASE();
		@Optional()
		Short VKILL();
		@Optional()
		Short VEOF();
		@Optional()
		Short VTIME();
		@Optional()
		Short VMIN();
		@Optional()
		Short VSWTC();
		@Optional()
		Short VSWTCH();
		@Optional()
		Short VSTART();
		@Optional()
		Short VSTOP();
		@Optional()
		Short VSUSP();
		@Optional()
		Short VEOL();
		@Optional()
		Short VREPRINT();
		@Optional()
		Short VDISCARD();
		@Optional()
		Short VWERASE();
		@Optional()
		Short VLNEXT();
		@Optional()
		Short VEOL2();
		@Mandatory()
		short IGNBRK();
		@Mandatory()
		short BRKINT();
		@Mandatory()
		short IGNPAR();
		@Mandatory()
		short PARMRK();
		@Mandatory()
		short INPCK();
		@Mandatory()
		short ISTRIP();
		@Mandatory()
		short INLCR();
		@Mandatory()
		short IGNCR();
		@Mandatory()
		short ICRNL();
		@Mandatory()
		short IUCLC();
		@Mandatory()
		short IXON();
		@Mandatory()
		short IXANY();
		@Mandatory()
		short IXOFF();
		@Mandatory()
		short IMAXBEL();
		@Mandatory()
		short IUTF8();
		@Mandatory()
		short OPOST();
		@Mandatory()
		short OLCUC();
		@Mandatory()
		short ONLCR();
		@Mandatory()
		short OCRNL();
		@Mandatory()
		short ONOCR();
		@Mandatory()
		short ONLRET();
		@Mandatory()
		short OFILL();
		@Mandatory()
		short OFDEL();
		@Mandatory()
		short NLDLY();
		@Mandatory()
		short NL0();
		@Mandatory()
		short NL1();
		@Mandatory()
		short CRDLY();
		@Mandatory()
		short CR0();
		@Mandatory()
		short CR1();
		@Mandatory()
		short CR2();
		@Mandatory()
		short CR3();
		@Mandatory()
		short TABDLY();
		@Mandatory()
		short TAB0();
		@Mandatory()
		short TAB1();
		@Mandatory()
		short TAB2();
		@Mandatory()
		short TAB3();
		@Mandatory()
		short BSDLY();
		@Mandatory()
		short BS0();
		@Mandatory()
		short BS1();
		@Mandatory()
		short FFDLY();
		@Mandatory()
		short FF0();
		@Mandatory()
		short FF1();
		@Mandatory()
		short VTDLY();
		@Mandatory()
		short VT0();
		@Mandatory()
		short VT1();
		@Mandatory()
		short XTABS();
		@Mandatory()
		short CBAUD();
		@Mandatory()
		short B0();
		@Mandatory()
		short B50();
		@Mandatory()
		short B75();
		@Mandatory()
		short B110();
		@Mandatory()
		short B134();
		@Mandatory()
		short B150();
		@Mandatory()
		short B200();
		@Mandatory()
		short B300();
		@Mandatory()
		short B600();
		@Mandatory()
		short B1200();
		@Mandatory()
		short B1800();
		@Mandatory()
		short B2400();
		@Mandatory()
		short B4800();
		@Mandatory()
		short B9600();
		@Mandatory()
		short B19200();
		@Mandatory()
		short B38400();
		@Mandatory()
		short EXTA();
		@Mandatory()
		short EXTB();
		@Mandatory()
		short CSIZE();
		@Mandatory()
		short CS5();
		@Mandatory()
		short CS6();
		@Mandatory()
		short CS7();
		@Mandatory()
		short CS8();
		@Mandatory()
		short CSTOPB();
		@Mandatory()
		short CREAD();
		@Mandatory()
		short PARENB();
		@Mandatory()
		short PARODD();
		@Mandatory()
		short HUPCL();
		@Mandatory()
		short CLOCAL();
		@Mandatory()
		short CBAUDEX();
		@Optional()
		short B57600();
		@Optional()
		short B115200();
		@Optional()
		short B230400();
		@Optional()
		short B460800();
		@Optional()
		short B500000();
		@Optional()
		short B576000();
		@Optional()
		short B921600();
		@Optional()
		short B1000000();
		@Optional()
		short B1152000();
		@Optional()
		short B1500000();
		@Optional()
		short B2000000();
		@Optional()
		short B2500000();
		@Optional()
		short B3000000();
		@Optional()
		short B3500000();
		@Optional()
		short B4000000();
		@Mandatory()
		short __MAX_BAUD();
		@Mandatory()
		int CIBAUD();
		@Optional()
		Integer CMSPAR();
		@Mandatory()
		int CRTSCTS();
		@Mandatory()
		short ISIG();
		@Mandatory()
		short ICANON();
		@Mandatory()
		short XCASE();
		@Mandatory()
		short ECHO();
		@Mandatory()
		short ECHOE();
		@Mandatory()
		short ECHOK();
		@Mandatory()
		short ECHONL();
		@Mandatory()
		short NOFLSH();
		@Mandatory()
		short TOSTOP();
		@Mandatory()
		short ECHOCTL();
		@Mandatory()
		short ECHOPRT();
		@Mandatory()
		short ECHOKE();
		@Mandatory()
		short FLUSHO();
		@Mandatory()
		short PENDIN();
		@Mandatory()
		short IEXTEN();
		@Mandatory()
		int EXTPROC();
		@Mandatory
		short TCOOFF();
		@Mandatory()
		short TCOON();
		@Mandatory()
		short TCIOFF();
		@Mandatory()
		short TCION();
		@Mandatory()
		short TCIFLUSH();
		@Mandatory()
		short TCOFLUSH();
		@Mandatory()
		short TCIOFLUSH();
		@Mandatory()
		short TCSANOW();
		@Mandatory()
		short TCSADRAIN();
		@Mandatory()
		short TCSAFLUSH();
		@Optional()
		Short PAREXT();
		// @Prototype("switch(value && CBAUD) {case B0: return 50; case B50: return 50;
		// default throw IllegalArgumentException(); end;")
		// int toBaudrate(int value);
	}

	public final class Termios extends Struct {
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

	// Wrap the interface
	public interface NativeFunctions {
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

	final NativeFunctions nativeFunctions;

	public Termios_H() {
		this.nativeFunctions = LibraryLoader.create(NativeFunctions.class).load("c");
		TermiosDefines termiosFlags = JnrDefines.getInstance(TermiosDefines.class);
		this.NCCS = termiosFlags.NCCS();
		this._HAVE_STRUCT_TERMIOS_C_ISPEED = termiosFlags._HAVE_STRUCT_TERMIOS_C_ISPEED();
		this._HAVE_STRUCT_TERMIOS_C_OSPEED = termiosFlags._HAVE_STRUCT_TERMIOS_C_OSPEED();
		this.VINTR = termiosFlags.VINTR();
		this.VQUIT = termiosFlags.VQUIT();
		this.VERASE = termiosFlags.VERASE();
		this.VKILL = termiosFlags.VKILL();
		this.VEOF = termiosFlags.VEOF();
		this.VTIME = termiosFlags.VTIME();
		this.VMIN = termiosFlags.VMIN();
		this.VSWTC = termiosFlags.VSWTC();
		this.VSWTCH = termiosFlags.VSWTCH();
		this.VSTART = termiosFlags.VSTART();
		this.VSTOP = termiosFlags.VSTOP();
		this.VSUSP = termiosFlags.VSUSP();
		this.VEOL = termiosFlags.VEOL();
		this.VREPRINT = termiosFlags.VREPRINT();
		this.VDISCARD = termiosFlags.VDISCARD();
		this.VWERASE = termiosFlags.VWERASE();
		this.VLNEXT = termiosFlags.VLNEXT();
		this.VEOL2 = termiosFlags.VEOL2();
		this.IGNBRK = termiosFlags.IGNBRK();
		this.BRKINT = termiosFlags.BRKINT();
		this.IGNPAR = termiosFlags.IGNPAR();
		this.PARMRK = termiosFlags.PARMRK();
		this.INPCK = termiosFlags.INPCK();
		this.ISTRIP = termiosFlags.ISTRIP();
		this.INLCR = termiosFlags.INLCR();
		this.IGNCR = termiosFlags.IGNCR();
		this.ICRNL = termiosFlags.ICRNL();
		this.IUCLC = termiosFlags.IUCLC();
		this.IXON = termiosFlags.IXON();
		this.IXANY = termiosFlags.IXANY();
		this.IXOFF = termiosFlags.IXOFF();
		this.IMAXBEL = termiosFlags.IMAXBEL();
		this.IUTF8 = termiosFlags.IUTF8();
		this.OPOST = termiosFlags.OPOST();
		this.OLCUC = termiosFlags.OLCUC();
		this.ONLCR = termiosFlags.ONLCR();
		this.OCRNL = termiosFlags.OCRNL();
		this.ONOCR = termiosFlags.ONOCR();
		this.ONLRET = termiosFlags.ONLRET();
		this.OFILL = termiosFlags.OFILL();
		this.OFDEL = termiosFlags.OFDEL();
		this.NLDLY = termiosFlags.NLDLY();
		this.NL0 = termiosFlags.NL0();
		this.NL1 = termiosFlags.NL1();
		this.CRDLY = termiosFlags.CRDLY();
		this.CR0 = termiosFlags.CR0();
		this.CR1 = termiosFlags.CR1();
		this.CR2 = termiosFlags.CR2();
		this.CR3 = termiosFlags.CR3();
		this.TABDLY = termiosFlags.TABDLY();
		this.TAB0 = termiosFlags.TAB0();
		this.TAB1 = termiosFlags.TAB1();
		this.TAB2 = termiosFlags.TAB2();
		this.TAB3 = termiosFlags.TAB3();
		this.BSDLY = termiosFlags.BSDLY();
		this.BS0 = termiosFlags.BS0();
		this.BS1 = termiosFlags.BS1();
		this.FFDLY = termiosFlags.FFDLY();
		this.FF0 = termiosFlags.FF0();
		this.FF1 = termiosFlags.FF1();
		this.VTDLY = termiosFlags.VTDLY();
		this.VT0 = termiosFlags.VT0();
		this.VT1 = termiosFlags.VT1();
		this.XTABS = termiosFlags.XTABS();
		this.CBAUD = termiosFlags.CBAUD();
		this.B0 = termiosFlags.B0();
		this.B50 = termiosFlags.B50();
		this.B75 = termiosFlags.B75();
		this.B110 = termiosFlags.B110();
		this.B134 = termiosFlags.B134();
		this.B150 = termiosFlags.B150();
		this.B200 = termiosFlags.B200();
		this.B300 = termiosFlags.B300();
		this.B600 = termiosFlags.B600();
		this.B1200 = termiosFlags.B1200();
		this.B1800 = termiosFlags.B1800();
		this.B2400 = termiosFlags.B2400();
		this.B4800 = termiosFlags.B4800();
		this.B9600 = termiosFlags.B9600();
		this.B19200 = termiosFlags.B19200();
		this.B38400 = termiosFlags.B38400();
		this.EXTA = termiosFlags.EXTA();
		this.EXTB = termiosFlags.EXTB();
		this.CSIZE = termiosFlags.CSIZE();
		this.CS5 = termiosFlags.CS5();
		this.CS6 = termiosFlags.CS6();
		this.CS7 = termiosFlags.CS7();
		this.CS8 = termiosFlags.CS8();
		this.CSTOPB = termiosFlags.CSTOPB();
		this.CREAD = termiosFlags.CREAD();
		this.PARENB = termiosFlags.PARENB();
		this.PARODD = termiosFlags.PARODD();
		this.HUPCL = termiosFlags.HUPCL();
		this.CLOCAL = termiosFlags.CLOCAL();
		this.CBAUDEX = termiosFlags.CBAUDEX();
		this.B57600 = termiosFlags.B57600();
		this.B115200 = termiosFlags.B115200();
		this.B230400 = termiosFlags.B230400();
		this.B460800 = termiosFlags.B460800();
		this.B500000 = termiosFlags.B500000();
		this.B576000 = termiosFlags.B576000();
		this.B921600 = termiosFlags.B921600();
		this.B1000000 = termiosFlags.B1000000();
		this.B1152000 = termiosFlags.B1152000();
		this.B1500000 = termiosFlags.B1500000();
		this.B2000000 = termiosFlags.B2000000();
		this.B2500000 = termiosFlags.B2500000();
		this.B3000000 = termiosFlags.B3000000();
		this.B3500000 = termiosFlags.B3500000();
		this.B4000000 = termiosFlags.B4000000();
		this.__MAX_BAUD = termiosFlags.__MAX_BAUD();
		this.CIBAUD = termiosFlags.CIBAUD();
		this.CMSPAR = termiosFlags.CMSPAR();
		this.CRTSCTS = termiosFlags.CRTSCTS();
		this.ISIG = termiosFlags.ISIG();
		this.ICANON = termiosFlags.ICANON();
		this.XCASE = termiosFlags.XCASE();
		this.ECHO = termiosFlags.ECHO();
		this.ECHOE = termiosFlags.ECHOE();
		this.ECHOK = termiosFlags.ECHOK();
		this.ECHONL = termiosFlags.ECHONL();
		this.NOFLSH = termiosFlags.NOFLSH();
		this.TOSTOP = termiosFlags.TOSTOP();
		this.ECHOCTL = termiosFlags.ECHOCTL();
		this.ECHOPRT = termiosFlags.ECHOPRT();
		this.ECHOKE = termiosFlags.ECHOKE();
		this.FLUSHO = termiosFlags.FLUSHO();
		this.PENDIN = termiosFlags.PENDIN();
		this.IEXTEN = termiosFlags.IEXTEN();
		this.EXTPROC = termiosFlags.EXTPROC();
		this.TCOOFF = termiosFlags.TCOOFF();
		this.TCOON = termiosFlags.TCOON();
		this.TCIOFF = termiosFlags.TCIOFF();
		this.TCION = termiosFlags.TCION();
		this.TCIFLUSH = termiosFlags.TCIFLUSH();
		this.TCOFLUSH = termiosFlags.TCOFLUSH();
		this.TCIOFLUSH = termiosFlags.TCIOFLUSH();
		this.TCSANOW = termiosFlags.TCSANOW();
		this.TCSADRAIN = termiosFlags.TCSADRAIN();
		this.TCSAFLUSH = termiosFlags.TCSAFLUSH();
		this.PAREXT = termiosFlags.PAREXT();
		
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

	public int tcdrain(int fildes) {
		return nativeFunctions.tcdrain(fildes);
	}

	// int tcflow(int fildes, int action);
	// int tcflush(int fildes, int queue_selector);
	public int tcgetattr(int fildes, @Out @Transient Termios termios) {
		return nativeFunctions.tcgetattr(fildes, termios);
	}

	// pid_t tcgetsid(int fildes);
	public int tcsendbreak(int fildes, int duration) {
		return nativeFunctions.tcsendbreak(fildes, duration);
	}

	public int tcsetattr(int fildes, int optional_actions, Termios termios) {
		return nativeFunctions.tcsetattr(fildes, optional_actions, termios);
	}

	/**
	 * Returna a new struct termios.
	 * 
	 * @return
	 */
	public Termios createTermios() {
		return new Termios(Runtime.getRuntime(nativeFunctions));
	}

}