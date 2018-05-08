package de.ibapl.jnrheader.linux;

import de.ibapl.jnrheader.POSIX;
import de.ibapl.jnrheader.isoc.Errno_H;
import jnr.ffi.LibraryLoader;
import jnr.ffi.Runtime;
import jnr.ffi.types.intptr_t;

public abstract class Errno_Generic extends Errno_H {
	
	//TODO we can't use this were miles away ???
	@de.ibapl.jnrheader.NativeFunctions
	protected interface NativeFunctions {
		@intptr_t int __errno_location();
//		int errno(); //TODO was __errno_location ????
	}

	@POSIX()
	public static final int EPERM = 1;

	@POSIX()
	public static final int ENOENT = 2;

	@POSIX()
	public static final int ESRCH = 3;

	@POSIX()
	public static final int EINTR = 4;

	@POSIX()
	public static final int EIO = 5;

	@POSIX()
	public static final int ENXIO = 6;

	@POSIX()
	public static final int E2BIG = 7;

	@POSIX()
	public static final int ENOEXEC = 8;

	@POSIX()
	public static final int EBADF = 9;

	@POSIX()
	public static final int ECHILD = 10;

	@POSIX()
	public static final int EAGAIN = 11;

	@POSIX()
	public static final int ENOMEM = 12;

	@POSIX()
	public static final int EACCES = 13;

	@POSIX()
	public static final int EFAULT = 14;

	@POSIX()
	public static final int ENOTBLK = 15;

	@POSIX()
	public static final int EBUSY = 16;

	@POSIX()
	public static final int EEXIST = 17;

	@POSIX()
	public static final int EXDEV = 18;

	@POSIX()
	public static final int ENODEV = 19;

	@POSIX()
	public static final int ENOTDIR = 20;

	@POSIX()
	public static final int EISDIR = 21;

	@POSIX()
	public static final int EINVAL = 22;

	@POSIX()
	public static final int ENFILE = 23;

	@POSIX()
	public static final int EMFILE = 24;

	@POSIX()
	public static final int ENOTTY = 25;

	@POSIX()
	public static final int ETXTBSY = 26;

	@POSIX()
	public static final int EFBIG = 27;

	@POSIX()
	public static final int ENOSPC = 28;

	@POSIX()
	public static final int ESPIPE = 29;

	@POSIX()
	public static final int EROFS = 30;

	@POSIX()
	public static final int EMLINK = 31;

	@POSIX()
	public static final int EPIPE = 32;

	@POSIX()
	public static final int EDOM = 33;

	@POSIX()
	public static final int ERANGE = 34;

	@POSIX()
	public static final int EDEADLK = 35;

	@POSIX()
	public static final int ENAMETOOLONG = 36;

	@POSIX()
	public static final int ENOLCK = 37;

	@POSIX()
	public static final int ENOSYS = 38;

	@POSIX()
	public static final int ENOTEMPTY = 39;

	@POSIX()
	public static final int ELOOP = 40;

	@POSIX()
	public static final int EWOULDBLOCK = EAGAIN;

	@POSIX()
	public static final int ENOMSG = 42;

	@POSIX()
	public static final int EIDRM = 43;

	@POSIX()
	public static final int ECHRNG = 44;

	@POSIX()
	public static final int EL2NSYNC = 45;

	@POSIX()
	public static final int EL3HLT = 46;

	@POSIX()
	public static final int EL3RST = 47;

	@POSIX()
	public static final int ELNRNG = 48;

	@POSIX()
	public static final int EUNATCH = 49;

	@POSIX()
	public static final int ENOCSI = 50;

	@POSIX()
	public static final int EL2HLT = 51;

	@POSIX()
	public static final int EBADE = 52;

	@POSIX()
	public static final int EBADR = 53;

	@POSIX()
	public static final int EXFULL = 54;

	@POSIX()
	public static final int ENOANO = 55;

	@POSIX()
	public static final int EBADRQC = 56;

	@POSIX()
	public static final int EBADSLT = 57;

	@POSIX()
	public static final int EDEADLOCK = EDEADLK;

	@POSIX()
	public static final int EBFONT = 59;

	@POSIX()
	public static final int ENOSTR = 60;

	@POSIX()
	public static final int ENODATA = 61;

	@POSIX()
	public static final int ETIME = 62;

	@POSIX()
	public static final int ENOSR = 63;

	@POSIX()
	public static final int ENONET = 64;

	@POSIX()
	public static final int ENOPKG = 65;

	@POSIX()
	public static final int EREMOTE = 66;

	@POSIX()
	public static final int ENOLINK = 67;

	@POSIX()
	public static final int EADV = 68;

	@POSIX()
	public static final int ESRMNT = 69;

	@POSIX()
	public static final int ECOMM = 70;

	@POSIX()
	public static final int EPROTO = 71;

	@POSIX()
	public static final int EMULTIHOP = 72;

	@POSIX()
	public static final int EDOTDOT = 73;

	@POSIX()
	public static final int EBADMSG = 74;

	@POSIX()
	public static final int EOVERFLOW = 75;

	@POSIX()
	public static final int ENOTUNIQ = 76;

	@POSIX()
	public static final int EBADFD = 77;

	@POSIX()
	public static final int EREMCHG = 78;

	@POSIX()
	public static final int ELIBACC = 79;

	@POSIX()
	public static final int ELIBBAD = 80;

	@POSIX()
	public static final int ELIBSCN = 81;

	@POSIX()
	public static final int ELIBMAX = 82;

	@POSIX()
	public static final int ELIBEXEC = 83;

	@POSIX()
	public static final int EILSEQ = 84;

	@POSIX()
	public static final int ERESTART = 85;

	@POSIX()
	public static final int ESTRPIPE = 86;

	@POSIX()
	public static final int EUSERS = 87;

	@POSIX()
	public static final int ENOTSOCK = 88;

	@POSIX()
	public static final int EDESTADDRREQ = 89;

	@POSIX()
	public static final int EMSGSIZE = 90;

	@POSIX()
	public static final int EPROTOTYPE = 91;

	@POSIX()
	public static final int ENOPROTOOPT = 92;

	@POSIX()
	public static final int EPROTONOSUPPORT = 93;

	@POSIX()
	public static final int ESOCKTNOSUPPORT = 94;

	@POSIX()
	public static final int EOPNOTSUPP = 95;

	@POSIX()
	public static final int EPFNOSUPPORT = 96;

	@POSIX()
	public static final int EAFNOSUPPORT = 97;

	@POSIX()
	public static final int EADDRINUSE = 98;

	@POSIX()
	public static final int EADDRNOTAVAIL = 99;

	@POSIX()
	public static final int ENETDOWN = 100;

	@POSIX()
	public static final int ENETUNREACH = 101;

	@POSIX()
	public static final int ENETRESET = 102;

	@POSIX()
	public static final int ECONNABORTED = 103;

	@POSIX()
	public static final int ECONNRESET = 104;

	@POSIX()
	public static final int ENOBUFS = 105;

	@POSIX()
	public static final int EISCONN = 106;

	@POSIX()
	public static final int ENOTCONN = 107;

	@POSIX()
	public static final int ESHUTDOWN = 108;

	@POSIX()
	public static final int ETOOMANYREFS = 109;

	@POSIX()
	public static final int ETIMEDOUT = 110;

	@POSIX()
	public static final int ECONNREFUSED = 111;

	@POSIX()
	public static final int EHOSTDOWN = 112;

	@POSIX()
	public static final int EHOSTUNREACH = 113;

	@POSIX()
	public static final int EALREADY = 114;

	@POSIX()
	public static final int EINPROGRESS = 115;

	@POSIX()
	public static final int ESTALE = 116;

	@POSIX()
	public static final int EUCLEAN = 117;

	@POSIX()
	public static final int ENOTNAM = 118;

	@POSIX()
	public static final int ENAVAIL = 119;

	@POSIX()
	public static final int EISNAM = 120;

	@POSIX()
	public static final int EREMOTEIO = 121;

	@POSIX()
	public static final int EDQUOT = 122;

	@POSIX()
	public static final int ENOMEDIUM = 123;

	@POSIX()
	public static final int EMEDIUMTYPE = 124;

	@POSIX()
	public static final int ECANCELED = 125;

	@POSIX()
	public static final int ENOKEY = 126;

	@POSIX()
	public static final int EKEYEXPIRED = 127;

	@POSIX()
	public static final int EKEYREVOKED = 128;

	@POSIX()
	public static final int EKEYREJECTED = 129;

	@POSIX()
	public static final int EOWNERDEAD = 130;

	@POSIX()
	public static final int ENOTRECOVERABLE = 131;

	@POSIX()
	public static final int ERFKILL = 132;

	@POSIX()
	public static final int EHWPOISON = 133;

	@POSIX()
	public static final int ENOTSUP = EOPNOTSUPP;

	final private NativeFunctions nativeFunctions;

	public Errno_Generic() {
		nativeFunctions = LibraryLoader.create(NativeFunctions.class).load("c");
	}

	@Override
	protected int E2BIG() {
		return Errno_Generic.E2BIG;
	}

	@Override
	protected int EACCES() {
		return Errno_Generic.EACCES;
	}

	@Override
	protected int EADDRINUSE() {
		return Errno_Generic.EADDRINUSE;
	}

	@Override
	protected int EADDRNOTAVAIL() {
		return Errno_Generic.EADDRNOTAVAIL;
	}

	@Override
	protected int EADV() {
		return Errno_Generic.EADV;
	}

	@Override
	protected int EAFNOSUPPORT() {
		return Errno_Generic.EAFNOSUPPORT;
	}

	@Override
	protected int EAGAIN() {
		return Errno_Generic.EAGAIN;
	}

	@Override
	protected int EALREADY() {
		return Errno_Generic.EALREADY;
	}

	@Override
	protected int EBADE() {
		return Errno_Generic.EBADE;
	}

	@Override
	protected int EBADF() {
		return Errno_Generic.EBADF;
	}

	@Override
	protected int EBADFD() {
		return Errno_Generic.EBADFD;
	}

	@Override
	protected int EBADMSG() {
		return Errno_Generic.EBADMSG;
	}

	@Override
	protected int EBADR() {
		return Errno_Generic.EBADR;
	}

	@Override
	protected int EBADRQC() {
		return Errno_Generic.EBADRQC;
	}

	@Override
	protected int EBADSLT() {
		return Errno_Generic.EBADSLT;
	}

	@Override
	protected int EBFONT() {
		return Errno_Generic.EBFONT;
	}

	@Override
	protected int EBUSY() {
		return Errno_Generic.EBUSY;
	}

	@Override
	protected int ECANCELED() {
		return Errno_Generic.ECANCELED;
	}

	@Override
	protected int ECHILD() {
		return Errno_Generic.ECHILD;
	}

	@Override
	protected int ECHRNG() {
		return Errno_Generic.ECHRNG;
	}

	@Override
	protected int ECOMM() {
		return Errno_Generic.ECOMM;
	}

	@Override
	protected int ECONNABORTED() {
		return Errno_Generic.ECONNABORTED;
	}

	@Override
	protected int ECONNREFUSED() {
		return Errno_Generic.ECONNREFUSED;
	}

	@Override
	protected int ECONNRESET() {
		return Errno_Generic.ECONNRESET;
	}

	@Override
	protected int EDEADLK() {
		return Errno_Generic.EDEADLK;
	}

	@Override
	protected int EDEADLOCK() {
		return Errno_Generic.EDEADLOCK;
	}

	@Override
	protected int EDESTADDRREQ() {
		return Errno_Generic.EDESTADDRREQ;
	}

	@Override
	protected int EDOM() {
		return Errno_Generic.EDOM;
	}

	@Override
	protected int EDOTDOT() {
		return Errno_Generic.EDOTDOT;
	}

	@Override
	protected int EDQUOT() {
		return Errno_Generic.EDQUOT;
	}

	@Override
	protected int EEXIST() {
		return Errno_Generic.EEXIST;
	}

	@Override
	protected int EFAULT() {
		return Errno_Generic.EFAULT;
	}

	@Override
	protected int EFBIG() {
		return Errno_Generic.EFBIG;
	}

	@Override
	protected int EHOSTDOWN() {
		return Errno_Generic.EHOSTDOWN;
	}

	@Override
	protected int EHOSTUNREACH() {
		return Errno_Generic.EHOSTUNREACH;
	}

	@Override
	protected int EHWPOISON() {
		return Errno_Generic.EHWPOISON;
	}

	@Override
	protected int EIDRM() {
		return Errno_Generic.EIDRM;
	}

	@Override
	protected int EILSEQ() {
		return Errno_Generic.EILSEQ;
	}

	@Override
	protected int EINPROGRESS() {
		return Errno_Generic.EINPROGRESS;
	}

	@Override
	protected int EINTR() {
		return Errno_Generic.EINTR;
	}

	@Override
	protected int EINVAL() {
		return Errno_Generic.EINVAL;
	}

	@Override
	protected int EIO() {
		return Errno_Generic.EIO;
	}

	@Override
	protected int EISCONN() {
		return Errno_Generic.EISCONN;
	}

	@Override
	protected int EISDIR() {
		return Errno_Generic.EISDIR;
	}

	@Override
	protected int EISNAM() {
		return Errno_Generic.EISNAM;
	}

	@Override
	protected int EKEYEXPIRED() {
		return Errno_Generic.EKEYEXPIRED;
	}

	@Override
	protected int EKEYREJECTED() {
		return Errno_Generic.EKEYREJECTED;
	}

	@Override
	protected int EKEYREVOKED() {
		return Errno_Generic.EKEYREVOKED;
	}

	@Override
	protected int EL2HLT() {
		return Errno_Generic.EL2HLT;
	}

	@Override
	protected int EL2NSYNC() {
		return Errno_Generic.EL2NSYNC;
	}

	@Override
	protected int EL3HLT() {
		return Errno_Generic.EL3HLT;
	}

	@Override
	protected int EL3RST() {
		return Errno_Generic.EL3RST;
	}

	@Override
	protected int ELIBACC() {
		return Errno_Generic.ELIBACC;
	}

	@Override
	protected int ELIBBAD() {
		return Errno_Generic.ELIBBAD;
	}

	@Override
	protected int ELIBEXEC() {
		return Errno_Generic.ELIBEXEC;
	}

	@Override
	protected int ELIBMAX() {
		return Errno_Generic.ELIBMAX;
	}

	@Override
	protected int ELIBSCN() {
		return Errno_Generic.ELIBSCN;
	}

	@Override
	protected int ELNRNG() {
		return Errno_Generic.ELNRNG;
	}

	@Override
	protected int ELOOP() {
		return Errno_Generic.ELOOP;
	}

	@Override
	protected int EMEDIUMTYPE() {
		return Errno_Generic.EMEDIUMTYPE;
	}

	@Override
	protected int EMFILE() {
		return Errno_Generic.EMFILE;
	}

	@Override
	protected int EMLINK() {
		return Errno_Generic.EMLINK;
	}

	@Override
	protected int EMSGSIZE() {
		return Errno_Generic.EMSGSIZE;
	}

	@Override
	protected int EMULTIHOP() {
		return Errno_Generic.EMULTIHOP;
	}

	@Override
	protected int ENAMETOOLONG() {
		return Errno_Generic.ENAMETOOLONG;
	}

	@Override
	protected int ENAVAIL() {
		return Errno_Generic.ENAVAIL;
	}

	@Override
	protected int ENETDOWN() {
		return Errno_Generic.ENETDOWN;
	}

	@Override
	protected int ENETRESET() {
		return Errno_Generic.ENETRESET;
	}

	@Override
	protected int ENETUNREACH() {
		return Errno_Generic.ENETUNREACH;
	}

	@Override
	protected int ENFILE() {
		return Errno_Generic.ENFILE;
	}

	@Override
	protected int ENOANO() {
		return Errno_Generic.ENOANO;
	}

	@Override
	protected int ENOBUFS() {
		return Errno_Generic.ENOBUFS;
	}

	@Override
	protected int ENOCSI() {
		return Errno_Generic.ENOCSI;
	}

	@Override
	protected int ENODATA() {
		return Errno_Generic.ENODATA;
	}

	@Override
	protected int ENODEV() {
		return Errno_Generic.ENODEV;
	}

	@Override
	protected int ENOENT() {
		return Errno_Generic.ENOENT;
	}

	@Override
	protected int ENOEXEC() {
		return Errno_Generic.ENOEXEC;
	}

	@Override
	protected int ENOKEY() {
		return Errno_Generic.ENOKEY;
	}

	@Override
	protected int ENOLCK() {
		return Errno_Generic.ENOLCK;
	}

	@Override
	protected int ENOLINK() {
		return Errno_Generic.ENOLINK;
	}

	@Override
	protected int ENOMEDIUM() {
		return Errno_Generic.ENOMEDIUM;
	}

	@Override
	protected int ENOMEM() {
		return Errno_Generic.ENOMEM;
	}

	@Override
	protected int ENOMSG() {
		return Errno_Generic.ENOMSG;
	}

	@Override
	protected int ENONET() {
		return Errno_Generic.ENONET;
	}

	@Override
	protected int ENOPKG() {
		return Errno_Generic.ENOPKG;
	}

	@Override
	protected int ENOPROTOOPT() {
		return Errno_Generic.ENOPROTOOPT;
	}

	@Override
	protected int ENOSPC() {
		return Errno_Generic.ENOSPC;
	}

	@Override
	protected int ENOSR() {
		return Errno_Generic.ENOSR;
	}

	@Override
	protected int ENOSTR() {
		return Errno_Generic.ENOSTR;
	}

	@Override
	protected int ENOSYS() {
		return Errno_Generic.ENOSYS;
	}

	@Override
	protected int ENOTBLK() {
		return Errno_Generic.ENOTBLK;
	}

	@Override
	protected int ENOTCONN() {
		return Errno_Generic.ENOTCONN;
	}

	@Override
	protected int ENOTDIR() {
		return Errno_Generic.ENOTDIR;
	}

	@Override
	protected int ENOTEMPTY() {
		return Errno_Generic.ENOTEMPTY;
	}

	@Override
	protected int ENOTNAM() {
		return Errno_Generic.ENOTNAM;
	}

	@Override
	protected int ENOTRECOVERABLE() {
		return Errno_Generic.ENOTRECOVERABLE;
	}

	@Override
	protected int ENOTSOCK() {
		return Errno_Generic.ENOTSOCK;
	}

	@Override
	protected int ENOTSUP() {
		return Errno_Generic.ENOTSUP;
	}

	@Override
	protected int ENOTTY() {
		return Errno_Generic.ENOTTY;
	}

	@Override
	protected int ENOTUNIQ() {
		return Errno_Generic.ENOTUNIQ;
	}

	@Override
	protected int ENXIO() {
		return Errno_Generic.ENXIO;
	}

	@Override
	protected int EOPNOTSUPP() {
		return Errno_Generic.EOPNOTSUPP;
	}

	@Override
	protected int EOVERFLOW() {
		return Errno_Generic.EOVERFLOW;
	}

	@Override
	protected int EOWNERDEAD() {
		return Errno_Generic.EOWNERDEAD;
	}

	@Override
	protected int EPERM() {
		return Errno_Generic.EPERM;
	}

	@Override
	protected int EPFNOSUPPORT() {
		return Errno_Generic.EPFNOSUPPORT;
	}

	@Override
	protected int EPIPE() {
		return Errno_Generic.EPIPE;
	}

	@Override
	protected int EPROTO() {
		return Errno_Generic.EPROTO;
	}

	@Override
	protected int EPROTONOSUPPORT() {
		return Errno_Generic.EPROTONOSUPPORT;
	}

	@Override
	protected int EPROTOTYPE() {
		return Errno_Generic.EPROTOTYPE;
	}

	@Override
	protected int ERANGE() {
		return Errno_Generic.ERANGE;
	}

	@Override
	protected int EREMCHG() {
		return Errno_Generic.EREMCHG;
	}

	@Override
	protected int EREMOTE() {
		return Errno_Generic.EREMOTE;
	}

	@Override
	protected int EREMOTEIO() {
		return Errno_Generic.EREMOTEIO;
	}

	@Override
	protected int ERESTART() {
		return Errno_Generic.ERESTART;
	}

	@Override
	protected int ERFKILL() {
		return Errno_Generic.ERFKILL;
	}

	@Override
	protected int EROFS() {
		return Errno_Generic.EROFS;
	}

	@Override
    public int errno() {
		//return nativeFunctions.__errno_location().errno.intValue();
         return Runtime.getRuntime(nativeFunctions).getLastError();
    }

	@Override
    public void errno(int value) {
        Runtime.getRuntime(nativeFunctions).setLastError(value);
    }

	@Override
	protected int ESHUTDOWN() {
		return Errno_Generic.ESHUTDOWN;
	}

	@Override
	protected int ESOCKTNOSUPPORT() {
		return Errno_Generic.ESOCKTNOSUPPORT;
	}

	@Override
	protected int ESPIPE() {
		return Errno_Generic.ESPIPE;
	}

	@Override
	protected int ESRCH() {
		return Errno_Generic.ESRCH;
	}

	@Override
	protected int ESRMNT() {
		return Errno_Generic.ESRMNT;
	}

	@Override
	protected int ESTALE() {
		return Errno_Generic.ESTALE;
	}

	@Override
	protected int ESTRPIPE() {
		return Errno_Generic.ESTRPIPE;
	}

	@Override
	protected int ETIME() {
		return Errno_Generic.ETIME;
	}

	@Override
	protected int ETIMEDOUT() {
		return Errno_Generic.ETIMEDOUT;
	}

	@Override
	protected int ETOOMANYREFS() {
		return Errno_Generic.ETOOMANYREFS;
	}

	@Override
	protected int ETXTBSY() {
		return Errno_Generic.ETXTBSY;
	}

	@Override
	protected int EUCLEAN() {
		return Errno_Generic.EUCLEAN;
	}

	@Override
	protected int EUNATCH() {
		return Errno_Generic.EUNATCH;
	}

	@Override
	protected int EUSERS() {
		return Errno_Generic.EUSERS;
	}

	@Override
	protected int EWOULDBLOCK() {
		return Errno_Generic.EWOULDBLOCK;
	}

	@Override
	protected int EXDEV() {
		return Errno_Generic.EXDEV;
	}

	@Override
	protected int EXFULL() {
		return Errno_Generic.EXFULL;
	}

}
