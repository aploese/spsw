package de.ibapl.jnrheader.posix;

import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.POSIX;
import de.ibapl.jnrheader.Wrapper;

@Wrapper("errno.h")
public abstract class Errno_H implements JnrHeader {

	@POSIX()
	public final int EPERM = EPERM();

	@POSIX()
	public final int ENOENT = ENOENT();

	@POSIX()
	public final int ESRCH = ESRCH();

	@POSIX()
	public final int EINTR = EINTR();

	@POSIX()
	public final int EIO = EIO();

	@POSIX()
	public final int ENXIO = ENXIO();

	@POSIX()
	public final int E2BIG = E2BIG();

	@POSIX()
	public final int ENOEXEC = ENOEXEC();

	@POSIX()
	public final int EBADF = EBADF();

	@POSIX()
	public final int ECHILD = ECHILD();

	@POSIX()
	public final int EAGAIN = EAGAIN();

	@POSIX()
	public final int ENOMEM = ENOMEM();

	@POSIX()
	public final int EACCES = EACCES();

	@POSIX()
	public final int EFAULT = EFAULT();

	@POSIX()
	public final int ENOTBLK = ENOTBLK();

	@POSIX()
	public final int EBUSY = EBUSY();

	@POSIX()
	public final int EEXIST = EEXIST();

	@POSIX()
	public final int EXDEV = EXDEV();

	@POSIX()
	public final int ENODEV = ENODEV();

	@POSIX()
	public final int ENOTDIR = ENOTDIR();

	@POSIX()
	public final int EISDIR = EISDIR();

	@POSIX()
	public final int EINVAL = EINVAL();

	@POSIX()
	public final int ENFILE = ENFILE();

	@POSIX()
	public final int EMFILE = EMFILE();

	@POSIX()
	public final int ENOTTY = ENOTTY();

	@POSIX()
	public final int ETXTBSY = ETXTBSY();

	@POSIX()
	public final int EFBIG = EFBIG();

	@POSIX()
	public final int ENOSPC = ENOSPC();

	@POSIX()
	public final int ESPIPE = ESPIPE();

	@POSIX()
	public final int EROFS = EROFS();

	@POSIX()
	public final int EMLINK = EMLINK();

	@POSIX()
	public final int EPIPE = EPIPE();

	@POSIX()
	public final int EDOM = EDOM();

	@POSIX()
	public final int ERANGE = ERANGE();

	@POSIX()
	public final int EDEADLK = EDEADLK();

	@POSIX()
	public final int ENAMETOOLONG = ENAMETOOLONG();

	@POSIX()
	public final int ENOLCK = ENOLCK();

	@POSIX()
	public final int ENOSYS = ENOSYS();

	@POSIX()
	public final int ENOTEMPTY = ENOTEMPTY();

	@POSIX()
	public final int ELOOP = ELOOP();

	@POSIX()
	public final int EWOULDBLOCK = EWOULDBLOCK();

	@POSIX()
	public final int ENOMSG = ENOMSG();

	@POSIX()
	public final int EIDRM = EIDRM();

	@POSIX()
	public final int ECHRNG = ECHRNG();

	@POSIX()
	public final int EL2NSYNC = EL2NSYNC();

	@POSIX()
	public final int EL3HLT = EL3HLT();

	@POSIX()
	public final int EL3RST = EL3RST();

	@POSIX()
	public final int ELNRNG = ELNRNG();

	@POSIX()
	public final int EUNATCH = EUNATCH();

	@POSIX()
	public final int ENOCSI = ENOCSI();

	@POSIX()
	public final int EL2HLT = EL2HLT();

	@POSIX()
	public final int EBADE = EBADE();

	@POSIX()
	public final int EBADR = EBADR();

	@POSIX()
	public final int EXFULL = EXFULL();

	@POSIX()
	public final int ENOANO = ENOANO();

	@POSIX()
	public final int EBADRQC = EBADRQC();

	@POSIX()
	public final int EBADSLT = EBADSLT();

	@POSIX()
	public final int EDEADLOCK = EDEADLOCK();

	@POSIX()
	public final int EBFONT = EBFONT();

	@POSIX()
	public final int ENOSTR = ENOSTR();

	@POSIX()
	public final int ENODATA = ENODATA();

	@POSIX()
	public final int ETIME = ETIME();

	@POSIX()
	public final int ENOSR = ENOSR();

	@POSIX()
	public final int ENONET = ENONET();

	@POSIX()
	public final int ENOPKG = ENOPKG();

	@POSIX()
	public final int EREMOTE = EREMOTE();

	@POSIX()
	public final int ENOLINK = ENOLINK();

	@POSIX()
	public final int EADV = EADV();

	@POSIX()
	public final int ESRMNT = ESRMNT();

	@POSIX()
	public final int ECOMM = ECOMM();

	@POSIX()
	public final int EPROTO = EPROTO();

	@POSIX()
	public final int EMULTIHOP = EMULTIHOP();

	@POSIX()
	public final int EDOTDOT = EDOTDOT();

	@POSIX()
	public final int EBADMSG = EBADMSG();

	@POSIX()
	public final int EOVERFLOW = EOVERFLOW();

	@POSIX()
	public final int ENOTUNIQ = ENOTUNIQ();

	@POSIX()
	public final int EBADFD = EBADFD();

	@POSIX()
	public final int EREMCHG = EREMCHG();

	@POSIX()
	public final int ELIBACC = ELIBACC();

	@POSIX()
	public final int ELIBBAD = ELIBBAD();

	@POSIX()
	public final int ELIBSCN = ELIBSCN();

	@POSIX()
	public final int ELIBMAX = ELIBMAX();

	@POSIX()
	public final int ELIBEXEC = ELIBEXEC();

	@POSIX()
	public final int EILSEQ = EILSEQ();

	@POSIX()
	public final int ERESTART = ERESTART();

	@POSIX()
	public final int ESTRPIPE = ESTRPIPE();

	@POSIX()
	public final int EUSERS = EUSERS();

	@POSIX()
	public final int ENOTSOCK = ENOTSOCK();

	@POSIX()
	public final int EDESTADDRREQ = EDESTADDRREQ();

	@POSIX()
	public final int EMSGSIZE = EMSGSIZE();

	@POSIX()
	public final int EPROTOTYPE = EPROTOTYPE();

	@POSIX()
	public final int ENOPROTOOPT = ENOPROTOOPT();

	@POSIX()
	public final int EPROTONOSUPPORT = EPROTONOSUPPORT();

	@POSIX()
	public final int ESOCKTNOSUPPORT = ESOCKTNOSUPPORT();

	@POSIX()
	public final int EOPNOTSUPP = EOPNOTSUPP();

	@POSIX()
	public final int EPFNOSUPPORT = EPFNOSUPPORT();

	@POSIX()
	public final int EAFNOSUPPORT = EAFNOSUPPORT();

	@POSIX()
	public final int EADDRINUSE = EADDRINUSE();

	@POSIX()
	public final int EADDRNOTAVAIL = EADDRNOTAVAIL();

	@POSIX()
	public final int ENETDOWN = ENETDOWN();

	@POSIX()
	public final int ENETUNREACH = ENETUNREACH();

	@POSIX()
	public final int ENETRESET = ENETRESET();

	@POSIX()
	public final int ECONNABORTED = ECONNABORTED();

	@POSIX()
	public final int ECONNRESET = ECONNRESET();

	@POSIX()
	public final int ENOBUFS = ENOBUFS();

	@POSIX()
	public final int EISCONN = EISCONN();

	@POSIX()
	public final int ENOTCONN = ENOTCONN();

	@POSIX()
	public final int ESHUTDOWN = ESHUTDOWN();

	@POSIX()
	public final int ETOOMANYREFS = ETOOMANYREFS();

	@POSIX()
	public final int ETIMEDOUT = ETIMEDOUT();

	@POSIX()
	public final int ECONNREFUSED = ECONNREFUSED();

	@POSIX()
	public final int EHOSTDOWN = EHOSTDOWN();

	@POSIX()
	public final int EHOSTUNREACH = EHOSTUNREACH();

	@POSIX()
	public final int EALREADY = EALREADY();

	@POSIX()
	public final int EINPROGRESS = EINPROGRESS();

	@POSIX()
	public final int ESTALE = ESTALE();

	@POSIX()
	public final int EUCLEAN = EUCLEAN();

	@POSIX()
	public final int ENOTNAM = ENOTNAM();

	@POSIX()
	public final int ENAVAIL = ENAVAIL();

	@POSIX()
	public final int EISNAM = EISNAM();

	@POSIX()
	public final int EREMOTEIO = EREMOTEIO();

	@POSIX()
	public final int EDQUOT = EDQUOT();

	@POSIX()
	public final int ENOMEDIUM = ENOMEDIUM();

	@POSIX()
	public final int EMEDIUMTYPE = EMEDIUMTYPE();

	@POSIX()
	public final int ECANCELED = ECANCELED();

	@POSIX()
	public final int ENOKEY = ENOKEY();

	@POSIX()
	public final int EKEYEXPIRED = EKEYEXPIRED();

	@POSIX()
	public final int EKEYREVOKED = EKEYREVOKED();

	@POSIX()
	public final int EKEYREJECTED = EKEYREJECTED();

	@POSIX()
	public final int EOWNERDEAD = EOWNERDEAD();

	@POSIX()
	public final int ENOTRECOVERABLE = ENOTRECOVERABLE();

	@POSIX()
	public final int ERFKILL = ERFKILL();

	@POSIX()
	public final int EHWPOISON = EHWPOISON();

	@POSIX()
	public final int ENOTSUP = ENOTSUP();

	protected abstract int E2BIG();

	protected abstract int EACCES();

	protected abstract int EADDRINUSE();

	protected abstract int EADDRNOTAVAIL();

	protected abstract int EADV();

	protected abstract int EAFNOSUPPORT();

	protected abstract int EAGAIN();

	protected abstract int EALREADY();

	protected abstract int EBADE();

	protected abstract int EBADF();

	protected abstract int EBADFD();

	protected abstract int EBADMSG();

	protected abstract int EBADR();

	protected abstract int EBADRQC();

	protected abstract int EBADSLT();

	protected abstract int EBFONT();

	protected abstract int EBUSY();

	protected abstract int ECANCELED();

	protected abstract int ECHILD();

	protected abstract int ECHRNG();

	protected abstract int ECOMM();

	protected abstract int ECONNABORTED();

	protected abstract int ECONNREFUSED();

	protected abstract int ECONNRESET();

	protected abstract int EDEADLK();

	protected abstract int EDEADLOCK();

	protected abstract int EDESTADDRREQ();

	protected abstract int EDOM();

	protected abstract int EDOTDOT();

	protected abstract int EDQUOT();

	protected abstract int EEXIST();

	protected abstract int EFAULT();

	protected abstract int EFBIG();

	protected abstract int EHOSTDOWN();

	protected abstract int EHOSTUNREACH();

	protected abstract int EHWPOISON();

	protected abstract int EIDRM();

	protected abstract int EILSEQ();

	protected abstract int EINPROGRESS();

	protected abstract int EINTR();

	protected abstract int EINVAL();

	protected abstract int EIO();

	protected abstract int EISCONN();

	protected abstract int EISDIR();

	protected abstract int EISNAM();

	protected abstract int EKEYEXPIRED();

	protected abstract int EKEYREJECTED();

	protected abstract int EKEYREVOKED();

	protected abstract int EL2HLT();

	protected abstract int EL2NSYNC();

	protected abstract int EL3HLT();

	protected abstract int EL3RST();

	protected abstract int ELIBACC();

	protected abstract int ELIBBAD();

	protected abstract int ELIBEXEC();

	protected abstract int ELIBMAX();

	protected abstract int ELIBSCN();

	protected abstract int ELNRNG();

	protected abstract int ELOOP();

	protected abstract int EMEDIUMTYPE();

	protected abstract int EMFILE();

	protected abstract int EMLINK();

	protected abstract int EMSGSIZE();

	protected abstract int EMULTIHOP();

	protected abstract int ENAMETOOLONG();

	protected abstract int ENAVAIL();

	protected abstract int ENETDOWN();

	protected abstract int ENETRESET();

	protected abstract int ENETUNREACH();

	protected abstract int ENFILE();

	protected abstract int ENOANO();

	protected abstract int ENOBUFS();

	protected abstract int ENOCSI();

	protected abstract int ENODATA();

	protected abstract int ENODEV();

	protected abstract int ENOENT();

	protected abstract int ENOEXEC();

	protected abstract int ENOKEY();

	protected abstract int ENOLCK();

	protected abstract int ENOLINK();

	protected abstract int ENOMEDIUM();

	protected abstract int ENOMEM();

	protected abstract int ENOMSG();

	protected abstract int ENONET();

	protected abstract int ENOPKG();

	protected abstract int ENOPROTOOPT();

	protected abstract int ENOSPC();

	protected abstract int ENOSR();

	protected abstract int ENOSTR();

	protected abstract int ENOSYS();

	protected abstract int ENOTBLK();

	protected abstract int ENOTCONN();

	protected abstract int ENOTDIR();

	protected abstract int ENOTEMPTY();

	protected abstract int ENOTNAM();

	protected abstract int ENOTRECOVERABLE();

	protected abstract int ENOTSOCK();

	protected abstract int ENOTSUP();

	protected abstract int ENOTTY();

	protected abstract int ENOTUNIQ();

	protected abstract int ENXIO();

	protected abstract int EOPNOTSUPP();

	protected abstract int EOVERFLOW();

	protected abstract int EOWNERDEAD();

	protected abstract int EPERM();

	protected abstract int EPFNOSUPPORT();

	protected abstract int EPIPE();

	protected abstract int EPROTO();

	protected abstract int EPROTONOSUPPORT();

	protected abstract int EPROTOTYPE();

	protected abstract int ERANGE();

	protected abstract int EREMCHG();

	protected abstract int EREMOTE();

	protected abstract int EREMOTEIO();

	protected abstract int ERESTART();

	protected abstract int ERFKILL();

	protected abstract int EROFS();

	public abstract int errno();

    public abstract void errno(int value);
    
	protected abstract int ESHUTDOWN();

	protected abstract int ESOCKTNOSUPPORT();

	protected abstract int ESPIPE();

	protected abstract int ESRCH();

	protected abstract int ESRMNT();

	protected abstract int ESTALE();

	protected abstract int ESTRPIPE();

	protected abstract int ETIME();

	protected abstract int ETIMEDOUT();

	protected abstract int ETOOMANYREFS();

	protected abstract int ETXTBSY();

	protected abstract int EUCLEAN();

	protected abstract int EUNATCH();

	protected abstract int EUSERS();

	protected abstract int EWOULDBLOCK();

	protected abstract int EXDEV();

	protected abstract int EXFULL();

}
