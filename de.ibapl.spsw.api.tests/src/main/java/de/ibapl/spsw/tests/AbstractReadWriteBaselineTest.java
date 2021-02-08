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
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import de.ibapl.spsw.tests.tags.BaselineTest;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Arne Plöse
 */
public abstract class AbstractReadWriteBaselineTest extends AbstractReadWriteTest {

    /**
     * Send two bytes with half the InterbyteReadTimeout and expect a return
     * from read within OverallReadTimeout - InterbyteReadTimeout
     *
     * @throws Exception
     */
    @BaselineTest
    @Test
    public void testInterbyteReadTimeout() throws Exception {
        openDefault();
        final byte[] sendData = new byte[]{12, 15};
        final byte[] data = new byte[sendData.length * 2];

        readSpc.setTimeouts(200, 1000, 2000);
        final Future<Object> readResult = EXECUTOR_SERVICE.submit(() -> {
            try {
                return readSpc.getInputStream().read(data);
            } catch (Exception e) {
                return e;
            }
        });
        Thread.sleep(100);

        writeSpc.getOutputStream().write(sendData[0]);
        Thread.sleep(readSpc.getInterByteReadTimeout() / 2);
        writeSpc.getOutputStream().write(sendData[1]);
        Object o = readResult.get(readSpc.getOverallReadTimeout(), TimeUnit.MILLISECONDS);
        if (o instanceof Integer) {
            assertEquals(sendData.length, (Integer) o);
            assertEquals(sendData[0], data[0]);
            assertEquals(sendData[1], data[1]);
        } else {
            fail((Throwable) o);
        }
    }
}
