package de.ibapl.jnrheader.posix;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.ibapl.jnrheader.JnrHeader;

class Fcntl_Tests extends JNRHeaderBase {

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
		testDefines(Fcntl_H.class);
	}

	@Test
	void testOpen() throws Exception {
		Errno_H errno_H = JnrHeader.getInstance(Errno_H.class);
		Fcntl_H fcntl_H = JnrHeader.getInstance(Fcntl_H.class);
		Unistd_H unistd_H = JnrHeader.getInstance(Unistd_H.class);
		
		File f = File.createTempFile("Unist_Test", ".data");
		
		int fd = fcntl_H.open(f.getAbsolutePath(), fcntl_H.O_CREAT | fcntl_H.O_EXCL);
		if (fd <0) {
			int errno = errno_H.errno();
			assertEquals(errno_H.EEXIST, errno, "Excpected EEXIST");
		}
		int result = unistd_H.close(fd);
		if (result <0) {
			int errno = errno_H.errno();
			if (errno == errno_H.EALREADY) {
				
			}
		}
	}

}
