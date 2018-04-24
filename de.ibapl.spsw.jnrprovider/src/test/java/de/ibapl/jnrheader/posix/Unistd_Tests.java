package de.ibapl.jnrheader.posix;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.ibapl.jnrheader.JNRHeaderBase;
import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.isoc.Errno_H;

class Unistd_Tests extends JNRHeaderBase {

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
		testDefines(Unistd_H.class);
	}

	
//	@Test
	void testOpen() throws Exception {
		Errno_H errno_H = JnrHeader.getInstance(Errno_H.class);
		Unistd_H unistd_H = JnrHeader.getInstance(Unistd_H.class);
		File f = File.createTempFile("Unist_Test", "data");
		
//		int fd = unistd_H.close(fd)(f.getAbsolutePath(), uni);
		
		int errno = errno_H.errno();
		errno = errno_H.errno();
		assertEquals(0, errno);
	}

}
