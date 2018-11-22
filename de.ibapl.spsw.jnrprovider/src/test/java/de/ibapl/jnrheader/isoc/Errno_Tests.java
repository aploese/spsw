package de.ibapl.jnrheader.isoc;

import de.ibapl.jnrheader.api.isoc.Errno_H;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.api.posix.Fcntl_H;
import de.ibapl.jnrheader.JNRHeaderBase;

class Errno_Tests extends JNRHeaderBase {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testDefines() throws Exception {
		testDefines(Errno_H.class);
	}

	@Test
	void testErrno() throws Exception {
		Errno_H errno_H = JnrHeader.getInstance(Errno_H.class);
		Fcntl_H fcntl_H = JnrHeader.getInstance(Fcntl_H.class);
		
		File f = File.createTempFile("Errno_Tests", ".data");
		f.deleteOnExit();
		
		int fd = fcntl_H.open(f.getAbsolutePath(), fcntl_H.O_CREAT | fcntl_H.O_EXCL);
		assertTrue(fd <0, "Filedescriptor valid!");
		assertEquals(errno_H.EEXIST, errno_H.errno(), "Excpected EEXIST");
	}

}
