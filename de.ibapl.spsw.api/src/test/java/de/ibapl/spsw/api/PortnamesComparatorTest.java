package de.ibapl.spsw.api;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.LinkedList;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.ibapl.spsw.api.PortnamesComparator.Iterator;

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

		assertEquals(-2, "".compareTo("0a"));

		assertEquals(-2, pnc.compare("", "a0"));
		assertEquals(-1, pnc.compare("", "0"));
		assertEquals(-2, pnc.compare("", "0a"));
		
		assertEquals(-1, pnc.compare("ttyUSB9", "ttyUSB09"));
		assertEquals("ttyUSB09".compareTo("ttyUSB19"), pnc.compare("ttyUSB9", "ttyUSB19"));
		assertEquals("ttyUSB19".compareTo("ttyUSB09"), pnc.compare("ttyUSB19", "ttyUSB9"));

		assertEquals("ttyUSB09.A".compareTo("ttyUSB29.A"), pnc.compare("ttyUSB9.A", "ttyUSB29.A"));
		assertEquals("ttyUSB29.A".compareTo("ttyUSB09.A"), pnc.compare("ttyUSB29.A", "ttyUSB9.A"));

		assertEquals(1, pnc.compare("ttyUSB09B", "ttyUSB0009A"));
		assertEquals(1, pnc.compare("ttyUSB0009B", "ttyUSB009A"));
		assertEquals(1, pnc.compare("ttyUSB0009B", "ttyUSB9A0"));
		
		assertEquals(2, pnc.compare("ttyUSB000", "ttyUSB0"));
		
		assertEquals(1, pnc.compare("ttyUSB0", "ttyUSB"));
		
		
	}

	@Test
	void testCompareLinux1() {
		PortnamesComparator pnc = new PortnamesComparator();

		assertEquals(-1, pnc.compare("ttyUSB08", "ttyUSB008"));

		assertEquals(-1, pnc.compare("ttyUSB7", "ttyUSB08"));
		assertEquals(-1, pnc.compare("ttyUSB8", "ttyUSB08"));
		assertEquals(1, pnc.compare("ttyUSB9", "ttyUSB08"));

		assertEquals(1, pnc.compare("ttyUSB08", "ttyUSB7"));
		assertEquals(1, pnc.compare("ttyUSB08", "ttyUSB8"));
		assertEquals(-1, pnc.compare("ttyUSB08", "ttyUSB9"));

		assertEquals(-1, pnc.compare("ttyUSB07", "ttyUSB008"));
		assertEquals(-1, pnc.compare("ttyUSB08", "ttyUSB008"));
		assertEquals(1, pnc.compare("ttyUSB09", "ttyUSB008"));

		assertEquals(1, pnc.compare("ttyUSB008", "ttyUSB07"));
		assertEquals(1, pnc.compare("ttyUSB008", "ttyUSB08"));
		assertEquals(-1, pnc.compare("ttyUSB008", "ttyUSB09"));
	}
	@Test
	void testCompareLinux2() {
		PortnamesComparator pnc = new PortnamesComparator();
		LinkedList<String> list = new LinkedList<>();
		list.add("USB");
		list.add("USB0");
		list.add("USB1");
		list.add("USB4");
		list.add("USB10");
		list.add("USB12");
		list.add("USB1.A");
		list.add("USB01.A");
		list.add("USB4.A");
		list.add("USB04.B");
		list.add("USB10.A");
		list.add("USB12.A");
		list.add("USB1.B");
		list.add("USB4.B");
		list.add("USB10.B");
		list.add("USB12.B");

		list.sort(pnc);
		
		java.util.Iterator<String> iter = list.iterator();
		assertEquals("USB", iter.next());
		assertEquals("USB0", iter.next());
		assertEquals("USB1", iter.next());
		assertEquals("USB1.A", iter.next());
		assertEquals("USB01.A", iter.next());
		assertEquals("USB1.B", iter.next());
		assertEquals("USB4", iter.next());
		assertEquals("USB4.A", iter.next());
		assertEquals("USB4.B", iter.next());
		assertEquals("USB04.B", iter.next());
		assertEquals("USB10", iter.next());
		assertEquals("USB10.A", iter.next());
		assertEquals("USB10.B", iter.next());
		assertEquals("USB12", iter.next());
		assertEquals("USB12.A", iter.next());
		assertEquals("USB12.B", iter.next());
		assertFalse(iter.hasNext());
		Collections.reverse(list);
		list.sort(pnc);
		
		iter = list.iterator();
		assertEquals("USB", iter.next());
		assertEquals("USB0", iter.next());
		assertEquals("USB1", iter.next());
		assertEquals("USB1.A", iter.next());
		assertEquals("USB01.A", iter.next());
		assertEquals("USB1.B", iter.next());
		assertEquals("USB4", iter.next());
		assertEquals("USB4.A", iter.next());
		assertEquals("USB4.B", iter.next());
		assertEquals("USB04.B", iter.next());
		assertEquals("USB10", iter.next());
		assertEquals("USB10.A", iter.next());
		assertEquals("USB10.B", iter.next());
		assertEquals("USB12", iter.next());
		assertEquals("USB12.A", iter.next());
		assertEquals("USB12.B", iter.next());
		
		/*
		for (String s: list) {
			System.out.println("assertEquals(\"" + s + "\", iter.next());");
		}
		*/
	}

}
