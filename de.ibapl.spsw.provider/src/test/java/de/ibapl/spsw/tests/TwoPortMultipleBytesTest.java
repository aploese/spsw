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
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.provider.SerialPortSocketFactoryImpl;
import java.io.OutputStream;
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
public class TwoPortMultipleBytesTest {

    private static final Logger LOG = Logger.getLogger("SerialTests");
    private final static int DEFAULT_TEST_BUFFER_SIZE = 1024 * 2;

    public class ReceiverThread extends Thread {

        boolean done;
        final Object LOCK = new Object();
        byte[] dataIn;
        byte[] dataOut;
        long startTimeStamp;
        Exception ex;
        Error err;

        @Override
        public void run() {
            try {
                int offset = 0;
                while (true) {
                    try {
                        final int count = spc[1].getInputStream().read(dataIn, offset, dataIn.length - offset);
                        if (count > 0) {
                            Assert.assertArrayEquals("Error @Offset: " + offset + " @Count: " + count, Arrays.copyOfRange(dataOut, offset, offset + count), Arrays.copyOfRange(dataIn, offset, offset + count));
                            offset += count;
                            if (offset == dataOut.length) {
                                break;
                            }
                        }
                        LOG.log(Level.FINEST, "Bytes read: {0}", count);
                        if (count <= 0) {
                            if (offset < dataIn.length) {
                                LOG.log(Level.SEVERE, "Bytes missing: {0}", dataIn.length - offset);
                                printArrays("Too short");
                            }
                            break;
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                LOG.log(Level.INFO, "Byte total read: {0}", offset);
                LOG.log(Level.INFO, "Receive time in millis:: {0}", (System.currentTimeMillis() - startTimeStamp));
                synchronized (LOCK) {
                    done = true;
                    LOCK.notifyAll();
                    LOG.log(Level.INFO, "Send Thread finished");
                }
            } catch (Exception ex) {
                synchronized (LOCK) {
                    done = true;
                    this.ex = ex;
                    LOG.log(Level.SEVERE, "Send Thread Exception", ex);
                    LOCK.notifyAll();
                }
            } catch (Error err) {
                synchronized (LOCK) {
                    done = true;
                    this.err = err;
                    printArrays("Error");
                    LOG.log(Level.SEVERE, "Send Thread Error", err);
                    LOCK.notifyAll();
                }
            }
        }

        private void initBuffers(final int size) {
            dataOut = new byte[size];
            for (int i = 0; i < size; i++) {
//                  dataOut[i] = (byte) (i & 0xFF);
                dataOut[i] = (byte) (Math.round(Math.random() * 0xFF) & 0xFF);
            }
            dataIn = new byte[size];
        }

        private void printArrays(String title) {
            System.err.println(title);
            for (int i = 0; i < dataOut.length; i++) {
                System.err.println(i + "\t: " + dataOut[i] + "\t" + dataIn[i]);
            }
        }

    }

    private static final String[] serialPortName = new String[2];
    private final SerialPortSocket[] spc = new SerialPortSocket[2];
    private ReceiverThread receiverThread;

    @BeforeClass
    public static void setUpClass() throws Exception {
        try (InputStream is = TwoPortMultipleBytesTest.class.getClassLoader().getResourceAsStream("junit-spsw-config.properties")) {
            if (is == null) {
                serialPortName[0] = null;
                serialPortName[1] = null;
            } else {
                Properties p = new Properties();
                p.load(is);
                serialPortName[0] = p.getProperty("port0", null);
                serialPortName[1] = p.getProperty("port1", null);
            }
        }
    }

    @Before
    public void setUp() throws Exception {
        for (int i = 0; i < serialPortName.length; i++) {
            if (serialPortName[i] != null) {
//LOG output                spc[i] = new LoggingSerialPortSocket(SerialPortSocket.FACTORY.createSerialPortSocket(serialPortName[i]), "/tmp/log." + serialPortName[i].replace("/", "_") + ".txt");
                spc[i] = SerialPortSocketFactoryImpl.singleton().createSerialPortSocket(serialPortName[i]);
                receiverThread = new ReceiverThread();
            } else {
                spc[i] = null;
            }
        }
    }

    @After
    public void tearDown() throws Exception {
        for (int i = 0; i < spc.length; i++) {
            if (spc[i] != null) {
                if (spc[i].isOpen()) {
                    spc[i].close();
                }
            }
            spc[i] = null;
        }
        receiverThread = null;
        //On windows the COM ports needs time to properly close...
        Thread.sleep(100);
    }

    private void runTest(Baudrate baudrate, int buffersize) throws Exception {
        LOG.info(MessageFormat.format("do run test @baudrate: {0}, buffer size: {1}", baudrate.value, buffersize));

        receiverThread.initBuffers(buffersize);
        receiverThread.startTimeStamp = System.currentTimeMillis();

        Assume.assumeNotNull((Object[]) spc);
        spc[0].openRaw(baudrate, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_RTS_CTS());
        spc[1].openRaw(spc[0].getBaudrate(), spc[0].getDatatBits(), spc[0].getStopBits(), spc[0].getParity(), spc[0].getFlowControl());
        printPort(spc[0]);

        //Make sure buffers are empty
        Assert.assertEquals(0, spc[0].getOutBufferBytesCount());
        Assert.assertEquals(0, spc[1].getInBufferBytesCount());

        receiverThread.start();

        spc[0].getOutputStream().write(receiverThread.dataOut);
//        for (int i = 0; i < receiverThread.dataOut.length; i++) spc[0].getOutputStream().write(receiverThread.dataOut[i]);

        LOG.log(Level.INFO, "Send time in millis:: {0}", (System.currentTimeMillis() - receiverThread.startTimeStamp));
        synchronized (receiverThread.LOCK) {
            while (!receiverThread.done) {
                receiverThread.LOCK.wait();
                if (receiverThread.ex != null) {
                    throw receiverThread.ex;
                }
                if (receiverThread.err != null) {
                    throw receiverThread.err;
                }
            }
            LOG.log(Level.INFO, "Thread finished received");
        }
        Assert.assertArrayEquals(receiverThread.dataOut, receiverThread.dataIn);
    }

    private void printPort(SerialPortSocket sPort) throws IOException {
        System.err.println("Baudrate: " + sPort.getBaudrate().value);
        System.err.println("DataBits: " + sPort.getDatatBits().value);
        System.err.println("StopBits: " + sPort.getStopBits().value);
        System.err.println("Parity: " + sPort.getParity());
        System.err.println("Flowcontrol: " + sPort.getFlowControl());

    }

    @Ignore
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 300)
    public void test_0000300() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B300, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Ignore
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 2400)
    public void test_0002400() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B2400, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Ignore
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 9600)
    public void test_0009600() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B9600, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 19200)
    public void test_0019200() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B19200, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 57600)
    public void test_0057600() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B57600, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 115200)
    public void test_0115200() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B115200, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 230400)
    public void test_0230400() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B230400, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 460800)
    public void test_0460800() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B460800, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 500000)
    public void test_0500000() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B500000, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 576000)
    public void test_0576000() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B576000, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Ignore
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 1000000)
    public void test_1000000() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B1000000, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Ignore
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 1152000)
    public void test_1152000() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B1152000, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Ignore
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 2000000)
    public void test_2000000() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B2000000, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Ignore
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 3000000)
    public void test_3000000() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B3000000, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Ignore
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 4000000)
    public void test_4000000() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B4000000, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Test(timeout = 5000)
    public void testTimeout() throws Exception {
        Assume.assumeNotNull(spc);

        spc[0].openRaw(Baudrate.B9600, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_RTS_CTS());
        spc[1].openRaw(spc[0].getBaudrate(), spc[0].getDatatBits(), spc[0].getStopBits(), spc[0].getParity(), spc[0].getFlowControl());
//        spc[0].setInterByteTimeout(100);
//        spc[0].setOverallTimeout(2000);
        Assert.assertEquals(100, spc[0].getInterByteReadTimeout());
        final InputStream is = spc[0].getInputStream();
        final OutputStream os = spc[1].getOutputStream();

        final byte[] recBuff = new byte[255];
        Arrays.fill(recBuff, (byte) 0);
        
        final byte[] sendBuff = new byte[128];
        Arrays.fill(sendBuff, (byte) 0x7F);
 
        final Object lock = new Object();
        final Thread t = new Thread(() -> {
            try {
                final long start = System.currentTimeMillis();
                final int i = is.read(recBuff);
                final long diff = System.currentTimeMillis() - start;
                Assert.assertEquals("Only expected 1 byte to read " + diff + "ms ", 1, i);
                Assert.assertEquals("Error @0", 0x7F, recBuff[0]);
                Assert.assertEquals("Error @1", 0x00, recBuff[1]);
                synchronized (lock) {
                    lock.notifyAll();
                }
            } catch (IOException ex) {
                Assert.fail(ex.getMessage());
            }

        });
        t.start();
        
        //Quick and dirty time lock to start receiver thread
        Thread.sleep(200);
        
        os.write(sendBuff);
        os.flush();
        
        synchronized (lock) {
            lock.wait();
        }
        
        //Wait for all data to arrive...
        Thread.sleep(200);
        
        Assert.assertEquals(127, is.read(recBuff, 1, 200));
        
        for (int i = 0; i < 128; i++) {
            Assert.assertEquals("Error @" + i, sendBuff[i], recBuff[i]);
        }
        
    }

        @Test(timeout = 5000)
    public void testInfiniteTimeout() throws Exception {
        Assume.assumeNotNull(spc);

        spc[0].openRaw(Baudrate.B9600, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_RTS_CTS());
        spc[1].openRaw(spc[0].getBaudrate(), spc[0].getDatatBits(), spc[0].getStopBits(), spc[0].getParity(), spc[0].getFlowControl());
        final InputStream is = spc[0].getInputStream();
        final OutputStream os = spc[1].getOutputStream();

        final byte[] recBuff = new byte[255];
        Arrays.fill(recBuff, (byte) 0);
        
        final byte[] sendBuff = new byte[128];
        Arrays.fill(sendBuff, (byte) 0x7F);
 
        os.write(sendBuff);
        os.flush();

        Thread.sleep(200);
        Assert.assertEquals(128, is.available());
        final int bytesread = is.read(recBuff);
        
        Assert.assertEquals(128, bytesread);
        
        for (int i = 0; i < 128; i++) {
            Assert.assertEquals("Error @" + i, sendBuff[i], recBuff[i]);
        }
        
    }

}
