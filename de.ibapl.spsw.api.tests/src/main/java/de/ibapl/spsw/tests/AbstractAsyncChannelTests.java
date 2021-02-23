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

import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.tests.tags.BaselineTest;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.time.Duration;
import java.util.LinkedList;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 *
 * @author aploese
 */
public abstract class AbstractAsyncChannelTests extends AbstractAsyncSerialPortSocketTest {

    public static void main(String[] args) {

    }

    public AbstractAsyncChannelTests() {
    }

    @BaselineTest
    @Test
    public void testRead() throws Exception {
        final int BYTES_TO_TRANSFER = 512;
        assumeRWTest();
        LOG.log(Level.INFO, "run testRead");
        openDefault();
        //TODO wait only for specific jni provider does not work with wait forever??? bug?
        //readSpc.setTimeouts(50, 0, readSpc.calculateMillisForCharacters(BYTES_TO_TRANSFER));
        assertTrue(writeSpc.isOpen());

        final ByteBuffer dst = ByteBuffer.allocateDirect(BYTES_TO_TRANSFER * 3);

        final LinkedList readResult = new LinkedList();

        readSpc.readAsync(dst, (buff) -> {
            synchronized (readResult) {
                readResult.add(buff.position());
                readResult.notifyAll();
            }
        }, (error) -> {
            synchronized (readResult) {
                readResult.add(error);
                readResult.notifyAll();
            }
        });

        final ByteBuffer src = ByteBuffer.allocateDirect(BYTES_TO_TRANSFER * 2);
        for (int i = 0; i < BYTES_TO_TRANSFER; i++) {
            src.put((byte) i);
        }
        src.flip();

        final LinkedList writeResult = new LinkedList();

        writeSpc.writeAsync(src, (buff) -> {
            synchronized (writeResult) {
                writeResult.add(buff.position());
                writeResult.notifyAll();
            }
        }, (error) -> {
            synchronized (writeResult) {
                writeResult.add(error);
                writeResult.notifyAll();
            }
        });

        synchronized (writeResult) {
            if (writeResult.isEmpty()) {
                writeResult.wait(2000);
            }
        }

        Assertions.assertEquals(1, writeResult.size());
        Assertions.assertEquals(Integer.class, writeResult.getFirst().getClass());
        Assertions.assertEquals(BYTES_TO_TRANSFER, writeResult.getFirst());
        Assertions.assertEquals(BYTES_TO_TRANSFER, src.position());

        synchronized (readResult) {
            if (readResult.isEmpty()) {
                Thread.yield();
                readResult.wait(2000);
            }
        }

        Assertions.assertEquals(1, readResult.size());
        Assertions.assertEquals(Integer.class, readResult.getFirst().getClass());
        Assertions.assertEquals(BYTES_TO_TRANSFER, readResult.getFirst());
        Assertions.assertEquals(BYTES_TO_TRANSFER, dst.position());

    }

    @BaselineTest
    //@Test
    //We need to really cancel the write ... currently not implememnted
    public void testWriteCancel() throws Exception {
        final int BYTES_TO_TRANSFER = 1024 * 1024 * 4;
        assumeRWTest();
        LOG.log(Level.INFO, "run testWriteCancel");
        openDefault();
        assertTrue(writeSpc.isOpen());

        final ByteBuffer dst = ByteBuffer.allocateDirect(BYTES_TO_TRANSFER * 3);

        final LinkedList readResult = new LinkedList();

        readSpc.readAsync(dst, (buff) -> {
            synchronized (readResult) {
                readResult.add(buff.position());
                readResult.notifyAll();
            }
        }, (error) -> {
            synchronized (readResult) {
                readResult.add(error);
                readResult.notifyAll();
            }
        });

        final ByteBuffer src = ByteBuffer.allocateDirect(BYTES_TO_TRANSFER * 2);
        for (int i = 0; i < BYTES_TO_TRANSFER; i++) {
            src.put((byte) i);
        }
        src.flip();

        Future<ByteBuffer> result = writeSpc.writeAsync(src);
        result.cancel(true);

        CancellationException ce = assertThrows(CancellationException.class, () -> {
            result.get();
        });

        Assertions.assertTrue(src.position() < BYTES_TO_TRANSFER);

        synchronized (readResult) {
            if (readResult.isEmpty()) {
                readResult.wait(10000);
            }
        }

        if (readResult.size() == 1) {
            Assertions.assertEquals(Integer.class, readResult.getFirst().getClass());
        }
        Assertions.assertTrue(dst.position() < BYTES_TO_TRANSFER);
        Assertions.assertEquals(src.position(), dst.position());

    }

    @BaselineTest
    @Test
    public void testWriteAsync_ByteBuffer() throws Exception {
        final int BYTES_TO_TRANSFER = 1024;
        assumeWTest();
        LOG.log(Level.INFO, "run testWriteAsync");
        openDefault();
        assertTrue(writeSpc.isOpen());
        final ByteBuffer buf = ByteBuffer.allocateDirect(BYTES_TO_TRANSFER);
        while (buf.hasRemaining()) {
            buf.put((byte) buf.position());
        }
        buf.flip();

        final LinkedList result = new LinkedList();

        writeSpc.writeAsync(buf, (buff) -> {
            synchronized (result) {
                result.add(buff.position());
                result.notifyAll();
            }
        }, (error) -> {
            synchronized (result) {
                result.add(error);
                result.notifyAll();
            }
        });

        synchronized (result) {
            if (result.isEmpty()) {
                result.wait(2000);
            }
        }
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(Integer.class, result.getFirst().getClass());
        Assertions.assertEquals(BYTES_TO_TRANSFER, result.getFirst());
        Assertions.assertEquals(BYTES_TO_TRANSFER, buf.position());
    }

    //TODO @BaselineTest
    @Test
    @Disabled
    public void testCloseDuringBytesWrite() throws Exception {
        assumeWTest();
        LOG.log(Level.INFO, "run testCloseDuringBytesRead");
        openAsync(Speed._230400_BPS, DataBits.DB_8, StopBits.SB_1, Parity.EVEN, FlowControl.getFC_NONE());
        int len = (int) Math.ceil(2000.0 / writeSpc.calculateMillisPerCharacter());

        byte b[] = new byte[len];
        assertTrue(writeSpc.isOpen());

        final Future<Boolean> isClosed = EXECUTOR_SERVICE.submit(() -> {
            try {
                Thread.sleep(100);
                LOG.log(Level.INFO, "Will now close the port: {0}", writeSpc);
                writeSpc.close();
                LOG.log(Level.INFO, "Port is closed");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return !writeSpc.isOpen();
        });

        AsynchronousCloseException ace = assertThrows(AsynchronousCloseException.class, () -> {
            ByteBuffer out = ByteBuffer.allocateDirect(1024 * 1024);
            assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
                LOG.log(Level.INFO, "Will write {0} bytes in one second to fill the output buffer", len);
                //Loop to fill the outputbuffer until it blocks...
                while (true) {
                    out.position(0);
                    out.limit(out.capacity());
                    writeSpc.writeAsync(out, (buf, ioEx) -> {
                        if (ioEx == null) {
                            LOG.log(Level.SEVERE, "Unexpected Written: {0} bytes", buf.position());
                        }
                    });
                }
            });
        });
        LOG.log(Level.INFO, "Port closed msg: {0}", ace.getMessage());

        assertFalse(isClosed.get(), "Port was not closed!");

        assertFalse(writeSpc.isOpen(), "Port was not closed!");
        // Allow 200ms to recover on win the next executed test may fail with port busy
        // otherwise (FTDI on win)
        Thread.sleep(PORT_RECOVERY_TIME_MS);
    }

}
