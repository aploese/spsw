package de.ibapl.spsw.tests;

/*-
 * #%L
 * SPSW Provider
 * %%
 * Copyright (C) 2009 - 2017 Arne Plöse
 * %%
 * SPSW - Drivers for the serial port, https://github.com/aploese/spsw/
 * Copyright (C) 2009, 2017, Arne Plöse and individual contributors as indicated
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.provider.GenericTermiosSerialPortSocket;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortException;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.provider.SerialPortSocketFactoryImpl;
import java.io.InterruptedIOException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class OnePortTest {

    private static final Logger LOG = Logger.getLogger("SerialTests");

    private static String serialPortName;
    private SerialPortSocket spc;

    @BeforeClass
    public static void setUpClass() throws Exception {
        try (InputStream is = OnePortTest.class.getClassLoader().getResourceAsStream("junit-spsw-config.properties")) {
            if (is == null) {
                serialPortName = null;
            } else {
                Properties p = new Properties();
                p.load(is);
                serialPortName = p.getProperty("port0");
            }
        }
    }

    @Before
    public void setUp() throws Exception {
        if (serialPortName != null) {
            spc = SerialPortSocketFactoryImpl.singleton().createSerialPortSocket(serialPortName);
        } else {
            spc = null;
        }
    }

    @After
    public void tearDown() throws Exception {
        if (spc != null) {
            if (spc.isOpen()) {
                spc.close();
            }
        }
        spc = null;
        //On windows the COM ports needs time to properly close...
        Thread.sleep(100);
    }

    @Test
    public void testOpenClose() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testOpenClose");

        spc.openAsIs();
        Assert.assertTrue(spc.isOpen());
        spc.close();
        Assert.assertTrue(spc.isClosed());
    }

    @Test
    public void testTimeoutNoDataReceived() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testTimeoutNoDataReceived");

        spc.openAsIs();
        spc.setTimeout(2000);
        Assert.assertTrue(spc.isOpen());
        final long start = System.currentTimeMillis();
        try {
            int i = spc.getInputStream().read();
            Assert.fail("No timeout Exception result of Read: " + i);
        } catch (InterruptedIOException iioe) {
            final long time = System.currentTimeMillis() - start;
            LOG.log(Level.INFO, "Timeout: 2000ms and it took: " + time + "ms");
            Assert.assertEquals(2000.0, time, 100.0);  // We tolerate 5% difference
        }
        spc.close();
        Assert.assertTrue(spc.isClosed());
    }

    @Test(expected = SerialPortException.class)
    public void testOpenTempDir() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testOpenTempDir");

        File tmpFile = File.createTempFile("serial", "native");
        tmpFile.deleteOnExit();
        SerialPortSocket sp = SerialPortSocketFactoryImpl.singleton().createSerialPortSocket(tmpFile.getAbsolutePath());
        sp.openAsIs();
        Assert.fail("No serial port opend");
    }

    private void testFlowControl(Set<FlowControl> expected) throws Exception {
        spc.setFlowControl(expected);
        Set<FlowControl> result = spc.getFlowControl();
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testFlowControl() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testFlowControl");

        spc.openAsIs();

        testFlowControl(FlowControl.getFC_NONE());
        testFlowControl(FlowControl.getFC_RTS_CTS());
        testFlowControl(FlowControl.getFC_XON_XOFF_IN());
        testFlowControl(FlowControl.getFC__XON_XOFF_OUT());
        testFlowControl(FlowControl.getFC_XON_XOFF());
        testFlowControl(FlowControl.getFC_RTS_CTS_XON_XOFF());

        spc.close();
        Assert.assertTrue(spc.isClosed());
}

    @Test
    public void testRTS() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testRTS");

        spc.openAsIs();
        
        spc.setRTS(true);
        if (spc instanceof GenericTermiosSerialPortSocket) {
            Assert.assertTrue(((GenericTermiosSerialPortSocket) spc).isRTS());
        }
        spc.setRTS(false);
        if (spc instanceof GenericTermiosSerialPortSocket) {
            Assert.assertFalse(((GenericTermiosSerialPortSocket) spc).isRTS());
        }

        spc.close();
        Assert.assertTrue(spc.isClosed());
    }

    @Test
    public void testDTR() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testDTR");

        spc.openAsIs();

        spc.setDTR(true);
        if (spc instanceof GenericTermiosSerialPortSocket) {
            Assert.assertTrue(((GenericTermiosSerialPortSocket) spc).isDTR());
        }
        spc.setDTR(false);
        if (spc instanceof GenericTermiosSerialPortSocket) {
            Assert.assertFalse(((GenericTermiosSerialPortSocket) spc).isDTR());
        }

        spc.close();
        Assert.assertTrue(spc.isClosed());
    }

    @Test
    public void testBreak() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testBreak");

        spc.openAsIs();

        spc.setBreak(true);
        spc.setBreak(false);

        spc.close();
        Assert.assertTrue(spc.isClosed());
    }

    @Test
    public void testIncommingRI() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testIncommingRI");

        spc.openAsIs();

        spc.isIncommingRI();

        spc.close();
        Assert.assertTrue(spc.isClosed());
    }

    @Test
    public void testCTS() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testDataBits");

        spc.openAsIs();

        spc.isCTS();

        spc.close();
        Assert.assertTrue(spc.isClosed());
    }

    @Test
    public void testDSR() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testDSR");

        spc.openAsIs();

        spc.isDSR();

        spc.close();
        Assert.assertTrue(spc.isClosed());
    }

    @Test
    public void testXONChar() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testXONChar");

        spc.openAsIs();

        LOG.log(Level.INFO, "port: {0}", spc);
        char c = spc.getXONChar();
        spc.setXONChar('a');
        Assert.assertEquals('a', spc.getXONChar());
        spc.setXONChar('z');
        Assert.assertEquals('z', spc.getXONChar());
        spc.setXONChar(c);

        spc.close();
        Assert.assertTrue(spc.isClosed());
    }

    @Test
    public void testXOFFChar() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testXOFFChar");

        spc.openAsIs();

        char c = spc.getXOFFChar();
        spc.setXOFFChar('a');
        Assert.assertEquals('a', spc.getXOFFChar());
        spc.setXOFFChar('z');
        Assert.assertEquals('z', spc.getXOFFChar());
        spc.setXOFFChar(c);

        spc.close();
        Assert.assertTrue(spc.isClosed());
    }

    @Test
    public void testParity() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testParity");

        spc.openAsIs();

        for (Parity p : Parity.values()) {
            spc.setParity(p);
            Assert.assertEquals(p, spc.getParity());
        }

        spc.close();
        Assert.assertTrue(spc.isClosed());
    }

    @Test
    public void testStopBits() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testStopBits");

        spc.openAsIs();

        spc.setStopBits(StopBits.SB_1);
        Assert.assertEquals(StopBits.SB_1, spc.getStopBits());

        try {
            spc.setStopBits(StopBits.SB_1_5);
            Assert.assertEquals(StopBits.SB_1_5, spc.getStopBits());
        } catch (IllegalArgumentException ex) {
        }

        spc.setStopBits(StopBits.SB_2);
        Assert.assertEquals(StopBits.SB_2, spc.getStopBits());

        spc.close();
        Assert.assertTrue(spc.isClosed());
    }

    @Test
    public void testWinFun() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testWinFun");

        spc.openRaw(Baudrate.B9600, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_NONE());
        spc.setDataBits(DataBits.DB_5);
        Assert.assertEquals(DataBits.DB_5, spc.getDatatBits());
        spc.setDataBits(DataBits.DB_8);
        Assert.assertEquals(DataBits.DB_8, spc.getDatatBits());
        spc.setStopBits(StopBits.SB_2); //TODO SB_2 setting 5 databits will break this on win ??????
        Assert.assertEquals(StopBits.SB_2, spc.getStopBits());
        spc.setDataBits(DataBits.DB_5);
        Assert.assertEquals(DataBits.DB_5, spc.getDatatBits());
        spc.setDataBits(DataBits.DB_8);
        Assert.assertEquals(DataBits.DB_8, spc.getDatatBits());

        spc.close();
        Assert.assertTrue(spc.isClosed());
    }

    @Test
    public void testDataBits() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testDataBits");

        spc.openAsIs();

        spc.setStopBits(StopBits.SB_1); //TODO SB_2 will break this on win ??????
        for (DataBits db : DataBits.values()) {
            spc.setDataBits(db);
            Assert.assertEquals(db.toString() + "Failed", db, spc.getDatatBits());
        }

        spc.close();
        Assert.assertTrue(spc.isClosed());
    }

    @Test
    public void testBaudrate() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testBaudrate");

        spc.openAsIs();

        for (Baudrate b : Baudrate.values()) {
            spc.setBaudrate(b);
/*            try {
                spc.setBaudrate(b);
            } catch (SerialPortException ex) {
                if ("Set baudrate not supported".equals(ex.getMessage())) {
                    System.err.println(ex.getMessage() + b.value);
                }
            }
            try {
                System.err.println("BR: " + b.value + "  OT: " + spc.getBaudrate());
            } catch (SerialPortException ex) {
                if ("Get baudrate not supported".equals(ex.getMessage())) {
                    System.err.println(ex.getMessage() + b.value);
                }
            }*/
            Assert.assertEquals("testBaudrate", b, spc.getBaudrate());
        }

        spc.close();
        Assert.assertTrue(spc.isClosed());
    }

    @Test(expected = SerialPortException.class)
    public void testOpen2() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testOpen2");

        spc.openAsIs();
        try (SerialPortSocket spc1 = SerialPortSocketFactoryImpl.singleton().createSerialPortSocket(serialPortName)) {
            spc1.openAsIs();
        }
        Assert.fail();
    }

    @Test
    public void testWrite() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testWrite");

        spc.openRaw();
        
        Assert.assertEquals(0, spc.getOutBufferBytesCount());

        spc.setBaudrate(Baudrate.B9600);
        spc.setParity(Parity.NONE);
        spc.setDataBits(DataBits.DB_8);
        spc.setStopBits(StopBits.SB_1);
        spc.setFlowControl(FlowControl.getFC_NONE());

        spc.getOutputStream().write('a');
        spc.getOutputStream().write('A');
        spc.getOutputStream().write('1');
        spc.getOutputStream().write(1);

        spc.close();
        Assert.assertTrue(spc.isClosed());
    }

    @Test
    public void testWriteBytes() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testWriteBytes");

        spc.openRaw();

        Assert.assertEquals(0, spc.getOutBufferBytesCount());

        spc.setBaudrate(Baudrate.B19200);
        spc.setParity(Parity.NONE);
        spc.setDataBits(DataBits.DB_8);
        spc.setStopBits(StopBits.SB_1);
        spc.setFlowControl(FlowControl.getFC_NONE());
        spc.getOutputStream().write("abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ\n\r".getBytes());

        spc.close();
        Assert.assertTrue(spc.isClosed());
    }

    class TestRead implements Runnable {

        boolean done = false;
        final Object lock = new Object();

        @Override
        public void run() {
            while (true) {
                try {
                    LOG.info("Start Read");
                    int data = spc.getInputStream().read();
                    LOG.info("Read done: " + data);
                    if (data == -1) {
                        LOG.info("Will notify");
                        synchronized (lock) {
                            done = true;
                            lock.notifyAll();
                            return;
                        }
                    } else {
                        LOG.info(String.format("DATA: %x", data));
                    }
                } catch (IOException e) {

                }
            }
        }
    }

    @Test
    public void testCloseIn() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testCloseIn");

        for (int loopIndex = 0; loopIndex < 1; loopIndex++) {
            spc.openRaw(Baudrate.B2400, DataBits.DB_8, StopBits.SB_1, Parity.EVEN, FlowControl.getFC_NONE());
            printPort(spc);
            final TestRead tr = new TestRead();
            Thread t = new Thread(tr);
            t.setDaemon(false);
            LOG.info("Start Thread");
            t.start();
            LOG.info("Thread started");

            spc.getOutputStream().write(104);
            spc.getOutputStream().write(11);
            spc.getOutputStream().write(11);
            spc.getOutputStream().write(104);
            spc.getOutputStream().write(83);
            spc.getOutputStream().write(-3);
            spc.getOutputStream().write(82);
            spc.getOutputStream().write(-1);
            spc.getOutputStream().write(-1);
            spc.getOutputStream().write(-1);
            spc.getOutputStream().write(-1);
            spc.getOutputStream().write(-1);
            spc.getOutputStream().write(-1);
            spc.getOutputStream().write(-1);
            spc.getOutputStream().write(-1);
            spc.getOutputStream().write(-102);
            spc.getOutputStream().write(22);

            LOG.info("Close Port");
            Thread.sleep(100);
            spc.close();
            Assert.assertTrue(spc.isClosed());
            LOG.info("Port closed");
            t.interrupt();
            synchronized (tr.lock) {
                if (!tr.done) {
                    LOG.info("Will Wait");
                    tr.lock.wait(5000); // Allow 5s to close the port and unlock the rad/write Threads.
                    if (!tr.done) {
                        Assert.fail("Port not closed in loopIndex = " + loopIndex);
                    }
                }

            }
        }
        LOG.info("OK Finish");
    }

    @Test(expected = NullPointerException.class)
    public void testWriteBytesNPE() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testWriteBytesNPE");

        spc.openRaw();
        Assert.assertEquals(0, spc.getOutBufferBytesCount());
        spc.getOutputStream().write(null);
    }

    @Test
    public void testAllSettings() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testAllSettings");

        spc.openAsIs();
        
        for (Baudrate br : Baudrate.values()) {
            spc.setBaudrate(br); 
            for (DataBits db: DataBits.values()) {
                spc.setDataBits(db); 
                for (Parity p : Parity.values()) {
                    spc.setParity(p); 
                    for (StopBits sb: StopBits.values()) {
                        try {
                            spc.setStopBits(sb);
                        } catch (IllegalArgumentException iae) {
                            if (sb == StopBits.SB_1_5) {
                                continue;
                            }
                        }
                        Assert.assertEquals(br, spc.getBaudrate());
                        Assert.assertEquals(db, spc.getDatatBits());
                        Assert.assertEquals(p, spc.getParity());
                        Assert.assertEquals(sb, spc.getStopBits());
                    }
                }
            }
        }

        spc.close();
        Assert.assertTrue(spc.isClosed());
    }


    private void printPort(SerialPortSocket sPort) throws IOException {
        System.err.println(sPort.toString());
    }
}
