package de.ibapl.nativeutils;

import java.util.Set;

import de.ibapl.nativeutils.arm.ArmEFlags;

public class ElfHeader<EF extends EFlags> {
	public enum ElfClass {
		ELFCLASSNONE, ELFCLASS32, ELFCLASS64;
	}

	public enum ElfData {
		ELFDATANONE, ELFDATA2LSB, ELFDATA2MSB;
	}

	public enum ElfOsAbi {
		ELFOSABI_SYSV, ELFOSABI_HPUX, ELFOSABI_NETBSD, ELFOSABI_LINUX, ELFOSABI_SOLARIS, ELFOSABI_AIX, ELFOSABI_IRIX, ELFOSABI_FREEBSD, ELFOSABI_TRU64, ELFOSABI_MODESTO, ELFOSABI_OPENBSD, ELFOSABI_ARM_AEABI, ELFOSABI_ARM, ELFOSABI_STANDALONE;
	}

	public enum ElfType {
		ET_NONE, ET_REL, ET_DYN, ET_EXEC, ET_CORE;
	}

	public enum ElfMachine {
		EM_NONE, EM_M32, EM_SPARC, EM_386, EM_68K, EM_88K, EM_IAMCU, EM_860, EM_MIPS, EM_S370, EM_MIPS_RS3_LE, EM_PARISC, EM_VPP500, EM_SPARC32PLUS, EM_960, EM_PPC, EM_PPC64, EM_S390, EM_SPU, EM_V800, EM_FR20, EM_RH32, EM_RCE, EM_ARM, EM_FAKE_ALPHA, EM_SH, EM_SPARCV9, EM_TRICORE, EM_ARC, EM_H8_300, EM_H8_300H, EM_H8S, EM_H8_500, EM_IA_64, EM_MIPS_X, EM_COLDFIRE, EM_68HC12, EM_MMA, EM_PCP, EM_NCPU, EM_NDR1, EM_STARCORE, EM_ME16, EM_ST100, EM_TINYJ, EM_X86_64, EM_PDSP, EM_PDP10, EM_PDP11, EM_FX66, EM_ST9PLUS, EM_ST7, EM_68HC16, EM_68HC11, EM_68HC08, EM_68HC05, EM_SVX, EM_ST19, EM_VAX, EM_CRIS, EM_JAVELIN, EM_FIREPATH, EM_ZSP, EM_MMIX, EM_HUANY, EM_PRISM, EM_AVR, EM_FR30, EM_D10V, EM_D30V, EM_V850, EM_M32R, EM_MN10300, EM_MN10200, EM_PJ, EM_OPENRISC, EM_ARC_COMPACT, EM_XTENSA, EM_VIDEOCORE, EM_TMM_GPP, EM_NS32K, EM_TPC, EM_SNP1K, EM_ST200, EM_IP2K, EM_MAX, EM_CR, EM_F2MC16, EM_MSP430, EM_BLACKFIN, EM_SE_C33, EM_SEP, EM_ARCA, EM_UNICORE, EM_EXCESS, EM_DXP, EM_ALTERA_NIOS2, EM_CRX, EM_XGATE, EM_C166, EM_M16C, EM_DSPIC30F, EM_CE, EM_M32C, EM_TSK3000, EM_RS08, EM_SHARC, EM_ECOG2, EM_SCORE7, EM_DSP24, EM_VIDEOCORE3, EM_LATTICEMICO32, EM_SE_C17, EM_TI_C6000, EM_TI_C2000, EM_TI_C5500, EM_TI_ARP32, EM_TI_PRU, EM_MMDSP_PLUS, EM_CYPRESS_M8C, EM_R32C, EM_TRIMEDIA, EM_QDSP6, EM_8051, EM_STXP7X, EM_NDS32, EM_ECOG1X, EM_MAXQ30, EM_XIMO16, EM_MANIK, EM_CRAYNV2, EM_RX, EM_METAG, EM_MCST_ELBRUS, EM_ECOG16, EM_CR16, EM_ETPU, EM_SLE9X, EM_L10M, EM_K10M, EM_AARCH64, EM_AVR32, EM_STM8, EM_TILE64, EM_TILEPRO, EM_MICROBLAZE, EM_CUDA, EM_TILEGX, EM_CLOUDSHIELD, EM_COREA_1ST, EM_COREA_2ND, EM_ARC_COMPACT2, EM_OPEN8, EM_RL78, EM_VIDEOCORE5, EM_78KOR, EM_56800EX, EM_BA1, EM_BA2, EM_XCORE, EM_MCHP_PIC, EM_KM32, EM_KMX32, EM_EMX16, EM_EMX8, EM_KVARC, EM_CDP, EM_COGE, EM_COOL, EM_NORC, EM_CSR_KALIMBA, EM_Z80, EM_VISIUM, EM_FT32, EM_MOXIE, EM_AMDGPU, EM_RISCV, EM_BPF;
	}

	public ElfClass elfClass;
	public ElfData elfData;
	public byte elfVersion;
	public ElfOsAbi elfOsAbi;
	public byte elfAbiVersion;
	public ElfType elfType;
	public ElfMachine machine;
	public int version;
	public Set<EF> e_Flags;

	// e_ehsize

	public ElfClass getElfClass() {
		return elfClass;
	}

	public ElfData getElfData() {
		return elfData;
	}

	public byte getElfVersion() {
		return elfVersion;
	}

	public ElfOsAbi getElfOsAbi() {
		return elfOsAbi;
	}

	public byte getElfAbiVersion() {
		return elfAbiVersion;
	}

	public ElfType getElfType() {
		return elfType;
	}

	public ElfMachine getMachine() {
		return machine;
	}

	public Set<EF> getFlags() {
		return e_Flags;
	}

	/**
	 * https://wiki.debian.org/Multiarch/Tuples
	 */
	public String getMultiarchTupel(String osName) {
		switch (getMachine()) {
		case EM_ARM:
			switch (getElfData()) {
			case ELFDATA2LSB:
				if (e_Flags.contains(ArmEFlags.EF_ARM_VFP_FLOAT)) {
					return "arm-linux-gnueabihf";
				} else if (e_Flags.contains(ArmEFlags.EF_ARM_SOFT_FLOAT)) {
					return "arm-linux-gnueabi";
				}
				break;
			default:
				throw new RuntimeException("Not implemented yet");
			}

		case EM_AARCH64:
			return "aarch64-linux-gnu";
		case EM_X86_64:
			return "x86_64-linux-gnu";
		case EM_386:
			return "i386-linux-gnu";
		case EM_MIPS:
			switch (getElfClass()) {
			case ELFCLASS32:
				switch (getElfData()) {
				case ELFDATA2LSB:
					return "mipsel-linux-gnu";
				case ELFDATA2MSB:
					return "mips-linux-gnu";
				default:
					throw new RuntimeException("Unknown endianess");
				}
			case ELFCLASS64:
				switch (getElfData()) {
				case ELFDATA2LSB:
					return "mips64el-linux-gnuabi64";
				case ELFDATA2MSB:
					return "mips64-linux-gnuabi64";
				default:
					throw new RuntimeException("Unknown endianess");
				}
			default:
				throw new RuntimeException("Should never happen");
			}
		default:
			throw new UnsupportedOperationException("Cant handle Linux architecture: " + getMachine());
		}

	}

}
