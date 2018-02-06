package de.ibapl.spsw.mock;

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
