/*-
 * #%L
 * SPSW API
 * %%
 * Copyright (C) 2009 - 2018 Arne Plöse
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */
package de.ibapl.spsw.jnrprovider;

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
import de.ibapl.spsw.jnrprovider.PosixSerialPortSocket;

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
