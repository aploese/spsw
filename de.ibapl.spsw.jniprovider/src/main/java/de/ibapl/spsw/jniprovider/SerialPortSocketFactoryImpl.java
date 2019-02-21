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
package de.ibapl.spsw.jniprovider;

import de.ibapl.jnhw.NativeLibLoader;
import de.ibapl.jnhw.OS;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;

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

/**
 * Impements the {@linkplain SerialPortSocketFactory}.
 * 
 * @author Arne Plöse
 */
@Singleton
@Component(name = "de.ibapl.spsw.jniprovider", scope = ServiceScope.SINGLETON, immediate = true)
public class SerialPortSocketFactoryImpl implements SerialPortSocketFactory {

	protected final static Logger LOG = Logger.getLogger("de.ibapl.spsw.jniprovider");

	/**
	 * Do not load the native library here on failure it may throw up the running
	 * framework (OSGi, JEE, Spring...)
	 */
	public SerialPortSocketFactoryImpl() {
	}

	private final static String LIB_SPSW_NAME = "spsw";
	private final static int LIB_SPSW_VERSION = 0;

	public static boolean isLibLoaded() {
            return (NativeLibLoader.isLibLoaded(LIB_SPSW_NAME));
	}

	public static String getLibName() {
		return LIB_SPSW_NAME;
	}

	protected LinkedList<String> getWindowsBasedPortNames() {
		if (!isLibLoaded()) {
			// Make sure lib is loaded to avoid Link error
			loadNativeLib(false);
		}
                LinkedList<String> portNames = new LinkedList<>();
		GenericWinSerialPortSocket.getWindowsBasedPortNames(portNames);
                return portNames;
	}

	@Override
	public SerialPortSocket createSerialPortSocket(String portName) {
		// ServiceLoader instantiates this lazy so this is the last chance to do so
		if (!isLibLoaded()) {
			loadNativeLib(false);
		}

		switch (NativeLibLoader.getOS()) {
		case LINUX:
			return new GenericTermiosSerialPortSocket(portName);
		case FREE_BSD:
			return new GenericTermiosSerialPortSocket(portName);
		case WINDOWS:
				return new GenericWinSerialPortSocket(portName);
		default:
			throw new UnsupportedOperationException(NativeLibLoader.getOS() + " is currently not supported yet\nSystem.properties:\n");
		}
	}

	protected String getPortnamesPath() {
		switch (NativeLibLoader.getOS()) {
		case LINUX: {
			return DEFAULT_LINUX_DEVICE_PATH;
		}
		case FREE_BSD: {
			return DEFAULT_FREE_BSD_DEVICE_PATH;
		}
		case SOLARIS: {
			return DEFAULT_SUNOS_DEVICE_PATH;
		}
		case MAC_OS_X: {
			return DEFAULT_MACOS_DEVICE_PATH;
		}
		case WINDOWS: {
			return DEFAULT_WINDOWS_DEVICE_PATH;
		}
		default: {
			LOG.log(Level.SEVERE, "Unknown OS, os.name: {0} mapped to: {1}",
					new Object[] { System.getProperty("os.name"), NativeLibLoader.getOS() });
			return null;
		}
		}
	}

	protected Pattern getPortnamesRegExp() {
		switch (NativeLibLoader.getOS()) {
		case LINUX: {
			return Pattern.compile(DEFAULT_LINUX_PORTNAME_PATTERN);
		}
		case FREE_BSD: {
			return Pattern.compile(DEFAULT_FREE_BSD_PORTNAME_PATTERN);
		}
		case SOLARIS: {
			return Pattern.compile(DEFAULT_SUNOS_PORTNAME_PATTERN);
		}
		case MAC_OS_X: {
			return Pattern.compile(DEFAULT_MACOS_PORTNAME_PATTERN);
		}
		case WINDOWS: {
			return Pattern.compile(DEFAULT_WINDOWS_PORTNAME_PATTERN);
		}
		default: {
			LOG.log(Level.SEVERE, "Unknown OS, os.name: {0} mapped to: {1}",
					new Object[] { System.getProperty("os.name"), NativeLibLoader.getOS()});
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
		if (OS.WINDOWS == NativeLibLoader.getOS()) {
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
		if (OS.WINDOWS == NativeLibLoader.getOS()) {
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
		for (String portName : result) {
			if (pattern.matcher(portName).find()) {
				if (hideBusyPorts) {
					try (SerialPortSocket sp = createSerialPortSocket(portName)) {
						sp.open();
					} catch (IOException ex) {
						if (!portToInclude.isEmpty() && portToInclude.equals(portName)) {
						} else {
							result.remove(portName);
							LOG.log(Level.FINEST, "found busy port: " + portName, ex);
						}
					}
				} else {
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

	/**
	 * Load the native library in the right lifecycle for the running framework
	 * (OSGi, JEE, Spring).
	 */
	@PostConstruct
	@Activate
	public void activate() {
		if (!isLibLoaded()) {
			loadNativeLib(false);
		}
	}

	@PreDestroy
	@Deactivate
	public void deActivate() {
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
			Set<FlowControl> flowControls) throws IOException, IllegalStateException {
		final SerialPortSocket result = createSerialPortSocket(portName);
		try {
			result.open(speed, dataBits, stopBits, parity, flowControls);
			return result;
		} catch (Exception e) {
			result.close();
			throw e;
		}
	}

	@Override
	public void getPortNames(BiConsumer<String, Boolean> portNameConsumer) {
		final Pattern pattern = getPortnamesRegExp();
		switch (NativeLibLoader.getOS()) {
		case WINDOWS:
			LinkedList<String> portNames = getWindowsBasedPortNames();
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

    private boolean loadNativeLib(boolean supressException) {
        if (NativeLibLoader.loadNativeLib(LIB_SPSW_NAME, LIB_SPSW_VERSION)) {
            return true;
        } else if (supressException) {
            return false;
        } else {
            throw  new RuntimeException("Could not load native lib", NativeLibLoader.getLoadError(LIB_SPSW_NAME));
        }
    }

}
