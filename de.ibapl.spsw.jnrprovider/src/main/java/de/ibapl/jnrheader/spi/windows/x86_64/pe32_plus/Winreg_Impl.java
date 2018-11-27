package de.ibapl.jnrheader.spi.windows.x86_64.pe32_plus;

import static de.ibapl.jnrheader.JnrHeader.UTF16_LE_ENCODING;
import de.ibapl.jnrheader.api.windows.Minwindef_H.LPWSTR;
import de.ibapl.jnrheader.api.windows.Minwindef_H.HKEY;
import de.ibapl.jnrheader.api.windows.Minwindef_H.LPDWORD;
import de.ibapl.jnrheader.api.windows.Minwindef_H.PHKEY;
import jnr.ffi.LibraryLoader;
import jnr.ffi.annotations.StdCall;
import jnr.ffi.byref.LongLongByReference;
import jnr.ffi.byref.NativeLongByReference;
import jnr.ffi.types.int32_t;
import de.ibapl.jnrheader.api.windows.Winreg_H;
import de.ibapl.jnrheader.spi.windows.LPDWORD_Wrapper;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import jnr.ffi.annotations.Encoding;
import jnr.ffi.annotations.In;
import jnr.ffi.annotations.Out;
import jnr.ffi.byref.IntByReference;

public class Winreg_Impl extends Winreg_H {

	@de.ibapl.jnrheader.NativeFunctions
	protected interface NativeFunctions {

            
		@StdCall
		long RegEnumValueW(long hKey, int dwIndex, Buffer lpValueName,
				IntByReference lpcchValueName, IntByReference lpReserved, LPDWORD_Wrapper lpType, ByteBuffer lpData,
				IntByReference lpcbData);

		@StdCall
		long RegOpenKeyExW(long hKey, @In @Encoding(UTF16_LE_ENCODING) String lpSubKey, int ulOptions, int samDesired,
				@Out NativeLongByReference phkResult);
	}

	final private NativeFunctions nativeFunctions;

	public Winreg_Impl() {
		nativeFunctions = LibraryLoader.create(NativeFunctions.class).load("Advapi32");
	}

	@Override
	public long RegEnumValueW(HKEY hKey, int dwIndex, LPWSTR lpValueName, LPDWORD lpType, ByteBuffer lpData) {

            IntByReference lpcchValueName = new IntByReference(lpValueName.backingBuffer().limit());
            IntByReference lpcbData = new IntByReference(lpData.limit());
            final LPDWORD_Wrapper lpTypeWrapper = LPDWORD_Wrapper.wrap(lpType);
                    
            final long result = nativeFunctions.RegEnumValueW(hKey.value, dwIndex, lpValueName.backingBuffer(), lpcchValueName, null,
				lpTypeWrapper, lpData, lpcbData);
            lpValueName.backingBuffer().limit(lpcchValueName.intValue() * 2);
            lpData.limit(lpData.position() + lpcbData.intValue());
            return result;
	}

        //TODO PHKEY_Wrapper???
	@Override
	public long RegOpenKeyExW(HKEY hKey, String lpSubKey, int ulOptions, REGSAM samDesired, PHKEY phkResult) {
		NativeLongByReference phkResultRef = new NativeLongByReference(0);
		final long result = nativeFunctions.RegOpenKeyExW(hKey.value, lpSubKey, ulOptions, samDesired.value,
				phkResultRef);
		phkResult.indirection.value = phkResultRef.longValue();
		return result;
	}

}
