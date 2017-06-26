/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ibapl.spsw.api;

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
