package de.ibapl.jnrheader.linux;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.ibapl.jnrheader.NativeDataType;
import de.ibapl.jnrheader.POSIX;
import de.ibapl.jnrheader.posix.Unistd_H;
import jnr.ffi.LibraryLoader;
import jnr.ffi.TypeAlias;
import jnr.ffi.annotations.TypeDefinition;
import jnr.ffi.types.int32_t;

//TODO ask for _SC_LONG_BIT etc ...
//map c long int => java long 
public class Unistd_Lib extends Unistd_H {
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = { ElementType.PARAMETER, ElementType.METHOD })
	@TypeDefinition(alias = TypeAlias.int64_t)
	@NativeDataType("long int")
	public @interface __off_t {
		
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = { ElementType.PARAMETER, ElementType.METHOD })
	@TypeDefinition(alias = TypeAlias.int64_t)
	@NativeDataType("long int")
	public @interface size_t {
		
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = { ElementType.PARAMETER, ElementType.METHOD })
	@TypeDefinition(alias = TypeAlias.u_int32_t)
	@NativeDataType("unsigned int")
	public @interface __useconds_t {
		
	}

	@de.ibapl.jnrheader.NativeFunctions
	protected interface NativeFunctions {
		@int32_t int close(@int32_t int fd);

		@size_t long pread(@int32_t int fildes, byte[] buf, @size_t long nbyte, @__off_t long offset);

		@size_t long pwrite(@int32_t int fildes, byte[] _buf, @size_t long nbyte, @__off_t long offset);

		@size_t public long read(@int32_t int fildes, byte[] buf, @size_t long nbyte);

		@size_t public long write(@int32_t int fildes, byte[] buf, @size_t long nbyte);
		
		@int32_t int usleep(@__useconds_t int useconds); 

		/*
		 * int access (String name, int type); int faccessat (int fd, const char * file,
		 * int type, int flag); __off_t lseek (int fd, __off_t offset, int whence); int pipe (int __pipedes[2]); unsigned int alarm (unsigned
		 * int __seconds); unsigned int sleep (unsigned int __seconds); __useconds_t
		 * ualarm (__useconds_t __value, __useconds_t __interval); int pause (void); int chown (CharSequence file,
		 * __uid_t __owner, __gid_t __group); int fchown (int __fd, __uid_t __owner,
		 * __gid_t __group); int lchown (CharSequence file, __uid_t __owner, __gid_t
		 * __group); int fchownat (int __fd, CharSequence file, __uid_t __owner, __gid_t
		 * __group, int __flag); int chdir (const char *__path); int fchdir (int __fd);
		 * char *getcwd (char *__buf, size_t __size); char *getwd (char *__buf); int dup
		 * (int __fd); int dup2 (int __fd, int __fd2); char **__environ; int execve
		 * (const char *__path, char *const __argv[], char *const __envp[]); int fexecve
		 * (int __fd, char *const __argv[], char *const __envp[]); int execv (const char
		 * *__path, char *const __argv[]); int execle (const char *__path, const char
		 * *__arg, ...); int execl (const char *__path, const char *__arg, ...); int
		 * execvp (const char *__file, char *const __argv[]); int execlp (const char
		 * *__file, const char *__arg, ...); int nice (int __inc); void _exit (int
		 * __status); long int pathconf (const char *__path, int __name); long int
		 * fpathconf (int __fd, int __name); long int sysconf (int __name); size_t
		 * confstr (int __name, char *__buf, size_t __len); __pid_t getpid (void);
		 * __pid_t getppid (void); __pid_t getpgrp (void); __pid_t __getpgid (__pid_t
		 * __pid); __pid_t getpgid (__pid_t __pid); int setpgid (__pid_t __pid, __pid_t
		 * __pgid); int setpgrp (void); __pid_t setsid (void); __pid_t getsid (__pid_t
		 * __pid); __uid_t getuid (void); __uid_t geteuid (void); __gid_t getgid (void);
		 * __gid_t getegid (void); int getgroups (int __size, __gid_t __list[]); int
		 * setuid (__uid_t __uid); int setreuid (__uid_t __ruid, __uid_t __euid); int
		 * seteuid (__uid_t __uid); int setgid (__gid_t __gid); int setregid (__gid_t
		 * __rgid, __gid_t __egid); int setegid (__gid_t __gid); __pid_t fork (void);
		 * __pid_t vfork (void); char *ttyname (int __fd); int ttyname_r (int __fd, char
		 * *__buf, size_t __buflen); int isatty (int __fd); int ttyslot (void); int link
		 * (const char *__from, const char *__to); int linkat (int __fromfd, const char
		 * *__from, int __tofd, const char *__to, int __flags); int symlink (const char
		 * *__from, const char *__to); ssize_t readlink (const char *__restrict __path,
		 * char *__restrict __buf, size_t __len); int symlinkat (const char *__from, int
		 * __tofd, const char *__to); ssize_t readlinkat (int __fd, const char
		 * *__restrict __path, char *__restrict __buf, size_t __len); int unlink (const
		 * char *__name); int unlinkat (int __fd, const char *__name, int __flag); int
		 * rmdir (const char *__path); __pid_t tcgetpgrp (int __fd); int tcsetpgrp (int
		 * __fd, __pid_t __pgrp_id); char *getlogin (void); int getlogin_r (char
		 * *__name, size_t __name_len); int setlogin (const char *__name); extern char
		 * *optarg; extern int optind; extern int opterr; extern int optopt; int getopt
		 * (int ___argc, char *const *___argv, const char *__shortopts); int gethostname
		 * (char *__name, size_t __len); int sethostname (const char *__name, size_t
		 * __len); int sethostid (long int __id); int getdomainname (char *__name,
		 * size_t __len); int setdomainname (const char *__name, size_t __len); int
		 * vhangup (void) __attribute__ ((__nothrow__ , __leaf__)); int revoke (const
		 * char *__file); int profil (unsigned short int *__sample_buffer, size_t
		 * __size, size_t __offset, unsigned int __scale); int acct (const char
		 * *__name); char *getusershell (void); void endusershell (void); void
		 * setusershell (void); int daemon (int __nochdir, int __noclose); int chroot
		 * (const char *__path); char *getpass (const char *__prompt); int fsync (int
		 * __fd); long int gethostid (void); void sync (void); int getpagesize (void);
		 * int getdtablesize (void); int truncate (const char *__file, __off_t
		 * __length); int ftruncate (int __fd, __off_t __length); int brk (void
		 * *__addr); void *sbrk (intptr_t __delta); long int syscall (long int __sysno,
		 * ...); int lockf (int __fd, int __cmd, __off_t __len) ; int fdatasync (int
		 * __fildes); int getentropy (void *__buffer, size_t __length) ;
		 */
	}

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
	public static final int L_SET = SEEK_SET;

	@POSIX
	public static final int L_INCR = SEEK_CUR;

	@POSIX
	public static final int L_XTND = SEEK_END;

	@POSIX
	public static final int _PC_LINK_MAX = 0;

	@POSIX
	public static final int _PC_MAX_CANON = 1;

	@POSIX
	public static final int _PC_MAX_INPUT = 2;

	@POSIX
	public static final int _PC_NAME_MAX = 3;

	@POSIX
	public static final int _PC_PATH_MAX = 4;

	@POSIX
	public static final int _PC_PIPE_BUF = 5;

	@POSIX
	public static final int _PC_CHOWN_RESTRICTED = 6;

	@POSIX
	public static final int _PC_NO_TRUNC = 7;

	@POSIX
	public static final int _PC_VDISABLE = 8;

	@POSIX
	public static final int _PC_SYNC_IO = 9;

	@POSIX
	public static final int _PC_ASYNC_IO = 10;

	@POSIX
	public static final int _PC_PRIO_IO = 11;

	@POSIX
	public static final int _PC_SOCK_MAXBUF = 12;

	@POSIX
	public static final int _PC_FILESIZEBITS = 13;

	@POSIX
	public static final int _PC_REC_INCR_XFER_SIZE = 14;

	@POSIX
	public static final int _PC_REC_MAX_XFER_SIZE = 15;

	@POSIX
	public static final int _PC_REC_MIN_XFER_SIZE = 16;

	@POSIX
	public static final int _PC_REC_XFER_ALIGN = 17;

	@POSIX
	public static final int _PC_ALLOC_SIZE_MIN = 18;

	@POSIX
	public static final int _PC_SYMLINK_MAX = 19;

	@POSIX
	public static final int _PC_2_SYMLINKS = 20;

	@POSIX
	public static final int _SC_ARG_MAX = 0;

	@POSIX
	public static final int _SC_CHILD_MAX = 1;

	@POSIX
	public static final int _SC_CLK_TCK = 2;

	@POSIX
	public static final int _SC_NGROUPS_MAX = 3;

	@POSIX
	public static final int _SC_OPEN_MAX = 4;

	@POSIX
	public static final int _SC_STREAM_MAX = 5;

	@POSIX
	public static final int _SC_TZNAME_MAX = 6;

	@POSIX
	public static final int _SC_JOB_CONTROL = 7;

	@POSIX
	public static final int _SC_SAVED_IDS = 8;

	@POSIX
	public static final int _SC_REALTIME_SIGNALS = 9;

	@POSIX
	public static final int _SC_PRIORITY_SCHEDULING = 10;

	@POSIX
	public static final int _SC_TIMERS = 11;

	@POSIX
	public static final int _SC_ASYNCHRONOUS_IO = 12;

	@POSIX
	public static final int _SC_PRIORITIZED_IO = 13;

	@POSIX
	public static final int _SC_SYNCHRONIZED_IO = 14;

	@POSIX
	public static final int _SC_FSYNC = 15;

	@POSIX
	public static final int _SC_MAPPED_FILES = 16;

	@POSIX
	public static final int _SC_MEMLOCK = 17;

	@POSIX
	public static final int _SC_MEMLOCK_RANGE = 18;

	@POSIX
	public static final int _SC_MEMORY_PROTECTION = 19;

	@POSIX
	public static final int _SC_MESSAGE_PASSING = 20;

	@POSIX
	public static final int _SC_SEMAPHORES = 21;

	@POSIX
	public static final int _SC_SHARED_MEMORY_OBJECTS = 22;

	@POSIX
	public static final int _SC_AIO_LISTIO_MAX = 23;

	@POSIX
	public static final int _SC_AIO_MAX = 24;

	@POSIX
	public static final int _SC_AIO_PRIO_DELTA_MAX = 25;

	@POSIX
	public static final int _SC_DELAYTIMER_MAX = 26;

	@POSIX
	public static final int _SC_MQ_OPEN_MAX = 27;

	@POSIX
	public static final int _SC_MQ_PRIO_MAX = 28;

	@POSIX
	public static final int _SC_VERSION = 29;

	@POSIX
	public static final int _SC_PAGESIZE = 30;

	@POSIX
	public static final int _SC_PAGE_SIZE = _SC_PAGESIZE;

	@POSIX
	public static final int _SC_RTSIG_MAX = 31;

	@POSIX
	public static final int _SC_SEM_NSEMS_MAX = 32;

	@POSIX
	public static final int _SC_SEM_VALUE_MAX = 33;

	@POSIX
	public static final int _SC_SIGQUEUE_MAX = 34;

	@POSIX
	public static final int _SC_TIMER_MAX = 35;

	@POSIX
	public static final int _SC_BC_BASE_MAX = 36;

	@POSIX
	public static final int _SC_BC_DIM_MAX = 37;

	@POSIX
	public static final int _SC_BC_SCALE_MAX = 38;

	@POSIX
	public static final int _SC_BC_STRING_MAX = 39;

	@POSIX
	public static final int _SC_COLL_WEIGHTS_MAX = 40;

	@POSIX
	public static final int _SC_EQUIV_CLASS_MAX = 41;

	@POSIX
	public static final int _SC_EXPR_NEST_MAX = 42;

	@POSIX
	public static final int _SC_LINE_MAX = 43;

	@POSIX
	public static final int _SC_RE_DUP_MAX = 44;

	@POSIX
	public static final int _SC_CHARCLASS_NAME_MAX = 45;

	@POSIX
	public static final int _SC_2_VERSION = 46;

	@POSIX
	public static final int _SC_2_C_BIND = 47;

	@POSIX
	public static final int _SC_2_C_DEV = 48;

	@POSIX
	public static final int _SC_2_FORT_DEV = 49;

	@POSIX
	public static final int _SC_2_FORT_RUN = 50;

	@POSIX
	public static final int _SC_2_SW_DEV = 51;

	@POSIX
	public static final int _SC_2_LOCALEDEF = 52;

	@POSIX
	public static final int _SC_PII = 53;

	@POSIX
	public static final int _SC_PII_XTI = 54;

	@POSIX
	public static final int _SC_PII_SOCKET = 55;

	@POSIX
	public static final int _SC_PII_INTERNET = 56;

	@POSIX
	public static final int _SC_PII_OSI = 57;

	@POSIX
	public static final int _SC_POLL = 58;

	@POSIX
	public static final int _SC_SELECT = 59;

	@POSIX
	public static final int _SC_UIO_MAXIOV = 60;

	@POSIX
	public static final int _SC_IOV_MAX = _SC_UIO_MAXIOV;

	@POSIX
	public static final int _SC_PII_INTERNET_STREAM = 61;

	@POSIX
	public static final int _SC_PII_INTERNET_DGRAM = 62;

	@POSIX
	public static final int _SC_PII_OSI_COTS = 63;

	@POSIX
	public static final int _SC_PII_OSI_CLTS = 64;

	@POSIX
	public static final int _SC_PII_OSI_M = 65;

	@POSIX
	public static final int _SC_T_IOV_MAX = 66;

	@POSIX
	public static final int _SC_THREADS = 67;

	@POSIX
	public static final int _SC_THREAD_SAFE_FUNCTIONS = 68;

	@POSIX
	public static final int _SC_GETGR_R_SIZE_MAX = 69;

	@POSIX
	public static final int _SC_GETPW_R_SIZE_MAX = 70;

	@POSIX
	public static final int _SC_LOGIN_NAME_MAX = 71;

	@POSIX
	public static final int _SC_TTY_NAME_MAX = 72;

	@POSIX
	public static final int _SC_THREAD_DESTRUCTOR_ITERATIONS = 73;

	@POSIX
	public static final int _SC_THREAD_KEYS_MAX = 74;

	@POSIX
	public static final int _SC_THREAD_STACK_MIN = 75;

	@POSIX
	public static final int _SC_THREAD_THREADS_MAX = 76;

	@POSIX
	public static final int _SC_THREAD_ATTR_STACKADDR = 77;

	@POSIX
	public static final int _SC_THREAD_ATTR_STACKSIZE = 78;

	@POSIX
	public static final int _SC_THREAD_PRIORITY_SCHEDULING = 79;

	@POSIX
	public static final int _SC_THREAD_PRIO_INHERIT = 80;

	@POSIX
	public static final int _SC_THREAD_PRIO_PROTECT = 81;

	@POSIX
	public static final int _SC_THREAD_PROCESS_SHARED = 82;

	@POSIX
	public static final int _SC_NPROCESSORS_CONF = 83;

	@POSIX
	public static final int _SC_NPROCESSORS_ONLN = 84;

	@POSIX
	public static final int _SC_PHYS_PAGES = 85;

	@POSIX
	public static final int _SC_AVPHYS_PAGES = 86;

	@POSIX
	public static final int _SC_ATEXIT_MAX = 87;

	@POSIX
	public static final int _SC_PASS_MAX = 88;

	@POSIX
	public static final int _SC_XOPEN_VERSION = 89;

	@POSIX
	public static final int _SC_XOPEN_XCU_VERSION = 90;

	@POSIX
	public static final int _SC_XOPEN_UNIX = 91;

	@POSIX
	public static final int _SC_XOPEN_CRYPT = 92;

	@POSIX
	public static final int _SC_XOPEN_ENH_I18N = 93;

	@POSIX
	public static final int _SC_XOPEN_SHM = 94;

	@POSIX
	public static final int _SC_2_CHAR_TERM = 95;

	@POSIX
	public static final int _SC_2_C_VERSION = 96;

	@POSIX
	public static final int _SC_2_UPE = 97;

	@POSIX
	public static final int _SC_XOPEN_XPG2 = 98;

	@POSIX
	public static final int _SC_XOPEN_XPG3 = 99;

	@POSIX
	public static final int _SC_XOPEN_XPG4 = 100;

	@POSIX
	public static final int _SC_CHAR_BIT = 101;

	@POSIX
	public static final int _SC_CHAR_MAX = 102;

	@POSIX
	public static final int _SC_CHAR_MIN = 103;

	@POSIX
	public static final int _SC_INT_MAX = 104;

	@POSIX
	public static final int _SC_INT_MIN = 105;

	@POSIX
	public static final int _SC_LONG_BIT = 106;

	@POSIX
	public static final int _SC_WORD_BIT = 107;

	@POSIX
	public static final int _SC_MB_LEN_MAX = 108;

	@POSIX
	public static final int _SC_NZERO = 109;

	@POSIX
	public static final int _SC_SSIZE_MAX = 110;

	@POSIX
	public static final int _SC_SCHAR_MAX = 111;

	@POSIX
	public static final int _SC_SCHAR_MIN = 112;

	@POSIX
	public static final int _SC_SHRT_MAX = 113;

	@POSIX
	public static final int _SC_SHRT_MIN = 114;

	@POSIX
	public static final int _SC_UCHAR_MAX = 115;

	@POSIX
	public static final int _SC_UINT_MAX = 116;

	@POSIX
	public static final int _SC_ULONG_MAX = 117;

	@POSIX
	public static final int _SC_USHRT_MAX = 118;

	@POSIX
	public static final int _SC_NL_ARGMAX = 119;

	@POSIX
	public static final int _SC_NL_LANGMAX = 120;

	@POSIX
	public static final int _SC_NL_MSGMAX = 121;

	@POSIX
	public static final int _SC_NL_NMAX = 122;

	@POSIX
	public static final int _SC_NL_SETMAX = 123;

	@POSIX
	public static final int _SC_NL_TEXTMAX = 124;

	@POSIX
	public static final int _SC_XBS5_ILP32_OFF32 = 125;

	@POSIX
	public static final int _SC_XBS5_ILP32_OFFBIG = 126;

	@POSIX
	public static final int _SC_XBS5_LP64_OFF64 = 127;

	@POSIX
	public static final int _SC_XBS5_LPBIG_OFFBIG = 128;

	@POSIX
	public static final int _SC_XOPEN_LEGACY = 129;

	@POSIX
	public static final int _SC_XOPEN_REALTIME = 130;

	@POSIX
	public static final int _SC_XOPEN_REALTIME_THREADS = 131;

	@POSIX
	public static final int _SC_ADVISORY_INFO = 132;

	@POSIX
	public static final int _SC_BARRIERS = 133;

	@POSIX
	public static final int _SC_BASE = 134;

	@POSIX
	public static final int _SC_C_LANG_SUPPORT = 135;

	@POSIX
	public static final int _SC_C_LANG_SUPPORT_R = 136;

	@POSIX
	public static final int _SC_CLOCK_SELECTION = 137;

	@POSIX
	public static final int _SC_CPUTIME = 138;

	@POSIX
	public static final int _SC_THREAD_CPUTIME = 139;

	@POSIX
	public static final int _SC_DEVICE_IO = 140;

	@POSIX
	public static final int _SC_DEVICE_SPECIFIC = 141;

	@POSIX
	public static final int _SC_DEVICE_SPECIFIC_R = 142;

	@POSIX
	public static final int _SC_FD_MGMT = 143;

	@POSIX
	public static final int _SC_FIFO = 144;

	@POSIX
	public static final int _SC_PIPE = 145;

	@POSIX
	public static final int _SC_FILE_ATTRIBUTES = 146;

	@POSIX
	public static final int _SC_FILE_LOCKING = 147;

	@POSIX
	public static final int _SC_FILE_SYSTEM = 148;

	@POSIX
	public static final int _SC_MONOTONIC_CLOCK = 149;

	@POSIX
	public static final int _SC_MULTI_PROCESS = 150;

	@POSIX
	public static final int _SC_SINGLE_PROCESS = 151;

	@POSIX
	public static final int _SC_NETWORKING = 152;

	@POSIX
	public static final int _SC_READER_WRITER_LOCKS = 153;

	@POSIX
	public static final int _SC_SPIN_LOCKS = 154;

	@POSIX
	public static final int _SC_REGEXP = 155;

	@POSIX
	public static final int _SC_REGEX_VERSION = 156;

	@POSIX
	public static final int _SC_SHELL = 157;

	@POSIX
	public static final int _SC_SIGNALS = 158;

	@POSIX
	public static final int _SC_SPAWN = 159;

	@POSIX
	public static final int _SC_SPORADIC_SERVER = 160;

	@POSIX
	public static final int _SC_THREAD_SPORADIC_SERVER = 161;

	@POSIX
	public static final int _SC_SYSTEM_DATABASE = 162;

	@POSIX
	public static final int _SC_SYSTEM_DATABASE_R = 163;

	@POSIX
	public static final int _SC_TIMEOUTS = 164;

	@POSIX
	public static final int _SC_TYPED_MEMORY_OBJECTS = 165;

	@POSIX
	public static final int _SC_USER_GROUPS = 166;

	@POSIX
	public static final int _SC_USER_GROUPS_R = 167;

	@POSIX
	public static final int _SC_2_PBS = 168;

	@POSIX
	public static final int _SC_2_PBS_ACCOUNTING = 169;

	@POSIX
	public static final int _SC_2_PBS_LOCATE = 170;

	@POSIX
	public static final int _SC_2_PBS_MESSAGE = 171;

	@POSIX
	public static final int _SC_2_PBS_TRACK = 172;

	@POSIX
	public static final int _SC_SYMLOOP_MAX = 173;

	@POSIX
	public static final int _SC_STREAMS = 174;

	@POSIX
	public static final int _SC_2_PBS_CHECKPOINT = 175;

	@POSIX
	public static final int _SC_V6_ILP32_OFF32 = 176;

	@POSIX
	public static final int _SC_V6_ILP32_OFFBIG = 177;

	@POSIX
	public static final int _SC_V6_LP64_OFF64 = 178;

	@POSIX
	public static final int _SC_V6_LPBIG_OFFBIG = 179;

	@POSIX
	public static final int _SC_HOST_NAME_MAX = 180;

	@POSIX
	public static final int _SC_TRACE = 181;

	@POSIX
	public static final int _SC_TRACE_EVENT_FILTER = 182;

	@POSIX
	public static final int _SC_TRACE_INHERIT = 183;

	@POSIX
	public static final int _SC_TRACE_LOG = 184;

	@POSIX
	public static final int _SC_LEVEL1_ICACHE_SIZE = 185;

	@POSIX
	public static final int _SC_LEVEL1_ICACHE_ASSOC = 186;

	@POSIX
	public static final int _SC_LEVEL1_ICACHE_LINESIZE = 187;

	@POSIX
	public static final int _SC_LEVEL1_DCACHE_SIZE = 188;

	@POSIX
	public static final int _SC_LEVEL1_DCACHE_ASSOC = 189;

	@POSIX
	public static final int _SC_LEVEL1_DCACHE_LINESIZE = 190;

	@POSIX
	public static final int _SC_LEVEL2_CACHE_SIZE = 191;

	@POSIX
	public static final int _SC_LEVEL2_CACHE_ASSOC = 192;

	@POSIX
	public static final int _SC_LEVEL2_CACHE_LINESIZE = 193;

	@POSIX
	public static final int _SC_LEVEL3_CACHE_SIZE = 194;

	@POSIX
	public static final int _SC_LEVEL3_CACHE_ASSOC = 195;

	@POSIX
	public static final int _SC_LEVEL3_CACHE_LINESIZE = 196;

	@POSIX
	public static final int _SC_LEVEL4_CACHE_SIZE = 197;

	@POSIX
	public static final int _SC_LEVEL4_CACHE_ASSOC = 198;

	@POSIX
	public static final int _SC_LEVEL4_CACHE_LINESIZE = 199;

	@POSIX
	public static final int _SC_IPV6 = 235;

	@POSIX
	public static final int _SC_RAW_SOCKETS = 236;

	@POSIX
	public static final int _SC_V7_ILP32_OFF32 = 237;

	@POSIX
	public static final int _SC_V7_ILP32_OFFBIG = 238;

	@POSIX
	public static final int _SC_V7_LP64_OFF64 = 239;

	@POSIX
	public static final int _SC_V7_LPBIG_OFFBIG = 240;

	@POSIX
	public static final int _SC_SS_REPL_MAX = 241;

	@POSIX
	public static final int _SC_TRACE_EVENT_NAME_MAX = 242;

	@POSIX
	public static final int _SC_TRACE_NAME_MAX = 243;

	@POSIX
	public static final int _SC_TRACE_SYS_MAX = 244;

	@POSIX
	public static final int _SC_TRACE_USER_EVENT_MAX = 245;

	@POSIX
	public static final int _SC_XOPEN_STREAMS = 246;

	@POSIX
	public static final int _SC_THREAD_ROBUST_PRIO_INHERIT = 247;

	@POSIX
	public static final int _SC_THREAD_ROBUST_PRIO_PROTECT = 248;

	@POSIX
	public static final int _CS_PATH = 0;

	@POSIX
	public static final int _CS_V6_WIDTH_RESTRICTED_ENVS = 1;

	@POSIX
	public static final int _CS_POSIX_V6_WIDTH_RESTRICTED_ENVS = _CS_V6_WIDTH_RESTRICTED_ENVS;

	@POSIX
	public static final int _CS_GNU_LIBC_VERSION = 2;

	@POSIX
	public static final int _CS_GNU_LIBPTHREAD_VERSION = 3;

	@POSIX
	public static final int _CS_V5_WIDTH_RESTRICTED_ENVS = 4;

	@POSIX
	public static final int _CS_POSIX_V5_WIDTH_RESTRICTED_ENVS = _CS_V5_WIDTH_RESTRICTED_ENVS;

	@POSIX
	public static final int _CS_V7_WIDTH_RESTRICTED_ENVS = 5;

	@POSIX
	public static final int _CS_POSIX_V7_WIDTH_RESTRICTED_ENVS = _CS_V7_WIDTH_RESTRICTED_ENVS;

	@POSIX
	public static final int _CS_LFS_CFLAGS = 1000;

	@POSIX
	public static final int _CS_LFS_LDFLAGS = 1001;

	@POSIX
	public static final int _CS_LFS_LIBS = 1002;

	@POSIX
	public static final int _CS_LFS_LINTFLAGS = 1003;

	@POSIX
	public static final int _CS_LFS64_CFLAGS = 1004;

	@POSIX
	public static final int _CS_LFS64_LDFLAGS = 1005;

	@POSIX
	public static final int _CS_LFS64_LIBS = 1006;

	@POSIX
	public static final int _CS_LFS64_LINTFLAGS = 1007;

	@POSIX
	public static final int _CS_XBS5_ILP32_OFF32_CFLAGS = 1100;

	@POSIX
	public static final int _CS_XBS5_ILP32_OFF32_LDFLAGS = 1101;

	@POSIX
	public static final int _CS_XBS5_ILP32_OFF32_LIBS = 1102;

	@POSIX
	public static final int _CS_XBS5_ILP32_OFF32_LINTFLAGS = 1103;

	@POSIX
	public static final int _CS_XBS5_ILP32_OFFBIG_CFLAGS = 1104;

	@POSIX
	public static final int _CS_XBS5_ILP32_OFFBIG_LDFLAGS = 1105;

	@POSIX
	public static final int _CS_XBS5_ILP32_OFFBIG_LIBS = 1106;

	@POSIX
	public static final int _CS_XBS5_ILP32_OFFBIG_LINTFLAGS = 1107;

	@POSIX
	public static final int _CS_XBS5_LP64_OFF64_CFLAGS = 1108;

	@POSIX
	public static final int _CS_XBS5_LP64_OFF64_LDFLAGS = 1109;

	@POSIX
	public static final int _CS_XBS5_LP64_OFF64_LIBS = 1110;

	@POSIX
	public static final int _CS_XBS5_LP64_OFF64_LINTFLAGS = 1111;

	@POSIX
	public static final int _CS_XBS5_LPBIG_OFFBIG_CFLAGS = 1112;

	@POSIX
	public static final int _CS_XBS5_LPBIG_OFFBIG_LDFLAGS = 1113;

	@POSIX
	public static final int _CS_XBS5_LPBIG_OFFBIG_LIBS = 1114;

	@POSIX
	public static final int _CS_XBS5_LPBIG_OFFBIG_LINTFLAGS = 1115;

	@POSIX
	public static final int _CS_POSIX_V6_ILP32_OFF32_CFLAGS = 1116;

	@POSIX
	public static final int _CS_POSIX_V6_ILP32_OFF32_LDFLAGS = 1117;

	@POSIX
	public static final int _CS_POSIX_V6_ILP32_OFF32_LIBS = 1118;

	@POSIX
	public static final int _CS_POSIX_V6_ILP32_OFF32_LINTFLAGS = 1119;

	@POSIX
	public static final int _CS_POSIX_V6_ILP32_OFFBIG_CFLAGS = 1120;

	@POSIX
	public static final int _CS_POSIX_V6_ILP32_OFFBIG_LDFLAGS = 1121;

	@POSIX
	public static final int _CS_POSIX_V6_ILP32_OFFBIG_LIBS = 1122;

	@POSIX
	public static final int _CS_POSIX_V6_ILP32_OFFBIG_LINTFLAGS = 1123;

	@POSIX
	public static final int _CS_POSIX_V6_LP64_OFF64_CFLAGS = 1124;

	@POSIX
	public static final int _CS_POSIX_V6_LP64_OFF64_LDFLAGS = 1125;

	@POSIX
	public static final int _CS_POSIX_V6_LP64_OFF64_LIBS = 1126;

	@POSIX
	public static final int _CS_POSIX_V6_LP64_OFF64_LINTFLAGS = 1127;

	@POSIX
	public static final int _CS_POSIX_V6_LPBIG_OFFBIG_CFLAGS = 1128;

	@POSIX
	public static final int _CS_POSIX_V6_LPBIG_OFFBIG_LDFLAGS = 1129;

	@POSIX
	public static final int _CS_POSIX_V6_LPBIG_OFFBIG_LIBS = 1130;

	@POSIX
	public static final int _CS_POSIX_V6_LPBIG_OFFBIG_LINTFLAGS = 1131;

	@POSIX
	public static final int _CS_POSIX_V7_ILP32_OFF32_CFLAGS = 1132;

	@POSIX
	public static final int _CS_POSIX_V7_ILP32_OFF32_LDFLAGS = 1133;

	@POSIX
	public static final int _CS_POSIX_V7_ILP32_OFF32_LIBS = 1134;

	@POSIX
	public static final int _CS_POSIX_V7_ILP32_OFF32_LINTFLAGS = 1135;

	@POSIX
	public static final int _CS_POSIX_V7_ILP32_OFFBIG_CFLAGS = 1136;

	@POSIX
	public static final int _CS_POSIX_V7_ILP32_OFFBIG_LDFLAGS = 1137;

	@POSIX
	public static final int _CS_POSIX_V7_ILP32_OFFBIG_LIBS = 1138;

	@POSIX
	public static final int _CS_POSIX_V7_ILP32_OFFBIG_LINTFLAGS = 1139;

	@POSIX
	public static final int _CS_POSIX_V7_LP64_OFF64_CFLAGS = 1140;

	@POSIX
	public static final int _CS_POSIX_V7_LP64_OFF64_LDFLAGS = 1141;

	@POSIX
	public static final int _CS_POSIX_V7_LP64_OFF64_LIBS = 1142;

	@POSIX
	public static final int _CS_POSIX_V7_LP64_OFF64_LINTFLAGS = 1143;

	@POSIX
	public static final int _CS_POSIX_V7_LPBIG_OFFBIG_CFLAGS = 1144;

	@POSIX
	public static final int _CS_POSIX_V7_LPBIG_OFFBIG_LDFLAGS = 1145;

	@POSIX
	public static final int _CS_POSIX_V7_LPBIG_OFFBIG_LIBS = 1146;

	@POSIX
	public static final int _CS_POSIX_V7_LPBIG_OFFBIG_LINTFLAGS = 1147;

	@POSIX
	public static final int _CS_V6_ENV = 1148;

	@POSIX
	public static final int _CS_V7_ENV = 1149;

	@POSIX
	public static final int F_ULOCK = 0;

	@POSIX
	public static final int F_LOCK = 1;

	@POSIX
	public static final int F_TLOCK = 2;

	@POSIX
	public static final int F_TEST = 3;

	private NativeFunctions nativeFunctions;

	public Unistd_Lib() {
		nativeFunctions = LibraryLoader.create(NativeFunctions.class).load("c");
	}

	@Override
	protected int _CS_GNU_LIBC_VERSION() {
		return Unistd_Lib._CS_GNU_LIBC_VERSION;
	}

	@Override
	protected int _CS_GNU_LIBPTHREAD_VERSION() {
		return Unistd_Lib._CS_GNU_LIBPTHREAD_VERSION;
	}

	@Override
	protected int _CS_LFS_CFLAGS() {
		return Unistd_Lib._CS_LFS_CFLAGS;
	}

	@Override
	protected int _CS_LFS_LDFLAGS() {
		return Unistd_Lib._CS_LFS_LDFLAGS;
	}

	@Override
	protected int _CS_LFS_LIBS() {
		return Unistd_Lib._CS_LFS_LIBS;
	}

	@Override
	protected int _CS_LFS_LINTFLAGS() {
		return Unistd_Lib._CS_LFS_LINTFLAGS;
	}

	@Override
	protected int _CS_LFS64_CFLAGS() {
		return Unistd_Lib._CS_LFS64_CFLAGS;
	}

	@Override
	protected int _CS_LFS64_LDFLAGS() {
		return Unistd_Lib._CS_LFS64_LDFLAGS;
	}

	@Override
	protected int _CS_LFS64_LIBS() {
		return Unistd_Lib._CS_LFS64_LIBS;
	}

	@Override
	protected int _CS_LFS64_LINTFLAGS() {
		return Unistd_Lib._CS_LFS64_LINTFLAGS;
	}

	@Override
	protected int _CS_PATH() {
		return Unistd_Lib._CS_PATH;
	}

	@Override
	protected int _CS_POSIX_V5_WIDTH_RESTRICTED_ENVS() {
		return Unistd_Lib._CS_POSIX_V5_WIDTH_RESTRICTED_ENVS;
	}

	@Override
	protected int _CS_POSIX_V6_ILP32_OFF32_CFLAGS() {
		return Unistd_Lib._CS_POSIX_V6_ILP32_OFF32_CFLAGS;
	}

	@Override
	protected int _CS_POSIX_V6_ILP32_OFF32_LDFLAGS() {
		return Unistd_Lib._CS_POSIX_V6_ILP32_OFF32_LDFLAGS;
	}

	@Override
	protected int _CS_POSIX_V6_ILP32_OFF32_LIBS() {
		return Unistd_Lib._CS_POSIX_V6_ILP32_OFF32_LIBS;
	}

	@Override
	protected int _CS_POSIX_V6_ILP32_OFF32_LINTFLAGS() {
		return Unistd_Lib._CS_POSIX_V6_ILP32_OFF32_LINTFLAGS;
	}

	@Override
	protected int _CS_POSIX_V6_ILP32_OFFBIG_CFLAGS() {
		return Unistd_Lib._CS_POSIX_V6_ILP32_OFFBIG_CFLAGS;
	}

	@Override
	protected int _CS_POSIX_V6_ILP32_OFFBIG_LDFLAGS() {
		return Unistd_Lib._CS_POSIX_V6_ILP32_OFFBIG_LDFLAGS;
	}

	@Override
	protected int _CS_POSIX_V6_ILP32_OFFBIG_LIBS() {
		return Unistd_Lib._CS_POSIX_V6_ILP32_OFFBIG_LIBS;
	}

	@Override
	protected int _CS_POSIX_V6_ILP32_OFFBIG_LINTFLAGS() {
		return Unistd_Lib._CS_POSIX_V6_ILP32_OFFBIG_LINTFLAGS;
	}

	@Override
	protected int _CS_POSIX_V6_LP64_OFF64_CFLAGS() {
		return Unistd_Lib._CS_POSIX_V6_LP64_OFF64_CFLAGS;
	}

	@Override
	protected int _CS_POSIX_V6_LP64_OFF64_LDFLAGS() {
		return Unistd_Lib._CS_POSIX_V6_LP64_OFF64_LDFLAGS;
	}

	@Override
	protected int _CS_POSIX_V6_LP64_OFF64_LIBS() {
		return Unistd_Lib._CS_POSIX_V6_LP64_OFF64_LIBS;
	}

	@Override
	protected int _CS_POSIX_V6_LP64_OFF64_LINTFLAGS() {
		return Unistd_Lib._CS_POSIX_V6_LP64_OFF64_LINTFLAGS;
	}

	@Override
	protected int _CS_POSIX_V6_LPBIG_OFFBIG_CFLAGS() {
		return Unistd_Lib._CS_POSIX_V6_LPBIG_OFFBIG_CFLAGS;
	}

	@Override
	protected int _CS_POSIX_V6_LPBIG_OFFBIG_LDFLAGS() {
		return Unistd_Lib._CS_POSIX_V6_LPBIG_OFFBIG_LDFLAGS;
	}

	@Override
	protected int _CS_POSIX_V6_LPBIG_OFFBIG_LIBS() {
		return Unistd_Lib._CS_POSIX_V6_LPBIG_OFFBIG_LIBS;
	}

	@Override
	protected int _CS_POSIX_V6_LPBIG_OFFBIG_LINTFLAGS() {
		return Unistd_Lib._CS_POSIX_V6_LPBIG_OFFBIG_LINTFLAGS;
	}

	@Override
	protected int _CS_POSIX_V6_WIDTH_RESTRICTED_ENVS() {
		return Unistd_Lib._CS_POSIX_V6_WIDTH_RESTRICTED_ENVS;
	}

	@Override
	protected int _CS_POSIX_V7_ILP32_OFF32_CFLAGS() {
		return Unistd_Lib._CS_POSIX_V7_ILP32_OFF32_CFLAGS;
	}

	@Override
	protected int _CS_POSIX_V7_ILP32_OFF32_LDFLAGS() {
		return Unistd_Lib._CS_POSIX_V7_ILP32_OFF32_LDFLAGS;
	}

	@Override
	protected int _CS_POSIX_V7_ILP32_OFF32_LIBS() {
		return Unistd_Lib._CS_POSIX_V7_ILP32_OFF32_LIBS;
	}

	@Override
	protected int _CS_POSIX_V7_ILP32_OFF32_LINTFLAGS() {
		return Unistd_Lib._CS_POSIX_V7_ILP32_OFF32_LINTFLAGS;
	}

	@Override
	protected int _CS_POSIX_V7_ILP32_OFFBIG_CFLAGS() {
		return Unistd_Lib._CS_POSIX_V7_ILP32_OFFBIG_CFLAGS;
	}

	@Override
	protected int _CS_POSIX_V7_ILP32_OFFBIG_LDFLAGS() {
		return Unistd_Lib._CS_POSIX_V7_ILP32_OFFBIG_LDFLAGS;
	}

	@Override
	protected int _CS_POSIX_V7_ILP32_OFFBIG_LIBS() {
		return Unistd_Lib._CS_POSIX_V7_ILP32_OFFBIG_LIBS;
	}

	@Override
	protected int _CS_POSIX_V7_ILP32_OFFBIG_LINTFLAGS() {
		return Unistd_Lib._CS_POSIX_V7_ILP32_OFFBIG_LINTFLAGS;
	}

	@Override
	protected int _CS_POSIX_V7_LP64_OFF64_CFLAGS() {
		return Unistd_Lib._CS_POSIX_V7_LP64_OFF64_CFLAGS;
	}

	@Override
	protected int _CS_POSIX_V7_LP64_OFF64_LDFLAGS() {
		return Unistd_Lib._CS_POSIX_V7_LP64_OFF64_LDFLAGS;
	}

	@Override
	protected int _CS_POSIX_V7_LP64_OFF64_LIBS() {
		return Unistd_Lib._CS_POSIX_V7_LP64_OFF64_LIBS;
	}

	@Override
	protected int _CS_POSIX_V7_LP64_OFF64_LINTFLAGS() {
		return Unistd_Lib._CS_POSIX_V7_LP64_OFF64_LINTFLAGS;
	}

	@Override
	protected int _CS_POSIX_V7_LPBIG_OFFBIG_CFLAGS() {
		return Unistd_Lib._CS_POSIX_V7_LPBIG_OFFBIG_CFLAGS;
	}

	@Override
	protected int _CS_POSIX_V7_LPBIG_OFFBIG_LDFLAGS() {
		return Unistd_Lib._CS_POSIX_V7_LPBIG_OFFBIG_LDFLAGS;
	}

	@Override
	protected int _CS_POSIX_V7_LPBIG_OFFBIG_LIBS() {
		return Unistd_Lib._CS_POSIX_V7_LPBIG_OFFBIG_LIBS;
	}

	@Override
	protected int _CS_POSIX_V7_LPBIG_OFFBIG_LINTFLAGS() {
		return Unistd_Lib._CS_POSIX_V7_LPBIG_OFFBIG_LINTFLAGS;
	}

	@Override
	protected int _CS_POSIX_V7_WIDTH_RESTRICTED_ENVS() {
		return Unistd_Lib._CS_POSIX_V7_WIDTH_RESTRICTED_ENVS;
	}

	@Override
	protected int _CS_V5_WIDTH_RESTRICTED_ENVS() {
		return Unistd_Lib._CS_V5_WIDTH_RESTRICTED_ENVS;
	}

	@Override
	protected int _CS_V6_ENV() {
		return Unistd_Lib._CS_V6_ENV;
	}

	@Override
	protected int _CS_V6_WIDTH_RESTRICTED_ENVS() {
		return Unistd_Lib._CS_V6_WIDTH_RESTRICTED_ENVS;
	}

	@Override
	protected int _CS_V7_ENV() {
		return Unistd_Lib._CS_V7_ENV;
	}

	@Override
	protected int _CS_V7_WIDTH_RESTRICTED_ENVS() {
		return Unistd_Lib._CS_V7_WIDTH_RESTRICTED_ENVS;
	}

	@Override
	protected int _CS_XBS5_ILP32_OFF32_CFLAGS() {
		return Unistd_Lib._CS_XBS5_ILP32_OFF32_CFLAGS;
	}

	@Override
	protected int _CS_XBS5_ILP32_OFF32_LDFLAGS() {
		return Unistd_Lib._CS_XBS5_ILP32_OFF32_LDFLAGS;
	}

	@Override
	protected int _CS_XBS5_ILP32_OFF32_LIBS() {
		return Unistd_Lib._CS_XBS5_ILP32_OFF32_LIBS;
	}

	@Override
	protected int _CS_XBS5_ILP32_OFF32_LINTFLAGS() {
		return Unistd_Lib._CS_XBS5_ILP32_OFF32_LINTFLAGS;
	}

	@Override
	protected int _CS_XBS5_ILP32_OFFBIG_CFLAGS() {
		return Unistd_Lib._CS_XBS5_ILP32_OFFBIG_CFLAGS;
	}

	@Override
	protected int _CS_XBS5_ILP32_OFFBIG_LDFLAGS() {
		return Unistd_Lib._CS_XBS5_ILP32_OFFBIG_LDFLAGS;
	}

	@Override
	protected int _CS_XBS5_ILP32_OFFBIG_LIBS() {
		return Unistd_Lib._CS_XBS5_ILP32_OFFBIG_LIBS;
	}

	@Override
	protected int _CS_XBS5_ILP32_OFFBIG_LINTFLAGS() {
		return Unistd_Lib._CS_XBS5_ILP32_OFFBIG_LINTFLAGS;
	}

	@Override
	protected int _CS_XBS5_LP64_OFF64_CFLAGS() {
		return Unistd_Lib._CS_XBS5_LP64_OFF64_CFLAGS;
	}

	@Override
	protected int _CS_XBS5_LP64_OFF64_LDFLAGS() {
		return Unistd_Lib._CS_XBS5_LP64_OFF64_LDFLAGS;
	}

	@Override
	protected int _CS_XBS5_LP64_OFF64_LIBS() {
		return Unistd_Lib._CS_XBS5_LP64_OFF64_LIBS;
	}

	@Override
	protected int _CS_XBS5_LP64_OFF64_LINTFLAGS() {
		return Unistd_Lib._CS_XBS5_LP64_OFF64_LINTFLAGS;
	}

	@Override
	protected int _CS_XBS5_LPBIG_OFFBIG_CFLAGS() {
		return Unistd_Lib._CS_XBS5_LPBIG_OFFBIG_CFLAGS;
	}

	@Override
	protected int _CS_XBS5_LPBIG_OFFBIG_LDFLAGS() {
		return Unistd_Lib._CS_XBS5_LPBIG_OFFBIG_LDFLAGS;
	}

	@Override
	protected int _CS_XBS5_LPBIG_OFFBIG_LIBS() {
		return Unistd_Lib._CS_XBS5_LPBIG_OFFBIG_LIBS;
	}

	@Override
	protected int _CS_XBS5_LPBIG_OFFBIG_LINTFLAGS() {
		return Unistd_Lib._CS_XBS5_LPBIG_OFFBIG_LINTFLAGS;
	}

	@Override
	protected int _PC_2_SYMLINKS() {
		return Unistd_Lib._PC_2_SYMLINKS;
	}

	@Override
	protected int _PC_ALLOC_SIZE_MIN() {
		return Unistd_Lib._PC_ALLOC_SIZE_MIN;
	}

	@Override
	protected int _PC_ASYNC_IO() {
		return Unistd_Lib._PC_ASYNC_IO;
	}

	@Override
	protected int _PC_CHOWN_RESTRICTED() {
		return Unistd_Lib._PC_CHOWN_RESTRICTED;
	}

	@Override
	protected int _PC_FILESIZEBITS() {
		return Unistd_Lib._PC_FILESIZEBITS;
	}

	@Override
	protected int _PC_LINK_MAX() {
		return Unistd_Lib._PC_LINK_MAX;
	}

	@Override
	protected int _PC_MAX_CANON() {
		return Unistd_Lib._PC_MAX_CANON;
	}

	@Override
	protected int _PC_MAX_INPUT() {
		return Unistd_Lib._PC_MAX_INPUT;
	}

	@Override
	protected int _PC_NAME_MAX() {
		return Unistd_Lib._PC_NAME_MAX;
	}

	@Override
	protected int _PC_NO_TRUNC() {
		return Unistd_Lib._PC_NO_TRUNC;
	}

	@Override
	protected int _PC_PATH_MAX() {
		return Unistd_Lib._PC_PATH_MAX;
	}

	@Override
	protected int _PC_PIPE_BUF() {
		return Unistd_Lib._PC_PIPE_BUF;
	}

	@Override
	protected int _PC_PRIO_IO() {
		return Unistd_Lib._PC_PRIO_IO;
	}

	@Override
	protected int _PC_REC_INCR_XFER_SIZE() {
		return Unistd_Lib._PC_REC_INCR_XFER_SIZE;
	}

	@Override
	protected int _PC_REC_MAX_XFER_SIZE() {
		return Unistd_Lib._PC_REC_MAX_XFER_SIZE;
	}

	@Override
	protected int _PC_REC_MIN_XFER_SIZE() {
		return Unistd_Lib._PC_REC_MIN_XFER_SIZE;
	}

	@Override
	protected int _PC_REC_XFER_ALIGN() {
		return Unistd_Lib._PC_REC_XFER_ALIGN;
	}

	@Override
	protected int _PC_SOCK_MAXBUF() {
		return Unistd_Lib._PC_SOCK_MAXBUF;
	}

	@Override
	protected int _PC_SYMLINK_MAX() {
		return Unistd_Lib._PC_SYMLINK_MAX;
	}

	@Override
	protected int _PC_SYNC_IO() {
		return Unistd_Lib._PC_SYNC_IO;
	}

	@Override
	protected int _PC_VDISABLE() {
		return Unistd_Lib._PC_VDISABLE;
	}

	@Override
	protected int _SC_2_C_BIND() {
		return Unistd_Lib._SC_2_C_BIND;
	}

	@Override
	protected int _SC_2_C_DEV() {
		return Unistd_Lib._SC_2_C_DEV;
	}

	@Override
	protected int _SC_2_C_VERSION() {
		return Unistd_Lib._SC_2_C_VERSION;
	}

	@Override
	protected int _SC_2_CHAR_TERM() {
		return Unistd_Lib._SC_2_CHAR_TERM;
	}

	@Override
	protected int _SC_2_FORT_DEV() {
		return Unistd_Lib._SC_2_FORT_DEV;
	}

	@Override
	protected int _SC_2_FORT_RUN() {
		return Unistd_Lib._SC_2_FORT_RUN;
	}

	@Override
	protected int _SC_2_LOCALEDEF() {
		return Unistd_Lib._SC_2_LOCALEDEF;
	}

	@Override
	protected int _SC_2_PBS() {
		return Unistd_Lib._SC_2_PBS;
	}

	@Override
	protected int _SC_2_PBS_ACCOUNTING() {
		return Unistd_Lib._SC_2_PBS_ACCOUNTING;
	}

	@Override
	protected int _SC_2_PBS_CHECKPOINT() {
		return Unistd_Lib._SC_2_PBS_CHECKPOINT;
	}

	@Override
	protected int _SC_2_PBS_LOCATE() {
		return Unistd_Lib._SC_2_PBS_LOCATE;
	}

	@Override
	protected int _SC_2_PBS_MESSAGE() {
		return Unistd_Lib._SC_2_PBS_MESSAGE;
	}

	@Override
	protected int _SC_2_PBS_TRACK() {
		return Unistd_Lib._SC_2_PBS_TRACK;
	}

	@Override
	protected int _SC_2_SW_DEV() {
		return Unistd_Lib._SC_2_SW_DEV;
	}

	@Override
	protected int _SC_2_UPE() {
		return Unistd_Lib._SC_2_UPE;
	}

	@Override
	protected int _SC_2_VERSION() {
		return Unistd_Lib._SC_2_VERSION;
	}

	@Override
	protected int _SC_ADVISORY_INFO() {
		return Unistd_Lib._SC_ADVISORY_INFO;
	}

	@Override
	protected int _SC_AIO_LISTIO_MAX() {
		return Unistd_Lib._SC_AIO_LISTIO_MAX;
	}

	@Override
	protected int _SC_AIO_MAX() {
		return Unistd_Lib._SC_AIO_MAX;
	}

	@Override
	protected int _SC_AIO_PRIO_DELTA_MAX() {
		return Unistd_Lib._SC_AIO_PRIO_DELTA_MAX;
	}

	@Override
	protected int _SC_ARG_MAX() {
		return Unistd_Lib._SC_ARG_MAX;
	}

	@Override
	protected int _SC_ASYNCHRONOUS_IO() {
		return Unistd_Lib._SC_ASYNCHRONOUS_IO;
	}

	@Override
	protected int _SC_ATEXIT_MAX() {
		return Unistd_Lib._SC_ATEXIT_MAX;
	}

	@Override
	protected int _SC_AVPHYS_PAGES() {
		return Unistd_Lib._SC_AVPHYS_PAGES;
	}

	@Override
	protected int _SC_BARRIERS() {
		return Unistd_Lib._SC_BARRIERS;
	}

	@Override
	protected int _SC_BASE() {
		return Unistd_Lib._SC_BASE;
	}

	@Override
	protected int _SC_BC_BASE_MAX() {
		return Unistd_Lib._SC_BC_BASE_MAX;
	}

	@Override
	protected int _SC_BC_DIM_MAX() {
		return Unistd_Lib._SC_BC_DIM_MAX;
	}

	@Override
	protected int _SC_BC_SCALE_MAX() {
		return Unistd_Lib._SC_BC_SCALE_MAX;
	}

	@Override
	protected int _SC_BC_STRING_MAX() {
		return Unistd_Lib._SC_BC_STRING_MAX;
	}

	@Override
	protected int _SC_C_LANG_SUPPORT() {
		return Unistd_Lib._SC_C_LANG_SUPPORT;
	}

	@Override
	protected int _SC_C_LANG_SUPPORT_R() {
		return Unistd_Lib._SC_C_LANG_SUPPORT_R;
	}

	@Override
	protected int _SC_CHAR_BIT() {
		return Unistd_Lib._SC_CHAR_BIT;
	}

	@Override
	protected int _SC_CHAR_MAX() {
		return Unistd_Lib._SC_CHAR_MAX;
	}

	@Override
	protected int _SC_CHAR_MIN() {
		return Unistd_Lib._SC_CHAR_MIN;
	}

	@Override
	protected int _SC_CHARCLASS_NAME_MAX() {
		return Unistd_Lib._SC_CHARCLASS_NAME_MAX;
	}

	@Override
	protected int _SC_CHILD_MAX() {
		return Unistd_Lib._SC_CHILD_MAX;
	}

	@Override
	protected int _SC_CLK_TCK() {
		return Unistd_Lib._SC_CLK_TCK;
	}

	@Override
	protected int _SC_CLOCK_SELECTION() {
		return Unistd_Lib._SC_CLOCK_SELECTION;
	}

	@Override
	protected int _SC_COLL_WEIGHTS_MAX() {
		return Unistd_Lib._SC_COLL_WEIGHTS_MAX;
	}

	@Override
	protected int _SC_CPUTIME() {
		return Unistd_Lib._SC_CPUTIME;
	}

	@Override
	protected int _SC_DELAYTIMER_MAX() {
		return Unistd_Lib._SC_DELAYTIMER_MAX;
	}

	@Override
	protected int _SC_DEVICE_IO() {
		return Unistd_Lib._SC_DEVICE_IO;
	}

	@Override
	protected int _SC_DEVICE_SPECIFIC() {
		return Unistd_Lib._SC_DEVICE_SPECIFIC;
	}

	@Override
	protected int _SC_DEVICE_SPECIFIC_R() {
		return Unistd_Lib._SC_DEVICE_SPECIFIC_R;
	}

	@Override
	protected int _SC_EQUIV_CLASS_MAX() {
		return Unistd_Lib._SC_EQUIV_CLASS_MAX;
	}

	@Override
	protected int _SC_EXPR_NEST_MAX() {
		return Unistd_Lib._SC_EXPR_NEST_MAX;
	}

	@Override
	protected int _SC_FD_MGMT() {
		return Unistd_Lib._SC_FD_MGMT;
	}

	@Override
	protected int _SC_FIFO() {
		return Unistd_Lib._SC_FIFO;
	}

	@Override
	protected int _SC_FILE_ATTRIBUTES() {
		return Unistd_Lib._SC_FILE_ATTRIBUTES;
	}

	@Override
	protected int _SC_FILE_LOCKING() {
		return Unistd_Lib._SC_FILE_LOCKING;
	}

	@Override
	protected int _SC_FILE_SYSTEM() {
		return Unistd_Lib._SC_FILE_SYSTEM;
	}

	@Override
	protected int _SC_FSYNC() {
		return Unistd_Lib._SC_FSYNC;
	}

	@Override
	protected int _SC_GETGR_R_SIZE_MAX() {
		return Unistd_Lib._SC_GETGR_R_SIZE_MAX;
	}

	@Override
	protected int _SC_GETPW_R_SIZE_MAX() {
		return Unistd_Lib._SC_GETPW_R_SIZE_MAX;
	}

	@Override
	protected int _SC_HOST_NAME_MAX() {
		return Unistd_Lib._SC_HOST_NAME_MAX;
	}

	@Override
	protected int _SC_INT_MAX() {
		return Unistd_Lib._SC_INT_MAX;
	}

	@Override
	protected int _SC_INT_MIN() {
		return Unistd_Lib._SC_INT_MIN;
	}

	@Override
	protected int _SC_IOV_MAX() {
		return Unistd_Lib._SC_IOV_MAX;
	}

	@Override
	protected int _SC_IPV6() {
		return Unistd_Lib._SC_IPV6;
	}

	@Override
	protected int _SC_JOB_CONTROL() {
		return Unistd_Lib._SC_JOB_CONTROL;
	}

	@Override
	protected int _SC_LEVEL1_DCACHE_ASSOC() {
		return Unistd_Lib._SC_LEVEL1_DCACHE_ASSOC;
	}

	@Override
	protected int _SC_LEVEL1_DCACHE_LINESIZE() {
		return Unistd_Lib._SC_LEVEL1_DCACHE_LINESIZE;
	}

	@Override
	protected int _SC_LEVEL1_DCACHE_SIZE() {
		return Unistd_Lib._SC_LEVEL1_DCACHE_SIZE;
	}

	@Override
	protected int _SC_LEVEL1_ICACHE_ASSOC() {
		return Unistd_Lib._SC_LEVEL1_ICACHE_ASSOC;
	}

	@Override
	protected int _SC_LEVEL1_ICACHE_LINESIZE() {
		return Unistd_Lib._SC_LEVEL1_ICACHE_LINESIZE;
	}

	@Override
	protected int _SC_LEVEL1_ICACHE_SIZE() {
		return Unistd_Lib._SC_LEVEL1_ICACHE_SIZE;
	}

	@Override
	protected int _SC_LEVEL2_CACHE_ASSOC() {
		return Unistd_Lib._SC_LEVEL2_CACHE_ASSOC;
	}

	@Override
	protected int _SC_LEVEL2_CACHE_LINESIZE() {
		return Unistd_Lib._SC_LEVEL2_CACHE_LINESIZE;
	}

	@Override
	protected int _SC_LEVEL2_CACHE_SIZE() {
		return Unistd_Lib._SC_LEVEL2_CACHE_SIZE;
	}

	@Override
	protected int _SC_LEVEL3_CACHE_ASSOC() {
		return Unistd_Lib._SC_LEVEL3_CACHE_ASSOC;
	}

	@Override
	protected int _SC_LEVEL3_CACHE_LINESIZE() {
		return Unistd_Lib._SC_LEVEL3_CACHE_LINESIZE;
	}

	@Override
	protected int _SC_LEVEL3_CACHE_SIZE() {
		return Unistd_Lib._SC_LEVEL3_CACHE_SIZE;
	}

	@Override
	protected int _SC_LEVEL4_CACHE_ASSOC() {
		return Unistd_Lib._SC_LEVEL4_CACHE_ASSOC;
	}

	@Override
	protected int _SC_LEVEL4_CACHE_LINESIZE() {
		return Unistd_Lib._SC_LEVEL4_CACHE_LINESIZE;
	}

	@Override
	protected int _SC_LEVEL4_CACHE_SIZE() {
		return Unistd_Lib._SC_LEVEL4_CACHE_SIZE;
	}

	@Override
	protected int _SC_LINE_MAX() {
		return Unistd_Lib._SC_LINE_MAX;
	}

	@Override
	protected int _SC_LOGIN_NAME_MAX() {
		return Unistd_Lib._SC_LOGIN_NAME_MAX;
	}

	@Override
	protected int _SC_LONG_BIT() {
		return Unistd_Lib._SC_LONG_BIT;
	}

	@Override
	protected int _SC_MAPPED_FILES() {
		return Unistd_Lib._SC_MAPPED_FILES;
	}

	@Override
	protected int _SC_MB_LEN_MAX() {
		return Unistd_Lib._SC_MB_LEN_MAX;
	}

	@Override
	protected int _SC_MEMLOCK() {
		return Unistd_Lib._SC_MEMLOCK;
	}

	@Override
	protected int _SC_MEMLOCK_RANGE() {
		return Unistd_Lib._SC_MEMLOCK_RANGE;
	}

	@Override
	protected int _SC_MEMORY_PROTECTION() {
		return Unistd_Lib._SC_MEMORY_PROTECTION;
	}

	@Override
	protected int _SC_MESSAGE_PASSING() {
		return Unistd_Lib._SC_MESSAGE_PASSING;
	}

	@Override
	protected int _SC_MONOTONIC_CLOCK() {
		return Unistd_Lib._SC_MONOTONIC_CLOCK;
	}

	@Override
	protected int _SC_MQ_OPEN_MAX() {
		return Unistd_Lib._SC_MQ_OPEN_MAX;
	}

	@Override
	protected int _SC_MQ_PRIO_MAX() {
		return Unistd_Lib._SC_MQ_PRIO_MAX;
	}

	@Override
	protected int _SC_MULTI_PROCESS() {
		return Unistd_Lib._SC_MULTI_PROCESS;
	}

	@Override
	protected int _SC_NETWORKING() {
		return Unistd_Lib._SC_NETWORKING;
	}

	@Override
	protected int _SC_NGROUPS_MAX() {
		return Unistd_Lib._SC_NGROUPS_MAX;
	}

	@Override
	protected int _SC_NL_ARGMAX() {
		return Unistd_Lib._SC_NL_ARGMAX;
	}

	@Override
	protected int _SC_NL_LANGMAX() {
		return Unistd_Lib._SC_NL_LANGMAX;
	}

	@Override
	protected int _SC_NL_MSGMAX() {
		return Unistd_Lib._SC_NL_MSGMAX;
	}

	@Override
	protected int _SC_NL_NMAX() {
		return Unistd_Lib._SC_NL_NMAX;
	}

	@Override
	protected int _SC_NL_SETMAX() {
		return Unistd_Lib._SC_NL_SETMAX;
	}

	@Override
	protected int _SC_NL_TEXTMAX() {
		return Unistd_Lib._SC_NL_TEXTMAX;
	}

	@Override
	protected int _SC_NPROCESSORS_CONF() {
		return Unistd_Lib._SC_NPROCESSORS_CONF;
	}

	@Override
	protected int _SC_NPROCESSORS_ONLN() {
		return Unistd_Lib._SC_NPROCESSORS_ONLN;
	}

	@Override
	protected int _SC_NZERO() {
		return Unistd_Lib._SC_NZERO;
	}

	@Override
	protected int _SC_OPEN_MAX() {
		return Unistd_Lib._SC_OPEN_MAX;
	}

	@Override
	protected int _SC_PAGE_SIZE() {
		return Unistd_Lib._SC_PAGE_SIZE;
	}

	@Override
	protected int _SC_PAGESIZE() {
		return Unistd_Lib._SC_PAGESIZE;
	}

	@Override
	protected int _SC_PASS_MAX() {
		return Unistd_Lib._SC_PASS_MAX;
	}

	@Override
	protected int _SC_PHYS_PAGES() {
		return Unistd_Lib._SC_PHYS_PAGES;
	}

	@Override
	protected int _SC_PII() {
		return Unistd_Lib._SC_PII;
	}

	@Override
	protected int _SC_PII_INTERNET() {
		return Unistd_Lib._SC_PII_INTERNET;
	}

	@Override
	protected int _SC_PII_INTERNET_DGRAM() {
		return Unistd_Lib._SC_PII_INTERNET_DGRAM;
	}

	@Override
	protected int _SC_PII_INTERNET_STREAM() {
		return Unistd_Lib._SC_PII_INTERNET_STREAM;
	}

	@Override
	protected int _SC_PII_OSI() {
		return Unistd_Lib._SC_PII_OSI;
	}

	@Override
	protected int _SC_PII_OSI_CLTS() {
		return Unistd_Lib._SC_PII_OSI_CLTS;
	}

	@Override
	protected int _SC_PII_OSI_COTS() {
		return Unistd_Lib._SC_PII_OSI_COTS;
	}

	@Override
	protected int _SC_PII_OSI_M() {
		return Unistd_Lib._SC_PII_OSI_M;
	}

	@Override
	protected int _SC_PII_SOCKET() {
		return Unistd_Lib._SC_PII_SOCKET;
	}

	@Override
	protected int _SC_PII_XTI() {
		return Unistd_Lib._SC_PII_XTI;
	}

	@Override
	protected int _SC_PIPE() {
		return Unistd_Lib._SC_PIPE;
	}

	@Override
	protected int _SC_POLL() {
		return Unistd_Lib._SC_POLL;
	}

	@Override
	protected int _SC_PRIORITIZED_IO() {
		return Unistd_Lib._SC_PRIORITIZED_IO;
	}

	@Override
	protected int _SC_PRIORITY_SCHEDULING() {
		return Unistd_Lib._SC_PRIORITY_SCHEDULING;
	}

	@Override
	protected int _SC_RAW_SOCKETS() {
		return Unistd_Lib._SC_RAW_SOCKETS;
	}

	@Override
	protected int _SC_RE_DUP_MAX() {
		return Unistd_Lib._SC_RE_DUP_MAX;
	}

	@Override
	protected int _SC_READER_WRITER_LOCKS() {
		return Unistd_Lib._SC_READER_WRITER_LOCKS;
	}

	@Override
	protected int _SC_REALTIME_SIGNALS() {
		return Unistd_Lib._SC_REALTIME_SIGNALS;
	}

	@Override
	protected int _SC_REGEX_VERSION() {
		return Unistd_Lib._SC_REGEX_VERSION;
	}

	@Override
	protected int _SC_REGEXP() {
		return Unistd_Lib._SC_REGEXP;
	}

	@Override
	protected int _SC_RTSIG_MAX() {
		return Unistd_Lib._SC_RTSIG_MAX;
	}

	@Override
	protected int _SC_SAVED_IDS() {
		return Unistd_Lib._SC_SAVED_IDS;
	}

	@Override
	protected int _SC_SCHAR_MAX() {
		return Unistd_Lib._SC_SCHAR_MAX;
	}

	@Override
	protected int _SC_SCHAR_MIN() {
		return Unistd_Lib._SC_SCHAR_MIN;
	}

	@Override
	protected int _SC_SELECT() {
		return Unistd_Lib._SC_SELECT;
	}

	@Override
	protected int _SC_SEM_NSEMS_MAX() {
		return Unistd_Lib._SC_SEM_NSEMS_MAX;
	}

	@Override
	protected int _SC_SEM_VALUE_MAX() {
		return Unistd_Lib._SC_SEM_VALUE_MAX;
	}

	@Override
	protected int _SC_SEMAPHORES() {
		return Unistd_Lib._SC_SEMAPHORES;
	}

	@Override
	protected int _SC_SHARED_MEMORY_OBJECTS() {
		return Unistd_Lib._SC_SHARED_MEMORY_OBJECTS;
	}

	@Override
	protected int _SC_SHELL() {
		return Unistd_Lib._SC_SHELL;
	}

	@Override
	protected int _SC_SHRT_MAX() {
		return Unistd_Lib._SC_SHRT_MAX;
	}

	@Override
	protected int _SC_SHRT_MIN() {
		return Unistd_Lib._SC_SHRT_MIN;
	}

	@Override
	protected int _SC_SIGNALS() {
		return Unistd_Lib._SC_SIGNALS;
	}

	@Override
	protected int _SC_SIGQUEUE_MAX() {
		return Unistd_Lib._SC_SIGQUEUE_MAX;
	}

	@Override
	protected int _SC_SINGLE_PROCESS() {
		return Unistd_Lib._SC_SINGLE_PROCESS;
	}

	@Override
	protected int _SC_SPAWN() {
		return Unistd_Lib._SC_SPAWN;
	}

	@Override
	protected int _SC_SPIN_LOCKS() {
		return Unistd_Lib._SC_SPIN_LOCKS;
	}

	@Override
	protected int _SC_SPORADIC_SERVER() {
		return Unistd_Lib._SC_SPORADIC_SERVER;
	}

	@Override
	protected int _SC_SS_REPL_MAX() {
		return Unistd_Lib._SC_SS_REPL_MAX;
	}

	@Override
	protected int _SC_SSIZE_MAX() {
		return Unistd_Lib._SC_SSIZE_MAX;
	}

	@Override
	protected int _SC_STREAM_MAX() {
		return Unistd_Lib._SC_STREAM_MAX;
	}

	@Override
	protected int _SC_STREAMS() {
		return Unistd_Lib._SC_STREAMS;
	}

	@Override
	protected int _SC_SYMLOOP_MAX() {
		return Unistd_Lib._SC_SYMLOOP_MAX;
	}

	@Override
	protected int _SC_SYNCHRONIZED_IO() {
		return Unistd_Lib._SC_SYNCHRONIZED_IO;
	}

	@Override
	protected int _SC_SYSTEM_DATABASE() {
		return Unistd_Lib._SC_SYSTEM_DATABASE;
	}

	@Override
	protected int _SC_SYSTEM_DATABASE_R() {
		return Unistd_Lib._SC_SYSTEM_DATABASE_R;
	}

	@Override
	protected int _SC_T_IOV_MAX() {
		return Unistd_Lib._SC_T_IOV_MAX;
	}

	@Override
	protected int _SC_THREAD_ATTR_STACKADDR() {
		return Unistd_Lib._SC_THREAD_ATTR_STACKADDR;
	}

	@Override
	protected int _SC_THREAD_ATTR_STACKSIZE() {
		return Unistd_Lib._SC_THREAD_ATTR_STACKSIZE;
	}

	@Override
	protected int _SC_THREAD_CPUTIME() {
		return Unistd_Lib._SC_THREAD_CPUTIME;
	}

	@Override
	protected int _SC_THREAD_DESTRUCTOR_ITERATIONS() {
		return Unistd_Lib._SC_THREAD_DESTRUCTOR_ITERATIONS;
	}

	@Override
	protected int _SC_THREAD_KEYS_MAX() {
		return Unistd_Lib._SC_THREAD_KEYS_MAX;
	}

	@Override
	protected int _SC_THREAD_PRIO_INHERIT() {
		return Unistd_Lib._SC_THREAD_PRIO_INHERIT;
	}

	@Override
	protected int _SC_THREAD_PRIO_PROTECT() {
		return Unistd_Lib._SC_THREAD_PRIO_PROTECT;
	}

	@Override
	protected int _SC_THREAD_PRIORITY_SCHEDULING() {
		return Unistd_Lib._SC_THREAD_PRIORITY_SCHEDULING;
	}

	@Override
	protected int _SC_THREAD_PROCESS_SHARED() {
		return Unistd_Lib._SC_THREAD_PROCESS_SHARED;
	}

	@Override
	protected int _SC_THREAD_ROBUST_PRIO_INHERIT() {
		return Unistd_Lib._SC_THREAD_ROBUST_PRIO_INHERIT;
	}

	@Override
	protected int _SC_THREAD_ROBUST_PRIO_PROTECT() {
		return Unistd_Lib._SC_THREAD_ROBUST_PRIO_PROTECT;
	}

	@Override
	protected int _SC_THREAD_SAFE_FUNCTIONS() {
		return Unistd_Lib._SC_THREAD_SAFE_FUNCTIONS;
	}

	@Override
	protected int _SC_THREAD_SPORADIC_SERVER() {
		return Unistd_Lib._SC_THREAD_SPORADIC_SERVER;
	}

	@Override
	protected int _SC_THREAD_STACK_MIN() {
		return Unistd_Lib._SC_THREAD_STACK_MIN;
	}

	@Override
	protected int _SC_THREAD_THREADS_MAX() {
		return Unistd_Lib._SC_THREAD_THREADS_MAX;
	}

	@Override
	protected int _SC_THREADS() {
		return Unistd_Lib._SC_THREADS;
	}

	@Override
	protected int _SC_TIMEOUTS() {
		return Unistd_Lib._SC_TIMEOUTS;
	}

	@Override
	protected int _SC_TIMER_MAX() {
		return Unistd_Lib._SC_TIMER_MAX;
	}

	@Override
	protected int _SC_TIMERS() {
		return Unistd_Lib._SC_TIMERS;
	}

	@Override
	protected int _SC_TRACE() {
		return Unistd_Lib._SC_TRACE;
	}

	@Override
	protected int _SC_TRACE_EVENT_FILTER() {
		return Unistd_Lib._SC_TRACE_EVENT_FILTER;
	}

	@Override
	protected int _SC_TRACE_EVENT_NAME_MAX() {
		return Unistd_Lib._SC_TRACE_EVENT_NAME_MAX;
	}

	@Override
	protected int _SC_TRACE_INHERIT() {
		return Unistd_Lib._SC_TRACE_INHERIT;
	}

	@Override
	protected int _SC_TRACE_LOG() {
		return Unistd_Lib._SC_TRACE_LOG;
	}

	@Override
	protected int _SC_TRACE_NAME_MAX() {
		return Unistd_Lib._SC_TRACE_NAME_MAX;
	}

	@Override
	protected int _SC_TRACE_SYS_MAX() {
		return Unistd_Lib._SC_TRACE_SYS_MAX;
	}

	@Override
	protected int _SC_TRACE_USER_EVENT_MAX() {
		return Unistd_Lib._SC_TRACE_USER_EVENT_MAX;
	}

	@Override
	protected int _SC_TTY_NAME_MAX() {
		return Unistd_Lib._SC_TTY_NAME_MAX;
	}

	@Override
	protected int _SC_TYPED_MEMORY_OBJECTS() {
		return Unistd_Lib._SC_TYPED_MEMORY_OBJECTS;
	}

	@Override
	protected int _SC_TZNAME_MAX() {
		return Unistd_Lib._SC_TZNAME_MAX;
	}

	@Override
	protected int _SC_UCHAR_MAX() {
		return Unistd_Lib._SC_UCHAR_MAX;
	}

	@Override
	protected int _SC_UINT_MAX() {
		return Unistd_Lib._SC_UINT_MAX;
	}

	@Override
	protected int _SC_UIO_MAXIOV() {
		return Unistd_Lib._SC_UIO_MAXIOV;
	}

	@Override
	protected int _SC_ULONG_MAX() {
		return Unistd_Lib._SC_ULONG_MAX;
	}

	@Override
	protected int _SC_USER_GROUPS() {
		return Unistd_Lib._SC_USER_GROUPS;
	}

	@Override
	protected int _SC_USER_GROUPS_R() {
		return Unistd_Lib._SC_USER_GROUPS_R;
	}

	@Override
	protected int _SC_USHRT_MAX() {
		return Unistd_Lib._SC_USHRT_MAX;
	}

	@Override
	protected int _SC_V6_ILP32_OFF32() {
		return Unistd_Lib._SC_V6_ILP32_OFF32;
	}

	@Override
	protected int _SC_V6_ILP32_OFFBIG() {
		return Unistd_Lib._SC_V6_ILP32_OFFBIG;
	}

	@Override
	protected int _SC_V6_LP64_OFF64() {
		return Unistd_Lib._SC_V6_LP64_OFF64;
	}

	@Override
	protected int _SC_V6_LPBIG_OFFBIG() {
		return Unistd_Lib._SC_V6_LPBIG_OFFBIG;
	}

	@Override
	protected int _SC_V7_ILP32_OFF32() {
		return Unistd_Lib._SC_V7_ILP32_OFF32;
	}

	@Override
	protected int _SC_V7_ILP32_OFFBIG() {
		return Unistd_Lib._SC_V7_ILP32_OFFBIG;
	}

	@Override
	protected int _SC_V7_LP64_OFF64() {
		return Unistd_Lib._SC_V7_LP64_OFF64;
	}

	@Override
	protected int _SC_V7_LPBIG_OFFBIG() {
		return Unistd_Lib._SC_V7_LPBIG_OFFBIG;
	}

	@Override
	protected int _SC_VERSION() {
		return Unistd_Lib._SC_VERSION;
	}

	@Override
	protected int _SC_WORD_BIT() {
		return Unistd_Lib._SC_WORD_BIT;
	}

	@Override
	protected int _SC_XBS5_ILP32_OFF32() {
		return Unistd_Lib._SC_XBS5_ILP32_OFF32;
	}

	@Override
	protected int _SC_XBS5_ILP32_OFFBIG() {
		return Unistd_Lib._SC_XBS5_ILP32_OFFBIG;
	}

	@Override
	protected int _SC_XBS5_LP64_OFF64() {
		return Unistd_Lib._SC_XBS5_LP64_OFF64;
	}

	@Override
	protected int _SC_XBS5_LPBIG_OFFBIG() {
		return Unistd_Lib._SC_XBS5_LPBIG_OFFBIG;
	}

	@Override
	protected int _SC_XOPEN_CRYPT() {
		return Unistd_Lib._SC_XOPEN_CRYPT;
	}

	@Override
	protected int _SC_XOPEN_ENH_I18N() {
		return Unistd_Lib._SC_XOPEN_ENH_I18N;
	}

	@Override
	protected int _SC_XOPEN_LEGACY() {
		return Unistd_Lib._SC_XOPEN_LEGACY;
	}

	@Override
	protected int _SC_XOPEN_REALTIME() {
		return Unistd_Lib._SC_XOPEN_REALTIME;
	}

	@Override
	protected int _SC_XOPEN_REALTIME_THREADS() {
		return Unistd_Lib._SC_XOPEN_REALTIME_THREADS;
	}

	@Override
	protected int _SC_XOPEN_SHM() {
		return Unistd_Lib._SC_XOPEN_SHM;
	}

	@Override
	protected int _SC_XOPEN_STREAMS() {
		return Unistd_Lib._SC_XOPEN_STREAMS;
	}

	@Override
	protected int _SC_XOPEN_UNIX() {
		return Unistd_Lib._SC_XOPEN_UNIX;
	}

	@Override
	protected int _SC_XOPEN_VERSION() {
		return Unistd_Lib._SC_XOPEN_VERSION;
	}

	@Override
	protected int _SC_XOPEN_XCU_VERSION() {
		return Unistd_Lib._SC_XOPEN_XCU_VERSION;
	}

	@Override
	protected int _SC_XOPEN_XPG2() {
		return Unistd_Lib._SC_XOPEN_XPG2;
	}

	@Override
	protected int _SC_XOPEN_XPG3() {
		return Unistd_Lib._SC_XOPEN_XPG3;
	}

	@Override
	protected int _SC_XOPEN_XPG4() {
		return Unistd_Lib._SC_XOPEN_XPG4;
	}

	public int close(int fd) {
		return nativeFunctions.close(fd);
	}

	@Override
	protected int F_LOCK() {
		return Unistd_Lib.F_LOCK;
	}

	@Override
	protected int F_OK() {
		return Unistd_Lib.F_OK;
	}

	@Override
	protected int F_TEST() {
		return Unistd_Lib.F_TEST;
	}

	@Override
	protected int F_TLOCK() {
		return Unistd_Lib.F_TLOCK;
	}

	@Override
	protected int F_ULOCK() {
		return Unistd_Lib.F_ULOCK;
	}

	@Override
	protected int L_INCR() {
		return Unistd_Lib.L_INCR;
	}

	@Override
	protected int L_SET() {
		return Unistd_Lib.L_SET;
	}

	@Override
	protected int L_XTND() {
		return Unistd_Lib.L_XTND;
	}

	@Override
	protected int R_OK() {
		return Unistd_Lib.R_OK;
	}

	@Override
	protected int SEEK_CUR() {
		return Unistd_Lib.SEEK_CUR;
	}

	@Override
	protected int SEEK_END() {
		return Unistd_Lib.SEEK_END;
	}

	@Override
	protected int SEEK_SET() {
		return Unistd_Lib.SEEK_SET;
	}

	@Override
	protected int W_OK() {
		return Unistd_Lib.W_OK;
	}

	@Override
	protected int X_OK() {
		return Unistd_Lib.X_OK;
	}

	@Override
	public long pread(int fildes, byte[] buf, long nbyte, long offset) {
		return nativeFunctions.pread(fildes, buf, nbyte, offset);
	}

	@Override
	public long pwrite(int fildes, byte[] _buf, long nbyte, long offset) {
		return nativeFunctions.pwrite(fildes, _buf, nbyte, offset);
	}

	@Override
	public long read(int fildes, byte[] buf, long nbyte) {
		return nativeFunctions.read(fildes, buf, nbyte);
	}

	@Override
	public long write(int fildes, byte[] buf, long nbyte) {
		return nativeFunctions.write(fildes, buf, nbyte);
	}
	
	@Override
	public int usleep(int useconds) {
		return nativeFunctions.usleep(useconds);
	}

}
