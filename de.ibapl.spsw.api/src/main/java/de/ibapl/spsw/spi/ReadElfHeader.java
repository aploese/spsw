package de.ibapl.spsw.spi;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReadElfHeader {
	public enum Format {
		_32_BIT,
		_64_BIT;
	}
	
	public enum Endian {
		BIG_ENDIAN,
		LITTLE_ENDIAN;
	}
	
	private final Format format;
	private final Endian endian;
	
	public ReadElfHeader() throws IOException {
		this(new FileInputStream("/proc/self/exe"));
	}
	
	public ReadElfHeader(InputStream is) throws IOException {
		if ((is.read() != 0x7f) || (is.read() != 'E')   || (is.read() != 'L') || (is.read() != 'F')) {
			throw new RuntimeException("Not an ELF file");
		}
		switch (is.read()) {
		case 0x01:
			format = Format._32_BIT;
			break;
		case 0x02:
			format = Format._64_BIT;
			break;
		default:
			throw new RuntimeException("Cant figure out format");
		}
		switch (is.read()) {
		case 0x01:
			endian = Endian.LITTLE_ENDIAN;
			break;
		case 0x02:
			endian = Endian.BIG_ENDIAN;
			break;
		default:
			throw new RuntimeException("Cant figure out endian");
		}
	}

	public Format getFormat() {
		return format;
	}

	public Endian getEndian() {
		return endian;
	}

}
