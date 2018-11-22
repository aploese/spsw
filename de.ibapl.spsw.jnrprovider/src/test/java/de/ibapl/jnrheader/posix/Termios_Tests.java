package de.ibapl.jnrheader.posix;

import de.ibapl.jnrheader.api.posix.Termios_H;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.ibapl.jnrheader.JNRHeaderBase;
import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.api.isoc.Errno_H;
import de.ibapl.jnrheader.api.posix.Termios_H.Termios;

class Termios_Tests extends JNRHeaderBase {

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
		testDefines(Termios_H.class);
	}

	@Test
	void testTermios() {
		Errno_H errno_H = JnrHeader.getInstance(Errno_H.class);
		Termios_H termios_H = JnrHeader.getInstance(Termios_H.class);
		Termios termios = termios_H.createTermios();
		int result = termios_H.tcgetattr(0, termios);
		if (result != 0) {
			int errno = errno_H.errno();
			assertEquals(errno_H.ENOTTY, errno);
		}
	}

}
