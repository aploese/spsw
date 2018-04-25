package de.ibapl.jnrheader.linux;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.jdt.annotation.Nullable;

import jnr.ffi.types.int32_t;
import de.ibapl.jnrheader.Defined;
import de.ibapl.jnrheader.NativeDataType;
import de.ibapl.jnrheader.posix.Termios_H;
import jnr.ffi.LibraryLoader;
import jnr.ffi.Runtime;
import jnr.ffi.Struct;
import jnr.ffi.TypeAlias;
import jnr.ffi.annotations.In;
import jnr.ffi.annotations.Out;
import jnr.ffi.annotations.Transient;
import jnr.ffi.annotations.TypeDefinition;

public abstract class Termios_Lib extends Termios_H {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = { ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD })
	@TypeDefinition(alias = TypeAlias.int32_t)
	@NativeDataType("unsigned int")
	public @interface speed_t {

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = { ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD })
	@TypeDefinition(alias = TypeAlias.int8_t)
	@NativeDataType("unsigned char")
	public @interface cc_t {

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = { ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD })
	@TypeDefinition(alias = TypeAlias.int32_t)
	@NativeDataType("unsigned int")
	public @interface tcflag_t {

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = { ElementType.PARAMETER, ElementType.METHOD })
	@TypeDefinition(alias = TypeAlias.int32_t)
	@NativeDataType("int")
	public @interface pid_t {

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = { ElementType.PARAMETER, ElementType.METHOD, ElementType.TYPE })
	@NativeDataType("termios")
	public @interface termios {

	}

	@de.ibapl.jnrheader.NativeFunctions
	protected interface NativeFunctions {
		@speed_t
		int cfgetispeed(@termios TermiosImpl termios);

		@speed_t
		int cfgetospeed(@termios TermiosImpl termios);

		@speed_t
		int cfsetispeed(@Out @Transient @termios TermiosImpl termios, @speed_t int speed);

		@int32_t
		int cfsetospeed(@Out @Transient @termios TermiosImpl termios, @speed_t int speed);

		// TODO POSIX ??? set both in and out
		@int32_t
		int cfsetspeed(@Out @Transient @termios TermiosImpl termios, @speed_t int speed);

		@int32_t
		int tcdrain(@int32_t int fildes);

		@int32_t
		int tcflow(@int32_t int fildes, @int32_t int action);

		@int32_t
		int tcflush(@int32_t int fildes, @int32_t int queue_selector);

		@int32_t
		int tcgetattr(@int32_t int fildes, @termios TermiosImpl termios);

		@pid_t
		int tcgetsid(@int32_t int fildes);

		@int32_t
		int tcsendbreak(@int32_t int fildes, @int32_t int duration);

		@int32_t
		int tcsetattr(@int32_t int fildes, @int32_t int optional_actions, @termios TermiosImpl termios);
	}

	public static final int NCCS = 32;
	public static final Integer VINTR = 0;
	public static final Integer VQUIT = 1;
	public static final Integer VERASE = 2;
	public static final Integer VKILL = 3;
	public static final Integer VEOF = 4;
	public static final Integer VTIME = 5;
	public static final Integer VMIN = 6;
	public static final Integer VSWTC = 7;
	public static final Integer VSWTCH = null;
	public static final Integer VSTART = 8;
	public static final Integer VSTOP = 9;
	public static final Integer VSUSP = 10;
	public static final Integer VEOL = 11;
	public static final Integer VREPRINT = 12;
	public static final Integer VDISCARD = 13;
	public static final Integer VWERASE = 14;
	public static final Integer VLNEXT = 15;
	public static final Integer VEOL2 = 16;
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
	public static final Integer CMSPAR = 010000000000;
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

	public Termios_Lib() {
		nativeFunctions = LibraryLoader.create(NativeFunctions.class).load("c");
	}

	@termios
	public class TermiosImpl extends Struct {
		@Termios_Lib.tcflag_t
		public final int32_t c_iflag = new int32_t(); /* input mode flags */
		@Termios_Lib.tcflag_t
		public final int32_t c_oflag = new int32_t(); /* output mode flags */
		@Termios_Lib.tcflag_t
		public final int32_t c_cflag = new int32_t(); /* control mode flags */
		@Termios_Lib.tcflag_t
		public final int32_t c_lflag = new int32_t(); /* local mode flags */
		@Termios_Lib.cc_t
		public final int8_t c_line = new int8_t(); /* line discipline */
		@Termios_Lib.cc_t
		public final int8_t[] c_cc = createCc_t(); /* control characters */

		// This is termios2 ??? - but linux except mips does define this.
		/**
		 * Use this only if {@link TermiosFlags#._HAVE_STRUCT_TERMIOS_C_ISPEED} is
		 * defined.
		 * 
		 */
		@Termios_Lib.speed_t
		public final int32_t c_ispeed = Defined.isDefined(_HAVE_STRUCT_TERMIOS_C_ISPEED) ? new int32_t() : null; /* input speed */
		/**
		 * Use this only if {@link TermiosFlags#._HAVE_STRUCT_TERMIOS_C_OSPEED} is
		 * defined.
		 * 
		 */
		@Termios_Lib.speed_t
		public final int32_t c_ospeed = Defined.isDefined(_HAVE_STRUCT_TERMIOS_C_OSPEED) ? new int32_t() : null; /* output speed */

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

	@Override
	protected int __MAX_BAUD() {
		return Termios_Lib.__MAX_BAUD;
	}

	@Override
	protected int B0() {
		return Termios_Lib.B0;
	}

	@Override
	protected int B1000000() {
		return Termios_Lib.B1000000;
	}

	@Override
	protected int B110() {
		return Termios_Lib.B110;
	}

	@Override
	protected int B115200() {
		return Termios_Lib.B115200;
	}

	@Override
	protected int B1152000() {
		return Termios_Lib.B1152000;
	}

	@Override
	protected int B1200() {
		return Termios_Lib.B1200;
	}

	@Override
	protected int B134() {
		return Termios_Lib.B134;
	}

	@Override
	protected int B150() {
		return Termios_Lib.B150;
	}

	@Override
	protected int B1500000() {
		return Termios_Lib.B1500000;
	}

	@Override
	protected int B1800() {
		return Termios_Lib.B1800;
	}

	@Override
	protected int B19200() {
		return Termios_Lib.B19200;
	}

	@Override
	protected int B200() {
		return Termios_Lib.B200;
	}

	@Override
	protected int B2000000() {
		return Termios_Lib.B2000000;
	}

	@Override
	protected int B230400() {
		return Termios_Lib.B230400;
	}

	@Override
	protected int B2400() {
		return Termios_Lib.B2400;
	}

	@Override
	protected int B2500000() {
		return Termios_Lib.B2500000;
	}

	@Override
	protected int B300() {
		return Termios_Lib.B300;
	}

	@Override
	protected int B3000000() {
		return Termios_Lib.B3000000;
	}

	@Override
	protected int B3500000() {
		return Termios_Lib.B3500000;
	}

	@Override
	protected int B38400() {
		return Termios_Lib.B38400;
	}

	@Override
	protected int B4000000() {
		return Termios_Lib.B4000000;
	}

	@Override
	protected int B460800() {
		return Termios_Lib.B460800;
	}

	@Override
	protected int B4800() {
		return Termios_Lib.B4800;
	}

	@Override
	protected int B50() {
		return Termios_Lib.B50;
	}

	@Override
	protected int B500000() {
		return Termios_Lib.B500000;
	}

	@Override
	protected int B57600() {
		return Termios_Lib.B57600;
	}

	@Override
	protected int B576000() {
		return Termios_Lib.B576000;
	}

	@Override
	protected int B600() {
		return Termios_Lib.B600;
	}

	@Override
	protected int B75() {
		return Termios_Lib.B75;
	}

	@Override
	protected int B921600() {
		return Termios_Lib.B921600;
	}

	@Override
	protected int B9600() {
		return Termios_Lib.B9600;
	}

	@Override
	protected int BRKINT() {
		return Termios_Lib.BRKINT;
	}

	@Override
	protected int BS0() {
		return Termios_Lib.BS0;
	}

	@Override
	protected int BS1() {
		return Termios_Lib.BS1;
	}

	@Override
	protected int BSDLY() {
		return Termios_Lib.BSDLY;
	}

	@Override
	protected int CBAUD() {
		return Termios_Lib.CBAUD;
	}

	@Override
	protected int CBAUDEX() {
		return Termios_Lib.CBAUDEX;
	}

	@Override
	public int cfgetispeed(Termios termios) {
		TermiosImpl termiosImpl = wrap(termios);
		int result = nativeFunctions.cfgetispeed(termiosImpl);
		unwrap(termios, termiosImpl);
		return result;
	}

	private void unwrap(Termios termios, TermiosImpl termiosImpl) {
		termios.c_iflag = termiosImpl.c_iflag.intValue();
		termios.c_oflag = termiosImpl.c_oflag.intValue();
		termios.c_cflag = termiosImpl.c_cflag.intValue();
		termios.c_lflag = termiosImpl.c_lflag.intValue();
		termios.c_line = termiosImpl.c_line.byteValue();
		for (int i = 0; i < NCCS; i++) {
			termios.c_cc[i] = termiosImpl.c_cc[i].byteValue();
		}
		termios.c_ispeed = termiosImpl.c_ispeed == null ? null : termiosImpl.c_ispeed.intValue();
		termios.c_ospeed = termiosImpl.c_ospeed == null ? null : termiosImpl.c_ospeed.intValue();
	}

	@Override
	public int cfgetospeed(Termios termios) {
		TermiosImpl termiosImpl = wrap(termios);
		int result = nativeFunctions.cfgetospeed(termiosImpl);
		unwrap(termios, termiosImpl);
		return result;
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
	protected int CIBAUD() {
		return Termios_Lib.CIBAUD;
	}

	@Override
	protected int CLOCAL() {
		return Termios_Lib.CLOCAL;
	}

	@Override
	protected @Nullable Integer CMSPAR() {
		return Termios_Lib.CMSPAR;
	}

	@Override
	protected int CR0() {
		return Termios_Lib.CR0;
	}

	@Override
	protected int CR1() {
		return Termios_Lib.CR1;
	}

	@Override
	protected int CR2() {
		return Termios_Lib.CR2;
	}

	@Override
	protected int CR3() {
		return Termios_Lib.CR3;
	}

	@Override
	protected int CRDLY() {
		return Termios_Lib.CRDLY;
	}

	@Override
	protected int CREAD() {
		return Termios_Lib.CREAD;
	}

	@Override
	public Termios createTermios() {
		Termios result = new Termios();
		result.c_cc = new byte[NCCS];
		result.c_ispeed = Defined.isDefined(_HAVE_STRUCT_TERMIOS_C_ISPEED) ? 0 : null;
		result.c_ospeed = Defined.isDefined(_HAVE_STRUCT_TERMIOS_C_OSPEED) ? 0 : null;
		return result;
	}

	@Override
	protected int CRTSCTS() {
		return Termios_Lib.CRTSCTS;
	}

	@Override
	protected int CS5() {
		return Termios_Lib.CS5;
	}

	@Override
	protected int CS6() {
		return Termios_Lib.CS6;
	}

	@Override
	protected int CS7() {
		return Termios_Lib.CS7;
	}

	@Override
	protected int CS8() {
		return Termios_Lib.CS8;
	}

	@Override
	protected int CSIZE() {
		return Termios_Lib.CSIZE;
	}

	@Override
	protected int CSTOPB() {
		return Termios_Lib.CSTOPB;
	}

	@Override
	protected int ECHO() {
		return Termios_Lib.ECHO;
	}

	@Override
	protected int ECHOCTL() {
		return Termios_Lib.ECHOCTL;
	}

	@Override
	protected int ECHOE() {
		return Termios_Lib.ECHOE;
	}

	@Override
	protected int ECHOK() {
		return Termios_Lib.ECHOK;
	}

	@Override
	protected int ECHOKE() {
		return Termios_Lib.ECHOKE;
	}

	@Override
	protected int ECHONL() {
		return Termios_Lib.ECHONL;
	}

	@Override
	protected int ECHOPRT() {
		return Termios_Lib.ECHOPRT;
	}

	@Override
	protected int EXTA() {
		return Termios_Lib.EXTA;
	}

	@Override
	protected int EXTB() {
		return Termios_Lib.EXTB;
	}

	@Override
	protected int EXTPROC() {
		return Termios_Lib.EXTPROC;
	}

	@Override
	protected int FF0() {
		return Termios_Lib.FF0;
	}

	@Override
	protected int FF1() {
		return Termios_Lib.FF1;
	}

	@Override
	protected int FFDLY() {
		return Termios_Lib.FFDLY;
	}

	@Override
	protected int FLUSHO() {
		return Termios_Lib.FLUSHO;
	}

	@Override
	protected int HUPCL() {
		return Termios_Lib.HUPCL;
	}

	@Override
	protected int ICANON() {
		return Termios_Lib.ICANON;
	}

	@Override
	protected int ICRNL() {
		return Termios_Lib.ICRNL;
	}

	@Override
	protected int IEXTEN() {
		return Termios_Lib.IEXTEN;
	}

	@Override
	protected int IGNBRK() {
		return Termios_Lib.IGNBRK;
	}

	@Override
	protected int IGNCR() {
		return Termios_Lib.IGNCR;
	}

	@Override
	protected int IGNPAR() {
		return Termios_Lib.IGNPAR;
	}

	@Override
	protected int IMAXBEL() {
		return Termios_Lib.IMAXBEL;
	}

	@Override
	protected int INLCR() {
		return Termios_Lib.INLCR;
	}

	@Override
	protected int INPCK() {
		return Termios_Lib.INPCK;
	}

	@Override
	protected int ISIG() {
		return Termios_Lib.ISIG;
	}

	@Override
	protected int ISTRIP() {
		return Termios_Lib.ISTRIP;
	}

	@Override
	protected int IUCLC() {
		return Termios_Lib.IUCLC;
	}

	@Override
	protected int IUTF8() {
		return Termios_Lib.IUTF8;
	}

	@Override
	protected int IXANY() {
		return Termios_Lib.IXANY;
	}

	@Override
	protected int IXOFF() {
		return Termios_Lib.IXOFF;
	}

	@Override
	protected int IXON() {
		return Termios_Lib.IXON;
	}

	@Override
	protected int NCCS() {
		return Termios_Lib.NCCS;
	}

	@Override
	protected int NL0() {
		return Termios_Lib.NL0;
	}

	@Override
	protected int NL1() {
		return Termios_Lib.NL1;
	}

	@Override
	protected int NLDLY() {
		return Termios_Lib.NLDLY;
	}

	@Override
	protected int NOFLSH() {
		return Termios_Lib.NOFLSH;
	}

	@Override
	protected int OCRNL() {
		return Termios_Lib.OCRNL;
	}

	@Override
	protected int OFDEL() {
		return Termios_Lib.OFDEL;
	}

	@Override
	protected int OFILL() {
		return Termios_Lib.OFILL;
	}

	@Override
	protected int OLCUC() {
		return Termios_Lib.OLCUC;
	}

	@Override
	protected int ONLCR() {
		return Termios_Lib.ONLCR;
	}

	@Override
	protected int ONLRET() {
		return Termios_Lib.ONLRET;
	}

	@Override
	protected int ONOCR() {
		return Termios_Lib.ONOCR;
	}

	@Override
	protected int OPOST() {
		return Termios_Lib.OPOST;
	}

	@Override
	protected int PARENB() {
		return Termios_Lib.PARENB;
	}

	@Override
	protected Integer PAREXT() {
		return null;
	}

	@Override
	protected int PARMRK() {
		return Termios_Lib.PARMRK;
	}

	@Override
	protected int PARODD() {
		return Termios_Lib.PARODD;
	}

	@Override
	protected int PENDIN() {
		return Termios_Lib.PENDIN;
	}

	@Override
	protected int TAB0() {
		return Termios_Lib.TAB0;
	}

	@Override
	protected int TAB1() {
		return Termios_Lib.TAB1;
	}

	@Override
	protected int TAB2() {
		return Termios_Lib.TAB2;
	}

	@Override
	protected int TAB3() {
		return Termios_Lib.TAB3;
	}

	@Override
	protected int TABDLY() {
		return Termios_Lib.TABDLY;
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

	private TermiosImpl wrap(Termios termios) {
		TermiosImpl result = new TermiosImpl(Runtime.getRuntime(nativeFunctions));
		result.c_iflag.set(termios.c_iflag);
		result.c_oflag.set(termios.c_oflag);
		result.c_cflag.set(termios.c_cflag);
		result.c_lflag.set(termios.c_lflag);
		result.c_line.set(termios.c_line);
		for (int i = 0; i < NCCS; i++) {
			result.c_cc[i].set(termios.c_cc[i]);
		}

		if (result.c_ispeed == null) {
			if (termios.c_ispeed != null) {
				throw new IllegalArgumentException();
			}
		} else {
			if (termios.c_ispeed == null) {
				throw new IllegalArgumentException();
			}
			result.c_ispeed.set(termios.c_ispeed);
		}

		if (result.c_ospeed == null) {
			if (termios.c_ospeed != null) {
				throw new IllegalArgumentException();
			}
		} else {
			if (termios.c_ospeed == null) {
				throw new IllegalArgumentException();
			}
			result.c_ospeed.set(termios.c_ospeed);
		}
		return result;
	}

	@Override
	public int tcgetsid(int fildes) {
		return nativeFunctions.tcgetsid(fildes);
	}

	@Override
	protected int TCIFLUSH() {
		return Termios_Lib.TCIFLUSH;
	}

	@Override
	protected int TCIOFF() {
		return Termios_Lib.TCIOFF;
	}

	@Override
	protected int TCIOFLUSH() {
		return Termios_Lib.TCIOFLUSH;
	}

	@Override
	protected int TCION() {
		return Termios_Lib.TCION;
	}

	@Override
	protected int TCOFLUSH() {
		return Termios_Lib.TCOFLUSH;
	}

	@Override
	protected int TCOOFF() {
		return Termios_Lib.TCOOFF;
	}

	@Override
	protected int TCOON() {
		return Termios_Lib.TCOON;
	}

	@Override
	protected int TCSADRAIN() {
		return Termios_Lib.TCSADRAIN;
	}

	@Override
	protected int TCSAFLUSH() {
		return Termios_Lib.TCSAFLUSH;
	}

	@Override
	protected int TCSANOW() {
		return Termios_Lib.TCSANOW;
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
		return Termios_Lib.TOSTOP;
	}

	@Override
	protected @Nullable Integer VDISCARD() {
		return Termios_Lib.VDISCARD;
	}

	@Override
	protected @Nullable Integer VEOF() {
		return Termios_Lib.VEOF;
	}

	@Override
	protected @Nullable Integer VEOL() {
		return Termios_Lib.VEOL;
	}

	@Override
	protected @Nullable Integer VEOL2() {
		return Termios_Lib.VEOL2;
	}

	@Override
	protected @Nullable Integer VERASE() {
		return Termios_Lib.VERASE;
	}

	@Override
	protected @Nullable Integer VINTR() {
		return Termios_Lib.VINTR;
	}

	@Override
	protected @Nullable Integer VKILL() {
		return Termios_Lib.VKILL;
	}

	@Override
	protected @Nullable Integer VLNEXT() {
		return Termios_Lib.VLNEXT;
	}

	@Override
	protected @Nullable Integer VMIN() {
		return Termios_Lib.VMIN;
	}

	@Override
	protected @Nullable Integer VQUIT() {
		return Termios_Lib.VQUIT;
	}

	@Override
	protected @Nullable Integer VREPRINT() {
		return Termios_Lib.VREPRINT;
	}

	@Override
	protected @Nullable Integer VSTART() {
		return Termios_Lib.VSTART;
	}

	@Override
	protected @Nullable Integer VSTOP() {
		return Termios_Lib.VSTOP;
	}

	@Override
	protected @Nullable Integer VSUSP() {
		return Termios_Lib.VSUSP;
	}

	@Override
	protected @Nullable Integer VSWTC() {
		return Termios_Lib.VSWTC;
	}

	@Override
	protected @Nullable Integer VSWTCH() {
		return Termios_Lib.VSWTCH;
	}

	@Override
	protected int VT0() {
		return Termios_Lib.VT0;
	}

	@Override
	protected int VT1() {
		return Termios_Lib.VT1;
	}

	@Override
	protected int VTDLY() {
		return Termios_Lib.VTDLY;
	}

	@Override
	protected @Nullable Integer VTIME() {
		return Termios_Lib.VTIME;
	}

	@Override
	protected @Nullable Integer VWERASE() {
		return Termios_Lib.VWERASE;
	}

	@Override
	protected int XCASE() {
		return Termios_Lib.XCASE;
	}

	@Override
	protected int XTABS() {
		return Termios_Lib.XTABS;
	}

}