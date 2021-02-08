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
package de.ibapl.spsw.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.api.TimeoutIOException;
import de.ibapl.spsw.tests.tags.ByteChannelTest;
import de.ibapl.spsw.tests.tags.DtrDsrTest;
import de.ibapl.spsw.tests.tags.IOStreamTest;
import de.ibapl.spsw.tests.tags.RtsCtsTest;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;

/**
 * @author Arne Plöse
 */
public abstract class AbstractReadWriteRtsCtsTests extends AbstractReadWriteTest {

    public Iterator<PortConfiguration> getBaselinePortConfigurations() {
        return new PortConfigurationFactory().setFlowControl(FlowControl.getFC_RTS_CTS())
                .getSpeedIterator(Speed._1200_BPS, Speed._115200_BPS);
    }

    @IOStreamTest
    @RtsCtsTest
    @ParameterizedTest
    @MethodSource({"getBaselinePortConfigurations"})
    public void test_WriteBytes_ReadBytes(PortConfiguration pc) throws Exception {
        write_Read_nonThreaded(SocketIoType.STREAM, SocketIoType.STREAM, pc);
    }

    @IOStreamTest
    @RtsCtsTest
    @ParameterizedTest
    @MethodSource({"getBaselinePortConfigurations"})
    public void test_WriteBytes_ReadSingle(PortConfiguration pc) throws Exception {
        write_Read_nonThreaded(SocketIoType.STREAM, SocketIoType.SINGLE_BYTE, pc);
    }

    @IOStreamTest
    @RtsCtsTest
    @ParameterizedTest
    @MethodSource({"getBaselinePortConfigurations"})
    public void test_WriteSingle_ReadBytes(PortConfiguration pc) throws Exception {
        write_Read_nonThreaded(SocketIoType.SINGLE_BYTE, SocketIoType.STREAM, pc);
    }

    @RtsCtsTest
    @ParameterizedTest
    @MethodSource({"getBaselinePortConfigurations"})
    public void test_WriteSingle_ReadSingle(PortConfiguration pc) throws Exception {
        write_Read_nonThreaded(SocketIoType.SINGLE_BYTE, SocketIoType.SINGLE_BYTE, pc);
    }

    @IOStreamTest
    @RtsCtsTest
    @ParameterizedTest
    @MethodSource({"getBaselinePortConfigurations"})
    public void test_WriteBytes_ReadBytes_Threaded(PortConfiguration pc) throws Exception {
        write_Read_Threaded(SocketIoType.STREAM, SocketIoType.STREAM, pc);
    }

    @IOStreamTest
    @RtsCtsTest
    @ParameterizedTest
    @MethodSource({"getBaselinePortConfigurations"})
    public void test_WriteBytes_ReadSingle_Threaded(PortConfiguration pc) throws Exception {
        write_Read_Threaded(SocketIoType.STREAM, SocketIoType.SINGLE_BYTE, pc);
    }

    @IOStreamTest
    @RtsCtsTest
    @ParameterizedTest
    @MethodSource({"getBaselinePortConfigurations"})
    public void test_WriteSingle_ReadBytes_Threaded(PortConfiguration pc) throws Exception {
        write_Read_Threaded(SocketIoType.SINGLE_BYTE, SocketIoType.STREAM, pc);
    }

    @RtsCtsTest
    @ParameterizedTest
    @MethodSource({"getBaselinePortConfigurations"})
    public void test_WriteSingle_ReadSingle_Threaded(PortConfiguration pc) throws Exception {
        write_Read_Threaded(SocketIoType.SINGLE_BYTE, SocketIoType.SINGLE_BYTE, pc);
    }

    @ByteChannelTest
    @RtsCtsTest
    @ParameterizedTest
    @MethodSource({"getBaselinePortConfigurations"})
    public void test_Channel_WriteBytes_ReadBytes(PortConfiguration pc) throws Exception {
        write_Read_nonThreaded(SocketIoType.CHANNEL, SocketIoType.CHANNEL, pc);
    }

    @ByteChannelTest
    @RtsCtsTest
    @ParameterizedTest
    @MethodSource({"getBaselinePortConfigurations"})
    public void test_Channel_WriteBytes_ReadSingle(PortConfiguration pc) throws Exception {
        write_Read_nonThreaded(SocketIoType.CHANNEL, SocketIoType.SINGLE_BYTE, pc);
    }

    @ByteChannelTest
    @RtsCtsTest
    @ParameterizedTest
    @MethodSource({"getBaselinePortConfigurations"})
    public void test_Channel_WriteSingle_ReadBytes(PortConfiguration pc) throws Exception {
        write_Read_nonThreaded(SocketIoType.SINGLE_BYTE, SocketIoType.CHANNEL, pc);
    }

    @ByteChannelTest
    @RtsCtsTest
    @ParameterizedTest
    @MethodSource({"getBaselinePortConfigurations"})
    public void test_Channel_WriteBytes_ReadBytes_Threaded(PortConfiguration pc) throws Exception {
        write_Read_Threaded(SocketIoType.CHANNEL, SocketIoType.CHANNEL, pc);
    }

    @ByteChannelTest
    @RtsCtsTest
    @ParameterizedTest
    @MethodSource({"getBaselinePortConfigurations"})
    public void test_Channel_WriteBytes_ReadSingle_Threaded(PortConfiguration pc) throws Exception {
        write_Read_Threaded(SocketIoType.CHANNEL, SocketIoType.SINGLE_BYTE, pc);
    }

    @ByteChannelTest
    @RtsCtsTest
    @ParameterizedTest
    @MethodSource({"getBaselinePortConfigurations"})
    public void test_Channel_WriteSingle_ReadBytes_Threaded(PortConfiguration pc) throws Exception {
        write_Read_Threaded(SocketIoType.SINGLE_BYTE, SocketIoType.CHANNEL, pc);
    }
    // TODO does not work as expected Linux Kernel fills in buffer size?

    @Disabled
    @Test
    public void testWriteBufferFull() throws Exception {
        assumeRWTest();
        LOG.log(Level.INFO, "run testWriteSingleByteTimeout");
        // Set a high speed to speed up things
        open(Speed._115200_BPS, DataBits.DB_8, StopBits.SB_1, Parity.EVEN, FlowControl.getFC_RTS_CTS());
        setTimeouts(100, 1000, 10000);

        final byte[] b = new byte[1024 * 1024];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) i;
        }
        int round = 1;
        int dataWritten;
        int overallDataWritten = 0;

        do {
            try {
                writeSpc.getOutputStream().write(0xFF);
                dataWritten = 1; // b.length;
            } catch (TimeoutIOException e) {
                dataWritten = e.bytesTransferred;
            }
            try {
                writeSpc.getOutputStream().flush();
            } catch (TimeoutIOException e) {
            }
            round++;
            overallDataWritten += dataWritten;
            LOG.log(Level.INFO,
                    "Wrote: " + dataWritten + " (" + overallDataWritten + ") in " + round + " rounds; OutBuf:  "
                    + writeSpc.getOutBufferBytesCount() + "inBuffer: " + readSpc.getInBufferBytesCount());
        } while (dataWritten > 0);

        LOG.log(Level.INFO, "Wrote: " + overallDataWritten + " in " + round + " rounds; OutBuf:  "
                + writeSpc.getOutBufferBytesCount());
        LOG.log(Level.INFO, "disable flow control to sped up closing");
        byte[] read = new byte[256];
        assertEquals(256, readSpc.getInputStream().read(read));

        final Future<Object> writeResult = EXECUTOR_SERVICE.submit(() -> {
            try {
                writeSpc.getOutputStream().write(b, 0, 1024);
                return 1024;
            } catch (Throwable t) {
                return t;
            }
        });

        Object o = writeResult.get(2, TimeUnit.SECONDS);
        if (o instanceof Integer) {
            fail("Expected IOException");
        } else if (o instanceof TimeoutIOException) {
            assertEquals(512, ((TimeoutIOException) o).bytesTransferred);
        } else {
            fail((Throwable) o);
        }

        assertEquals(256, readSpc.getInputStream().read(read));

        writeSpc.setSpeed(Speed._500000_BPS);
        writeSpc.setFlowControl(FlowControl.getFC_NONE());
    }

    /**
     * The receiving (spc[1] should stop transfer via RTS/CTS ...
     *
     * @throws Exception
     */
    // TODO Does not work as expected ....
    @Disabled
    @Test
    public void testFillInbuffer() throws Exception {
        assumeRWTest();
        LOG.log(Level.INFO, "run testWriteSingleByteTimeout");
        // Set a high speed to speed up things
        open(Speed._115200_BPS, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_RTS_CTS());
        setTimeouts(100, 1000, 10000);

        final byte[] b = new byte[1024];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) i;
        }
        int round = 0;
        int dataWritten;
        int overallDataWritten = 0;

        do {
            try {
                writeSpc.getOutputStream().write(b);
                dataWritten = b.length;
            } catch (TimeoutIOException e) {
                dataWritten = e.bytesTransferred;
            }
            try {
                writeSpc.getOutputStream().flush();
            } catch (TimeoutIOException e) {
            }
            round++;
            overallDataWritten += dataWritten;
            LOG.log(Level.INFO,
                    "Wrote: " + dataWritten + " (" + overallDataWritten + ") in " + round + " rounds; OutBuf:  "
                    + writeSpc.getOutBufferBytesCount() + "inBuffer: " + readSpc.getInBufferBytesCount());
            if (round == 1024) {
                break;
            }
        } while (dataWritten > 0);

        LOG.log(Level.INFO, "Wrote: " + overallDataWritten + " in " + round + " rounds; OutBuf:  "
                + writeSpc.getOutBufferBytesCount());
        LOG.log(Level.INFO, "disable flow control to sped up closing");
        int dataread = 0;
        int readTotal = 0;

        do {
            try {
                dataread = readSpc.getInputStream().read(b);
                LOG.log(Level.INFO, "Read: " + dataread);
            } catch (TimeoutIOException e) {
                dataread = e.bytesTransferred;
                LOG.log(Level.INFO, "Got timeout");
            }
            readTotal += dataread;
        } while (dataread > 0);
        assertEquals(overallDataWritten, readTotal);

        writeSpc.setSpeed(Speed._500000_BPS);
        writeSpc.setFlowControl(FlowControl.getFC_NONE());
    }

    /**
     * Send 128 bytes out - the outputbuffer will hold this - so write returns
     * assuming all bytes are written. But we will only read one byte ...
     * because interbyteRead is 0
     *
     * @throws Exception
     */
    @Disabled
    @Test
    public void testTimeout() throws Exception {
        assumeRWTest();

        open(Speed._9600_BPS, DataBits.DB_8, StopBits.SB_1, Parity.EVEN, FlowControl.getFC_RTS_CTS());
        setTimeouts(0, 2000, 2000);

        assertEquals(0, readSpc.getInterByteReadTimeout());
        final InputStream is = readSpc.getInputStream();
        final OutputStream os = writeSpc.getOutputStream();

        final byte[] recBuff = new byte[255];
        Arrays.fill(recBuff, (byte) 0);

        final byte[] sendBuff = new byte[128];
        Arrays.fill(sendBuff, (byte) 0x7F);

        final Future<Object> readResult = EXECUTOR_SERVICE.submit(() -> {
            try {
                final long start = System.currentTimeMillis();
                final int i = is.read(recBuff);
                final long diff = System.currentTimeMillis() - start;
                if (i == 1) {
                    if (0x7F != recBuff[0]) {
                        return "Error @0";
                    } else if (0x00 != recBuff[1]) {
                        return "Error @1";
                    } else {
                        return true;
                    }
                } else {
                    return "Only expected 1 byte to read in " + diff + "ms but got: " + i;

                }
            } catch (IOException ex) {
                return ex;
            }

        });

        // Quick and dirty time lock to start receiver thread
        Thread.sleep(100);
        assertTimeoutPreemptively(Duration.ofMillis(5000), () -> {
            try {
                os.write(sendBuff);
                os.flush();
            } catch (InterruptedIOException e) {
                LOG.severe("Interrupted IO " + e.bytesTransferred);// TODO: handle exception
            }

            // Wait for all data to arrive...
            Object o = readResult.get(1, TimeUnit.SECONDS);
            if (o instanceof Boolean) {
            } else if (o instanceof String) {
                fail((String) o);
            } else {
                fail((Throwable) o);
            }

            Thread.sleep(200);

            assertEquals(127, is.read(recBuff, 1, 200));
        });

        for (int i = 0; i < 128; i++) {
            assertEquals(sendBuff[i], recBuff[i], "Error @" + i);
        }

    }

    @RtsCtsTest
    @Test
    public void testInfiniteTimeout() throws Exception {
        assumeRWTest();

        open(Speed._9600_BPS, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_RTS_CTS());
        final InputStream is = readSpc.getInputStream();
        final OutputStream os = writeSpc.getOutputStream();

        final byte[] recBuff = new byte[255];
        Arrays.fill(recBuff, (byte) 0);

        final byte[] sendBuff = new byte[128];
        Arrays.fill(sendBuff, (byte) 0x7F);

        assertTimeoutPreemptively(Duration.ofMillis(5000), () -> {
            os.write(sendBuff);
            os.flush();

            Thread.sleep(200);
            assertEquals(128, is.available());
            final int bytesread = is.read(recBuff);
            assertEquals(128, bytesread);
        });

        for (int i = 0; i < 128; i++) {
            assertEquals(sendBuff[i], recBuff[i], "Error @" + i);
        }

    }

    final static int WAIT_TIME = 1;

    @RtsCtsTest
    @Test
    public void testManualRTS_CTS() throws Exception {
        assumeRWTest();
        open(Speed._9600_BPS, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_NONE());

        readSpc.setRTS(false);
        Thread.sleep(WAIT_TIME);
        assertFalse(writeSpc.isCTS());
        readSpc.setRTS(true);
        Thread.sleep(WAIT_TIME);
        assertTrue(writeSpc.isCTS());

        writeSpc.setRTS(false);
        Thread.sleep(WAIT_TIME);
        assertFalse(readSpc.isCTS());
        writeSpc.setRTS(true);
        Thread.sleep(WAIT_TIME);
        assertTrue(readSpc.isCTS());
    }

    @DtrDsrTest
    @Test
    public void testManualDTR_DSR() throws Exception {
        assumeRWTest();
        open(Speed._9600_BPS, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_NONE());

        readSpc.setDTR(false);
        Thread.sleep(WAIT_TIME);
        assertFalse(writeSpc.isDSR());
        readSpc.setDTR(true);
        Thread.sleep(WAIT_TIME);
        assertTrue(writeSpc.isDSR());

        writeSpc.setDTR(false);
        Thread.sleep(WAIT_TIME);
        assertFalse(readSpc.isDSR());
        // If this fails maybe DSR and DCD are not shortend?
        assertFalse(readSpc.isDCD());
        writeSpc.setDTR(true);
        Thread.sleep(WAIT_TIME);
        assertTrue(readSpc.isDSR());
        // If this fails maybe DSR and DCD are not shortend?
        assertTrue(readSpc.isDCD());
    }

}
