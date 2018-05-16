package de.ibapl.jnrheader.spi.windows.x86_64.pe32_plus;

import de.ibapl.jnrheader.api.windows.Minwindef_H.HKEY;
import de.ibapl.jnrheader.api.windows.Minwindef_H.LPBYTE;
import de.ibapl.jnrheader.api.windows.Minwindef_H.LPDWORD;
import de.ibapl.jnrheader.api.windows.Minwindef_H.LPTSTR;
import de.ibapl.jnrheader.api.windows.Minwindef_H.PHKEY;
import jnr.ffi.LibraryLoader;
import jnr.ffi.annotations.StdCall;
import jnr.ffi.byref.IntByReference;
import jnr.ffi.byref.LongLongByReference;
import de.ibapl.jnrheader.api.windows.Winreg_H;

public class Winreg_Lib extends Winreg_H {
	
	@de.ibapl.jnrheader.NativeFunctions
	protected interface NativeFunctions {
		
		
		 @StdCall
		long RegEnumValue(long hKey, int dwIndex, byte[] lpValueName, IntByReference lpcchValueName, int[] lpReserved,
				IntByReference lpType, byte[] lpData, IntByReference lpcbData);
		 
		 @StdCall
		long RegOpenKeyEx(long hKey, byte[] lpSubKey, int ulOptions, int samDesired, LongLongByReference phkResult);
	}

	final private NativeFunctions nativeFunctions;

	public Winreg_Lib() {
		nativeFunctions = LibraryLoader.create(NativeFunctions.class).load("Advapi32");
	}


	@Override
	public long RegEnumValue(HKEY hKey, int dwIndex, LPTSTR lpValueName, LPDWORD lpcchValueName, LPDWORD lpReserved,
			LPDWORD lpType, LPBYTE lpData, LPDWORD lpcbData) {
//		long result = nativeFunctions.RegEnumValue(hKey.value, dwIndex, lpValueName.value, lpcchValueName.value[0], lpReserved == null? null : lpReserved.value, lpType == null? null : lpType.value, lpData== null? null : lpbData.value, lpcbData== null? null :  lpcbData.value[0])
//		return result;
		return 0;
	}


	@Override
	public long RegOpenKeyEx(HKEY hKey, LPTSTR lpSubKey, int ulOptions, REGSAM samDesired, PHKEY phkResult) {
		final LongLongByReference phkResultRef = new LongLongByReference();
		final long result = nativeFunctions.RegOpenKeyEx(hKey.value, lpSubKey.value, ulOptions, samDesired.value, phkResultRef);
		phkResult.value = HKEY.ofLong(phkResultRef.longValue());
		return result;
	}

}
