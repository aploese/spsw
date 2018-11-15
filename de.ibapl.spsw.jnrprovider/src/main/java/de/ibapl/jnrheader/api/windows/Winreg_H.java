package de.ibapl.jnrheader.api.windows;

import de.ibapl.jnrheader.Defined;
import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.Wrapper;
import de.ibapl.jnrheader.api.windows.Minwindef_H.HKEY;
import de.ibapl.jnrheader.api.windows.Minwindef_H.LPBYTE;
import de.ibapl.jnrheader.api.windows.Minwindef_H.LPDWORD;
import de.ibapl.jnrheader.api.windows.Minwindef_H.LPVOID;
import de.ibapl.jnrheader.api.windows.Minwindef_H.PHKEY;
import de.ibapl.jnrheader.api.windows.Winnt_H.ACCESS_MASK;

@Wrapper("winreg.h")
public abstract class Winreg_H implements JnrHeader {
	
	public static class REGSAM extends ACCESS_MASK {

		public static REGSAM of(int value) {
			return new REGSAM(value);
		}
		private REGSAM(int value) {
			super(value);
		}

	}

	public static final int RRF_RT_REG_NONE = 0x00000001;
	public static final int RRF_RT_REG_SZ = 0x00000002;
	public static final int RRF_RT_REG_EXPAND_SZ = 0x00000004;	
	public static final int RRF_RT_REG_BINARY = 0x00000008;
	public static final int RRF_RT_REG_DWORD = 0x00000010;
	public static final int RRF_RT_REG_MULTI_SZ = 0x00000020;
	public static final int RRF_RT_REG_QWORD = 0x00000040;
	public static final int RRF_RT_DWORD = (RRF_RT_REG_BINARY | RRF_RT_REG_DWORD);
	public static final int RRF_RT_QWORD = (RRF_RT_REG_BINARY | RRF_RT_REG_QWORD);
	public static final int RRF_RT_ANY = 0x0000ffff;
	public static final int RRF_NOEXPAND = 0x10000000;
	public static final int RRF_ZEROONFAILURE = 0x20000000;
	public static final HKEY HKEY_CLASSES_ROOT = HKEY.ofLong(0x80000000);
	public static final HKEY HKEY_CURRENT_USER = HKEY.ofLong(0x80000001);
	public static final HKEY HKEY_LOCAL_MACHINE = HKEY.ofLong(0x80000002);
	public static final HKEY HKEY_USERS = HKEY.ofLong(0x80000003);
	public static final HKEY HKEY_PERFORMANCE_DATA = HKEY.ofLong(0x80000004);
	public static final HKEY HKEY_PERFORMANCE_TEXT = HKEY.ofLong(0x80000050);
	public static final HKEY HKEY_PERFORMANCE_NLSTEXT = HKEY.ofLong(0x80000060);
	public static final HKEY HKEY_CURRENT_CONFIG = HKEY.ofLong(0x80000005);
	public static final HKEY HKEY_DYN_DATA = HKEY.ofLong(0x80000006);
	public static final int REG_SECURE_CONNECTION = 1;
	public static final Defined _PROVIDER_STRUCTS_DEFINED= Defined.DEFINED;
	public static final int PROVIDER_KEEPS_VALUE_LENGTH = 0x1;
	public static final Integer WIN31_CLASS = null;
	public static final Defined SENTINEL_Reason = Defined.DEFINED;
	public static final int SHTDN_REASON_FLAG_COMMENT_REQUIRED = 0x01000000;
	public static final int SHTDN_REASON_FLAG_DIRTY_PROBLEM_ID_REQUIRED = 0x02000000;
	public static final int SHTDN_REASON_FLAG_CLEAN_UI = 0x04000000;
	public static final int SHTDN_REASON_FLAG_DIRTY_UI = 0x08000000;
	public static final int SHTDN_REASON_FLAG_USER_DEFINED = 0x40000000;
	public static final int SHTDN_REASON_FLAG_PLANNED = 0x80000000;
	public static final int SHTDN_REASON_MAJOR_OTHER = 0x00000000;
	public static final int SHTDN_REASON_MAJOR_NONE = 0x00000000;
	public static final int SHTDN_REASON_MAJOR_HARDWARE = 0x00010000;
	public static final int SHTDN_REASON_MAJOR_OPERATINGSYSTEM = 0x00020000;
	public static final int SHTDN_REASON_MAJOR_SOFTWARE = 0x00030000;
	public static final int SHTDN_REASON_MAJOR_APPLICATION = 0x00040000;
	public static final int SHTDN_REASON_MAJOR_SYSTEM = 0x00050000;
	public static final int SHTDN_REASON_MAJOR_POWER = 0x00060000;
	public static final int SHTDN_REASON_MAJOR_LEGACY_API = 0x00070000;
	public static final int SHTDN_REASON_MINOR_OTHER = 0x00000000;
	public static final int SHTDN_REASON_MINOR_NONE = 0x000000ff;
	public static final int SHTDN_REASON_MINOR_MAINTENANCE = 0x00000001;
	public static final int SHTDN_REASON_MINOR_INSTALLATION = 0x00000002; 
	public static final int SHTDN_REASON_MINOR_UPGRADE = 0x00000003;
	public static final int SHTDN_REASON_MINOR_RECONFIG = 0x00000004;
	public static final int SHTDN_REASON_MINOR_HUNG = 0x00000005;
	public static final int SHTDN_REASON_MINOR_UNSTABLE = 0x00000006;
	public static final int SHTDN_REASON_MINOR_DISK = 0x00000007;
	public static final int SHTDN_REASON_MINOR_PROCESSOR = 0x00000008;
	public static final int SHTDN_REASON_MINOR_NETWORKCARD = 0x00000009;
	public static final int SHTDN_REASON_MINOR_POWER_SUPPLY = 0x0000000a;
	public static final int SHTDN_REASON_MINOR_CORDUNPLUGGED = 0x0000000b;
	public static final int SHTDN_REASON_MINOR_ENVIRONMENT = 0x0000000c;
	public static final int SHTDN_REASON_MINOR_HARDWARE_DRIVER = 0x0000000d;
	public static final int SHTDN_REASON_MINOR_OTHERDRIVER = 0x0000000e;
	public static final int SHTDN_REASON_MINOR_BLUESCREEN = 0x0000000F;
	public static final int SHTDN_REASON_MINOR_SERVICEPACK = 0x00000010;
	public static final int SHTDN_REASON_MINOR_HOTFIX = 0x00000011;
	public static final int SHTDN_REASON_MINOR_SECURITYFIX = 0x00000012;
	public static final int SHTDN_REASON_MINOR_SECURITY = 0x00000013;
	public static final int SHTDN_REASON_MINOR_NETWORK_CONNECTIVITY = 0x00000014;
	public static final int SHTDN_REASON_MINOR_WMI = 0x00000015;
	public static final int SHTDN_REASON_MINOR_SERVICEPACK_UNINSTALL = 0x00000016;
	public static final int SHTDN_REASON_MINOR_HOTFIX_UNINSTALL = 0x00000017;
	public static final int SHTDN_REASON_MINOR_SECURITYFIX_UNINSTALL = 0x00000018;
	public static final int SHTDN_REASON_MINOR_MMC = 0x00000019;
	public static final int SHTDN_REASON_MINOR_SYSTEMRESTORE = 0x0000001a;
	public static final int SHTDN_REASON_MINOR_TERMSRV = 0x00000020;
	public static final int SHTDN_REASON_MINOR_DC_PROMOTION = 0x00000021;
	public static final int SHTDN_REASON_MINOR_DC_DEMOTION = 0x00000022;
	public static final int SHTDN_REASON_UNKNOWN = SHTDN_REASON_MINOR_NONE;
	public static final int SHTDN_REASON_LEGACY_API = (SHTDN_REASON_MAJOR_LEGACY_API | SHTDN_REASON_FLAG_PLANNED);
	public static final int SHTDN_REASON_VALID_BIT_MASK = 0xc0ffffff;
	public static final int PCLEANUI = (SHTDN_REASON_FLAG_PLANNED | SHTDN_REASON_FLAG_CLEAN_UI);
	public static final int UCLEANUI = (SHTDN_REASON_FLAG_CLEAN_UI);
	public static final int PDIRTYUI = (SHTDN_REASON_FLAG_PLANNED | SHTDN_REASON_FLAG_DIRTY_UI);
	public static final int UDIRTYUI = (SHTDN_REASON_FLAG_DIRTY_UI);
	public static final int MAX_REASON_NAME_LEN = 64;
	public static final int MAX_REASON_DESC_LEN = 256;
	public static final int MAX_REASON_BUGID_LEN = 32;
	public static final int MAX_REASON_COMMENT_LEN = 512;
	public static final int SHUTDOWN_TYPE_LEN = 32;
	public static final int POLICY_SHOWREASONUI_NEVER = 0;
	public static final int POLICY_SHOWREASONUI_ALWAYS = 1;
	public static final int POLICY_SHOWREASONUI_WORKSTATIONONLY = 2;
	public static final int POLICY_SHOWREASONUI_SERVERONLY = 3;
	public static final int SNAPSHOT_POLICY_NEVER = 0;
	public static final int SNAPSHOT_POLICY_ALWAYS = 1;
	public static final int SNAPSHOT_POLICY_UNPLANNED = 2;
	public static final int MAX_NUM_REASONS = 256;
	public static final int REASON_SWINSTALL = SHTDN_REASON_MAJOR_SOFTWARE|SHTDN_REASON_MINOR_INSTALLATION;
	public static final int REASON_HWINSTALL = SHTDN_REASON_MAJOR_HARDWARE|SHTDN_REASON_MINOR_INSTALLATION;
	public static final int REASON_SERVICEHANG = SHTDN_REASON_MAJOR_SOFTWARE|SHTDN_REASON_MINOR_HUNG;
	public static final int REASON_UNSTABLE = SHTDN_REASON_MAJOR_SYSTEM|SHTDN_REASON_MINOR_UNSTABLE;
	public static final int REASON_SWHWRECONF = SHTDN_REASON_MAJOR_SOFTWARE|SHTDN_REASON_MINOR_RECONFIG;
	public static final int REASON_OTHER = SHTDN_REASON_MAJOR_OTHER|SHTDN_REASON_MINOR_OTHER;
	public static final int REASON_UNKNOWN = SHTDN_REASON_UNKNOWN;
	public static final int REASON_LEGACY_API = SHTDN_REASON_LEGACY_API;
	public static final int REASON_PLANNED_FLAG = SHTDN_REASON_FLAG_PLANNED;
	public static final int MAX_SHUTDOWN_TIMEOUT = (10*365*24*60*60);

          //TODO	public abstract long RegCloseKey(HKEY hKey);
  //TODO	public abstract long RegOverridePredefKey(HKEY hKey,HKEY hNewHKey);
  //TODO	public abstract long RegOpenUserClassesRoot(HANDLE hToken,DWORD dwOptions,REGSAM samDesired,PHKEY phkResult);
  //TODO	public abstract long RegOpenCurrentUser(REGSAM samDesired,PHKEY phkResult);
  //TODO	public abstract long RegDisablePredefinedCache(void);
  //TODO	public abstract long RegConnectRegistryA(LPCSTR lpMachineName,HKEY hKey,PHKEY phkResult);
  //TODO	public abstract long RegConnectRegistryW(LPCWSTR lpMachineName,HKEY hKey,PHKEY phkResult);
  //TODO	public abstract long RegConnectRegistryExA(LPCSTR lpMachineName,HKEY hKey,ULONG Flags,PHKEY phkResult);
  //TODO	public abstract long RegConnectRegistryExW(LPCWSTR lpMachineName,HKEY hKey,ULONG Flags,PHKEY phkResult);
  //TODO	public abstract long RegCreateKeyA(HKEY hKey,LPCSTR lpSubKey,PHKEY phkResult);
  //TODO	public abstract long RegCreateKeyW(HKEY hKey,LPCWSTR lpSubKey,PHKEY phkResult);
  //TODO	public abstract long RegCreateKeyExA(HKEY hKey,LPCSTR lpSubKey,DWORD Reserved,LPSTR lpClass,DWORD dwOptions,REGSAM samDesired,LPSECURITY_ATTRIBUTES lpSecurityAttributes,PHKEY phkResult,LPDWORD lpdwDisposition);
  //TODO	public abstract long RegCreateKeyExW(HKEY hKey,LPCWSTR lpSubKey,DWORD Reserved,LPWSTR lpClass,DWORD dwOptions,REGSAM samDesired,LPSECURITY_ATTRIBUTES lpSecurityAttributes,PHKEY phkResult,LPDWORD lpdwDisposition);
  //TODO	public abstract long RegDeleteKeyA(HKEY hKey,LPCSTR lpSubKey);
  //TODO	public abstract long RegDeleteKeyW(HKEY hKey,LPCWSTR lpSubKey);
  //TODO	public abstract long RegDeleteKeyExA(HKEY hKey,LPCSTR lpSubKey,REGSAM samDesired,DWORD Reserved);
  //TODO	public abstract long RegDeleteKeyExW(HKEY hKey,LPCWSTR lpSubKey,REGSAM samDesired,DWORD Reserved);
  //TODO	public abstract long RegDisableReflectionKey(HKEY hBase);
  //TODO	public abstract long RegEnableReflectionKey(HKEY hBase);
  //TODO	public abstract long RegQueryReflectionKey(HKEY hBase,WINBOOL *bIsReflectionDisabled);
  //TODO	public abstract long RegDeleteValueA(HKEY hKey,LPCSTR lpValueName);
  //TODO	public abstract long RegDeleteValueW(HKEY hKey,LPCWSTR lpValueName);
  //TODO	public abstract long RegEnumKeyA(HKEY hKey,DWORD dwIndex,LPSTR lpName,DWORD cchName);
  //TODO	public abstract long RegEnumKeyW(HKEY hKey,DWORD dwIndex,LPWSTR lpName,DWORD cchName);
  //TODO	public abstract long RegEnumKeyExA(HKEY hKey,DWORD dwIndex,LPSTR lpName,LPDWORD lpcchName,LPDWORD lpReserved,LPSTR lpClass,LPDWORD lpcchClass,PFILETIME lpftLastWriteTime);
  //TODO	public abstract long RegEnumKeyExW(HKEY hKey,DWORD dwIndex,LPWSTR lpName,LPDWORD lpcchName,LPDWORD lpReserved,LPWSTR lpClass,LPDWORD lpcchClass,PFILETIME lpftLastWriteTime);
  //TODO	public abstract long RegEnumValueA(HKEY hKey,DWORD dwIndex,LPSTR lpValueName,LPDWORD lpcchValueName,LPDWORD lpReserved,LPDWORD lpType,LPBYTE lpData,LPDWORD lpcbData);
  /**
   *  <a href="https://docs.microsoft.com/en-us/windows/desktop/api/winreg/nf-winreg-regenumvaluew" >nf-winreg-regenumvaluew</a>
   * 
   * @param hKey
   * @param dwIndex
   * @param lpValueName
   * @param lpcchValueName
   * @param lpReserved
   * @param lpType
   * @param lpData
   * @param lpcbData
   * @return 
   */
        public abstract long RegEnumValueW(HKEY hKey,int dwIndex,char[] lpValueName,LPDWORD lpcchValueName,LPDWORD lpReserved,LPDWORD lpType,byte[] lpData,LPDWORD lpcbData);
  //TODO	public abstract long RegFlushKey(HKEY hKey);
  //TODO	public abstract long RegGetKeySecurity(HKEY hKey,SECURITY_INFORMATION SecurityInformation,PSECURITY_DESCRIPTOR pSecurityDescriptor,LPDWORD lpcbSecurityDescriptor);
  //TODO	public abstract long RegLoadKeyA(HKEY hKey,LPCSTR lpSubKey,LPCSTR lpFile);
  //TODO	public abstract long RegLoadKeyW(HKEY hKey,LPCWSTR lpSubKey,LPCWSTR lpFile);
  //TODO	public abstract long RegNotifyChangeKeyValue(HKEY hKey,WINBOOL bWatchSubtree,DWORD dwNotifyFilter,HANDLE hEvent,WINBOOL fAsynchronous);
  //TODO	public abstract long RegOpenKeyA(HKEY hKey,LPCSTR lpSubKey,PHKEY phkResult);
  //TODO	public abstract long RegOpenKeyW(HKEY hKey,LPCWSTR lpSubKey,PHKEY phkResult);
  //TODO	public abstract long RegOpenKeyExA(HKEY hKey,LPCSTR lpSubKey,DWORD ulOptions,REGSAM samDesired,PHKEY phkResult);
  	public abstract long RegOpenKeyExW(HKEY hKey,String lpSubKey,int ulOptions,REGSAM samDesired,PHKEY phkResult);
  //TODO	public abstract long RegQueryInfoKeyA(HKEY hKey,LPSTR lpClass,LPDWORD lpcchClass,LPDWORD lpReserved,LPDWORD lpcSubKeys,LPDWORD lpcbMaxSubKeyLen,LPDWORD lpcbMaxClassLen,LPDWORD lpcValues,LPDWORD lpcbMaxValueNameLen,LPDWORD lpcbMaxValueLen,LPDWORD lpcbSecurityDescriptor,PFILETIME lpftLastWriteTime);
  //TODO	public abstract long RegQueryInfoKeyW(HKEY hKey,LPWSTR lpClass,LPDWORD lpcchClass,LPDWORD lpReserved,LPDWORD lpcSubKeys,LPDWORD lpcbMaxSubKeyLen,LPDWORD lpcbMaxClassLen,LPDWORD lpcValues,LPDWORD lpcbMaxValueNameLen,LPDWORD lpcbMaxValueLen,LPDWORD lpcbSecurityDescriptor,PFILETIME lpftLastWriteTime);
  //TODO	public abstract long RegQueryValueA(HKEY hKey,LPCSTR lpSubKey,LPSTR lpData,PLONG lpcbData);
  //TODO	public abstract long RegQueryValueW(HKEY hKey,LPCWSTR lpSubKey,LPWSTR lpData,PLONG lpcbData);
  //TODO	public abstract long RegQueryMultipleValuesA(HKEY hKey,PVALENTA val_list,DWORD num_vals,LPSTR lpValueBuf,LPDWORD ldwTotsize);
  //TODO	public abstract long RegQueryMultipleValuesW(HKEY hKey,PVALENTW val_list,DWORD num_vals,LPWSTR lpValueBuf,LPDWORD ldwTotsize);
  //TODO	public abstract long RegQueryValueExA(HKEY hKey,LPCSTR lpValueName,LPDWORD lpReserved,LPDWORD lpType,LPBYTE lpData,LPDWORD lpcbData);
  //TODO	public abstract long RegQueryValueExW(HKEY hKey,LPCWSTR lpValueName,LPDWORD lpReserved,LPDWORD lpType,LPBYTE lpData,LPDWORD lpcbData);
  //TODO	public abstract long RegReplaceKeyA(HKEY hKey,LPCSTR lpSubKey,LPCSTR lpNewFile,LPCSTR lpOldFile);
  //TODO	public abstract long RegReplaceKeyW(HKEY hKey,LPCWSTR lpSubKey,LPCWSTR lpNewFile,LPCWSTR lpOldFile);
  //TODO	public abstract long RegRestoreKeyA(HKEY hKey,LPCSTR lpFile,DWORD dwFlags);
  //TODO	public abstract long RegRestoreKeyW(HKEY hKey,LPCWSTR lpFile,DWORD dwFlags);
  //TODO	public abstract long RegSaveKeyA(HKEY hKey,LPCSTR lpFile,LPSECURITY_ATTRIBUTES lpSecurityAttributes);
  //TODO	//TODO	public abstract long RegSaveKeyW(HKEY hKey,LPCWSTR lpFile,LPSECURITY_ATTRIBUTES lpSecurityAttributes);
  //TODO	public abstract long RegSetKeySecurity(HKEY hKey,SECURITY_INFORMATION SecurityInformation,PSECURITY_DESCRIPTOR pSecurityDescriptor);
  //TODO	public abstract long RegSetValueA(HKEY hKey,LPCSTR lpSubKey,DWORD dwType,LPCSTR lpData,DWORD cbData);
  //TODO	public abstract long RegSetValueW(HKEY hKey,LPCWSTR lpSubKey,DWORD dwType,LPCWSTR lpData,DWORD cbData);
  //TODO	public abstract long RegSetValueExA(HKEY hKey,LPCSTR lpValueName,DWORD Reserved,DWORD dwType,CONST BYTE *lpData,DWORD cbData);
  //TODO	public abstract long RegSetValueExW(HKEY hKey,LPCWSTR lpValueName,DWORD Reserved,DWORD dwType,CONST BYTE *lpData,DWORD cbData);
  //TODO	public abstract long RegUnLoadKeyA(HKEY hKey,LPCSTR lpSubKey);
  //TODO	public abstract long RegUnLoadKeyW(HKEY hKey,LPCWSTR lpSubKey);
  //TODO	public abstract long RegGetValueA(HKEY hkey,LPCSTR lpSubKey,LPCSTR lpValue,DWORD dwFlags,LPDWORD pdwType,PVOID pvData,LPDWORD pcbData);
  //TODO	public abstract long RegGetValueW(HKEY hkey,LPCWSTR lpSubKey,LPCWSTR lpValue,DWORD dwFlags,LPDWORD pdwType,PVOID pvData,LPDWORD pcbData);
//TODO	public abstract boolean InitiateSystemShutdownA(LPSTR lpMachineName,LPSTR lpMessage,DWORD dwTimeout,WINBOOL bForceAppsClosed,WINBOOL bRebootAfterShutdown);
  //TODO	public abstract boolean  InitiateSystemShutdownW(LPWSTR lpMachineName,LPWSTR lpMessage,DWORD dwTimeout,WINBOOL bForceAppsClosed,WINBOOL bRebootAfterShutdown);
  //TODO	public abstract boolean  AbortSystemShutdownA(LPSTR lpMachineName);
  //TODO	public abstract boolean  AbortSystemShutdownW(LPWSTR lpMachineName);
  //TODO	public abstract boolean  InitiateSystemShutdownExA(LPSTR lpMachineName,LPSTR lpMessage,DWORD dwTimeout,WINBOOL bForceAppsClosed,WINBOOL bRebootAfterShutdown,DWORD dwReason);
  //TODO	public abstract boolean  InitiateSystemShutdownExW(LPWSTR lpMachineName,LPWSTR lpMessage,DWORD dwTimeout,WINBOOL bForceAppsClosed,WINBOOL bRebootAfterShutdown,DWORD dwReason);
  //TODO	public abstract long RegSaveKeyExA(HKEY hKey,LPCSTR lpFile,LPSECURITY_ATTRIBUTES lpSecurityAttributes,DWORD Flags);
  //TODO	public abstract long RegSaveKeyExW(HKEY hKey,LPCWSTR lpFile,LPSECURITY_ATTRIBUTES lpSecurityAttributes,DWORD Flags);
  //TODO	public abstract long Wow64Win32ApiEntry (DWORD dwFuncNumber,DWORD dwFlag,DWORD dwRes);

}
