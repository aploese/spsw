package de.ibapl.nativeutils;

/*-
 * #%L
 * SPSW Provider
 * %%
 * Copyright (C) 2009 - 2018 Arne Plöse
 * %%
 * SPSW - Drivers for the serial port, https://github.com/aploese/spsw/
 * Copyright (C) 2009-2018, Arne Plöse and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * #L%
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import de.ibapl.nativeutils.ElfHeader.ElfClass;
import de.ibapl.nativeutils.ElfHeader.ElfData;
import de.ibapl.nativeutils.ElfHeader.ElfMachine;
import de.ibapl.nativeutils.ElfHeader.ElfOsAbi;
import de.ibapl.nativeutils.ElfHeader.ElfType;
import de.ibapl.nativeutils.aarch64.AArch64EFlags;
import de.ibapl.nativeutils.arm.ArmEFlags;
import de.ibapl.nativeutils.mips.MipsEFlags;
import de.ibapl.nativeutils.x86.X68EfFlags;

public class ElfFileParser {

	private ElfFile result;
	private InputStream is;
	public long e_entry;
	public long e_phoff;
	public long e_shoff;

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
		if (this.result.header.elfData == ElfData.ELFDATA2LSB) {
			return (short) (data[0] & 0xff | ((data[1] & 0xff) << 8));
		} else if (this.result.header.elfData == ElfData.ELFDATA2MSB) {
			return (short) (data[1] & 0xff | ((data[0] & 0xff) << 8));
		} else {
			throw new RuntimeException("Should never happen");
		}
	}

	private int readInt() throws IOException {
		final byte[] data = new byte[4];
		final int result = is.read(data);
		if (result <= 0) {
			throw new IOException("Stream Closed");
		}
		if (this.result.header.elfData == ElfData.ELFDATA2LSB) {
		return data[0] & 0xff | ((data[1] & 0xff) << 8) | ((data[2] & 0xff) << 16) | ((data[3] & 0xff) << 24);
		} else if (this.result.header.elfData == ElfData.ELFDATA2MSB) {
			return data[3] & 0xff | ((data[2] & 0xff) << 8) | ((data[1] & 0xff) << 16) | ((data[0] & 0xff) << 24);
		} else {
			throw new RuntimeException("Should never happen");
		}
	}

	private long readLong() throws IOException {
		final byte[] data = new byte[8];
		final int result = is.read(data);
		if (result <= 0) {
			throw new IOException("Stream Closed");
		}
		if (this.result.header.elfData == ElfData.ELFDATA2LSB) {
		return data[0] & 0xffL | ((data[1] & 0xffL) << 8) | ((data[2] & 0xffL) << 16) | ((data[3] & 0xffL) << 24)
				| ((data[4] & 0xffL) << 32) | ((data[5] & 0xffL) << 40) | ((data[6] & 0xffL) << 48)
				| ((data[7] & 0xffL) << 56);
		} else if (this.result.header.elfData == ElfData.ELFDATA2MSB) {
			return data[7] & 0xffL | ((data[6] & 0xffL) << 8) | ((data[5] & 0xffL) << 16) | ((data[4] & 0xffL) << 24)
					| ((data[3] & 0xffL) << 32) | ((data[2] & 0xffL) << 40) | ((data[1] & 0xffL) << 48)
					| ((data[0] & 0xffL) << 56);
		} else {
			throw new RuntimeException("Should never happen");
		}
	}

	private byte readByte() throws IOException {
		final int result = is.read();
		if (result < 0) {
			throw new IOException("Stream Closed");
		}
		return (byte) result;
	}

	private void parseHeader() throws IOException {

		// e_ident[EI_MAG0] through e_ident[EI_MAG3]
		if ((readByte() != 0x7f) || (readByte() != 'E') || (readByte() != 'L') || (readByte() != 'F')) {
			throw new RuntimeException("Not an ELF file");
		}

		result.header = new ElfHeader<>();
		// e_ident[EI_CLASS]
		result.header.elfClass = parse_e_ident__EI_CLASS(readByte());
		// e_ident[EI_DATA]
		result.header.elfData = parse_e_ident__EI_DATA(readByte());

		// e_ident[EI_VERSION]
		result.header.elfVersion = parse_e_ident__EI_VERSION(readByte());
		// e_ident[EI_OSABI]
		result.header.elfOsAbi = parse_e_ident__EI_OSABI(readByte());
		// e_ident[EI_ABIVERSION]
		result.header.elfAbiVersion = parse_e_ident__EI_ABIVERSION(readByte());
		// e_ident[EI_PAD]
		readByte(7);
		// e_type
		result.header.elfType = parse_e_type(readShort());
		// e_machine
		result.header.machine = parse_e_machine(readShort());
		// e_version
		result.header.version = parse_e_version(readInt());

		// e_entry
		// e_phoff
		// e_shoff
		switch (result.header.elfClass) {
		case ELFCLASS32:
			e_entry = readInt();
			e_phoff = readInt();
			e_shoff = readInt();
			break;
		case ELFCLASS64:
			e_entry = readLong();
			e_phoff = readLong();
			e_shoff = readLong();
			break;

		default:
			throw new RuntimeException();
		}
		switch (result.header.machine) {
		case EM_ARM:
			result.header.e_Flags = parse_e_flags_EM_ARM(readInt());
			break;
		case EM_AARCH64:
			result.header.e_Flags = parse_e_flags_EM_AARCH64(readInt());
			break;
		case EM_MIPS:
			result.header.e_Flags = parse_e_flags_EM_MIPS(readInt());
			break;
		case EM_386:
			result.header.e_Flags = parse_e_flags_EM_386(readInt());
			break;
		case EM_X86_64:
			result.header.e_Flags = parse_e_flags_EM_X86_64(readInt());
			break;
		default:
			throw new RuntimeException("Not implemented Yet");
		}

	}

	private Set<X68EfFlags> parse_e_flags_EM_386(int readInt) {
		Set<X68EfFlags> result = EnumSet.noneOf(X68EfFlags.class);
		if (readInt != 0) {
			throw new RuntimeException("Unexpected value");
		}
		return result;
	}

	private Set<X68EfFlags> parse_e_flags_EM_X86_64(int readInt) {
		Set<X68EfFlags> result = EnumSet.noneOf(X68EfFlags.class);
		if (readInt != 0) {
			throw new RuntimeException("Unexpected value");
		}
		return result;
	}

	private Set<MipsEFlags> parse_e_flags_EM_MIPS(int readInt) {
		Set<MipsEFlags> result = EnumSet.noneOf(MipsEFlags.class);
		switch (readInt & 0xF0000000) {
		case 0x00000000:
			result.add(MipsEFlags.EF_MIPS_ARCH_1);
			break;
		case 0x10000000:
			result.add(MipsEFlags.EF_MIPS_ARCH_2);
			break;
		case 0x20000000:
			result.add(MipsEFlags.EF_MIPS_ARCH_3);
			break;
		case 0x30000000:
			result.add(MipsEFlags.EF_MIPS_ARCH_4);
			break;
		case 0x40000000:
			result.add(MipsEFlags.EF_MIPS_ARCH_5);
			break;
		case 0x50000000:
			result.add(MipsEFlags.EF_MIPS_ARCH_32);
			break;
		case 0x60000000:
			result.add(MipsEFlags.EF_MIPS_ARCH_64);
			break;
		case 0x70000000:
			result.add(MipsEFlags.EF_MIPS_ARCH_32R2);
			break;
		case 0x80000000:
			result.add(MipsEFlags.EF_MIPS_ARCH_64R2);
			break;
		default:
			throw new RuntimeException("Cant figure out mips version");
		}
		switch (readInt & 0x0000F000) {
		case 0x00001000:
			result.add(MipsEFlags.EF_MIPS_ABI_O32);
			break;
		case 0x00002000:
			result.add(MipsEFlags.EF_MIPS_ABI_O64);
			break;
		default:
			// No-op 
		}

		if ((readInt & 0x00000001) == 0x00000001) {
			result.add(MipsEFlags.EF_MIPS_NOREORDER);
		}
		if ((readInt & 0x00000002) == 0x00000002) {
			result.add(MipsEFlags.EF_MIPS_PIC);
		}
		if ((readInt & 0x00000004) == 0x00000004) {
			result.add(MipsEFlags.EF_MIPS_CPIC);
		}
		if ((readInt & 0x00000020) == 0x00000020) {
			result.add(MipsEFlags.EF_MIPS_ABI2);
		}
		if ((readInt & 0x00000080) == 0x00000080) {
			result.add(MipsEFlags.EF_MIPS_OPTIONS_FIRST);
		}
		if ((readInt & 0x00000100) == 0x00000100) {
			result.add(MipsEFlags.EF_MIPS_32BITMODE);
		}
		if ((readInt & 0x00000200) == 0x00000200) {
			result.add(MipsEFlags.EF_MIPS_FP64);
		}
		if ((readInt & 0x00000400) == 0x00000400) {
			result.add(MipsEFlags.EF_MIPS_NAN2008);
		}
		return result;
	}

	private Set<AArch64EFlags> parse_e_flags_EM_AARCH64(int readInt) {
		Set<AArch64EFlags> result = EnumSet.noneOf(AArch64EFlags.class);
		if (readInt != 0) {
			throw new RuntimeException("Unexpected value");
		}
		return result;
	}

	private ElfMachine parse_e_machine(short readShort) {
		switch (readShort & 0xFFFF) {
		case 0:
			return ElfMachine.EM_NONE;
		case 1:
			return ElfMachine.EM_M32;
		case 2:
			return ElfMachine.EM_SPARC;
		case 3:
			return ElfMachine.EM_386;
		case 4:
			return ElfMachine.EM_68K;
		case 5:
			return ElfMachine.EM_88K;
		case 6:
			return ElfMachine.EM_IAMCU;
		case 7:
			return ElfMachine.EM_860;
		case 8:
			return ElfMachine.EM_MIPS;
		case 9:
			return ElfMachine.EM_S370;
		case 10:
			return ElfMachine.EM_MIPS_RS3_LE;
		case 15:
			return ElfMachine.EM_PARISC;
		case 17:
			return ElfMachine.EM_VPP500;
		case 18:
			return ElfMachine.EM_SPARC32PLUS;
		case 19:
			return ElfMachine.EM_960;
		case 20:
			return ElfMachine.EM_PPC;
		case 21:
			return ElfMachine.EM_PPC64;
		case 22:
			return ElfMachine.EM_S390;
		case 23:
			return ElfMachine.EM_SPU;
		case 36:
			return ElfMachine.EM_V800;
		case 37:
			return ElfMachine.EM_FR20;
		case 38:
			return ElfMachine.EM_RH32;
		case 39:
			return ElfMachine.EM_RCE;
		case 40:
			return ElfMachine.EM_ARM;
		case 41:
			return ElfMachine.EM_FAKE_ALPHA;
		case 42:
			return ElfMachine.EM_SH;
		case 43:
			return ElfMachine.EM_SPARCV9;
		case 44:
			return ElfMachine.EM_TRICORE;
		case 45:
			return ElfMachine.EM_ARC;
		case 46:
			return ElfMachine.EM_H8_300;
		case 47:
			return ElfMachine.EM_H8_300H;
		case 48:
			return ElfMachine.EM_H8S;
		case 49:
			return ElfMachine.EM_H8_500;
		case 50:
			return ElfMachine.EM_IA_64;
		case 51:
			return ElfMachine.EM_MIPS_X;
		case 52:
			return ElfMachine.EM_COLDFIRE;
		case 53:
			return ElfMachine.EM_68HC12;
		case 54:
			return ElfMachine.EM_MMA;
		case 55:
			return ElfMachine.EM_PCP;
		case 56:
			return ElfMachine.EM_NCPU;
		case 57:
			return ElfMachine.EM_NDR1;
		case 58:
			return ElfMachine.EM_STARCORE;
		case 59:
			return ElfMachine.EM_ME16;
		case 60:
			return ElfMachine.EM_ST100;
		case 61:
			return ElfMachine.EM_TINYJ;
		case 62:
			return ElfMachine.EM_X86_64;
		case 63:
			return ElfMachine.EM_PDSP;
		case 64:
			return ElfMachine.EM_PDP10;
		case 65:
			return ElfMachine.EM_PDP11;
		case 66:
			return ElfMachine.EM_FX66;
		case 67:
			return ElfMachine.EM_ST9PLUS;
		case 68:
			return ElfMachine.EM_ST7;
		case 69:
			return ElfMachine.EM_68HC16;
		case 70:
			return ElfMachine.EM_68HC11;
		case 71:
			return ElfMachine.EM_68HC08;
		case 72:
			return ElfMachine.EM_68HC05;
		case 73:
			return ElfMachine.EM_SVX;
		case 74:
			return ElfMachine.EM_ST19;
		case 75:
			return ElfMachine.EM_VAX;
		case 76:
			return ElfMachine.EM_CRIS;
		case 77:
			return ElfMachine.EM_JAVELIN;
		case 78:
			return ElfMachine.EM_FIREPATH;
		case 79:
			return ElfMachine.EM_ZSP;
		case 80:
			return ElfMachine.EM_MMIX;
		case 81:
			return ElfMachine.EM_HUANY;
		case 82:
			return ElfMachine.EM_PRISM;
		case 83:
			return ElfMachine.EM_AVR;
		case 84:
			return ElfMachine.EM_FR30;
		case 85:
			return ElfMachine.EM_D10V;
		case 86:
			return ElfMachine.EM_D30V;
		case 87:
			return ElfMachine.EM_V850;
		case 88:
			return ElfMachine.EM_M32R;
		case 89:
			return ElfMachine.EM_MN10300;
		case 90:
			return ElfMachine.EM_MN10200;
		case 91:
			return ElfMachine.EM_PJ;
		case 92:
			return ElfMachine.EM_OPENRISC;
		case 93:
			return ElfMachine.EM_ARC_COMPACT;
		case 94:
			return ElfMachine.EM_XTENSA;
		case 95:
			return ElfMachine.EM_VIDEOCORE;
		case 96:
			return ElfMachine.EM_TMM_GPP;
		case 97:
			return ElfMachine.EM_NS32K;
		case 98:
			return ElfMachine.EM_TPC;
		case 99:
			return ElfMachine.EM_SNP1K;
		case 100:
			return ElfMachine.EM_ST200;
		case 101:
			return ElfMachine.EM_IP2K;
		case 102:
			return ElfMachine.EM_MAX;
		case 103:
			return ElfMachine.EM_CR;
		case 104:
			return ElfMachine.EM_F2MC16;
		case 105:
			return ElfMachine.EM_MSP430;
		case 106:
			return ElfMachine.EM_BLACKFIN;
		case 107:
			return ElfMachine.EM_SE_C33;
		case 108:
			return ElfMachine.EM_SEP;
		case 109:
			return ElfMachine.EM_ARCA;
		case 110:
			return ElfMachine.EM_UNICORE;
		case 111:
			return ElfMachine.EM_EXCESS;
		case 112:
			return ElfMachine.EM_DXP;
		case 113:
			return ElfMachine.EM_ALTERA_NIOS2;
		case 114:
			return ElfMachine.EM_CRX;
		case 115:
			return ElfMachine.EM_XGATE;
		case 116:
			return ElfMachine.EM_C166;
		case 117:
			return ElfMachine.EM_M16C;
		case 118:
			return ElfMachine.EM_DSPIC30F;
		case 119:
			return ElfMachine.EM_CE;
		case 120:
			return ElfMachine.EM_M32C;
		case 131:
			return ElfMachine.EM_TSK3000;
		case 132:
			return ElfMachine.EM_RS08;
		case 133:
			return ElfMachine.EM_SHARC;
		case 134:
			return ElfMachine.EM_ECOG2;
		case 135:
			return ElfMachine.EM_SCORE7;
		case 136:
			return ElfMachine.EM_DSP24;
		case 137:
			return ElfMachine.EM_VIDEOCORE3;
		case 138:
			return ElfMachine.EM_LATTICEMICO32;
		case 139:
			return ElfMachine.EM_SE_C17;
		case 140:
			return ElfMachine.EM_TI_C6000;
		case 141:
			return ElfMachine.EM_TI_C2000;
		case 142:
			return ElfMachine.EM_TI_C5500;
		case 143:
			return ElfMachine.EM_TI_ARP32;
		case 144:
			return ElfMachine.EM_TI_PRU;
		case 160:
			return ElfMachine.EM_MMDSP_PLUS;
		case 161:
			return ElfMachine.EM_CYPRESS_M8C;
		case 162:
			return ElfMachine.EM_R32C;
		case 163:
			return ElfMachine.EM_TRIMEDIA;
		case 164:
			return ElfMachine.EM_QDSP6;
		case 165:
			return ElfMachine.EM_8051;
		case 166:
			return ElfMachine.EM_STXP7X;
		case 167:
			return ElfMachine.EM_NDS32;
		case 168:
			return ElfMachine.EM_ECOG1X;
		case 169:
			return ElfMachine.EM_MAXQ30;
		case 170:
			return ElfMachine.EM_XIMO16;
		case 171:
			return ElfMachine.EM_MANIK;
		case 172:
			return ElfMachine.EM_CRAYNV2;
		case 173:
			return ElfMachine.EM_RX;
		case 174:
			return ElfMachine.EM_METAG;
		case 175:
			return ElfMachine.EM_MCST_ELBRUS;
		case 176:
			return ElfMachine.EM_ECOG16;
		case 177:
			return ElfMachine.EM_CR16;
		case 178:
			return ElfMachine.EM_ETPU;
		case 179:
			return ElfMachine.EM_SLE9X;
		case 180:
			return ElfMachine.EM_L10M;
		case 181:
			return ElfMachine.EM_K10M;
		case 183:
			return ElfMachine.EM_AARCH64;
		case 185:
			return ElfMachine.EM_AVR32;
		case 186:
			return ElfMachine.EM_STM8;
		case 187:
			return ElfMachine.EM_TILE64;
		case 188:
			return ElfMachine.EM_TILEPRO;
		case 189:
			return ElfMachine.EM_MICROBLAZE;
		case 190:
			return ElfMachine.EM_CUDA;
		case 191:
			return ElfMachine.EM_TILEGX;
		case 192:
			return ElfMachine.EM_CLOUDSHIELD;
		case 193:
			return ElfMachine.EM_COREA_1ST;
		case 194:
			return ElfMachine.EM_COREA_2ND;
		case 195:
			return ElfMachine.EM_ARC_COMPACT2;
		case 196:
			return ElfMachine.EM_OPEN8;
		case 197:
			return ElfMachine.EM_RL78;
		case 198:
			return ElfMachine.EM_VIDEOCORE5;
		case 199:
			return ElfMachine.EM_78KOR;
		case 200:
			return ElfMachine.EM_56800EX;
		case 201:
			return ElfMachine.EM_BA1;
		case 202:
			return ElfMachine.EM_BA2;
		case 203:
			return ElfMachine.EM_XCORE;
		case 204:
			return ElfMachine.EM_MCHP_PIC;
		case 210:
			return ElfMachine.EM_KM32;
		case 211:
			return ElfMachine.EM_KMX32;
		case 212:
			return ElfMachine.EM_EMX16;
		case 213:
			return ElfMachine.EM_EMX8;
		case 214:
			return ElfMachine.EM_KVARC;
		case 215:
			return ElfMachine.EM_CDP;
		case 216:
			return ElfMachine.EM_COGE;
		case 217:
			return ElfMachine.EM_COOL;
		case 218:
			return ElfMachine.EM_NORC;
		case 219:
			return ElfMachine.EM_CSR_KALIMBA;
		case 220:
			return ElfMachine.EM_Z80;
		case 221:
			return ElfMachine.EM_VISIUM;
		case 222:
			return ElfMachine.EM_FT32;
		case 223:
			return ElfMachine.EM_MOXIE;
		case 224:
			return ElfMachine.EM_AMDGPU;
		case 243:
			return ElfMachine.EM_RISCV;
		case 247:
			return ElfMachine.EM_BPF;
		default:
			throw new RuntimeException("Cant figure out machine");
		}
	}

	private ElfType parse_e_type(short readShort) {
		switch (readShort) {
		case 0x00:
			return ElfType.ET_NONE;
		case 0x01:
			return ElfType.ET_REL;
		case 0x02:
			return ElfType.ET_EXEC;
		case 0x03:
			return ElfType.ET_DYN;
		case 0x04:
			return ElfType.ET_CORE;
		default:
			throw new RuntimeException("Cant figure out Type");
		}
	}

	private byte parse_e_ident__EI_ABIVERSION(byte readByte) {
		return readByte;
	}

	private byte parse_e_ident__EI_VERSION(byte readByte) {
		return readByte;
	}

	private ElfData parse_e_ident__EI_DATA(byte readByte) {
		switch (readByte) {
		case 0x00:
			return ElfData.ELFDATANONE;
		case 0x01:
			return ElfData.ELFDATA2LSB;
		case 0x02:
			return ElfData.ELFDATA2MSB;
		default:
			throw new RuntimeException("Cant figure out endian");
		}
	}

	private ElfClass parse_e_ident__EI_CLASS(byte readByte) {
		switch (readByte) {
		case 0x00:
			return ElfClass.ELFCLASSNONE;
		case 0x01:
			return ElfClass.ELFCLASS32;
		case 0x02:
			return ElfClass.ELFCLASS64;
		default:
			throw new RuntimeException("Cant figure out format");
		}
	}

	private ElfOsAbi parse_e_ident__EI_OSABI(byte readByte) {

		switch (readByte & 0xFF) {
		case 0:
			return ElfOsAbi.ELFOSABI_SYSV;
		case 1:
			return ElfOsAbi.ELFOSABI_HPUX;
		case 2:
			return ElfOsAbi.ELFOSABI_NETBSD;
		case 3:
			return ElfOsAbi.ELFOSABI_LINUX;
		case 6:
			return ElfOsAbi.ELFOSABI_SOLARIS;
		case 07:
			return ElfOsAbi.ELFOSABI_AIX;
		case 8:
			return ElfOsAbi.ELFOSABI_IRIX;
		case 9:
			return ElfOsAbi.ELFOSABI_FREEBSD;
		case 10:
			return ElfOsAbi.ELFOSABI_TRU64;
		case 11:
			return ElfOsAbi.ELFOSABI_MODESTO;
		case 12:
			return ElfOsAbi.ELFOSABI_OPENBSD;
		case 64:
			return ElfOsAbi.ELFOSABI_ARM_AEABI;
		case 97:
			return ElfOsAbi.ELFOSABI_ARM;
		case 255:
			return ElfOsAbi.ELFOSABI_STANDALONE;
		default:
			throw new RuntimeException("Cant figure out OsAbi");
		}
	}

	public ElfFile parser(InputStream is) throws IOException {
		result = new ElfFile();
		this.is = is;
		try {
			parseHeader();
			return result;
		} finally {
			is.close();
		}
	}

	public ElfFile parseProcSelf() throws IOException {
		return parser(new FileInputStream("/proc/self/exe"));
	}

	private int parse_e_version(int readInt) {
		return readInt;
	}

	private Set<ArmEFlags> parse_e_flags_EM_ARM(int readInt) {
		Set<ArmEFlags> result = EnumSet.noneOf(ArmEFlags.class);
		switch (readInt & 0xff000000) {
		case 0x00000000:
			result.add(ArmEFlags.EF_ARM_EABI_UNKNOWN);
			break;
		case 0x01000000:
			result.add(ArmEFlags.EF_ARM_EABI_VER1);
			break;
		case 0x02000000:
			result.add(ArmEFlags.EF_ARM_EABI_VER2);
			break;
		case 0x03000000:
			result.add(ArmEFlags.EF_ARM_EABI_VER3);
			break;
		case 0x04000000:
			result.add(ArmEFlags.EF_ARM_EABI_VER4);
			break;
		case 0x05000000:
			result.add(ArmEFlags.EF_ARM_EABI_VER5);
			break;
		default:
			throw new RuntimeException("Not implemented e_flags ARM ABI Version");
		}
		if ((readInt & 0x00800000) == 0x00800000) {
			result.add(ArmEFlags.EF_ARM_BE8);
		}
		if ((readInt & 0x00400000) == 0x00400000) {
			result.add(ArmEFlags.EF_ARM_LE8);
		}
		if ((readInt & 0x00000800) == 0x00000800) {
			result.add(ArmEFlags.EF_ARM_MAVERICK_FLOAT);
		}
		if ((readInt & 0x00000400) == 0x00000400) {
			result.add(ArmEFlags.EF_ARM_VFP_FLOAT);
		}
		if ((readInt & 0x00000200) == 0x00000200) {
			result.add(ArmEFlags.EF_ARM_SOFT_FLOAT);
		}
		if ((readInt & 0x00000100) == 0x00000100) {
			result.add(ArmEFlags.EF_ARM_OLD_ABI);
		}
		if ((readInt & 0x00000080) == 0x00000080) {
			result.add(ArmEFlags.EF_ARM_NEW_ABI);
		}
		if ((readInt & 0x00000040) == 0x00000040) {
			result.add(ArmEFlags.EF_ARM_ALIGN8);
		}
		if ((readInt & 0x00000020) == 0x00000020) {
			result.add(ArmEFlags.EF_ARM_PIC);
		}
		if ((readInt & 0x00000010) == 0x00000010) {
			if (result.contains(ArmEFlags.EF_ARM_EABI_UNKNOWN)) {
				result.add(ArmEFlags.EF_ARM_APCS_FLOAT);
			} else {
				result.add(ArmEFlags.EF_ARM_MAPSYMSFIRST);
			}
		}
		if ((readInt & 0x00000008) == 0x00000008) {
			if (result.contains(ArmEFlags.EF_ARM_EABI_UNKNOWN)) {
				result.add(ArmEFlags.EF_ARM_APCS_26);
			} else {
				result.add(ArmEFlags.EF_ARM_DYNSYMSUSESEGIDX);
			}
		}
		if ((readInt & 0x00000004) == 0x00000004) {
			if (result.contains(ArmEFlags.EF_ARM_EABI_UNKNOWN)) {
				result.add(ArmEFlags.EF_ARM_INTERWORK);
			} else {
				result.add(ArmEFlags.EF_ARM_SYMSARESORTED);
			}
		}
		if ((readInt & 0x00000002) == 0x00000002) {
			result.add(ArmEFlags.EF_ARM_HASENTRY);
		}
		if ((readInt & 0x00000001) == 0x00000001) {
			result.add(ArmEFlags.EF_ARM_RELEXEC);
		}
		return result;
	}

	public String getMultiarchTupel(String osName) throws IOException {
		ElfFile elfFile = parseProcSelf();
		return elfFile.header.getMultiarchTupel(osName);
	}
}
