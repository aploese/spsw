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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.jniprovider.SerialPortSocketFactoryImpl;

public class PrintPortCapabillitiesMain {

	public static void main(String[] args) throws Exception {
		String serialPortName;
		try (InputStream is = PrintPortCapabillitiesMain.class.getClassLoader()
				.getResourceAsStream("junit-spsw-config.properties")) {
			if (is == null) {
				serialPortName = null;
			} else {
				Properties p = new Properties();
				p.load(is);
				serialPortName = p.getProperty("port0");
			}
		}
		SerialPortSocket serialPortSocket = new SerialPortSocketFactoryImpl().createSerialPortSocket(serialPortName);
		serialPortSocket.open();
		System.out.println("Use device: " + serialPortSocket.getPortName());
		System.out.println(String.format("%-20s%-20s%-20s%-20s", "Speed", "DataBits", "StopBits", "Parity"));

		for (Speed br : Speed.values()) {
			for (DataBits db : DataBits.values()) {
				for (StopBits sb : StopBits.values()) {
					for (Parity p : Parity.values()) {
						try {
							serialPortSocket.setSpeed(br);
						} catch (IllegalArgumentException e) {
							System.err.println("Can't set Speed to: " + br);
						} catch (IOException spe) {
							System.err.println("Error: set Speed to: " + br);
							System.err.println(spe);
						}
						try {
							serialPortSocket.setDataBits(db);
						} catch (IllegalArgumentException e) {
							System.err.println("Can't set DataBits to: " + db);
						} catch (IOException spe) {
							System.err.println("Error: set DataBits to: " + db);
							System.err.println(spe);
						}
						try {
							serialPortSocket.setStopBits(sb);
						} catch (IllegalArgumentException e) {
							System.err.println("Can't set StopBits to: " + sb);
						} catch (IOException spe) {
							System.err.println("Error: set StopBits to: " + sb);
							System.err.println(spe);
						}
						try {
							serialPortSocket.setParity(p);
						} catch (IllegalArgumentException e) {
							System.err.println("Can't set Parity to: " + p);
						} catch (IOException spe) {
							System.err.println("Error: set Parity to: " + p);
							System.err.println(spe);
							Thread.sleep(10);
						}
						printPort(serialPortSocket);
					}
				}
				// @5 Databis 1 or 1.5 Stopbits otherwise only 1 or 2 Stopbits...
				try {
					serialPortSocket.setStopBits(StopBits.SB_1);
				} catch (Exception e) {
					System.err.println("Error: set StopBits to: " + StopBits.SB_1);
					System.err.println(e);
				}
			}
		}
		serialPortSocket.close();
	}

	private static void printPort(SerialPortSocket sPort) throws IOException {
		System.out.println(String.format("%-20d%-20d%-20f%-20s", sPort.getSpeed().value, sPort.getDatatBits().value,
				sPort.getStopBits().value, sPort.getParity().name()));
	}

}
