/*-
 * #%L
 * SPSW Provider
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
package de.ibapl.spsw.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.SerialPortSocketFactory;
import de.ibapl.spsw.api.StopBits;

public abstract class AbstractPortTest {

	protected static final int PORT_RECOVERY_TIME = 0;
	protected static final boolean HARDWARE_SUPPORTS_RTS_CTS = false;

	protected static final Logger LOG = Logger.getLogger("SpswTests");
	private static String readSerialPortName;
	private static String writeSerialPortName;

	protected SerialPortSocket readSpc;
	protected SerialPortSocket writeSpc;

	protected abstract SerialPortSocketFactory getSerialPortSocketFactory();

	protected void setBaudrate(Baudrate baudrate) throws IOException {
		if (readSpc != null) {
			readSpc.setBaudrate(baudrate);
		}
		if (writeSpc != null && writeSpc != readSpc) {
			writeSpc.setBaudrate(baudrate);
		}
	}

	protected void assumeRTest() {
		assumeTrue(readSpc != null);
	}

	protected void assumeWTest() {
		assumeTrue(writeSpc != null);
	}

	protected void assumeRWTest() {
		assumeRTest();
		assumeWTest();
	}

	protected void openDefault() throws Exception {
		open(Baudrate.B9600, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_NONE());
	}

	/**
	 * Opens the port and make sure that In and out buffer are empty
	 * 
	 * @param baudrate
	 * @param dataBits
	 * @param stopBits
	 * @param parity
	 * @param flowControl
	 * @throws Exception
	 */
	protected void open(Baudrate baudrate, DataBits dataBits, StopBits stopBits, Parity parity,
			Set<FlowControl> flowControl) throws Exception {
		if (readSpc != null) {
			readSpc.open(baudrate, dataBits, stopBits, parity, flowControl);
			assertEquals(0, readSpc.getOutBufferBytesCount(), "Can't start test: OutBuffer is not empty");
			while (readSpc.getInBufferBytesCount() > 0) {
				readSpc.getInputStream().read(new byte[readSpc.getInBufferBytesCount()]);
				Thread.sleep(100);
			}
			assertEquals(0, readSpc.getInBufferBytesCount(), "Can't start test: InBuffer is not empty");

		}
		if (writeSpc != null && writeSpc != readSpc) {
			writeSpc.open(baudrate, dataBits, stopBits, parity, flowControl);
			assertEquals(0, readSpc.getOutBufferBytesCount(), "Can't start test: OutBuffer is not empty");
			while (readSpc.getInBufferBytesCount() > 0) {
				readSpc.getInputStream().read(new byte[readSpc.getInBufferBytesCount()]);
				Thread.sleep(100);
			}
			assertEquals(0, readSpc.getInBufferBytesCount(), "Can't start test: InBuffer is not empty");
		}
	}

	protected void setTimeouts(int interByteReadTimeout, int overallReadTimeout, int overallWriteTimeout)
			throws Exception {
		if (readSpc != null) {
			readSpc.setTimeouts(interByteReadTimeout, overallReadTimeout, overallWriteTimeout);
		}
		if (writeSpc != null && writeSpc != readSpc) {
			writeSpc.setTimeouts(interByteReadTimeout, overallReadTimeout, overallWriteTimeout);
		}
	}

	protected void printPorts() throws IOException {
		if (readSpc != null && writeSpc != null && readSpc != writeSpc) {
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("\n\tName:        %-20s %-20s\n", readSpc.getPortName(), writeSpc.getPortName()));
			sb.append(String.format("\tBaudrate:    %-20d %-20d\n", readSpc.getBaudrate().value,
					writeSpc.getBaudrate().value));
			sb.append(String.format("\tDataBits:    %-20d %-20d\n", readSpc.getDatatBits().value,
					writeSpc.getDatatBits().value));
			sb.append(String.format("\tStopBits:    %-20f %-20f\n", readSpc.getStopBits().value,
					writeSpc.getStopBits().value));
			sb.append(String.format("\tParity:      %-20s %-20s\n", readSpc.getParity().name(),
					writeSpc.getParity().name()));
			sb.append(String.format("\tFlowControl: %-20s %-20s\n", readSpc.getFlowControl().toString(),
					writeSpc.getFlowControl().toString()));
			sb.append(String.format("\tIntebyteReadTimeout:    %-20d %-20d\n", readSpc.getInterByteReadTimeout(),
					writeSpc.getInterByteReadTimeout()));
			sb.append(String.format("\tOverallReadTimeout:    %-20d %-20d\n", readSpc.getOverallReadTimeout(),
					writeSpc.getOverallReadTimeout()));
			sb.append(String.format("\tOverallWriteTimeout:    %-20d %-20d\n", readSpc.getOverallWriteTimeout(),
					writeSpc.getOverallWriteTimeout()));

			LOG.log(Level.INFO, sb.toString());
		} else {
			@SuppressWarnings("resource")
			SerialPortSocket spc = readSpc != null ? readSpc : writeSpc;
			if (spc != null) {
				StringBuilder sb = new StringBuilder();
				sb.append(String.format("\n\tName:        %-20s\n", spc.getPortName()));
				sb.append(String.format("\tBaudrate:    %-20d\n", spc.getBaudrate().value));
				sb.append(String.format("\tDataBits:    %-20d\n", spc.getDatatBits().value));
				sb.append(String.format("\tStopBits:    %-20f\n", spc.getStopBits().value));
				sb.append(String.format("\tParity:      %-20s\n", spc.getParity().name()));
				sb.append(String.format("\tFlowControl: %-20s\n", spc.getFlowControl().toString()));
				sb.append(String.format("\tIntebyteReadTimeout:    %-20d\n", spc.getInterByteReadTimeout()));
				sb.append(String.format("\tOverallReadTimeout:    %-20d\n", spc.getOverallReadTimeout()));
				sb.append(String.format("\tOverallWriteTimeout:    %-20d\n", spc.getOverallWriteTimeout()));

				LOG.log(Level.INFO, sb.toString());
			}
		}
	}

	@BeforeAll
	public static void setUpClass() throws Exception {
		try (InputStream is = AbstractPortTest.class.getClassLoader()
				.getResourceAsStream("junit-spsw-config.properties")) {
			if (is == null) {
				readSerialPortName = null;
				writeSerialPortName = null;
			} else {
				Properties p = new Properties();
				p.load(is);
				readSerialPortName = p.getProperty("readPort", null);
				writeSerialPortName = p.getProperty("writePort", null);
			}
		}
	}

	@BeforeEach
	public void setUp() throws Exception {
		if (readSerialPortName != null) {
			readSpc = getSerialPortSocketFactory().createSerialPortSocket(readSerialPortName);
		}
		if (writeSerialPortName != null) {
			if (writeSerialPortName.equals(readSerialPortName)) {
				writeSpc = readSpc;
			} else {
				writeSpc = getSerialPortSocketFactory().createSerialPortSocket(writeSerialPortName);
			}
		}
	}

	@AfterEach
	public void tearDown() throws Exception {
		if (writeSpc != null) {
			if (writeSpc != readSpc) {
				if (!writeSpc.isClosed()) {
					writeSpc.close();
					assertTrue(writeSpc.isClosed(), "Can't close write port");
				}
			}
			writeSpc = null;
		}
		if (readSpc != null) {
			if (!readSpc.isClosed()) {
				readSpc.close();
				assertTrue(readSpc.isClosed(), "Can't close read port");
			}
			readSpc = null;
		}

		Runtime.getRuntime().gc();
		Runtime.getRuntime().runFinalization();
		if (PORT_RECOVERY_TIME > 0) {
			Thread.sleep(PORT_RECOVERY_TIME * 1000);
		}
	}

	protected void open(PortConfiguration pc) throws Exception {
		open(pc.getBaudrate(), pc.getDataBits(), pc.getStopBits(), pc.getParity(), pc.getFlowControl());
		setTimeouts(pc.getInterByteReadTimeout(), pc.getOverallReadTimeout(), pc.getOverallWriteTimeout());
	}

}
