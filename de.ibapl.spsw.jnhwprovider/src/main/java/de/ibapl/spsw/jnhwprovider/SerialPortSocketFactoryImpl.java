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
package de.ibapl.spsw.jnhwprovider;

import de.ibapl.jnhw.libloader.MultiarchTupelBuilder;
import de.ibapl.jnhw.libloader.OS;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.PortnamesComparator;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.SerialPortSocketFactory;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;

/**
 * Impements the {@linkplain SerialPortSocketFactory}.
 *
 * @author Arne Plöse
 */
@Component(name = "de.ibapl.spsw.jnrprovider", scope = ServiceScope.SINGLETON, immediate = true)
public class SerialPortSocketFactoryImpl implements SerialPortSocketFactory {

    protected final static Logger LOG = Logger.getLogger("de.ibapl.spsw.jnrprovider");
    private final static MultiarchTupelBuilder MULTIARCH_TUPEL_BUILDER = new MultiarchTupelBuilder();

    @Override
    public SerialPortSocket open(String portName) throws IOException {
        switch (MULTIARCH_TUPEL_BUILDER.getOs()) {
            case LINUX:
                return new PosixSerialPortSocket(portName);
            case FREE_BSD:
                return new PosixSerialPortSocket(portName);
            case WINDOWS:
                return new GenericWinSerialPortSocket(portName);
            default:
                throw new RuntimeException("Cant handle OS: " + MULTIARCH_TUPEL_BUILDER.getOs());
        }
    }

    @Override
    public SerialPortSocket open(String portName, Speed speed, DataBits dataBits, StopBits stopBits, Parity parity,
            Set<FlowControl> flowControls) throws IOException {
        switch (MULTIARCH_TUPEL_BUILDER.getOs()) {
            case LINUX:
                return new PosixSerialPortSocket(portName, speed, dataBits, stopBits, parity, flowControls);
            case FREE_BSD:
                return new PosixSerialPortSocket(portName, speed, dataBits, stopBits, parity, flowControls);
            case WINDOWS:
                return new GenericWinSerialPortSocket(portName, speed, dataBits, stopBits, parity, flowControls);
            default:
                throw new RuntimeException("Cant handle OS: " + MULTIARCH_TUPEL_BUILDER.getOs());
        }
    }

    @Override
    public void getPortNames(BiConsumer<String, Boolean> portNameConsumer) {
        final Pattern pattern = getPortnamesRegExp();
        switch (MULTIARCH_TUPEL_BUILDER.getOs()) {
            case WINDOWS:
                List<String> portNames = getWindowsBasedPortNames();
                if (portNames == null) {
                    return;
                }
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
    public List<String> getPortNames(boolean hideBusyPorts) {
        if (MULTIARCH_TUPEL_BUILDER.getOs() == OS.WINDOWS) {
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
        if (MULTIARCH_TUPEL_BUILDER.getOs() == OS.WINDOWS) {
            return getWindowsPortNames(portToInclude, hideBusyPorts);
        } else {
            return getUnixBasedPortNames(portToInclude, hideBusyPorts);
        }
    }

    protected String getPortnamesPath() {
        switch (MULTIARCH_TUPEL_BUILDER.getOs()) {
            case LINUX:
                return DEFAULT_LINUX_DEVICE_PATH;
            case FREE_BSD:
                return DEFAULT_FREE_BSD_DEVICE_PATH;
            case SOLARIS:
                return DEFAULT_SUNOS_DEVICE_PATH;
            case MAC_OS_X:
                return DEFAULT_MACOS_DEVICE_PATH;
            case WINDOWS:
                return DEFAULT_WINDOWS_DEVICE_PATH;
            default:
                LOG.log(Level.SEVERE, "Unknown OS, os.name: {0} mapped to: {1}",
                        new Object[]{System.getProperty("os.name"), MULTIARCH_TUPEL_BUILDER.getOs()});
                return null;
        }
    }

    protected Pattern getPortnamesRegExp() {
        switch (MULTIARCH_TUPEL_BUILDER.getOs()) {
            case LINUX:
                return Pattern.compile(DEFAULT_LINUX_PORTNAME_PATTERN);
            case FREE_BSD:
                return Pattern.compile(DEFAULT_FREE_BSD_PORTNAME_PATTERN);
            case SOLARIS:
                return Pattern.compile(DEFAULT_SUNOS_PORTNAME_PATTERN);
            case MAC_OS_X:
                return Pattern.compile(DEFAULT_MACOS_PORTNAME_PATTERN);
            case WINDOWS:
                return Pattern.compile(DEFAULT_WINDOWS_PORTNAME_PATTERN);
            default:
                LOG.log(Level.SEVERE, "Unknown OS, os.name: {0} mapped to: {1}",
                        new Object[]{System.getProperty("os.name"), MULTIARCH_TUPEL_BUILDER.getOs()});
                return null;
        }
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

    protected List<String> getWindowsBasedPortNames() {
        return GenericWinSerialPortSocket.getWindowsBasedPortNames();
    }

    protected List<String> getWindowsPortNames(String portToInclude, boolean hideBusyPorts) {
        List<String> portNames = getWindowsBasedPortNames();
        if (portNames == null) {
            return Collections.emptyList();
        }
        final Pattern pattern = getPortnamesRegExp();
        List<String> result = new LinkedList<>();
        for (String portName : portNames) {
            if (pattern.matcher(portName).find()) {
                if (hideBusyPorts) {
                    try (SerialPortSocket sp = open(portName)) {
                        result.add(portName);
                    } catch (IOException ex) {
                        if (!portToInclude.isEmpty() && portToInclude.equals(portName)) {
                            result.add(portName);
                        } else {
                            LOG.log(Level.FINEST, "found busy port: " + portName, ex);
                        }
                    }
                } else {
                    result.add(portName);
                }
            }
        }
        result.sort(new PortnamesComparator());
        return result;
    }

}
