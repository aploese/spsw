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
package de.ibapl.spsw.api;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Factory class to create an instance of {@link SerialPortSocket}. To get an
 * instance of this:
 * 
 * <pre>
 * {
 * 	&#64;code
 * 	SerialPortSocketFactory spsf;
 * 	ServiceLoader<SerialPortSocketFactory> spsFactoryLoader = ServiceLoader.load(SerialPortSocketFactory.class);
 * 	Iterator<SerialPortSocketFactory> spsFactoryIterator = spsFactoryLoader.iterator();
 * 	if (spsFactoryIterator.hasNext()) {
 * 		spsf = spsFactoryIterator.next();
 * 	}
 * 	if (spsFactoryIterator.hasNext()) {
 * 		// Handle the second factory
 * 	}
 * }
 * </pre>
 * 
 * @author Arne Plöse
 */
@ProviderType()
public interface SerialPortSocketFactory {

	/**
	 * {@value #DEFAULT_LINUX_DEVICE_PATH}
	 */
	public final String DEFAULT_LINUX_DEVICE_PATH = "/dev/";
	/**
	 * {@value #DEFAULT_LINUX_PORTNAME_PATTERN}
	 */
	public final String DEFAULT_LINUX_PORTNAME_PATTERN = "(ttyS|ttyUSB|ttyACM|ttyAMA|rfcomm|ttyO)[0-9]{1,3}";
	/**
	 * {@value #DEFAULT_FREE_BSD_DEVICE_PATH}
	 */
	public final String DEFAULT_FREE_BSD_DEVICE_PATH = "/dev/";
	/**
	 * {@value #DEFAULT_FREE_BSD_PORTNAME_PATTERN}
	 */
	public final String DEFAULT_FREE_BSD_PORTNAME_PATTERN = "(cua|cuaU)[0-9]{1,3}";
	/**
	 * {@value #DEFAULT_MACOS_DEVICE_PATH}
	 */
	public final String DEFAULT_MACOS_DEVICE_PATH = "/dev/";
	/**
	 * {@value #DEFAULT_MACOS_PORTNAME_PATTERN}
	 */
	public final String DEFAULT_MACOS_PORTNAME_PATTERN = "tty.(serial|usbserial|usbmodem).*";
	/**
	 * {@value #DEFAULT_SUNOS_DEVICE_PATH}
	 */
	public final String DEFAULT_SUNOS_DEVICE_PATH = "/dev/term/";
	/**
	 * {@value #DEFAULT_SUNOS_PORTNAME_PATTERN}
	 */
	public final String DEFAULT_SUNOS_PORTNAME_PATTERN = "[0-9]*|[a-z]*";
	/**
	 * {@value #DEFAULT_WINDOWS_DEVICE_PATH}
	 */
	public final String DEFAULT_WINDOWS_DEVICE_PATH = "";
	/**
	 * {@value #DEFAULT_WINDOWS_PORTNAME_PATTERN}
	 */
	public final String DEFAULT_WINDOWS_PORTNAME_PATTERN = "(COM)[0-9]{1,3}";

	/**
	 * 
	 * @param portNameConsumer
	 */
	void getPortNames(BiConsumer<String, Boolean> portNameConsumer);

	/**
	 * Get sorted List of serial ports in the system using default settings:<br>
	 *
	 */
	List<String> getPortNames(boolean hideBusyPorts);

	/**
	 * Get sorted List of serial ports in the system using default settings:<br>
	 * include portToInclude if it exists and don't care if its busy.
	 * 
	 * @param portToInclude
	 *            include this port if found even if its busy.
	 * @param hideBusyPorts
	 *            if true hide busy ports.
	 * @return a List of serial ports found.
	 */
	List<String> getPortNames(String portToInclude, boolean hideBusyPorts);

	/**
	 * Open the port as is. A new instance will be created and opened. This is a
	 * shorthand that can be used with try with resource.
	 * 
	 * <pre>
	 * {@code 
	 *   try (SerialPortSocket sps = spsf.open(portName)) {
	 *     sps.write("Hello World".getBytes());
	 *   } catch (IOException ioe) {
	 *   }
	 * }
	 * </pre>
	 * 
	 * @param portName
	 *            The name of the port to open.
	 * @return the open port as is.
	 * @throws IOException
	 *             if the port can't be opened.
	 * @throws SecurityException
	 *             if the caller has no read or write permissions to the port.
	 * @see SerialPortSocket#open()
	 * @see SecurityManager#checkRead(String)
	 * @see SecurityManager#checkWrite(String)
	 * @see java.io.FileOutputStream#FileOutputStream(String)
	 * @see java.io.FileInputStream#FileInputStream(String).
	 */
	SerialPortSocket open(String portName) throws IOException, IllegalStateException;

	/**
	 * Open the port as is. A new instance will be created and opened. This is a
	 * shorthand that can be used with try with resource.
	 * 
	 * <pre>
	 * {@code 
	 *   try (SerialPortSocket sps = spsf.open(portName, speed, dataBits, stopBits, parity, flowControls)) {
	 *     sps.write("Hello World".getBytes());
	 *   } catch (IOException ioe) {
	 *   }
	 * }
	 * </pre>
	 * 
	 * @param portName
	 *            the name of the port to open.
	 * @param speed
	 *            the speed in bit/s.
	 * @param dataBits
	 *            the number of data bits.
	 * @param stopBits
	 *            the number of stop bits.
	 * @param parity
	 *            the parity.
	 * @param flowControls
	 *            the flow control.
	 * @return the opened port with parameters set.
	 * @throws IOException
	 *             if the port can't be opened or some error occurred.
	 * @throws IllegalArgumentException
	 *             if one ore more parameters can't be set.
	 * @throws SecurityException
	 *             if the caller has no read or write permissions to the port.
	 * 
	 * @see SerialPortSocket#open(Speed, DataBits, StopBits, Parity, Set)
	 * @see SecurityManager#checkRead(String)
	 * @see SecurityManager#checkWrite(String)
	 * @see java.io.FileOutputStream#FileOutputStream(String)
	 * @see java.io.FileInputStream#FileInputStream(String).
	 */
	SerialPortSocket open(String portName, Speed speed, DataBits dataBits, StopBits stopBits, Parity parity,
			Set<FlowControl> flowControls) throws IOException;

}