/*
 * SPSW - Drivers for the serial port, https://github.com/aploese/spsw/
 * Copyright (C) 2009-2019, Arne Plöse and individual contributors as indicated
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
package de.ibapl.spsw.testsjni;

import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.jniprovider.SerialPortSocketFactoryImpl;
import java.nio.ByteBuffer;

/**
 *
 * @author Arne Plöse
 *
 */
public class SendDataSingleMain {

    final static int BUFFER_SIZE = 512;

    public static void main(String[] args) throws Exception {
        final ByteBuffer sendBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        while (sendBuffer.hasRemaining()) {
            sendBuffer.put((byte) sendBuffer.position());
        }
        sendBuffer.flip();

        try (SerialPortSocket serialPortSocket = new SerialPortSocketFactoryImpl().open("/dev/ttyUSB0", Speed._9600_BPS, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_NONE())) {
            serialPortSocket.setTimeouts(100, 0, 0);
            Thread thread = new Thread(() -> {
                final ByteBuffer recBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
                byte currentData = 0;
                try {
                    while (true) {
                        serialPortSocket.read(recBuffer);
                        while (recBuffer.hasRemaining()) {
                            if (currentData != recBuffer.get()) {
                                throw new RuntimeException("REC wrong");
                            }
                            currentData++;
                        }
                        recBuffer.flip();
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    System.err.println(throwable);
                    System.err.flush();
                    System.exit(-1);
                }
            });
            thread.start();
            long i = 0;
            final long start = System.currentTimeMillis() - 1;
            while (true) {
                try {
                    serialPortSocket.write(sendBuffer);
                    sendBuffer.flip();
                    i++;
                    System.out.println("bit per s: " + ((i * BUFFER_SIZE * 1000L * 8) / (System.currentTimeMillis() - start)));
                    System.out.flush();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    System.err.println(throwable);
                    System.err.flush();
                    System.exit(-1);
                }
            }
        } finally {
            System.err.println("ERROR");
            System.err.flush();
        }
    }

}
