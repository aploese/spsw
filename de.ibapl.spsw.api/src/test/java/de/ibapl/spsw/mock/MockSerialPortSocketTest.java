/*
 * #%L
 * mbus4j-core
 * %%
 * Copyright (C) 2009 - 2014 MBus4J
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
package de.ibapl.spsw.mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.ibapl.spsw.api.TimeoutIOException;
import de.ibapl.spsw.mock.MockSerialPortSocket.UnexpectedRequestError;

/**
 *
 * @author Arne Plöse
 */
public class MockSerialPortSocketTest {

	private MockSerialPortSocket mockSerialPortSocket;

	@BeforeEach
	public void setUp() throws Exception {
		mockSerialPortSocket = new MockSerialPortSocket();
	}

	@AfterEach
	public void tearDown() throws Exception {
		mockSerialPortSocket.close();
		mockSerialPortSocket = null;
	}

	public MockSerialPortSocketTest() {
	}

	@Test()
	public void respondToRequest_1() throws IOException {
		mockSerialPortSocket.open();
		assertThrows(UnexpectedRequestError.class, () -> {
			mockSerialPortSocket.getOutputStream().write(0x01);
		});
	}

	@Test()
	public void respondToRequest_2() throws IOException {
		mockSerialPortSocket.open();
		assertThrows(UnexpectedRequestError.class, () -> {
			mockSerialPortSocket.getInputStream().read();
		});
	}

	@Test
	public void respondToRequest_Timeout() throws IOException {
		mockSerialPortSocket.expectedRead("01");
		mockSerialPortSocket.expectedRead(new TimeoutIOException("Test"));

		mockSerialPortSocket.open();

		mockSerialPortSocket.getInputStream().read();
		assertThrows(TimeoutIOException.class, () -> {
			mockSerialPortSocket.getInputStream().read();
		});
	}

	@Test
	public void respondToReques_3() throws Exception {
		mockSerialPortSocket.expectedWrite("0102");
		mockSerialPortSocket.expectedRead("0201");
		mockSerialPortSocket.expectedWrite("0304");
		mockSerialPortSocket.expectedRead("0403");

		mockSerialPortSocket.open();

		mockSerialPortSocket.getOutputStream().write(0x01);
		mockSerialPortSocket.getOutputStream().write(0x02);
		assertEquals(0x02, mockSerialPortSocket.getInputStream().read());
		assertEquals(0x01, mockSerialPortSocket.getInputStream().read());
		assertThrows(UnexpectedRequestError.class, () -> {
			mockSerialPortSocket.getInputStream().read();
		});
	}

	@Test
	public void test_read_array() throws Exception {
		mockSerialPortSocket.expectedRead("0102");

		mockSerialPortSocket.open();

		byte[] b = new byte[16];
		int count = mockSerialPortSocket.getInputStream().read(b);
		assertEquals(2, count);
		assertEquals(0x01, b[0]);
		assertEquals(0x02, b[1]);
	}

	@Test
	public void respondToReques_4() throws Exception {
		mockSerialPortSocket.expectedWrite("01");

		mockSerialPortSocket.open();

		assertFalse(mockSerialPortSocket.allRequestsHandled());
	}

	@Test
	public void respondToReques_5() throws Exception {
		mockSerialPortSocket.addRequest("0102", "0201");

		mockSerialPortSocket.open();

		assertEquals(0, mockSerialPortSocket.getInputStream().available());

		mockSerialPortSocket.getOutputStream().write(0x01);
		assertEquals(0, mockSerialPortSocket.getInputStream().available());
		mockSerialPortSocket.getOutputStream().write(0x02);
		assertEquals(2, mockSerialPortSocket.getInputStream().available());
		assertEquals(0x02, mockSerialPortSocket.getInputStream().read());
		assertEquals(1, mockSerialPortSocket.getInputStream().available());
		assertEquals(0x01, mockSerialPortSocket.getInputStream().read());
		assertEquals(0, mockSerialPortSocket.getInputStream().available());
	}

	@Test
	public void respondToRequest() throws Exception {
		mockSerialPortSocket.expectedWrite("0102");
		mockSerialPortSocket.expectedRead("0201");
		mockSerialPortSocket.expectedWrite("0304");
		mockSerialPortSocket.expectedRead("0403");
		mockSerialPortSocket.expectedWrite("0506");
		mockSerialPortSocket.expectedWrite("0708");
		mockSerialPortSocket.expectedRead("0807");

		mockSerialPortSocket.open();

		mockSerialPortSocket.getOutputStream().write(0x01);
		mockSerialPortSocket.getOutputStream().write(0x02);
		assertEquals(0x02, mockSerialPortSocket.getInputStream().read());
		assertEquals(0x01, mockSerialPortSocket.getInputStream().read());

		mockSerialPortSocket.getOutputStream().write(0x03);
		mockSerialPortSocket.getOutputStream().write(0x04);
		assertEquals(0x04, mockSerialPortSocket.getInputStream().read());
		assertEquals(0x03, mockSerialPortSocket.getInputStream().read());

		mockSerialPortSocket.getOutputStream().write(0x05);
		mockSerialPortSocket.getOutputStream().write(0x06);

		mockSerialPortSocket.getOutputStream().write(0x07);
		mockSerialPortSocket.getOutputStream().write(0x08);
		assertEquals(0x08, mockSerialPortSocket.getInputStream().read());
		assertEquals(0x07, mockSerialPortSocket.getInputStream().read());

		assertTrue(mockSerialPortSocket.allRequestsHandled());
	}

}