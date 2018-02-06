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
public class Main {

    final static String[] port = new String[] {"/dev/ttyUSB0", "/dev/ttyUSB1"};
    final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_INSTANT;
    
    static class Sender implements Runnable {

        SerialPortSocket serialPort = SerialPortSocketFactoryImpl.singleton().createSerialPortSocket(port[0]);

        @Override
        public void run() {
            try {
                serialPort.openRaw(Baudrate.B110, DataBits.DB_8, StopBits.SB_2, Parity.EVEN, FlowControl.getFC_NONE());
                final OutputStream os = serialPort.getOutputStream();
                        os.write(0xFF);
                    System.err.println("Write End @" + dateTimeFormatter.format(Instant.now()) + " IBC" + serialPort.getInBufferBytesCount());
            } catch (IOException ioe) {
                System.out.println("de.ibapl.spsw.provider.tests.Main.Sender.run() " + ioe);

            }

        }
    }

    public static void main(String[] args) throws Exception {
        SerialPortSocket serialPort = SerialPortSocketFactoryImpl.singleton().createSerialPortSocket(port[1]);
        serialPort.openRaw(Baudrate.B110, DataBits.DB_8, StopBits.SB_2, Parity.EVEN, FlowControl.getFC_NONE());

        System.err.println("InterByteReadTimeout: " + serialPort.getInterByteReadTimeout());
        System.err.println("OverallReadTimeout: " + serialPort.getOverallReadTimeout());
        final OutputStream os = serialPort.getOutputStream();
        final InputStream is = serialPort.getInputStream();

        Thread t = new Thread(new Sender());
        t.setDaemon(false);
        t.start();

        while (true) {
            
            try {
                System.err.println("ReadStart @" + dateTimeFormatter.format(Instant.now()) + " IBC: " + is.available());
                Thread.sleep(10);
            } catch (Exception iioe) {
            }
        }
    }
}
