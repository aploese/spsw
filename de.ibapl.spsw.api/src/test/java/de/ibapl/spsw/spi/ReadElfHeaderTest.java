package de.ibapl.spsw.spi;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class ReadElfHeaderTest {

	@Test
	public void test_arm_linux_gnueabi() throws IOException {
		InputStream is = ReadElfHeaderTest.class.getResourceAsStream("arm-linux-gnueabi/libspsw-1.1.0.so");
		ReadElfHeader elfHeader = new ReadElfHeader(is);
		assertEquals(ReadElfHeader.Format._32_BIT,elfHeader.getFormat());
		assertEquals(ReadElfHeader.Endian.LITTLE_ENDIAN,elfHeader.getEndian());
		assertEquals(1,elfHeader.getElfVersion());
		assertEquals(ReadElfHeader.OsAbi.SYSTEM_V,elfHeader.getOsAbi());
		assertEquals(0,elfHeader.getAbiVersion());
		assertEquals(ReadElfHeader.Type.SHARED,elfHeader.getType());
		assertEquals(ReadElfHeader.Isa.ARM,elfHeader.getMachine());
	}

	@Test
	public void test_aarch64_linux_gnu() throws IOException {
		InputStream is = ReadElfHeaderTest.class.getResourceAsStream("aarch64-linux-gnu/libspsw-1.1.0.so");
		ReadElfHeader elfHeader = new ReadElfHeader(is);
		assertEquals(ReadElfHeader.Format._64_BIT,elfHeader.getFormat());
		assertEquals(ReadElfHeader.Endian.LITTLE_ENDIAN,elfHeader.getEndian());
		assertEquals(1,elfHeader.getElfVersion());
		assertEquals(ReadElfHeader.OsAbi.SYSTEM_V,elfHeader.getOsAbi());
		assertEquals(0,elfHeader.getAbiVersion());
		assertEquals(ReadElfHeader.Type.SHARED,elfHeader.getType());
		assertEquals(ReadElfHeader.Isa.A_ARCH_64,elfHeader.getMachine());
	}

	@Test
	public void test_self() throws IOException {
            try {
                ReadElfHeader elfHeader = new ReadElfHeader();
            } catch (FileNotFoundException e) {
                //Maybe were on win???
            }
/*
		assertEquals(ReadElfHeader.Format._64_BIT,elfHeader.getFormat());
		assertEquals(ReadElfHeader.Endian.LITTLE_ENDIAN,elfHeader.getEndian());
*/
	}

}
