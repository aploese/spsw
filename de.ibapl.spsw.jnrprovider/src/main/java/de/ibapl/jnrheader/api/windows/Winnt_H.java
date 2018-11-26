package de.ibapl.jnrheader.api.windows;

import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.Wrapper;

@Wrapper("winnt.h")
public abstract class Winnt_H implements JnrHeader {

    public static class ACCESS_MASK {

        public final int value;

        protected ACCESS_MASK(int value) {
            this.value = value;
        }

    }

    public static final byte MINCHAR = (byte) 0x80;
    public static final byte MAXCHAR = 0x7f;
    public static final short MINSHORT = (short) 0x8000;
    public static final short MAXSHORT = 0x7fff;
    public static final int MINLONG = 0x80000000;
    public static final int MAXLONG = 0x7fffffff;
    public static final byte MAXBYTE = (byte) 0xff;
    public static final short MAXWORD = (short) 0xffff;
    public static final int MAXDWORD = 0xffffffff;

    public static final int DELETE = 0x00010000;
    public static final int READ_CONTROL = 0x00020000;
    public static final int WRITE_DAC = 0x00040000;
    public static final int WRITE_OWNER = 0x00080000;
    public static final int SYNCHRONIZE = 0x00100000;

    public static final int STANDARD_RIGHTS_REQUIRED = 0x000F0000;

    public static final int STANDARD_RIGHTS_READ = READ_CONTROL;
    public static final int STANDARD_RIGHTS_WRITE = READ_CONTROL;
    public static final int STANDARD_RIGHTS_EXECUTE = READ_CONTROL;

    public static final int STANDARD_RIGHTS_ALL = 0x001F0000;

    public static final int SPECIFIC_RIGHTS_ALL = 0x0000FFFF;

    public static final int ACCESS_SYSTEM_SECURITY = 0x01000000;
    public static final int MAXIMUM_ALLOWED = 0x02000000;

    public static final int GENERIC_READ = 0x80000000;
    public static final int GENERIC_WRITE = 0x40000000;
    public static final int GENERIC_EXECUTE = 0x20000000;
    public static final int GENERIC_ALL = 0x10000000;

    public static final int KEY_QUERY_VALUE = 0x0001;
    public static final int KEY_SET_VALUE = 0x0002;
    public static final int KEY_CREATE_SUB_KEY = 0x0004;
    public static final int KEY_ENUMERATE_SUB_KEYS = 0x0008;
    public static final int KEY_NOTIFY = 0x0010;
    public static final int KEY_CREATE_LINK = 0x0020;
    public static final int KEY_WOW64_64KEY = 0x0100;
    public static final int KEY_WOW64_32KEY = 0x0200;
    public static final int KEY_WOW64_RES = 0x0300;

    public static final int KEY_READ = (STANDARD_RIGHTS_READ | KEY_QUERY_VALUE | KEY_ENUMERATE_SUB_KEYS | KEY_NOTIFY)
            & ~SYNCHRONIZE;
    public static final int KEY_WRITE = (STANDARD_RIGHTS_WRITE | KEY_SET_VALUE | KEY_CREATE_SUB_KEY) & ~SYNCHRONIZE;
    public static final int KEY_EXECUTE = KEY_READ & ~SYNCHRONIZE;
    public static final int KEY_ALL_ACCESS = (STANDARD_RIGHTS_ALL | KEY_QUERY_VALUE | KEY_SET_VALUE | KEY_CREATE_SUB_KEY
            | KEY_ENUMERATE_SUB_KEYS | KEY_NOTIFY | KEY_CREATE_LINK) & ~SYNCHRONIZE;
    public static final int REG_OPTION_RESERVED = 0x00000000;

    public static final int REG_OPTION_NON_VOLATILE = 0x00000000;
    public static final int REG_OPTION_VOLATILE = 0x00000001;
    public static final int REG_OPTION_CREATE_LINK = 0x00000002;
    public static final int REG_OPTION_BACKUP_RESTORE = 0x00000004;
    public static final int REG_OPTION_OPEN_LINK = 0x00000008;
    public static final int REG_LEGAL_OPTION = REG_OPTION_RESERVED | REG_OPTION_NON_VOLATILE | REG_OPTION_VOLATILE
            | REG_OPTION_CREATE_LINK | REG_OPTION_BACKUP_RESTORE | REG_OPTION_OPEN_LINK;

    public static final int REG_CREATED_NEW_KEY = 0x00000001;
    public static final int REG_OPENED_EXISTING_KEY = 0x00000002;

    public static final int REG_STANDARD_FORMAT = 1;
    public static final int REG_LATEST_FORMAT = 2;
    public static final int REG_NO_COMPRESSION = 4;

    public static final int REG_WHOLE_HIVE_VOLATILE = 0x00000001;
    public static final int REG_REFRESH_HIVE = 0x00000002;
    public static final int REG_NO_LAZY_FLUSH = 0x00000004;
    public static final int REG_FORCE_RESTORE = 0x00000008;
    public static final int REG_APP_HIVE = 0x00000010;
    public static final int REG_PROCESS_PRIVATE = 0x00000020;
    public static final int REG_START_JOURNAL = 0x00000040;
    public static final int REG_HIVE_EXACT_FILE_GROWTH = 0x00000080;
    public static final int REG_HIVE_NO_RM = 0x00000100;
    public static final int REG_HIVE_SINGLE_LOG = 0x00000200;
    public static final int REG_BOOT_HIVE = 0x00000400;

    public static final int REG_FORCE_UNLOAD = 1;

    public static final int REG_NOTIFY_CHANGE_NAME = 0x00000001;
    public static final int REG_NOTIFY_CHANGE_ATTRIBUTES = 0x00000002;
    public static final int REG_NOTIFY_CHANGE_LAST_SET = 0x00000004;
    public static final int REG_NOTIFY_CHANGE_SECURITY = 0x00000008;
    public static final int REG_NOTIFY_THREAD_AGNOSTIC = 0x10000000;

    public static final int REG_LEGAL_CHANGE_FILTER = REG_NOTIFY_CHANGE_NAME | REG_NOTIFY_CHANGE_ATTRIBUTES
            | REG_NOTIFY_CHANGE_LAST_SET | REG_NOTIFY_CHANGE_SECURITY | REG_NOTIFY_THREAD_AGNOSTIC;

    public static final int REG_NONE = 0;
    public static final int REG_SZ = 1;
    public static final int REG_EXPAND_SZ = 2;
    public static final int REG_BINARY = 3;
    public static final int REG_DWORD = 4;
    public static final int REG_DWORD_LITTLE_ENDIAN = 4;
    public static final int REG_DWORD_BIG_ENDIAN = 5;
    public static final int REG_LINK = 6;
    public static final int REG_MULTI_SZ = 7;
    public static final int REG_RESOURCE_LIST = 8;
    public static final int REG_FULL_RESOURCE_DESCRIPTOR = 9;
    public static final int REG_RESOURCE_REQUIREMENTS_LIST = 10;
    public static final int REG_QWORD = 11;
    public static final int REG_QWORD_LITTLE_ENDIAN = 11;
    
}
