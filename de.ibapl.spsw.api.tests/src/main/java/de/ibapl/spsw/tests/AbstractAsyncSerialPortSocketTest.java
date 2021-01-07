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

import de.ibapl.spsw.api.AsyncSerialPortSocket;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.SerialPortSocketFactory;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;
import static de.ibapl.spsw.tests.SetupAndTeardownTests.LOG;
import de.ibapl.spsw.wrapper.AsyncSerialPortSocketThreadPoolWrapper;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.logging.Level;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author aploese
 */
public class AbstractAsyncSerialPortSocketTest extends SetupAndTeardownTests<AsyncSerialPortSocket> {

    protected void openDefault() throws IOException {
        openAsync(Speed._9600_BPS, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_NONE());
    }

    /**
     * Opens the port and make sure that In and out buffer are empty
     *
     * @param speed
     * @param dataBits
     * @param stopBits
     * @param parity
     * @param flowControl
     */
    protected void openAsync(Speed speed, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControl)
            throws IOException {
        final SerialPortSocketFactory factory = getSerialPortSocketFactory();
        if (readSerialPortName != null) {
            try {
                readSpc = wrapReadSpc(factory.openAsync(readSerialPortName, speed, dataBits, stopBits, parity, flowControl, EXECUTOR_SERVICE));
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Error during readSpc.open(" + speed + ", " + dataBits + ", " + stopBits + ", " + parity + ", " + flowControl + ");", e);
                try {
                    readSpc = wrapReadSpc(new AsyncSerialPortSocketThreadPoolWrapper(factory.open(readSerialPortName, speed, dataBits, stopBits, parity, flowControl), EXECUTOR_SERVICE));
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, "Error during readSpc.open(" + speed + ", " + dataBits + ", " + stopBits + ", " + parity + ", " + flowControl + ");", ex);
                    throw ex;
                }
            }
            assertEquals(0, readSpc.getOutBufferBytesCount(), "Can't start test: OutBuffer is not empty");
            while (readSpc.getInBufferBytesCount() > 0) {
                readSpc.readAsync(ByteBuffer.allocateDirect(writeSpc.getInBufferBytesCount()));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    throw new RuntimeException(ie);
                }
            }
            assertEquals(0, readSpc.getInBufferBytesCount(), "Can't start test: InBuffer is not empty");
//No wrapper			readSpc = LoggingSerialPortSocket.wrapWithHexOutputStream(readSpc, new FileOutputStream("/tmp/test_serial.txt"), true, TimeStampLogging.FROM_OPEN);
        }
        if (writeSerialPortName != null) {
            if (writeSerialPortName.equals(readSerialPortName)) {
                writeSpc = readSpc;
            } else {
                try {
                    writeSpc = wrapWriteSpc(factory.openAsync(writeSerialPortName, speed, dataBits, stopBits, parity, flowControl, EXECUTOR_SERVICE));
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Error during writeSpc.open(" + speed + ", " + dataBits + ", " + stopBits + ", " + parity + ", " + flowControl + ");", e);
                try {
                    writeSpc = wrapWriteSpc(new AsyncSerialPortSocketThreadPoolWrapper(factory.open(writeSerialPortName, speed, dataBits, stopBits, parity, flowControl), EXECUTOR_SERVICE));
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, "Error during writeSpc.open(" + speed + ", " + dataBits + ", " + stopBits + ", " + parity + ", " + flowControl + ");", ex);
                    throw ex;
                }
                }
                assertEquals(0, writeSpc.getOutBufferBytesCount(), "Can't start test: OutBuffer is not empty");
                while (writeSpc.getInBufferBytesCount() > 0) {
                    writeSpc.readAsync(ByteBuffer.allocateDirect(writeSpc.getInBufferBytesCount()));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ie) {
                        throw new RuntimeException(ie);
                    }
                }
                assertEquals(0, writeSpc.getInBufferBytesCount(), "Can't start test: InBuffer is not empty");
            }
        }

    }

    protected void openAsync(PortConfiguration pc) throws Exception {
        openAsync(pc.getSpeed(), pc.getDataBits(), pc.getStopBits(), pc.getParity(), pc.getFlowControl());
        setTimeouts(pc.getInterByteReadTimeout(), pc.getOverallReadTimeout(), pc.getOverallWriteTimeout());
    }

    /*
    private void cachAsyncSpc() throws IOException {
        if (readSpc != null) {
            if (readSpc instanceof AsyncSerialPortSocket) {
                asyncReadSpc = (AsyncSerialPortSocket) readSpc;
            } else {
                asyncReadSpc = new AsyncSerialPortSocketThreadPoolWrapper(readSpc);
            }
            if (writeSpc == readSpc) {
                asyncWriteSpc = asyncReadSpc;
            } else {
                if (writeSpc instanceof AsyncSerialPortSocket) {
                    asyncWriteSpc = (AsyncSerialPortSocket) writeSpc;
                } else {
                    asyncWriteSpc = new AsyncSerialPortSocketThreadPoolWrapper(writeSpc);
                }
            }
        };
    }
     */
}
