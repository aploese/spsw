package de.ibapl.jnrheader.posix;

import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.POSIX;
import de.ibapl.jnrheader.Wrapper;

@Wrapper("unistd.h")
public abstract class Unistd_H implements JnrHeader {
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
	public final int L_SET = L_SET();

	@POSIX
	public final int L_INCR = L_INCR();

	@POSIX
	public final int L_XTND = L_XTND();

	@POSIX
	public final int _PC_LINK_MAX = _PC_LINK_MAX();

	@POSIX
	public final int _PC_MAX_CANON = _PC_MAX_CANON();

	@POSIX
	public final int _PC_MAX_INPUT = _PC_MAX_INPUT();

	@POSIX
	public final int _PC_NAME_MAX = _PC_NAME_MAX();

	@POSIX
	public final int _PC_PATH_MAX = _PC_PATH_MAX();

	@POSIX
	public final int _PC_PIPE_BUF = _PC_PIPE_BUF();

	@POSIX
	public final int _PC_CHOWN_RESTRICTED = _PC_CHOWN_RESTRICTED();

	@POSIX
	public final int _PC_NO_TRUNC = _PC_NO_TRUNC();

	@POSIX
	public final int _PC_VDISABLE = _PC_VDISABLE();

	@POSIX
	public final int _PC_SYNC_IO = _PC_SYNC_IO();

	@POSIX
	public final int _PC_ASYNC_IO = _PC_ASYNC_IO();

	@POSIX
	public final int _PC_PRIO_IO = _PC_PRIO_IO();

	@POSIX
	public final int _PC_SOCK_MAXBUF = _PC_SOCK_MAXBUF();

	@POSIX
	public final int _PC_FILESIZEBITS = _PC_FILESIZEBITS();

	@POSIX
	public final int _PC_REC_INCR_XFER_SIZE = _PC_REC_INCR_XFER_SIZE();

	@POSIX
	public final int _PC_REC_MAX_XFER_SIZE = _PC_REC_MAX_XFER_SIZE();

	@POSIX
	public final int _PC_REC_MIN_XFER_SIZE = _PC_REC_MIN_XFER_SIZE();

	@POSIX
	public final int _PC_REC_XFER_ALIGN = _PC_REC_XFER_ALIGN();

	@POSIX
	public final int _PC_ALLOC_SIZE_MIN = _PC_ALLOC_SIZE_MIN();

	@POSIX
	public final int _PC_SYMLINK_MAX = _PC_SYMLINK_MAX();

	@POSIX
	public final int _PC_2_SYMLINKS = _PC_2_SYMLINKS();

	@POSIX
	public final int _SC_ARG_MAX = _SC_ARG_MAX();

	@POSIX
	public final int _SC_CHILD_MAX = _SC_CHILD_MAX();

	@POSIX
	public final int _SC_CLK_TCK = _SC_CLK_TCK();

	@POSIX
	public final int _SC_NGROUPS_MAX = _SC_NGROUPS_MAX();

	@POSIX
	public final int _SC_OPEN_MAX = _SC_OPEN_MAX();

	@POSIX
	public final int _SC_STREAM_MAX = _SC_STREAM_MAX();

	@POSIX
	public final int _SC_TZNAME_MAX = _SC_TZNAME_MAX();

	@POSIX
	public final int _SC_JOB_CONTROL = _SC_JOB_CONTROL();

	@POSIX
	public final int _SC_SAVED_IDS = _SC_SAVED_IDS();

	@POSIX
	public final int _SC_REALTIME_SIGNALS = _SC_REALTIME_SIGNALS();

	@POSIX
	public final int _SC_PRIORITY_SCHEDULING = _SC_PRIORITY_SCHEDULING();

	@POSIX
	public final int _SC_TIMERS = _SC_TIMERS();

	@POSIX
	public final int _SC_ASYNCHRONOUS_IO = _SC_ASYNCHRONOUS_IO();

	@POSIX
	public final int _SC_PRIORITIZED_IO = _SC_PRIORITIZED_IO();

	@POSIX
	public final int _SC_SYNCHRONIZED_IO = _SC_SYNCHRONIZED_IO();

	@POSIX
	public final int _SC_FSYNC = _SC_FSYNC();

	@POSIX
	public final int _SC_MAPPED_FILES = _SC_MAPPED_FILES();

	@POSIX
	public final int _SC_MEMLOCK = _SC_MEMLOCK();

	@POSIX
	public final int _SC_MEMLOCK_RANGE = _SC_MEMLOCK_RANGE();

	@POSIX
	public final int _SC_MEMORY_PROTECTION = _SC_MEMORY_PROTECTION();

	@POSIX
	public final int _SC_MESSAGE_PASSING = _SC_MESSAGE_PASSING();

	@POSIX
	public final int _SC_SEMAPHORES = _SC_SEMAPHORES();

	@POSIX
	public final int _SC_SHARED_MEMORY_OBJECTS = _SC_SHARED_MEMORY_OBJECTS();

	@POSIX
	public final int _SC_AIO_LISTIO_MAX = _SC_AIO_LISTIO_MAX();

	@POSIX
	public final int _SC_AIO_MAX = _SC_AIO_MAX();

	@POSIX
	public final int _SC_AIO_PRIO_DELTA_MAX = _SC_AIO_PRIO_DELTA_MAX();

	@POSIX
	public final int _SC_DELAYTIMER_MAX = _SC_DELAYTIMER_MAX();

	@POSIX
	public final int _SC_MQ_OPEN_MAX = _SC_MQ_OPEN_MAX();

	@POSIX
	public final int _SC_MQ_PRIO_MAX = _SC_MQ_PRIO_MAX();

	@POSIX
	public final int _SC_VERSION = _SC_VERSION();

	@POSIX
	public final int _SC_PAGESIZE = _SC_PAGESIZE();

	@POSIX
	public final int _SC_PAGE_SIZE = _SC_PAGE_SIZE();

	@POSIX
	public final int _SC_RTSIG_MAX = _SC_RTSIG_MAX();

	@POSIX
	public final int _SC_SEM_NSEMS_MAX = _SC_SEM_NSEMS_MAX();

	@POSIX
	public final int _SC_SEM_VALUE_MAX = _SC_SEM_VALUE_MAX();

	@POSIX
	public final int _SC_SIGQUEUE_MAX = _SC_SIGQUEUE_MAX();

	@POSIX
	public final int _SC_TIMER_MAX = _SC_TIMER_MAX();

	@POSIX
	public final int _SC_BC_BASE_MAX = _SC_BC_BASE_MAX();

	@POSIX
	public final int _SC_BC_DIM_MAX = _SC_BC_DIM_MAX();

	@POSIX
	public final int _SC_BC_SCALE_MAX = _SC_BC_SCALE_MAX();

	@POSIX
	public final int _SC_BC_STRING_MAX = _SC_BC_STRING_MAX();

	@POSIX
	public final int _SC_COLL_WEIGHTS_MAX = _SC_COLL_WEIGHTS_MAX();

	@POSIX
	public final int _SC_EQUIV_CLASS_MAX = _SC_EQUIV_CLASS_MAX();

	@POSIX
	public final int _SC_EXPR_NEST_MAX = _SC_EXPR_NEST_MAX();

	@POSIX
	public final int _SC_LINE_MAX = _SC_LINE_MAX();

	@POSIX
	public final int _SC_RE_DUP_MAX = _SC_RE_DUP_MAX();

	@POSIX
	public final int _SC_CHARCLASS_NAME_MAX = _SC_CHARCLASS_NAME_MAX();

	@POSIX
	public final int _SC_2_VERSION = _SC_2_VERSION();

	@POSIX
	public final int _SC_2_C_BIND = _SC_2_C_BIND();

	@POSIX
	public final int _SC_2_C_DEV = _SC_2_C_DEV();

	@POSIX
	public final int _SC_2_FORT_DEV = _SC_2_FORT_DEV();

	@POSIX
	public final int _SC_2_FORT_RUN = _SC_2_FORT_RUN();

	@POSIX
	public final int _SC_2_SW_DEV = _SC_2_SW_DEV();

	@POSIX
	public final int _SC_2_LOCALEDEF = _SC_2_LOCALEDEF();

	@POSIX
	public final int _SC_PII = _SC_PII();

	@POSIX
	public final int _SC_PII_XTI = _SC_PII_XTI();

	@POSIX
	public final int _SC_PII_SOCKET = _SC_PII_SOCKET();

	@POSIX
	public final int _SC_PII_INTERNET = _SC_PII_INTERNET();

	@POSIX
	public final int _SC_PII_OSI = _SC_PII_OSI();

	@POSIX
	public final int _SC_POLL = _SC_POLL();

	@POSIX
	public final int _SC_SELECT = _SC_SELECT();

	@POSIX
	public final int _SC_UIO_MAXIOV = _SC_UIO_MAXIOV();

	@POSIX
	public final int _SC_IOV_MAX = _SC_IOV_MAX();

	@POSIX
	public final int _SC_PII_INTERNET_STREAM = _SC_PII_INTERNET_STREAM();

	@POSIX
	public final int _SC_PII_INTERNET_DGRAM = _SC_PII_INTERNET_DGRAM();

	@POSIX
	public final int _SC_PII_OSI_COTS = _SC_PII_OSI_COTS();

	@POSIX
	public final int _SC_PII_OSI_CLTS = _SC_PII_OSI_CLTS();

	@POSIX
	public final int _SC_PII_OSI_M = _SC_PII_OSI_M();

	@POSIX
	public final int _SC_T_IOV_MAX = _SC_T_IOV_MAX();

	@POSIX
	public final int _SC_THREADS = _SC_THREADS();

	@POSIX
	public final int _SC_THREAD_SAFE_FUNCTIONS = _SC_THREAD_SAFE_FUNCTIONS();

	@POSIX
	public final int _SC_GETGR_R_SIZE_MAX = _SC_GETGR_R_SIZE_MAX();

	@POSIX
	public final int _SC_GETPW_R_SIZE_MAX = _SC_GETPW_R_SIZE_MAX();

	@POSIX
	public final int _SC_LOGIN_NAME_MAX = _SC_LOGIN_NAME_MAX();

	@POSIX
	public final int _SC_TTY_NAME_MAX = _SC_TTY_NAME_MAX();

	@POSIX
	public final int _SC_THREAD_DESTRUCTOR_ITERATIONS = _SC_THREAD_DESTRUCTOR_ITERATIONS();

	@POSIX
	public final int _SC_THREAD_KEYS_MAX = _SC_THREAD_KEYS_MAX();

	@POSIX
	public final int _SC_THREAD_STACK_MIN = _SC_THREAD_STACK_MIN();

	@POSIX
	public final int _SC_THREAD_THREADS_MAX = _SC_THREAD_THREADS_MAX();

	@POSIX
	public final int _SC_THREAD_ATTR_STACKADDR = _SC_THREAD_ATTR_STACKADDR();

	@POSIX
	public final int _SC_THREAD_ATTR_STACKSIZE = _SC_THREAD_ATTR_STACKSIZE();

	@POSIX
	public final int _SC_THREAD_PRIORITY_SCHEDULING = _SC_THREAD_PRIORITY_SCHEDULING();

	@POSIX
	public final int _SC_THREAD_PRIO_INHERIT = _SC_THREAD_PRIO_INHERIT();

	@POSIX
	public final int _SC_THREAD_PRIO_PROTECT = _SC_THREAD_PRIO_PROTECT();

	@POSIX
	public final int _SC_THREAD_PROCESS_SHARED = _SC_THREAD_PROCESS_SHARED();

	@POSIX
	public final int _SC_NPROCESSORS_CONF = _SC_NPROCESSORS_CONF();

	@POSIX
	public final int _SC_NPROCESSORS_ONLN = _SC_NPROCESSORS_ONLN();

	@POSIX
	public final int _SC_PHYS_PAGES = _SC_PHYS_PAGES();

	@POSIX
	public final int _SC_AVPHYS_PAGES = _SC_AVPHYS_PAGES();

	@POSIX
	public final int _SC_ATEXIT_MAX = _SC_ATEXIT_MAX();

	@POSIX
	public final int _SC_PASS_MAX = _SC_PASS_MAX();

	@POSIX
	public final int _SC_XOPEN_VERSION = _SC_XOPEN_VERSION();

	@POSIX
	public final int _SC_XOPEN_XCU_VERSION = _SC_XOPEN_XCU_VERSION();

	@POSIX
	public final int _SC_XOPEN_UNIX = _SC_XOPEN_UNIX();

	@POSIX
	public final int _SC_XOPEN_CRYPT = _SC_XOPEN_CRYPT();

	@POSIX
	public final int _SC_XOPEN_ENH_I18N = _SC_XOPEN_ENH_I18N();

	@POSIX
	public final int _SC_XOPEN_SHM = _SC_XOPEN_SHM();

	@POSIX
	public final int _SC_2_CHAR_TERM = _SC_2_CHAR_TERM();

	@POSIX
	public final int _SC_2_C_VERSION = _SC_2_C_VERSION();

	@POSIX
	public final int _SC_2_UPE = _SC_2_UPE();

	@POSIX
	public final int _SC_XOPEN_XPG2 = _SC_XOPEN_XPG2();

	@POSIX
	public final int _SC_XOPEN_XPG3 = _SC_XOPEN_XPG3();

	@POSIX
	public final int _SC_XOPEN_XPG4 = _SC_XOPEN_XPG4();

	@POSIX
	public final int _SC_CHAR_BIT = _SC_CHAR_BIT();

	@POSIX
	public final int _SC_CHAR_MAX = _SC_CHAR_MAX();

	@POSIX
	public final int _SC_CHAR_MIN = _SC_CHAR_MIN();

	@POSIX
	public final int _SC_INT_MAX = _SC_INT_MAX();

	@POSIX
	public final int _SC_INT_MIN = _SC_INT_MIN();

	@POSIX
	public final int _SC_LONG_BIT = _SC_LONG_BIT();

	@POSIX
	public final int _SC_WORD_BIT = _SC_WORD_BIT();

	@POSIX
	public final int _SC_MB_LEN_MAX = _SC_MB_LEN_MAX();

	@POSIX
	public final int _SC_NZERO = _SC_NZERO();

	@POSIX
	public final int _SC_SSIZE_MAX = _SC_SSIZE_MAX();

	@POSIX
	public final int _SC_SCHAR_MAX = _SC_SCHAR_MAX();

	@POSIX
	public final int _SC_SCHAR_MIN = _SC_SCHAR_MIN();

	@POSIX
	public final int _SC_SHRT_MAX = _SC_SHRT_MAX();

	@POSIX
	public final int _SC_SHRT_MIN = _SC_SHRT_MIN();

	@POSIX
	public final int _SC_UCHAR_MAX = _SC_UCHAR_MAX();

	@POSIX
	public final int _SC_UINT_MAX = _SC_UINT_MAX();

	@POSIX
	public final int _SC_ULONG_MAX = _SC_ULONG_MAX();

	@POSIX
	public final int _SC_USHRT_MAX = _SC_USHRT_MAX();

	@POSIX
	public final int _SC_NL_ARGMAX = _SC_NL_ARGMAX();

	@POSIX
	public final int _SC_NL_LANGMAX = _SC_NL_LANGMAX();

	@POSIX
	public final int _SC_NL_MSGMAX = _SC_NL_MSGMAX();

	@POSIX
	public final int _SC_NL_NMAX = _SC_NL_NMAX();

	@POSIX
	public final int _SC_NL_SETMAX = _SC_NL_SETMAX();

	@POSIX
	public final int _SC_NL_TEXTMAX = _SC_NL_TEXTMAX();

	@POSIX
	public final int _SC_XBS5_ILP32_OFF32 = _SC_XBS5_ILP32_OFF32();

	@POSIX
	public final int _SC_XBS5_ILP32_OFFBIG = _SC_XBS5_ILP32_OFFBIG();

	@POSIX
	public final int _SC_XBS5_LP64_OFF64 = _SC_XBS5_LP64_OFF64();

	@POSIX
	public final int _SC_XBS5_LPBIG_OFFBIG = _SC_XBS5_LPBIG_OFFBIG();

	@POSIX
	public final int _SC_XOPEN_LEGACY = _SC_XOPEN_LEGACY();

	@POSIX
	public final int _SC_XOPEN_REALTIME = _SC_XOPEN_REALTIME();

	@POSIX
	public final int _SC_XOPEN_REALTIME_THREADS = _SC_XOPEN_REALTIME_THREADS();

	@POSIX
	public final int _SC_ADVISORY_INFO = _SC_ADVISORY_INFO();

	@POSIX
	public final int _SC_BARRIERS = _SC_BARRIERS();

	@POSIX
	public final int _SC_BASE = _SC_BASE();

	@POSIX
	public final int _SC_C_LANG_SUPPORT = _SC_C_LANG_SUPPORT();

	@POSIX
	public final int _SC_C_LANG_SUPPORT_R = _SC_C_LANG_SUPPORT_R();

	@POSIX
	public final int _SC_CLOCK_SELECTION = _SC_CLOCK_SELECTION();

	@POSIX
	public final int _SC_CPUTIME = _SC_CPUTIME();

	@POSIX
	public final int _SC_THREAD_CPUTIME = _SC_THREAD_CPUTIME();

	@POSIX
	public final int _SC_DEVICE_IO = _SC_DEVICE_IO();

	@POSIX
	public final int _SC_DEVICE_SPECIFIC = _SC_DEVICE_SPECIFIC();

	@POSIX
	public final int _SC_DEVICE_SPECIFIC_R = _SC_DEVICE_SPECIFIC_R();

	@POSIX
	public final int _SC_FD_MGMT = _SC_FD_MGMT();

	@POSIX
	public final int _SC_FIFO = _SC_FIFO();

	@POSIX
	public final int _SC_PIPE = _SC_PIPE();

	@POSIX
	public final int _SC_FILE_ATTRIBUTES = _SC_FILE_ATTRIBUTES();

	@POSIX
	public final int _SC_FILE_LOCKING = _SC_FILE_LOCKING();

	@POSIX
	public final int _SC_FILE_SYSTEM = _SC_FILE_SYSTEM();

	@POSIX
	public final int _SC_MONOTONIC_CLOCK = _SC_MONOTONIC_CLOCK();

	@POSIX
	public final int _SC_MULTI_PROCESS = _SC_MULTI_PROCESS();

	@POSIX
	public final int _SC_SINGLE_PROCESS = _SC_SINGLE_PROCESS();

	@POSIX
	public final int _SC_NETWORKING = _SC_NETWORKING();

	@POSIX
	public final int _SC_READER_WRITER_LOCKS = _SC_READER_WRITER_LOCKS();

	@POSIX
	public final int _SC_SPIN_LOCKS = _SC_SPIN_LOCKS();

	@POSIX
	public final int _SC_REGEXP = _SC_REGEXP();

	@POSIX
	public final int _SC_REGEX_VERSION = _SC_REGEX_VERSION();

	@POSIX
	public final int _SC_SHELL = _SC_SHELL();

	@POSIX
	public final int _SC_SIGNALS = _SC_SIGNALS();

	@POSIX
	public final int _SC_SPAWN = _SC_SPAWN();

	@POSIX
	public final int _SC_SPORADIC_SERVER = _SC_SPORADIC_SERVER();

	@POSIX
	public final int _SC_THREAD_SPORADIC_SERVER = _SC_THREAD_SPORADIC_SERVER();

	@POSIX
	public final int _SC_SYSTEM_DATABASE = _SC_SYSTEM_DATABASE();

	@POSIX
	public final int _SC_SYSTEM_DATABASE_R = _SC_SYSTEM_DATABASE_R();

	@POSIX
	public final int _SC_TIMEOUTS = _SC_TIMEOUTS();

	@POSIX
	public final int _SC_TYPED_MEMORY_OBJECTS = _SC_TYPED_MEMORY_OBJECTS();

	@POSIX
	public final int _SC_USER_GROUPS = _SC_USER_GROUPS();

	@POSIX
	public final int _SC_USER_GROUPS_R = _SC_USER_GROUPS_R();

	@POSIX
	public final int _SC_2_PBS = _SC_2_PBS();

	@POSIX
	public final int _SC_2_PBS_ACCOUNTING = _SC_2_PBS_ACCOUNTING();

	@POSIX
	public final int _SC_2_PBS_LOCATE = _SC_2_PBS_LOCATE();

	@POSIX
	public final int _SC_2_PBS_MESSAGE = _SC_2_PBS_MESSAGE();

	@POSIX
	public final int _SC_2_PBS_TRACK = _SC_2_PBS_TRACK();

	@POSIX
	public final int _SC_SYMLOOP_MAX = _SC_SYMLOOP_MAX();

	@POSIX
	public final int _SC_STREAMS = _SC_STREAMS();

	@POSIX
	public final int _SC_2_PBS_CHECKPOINT = _SC_2_PBS_CHECKPOINT();

	@POSIX
	public final int _SC_V6_ILP32_OFF32 = _SC_V6_ILP32_OFF32();

	@POSIX
	public final int _SC_V6_ILP32_OFFBIG = _SC_V6_ILP32_OFFBIG();

	@POSIX
	public final int _SC_V6_LP64_OFF64 = _SC_V6_LP64_OFF64();

	@POSIX
	public final int _SC_V6_LPBIG_OFFBIG = _SC_V6_LPBIG_OFFBIG();

	@POSIX
	public final int _SC_HOST_NAME_MAX = _SC_HOST_NAME_MAX();

	@POSIX
	public final int _SC_TRACE = _SC_TRACE();

	@POSIX
	public final int _SC_TRACE_EVENT_FILTER = _SC_TRACE_EVENT_FILTER();

	@POSIX
	public final int _SC_TRACE_INHERIT = _SC_TRACE_INHERIT();

	@POSIX
	public final int _SC_TRACE_LOG = _SC_TRACE_LOG();

	@POSIX
	public final int _SC_LEVEL1_ICACHE_SIZE = _SC_LEVEL1_ICACHE_SIZE();

	@POSIX
	public final int _SC_LEVEL1_ICACHE_ASSOC = _SC_LEVEL1_ICACHE_ASSOC();

	@POSIX
	public final int _SC_LEVEL1_ICACHE_LINESIZE = _SC_LEVEL1_ICACHE_LINESIZE();

	@POSIX
	public final int _SC_LEVEL1_DCACHE_SIZE = _SC_LEVEL1_DCACHE_SIZE();

	@POSIX
	public final int _SC_LEVEL1_DCACHE_ASSOC = _SC_LEVEL1_DCACHE_ASSOC();

	@POSIX
	public final int _SC_LEVEL1_DCACHE_LINESIZE = _SC_LEVEL1_DCACHE_LINESIZE();

	@POSIX
	public final int _SC_LEVEL2_CACHE_SIZE = _SC_LEVEL2_CACHE_SIZE();

	@POSIX
	public final int _SC_LEVEL2_CACHE_ASSOC = _SC_LEVEL2_CACHE_ASSOC();

	@POSIX
	public final int _SC_LEVEL2_CACHE_LINESIZE = _SC_LEVEL2_CACHE_LINESIZE();

	@POSIX
	public final int _SC_LEVEL3_CACHE_SIZE = _SC_LEVEL3_CACHE_SIZE();

	@POSIX
	public final int _SC_LEVEL3_CACHE_ASSOC = _SC_LEVEL3_CACHE_ASSOC();

	@POSIX
	public final int _SC_LEVEL3_CACHE_LINESIZE = _SC_LEVEL3_CACHE_LINESIZE();

	@POSIX
	public final int _SC_LEVEL4_CACHE_SIZE = _SC_LEVEL4_CACHE_SIZE();

	@POSIX
	public final int _SC_LEVEL4_CACHE_ASSOC = _SC_LEVEL4_CACHE_ASSOC();

	@POSIX
	public final int _SC_LEVEL4_CACHE_LINESIZE = _SC_LEVEL4_CACHE_LINESIZE();

	@POSIX
	public final int _SC_IPV6 = _SC_IPV6();

	@POSIX
	public final int _SC_RAW_SOCKETS = _SC_RAW_SOCKETS();

	@POSIX
	public final int _SC_V7_ILP32_OFF32 = _SC_V7_ILP32_OFF32();

	@POSIX
	public final int _SC_V7_ILP32_OFFBIG = _SC_V7_ILP32_OFFBIG();

	@POSIX
	public final int _SC_V7_LP64_OFF64 = _SC_V7_LP64_OFF64();

	@POSIX
	public final int _SC_V7_LPBIG_OFFBIG = _SC_V7_LPBIG_OFFBIG();

	@POSIX
	public final int _SC_SS_REPL_MAX = _SC_SS_REPL_MAX();

	@POSIX
	public final int _SC_TRACE_EVENT_NAME_MAX = _SC_TRACE_EVENT_NAME_MAX();

	@POSIX
	public final int _SC_TRACE_NAME_MAX = _SC_TRACE_NAME_MAX();

	@POSIX
	public final int _SC_TRACE_SYS_MAX = _SC_TRACE_SYS_MAX();

	@POSIX
	public final int _SC_TRACE_USER_EVENT_MAX = _SC_TRACE_USER_EVENT_MAX();

	@POSIX
	public final int _SC_XOPEN_STREAMS = _SC_XOPEN_STREAMS();

	@POSIX
	public final int _SC_THREAD_ROBUST_PRIO_INHERIT = _SC_THREAD_ROBUST_PRIO_INHERIT();

	@POSIX
	public final int _SC_THREAD_ROBUST_PRIO_PROTECT = _SC_THREAD_ROBUST_PRIO_PROTECT();

	@POSIX
	public final int _CS_PATH = _CS_PATH();

	@POSIX
	public final int _CS_V6_WIDTH_RESTRICTED_ENVS = _CS_V6_WIDTH_RESTRICTED_ENVS();

	@POSIX
	public final int _CS_POSIX_V6_WIDTH_RESTRICTED_ENVS = _CS_POSIX_V6_WIDTH_RESTRICTED_ENVS();

	@POSIX
	public final int _CS_GNU_LIBC_VERSION = _CS_GNU_LIBC_VERSION();

	@POSIX
	public final int _CS_GNU_LIBPTHREAD_VERSION = _CS_GNU_LIBPTHREAD_VERSION();

	@POSIX
	public final int _CS_V5_WIDTH_RESTRICTED_ENVS = _CS_V5_WIDTH_RESTRICTED_ENVS();

	@POSIX
	public final int _CS_POSIX_V5_WIDTH_RESTRICTED_ENVS = _CS_POSIX_V5_WIDTH_RESTRICTED_ENVS();

	@POSIX
	public final int _CS_V7_WIDTH_RESTRICTED_ENVS = _CS_V7_WIDTH_RESTRICTED_ENVS();

	@POSIX
	public final int _CS_POSIX_V7_WIDTH_RESTRICTED_ENVS = _CS_POSIX_V7_WIDTH_RESTRICTED_ENVS();

	@POSIX
	public final int _CS_LFS_CFLAGS = _CS_LFS_CFLAGS();

	@POSIX
	public final int _CS_LFS_LDFLAGS = _CS_LFS_LDFLAGS();

	@POSIX
	public final int _CS_LFS_LIBS = _CS_LFS_LIBS();

	@POSIX
	public final int _CS_LFS_LINTFLAGS = _CS_LFS_LINTFLAGS();

	@POSIX
	public final int _CS_LFS64_CFLAGS = _CS_LFS64_CFLAGS();

	@POSIX
	public final int _CS_LFS64_LDFLAGS = _CS_LFS64_LDFLAGS();

	@POSIX
	public final int _CS_LFS64_LIBS = _CS_LFS64_LIBS();

	@POSIX
	public final int _CS_LFS64_LINTFLAGS = _CS_LFS64_LINTFLAGS();

	@POSIX
	public final int _CS_XBS5_ILP32_OFF32_CFLAGS = _CS_XBS5_ILP32_OFF32_CFLAGS();

	@POSIX
	public final int _CS_XBS5_ILP32_OFF32_LDFLAGS = _CS_XBS5_ILP32_OFF32_LDFLAGS();

	@POSIX
	public final int _CS_XBS5_ILP32_OFF32_LIBS = _CS_XBS5_ILP32_OFF32_LIBS();

	@POSIX
	public final int _CS_XBS5_ILP32_OFF32_LINTFLAGS = _CS_XBS5_ILP32_OFF32_LINTFLAGS();

	@POSIX
	public final int _CS_XBS5_ILP32_OFFBIG_CFLAGS = _CS_XBS5_ILP32_OFFBIG_CFLAGS();

	@POSIX
	public final int _CS_XBS5_ILP32_OFFBIG_LDFLAGS = _CS_XBS5_ILP32_OFFBIG_LDFLAGS();

	@POSIX
	public final int _CS_XBS5_ILP32_OFFBIG_LIBS = _CS_XBS5_ILP32_OFFBIG_LIBS();

	@POSIX
	public final int _CS_XBS5_ILP32_OFFBIG_LINTFLAGS = _CS_XBS5_ILP32_OFFBIG_LINTFLAGS();

	@POSIX
	public final int _CS_XBS5_LP64_OFF64_CFLAGS = _CS_XBS5_LP64_OFF64_CFLAGS();

	@POSIX
	public final int _CS_XBS5_LP64_OFF64_LDFLAGS = _CS_XBS5_LP64_OFF64_LDFLAGS();

	@POSIX
	public final int _CS_XBS5_LP64_OFF64_LIBS = _CS_XBS5_LP64_OFF64_LIBS();

	@POSIX
	public final int _CS_XBS5_LP64_OFF64_LINTFLAGS = _CS_XBS5_LP64_OFF64_LINTFLAGS();

	@POSIX
	public final int _CS_XBS5_LPBIG_OFFBIG_CFLAGS = _CS_XBS5_LPBIG_OFFBIG_CFLAGS();

	@POSIX
	public final int _CS_XBS5_LPBIG_OFFBIG_LDFLAGS = _CS_XBS5_LPBIG_OFFBIG_LDFLAGS();

	@POSIX
	public final int _CS_XBS5_LPBIG_OFFBIG_LIBS = _CS_XBS5_LPBIG_OFFBIG_LIBS();

	@POSIX
	public final int _CS_XBS5_LPBIG_OFFBIG_LINTFLAGS = _CS_XBS5_LPBIG_OFFBIG_LINTFLAGS();

	@POSIX
	public final int _CS_POSIX_V6_ILP32_OFF32_CFLAGS = _CS_POSIX_V6_ILP32_OFF32_CFLAGS();

	@POSIX
	public final int _CS_POSIX_V6_ILP32_OFF32_LDFLAGS = _CS_POSIX_V6_ILP32_OFF32_LDFLAGS();

	@POSIX
	public final int _CS_POSIX_V6_ILP32_OFF32_LIBS = _CS_POSIX_V6_ILP32_OFF32_LIBS();

	@POSIX
	public final int _CS_POSIX_V6_ILP32_OFF32_LINTFLAGS = _CS_POSIX_V6_ILP32_OFF32_LINTFLAGS();

	@POSIX
	public final int _CS_POSIX_V6_ILP32_OFFBIG_CFLAGS = _CS_POSIX_V6_ILP32_OFFBIG_CFLAGS();

	@POSIX
	public final int _CS_POSIX_V6_ILP32_OFFBIG_LDFLAGS = _CS_POSIX_V6_ILP32_OFFBIG_LDFLAGS();

	@POSIX
	public final int _CS_POSIX_V6_ILP32_OFFBIG_LIBS = _CS_POSIX_V6_ILP32_OFFBIG_LIBS();

	@POSIX
	public final int _CS_POSIX_V6_ILP32_OFFBIG_LINTFLAGS = _CS_POSIX_V6_ILP32_OFFBIG_LINTFLAGS();

	@POSIX
	public final int _CS_POSIX_V6_LP64_OFF64_CFLAGS = _CS_POSIX_V6_LP64_OFF64_CFLAGS();

	@POSIX
	public final int _CS_POSIX_V6_LP64_OFF64_LDFLAGS = _CS_POSIX_V6_LP64_OFF64_LDFLAGS();

	@POSIX
	public final int _CS_POSIX_V6_LP64_OFF64_LIBS = _CS_POSIX_V6_LP64_OFF64_LIBS();

	@POSIX
	public final int _CS_POSIX_V6_LP64_OFF64_LINTFLAGS = _CS_POSIX_V6_LP64_OFF64_LINTFLAGS();

	@POSIX
	public final int _CS_POSIX_V6_LPBIG_OFFBIG_CFLAGS = _CS_POSIX_V6_LPBIG_OFFBIG_CFLAGS();

	@POSIX
	public final int _CS_POSIX_V6_LPBIG_OFFBIG_LDFLAGS = _CS_POSIX_V6_LPBIG_OFFBIG_LDFLAGS();

	@POSIX
	public final int _CS_POSIX_V6_LPBIG_OFFBIG_LIBS = _CS_POSIX_V6_LPBIG_OFFBIG_LIBS();

	@POSIX
	public final int _CS_POSIX_V6_LPBIG_OFFBIG_LINTFLAGS = _CS_POSIX_V6_LPBIG_OFFBIG_LINTFLAGS();

	@POSIX
	public final int _CS_POSIX_V7_ILP32_OFF32_CFLAGS = _CS_POSIX_V7_ILP32_OFF32_CFLAGS();

	@POSIX
	public final int _CS_POSIX_V7_ILP32_OFF32_LDFLAGS = _CS_POSIX_V7_ILP32_OFF32_LDFLAGS();

	@POSIX
	public final int _CS_POSIX_V7_ILP32_OFF32_LIBS = _CS_POSIX_V7_ILP32_OFF32_LIBS();

	@POSIX
	public final int _CS_POSIX_V7_ILP32_OFF32_LINTFLAGS = _CS_POSIX_V7_ILP32_OFF32_LINTFLAGS();

	@POSIX
	public final int _CS_POSIX_V7_ILP32_OFFBIG_CFLAGS = _CS_POSIX_V7_ILP32_OFFBIG_CFLAGS();

	@POSIX
	public final int _CS_POSIX_V7_ILP32_OFFBIG_LDFLAGS = _CS_POSIX_V7_ILP32_OFFBIG_LDFLAGS();

	@POSIX
	public final int _CS_POSIX_V7_ILP32_OFFBIG_LIBS = _CS_POSIX_V7_ILP32_OFFBIG_LIBS();

	@POSIX
	public final int _CS_POSIX_V7_ILP32_OFFBIG_LINTFLAGS = _CS_POSIX_V7_ILP32_OFFBIG_LINTFLAGS();

	@POSIX
	public final int _CS_POSIX_V7_LP64_OFF64_CFLAGS = _CS_POSIX_V7_LP64_OFF64_CFLAGS();

	@POSIX
	public final int _CS_POSIX_V7_LP64_OFF64_LDFLAGS = _CS_POSIX_V7_LP64_OFF64_LDFLAGS();

	@POSIX
	public final int _CS_POSIX_V7_LP64_OFF64_LIBS = _CS_POSIX_V7_LP64_OFF64_LIBS();

	@POSIX
	public final int _CS_POSIX_V7_LP64_OFF64_LINTFLAGS = _CS_POSIX_V7_LP64_OFF64_LINTFLAGS();

	@POSIX
	public final int _CS_POSIX_V7_LPBIG_OFFBIG_CFLAGS = _CS_POSIX_V7_LPBIG_OFFBIG_CFLAGS();

	@POSIX
	public final int _CS_POSIX_V7_LPBIG_OFFBIG_LDFLAGS = _CS_POSIX_V7_LPBIG_OFFBIG_LDFLAGS();

	@POSIX
	public final int _CS_POSIX_V7_LPBIG_OFFBIG_LIBS = _CS_POSIX_V7_LPBIG_OFFBIG_LIBS();

	@POSIX
	public final int _CS_POSIX_V7_LPBIG_OFFBIG_LINTFLAGS = _CS_POSIX_V7_LPBIG_OFFBIG_LINTFLAGS();

	@POSIX
	public final int _CS_V6_ENV = _CS_V6_ENV();

	@POSIX
	public final int _CS_V7_ENV = _CS_V7_ENV();

	@POSIX
	public final int F_ULOCK = F_ULOCK();

	@POSIX
	public final int F_LOCK = F_LOCK();

	@POSIX
	public final int F_TLOCK = F_TLOCK();

	@POSIX
	public final int F_TEST = F_TEST();

	protected abstract int _CS_GNU_LIBC_VERSION();

	protected abstract int _CS_GNU_LIBPTHREAD_VERSION();

	protected abstract int _CS_LFS_CFLAGS();

	protected abstract int _CS_LFS_LDFLAGS();

	protected abstract int _CS_LFS_LIBS();

	protected abstract int _CS_LFS_LINTFLAGS();

	protected abstract int _CS_LFS64_CFLAGS();

	protected abstract int _CS_LFS64_LDFLAGS();

	protected abstract int _CS_LFS64_LIBS();

	protected abstract int _CS_LFS64_LINTFLAGS();

	protected abstract int _CS_PATH();

	protected abstract int _CS_POSIX_V5_WIDTH_RESTRICTED_ENVS();

	protected abstract int _CS_POSIX_V6_ILP32_OFF32_CFLAGS();

	protected abstract int _CS_POSIX_V6_ILP32_OFF32_LDFLAGS();

	protected abstract int _CS_POSIX_V6_ILP32_OFF32_LIBS();

	protected abstract int _CS_POSIX_V6_ILP32_OFF32_LINTFLAGS();

	protected abstract int _CS_POSIX_V6_ILP32_OFFBIG_CFLAGS();

	protected abstract int _CS_POSIX_V6_ILP32_OFFBIG_LDFLAGS();

	protected abstract int _CS_POSIX_V6_ILP32_OFFBIG_LIBS();

	protected abstract int _CS_POSIX_V6_ILP32_OFFBIG_LINTFLAGS();

	protected abstract int _CS_POSIX_V6_LP64_OFF64_CFLAGS();

	protected abstract int _CS_POSIX_V6_LP64_OFF64_LDFLAGS();

	protected abstract int _CS_POSIX_V6_LP64_OFF64_LIBS();

	protected abstract int _CS_POSIX_V6_LP64_OFF64_LINTFLAGS();

	protected abstract int _CS_POSIX_V6_LPBIG_OFFBIG_CFLAGS();

	protected abstract int _CS_POSIX_V6_LPBIG_OFFBIG_LDFLAGS();

	protected abstract int _CS_POSIX_V6_LPBIG_OFFBIG_LIBS();

	protected abstract int _CS_POSIX_V6_LPBIG_OFFBIG_LINTFLAGS();

	protected abstract int _CS_POSIX_V6_WIDTH_RESTRICTED_ENVS();

	protected abstract int _CS_POSIX_V7_ILP32_OFF32_CFLAGS();

	protected abstract int _CS_POSIX_V7_ILP32_OFF32_LDFLAGS();

	protected abstract int _CS_POSIX_V7_ILP32_OFF32_LIBS();

	protected abstract int _CS_POSIX_V7_ILP32_OFF32_LINTFLAGS();

	protected abstract int _CS_POSIX_V7_ILP32_OFFBIG_CFLAGS();

	protected abstract int _CS_POSIX_V7_ILP32_OFFBIG_LDFLAGS();

	protected abstract int _CS_POSIX_V7_ILP32_OFFBIG_LIBS();

	protected abstract int _CS_POSIX_V7_ILP32_OFFBIG_LINTFLAGS();

	protected abstract int _CS_POSIX_V7_LP64_OFF64_CFLAGS();

	protected abstract int _CS_POSIX_V7_LP64_OFF64_LDFLAGS();

	protected abstract int _CS_POSIX_V7_LP64_OFF64_LIBS();

	protected abstract int _CS_POSIX_V7_LP64_OFF64_LINTFLAGS();

	protected abstract int _CS_POSIX_V7_LPBIG_OFFBIG_CFLAGS();

	protected abstract int _CS_POSIX_V7_LPBIG_OFFBIG_LDFLAGS();

	protected abstract int _CS_POSIX_V7_LPBIG_OFFBIG_LIBS();

	protected abstract int _CS_POSIX_V7_LPBIG_OFFBIG_LINTFLAGS();

	protected abstract int _CS_POSIX_V7_WIDTH_RESTRICTED_ENVS();

	protected abstract int _CS_V5_WIDTH_RESTRICTED_ENVS();

	protected abstract int _CS_V6_ENV();

	protected abstract int _CS_V6_WIDTH_RESTRICTED_ENVS();

	protected abstract int _CS_V7_ENV();

	protected abstract int _CS_V7_WIDTH_RESTRICTED_ENVS();

	protected abstract int _CS_XBS5_ILP32_OFF32_CFLAGS();

	protected abstract int _CS_XBS5_ILP32_OFF32_LDFLAGS();

	protected abstract int _CS_XBS5_ILP32_OFF32_LIBS();

	protected abstract int _CS_XBS5_ILP32_OFF32_LINTFLAGS();

	protected abstract int _CS_XBS5_ILP32_OFFBIG_CFLAGS();

	protected abstract int _CS_XBS5_ILP32_OFFBIG_LDFLAGS();

	protected abstract int _CS_XBS5_ILP32_OFFBIG_LIBS();

	protected abstract int _CS_XBS5_ILP32_OFFBIG_LINTFLAGS();

	protected abstract int _CS_XBS5_LP64_OFF64_CFLAGS();

	protected abstract int _CS_XBS5_LP64_OFF64_LDFLAGS();

	protected abstract int _CS_XBS5_LP64_OFF64_LIBS();

	protected abstract int _CS_XBS5_LP64_OFF64_LINTFLAGS();

	protected abstract int _CS_XBS5_LPBIG_OFFBIG_CFLAGS();

	protected abstract int _CS_XBS5_LPBIG_OFFBIG_LDFLAGS();

	protected abstract int _CS_XBS5_LPBIG_OFFBIG_LIBS();

	protected abstract int _CS_XBS5_LPBIG_OFFBIG_LINTFLAGS();

	protected abstract int _PC_2_SYMLINKS();

	protected abstract int _PC_ALLOC_SIZE_MIN();

	protected abstract int _PC_ASYNC_IO();

	protected abstract int _PC_CHOWN_RESTRICTED();

	protected abstract int _PC_FILESIZEBITS();

	protected abstract int _PC_LINK_MAX();

	protected abstract int _PC_MAX_CANON();

	protected abstract int _PC_MAX_INPUT();

	protected abstract int _PC_NAME_MAX();

	protected abstract int _PC_NO_TRUNC();

	protected abstract int _PC_PATH_MAX();

	protected abstract int _PC_PIPE_BUF();

	protected abstract int _PC_PRIO_IO();

	protected abstract int _PC_REC_INCR_XFER_SIZE();

	protected abstract int _PC_REC_MAX_XFER_SIZE();

	protected abstract int _PC_REC_MIN_XFER_SIZE();

	protected abstract int _PC_REC_XFER_ALIGN();

	protected abstract int _PC_SOCK_MAXBUF();

	protected abstract int _PC_SYMLINK_MAX();

	protected abstract int _PC_SYNC_IO();

	protected abstract int _PC_VDISABLE();

	protected abstract int _SC_2_C_BIND();

	protected abstract int _SC_2_C_DEV();

	protected abstract int _SC_2_C_VERSION();

	protected abstract int _SC_2_CHAR_TERM();

	protected abstract int _SC_2_FORT_DEV();

	protected abstract int _SC_2_FORT_RUN();

	protected abstract int _SC_2_LOCALEDEF();

	protected abstract int _SC_2_PBS();

	protected abstract int _SC_2_PBS_ACCOUNTING();

	protected abstract int _SC_2_PBS_CHECKPOINT();

	protected abstract int _SC_2_PBS_LOCATE();

	protected abstract int _SC_2_PBS_MESSAGE();

	protected abstract int _SC_2_PBS_TRACK();

	protected abstract int _SC_2_SW_DEV();

	protected abstract int _SC_2_UPE();

	protected abstract int _SC_2_VERSION();

	protected abstract int _SC_ADVISORY_INFO();

	protected abstract int _SC_AIO_LISTIO_MAX();

	protected abstract int _SC_AIO_MAX();

	protected abstract int _SC_AIO_PRIO_DELTA_MAX();

	protected abstract int _SC_ARG_MAX();

	protected abstract int _SC_ASYNCHRONOUS_IO();

	protected abstract int _SC_ATEXIT_MAX();

	protected abstract int _SC_AVPHYS_PAGES();

	protected abstract int _SC_BARRIERS();

	protected abstract int _SC_BASE();

	protected abstract int _SC_BC_BASE_MAX();

	protected abstract int _SC_BC_DIM_MAX();

	protected abstract int _SC_BC_SCALE_MAX();

	protected abstract int _SC_BC_STRING_MAX();

	protected abstract int _SC_C_LANG_SUPPORT();

	protected abstract int _SC_C_LANG_SUPPORT_R();

	protected abstract int _SC_CHAR_BIT();

	protected abstract int _SC_CHAR_MAX();

	protected abstract int _SC_CHAR_MIN();

	protected abstract int _SC_CHARCLASS_NAME_MAX();

	protected abstract int _SC_CHILD_MAX();

	protected abstract int _SC_CLK_TCK();

	protected abstract int _SC_CLOCK_SELECTION();

	protected abstract int _SC_COLL_WEIGHTS_MAX();

	protected abstract int _SC_CPUTIME();

	protected abstract int _SC_DELAYTIMER_MAX();

	protected abstract int _SC_DEVICE_IO();

	protected abstract int _SC_DEVICE_SPECIFIC();

	protected abstract int _SC_DEVICE_SPECIFIC_R();

	protected abstract int _SC_EQUIV_CLASS_MAX();

	protected abstract int _SC_EXPR_NEST_MAX();

	protected abstract int _SC_FD_MGMT();

	protected abstract int _SC_FIFO();

	protected abstract int _SC_FILE_ATTRIBUTES();

	protected abstract int _SC_FILE_LOCKING();

	protected abstract int _SC_FILE_SYSTEM();

	protected abstract int _SC_FSYNC();

	protected abstract int _SC_GETGR_R_SIZE_MAX();

	protected abstract int _SC_GETPW_R_SIZE_MAX();

	protected abstract int _SC_HOST_NAME_MAX();

	protected abstract int _SC_INT_MAX();

	protected abstract int _SC_INT_MIN();

	protected abstract int _SC_IOV_MAX();

	protected abstract int _SC_IPV6();

	protected abstract int _SC_JOB_CONTROL();

	protected abstract int _SC_LEVEL1_DCACHE_ASSOC();

	protected abstract int _SC_LEVEL1_DCACHE_LINESIZE();

	protected abstract int _SC_LEVEL1_DCACHE_SIZE();

	protected abstract int _SC_LEVEL1_ICACHE_ASSOC();

	protected abstract int _SC_LEVEL1_ICACHE_LINESIZE();

	protected abstract int _SC_LEVEL1_ICACHE_SIZE();

	protected abstract int _SC_LEVEL2_CACHE_ASSOC();

	protected abstract int _SC_LEVEL2_CACHE_LINESIZE();

	protected abstract int _SC_LEVEL2_CACHE_SIZE();

	protected abstract int _SC_LEVEL3_CACHE_ASSOC();

	protected abstract int _SC_LEVEL3_CACHE_LINESIZE();

	protected abstract int _SC_LEVEL3_CACHE_SIZE();

	protected abstract int _SC_LEVEL4_CACHE_ASSOC();

	protected abstract int _SC_LEVEL4_CACHE_LINESIZE();

	protected abstract int _SC_LEVEL4_CACHE_SIZE();

	protected abstract int _SC_LINE_MAX();

	protected abstract int _SC_LOGIN_NAME_MAX();

	protected abstract int _SC_LONG_BIT();

	protected abstract int _SC_MAPPED_FILES();

	protected abstract int _SC_MB_LEN_MAX();

	protected abstract int _SC_MEMLOCK();

	protected abstract int _SC_MEMLOCK_RANGE();

	protected abstract int _SC_MEMORY_PROTECTION();

	protected abstract int _SC_MESSAGE_PASSING();

	protected abstract int _SC_MONOTONIC_CLOCK();

	protected abstract int _SC_MQ_OPEN_MAX();

	protected abstract int _SC_MQ_PRIO_MAX();

	protected abstract int _SC_MULTI_PROCESS();

	protected abstract int _SC_NETWORKING();

	protected abstract int _SC_NGROUPS_MAX();

	protected abstract int _SC_NL_ARGMAX();

	protected abstract int _SC_NL_LANGMAX();

	protected abstract int _SC_NL_MSGMAX();

	protected abstract int _SC_NL_NMAX();

	protected abstract int _SC_NL_SETMAX();

	protected abstract int _SC_NL_TEXTMAX();

	protected abstract int _SC_NPROCESSORS_CONF();

	protected abstract int _SC_NPROCESSORS_ONLN();

	protected abstract int _SC_NZERO();

	protected abstract int _SC_OPEN_MAX();

	protected abstract int _SC_PAGE_SIZE();

	protected abstract int _SC_PAGESIZE();

	protected abstract int _SC_PASS_MAX();

	protected abstract int _SC_PHYS_PAGES();

	protected abstract int _SC_PII();

	protected abstract int _SC_PII_INTERNET();

	protected abstract int _SC_PII_INTERNET_DGRAM();

	protected abstract int _SC_PII_INTERNET_STREAM();

	protected abstract int _SC_PII_OSI();

	protected abstract int _SC_PII_OSI_CLTS();

	protected abstract int _SC_PII_OSI_COTS();

	protected abstract int _SC_PII_OSI_M();

	protected abstract int _SC_PII_SOCKET();

	protected abstract int _SC_PII_XTI();

	protected abstract int _SC_PIPE();

	protected abstract int _SC_POLL();

	protected abstract int _SC_PRIORITIZED_IO();

	protected abstract int _SC_PRIORITY_SCHEDULING();

	protected abstract int _SC_RAW_SOCKETS();

	protected abstract int _SC_RE_DUP_MAX();

	protected abstract int _SC_READER_WRITER_LOCKS();

	protected abstract int _SC_REALTIME_SIGNALS();

	protected abstract int _SC_REGEX_VERSION();

	protected abstract int _SC_REGEXP();

	protected abstract int _SC_RTSIG_MAX();

	protected abstract int _SC_SAVED_IDS();

	protected abstract int _SC_SCHAR_MAX();

	protected abstract int _SC_SCHAR_MIN();

	protected abstract int _SC_SELECT();

	protected abstract int _SC_SEM_NSEMS_MAX();

	protected abstract int _SC_SEM_VALUE_MAX();

	protected abstract int _SC_SEMAPHORES();

	protected abstract int _SC_SHARED_MEMORY_OBJECTS();

	protected abstract int _SC_SHELL();

	protected abstract int _SC_SHRT_MAX();

	protected abstract int _SC_SHRT_MIN();

	protected abstract int _SC_SIGNALS();

	protected abstract int _SC_SIGQUEUE_MAX();

	protected abstract int _SC_SINGLE_PROCESS();

	protected abstract int _SC_SPAWN();

	protected abstract int _SC_SPIN_LOCKS();

	protected abstract int _SC_SPORADIC_SERVER();

	protected abstract int _SC_SS_REPL_MAX();

	protected abstract int _SC_SSIZE_MAX();

	protected abstract int _SC_STREAM_MAX();

	protected abstract int _SC_STREAMS();

	protected abstract int _SC_SYMLOOP_MAX();

	protected abstract int _SC_SYNCHRONIZED_IO();

	protected abstract int _SC_SYSTEM_DATABASE();

	protected abstract int _SC_SYSTEM_DATABASE_R();

	protected abstract int _SC_T_IOV_MAX();

	protected abstract int _SC_THREAD_ATTR_STACKADDR();

	protected abstract int _SC_THREAD_ATTR_STACKSIZE();

	protected abstract int _SC_THREAD_CPUTIME();

	protected abstract int _SC_THREAD_DESTRUCTOR_ITERATIONS();

	protected abstract int _SC_THREAD_KEYS_MAX();

	protected abstract int _SC_THREAD_PRIO_INHERIT();

	protected abstract int _SC_THREAD_PRIO_PROTECT();

	protected abstract int _SC_THREAD_PRIORITY_SCHEDULING();

	protected abstract int _SC_THREAD_PROCESS_SHARED();

	protected abstract int _SC_THREAD_ROBUST_PRIO_INHERIT();

	protected abstract int _SC_THREAD_ROBUST_PRIO_PROTECT();

	protected abstract int _SC_THREAD_SAFE_FUNCTIONS();

	protected abstract int _SC_THREAD_SPORADIC_SERVER();

	protected abstract int _SC_THREAD_STACK_MIN();

	protected abstract int _SC_THREAD_THREADS_MAX();

	protected abstract int _SC_THREADS();

	protected abstract int _SC_TIMEOUTS();

	protected abstract int _SC_TIMER_MAX();

	protected abstract int _SC_TIMERS();

	protected abstract int _SC_TRACE();

	protected abstract int _SC_TRACE_EVENT_FILTER();

	protected abstract int _SC_TRACE_EVENT_NAME_MAX();

	protected abstract int _SC_TRACE_INHERIT();

	protected abstract int _SC_TRACE_LOG();

	protected abstract int _SC_TRACE_NAME_MAX();

	protected abstract int _SC_TRACE_SYS_MAX();

	protected abstract int _SC_TRACE_USER_EVENT_MAX();

	protected abstract int _SC_TTY_NAME_MAX();

	protected abstract int _SC_TYPED_MEMORY_OBJECTS();

	protected abstract int _SC_TZNAME_MAX();

	protected abstract int _SC_UCHAR_MAX();

	protected abstract int _SC_UINT_MAX();

	protected abstract int _SC_UIO_MAXIOV();

	protected abstract int _SC_ULONG_MAX();

	protected abstract int _SC_USER_GROUPS();

	protected abstract int _SC_USER_GROUPS_R();

	protected abstract int _SC_USHRT_MAX();

	protected abstract int _SC_V6_ILP32_OFF32();

	protected abstract int _SC_V6_ILP32_OFFBIG();

	protected abstract int _SC_V6_LP64_OFF64();

	protected abstract int _SC_V6_LPBIG_OFFBIG();

	protected abstract int _SC_V7_ILP32_OFF32();

	protected abstract int _SC_V7_ILP32_OFFBIG();

	protected abstract int _SC_V7_LP64_OFF64();

	protected abstract int _SC_V7_LPBIG_OFFBIG();

	protected abstract int _SC_VERSION();

	protected abstract int _SC_WORD_BIT();

	protected abstract int _SC_XBS5_ILP32_OFF32();

	protected abstract int _SC_XBS5_ILP32_OFFBIG();

	protected abstract int _SC_XBS5_LP64_OFF64();

	protected abstract int _SC_XBS5_LPBIG_OFFBIG();

	protected abstract int _SC_XOPEN_CRYPT();

	protected abstract int _SC_XOPEN_ENH_I18N();

	protected abstract int _SC_XOPEN_LEGACY();

	protected abstract int _SC_XOPEN_REALTIME();

	protected abstract int _SC_XOPEN_REALTIME_THREADS();

	protected abstract int _SC_XOPEN_SHM();

	protected abstract int _SC_XOPEN_STREAMS();

	protected abstract int _SC_XOPEN_UNIX();

	protected abstract int _SC_XOPEN_VERSION();

	protected abstract int _SC_XOPEN_XCU_VERSION();

	protected abstract int _SC_XOPEN_XPG2();

	protected abstract int _SC_XOPEN_XPG3();

	protected abstract int _SC_XOPEN_XPG4();

	public abstract int close(int fd);

	protected abstract int F_LOCK();

	protected abstract int F_OK();

	protected abstract int F_TEST();

	protected abstract int F_TLOCK();

	protected abstract int F_ULOCK();

	protected abstract int L_INCR();

	protected abstract int L_SET();

	protected abstract int L_XTND();

	public abstract long pread(int fildes, byte[] buf, long nbyte, long offset);

	public abstract long pwrite(int fildes, byte[] _buf, long nbyte, long offset);

	protected abstract int R_OK();

	public abstract long read(int fildes, byte[] buf, long nbyte);

	protected abstract int SEEK_CUR();

	protected abstract int SEEK_END();

	protected abstract int SEEK_SET();

	protected abstract int W_OK();

	public abstract long write(int fildes, byte[] buf, long nbyte);

	protected abstract int X_OK();
}
