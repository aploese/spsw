package de.ibapl.spsw.api;

/*-
 * #%L
 * SPSW API
 * %%
 * Copyright (C) 2009 - 2017 Arne Plöse
 * %%
 * SPSW - Drivers for the serial port, https://github.com/aploese/spsw/
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

     /**
     * Get sorted array of serial ports in the system using default
     * settings:<br>
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
     * <b>zero</b> length will be returned (since jSSC-0.8 in previous versions
     * null will be returned)
     */
    Set<String> getPortNames(boolean hideBusyPorts);

    /**
     * Get sorted array of serial ports in the system located on searchPath
     *
     * @param searchPath Path for searching serial ports <b>(not null)</b><br>
     * The default search paths:<br>
     * Linux, MacOSX: <b>/dev/</b><br>
     * Solaris: <b>/dev/term/</b><br>
     * Windows: <b>this parameter ingored</b>
     *
     * @return String array. If there is no ports in the system String[]
     *
     * @since 2.3.0
     */
    Set<String> getPortNames(String searchPath, boolean hideBusyPorts);

    /**
     * Get sorted array of serial ports in the system matched pattern
     *
     * @param pattern RegExp pattern for matching port names <b>(not null)</b>
     *
     * @return String array. If there is no ports in the system String[]
     *
     * @since 2.3.0
     */
    Set<String> getPortNames(Pattern pattern, boolean hideBusyPorts);

    /**
     * Get sorted array of serial ports in the system matched pattern
     *
     * @param comparator Comparator for sotring port names <b>(not null)</b>
     *
     * @return String array. If there is no ports in the system String[]
     *
     * @since 2.3.0
     */
    Set<String> getPortNames(Comparator<String> comparator, boolean hideBusyPorts);

    /**
     * Get sorted array of serial ports in the system located on searchPath,
     * matched pattern
     *
     * @param searchPath Path for searching serial ports <b>(not null)</b><br>
     * The default search paths:<br>
     * Linux, MacOSX: <b>/dev/</b><br>
     * Solaris: <b>/dev/term/</b><br>
     * Windows: <b>this parameter ingored</b>
     * @param pattern RegExp pattern for matching port names <b>(not null)</b>
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
     * @param searchPath Path for searching serial ports <b>(not null)</b><br>
     * The default search paths:<br>
     * Linux, MacOSX: <b>/dev/</b><br>
     * Solaris: <b>/dev/term/</b><br>
     * Windows: <b>this parameter ingored</b>
     * @param comparator Comparator for sotring port names <b>(not null)</b>
     *
     * @return String array. If there is no ports in the system String[]
     *
     * @since 2.3.0
     */
    Set<String> getPortNames(String searchPath, Comparator<String> comparator, boolean hideBusyPorts);

    /**
     * Get sorted array of serial ports in the system matched pattern and sorted
     * by comparator
     *
     * @param pattern RegExp pattern for matching port names <b>(not null)</b>
     * @param comparator Comparator for sotring port names <b>(not null)</b>
     *
     * @return String array. If there is no ports in the system String[]
     *
     * @since 2.3.0
     */
    Set<String> getPortNames(Pattern pattern, Comparator<String> comparator, boolean hideBusyPorts);

    /**
     * Get sorted array of serial ports in the system located on searchPath,
     * matched pattern and sorted by comparator
     *
     * @param searchPath Path for searching serial ports <b>(not null)</b><br>
     * The default search paths:<br>
     * Linux, MacOSX: <b>/dev/</b><br>
     * Solaris: <b>/dev/term/</b><br>
     * Windows: <b>this parameter ingored</b>
     * @param pattern RegExp pattern for matching port names <b>(not null)</b>
     * @param comparator Comparator for sotring port names <b>(not null)</b>
     * @param hideBusyPorts
     *
     * @return String array. If there is no ports in the system String[]
     *
     * @since 2.3.0
     */
    Set<String> getPortNames(String searchPath, Pattern pattern, Comparator<String> comparator, boolean hideBusyPorts);


}
