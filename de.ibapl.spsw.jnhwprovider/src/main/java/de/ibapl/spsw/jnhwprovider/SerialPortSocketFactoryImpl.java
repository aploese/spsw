/*-
 * #%L
 * SPSW API
 * %%
 * Copyright (C) 2009 - 2018 Arne Plöse
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
package de.ibapl.spsw.jnhwprovider;

import de.ibapl.jnhw.MultiarchTupelBuilder;
import de.ibapl.jnhw.OS;
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

import javax.inject.Singleton;

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
@Singleton
@Component(name = "de.ibapl.spsw.jnrprovider", scope = ServiceScope.SINGLETON, immediate = true)
public class SerialPortSocketFactoryImpl implements SerialPortSocketFactory {

    protected final static Logger LOG = Logger.getLogger("de.ibapl.spsw.jnrprovider");
    private final static MultiarchTupelBuilder MULTIARCH_TUPEL_BUILDER = new MultiarchTupelBuilder();

    @Override
    public SerialPortSocket createSerialPortSocket(String portName) {
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
                        try (SerialPortSocket sp = createSerialPortSocket(portName)) {
                            sp.open();
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
                            try (SerialPortSocket sp = createSerialPortSocket(deviceName)) {
                                sp.open();
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
                        try (SerialPortSocket sp = createSerialPortSocket(deviceName)) {
                            sp.open();
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
                    try (SerialPortSocket sp = createSerialPortSocket(portName)) {
                        sp.open();
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

    @Override
    public SerialPortSocket open(String portName) throws IOException, IllegalStateException {
        final SerialPortSocket result = createSerialPortSocket(portName);
        try {
            result.open();
            return result;
        } catch (Exception e) {
            result.close();
            throw e;
        }
    }

    @Override
    public SerialPortSocket open(String portName, Speed speed, DataBits dataBits, StopBits stopBits, Parity parity,
            Set<FlowControl> flowControls) throws IOException {
        final SerialPortSocket result = createSerialPortSocket(portName);
        try {
            result.open(speed, dataBits, stopBits, parity, flowControls);
            return result;
        } catch (Exception e) {
            result.close();
            throw e;
        }
    }

}
