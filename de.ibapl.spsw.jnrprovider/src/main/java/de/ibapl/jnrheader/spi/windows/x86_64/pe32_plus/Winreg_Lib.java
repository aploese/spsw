package de.ibapl.jnrheader.spi.windows.x86_64.pe32_plus;

import static de.ibapl.jnrheader.JnrHeader.UTF16_LE_ENCODING;
import de.ibapl.jnrheader.api.windows.Minwindef_H;
import de.ibapl.jnrheader.api.windows.Minwindef_H.HKEY;
import de.ibapl.jnrheader.api.windows.Minwindef_H.LPBYTE;
import de.ibapl.jnrheader.api.windows.Minwindef_H.LPDWORD;
import de.ibapl.jnrheader.api.windows.Minwindef_H.PHKEY;
import jnr.ffi.LibraryLoader;
import jnr.ffi.annotations.StdCall;
import jnr.ffi.byref.IntByReference;
import jnr.ffi.byref.LongLongByReference;
import de.ibapl.jnrheader.api.windows.Winreg_H;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import jnr.ffi.annotations.Encoding;
import jnr.ffi.annotations.In;
import sun.nio.cs.ThreadLocalCoders;

public class Winreg_Lib extends Winreg_H {

	@de.ibapl.jnrheader.NativeFunctions
	protected interface NativeFunctions {

		@StdCall
		long RegEnumValueW(long hKey, int dwIndex, @Encoding(UTF16_LE_ENCODING) char[] lpValueName,
				IntByReference lpcchValueName, IntByReference lpReserved, IntByReference lpType, byte[] lpData,
				IntByReference lpcbData);

		@StdCall
		long RegOpenKeyExW(long hKey, @In @Encoding(UTF16_LE_ENCODING) String lpSubKey, int ulOptions, int samDesired,
				LongLongByReference phkResult);
	}

	final private NativeFunctions nativeFunctions;

	public Winreg_Lib() {
		nativeFunctions = LibraryLoader.create(NativeFunctions.class).load("Advapi32");
	}

	@Override
	public long RegEnumValueW(HKEY hKey, int dwIndex, char[] lpValueName, LPDWORD lpcchValueName, LPDWORD lpReserved,
			LPDWORD lpType, byte[] lpData, LPDWORD lpcbData) {
		IntByReference lpcchValueNameRef = new IntByReference(lpcchValueName.value);
		IntByReference lpcbDataRef = lpcbData != null ? new IntByReference(lpcbData.value) : null;
		IntByReference lpReservedRef = lpReserved != null ? new IntByReference(lpReserved.value) : null;
		IntByReference lpTypeRef = lpType != null ? new IntByReference(lpType.value) : null;

		long result = nativeFunctions.RegEnumValueW(hKey.value, dwIndex, lpValueName, lpcchValueNameRef, lpReservedRef,
				lpTypeRef, lpData, lpcbDataRef);
		lpcchValueName.value = lpcchValueNameRef.intValue();
		if (lpcbData != null) {
			lpcbData.value = lpcbDataRef.intValue();
		}
		if (lpReserved != null) {
			lpReserved.value = lpReservedRef.intValue();
		}
		if (lpType != null) {
			lpType.value = lpTypeRef.intValue();
		}
		return result;
	}

	@Override
	public long RegOpenKeyExW(HKEY hKey, String lpSubKey, int ulOptions, REGSAM samDesired, PHKEY phkResult) {
		final LongLongByReference phkResultRef = new LongLongByReference();
		final long result = nativeFunctions.RegOpenKeyExW(hKey.value, lpSubKey, ulOptions, samDesired.value,
				phkResultRef);
		phkResult.value = HKEY.ofLong(phkResultRef.longValue());
		return result;
	}

}
