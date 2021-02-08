/*
 * SPSW - Drivers for the serial port, https://github.com/aploese/spsw/
 * Copyright (C) 2009-2021, Arne Plöse and individual contributors as indicated
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
package de.ibapl.spsw.mock;

import de.ibapl.spsw.api.SerialPortSocket;
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
import org.junit.jupiter.api.Assertions;

/**
 *
 * @author Arne Plöse
 */
public class MockSerialPortSocketTest {

    private final static String PORT_NAME = "MOCK_PORT";

    private MockSerialPortFactory factory;
    private SerialPortSocket socket;

    @BeforeEach
    public void setUp() throws Exception {
        socket = null;
        factory = new MockSerialPortFactory();
    }

    @AfterEach
    public void tearDown() throws Exception {
        Assertions.assertNotNull(socket);
        socket.close();
        socket = null;
    }

    public MockSerialPortSocketTest() {
    }

    @Test()
    public void respondToRequest_1() throws IOException {
        socket = factory.open(PORT_NAME);
        assertThrows(UnexpectedRequestError.class, () -> {
            socket.getOutputStream().write(0x01);
        });
    }

    @Test()
    public void respondToRequest_2() throws IOException {
        socket = factory.open(PORT_NAME);
        assertThrows(UnexpectedRequestError.class, () -> {
            socket.getInputStream().read();
        });
    }

    @Test
    public void respondToRequest_Timeout() throws IOException {
        factory.expectedRead("01");
        factory.expectedRead(new TimeoutIOException("Test"));
        socket = factory.open(PORT_NAME);

        socket.getInputStream().read();
        assertThrows(TimeoutIOException.class, () -> {
            socket.getInputStream().read();
        });
    }

    @Test
    public void respondToReques_3() throws Exception {
        factory.expectedWrite("0102");
        factory.expectedRead("0201");
        factory.expectedWrite("0304");
        factory.expectedRead("0403");
        socket = factory.open(PORT_NAME);

        socket.getOutputStream().write(0x01);
        socket.getOutputStream().write(0x02);
        assertEquals(0x02, socket.getInputStream().read());
        assertEquals(0x01, socket.getInputStream().read());
        assertThrows(UnexpectedRequestError.class, () -> {
            socket.getInputStream().read();
        });
    }

    @Test
    public void test_read_array() throws Exception {
        factory.expectedRead("0102");
        socket = factory.open(PORT_NAME);

        byte[] b = new byte[16];
        int count = socket.getInputStream().read(b);
        assertEquals(2, count);
        assertEquals(0x01, b[0]);
        assertEquals(0x02, b[1]);
    }

    @Test
    public void respondToReques_4() throws Exception {
        factory.expectedWrite("01");
        socket = factory.open(PORT_NAME);

        assertFalse(factory.allRequestsHandled());
    }

    @Test
    public void respondToReques_5() throws Exception {
        factory.addRequest("0102", "0201");

        socket = factory.open(PORT_NAME);

        assertEquals(0, socket.getInputStream().available());

        socket.getOutputStream().write(0x01);
        assertEquals(0, socket.getInputStream().available());
        socket.getOutputStream().write(0x02);
        assertEquals(2, socket.getInputStream().available());
        assertEquals(0x02, socket.getInputStream().read());
        assertEquals(1, socket.getInputStream().available());
        assertEquals(0x01, socket.getInputStream().read());
        assertEquals(0, socket.getInputStream().available());
    }

    @Test
    public void respondToRequest() throws Exception {
        factory.expectedWrite("0102");
        factory.expectedRead("0201");
        factory.expectedWrite("0304");
        factory.expectedRead("0403");
        factory.expectedWrite("0506");
        factory.expectedWrite("0708");
        factory.expectedRead("0807");

        socket = factory.open(PORT_NAME);

        socket.getOutputStream().write(0x01);
        socket.getOutputStream().write(0x02);
        assertEquals(0x02, socket.getInputStream().read());
        assertEquals(0x01, socket.getInputStream().read());

        socket.getOutputStream().write(0x03);
        socket.getOutputStream().write(0x04);
        assertEquals(0x04, socket.getInputStream().read());
        assertEquals(0x03, socket.getInputStream().read());

        socket.getOutputStream().write(0x05);
        socket.getOutputStream().write(0x06);

        socket.getOutputStream().write(0x07);
        socket.getOutputStream().write(0x08);
        assertEquals(0x08, socket.getInputStream().read());
        assertEquals(0x07, socket.getInputStream().read());

        assertTrue(factory.allRequestsHandled());
    }

}
