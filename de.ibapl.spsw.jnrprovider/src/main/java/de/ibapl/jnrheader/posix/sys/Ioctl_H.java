package de.ibapl.jnrheader.posix.sys;

import de.ibapl.jnrheader.Define;
import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.POSIX;
import de.ibapl.jnrheader.Wrapper;
import jnr.ffi.Platform;

@Wrapper("sys/ioctl.h")
public abstract class Ioctl_H implements JnrHeader {

	public final int TCGETS = TCGETS();

	public final int TCSETS = TCSETS();

	public final int TCSETSW = TCSETSW();

	public final int TCSETSF = TCSETSF();

	public final int TCGETA = TCGETA();

	public final int TCSETA = TCSETA();

	public final int TCSETAW = TCSETAW();

	public final int TCSETAF = TCSETAF();

	public final int TCSBRK = TCSBRK();

	public final int TCXONC = TCXONC();

	public final int TCFLSH = TCFLSH();

	public final int TIOCEXCL = TIOCEXCL();

	public final int TIOCNXCL = TIOCNXCL();

	public final int TIOCSCTTY = TIOCSCTTY();

	public final int TIOCGPGRP = TIOCGPGRP();

	public final int TIOCSPGRP = TIOCSPGRP();

	public final int TIOCOUTQ = TIOCOUTQ();

	public final int TIOCSTI = TIOCSTI();

	public final int TIOCGWINSZ = TIOCGWINSZ();

	public final int TIOCSWINSZ = TIOCSWINSZ();

	public final int TIOCMGET = TIOCMGET();

	public final int TIOCMBIS = TIOCMBIS();

	public final int TIOCMBIC = TIOCMBIC();

	public final int TIOCMSET = TIOCMSET();

	public final int TIOCGSOFTCAR = TIOCGSOFTCAR();

	public final int TIOCSSOFTCAR = TIOCSSOFTCAR();

	public final int FIONREAD = FIONREAD();

	public final int TIOCINQ = TIOCINQ();

	public final Short TIOCLINUX = TIOCLINUX();

	public final int TIOCCONS = TIOCCONS();

	public final int TIOCGSERIAL = TIOCGSERIAL();

	public final int TIOCSSERIAL = TIOCSSERIAL();

	public final int TIOCPKT = TIOCPKT();

	public final int FIONBIO = FIONBIO();

	public final int TIOCNOTTY = TIOCNOTTY();

	public final int TIOCSETD = TIOCSETD();

	public final int TIOCGETD = TIOCGETD();

	public final int TCSBRKP = TCSBRKP();

	public final int TIOCSBRK = TIOCSBRK();

	public final int TIOCCBRK = TIOCCBRK();

	public final int TIOCGSID = TIOCGSID();

	public final int TIOCGRS485 = TIOCGRS485();

	public final int TIOCSRS485 = TIOCSRS485();

	public final int TIOCGPTN = TIOCGPTN();

	public final int TIOCSPTLCK = TIOCSPTLCK();

	public final int TIOCGDEV = TIOCGDEV();

	public final int TCGETX = TCGETX();

	public final int TCSETX = TCSETX();

	public final int TCSETXF = TCSETXF();

	public final int TCSETXW = TCSETXW();

	public final int TIOCSIG = TIOCSIG();

	public final int TIOCVHANGUP = TIOCVHANGUP();

	public final int TIOCGPKT = TIOCGPKT();

	public final int TIOCGPTLCK = TIOCGPTLCK();

	public final int TIOCGEXCL = TIOCGEXCL();

	public final int TIOCGPTPEER = TIOCGPTPEER();

	public final int FIONCLEX = FIONCLEX();

	public final int FIOCLEX = FIOCLEX();

	public final int FIOASYNC = FIOASYNC();

	public final int TIOCSERCONFIG = TIOCSERCONFIG();

	public final int TIOCSERGWILD = TIOCSERGWILD();

	public final int TIOCSERSWILD = TIOCSERSWILD();

	public final int TIOCGLCKTRMIOS = TIOCGLCKTRMIOS();

	public final int TIOCSLCKTRMIOS = TIOCSLCKTRMIOS();

	public final int TIOCSERGSTRUCT = TIOCSERGSTRUCT();

	public final int TIOCSERGETLSR = TIOCSERGETLSR();

	public final int TIOCSERGETMULTI = TIOCSERGETMULTI();

	public final int TIOCSERSETMULTI = TIOCSERSETMULTI();

	public final int TIOCMIWAIT = TIOCMIWAIT();

	public final int TIOCGICOUNT = TIOCGICOUNT();

	public final int FIOQSIZE = FIOQSIZE();

	public final int TIOCPKT_DATA = TIOCPKT_DATA();

	public final int TIOCPKT_FLUSHREAD = TIOCPKT_FLUSHREAD();

	public final int TIOCPKT_FLUSHWRITE = TIOCPKT_FLUSHWRITE();

	public final int TIOCPKT_STOP = TIOCPKT_STOP();

	public final int TIOCPKT_START = TIOCPKT_START();

	public final int TIOCPKT_NOSTOP = TIOCPKT_NOSTOP();

	public final int TIOCPKT_DOSTOP = TIOCPKT_DOSTOP();

	public final int TIOCPKT_IOCTL = TIOCPKT_IOCTL();

	public final int TIOCSER_TEMT = TIOCSER_TEMT();

	public final int SIOCADDRT = SIOCADDRT();

	public final int SIOCDELRT = SIOCDELRT();

	public final int SIOCRTMSG = SIOCRTMSG();

	public final int SIOCGIFNAME = SIOCGIFNAME();

	public final int SIOCSIFLINK = SIOCSIFLINK();

	public final int SIOCGIFCONF = SIOCGIFCONF();

	public final int SIOCGIFFLAGS = SIOCGIFFLAGS();

	public final int SIOCSIFFLAGS = SIOCSIFFLAGS();

	public final int SIOCGIFADDR = SIOCGIFADDR();

	public final int SIOCSIFADDR = SIOCSIFADDR();

	public final int SIOCGIFDSTADDR = SIOCGIFDSTADDR();

	public final int SIOCSIFDSTADDR = SIOCSIFDSTADDR();

	public final int SIOCGIFBRDADDR = SIOCGIFBRDADDR();

	public final int SIOCSIFBRDADDR = SIOCSIFBRDADDR();

	public final int SIOCGIFNETMASK = SIOCGIFNETMASK();

	public final int SIOCSIFNETMASK = SIOCSIFNETMASK();

	public final int SIOCGIFMETRIC = SIOCGIFMETRIC();

	public final int SIOCSIFMETRIC = SIOCSIFMETRIC();

	public final int SIOCGIFMEM = SIOCGIFMEM();

	public final int SIOCSIFMEM = SIOCSIFMEM();

	public final int SIOCGIFMTU = SIOCGIFMTU();

	public final int SIOCSIFMTU = SIOCSIFMTU();

	public final int SIOCSIFNAME = SIOCSIFNAME();

	public final int SIOCSIFHWADDR = SIOCSIFHWADDR();

	public final int SIOCGIFENCAP = SIOCGIFENCAP();

	public final int SIOCSIFENCAP = SIOCSIFENCAP();

	public final int SIOCGIFHWADDR = SIOCGIFHWADDR();

	public final int SIOCGIFSLAVE = SIOCGIFSLAVE();

	public final int SIOCSIFSLAVE = SIOCSIFSLAVE();

	public final int SIOCADDMULTI = SIOCADDMULTI();

	public final int SIOCDELMULTI = SIOCDELMULTI();

	public final int SIOCGIFINDEX = SIOCGIFINDEX();

	public final int SIOGIFINDEX = SIOGIFINDEX();

	public final int SIOCSIFPFLAGS = SIOCSIFPFLAGS();

	public final int SIOCGIFPFLAGS = SIOCGIFPFLAGS();

	public final int SIOCDIFADDR = SIOCDIFADDR();

	public final int SIOCSIFHWBROADCAST = SIOCSIFHWBROADCAST();

	public final int SIOCGIFCOUNT = SIOCGIFCOUNT();

	public final int SIOCGIFBR = SIOCGIFBR();

	public final int SIOCSIFBR = SIOCSIFBR();

	public final int SIOCGIFTXQLEN = SIOCGIFTXQLEN();

	public final int SIOCSIFTXQLEN = SIOCSIFTXQLEN();

	public final int SIOCDARP = SIOCDARP();

	public final int SIOCGARP = SIOCGARP();

	public final int SIOCSARP = SIOCSARP();

	public final int SIOCDRARP = SIOCDRARP();

	public final int SIOCGRARP = SIOCGRARP();

	public final int SIOCSRARP = SIOCSRARP();

	public final int SIOCGIFMAP = SIOCGIFMAP();

	public final int SIOCSIFMAP = SIOCSIFMAP();

	public final int SIOCADDDLCI = SIOCADDDLCI();

	public final int SIOCDELDLCI = SIOCDELDLCI();

	public final int SIOCDEVPRIVATE = SIOCDEVPRIVATE();

	public final int SIOCPROTOPRIVATE = SIOCPROTOPRIVATE();

	@POSIX
	protected abstract int FIOASYNC();

	@POSIX
	protected abstract int FIOCLEX();

	@POSIX
	protected abstract int FIONBIO();

	@POSIX
	protected abstract int FIONCLEX();

	@POSIX
	protected abstract int FIONREAD();

	@POSIX
	protected abstract int FIOQSIZE();

	public abstract int ioctl(int fd, long request);

	@POSIX
	protected abstract int SIOCADDDLCI();

	@POSIX
	protected abstract int SIOCADDMULTI();

	@POSIX
	protected abstract int SIOCADDRT();

	@POSIX
	protected abstract int SIOCDARP();

	@POSIX
	protected abstract int SIOCDELDLCI();

	@POSIX
	protected abstract int SIOCDELMULTI();

	@POSIX
	protected abstract int SIOCDELRT();

	@POSIX
	protected abstract int SIOCDEVPRIVATE();

	@POSIX
	protected abstract int SIOCDIFADDR();

	@POSIX
	protected abstract int SIOCDRARP();

	@POSIX
	protected abstract int SIOCGARP();

	@POSIX
	protected abstract int SIOCGIFADDR();

	@POSIX
	protected abstract int SIOCGIFBR();

	@POSIX
	protected abstract int SIOCGIFBRDADDR();

	@POSIX
	protected abstract int SIOCGIFCONF();

	@POSIX
	protected abstract int SIOCGIFCOUNT();

	@POSIX
	protected abstract int SIOCGIFDSTADDR();

	@POSIX
	protected abstract int SIOCGIFENCAP();

	@POSIX
	protected abstract int SIOCGIFFLAGS();

	@POSIX
	protected abstract int SIOCGIFHWADDR();

	@POSIX
	protected abstract int SIOCGIFINDEX();

	@POSIX
	protected abstract int SIOCGIFMAP();

	@POSIX
	protected abstract int SIOCGIFMEM();

	@POSIX
	protected abstract int SIOCGIFMETRIC();

	@POSIX
	protected abstract int SIOCGIFMTU();

	@POSIX
	protected abstract int SIOCGIFNAME();

	@POSIX
	protected abstract int SIOCGIFNETMASK();

	@POSIX
	protected abstract int SIOCGIFPFLAGS();

	@POSIX
	protected abstract int SIOCGIFSLAVE();

	@POSIX
	protected abstract int SIOCGIFTXQLEN();

	@POSIX
	protected abstract int SIOCGRARP();

	@POSIX
	protected abstract int SIOCPROTOPRIVATE();

	@POSIX
	protected abstract int SIOCRTMSG();

	@POSIX
	protected abstract int SIOCSARP();

	@POSIX
	protected abstract int SIOCSIFADDR();

	@POSIX
	protected abstract int SIOCSIFBR();

	@POSIX
	protected abstract int SIOCSIFBRDADDR();

	@POSIX
	protected abstract int SIOCSIFDSTADDR();

	@POSIX
	protected abstract int SIOCSIFENCAP();

	@POSIX
	protected abstract int SIOCSIFFLAGS();

	@POSIX
	protected abstract int SIOCSIFHWADDR();

	@POSIX
	protected abstract int SIOCSIFHWBROADCAST();

	@POSIX
	protected abstract int SIOCSIFLINK();

	@POSIX
	protected abstract int SIOCSIFMAP();

	@POSIX
	protected abstract int SIOCSIFMEM();

	@POSIX
	protected abstract int SIOCSIFMETRIC();

	@POSIX
	protected abstract int SIOCSIFMTU();

	@POSIX
	protected abstract int SIOCSIFNAME();

	@POSIX
	protected abstract int SIOCSIFNETMASK();

	@POSIX
	protected abstract int SIOCSIFPFLAGS();

	@POSIX
	protected abstract int SIOCSIFSLAVE();

	@POSIX
	protected abstract int SIOCSIFTXQLEN();

	@POSIX
	protected abstract int SIOCSRARP();

	@POSIX
	protected abstract int SIOGIFINDEX();

	@POSIX
	protected abstract int TCFLSH();

	@POSIX
	protected abstract int TCGETA();

	@POSIX
	protected abstract int TCGETS();

	@POSIX
	protected abstract int TCGETX();

	@POSIX
	protected abstract int TCSBRK();

	@POSIX
	protected abstract int TCSBRKP();

	@POSIX
	protected abstract int TCSETA();

	@POSIX
	protected abstract int TCSETAF();

	@POSIX
	protected abstract int TCSETAW();

	@POSIX
	protected abstract int TCSETS();

	@POSIX
	protected abstract int TCSETSF();

	@Define({Platform.OS.LINUX})
	protected abstract Short TCSETSW();

	@POSIX
	protected abstract int TCSETX();

	@POSIX
	protected abstract int TCSETXF();

	@POSIX
	protected abstract int TCSETXW();

	@POSIX
	protected abstract int TCXONC();

	@POSIX
	protected abstract int TIOCCBRK();

	@POSIX
	protected abstract int TIOCCONS();

	@POSIX
	protected abstract int TIOCEXCL();

	@POSIX
	protected abstract int TIOCGDEV();

	@POSIX
	protected abstract int TIOCGETD();

	@POSIX
	protected abstract int TIOCGEXCL();

	@POSIX
	protected abstract int TIOCGICOUNT();

	@POSIX
	protected abstract int TIOCGLCKTRMIOS();

	@POSIX
	protected abstract int TIOCGPGRP();

	@POSIX
	protected abstract int TIOCGPKT();

	@POSIX
	protected abstract int TIOCGPTLCK();

	@POSIX
	protected abstract int TIOCGPTN();

	@POSIX
	protected abstract int TIOCGPTPEER();

	@POSIX
	protected abstract int TIOCGRS485();

	@POSIX
	protected abstract int TIOCGSERIAL();

	@POSIX
	protected abstract int TIOCGSID();

	@POSIX
	protected abstract int TIOCGSOFTCAR();

	@POSIX
	protected abstract int TIOCGWINSZ();

	@POSIX
	protected abstract int TIOCINQ();

	@Define({Platform.OS.LINUX})
	protected abstract Short TIOCLINUX();

	@POSIX
	protected abstract int TIOCMBIC();

	@POSIX
	protected abstract int TIOCMBIS();

	@POSIX
	protected abstract int TIOCMGET();

	@POSIX
	protected abstract int TIOCMIWAIT();

	@POSIX
	protected abstract int TIOCMSET();

	@POSIX
	protected abstract int TIOCNOTTY();

	@POSIX
	protected abstract int TIOCNXCL();

	@POSIX
	protected abstract int TIOCOUTQ();

	@POSIX
	protected abstract int TIOCPKT();

	@POSIX
	protected abstract int TIOCPKT_DATA();

	@POSIX
	protected abstract int TIOCPKT_DOSTOP();

	@POSIX
	protected abstract int TIOCPKT_FLUSHREAD();

	@POSIX
	protected abstract int TIOCPKT_FLUSHWRITE();

	@POSIX
	protected abstract int TIOCPKT_IOCTL();

	@POSIX
	protected abstract int TIOCPKT_NOSTOP();

	@POSIX
	protected abstract int TIOCPKT_START();

	@POSIX
	protected abstract int TIOCPKT_STOP();

	@POSIX
	protected abstract int TIOCSBRK();

	@POSIX
	protected abstract int TIOCSCTTY();

	@POSIX
	protected abstract int TIOCSER_TEMT();

	@POSIX
	protected abstract int TIOCSERCONFIG();

	@POSIX
	protected abstract int TIOCSERGETLSR();

	@POSIX
	protected abstract int TIOCSERGETMULTI();

	@POSIX
	protected abstract int TIOCSERGSTRUCT();

	@POSIX
	protected abstract int TIOCSERGWILD();

	@POSIX
	protected abstract int TIOCSERSETMULTI();

	@POSIX
	protected abstract int TIOCSERSWILD();

	@POSIX
	protected abstract int TIOCSETD();

	@POSIX
	protected abstract int TIOCSIG();

	@POSIX
	protected abstract int TIOCSLCKTRMIOS();

	@POSIX
	protected abstract int TIOCSPGRP();

	@POSIX
	protected abstract int TIOCSPTLCK();

	@POSIX
	protected abstract int TIOCSRS485();

	@POSIX
	protected abstract int TIOCSSERIAL();

	@POSIX
	protected abstract int TIOCSSOFTCAR();

	@POSIX
	protected abstract int TIOCSTI();

	@POSIX
	protected abstract int TIOCSWINSZ();

	@POSIX
	protected abstract int TIOCVHANGUP();

}
