package de.ibapl.spsw.jnr;

import jnr.ffi.LibraryLoader;

@Wrapper("sys/ioctl.h")
public class Sys_Ioctl_H {

	public interface NativeFunctions {
		int ioctl(int fd, long request);
	}

	@Defines("sys/ioctl.h")
	public interface Sys_IoctlDefines extends JnrDefines {
		@Mandatory
		short FIOASYNC();

		@Mandatory
		short FIOCLEX();

		@Mandatory
		short FIONBIO();

		@Mandatory
		short FIONCLEX();

		@Mandatory
		short FIONREAD();

		@Mandatory
		short FIOQSIZE();

		@Mandatory
		short SIOCADDDLCI();

		@Mandatory
		short SIOCADDMULTI();

		@Mandatory
		short SIOCADDRT();

		@Mandatory
		short SIOCDARP();

		@Mandatory
		short SIOCDELDLCI();

		@Mandatory
		short SIOCDELMULTI();

		@Mandatory
		short SIOCDELRT();

		@Mandatory
		short SIOCDEVPRIVATE();

		@Mandatory
		short SIOCDIFADDR();

		@Mandatory
		short SIOCDRARP();

		@Mandatory
		short SIOCGARP();

		@Mandatory
		short SIOCGIFADDR();

		@Mandatory
		short SIOCGIFBR();

		@Mandatory
		short SIOCGIFBRDADDR();

		@Mandatory
		short SIOCGIFCONF();

		@Mandatory
		short SIOCGIFCOUNT();

		@Mandatory
		short SIOCGIFDSTADDR();

		@Mandatory
		short SIOCGIFENCAP();

		@Mandatory
		short SIOCGIFFLAGS();

		@Mandatory
		short SIOCGIFHWADDR();

		@Mandatory
		short SIOCGIFINDEX();

		@Mandatory
		short SIOCGIFMAP();

		@Mandatory
		short SIOCGIFMEM();

		@Mandatory
		short SIOCGIFMETRIC();

		@Mandatory
		short SIOCGIFMTU();

		@Mandatory
		short SIOCGIFNAME();

		@Mandatory
		short SIOCGIFNETMASK();

		@Mandatory
		short SIOCGIFPFLAGS();

		@Mandatory
		short SIOCGIFSLAVE();

		@Mandatory
		short SIOCGIFTXQLEN();

		@Mandatory
		short SIOCGRARP();

		@Mandatory
		short SIOCPROTOPRIVATE();

		@Mandatory
		short SIOCRTMSG();

		@Mandatory
		short SIOCSARP();

		@Mandatory
		short SIOCSIFADDR();

		@Mandatory
		short SIOCSIFBR();

		@Mandatory
		short SIOCSIFBRDADDR();

		@Mandatory
		short SIOCSIFDSTADDR();

		@Mandatory
		short SIOCSIFENCAP();

		@Mandatory
		short SIOCSIFFLAGS();

		@Mandatory
		short SIOCSIFHWADDR();

		@Mandatory
		short SIOCSIFHWBROADCAST();

		@Mandatory
		short SIOCSIFLINK();

		@Mandatory
		short SIOCSIFMAP();

		@Mandatory
		short SIOCSIFMEM();

		@Mandatory
		short SIOCSIFMETRIC();

		@Mandatory
		short SIOCSIFMTU();

		@Mandatory
		short SIOCSIFNAME();

		@Mandatory
		short SIOCSIFNETMASK();

		@Mandatory
		short SIOCSIFPFLAGS();

		@Mandatory
		short SIOCSIFSLAVE();

		@Mandatory
		short SIOCSIFTXQLEN();

		@Mandatory
		short SIOCSRARP();

		@Mandatory
		short SIOGIFINDEX();

		@Mandatory
		short TCFLSH();

		@Mandatory
		short TCGETA();

		@Mandatory
		short TCGETS();

		@Optional
		Short TCGETS2();

		@Mandatory
		short TCGETX();

		@Mandatory
		short TCSBRK();

		@Mandatory
		short TCSBRKP();

		@Mandatory
		short TCSETA();

		@Mandatory
		short TCSETAF();

		@Mandatory
		short TCSETAW();

		@Mandatory
		short TCSETS();

		@Optional
		Short TCSETS2();

		@Mandatory
		short TCSETSF();

		@Optional
		Short TCSETSF2();

		@Optional
		Short TCSETSW();

		@Optional
		Short TCSETSW2();

		@Mandatory
		short TCSETX();

		@Mandatory
		short TCSETXF();

		@Mandatory
		short TCSETXW();

		@Mandatory
		short TCXONC();

		@Mandatory
		short TIOCCBRK();

		@Mandatory
		short TIOCCONS();

		@Mandatory
		short TIOCEXCL();

		@Mandatory
		short TIOCGDEV();

		@Mandatory
		short TIOCGETD();

		@Mandatory
		short TIOCGEXCL();

		@Mandatory
		short TIOCGICOUNT();

		@Mandatory
		short TIOCGLCKTRMIOS();

		@Mandatory
		short TIOCGPGRP();

		@Mandatory
		short TIOCGPKT();

		@Mandatory
		short TIOCGPTLCK();

		@Mandatory
		short TIOCGPTN();

		@Mandatory
		short TIOCGPTPEER();

		@Mandatory
		short TIOCGRS485();

		@Mandatory
		short TIOCGSERIAL();

		@Mandatory
		short TIOCGSID();

		@Mandatory
		short TIOCGSOFTCAR();

		@Mandatory
		short TIOCGWINSZ();

		@Mandatory
		short TIOCINQ();

		@Optional
		Short TIOCLINUX();

		@Mandatory
		short TIOCMBIC();

		@Mandatory
		short TIOCMBIS();

		@Mandatory
		short TIOCMGET();

		@Mandatory
		short TIOCMIWAIT();

		@Mandatory
		short TIOCMSET();

		@Mandatory
		short TIOCNOTTY();

		@Mandatory
		short TIOCNXCL();

		@Mandatory
		short TIOCOUTQ();

		@Mandatory
		short TIOCPKT();

		@Mandatory
		short TIOCPKT_DATA();

		@Mandatory
		short TIOCPKT_DOSTOP();

		@Mandatory
		short TIOCPKT_FLUSHREAD();

		@Mandatory
		short TIOCPKT_FLUSHWRITE();

		@Mandatory
		short TIOCPKT_IOCTL();

		@Mandatory
		short TIOCPKT_NOSTOP();

		@Mandatory
		short TIOCPKT_START();

		@Mandatory
		short TIOCPKT_STOP();

		@Mandatory
		short TIOCSBRK();

		@Mandatory
		short TIOCSCTTY();

		@Mandatory
		short TIOCSER_TEMT();

		@Mandatory
		short TIOCSERCONFIG();

		@Mandatory
		short TIOCSERGETLSR();

		@Mandatory
		short TIOCSERGETMULTI();

		@Mandatory
		short TIOCSERGSTRUCT();

		@Mandatory
		short TIOCSERGWILD();

		@Mandatory
		short TIOCSERSETMULTI();

		@Mandatory
		short TIOCSERSWILD();

		@Mandatory
		short TIOCSETD();

		@Mandatory
		short TIOCSIG();

		@Mandatory
		short TIOCSLCKTRMIOS();

		@Mandatory
		short TIOCSPGRP();

		@Mandatory
		short TIOCSPTLCK();

		@Mandatory
		short TIOCSRS485();

		@Mandatory
		short TIOCSSERIAL();

		@Mandatory
		short TIOCSSOFTCAR();

		@Mandatory
		short TIOCSTI();

		@Mandatory
		short TIOCSWINSZ();

		@Mandatory
		short TIOCVHANGUP();
	}

	final public short TCGETS;
	final public short TCSETS;
	final public short TCSETSW;
	final public short TCSETSF;
	final public short TCGETA;
	final public short TCSETA;
	final public short TCSETAW;
	final public short TCSETAF;
	final public short TCSBRK;
	final public short TCXONC;
	final public short TCFLSH;
	final public short TIOCEXCL;
	final public short TIOCNXCL;
	final public short TIOCSCTTY;
	final public short TIOCGPGRP;
	final public short TIOCSPGRP;
	final public short TIOCOUTQ;
	final public short TIOCSTI;
	final public short TIOCGWINSZ;
	final public short TIOCSWINSZ;
	final public short TIOCMGET;
	final public short TIOCMBIS;
	final public short TIOCMBIC;
	final public short TIOCMSET;
	final public short TIOCGSOFTCAR;
	final public short TIOCSSOFTCAR;
	final public short FIONREAD;
	final public short TIOCINQ;
	final public Short TIOCLINUX;
	final public short TIOCCONS;
	final public short TIOCGSERIAL;
	final public short TIOCSSERIAL;
	final public short TIOCPKT;
	final public short FIONBIO;
	final public short TIOCNOTTY;
	final public short TIOCSETD;
	final public short TIOCGETD;
	final public short TCSBRKP;
	final public short TIOCSBRK;
	final public short TIOCCBRK;
	final public short TIOCGSID;
	final public Short TCGETS2;
	final public Short TCSETS2;
	final public Short TCSETSW2;
	final public Short TCSETSF2;
	final public short TIOCGRS485;
	final public short TIOCSRS485;
	final public short TIOCGPTN;
	final public short TIOCSPTLCK;
	final public short TIOCGDEV;
	final public short TCGETX;
	final public short TCSETX;
	final public short TCSETXF;
	final public short TCSETXW;
	final public short TIOCSIG;
	final public short TIOCVHANGUP;
	final public short TIOCGPKT;
	final public short TIOCGPTLCK;
	final public short TIOCGEXCL;
	final public short TIOCGPTPEER;
	final public short FIONCLEX;
	final public short FIOCLEX;
	final public short FIOASYNC;
	final public short TIOCSERCONFIG;
	final public short TIOCSERGWILD;
	final public short TIOCSERSWILD;
	final public short TIOCGLCKTRMIOS;
	final public short TIOCSLCKTRMIOS;
	final public short TIOCSERGSTRUCT;
	final public short TIOCSERGETLSR;
	final public short TIOCSERGETMULTI;
	final public short TIOCSERSETMULTI;
	final public short TIOCMIWAIT;
	final public short TIOCGICOUNT;
	final public short FIOQSIZE;
	final public short TIOCPKT_DATA;
	final public short TIOCPKT_FLUSHREAD;
	final public short TIOCPKT_FLUSHWRITE;
	final public short TIOCPKT_STOP;
	final public short TIOCPKT_START;
	final public short TIOCPKT_NOSTOP;
	final public short TIOCPKT_DOSTOP;
	final public short TIOCPKT_IOCTL;
	final public short TIOCSER_TEMT;
	final public short SIOCADDRT;
	final public short SIOCDELRT;
	final public short SIOCRTMSG;
	final public short SIOCGIFNAME;
	final public short SIOCSIFLINK;
	final public short SIOCGIFCONF;
	final public short SIOCGIFFLAGS;
	final public short SIOCSIFFLAGS;
	final public short SIOCGIFADDR;
	final public short SIOCSIFADDR;
	final public short SIOCGIFDSTADDR;
	final public short SIOCSIFDSTADDR;
	final public short SIOCGIFBRDADDR;
	final public short SIOCSIFBRDADDR;
	final public short SIOCGIFNETMASK;
	final public short SIOCSIFNETMASK;
	final public short SIOCGIFMETRIC;
	final public short SIOCSIFMETRIC;
	final public short SIOCGIFMEM;
	final public short SIOCSIFMEM;
	final public short SIOCGIFMTU;
	final public short SIOCSIFMTU;
	final public short SIOCSIFNAME;
	final public short SIOCSIFHWADDR;
	final public short SIOCGIFENCAP;
	final public short SIOCSIFENCAP;
	final public short SIOCGIFHWADDR;
	final public short SIOCGIFSLAVE;
	final public short SIOCSIFSLAVE;
	final public short SIOCADDMULTI;
	final public short SIOCDELMULTI;
	final public short SIOCGIFINDEX;
	final public short SIOGIFINDEX;
	final public short SIOCSIFPFLAGS;
	final public short SIOCGIFPFLAGS;
	final public short SIOCDIFADDR;
	final public short SIOCSIFHWBROADCAST;
	final public short SIOCGIFCOUNT;
	final public short SIOCGIFBR;
	final public short SIOCSIFBR;
	final public short SIOCGIFTXQLEN;
	final public short SIOCSIFTXQLEN;
	final public short SIOCDARP;
	final public short SIOCGARP;
	final public short SIOCSARP;
	final public short SIOCDRARP;
	final public short SIOCGRARP;
	final public short SIOCSRARP;
	final public short SIOCGIFMAP;
	final public short SIOCSIFMAP;
	final public short SIOCADDDLCI;
	final public short SIOCDELDLCI;
	final public short SIOCDEVPRIVATE;
	final public short SIOCPROTOPRIVATE;

	final private NativeFunctions nativeFunctions;

	protected Sys_Ioctl_H() {
		this.nativeFunctions = LibraryLoader.create(NativeFunctions.class).load("c");
		final Sys_IoctlDefines sys_ioctlDefines = JnrDefines.getInstance(Sys_IoctlDefines.class);
		TCGETS = sys_ioctlDefines.TCGETS();
		TCSETS = sys_ioctlDefines.TCSETS();
		TCSETSW = sys_ioctlDefines.TCSETSW();
		TCSETSF = sys_ioctlDefines.TCSETSF();
		TCGETA = sys_ioctlDefines.TCGETA();
		TCSETA = sys_ioctlDefines.TCSETA();
		TCSETAW = sys_ioctlDefines.TCSETAW();
		TCSETAF = sys_ioctlDefines.TCSETAF();
		TCSBRK = sys_ioctlDefines.TCSBRK();
		TCXONC = sys_ioctlDefines.TCXONC();
		TCFLSH = sys_ioctlDefines.TCFLSH();
		TIOCEXCL = sys_ioctlDefines.TIOCEXCL();
		TIOCNXCL = sys_ioctlDefines.TIOCNXCL();
		TIOCSCTTY = sys_ioctlDefines.TIOCSCTTY();
		TIOCGPGRP = sys_ioctlDefines.TIOCGPGRP();
		TIOCSPGRP = sys_ioctlDefines.TIOCSPGRP();
		TIOCOUTQ = sys_ioctlDefines.TIOCOUTQ();
		TIOCSTI = sys_ioctlDefines.TIOCSTI();
		TIOCGWINSZ = sys_ioctlDefines.TIOCGWINSZ();
		TIOCSWINSZ = sys_ioctlDefines.TIOCSWINSZ();
		TIOCMGET = sys_ioctlDefines.TIOCMGET();
		TIOCMBIS = sys_ioctlDefines.TIOCMBIS();
		TIOCMBIC = sys_ioctlDefines.TIOCMBIC();
		TIOCMSET = sys_ioctlDefines.TIOCMSET();
		TIOCGSOFTCAR = sys_ioctlDefines.TIOCGSOFTCAR();
		TIOCSSOFTCAR = sys_ioctlDefines.TIOCSSOFTCAR();
		FIONREAD = sys_ioctlDefines.FIONREAD();
		TIOCINQ = sys_ioctlDefines.TIOCINQ();
		TIOCLINUX = sys_ioctlDefines.TIOCLINUX();
		TIOCCONS = sys_ioctlDefines.TIOCCONS();
		TIOCGSERIAL = sys_ioctlDefines.TIOCGSERIAL();
		TIOCSSERIAL = sys_ioctlDefines.TIOCSSERIAL();
		TIOCPKT = sys_ioctlDefines.TIOCPKT();
		FIONBIO = sys_ioctlDefines.FIONBIO();
		TIOCNOTTY = sys_ioctlDefines.TIOCNOTTY();
		TIOCSETD = sys_ioctlDefines.TIOCSETD();
		TIOCGETD = sys_ioctlDefines.TIOCGETD();
		TCSBRKP = sys_ioctlDefines.TCSBRKP();
		TIOCSBRK = sys_ioctlDefines.TIOCSBRK();
		TIOCCBRK = sys_ioctlDefines.TIOCCBRK();
		TIOCGSID = sys_ioctlDefines.TIOCGSID();
		TCGETS2 = sys_ioctlDefines.TCGETS2();
		TCSETS2 = sys_ioctlDefines.TCSETS2();
		TCSETSW2 = sys_ioctlDefines.TCSETSW2();
		TCSETSF2 = sys_ioctlDefines.TCSETSF2();
		TIOCGRS485 = sys_ioctlDefines.TIOCGRS485();
		TIOCSRS485 = sys_ioctlDefines.TIOCSRS485();
		TIOCGPTN = sys_ioctlDefines.TIOCGPTN();
		TIOCSPTLCK = sys_ioctlDefines.TIOCSPTLCK();
		TIOCGDEV = sys_ioctlDefines.TIOCGDEV();
		TCGETX = sys_ioctlDefines.TCGETX();
		TCSETX = sys_ioctlDefines.TCSETX();
		TCSETXF = sys_ioctlDefines.TCSETXF();
		TCSETXW = sys_ioctlDefines.TCSETXW();
		TIOCSIG = sys_ioctlDefines.TIOCSIG();
		TIOCVHANGUP = sys_ioctlDefines.TIOCVHANGUP();
		TIOCGPKT = sys_ioctlDefines.TIOCGPKT();
		TIOCGPTLCK = sys_ioctlDefines.TIOCGPTLCK();
		TIOCGEXCL = sys_ioctlDefines.TIOCGEXCL();
		TIOCGPTPEER = sys_ioctlDefines.TIOCGPTPEER();
		FIONCLEX = sys_ioctlDefines.FIONCLEX();
		FIOCLEX = sys_ioctlDefines.FIOCLEX();
		FIOASYNC = sys_ioctlDefines.FIOASYNC();
		TIOCSERCONFIG = sys_ioctlDefines.TIOCSERCONFIG();
		TIOCSERGWILD = sys_ioctlDefines.TIOCSERGWILD();
		TIOCSERSWILD = sys_ioctlDefines.TIOCSERSWILD();
		TIOCGLCKTRMIOS = sys_ioctlDefines.TIOCGLCKTRMIOS();
		TIOCSLCKTRMIOS = sys_ioctlDefines.TIOCSLCKTRMIOS();
		TIOCSERGSTRUCT = sys_ioctlDefines.TIOCSERGSTRUCT();
		TIOCSERGETLSR = sys_ioctlDefines.TIOCSERGETLSR();
		TIOCSERGETMULTI = sys_ioctlDefines.TIOCSERGETMULTI();
		TIOCSERSETMULTI = sys_ioctlDefines.TIOCSERSETMULTI();
		TIOCMIWAIT = sys_ioctlDefines.TIOCMIWAIT();
		TIOCGICOUNT = sys_ioctlDefines.TIOCGICOUNT();
		FIOQSIZE = sys_ioctlDefines.FIOQSIZE();
		TIOCPKT_DATA = sys_ioctlDefines.TIOCPKT_DATA();
		TIOCPKT_FLUSHREAD = sys_ioctlDefines.TIOCPKT_FLUSHREAD();
		TIOCPKT_FLUSHWRITE = sys_ioctlDefines.TIOCPKT_FLUSHWRITE();
		TIOCPKT_STOP = sys_ioctlDefines.TIOCPKT_STOP();
		TIOCPKT_START = sys_ioctlDefines.TIOCPKT_START();
		TIOCPKT_NOSTOP = sys_ioctlDefines.TIOCPKT_NOSTOP();
		TIOCPKT_DOSTOP = sys_ioctlDefines.TIOCPKT_DOSTOP();
		TIOCPKT_IOCTL = sys_ioctlDefines.TIOCPKT_IOCTL();
		TIOCSER_TEMT = sys_ioctlDefines.TIOCSER_TEMT();
		SIOCADDRT = sys_ioctlDefines.SIOCADDRT();
		SIOCDELRT = sys_ioctlDefines.SIOCDELRT();
		SIOCRTMSG = sys_ioctlDefines.SIOCRTMSG();
		SIOCGIFNAME = sys_ioctlDefines.SIOCGIFNAME();
		SIOCSIFLINK = sys_ioctlDefines.SIOCSIFLINK();
		SIOCGIFCONF = sys_ioctlDefines.SIOCGIFCONF();
		SIOCGIFFLAGS = sys_ioctlDefines.SIOCGIFFLAGS();
		SIOCSIFFLAGS = sys_ioctlDefines.SIOCSIFFLAGS();
		SIOCGIFADDR = sys_ioctlDefines.SIOCGIFADDR();
		SIOCSIFADDR = sys_ioctlDefines.SIOCSIFADDR();
		SIOCGIFDSTADDR = sys_ioctlDefines.SIOCGIFDSTADDR();
		SIOCSIFDSTADDR = sys_ioctlDefines.SIOCSIFDSTADDR();
		SIOCGIFBRDADDR = sys_ioctlDefines.SIOCGIFBRDADDR();
		SIOCSIFBRDADDR = sys_ioctlDefines.SIOCSIFBRDADDR();
		SIOCGIFNETMASK = sys_ioctlDefines.SIOCGIFNETMASK();
		SIOCSIFNETMASK = sys_ioctlDefines.SIOCSIFNETMASK();
		SIOCGIFMETRIC = sys_ioctlDefines.SIOCGIFMETRIC();
		SIOCSIFMETRIC = sys_ioctlDefines.SIOCSIFMETRIC();
		SIOCGIFMEM = sys_ioctlDefines.SIOCGIFMEM();
		SIOCSIFMEM = sys_ioctlDefines.SIOCSIFMEM();
		SIOCGIFMTU = sys_ioctlDefines.SIOCGIFMTU();
		SIOCSIFMTU = sys_ioctlDefines.SIOCSIFMTU();
		SIOCSIFNAME = sys_ioctlDefines.SIOCSIFNAME();
		SIOCSIFHWADDR = sys_ioctlDefines.SIOCSIFHWADDR();
		SIOCGIFENCAP = sys_ioctlDefines.SIOCGIFENCAP();
		SIOCSIFENCAP = sys_ioctlDefines.SIOCSIFENCAP();
		SIOCGIFHWADDR = sys_ioctlDefines.SIOCGIFHWADDR();
		SIOCGIFSLAVE = sys_ioctlDefines.SIOCGIFSLAVE();
		SIOCSIFSLAVE = sys_ioctlDefines.SIOCSIFSLAVE();
		SIOCADDMULTI = sys_ioctlDefines.SIOCADDMULTI();
		SIOCDELMULTI = sys_ioctlDefines.SIOCDELMULTI();
		SIOCGIFINDEX = sys_ioctlDefines.SIOCGIFINDEX();
		SIOGIFINDEX = sys_ioctlDefines.SIOGIFINDEX();
		SIOCSIFPFLAGS = sys_ioctlDefines.SIOCSIFPFLAGS();
		SIOCGIFPFLAGS = sys_ioctlDefines.SIOCGIFPFLAGS();
		SIOCDIFADDR = sys_ioctlDefines.SIOCDIFADDR();
		SIOCSIFHWBROADCAST = sys_ioctlDefines.SIOCSIFHWBROADCAST();
		SIOCGIFCOUNT = sys_ioctlDefines.SIOCGIFCOUNT();
		SIOCGIFBR = sys_ioctlDefines.SIOCGIFBR();
		SIOCSIFBR = sys_ioctlDefines.SIOCSIFBR();
		SIOCGIFTXQLEN = sys_ioctlDefines.SIOCGIFTXQLEN();
		SIOCSIFTXQLEN = sys_ioctlDefines.SIOCSIFTXQLEN();
		SIOCDARP = sys_ioctlDefines.SIOCDARP();
		SIOCGARP = sys_ioctlDefines.SIOCGARP();
		SIOCSARP = sys_ioctlDefines.SIOCSARP();
		SIOCDRARP = sys_ioctlDefines.SIOCDRARP();
		SIOCGRARP = sys_ioctlDefines.SIOCGRARP();
		SIOCSRARP = sys_ioctlDefines.SIOCSRARP();
		SIOCGIFMAP = sys_ioctlDefines.SIOCGIFMAP();
		SIOCSIFMAP = sys_ioctlDefines.SIOCSIFMAP();
		SIOCADDDLCI = sys_ioctlDefines.SIOCADDDLCI();
		SIOCDELDLCI = sys_ioctlDefines.SIOCDELDLCI();
		SIOCDEVPRIVATE = sys_ioctlDefines.SIOCDEVPRIVATE();
		SIOCPROTOPRIVATE = sys_ioctlDefines.SIOCPROTOPRIVATE();
	}

	public int ioctl(int fd, long request) {
		return nativeFunctions.ioctl(fd, request);
	}

}
