package de.ibapl.jnrheader.spi.posix.linux;

import static de.ibapl.jnrheader.Defined.isDefined;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jnr.ffi.types.int32_t;
import de.ibapl.jnrheader.Defined;
import de.ibapl.jnrheader.NativeDataType;
import de.ibapl.jnrheader.api.posix.Termios_H;
import de.ibapl.jnrheader.api.posix.Termios_H.cfmakeraw;
import de.ibapl.jnrheader.api.posix.Termios_H.cfsetspeed;
import jnr.ffi.LibraryLoader;
import jnr.ffi.Runtime;
import jnr.ffi.Struct;
import jnr.ffi.TypeAlias;
import jnr.ffi.annotations.TypeDefinition;
import org.eclipse.jdt.annotation.Nullable;

public abstract class Termios_Generic extends Termios_H implements cfsetspeed, cfmakeraw {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = { ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD })
	@TypeDefinition(alias = TypeAlias.int8_t)
	@NativeDataType("unsigned char")
	public @interface cc_t {

	}

	public class LinuxTermios extends Termios_H.Termios {
		/**
		 *  Line discipline.
		 */
		public byte c_line;
		
		/**
		 * Input speed.
		 */
		public int c_ispeed;

		/**
		 * Output speed.
		 */
		public int c_ospeed;
		
		protected void c_line2String(StringBuilder sb, Byte c_line) {
			if (isDefined(c_line)) {
				sb.append(String.format("0x%02x", c_line));
			} else {
				sb.append("null");
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
			c_line2String(sb, c_line);

			c_cc2String(sb, c_cc);

			final java.lang.String formatString = "\n\t%s = 0x%08x";
			if (isDefined(_HAVE_STRUCT_TERMIOS_C_ISPEED)) {
				sb.append(java.lang.String.format(formatString, "c_ispeed", c_ispeed));
			} else {
				sb.append("    c_ispeed not defined\n");
			}
			if (isDefined(_HAVE_STRUCT_TERMIOS_C_OSPEED)) {
				sb.append(java.lang.String.format(formatString, "c_ospeed", c_ospeed));
			} else {
				sb.append("    c_ospeed not defined\n");
			}

			sb.append("\n}");
			return sb.toString();
		}

	}
	
	@de.ibapl.jnrheader.NativeFunctions
	protected interface NativeFunctions {

		@speed_t
		int cfgetispeed(TermiosImpl termios);

		@speed_t
		int cfgetospeed(TermiosImpl termios);

		void cfmakeraw(TermiosImpl termios);

		@speed_t
		int cfsetispeed(TermiosImpl termios, @speed_t int speed);

		@int32_t
		int cfsetospeed(TermiosImpl termios, @speed_t int speed);

		// TODO POSIX ??? set both in and out
		@int32_t
		int cfsetspeed(TermiosImpl termios, @speed_t int speed);

		@int32_t
		int tcdrain(@int32_t int fildes);

		@int32_t
		int tcflow(@int32_t int fildes, @int32_t int action);

		@int32_t
		int tcflush(@int32_t int fildes, @int32_t int queue_selector);

		@int32_t
		int tcgetattr(@int32_t int fildes, TermiosImpl termios);

		@pid_t
		int tcgetsid(@int32_t int fildes);

		@int32_t
		int tcsendbreak(@int32_t int fildes, @int32_t int duration);

		@int32_t
		int tcsetattr(@int32_t int fildes, @int32_t int optional_actions, TermiosImpl termios);
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = { ElementType.PARAMETER, ElementType.METHOD })
	@TypeDefinition(alias = TypeAlias.int32_t)
	@NativeDataType("int")
	public @interface pid_t {

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = { ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD })
	@TypeDefinition(alias = TypeAlias.int32_t)
	@NativeDataType("unsigned int")
	public @interface speed_t {

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = { ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD })
	@TypeDefinition(alias = TypeAlias.int32_t)
	@NativeDataType("unsigned int")
	public @interface tcflag_t {

	}

	private class TermiosImpl extends Struct {
		@Termios_Generic.tcflag_t
		public final int32_t c_iflag = new int32_t(); /* input mode flags */
		@Termios_Generic.tcflag_t
		public final int32_t c_oflag = new int32_t(); /* output mode flags */
		@Termios_Generic.tcflag_t
		public final int32_t c_cflag = new int32_t(); /* control mode flags */
		@Termios_Generic.tcflag_t
		public final int32_t c_lflag = new int32_t(); /* local mode flags */
		@Termios_Generic.cc_t
		public final int8_t c_line = new int8_t(); /* line discipline */
		@Termios_Generic.cc_t
		public final int8_t[] c_cc = createCc_t(); /* control characters */

		// This is termios2 ??? - but linux except mips does define this.
		/**
		 * Use this only if {@link TermiosFlags#._HAVE_STRUCT_TERMIOS_C_ISPEED} is
		 * defined.
		 * 
		 */
		@Termios_Generic.speed_t
		public final int32_t c_ispeed = Defined.isDefined(_HAVE_STRUCT_TERMIOS_C_ISPEED) ? new int32_t()
				: null; /* input speed */
		/**
		 * Use this only if {@link TermiosFlags#._HAVE_STRUCT_TERMIOS_C_OSPEED} is
		 * defined.
		 * 
		 */
		@Termios_Generic.speed_t
		public final int32_t c_ospeed = Defined.isDefined(_HAVE_STRUCT_TERMIOS_C_OSPEED) ? new int32_t()
				: null; /* output speed */

		private TermiosImpl(Runtime runtime) {
			super(runtime);
		}

		private int8_t[] createCc_t() {
			final int8_t[] result = new int8_t[NCCS];
			for (int i = 0; i < NCCS; i++) {
				result[i] = new int8_t();
			}
			return result;
		}

	}
	public static final int NCCS = 32;
	public static final int VINTR = 0;
	public static final int VQUIT = 1;
	public static final int VERASE = 2;
	public static final int VKILL = 3;
	public static final int VEOF = 4;
	public static final int VTIME = 5;
	public static final int VMIN = 6;
	public static final int VSWTC = 7;
	public static final int VSTART = 8;
	public static final int VSTOP = 9;
	public static final int VSUSP = 10;
	public static final int VEOL = 11;
	public static final int VREPRINT = 12;
	public static final int VDISCARD = 13;
	public static final int VWERASE = 14;
	public static final int VLNEXT = 15;
	public static final int VEOL2 = 16;
	public static final int IGNBRK = 0000001;
	public static final int BRKINT = 0000002;
	public static final int IGNPAR = 0000004;
	public static final int PARMRK = 0000010;
	public static final int INPCK = 0000020;
	public static final int ISTRIP = 0000040;
	public static final int INLCR = 0000100;
	public static final int IGNCR = 0000200;
	public static final int ICRNL = 0000400;
	public static final int IUCLC = 0001000;
	public static final int IXON = 0002000;
	public static final int IXANY = 0004000;
	public static final int IXOFF = 0010000;
	public static final int IMAXBEL = 0020000;
	public static final int IUTF8 = 0040000;
	public static final int OPOST = 0000001;
	public static final int OLCUC = 0000002;
	public static final int ONLCR = 0000004;
	public static final int OCRNL = 0000010;
	public static final int ONOCR = 0000020;
	public static final int ONLRET = 0000040;
	public static final int OFILL = 0000100;
	public static final int OFDEL = 0000200;
	public static final int NLDLY = 0000400;
	public static final int NL0 = 0000000;
	public static final int NL1 = 0000400;
	public static final int CRDLY = 0003000;
	public static final int CR0 = 0000000;
	public static final int CR1 = 0001000;
	public static final int CR2 = 0002000;
	public static final int CR3 = 0003000;
	public static final int TABDLY = 0014000;
	public static final int TAB0 = 0000000;
	public static final int TAB1 = 0004000;
	public static final int TAB2 = 0010000;
	public static final int TAB3 = 0014000;
	public static final int BSDLY = 0020000;
	public static final int BS0 = 0000000;
	public static final int BS1 = 0020000;
	public static final int FFDLY = 0100000;
	public static final int FF0 = 0000000;
	public static final int FF1 = 0100000;
	public static final int VTDLY = 0040000;
	public static final int VT0 = 0000000;
	public static final int VT1 = 0040000;
	public static final int XTABS = 0014000;
	public static final int CBAUD = 0010017;
	public static final int B0 = 0000000;
	public static final int B50 = 0000001;
	public static final int B75 = 0000002;
	public static final int B110 = 0000003;
	public static final int B134 = 0000004;
	public static final int B150 = 0000005;
	public static final int B200 = 0000006;
	public static final int B300 = 0000007;
	public static final int B600 = 0000010;
	public static final int B1200 = 0000011;
	public static final int B1800 = 0000012;
	public static final int B2400 = 0000013;
	public static final int B4800 = 0000014;
	public static final int B9600 = 0000015;
	public static final int B19200 = 0000016;
	public static final int B38400 = 0000017;
	public static final int EXTA = B19200;
	public static final int EXTB = B38400;
	public static final int CSIZE = 0000060;
	public static final int CS5 = 0000000;
	public static final int CS6 = 0000020;
	public static final int CS7 = 0000040;
	public static final int CS8 = 0000060;
	public static final int CSTOPB = 0000100;
	public static final int CREAD = 0000200;
	public static final int PARENB = 0000400;
	public static final int PARODD = 0001000;
	public static final int HUPCL = 0002000;
	public static final int CLOCAL = 0004000;
	public static final int CBAUDEX = 0010000;
	public static final int B57600 = 0010001;
	public static final int B115200 = 0010002;
	public static final int B230400 = 0010003;
	public static final int B460800 = 0010004;
	public static final int B500000 = 0010005;
	public static final int B576000 = 0010006;
	public static final int B921600 = 0010007;
	public static final int B1000000 = 0010010;
	public static final int B1152000 = 0010011;
	public static final int B1500000 = 0010012;
	public static final int B2000000 = 0010013;
	public static final int B2500000 = 0010014;
	public static final int B3000000 = 0010015;
	public static final int B3500000 = 0010016;
	public static final int B4000000 = 0010017;
	public static final int __MAX_BAUD = B4000000;
	public static final int CIBAUD = 002003600000;
	public static final int CMSPAR = 010000000000;
	public static final int CRTSCTS = 020000000000;
	public static final int ISIG = 0000001;
	public static final int ICANON = 0000002;
	public static final int XCASE = 0000004;
	public static final int ECHO = 0000010;
	public static final int ECHOE = 0000020;
	public static final int ECHOK = 0000040;
	public static final int ECHONL = 0000100;
	public static final int NOFLSH = 0000200;
	public static final int TOSTOP = 0000400;
	public static final int ECHOCTL = 0001000;
	public static final int ECHOPRT = 0002000;
	public static final int ECHOKE = 0004000;
	public static final int FLUSHO = 0010000;
	public static final int PENDIN = 0040000;
	public static final int IEXTEN = 0100000;
	public static final int EXTPROC = 0200000;
	public static final int TCOOFF = 0;
	public static final int TCOON = 1;
	public static final int TCIOFF = 2;
	public static final int TCION = 3;
	public static final int TCIFLUSH = 0;
	public static final int TCOFLUSH = 1;
	public static final int TCIOFLUSH = 2;
	public static final int TCSANOW = 0;
	public static final int TCSADRAIN = 1;
	public static final int TCSAFLUSH = 2;

	final private NativeFunctions nativeFunctions;

	public Termios_Generic() {
		nativeFunctions = LibraryLoader.create(NativeFunctions.class).load("c");
	}

	@Override
	protected Integer __MAX_BAUD() {
		return Termios_Generic.__MAX_BAUD;
	}

	@Override
	protected int B0() {
		return Termios_Generic.B0;
	}

	@Override
	protected int B1000000() {
		return Termios_Generic.B1000000;
	}

	@Override
	protected int B110() {
		return Termios_Generic.B110;
	}

	@Override
	protected int B115200() {
		return Termios_Generic.B115200;
	}

	@Override
	protected int B1152000() {
		return Termios_Generic.B1152000;
	}

	@Override
	protected int B1200() {
		return Termios_Generic.B1200;
	}

	@Override
	protected int B134() {
		return Termios_Generic.B134;
	}

	@Override
	protected int B150() {
		return Termios_Generic.B150;
	}

	@Override
	protected int B1500000() {
		return Termios_Generic.B1500000;
	}

	@Override
	protected int B1800() {
		return Termios_Generic.B1800;
	}

	@Override
	protected int B19200() {
		return Termios_Generic.B19200;
	}

	@Override
	protected int B200() {
		return Termios_Generic.B200;
	}

	@Override
	protected int B2000000() {
		return Termios_Generic.B2000000;
	}

	@Override
	protected int B230400() {
		return Termios_Generic.B230400;
	}

	@Override
	protected int B2400() {
		return Termios_Generic.B2400;
	}

	@Override
	protected int B2500000() {
		return Termios_Generic.B2500000;
	}

	@Override
	protected int B300() {
		return Termios_Generic.B300;
	}

	@Override
	protected int B3000000() {
		return Termios_Generic.B3000000;
	}

	@Override
	protected int B3500000() {
		return Termios_Generic.B3500000;
	}

	@Override
	protected int B38400() {
		return Termios_Generic.B38400;
	}

	@Override
	protected int B4000000() {
		return Termios_Generic.B4000000;
	}

	@Override
	protected int B460800() {
		return Termios_Generic.B460800;
	}

	@Override
	protected int B4800() {
		return Termios_Generic.B4800;
	}

	@Override
	protected int B50() {
		return Termios_Generic.B50;
	}

	@Override
	protected int B500000() {
		return Termios_Generic.B500000;
	}

	@Override
	protected int B57600() {
		return Termios_Generic.B57600;
	}

	@Override
	protected int B576000() {
		return Termios_Generic.B576000;
	}

	@Override
	protected int B600() {
		return Termios_Generic.B600;
	}

	@Override
	protected int B75() {
		return Termios_Generic.B75;
	}

	@Override
	protected int B921600() {
		return Termios_Generic.B921600;
	}

	@Override
	protected int B9600() {
		return Termios_Generic.B9600;
	}

	@Override
	protected int BRKINT() {
		return Termios_Generic.BRKINT;
	}

	@Override
	protected int BS0() {
		return Termios_Generic.BS0;
	}

	@Override
	protected int BS1() {
		return Termios_Generic.BS1;
	}

	@Override
	protected int BSDLY() {
		return Termios_Generic.BSDLY;
	}

	@Override
	protected Integer CBAUD() {
		return Termios_Generic.CBAUD;
	}

	@Override
	protected Integer CBAUDEX() {
		return Termios_Generic.CBAUDEX;
	}

	@Override
	public int cfgetispeed(Termios termios) {
		TermiosImpl termiosImpl = wrap(termios);
		int result = nativeFunctions.cfgetispeed(termiosImpl);
//TODO need to unwrap???		unwrap(termios, termiosImpl);
		unwrap(termios, termiosImpl);
		return result;
	}

	@Override
	public int cfgetospeed(Termios termios) {
		TermiosImpl termiosImpl = wrap(termios);
		int result = nativeFunctions.cfgetospeed(termiosImpl);
//TODO need to unwrap???		unwrap(termios, termiosImpl);
		return result;
	}

	@Override
	public void cfmakeraw(Termios termios) {
		TermiosImpl termiosImpl = wrap(termios);
		nativeFunctions.cfmakeraw(termiosImpl);
		unwrap(termios, termiosImpl);
	}

	@Override
	public int cfsetispeed(Termios termios, int speed) {
		TermiosImpl termiosImpl = wrap(termios);
		int result = nativeFunctions.cfsetispeed(termiosImpl, speed);
		unwrap(termios, termiosImpl);
		return result;
	}

	@Override
	public int cfsetospeed(Termios termios, int speed) {
		TermiosImpl termiosImpl = wrap(termios);
		int result = nativeFunctions.cfsetospeed(termiosImpl, speed);
		unwrap(termios, termiosImpl);
		return result;
	}

	@Override
	public int cfsetspeed(Termios termios, int speed) {
		TermiosImpl termiosImpl = wrap(termios);
		int result = nativeFunctions.cfsetspeed(termiosImpl, speed);
		unwrap(termios, termiosImpl);
		return result;
	}

	@Override
	protected Integer CIBAUD() {
		return Termios_Generic.CIBAUD;
	}

	@Override
	protected int CLOCAL() {
		return Termios_Generic.CLOCAL;
	}

	@Override
	protected Integer CMSPAR() {
		return Termios_Generic.CMSPAR;
	}

	@Override
	protected int CR0() {
		return Termios_Generic.CR0;
	}

	@Override
	protected int CR1() {
		return Termios_Generic.CR1;
	}

	@Override
	protected int CR2() {
		return Termios_Generic.CR2;
	}

	@Override
	protected int CR3() {
		return Termios_Generic.CR3;
	}

	@Override
	protected int CRDLY() {
		return Termios_Generic.CRDLY;
	}

	@Override
	protected int CREAD() {
		return Termios_Generic.CREAD;
	}

	@Override
	public LinuxTermios createTermios() {
		LinuxTermios result = new LinuxTermios();
		result.c_cc = new byte[NCCS];
		return result;
	}

	@Override
	protected int CRTSCTS() {
		return Termios_Generic.CRTSCTS;
	}

	@Override
	protected int CS5() {
		return Termios_Generic.CS5;
	}

	@Override
	protected int CS6() {
		return Termios_Generic.CS6;
	}

	@Override
	protected int CS7() {
		return Termios_Generic.CS7;
	}

	@Override
	protected int CS8() {
		return Termios_Generic.CS8;
	}

	@Override
	protected int CSIZE() {
		return Termios_Generic.CSIZE;
	}

	@Override
	protected int CSTOPB() {
		return Termios_Generic.CSTOPB;
	}

	@Override
	protected Integer DEFECHO() {
		return null;
	}

	@Override
	protected int ECHO() {
		return Termios_Generic.ECHO;
	}

	@Override
	protected Integer ECHOCTL() {
		return Termios_Generic.ECHOCTL;
	}

	@Override
	protected int ECHOE() {
		return Termios_Generic.ECHOE;
	}

	@Override
	protected Integer ECHOK() {
		return Termios_Generic.ECHOK;
	}

	@Override
	protected int ECHOKE() {
		return Termios_Generic.ECHOKE;
	}

	@Override
	protected int ECHONL() {
		return Termios_Generic.ECHONL;
	}

	@Override
	protected Integer ECHOPRT() {
		return Termios_Generic.ECHOPRT;
	}

	@Override
	protected Integer EXTA() {
		return Termios_Generic.EXTA;
	}

	@Override
	protected Integer EXTB() {
		return Termios_Generic.EXTB;
	}

	@Override
	protected Integer EXTPROC() {
		return Termios_Generic.EXTPROC;
	}

	@Override
	protected int FF0() {
		return Termios_Generic.FF0;
	}

	@Override
	protected int FF1() {
		return Termios_Generic.FF1;
	}

	@Override
	protected int FFDLY() {
		return Termios_Generic.FFDLY;
	}

	@Override
	protected Integer FLUSHO() {
		return Termios_Generic.FLUSHO;
	}

	@Override
	protected int HUPCL() {
		return Termios_Generic.HUPCL;
	}

	@Override
	protected int ICANON() {
		return Termios_Generic.ICANON;
	}

	@Override
	protected int ICRNL() {
		return Termios_Generic.ICRNL;
	}

	@Override
	protected int IEXTEN() {
		return Termios_Generic.IEXTEN;
	}

	@Override
	protected int IGNBRK() {
		return Termios_Generic.IGNBRK;
	}

	@Override
	protected int IGNCR() {
		return Termios_Generic.IGNCR;
	}

	@Override
	protected int IGNPAR() {
		return Termios_Generic.IGNPAR;
	}

	@Override
	protected Integer IMAXBEL() {
		return Termios_Generic.IMAXBEL;
	}

	@Override
	protected int INLCR() {
		return Termios_Generic.INLCR;
	}

	@Override
	protected int INPCK() {
		return Termios_Generic.INPCK;
	}

	@Override
	protected int ISIG() {
		return Termios_Generic.ISIG;
	}

	@Override
	protected int ISTRIP() {
		return Termios_Generic.ISTRIP;
	}

	@Override
	protected Integer IUCLC() {
		return Termios_Generic.IUCLC;
	}

	@Override
	protected Integer IUTF8() {
		return Termios_Generic.IUTF8;
	}

	@Override
	protected int IXANY() {
		return Termios_Generic.IXANY;
	}

	@Override
	protected int IXOFF() {
		return Termios_Generic.IXOFF;
	}

	@Override
	protected int IXON() {
		return Termios_Generic.IXON;
	}

	@Override
	protected Integer LOBLK() {
		return null;
	}

	@Override
	protected int NCCS() {
		return Termios_Generic.NCCS;
	}

	@Override
	protected int NL0() {
		return Termios_Generic.NL0;
	}

	@Override
	protected int NL1() {
		return Termios_Generic.NL1;
	}

	@Override
	protected int NLDLY() {
		return Termios_Generic.NLDLY;
	}

	@Override
	protected int NOFLSH() {
		return Termios_Generic.NOFLSH;
	}

	@Override
	protected int OCRNL() {
		return Termios_Generic.OCRNL;
	}

	@Override
	protected int OFDEL() {
		return Termios_Generic.OFDEL;
	}

	@Override
	protected int OFILL() {
		return Termios_Generic.OFILL;
	}

	@Override
	protected Integer OLCUC() {
		return Termios_Generic.OLCUC;
	}

	@Override
	protected int ONLCR() {
		return Termios_Generic.ONLCR;
	}

	@Override
	protected int ONLRET() {
		return Termios_Generic.ONLRET;
	}

	@Override
	protected int ONOCR() {
		return Termios_Generic.ONOCR;
	}

	@Override
	protected int OPOST() {
		return Termios_Generic.OPOST;
	}

	@Override
	protected int PARENB() {
		return Termios_Generic.PARENB;
	}

	@Override
	protected Integer PAREXT() {
		return null;
	}

	@Override
	protected int PARMRK() {
		return Termios_Generic.PARMRK;
	}

	@Override
	protected int PARODD() {
		return Termios_Generic.PARODD;
	}

	@Override
	protected Integer PENDIN() {
		return Termios_Generic.PENDIN;
	}

	@Override
	protected Integer SWITCH() {
		return null;
	}

	@Override
	protected int TAB0() {
		return Termios_Generic.TAB0;
	}

	@Override
	protected int TAB1() {
		return Termios_Generic.TAB1;
	}

	@Override
	protected int TAB2() {
		return Termios_Generic.TAB2;
	}

	@Override
	protected int TAB3() {
		return Termios_Generic.TAB3;
	}

	@Override
	protected int TABDLY() {
		return Termios_Generic.TABDLY;
	}

	@Override
	public int tcdrain(int fildes) {
		return nativeFunctions.tcdrain(fildes);
	}

	@Override
	public int tcflow(int fildes, int action) {
		return nativeFunctions.tcflow(fildes, action);
	}

	@Override
	public int tcflush(int fildes, int queue_selector) {
		return nativeFunctions.tcflush(fildes, queue_selector);
	}

	@Override
	public int tcgetattr(int fildes, Termios termios) {
		TermiosImpl termiosImpl = new TermiosImpl(Runtime.getRuntime(nativeFunctions));
		int result = nativeFunctions.tcgetattr(fildes, termiosImpl);
		unwrap(termios, termiosImpl);
		return result;
	}

	@Override
	public int tcgetsid(int fildes) {
		return nativeFunctions.tcgetsid(fildes);
	}

	@Override
	protected int TCIFLUSH() {
		return Termios_Generic.TCIFLUSH;
	}

	@Override
	protected int TCIOFF() {
		return Termios_Generic.TCIOFF;
	}

	@Override
	protected int TCIOFLUSH() {
		return Termios_Generic.TCIOFLUSH;
	}

	@Override
	protected int TCION() {
		return Termios_Generic.TCION;
	}

	@Override
	protected int TCOFLUSH() {
		return Termios_Generic.TCOFLUSH;
	}

	@Override
	protected int TCOOFF() {
		return Termios_Generic.TCOOFF;
	}

	@Override
	protected int TCOON() {
		return Termios_Generic.TCOON;
	}

	@Override
	protected int TCSADRAIN() {
		return Termios_Generic.TCSADRAIN;
	}

	@Override
	protected int TCSAFLUSH() {
		return Termios_Generic.TCSAFLUSH;
	}

	@Override
	protected int TCSANOW() {
		return Termios_Generic.TCSANOW;
	}

	@Override
	public int tcsendbreak(int fildes, int duration) {
		return nativeFunctions.tcsendbreak(fildes, duration);
	}

	@Override
	public int tcsetattr(int fildes, int optional_actions, Termios termios) {
		TermiosImpl termiosImpl = wrap(termios);
		int result = nativeFunctions.tcsetattr(fildes, optional_actions, termiosImpl);
		unwrap(termios, termiosImpl);
		return result;
	}

	@Override
	protected int TOSTOP() {
		return Termios_Generic.TOSTOP;
	}

	private void unwrap(Termios termios, TermiosImpl termiosImpl) {
		if (!(termios instanceof LinuxTermios)) {
			throw new IllegalArgumentException("termios mus be a LinuxTermios");
		} 
		final LinuxTermios linuxTermios = (LinuxTermios)termios;
		linuxTermios.c_iflag = termiosImpl.c_iflag.intValue();
		linuxTermios.c_oflag = termiosImpl.c_oflag.intValue();
		linuxTermios.c_cflag = termiosImpl.c_cflag.intValue();
		linuxTermios.c_lflag = termiosImpl.c_lflag.intValue();
		linuxTermios.c_line = termiosImpl.c_line.byteValue();
		for (int i = 0; i < NCCS; i++) {
			linuxTermios.c_cc[i] = termiosImpl.c_cc[i].byteValue();
		}
		linuxTermios.c_ispeed = termiosImpl.c_ispeed == null ? null : termiosImpl.c_ispeed.intValue();
		linuxTermios.c_ospeed = termiosImpl.c_ospeed == null ? null : termiosImpl.c_ospeed.intValue();
	}

	@Override
	protected Integer VDISCARD() {
		return Termios_Generic.VDISCARD;
	}

	@Override
	protected Integer VDSUSP() {
		return null;
	}

	@Override
	protected int VEOF() {
		return Termios_Generic.VEOF;
	}

	@Override
	protected int VEOL() {
		return Termios_Generic.VEOL;
	}

	@Override
	protected Integer VEOL2() {
		return Termios_Generic.VEOL2;
	}

	@Override
	protected int VERASE() {
		return Termios_Generic.VERASE;
	}

	@Override
	protected int VINTR() {
		return Termios_Generic.VINTR;
	}

	@Override
	protected int VKILL() {
		return Termios_Generic.VKILL;
	}

	@Override
	protected Integer VLNEXT() {
		return Termios_Generic.VLNEXT;
	}

	@Override
	protected int VMIN() {
		return Termios_Generic.VMIN;
	}

	@Override
	protected int VQUIT() {
		return Termios_Generic.VQUIT;
	}

	@Override
	protected Integer VREPRINT() {
		return Termios_Generic.VREPRINT;
	}

	@Override
	protected int VSTART() {
		return Termios_Generic.VSTART;
	}

	@Override
	protected Integer VSTATUS() {
		return null;
	}

	@Override
	protected int VSTOP() {
		return Termios_Generic.VSTOP;
	}

	@Override
	protected int VSUSP() {
		return Termios_Generic.VSUSP;
	}

	@Override
	protected Integer VSWTC() {
		return Termios_Generic.VSWTC;
	}

	@Override
	protected @Nullable Integer VSWTCH() {
		return null;
	}

	@Override
	protected int VT0() {
		return Termios_Generic.VT0;
	}

	@Override
	protected int VT1() {
		return Termios_Generic.VT1;
	}

	@Override
	protected int VTDLY() {
		return Termios_Generic.VTDLY;
	}

	@Override
	protected int VTIME() {
		return Termios_Generic.VTIME;
	}

	@Override
	protected Integer VWERASE() {
		return Termios_Generic.VWERASE;
	}

	private TermiosImpl wrap(Termios termios) {
		if (!(termios instanceof LinuxTermios)) {
			throw new IllegalArgumentException("termios mus be a LinuxTermios");
		} 
		final LinuxTermios linuxTermios = (LinuxTermios)termios;
		TermiosImpl result = new TermiosImpl(Runtime.getRuntime(nativeFunctions));
		result.c_iflag.set(linuxTermios.c_iflag);
		result.c_oflag.set(linuxTermios.c_oflag);
		result.c_cflag.set(linuxTermios.c_cflag);
		result.c_lflag.set(linuxTermios.c_lflag);
		result.c_line.set(linuxTermios.c_line);
		for (int i = 0; i < NCCS; i++) {
			result.c_cc[i].set(linuxTermios.c_cc[i]);
		}

		if (isDefined(_HAVE_STRUCT_TERMIOS_C_ISPEED())) {
			result.c_ispeed.set(linuxTermios.c_ispeed);
		} else {
			if (linuxTermios.c_ispeed != 0) {
				throw new IllegalArgumentException();
			}
		}

		if (isDefined(_HAVE_STRUCT_TERMIOS_C_OSPEED)) {
			result.c_ospeed.set(linuxTermios.c_ospeed);
		} else {
			if (linuxTermios.c_ospeed == 0) {
				throw new IllegalArgumentException();
			}
		}
		return result;
	}

	@Override
	protected Integer XCASE() {
		return Termios_Generic.XCASE;
	}

	@Override
	protected Integer XTABS() {
		return Termios_Generic.XTABS;
	}

}