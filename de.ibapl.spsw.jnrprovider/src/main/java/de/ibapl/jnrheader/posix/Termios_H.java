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

import static de.ibapl.jnrheader.POSIX.Marker.XSI;
import static de.ibapl.jnrheader.POSIX_Removed.Issue.ISSUE_6;
import static de.ibapl.jnrheader.Defined.isDefined;

import org.eclipse.jdt.annotation.Nullable;

import de.ibapl.jnrheader.Defined;
import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.NativeFunction;
import de.ibapl.jnrheader.POSIX;
import de.ibapl.jnrheader.POSIX_Removed;
import de.ibapl.jnrheader.POSIX_XSI_Conformant;
import de.ibapl.jnrheader.Wrapper;

/**
 * 
 * Wrapper around POSIX termios.h with optional,os and architecture extensions.
 * See specs at: <a href=
 * "http://pubs.opengroup.org/onlinepubs/9699919799/basedefs/termios.h.html">http://pubs.opengroup.org/onlinepubs/9699919799/basedefs/termios.h.html</a>
 * 
 * @author aploese
 *
 */
@Wrapper("termios.h")
public abstract class Termios_H<T extends Termios_H<T>.Termios> implements JnrHeader {

	@NativeFunction
	public interface cfmakeraw<T extends Termios_H<T>.Termios> {
		/**
		 * Set the terminal to something like the "raw" mode of the old Version 7
		 * terminal driver.
		 * 
		 * @param termios
		 *            the {@link Termios_H.Termios} structure.
		 */
		void cfmakeraw(T termios);
	}

	@NativeFunction
	public interface cfsetspeed<T extends Termios_H<T>.Termios> {
		/**
		 * is a 4.4BSD extension. It takes the same arguments as
		 * {@link Termios_H#cfsetispeed(Termios, int)}, and sets both input and output
		 * speed.
		 * 
		 * @param termios
		 *            the {@link #Termios_H.Termios} structure.
		 * @param speed
		 * @return
		 */
		int cfsetspeed(T termios, int speed);
	}

	/**
	 * Wrapper for the termios struct.
	 * 
	 * @author aploese
	 *
	 */
	public class Termios {
		/**
		 * Input modes.
		 */
		@POSIX
		public int c_iflag;
		/**
		 * Output modes.
		 */
		@POSIX
		public int c_oflag;
		/**
		 * Control modes
		 */
		@POSIX
		public int c_cflag;
		/**
		 * Local modes.
		 */
		@POSIX
		public int c_lflag;

		/**
		 * Control characters.
		 */
		@POSIX
		public byte[] c_cc;

		protected Termios() {
			super();
		}

		protected void c_cflag2String(StringBuilder sb, int c_cflag) {
			if ((CSIZE & c_cflag) == CS5) {
				sb.append("CS5 ");
				c_cflag &= ~CS5;
			} else if ((CSIZE & c_cflag) == CS6) {
				sb.append("CS6 ");
				c_cflag &= ~CS6;
			} else if ((CSIZE & c_cflag) == CS7) {
				sb.append("CS7 ");
				c_cflag &= ~CS7;
			} else if ((CSIZE & c_cflag) == CS8) {
				sb.append("CS8 ");
				c_cflag &= ~CS8;
			}
			if ((CSTOPB & c_cflag) == CSTOPB) {
				sb.append("CSTOPB ");
				c_cflag &= ~CSTOPB;
			}
			if ((CREAD & c_cflag) == CREAD) {
				sb.append("CREAD ");
				c_cflag &= ~CREAD;
			}
			if ((PARENB & c_cflag) == PARENB) {
				sb.append("PARENB ");
				c_cflag &= ~PARENB;
			}
			if ((OPOST & c_cflag) == PARODD) {
				sb.append("PARODD ");
				c_cflag &= ~PARODD;
			}
			if ((HUPCL & c_cflag) == HUPCL) {
				sb.append("HUPCL ");
				c_cflag &= ~HUPCL;
			}
			if ((CLOCAL & c_cflag) == CLOCAL) {
				sb.append("CLOCAL ");
				c_cflag &= ~CLOCAL;
			}
			if (c_cflag != 0) {
				sb.append(String.format("0x%08x", c_cflag));
			}
		}

		protected void c_iflag2String(StringBuilder sb, int c_iflag) {
			if ((BRKINT & c_iflag) == BRKINT) {
				sb.append("BRKINT ");
				c_iflag &= ~BRKINT;
			}
			if ((ICRNL & c_iflag) == ICRNL) {
				sb.append("ICRNL ");
				c_iflag &= ~ICRNL;
			}
			if ((IGNBRK & c_iflag) == IGNBRK) {
				sb.append("IGNBRK ");
				c_iflag &= ~IGNBRK;
			}
			if ((IGNCR & c_iflag) == IGNCR) {
				sb.append("IGNCR ");
				c_iflag &= ~IGNCR;
			}
			if ((IGNPAR & c_iflag) == IGNPAR) {
				sb.append("IGNPAR ");
				c_iflag &= ~IGNPAR;
			}
			if ((INLCR & c_iflag) == INLCR) {
				sb.append("INLCR ");
				c_iflag &= ~INLCR;
			}
			if ((INPCK & c_iflag) == INPCK) {
				sb.append("INPCK ");
				c_iflag &= ~INPCK;
			}
			if ((ISTRIP & c_iflag) == ISTRIP) {
				sb.append("ISTRIP ");
				c_iflag &= ~ISTRIP;
			}
			if ((IXANY & c_iflag) == IXANY) {
				sb.append("IXANY ");
				c_iflag &= ~IXANY;
			}
			if ((IXOFF & c_iflag) == IXOFF) {
				sb.append("IXOFF ");
				c_iflag &= ~IXOFF;
			}
			if ((IXON & c_iflag) == IXON) {
				sb.append("IXON ");
				c_iflag &= ~IXON;
			}
			if ((PARMRK & c_iflag) == PARMRK) {
				sb.append("PARMRK ");
				c_iflag &= ~PARMRK;
			}
			if (c_iflag != 0) {
				sb.append(String.format("0x%08x", c_iflag));
			}
		}

		protected void c_lflag2String(StringBuilder sb, int c_lflag) {
			if ((ECHO & c_lflag) == ECHO) {
				sb.append("ECHO ");
				c_lflag &= ~ECHO;
			}
			if ((ECHOE & c_lflag) == ECHOE) {
				sb.append("ECHOE ");
				c_lflag &= ~ECHOE;
			}
			if ((ECHOK & c_lflag) == ECHOK) {
				sb.append("ECHOK ");
				c_lflag &= ~ECHOK;
			}
			if ((ECHONL & c_lflag) == ECHONL) {
				sb.append("ECHONL ");
				c_lflag &= ~ECHONL;
			}
			if ((ICANON & c_lflag) == ICANON) {
				sb.append("ICANON ");
				c_lflag &= ~ICANON;
			}
			if ((IEXTEN & c_lflag) == IEXTEN) {
				sb.append("IEXTEN ");
				c_lflag &= ~IEXTEN;
			}
			if ((ISIG & c_lflag) == ISIG) {
				sb.append("ISIG ");
				c_lflag &= ~ISIG;
			}
			if ((NOFLSH & c_lflag) == NOFLSH) {
				sb.append("NOFLSH ");
				c_lflag &= ~NOFLSH;
			}
			if ((TOSTOP & c_lflag) == TOSTOP) {
				sb.append("TOSTOP ");
				c_lflag &= ~TOSTOP;
			}
			if (c_lflag != 0) {
				sb.append(String.format("0x%08x", c_lflag));
			}
		}

		protected void c_oflag2String(StringBuilder sb, int c_oflag) {
			if ((OPOST & c_oflag) == OPOST) {
				sb.append("OPOST ");
				c_oflag &= ~OPOST;
			}
			if ((ONLCR & c_oflag) == ONLCR) {
				sb.append("ONLCR ");
				c_oflag &= ~ONLCR;
			}
			if ((OCRNL & c_oflag) == OCRNL) {
				sb.append("OCRNL ");
				c_oflag &= ~OCRNL;
			}
			if ((ONOCR & c_oflag) == ONOCR) {
				sb.append("ONOCR ");
				c_oflag &= ~ONOCR;
			}
			if ((ONLRET & c_oflag) == ONLRET) {
				sb.append("ONLRET ");
				c_oflag &= ~ONLRET;
			}
			if ((OFILL & c_oflag) == OFILL) {
				sb.append("OFILL ");
				c_oflag &= ~OFILL;
			}
			if ((NLDLY & c_oflag) == NL0) {
				sb.append("NL0 ");
				c_oflag &= ~NL0;
			} else if ((NLDLY & c_oflag) == NL1) {
				sb.append("NL1 ");
				c_oflag &= ~NL1;
			}
			if ((CRDLY & c_oflag) == CR0) {
				sb.append("CR0 ");
				c_oflag &= ~CR0;
			} else if ((CRDLY & c_oflag) == CR1) {
				sb.append("CR1 ");
				c_oflag &= ~CR1;
			} else if ((CRDLY & c_oflag) == CR2) {
				sb.append("CR2 ");
				c_oflag &= ~CR2;
			} else if ((CRDLY & c_oflag) == CR3) {
				sb.append("CR3 ");
				c_oflag &= ~CR3;
			}
			if ((TABDLY & c_oflag) == TAB0) {
				sb.append("TAB0 ");
				c_oflag &= ~TAB0;
			} else if ((TABDLY & c_oflag) == TAB1) {
				sb.append("TAB1 ");
				c_oflag &= ~TAB1;
			} else if ((TABDLY & c_oflag) == TAB2) {
				sb.append("TAB2 ");
				c_oflag &= ~TAB2;
			} else if ((TABDLY & c_oflag) == TAB3) {
				sb.append("TAB3 ");
				c_oflag &= ~TAB3;
			}
			if ((BSDLY & c_oflag) == BS0) {
				sb.append("BS0 ");
				c_oflag &= ~BS0;
			} else if ((BSDLY & c_oflag) == BS1) {
				sb.append("BS1 ");
				c_oflag &= ~BS1;
			}
			if ((VTDLY & c_oflag) == VT0) {
				sb.append("VT0 ");
				c_oflag &= ~VT0;
			} else if ((VTDLY & c_oflag) == VT1) {
				sb.append("VT1 ");
				c_oflag &= ~VT1;
			}
			if ((FFDLY & c_oflag) == FF0) {
				sb.append("FF0 ");
				c_oflag &= ~FF0;
			} else if ((FFDLY & c_oflag) == FF1) {
				sb.append("FF1 ");
				c_oflag &= ~FF1;
			}
			if (c_oflag != 0) {
				sb.append(String.format("0x%08x", c_oflag));
			}
		}

		protected void c_cc2String(StringBuilder sb, byte[] c_cc) {
			final java.lang.String c_ccFormatString = "\"\n\tc_cc[%s] = 0x%02x";
			for (int i = 0; i < NCCS; i++) {
				java.lang.String c_ccName = java.lang.String.valueOf(i);
				if (VINTR != null && VINTR == i) {
					c_ccName = "VINTR";
				} else if (VQUIT == i) {
					c_ccName = "VQUIT";
				} else if (VERASE == i) {
					c_ccName = "VERASE";
				} else if (VKILL == i) {
					c_ccName = "VKILL";
				} else if (VEOF == i) {
					c_ccName = "VEOF";
				} else if (VTIME == i) {
					c_ccName = "VTIME";
				} else if (VMIN == i) {
					c_ccName = "VMIN";
				} else if (VSWTC != null && VSWTC == i) {
					c_ccName = "VSWTC";
				} else if (VSTART == i) {
					c_ccName = "VSTART";
				} else if (VSTOP == i) {
					c_ccName = "VSTOP";
				} else if (VSUSP == i) {
					c_ccName = "VSUSP";
				} else if (VEOL == i) {
					c_ccName = "VEOL";
				} else if (isDefined(VREPRINT) && VREPRINT == i) {
					c_ccName = "VREPRINT";
				} else if (isDefined(VDISCARD) && VDISCARD == i) {
					c_ccName = "VDISCARD";
				} else if (isDefined(VWERASE) && VWERASE == i) {
					c_ccName = "VWERASE";
				} else if (isDefined(VLNEXT) && VLNEXT == i) {
					c_ccName = "VLNEXT";
				} else if (isDefined(VEOL2) && VEOL2 == i) {
					c_ccName = "VEOL2";
				}
				sb.append(java.lang.String.format(c_ccFormatString, c_ccName, c_cc[i]));
			}

		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(getClass().getSimpleName()).append(" {");
			sb.append("\n\tc_iflag = \"");
			c_iflag2String(sb, c_iflag);
			sb.append("\"\n\tc_oflag = \"");
			c_oflag2String(sb, c_oflag);
			sb.append("\"\n\tc_cflag = \"");
			c_cflag2String(sb, c_cflag);
			sb.append("\"\n\tc_lflag = \"");
			c_lflag2String(sb, c_lflag);
			sb.append("\"\n\tc_line = \"");
			c_cc2String(sb, c_cc);
			sb.append("\n}");
			return sb.toString();
		}
	}

	@Nullable
	final public Integer __MAX_BAUD = __MAX_BAUD();

	@Nullable
	final public Defined _HAVE_STRUCT_TERMIOS_C_ISPEED = _HAVE_STRUCT_TERMIOS_C_ISPEED();

	@Nullable
	final public Defined _HAVE_STRUCT_TERMIOS_C_OSPEED = _HAVE_STRUCT_TERMIOS_C_OSPEED();

	/**
	 * Hang up.
	 */
	@POSIX
	final public int B0 = B0();

	@POSIX
	final public int B50 = B50();

	@POSIX
	final public int B75 = B75();

	@POSIX
	final public int B110 = B110();

	@POSIX
	final public int B134 = B134();

	@POSIX
	final public int B150 = B150();

	@POSIX
	final public int B200 = B200();

	@POSIX
	final public int B300 = B300();

	@POSIX
	final public int B600 = B600();

	@POSIX
	final public int B1200 = B1200();

	@POSIX
	final public int B1800 = B1800();

	@POSIX
	final public int B2400 = B2400();

	@POSIX
	final public int B4800 = B4800();

	@POSIX
	final public int B9600 = B9600();

	@POSIX
	final public int B19200 = B19200();

	@POSIX
	final public int B38400 = B38400();

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

	/**
	 * Signal interrupt on break, used in {@link Termios#c_iflag}.
	 */
	@POSIX
	final public int BRKINT = BRKINT();

	/**
	 * Backspace-delay type 0, used in {@link Termios#c_oflag}.
	 */
	@POSIX(XSI)
	final public int BS0 = BS0();

	/**
	 * Backspace-delay type 0, used in {@link Termios#c_oflag}.
	 */
	@POSIX(XSI)
	final public int BS1 = BS1();

	/**
	 * Select backspace delays, used in {@link Termios#c_oflag}. Values:
	 * <li>{@link #BS0}</li>
	 * <li>{@link #BS1}</li>
	 */
	@POSIX(XSI)
	final public int BSDLY = BSDLY();

	/**
	 * Speed mask (4+1 bits), used in {@link Termios#c_cflag}.
	 */
	@POSIX_XSI_Conformant
	@Nullable
	final public Integer CBAUD = CBAUD();

	/**
	 * CBAUDEX is a mask for the speeds beyond those defined in POSIX.1 (57600 and
	 * above). Thus, B57600 & CBAUDEX is nonzero. Used in {@link Termios#c_cflag}.
	 */
	@Nullable
	final public Integer CBAUDEX = CBAUDEX();

	/**
	 * Mask for input speeds. The values for the CIBAUD bits are the same as the
	 * values for the CBAUD bits, shifted left IBSHIFT bits. Used in
	 * {@link Termios#c_cflag}.
	 */
	@Nullable
	final public Integer CIBAUD = CIBAUD();

	/**
	 * Ignore modem status lines, used in {@link Termios#c_cflag}.
	 */
	@POSIX
	final public int CLOCAL = CLOCAL();

	/**
	 * Use "stick" (mark/space) parity (supported on certain serial devices): if
	 * {@link #PARODD} is set, the parity bit is always 1; if {@link #PARODD} is not
	 * set, then the parity bit is always 0. Used in {@link Termios#c_cflag}. This
	 * equals {@link #PAREXT} for other OS.
	 */
	@Nullable
	final public Integer CMSPAR = CMSPAR();

	/**
	 * Carriage-return delay type 0, used in {@link Termios#c_oflag}.
	 */
	@POSIX(XSI)
	final public int CR0 = CR0();

	/**
	 * Carriage-return delay type 1, used in {@link Termios#c_oflag}.
	 */
	@POSIX(XSI)
	final public int CR1 = CR1();

	/**
	 * Carriage-return delay type 2, used in {@link Termios#c_oflag}.
	 */
	@POSIX(XSI)
	final public int CR2 = CR2();

	/**
	 * Carriage-return delay type 3, used in {@link Termios#c_oflag}.
	 */
	@POSIX(XSI)
	final public int CR3 = CR3();

	/**
	 * Select carriage-return delays, used in {@link Termios#c_oflag}. Values:
	 * <li>{@link #CR0}</li>
	 * <li>{@link #CR1}</li>
	 * <li>{@link #CR2}</li>
	 * <li>{@link #CR3}</li>
	 */
	@POSIX(XSI)
	final public int CRDLY = CRDLY();

	/**
	 * Enable receiver, used in {@link Termios#c_cflag}.
	 */
	@POSIX
	final public int CREAD = CREAD();

	/**
	 * Enable RTS/CTS (hardware) flow control, used in {@link Termios#c_cflag}.
	 */
	@Nullable
	final public int CRTSCTS = CRTSCTS();

	/**
	 * Character size 5 bits, , used in {@link Termios#c_cflag}.
	 */
	@POSIX
	final public int CS5 = CS5();

	/**
	 * Character size 6 bits, , used in {@link Termios#c_cflag}.
	 */
	@POSIX
	final public int CS6 = CS6();

	/**
	 * Character size 7 bits, , used in {@link Termios#c_cflag}.
	 */
	@POSIX
	final public int CS7 = CS7();

	/**
	 * Character size 8 bits, , used in {@link Termios#c_cflag}.
	 */
	@POSIX
	final public int CS8 = CS8();

	/**
	 * Character size, used in {@link Termios#c_cflag}. Values:
	 * <li>{@link #CS5}</li>
	 * <li>{@link #CS6}</li>
	 * <li>{@link #CS7}</li>
	 * <li>{@link #CS8}</li>
	 */
	@POSIX
	final public int CSIZE = CSIZE();

	/**
	 * Send two stop bits, else one, used in {@link Termios#c_cflag}.
	 */
	@POSIX
	final public int CSTOPB = CSTOPB();

	/**
	 * Echo only when a process is reading, used in {@link Termios#c_lflag}.
	 */
	@POSIX_XSI_Conformant
	@Nullable
	final public Integer DEFECHO = DEFECHO();

	/**
	 * Enable echo, used in {@link Termios#c_lflag}.
	 */
	@POSIX
	final public int ECHO = ECHO();

	/**
	 * If ECHO is also set, terminal special characters other than TAB, NL, START,
	 * and STOP are echoed as ^X, where X is the character with ASCII code 0x40
	 * greater than the special character. For example, character 0x08 (BS) is
	 * echoed as ^H. Used in {@link Termios#c_lflag}.
	 */
	@POSIX_XSI_Conformant
	@Nullable
	final public Integer ECHOCTL = ECHOCTL();

	/**
	 * Echo erase character as error-correcting backspace, used in
	 * {@link Termios#c_lflag}.
	 */
	@POSIX
	final public int ECHOE = ECHOE();

	/**
	 * Echo KILL, used in {@link Termios#c_lflag}.
	 */
	@POSIX
	final public int ECHOK = ECHOK();

	/**
	 * If {@link #ICANON} is also set, KILL is echoed by eras‐ ing each character on
	 * the line, as specified by {@link #ECHOE} and {@link #ECHOPRT}, used in
	 * {@link Termios#c_lflag}.
	 */
	@POSIX_XSI_Conformant
	@Nullable
	final public Integer ECHOKE = ECHOKE();

	/**
	 * Echo NL, used in {@link Termios#c_lflag}.
	 */
	@POSIX
	final public int ECHONL = ECHONL();

	/**
	 * If ICANON and ECHO are also set, characters are printed as they are being
	 * erased, used in {@link Termios#c_lflag}.
	 */
	@Nullable
	final public Integer ECHOPRT = ECHOPRT();

	// TODO
	/**
	 * UNIX V7 and several later systems have a list of speeds where after the
	 * fourteen values B0, ..., B9600 one finds the two constants EXTA, EXTB
	 * ("External A" and "External B"). Many systems extend the list with much
	 * higher speeds.
	 */
	@POSIX_XSI_Conformant
	@Nullable
	final public Integer EXTA = EXTA();

	/**
	 * UNIX V7 and several later systems have a list of speed values where after the
	 * fourteen values B0, ..., B9600 one finds the two constants EXTA, EXTB
	 * ("External A" and "External B"). Many systems extend the list with much
	 * higher speed values.
	 */
	@POSIX_XSI_Conformant
	@Nullable
	final public Integer EXTB = EXTB();

	// TODO document me!
	@Nullable
	final public Integer EXTPROC = EXTPROC();

	/**
	 * Form-feed delay type 0, used in {@link Termios#c_oflag}.
	 */
	@POSIX(XSI)
	final public int FF0 = FF0();

	/**
	 * Form-feed delay type 1, used in {@link Termios#c_oflag}.
	 */
	@POSIX(XSI)
	final public int FF1 = FF1();

	/**
	 * Select form-feed delays, used in {@link Termios#c_oflag}. Values:
	 * <li>{@link #FF0}</li>
	 * <li>{@link #FF1}</li>
	 */
	@POSIX(XSI)
	final public int FFDLY = FFDLY();

	/**
	 * Output is being flushed. This flag is toggled by typing the DISCARD charac‐
	 * ter, used in {@link Termios#c_lflag}
	 */
	@POSIX_XSI_Conformant
	@Nullable
	final public Integer FLUSHO = FLUSHO();

	/**
	 * Hang up on last close, used in {@link Termios#c_cflag}.
	 */
	@POSIX
	final public int HUPCL = HUPCL();

	/**
	 * Canonical input (erase and kill processing), used in {@link Termios#c_lflag}.
	 */
	@POSIX
	final public int ICANON = ICANON();

	/**
	 * Map CR to NL on input, used in {@link Termios#c_iflag}.
	 */
	@POSIX
	final public int ICRNL = ICRNL();

	/**
	 * Enable extended input character processing, used in {@link Termios#c_lflag}.
	 */
	@POSIX
	final public int IEXTEN = IEXTEN();

	/**
	 * Ignore break condition, used in {@link Termios#c_iflag}.
	 */
	@POSIX
	final public int IGNBRK = IGNBRK();

	/**
	 * Ignore CR, used in {@link Termios#c_iflag}.
	 */
	@POSIX
	final public int IGNCR = IGNCR();

	/**
	 * Ignore characters with parity errors, used in {@link Termios#c_iflag}.
	 */
	@POSIX
	final public int IGNPAR = IGNPAR();

	/**
	 * Ring bell when input queue is full, used in {@link Termios#c_iflag}.
	 */
	@Nullable
	final public int IMAXBEL = IMAXBEL();

	/**
	 * Map NL to CR on input, used in {@link Termios#c_iflag}.
	 */
	@POSIX
	final public int INLCR = INLCR();

	/**
	 * Enable input parity check, used in {@link Termios#c_iflag}.
	 */
	@POSIX
	final public int INPCK = INPCK();

	/**
	 * Enable signals, used in {@link Termios#c_lflag}.
	 */
	@POSIX
	final public int ISIG = ISIG();

	/**
	 * Strip character, used in {@link Termios#c_iflag}.
	 */
	@POSIX
	final public int ISTRIP = ISTRIP();

	/**
	 * Map uppercase characters to lowercase on input, used in
	 * {@link Termios#c_iflag}.
	 */
	@POSIX_Removed(ISSUE_6)
	@Nullable
	final public Integer IUCLC = IUCLC();

	/**
	 * Input is UTF8; this allows character-erase to be correctly performed in
	 * cooked mode. Used in {@link Termios#c_iflag}.
	 */
	@Nullable
	final public Integer IUTF8 = IUTF8();

	/**
	 * Enable any character to restart output, used in {@link Termios#c_iflag}.
	 */
	@POSIX
	final public int IXANY = IXANY();

	/**
	 * Enable start/stop input control, used in {@link Termios#c_iflag}.
	 */
	@POSIX
	final public int IXOFF = IXOFF();

	/**
	 * Enable start/stop output control, used in {@link Termios#c_iflag}.
	 */
	@POSIX
	final public int IXON = IXON();

	/**
	 * Block output from a noncurrent shell layer. For use by shl (shell layers),
	 * used in {@link Termios#c_lflag}.
	 */
	@POSIX_XSI_Conformant
	@Nullable
	final public Integer LOBLK = LOBLK();

	/**
	 * Size of the array {@link Termios#c_cc} for control characters.
	 */
	@POSIX
	final public int NCCS = NCCS();

	/**
	 * Newline type 0, used in {@link Termios#c_oflag}.
	 */
	@POSIX(XSI)
	final public int NL0 = NL0();

	/**
	 * Newline type 1, used in {@link Termios#c_oflag}.
	 */
	@POSIX(XSI)
	final public int NL1 = NL1();

	/**
	 * Select newline delay, used in {@link Termios#c_oflag}. Values:
	 * <li>{@link #NL0}</li>
	 * <li>{@link #NL1}</li>
	 */
	@POSIX(XSI)
	final public int NLDLY = NLDLY();

	/**
	 * Disable flush after interrupt or quit, used in {@link Termios#c_lflag}.
	 */
	@POSIX
	final public int NOFLSH = NOFLSH();

	/**
	 * Map CR to NL on output, used in {@link Termios#c_oflag}.
	 */
	@POSIX(XSI)
	final public int OCRNL = OCRNL();

	/**
	 * NL performs CR function, used in {@link Termios#c_oflag}.
	 */
	@POSIX(XSI)
	final public int OFDEL = OFDEL();

	/**
	 * Use fill characters for delay, used in {@link Termios#c_oflag}.
	 */
	@POSIX(XSI)
	final public int OFILL = OFILL();

	/**
	 * Map lowercase characters to uppercase , used in {@link Termios#c_oflag}.
	 */
	@POSIX_Removed(ISSUE_6)
	@Nullable
	final public Integer OLCUC = OLCUC();

	/**
	 * Map NL to CR-NL on output, used in {@link Termios#c_oflag}.
	 */
	@POSIX(XSI)
	final public int ONLCR = ONLCR();

	/**
	 * NL performs CR function, used in {@link Termios#c_oflag}.
	 */
	@POSIX(XSI)
	final public int ONLRET = ONLRET();

	/**
	 * No CR output at column 0, used in {@link Termios#c_oflag}.
	 */
	@POSIX(XSI)
	final public int ONOCR = ONOCR();

	/**
	 * Post-process output, used in {@link Termios#c_oflag}.
	 */
	@POSIX
	final public int OPOST = OPOST();

	/**
	 * Parity enable, used in {@link Termios#c_cflag}.
	 */
	@POSIX
	final public int PARENB = PARENB();

	/**
	 * Specifies extended parity for mark and space parity, used in
	 * {@link Termios#c_iflag}. This equals {@link #CMSPAR} for other OS.
	 */
	@Nullable
	final public Integer PAREXT = PAREXT();

	/**
	 * Mark parity errors, used in {@link Termios#c_iflag}.
	 */
	@POSIX
	final public int PARMRK = PARMRK();

	/**
	 * Odd parity, else even, used in {@link Termios#c_cflag}.
	 */
	@POSIX
	final public int PARODD = PARODD();

	/**
	 * All characters in the input queue are reprinted when the next character is
	 * read. (bash(1) handles typeahead this way.), used in {@link Termios#c_lflag}.
	 */
	@POSIX_XSI_Conformant
	@Nullable
	final public Integer PENDIN = PENDIN();

	@POSIX_XSI_Conformant
	@Nullable
	final public Integer SWTCH = SWITCH();
	/**
	 * Horizontal-tab delay type 0, used in {@link Termios#c_oflag}.
	 */
	@POSIX(XSI)
	final public int TAB0 = TAB0();

	/**
	 * Horizontal-tab delay type 1, used in {@link Termios#c_oflag}.
	 */
	@POSIX(XSI)
	final public int TAB1 = TAB1();

	/**
	 * Horizontal-tab delay type 2, used in {@link Termios#c_oflag}.
	 */
	@POSIX(XSI)
	final public int TAB2 = TAB2();

	/**
	 * Horizontal-tab delay type 3, used in {@link Termios#c_oflag}.
	 */
	@POSIX(XSI)
	final public int TAB3 = TAB3();

	/**
	 * Select horizontal-tab delays, used in {@link Termios#c_oflag}. Values:
	 * <li>{@link #TAB0}</li>
	 * <li>{@link #TAB1}</li>
	 * <li>{@link #TAB2}</li>
	 * <li>{@link #TAB3}</li>
	 */
	@POSIX(XSI)
	final public int TABDLY = TABDLY();

	/**
	 * Flush pending input, used with {@link #tcflush(int, int)}.
	 */
	@POSIX
	final public int TCIFLUSH = TCIFLUSH();

	/**
	 * Transmit a STOP character, intended to suspend input data, used with
	 * {@link #tcflow(int, int)}.
	 */
	@POSIX
	final public int TCIOFF = TCIOFF();

	/**
	 * Flush both pending input and untransmitted output, used with
	 * {@link #tcflush(int, int)}.
	 */
	@POSIX
	final public int TCIOFLUSH = TCIOFLUSH();

	/**
	 * Transmit a START character, intended to restart input data, used with
	 * {@link #tcflow(int, int)}.
	 */
	@POSIX
	final public int TCION = TCION();

	/**
	 * Flush untransmitted output, used with {@link #tcflush(int, int)}.
	 */
	@POSIX
	final public int TCOFLUSH = TCOFLUSH();

	/**
	 * Suspend output, used with {@link #tcflow(int, int)}.
	 */
	@POSIX
	final public int TCOOFF = TCOOFF();

	/**
	 * Restart output, used with {@link #tcflow(int, int)}.
	 */
	@POSIX
	final public int TCOON = TCOON();

	/**
	 * Change attributes when output has drained, used with
	 * {@link #tcsetattr(int, int, Termios)}.
	 */
	@POSIX
	final public int TCSADRAIN = TCSADRAIN();

	/**
	 * Change attributes when output has drained; also flush pending input, used
	 * with {@link #tcsetattr(int, int, Termios)}.
	 */
	@POSIX
	final public int TCSAFLUSH = TCSAFLUSH();

	/**
	 * Change attributes immediately, used with
	 * {@link #tcsetattr(int, int, Termios)}.
	 */
	@POSIX
	final public int TCSANOW = TCSANOW();

	/**
	 * Send {@link #SIGTTOU} for background output, used in {@link Termios#c_lflag}.
	 */
	@POSIX
	final public int TOSTOP = TOSTOP();

	/**
	 * Toggle: start/stop discarding pending output. Recognized when {@link #IEXTEN}
	 * is set, and then not passed as input. Array index of {@link Termios#c_cc}.
	 */
	@POSIX_XSI_Conformant
	@Nullable
	final public Integer VDISCARD = VDISCARD();

	/**
	 * Delayed suspend character ({@link #DSUSP}): send #SIGTSTP signal when the
	 * character is read by the user program. Recognized when {@link #IEXTEN} and
	 * {@link #ISIG} are set, and the system supports job control, and then not
	 * passed as input. Array index of {@link Termios#c_cc}.
	 */
	@POSIX_XSI_Conformant
	@Nullable
	final public Integer VDSUSP = VDSUSP();

	/**
	 * <b>Canonical Mode</b>, EOF character. Array index of {@link Termios#c_cc}.
	 */
	@POSIX
	final public int VEOF = VEOF();

	/**
	 * <b>Canonical Mode</b>, EOL character. Array index of {@link Termios#c_cc}.
	 */
	@POSIX
	final public int VEOL = VEOL();

	/**
	 * Yet another end-of-line character (EOL2). Recognized when {@link #ICANON} is
	 * set. Array index of {@link Termios#c_cc}.
	 * 
	 */
	@Nullable
	final public Integer VEOL2 = VEOL2();

	/**
	 * <b>Canonical Mode</b>, ERASE character. Array index of {@link Termios#c_cc}.
	 */
	@POSIX
	final public int VERASE = VERASE();

	/**
	 * <b>Canonical Mode</b>, <b>Non-Canonical Mode</b>, INTR character. Array index
	 * of {@link Termios#c_cc}.
	 */
	@POSIX
	final public Integer VINTR = VINTR();

	/**
	 * <b>Canonical Mode</b>, KILL character. Array index of {@link Termios#c_cc}.
	 */
	@POSIX
	final public int VKILL = VKILL();

	/**
	 * Literal next (LNEXT). Quotes the next input character, depriving it of a
	 * possible special meaning. Recognized when {@link #IEXTEN} is set, and then
	 * not passed as input. Array index of {@link Termios#c_cc}.
	 */
	@POSIX_XSI_Conformant
	@Nullable
	final public Integer VLNEXT = VLNEXT();

	/**
	 * <b>Non-Canonical Mode</b>, MIN value. Array index of {@link Termios#c_cc}.
	 */
	@POSIX
	final public int VMIN = VMIN();

	/**
	 * <b>Canonical Mode</b>, <b>Non-Canonical Mode</b>, QUIT character. Array index
	 * of {@link Termios#c_cc}.
	 */
	@POSIX
	final public int VQUIT = VQUIT();

	/**
	 * Reprint unread characters (REPRINT). Recognized when ICANON and IEXTEN are
	 * set, and then not passed as input. Array index of {@link Termios#c_cc}.
	 */
	@POSIX_XSI_Conformant
	@Nullable
	final public Integer VREPRINT = VREPRINT();

	/**
	 * <b>Canonical Mode</b>, <b>Non-Canonical Mode</b>, START character. Array
	 * index of {@link Termios#c_cc}.
	 */
	@POSIX
	final public int VSTART = VSTART();

	/**
	 * Status request: 024, DC4, Ctrl-T). Status character (STATUS). Display status
	 * information at terminal, including state of foreground process and amount of
	 * CPU time it has consumed. Also sends a SIGINFO signal (not supported on
	 * Linux) to the foreground process group. Array index of {@link Termios#c_cc}.
	 */
	@POSIX_XSI_Conformant
	@Nullable
	final public Integer VSTATUS = VSTATUS();

	/**
	 * <b>Canonical Mode</b>, <b>Non-Canonical Mode</b>, STOP character. Array index
	 * of {@link Termios#c_cc}.
	 */
	@POSIX
	final public int VSTOP = VSTOP();

	/**
	 * <b>Canonical Mode</b>, <b>Non-Canonical Mode</b>, SUSP character. Array index
	 * of {@link Termios#c_cc}.
	 */
	@POSIX
	final public int VSUSP = VSUSP();

	/**
	 * Switch character (SWTCH). Used in System V to switch shells in shell layers,
	 * a predecessor to shell job control. Array index of {@link Termios#c_cc}.
	 */
	@Nullable
	final public Integer VSWTC = VSWTC();

	/**
	 * Switch character (SWTCH). Used in System V to switch shells in shell layers,
	 * a predecessor to shell job control. Array index of {@link Termios#c_cc}.
	 */
	@Nullable
	final public Integer VSWTCH = VSWTCH();

	/**
	 * Vertical-tab delay type 0, used in {@link Termios#c_oflag}.
	 */
	@POSIX(XSI)
	final public int VT0 = VT0();

	/**
	 * Vertical-tab delay type 1, used in {@link Termios#c_oflag}.
	 */
	@POSIX(XSI)
	final public int VT1 = VT1();

	/**
	 * Select vertical-tab delays, used in {@link Termios#c_oflag}. Values:
	 * <li>{@link #VT0}</li>
	 * <li>{@link #VT1}</li>
	 */
	@POSIX(XSI)
	final public int VTDLY = VTDLY();

	/**
	 * <b>Non-Canonical Mode</b>, TIME value. Array index of {@link Termios#c_cc}.
	 */
	@POSIX
	final public int VTIME = VTIME();

	/**
	 * Word erase (WERASE). Recognized when {@link #ICANON()} and {@link #IEXTEN}
	 * are set, and then not passed as input. Array index of {@link Termios#c_cc}.
	 */
	@POSIX_XSI_Conformant
	@Nullable
	final public Integer VWERASE = VWERASE();

	/**
	 * If {@link #ICANON} is also set, terminal is uppercase only. Input is
	 * converted to lower‐ case, except for characters preceded by \. On output,
	 * upper‐ case characters are preceded by \ and lowercase characters are
	 * converted to uppercase. Used in {@link Termios#c_lflag}.
	 */
	@POSIX_Removed(ISSUE_6)
	@Nullable
	final public Integer XCASE = XCASE();

	// TODO document me!
	@Nullable
	final public Integer XTABS = XTABS();

	// TODO document me!
	@Nullable
	protected abstract Integer __MAX_BAUD();

	protected abstract Integer VSTATUS();

	protected abstract Integer VDSUSP();

	protected abstract Integer SWITCH();

	protected abstract Integer LOBLK();

	protected abstract Integer DEFECHO();

	protected abstract Defined _HAVE_STRUCT_TERMIOS_C_ISPEED();

	protected abstract Defined _HAVE_STRUCT_TERMIOS_C_OSPEED();

	protected abstract int B0();

	protected abstract int B1000000();

	protected abstract int B110();

	protected abstract int B115200();

	protected abstract int B1152000();

	protected abstract int B1200();

	protected abstract int B134();

	protected abstract int B150();

	protected abstract int B1500000();

	protected abstract int B1800();

	protected abstract int B19200();

	protected abstract int B200();

	protected abstract int B2000000();

	protected abstract int B230400();

	protected abstract int B2400();

	protected abstract int B2500000();

	protected abstract int B300();

	protected abstract int B3000000();

	protected abstract int B3500000();

	protected abstract int B38400();

	protected abstract int B4000000();

	protected abstract int B460800();

	protected abstract int B4800();

	protected abstract int B50();

	protected abstract int B500000();

	protected abstract int B57600();

	protected abstract int B576000();

	protected abstract int B600();

	protected abstract int B75();

	protected abstract int B921600();

	protected abstract int B9600();

	protected abstract int BRKINT();

	protected abstract int BS0();

	protected abstract int BS1();

	protected abstract int BSDLY();

	protected abstract Integer CBAUD();

	protected abstract Integer CBAUDEX();

	/**
	 * Returns the input speed stored in the termios structure.
	 * 
	 * @param termios
	 *            the Termios structure.
	 * @return the speed encoded in the B* constants.
	 */
	@POSIX
	public abstract int cfgetispeed(T termios);

	/**
	 * Returns the output speed stored in the termios structure.
	 * 
	 * @param termios
	 *            the Termios structure.
	 * @return the speed encoded in the B* constants.
	 */
	@POSIX
	public abstract int cfgetospeed(T termios);

	/**
	 * sets the input speed stored in the termios structure pointed to by termios_p
	 * to speed, which must be one of the {@link #B0()}... constants.
	 * 
	 * <h2>ERRORS</h2>
	 * <p>
	 * The cfsetispeed() function may fail if:
	 * </p>
	 * <dl>
	 * <dt>EINVAL</dt>
	 * <dd>The speed value is not a valid speed.</dd>
	 * <dt>EINVAL</dt>
	 * <dd>The value of speed is outside the range of possible speed values as
	 * specified in <termios.h>.</dd>
	 * <dl>
	 * 
	 * @param termios
	 *            the Termios structure.
	 * @param speed
	 *            the speed encoded in the {@link #B0}... constants.
	 * @return 0 on success -1 otherwise.
	 */
	@POSIX
	public abstract int cfsetispeed(T termios, int speed);

	/**
	 * sets the output speed stored in the termios structure pointed to by termios_p
	 * to speed, which must be one of the {@link #B0()}... constants.
	 * 
	 * <h2>ERRORS</h2>
	 * <p>
	 * The cfsetospeed() function may fail if:
	 * </p>
	 * <dl>
	 * <dt>EINVAL</dt>
	 * <dd>The speed value is not a valid speed.</dd>
	 * <dt>EINVAL</dt>
	 * <dd>The value of speed is outside the range of possible speed values as
	 * specified in <termios.h>.
	 * <dd>
	 * </dl>
	 * 
	 * @param termios
	 *            the Termios structure.
	 * @param speed
	 *            the speed encoded in the {@link #B0}... constants.
	 * @return 0 on success -1 otherwise.
	 */
	@POSIX
	public abstract int cfsetospeed(T termios, int speed);

	protected abstract Integer CIBAUD();

	protected abstract int CLOCAL();

	protected abstract Integer CMSPAR();

	protected abstract int CR0();

	protected abstract int CR1();

	protected abstract int CR2();

	protected abstract int CR3();

	protected abstract int CRDLY();

	protected abstract int CREAD();

	/**
	 * Returns a new struct termios for this os, arch and abi.
	 * 
	 * @return
	 */
	public abstract T createTermios();

	protected abstract int CRTSCTS();

	protected abstract int CS5();

	protected abstract int CS6();

	protected abstract int CS7();

	protected abstract int CS8();

	protected abstract int CSIZE();

	protected abstract int CSTOPB();

	protected abstract int ECHO();

	protected abstract Integer ECHOCTL();

	protected abstract int ECHOE();

	protected abstract Integer ECHOK();

	protected abstract int ECHOKE();

	protected abstract int ECHONL();

	protected abstract Integer ECHOPRT();

	protected abstract Integer EXTA();

	protected abstract Integer EXTB();

	protected abstract Integer EXTPROC();

	protected abstract int FF0();

	protected abstract int FF1();

	protected abstract int FFDLY();

	protected abstract Integer FLUSHO();

	protected abstract int HUPCL();

	protected abstract int ICANON();

	protected abstract int ICRNL();

	protected abstract int IEXTEN();

	protected abstract int IGNBRK();

	protected abstract int IGNCR();

	protected abstract int IGNPAR();

	protected abstract Integer IMAXBEL();

	protected abstract int INLCR();

	protected abstract int INPCK();

	protected abstract int ISIG();

	protected abstract int ISTRIP();

	protected abstract Integer IUCLC();

	protected abstract Integer IUTF8();

	protected abstract int IXANY();

	protected abstract int IXOFF();

	protected abstract int IXON();

	protected abstract int NCCS();

	protected abstract int NL0();

	protected abstract int NL1();

	protected abstract int NLDLY();

	protected abstract int NOFLSH();

	protected abstract int OCRNL();

	protected abstract int OFDEL();

	protected abstract int OFILL();

	protected abstract Integer OLCUC();

	protected abstract int ONLCR();

	protected abstract int ONLRET();

	protected abstract int ONOCR();

	protected abstract int OPOST();

	protected abstract int PARENB();

	protected abstract Integer PAREXT();

	protected abstract int PARMRK();

	protected abstract int PARODD();

	protected abstract Integer PENDIN();

	protected abstract int TAB0();

	protected abstract int TAB1();

	protected abstract int TAB2();

	protected abstract int TAB3();

	protected abstract int TABDLY();

	/**
	 * This function shall block until all output written to the object referred to
	 * by fildes is transmitted.
	 * 
	 * <h2>ERRORS:</h2>
	 * <p>
	 * The tcdrain() function shall fail if:
	 * </p>
	 * <dl>
	 * <dt>EBADF</dt>
	 * <dd>The fildes argument is not a valid file descriptor.</dd>
	 * <dt>EINTR</dt>
	 * <dd>A signal interrupted tcdrain().</dd>
	 * <dt>EIO</dt>
	 * <dd>The process group of the writing process is orphaned, the calling thread
	 * is not blocking SIGTTOU, and the process is not ignoring SIGTTOU.</dd>
	 * <dt>ENOTTY</dt>
	 * <dd>The file associated with fildes is not a terminal.</dd>
	 * </dl>
	 * 
	 * @param fildes
	 *            The file descriptor.
	 * @return 0 on success -1 otherwise.
	 */
	@POSIX
	public abstract int tcdrain(int fildes);

	/**
	 * Suspends transmission or reception of data on the object referred to by
	 * fildes, depending on the value of action:
	 * 
	 * <dl>
	 * <dt>TCOOFF</dt>
	 * <dd>suspends output.</dd>
	 * <dt>TCOON</dt>
	 * <dd>restarts suspended output.</dd>
	 * <dt>TCIOFF</dt>
	 * <dd>transmits a STOP character, which stops the terminal device from
	 * transmitting data to the system.</dd>
	 * <dt>TCION</dt>
	 * <dd>transmits a START character, which starts the terminal device
	 * transmitting data to the system.</dd>
	 * </dl>
	 * <h2>ERRORS</h2>
	 * <p>
	 * The tcflow() function shall fail if:
	 * </p>
	 * <dl>
	 * <dt>EBADF</dt>
	 * <dd>The fildes argument is not a valid file descriptor.</dd>
	 * <dt>EINVAL</dt>
	 * <dd>The action argument is not a supported value.</dd>
	 * <dt>EIO</dt>
	 * <dd>The process group of the writing process is orphaned, the calling thread
	 * is not blocking SIGTTOU, and the process is not ignoring SIGTTOU.</dd>
	 * <dt>ENOTTY</dt>
	 * <dd>The file associated with fildes is not a terminal.</dd>
	 * </dl>
	 * 
	 * 
	 * @param fildes
	 *            The file descriptor.
	 * @param action
	 *            The action to execute.
	 * @return 0 on success -1 otherwise.
	 */
	@POSIX
	public abstract int tcflow(int fildes, int action);

	/**
	 * 
	 * Discards data written to the object referred to by filedes but not
	 * transmitted, or data received but not read, depending on the value of
	 * queue_selector:
	 * 
	 * <dl>
	 * <dt>TCIFLUSH</dt>
	 * <dd>flushes data received but not read.</dd>
	 * <dt>TCOFLUSH</dt>
	 * <dd>flushes data written but not transmitted.</dd>
	 * <dt>TCIOFLUSH</dt>
	 * <dd>flushes both data received but not read, and data written but not
	 * transmitted.</dd>
	 * </dl>
	 * <h2>ERRORS</h2>
	 * <p>
	 * The tcflush() function shall fail if:
	 * </p>
	 * <dl>
	 * <dt>EBADF</dt>
	 * <dd>The fildes argument is not a valid file descriptor.</dd>
	 * <dt>EINVAL</dt>
	 * <dd>The queue_selector argument is not a supported value.</dd>
	 * <dt>EIO</dt>
	 * <dd>The process group of the writing process is orphaned, the calling thread
	 * is not blocking SIGTTOU, and the process is not ignoring SIGTTOU.
	 * <dd>
	 * <dt>ENOTTY</dt>
	 * <dd>The file associated with fildes is not a terminal.</dd>
	 * </dl>
	 * 
	 * @param fildes
	 *            The file descriptor.
	 * @param queue_selector
	 *            The queue selector.
	 * @return 0 on success -1 otherwise.
	 */
	@POSIX
	public abstract int tcflush(int fildes, int queue_selector);

	/**
	 * gets the parameters associated with the object referred by fd and stores them
	 * in the termios structure referenced by termios_p. This function may be
	 * invoked from a background process; however, the terminal attributes may be
	 * subsequently changed by a foreground process.
	 * <h2>ERRORS</h2>
	 * <p>
	 * The tcgetattr() function shall fail if:
	 * </p>
	 * <dl>
	 * <dt>EBADF</dt>
	 * <dd>The fildes argument is not a valid file descriptor.</dd>
	 * <dt>ENOTTY</dt>
	 * <dd>The file associated with fildes is not a terminal.</dd>
	 * </dl>
	 * 
	 * 
	 * @param fildes
	 *            The file descriptor.
	 * @param termios
	 *            The Termios struct.
	 * @return 0 on success -1 otherwise.
	 */
	@POSIX
	public abstract int tcgetattr(int fildes, T termios);

	/**
	 * The function tcgetsid() returns the session ID of the current session that
	 * has the terminal associated to fd as controlling terminal. This terminal must
	 * be the controlling terminal of the calling process.
	 * <h2>ERRORS</h2>
	 * <p>
	 * The tcgetsid() function shall fail if:
	 * </p>
	 * <dl>
	 * <dt>EBADF</dt>
	 * <dd>The fildes argument is not a valid file descriptor.</dd>
	 * <dt>ENOTTY</dt>
	 * <dd>The calling process does not have a controlling terminal, or the file is
	 * not the controlling terminal.</dd>
	 * </dl>
	 * 
	 * 
	 * @param fildes
	 *            The file descriptor.
	 * @return 0 on success -1 otherwise.
	 */
	@POSIX
	public abstract int tcgetsid(int fildes);

	protected abstract int TCIFLUSH();

	protected abstract int TCIOFF();

	protected abstract int TCIOFLUSH();

	protected abstract int TCION();

	protected abstract int TCOFLUSH();

	protected abstract int TCOOFF();

	protected abstract int TCOON();

	protected abstract int TCSADRAIN();

	protected abstract int TCSAFLUSH();

	protected abstract int TCSANOW();

	/**
	 * Transmits a continuous stream of zero-valued bits for a specific duration, if
	 * the terminal is using asynchronous serial data transmission. If duration is
	 * zero, it transmits zero-val‐ ued bits for at least 0.25 seconds, and not more
	 * that 0.5 seconds. If duration is not zero, it sends zero-valued bits for some
	 * implementation-defined length of time.
	 * <h2>ERRORS</h2>
	 * <p>
	 * 
	 * The tcsendbreak() function shall fail if:
	 * </p>
	 * <dl>
	 * <dt>EBADF</dt>
	 * <dd>The fildes argument is not a valid file descriptor.</dd>
	 * <dt>EIO</dt>
	 * <dd>The process group of the writing process is orphaned, the calling thread
	 * is not blocking SIGTTOU, and the process is not ignoring SIGTTOU.</dd>
	 * <dt>ENOTTY</dt>
	 * <dd>The file associated with fildes is not a terminal.</dd>
	 * </dl>
	 * 
	 * 
	 * @param fildes
	 *            The file descriptor.
	 * @param duration
	 *            The duration
	 * @return 0 on success -1 otherwise.
	 */
	@POSIX
	public abstract int tcsendbreak(int fildes, int duration);

	/**
	 * Set the parameters associated with the terminal (unless support is required
	 * from the underlying hardware that is not available) from the termios
	 * structure. optional_actions specifies when the changes take effect:
	 * <dl>
	 * <dt>TCSANOW</dt>
	 * <dd>the change occurs immediately.</dd>
	 * <dt>TCSADRAIN/dt>
	 * <dd>the change occurs after all output written to fd has been transmitted.
	 * This option should be used when changing parameters that affect output.</dd>
	 * <dt>TCSAFLUSH</dt>
	 * <dd>the change occurs after all output written to the object referred by fd
	 * has been transmitted, and all input that has been received but not read will
	 * be discarded before the change is made.</dd>
	 * </dl>
	 * <p>
	 * If the output baud rate stored in the termios structure pointed to by
	 * termios_p is the zero baud rate, B0, the modem control lines shall no longer
	 * be asserted. Normally, this shall disconnect the line.
	 * </p>
	 * <p>
	 * If the input speed stored in the termios structure is 0, the input speed given to the hardware is the same as the
	 * output speed stored in the termios structure.
	 * </p>
	 * <p>
	 * The tcsetattr() function shall return successfully if it was able to perform
	 * any of the requested actions, even if some of the requested actions could not
	 * be performed. It shall set all the attributes that the implementation
	 * supports as requested and leave all the attributes not supported by the
	 * implementation unchanged. If no part of the request can be honored, it shall
	 * return -1 and set errno to [EINVAL]. If the input and output speed values
	 * differ and are a combination that is not supported, neither speed shall
	 * be changed. A subsequent call to tcgetattr() shall return the actual state of
	 * the terminal device (reflecting both the changes made and not made in the
	 * previous tcsetattr() call). The tcsetattr() function shall not change the
	 * values found in the termios structure under any circumstances.
	 * </p>
	 * <p>
	 * The effect of tcsetattr() is undefined if the value of the termios structure
	 * pointed to by termios_p was not derived from the result of a call to
	 * tcgetattr() on fildes; an application should modify only fields and flags
	 * defined by this volume of POSIX.1-2017 between the call to tcgetattr() and
	 * tcsetattr(), leaving all other fields and flags unmodified.
	 * </p>
	 * *
	 * <h2>ERRORS</h2>
	 * <p>
	 * The tcsetattr() function shall fail if:
	 * </p>
	 * <dl>
	 * <dt>EBADF</dt>
	 * <dd>The fildes argument is not a valid file descriptor.</dd>
	 * <dt>EINTR</dt>
	 * <dd>A signal interrupted tcsetattr().</dd>
	 * <dt>EINVAL</dt>
	 * <dd>The optional_actions argument is not a supported value, or an attempt was
	 * made to change an attribute represented in the termios structure to an
	 * unsupported value.</dd>
	 * <dt>EIO</dt>
	 * <dd>The process group of the writing process is orphaned, the calling thread
	 * is not blocking SIGTTOU, and the process is not ignoring SIGTTOU.</dd>
	 * <dt>ENOTTY</dt>
	 * <dd>The file associated with fildes is not a terminal.</dd>
	 * </dl>
	 * 
	 * @param fildes
	 *            The file descriptor.
	 * @param optional_actions
	 *            Specifies when changes take effect.
	 * @param termios
	 *            The Termios struct.
	 * @return 0 on success -1 otherwise.
	 */
	@POSIX
	public abstract int tcsetattr(int fildes, int optional_actions, T termios);

	protected abstract int TOSTOP();

	protected abstract Integer VDISCARD();

	protected abstract int VEOF();

	protected abstract int VEOL();

	protected abstract Integer VEOL2();

	protected abstract int VERASE();

	protected abstract int VINTR();

	protected abstract int VKILL();

	protected abstract Integer VLNEXT();

	protected abstract int VMIN();

	protected abstract int VQUIT();

	protected abstract Integer VREPRINT();

	protected abstract int VSTART();

	protected abstract int VSTOP();

	protected abstract int VSUSP();

	protected abstract Integer VSWTC();

	protected abstract Integer VSWTCH();

	protected abstract int VT0();

	protected abstract int VT1();

	protected abstract int VTDLY();

	protected abstract int VTIME();

	protected abstract Integer VWERASE();

	protected abstract Integer XCASE();

	protected abstract Integer XTABS();

}