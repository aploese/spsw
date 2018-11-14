package de.ibapl.jnrheader.api.windows;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.Wrapper;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.logging.Level;
import java.util.logging.Logger;

@Wrapper("minwindef.h")
public abstract class Minwindef_H implements JnrHeader {

    public static class LPVOID {

    }

    public static class HKEY extends HANDLE {

        private HKEY(long value) {
            super(value);
        }

        public static HKEY ofLong(long value) {
            return new HKEY(value);
        }

    }

    public static class PHKEY extends Pointer<HKEY> {

        public PHKEY() {
            super(null);
        }

        public PHKEY(HKEY hkey) {
            super(hkey);
        }

    }

    public static class HRESULT {

        public static HRESULT of(int hresult) {
            return new HRESULT(hresult);
        }

        public final int hresult;

        private HRESULT(int hresult) {
            this.hresult = hresult;
        }

    }

    
    public static class LPBYTE {

        public byte value;

    }
    
    public static class LPDWORD {

        public int value;
        
        public static LPDWORD ofValue(int value) {
        	LPDWORD result = new LPDWORD();
        	result.value = value;
        	return result;
        }

    }

    public static class HANDLE {

        public long value;

        protected HANDLE(long value) {
            this.value = value;
        }

        public static HANDLE of(long value) {
            return new HANDLE(value);
        }
    }

    /*
         *Naming of Strings:
        LP == Long Pointer. Just think pointer or char*

C = Const, in this case, I think they mean the character string is a const, not the pointer being const.

STR is string

the T is for a wide character or char (TCHAR) depending on compile options.
     */
 }
