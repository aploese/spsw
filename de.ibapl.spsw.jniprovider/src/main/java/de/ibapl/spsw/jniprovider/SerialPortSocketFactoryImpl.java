/*
 * SPSW - Drivers for the serial port, https://github.com/aploese/spsw/
 * Copyright (C) 2009-2021, Arne Plöse and individual contributors as indicated
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
package de.ibapl.spsw.jniprovider;

import de.ibapl.jnhw.libloader.LoadResult;
import de.ibapl.jnhw.libloader.NativeLibResolver;
import de.ibapl.jnhw.libloader.OS;
import de.ibapl.spsw.api.AsyncSerialPortSocket;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.ServiceScope;

import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.PortnamesComparator;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.SerialPortSocketFactory;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.api.TimeoutIOException;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;

/**
 * Impements the {@linkplain SerialPortSocketFactory}.
 *
 * @author Arne Plöse
 */
@Component(name = "de.ibapl.spsw.jniprovider", scope = ServiceScope.SINGLETON, immediate = true)
public class SerialPortSocketFactoryImpl implements SerialPortSocketFactory {

    protected final static Logger LOG = Logger.getLogger("de.ibapl.spsw.jniprovider");

    /**
     * Do not load the native library here on failure it may throw up the
     * running framework (OSGi, JEE, Spring...)
     */
    public SerialPortSocketFactoryImpl() {
    }

    //Hold this here so loading the lib wont fail for OSGi
    private final static Class<TimeoutIOException> T_CLASS = TimeoutIOException.class;

    private final static String LIB_SPSW_NAME = "spsw";
    private final static int LIB_SPSW_VERSION = 1;
    private static LoadResult LIB_SPSW_LOAD_RESULT;

    protected static void doSystemLoad(String absoluteLibName) {
        System.load(absoluteLibName);
    }

    public static boolean touchNativeLib() {
        if (LIB_SPSW_LOAD_RESULT == null) {
            LIB_SPSW_LOAD_RESULT = NativeLibResolver.loadNativeLib(LIB_SPSW_NAME, LIB_SPSW_VERSION, SerialPortSocketFactoryImpl::doSystemLoad);
        }
        if (LIB_SPSW_LOAD_RESULT.isLoaded()) {
            return true;
        } else if (LIB_SPSW_LOAD_RESULT.isError()) {
            throw new RuntimeException(LIB_SPSW_LOAD_RESULT.loadError);
        } else {
            throw new RuntimeException("Should never happen: lib spsw not loaded for unknow reason.");
        }
    }

    public static String getLibName() {
        return LIB_SPSW_NAME;
    }

    protected LinkedList<String> getWindowsBasedPortNames() {
        // ServiceLoader instantiates this lazy so this is the last chance to do so
        touchNativeLib();
        final LinkedList<String> portNames = new LinkedList<>();
        GenericWinSerialPortSocket.getWindowsBasedPortNames(portNames);
        return portNames;
    }

    @Override
    public SerialPortSocket open(String portName) throws IOException {
        // ServiceLoader instantiates this lazy so this is the last chance to do so
        touchNativeLib();

        switch (NativeLibResolver.getOS()) {
            case DARWIN:
            case FREE_BSD:
            case LINUX:
            case OPEN_BSD:
                return new GenericTermiosSerialPortSocket(portName);
            case WINDOWS:
                return new GenericWinSerialPortSocket(portName);
            default:
                throw new UnsupportedOperationException(NativeLibResolver.getOS() + " is currently not supported yet\nSystem.properties:\n");
        }
    }

    protected String getPortnamesPath() {
        switch (NativeLibResolver.getOS()) {
            case DARWIN: {
                return DEFAULT_DARWIN_DEVICE_PATH;
            }
            case FREE_BSD: {
                return DEFAULT_FREE_BSD_DEVICE_PATH;
            }
            case LINUX: {
                return DEFAULT_LINUX_DEVICE_PATH;
            }
            case OPEN_BSD: {
                return DEFAULT_OPEN_BSD_DEVICE_PATH;
            }
            case SOLARIS: {
                return DEFAULT_SUNOS_DEVICE_PATH;
            }
            case WINDOWS: {
                return DEFAULT_WINDOWS_DEVICE_PATH;
            }
            default: {
                LOG.log(Level.SEVERE, "Unknown OS, os.name: {0} mapped to: {1}",
                        new Object[]{System.getProperty("os.name"), NativeLibResolver.getOS()});
                return null;
            }
        }
    }

    protected Pattern getPortnamesRegExp() {
        switch (NativeLibResolver.getOS()) {
            case DARWIN: {
                return Pattern.compile(DEFAULT_DARWIN_PORTNAME_PATTERN);
            }
            case FREE_BSD: {
                return Pattern.compile(DEFAULT_FREE_BSD_PORTNAME_PATTERN);
            }
            case LINUX: {
                return Pattern.compile(DEFAULT_LINUX_PORTNAME_PATTERN);
            }
            case OPEN_BSD: {
                return Pattern.compile(DEFAULT_OPEN_BSD_PORTNAME_PATTERN);
            }
            case SOLARIS: {
                return Pattern.compile(DEFAULT_SUNOS_PORTNAME_PATTERN);
            }
            case WINDOWS: {
                return Pattern.compile(DEFAULT_WINDOWS_PORTNAME_PATTERN);
            }
            default: {
                LOG.log(Level.SEVERE, "Unknown OS, os.name: {0} mapped to: {1}",
                        new Object[]{System.getProperty("os.name"), NativeLibResolver.getOS()});
                return null;
            }
        }
    }

    /**
     * Get sorted List of serial ports in the system using default settings:<br>
     *
     */
    @Override
    public List<String> getPortNames(boolean hideBusyPorts) {
        if (OS.WINDOWS == NativeLibResolver.getOS()) {
            return getWindowsPortNames("", hideBusyPorts);
        } else {
            return getUnixBasedPortNames("", hideBusyPorts);
        }
    }

    @Override
    public List<String> getPortNames(String portToInclude, boolean hideBusyPorts) {
        if (portToInclude == null || portToInclude.isEmpty()) {
            throw new IllegalArgumentException("portToInclude is null or empty");
        }
        if (OS.WINDOWS == NativeLibResolver.getOS()) {
            return getWindowsPortNames(portToInclude, hideBusyPorts);
        } else {
            return getUnixBasedPortNames(portToInclude, hideBusyPorts);
        }
    }

    /**
     * Get serial port names in Windows
     *
     * @since 2.3.0
     */
    protected List<String> getWindowsPortNames(String portToInclude, boolean hideBusyPorts) {
        LinkedList<String> result = getWindowsBasedPortNames();
        final Pattern pattern = getPortnamesRegExp();
        Iterator<String> iter = result.iterator();

        while (iter.hasNext()) {
            final String portName = iter.next();
            if (pattern.matcher(portName).find()) {
                if (hideBusyPorts) {
                    try (SerialPortSocket sp = open(portName)) {
                    } catch (IOException ex) {
                        if (!portName.equals(portToInclude)) {
                            iter.remove();
                            LOG.log(Level.FINEST, "found busy port: " + portName, ex);
                        }
                    }
                }
            }
        }
        result.sort(new PortnamesComparator());
        return result;
    }

    /**
     * Universal method for getting port names of _nix based systems
     */
    protected List<String> getUnixBasedPortNames(String portToInclude, boolean hideBusyPorts) {
        File dir = new File(getPortnamesPath());
        final Pattern pattern = getPortnamesRegExp();
        final List<String> result = new LinkedList<>();

        // We misuse the listFiles method to get to the contents of the dir and return
        // always false to prevent the creatin of an array;
        dir.listFiles((File parentDir, String name) -> {
            if (pattern.matcher(name).find()) {
                final File deviceFile = new File(parentDir, name);
                final String deviceName = deviceFile.getAbsolutePath();
                if (!deviceFile.isDirectory() && !deviceFile.isFile()) {
                    if (hideBusyPorts) {
                        try (SerialPortSocket sp = open(deviceName)) {
                            result.add(deviceName);
                        } catch (IOException ex) {
                            if (!portToInclude.isEmpty() && portToInclude.equals(deviceName)) {
                                result.add(deviceName);
                            } else {
                                LOG.log(Level.FINEST, "found busy port: " + deviceName, ex);
                            }
                        }
                    } else {
                        result.add(deviceName);
                    }
                }
            }
            return false;
        });

        result.sort(new PortnamesComparator());
        return result;
    }

    /**
     * Load the native library in the right lifecycle for the running framework
     * (OSGi, JEE, Spring).
     */
    @Activate
    public void activate() {
        touchNativeLib();
    }

    @Deactivate
    public void deActivate() {
    }

    @Override
    public SerialPortSocket open(String portName, Speed speed, DataBits dataBits, StopBits stopBits, Parity parity,
            Set<FlowControl> flowControls) throws IOException, IllegalStateException {
        // ServiceLoader instantiates this lazy so this is the last chance to do so
        touchNativeLib();

        switch (NativeLibResolver.getOS()) {
            case DARWIN:
            case FREE_BSD:
            case LINUX:
            case OPEN_BSD:
                return new GenericTermiosSerialPortSocket(portName, speed, dataBits, stopBits, parity, flowControls);
            case WINDOWS:
                return new GenericWinSerialPortSocket(portName, speed, dataBits, stopBits, parity, flowControls);
            default:
                throw new UnsupportedOperationException(NativeLibResolver.getOS() + " is currently not supported yet\nSystem.properties:\n");
        }
    }

    @Override
    public void getPortNames(BiConsumer<String, Boolean> portNameConsumer) {
        final Pattern pattern = getPortnamesRegExp();
        switch (NativeLibResolver.getOS()) {
            case WINDOWS:
                LinkedList<String> portNames = getWindowsBasedPortNames();
                for (String portName : portNames) {
                    if (pattern.matcher(portName).find()) {
                        boolean busy = true;
                        try (SerialPortSocket sp = open(portName)) {
                            busy = false;
                        } catch (IOException ex) {
                        }
                        portNameConsumer.accept(portName, Boolean.valueOf(busy));
                    }
                }
            default:
                File dir = new File(getPortnamesPath());

                // We misuse the listFiles method to get to the contents of the dir and return
                // always false to prevent the creatin of an array;
                dir.listFiles((File parentDir, String name) -> {
                    if (pattern.matcher(name).find()) {
                        final File deviceFile = new File(parentDir, name);
                        final String deviceName = deviceFile.getAbsolutePath();
                        if (!deviceFile.isDirectory() && !deviceFile.isFile()) {
                            boolean busy = true;
                            try (SerialPortSocket sp = open(deviceName)) {
                                busy = false;
                            } catch (IOException ex) {
                            }
                            portNameConsumer.accept(deviceName, Boolean.valueOf(busy));
                        }
                    }
                    return false;
                });
        }
    }

    @Override
    public AsyncSerialPortSocket openAsync(String portName, ExecutorService executor) throws IOException, IllegalStateException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AsyncSerialPortSocket openAsync(String portName, Speed speed, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls, ExecutorService executor) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
