package de.ibapl.jnrheader.api.windows;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.Wrapper;

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
	
	public static class PHKEY extends Pointer<HKEY>{
		
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
		
		public byte[] value;
		
		private LPBYTE(int size) {
			this.value = new byte[size];
		}

		public static final LPBYTE ofSize(int size) {
			return new LPBYTE(size);
		}
		
		public static final LPBYTE ofValue(byte value) {
			final LPBYTE result = new LPBYTE(1);
			result.value[0] = value;
			return result;
		}

		public int length() {
			return value.length;
		}

	}

	
	public static class LPDWORD {
		
		public int[] value;
		
		private LPDWORD(int size) {
			this.value = new int[size];
		}

		public static final LPDWORD ofSize(int size) {
			return new LPDWORD(size);
		}
		
		public static final LPDWORD ofValue(int value) {
			final LPDWORD result = new LPDWORD(1);
			result.value[0] = value;
			return result;
		}
		
		public int length() {
			return value.length;
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

	public static class LPTSTR {
		
		public final static Charset CS_UTF_16LE = Charset.forName("UTF-16LE");

		public byte[] value;
		
		private LPTSTR(int size) {
			value = new byte[size]; 
		}
		
		private LPTSTR(String value) {
			this.value = value.getBytes(CS_UTF_16LE); 
		}
		
		public static final LPTSTR ofSize(int size) {
			return new LPTSTR(size);
		}
		
		public static final LPTSTR ofValue(String value) {
			return new LPTSTR(value);
		}
		

		public int length() {
			return value.length;
		}

		public String toString(int size) {
			return new String(value, 0, size, CS_UTF_16LE);
		}

	}

}
