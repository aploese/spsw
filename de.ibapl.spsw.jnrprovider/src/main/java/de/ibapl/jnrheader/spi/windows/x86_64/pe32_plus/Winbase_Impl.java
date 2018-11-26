package de.ibapl.jnrheader.spi.windows.x86_64.pe32_plus;

import de.ibapl.jnrheader.api.windows.Minwindef_H.HANDLE;
import jnr.ffi.LibraryLoader;
import jnr.ffi.annotations.StdCall;
import de.ibapl.jnrheader.api.windows.Winbase_H;

public abstract class Winbase_Impl extends Winbase_H {

	@de.ibapl.jnrheader.NativeFunctions
	protected interface NativeFunctions {
		 @StdCall
		boolean CloseHandle(long hObject);
	}

	final private NativeFunctions nativeFunctions;

	public Winbase_Impl() {
		nativeFunctions = LibraryLoader.create(NativeFunctions.class).load("Kernel32");
	}


	@Override
	public boolean CloseHandle(HANDLE hObject) {
		return nativeFunctions.CloseHandle(hObject.value);
	}

}
