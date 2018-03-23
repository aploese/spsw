/*-
 * #%L
 * SPSW Provider
 * %%
 * Copyright (C) 2009 - 2017 Arne Plöse
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
package de.ibapl.spsw.provider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
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

import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.PortnamesComparator;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.SerialPortSocketFactory;
import de.ibapl.spsw.api.StopBits;

/**
 *
 * @author aploese
 */
@Singleton
@Component(name = "de.ibapl.spsw.provider", scope = ServiceScope.SINGLETON, immediate = true)
public class SerialPortSocketFactoryImpl implements SerialPortSocketFactory {

	protected final static Logger LOG = Logger.getLogger("de.ibapl.spsw.provider");

	private static SerialPortSocketFactoryImpl singleton;

	/**
	 * Creates and activates @see #activate a singleton instance for use in non
	 * framework environments
	 * 
	 * @deprecated Use {@link java.util.ServiceLoader} to get an instance
	 * @return
	 */
	@Deprecated
	public synchronized static SerialPortSocketFactoryImpl singleton() {
		if (singleton == null) {
			singleton = new SerialPortSocketFactoryImpl();
			singleton.activate();
		}
		return singleton;
	}

	/**
	 * Do not load the native library here on failure it may thow up the running
	 * framework (OSGi, JEE, Spring...)
	 */
	public SerialPortSocketFactoryImpl() {
	}

	private static boolean libLoaded;
	private static String libName;
	public final static String SPSW_PROPERTIES = "de/ibapl/spsw/provider/spsw.properties";
	private final static MultiarchTupelBuilder MULTIARCH_TUPEL_BUILDER = new MultiarchTupelBuilder();

	public boolean isLibLoaded() {
		return libLoaded;
	}

	public String getLibName() {
		return libName;
	}

	// TODO usable LOG INFOS ...
	public synchronized boolean loadNativeLib() {
		if (libLoaded) {
			LOG.log(Level.INFO, "Lib was Loaded");
			return false;
		}
		LOG.log(Level.INFO, "java.library.path: \"{0}\"", System.getProperty("java.library.path"));
		Properties p = new Properties();
		try {
			p.load(getClass().getClassLoader().getResourceAsStream(SPSW_PROPERTIES));
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, "Can't find spsw.properties");
			throw new RuntimeException("Can't load version information", ex);
		}

		for (String multiarchtupel : MULTIARCH_TUPEL_BUILDER.getMultiarchTupels()) {

			libName = String.format("spsw-%s", p.getProperty("version." + multiarchtupel));

			// Try it plain - OSGi will load with the bundle classloader - or if there are
			// in the "java.library.path"
			LOG.log(Level.INFO, "Try plain with libName: {0}", libName);
			try {
				System.loadLibrary(libName);
				LOG.log(Level.INFO, "Lib loaded via System.loadLibrary(\"{0}\")", libName);
				return true;
			} catch (UnsatisfiedLinkError ule) {
				LOG.log(Level.INFO, "Native lib {0} not loaded: {1}", new String[] { libName, ule.getMessage() });
				libLoaded = false;
			} catch (Throwable t) {
				LOG.log(Level.INFO, "Native lib not loaded.", t);
				libLoaded = false;
			}

			// Figure out os and arch
			final String libResourceName = String.format("lib/%s/%s", multiarchtupel, System.mapLibraryName(libName));
			// Try from filesystem like the tests do
			libName = getClass().getClassLoader().getResource(libResourceName).getFile();
			if (new File(libName).exists()) {
				// Unbundled aka not within a jar
				LOG.log(Level.INFO, "Try from filesystem with libName: {0}", libName);
				try {
					System.load(libName);
					LOG.log(Level.INFO, "Lib loaded via System.load(\"{0}\")", libName);
					return true;
				} catch (UnsatisfiedLinkError ule) {
					LOG.log(Level.INFO, "Native lib {0} not loaded: {1}", new String[] { libName, ule.getMessage() });
					libLoaded = false;
				} catch (Throwable t) {
					LOG.log(Level.INFO, "Native lib not loaded.", t);
					libLoaded = false;
				}
			}

			// If nothing helps, do it the hard way: unpack to temp and load that.
			File tmpLib = null;
			try (InputStream is = AbstractSerialPortSocket.class.getClassLoader()
					.getResourceAsStream(libResourceName)) {
				if (is == null) {
					throw new RuntimeException("Cant find lib: " + libName + "in resources");
				}
				int splitPos = libName.lastIndexOf('.');
				if (splitPos <= 0) {
					// ERROR
				}
				tmpLib = File.createTempFile(libName.substring(0, splitPos), libName.substring(splitPos));
				tmpLib.deleteOnExit();
				try (FileOutputStream fos = new FileOutputStream(tmpLib)) {
					byte[] buff = new byte[1024];
					int i;
					while ((i = is.read(buff)) > 0) {
						fos.write(buff, 0, i);
					}
					fos.flush();
				}
				LOG.log(Level.INFO, "Try temp copy\nfrom:\t{0}\nto:\t{1}",
						new String[] { libName, tmpLib.getAbsolutePath() });
				libName = tmpLib.getAbsolutePath();
				try {
					System.load(libName);
				} catch (Throwable t) {
					LOG.log(Level.INFO, "Native lib not loaded.", t);
					libLoaded = false;
					throw t;
				}
				libName = tmpLib.getAbsolutePath();
				LOG.log(Level.INFO, "Lib loaded via System.load(\"{0}\")", tmpLib.getAbsolutePath());
				return true;
			} catch (Throwable t) {
				LOG.log(Level.SEVERE, "Can't load the lib \"" + tmpLib.getAbsolutePath() + "\" List System Properties",
						t);
			}
		}
		LOG.log(Level.SEVERE, "Giving up can't load the lib! Will list System Properties\n{0}",
				new Object[] { MULTIARCH_TUPEL_BUILDER.listSystemProperties() });
		throw new RuntimeException("Can't load spsw native lib, giving up! See logs for details!");
	}

	protected String[] getWindowsBasedPortNames() {
		if (!isLibLoaded()) {
			// Make sure lib is loaded to avoid Link error
			loadNativeLib();
		}
		return GenericWinSerialPortSocket.getWindowsBasedPortNames();
	}

	@Override
	public SerialPortSocket createSerialPortSocket(String portName) {
		// ServiceLoader instatiates this lazy so this is the last chance to do so
		if (!libLoaded) {
			loadNativeLib();
		}

		switch (MULTIARCH_TUPEL_BUILDER.getSimpleOsName()) {
		case "linux":
			return new GenericTermiosSerialPortSocket(portName);
		case "windows":
			switch (MULTIARCH_TUPEL_BUILDER.os_name) {
			case "Windows CE":
				throw new UnsupportedOperationException("Windows CE is currently not supported yet");
			default:
				return new GenericWinSerialPortSocket(portName);
			}
		default:
			throw new UnsupportedOperationException(
					MULTIARCH_TUPEL_BUILDER.os_name + " is currently not supported yet\nSystem.properties:\n"
							+ MULTIARCH_TUPEL_BUILDER.listSystemProperties());
		}
	}

	protected String getPortnamesPath() {
		switch (MULTIARCH_TUPEL_BUILDER.getSimpleOsName()) {
		case "linux": {
			return DEFAULT_LINUX_DEVICE_PATH;
		}
		case "SunOS": {
			return DEFAULT_SUNOS_DEVICE_PATH;
		}
		case "Mac OS X":
		case "Darwin": {
			return DEFAULT_MACOS_DEVICE_PATH;
		}
		case "windows": {
			return DEFAULT_WINDOWS_DEVICE_PATH;
		}
		default: {
			LOG.log(Level.SEVERE, "Unknown OS, os.name: {0} mapped to: {1}",
					new Object[] { System.getProperty("os.name"), MULTIARCH_TUPEL_BUILDER.os_name });
			return null;
		}
		}
	}

	protected Pattern getPortnamesRegExp() {
		switch (MULTIARCH_TUPEL_BUILDER.getSimpleOsName()) {
		case "linux": {
			return Pattern.compile(DEFAULT_LINUX_PORTNAME_PATTERN);
		}
		case "SunOS": {
			return Pattern.compile(DEFAULT_SUNOS_PORTNAME_PATTERN);
		}
		case "Mac OS X":
		case "Darwin": {
			return Pattern.compile(DEFAULT_MACOS_PORTNAME_PATTERN);
		}
		case "windows": {
			return Pattern.compile(DEFAULT_WINDOWS_PORTNAME_PATTERN);
		}
		default: {
			LOG.log(Level.SEVERE, "Unknown OS, os.name: {0} mapped to: {1}",
					new Object[] { System.getProperty("os.name"), MULTIARCH_TUPEL_BUILDER.os_name });
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
		if ("windows".equals(MULTIARCH_TUPEL_BUILDER.getSimpleOsName())) {
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
		if ("windows".equals(MULTIARCH_TUPEL_BUILDER.getSimpleOsName())) {
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
		String[] portNames = getWindowsBasedPortNames();
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

	/**
	 * Universal method for getting port names of _nix based systems
	 */
	protected List<String> getUnixBasedPortNames(String portToInclude, boolean hideBusyPorts) {
		File dir = new File(getPortnamesPath());
		final Pattern pattern = getPortnamesRegExp();
		final List<String> result = new LinkedList<>();

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
		if (!libLoaded) {
			loadNativeLib();
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
	public SerialPortSocket open(String portName, Baudrate baudRate, DataBits dataBits, StopBits stopBits,
			Parity parity, Set<FlowControl> flowControls) throws IOException, IllegalStateException {
		final SerialPortSocket result = createSerialPortSocket(portName);
		try {
			result.open(baudRate, dataBits, stopBits, parity, flowControls);
			return result;
		} catch (Exception e) {
			result.close();
			throw e;
		}
	}

	@Override
	public void getPortNames(BiConsumer<String, Boolean> portNameConsumer) {
		final Pattern pattern = getPortnamesRegExp();
		switch (MULTIARCH_TUPEL_BUILDER.getSimpleOsName()) {
		case "windows":
			String[] portNames = getWindowsBasedPortNames();
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

}
