package de.ibapl.spsw.provider.tests;


/*-
 * #%L
 * FHZ4J Console
 * %%
 * Copyright (C) 2009 - 2017 Arne Plöse
 * %%
 * FHZ4J - Drivers for the Wireless FS20, FHT and HMS protocol https://github.com/aploese/fhz4j/
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

    static class Sender implements Runnable {

        SerialPortSocket serialPort = SerialPortSocketFactoryImpl.singleton().createSerialPortSocket("COM3");

        @Override
        public void run() {
            try {
                serialPort.openRaw(Baudrate.B9600, DataBits.DB_8, StopBits.SB_2, Parity.EVEN, FlowControl.getFC_NONE());
                final OutputStream os = serialPort.getOutputStream();

                while (true) {
                    String s = "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                    os.write("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX".getBytes());
                    os.flush();
                    for (char c : s.toCharArray()) {
                        os.write(c);
                        os.flush();
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ie) {

                        }
                    }
                }

            } catch (IOException ioe) {
                System.out.println("de.ibapl.spsw.provider.tests.Main.Sender.run() " + ioe);

            }

        }
    }

    public static void main(String[] args) throws Exception {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_INSTANT;
        SerialPortSocket serialPort = SerialPortSocketFactoryImpl.singleton().createSerialPortSocket("COM5");
        serialPort.openRaw(Baudrate.B9600, DataBits.DB_8, StopBits.SB_2, Parity.EVEN, FlowControl.getFC_NONE());

        serialPort.setReadTimeouts(0, 0);

        System.err.println("InterByteReadTimeout: " + serialPort.getInterByteReadTimeout());
        System.err.println("OverallReadTimeout: " + serialPort.getOverallReadTimeout());
        final OutputStream os = serialPort.getOutputStream();
        final InputStream is = serialPort.getInputStream();

        os.write("\r\n".getBytes());
        os.flush();
        Thread.sleep(100);

        os.write("X21\r\n".getBytes());
        Sender s = new Sender();
        Thread t = new Thread(s);
        t.setDaemon(false);
        t.start();

        byte[] buffer = new byte[64];
        int count;
        long startTime = 0;
        Instant startTS = null;
        boolean timeout = false;
        while (true) {
            try {
                startTime = System.currentTimeMillis();
                startTS = Instant.now();
                if (!timeout) {
                    //System.err.print("ReadStart @" + dateTimeFormatter.format(startTS));
                }
                count = is.read(buffer);
                if (timeout) {
                    System.err.print("ReadStart @" + dateTimeFormatter.format(startTS));
                    timeout = false;
                }
                if (count > 0) {
                    System.err.print("Data @ " + dateTimeFormatter.format(Instant.now()) + ">>");
                    for (int i = 0; i < count; i++) {
                        System.err.print((char) buffer[i]);
                    }
                    System.err.println("<< " + (System.currentTimeMillis() - startTime));
                } else {
                    //System.err.println("NODATA" + (System.currentTimeMillis() - startTime));
                }

            } catch (InterruptedIOException iioe) {
                if (!timeout) {
                    System.err.println("\n.");
                }
                timeout = true;
                System.err.println('.');
                //System.err.println("Timeout @" + dateTimeFormatter.format(Instant.now()) + " " + (System.currentTimeMillis() - startTime));
            }
        }
    }
}
