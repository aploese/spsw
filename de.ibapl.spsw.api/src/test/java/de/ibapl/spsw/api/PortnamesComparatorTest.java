package de.ibapl.spsw.api;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PortnamesComparatorTest {

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
	void testCompareLinux() {
		PortnamesComparator pnc = new PortnamesComparator();
		assertEquals(pnc.compare("ttyUSB9", "ttyUSB19"), -pnc.compare("ttyUSB19", "ttyUSB9"));
		assertEquals(-10, pnc.compare("ttyUSB9", "ttyUSB19"));
	}

}
