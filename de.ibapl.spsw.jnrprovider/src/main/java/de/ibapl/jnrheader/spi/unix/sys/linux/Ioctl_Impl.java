package de.ibapl.jnrheader.spi.unix.sys.linux;

import de.ibapl.jnrheader.Defined;
import de.ibapl.jnrheader.ExtraInclude;
import de.ibapl.jnrheader.spi.posix.linux.Termios_Generic;
import de.ibapl.jnrheader.api.unix.sys.Ioctl_H;
import jnr.ffi.LibraryLoader;
import jnr.ffi.byref.IntByReference;
import jnr.ffi.types.int32_t;
import jnr.ffi.types.intptr_t;
import jnr.ffi.types.u_int64_t;

@ExtraInclude("termios.h")
public final class Ioctl_Impl extends Ioctl_H {
	@de.ibapl.jnrheader.NativeFunctions
	protected interface NativeFunctions {
		@int32_t
		int ioctl(@int32_t int fd, @u_int64_t long request);

		@int32_t
		int ioctl(@int32_t int fd, @u_int64_t long request, @intptr_t IntByReference value);
	}

        @SuppressWarnings("FieldNameHidesFieldInSuperclass")
	public static final int TCGETS = 0x5401;
	public static final int TCSETS = 0x5402;
	public static final int TCSETSW = 0x5403;
	public static final int TCSETSF = 0x5404;
	public static final int TCGETA = 0x5405;
	public static final int TCSETA = 0x5406;
	public static final int TCSETAW = 0x5407;
	public static final int TCSETAF = 0x5408;
	public static final int TCSBRK = 0x5409;
	public static final int TCXONC = 0x540A;
	public static final int TCFLSH = 0x540B;
	public static final int TIOCEXCL = 0x540C;
	public static final int TIOCNXCL = 0x540D;
	public static final int TIOCSCTTY = 0x540E;
	public static final int TIOCGPGRP = 0x540F;
	public static final int TIOCSPGRP = 0x5410;
	public static final int TIOCOUTQ = 0x5411;
	public static final int TIOCSTI = 0x5412;
	public static final int TIOCGWINSZ = 0x5413;
	public static final int TIOCSWINSZ = 0x5414;
	public static final int TIOCMGET = 0x5415;
	public static final int TIOCMBIS = 0x5416;
	public static final int TIOCMBIC = 0x5417;
	public static final int TIOCMSET = 0x5418;
	public static final int TIOCGSOFTCAR = 0x5419;
	public static final int TIOCSSOFTCAR = 0x541A;
	public static final int FIONREAD = 0x541B;
	public static final int TIOCINQ = FIONREAD;
	public static final int TIOCLINUX = 0x541C;
	public static final int TIOCCONS = 0x541D;
	public static final int TIOCGSERIAL = 0x541E;
	public static final int TIOCSSERIAL = 0x541F;
	public static final int TIOCPKT = 0x5420;
	public static final int FIONBIO = 0x5421;
	public static final int TIOCNOTTY = 0x5422;
	public static final int TIOCSETD = 0x5423;
	public static final int TIOCGETD = 0x5424;
	public static final int TCSBRKP = 0x5425;
	public static final int TIOCSBRK = 0x5427;
	public static final int TIOCCBRK = 0x5428;
	public static final int TIOCGSID = 0x5429;
	// TODO Linux termios2 ???
	// public static final int TCGETS2 _IOR('T', 0x2A, struct termios2)
	// public static final int TCSETS2 _IOW('T', 0x2B, struct termios2)
	// public static final int TCSETSW2 _IOW('T', 0x2C, struct termios2)
	// public static final int TCSETSF2 _IOW('T', 0x2D, struct termios2)
	public static final int TIOCGRS485 = 0x542E;
	public static final int TIOCSRS485 = 0x542F;
	public static final int TIOCGPTN = 0x80045430; // _IOR('T', 0x30, NativeType.SINT);
	public static final int TIOCSPTLCK = 0x40045431; // _IOW('T', 0x31, NativeType.SINT);
	public static final int TIOCGDEV = 0x80045432; // _IOR('T', 0x32, NativeType.SINT);
	public static final int TCGETX = 0x5432;
	public static final int TCSETX = 0x5433;
	public static final int TCSETXF = 0x5434;
	public static final int TCSETXW = 0x5435;
	public static final int TIOCSIG = 0x40045436; // _IOW('T', 0x36, int32_t);
	public static final int TIOCVHANGUP = 0x5437;
	public static final int TIOCGPKT = 0x80045438; // _IOR('T', 0x38, int32_t);
	public static final int TIOCGPTLCK = 0x80045439; // _IOR('T', 0x39, int32_t);
	public static final int TIOCGEXCL = 0x80045440; // _IOR('T', 0x40, int32_t);
	public static final int TIOCGPTPEER = 0x5441; // _IO('T', 0x41);
	public static final int FIONCLEX = 0x5450;
	public static final int FIOCLEX = 0x5451;
	public static final int FIOASYNC = 0x5452;
	public static final int TIOCSERCONFIG = 0x5453;
	public static final int TIOCSERGWILD = 0x5454;
	public static final int TIOCSERSWILD = 0x5455;
	public static final int TIOCGLCKTRMIOS = 0x5456;
	public static final int TIOCSLCKTRMIOS = 0x5457;
	public static final int TIOCSERGSTRUCT = 0x5458;
	public static final int TIOCSERGETLSR = 0x5459;
	public static final int TIOCSERGETMULTI = 0x545A;
	public static final int TIOCSERSETMULTI = 0x545B;
	public static final int TIOCMIWAIT = 0x545C;
	public static final int TIOCGICOUNT = 0x545D;
	public static final int FIOQSIZE = 0x5460;
	public static final int TIOCPKT_DATA = 0;
	public static final int TIOCPKT_FLUSHREAD = 1;
	public static final int TIOCPKT_FLUSHWRITE = 2;
	public static final int TIOCPKT_STOP = 4;
	public static final int TIOCPKT_START = 8;
	public static final int TIOCPKT_NOSTOP = 16;
	public static final int TIOCPKT_DOSTOP = 32;
	public static final int TIOCPKT_IOCTL = 64;
	public static final int TIOCSER_TEMT = 0x01;
	public static final int SIOCADDRT = 0x890B;
	public static final int SIOCDELRT = 0x890C;
	public static final int SIOCRTMSG = 0x890D;
	public static final int SIOCGIFNAME = 0x8910;
	public static final int SIOCSIFLINK = 0x8911;
	public static final int SIOCGIFCONF = 0x8912;
	public static final int SIOCGIFFLAGS = 0x8913;
	public static final int SIOCSIFFLAGS = 0x8914;
	public static final int SIOCGIFADDR = 0x8915;
	public static final int SIOCSIFADDR = 0x8916;
	public static final int SIOCGIFDSTADDR = 0x8917;
	public static final int SIOCSIFDSTADDR = 0x8918;
	public static final int SIOCGIFBRDADDR = 0x8919;
	public static final int SIOCSIFBRDADDR = 0x891a;
	public static final int SIOCGIFNETMASK = 0x891b;
	public static final int SIOCSIFNETMASK = 0x891c;
	public static final int SIOCGIFMETRIC = 0x891d;
	public static final int SIOCSIFMETRIC = 0x891e;
	public static final int SIOCGIFMEM = 0x891f;
	public static final int SIOCSIFMEM = 0x8920;
	public static final int SIOCGIFMTU = 0x8921;
	public static final int SIOCSIFMTU = 0x8922;
	public static final int SIOCSIFNAME = 0x8923;
	public static final int SIOCSIFHWADDR = 0x8924;
	public static final int SIOCGIFENCAP = 0x8925;
	public static final int SIOCSIFENCAP = 0x8926;
	public static final int SIOCGIFHWADDR = 0x8927;
	public static final int SIOCGIFSLAVE = 0x8929;
	public static final int SIOCSIFSLAVE = 0x8930;
	public static final int SIOCADDMULTI = 0x8931;
	public static final int SIOCDELMULTI = 0x8932;
	public static final int SIOCGIFINDEX = 0x8933;
	public static final int SIOGIFINDEX = SIOCGIFINDEX;
	public static final int SIOCSIFPFLAGS = 0x8934;
	public static final int SIOCGIFPFLAGS = 0x8935;
	public static final int SIOCDIFADDR = 0x8936;
	public static final int SIOCSIFHWBROADCAST = 0x8937;
	public static final int SIOCGIFCOUNT = 0x8938;
	public static final int SIOCGIFBR = 0x8940;
	public static final int SIOCSIFBR = 0x8941;
	public static final int SIOCGIFTXQLEN = 0x8942;
	public static final int SIOCSIFTXQLEN = 0x8943;
	public static final int SIOCDARP = 0x8953;
	public static final int SIOCGARP = 0x8954;
	public static final int SIOCSARP = 0x8955;
	public static final int SIOCDRARP = 0x8960;
	public static final int SIOCGRARP = 0x8961;
	public static final int SIOCSRARP = 0x8962;
	public static final int SIOCGIFMAP = 0x8970;
	public static final int SIOCSIFMAP = 0x8971;
	public static final int SIOCADDDLCI = 0x8980;
	public static final int SIOCDELDLCI = 0x8981;
	public static final int SIOCDEVPRIVATE = 0x89F0;
	public static final int SIOCPROTOPRIVATE = 0x89E0;
	public static final int TIOCM_LE = 0x001;
	public static final int TIOCM_DTR = 0x002;
	public static final int TIOCM_RTS = 0x004;
	public static final int TIOCM_ST = 0x008;
	public static final int TIOCM_SR = 0x010;
	public static final int TIOCM_CTS = 0x020;
	public static final int TIOCM_CAR = 0x040;
	public static final int TIOCM_RNG = 0x080;
	public static final int TIOCM_DSR = 0x100;
	public static final int TIOCM_CD = TIOCM_CAR;
	public static final int TIOCM_RI = TIOCM_RNG;
	public static final int N_TTY = 0;
	public static final int N_SLIP = 1;
	public static final int N_MOUSE = 2;
	public static final int N_PPP = 3;
	public static final int N_STRIP = 4;
	public static final int N_AX25 = 5;
	public static final int N_X25 = 6;
	public static final int N_6PACK = 7;
	public static final int N_MASC = 8;
	public static final int N_R3964 = 9;
	public static final int N_PROFIBUS_FDL = 10;
	public static final int N_IRDA = 11;
	public static final int N_SMSBLOCK = 12;
	public static final int N_HDLC = 13;
	public static final int N_SYNC_PPP = 14;
	public static final int N_HCI = 15;
	public static final Defined _SYS_TTYDEFAULTS_H_ = Defined.DEFINED;
	public static final int TTYDEF_IFLAG = (Termios_Generic.BRKINT | Termios_Generic.ISTRIP | Termios_Generic.ICRNL
			| Termios_Generic.IMAXBEL | Termios_Generic.IXON | Termios_Generic.IXANY);
	public static final int TTYDEF_OFLAG = (Termios_Generic.OPOST | Termios_Generic.ONLCR | Termios_Generic.XTABS);
	public static final int TTYDEF_LFLAG = (Termios_Generic.ECHO | Termios_Generic.ICANON | Termios_Generic.ISIG
			| Termios_Generic.IEXTEN | Termios_Generic.ECHOE | Termios_Generic.ECHOKE | Termios_Generic.ECHOCTL);
	public static final int TTYDEF_CFLAG = (Termios_Generic.CREAD | Termios_Generic.CS7 | Termios_Generic.PARENB
			| Termios_Generic.HUPCL);
	public static final int TTYDEF_SPEED = Termios_Generic.B9600;
	private static final int CTRL_AND = 037;
	public static final int CEOF = 'd' & CTRL_AND;
	public static final int CEOL = '\0';
	public static final int CERASE = 0177;
	public static final int CINTR = 'c' & CTRL_AND;
	public static final int CSTATUS = '\0';
	public static final int CKILL = 'u' & CTRL_AND;
	public static final int CMIN = 1;
	public static final int CQUIT = 034;
	public static final int CSUSP = 'z' & CTRL_AND;
	public static final int CTIME = 0;
	public static final int CDSUSP = 'y' & CTRL_AND;
	public static final int CSTART = 'q' & CTRL_AND;
	public static final int CSTOP = 's' & CTRL_AND;
	public static final int CLNEXT = 'v' & CTRL_AND;
	public static final int CDISCARD = 'o' & CTRL_AND;
	public static final int CWERASE = 'w' & CTRL_AND;
	public static final int CREPRINT = 'r' & CTRL_AND;
	public static final int CEOT = CEOF;
	public static final int CBRK = CEOL;
	public static final int CRPRNT = CREPRINT;

	public static final int CFLUSH = CDISCARD;

	final private NativeFunctions nativeFunctions;

	public Ioctl_Impl() {
		nativeFunctions = LibraryLoader.create(NativeFunctions.class).load("c");
	}

	@Override
	protected Defined _SYS_TTYDEFAULTS_H_() {
		return Ioctl_Impl._SYS_TTYDEFAULTS_H_;
	}

	@Override
	protected int CBRK() {
		return Ioctl_Impl.CBRK;
	}

	@Override
	protected int CDISCARD() {
		return Ioctl_Impl.CDISCARD;
	}

	@Override
	protected int CDSUSP() {
		return Ioctl_Impl.CDSUSP;
	}

	@Override
	protected int CEOF() {
		return Ioctl_Impl.CEOF;
	}

	@Override
	protected int CEOL() {
		return Ioctl_Impl.CEOL;
	}

	@Override
	protected int CEOT() {
		return Ioctl_Impl.CEOT;
	}

	@Override
	protected int CERASE() {
		return Ioctl_Impl.CERASE;
	}

	@Override
	protected int CFLUSH() {
		return Ioctl_Impl.CFLUSH;
	}

	@Override
	protected int CINTR() {
		return Ioctl_Impl.CINTR;
	}

	@Override
	protected int CKILL() {
		return Ioctl_Impl.CKILL;
	}

	@Override
	protected int CLNEXT() {
		return Ioctl_Impl.CLNEXT;
	}

	@Override
	protected int CMIN() {
		return Ioctl_Impl.CMIN;
	}

	@Override
	protected int CQUIT() {
		return Ioctl_Impl.CQUIT;
	}

	@Override
	protected int CREPRINT() {
		return Ioctl_Impl.CREPRINT;
	}

	@Override
	protected int CRPRNT() {
		return Ioctl_Impl.CRPRNT;
	}

	@Override
	protected int CSTART() {
		return Ioctl_Impl.CSTART;
	}

	@Override
	protected int CSTATUS() {
		return Ioctl_Impl.CSTATUS;
	}

	@Override
	protected int CSTOP() {
		return Ioctl_Impl.CSTOP;
	}

	@Override
	protected int CSUSP() {
		return Ioctl_Impl.CSUSP;
	}

	@Override
	protected int CTIME() {
		return Ioctl_Impl.CTIME;
	}

	@Override
	protected int CWERASE() {
		return Ioctl_Impl.CWERASE;
	}

	@Override
	protected int FIOASYNC() {
		return Ioctl_Impl.FIOASYNC;
	}

	@Override
	protected int FIOCLEX() {
		return Ioctl_Impl.FIOCLEX;
	}

	@Override
	protected int FIONBIO() {
		return Ioctl_Impl.FIONBIO;
	}

	@Override
	protected int FIONCLEX() {
		return Ioctl_Impl.FIONCLEX;
	}

	@Override
	protected int FIONREAD() {
		return Ioctl_Impl.FIONREAD;
	}

	@Override
	protected int FIOQSIZE() {
		return Ioctl_Impl.FIOQSIZE;
	}

	@Override
	public int ioctl(int fd, long request) {
		return nativeFunctions.ioctl(fd, request);
	}

	@Override
	public int ioctl(int fd, long request, IntByReference value) {
		return nativeFunctions.ioctl(fd, request, value);
	}

	@Override
	protected int N_6PACK() {
		return Ioctl_Impl.N_6PACK;
	}

	@Override
	protected int N_AX25() {
		return Ioctl_Impl.N_AX25;
	}

	@Override
	protected int N_HCI() {
		return Ioctl_Impl.N_HCI;
	}

	@Override
	protected int N_HDLC() {
		return Ioctl_Impl.N_HDLC;
	}

	@Override
	protected int N_IRDA() {
		return Ioctl_Impl.N_IRDA;
	}

	@Override
	protected int N_MASC() {
		return Ioctl_Impl.N_MASC;
	}

	@Override
	protected int N_MOUSE() {
		return Ioctl_Impl.N_MOUSE;
	}

	@Override
	protected int N_PPP() {
		return Ioctl_Impl.N_PPP;
	}

	@Override
	protected int N_PROFIBUS_FDL() {
		return Ioctl_Impl.N_PROFIBUS_FDL;
	}

	@Override
	protected int N_R3964() {
		return Ioctl_Impl.N_R3964;
	}

	@Override
	protected int N_SLIP() {
		return Ioctl_Impl.N_SLIP;
	}

	@Override
	protected int N_SMSBLOCK() {
		return Ioctl_Impl.N_SMSBLOCK;
	}

	@Override
	protected int N_STRIP() {
		return Ioctl_Impl.N_STRIP;
	}

	@Override
	protected int N_SYNC_PPP() {
		return Ioctl_Impl.N_SYNC_PPP;
	}

	@Override
	protected int N_TTY() {
		return Ioctl_Impl.N_TTY;
	}

	@Override
	protected int N_X25() {
		return Ioctl_Impl.N_X25;
	}

	@Override
	protected int SIOCADDDLCI() {
		return Ioctl_Impl.SIOCADDDLCI;
	}

	@Override
	protected int SIOCADDMULTI() {
		return Ioctl_Impl.SIOCADDMULTI;
	}

	@Override
	protected int SIOCADDRT() {
		return Ioctl_Impl.SIOCADDRT;
	}

	@Override
	protected int SIOCDARP() {
		return Ioctl_Impl.SIOCDARP;
	}

	@Override
	protected int SIOCDELDLCI() {
		return Ioctl_Impl.SIOCDELDLCI;
	}

	@Override
	protected int SIOCDELMULTI() {
		return Ioctl_Impl.SIOCDELMULTI;
	}

	@Override
	protected int SIOCDELRT() {
		return Ioctl_Impl.SIOCDELRT;
	}

	@Override
	protected int SIOCDEVPRIVATE() {
		return Ioctl_Impl.SIOCDEVPRIVATE;
	}

	@Override
	protected int SIOCDIFADDR() {
		return Ioctl_Impl.SIOCDIFADDR;
	}

	@Override
	protected int SIOCDRARP() {
		return Ioctl_Impl.SIOCDRARP;
	}

	@Override
	protected int SIOCGARP() {
		return Ioctl_Impl.SIOCGARP;
	}

	@Override
	protected int SIOCGIFADDR() {
		return Ioctl_Impl.SIOCGIFADDR;
	}

	@Override
	protected int SIOCGIFBR() {
		return Ioctl_Impl.SIOCGIFBR;
	}

	@Override
	protected int SIOCGIFBRDADDR() {
		return Ioctl_Impl.SIOCGIFBRDADDR;
	}

	@Override
	protected int SIOCGIFCONF() {
		return Ioctl_Impl.SIOCGIFCONF;
	}

	@Override
	protected int SIOCGIFCOUNT() {
		return Ioctl_Impl.SIOCGIFCOUNT;
	}

	@Override
	protected int SIOCGIFDSTADDR() {
		return Ioctl_Impl.SIOCGIFDSTADDR;
	}

	@Override
	protected int SIOCGIFENCAP() {
		return Ioctl_Impl.SIOCGIFENCAP;
	}

	@Override
	protected int SIOCGIFFLAGS() {
		return Ioctl_Impl.SIOCGIFFLAGS;
	}

	@Override
	protected int SIOCGIFHWADDR() {
		return Ioctl_Impl.SIOCGIFHWADDR;
	}

	@Override
	protected int SIOCGIFINDEX() {
		return Ioctl_Impl.SIOCGIFINDEX;
	}

	@Override
	protected int SIOCGIFMAP() {
		return Ioctl_Impl.SIOCGIFMAP;
	}

	@Override
	protected int SIOCGIFMEM() {
		return Ioctl_Impl.SIOCGIFMEM;
	}

	@Override
	protected int SIOCGIFMETRIC() {
		return Ioctl_Impl.SIOCGIFMETRIC;
	}

	@Override
	protected int SIOCGIFMTU() {
		return Ioctl_Impl.SIOCGIFMTU;
	}

	@Override
	protected int SIOCGIFNAME() {
		return Ioctl_Impl.SIOCGIFNAME;
	}

	@Override
	protected int SIOCGIFNETMASK() {
		return Ioctl_Impl.SIOCGIFNETMASK;
	}

	@Override
	protected int SIOCGIFPFLAGS() {
		return Ioctl_Impl.SIOCGIFPFLAGS;
	}

	@Override
	protected int SIOCGIFSLAVE() {
		return Ioctl_Impl.SIOCGIFSLAVE;
	}

	@Override
	protected int SIOCGIFTXQLEN() {
		return Ioctl_Impl.SIOCGIFTXQLEN;
	}

	@Override
	protected int SIOCGRARP() {
		return Ioctl_Impl.SIOCGRARP;
	}

	@Override
	protected int SIOCPROTOPRIVATE() {
		return Ioctl_Impl.SIOCPROTOPRIVATE;
	}

	@Override
	protected int SIOCRTMSG() {
		return Ioctl_Impl.SIOCRTMSG;
	}

	@Override
	protected int SIOCSARP() {
		return Ioctl_Impl.SIOCSARP;
	}

	@Override
	protected int SIOCSIFADDR() {
		return Ioctl_Impl.SIOCSIFADDR;
	}

	@Override
	protected int SIOCSIFBR() {
		return Ioctl_Impl.SIOCSIFBR;
	}

	@Override
	protected int SIOCSIFBRDADDR() {
		return Ioctl_Impl.SIOCSIFBRDADDR;
	}

	@Override
	protected int SIOCSIFDSTADDR() {
		return Ioctl_Impl.SIOCSIFDSTADDR;
	}

	@Override
	protected int SIOCSIFENCAP() {
		return Ioctl_Impl.SIOCSIFENCAP;
	}

	@Override
	protected int SIOCSIFFLAGS() {
		return Ioctl_Impl.SIOCSIFFLAGS;
	}

	@Override
	protected int SIOCSIFHWADDR() {
		return Ioctl_Impl.SIOCSIFHWADDR;
	}

	@Override
	protected int SIOCSIFHWBROADCAST() {
		return Ioctl_Impl.SIOCSIFHWBROADCAST;
	}

	@Override
	protected int SIOCSIFLINK() {
		return Ioctl_Impl.SIOCSIFLINK;
	}

	@Override
	protected int SIOCSIFMAP() {
		return Ioctl_Impl.SIOCSIFMAP;
	}

	@Override
	protected int SIOCSIFMEM() {
		return Ioctl_Impl.SIOCSIFMEM;
	}

	@Override
	protected int SIOCSIFMETRIC() {
		return Ioctl_Impl.SIOCSIFMETRIC;
	}

	@Override
	protected int SIOCSIFMTU() {
		return Ioctl_Impl.SIOCSIFMTU;
	}

	@Override
	protected int SIOCSIFNAME() {
		return Ioctl_Impl.SIOCSIFNAME;
	}

	@Override
	protected int SIOCSIFNETMASK() {
		return Ioctl_Impl.SIOCSIFNETMASK;
	}

	@Override
	protected int SIOCSIFPFLAGS() {
		return Ioctl_Impl.SIOCSIFPFLAGS;
	}

	@Override
	protected int SIOCSIFSLAVE() {
		return Ioctl_Impl.SIOCSIFSLAVE;
	}

	@Override
	protected int SIOCSIFTXQLEN() {
		return Ioctl_Impl.SIOCSIFTXQLEN;
	}

	@Override
	protected int SIOCSRARP() {
		return Ioctl_Impl.SIOCSRARP;
	}

	@Override
	protected int SIOGIFINDEX() {
		return Ioctl_Impl.SIOGIFINDEX;
	}

	@Override
	protected int TCFLSH() {
		return Ioctl_Impl.TCFLSH;
	}

	@Override
	protected int TCGETA() {
		return Ioctl_Impl.TCGETA;
	}

	@Override
	protected int TCGETS() {
		return Ioctl_Impl.TCGETS;
	}

	@Override
	protected int TCGETX() {
		return Ioctl_Impl.TCGETX;
	}

	@Override
	protected int TCSBRK() {
		return Ioctl_Impl.TCSBRK;
	}

	@Override
	protected int TCSBRKP() {
		return Ioctl_Impl.TCSBRKP;
	}

	@Override
	protected int TCSETA() {
		return Ioctl_Impl.TCSETA;
	}

	@Override
	protected int TCSETAF() {
		return Ioctl_Impl.TCSETAF;
	}

	@Override
	protected int TCSETAW() {
		return Ioctl_Impl.TCSETAW;
	}

	@Override
	protected int TCSETS() {
		return Ioctl_Impl.TCSETS;
	}

	@Override
	protected int TCSETSF() {
		return Ioctl_Impl.TCSETSF;
	}

	@Override
	protected Short TCSETSW() {
		return Ioctl_Impl.TCSETSW;
	}

	@Override
	protected int TCSETX() {
		return Ioctl_Impl.TCSETX;
	}

	@Override
	protected int TCSETXF() {
		return Ioctl_Impl.TCSETXF;
	}

	@Override
	protected int TCSETXW() {
		return Ioctl_Impl.TCSETXW;
	}

	@Override
	protected int TCXONC() {
		return Ioctl_Impl.TCXONC;
	}

	@Override
	protected int TIOCCBRK() {
		return Ioctl_Impl.TIOCCBRK;
	}

	@Override
	protected int TIOCCONS() {
		return Ioctl_Impl.TIOCCONS;
	}

	@Override
	protected int TIOCEXCL() {
		return Ioctl_Impl.TIOCEXCL;
	}

	@Override
	protected int TIOCGDEV() {
		return Ioctl_Impl.TIOCGDEV;
	}

	@Override
	protected int TIOCGETD() {
		return Ioctl_Impl.TIOCGETD;
	}

	@Override
	protected int TIOCGEXCL() {
		return Ioctl_Impl.TIOCGEXCL;
	}

	@Override
	protected int TIOCGICOUNT() {
		return Ioctl_Impl.TIOCGICOUNT;
	}

	@Override
	protected int TIOCGLCKTRMIOS() {
		return Ioctl_Impl.TIOCGLCKTRMIOS;
	}

	@Override
	protected int TIOCGPGRP() {
		return Ioctl_Impl.TIOCGPGRP;
	}

	@Override
	protected int TIOCGPKT() {
		return Ioctl_Impl.TIOCGPKT;
	}

	@Override
	protected int TIOCGPTLCK() {
		return Ioctl_Impl.TIOCGPTLCK;
	}

	@Override
	protected int TIOCGPTN() {
		return Ioctl_Impl.TIOCGPTN;
	}

	@Override
	protected int TIOCGPTPEER() {
		return Ioctl_Impl.TIOCGPTPEER;
	}

	@Override
	protected int TIOCGRS485() {
		return Ioctl_Impl.TIOCGRS485;
	}

	@Override
	protected int TIOCGSERIAL() {
		return Ioctl_Impl.TIOCGSERIAL;
	}

	@Override
	protected int TIOCGSID() {
		return Ioctl_Impl.TIOCGSID;
	}

	@Override
	protected int TIOCGSOFTCAR() {
		return Ioctl_Impl.TIOCGSOFTCAR;
	}

	@Override
	protected int TIOCGWINSZ() {
		return Ioctl_Impl.TIOCGWINSZ;
	}

	@Override
	protected int TIOCINQ() {
		return Ioctl_Impl.TIOCINQ;
	}

	@Override
	protected Short TIOCLINUX() {
		return Ioctl_Impl.TIOCLINUX;
	}

	@Override
	protected int TIOCM_CAR() {
		return Ioctl_Impl.TIOCM_CAR;
	}

	@Override
	protected int TIOCM_CD() {
		return Ioctl_Impl.TIOCM_CD;
	}

	@Override
	protected int TIOCM_CTS() {
		return Ioctl_Impl.TIOCM_CTS;
	}

	@Override
	protected int TIOCM_DSR() {
		return Ioctl_Impl.TIOCM_DSR;
	}

	@Override
	protected int TIOCM_DTR() {
		return Ioctl_Impl.TIOCM_DTR;
	}

	@Override
	protected int TIOCM_LE() {
		return Ioctl_Impl.TIOCM_LE;
	}

	@Override
	protected int TIOCM_RI() {
		return Ioctl_Impl.TIOCM_RI;
	}

	@Override
	protected int TIOCM_RNG() {
		return Ioctl_Impl.TIOCM_RNG;
	}

	@Override
	protected int TIOCM_RTS() {
		return Ioctl_Impl.TIOCM_RTS;
	}

	@Override
	protected int TIOCM_SR() {
		return Ioctl_Impl.TIOCM_SR;
	}

	@Override
	protected int TIOCM_ST() {
		return Ioctl_Impl.TIOCM_ST;
	}

	@Override
	protected int TIOCMBIC() {
		return Ioctl_Impl.TIOCMBIC;
	}

	@Override
	protected int TIOCMBIS() {
		return Ioctl_Impl.TIOCMBIS;
	}

	@Override
	protected int TIOCMGET() {
		return Ioctl_Impl.TIOCMGET;
	}

	@Override
	protected int TIOCMIWAIT() {
		return Ioctl_Impl.TIOCMIWAIT;
	}

	@Override
	protected int TIOCMSET() {
		return Ioctl_Impl.TIOCMSET;
	}

	@Override
	protected int TIOCNOTTY() {
		return Ioctl_Impl.TIOCNOTTY;
	}

	@Override
	protected int TIOCNXCL() {
		return Ioctl_Impl.TIOCNXCL;
	}

	@Override
	protected int TIOCOUTQ() {
		return Ioctl_Impl.TIOCOUTQ;
	}

	@Override
	protected int TIOCPKT() {
		return Ioctl_Impl.TIOCPKT;
	}

	@Override
	protected int TIOCPKT_DATA() {
		return Ioctl_Impl.TIOCPKT_DATA;
	}

	@Override
	protected int TIOCPKT_DOSTOP() {
		return Ioctl_Impl.TIOCPKT_DOSTOP;
	}

	@Override
	protected int TIOCPKT_FLUSHREAD() {
		return Ioctl_Impl.TIOCPKT_FLUSHREAD;
	}

	@Override
	protected int TIOCPKT_FLUSHWRITE() {
		return Ioctl_Impl.TIOCPKT_FLUSHWRITE;
	}

	@Override
	protected int TIOCPKT_IOCTL() {
		return Ioctl_Impl.TIOCPKT_IOCTL;
	}

	@Override
	protected int TIOCPKT_NOSTOP() {
		return Ioctl_Impl.TIOCPKT_NOSTOP;
	}

	@Override
	protected int TIOCPKT_START() {
		return Ioctl_Impl.TIOCPKT_START;
	}

	@Override
	protected int TIOCPKT_STOP() {
		return Ioctl_Impl.TIOCPKT_STOP;
	}

	@Override
	protected int TIOCSBRK() {
		return Ioctl_Impl.TIOCSBRK;
	}

	@Override
	protected int TIOCSCTTY() {
		return Ioctl_Impl.TIOCSCTTY;
	}

	@Override
	protected int TIOCSER_TEMT() {
		return Ioctl_Impl.TIOCSER_TEMT;
	}

	@Override
	protected int TIOCSERCONFIG() {
		return Ioctl_Impl.TIOCSERCONFIG;
	}

	@Override
	protected int TIOCSERGETLSR() {
		return Ioctl_Impl.TIOCSERGETLSR;
	}

	@Override
	protected int TIOCSERGETMULTI() {
		return Ioctl_Impl.TIOCSERGETMULTI;
	}

	@Override
	protected int TIOCSERGSTRUCT() {
		return Ioctl_Impl.TIOCSERGSTRUCT;
	}

	@Override
	protected int TIOCSERGWILD() {
		return Ioctl_Impl.TIOCSERGWILD;
	}

	@Override
	protected int TIOCSERSETMULTI() {
		return Ioctl_Impl.TIOCSERSETMULTI;
	}

	@Override
	protected int TIOCSERSWILD() {
		return Ioctl_Impl.TIOCSERSWILD;
	}

	@Override
	protected int TIOCSETD() {
		return Ioctl_Impl.TIOCSETD;
	}

	@Override
	protected int TIOCSIG() {
		return Ioctl_Impl.TIOCSIG;
	}

	@Override
	protected int TIOCSLCKTRMIOS() {
		return Ioctl_Impl.TIOCSLCKTRMIOS;
	}

	@Override
	protected int TIOCSPGRP() {
		return Ioctl_Impl.TIOCSPGRP;
	}

	@Override
	protected int TIOCSPTLCK() {
		return Ioctl_Impl.TIOCSPTLCK;
	}

	@Override
	protected int TIOCSRS485() {
		return Ioctl_Impl.TIOCSRS485;
	}

	@Override
	protected int TIOCSSERIAL() {
		return Ioctl_Impl.TIOCSSERIAL;
	}

	@Override
	protected int TIOCSSOFTCAR() {
		return Ioctl_Impl.TIOCSSOFTCAR;
	}

	@Override
	protected int TIOCSTI() {
		return Ioctl_Impl.TIOCSTI;
	}

	@Override
	protected int TIOCSWINSZ() {
		return Ioctl_Impl.TIOCSWINSZ;
	}

	@Override
	protected int TIOCVHANGUP() {
		return Ioctl_Impl.TIOCVHANGUP;
	}

	@Override
	protected int TTYDEF_CFLAG() {
		return Ioctl_Impl.TTYDEF_CFLAG;
	}

	@Override
	protected int TTYDEF_IFLAG() {
		return Ioctl_Impl.TTYDEF_IFLAG;
	}

	@Override
	protected int TTYDEF_LFLAG() {
		return Ioctl_Impl.TTYDEF_LFLAG;
	}

	@Override
	protected int TTYDEF_OFLAG() {
		return Ioctl_Impl.TTYDEF_OFLAG;
	}

	@Override
	protected int TTYDEF_SPEED() {
		return Ioctl_Impl.TTYDEF_SPEED;
	}

}
