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

import static de.ibapl.spsw.tests.SetupAndTeardownTests.LOG;
import de.ibapl.spsw.tests.tags.BaselineTest;
import de.ibapl.spsw.tests.tags.ByteChannelTest;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public abstract class AbstractChannelTests extends AbstractSerialPortSocketTest {

    @BaselineTest
    @ByteChannelTest
    @Test
    public void test_Channel_Write_Read_Position() throws Exception {
        assumeRWTest();
        PortConfiguration pc = new PortConfigurationFactory().ofCurrent();
        open(pc);
        ByteBuffer sendBuffer = ByteBuffer.allocateDirect(64);
        sendBuffer.put("abcdefghijklmnopqrstuvwxyz".getBytes());
        sendBuffer.flip();
        final int writeStart = 2;
        sendBuffer.position(writeStart);

        long written = writeSpc.write(sendBuffer);
        assertEquals(24, written);
        assertEquals(sendBuffer.position(), sendBuffer.limit());

        ByteBuffer recBuffer = ByteBuffer.allocateDirect(64);
        recBuffer.position(0);
        recBuffer.limit(2);

        long read = readSpc.read(recBuffer);
        assertEquals(2, read);
        assertEquals(recBuffer.position(), recBuffer.limit());
        recBuffer.flip();
        assertEquals('c', (char) recBuffer.get());
        assertEquals('d', (char) recBuffer.get());
        recBuffer.limit(recBuffer.limit() + 5);
        read = readSpc.read(recBuffer);
        assertEquals(5, read);
        assertEquals(recBuffer.position(), recBuffer.limit());
        recBuffer.flip();
        assertEquals('c', (char) recBuffer.get());
        assertEquals('d', (char) recBuffer.get());
        assertEquals('e', (char) recBuffer.get());
        assertEquals('f', (char) recBuffer.get());
        assertEquals('g', (char) recBuffer.get());
        assertEquals('h', (char) recBuffer.get());
        assertEquals('i', (char) recBuffer.get());

    }

    @BaselineTest
    @Test
    public void testRead_InfiniteReadTimeout() throws Exception {
        final int BYTES_TO_TRANSFER = 512;
        assumeRWTest();
        LOG.log(Level.INFO, "run testRead");
        openDefault();
        //Some devices have an buffer of 32 so interByteTimeout must be greater...
        readSpc.setTimeouts(readSpc.calculateMillisForCharacters(40), 0, readSpc.calculateMillisForCharacters(BYTES_TO_TRANSFER));
        assertTrue(writeSpc.isOpen());

        final ByteBuffer dst = ByteBuffer.allocateDirect(BYTES_TO_TRANSFER * 3);

        final Future<Object> readResult = EXECUTOR_SERVICE.submit(() -> {
            try {
                return readSpc.read(dst);
            } catch (Throwable t) {
                return t;
            }
        });

        final ByteBuffer src = ByteBuffer.allocateDirect(BYTES_TO_TRANSFER * 2);
        for (int i = 0; i < BYTES_TO_TRANSFER; i++) {
            src.put((byte) i);
        }
        src.flip();

        writeSpc.write(src);

        Assertions.assertEquals(BYTES_TO_TRANSFER, src.position(), "BYTES_TO_TRANSFER != src.position()");

        Object o = readResult.get(2, TimeUnit.SECONDS);
        if (o instanceof Integer) {
            int transferred = (Integer) o;
            Assertions.assertEquals(BYTES_TO_TRANSFER, transferred, "BYTES_TO_TRANSFER != (Integer)readResult");
            Assertions.assertEquals(BYTES_TO_TRANSFER, dst.position(), "BYTES_TO_TRANSFER != dst.position()");
        } else {
            Assertions.fail("readResult is not an Integer but: " + o.getClass());
        }

    }

    @BaselineTest
    @Test
    public void testRead_FiniteReadTimeout() throws Exception {
        final int BYTES_TO_TRANSFER = 512;
        assumeRWTest();
        LOG.log(Level.INFO, "run testRead");
        openDefault();
        //Some devices have an buffer of 32 so interByteTimeout must be greater...
        readSpc.setTimeouts(readSpc.calculateMillisForCharacters(40), readSpc.calculateMillisForCharacters(BYTES_TO_TRANSFER * 2), readSpc.calculateMillisForCharacters(BYTES_TO_TRANSFER));
        assertTrue(writeSpc.isOpen());

        final ByteBuffer dst = ByteBuffer.allocateDirect(BYTES_TO_TRANSFER * 3);

        final Future<Object> readResult = EXECUTOR_SERVICE.submit(() -> {
            try {
                return readSpc.read(dst);
            } catch (Throwable t) {
                return t;
            }
        });

        final ByteBuffer src = ByteBuffer.allocateDirect(BYTES_TO_TRANSFER * 2);
        for (int i = 0; i < BYTES_TO_TRANSFER; i++) {
            src.put((byte) i);
        }
        src.flip();

        writeSpc.write(src);

        Assertions.assertEquals(BYTES_TO_TRANSFER, src.position(), "BYTES_TO_TRANSFER != src.position()");

        Object o = readResult.get(2, TimeUnit.SECONDS);
        if (o instanceof Integer) {
            int transferred = (Integer) o;
            Assertions.assertEquals(BYTES_TO_TRANSFER, transferred, "BYTES_TO_TRANSFER != (Integer)readResult");
            Assertions.assertEquals(BYTES_TO_TRANSFER, dst.position(), "BYTES_TO_TRANSFER != dst.position()");
        } else {
            Assertions.fail("readResult is not an Integer but: " + o.getClass());
        }

    }
}
