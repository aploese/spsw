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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.SerialPortSocketFactory;
import de.ibapl.spsw.api.StopBits;
import java.io.InterruptedIOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit test for simple App. Timeout is computed 8 data bits + 2 stop bits +
 * parity bit + start bit == 12
 */
public abstract class AbstractTwoPortsBaselineTest extends AbstractPortTest {

    private final static int DEFAULT_TEST_BUFFER_SIZE = 128;

    private byte[] dataOut;
    private byte[] dataIn;
    protected Set<FlowControl> flowControl = FlowControl.getFC_NONE(); // getFC_RTS_CTS();
    protected Parity parity = Parity.NONE;
    protected StopBits stopBits = StopBits.SB_1;
    protected DataBits dataBits = DataBits.DB_8;


    public void setUp() throws Exception {
        initBuffers(DEFAULT_TEST_BUFFER_SIZE);
        super.setUp();
    }

    private void initBuffers(final int size) {
        dataOut = new byte[size];
        for (int i = 0; i < size; i++) {
            dataOut[i] = (byte) (Math.round(Math.random() * 0xFF) & 0xFF);
        }
        dataIn = new byte[size];
    }

    private void writeBytes() throws IOException {
        try {
            writeSpc.getOutputStream().write(dataOut);
            LOG.fine("data written");
        } catch (InterruptedIOException iioe) {
            LOG.log(Level.SEVERE, "{0} Bytes send total: {1}", new Object[]{iioe.getMessage(), iioe.bytesTransferred});
            Assert.fail(iioe.getMessage() + " Bytes send total: " + iioe.bytesTransferred);
        } catch (IOException e) {
            LOG.severe(e.getMessage());
            Assert.fail(e.getMessage());
        }

        int received = 0;
        while (dataIn.length > received) {
            try {
                int length = readSpc.getInputStream().read(dataIn, received, dataIn.length - received);
                if (length >= 0) {
                    received += length;
                } else {
                    throw new RuntimeException();
                }
            } catch (InterruptedIOException iioe) {
                LOG.log(Level.SEVERE, "{0} Bytes received total: {1} Bytes received this: {2} Bytes in in/out buffer {3}/{4}", new Object[]{iioe.getMessage(), received, iioe.bytesTransferred, readSpc.getInBufferBytesCount(), writeSpc.getOutBufferBytesCount()});
                Assert.fail(iioe.getMessage() + " Bytes received total: " + received + " Bytes received this: " + iioe.bytesTransferred + " Bytes in in/out buffer " + readSpc.getInBufferBytesCount() + "/" + writeSpc.getOutBufferBytesCount());
            } catch (IOException e) {
                LOG.severe(e.getMessage());
            }
        }
        Assert.assertArrayEquals(dataOut, dataIn);
    }

    private void writeSingle() throws IOException {
        try {
            for (int i = 0; i < dataOut.length; i++) {
                writeSpc.getOutputStream().write(dataOut[i]);
            }
            LOG.fine("data written");
        } catch (InterruptedIOException iioe) {
            LOG.log(Level.SEVERE, "{0} Bytes send total: {1}", new Object[]{iioe.getMessage(), iioe.bytesTransferred});
            Assert.fail(iioe.getMessage() + " Bytes send total: " + iioe.bytesTransferred);
        } catch (IOException e) {
            LOG.severe(e.getMessage());
            Assert.fail(e.getMessage());
        }

        for (int i= 0; i < dataIn.length; i++) {
            try {
                int received = readSpc.getInputStream().read();
                if (received >= 0) {
                    dataIn[i] = (byte)received;
                } else {
                    throw new RuntimeException();
                }
            } catch (InterruptedIOException iioe) {
                LOG.log(Level.SEVERE, "{0} Bytes received total: {1} Bytes received this: {2} Bytes in in/out buffer {3}/{4}", new Object[]{iioe.getMessage(), i, iioe.bytesTransferred, readSpc.getInBufferBytesCount(), writeSpc.getOutBufferBytesCount()});
                Assert.fail(iioe.getMessage() + " Bytes received total: " + i + " Bytes received this: " + iioe.bytesTransferred + " Bytes in in/out buffer " + readSpc.getInBufferBytesCount() + "/" + writeSpc.getOutBufferBytesCount());
            } catch (IOException e) {
                LOG.severe(e.getMessage());
            }
        }
        Assert.assertArrayEquals(dataOut, dataIn);
    }

    private void runTest(Baudrate baudrate, int buffersize) throws Exception {
        assumeRWTest();
        LOG.info(MessageFormat.format("do run test @baudrate: {0}, buffer size: {1}", baudrate.value, buffersize));

        openRaw(baudrate, dataBits, stopBits, parity, flowControl);
        setTimeouts(100, 20000, 20000);
        printPorts();

        writeBytes();
        writeSingle();
    }

    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 2 * 12 * 1000) / 300)
    public void test_0000300() throws Exception {
        runTest(Baudrate.B300, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 2 * 12 * 1000) / 600)
    public void test_0000600() throws Exception {
        runTest(Baudrate.B600, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 2 * 12 * 1000) / 1200)
    public void test_0001200() throws Exception {
        runTest(Baudrate.B1200, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 2 * 12 * 1000) / 2400)
    public void test_0002400() throws Exception {
        runTest(Baudrate.B2400, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 2 * 12 * 1000) / 4800)
    public void test_0004800() throws Exception {
        runTest(Baudrate.B4800, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 2 * 12 * 1000) / 9600)
    public void test_0009600() throws Exception {
        runTest(Baudrate.B9600, DEFAULT_TEST_BUFFER_SIZE);
    }

//    @Ignore
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 2 * 12 * 1000) / 19200)
    public void test_0019200() throws Exception {
        runTest(Baudrate.B19200, DEFAULT_TEST_BUFFER_SIZE);
    }

    //@Ignore
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 2 * 12 * 1000) / 38400)
    public void test_0038400() throws Exception {
        runTest(Baudrate.B38400, DEFAULT_TEST_BUFFER_SIZE);
    }

    //@Ignore
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 2 * 12 * 1000) / 57600)
    public void test_0057600() throws Exception {
        runTest(Baudrate.B57600, DEFAULT_TEST_BUFFER_SIZE);
    }

    //@Ignore
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 2 * 12 * 1000) / 115200)
    public void test_0115200() throws Exception {
        runTest(Baudrate.B115200, DEFAULT_TEST_BUFFER_SIZE);
    }

    //@Ignore
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 2 * 12 * 1000) / 230400)
    public void test_0230400() throws Exception {
        runTest(Baudrate.B230400, DEFAULT_TEST_BUFFER_SIZE);
    }

    //@Ignore
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 2 * 12 * 1000) / 460800)
    public void test_0460800() throws Exception {
        runTest(Baudrate.B460800, DEFAULT_TEST_BUFFER_SIZE);
    }

    //@Ignore
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 2 * 12 * 1000) / 500000)
    public void test_0500000() throws Exception {
        runTest(Baudrate.B500000, DEFAULT_TEST_BUFFER_SIZE);
    }

    //@Ignore
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 2 * 12 * 1000) / 576000)
    public void test_0576000() throws Exception {
        runTest(Baudrate.B576000, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Ignore
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 2 * 12 * 1000) / 1000000)
    public void test_1000000() throws Exception {
        runTest(Baudrate.B1000000, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Ignore
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 2 * 12 * 1000) / 1152000)
    public void test_1152000() throws Exception {
        runTest(Baudrate.B1152000, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Ignore
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 2 * 12 * 1000) / 2000000)
    public void test_2000000() throws Exception {
        runTest(Baudrate.B2000000, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Ignore
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 2 * 12 * 1000) / 3000000)
    public void test_3000000() throws Exception {
        runTest(Baudrate.B3000000, DEFAULT_TEST_BUFFER_SIZE);
    }

    @Ignore
    @Test(timeout = 1000 + (DEFAULT_TEST_BUFFER_SIZE * 2 * 12 * 1000) / 4000000)
    public void test_4000000() throws Exception {
        runTest(Baudrate.B4000000, DEFAULT_TEST_BUFFER_SIZE);
    }

}