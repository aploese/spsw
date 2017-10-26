package de.ibapl.spsw.spi;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReadElfHeader {
	public enum Format {
		_32_BIT, _64_BIT;
	}

	public enum Endian {
		BIG_ENDIAN, LITTLE_ENDIAN;
	}

	public enum OsAbi {
		SYSTEM_V, HP_UX, LINUX, NET_BSD, GNU_HURD, SOLARIS, AIX, IRIX, NONSTOP_KERNEL, OPEN_VMS, AROS, OPEN_BSD, FREE_BSD, TRU_64, NOVELL_MODESTO, FENIX_OS, SORTIX, CLOUD_ABI;
	}

	public enum Type {
		RELOCATABLE, EXECUTABLE, SHARED, CORE;
	}

	public enum Isa {
		NO_SPECIFIC_INSTRUCTION_SET

		, SPARC, X86, MIPS, POWER_PC, S390, ARM, SUPER_H, IA_64, X86_64, A_ARCH_64, RISC_V
	}

	private Format e_ident__ei_class;
	private Endian e_ident__ei_data;
	private byte e_ident__ei_version;
	private OsAbi e_ident__ei_osAbi;
	private byte e_ident__ei_abiVersion;
	private Type e_type;
	private Isa e_machine;

	private transient InputStream is;

	public ReadElfHeader() throws IOException {
		this(new FileInputStream("/proc/self/exe"));
	}

	public ReadElfHeader(InputStream is) throws IOException {
		this.is = is;
		try {

			// e_ident[EI_MAG0] through e_ident[EI_MAG3]
			if ((readByte() != 0x7f) || (readByte() != 'E') || (readByte() != 'L') || (readByte() != 'F')) {
				throw new RuntimeException("Not an ELF file");
			}

			// e_ident[EI_CLASS]
			switch (readByte()) {
			case 0x01:
				e_ident__ei_class = Format._32_BIT;
				break;
			case 0x02:
				e_ident__ei_class = Format._64_BIT;
				break;
			default:
				throw new RuntimeException("Cant figure out format");
			}

			// e_ident[EI_DATA]
			switch (readByte()) {
			case 0x01:
				e_ident__ei_data = Endian.LITTLE_ENDIAN;
				break;
			case 0x02:
				e_ident__ei_data = Endian.BIG_ENDIAN;
				break;
			default:
				throw new RuntimeException("Cant figure out endian");
			}

			// e_ident[EI_VERSION]
			e_ident__ei_version = (byte) is.read();
			switch (readByte()) {
			case 0x00:
				e_ident__ei_osAbi = OsAbi.SYSTEM_V;
				break;
			case 0x01:
				e_ident__ei_osAbi = OsAbi.HP_UX;
				break;
			case 0x02:
				e_ident__ei_osAbi = OsAbi.NET_BSD;
				break;
			case 0x03:
				e_ident__ei_osAbi = OsAbi.LINUX;
				break;
			case 0x04:
				e_ident__ei_osAbi = OsAbi.GNU_HURD;
				break;
			case 0x06:
				e_ident__ei_osAbi = OsAbi.SOLARIS;
				break;
			case 0x07:
				e_ident__ei_osAbi = OsAbi.AIX;
				break;
			case 0x08:
				e_ident__ei_osAbi = OsAbi.IRIX;
				break;
			case 0x09:
				e_ident__ei_osAbi = OsAbi.FREE_BSD;
				break;
			case 0x0A:
				e_ident__ei_osAbi = OsAbi.TRU_64;
				break;
			case 0x0B:
				e_ident__ei_osAbi = OsAbi.NOVELL_MODESTO;
				break;
			case 0x0C:
				e_ident__ei_osAbi = OsAbi.OPEN_BSD;
				break;
			case 0x0D:
				e_ident__ei_osAbi = OsAbi.OPEN_VMS;
				break;
			case 0x0E:
				e_ident__ei_osAbi = OsAbi.NONSTOP_KERNEL;
				break;
			case 0x0F:
				e_ident__ei_osAbi = OsAbi.AROS;
				break;
			case 0x10:
				e_ident__ei_osAbi = OsAbi.FENIX_OS;
				break;
			case 0x11:
				e_ident__ei_osAbi = OsAbi.CLOUD_ABI;
				break;
			case 0x53:
				e_ident__ei_osAbi = OsAbi.SORTIX;
				break;
			default:
				throw new RuntimeException("Cant figure out OsAbi");
			}
			// e_ident[EI_ABIVERSION]
			e_ident__ei_abiVersion = readByte();
			// e_ident[EI_PAD]
			readByte(7);
			// e_type
			switch (readShort()) {
			case 0x01:
				e_type = Type.RELOCATABLE;
				break;
			case 0x02:
				e_type = Type.EXECUTABLE;
				break;
			case 0x03:
				e_type = Type.SHARED;
				break;
			case 0x04:
				e_type = Type.CORE;
				break;
			default:
				throw new RuntimeException("Cant figure out Type");

			}
			switch (readShort()) {
			case 0x00:
				e_machine = Isa.NO_SPECIFIC_INSTRUCTION_SET;
				break;
			case 0x02:
				e_machine = Isa.SPARC;
				break;
			case 0x03:
				e_machine = Isa.X86;
				break;
			case 0x08:
				e_machine = Isa.MIPS;
				break;
			case 0x14:
				e_machine = Isa.POWER_PC;
				break;
			case 0x16:
				e_machine = Isa.S390;
				break;
			case 0x28:
				e_machine = Isa.ARM;
				break;
			case 0x2A:
				e_machine = Isa.SUPER_H;
				break;
			case 0x32:
				e_machine = Isa.IA_64;
				break;
			case 0x3E:
				e_machine = Isa.X86_64;
				break;
			case 0xB7:
				e_machine = Isa.A_ARCH_64;
				break;
			case 0xF3:
				e_machine = Isa.RISC_V;
				break;
			default:
				throw new RuntimeException("Cant figure out machine");
			}
			
			
			
			
		//TODO in den lags ist bei arm sf oder hf
			
			
		} finally {
			is.close();
		}

	}

	private void readByte(int i) throws IOException {
		final int result = is.read(new byte[i]);
		if (result <= 0) {
			throw new IOException("Stream Closed");
		}
	}

	private short readShort() throws IOException {
		final byte[] data = new byte[2];
		final int result = is.read(data);
		if (result <= 0) {
			throw new IOException("Stream Closed");
		}
		return (short) (data[0] & 0xff | ((data[1] & 0xff) << 8));
	}

	private byte readByte() throws IOException {
		final int result = is.read();
		if (result < 0) {
			throw new IOException("Stream Closed");
		}
		return (byte) result;
	}

	public Format getFormat() {
		return e_ident__ei_class;
	}

	public Endian getEndian() {
		return e_ident__ei_data;
	}

	public byte getElfVersion() {
		return e_ident__ei_version;
	}

	public OsAbi getOsAbi() {
		return e_ident__ei_osAbi;
	}

	public byte getAbiVersion() {
		return e_ident__ei_abiVersion;
	}

	public Type getType() {
		return e_type;
	}

	public Isa getMachine() {
		return e_machine;
	}

}
