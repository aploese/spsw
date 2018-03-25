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
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.jniprovider.SerialPortSocketFactoryImpl;

public class SendDataSingleMain {

	public static void main(String[] args) throws Exception {
		SerialPortSocket serialPortSocket = new SerialPortSocketFactoryImpl()
				.createSerialPortSocket("/dev/ttyUSB0");
		serialPortSocket.open(Speed._300_BPS, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_NONE());
		serialPortSocket.setTimeouts(1000, 0, 0);
		Thread t = new Thread(() -> {
			final DateTimeFormatter dtf = DateTimeFormatter.ISO_INSTANT;
			try {
				while (true) {
					int received = serialPortSocket.getInputStream().read();
					char data = (char) received;
					if (received > 0) {
						System.out.println(dtf.format(Instant.now()) + " DataReceived: \""
								+ (data == '\\' ? "\\\\" : data) + "\"");
					}
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		});
		t.start();
		long i = 0;
		while (true) {
			String s = String.format("%10d The quick brown fox jumps over the lazy dog\n", i++);
			byte[] data = s.getBytes();
			Thread.sleep(1000);
			for (int j = 0; j < data.length; j++)
				serialPortSocket.getOutputStream().write(data[j]);
			System.out.println("DataSend: " + s);
		}
		// serialPortSocket.close();
	}

}
