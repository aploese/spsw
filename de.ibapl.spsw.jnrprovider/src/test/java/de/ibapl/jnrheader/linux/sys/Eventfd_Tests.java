package de.ibapl.jnrheader.linux.sys;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.ibapl.jnrheader.api.linux.sys.Eventfd_H;
import de.ibapl.jnrheader.JNRHeaderBase;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

@EnabledOnOs({ OS.LINUX })
class Eventfd_Tests extends JNRHeaderBase {

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
		testDefines(Eventfd_H.class);
	}

}
