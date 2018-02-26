package de.ibapl.spsw.tests;

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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.PortBusyException;
import de.ibapl.spsw.api.SerialPortException;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.api.TimeoutIOException;
import de.ibapl.spsw.api.SerialPortSocketFactory;
import java.lang.ref.WeakReference;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit test for simple App.
 */
public abstract class AbstractOnePortTest extends AbstractPortTest {

	private void testFlowControl(Set<FlowControl> expected) throws Exception {
		writeSpc.setFlowControl(expected);
		Set<FlowControl> result = writeSpc.getFlowControl();
		Assert.assertEquals(expected, result);
	}

	@Test
	public void testFlowControl() throws Exception {
		Assume.assumeNotNull(writeSpc);
		LOG.log(Level.INFO, "run testFlowControl");
		openDefault();

		testFlowControl(FlowControl.getFC_NONE());
		testFlowControl(FlowControl.getFC_RTS_CTS());
		testFlowControl(FlowControl.getFC_XON_XOFF_IN());
		testFlowControl(FlowControl.getFC__XON_XOFF_OUT());
		testFlowControl(FlowControl.getFC_XON_XOFF());
		testFlowControl(FlowControl.getFC_RTS_CTS_XON_XOFF());
	}

	@Test
	public void testRTS() throws Exception {
		Assume.assumeNotNull(writeSpc);
		LOG.log(Level.INFO, "run testRTS");
		openDefault();

		writeSpc.setRTS(true);
		// if (spc instanceof GenericTermiosSerialPortSocket) {
		// Assert.assertTrue(((GenericTermiosSerialPortSocket) spc).isRTS());
		// }
		writeSpc.setRTS(false);
		// if (spc instanceof GenericTermiosSerialPortSocket) {
		// Assert.assertFalse(((GenericTermiosSerialPortSocket) spc).isRTS());
		// }
	}

	@Test
	public void testDTR() throws Exception {
		Assume.assumeNotNull(writeSpc);
		LOG.log(Level.INFO, "run testDTR");
		openDefault();

		writeSpc.setDTR(true);
		// if (spc instanceof GenericTermiosSerialPortSocket) {
		// Assert.assertTrue(((GenericTermiosSerialPortSocket) spc).isDTR());
		// }
		writeSpc.setDTR(false);
		// if (spc instanceof GenericTermiosSerialPortSocket) {
		// Assert.assertFalse(((GenericTermiosSerialPortSocket) spc).isDTR());
		// }
	}

	@Test
	public void testIncommingRI() throws Exception {
		Assume.assumeNotNull(readSpc);
		LOG.log(Level.INFO, "run testIncommingRI");
		readSpc.openAsIs();

		readSpc.isIncommingRI();
	}

	@Test
	public void testCTS() throws Exception {
		Assume.assumeNotNull(readSpc);
		LOG.log(Level.INFO, "run testDataBits");
		openDefault();

		readSpc.isCTS();
	}

	@Test
	public void testDSR() throws Exception {
		Assume.assumeNotNull(readSpc);
		LOG.log(Level.INFO, "run testDSR");
		openDefault();

		readSpc.isDSR();
	}

	@Test
	public void testXONChar() throws Exception {
		Assume.assumeNotNull(writeSpc);
		LOG.log(Level.INFO, "run testXONChar");
		openDefault();

		LOG.log(Level.INFO, "port: {0}", writeSpc);
		char c = writeSpc.getXONChar();
		writeSpc.setXONChar('a');
		Assert.assertEquals('a', writeSpc.getXONChar());
		writeSpc.setXONChar('z');
		Assert.assertEquals('z', writeSpc.getXONChar());
		writeSpc.setXONChar(c);
	}

	@Test
	public void testXOFFChar() throws Exception {
		Assume.assumeNotNull(writeSpc);
		LOG.log(Level.INFO, "run testXOFFChar");
		openDefault();

		char c = writeSpc.getXOFFChar();
		writeSpc.setXOFFChar('a');
		Assert.assertEquals('a', writeSpc.getXOFFChar());
		writeSpc.setXOFFChar('z');
		Assert.assertEquals('z', writeSpc.getXOFFChar());
		writeSpc.setXOFFChar(c);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_2_StopBitsAnd_5_DataBits() throws Exception {
		Assume.assumeNotNull(readSpc);
		LOG.log(Level.INFO, "run testStopBits");
		openDefault();

		readSpc.setStopBits(StopBits.SB_1);
		Assert.assertEquals(StopBits.SB_1, readSpc.getStopBits());

		readSpc.setDataBits(DataBits.DB_5);
		readSpc.setStopBits(StopBits.SB_2);
		fail("Could set 2 stopBits @5 dataBits");
	}

	@Test()
	public void test_1_5_StopBitsAnd_6_7_8_DataBits() throws Exception {
		Assume.assumeNotNull(readSpc);
		LOG.log(Level.INFO, "run testStopBits");
		openDefault();

		try {
			readSpc.setStopBits(StopBits.SB_1);
			Assert.assertEquals(StopBits.SB_1, readSpc.getStopBits());
			readSpc.setDataBits(DataBits.DB_6);
			readSpc.setStopBits(StopBits.SB_1_5);
			fail("Could set 1.5 stopbits @6 dataBits");
		} catch (IllegalArgumentException iae) {
		}
		try {
			readSpc.setStopBits(StopBits.SB_1);
			Assert.assertEquals(StopBits.SB_1, readSpc.getStopBits());
			readSpc.setDataBits(DataBits.DB_7);
			readSpc.setStopBits(StopBits.SB_1_5);
			fail("Could set 1.5 stopbits @7 dataBits");
		} catch (IllegalArgumentException iae) {
		}
		try {
			readSpc.setStopBits(StopBits.SB_1);
			Assert.assertEquals(StopBits.SB_1, readSpc.getStopBits());
			readSpc.setDataBits(DataBits.DB_8);
			readSpc.setStopBits(StopBits.SB_1_5);
			fail("Could set 1.5 stopbits @8 dataBits");
		} catch (IllegalArgumentException iae) {
		}
	}

	@Test
	public void testStopBits() throws Exception {
		Assume.assumeNotNull(readSpc);
		LOG.log(Level.INFO, "run testStopBits");
		openDefault();

		readSpc.setStopBits(StopBits.SB_1);
		Assert.assertEquals(StopBits.SB_1, readSpc.getStopBits());

		readSpc.setDataBits(DataBits.DB_5);
		readSpc.setStopBits(StopBits.SB_1_5);
		Assert.assertEquals(StopBits.SB_1_5, readSpc.getStopBits());
		readSpc.setStopBits(StopBits.SB_1);
		Assert.assertEquals(StopBits.SB_1, readSpc.getStopBits());

		readSpc.setDataBits(DataBits.DB_6);
		readSpc.setStopBits(StopBits.SB_2);
		Assert.assertEquals(StopBits.SB_2, readSpc.getStopBits());
		readSpc.setStopBits(StopBits.SB_1);
		Assert.assertEquals(StopBits.SB_1, readSpc.getStopBits());

		readSpc.setDataBits(DataBits.DB_7);
		readSpc.setStopBits(StopBits.SB_2);
		Assert.assertEquals(StopBits.SB_2, readSpc.getStopBits());
		readSpc.setStopBits(StopBits.SB_1);
		Assert.assertEquals(StopBits.SB_1, readSpc.getStopBits());

		readSpc.setDataBits(DataBits.DB_8);
		readSpc.setStopBits(StopBits.SB_2);
		Assert.assertEquals(StopBits.SB_2, readSpc.getStopBits());
		readSpc.setStopBits(StopBits.SB_1);
		Assert.assertEquals(StopBits.SB_1, readSpc.getStopBits());
	}

	@Test
	public void testDataBits() throws Exception {
		Assume.assumeNotNull(readSpc);
		LOG.log(Level.INFO, "run testDataBits");
		openDefault();

		for (DataBits db : DataBits.values()) {
			try {
				readSpc.setDataBits(db);
			} catch (Exception e) {
				Assert.fail(e.getMessage() + "dataBits: " + db);
			}
			Assert.assertEquals(db.toString() + "Failed", db, readSpc.getDatatBits());
		}
	}

	@Test
	public void testBaudrate() throws Exception {
		Assume.assumeNotNull(readSpc);
		LOG.log(Level.INFO, "run testBaudrate");
		openDefault();

		for (Baudrate b : Baudrate.values()) {
			try {
				readSpc.setBaudrate(b);
				Assert.assertEquals("testBaudrate", b, readSpc.getBaudrate());
			} catch (IllegalArgumentException iae) {
				// This is Hardware dependent watch for logs ...
				LOG.log(Level.WARNING, "Error setBaudrate " + b, iae);
			}
		}
	}

	/**
	 * Write byte[1024] blocks with set RTS/CTS so the port will actually block The
	 * logs give information about the actual behavior If the port does not support
	 * RTS/CTS (like MCS7820 on linux TODO BUG?)it will caught by the timeout.
	 * 
	 *
	 * @throws Exception
	 */
	@Test
	public void testWriteBytesTimeout() throws Exception {
		Assume.assumeNotNull(writeSpc);
		Assume.assumeTrue(HARDWARE_SUPPORTS_RTS_CTS);
		LOG.log(Level.INFO, "run testWriteBytesTimeout");

		// Set a high baudrate to speed up things
		openRaw(Baudrate.B115200, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_RTS_CTS());
		//Disabling timeout on the reading side - so the writing side has a chance to fill the buffer...
		readSpc.setFlowControl(FlowControl.getFC_NONE());
		readSpc.setRTS(false);
		Thread.sleep(100);
		assertFalse("CTS is true; No chance to ever fill the buffer", writeSpc.isCTS());
		setTimeouts(100, 1000, 1000);

		byte[] data = new byte[1024];
		int round = 1;
		int i = 0;
		int dataWritten;
		int overallDataWritten = 0;
		do {
			dataWritten = 0;
			try {
				for (i = 0; i < 1024; i++) {
					writeSpc.getOutputStream().write(data);
				}
				fail("RTS/CTS enabled so a timeout is expectd when the buffer is full!");
			} catch (TimeoutIOException e) {
				dataWritten = ((i * data.length) + e.bytesTransferred);
				LOG.log(Level.INFO, "Round: " + round + ": " + dataWritten + " bytes written; OutBuf:  "
						+ writeSpc.getOutBufferBytesCount());
				assertTrue(true);
			}
			try {
				writeSpc.getOutputStream().flush();
				// TODO NOT on winfail();
			} catch (TimeoutIOException e) {
				LOG.log(Level.INFO, "Round: " + round + " Flush; OutBuf:  " + writeSpc.getOutBufferBytesCount());
				assertTrue(true);
			}
			round++;
			overallDataWritten += dataWritten;
			assertTrue("Rounds exceed maximum of " + 100, 100 > round);
		} while (dataWritten > 0);

		LOG.log(Level.INFO, "Wrote: " + overallDataWritten + " in " + round + " rounds; OutBuf:  "
				+ writeSpc.getOutBufferBytesCount());
		LOG.log(Level.INFO, "disable flow control to sped up closing");
		writeSpc.setFlowControl(FlowControl.getFC_NONE());
		LOG.log(Level.INFO, "will close port");
		writeSpc.close();
		Assert.assertTrue(writeSpc.isClosed());
		LOG.log(Level.INFO, "port closed");
	}

	/**
	 * Write a single byte with set RTS/CTS so the port will actually block The logs
	 * give information about the actual behavior
	 *
	 * @throws Exception
	 */
	@Test
	public void testWriteSingleByteTimeout() throws Exception {
		Assume.assumeNotNull(writeSpc);
		Assume.assumeTrue(HARDWARE_SUPPORTS_RTS_CTS);
		LOG.log(Level.INFO, "run testWriteSingleByteTimeout");

		// Set a high baudrate to speed up things
		openRaw(Baudrate.B115200, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_RTS_CTS());
		//Disabling timeout on the reading side - so the writing side has a chance to fill the buffer...
		readSpc.setFlowControl(FlowControl.getFC_NONE());
		readSpc.setRTS(false);
		Thread.sleep(100);
		assertFalse("CTS is true; No chance to ever fill the buffer", writeSpc.isCTS());
		setTimeouts(100, 100, 100);

		int round = 1;
		int i = 0;
		int dataWritten;
		int overallDataWritten = 0;
		do {
			dataWritten = 0;
			try {
				for (i = 0; i < 1048576; i++) {
					writeSpc.getOutputStream().write(0);
				}
				fail("RTS/CTS enabled so a timeout is expectd when the buffer is full!");
			} catch (TimeoutIOException e) {
				dataWritten = i + e.bytesTransferred;
				LOG.log(Level.INFO, "Round: " + round + ": " + dataWritten + " bytes written; OutBuf:  "
						+ writeSpc.getOutBufferBytesCount());
				assertTrue(true);
			}
			try {
				writeSpc.getOutputStream().flush();
				// TODO not on win??? fail();
			} catch (TimeoutIOException e) {
				LOG.log(Level.INFO, "Round: " + round + " Flush; OutBuf:  " + writeSpc.getOutBufferBytesCount());
				assertTrue(true);
			}
			round++;
			overallDataWritten += dataWritten;

		} while (dataWritten > 0);

		LOG.log(Level.INFO, "Wrote: " + overallDataWritten + " in " + round + " rounds; OutBuf:  "
				+ writeSpc.getOutBufferBytesCount());
		LOG.log(Level.INFO, "disable flow control to sped up closing");
		writeSpc.setFlowControl(FlowControl.getFC_NONE());
		LOG.log(Level.INFO, "will close port");
		writeSpc.close();
		Assert.assertTrue(writeSpc.isClosed());
		LOG.log(Level.INFO, "port closed");
	}

}
