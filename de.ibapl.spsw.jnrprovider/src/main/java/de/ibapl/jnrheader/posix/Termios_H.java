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
package de.ibapl.jnrheader.posix;

import org.eclipse.jdt.annotation.Nullable;

import de.ibapl.jnrheader.Define;
import de.ibapl.jnrheader.IsDefined;
import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.POSIX;
import de.ibapl.jnrheader.Wrapper;
import jnr.ffi.Platform;

@Wrapper("termios.h")
public abstract class Termios_H implements JnrHeader {

	public class Termios {
		public int c_iflag;

		public int c_oflag;

		public int c_cflag;

		public int c_lflag;

		public byte c_line;

		public byte[] c_cc;
		
		@Nullable
		public Integer c_ispeed;
		
		@Nullable
		public Integer c_ospeed;
		
		public java.lang.String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(getClass().getSimpleName()).append(" { \n");
			final java.lang.String formatString = "    %s = 0x%08x\n";
			sb.append(java.lang.String.format(formatString, "c_iflag", c_iflag));
			sb.append(java.lang.String.format(formatString, "c_oflag", c_oflag));
			sb.append(java.lang.String.format(formatString, "c_cflag", c_cflag));
			sb.append(java.lang.String.format(formatString, "c_lflag", c_lflag));
			sb.append(java.lang.String.format(formatString, "c_line", c_line));
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
				sb.append(java.lang.String.format(c_ccFormatString, c_ccName, c_cc[i]));
			}

			if (c_ispeed != null) {
				sb.append(java.lang.String.format(formatString, "c_ispeed", c_ispeed));
			} else {
				sb.append("    c_ispeed not defined\n");
			}
			if (c_ospeed != null) {
				sb.append(java.lang.String.format(formatString, "c_ospeed", c_ospeed));
			} else {
				sb.append("    c_ospeed not defined\n");
			}

			sb.append("}\n");
			return sb.toString();
		}
}

	final public int NCCS = NCCS();

	final public boolean _HAVE_STRUCT_TERMIOS_C_ISPEED = _HAVE_STRUCT_TERMIOS_C_ISPEED();

	final public boolean _HAVE_STRUCT_TERMIOS_C_OSPEED = _HAVE_STRUCT_TERMIOS_C_OSPEED();

	@Nullable
	final public Integer VINTR = VINTR();

	@Nullable
	final public Integer VQUIT = VQUIT();

	@Nullable
	final public Integer VERASE = VERASE();

	@Nullable
	final public Integer VKILL = VKILL();

	@Nullable
	final public Integer VEOF = VEOF();

	@Nullable
	final public Integer VTIME = VTIME();

	@Nullable
	final public Integer VMIN = VMIN();

	@Nullable
	final public Integer VSWTC = VSWTC();

	@Nullable
	final public Integer VSWTCH = VSWTCH();

	@Nullable
	final public Integer VSTART = VSTART();

	@Nullable
	final public Integer VSTOP = VSTOP();

	@Nullable
	final public Integer VSUSP = VSUSP();

	@Nullable
	final public Integer VEOL = VEOL();

	@Nullable
	final public Integer VREPRINT = VREPRINT();

	@Nullable
	final public Integer VDISCARD = VDISCARD();

	@Nullable
	final public Integer VWERASE = VWERASE();

	@Nullable
	final public Integer VLNEXT = VLNEXT();

	@Nullable
	final public Integer VEOL2 = VEOL2();

	final public int IGNBRK = IGNBRK();

	final public int BRKINT = BRKINT();

	final public int IGNPAR = IGNPAR();

	final public int PARMRK = PARMRK();

	final public int INPCK = INPCK();

	final public int ISTRIP = ISTRIP();

	final public int INLCR = INLCR();

	final public int IGNCR = IGNCR();

	final public int ICRNL = ICRNL();

	final public int IUCLC = IUCLC();

	final public int IXON = IXON();

	final public int IXANY = IXANY();

	final public int IXOFF = IXOFF();

	final public int IMAXBEL = IMAXBEL();

	final public int IUTF8 = IUTF8();

	final public int OPOST = OPOST();

	final public int OLCUC = OLCUC();

	final public int ONLCR = ONLCR();

	final public int OCRNL = OCRNL();

	final public int ONOCR = ONOCR();

	final public int ONLRET = ONLRET();

	final public int OFILL = OFILL();

	final public int OFDEL = OFDEL();

	final public int NLDLY = NLDLY();

	final public int NL0 = NL0();

	final public int NL1 = NL1();

	final public int CRDLY = CRDLY();

	final public int CR0 = CR0();

	final public int CR1 = CR1();

	final public int CR2 = CR2();

	final public int CR3 = CR3();

	final public int TABDLY = TABDLY();

	final public int TAB0 = TAB0();

	final public int TAB1 = TAB1();

	final public int TAB2 = TAB2();

	final public int TAB3 = TAB3();

	final public int BSDLY = BSDLY();

	final public int BS0 = BS0();

	final public int BS1 = BS1();

	final public int FFDLY = FFDLY();

	final public int FF0 = FF0();

	final public int FF1 = FF1();

	final public int VTDLY = VTDLY();

	final public int VT0 = VT0();

	final public int VT1 = VT1();

	final public int XTABS = XTABS();

	final public int CBAUD = CBAUD();

	final public int B0 = B0();

	final public int B50 = B50();

	final public int B75 = B75();

	final public int B110 = B110();

	final public int B134 = B134();

	final public int B150 = B150();

	final public int B200 = B200();

	final public int B300 = B300();

	final public int B600 = B600();

	final public int B1200 = B1200();

	final public int B1800 = B1800();

	final public int B2400 = B2400();

	final public int B4800 = B4800();

	final public int B9600 = B9600();

	final public int B19200 = B19200();

	final public int B38400 = B38400();

	final public int EXTA = EXTA();

	final public int EXTB = EXTB();

	final public int CSIZE = CSIZE();

	final public int CS5 = CS5();

	final public int CS6 = CS6();

	final public int CS7 = CS7();

	final public int CS8 = CS8();

	final public int CSTOPB = CSTOPB();

	final public int CREAD = CREAD();

	final public int PARENB = PARENB();

	final public int PARODD = PARODD();

	final public int HUPCL = HUPCL();

	final public int CLOCAL = CLOCAL();

	final public int CBAUDEX = CBAUDEX();

	@Nullable
	final public Integer B57600 = B57600();

	@Nullable
	final public Integer B115200 = B115200();

	@Nullable
	final public Integer B230400 = B230400();

	@Nullable
	final public Integer B460800 = B460800();

	@Nullable
	final public Integer B500000 = B500000();

	@Nullable
	final public Integer B576000 = B576000();

	@Nullable
	final public Integer B921600 = B921600();

	@Nullable
	final public Integer B1000000 = B1000000();

	@Nullable
	final public Integer B1152000 = B1152000();

	@Nullable
	final public Integer B1500000 = B1500000();

	@Nullable
	final public Integer B2000000 = B2000000();

	@Nullable
	final public Integer B2500000 = B2500000();

	@Nullable
	final public Integer B3000000 = B3000000();

	@Nullable
	final public Integer B3500000 = B3500000();

	@Nullable
	final public Integer B4000000 = B4000000();

	final public int __MAX_BAUD = __MAX_BAUD();

	final public int CIBAUD = CIBAUD();

	@Nullable
	final public Integer CMSPAR = CMSPAR();

	final public int CRTSCTS = CRTSCTS();

	final public int ISIG = ISIG();

	final public int ICANON = ICANON();

	final public int XCASE = XCASE();

	final public int ECHO = ECHO();

	final public int ECHOE = ECHOE();

	final public int ECHOK = ECHOK();

	final public int ECHONL = ECHONL();

	final public int NOFLSH = NOFLSH();

	final public int TOSTOP = TOSTOP();

	final public int ECHOCTL = ECHOCTL();

	final public int ECHOPRT = ECHOPRT();

	final public int ECHOKE = ECHOKE();

	final public int FLUSHO = FLUSHO();

	final public int PENDIN = PENDIN();

	final public int IEXTEN = IEXTEN();

	final public int EXTPROC = EXTPROC();

	final public int TCOOFF = TCOOFF();

	final public int TCOON = TCOON();

	final public int TCIOFF = TCIOFF();

	final public int TCION = TCION();

	final public int TCIFLUSH = TCIFLUSH();

	final public int TCOFLUSH = TCOFLUSH();

	final public int TCIOFLUSH = TCIOFLUSH();

	final public int TCSANOW = TCSANOW();

	final public int TCSADRAIN = TCSADRAIN();

	final public int TCSAFLUSH = TCSAFLUSH();

	@Nullable
	final public Integer PAREXT = PAREXT();

	@POSIX
	protected abstract int __MAX_BAUD();

	@IsDefined()
	protected abstract boolean _HAVE_STRUCT_TERMIOS_C_ISPEED();

	@IsDefined()
	protected abstract boolean _HAVE_STRUCT_TERMIOS_C_OSPEED();

	@POSIX
	protected abstract int B0();

	@Define({Platform.OS.LINUX})
	protected abstract int B1000000();

	@POSIX
	protected abstract int B110();

	@Define({Platform.OS.LINUX})
	protected abstract int B115200();

	@Define({Platform.OS.LINUX})
	protected abstract int B1152000();

	@POSIX
	protected abstract int B1200();

	@POSIX
	protected abstract int B134();

	@POSIX
	protected abstract int B150();

	@Define({Platform.OS.LINUX})
	protected abstract int B1500000();

	@POSIX
	protected abstract int B1800();

	@POSIX
	protected abstract int B19200();

	@POSIX
	protected abstract int B200();

	@Define({Platform.OS.LINUX})
	protected abstract int B2000000();

	@Define({Platform.OS.LINUX})
	protected abstract int B230400();

	@POSIX
	protected abstract int B2400();

	@Define({Platform.OS.LINUX})
	protected abstract int B2500000();

	@POSIX
	protected abstract int B300();

	@Define({Platform.OS.LINUX})
	protected abstract int B3000000();

	@Define({Platform.OS.LINUX})
	protected abstract int B3500000();

	@POSIX
	protected abstract int B38400();

	@Define({Platform.OS.LINUX})
	protected abstract int B4000000();

	@Define({Platform.OS.LINUX})
	protected abstract int B460800();

	@POSIX
	protected abstract int B4800();

	@POSIX
	protected abstract int B50();

	@Define({Platform.OS.LINUX})
	protected abstract int B500000();

	@Define({Platform.OS.LINUX})
	protected abstract int B57600();

	@Define({Platform.OS.LINUX})
	protected abstract int B576000();

	@POSIX
	protected abstract int B600();

	@POSIX
	protected abstract int B75();

	@Define({Platform.OS.LINUX})
	protected abstract int B921600();

	@POSIX
	protected abstract int B9600();

	@POSIX
	protected abstract int BRKINT();

	@POSIX
	protected abstract int BS0();

	@POSIX
	protected abstract int BS1();

	@POSIX
	protected abstract int BSDLY();

	@POSIX
	protected abstract int CBAUD();

	@POSIX
	protected abstract int CBAUDEX();

	public abstract int cfgetispeed(Termios termios);

	public abstract int cfgetospeed(Termios termios);

	public abstract int cfsetispeed(Termios termios, int speed);

	public abstract int cfsetospeed(Termios termios, int speed);

	public abstract int cfsetspeed(Termios termios, int speed);

	@POSIX
	protected abstract int CIBAUD();

	@POSIX
	protected abstract int CLOCAL();

	@Define({Platform.OS.LINUX})
	@Nullable
	protected abstract Integer CMSPAR();

	@POSIX
	protected abstract int CR0();

	@POSIX
	protected abstract int CR1();

	@POSIX
	protected abstract int CR2();

	@POSIX
	protected abstract int CR3();

	@POSIX
	protected abstract int CRDLY();

	@POSIX
	protected abstract int CREAD();

	/**
	 * Returns a new struct termios.
	 * 
	 * @return
	 */
	public abstract Termios createTermios();

	@POSIX
	protected abstract int CRTSCTS();

	@POSIX
	protected abstract int CS5();

	@POSIX
	protected abstract int CS6();

	@POSIX
	protected abstract int CS7();

	@POSIX
	protected abstract int CS8();

	@POSIX
	protected abstract int CSIZE();

	@POSIX
	protected abstract int CSTOPB();

	@POSIX
	protected abstract int ECHO();

	@POSIX
	protected abstract int ECHOCTL();

	@POSIX
	protected abstract int ECHOE();

	@POSIX
	protected abstract int ECHOK();

	@POSIX
	protected abstract int ECHOKE();

	@POSIX
	protected abstract int ECHONL();

	@POSIX
	protected abstract int ECHOPRT();

	@POSIX
	protected abstract int EXTA();

	@POSIX
	protected abstract int EXTB();

	@POSIX
	protected abstract int EXTPROC();

	@POSIX
	protected abstract int FF0();

	@POSIX
	protected abstract int FF1();

	@POSIX
	protected abstract int FFDLY();

	@POSIX
	protected abstract int FLUSHO();

	@POSIX
	protected abstract int HUPCL();

	@POSIX
	protected abstract int ICANON();

	@POSIX
	protected abstract int ICRNL();

	@POSIX
	protected abstract int IEXTEN();

	@POSIX
	protected abstract int IGNBRK();

	@POSIX
	protected abstract int IGNCR();

	@POSIX
	protected abstract int IGNPAR();

	@POSIX
	protected abstract int IMAXBEL();

	@POSIX
	protected abstract int INLCR();

	@POSIX
	protected abstract int INPCK();

	@POSIX
	protected abstract int ISIG();

	@POSIX
	protected abstract int ISTRIP();

	@POSIX
	protected abstract int IUCLC();

	@POSIX
	protected abstract int IUTF8();

	@POSIX
	protected abstract int IXANY();

	@POSIX
	protected abstract int IXOFF();

	@POSIX
	protected abstract int IXON();

	@POSIX
	protected abstract int NCCS();

	@POSIX
	protected abstract int NL0();

	@POSIX
	protected abstract int NL1();

	@POSIX
	protected abstract int NLDLY();

	@POSIX
	protected abstract int NOFLSH();

	@POSIX
	protected abstract int OCRNL();

	@POSIX
	protected abstract int OFDEL();

	@POSIX
	protected abstract int OFILL();

	@POSIX
	protected abstract int OLCUC();

	@POSIX
	protected abstract int ONLCR();

	@POSIX
	protected abstract int ONLRET();

	@POSIX
	protected abstract int ONOCR();

	@POSIX
	protected abstract int OPOST();

	@POSIX
	protected abstract int PARENB();

	@Define({Platform.OS.LINUX})
	protected abstract Integer PAREXT();

	@POSIX
	protected abstract int PARMRK();

	@POSIX
	protected abstract int PARODD();

	@POSIX
	protected abstract int PENDIN();

	@POSIX
	protected abstract int TAB0();

	@POSIX
	protected abstract int TAB1();

	@POSIX
	protected abstract int TAB2();

	@POSIX
	protected abstract int TAB3();

	@POSIX
	protected abstract int TABDLY();

	public abstract int tcdrain(int fildes);

	public abstract int tcflow(int fildes, int action);

	public abstract int tcflush(int fildes, int queue_selector);

	public abstract int tcgetattr(int fildes, Termios termios);

	public abstract int tcgetsid(int fildes);

	@POSIX
	protected abstract int TCIFLUSH();

	@POSIX
	protected abstract int TCIOFF();

	@POSIX
	protected abstract int TCIOFLUSH();

	@POSIX
	protected abstract int TCION();

	@POSIX
	protected abstract int TCOFLUSH();

	@POSIX
	protected abstract int TCOOFF();

	@POSIX
	protected abstract int TCOON();

	@POSIX
	protected abstract int TCSADRAIN();

	@POSIX
	protected abstract int TCSAFLUSH();

	@POSIX
	protected abstract int TCSANOW();

	public abstract int tcsendbreak(int fildes, int duration);

	public abstract int tcsetattr(int fildes, int optional_actions, Termios termios);

	@POSIX
	protected abstract int TOSTOP();

	@Define({Platform.OS.LINUX})
	@Nullable
	protected abstract Integer VDISCARD();

	@Define({Platform.OS.LINUX})
	@Nullable
	protected abstract Integer VEOF();

	@Define({Platform.OS.LINUX})
	@Nullable
	protected abstract Integer VEOL();

	@Define({Platform.OS.LINUX})
	@Nullable
	protected abstract Integer VEOL2();

	@Define({Platform.OS.LINUX})
	@Nullable
	protected abstract Integer VERASE();

	@Define({Platform.OS.LINUX})
	@Nullable
	protected abstract Integer VINTR();

	@Define({Platform.OS.LINUX})
	@Nullable
	protected abstract Integer VKILL();

	@Define({Platform.OS.LINUX})
	@Nullable
	protected abstract Integer VLNEXT();

	@Define({Platform.OS.LINUX})
	@Nullable
	protected abstract Integer VMIN();

	@Define({Platform.OS.LINUX})
	@Nullable
	protected abstract Integer VQUIT();

	@Define({Platform.OS.LINUX})
	@Nullable
	protected abstract Integer VREPRINT();

	@Define({Platform.OS.LINUX})
	@Nullable
	protected abstract Integer VSTART();

	@Define({Platform.OS.LINUX})
	@Nullable
	protected abstract Integer VSTOP();

	@Define({Platform.OS.LINUX})
	@Nullable
	protected abstract Integer VSUSP();

	@Define({Platform.OS.LINUX})
	@Nullable
	protected abstract Integer VSWTC();

	@Define({Platform.OS.LINUX})
	@Nullable
	protected abstract Integer VSWTCH();

	@POSIX
	protected abstract int VT0();

	@POSIX
	protected abstract int VT1();

	@POSIX
	protected abstract int VTDLY();

	@Define({Platform.OS.LINUX})
	@Nullable
	protected abstract Integer VTIME();

	@Define({Platform.OS.LINUX})
	@Nullable
	protected abstract Integer VWERASE();

	@POSIX
	protected abstract int XCASE();

	@POSIX
	protected abstract int XTABS();

}