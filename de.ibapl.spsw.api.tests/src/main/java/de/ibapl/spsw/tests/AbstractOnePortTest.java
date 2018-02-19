package de.ibapl.spsw.tests;

/*-
 * #%L
 * SPSW Provider
 * %%
 * Copyright (C) 2009 - 2017 Arne Plöse
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
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.PortBusyException;
import de.ibapl.spsw.api.SerialPortException;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.api.TimeoutIOException;
import de.ibapl.spsw.api.SerialPortSocketFactory;
import java.lang.ref.WeakReference;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit test for simple App.
 */
public abstract class AbstractOnePortTest {

    protected static final Logger LOG = Logger.getLogger("SerialTests");

    protected static String serialPortName;
    protected SerialPortSocket spc;

    protected abstract SerialPortSocketFactory getSerialPortSocketFactory();

    @BeforeClass
    public static void setUpClass() throws Exception {
        try (InputStream is = AbstractOnePortTest.class.getClassLoader().getResourceAsStream("junit-spsw-config.properties")) {
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
            spc = getSerialPortSocketFactory().createSerialPortSocket(serialPortName);
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
        Runtime.getRuntime().gc();
        Runtime.getRuntime().runFinalization();
        // On windows the COM ports needs time to properly close...
        // Thread.sleep(100);
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

    @Test(timeout = 5000)
    public void testSetTimeouts() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testSetTimeOuts");

        spc.openAsIs();

        spc.setTimeouts(100, 1000, 2000);
        assertEquals(100, spc.getInterByteReadTimeout());
        assertEquals(1000, spc.getOverallReadTimeout());
        assertEquals(2000, spc.getOverallWriteTimeout());

        spc.setTimeouts(0, 2222, 3333);
        assertEquals(0, spc.getInterByteReadTimeout());
        assertEquals(2222, spc.getOverallReadTimeout());
        assertEquals(3333, spc.getOverallWriteTimeout());

        spc.setTimeouts(0, 2222, 0);
        assertEquals(0, spc.getInterByteReadTimeout());
        assertEquals(2222, spc.getOverallReadTimeout());
        assertEquals(0, spc.getOverallWriteTimeout());

        spc.setTimeouts(0, 0, 3333);
        assertEquals(0, spc.getInterByteReadTimeout());
        assertEquals(0, spc.getOverallReadTimeout());
        assertEquals(3333, spc.getOverallWriteTimeout());

        spc.setTimeouts(100, 0, 3333);
        assertEquals(100, spc.getInterByteReadTimeout());
        assertEquals(0, spc.getOverallReadTimeout());
        assertEquals(3333, spc.getOverallWriteTimeout());

        spc.setTimeouts(100, 0, 0);
        assertEquals(100, spc.getInterByteReadTimeout());
        assertEquals(0, spc.getOverallReadTimeout());
        assertEquals(0, spc.getOverallWriteTimeout());

        spc.close();
        Assert.assertTrue(spc.isClosed());
    }

    @Test(timeout = 5000)
    public void testOverallTimeoutBlocking() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testOverallTimeoutBlocking");

        spc.openAsIs();

        spc.setTimeouts(100, 1000, 2000);
        assertEquals(100, spc.getInterByteReadTimeout());
        assertEquals(1000, spc.getOverallReadTimeout());
        assertEquals(2000, spc.getOverallWriteTimeout());

        long start = System.currentTimeMillis();
        try {
            int i = spc.getInputStream().read();
            Assert.fail("No timeout Exception result of Read: " + i);
        } catch (TimeoutIOException tioe) {
            final long time = System.currentTimeMillis() - start;
            LOG.log(Level.INFO, "Timeout: 1000ms and it took: " + time + "ms");
            Assert.assertEquals(1000.0, time, 100.0); // We tolerate 5% difference
        }

        spc.setTimeouts(0, spc.getOverallReadTimeout(), spc.getOverallWriteTimeout());
        assertEquals(0, spc.getInterByteReadTimeout());
        assertEquals(1000, spc.getOverallReadTimeout());
        assertEquals(2000, spc.getOverallWriteTimeout());

        start = System.currentTimeMillis();
        try {
            int i = spc.getInputStream().read();
            Assert.fail("No timeout Exception result of Read: " + i);
        } catch (TimeoutIOException tioe) {
            final long time = System.currentTimeMillis() - start;
            LOG.log(Level.INFO, "Timeout: 1000ms and it took: " + time + "ms");
            Assert.assertEquals(1000.0, time, 100.0); // We tolerate 5% difference
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
        SerialPortSocket sp = getSerialPortSocketFactory().createSerialPortSocket(tmpFile.getAbsolutePath());
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
//		if (spc instanceof GenericTermiosSerialPortSocket) {
//			Assert.assertTrue(((GenericTermiosSerialPortSocket) spc).isRTS());
//		}
        spc.setRTS(false);
//		if (spc instanceof GenericTermiosSerialPortSocket) {
//			Assert.assertFalse(((GenericTermiosSerialPortSocket) spc).isRTS());
//		}

        spc.close();
        Assert.assertTrue(spc.isClosed());
    }

    @Test
    public void testDTR() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testDTR");

        spc.openAsIs();

        spc.setDTR(true);
//		if (spc instanceof GenericTermiosSerialPortSocket) {
//			Assert.assertTrue(((GenericTermiosSerialPortSocket) spc).isDTR());
//		}
        spc.setDTR(false);
//		if (spc instanceof GenericTermiosSerialPortSocket) {
//			Assert.assertFalse(((GenericTermiosSerialPortSocket) spc).isDTR());
//		}

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
        LOG.log(Level.INFO, "testIncommingRI done");
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
        spc.setStopBits(StopBits.SB_2); // TODO SB_2 setting 5 databits will break this on win ??????
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

        spc.setStopBits(StopBits.SB_1); // TODO SB_2 will break this on win ??????
        for (DataBits db : DataBits.values()) {
        	try {
        		spc.setDataBits(db);
        	} catch (Exception e) {
        		Assert.fail(e.getMessage() + "dataBits: " + db);
        	}
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
            try {
                spc.setBaudrate(b);
                Assert.assertEquals("testBaudrate", b, spc.getBaudrate());
            } catch (IllegalArgumentException iae) {
                // This is Hardware dependent watch for logs ...
                LOG.log(Level.WARNING, "Error setBaudrate " + b, iae);
            }
        }

        spc.close();
        Assert.assertTrue(spc.isClosed());
    }

    @Test(expected = SerialPortException.class)
    public void testOpen2() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testOpen2");

        spc.openAsIs();
        try (SerialPortSocket spc1 = getSerialPortSocketFactory().createSerialPortSocket(serialPortName)) {
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
                        LOG.log(Level.INFO, "Received -1");
                        if (spc.isClosed()) {
                            LOG.log(Level.INFO, "Received -1 and port is closed");
                            synchronized (lock) {
                                done = true;
                                lock.notifyAll();
                            }
                            LOG.log(Level.INFO, "Received -1 and port is closed - so we notified all");
                        } else {
                            LOG.log(Level.SEVERE, "Received -1 but port not closed?");
                        }
                        break;
                    } else {
                        LOG.info(String.format("DATA: %x", data));
                    }
                } catch (TimeoutIOException tioe) {
                    LOG.log(Level.INFO, "Caught Timeout: ", tioe);
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, "Caught Exception: ", e);
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
           // spc.setTimeouts(100,  1000,  1000);
            printPort(spc);
            final TestRead tr = new TestRead();
            Thread t = new Thread(tr);
            t.setDaemon(false);
            LOG.info("Start Thread");
            t.start();
            Thread.sleep(100);
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

            synchronized (tr.lock) {
                if (!tr.done) {
                    LOG.info("Will Wait");
                    tr.lock.wait(5000); // Allow 1s to close the port and unlock the rad/write Threads.
                    if (!tr.done) {
                        Assert.fail("Port not closed in loopIndex = " + loopIndex);
                    }
                }

            }
        }
        LOG.info("OK Finish");

        Assert.assertTrue(spc.isClosed());
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
            try {
                spc.setBaudrate(br);
            } catch (IllegalArgumentException iae) {
                // Some HW supports this, some not ...
                // This is logged in testSetBaudrate so ignore it here.
                continue;
            }
            for (DataBits db : DataBits.values()) {
                spc.setDataBits(db);
                for (Parity p : Parity.values()) {
                    spc.setParity(p);
                    for (StopBits sb : StopBits.values()) {
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

    @Test(timeout = 5000)
    public void testCloseDuringSingleRead() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testCloseDuringSingleRead");

        spc.openRaw(Baudrate.B2400, DataBits.DB_8, StopBits.SB_1, Parity.EVEN, FlowControl.getFC_NONE());
        assertEquals(0, spc.getInBufferBytesCount());
        new Thread(() -> {
            try {
                Thread.sleep(100);
                spc.close();
            } catch (InterruptedException | IOException e) {
                fail("Exception occured");
            }
        }).start();

        assertTrue(spc.isOpen());
        int result = spc.getInputStream().read();
        assertEquals(-1, result);

        Assert.assertTrue(spc.isClosed());
        //TODO commenting this out will make the next Test fail on windowx with  port busy ??? I have no idea why
        //Make sure it can be opend
        //spc.openRaw();
        //spc.close();
        //Assert.assertTrue(spc.isClosed());
        //Or 40ms will also do
        Thread.sleep(40);
    }

    @Test(timeout = 5000)
    public void testCloseDuringBytesRead() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testCloseDuringBytesRead");

        spc.openRaw(Baudrate.B2400, DataBits.DB_8, StopBits.SB_1, Parity.EVEN, FlowControl.getFC_NONE());
        assertEquals(0, spc.getInBufferBytesCount());
        new Thread(() -> {
            try {
                Thread.sleep(100);
                spc.close();
            } catch (InterruptedException | IOException e) {
                fail("Exception occured");
            }
        }).start();

        byte b[] = new byte[255];
        assertTrue(spc.isOpen());
        int result = spc.getInputStream().read(b);
        assertEquals(-1, result);

        Assert.assertTrue(spc.isClosed());
        
        //Make sure it can be opend
        //TODO commenting this out will make the next Test fail on windowx with  port busy ??? I have no idea why
        //spc.openRaw();
        //spc.close();
        //Assert.assertTrue(spc.isClosed());
        //Or 40s will also do
        Thread.sleep(40);
    }

    @Test
    public void testSendBreakBlocking() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testSendBreakBlocking");

        spc.openRaw(Baudrate.B2400, DataBits.DB_8, StopBits.SB_1, Parity.EVEN, FlowControl.getFC_NONE());
        final long start = System.currentTimeMillis();
        spc.sendBreak(500);
        final long end = System.currentTimeMillis();
        assertEquals(500, end - start, 50);

        spc.close();
        Assert.assertTrue(spc.isClosed());
    }

    /**
     * Write byte[1024] blocks with set RTS/CTS so the port will actually block
     * The logs give information about the actual behavior
     * If the port does not support RTS/CTS (like MCS7820 on linux TODO BUG?)it will caught by the timeout.
     * 
     *
     * @throws Exception
     */
    @Test
    public void testWriteBytesTimeout() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testWriteBytesTimeout");

        // Set a high baudrate to speed up things
        spc.openRaw(Baudrate.B1500000, DataBits.DB_8, StopBits.SB_1, Parity.EVEN, FlowControl.getFC_RTS_CTS());
        // make sure out buffer is empty
        assertEquals(0, spc.getOutBufferBytesCount());
        spc.setTimeouts(100, 1000, 1000);
        assertEquals(1000, spc.getOverallWriteTimeout());
        assertEquals(100, spc.getInterByteReadTimeout());
        assertEquals(1000, spc.getOverallReadTimeout());
        
        assertFalse(spc.isCTS());
        
        byte[] data = new byte[1024];
        int round = 1;
        int i = 0;
        int dataWritten;
        int overallDataWritten = 0;
        do {
            dataWritten = 0;
            try {
                for (i = 0; i < 1024; i++) {
                    spc.getOutputStream().write(data);
                }
                fail("RTS/CTS enabled so a timeout is expectd when the buffer is full!");
            } catch (TimeoutIOException e) {
                dataWritten = ((i * data.length) + e.bytesTransferred);
                LOG.log(Level.INFO, "Round: " + round + ": " + dataWritten + " bytes written; OutBuf:  "
                        + spc.getOutBufferBytesCount());
                assertTrue(true);
            }
            try {
                spc.getOutputStream().flush();
                // TODO NOT on winfail();
            } catch (TimeoutIOException e) {
                LOG.log(Level.INFO, "Round: " + round + " Flush; OutBuf:  " + spc.getOutBufferBytesCount());
                assertTrue(true);
            }
            round++;
            overallDataWritten += dataWritten;
            assertTrue("Rounds exceed maximum of " + 100, 100 > round);
        } while (dataWritten > 0);

        LOG.log(Level.INFO,
                "Wrote: " + overallDataWritten + " in " + round + " rounds; OutBuf:  " + spc.getOutBufferBytesCount());
        LOG.log(Level.INFO, "disable flow control to sped up closing");
        spc.setFlowControl(FlowControl.getFC_NONE());
        LOG.log(Level.INFO, "will close port");
        spc.close();
        Assert.assertTrue(spc.isClosed());
        LOG.log(Level.INFO, "port closed");
    }

    /**
     * Write a single byte with set RTS/CTS so the port will actually block The
     * logs give information about the actual behavior
     *
     * @throws Exception
     */
    @Test
    public void testWriteSingleByteTimeout() throws Exception {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testWriteSingleByteTimeout");

        // Set a high baudrate to speed up things
        spc.openRaw(Baudrate.B1500000, DataBits.DB_8, StopBits.SB_1, Parity.EVEN, FlowControl.getFC_RTS_CTS());
        // make sure out buffer is empty
        assertEquals(0, spc.getOutBufferBytesCount());
        spc.setTimeouts(100, 1000, 1000);
        assertEquals(1000, spc.getOverallWriteTimeout());
        assertEquals(100, spc.getInterByteReadTimeout());
        assertEquals(1000, spc.getOverallReadTimeout());
        int round = 1;
        int i = 0;
        int dataWritten;
        int overallDataWritten = 0;
        do {
            dataWritten = 0;
            try {
                for (i = 0; i < 1048576; i++) {
                    spc.getOutputStream().write(0);
                }
                fail();
            } catch (TimeoutIOException e) {
                dataWritten = i + e.bytesTransferred;
                LOG.log(Level.INFO, "Round: " + round + ": " + dataWritten + " bytes written; OutBuf:  "
                        + spc.getOutBufferBytesCount());
                assertTrue(true);
            }
            try {
                spc.getOutputStream().flush();
                // TODO not on win??? fail();
            } catch (TimeoutIOException e) {
                LOG.log(Level.INFO, "Round: " + round + " Flush; OutBuf:  " + spc.getOutBufferBytesCount());
                assertTrue(true);
            }
            round++;
            overallDataWritten += dataWritten;

        } while (dataWritten > 0);

        LOG.log(Level.INFO,
                "Wrote: " + overallDataWritten + " in " + round + " rounds; OutBuf:  " + spc.getOutBufferBytesCount());
        LOG.log(Level.INFO, "disable flow control to sped up closing");
        spc.setFlowControl(FlowControl.getFC_NONE());
        LOG.log(Level.INFO, "will close port");
        spc.close();
        Assert.assertTrue(spc.isClosed());
        LOG.log(Level.INFO, "port closed");
    }

    @Test
    public void testDefaultTimeouts() throws IOException {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "ruf testDefaultTimeouts");

        spc.openRaw();
        assertEquals(100, spc.getInterByteReadTimeout());
        assertEquals(0, spc.getOverallReadTimeout());
        assertEquals(0, spc.getOverallWriteTimeout());
        spc.close();
    }

    @Test
    public void testOpen2Times() throws IOException {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testDefaultTimeouts");

        spc.openRaw();
        SerialPortSocket spc2 = getSerialPortSocketFactory().createSerialPortSocket(serialPortName);
        try {
            spc2.openRaw();
            fail("Port opend twice");
        } catch (PortBusyException busyException) {
            Assert.assertFalse(spc2.isOpen());
        }
        spc.close();
    }

    @Test
    public void testOpenSamePort2Times() throws IOException {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testOpenSamePort2Times");

        spc.openRaw();
        try {
            spc.openRaw();
            fail("Same port opend twice");
        } catch (IOException ioe) {
            assertEquals(SerialPortSocket.PORT_IS_OPEN, ioe.getMessage());
            Assert.assertTrue(spc.isOpen());
        }
        spc.close();
    }

    /**
     * Test the finalization by the garbage collector
     * @throws IOException
     * @throws InterruptedException 
     */
    @Test
    public void testFinalize() throws IOException, InterruptedException {
        Assume.assumeNotNull(spc);
        LOG.log(Level.INFO, "run testFinalize on " + spc.getPortName());

        spc.openRaw();
        WeakReference<SerialPortSocket> refSpc = new WeakReference<>(spc);
        spc = null;

            //Order matters! First garbage collect then finalize....
            Runtime.getRuntime().gc();
            Runtime.getRuntime().runFinalization();
        Assert.assertNull(refSpc.get());

        //On Windows the GC needs some time - I don't know why...
        if (System.getProperty("os.name").startsWith("Windows")) {
        	Thread.sleep(100);
        }
        
        spc = getSerialPortSocketFactory().createSerialPortSocket(serialPortName);
        spc.openRaw();
        spc.close();
    }

    private void printPort(SerialPortSocket sPort) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\n\tName:        %-20s\n", sPort.getPortName()));
        sb.append(String.format("\tBaudrate:    %-20d\n", sPort.getBaudrate().value));
        sb.append(String.format("\tStopBits:    %-20f\n", sPort.getStopBits().value));
        sb.append(String.format("\tParity:      %-20s\n", sPort.getParity().name()));
        sb.append(String.format("\tFlowControl: %-20s\n", sPort.getFlowControl().toString()));
        sb.append(String.format("\tIntebyteReadTimeout:    %-20d\n", sPort.getInterByteReadTimeout()));
        sb.append(String.format("\tOverallReadTimeout:    %-20d\n", sPort.getOverallReadTimeout()));
        sb.append(String.format("\tOverallWriteTimeout:    %-20d\n", sPort.getOverallWriteTimeout()));

        LOG.log(Level.INFO, sb.toString());
}

}
