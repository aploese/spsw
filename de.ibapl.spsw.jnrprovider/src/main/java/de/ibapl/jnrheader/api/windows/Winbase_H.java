package de.ibapl.jnrheader.api.windows;

import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.Wrapper;
import de.ibapl.jnrheader.api.windows.Minwindef_H.HANDLE;

@Wrapper("winbase.h")
public abstract class Winbase_H implements JnrHeader {
	public static final HANDLE INVALID_HANDLE_VALUE = HANDLE.of(-1);


	  public abstract boolean CloseHandle (HANDLE hObject);

}
