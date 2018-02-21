package de.ibapl.spsw.tests;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;

import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.SerialPortSocketFactory;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.tests.AbstractTwoPortMultipleBytesTest.ReceiverThread;

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

	protected void assumeRWTest() {
		Assume.assumeNotNull(readSpc);
		Assume.assumeNotNull(writeSpc);
	}

	protected void openDefault() throws Exception {
		openRaw(Baudrate.B9600, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_NONE());
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
	protected void openRaw(Baudrate baudrate, DataBits dataBits, StopBits stopBits, Parity parity,
			Set<FlowControl> flowControl) throws Exception {
		if (readSpc != null) {
			readSpc.openRaw(baudrate, dataBits, stopBits, parity, flowControl);
			Assert.assertEquals(0, readSpc.getOutBufferBytesCount());
			while (readSpc.getInBufferBytesCount() > 0) {
				readSpc.getInputStream().read(new byte[readSpc.getInBufferBytesCount()]);
				Thread.sleep(100);
			}
			Assert.assertEquals(0, readSpc.getInBufferBytesCount());

		}
		if (writeSpc != null && writeSpc != readSpc) {
			writeSpc.openRaw(baudrate, dataBits, stopBits, parity, flowControl);
			Assert.assertEquals(0, readSpc.getOutBufferBytesCount());
			while (readSpc.getInBufferBytesCount() > 0) {
				readSpc.getInputStream().read(new byte[readSpc.getInBufferBytesCount()]);
				Thread.sleep(100);
			}
			Assert.assertEquals(0, readSpc.getInBufferBytesCount());
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

	@BeforeClass
	public static void setUpClass() throws Exception {
		try (InputStream is = AbstractTwoPortMultipleBytesTest.class.getClassLoader()
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

	@Before
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

	@After
	public void tearDown() throws Exception {
		if (writeSpc != null) {
			if (writeSpc != readSpc) {
				if (!writeSpc.isClosed()) {
					writeSpc.close();
					Assert.assertTrue(writeSpc.isClosed());
				}
			}
			writeSpc = null;
		}
		if (readSpc != null) {
			if (!readSpc.isClosed()) {
				readSpc.close();
				Assert.assertTrue(readSpc.isClosed());
			}
			readSpc = null;
		}

		Runtime.getRuntime().gc();
		Runtime.getRuntime().runFinalization();
		if (PORT_RECOVERY_TIME > 0) {
			Thread.sleep(PORT_RECOVERY_TIME * 1000);
		}
	}

}
