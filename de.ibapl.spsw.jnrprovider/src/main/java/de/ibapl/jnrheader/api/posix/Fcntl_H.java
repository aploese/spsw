package de.ibapl.jnrheader.api.posix;

import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.NativeStruct;
import de.ibapl.jnrheader.POSIX;
import de.ibapl.jnrheader.Wrapper;

@Wrapper("fcntl.h")
public abstract class Fcntl_H implements JnrHeader {

	public class Flock {
		public short l_type;
		public short l_whence;
		public long l_start;
		public long l_len;
		public int l_pid;
	};

	@POSIX
	public final int F_GETLK64 = F_GETLK64();

	@POSIX
	public final int F_SETLK64 = F_SETLK64();

	@POSIX
	public final int F_SETLKW64 = F_SETLKW64();

	@POSIX
	public final int O_ACCMODE = O_ACCMODE();

	@POSIX
	public final int O_RDONLY = O_RDONLY();

	@POSIX
	public final int O_WRONLY = O_WRONLY();

	@POSIX
	public final int O_RDWR = O_RDWR();

	@POSIX
	public final int O_CREAT = O_CREAT();

	@POSIX
	public final int O_EXCL = O_EXCL();

	@POSIX
	public final int O_NOCTTY = O_NOCTTY();

	@POSIX
	public final int O_TRUNC = O_TRUNC();

	@POSIX
	public final int O_APPEND = O_APPEND();

	@POSIX
	public final int O_NONBLOCK = O_NONBLOCK();

	@POSIX
	public final int O_NDELAY = O_NDELAY();

	@POSIX
	public final int O_SYNC = O_SYNC();

	@POSIX
	public final int O_FSYNC = O_FSYNC();

	@POSIX
	public final int O_ASYNC = O_ASYNC();

	@POSIX
	public final int F_GETLK = F_GETLK();

	@POSIX
	public final int F_SETLK = F_SETLK();

	@POSIX
	public final int F_SETLKW = F_SETLKW();

	@POSIX
	public final int O_DIRECTORY = O_DIRECTORY();

	@POSIX
	public final int O_NOFOLLOW = O_NOFOLLOW();

	@POSIX
	public final int O_CLOEXEC = O_CLOEXEC();

	@POSIX
	public final int O_DSYNC = O_DSYNC();

	@POSIX
	public final int O_RSYNC = O_RSYNC();

	@POSIX
	public final int F_DUPFD = F_DUPFD();

	@POSIX
	public final int F_GETFD = F_GETFD();

	@POSIX
	public final int F_SETFD = F_SETFD();

	@POSIX
	public final int F_GETFL = F_GETFL();

	@POSIX
	public final int F_SETFL = F_SETFL();

	@POSIX
	public final int F_SETOWN = F_SETOWN();

	@POSIX
	public final int F_GETOWN = F_GETOWN();

	@POSIX
	public final int F_DUPFD_CLOEXEC = F_DUPFD_CLOEXEC();

	@POSIX
	public final int FD_CLOEXEC = FD_CLOEXEC();

	@POSIX
	public final int F_RDLCK = F_RDLCK();

	@POSIX
	public final int F_WRLCK = F_WRLCK();

	@POSIX
	public final int F_UNLCK = F_UNLCK();

	@POSIX
	public final int F_EXLCK = F_EXLCK();

	@POSIX
	public final int F_SHLCK = F_SHLCK();

	@POSIX
	public final int LOCK_SH = LOCK_SH();

	@POSIX
	public final int LOCK_EX = LOCK_EX();

	@POSIX
	public final int LOCK_NB = LOCK_NB();

	@POSIX
	public final int LOCK_UN = LOCK_UN();

	@POSIX
	public final int FAPPEND = FAPPEND();

	@POSIX
	public final int FFSYNC = FFSYNC();

	@POSIX
	public final int FASYNC = FASYNC();

	@POSIX
	public final int FNONBLOCK = FNONBLOCK();

	@POSIX
	public final int FNDELAY = FNDELAY();

	@POSIX
	public final int POSIX_FADV_NORMAL = POSIX_FADV_NORMAL();

	@POSIX
	public final int POSIX_FADV_RANDOM = POSIX_FADV_RANDOM();

	@POSIX
	public final int POSIX_FADV_SEQUENTIAL = POSIX_FADV_SEQUENTIAL();

	@POSIX
	public final int POSIX_FADV_WILLNEED = POSIX_FADV_WILLNEED();

	@POSIX
	public final int POSIX_FADV_DONTNEED = POSIX_FADV_DONTNEED();

	@POSIX
	public final int POSIX_FADV_NOREUSE = POSIX_FADV_NOREUSE();

	@POSIX
	public final int AT_FDCWD = AT_FDCWD();

	@POSIX
	public final int AT_SYMLINK_NOFOLLOW = AT_SYMLINK_NOFOLLOW();

	@POSIX
	public final int AT_REMOVEDIR = AT_REMOVEDIR();

	@POSIX
	public final int AT_SYMLINK_FOLLOW = AT_SYMLINK_FOLLOW();

	@POSIX
	public final int AT_EACCESS = AT_EACCESS();

	@POSIX
	public final int S_IFMT = S_IFMT();

	@POSIX
	public final int S_IFDIR = S_IFDIR();

	@POSIX
	public final int S_IFCHR = S_IFCHR();

	@POSIX
	public final int S_IFBLK = S_IFBLK();

	@POSIX
	public final int S_IFREG = S_IFREG();

	@POSIX
	public final int S_IFIFO = S_IFIFO();

	@POSIX
	public final int S_IFLNK = S_IFLNK();

	@POSIX
	public final int S_IFSOCK = S_IFSOCK();

	@POSIX
	public final int S_ISUID = S_ISUID();

	@POSIX
	public final int S_ISGID = S_ISGID();

	@POSIX
	public final int S_ISVTX = S_ISVTX();

	@POSIX
	public final int S_IRUSR = S_IRUSR();

	@POSIX
	public final int S_IWUSR = S_IWUSR();

	@POSIX
	public final int S_IXUSR = S_IXUSR();

	@POSIX
	public final int S_IRWXU = S_IRWXU();

	@POSIX
	public final int S_IRGRP = S_IRGRP();

	@POSIX
	public final int S_IWGRP = S_IWGRP();

	@POSIX
	public final int S_IXGRP = S_IXGRP();

	@POSIX
	public final int S_IRWXG = S_IRWXG();

	@POSIX
	public final int S_IROTH = S_IROTH();

	@POSIX
	public final int S_IWOTH = S_IWOTH();

	@POSIX
	public final int S_IXOTH = S_IXOTH();

	@POSIX
	public final int S_IRWXO = S_IRWXO();

	@POSIX
	public final int R_OK = R_OK();

	@POSIX
	public final int W_OK = W_OK();

	@POSIX
	public final int X_OK = X_OK();

	@POSIX
	public final int F_OK = F_OK();

	@POSIX
	public final int SEEK_SET = SEEK_SET();

	@POSIX
	public final int SEEK_CUR = SEEK_CUR();

	@POSIX
	public final int SEEK_END = SEEK_END();

	@POSIX
	public final int F_ULOCK = F_ULOCK();

	@POSIX
	public final int F_LOCK = F_LOCK();

	@POSIX
	public final int F_TLOCK = F_TLOCK();

	@POSIX
	public final int F_TEST = F_TEST();

	protected abstract int AT_EACCESS();

	protected abstract int AT_FDCWD();

	protected abstract int AT_REMOVEDIR();

	protected abstract int AT_SYMLINK_FOLLOW();

	protected abstract int AT_SYMLINK_NOFOLLOW();

	public abstract int creat(String file, int mode);

	public abstract Flock createFlock();

	protected abstract int F_DUPFD();

	protected abstract int F_DUPFD_CLOEXEC();

	protected abstract int F_EXLCK();

	protected abstract int F_GETFD();

	protected abstract int F_GETFL();

	protected abstract int F_GETLK();

	protected abstract int F_GETLK64();

	protected abstract int F_GETOWN();

	protected abstract int F_LOCK();

	protected abstract int F_OK();

	protected abstract int F_RDLCK();

	protected abstract int F_SETFD();

	protected abstract int F_SETFL();

	protected abstract int F_SETLK();

	protected abstract int F_SETLK64();

	protected abstract int F_SETLKW();

	protected abstract int F_SETLKW64();

	protected abstract int F_SETOWN();

	protected abstract int F_SHLCK();

	protected abstract int F_TEST();

	protected abstract int F_TLOCK();

	protected abstract int F_ULOCK();

	protected abstract int F_UNLCK();

	protected abstract int F_WRLCK();

	protected abstract int FAPPEND();

	protected abstract int FASYNC();

	public abstract int fcntl(int fd, int cmd);

	protected abstract int FD_CLOEXEC();

	protected abstract int FFSYNC();

	protected abstract int FNDELAY();

	protected abstract int FNONBLOCK();

	protected abstract int LOCK_EX();

	protected abstract int LOCK_NB();

	protected abstract int LOCK_SH();

	protected abstract int LOCK_UN();

	public abstract int lockf(int fd, int cmd, long len);

	protected abstract int O_ACCMODE();

	protected abstract int O_APPEND();

	protected abstract int O_ASYNC();

	protected abstract int O_CLOEXEC();

	protected abstract int O_CREAT();

	protected abstract int O_DIRECTORY();

	protected abstract int O_DSYNC();

	protected abstract int O_EXCL();

	protected abstract int O_FSYNC();

	protected abstract int O_NDELAY();

	protected abstract int O_NOCTTY();

	protected abstract int O_NOFOLLOW();

	protected abstract int O_NONBLOCK();

	protected abstract int O_RDONLY();

	protected abstract int O_RDWR();

	protected abstract int O_RSYNC();

	protected abstract int O_SYNC();

	protected abstract int O_TRUNC();

	protected abstract int O_WRONLY();

	public abstract int open(String file, int oflag);

	public abstract int openat(int fd, String file, int oflag);

	protected abstract int POSIX_FADV_DONTNEED();

	protected abstract int POSIX_FADV_NOREUSE();

	protected abstract int POSIX_FADV_NORMAL();

	protected abstract int POSIX_FADV_RANDOM();

	protected abstract int POSIX_FADV_SEQUENTIAL();

	protected abstract int POSIX_FADV_WILLNEED();

	public abstract int posix_fadvise(int fd, long offset, long len, int advise);

	public abstract int posix_fallocate(int nfd, long offset, long len);

	protected abstract int R_OK();

	protected abstract int S_IFBLK();

	protected abstract int S_IFCHR();

	protected abstract int S_IFDIR();

	protected abstract int S_IFIFO();

	protected abstract int S_IFLNK();

	protected abstract int S_IFMT();

	protected abstract int S_IFREG();

	protected abstract int S_IFSOCK();

	protected abstract int S_IRGRP();

	protected abstract int S_IROTH();

	protected abstract int S_IRUSR();

	protected abstract int S_IRWXG();

	protected abstract int S_IRWXO();

	protected abstract int S_IRWXU();

	protected abstract int S_ISGID();

	protected abstract int S_ISUID();

	protected abstract int S_ISVTX();

	protected abstract int S_IWGRP();

	protected abstract int S_IWOTH();

	protected abstract int S_IWUSR();

	protected abstract int S_IXGRP();

	protected abstract int S_IXOTH();

	protected abstract int S_IXUSR();

	protected abstract int SEEK_CUR();

	protected abstract int SEEK_END();;

	protected abstract int SEEK_SET();

	protected abstract int W_OK();

	protected abstract int X_OK();
}
