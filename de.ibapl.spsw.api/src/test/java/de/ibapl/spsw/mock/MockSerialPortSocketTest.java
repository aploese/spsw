

/*
 * #%L
 * mbus4j-core
 * %%
 * Copyright (C) 2009 - 2014 MBus4J
 * %%
 * mbus4j - Drivers for the M-Bus protocol - http://mbus4j.sourceforge.net/
 * Copyright (C) 2009-2014, mbus4j.sf.net, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
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
 * #L%
 */
package de.ibapl.spsw.mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ibapl.spsw.api.TimeoutIOException;
import de.ibapl.spsw.mock.MockSerialPortSocket.UnexpectedRequestError;

/**
 *
 * @author arnep@users.sourceforge.net
 * @version $Id$
 */
public class MockSerialPortSocketTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    private MockSerialPortSocket slaves;

    public MockSerialPortSocketTest() {
    }

    @Test(expected=UnexpectedRequestError.class)
    public void respondToRequest_1() throws IOException {
   		slaves.getOutputStream().write(0x01);
    }

    @Test(expected=UnexpectedRequestError.class)
    public void respondToRequest_2() throws IOException {
        slaves.getInputStream().read();
    }

    @Test
    public void respondToRequest_Timeout() throws IOException {
        slaves.expectedRead("01");
        slaves.expectedRead(new TimeoutIOException("Test"));
        slaves.getInputStream().read();
        try {
        	slaves.getInputStream().read();
        	fail();
        } catch (TimeoutIOException e) {
        	assertTrue(true);
        }
    }

    @Test
    public void respondToReques_3() throws Exception {
        slaves.expectedWrite("0102");
        slaves.expectedRead("0201");
        slaves.expectedWrite("0304");
        slaves.expectedRead("0403");

        slaves.getOutputStream().write(0x01);
        slaves.getOutputStream().write(0x02);
        assertEquals(0x02, slaves.getInputStream().read());
        assertEquals(0x01, slaves.getInputStream().read());
        try {
        	slaves.getInputStream().read();
        	fail();
        } catch (UnexpectedRequestError  e) {
        	assertTrue(true);
		}
    }

    @Test
    public void test_read_array() throws Exception {
        slaves.expectedRead("0102");
        byte[] b = new byte[16];
        
        int count = slaves.getInputStream().read(b);
        assertEquals(2, count);
        assertEquals(0x01, b[0]);
        assertEquals(0x02, b[1]);
    }

    @Test
    public void respondToReques_4() throws Exception {
        slaves.expectedWrite("01");
        assertFalse(slaves.allRequestsHandled());
    }

    @Test
    public void respondToReques_5() throws Exception {
        slaves.addRequest("0102", "0201");
        assertEquals(0, slaves.getInputStream().available());

        slaves.getOutputStream().write(0x01);
        assertEquals(0, slaves.getInputStream().available());
        slaves.getOutputStream().write(0x02);
        assertEquals(2, slaves.getInputStream().available());
        assertEquals(0x02, slaves.getInputStream().read());
        assertEquals(1, slaves.getInputStream().available());
        assertEquals(0x01, slaves.getInputStream().read());
        assertEquals(0, slaves.getInputStream().available());
    }

    @Test
    public void respondToRequest() throws Exception {
        slaves.expectedWrite("0102");
        slaves.expectedRead("0201");
        slaves.expectedWrite("0304");
        slaves.expectedRead("0403");
        slaves.expectedWrite("0506");
        slaves.expectedWrite("0708");
        slaves.expectedRead("0807");

        slaves.getOutputStream().write(0x01);
        slaves.getOutputStream().write(0x02);
        assertEquals(0x02, slaves.getInputStream().read());
        assertEquals(0x01, slaves.getInputStream().read());
        
        slaves.getOutputStream().write(0x03);
        slaves.getOutputStream().write(0x04);
        assertEquals(0x04, slaves.getInputStream().read());
        assertEquals(0x03, slaves.getInputStream().read());
        
        slaves.getOutputStream().write(0x05);
        slaves.getOutputStream().write(0x06);
        
        slaves.getOutputStream().write(0x07);
        slaves.getOutputStream().write(0x08);
        assertEquals(0x08, slaves.getInputStream().read());
        assertEquals(0x07, slaves.getInputStream().read());
        
        assertTrue(slaves.allRequestsHandled());
    }

    @Before
    public void setUp() throws Exception {
        slaves = new MockSerialPortSocket();
        slaves.openRaw();
    }

    @After
    public void tearDown() throws Exception {
        slaves.close();
        slaves = null;
    }
}
