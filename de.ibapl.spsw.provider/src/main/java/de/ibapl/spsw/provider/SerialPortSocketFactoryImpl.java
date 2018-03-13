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
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
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
	 * @return
	 */
	public synchronized static SerialPortSocketFactoryImpl singleton() {
		if (singleton == null) {
			singleton = new SerialPortSocketFactoryImpl();
			singleton.activate();
		}
		return singleton;
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

	protected String[] getWindowsBasedPortNames(boolean hideBusyPorts) throws IOException {
		if (!isLibLoaded()) {
			// Make sure lib is loaded to avoid Link error
			loadNativeLib();
		}

		return GenericWinSerialPortSocket.getWindowsBasedPortNames(hideBusyPorts);
	}

	@Override
	public SerialPortSocket createSerialPortSocket(String portName) {
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
			return "/dev/";
		}
		case "SunOS": {
			return "/dev/term/";
		}
		case "Mac OS X":
		case "Darwin": {
			return "/dev/";
		}
		case "windows": {
			return "";
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
			return Pattern.compile("(ttyS|ttyUSB|ttyACM|ttyAMA|rfcomm|ttyO)[0-9]{1,3}");
		}
		case "SunOS": {
			return Pattern.compile("[0-9]*|[a-z]*");
		}
		case "Mac OS X":
		case "Darwin": {
			return Pattern.compile("tty.(serial|usbserial|usbmodem).*");
		}
		case "windows": {
			return Pattern.compile("");
		}
		default: {
			LOG.log(Level.SEVERE, "Unknown OS, os.name: {0} mapped to: {1}",
					new Object[] { System.getProperty("os.name"), MULTIARCH_TUPEL_BUILDER.os_name });
			return null;
		}
		}
	}

	// since 2.1.0 -> Fully rewrited port name comparator
	protected class PortnamesComparator implements Comparator<String> {

		@Override
		public int compare(String valueA, String valueB) {

			if (valueA.equalsIgnoreCase(valueB)) {
				return valueA.compareTo(valueB);
			}

			int minLength = Math.min(valueA.length(), valueB.length());

			int shiftA = 0;
			int shiftB = 0;

			for (int i = 0; i < minLength; i++) {
				char charA = valueA.charAt(i - shiftA);
				char charB = valueB.charAt(i - shiftB);
				if (charA != charB) {
					if (Character.isDigit(charA) && Character.isDigit(charB)) {
						int[] resultsA = getNumberAndLastIndex(valueA, i - shiftA);
						int[] resultsB = getNumberAndLastIndex(valueB, i - shiftB);

						if (resultsA[0] != resultsB[0]) {
							return resultsA[0] - resultsB[0];
						}

						if (valueA.length() < valueB.length()) {
							i = resultsA[1];
							shiftB = resultsA[1] - resultsB[1];
						} else {
							i = resultsB[1];
							shiftA = resultsB[1] - resultsA[1];
						}
					} else {
						if (Character.toLowerCase(charA) - Character.toLowerCase(charB) != 0) {
							return Character.toLowerCase(charA) - Character.toLowerCase(charB);
						}
					}
				}
			}
			return valueA.compareToIgnoreCase(valueB);
		}

		/**
		 * Evaluate port <b>index/number</b> from <b>startIndex</b> to the number end.
		 * For example: for port name <b>serial-123-FF</b> you should invoke this method
		 * with <b>startIndex = 7</b>
		 *
		 * @return If port <b>index/number</b> correctly evaluated it value will be
		 *         returned<br>
		 *         <b>returnArray[0] = index/number</b><br>
		 *         <b>returnArray[1] = stopIndex</b><br>
		 *
		 *         If incorrect:<br>
		 *         <b>returnArray[0] = -1</b><br>
		 *         <b>returnArray[1] = startIndex</b><br>
		 *
		 *         For this name <b>serial-123-FF</b> result is: <b>returnArray[0] =
		 *         123</b><br>
		 *         <b>returnArray[1] = 10</b><br>
		 */
		private int[] getNumberAndLastIndex(String str, int startIndex) {
			String numberValue = "";
			int[] returnValues = { -1, startIndex };
			for (int i = startIndex; i < str.length(); i++) {
				returnValues[1] = i;
				char c = str.charAt(i);
				if (Character.isDigit(c)) {
					numberValue += c;
				} else {
					break;
				}
			}
			try {
				returnValues[0] = Integer.valueOf(numberValue);
			} catch (Exception ex) {
				// Do nothing
			}
			return returnValues;
		}
	};
	// <-since 2.1.0

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
	@Override
	public Set<String> getPortNames(boolean hideBusyPorts) {
		return getPortNames(getPortnamesPath(), getPortnamesRegExp(), new PortnamesComparator(), hideBusyPorts);
	}

	@Override
	public Set<String> getPortNames(String searchPath, boolean hideBusyPorts) {
		return getPortNames(searchPath, getPortnamesRegExp(), new PortnamesComparator(), hideBusyPorts);
	}

	@Override
	public Set<String> getPortNames(Pattern pattern, boolean hideBusyPorts) {
		return getPortNames(getPortnamesPath(), pattern, new PortnamesComparator(), hideBusyPorts);
	}

	@Override
	public Set<String> getPortNames(Comparator<String> comparator, boolean hideBusyPorts) {
		return getPortNames(getPortnamesPath(), getPortnamesRegExp(), comparator, hideBusyPorts);
	}

	@Override
	public Set<String> getPortNames(String searchPath, Pattern pattern, boolean hideBusyPorts) {
		return getPortNames(searchPath, pattern, new PortnamesComparator(), hideBusyPorts);
	}

	@Override
	public Set<String> getPortNames(String searchPath, Comparator<String> comparator, boolean hideBusyPorts) {
		return getPortNames(searchPath, getPortnamesRegExp(), comparator, hideBusyPorts);
	}

	@Override
	public Set<String> getPortNames(Pattern pattern, Comparator<String> comparator, boolean hideBusyPorts) {
		return getPortNames(getPortnamesPath(), pattern, comparator, hideBusyPorts);
	}

	@Override
	public Set<String> getPortNames(String searchPath, Pattern pattern, Comparator<String> comparator,
			boolean hideBusyPorts) {
		if (searchPath == null || pattern == null || comparator == null) {
			return Collections.emptySet();
		}
		switch (MULTIARCH_TUPEL_BUILDER.getSimpleOsName()) {
		case "windows":
			return getWindowsPortNames(pattern, comparator, hideBusyPorts);
		default:
			return getUnixBasedPortNames(searchPath, pattern, comparator, hideBusyPorts);
		}
	}

	/**
	 * Get serial port names in Windows
	 *
	 * @since 2.3.0
	 */
	protected Set<String> getWindowsPortNames(Pattern pattern, Comparator<String> comparator, boolean hideBusyPorts) {
		try {
			String[] portNames = getWindowsBasedPortNames(hideBusyPorts);
			if (portNames == null) {
				return Collections.emptySet();
			}
			TreeSet<String> ports = new TreeSet<>(comparator);
			for (String portName : portNames) {
				if (pattern.matcher(portName).find()) {
					ports.add(portName);
				}
			}
			return ports;
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Universal method for getting port names of _nix based systems
	 */
	protected Set<String> getUnixBasedPortNames(String searchPath, Pattern pattern, Comparator<String> comparator,
			boolean hideBusyPorts) {
		searchPath = (searchPath.equals("") ? searchPath : (searchPath.endsWith("/") ? searchPath : searchPath + "/"));
		File dir = new File(searchPath);
		if (dir.exists() && dir.isDirectory()) {
			File[] files = dir.listFiles();
			if (files.length > 0) {
				TreeSet<String> portsTree = new TreeSet<>(comparator);
				for (File file : files) {
					String fileName = file.getName();
					if (!file.isDirectory() && !file.isFile() && pattern.matcher(fileName).find()) {
						String portName = searchPath + fileName;
						if (hideBusyPorts) {
							try (SerialPortSocket sp = createSerialPortSocket(portName)) {
								sp.open();
								portsTree.add(portName);
							} catch (IOException ex) {
								LOG.log(Level.FINEST, "find busy ports: " + portName, ex);
							}
						} else {
							portsTree.add(portName);
						}
					}
				}
				return portsTree;
			}
		}
		return Collections.emptySet();
	}

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

}
