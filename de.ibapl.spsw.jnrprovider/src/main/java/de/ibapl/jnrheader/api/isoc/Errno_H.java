package de.ibapl.jnrheader.api.isoc;

import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.LINUX;
import de.ibapl.jnrheader.POSIX;
import de.ibapl.jnrheader.Wrapper;

@Wrapper("errno.h")
public abstract class Errno_H implements JnrHeader {

    /**
     * Operation not permitted.
     */
	@POSIX()
	public final int EPERM = EPERM();

        /**
         * No such file or directory.
         */
	@POSIX()
	public final int ENOENT = ENOENT();

	/**
         * No such process.
         */
        @POSIX()
	public final int ESRCH = ESRCH();

	/**
         * Interrupted function.
         */
        @POSIX()
	public final int EINTR = EINTR();

        /**
         * I/O error.
         */
	@POSIX()
	public final int EIO = EIO();

	/**
         * No such device or address.
         */
        @POSIX()
	public final int ENXIO = ENXIO();

        /**
         * Argument list too long.
         */
	@POSIX()
	public final int E2BIG = E2BIG();

        /**
         * Executable file format error.
         */
	@POSIX()
	public final int ENOEXEC = ENOEXEC();

        /**
         * Bad file descriptor.
         */
	@POSIX()
	public final int EBADF = EBADF();

        /**
         * No child processes.
         */
	@POSIX()
	public final int ECHILD = ECHILD();

        /**
         * Resource unavailable, try again (may be the same value as [EWOULDBLOCK]).
         */
	@POSIX()
	public final int EAGAIN = EAGAIN();

        /**
         * Not enough space.
         */
	@POSIX()
	public final int ENOMEM = ENOMEM();

        /**
         * Permission denied.
         */
	@POSIX()
	public final int EACCES = EACCES();

	/**
         * Bad address.
         */
        @POSIX()
	public final int EFAULT = EFAULT();

	@POSIX()
	public final int ENOTBLK = ENOTBLK();

        /**
         * Device or resource busy.
         */
	@POSIX()
	public final int EBUSY = EBUSY();

        /**
         * File exists.
         */
	@POSIX()
	public final int EEXIST = EEXIST();

        /**
         * Cross-device link.
         */
	@POSIX()
	public final int EXDEV = EXDEV();

        /**
         * No such device.
         */
	@POSIX()
	public final int ENODEV = ENODEV();

	/**
         * Not a directory or a symbolic link to a directory.
         */
        @POSIX()
	public final int ENOTDIR = ENOTDIR();

        /**
         * Is a directory.
         */
	@POSIX()
	public final int EISDIR = EISDIR();

        /**
         * Invalid argument.
         */
	@POSIX()
	public final int EINVAL = EINVAL();

        /**
         * Too many files open in system.
         */
	@POSIX()
	public final int ENFILE = ENFILE();

	/**
         * File descriptor value too large.
         */
        @POSIX()
	public final int EMFILE = EMFILE();

	/**
         * Inappropriate I/O control operation.
         */
        @POSIX()
	public final int ENOTTY = ENOTTY();

        /**
         * Text file busy.
         */
	@POSIX()
	public final int ETXTBSY = ETXTBSY();

        /**
         * File too large.
         */
	@POSIX()
	public final int EFBIG = EFBIG();

        /**
         * No space left on device.
         */
	@POSIX()
	public final int ENOSPC = ENOSPC();

	/**
         * Invalid seek.
         */
        @POSIX()
	public final int ESPIPE = ESPIPE();

	/**
         * Read-only file system.
         */
        @POSIX()
	public final int EROFS = EROFS();

        /**
         * Too many links.
         */
	@POSIX()
	public final int EMLINK = EMLINK();

        /**
         * Broken pipe.
         */
	@POSIX()
	public final int EPIPE = EPIPE();

        /**
         * Mathematics argument out of domain of function.
         */
	@POSIX()
	public final int EDOM = EDOM();

	/**
         * Result too large.
         */
        @POSIX()
	public final int ERANGE = ERANGE();

        /**
         * Resource deadlock would occur.
         */
	@POSIX()
	public final int EDEADLK = EDEADLK();

        /**
         * Filename too long.
         */
	@POSIX()
	public final int ENAMETOOLONG = ENAMETOOLONG();

        /**
         * No locks available.
         */
	@POSIX()
	public final int ENOLCK = ENOLCK();

        /**
         * Functionality not supported.
         */
	@POSIX()
	public final int ENOSYS = ENOSYS();

        /**
         * Directory not empty.
         */
	@POSIX()
	public final int ENOTEMPTY = ENOTEMPTY();

        /**
         * Too many levels of symbolic links.
         */
	@POSIX()
	public final int ELOOP = ELOOP();

        /**
         * Operation would block (may be the same value as [@see EAGAIN]).
         */
	@POSIX()
	public final int EWOULDBLOCK = EWOULDBLOCK();

        /**
         * No message of the desired type.
         */
	@POSIX()
	public final int ENOMSG = ENOMSG();

	/**
         * Identifier removed.
         */
        @POSIX()
	public final int EIDRM = EIDRM();

	@LINUX()
	public final int ECHRNG = ECHRNG();

	@LINUX()
	public final int EL2NSYNC = EL2NSYNC();

	@LINUX()
	public final int EL3HLT = EL3HLT();

	@LINUX()
	public final int EL3RST = EL3RST();

	@LINUX()
	public final int ELNRNG = ELNRNG();

	@LINUX()
	public final int EUNATCH = EUNATCH();

	@LINUX()
	public final int ENOCSI = ENOCSI();

	@LINUX()
	public final int EL2HLT = EL2HLT();

	@LINUX()
	public final int EBADE = EBADE();

	@LINUX()
	public final int EBADR = EBADR();

	@LINUX()
	public final int EXFULL = EXFULL();

	@LINUX()
	public final int ENOANO = ENOANO();

	@LINUX()
	public final int EBADRQC = EBADRQC();

	@LINUX()
	public final int EBADSLT = EBADSLT();

	@LINUX()
	public final int EDEADLOCK = EDEADLOCK();

	@LINUX()
	public final int EBFONT = EBFONT();

	/**
         *  Not a STREAM. 
         */
        @POSIX(POSIX.Marker.XSI)
	public final int ENOSTR = ENOSTR();

        /**
         * No message is available on the STREAM head read queue.
         */
	@POSIX(POSIX.Marker.XSI)
	public final int ENODATA = ENODATA();

        /**
         * Stream @see ioctl_H.ioctl() timeout.
         */
	@POSIX()
	public final int ETIME = ETIME();

        /**
         * No STREAM resources.
         */
	@POSIX(POSIX.Marker.XSI)
	public final int ENOSR = ENOSR();

	@LINUX()
	public final int ENONET = ENONET();

	@LINUX()
	public final int ENOPKG = ENOPKG();

	@LINUX()
	public final int EREMOTE = EREMOTE();

        /**
         * Reserved.
         */
	@POSIX()
	public final int ENOLINK = ENOLINK();

	public final int EADV = EADV();

	@LINUX()
	public final int ESRMNT = ESRMNT();

	@LINUX()
	public final int ECOMM = ECOMM();

        /**
         * Protocol error.
         */
	@POSIX()
	public final int EPROTO = EPROTO();

        /**
         * Reserved.
         */
	@POSIX()
	public final int EMULTIHOP = EMULTIHOP();

	@LINUX()
	public final int EDOTDOT = EDOTDOT();

        /**
         * Bad message.
         */
	@POSIX()
	public final int EBADMSG = EBADMSG();

	/**
         * Value too large to be stored in data type.
         */
        @POSIX()
	public final int EOVERFLOW = EOVERFLOW();

	@LINUX()
	public final int ENOTUNIQ = ENOTUNIQ();

	@LINUX()
	public final int EBADFD = EBADFD();

	@LINUX()
	public final int EREMCHG = EREMCHG();

	@LINUX()
	public final int ELIBACC = ELIBACC();

	@LINUX()
	public final int ELIBBAD = ELIBBAD();

	@LINUX()
	public final int ELIBSCN = ELIBSCN();

	@LINUX()
	public final int ELIBMAX = ELIBMAX();

	@LINUX()
	public final int ELIBEXEC = ELIBEXEC();

        /**
         * Illegal byte sequence.
         */
	@POSIX()
	public final int EILSEQ = EILSEQ();

	@LINUX()
	public final int ERESTART = ERESTART();

	@LINUX()
	public final int ESTRPIPE = ESTRPIPE();

	@LINUX()
	public final int EUSERS = EUSERS();

	/**
         * Not a socket.
         */
        @POSIX()
	public final int ENOTSOCK = ENOTSOCK();

        /**
         * Destination address required.
         */
	@POSIX()
	public final int EDESTADDRREQ = EDESTADDRREQ();

        /**
         * Message too large.
         */
	@POSIX()
	public final int EMSGSIZE = EMSGSIZE();

	/**
         * Protocol wrong type for socket.
         */
        @POSIX()
	public final int EPROTOTYPE = EPROTOTYPE();

        /**
         * Protocol not available.
         */
	@POSIX()
	public final int ENOPROTOOPT = ENOPROTOOPT();

        /**
         * Protocol not supported.
         */
	@POSIX()
	public final int EPROTONOSUPPORT = EPROTONOSUPPORT();

	@LINUX()
	public final int ESOCKTNOSUPPORT = ESOCKTNOSUPPORT();

        /**
         * Operation not supported on socket (may be the same value as [@see ENOTSUP]).
         */
	@POSIX()
	public final int EOPNOTSUPP = EOPNOTSUPP();

	@LINUX()
	public final int EPFNOSUPPORT = EPFNOSUPPORT();

        /**
         * Address family not supported.
         */
	@POSIX()
	public final int EAFNOSUPPORT = EAFNOSUPPORT();

        /**
         * Address in use.
         */
	@POSIX()
	public final int EADDRINUSE = EADDRINUSE();

        /**
         * Address not available.
         */
	@POSIX()
	public final int EADDRNOTAVAIL = EADDRNOTAVAIL();

        /**
         * Network is down.
         */
	@POSIX()
	public final int ENETDOWN = ENETDOWN();

        /**
         * Network unreachable.
         */
	@POSIX()
	public final int ENETUNREACH = ENETUNREACH();

        /**
         * Connection aborted by network.
         */
	@POSIX()
	public final int ENETRESET = ENETRESET();

        /**
         * Connection aborted.
         */
	@POSIX()
	public final int ECONNABORTED = ECONNABORTED();

        /**
         * Connection reset.
         */
	@POSIX()
	public final int ECONNRESET = ECONNRESET();

        /**
         * No buffer space available.
         */
	@POSIX()
	public final int ENOBUFS = ENOBUFS();

        /**
         * Socket is connected.
         */
	@POSIX()
	public final int EISCONN = EISCONN();

        /**
         * The socket is not connected.
         */
	@POSIX()
	public final int ENOTCONN = ENOTCONN();

	@LINUX()
	public final int ESHUTDOWN = ESHUTDOWN();

	@LINUX()
	public final int ETOOMANYREFS = ETOOMANYREFS();

        /**
         * Connection timed out.
         */
	@POSIX(POSIX.Marker.XSI)
	public final int ETIMEDOUT = ETIMEDOUT();

        /**
         * Connection refused.
         */
	@POSIX()
	public final int ECONNREFUSED = ECONNREFUSED();

	@LINUX()
	public final int EHOSTDOWN = EHOSTDOWN();

        /**
         * Host is unreachable.
         */
	@POSIX()
	public final int EHOSTUNREACH = EHOSTUNREACH();

        /**
         *     Connection already in progress.
         */
	@POSIX()
	public final int EALREADY = EALREADY();

        /**
         * Operation in progress.
         */
	@POSIX()
	public final int EINPROGRESS = EINPROGRESS();

        /**
         * Reserved.
         */
	@POSIX()
	public final int ESTALE = ESTALE();

	@LINUX()
	public final int EUCLEAN = EUCLEAN();

	@LINUX()
	public final int ENOTNAM = ENOTNAM();

	@LINUX()
	public final int ENAVAIL = ENAVAIL();

	@LINUX()
	public final int EISNAM = EISNAM();

	@LINUX()
	public final int EREMOTEIO = EREMOTEIO();

        /**
         * Reserved.
         */
	@POSIX()
	public final int EDQUOT = EDQUOT();

	@LINUX()
	public final int ENOMEDIUM = ENOMEDIUM();

	@LINUX()
	public final int EMEDIUMTYPE = EMEDIUMTYPE();

        /**
         * Operation canceled.
         */
	@POSIX()
	public final int ECANCELED = ECANCELED();

	@LINUX()
	public final int ENOKEY = ENOKEY();

	@LINUX()
	public final int EKEYEXPIRED = EKEYEXPIRED();

	@LINUX()
	public final int EKEYREVOKED = EKEYREVOKED();

	@LINUX()
	public final int EKEYREJECTED = EKEYREJECTED();

        /**
         * Previous owner died.
         */
	@POSIX()
	public final int EOWNERDEAD = EOWNERDEAD();

        /**
         * State not recoverable.
         */
	@POSIX()
	public final int ENOTRECOVERABLE = ENOTRECOVERABLE();

	@LINUX()
	public final int ERFKILL = ERFKILL();

	@LINUX()
	public final int EHWPOISON = EHWPOISON();

        /**
         * Not supported (may be the same value as [@see EOPNOTSUPP]).
         */
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
