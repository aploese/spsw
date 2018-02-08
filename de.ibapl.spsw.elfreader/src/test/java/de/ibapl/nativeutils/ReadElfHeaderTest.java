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

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import de.ibapl.nativeutils.arm.ArmEFlags;
import de.ibapl.nativeutils.mips.MipsEFlags;

public class ReadElfHeaderTest {

	@Test
	public void test_arm_linux_gnueabi() throws IOException {
		InputStream is = ReadElfHeaderTest.class.getResourceAsStream("arm-linux-gnueabi/libspsw-1.1.0.so");
		ElfFile elfFile = new ElfFileParser().parser(is);
		assertEquals(ElfHeader.ElfClass.ELFCLASS32, elfFile.header.elfClass);
		assertEquals(ElfHeader.ElfData.ELFDATA2LSB, elfFile.header.elfData);

		assertEquals(1, elfFile.header.elfVersion);
		assertEquals(ElfHeader.ElfOsAbi.ELFOSABI_SYSV, elfFile.header.elfOsAbi);
		assertEquals(1, elfFile.header.version);
		assertEquals(ElfHeader.ElfType.ET_DYN, elfFile.header.elfType);
		assertEquals(ElfHeader.ElfMachine.EM_ARM, elfFile.header.machine);
		assertEquals(2, elfFile.header.e_Flags.size());
		assertTrue(elfFile.header.e_Flags.contains(ArmEFlags.EF_ARM_EABI_VER5));
		assertTrue(elfFile.header.e_Flags.contains(ArmEFlags.EF_ARM_SOFT_FLOAT));
		assertEquals("arm-linux-gnueabi", elfFile.header.getMultiarchTupel("linux"));
	}

	@Test
	public void test_arm_linux_gnueabihf() throws IOException {
		InputStream is = ReadElfHeaderTest.class.getResourceAsStream("arm-linux-gnueabihf/libspsw-1.1.0.so");
		ElfFile elfFile = new ElfFileParser().parser(is);
		assertEquals(ElfHeader.ElfClass.ELFCLASS32, elfFile.header.elfClass);
		assertEquals(ElfHeader.ElfData.ELFDATA2LSB, elfFile.header.elfData);
		assertEquals(1, elfFile.header.elfVersion);
		assertEquals(ElfHeader.ElfOsAbi.ELFOSABI_SYSV, elfFile.header.elfOsAbi);
		assertEquals(1, elfFile.header.version);
		assertEquals(ElfHeader.ElfType.ET_DYN, elfFile.header.elfType);
		assertEquals(ElfHeader.ElfMachine.EM_ARM, elfFile.header.machine);
		assertEquals(2, elfFile.header.e_Flags.size());
		assertTrue(elfFile.header.e_Flags.contains(ArmEFlags.EF_ARM_EABI_VER5));
		assertTrue(elfFile.header.e_Flags.contains(ArmEFlags.EF_ARM_VFP_FLOAT));
		assertEquals("arm-linux-gnueabihf", elfFile.header.getMultiarchTupel("linux"));
	}

	@Test
	public void test_aarch64_linux_gnu() throws IOException {
		InputStream is = ReadElfHeaderTest.class.getResourceAsStream("aarch64-linux-gnu/libspsw-1.1.0.so");
		ElfFile elfFile = new ElfFileParser().parser(is);
		assertEquals(ElfHeader.ElfClass.ELFCLASS64, elfFile.header.elfClass);
		assertEquals(ElfHeader.ElfData.ELFDATA2LSB, elfFile.header.elfData);
		assertEquals(1, elfFile.header.elfVersion);
		assertEquals(ElfHeader.ElfOsAbi.ELFOSABI_SYSV, elfFile.header.elfOsAbi);
		assertEquals(1, elfFile.header.version);
		assertEquals(ElfHeader.ElfType.ET_DYN, elfFile.header.elfType);
		assertEquals(ElfHeader.ElfMachine.EM_AARCH64, elfFile.header.machine);
		assertEquals(0, elfFile.header.e_Flags.size());
		assertEquals("aarch64-linux-gnu", elfFile.header.getMultiarchTupel("linux"));
	}

	@Test
	public void test_mips_linux_gnu() throws IOException {
		InputStream is = ReadElfHeaderTest.class.getResourceAsStream("mips-linux-gnu/libspsw-1.1.0.so");
		ElfFile elfFile = new ElfFileParser().parser(is);
		assertEquals(ElfHeader.ElfClass.ELFCLASS32, elfFile.header.elfClass);
		assertEquals(ElfHeader.ElfData.ELFDATA2MSB, elfFile.header.elfData);
		assertEquals(1, elfFile.header.elfVersion);
		assertEquals(ElfHeader.ElfOsAbi.ELFOSABI_SYSV, elfFile.header.elfOsAbi);
		assertEquals(1, elfFile.header.version);
		assertEquals(ElfHeader.ElfType.ET_DYN, elfFile.header.elfType);
		assertEquals(ElfHeader.ElfMachine.EM_MIPS, elfFile.header.machine);
		assertEquals(5, elfFile.header.e_Flags.size()); // 0x70001007, noreorder, pic, cpic, o32, mips32r2
		assertTrue(elfFile.header.e_Flags.contains(MipsEFlags.EF_MIPS_NOREORDER));
		assertTrue(elfFile.header.e_Flags.contains(MipsEFlags.EF_MIPS_PIC));
		assertTrue(elfFile.header.e_Flags.contains(MipsEFlags.EF_MIPS_CPIC));
		assertTrue(elfFile.header.e_Flags.contains(MipsEFlags.EF_MIPS_ABI_O32));
		assertTrue(elfFile.header.e_Flags.contains(MipsEFlags.EF_MIPS_ARCH_32R2));
		assertEquals("mips-linux-gnu", elfFile.header.getMultiarchTupel("linux"));
	}

	@Test
	public void test_mipsel_linux_gnu() throws IOException {
		InputStream is = ReadElfHeaderTest.class.getResourceAsStream("mipsel-linux-gnu/libspsw-1.1.0.so");
		ElfFile elfFile = new ElfFileParser().parser(is);
		assertEquals(ElfHeader.ElfClass.ELFCLASS32, elfFile.header.elfClass);
		assertEquals(ElfHeader.ElfData.ELFDATA2LSB, elfFile.header.elfData);
		assertEquals(1, elfFile.header.elfVersion);
		assertEquals(ElfHeader.ElfOsAbi.ELFOSABI_SYSV, elfFile.header.elfOsAbi);
		assertEquals(1, elfFile.header.version);
		assertEquals(ElfHeader.ElfType.ET_DYN, elfFile.header.elfType);
		assertEquals(ElfHeader.ElfMachine.EM_MIPS, elfFile.header.machine);
		assertEquals(5, elfFile.header.e_Flags.size()); // 0x70001007, noreorder, pic, cpic, o32, mips32r2
		assertTrue(elfFile.header.e_Flags.contains(MipsEFlags.EF_MIPS_NOREORDER));
		assertTrue(elfFile.header.e_Flags.contains(MipsEFlags.EF_MIPS_PIC));
		assertTrue(elfFile.header.e_Flags.contains(MipsEFlags.EF_MIPS_CPIC));
		assertTrue(elfFile.header.e_Flags.contains(MipsEFlags.EF_MIPS_ABI_O32));
		assertTrue(elfFile.header.e_Flags.contains(MipsEFlags.EF_MIPS_ARCH_32R2));
		assertEquals("mipsel-linux-gnu", elfFile.header.getMultiarchTupel("linux"));
	}

	@Test
	public void test_mips64_linux_gnuabi64() throws IOException {
		InputStream is = ReadElfHeaderTest.class.getResourceAsStream("mips64-linux-gnuabi64/libspsw-1.1.0.so");
		ElfFile elfFile = new ElfFileParser().parser(is);
		assertEquals(ElfHeader.ElfClass.ELFCLASS64, elfFile.header.elfClass);
		assertEquals(ElfHeader.ElfData.ELFDATA2MSB, elfFile.header.elfData);
		assertEquals(1, elfFile.header.elfVersion);
		assertEquals(ElfHeader.ElfOsAbi.ELFOSABI_SYSV, elfFile.header.elfOsAbi);
		assertEquals(1, elfFile.header.version);
		assertEquals(ElfHeader.ElfType.ET_DYN, elfFile.header.elfType);
		assertEquals(ElfHeader.ElfMachine.EM_MIPS, elfFile.header.machine);
		assertEquals(4, elfFile.header.e_Flags.size()); // 0x80000007, noreorder, pic, cpic, mips64r2
		assertTrue(elfFile.header.e_Flags.contains(MipsEFlags.EF_MIPS_NOREORDER));
		assertTrue(elfFile.header.e_Flags.contains(MipsEFlags.EF_MIPS_PIC));
		assertTrue(elfFile.header.e_Flags.contains(MipsEFlags.EF_MIPS_CPIC));
		assertTrue(elfFile.header.e_Flags.contains(MipsEFlags.EF_MIPS_ARCH_64R2));
		assertEquals("mips64-linux-gnuabi64", elfFile.header.getMultiarchTupel("linux"));
	}

	@Test
	public void test_mips64el_linux_gnuabi64() throws IOException {
		InputStream is = ReadElfHeaderTest.class.getResourceAsStream("mips64el-linux-gnuabi64/libspsw-1.1.0.so");
		ElfFile elfFile = new ElfFileParser().parser(is);
		assertEquals(ElfHeader.ElfClass.ELFCLASS64, elfFile.header.elfClass);
		assertEquals(ElfHeader.ElfData.ELFDATA2LSB, elfFile.header.elfData);
		assertEquals(1, elfFile.header.elfVersion);
		assertEquals(ElfHeader.ElfOsAbi.ELFOSABI_SYSV, elfFile.header.elfOsAbi);
		assertEquals(1, elfFile.header.version);
		assertEquals(ElfHeader.ElfType.ET_DYN, elfFile.header.elfType);
		assertEquals(ElfHeader.ElfMachine.EM_MIPS, elfFile.header.machine);
		assertEquals(4, elfFile.header.e_Flags.size()); // 0x80000007, noreorder, pic, cpic, mips64r2
		assertTrue(elfFile.header.e_Flags.contains(MipsEFlags.EF_MIPS_NOREORDER));
		assertTrue(elfFile.header.e_Flags.contains(MipsEFlags.EF_MIPS_PIC));
		assertTrue(elfFile.header.e_Flags.contains(MipsEFlags.EF_MIPS_CPIC));
		assertTrue(elfFile.header.e_Flags.contains(MipsEFlags.EF_MIPS_ARCH_64R2));
		assertEquals("mips64el-linux-gnuabi64", elfFile.header.getMultiarchTupel("linux"));
	}

	@Test
	public void test_i386_linux_gnu() throws IOException {
		InputStream is = ReadElfHeaderTest.class.getResourceAsStream("i386-linux-gnu/libspsw-1.1.0.so");
		ElfFile elfFile = new ElfFileParser().parser(is);
		assertEquals(ElfHeader.ElfClass.ELFCLASS32, elfFile.header.elfClass);
		assertEquals(ElfHeader.ElfData.ELFDATA2LSB, elfFile.header.elfData);
		assertEquals(1, elfFile.header.elfVersion);
		assertEquals(ElfHeader.ElfOsAbi.ELFOSABI_SYSV, elfFile.header.elfOsAbi);
		assertEquals(1, elfFile.header.version);
		assertEquals(ElfHeader.ElfType.ET_DYN, elfFile.header.elfType);
		assertEquals(ElfHeader.ElfMachine.EM_386, elfFile.header.machine);
		assertEquals(0, elfFile.header.e_Flags.size());
		assertEquals("i386-linux-gnu", elfFile.header.getMultiarchTupel("linux"));
	}

	@Test
	public void test_x86_64_linux_gnu() throws IOException {
		InputStream is = ReadElfHeaderTest.class.getResourceAsStream("x86_64-linux-gnu/libspsw-1.1.0.so");
		ElfFile elfFile = new ElfFileParser().parser(is);
		assertEquals(ElfHeader.ElfClass.ELFCLASS64, elfFile.header.elfClass);
		assertEquals(ElfHeader.ElfData.ELFDATA2LSB, elfFile.header.elfData);
		assertEquals(1, elfFile.header.elfVersion);
		assertEquals(ElfHeader.ElfOsAbi.ELFOSABI_SYSV, elfFile.header.elfOsAbi);
		assertEquals(1, elfFile.header.version);
		assertEquals(ElfHeader.ElfType.ET_DYN, elfFile.header.elfType);
		assertEquals(ElfHeader.ElfMachine.EM_X86_64, elfFile.header.machine);
		assertEquals(0, elfFile.header.e_Flags.size());
		assertEquals("x86_64-linux-gnu", elfFile.header.getMultiarchTupel("linux"));
	}

}
