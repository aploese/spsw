package de.ibapl.jnrheader.api.windows;

import java.nio.charset.Charset;

import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.Wrapper;
import java.lang.ref.SoftReference;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

@Wrapper("minwindef.h")
public abstract class Minwindef_H implements JnrHeader {

    private final static ThreadLocal<SoftReference<CharsetEncoder>> WIDE_ENCODER = new ThreadLocal<>();
    private final static ThreadLocal<SoftReference<CharsetDecoder>> WIDE_DECODER = new ThreadLocal<>();

    private static CharsetEncoder getWideEncoder() {
        SoftReference<CharsetEncoder> cse = WIDE_ENCODER.get();
        if (cse == null) {
            cse = new SoftReference<>(Charset.forName(JnrHeader.UTF16_LE_ENCODING).newEncoder());
            WIDE_ENCODER.set(cse);
        }
        return cse.get();
    }

    private static CharsetDecoder getWideDecoder() {
        SoftReference<CharsetDecoder> csd = WIDE_DECODER.get();
        if (csd == null) {
            csd = new SoftReference<>(Charset.forName(JnrHeader.UTF16_LE_ENCODING).newDecoder());
            WIDE_DECODER.set(csd);
        }
        return csd.get();
    }

    public static class LPVOID {
        public long address;
        
        public LPVOID(long address) {
            this.address = address;
        }

        LPVOID() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    public static class HKEY extends HANDLE {

        private HKEY(long value) {
            super(value);
        }

        public HKEY() {
            super();
        }

        public static HKEY ofLong(long value) {
            return new HKEY(value);
        }

    }

    public static class PHKEY implements Pointer<HKEY> {

        public HKEY indirection = new HKEY();

        public PHKEY() {
        }

        @Override
        public HKEY getIndirection() {
            return indirection;
        }

        @Override
        public void setIndirection(HKEY indirection) {
            this.indirection = indirection;
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

    public static class LPDWORD {

        public long value;

        private LPDWORD() {

        }

        public static LPDWORD ofValue(long value) {
            LPDWORD result = new LPDWORD();
            result.value = value;
            return result;
        }

    }

    //TODO Win32 use int instead of long???
    public static class HANDLE {

        public long value;

        protected HANDLE(long value) {
            this.value = value;
        }

        protected HANDLE() {
        }

        public static HANDLE of(long value) {
            return new HANDLE(value);
        }
        
        @Override
        public boolean equals(Object other) {
            if (other == null) {
                return false;
            }
            if (other.getClass() != getClass()) {
                return false;
            }
            return value == ((HANDLE)other).value;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 23 * hash + (int) (this.value ^ (this.value >>> 32));
            hash = 23 * hash + getClass().hashCode();
            return hash;
        }
        
    }

    /**
     *
     * The wrapper for a ByteBuffer. The position of the buffer is always 0! It
     * must be reset to 0 if changed. The limit of the buffer is always amount
     * of valid bytes in the buffer and must be set if the amount of valid bytes
     * changed.
     */
    public static class LPWSTR {

        private ByteBuffer buffer;

        private LPWSTR() {
        }

        public static LPWSTR of(String value) {
            LPWSTR result = new LPWSTR();
            CharsetEncoder cse = getWideEncoder();
            assert (int) Math.ceil(cse.maxBytesPerChar()) == 2;
            result.buffer = ByteBuffer.allocateDirect(value.length() * 2);
            CharBuffer cb = CharBuffer.wrap(value);
            cse.encode(cb, result.buffer, true);
            result.buffer.flip();
            return result;
        }

        public static LPWSTR allocate(int capacity) {
            LPWSTR result = new LPWSTR();
            result.buffer = ByteBuffer.allocateDirect(capacity);
            return result;
        }

        public String toString() {
            CharsetDecoder csd = getWideDecoder();
            CharBuffer cb;
            try {
                cb = csd.decode(buffer);
                buffer.position(0);
                return cb.toString();
            } catch (CharacterCodingException ex) {
                Logger.getLogger(Minwindef_H.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            }
        }

        public Buffer backingBuffer() {
            return buffer;
        }

        public void clear() {
            buffer.clear();
        }

        public static String buffer2String(ByteBuffer buffer, boolean nullTerminationIncluded) {
            final int oldPosition = buffer.position();
            if (nullTerminationIncluded) {
                buffer.limit(buffer.limit() - 2);
            }
            CharsetDecoder csd = getWideDecoder();
            CharBuffer cb;
            try {
                cb = csd.decode(buffer);
                buffer.position(oldPosition);
                if (nullTerminationIncluded) {
                    buffer.limit(buffer.limit() + 2);
                }
                return cb.toString();
            } catch (CharacterCodingException ex) {
                Logger.getLogger(Minwindef_H.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            }
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
