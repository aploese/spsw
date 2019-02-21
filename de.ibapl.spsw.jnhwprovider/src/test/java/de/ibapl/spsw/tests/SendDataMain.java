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
package de.ibapl.spsw.tests;

import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.SerialPortSocketFactory;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.api.TimeoutIOException;
import de.ibapl.spsw.jnhwprovider.SerialPortSocketFactoryImpl;

/**
 * 
 * @author Arne Plöse
 *
 */
public class SendDataMain {

	public static void main(String[] args) throws Exception {
		final boolean readSingle = false;
		final boolean writeSingle = false;
		final Object readLock = new Object();
		//final Object writeLock = readLock; 
		final Object writeLock = new Object();

		final Object printLock = new Object();
		final SerialPortSocketFactory spsf = new SerialPortSocketFactoryImpl();
		try (SerialPortSocket serialPortSocket = spsf.open("/dev/ttyUSB0", Speed._115200_BPS, DataBits.DB_8,
				StopBits.SB_1, Parity.NONE, FlowControl.getFC_NONE())) {

			// spsf.open("/dev/ttyUSB1", serialPortSocket.getSpeed(),
			// serialPortSocket.getDatatBits(),serialPortSocket.getStopBits(),
			// serialPortSocket.getParity(), serialPortSocket.getFlowControl()).close();

			serialPortSocket.setTimeouts(200, 1000, 1000);
			Thread receiver = new Thread(() -> {
				byte data[] = new byte[256];
				while (true) {
					try {
						if (readSingle) {
							int received;
							synchronized (readLock) {
								received = serialPortSocket.getInputStream().read();
							}
							if (received > 0) {
								System.out.print((char) received);
							}
						} else {
							System.err.print("\nread bytes");
							int received;
							synchronized (readLock) {
								received = serialPortSocket.getInputStream().read(data);
							}
							System.err.println(" => bytes read");
							if (received > 0) {
								char chars[] = new char[received];
								for (int i = 0; i < received; i++) {
									chars[i] = (char) data[i];
								}
								System.out.print(chars);
							}
						}
					} catch (TimeoutIOException tioe) {
						Thread.yield();
						System.err.println(" => timeout");
						// ignore
					} catch (Exception e) {
						synchronized (printLock) {
							System.err.println(e.getMessage());
							e.printStackTrace(System.err);
						}
					}
				}
			});
			receiver.start();
			Thread transmitter = new Thread(() -> {
				try {
					long i = 0;
					while (true) {
						String s = String.format("%10d The quick brown fox jumps over the lazy dog\n", i++);
						Thread.sleep(50);
						if (writeSingle) {
							synchronized (writeLock) {
								for (byte b : s.getBytes()) {
									serialPortSocket.getOutputStream().write(b);
								}
							}
						} else {
							System.err.print("\nwrite bytes");
							synchronized (writeLock) {
								serialPortSocket.getOutputStream().write(s.getBytes());
							}
							System.err.println(" => bytes written");
						}
					}
				} catch (Exception e) {
					synchronized (printLock) {
						System.err.println(e.getMessage());
						e.printStackTrace(System.err);
					}
				}
			});
			transmitter.start();
			System.in.read();
			System.err.println("FINISHED");
		}
	}
}
