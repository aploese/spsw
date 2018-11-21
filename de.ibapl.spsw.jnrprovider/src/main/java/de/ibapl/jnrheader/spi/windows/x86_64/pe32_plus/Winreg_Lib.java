package de.ibapl.jnrheader.spi.windows.x86_64.pe32_plus;

import static de.ibapl.jnrheader.JnrHeader.UTF16_LE_ENCODING;
import de.ibapl.jnrheader.api.windows.Minwindef_H.LPWSTR;
import de.ibapl.jnrheader.api.windows.Minwindef_H.HKEY;
import de.ibapl.jnrheader.api.windows.Minwindef_H.LPDWORD;
import de.ibapl.jnrheader.api.windows.Minwindef_H.PHKEY;
import jnr.ffi.LibraryLoader;
import jnr.ffi.annotations.StdCall;
import jnr.ffi.byref.LongLongByReference;
import de.ibapl.jnrheader.api.windows.Winreg_H;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import jnr.ffi.annotations.Encoding;
import jnr.ffi.annotations.In;
import jnr.ffi.byref.IntByReference;

public class Winreg_Lib extends Winreg_H {

	@de.ibapl.jnrheader.NativeFunctions
	protected interface NativeFunctions {

		@StdCall
		long RegEnumValueW(long hKey, int dwIndex, Buffer lpValueName,
				IntByReference lpcchValueName, LPDWORD lpReserved, LPDWORD lpType, ByteBuffer lpData,
				IntByReference lpcbData);

		@StdCall
		long RegOpenKeyExW(long hKey, @In @Encoding(UTF16_LE_ENCODING) String lpSubKey, int ulOptions, int samDesired,
				PHKEY phkResult);
	}

	final private NativeFunctions nativeFunctions;

	public Winreg_Lib() {
		nativeFunctions = LibraryLoader.create(NativeFunctions.class).load("Advapi32");
	}

	@Override
	public long RegEnumValueW(HKEY hKey, int dwIndex, LPWSTR lpValueName, LPDWORD lpType, ByteBuffer lpData) {

            //TODO drop assert
            assert lpValueName.backingBuffer().position() == 0;
            assert lpData.position() == 0;
            
            IntByReference lpcchValueName = new IntByReference(lpValueName.backingBuffer().limit());
            IntByReference lpcbData = new IntByReference(lpData.limit());
                    
            final long result = nativeFunctions.RegEnumValueW(hKey.value, dwIndex, lpValueName.backingBuffer(), lpcchValueName, null,
				lpType, lpData, lpcbData);
            lpValueName.backingBuffer().limit(lpValueName.backingBuffer().position() + lpcchValueName.intValue());
            lpData.limit(lpData.position() + lpcbData.intValue());
            return result;
	}

	@Override
	public long RegOpenKeyExW(HKEY hKey, String lpSubKey, int ulOptions, REGSAM samDesired, PHKEY phkResult) {
		final long result = nativeFunctions.RegOpenKeyExW(hKey.value, lpSubKey, ulOptions, samDesired.value,
				phkResult);
		return result;
	}

}
