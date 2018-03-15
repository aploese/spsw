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

import java.util.Comparator;
import java.util.Set;
import java.util.regex.Pattern;
import javax.inject.Named;
import org.osgi.annotation.versioning.ProviderType;

/**
 *
 * @author aploese
 */
@Named
@ProviderType()
public interface SerialPortSocketFactory {

	SerialPortSocket createSerialPortSocket(String portName);

	SerialPortSocket open(String portName) throws IOException, IllegalStateException;

	SerialPortSocket open(String portName, Baudrate baudRate, DataBits dataBits, StopBits stopBits, Parity parity,
			Set<FlowControl> flowControls) throws IOException, IllegalStateException;

	/**
	 * Get sorted array of serial ports in the system using default settings:<br>
	 *
	 * <b>Search path</b><br>
	 * Windows - ""(always ignored)<br>
	 * Linux - "/dev/"<br>
	 * Solaris - "/dev/term/"<br>
	 * MacOSX - "/dev/"<br>
	 *
	 * <b>RegExp</b><br>
	 * Windows - ""<br>
	 * Linux - "(ttyS|ttyUSB|ttyACM|ttyAMA|rfcomm)[0-9]{1,3}"<br>
	 * Solaris - "[0-9]*|[a-z]*"<br>
	 * MacOSX - "tty.(serial|usbserial|usbmodem).*"<br>
	 *
	 * @return String array. If there is no ports in the system String[] with
	 *         <b>zero</b> length will be returned (since jSSC-0.8 in previous
	 *         versions null will be returned)
	 */
	Set<String> getPortNames(boolean hideBusyPorts);

	/**
	 * Get sorted array of serial ports in the system located on searchPath
	 *
	 * @param searchPath
	 *            Path for searching serial ports <b>(not null)</b><br>
	 *            The default search paths:<br>
	 *            Linux, MacOSX: <b>/dev/</b><br>
	 *            Solaris: <b>/dev/term/</b><br>
	 *            Windows: <b>this parameter ingored</b>
	 *
	 * @return String array. If there is no ports in the system String[]
	 *
	 * @since 2.3.0
	 */
	Set<String> getPortNames(String searchPath, boolean hideBusyPorts);

	/**
	 * Get sorted array of serial ports in the system matched pattern
	 *
	 * @param pattern
	 *            RegExp pattern for matching port names <b>(not null)</b>
	 *
	 * @return String array. If there is no ports in the system String[]
	 *
	 * @since 2.3.0
	 */
	Set<String> getPortNames(Pattern pattern, boolean hideBusyPorts);

	/**
	 * Get sorted array of serial ports in the system matched pattern
	 *
	 * @param comparator
	 *            Comparator for sotring port names <b>(not null)</b>
	 *
	 * @return String array. If there is no ports in the system String[]
	 *
	 * @since 2.3.0
	 */
	Set<String> getPortNames(Comparator<String> comparator, boolean hideBusyPorts);

	/**
	 * Get sorted array of serial ports in the system located on searchPath, matched
	 * pattern
	 *
	 * @param searchPath
	 *            Path for searching serial ports <b>(not null)</b><br>
	 *            The default search paths:<br>
	 *            Linux, MacOSX: <b>/dev/</b><br>
	 *            Solaris: <b>/dev/term/</b><br>
	 *            Windows: <b>this parameter ingored</b>
	 * @param pattern
	 *            RegExp pattern for matching port names <b>(not null)</b>
	 *
	 * @return String array. If there is no ports in the system String[]
	 *
	 * @since 2.3.0
	 */
	Set<String> getPortNames(String searchPath, Pattern pattern, boolean hideBusyPorts);

	/**
	 * Get sorted array of serial ports in the system located on searchPath and
	 * sorted by comparator
	 *
	 * @param searchPath
	 *            Path for searching serial ports <b>(not null)</b><br>
	 *            The default search paths:<br>
	 *            Linux, MacOSX: <b>/dev/</b><br>
	 *            Solaris: <b>/dev/term/</b><br>
	 *            Windows: <b>this parameter ingored</b>
	 * @param comparator
	 *            Comparator for sotring port names <b>(not null)</b>
	 *
	 * @return String array. If there is no ports in the system String[]
	 *
	 * @since 2.3.0
	 */
	Set<String> getPortNames(String searchPath, Comparator<String> comparator, boolean hideBusyPorts);

	/**
	 * Get sorted array of serial ports in the system matched pattern and sorted by
	 * comparator
	 *
	 * @param pattern
	 *            RegExp pattern for matching port names <b>(not null)</b>
	 * @param comparator
	 *            Comparator for sotring port names <b>(not null)</b>
	 *
	 * @return String array. If there is no ports in the system String[]
	 *
	 * @since 2.3.0
	 */
	Set<String> getPortNames(Pattern pattern, Comparator<String> comparator, boolean hideBusyPorts);

	/**
	 * Get sorted array of serial ports in the system located on searchPath, matched
	 * pattern and sorted by comparator
	 *
	 * @param searchPath
	 *            Path for searching serial ports <b>(not null)</b><br>
	 *            The default search paths:<br>
	 *            Linux, MacOSX: <b>/dev/</b><br>
	 *            Solaris: <b>/dev/term/</b><br>
	 *            Windows: <b>this parameter ingored</b>
	 * @param pattern
	 *            RegExp pattern for matching port names <b>(not null)</b>
	 * @param comparator
	 *            Comparator for sotring port names <b>(not null)</b>
	 * @param hideBusyPorts
	 *
	 * @return String array. If there is no ports in the system String[]
	 *
	 * @since 2.3.0
	 */
	Set<String> getPortNames(String searchPath, Pattern pattern, Comparator<String> comparator, boolean hideBusyPorts);

}
