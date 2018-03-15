/*-
 * #%L
 * SPSW API
 * %%
 * Copyright (C) 2009 - 2017 Arne Plöse
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */
package de.ibapl.spsw.api;

import java.io.IOException;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import javax.inject.Named;
import org.osgi.annotation.versioning.ProviderType;

/**
 *
 * @author aploese
 */
@Named
@ProviderType()
public interface SerialPortSocketFactory {
	
	public final String DEFAULT_LINUX_DEVICE_PATH = "/dev/";
	public final String DEFAULT_LINUX_PORTNAME_PATTERN = "(ttyS|ttyUSB|ttyACM|ttyAMA|rfcomm|ttyO)[0-9]{1,3}";
	public final String DEFAULT_SUNOS_DEVICE_PATH = "/dev/term/";
	public final String DEFAULT_SUNOS_PORTNAME_PATTERN = "[0-9]*|[a-z]*";
	public final String DEFAULT_MACOS_DEVICE_PATH = "/dev/";
	public final String DEFAULT_MACOS_PORTNAME_PATTERN = "tty.(serial|usbserial|usbmodem).*";
	public final String DEFAULT_WINDOWS_DEVICE_PATH = "";
	public final String DEFAULT_WINDOWS_PORTNAME_PATTERN = "(COM)[0-9]{1,3}";
	
	

	SerialPortSocket createSerialPortSocket(String portName);

	SerialPortSocket open(String portName) throws IOException, IllegalStateException;

	SerialPortSocket open(String portName, Baudrate baudRate, DataBits dataBits, StopBits stopBits, Parity parity,
			Set<FlowControl> flowControls) throws IOException, IllegalStateException;

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
	 */
	List<String> getPortNames(String portToInclude, boolean hideBusyPorts);

}