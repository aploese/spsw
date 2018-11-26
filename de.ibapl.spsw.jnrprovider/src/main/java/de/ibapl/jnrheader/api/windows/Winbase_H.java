package de.ibapl.jnrheader.api.windows;

import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.Wrapper;
import de.ibapl.jnrheader.api.windows.Minwindef_H.HANDLE;
import de.ibapl.jnrheader.api.windows.Minwindef_H.LPDWORD;
import de.ibapl.jnrheader.api.windows.Minwinbase_H.OVERLAPPED;

@Wrapper("winbase.h")
public abstract class Winbase_H implements JnrHeader {

    public static final int STATUS_WAIT_0 = 0x00000000;
    public static final int STATUS_ABANDONED_WAIT_0 = 0x00000080;
    public static final int STATUS_USER_APC = 0x000000C0;

    public static final int FILE_BEGIN = 0;
    public static final int FILE_CURRENT = 1;
    public static final int FILE_END = 2;

    public static final int WAIT_FAILED = 0xffffffff;
    public static final int WAIT_OBJECT_0 = STATUS_WAIT_0 + 0;

    public static final int WAIT_ABANDONED = STATUS_ABANDONED_WAIT_0 + 0;
    public static final int WAIT_ABANDONED_0 = STATUS_ABANDONED_WAIT_0 + 0;

    public static final int WAIT_IO_COMPLETION = STATUS_USER_APC;

    public static final int FILE_FLAG_WRITE_THROUGH = 0x80000000;
    public static final int FILE_FLAG_OVERLAPPED = 0x40000000;
    public static final int FILE_FLAG_NO_BUFFERING = 0x20000000;
    public static final int FILE_FLAG_RANDOM_ACCESS = 0x10000000;
    public static final int FILE_FLAG_SEQUENTIAL_SCAN = 0x8000000;
    public static final int FILE_FLAG_DELETE_ON_CLOSE = 0x4000000;
    public static final int FILE_FLAG_BACKUP_SEMANTICS = 0x2000000;
    public static final int FILE_FLAG_POSIX_SEMANTICS = 0x1000000;
    public static final int FILE_FLAG_SESSION_AWARE = 0x800000;
    public static final int FILE_FLAG_OPEN_REPARSE_POINT = 0x200000;
    public static final int FILE_FLAG_OPEN_NO_RECALL = 0x100000;
    public static final int FILE_FLAG_FIRST_PIPE_INSTANCE = 0x80000;
    public static final int FILE_FLAG_OPEN_REQUIRING_OPLOCK = 0x40000;

    public static final int PROGRESS_CONTINUE = 0;
    public static final int PROGRESS_CANCEL = 1;
    public static final int PROGRESS_STOP = 2;
    public static final int PROGRESS_QUIET = 3;

    public static final int CALLBACK_CHUNK_FINISHED = 0x0;
    public static final int CALLBACK_STREAM_SWITCH = 0x1;

    public static final int COPY_FILE_FAIL_IF_EXISTS = 0x1;
    public static final int COPY_FILE_RESTARTABLE = 0x2;
    public static final int COPY_FILE_OPEN_SOURCE_FOR_WRITE = 0x4;
    public static final int COPY_FILE_ALLOW_DECRYPTED_DESTINATION = 0x8;
    public static final int COPY_FILE_COPY_SYMLINK = 0x800;
    public static final int COPY_FILE_NO_BUFFERING = 0x1000;
    public static final int COPY_FILE_REQUEST_SECURITY_PRIVILEGES = 0x2000;
    public static final int COPY_FILE_RESUME_FROM_PAUSE = 0x4000;
    public static final int COPY_FILE_NO_OFFLOAD = 0x40000;

    public static final int REPLACEFILE_WRITE_THROUGH = 0x1;
    public static final int REPLACEFILE_IGNORE_MERGE_ERRORS = 0x2;
    public static final int REPLACEFILE_IGNORE_ACL_ERRORS = 0x4;

    public static final int PIPE_ACCESS_INBOUND = 0x1;
    public static final int PIPE_ACCESS_OUTBOUND = 0x2;
    public static final int PIPE_ACCESS_DUPLEX = 0x3;

    public static final int PIPE_CLIENT_END = 0x0;
    public static final int PIPE_SERVER_END = 0x1;

    public static final int PIPE_WAIT = 0x0;
    public static final int PIPE_NOWAIT = 0x1;
    public static final int PIPE_READMODE_BYTE = 0x0;
    public static final int PIPE_READMODE_MESSAGE = 0x2;
    public static final int PIPE_TYPE_BYTE = 0x0;
    public static final int PIPE_TYPE_MESSAGE = 0x4;
    public static final int PIPE_ACCEPT_REMOTE_CLIENTS = 0x0;
    public static final int PIPE_REJECT_REMOTE_CLIENTS = 0x8;

    public static final int PIPE_UNLIMITED_INSTANCES = 255;

    public static final int SECURITY_CONTEXT_TRACKING = 0x40000;
    public static final int SECURITY_EFFECTIVE_ONLY = 0x80000;

    public static final int SECURITY_SQOS_PRESENT = 0x100000;
    public static final int SECURITY_VALID_SQOS_FLAGS = 0x1f0000;

    public static final int FAIL_FAST_GENERATE_EXCEPTION_ADDRESS = 0x1;
    public static final int FAIL_FAST_NO_HARD_ERROR_DLG = 0x2;

    public static final int SP_SERIALCOMM = 0x1;
    public static final int PST_UNSPECIFIED = 0x0;
    public static final int PST_RS232 = 0x1;
    public static final int PST_PARALLELPORT = 0x2;
    public static final int PST_RS422 = 0x3;
    public static final int PST_RS423 = 0x4;
    public static final int PST_RS449 = 0x5;
    public static final int PST_MODEM = 0x6;
    public static final int PST_FAX = 0x21;
    public static final int PST_SCANNER = 0x22;
    public static final int PST_NETWORK_BRIDGE = 0x100;
    public static final int PST_LAT = 0x101;
    public static final int PST_TCPIP_TELNET = 0x102;
    public static final int PST_X25 = 0x103;

    public static final int PCF_DTRDSR = 0x1;
    public static final int PCF_RTSCTS = 0x2;
    public static final int PCF_RLSD = 0x4;
    public static final int PCF_PARITY_CHECK = 0x8;
    public static final int PCF_XONXOFF = 0x10;
    public static final int PCF_SETXCHAR = 0x20;
    public static final int PCF_TOTALTIMEOUTS = 0x40;
    public static final int PCF_INTTIMEOUTS = 0x80;
    public static final int PCF_SPECIALCHARS = 0x100;
    public static final int PCF_16BITMODE = 0x200;

    public static final int SP_PARITY = 0x1;
    public static final int SP_BAUD = 0x2;
    public static final int SP_DATABITS = 0x4;
    public static final int SP_STOPBITS = 0x8;
    public static final int SP_HANDSHAKING = 0x10;
    public static final int SP_PARITY_CHECK = 0x20;
    public static final int SP_RLSD = 0x40;

    public static final int BAUD_075 = 0x1;
    public static final int BAUD_110 = 0x2;
    public static final int BAUD_134_5 = 0x4;
    public static final int BAUD_150 = 0x8;
    public static final int BAUD_300 = 0x10;
    public static final int BAUD_600 = 0x20;
    public static final int BAUD_1200 = 0x40;
    public static final int BAUD_1800 = 0x80;
    public static final int BAUD_2400 = 0x100;
    public static final int BAUD_4800 = 0x200;
    public static final int BAUD_7200 = 0x400;
    public static final int BAUD_9600 = 0x800;
    public static final int BAUD_14400 = 0x1000;
    public static final int BAUD_19200 = 0x2000;
    public static final int BAUD_38400 = 0x4000;
    public static final int BAUD_56K = 0x8000;
    public static final int BAUD_128K = 0x10000;
    public static final int BAUD_115200 = 0x20000;
    public static final int BAUD_57600 = 0x40000;
    public static final int BAUD_USER = 0x10000000;

    public static final short DATABITS_5 = 0x1;
    public static final short DATABITS_6 = 0x2;
    public static final short DATABITS_7 = 0x4;
    public static final short DATABITS_8 = 0x8;
    public static final short DATABITS_16 = 0x10;
    public static final short DATABITS_16X = 0x20;

    public static final short STOPBITS_10 = 0x1;
    public static final short STOPBITS_15 = 0x2;
    public static final short STOPBITS_20 = 0x4;
    public static final short PARITY_NONE = 0x100;
    public static final short PARITY_ODD = 0x200;
    public static final short PARITY_EVEN = 0x400;
    public static final short PARITY_MARK = 0x800;
    public static final short PARITY_SPACE = 0x1000;

    public static final int STATUS_TIMEOUT = 0x00000102;
    public static final int STATUS_PENDING = 0x00000103;
    public static final int DBG_EXCEPTION_HANDLED = 0x00010001;
    public static final int DBG_CONTINUE = 0x00010002;
    public static final int STATUS_SEGMENT_NOTIFICATION = 0x40000005;
    public static final int STATUS_FATAL_APP_EXIT = 0x40000015;
    public static final int DBG_TERMINATE_THREAD = 0x40010003;
    public static final int DBG_TERMINATE_PROCESS = 0x40010004;
    public static final int DBG_CONTROL_C = 0x40010005;
    public static final int DBG_PRINTEXCEPTION_C = 0x40010006;
    public static final int DBG_RIPEXCEPTION = 0x40010007;
    public static final int DBG_CONTROL_BREAK = 0x40010008;
    public static final int DBG_COMMAND_EXCEPTION = 0x40010009;
    public static final int STATUS_GUARD_PAGE_VIOLATION = 0x80000001;
    public static final int STATUS_DATATYPE_MISALIGNMENT = 0x80000002;
    public static final int STATUS_BREAKPOINT = 0x80000003;
    public static final int STATUS_SINGLE_STEP = 0x80000004;
    public static final int STATUS_LONGJUMP = 0x80000026;
    public static final int STATUS_UNWIND_CONSOLIDATE = 0x80000029;
    public static final int DBG_EXCEPTION_NOT_HANDLED = 0x80010001;
    public static final int STATUS_ACCESS_VIOLATION = 0xC0000005;
    public static final int STATUS_IN_PAGE_ERROR = 0xC0000006;
    public static final int STATUS_INVALID_HANDLE = 0xC0000008;
    public static final int STATUS_INVALID_PARAMETER = 0xC000000D;
    public static final int STATUS_NO_MEMORY = 0xC0000017;
    public static final int STATUS_ILLEGAL_INSTRUCTION = 0xC000001D;
    public static final int STATUS_NONCONTINUABLE_EXCEPTION = 0xC0000025;
    public static final int STATUS_INVALID_DISPOSITION = 0xC0000026;
    public static final int STATUS_ARRAY_BOUNDS_EXCEEDED = 0xC000008C;
    public static final int STATUS_FLOAT_DENORMAL_OPERAND = 0xC000008D;
    public static final int STATUS_FLOAT_DIVIDE_BY_ZERO = 0xC000008E;
    public static final int STATUS_FLOAT_INEXACT_RESULT = 0xC000008F;
    public static final int STATUS_FLOAT_INVALID_OPERATION = 0xC0000090;
    public static final int STATUS_FLOAT_OVERFLOW = 0xC0000091;
    public static final int STATUS_FLOAT_STACK_CHECK = 0xC0000092;
    public static final int STATUS_FLOAT_UNDERFLOW = 0xC0000093;
    public static final int STATUS_INTEGER_DIVIDE_BY_ZERO = 0xC0000094;
    public static final int STATUS_INTEGER_OVERFLOW = 0xC0000095;
    public static final int STATUS_PRIVILEGED_INSTRUCTION = 0xC0000096;
    public static final int STATUS_STACK_OVERFLOW = 0xC00000FD;
    public static final int STATUS_DLL_NOT_FOUND = 0xC0000135;
    public static final int STATUS_ORDINAL_NOT_FOUND = 0xC0000138;
    public static final int STATUS_ENTRYPOINT_NOT_FOUND = 0xC0000139;
    public static final int STATUS_CONTROL_C_EXIT = 0xC000013A;
    public static final int STATUS_DLL_INIT_FAILED = 0xC0000142;
    public static final int STATUS_FLOAT_MULTIPLE_FAULTS = 0xC00002B4;
    public static final int STATUS_FLOAT_MULTIPLE_TRAPS = 0xC00002B5;
    public static final int STATUS_REG_NAT_CONSUMPTION = 0xC00002C9;
    public static final int STATUS_STACK_BUFFER_OVERRUN = 0xC0000409;
    public static final int STATUS_INVALID_CRUNTIME_PARAMETER = 0xC0000417;
    public static final int STATUS_ASSERTION_FAILURE = 0xC0000420;
    public static final int STATUS_SXS_EARLY_DEACTIVATION = 0xC015000F;
    public static final int STATUS_SXS_INVALID_DEACTIVATION = 0xC0150010;

    public abstract DCB createDCB();

    public static class COMMPROP {

        public short wPacketLength;
        public short wPacketVersion;
        public int dwServiceMask;
        public int dwReserved1;
        public int dwMaxTxQueue;
        public int dwMaxRxQueue;
        public int dwMaxBaud;
        public int dwProvSubType;
        public int dwProvCapabilities;
        public int dwSettableParams;
        public int dwSettableBaud;
        public short wSettableData;
        public short wSettableStopParity;
        public int dwCurrentTxQueue;
        public int dwCurrentRxQueue;
        public int dwProvSpec1;
        public int dwProvSpec2;
        //TODO 8 Bit???
        /**
         * TODO Length
         */
        public final char[] wcProvChar = new char[1];
    }

    public static final int COMMPROP_INITIALIZED = 0xe73cf52e;

    public static class COMSTAT {

        public boolean fCtsHold;
        public boolean fDsrHold;
        public boolean fRlsdHold;
        public boolean fXoffHold;
        public boolean fXoffSent;
        public boolean fEof;
        public boolean fTxim;
        public int fReserved;
        public int cbInQue;
        public int cbOutQue;
    }

    public static final int DTR_CONTROL_DISABLE = 0x0;
    public static final int DTR_CONTROL_ENABLE = 0x1;
    public static final int DTR_CONTROL_HANDSHAKE = 0x2;

    public static final int RTS_CONTROL_DISABLE = 0x0;
    public static final int RTS_CONTROL_ENABLE = 0x1;
    public static final int RTS_CONTROL_HANDSHAKE = 0x2;
    public static final int RTS_CONTROL_TOGGLE = 0x3;

    
    //TODO         dcb.DCBlength = sizeof(DCB);
    public static class DCB {

        public int DCBlength;
        public int BaudRate;
        public boolean fBinary;
        public boolean fParity;
        public boolean fOutxCtsFlow;
        public boolean fOutxDsrFlow;
        public int fDtrControl;
        public boolean fDsrSensitivity;
        public boolean fTXContinueOnXoff;
        public boolean fOutX;
        public boolean fInX;
        public boolean fErrorChar;
        public boolean fNull;
        public int fRtsControl;
        public boolean fAbortOnError;
        public int fDummy2;
        public short wReserved;
        public short XonLim;
        public short XoffLim;
        public byte ByteSize;
        public byte Parity;
        public byte StopBits;
        public char XonChar;
        public char XoffChar;
        public char ErrorChar;
        public char EofChar;
        public char EvtChar;
        public short wReserved1;
    }

    public static class COMMTIMEOUTS {

        public int ReadIntervalTimeout;
        public int ReadTotalTimeoutMultiplier;
        public int ReadTotalTimeoutConstant;
        public int WriteTotalTimeoutMultiplier;
        public int WriteTotalTimeoutConstant;
    }

    public static class COMMCONFIG {

        public int dwSize;
        public short wVersion;
        public short wReserved;
        public DCB dcb;
        public int dwProviderSubType;
        public int dwProviderOffset;
        public int dwProviderSize;
        //TODO WCHAR 
        public final char[] wcProviderData = new char[1];
    }

    public static final HANDLE INVALID_HANDLE_VALUE = HANDLE.of(-1);

    public abstract boolean CloseHandle(HANDLE hObject);
    
  //__attribute__((dllimport)) WINBOOL PulseEvent (HANDLE hEvent);
  //__attribute__((dllimport)) DWORD WaitForMultipleObjects (DWORD nCount, const HANDLE *lpHandles, WINBOOL bWaitAll, DWORD dwMilliseconds);
  //__attribute__((dllimport)) ATOM GlobalDeleteAtom (ATOM nAtom);
  //__attribute__((dllimport)) WINBOOL InitAtomTable (DWORD nSize);
 // __attribute__((dllimport)) ATOM DeleteAtom (ATOM nAtom);
  //__attribute__((dllimport)) UINT SetHandleCount (UINT uNumber);
//  __attribute__((dllimport)) WINBOOL RequestDeviceWakeup (HANDLE hDevice);
  //__attribute__((dllimport)) WINBOOL CancelDeviceWakeupRequest (HANDLE hDevice);
  //__attribute__((dllimport)) WINBOOL GetDevicePowerState (HANDLE hDevice, WINBOOL *pfOn);
  //__attribute__((dllimport)) WINBOOL SetMessageWaitingIndicator (HANDLE hMsgIndicator, ULONG ulMsgCount);
  //__attribute__((dllimport)) WINBOOL SetFileShortNameA (HANDLE hFile, LPCSTR lpShortName);
  //__attribute__((dllimport)) WINBOOL SetFileShortNameW (HANDLE hFile, LPCWSTR lpShortName);
  //__attribute__((dllimport)) DWORD LoadModule (LPCSTR lpModuleName, LPVOID lpParameterBlock);
 // __attribute__((dllimport)) UINT WinExec (LPCSTR lpCmdLine, UINT uCmdShow);
  public abstract boolean ClearCommBreak (HANDLE hFile);
  public abstract boolean ClearCommError (HANDLE hFile, LPDWORD lpErrors, COMSTAT lpStat);
  public abstract boolean SetupComm (HANDLE hFile, long dwInQueue, long dwOutQueue);
  public abstract boolean EscapeCommFunction (HANDLE hFile, int dwFunc);
  public abstract boolean GetCommConfig (HANDLE hCommDev, COMMCONFIG lpCC, LPDWORD lpdwSize);
  public abstract boolean GetCommMask (HANDLE hFile, LPDWORD lpEvtMask);
  public abstract boolean GetCommProperties (HANDLE hFile, COMMPROP lpCommProp);
  public abstract boolean GetCommModemStatus (HANDLE hFile, LPDWORD lpModemStat);
  public abstract boolean GetCommState (HANDLE hFile, DCB lpDCB);
  public abstract boolean GetCommTimeouts (HANDLE hFile, COMMTIMEOUTS lpCommTimeouts);
  public abstract boolean PurgeComm (HANDLE hFile, int dwFlags);
  public abstract boolean SetCommBreak (HANDLE hFile);
  public abstract boolean SetCommConfig (HANDLE hCommDev, COMMCONFIG lpCC, long dwSize);
  public abstract boolean SetCommMask (HANDLE hFile, int dwEvtMask);
  public abstract boolean SetCommState (HANDLE hFile, DCB lpDCB);
  public abstract boolean SetCommTimeouts (HANDLE hFile, COMMTIMEOUTS lpCommTimeouts);
  public abstract boolean TransmitCommChar (HANDLE hFile, char cChar);
  public abstract boolean WaitCommEvent (HANDLE hFile, LPDWORD lpEvtMask, OVERLAPPED lpOverlapped);
  //__attribute__((dllimport)) DWORD SetTapePosition (HANDLE hDevice, DWORD dwPositionMethod, DWORD dwPartition, DWORD dwOffsetLow, DWORD dwOffsetHigh, WINBOOL bImmediate);
  //__attribute__((dllimport)) DWORD GetTapePosition (HANDLE hDevice, DWORD dwPositionType, LPDWORD lpdwPartition, LPDWORD lpdwOffsetLow, LPDWORD lpdwOffsetHigh);
  //__attribute__((dllimport)) DWORD PrepareTape (HANDLE hDevice, DWORD dwOperation, WINBOOL bImmediate);
  //__attribute__((dllimport)) DWORD EraseTape (HANDLE hDevice, DWORD dwEraseType, WINBOOL bImmediate);
  //__attribute__((dllimport)) DWORD CreateTapePartition (HANDLE hDevice, DWORD dwPartitionMethod, DWORD dwCount, DWORD dwSize);
  //__attribute__((dllimport)) DWORD WriteTapemark (HANDLE hDevice, DWORD dwTapemarkType, DWORD dwTapemarkCount, WINBOOL bImmediate);
  //__attribute__((dllimport)) DWORD GetTapeStatus (HANDLE hDevice);
  //__attribute__((dllimport)) DWORD GetTapeParameters (HANDLE hDevice, DWORD dwOperation, LPDWORD lpdwSize, LPVOID lpTapeInformation);
  //__attribute__((dllimport)) DWORD SetTapeParameters (HANDLE hDevice, DWORD dwOperation, LPVOID lpTapeInformation);
  //__attribute__((dllimport)) DEP_SYSTEM_POLICY_TYPE GetSystemDEPPolicy (void);
  //__attribute__((dllimport)) WINBOOL GetSystemRegistryQuota (PDWORD pdwQuotaAllowed, PDWORD pdwQuotaUsed);
  //WINBOOL GetSystemTimes (LPFILETIME lpIdleTime, LPFILETIME lpKernelTime, LPFILETIME lpUserTime);
  //__attribute__((dllimport)) WINBOOL FileTimeToDosDateTime (const FILETIME *lpFileTime, LPWORD lpFatDate, LPWORD lpFatTime);
  //__attribute__((dllimport)) WINBOOL DosDateTimeToFileTime (WORD wFatDate, WORD wFatTime, LPFILETIME lpFileTime);
  //__attribute__((dllimport)) WINBOOL SetSystemTimeAdjustment (DWORD dwTimeAdjustment, WINBOOL bTimeAdjustmentDisabled);

  
  public static final int  DRIVE_UNKNOWN = 0;
public static final int  DRIVE_NO_ROOT_DIR = 1;
public static final int  DRIVE_REMOVABLE = 2;
public static final int  DRIVE_FIXED = 3;
public static final int  DRIVE_REMOTE = 4;
public static final int  DRIVE_CDROM = 5;
public static final int  DRIVE_RAMDISK = 6;

public static final int  FILE_TYPE_UNKNOWN = 0x0;
public static final int  FILE_TYPE_DISK = 0x1;
public static final int  FILE_TYPE_CHAR = 0x2;
public static final int  FILE_TYPE_PIPE = 0x3;
public static final int  FILE_TYPE_REMOTE = 0x8000;

public static final int  STD_INPUT_HANDLE = -10;
public static final int  STD_OUTPUT_HANDLE = -11;
public static final int  STD_ERROR_HANDLE = -12;

public static final int  NOPARITY = 0;
public static final int  ODDPARITY = 1;
public static final int  EVENPARITY = 2;
public static final int  MARKPARITY = 3;
public static final int  SPACEPARITY = 4;

public static final int  ONESTOPBIT = 0;
public static final int  ONE5STOPBITS = 1;
public static final int  TWOSTOPBITS = 2;

public static final int  IGNORE = 0;
public static final int  INFINITE = 0xffffffff;

public static final int  CBR_110 = 110;
public static final int  CBR_300 = 300;
public static final int  CBR_600 = 600;
public static final int  CBR_1200 = 1200;
public static final int  CBR_2400 = 2400;
public static final int  CBR_4800 = 4800;
public static final int  CBR_9600 = 9600;
public static final int  CBR_14400 = 14400;
public static final int  CBR_19200 = 19200;
public static final int  CBR_38400 = 38400;
public static final int  CBR_56000 = 56000;
public static final int  CBR_57600 = 57600;
public static final int  CBR_115200 = 115200;
public static final int  CBR_128000 = 128000;
public static final int  CBR_256000 = 256000;

public static final int   CE_RXOVER = 0x1;
public static final int   CE_OVERRUN = 0x2;
public static final int   CE_RXPARITY = 0x4;
public static final int   CE_FRAME = 0x8;
public static final int   CE_BREAK = 0x10;
public static final int   CE_TXFULL = 0x100;
public static final int   CE_PTO = 0x200;
public static final int   CE_IOE = 0x400;
public static final int   CE_DNS = 0x800;
public static final int   CE_OOP = 0x1000;
public static final int   CE_MODE = 0x8000;

public static final int   IE_BADID = -1;
public static final int   IE_OPEN = -2;
public static final int   IE_NOPEN = -3;
public static final int   IE_MEMORY = -4;
public static final int   IE_DEFAULT = -5;
public static final int   IE_HARDWARE = -10;
public static final int   IE_BYTESIZE = -11;
public static final int   IE_BAUDRATE = -12;

public static final int   EV_RXCHAR = 0x1;
public static final int   EV_RXFLAG = 0x2;
public static final int   EV_TXEMPTY = 0x4;
public static final int   EV_CTS = 0x8;
public static final int   EV_DSR = 0x10;
public static final int   EV_RLSD = 0x20;
public static final int   EV_BREAK = 0x40;
public static final int   EV_ERR = 0x80;
public static final int   EV_RING = 0x100;
public static final int   EV_PERR = 0x200;
public static final int   EV_RX80FULL = 0x400;
public static final int   EV_EVENT1 = 0x800;
public static final int   EV_EVENT2 = 0x1000;

public static final int   SETXOFF = 1;
public static final int   SETXON = 2;
public static final int   SETRTS = 3;
public static final int   CLRRTS = 4;
public static final int   SETDTR = 5;
public static final int   CLRDTR = 6;
public static final int   RESETDEV = 7;
public static final int   SETBREAK = 8;
public static final int   CLRBREAK = 9;

public static final int   PURGE_TXABORT = 0x1;
public static final int   PURGE_RXABORT = 0x2;
public static final int   PURGE_TXCLEAR = 0x4;
public static final int   PURGE_RXCLEAR = 0x8;

public static final int   LPTx = 0x80;

public static final int   MS_CTS_ON = 0x10;
public static final int   MS_DSR_ON = 0x20;
public static final int   MS_RING_ON = 0x40;
public static final int   MS_RLSD_ON = 0x80;

public static final int   S_QUEUEEMPTY = 0;
public static final int   S_THRESHOLD = 1;
public static final int   S_ALLTHRESHOLD = 2;

public static final int   S_NORMAL = 0;
public static final int   S_LEGATO = 1;
public static final int   S_STACCATO = 2;

public static final int   S_PERIOD512 = 0;
public static final int   S_PERIOD1024 = 1;
public static final int   S_PERIOD2048 = 2;
public static final int   S_PERIODVOICE = 3;
public static final int   S_WHITE512 = 4;
public static final int   S_WHITE1024 = 5;
public static final int   S_WHITE2048 = 6;
public static final int   S_WHITEVOICE = 7;

public static final int   S_SERDVNA = -1;
public static final int   S_SEROFM = -2;
public static final int   S_SERMACT = -3;
public static final int   S_SERQFUL = -4;
public static final int   S_SERBDNT = -5;
public static final int   S_SERDLN = -6;
public static final int   S_SERDCC = -7;
public static final int   S_SERDTP = -8;
public static final int   S_SERDVL = -9;
public static final int   S_SERDMD = -10;
public static final int   S_SERDSH = -11;
public static final int   S_SERDPT = -12;
public static final int   S_SERDFQ = -13;
public static final int   S_SERDDR = -14;
public static final int   S_SERDSR = -15;
public static final int   S_SERDST = -16;


public abstract int GetLastError();

}
