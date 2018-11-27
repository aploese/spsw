package de.ibapl.jnrheader.spi.windows;

import de.ibapl.jnrheader.api.windows.Minwindef_H;
import jnr.ffi.Runtime;
import jnr.ffi.byref.ByReference;

/**
 *
 * @author aploese
 */
public class LPDWORD_Wrapper implements ByReference<Minwindef_H.LPDWORD> {

    public static LPDWORD_Wrapper wrap(Minwindef_H.LPDWORD lpDword) {
        if (lpDword == null) {
            return null;
        } else {
            return new LPDWORD_Wrapper(lpDword);
        }
    }
    
    final Minwindef_H.LPDWORD lpDword;

    @Override
    public int nativeSize(Runtime runtime) {
        return 4;
    }

    @Override
    public void toNative(Runtime runtime, jnr.ffi.Pointer memory, long offset) {
        memory.putInt(offset, ((int) lpDword.value) & 0xFFFFFFFF);
    }

    @Override
    public void fromNative(Runtime runtime, jnr.ffi.Pointer memory, long offset) {
        lpDword.value = memory.getInt(offset) & 0xFFFFFFFF;
    }

    @Override
    public Minwindef_H.LPDWORD getValue() {
        return lpDword;
    }

    public LPDWORD_Wrapper() {
        this.lpDword = Minwindef_H.LPDWORD.ofValue(0);
    }

    public LPDWORD_Wrapper(Minwindef_H.LPDWORD lpDword) {
        this.lpDword = lpDword;
    }
    
}
