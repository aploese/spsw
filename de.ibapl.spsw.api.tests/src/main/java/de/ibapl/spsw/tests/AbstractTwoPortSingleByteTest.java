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

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.SerialPortSocketFactory;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.api.TimeoutIOException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 * Unit test for simple App.
 * Use @Ignore if your hardware can't handle higer speeds.
 * 
 */
public abstract class AbstractTwoPortSingleByteTest {

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
                        final int data = spc[1].getInputStream().read();
                        if (data >= 0) {
                            dataIn[offset] = (byte) data;
                            Assert.assertEquals("Error @Offset: " + offset, dataOut[offset], dataIn[offset]);
                            offset++;
                            if (offset == dataOut.length) {
                                break;
                            }
                        } else {
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
    protected abstract SerialPortSocketFactory getSerialPortSocketFactory();

    @BeforeClass
    public static void setUpClass() throws Exception {
        try (InputStream is = AbstractTwoPortSingleByteTest.class.getClassLoader().getResourceAsStream("junit-spsw-config.properties")) {
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
                spc[i] = getSerialPortSocketFactory().createSerialPortSocket(serialPortName[i]);
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
                    String name = spc[i].getPortName();
                	spc[i].close();
                    LOG.severe("CLOSED PORT: " + name);
                }
            }
            spc[i] = null;
        }
        receiverThread = null;
        Runtime.getRuntime().gc();
        Runtime.getRuntime().runFinalization();
       //TODO Thread.sleep(10000);
        
        // On windows the COM ports needs time to properly close...
        // Thread.sleep(100);
    }

    private void runTest(Baudrate baudrate, int buffersize) throws Exception {
    	LOG.info(MessageFormat.format("do run test @baudrate: {0}, buffer size: {1}", baudrate.value, buffersize));

        receiverThread.initBuffers(buffersize);
        receiverThread.startTimeStamp = System.currentTimeMillis();

        Assume.assumeNotNull((Object[]) spc);
        spc[0].openRaw(baudrate, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_RTS_CTS());
        spc[1].openRaw(spc[0].getBaudrate(), spc[0].getDatatBits(), spc[0].getStopBits(), spc[0].getParity(), spc[0].getFlowControl());
        printPorts(spc[0], spc[1]);

        //Make sure buffers are empty
        Assert.assertEquals(0, spc[0].getOutBufferBytesCount());
        Assert.assertEquals(0, spc[1].getInBufferBytesCount());

        receiverThread.start();

        for (int i = 0; i < receiverThread.dataOut.length; i++) {
        	spc[0].getOutputStream().write(receiverThread.dataOut[i]);
        }

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

    private void printPorts(SerialPortSocket sPort0, SerialPortSocket sPort1) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\n\tName:        %-20s %-20s\n", sPort0.getPortName(), sPort1.getPortName()));
        sb.append(String.format("\tBaudrate:    %-20d %-20d\n", sPort0.getBaudrate().value, sPort1.getBaudrate().value));
        sb.append(String.format("\tStopBits:    %-20f %-20f\n", sPort0.getStopBits().value, sPort1.getStopBits().value));
        sb.append(String.format("\tParity:      %-20s %-20s\n", sPort0.getParity().name(), sPort1.getParity().name()));
        sb.append(String.format("\tFlowControl: %-20s %-20s\n", sPort0.getFlowControl().toString(), sPort1.getFlowControl().toString()));
        sb.append(String.format("\tIntebyteReadTimeout:    %-20d %-20d\n", sPort0.getInterByteReadTimeout(), sPort1.getInterByteReadTimeout()));
        sb.append(String.format("\tOverallReadTimeout:    %-20d %-20d\n", sPort0.getOverallReadTimeout(), sPort1.getOverallReadTimeout()));
        sb.append(String.format("\tOverallWriteTimeout:    %-20d %-20d\n", sPort0.getOverallWriteTimeout(), sPort1.getOverallWriteTimeout()));

        LOG.log(Level.INFO, sb.toString());
}

    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 300)
    public void test_0000300() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B300, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 1200)
    public void test_0001200() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B1200, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 2400)
    public void test_0002400() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B2400, DEFAULT_TEST_BUFFER_SIZE);
    }

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

    //@Ignore //if your hardware can't handle this speed.
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 57600)
    public void test_0057600() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B57600, DEFAULT_TEST_BUFFER_SIZE);
    }

    //@Ignore //if your hardware can't handle this speed.
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 115200)
    public void test_0115200() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B115200, DEFAULT_TEST_BUFFER_SIZE);
    }

    //@Ignore //if your hardware can't handle this speed.
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 230400)
    public void test_0230400() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B230400, DEFAULT_TEST_BUFFER_SIZE);
    }

    //@Ignore //if your hardware can't handle this speed.
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 460800)
    public void test_0460800() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B460800, DEFAULT_TEST_BUFFER_SIZE);
    }

    //@Ignore //if your hardware can't handle this speed.
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 500000)
    public void test_0500000() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B500000, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Ignore //if your hardware can't handle this speed.
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 576000)
    public void test_0576000() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B576000, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Ignore //if your hardware can't handle this speed.
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 1000000)
    public void test_1000000() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B1000000, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Ignore //if your hardware can't handle this speed.
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 1152000)
    public void test_1152000() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B1152000, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Ignore //if your hardware can't handle this speed.
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 2000000)
    public void test_2000000() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B2000000, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Ignore //if your hardware can't handle this speed.
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 3000000)
    public void test_3000000() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B3000000, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Ignore //if your hardware can't handle this speed.
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 10 * 1000) / 4000000)
    public void test_4000000() throws Exception {
        Assume.assumeNotNull(spc);
        runTest(Baudrate.B4000000, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Ignore // Can't test this my USB driver does not really do RTS/CTS
    @Test
    public void testWriteBufferFull() throws Exception {
        Assume.assumeNotNull(spc);
		LOG.log(Level.INFO, "run testWriteSingleByteTimeout");
		// Set a high baudrate to speed up things
		spc[0].openRaw(Baudrate.B115200, DataBits.DB_8, StopBits.SB_1, Parity.EVEN, FlowControl.getFC_RTS_CTS());
		assertEquals(FlowControl.getFC_RTS_CTS(), spc[0].getFlowControl());
		spc[1].openRaw(Baudrate.B115200, DataBits.DB_8, StopBits.SB_1, Parity.EVEN, FlowControl.getFC_RTS_CTS());
		assertEquals(FlowControl.getFC_RTS_CTS(), spc[1].getFlowControl());
		// make sure out buffer is empty
		assertEquals(0, spc[0].getOutBufferBytesCount());
		spc[0].setTimeouts(100, 1000, 10000);
		spc[1].setTimeouts(100, 1000, 10000);
		final byte[] b = new byte[1024*1024];
		for (int i = 0; i < b.length; i++ ) {
			b[i] = (byte)i;
		}
		int round = 1;
		int dataWritten;
		int overallDataWritten = 0;

		do {
			try {
				spc[0].getOutputStream().write(0xFF);
				dataWritten = 1; //b.length;
			} catch (TimeoutIOException e) {
				dataWritten = e.bytesTransferred;
			}
			try {
				spc[0].getOutputStream().flush();
			} catch (TimeoutIOException e) {
			}
			round++;
			overallDataWritten += dataWritten;
			LOG.log(Level.INFO,
					"Wrote: " + dataWritten + " (" + overallDataWritten + ") in " + round + " rounds; OutBuf:  " + spc[0].getOutBufferBytesCount() + "inBuffer: " + spc[1].getInBufferBytesCount());
		} while (dataWritten > 0);

		LOG.log(Level.INFO,
				"Wrote: " + overallDataWritten + " in " + round + " rounds; OutBuf:  " + spc[0].getOutBufferBytesCount());
		LOG.log(Level.INFO, "disable flow control to sped up closing");
		byte[] read = new byte[256];
		assertEquals(256, spc[1].getInputStream().read(read));
		new Thread(() -> {
			try {
				spc[0].getOutputStream().write(b, 0, 1024);
				fail();
			} catch (TimeoutIOException tIoException) {
				assertEquals(512, tIoException.bytesTransferred);
			}catch (Exception e) {
				fail();
			}
		}).start();

		Thread.sleep(2000);
		
		assertEquals(256, spc[1].getInputStream().read(read));
		
		
		
		spc[0].setBaudrate(Baudrate.B500000);
		spc[0].setFlowControl(FlowControl.getFC_NONE());
		spc[0].close();
		spc[1].close();
		Assert.assertTrue(spc[0].isClosed());
		Assert.assertTrue(spc[1].isClosed());
		LOG.log(Level.INFO, "port closed");
    	fail();
    }
    
    /**
     * The receiving (spc[1] should stop transfer via RTS/CTS ...
     * @throws Exception
     */
    @Ignore // Can't test this my USB driver does not really do RTS/CTS
    @Test
    public void testFillInbuffer() throws Exception {
        Assume.assumeNotNull(spc);
		LOG.log(Level.INFO, "run testWriteSingleByteTimeout");
		// Set a high baudrate to speed up things
		spc[0].openRaw(Baudrate.B115200, DataBits.DB_8, StopBits.SB_1, Parity.EVEN, FlowControl.getFC_RTS_CTS());
		assertEquals(FlowControl.getFC_RTS_CTS(), spc[0].getFlowControl());
		spc[1].openRaw(Baudrate.B115200, DataBits.DB_8, StopBits.SB_1, Parity.EVEN, FlowControl.getFC_RTS_CTS());
		assertEquals(FlowControl.getFC_RTS_CTS(), spc[1].getFlowControl());
		// make sure out buffer is empty
		assertEquals(0, spc[0].getOutBufferBytesCount());
		spc[0].setTimeouts(100, 1000, 10000);
		spc[1].setTimeouts(100, 1000, 10000);
		final byte[] b = new byte[1024];
		for (int i = 0; i < b.length; i++ ) {
			b[i] = (byte)i;
		}
		int round = 0;
		int dataWritten;
		int overallDataWritten = 0;

		do {
			try {
				spc[0].getOutputStream().write(b);
				dataWritten = b.length;
			} catch (TimeoutIOException e) {
				dataWritten = e.bytesTransferred;
			}
			try {
				spc[0].getOutputStream().flush();
			} catch (TimeoutIOException e) {
			}
			round++;
			overallDataWritten += dataWritten;
			LOG.log(Level.INFO,
					"Wrote: " + dataWritten + " (" + overallDataWritten + ") in " + round + " rounds; OutBuf:  " + spc[0].getOutBufferBytesCount() + "inBuffer: " + spc[1].getInBufferBytesCount());
			if (round == 1024) {
				break;
			}
		} while (dataWritten > 0);

		LOG.log(Level.INFO,
				"Wrote: " + overallDataWritten + " in " + round + " rounds; OutBuf:  " + spc[0].getOutBufferBytesCount());
		LOG.log(Level.INFO, "disable flow control to sped up closing");
		int dataread = 0;
		int readTotal = 0;;
		do {
			try {
				dataread = spc[1].getInputStream().read(b);
				LOG.log(Level.INFO, "Read: " + dataread);
			} catch (TimeoutIOException e) {
				dataread = e.bytesTransferred;
				LOG.log(Level.INFO, "Got timeout");
			}
			readTotal += dataread;
		} while (dataread > 0);
		assertEquals(overallDataWritten, readTotal);
		
		
		
		spc[0].setBaudrate(Baudrate.B500000);
		spc[0].setFlowControl(FlowControl.getFC_NONE());
		spc[0].close();
		spc[1].close();
		Assert.assertTrue(spc[0].isClosed());
		Assert.assertTrue(spc[1].isClosed());
		LOG.log(Level.INFO, "port closed");
    }
    
}
