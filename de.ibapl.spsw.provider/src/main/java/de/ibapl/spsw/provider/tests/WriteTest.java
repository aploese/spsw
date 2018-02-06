package de.ibapl.spsw.provider.tests;


/*-
 * #%L
 * FHZ4J Console
 * %%
 * Copyright (C) 2009 - 2017 Arne Plöse
 * %%
 * SPSW - Drivers for the serial port, https://github.com/aploese/spsw/
 * Copyright (C) 2009-2018, Arne Plöse and individual contributors as indicated
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
import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.provider.GenericTermiosSerialPortSocket;
import de.ibapl.spsw.provider.SerialPortSocketFactoryImpl;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.InterruptedByTimeoutException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * DOCUMENT ME!
 *
 * @author aploese
 */
public class WriteTest {

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_INSTANT;
        SerialPortSocket serialPort = SerialPortSocketFactoryImpl.singleton().createSerialPortSocket("/dev/ttyUSB0");
        System.err.println("Port aquired: " + (System.currentTimeMillis() - startTime) + "ms");
        serialPort.openRaw(Baudrate.B19200, DataBits.DB_8, StopBits.SB_2, Parity.EVEN, FlowControl.getFC_RTS_CTS());
        System.err.println("Port opend: " + (System.currentTimeMillis() - startTime) + "ms\n"+ serialPort);

        final OutputStream os = serialPort.getOutputStream();

        for (int i = 0; i < 1024; i++) {
            try {
                startTime = System.currentTimeMillis();
                os.write(i);
                System.err.println("Write (" + i + ") took: \t" + (System.currentTimeMillis() - startTime) + "ms " + serialPort.getOutBufferBytesCount() + " Bytes in Out Buffer");
            } catch (IOException ioe) {
                System.err.println("EX: " + ioe);
                serialPort.close();
                System.err.println("Port Closed: " + ioe);
                return;
            }
            try {
                Thread.sleep(100);
                System.err.println("BiOB: "+ serialPort.getOutBufferBytesCount());
                startTime = System.currentTimeMillis();
                os.flush();
                System.err.println("Flush (" + i + ") took: \t" + (System.currentTimeMillis() - startTime) + "ms " + serialPort.getOutBufferBytesCount() + " Bytes in Out Buffer");
            } catch (IOException ioe) {
                System.err.println("EX: " + ioe);
                serialPort.close();
                System.err.println("Port Closed: " + ioe);
                return;
            }
        }
    }
}
