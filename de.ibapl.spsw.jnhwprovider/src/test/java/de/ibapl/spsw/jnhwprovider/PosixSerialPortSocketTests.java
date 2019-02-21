/*
 * SPSW - Drivers for the serial port, https://github.com/aploese/spsw/
 * Copyright (C) 2009-2019, Arne Plöse and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
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
 */
package de.ibapl.spsw.jnhwprovider;

import de.ibapl.spsw.jnhwprovider.PosixSerialPortSocket;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;

class PosixSerialPortSocketTests {

	PosixSerialPortSocket posixSerialPortSocket;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		posixSerialPortSocket = new PosixSerialPortSocket("/dev/ttyUSB0");
	}

	@AfterEach
	void tearDown() throws Exception {
		posixSerialPortSocket.close();
	}

	@Test
	void testOpen() throws Exception {
		assertFalse(posixSerialPortSocket.isOpen());
		posixSerialPortSocket.open();
		assertTrue(posixSerialPortSocket.isOpen());
	}

	@Test
	void testClose() throws Exception {
		posixSerialPortSocket.open();
		assertTrue(posixSerialPortSocket.isOpen());
		posixSerialPortSocket.close();
		assertFalse(posixSerialPortSocket.isOpen());
	}

	@Test
	void testParity() throws Exception {
		posixSerialPortSocket.open();
		for (Parity p : Parity.values()) {
			posixSerialPortSocket.setParity(p);
			assertEquals(p, posixSerialPortSocket.getParity());
		}
	}

	@Test
	void testDataBits() throws Exception {
		posixSerialPortSocket.open();
		for (DataBits db : DataBits.values()) {
			if (db == DataBits.DB_6) {
				continue;
			}
			posixSerialPortSocket.setDataBits(db);
			assertEquals(db, posixSerialPortSocket.getDatatBits());
		}
	}

	@Test
	void testSpeed() throws Exception {
		posixSerialPortSocket.open();
		for (Speed s : Speed.values()) {
			if (s == Speed._0_BPS) {
				continue;
			}
			posixSerialPortSocket.setSpeed(s);
			assertEquals(s, posixSerialPortSocket.getSpeed());
		}
	}

	
	@Test
	void testStopBits() throws Exception {
		posixSerialPortSocket.open();
		posixSerialPortSocket.setDataBits(DataBits.DB_8);
		posixSerialPortSocket.setStopBits(StopBits.SB_2);
		assertEquals(StopBits.SB_2, posixSerialPortSocket.getStopBits());
	}

	@Test
	void testFlowControl() throws Exception {
		posixSerialPortSocket.open();
		posixSerialPortSocket.setFlowControl(FlowControl.getFC_NONE());
		assertEquals(FlowControl.getFC_NONE(), posixSerialPortSocket.getFlowControl());
		posixSerialPortSocket.setFlowControl(FlowControl.getFC_RTS_CTS());
		assertEquals(FlowControl.getFC_RTS_CTS(), posixSerialPortSocket.getFlowControl());
		posixSerialPortSocket.setFlowControl(FlowControl.getFC_RTS_CTS_XON_XOFF());
		assertEquals(FlowControl.getFC_RTS_CTS_XON_XOFF(), posixSerialPortSocket.getFlowControl());
	}

	@Test
	void testSendBreak() throws Exception {
		posixSerialPortSocket.open();
		posixSerialPortSocket.sendBreak(1000);
		}
	
	@Test
	void testXON_XOFF() throws Exception {
		posixSerialPortSocket.open();
		final char xoff = posixSerialPortSocket.getXOFFChar();
		final char xon = posixSerialPortSocket.getXONChar();
		posixSerialPortSocket.setXONChar('a');
		assertEquals(xoff, posixSerialPortSocket.getXOFFChar());
		assertEquals('a', posixSerialPortSocket.getXONChar());
		posixSerialPortSocket.setXONChar(xon);
		assertEquals(xoff, posixSerialPortSocket.getXOFFChar());
		assertEquals(xon, posixSerialPortSocket.getXONChar());

		posixSerialPortSocket.setXOFFChar('a');
		assertEquals('a', posixSerialPortSocket.getXOFFChar());
		assertEquals(xon, posixSerialPortSocket.getXONChar());
		posixSerialPortSocket.setXOFFChar(xoff);
		assertEquals(xoff, posixSerialPortSocket.getXOFFChar());
		assertEquals(xon, posixSerialPortSocket.getXONChar());
	}
	
	
	@Test 
	void testPrintTermios() throws IOException {
		posixSerialPortSocket.open();
		System.out.println(posixSerialPortSocket.printNative());
	}


}
