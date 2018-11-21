package de.ibapl.jnrheader.linux;

import static de.ibapl.jnrheader.JnrHeader.UTF8_ENCODING;
import de.ibapl.jnrheader.POSIX;
import de.ibapl.jnrheader.posix.Fcntl_H;
import jnr.ffi.LibraryLoader;
import jnr.ffi.annotations.Encoding;
import jnr.ffi.annotations.In;

public abstract class Fcntl_Lib extends Fcntl_H {

	@de.ibapl.jnrheader.NativeFunctions
	protected interface NativeFunctions {
		int creat(@In @Encoding(UTF8_ENCODING) CharSequence file, int mode);

		int fcntl(int fd, int cmd);

		int lockf(int fd, int cmd, long len);

		int open(@In @Encoding(UTF8_ENCODING) CharSequence file, int oflag);;

		int openat(int fd, @In @Encoding(UTF8_ENCODING)CharSequence file, int oflag);

		int posix_fadvise(int fd, long offset, long len, int advise);

		int posix_fallocate(int nfd, long offset, long len);

	}

	@POSIX
	public static final int __O_LARGEFILE = 0;
	@POSIX
	public static final int F_GETLK64 = 5;
	@POSIX
	public static final int F_SETLK64 = 6;
	@POSIX
	public static final int F_SETLKW64 = 7;
	@POSIX
	public static final int O_ACCMODE = 0003;
	@POSIX
	public static final int O_RDONLY = 00;
	@POSIX
	public static final int O_WRONLY = 01;
	@POSIX
	public static final int O_RDWR = 02;
	@POSIX
	public static final int O_CREAT = 0100;
	@POSIX
	public static final int O_EXCL = 0200;
	@POSIX
	public static final int O_NOCTTY = 0400;
	@POSIX
	public static final int O_TRUNC = 01000;
	@POSIX
	public static final int O_APPEND = 02000;
	@POSIX
	public static final int O_NONBLOCK = 04000;
	@POSIX
	public static final int O_NDELAY = O_NONBLOCK;
	@POSIX
	public static final int O_SYNC = 04010000;
	@POSIX
	public static final int O_FSYNC = O_SYNC;
	@POSIX
	public static final int O_ASYNC = 020000;
	@POSIX
	public static final int __O_DIRECTORY = 0200000;
	@POSIX
	public static final int __O_NOFOLLOW = 0400000;
	@POSIX
	public static final int __O_CLOEXEC = 02000000;
	@POSIX
	public static final int __O_DIRECT = 040000;
	@POSIX
	public static final int __O_NOATIME = 01000000;

	@POSIX
	public static final int __O_PATH = 010000000;

	@POSIX
	public static final int __O_DSYNC = 010000;

	@POSIX
	public static final int __O_TMPFILE = (020000000 | __O_DIRECTORY);

	@POSIX
	public static final int F_GETLK = 5;

	@POSIX
	public static final int F_SETLK = 6;

	@POSIX
	public static final int F_SETLKW = 7;

	@POSIX
	public static final int O_DIRECTORY = __O_DIRECTORY;

	@POSIX
	public static final int O_NOFOLLOW = __O_NOFOLLOW;

	@POSIX
	public static final int O_CLOEXEC = __O_CLOEXEC;

	@POSIX
	public static final int O_DSYNC = __O_DSYNC;

	@POSIX
	public static final int O_RSYNC = O_SYNC;

	@POSIX
	public static final int F_DUPFD = 0;

	@POSIX
	public static final int F_GETFD = 1;
	@POSIX
	public static final int F_SETFD = 2;
	@POSIX
	public static final int F_GETFL = 3;
	@POSIX
	public static final int F_SETFL = 4;
	@POSIX
	private static final int __F_SETOWN = 8;
	@POSIX
	private static final int __F_GETOWN = 9;
	@POSIX
	public static final int F_SETOWN = __F_SETOWN;
	@POSIX
	public static final int F_GETOWN = __F_GETOWN;
	@POSIX
	private static final int __F_SETSIG = 10;

	@POSIX
	private static final int __F_GETSIG = 11;

	@POSIX
	private static final int __F_SETOWN_EX = 15;

	@POSIX
	private static final int __F_GETOWN_EX = 16;

	@POSIX
	public static final int F_DUPFD_CLOEXEC = 1030;

	@POSIX
	public static final int FD_CLOEXEC = 1;

	@POSIX
	public static final int F_RDLCK = 0;

	@POSIX
	public static final int F_WRLCK = 1;

	@POSIX
	public static final int F_UNLCK = 2;

	@POSIX
	public static final int F_EXLCK = 4;

	@POSIX
	public static final int F_SHLCK = 8;

	@POSIX
	public static final int LOCK_SH = 1;

	@POSIX
	public static final int LOCK_EX = 2;

	@POSIX
	public static final int LOCK_NB = 4;

	@POSIX
	public static final int LOCK_UN = 8;

	@POSIX
	public static final int FAPPEND = O_APPEND;

	@POSIX
	public static final int FFSYNC = O_FSYNC;

	@POSIX
	public static final int FASYNC = O_ASYNC;

	@POSIX
	public static final int FNONBLOCK = O_NONBLOCK;

	@POSIX
	public static final int FNDELAY = O_NDELAY;

	@POSIX
	private static final int __POSIX_FADV_DONTNEED = 4;

	@POSIX
	private static final int __POSIX_FADV_NOREUSE = 5;

	@POSIX
	private static final int POSIX_FADV_NORMAL = 0;

	@POSIX
	public static final int POSIX_FADV_RANDOM = 1;

	@POSIX
	public static final int POSIX_FADV_SEQUENTIAL = 2;

	@POSIX
	public static final int POSIX_FADV_WILLNEED = 3;

	@POSIX
	public static final int POSIX_FADV_DONTNEED = __POSIX_FADV_DONTNEED;
	@POSIX
	public static final int POSIX_FADV_NOREUSE = __POSIX_FADV_NOREUSE;
	@POSIX
	public static final int AT_FDCWD = -100;

	@POSIX
	public static final int AT_SYMLINK_NOFOLLOW = 0x100;

	@POSIX
	public static final int AT_REMOVEDIR = 0x200;

	@POSIX
	public static final int AT_SYMLINK_FOLLOW = 0x400;

	@POSIX
	public static final int AT_EACCESS = 0x200;
	@POSIX
	public static final int _BITS_STAT_H = 1;
	@POSIX
	public static final int __S_IFMT = 0170000;
	@POSIX
	public static final int __S_IFDIR = 0040000;
	@POSIX
	public static final int __S_IFCHR = 0020000;

	@POSIX
	public static final int __S_IFBLK = 0060000;

	@POSIX
	public static final int __S_IFREG = 0100000;

	@POSIX
	public static final int __S_IFIFO = 0010000;

	@POSIX
	public static final int __S_IFLNK = 0120000;

	@POSIX
	public static final int __S_IFSOCK = 0140000;

	@POSIX
	public static final int __S_ISUID = 04000;

	@POSIX
	public static final int __S_ISGID = 02000;

	@POSIX
	public static final int __S_ISVTX = 01000;

	@POSIX
	public static final int __S_IREAD = 0400;

	@POSIX
	public static final int __S_IWRITE = 0200;

	@POSIX
	public static final int __S_IEXEC = 0100;

	@POSIX
	public static final int S_IFMT = __S_IFMT;

	@POSIX
	public static final int S_IFDIR = __S_IFDIR;

	@POSIX
	public static final int S_IFCHR = __S_IFCHR;

	@POSIX
	public static final int S_IFBLK = __S_IFBLK;

	@POSIX
	public static final int S_IFREG = __S_IFREG;

	@POSIX
	public static final int S_IFIFO = __S_IFIFO;

	@POSIX
	public static final int S_IFLNK = __S_IFLNK;

	@POSIX
	public static final int S_IFSOCK = __S_IFSOCK;

	@POSIX
	public static final int S_ISUID = __S_ISUID;

	@POSIX
	public static final int S_ISGID = __S_ISGID;

	@POSIX
	public static final int S_ISVTX = __S_ISVTX;

	@POSIX
	public static final int S_IRUSR = __S_IREAD;

	@POSIX
	public static final int S_IWUSR = __S_IWRITE;

	@POSIX
	public static final int S_IXUSR = __S_IEXEC;

	@POSIX
	public static final int S_IRWXU = (__S_IREAD | __S_IWRITE | __S_IEXEC);

	@POSIX
	public static final int S_IRGRP = (S_IRUSR >> 3);

	@POSIX
	public static final int S_IWGRP = (S_IWUSR >> 3);

	@POSIX
	public static final int S_IXGRP = (S_IXUSR >> 3);

	@POSIX
	public static final int S_IRWXG = (S_IRWXU >> 3);

	@POSIX
	public static final int S_IROTH = (S_IRGRP >> 3);

	@POSIX
	public static final int S_IWOTH = (S_IWGRP >> 3);
	@POSIX
	public static final int S_IXOTH = (S_IXGRP >> 3);
	@POSIX
	public static final int S_IRWXO = (int) (S_IRWXG >> 3);

	@POSIX
	public static final int R_OK = 4;

	@POSIX
	public static final int W_OK = 2;

	@POSIX
	public static final int X_OK = 1;

	@POSIX
	public static final int F_OK = 0;

	@POSIX
	public static final int SEEK_SET = 0;

	@POSIX
	public static final int SEEK_CUR = 1;

	@POSIX
	public static final int SEEK_END = 2;

	@POSIX
	public static final int F_ULOCK = 0;

	@POSIX
	public static final int F_LOCK = 1;

	@POSIX
	public static final int F_TLOCK = 2;

	@POSIX
	public static final int F_TEST = 3;

	private NativeFunctions nativeFunctions;

	public Fcntl_Lib() {
		nativeFunctions = LibraryLoader.create(NativeFunctions.class).load("c");
	}

	@Override
	protected int AT_EACCESS() {
		return Fcntl_Lib.AT_EACCESS;
	}

	@Override
	protected int AT_FDCWD() {
		return Fcntl_Lib.AT_FDCWD;
	}

	@Override
	protected int AT_REMOVEDIR() {
		return Fcntl_Lib.AT_REMOVEDIR;
	}

	@Override
	protected int AT_SYMLINK_FOLLOW() {
		return Fcntl_Lib.AT_SYMLINK_FOLLOW;
	}

	@Override
	protected int AT_SYMLINK_NOFOLLOW() {
		return Fcntl_Lib.AT_SYMLINK_NOFOLLOW;
	}

	@Override
	public int creat(String file, int mode) {
		return nativeFunctions.creat(file, mode);
	}

	@Override
	public Flock createFlock() {
		return new Flock();
	}

	@Override
	protected int F_DUPFD() {
		return Fcntl_Lib.F_DUPFD;
	}

	@Override
	protected int F_DUPFD_CLOEXEC() {
		return Fcntl_Lib.F_DUPFD_CLOEXEC;
	}

	@Override
	protected int F_EXLCK() {
		return Fcntl_Lib.F_EXLCK;
	}

	@Override
	protected int F_GETFD() {
		return Fcntl_Lib.F_GETFD;
	}

	@Override
	protected int F_GETFL() {
		return Fcntl_Lib.F_GETFL;
	}

	@Override
	protected int F_GETLK() {
		return Fcntl_Lib.F_GETLK;
	}

	@Override
	protected int F_GETLK64() {
		return Fcntl_Lib.F_GETLK64;
	}

	@Override
	protected int F_GETOWN() {
		return Fcntl_Lib.F_GETOWN;
	}

	@Override
	protected int F_LOCK() {
		return Fcntl_Lib.F_LOCK;
	}

	@Override
	protected int F_OK() {
		return Fcntl_Lib.F_OK;
	}

	@Override
	protected int F_RDLCK() {
		return Fcntl_Lib.F_RDLCK;
	}

	@Override
	protected int F_SETFD() {
		return Fcntl_Lib.F_SETFD;
	}

	@Override
	protected int F_SETFL() {
		return Fcntl_Lib.F_SETFL;
	}

	@Override
	protected int F_SETLK() {
		return Fcntl_Lib.F_SETLK;
	}

	@Override
	protected int F_SETLK64() {
		return Fcntl_Lib.F_SETLK64;
	}

	@Override
	protected int F_SETLKW() {
		return Fcntl_Lib.F_SETLKW;
	}

	@Override
	protected int F_SETLKW64() {
		return Fcntl_Lib.F_SETLKW64;
	}

	@Override
	protected int F_SETOWN() {
		return Fcntl_Lib.F_SETOWN;
	}

	@Override
	protected int F_SHLCK() {
		return Fcntl_Lib.F_SHLCK;
	}

	@Override
	protected int F_TEST() {
		return Fcntl_Lib.F_TEST;
	}

	@Override
	protected int F_TLOCK() {
		return Fcntl_Lib.F_TLOCK;
	}

	@Override
	protected int F_ULOCK() {
		return Fcntl_Lib.F_ULOCK;
	}

	@Override
	protected int F_UNLCK() {
		return Fcntl_Lib.F_UNLCK;
	}

	@Override
	protected int F_WRLCK() {
		return Fcntl_Lib.F_WRLCK;
	}

	@Override
	protected int FAPPEND() {
		return Fcntl_Lib.FAPPEND;
	}

	@Override
	protected int FASYNC() {
		return Fcntl_Lib.FASYNC;
	}

	@Override
	public int fcntl(int fd, int cmd) {
		return nativeFunctions.fcntl(fd, cmd);
	}

	@Override
	protected int FD_CLOEXEC() {
		return Fcntl_Lib.FD_CLOEXEC;
	}

	@Override
	protected int FFSYNC() {
		return Fcntl_Lib.FFSYNC;
	}

	@Override
	protected int FNDELAY() {
		return Fcntl_Lib.FNDELAY;
	}

	@Override
	protected int FNONBLOCK() {
		return Fcntl_Lib.FNONBLOCK;
	}

	@Override
	protected int LOCK_EX() {
		return Fcntl_Lib.LOCK_EX;
	}

	@Override
	protected int LOCK_NB() {
		return Fcntl_Lib.LOCK_NB;
	}

	@Override
	protected int LOCK_SH() {
		return Fcntl_Lib.LOCK_SH;
	}

	@Override
	protected int LOCK_UN() {
		return Fcntl_Lib.LOCK_UN;
	}

	@Override
	public int lockf(int fd, int cmd, long len) {
		return nativeFunctions.lockf(fd, cmd, len);
	}

	@Override
	protected int O_ACCMODE() {
		return Fcntl_Lib.O_ACCMODE;
	}

	@Override
	protected int O_APPEND() {
		return Fcntl_Lib.O_APPEND;
	}

	@Override
	protected int O_ASYNC() {
		return Fcntl_Lib.O_ASYNC;
	}

	@Override
	protected int O_CLOEXEC() {
		return Fcntl_Lib.O_CLOEXEC;
	}

	@Override
	protected int O_CREAT() {
		return Fcntl_Lib.O_CREAT;
	}

	@Override
	protected int O_DIRECTORY() {
		return Fcntl_Lib.O_DIRECTORY;
	}

	@Override
	protected int O_DSYNC() {
		return Fcntl_Lib.O_DSYNC;
	}

	@Override
	protected int O_EXCL() {
		return Fcntl_Lib.O_EXCL;
	}

	@Override
	protected int O_FSYNC() {
		return Fcntl_Lib.O_FSYNC;
	}

	@Override
	protected int O_NDELAY() {
		return Fcntl_Lib.O_NDELAY;
	}

	@Override
	protected int O_NOCTTY() {
		return Fcntl_Lib.O_NOCTTY;
	}

	@Override
	protected int O_NOFOLLOW() {
		return Fcntl_Lib.O_NOFOLLOW;
	}

	@Override
	protected int O_NONBLOCK() {
		return Fcntl_Lib.O_NONBLOCK;
	}

	@Override
	protected int O_RDONLY() {
		return Fcntl_Lib.O_RDONLY;
	}

	@Override
	protected int O_RDWR() {
		return Fcntl_Lib.O_RDWR;
	}

	@Override
	protected int O_RSYNC() {
		return Fcntl_Lib.O_RSYNC;
	}

	@Override
	protected int O_SYNC() {
		return Fcntl_Lib.O_SYNC;
	}

	@Override
	protected int O_TRUNC() {
		return Fcntl_Lib.O_TRUNC;
	}

	@Override
	protected int O_WRONLY() {
		return Fcntl_Lib.O_WRONLY;
	}

	@Override
	public int open(String file, int oflag) {
		return nativeFunctions.open(file, oflag);
	}

	@Override
	public int openat(int fd, String file, int oflag) {
		return nativeFunctions.openat(fd, file, oflag);
	}

	@Override
	protected int POSIX_FADV_DONTNEED() {
		return Fcntl_Lib.POSIX_FADV_DONTNEED;
	}

	@Override
	protected int POSIX_FADV_NOREUSE() {
		return Fcntl_Lib.POSIX_FADV_NOREUSE;
	}

	@Override
	protected int POSIX_FADV_NORMAL() {
		return Fcntl_Lib.POSIX_FADV_NORMAL;
	}

	@Override
	protected int POSIX_FADV_RANDOM() {
		return Fcntl_Lib.POSIX_FADV_RANDOM;
	}

	@Override
	protected int POSIX_FADV_SEQUENTIAL() {
		return Fcntl_Lib.POSIX_FADV_SEQUENTIAL;
	}

	@Override
	protected int POSIX_FADV_WILLNEED() {
		return Fcntl_Lib.POSIX_FADV_WILLNEED;
	}

	@Override
	public int posix_fadvise(int fd, long offset, long len, int advise) {
		return nativeFunctions.posix_fadvise(fd, offset, len, advise);
	}

	@Override
	public int posix_fallocate(int nfd, long offset, long len) {
		return nativeFunctions.posix_fallocate(nfd, offset, len);
	}

	@Override
	protected int R_OK() {
		return Fcntl_Lib.R_OK;
	}

	@Override
	protected int S_IFBLK() {
		return Fcntl_Lib.S_IFBLK;
	}

	@Override
	protected int S_IFCHR() {
		return Fcntl_Lib.S_IFCHR;
	}

	@Override
	protected int S_IFDIR() {
		return Fcntl_Lib.S_IFDIR;
	}

	@Override
	protected int S_IFIFO() {
		return Fcntl_Lib.S_IFIFO;
	}

	@Override
	protected int S_IFLNK() {
		return Fcntl_Lib.S_IFLNK;
	}

	@Override
	protected int S_IFMT() {
		return Fcntl_Lib.S_IFMT;
	}

	@Override
	protected int S_IFREG() {
		return Fcntl_Lib.S_IFREG;
	}

	@Override
	protected int S_IFSOCK() {
		return Fcntl_Lib.S_IFSOCK;
	}

	@Override
	protected int S_IRGRP() {
		return Fcntl_Lib.S_IRGRP;
	}

	@Override
	protected int S_IROTH() {
		return Fcntl_Lib.S_IROTH;
	}

	@Override
	protected int S_IRUSR() {
		return Fcntl_Lib.S_IRUSR;
	}

	@Override
	protected int S_IRWXG() {
		return Fcntl_Lib.S_IRWXG;
	}

	@Override
	protected int S_IRWXO() {
		return Fcntl_Lib.S_IRWXO;
	}

	@Override
	protected int S_IRWXU() {
		return Fcntl_Lib.S_IRWXU;
	}

	@Override
	protected int S_ISGID() {
		return Fcntl_Lib.S_ISGID;
	}

	@Override
	protected int S_ISUID() {
		return Fcntl_Lib.S_ISUID;
	}

	@Override
	protected int S_ISVTX() {
		return Fcntl_Lib.S_ISVTX;
	}

	@Override
	protected int S_IWGRP() {
		return Fcntl_Lib.S_IWGRP;
	}

	@Override
	protected int S_IWOTH() {
		return Fcntl_Lib.S_IWOTH;
	}

	@Override
	protected int S_IWUSR() {
		return Fcntl_Lib.S_IWUSR;
	}

	@Override
	protected int S_IXGRP() {
		return Fcntl_Lib.S_IXGRP;
	}

	@Override
	protected int S_IXOTH() {
		return Fcntl_Lib.S_IXOTH;
	}

	@Override
	protected int S_IXUSR() {
		return Fcntl_Lib.S_IXUSR;
	}

	@Override
	protected int SEEK_CUR() {
		return Fcntl_Lib.SEEK_CUR;
	}

	@Override
	protected int SEEK_END() {
		return Fcntl_Lib.SEEK_END;
	}

	@Override
	protected int SEEK_SET() {
		return Fcntl_Lib.SEEK_SET;
	}

	@Override
	protected int W_OK() {
		return Fcntl_Lib.W_OK;
	}

	@Override
	protected int X_OK() {
		return Fcntl_Lib.X_OK;
	}

}
