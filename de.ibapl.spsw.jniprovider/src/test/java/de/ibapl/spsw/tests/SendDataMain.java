/*-
 * #%L
 * SPSW Provider
 * %%
 * Copyright (C) 2009 - 2018 Arne Plöse
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
package de.ibapl.spsw.tests;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.SerialPortSocketFactory;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.api.TimeoutIOException;
import de.ibapl.spsw.jniprovider.SerialPortSocketFactoryImpl;

public class SendDataMain {

	public static void main(String[] args) throws Exception {
		final Object printLock = new Object();
		final SerialPortSocketFactory spsf = new SerialPortSocketFactoryImpl();
		try (SerialPortSocket serialPortSocket = spsf.open("/dev/ttyUSB0", Speed._230400_BPS, DataBits.DB_8, StopBits.SB_1,
				Parity.NONE, FlowControl.getFC_NONE())) {
			serialPortSocket.setTimeouts(200, 1000, 1000);
			Thread t = new Thread(() -> {
				final DateTimeFormatter dtf = DateTimeFormatter.ISO_INSTANT;
				byte data[] = new byte[256];
				while (true) {
					try {
						int received = serialPortSocket.getInputStream().read(data);
						if (received > 0) {
							char chars[] = new char[received];
							for (int i = 0; i < received; i++) {
								chars[i] = (char) data[i];
							}
							synchronized (printLock) {
								System.out.println(dtf.format(Instant.now()) + " DataReceived: >>>\n"
										+ String.copyValueOf(chars).replace("\"", "\\\"") + "\n<<<");
							}
						}
					} catch (Exception e) {
						synchronized (printLock) {
							System.err.println(e.getMessage());
						}
					}
				}
			});
			t.start();
			long i = 0;
			while (true) {
				String s = String.format("%10d The quick brown fox jumps over the lazy dog\n", i++);
				Thread.sleep(100);
				try {
					serialPortSocket.getOutputStream().write(s.getBytes());
				} catch (TimeoutIOException e) {
					synchronized (printLock) {
						System.err.println("DataSend: Could only write " + e.bytesTransferred + " bytes");
					}
					while (serialPortSocket.getOutBufferBytesCount() > 0) {
						Thread.sleep(10);
					}
					serialPortSocket.getOutputStream().write(s.getBytes(), e.bytesTransferred,
							s.length() - e.bytesTransferred);
				}
				synchronized (printLock) {
					System.out.println("DataSend: " + s);
				}
			}
		}
	}
}
