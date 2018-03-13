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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.InterruptedIOException;
import java.util.Set;
import java.util.logging.Level;

import org.junit.jupiter.api.Test;

import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.api.TimeoutIOException;

/**
 * Unit test for simple App.
 */
public abstract class AbstractOnePortTest extends AbstractPortTest {

	private void testFlowControl(Set<FlowControl> expected) throws Exception {
		writeSpc.setFlowControl(expected);
		Set<FlowControl> result = writeSpc.getFlowControl();
		assertEquals(expected, result);
	}

	@Test
	public void testFlowControl() throws Exception {
		assumeWTest();
		LOG.log(Level.INFO, "run testFlowControl");
		openDefault();

		testFlowControl(FlowControl.getFC_NONE());
		testFlowControl(FlowControl.getFC_RTS_CTS());
		testFlowControl(FlowControl.getFC_XON_XOFF_IN());
		testFlowControl(FlowControl.getFC__XON_XOFF_OUT());
		testFlowControl(FlowControl.getFC_XON_XOFF());
		testFlowControl(FlowControl.getFC_RTS_CTS_XON_XOFF());
		testFlowControl(FlowControl.getFC_NONE());
	}

	@Test
	public void testRTS() throws Exception {
		assumeWTest();
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
		assumeWTest();
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
		assumeRTest();
		LOG.log(Level.INFO, "run testIncommingRI");
		readSpc.open();

		readSpc.isIncommingRI();
	}

	@Test
	public void testCTS() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testDataBits");
		openDefault();

		readSpc.isCTS();
	}

	@Test
	public void testDSR() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testDSR");
		openDefault();

		readSpc.isDSR();
	}

	@Test
	public void testXONChar() throws Exception {
		assumeWTest();
		LOG.log(Level.INFO, "run testXONChar");
		openDefault();

		LOG.log(Level.INFO, "port: {0}", writeSpc);
		char c = writeSpc.getXONChar();
		writeSpc.setXONChar('a');
		assertEquals('a', writeSpc.getXONChar());
		writeSpc.setXONChar('z');
		assertEquals('z', writeSpc.getXONChar());
		writeSpc.setXONChar(c);
	}

	@Test
	public void testXOFFChar() throws Exception {
		assumeWTest();
		LOG.log(Level.INFO, "run testXOFFChar");
		openDefault();

		char c = writeSpc.getXOFFChar();
		writeSpc.setXOFFChar('a');
		assertEquals('a', writeSpc.getXOFFChar());
		writeSpc.setXOFFChar('z');
		assertEquals('z', writeSpc.getXOFFChar());
		writeSpc.setXOFFChar(c);
	}

	@Test()
	public void test_2_StopBitsAnd_5_DataBits() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testStopBits");
		openDefault();

		readSpc.setStopBits(StopBits.SB_1);
		assertEquals(StopBits.SB_1, readSpc.getStopBits());

		readSpc.setDataBits(DataBits.DB_5);
		assertThrows(IllegalArgumentException.class, () -> {
			readSpc.setStopBits(StopBits.SB_2);
		});
	}

	@Test()
	public void test_1_5_StopBitsAnd_6_7_8_DataBits() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testStopBits");
		openDefault();

		readSpc.setStopBits(StopBits.SB_1);
		assertEquals(StopBits.SB_1, readSpc.getStopBits());
		readSpc.setDataBits(DataBits.DB_6);
		assertThrows(IllegalArgumentException.class, () -> {
			readSpc.setStopBits(StopBits.SB_1_5);
		});

		readSpc.setStopBits(StopBits.SB_1);
		assertEquals(StopBits.SB_1, readSpc.getStopBits());
		readSpc.setDataBits(DataBits.DB_7);
		assertThrows(IllegalArgumentException.class, () -> {
			readSpc.setStopBits(StopBits.SB_1_5);
		});
		readSpc.setStopBits(StopBits.SB_1);
		assertEquals(StopBits.SB_1, readSpc.getStopBits());
		readSpc.setDataBits(DataBits.DB_8);
		assertThrows(IllegalArgumentException.class, () -> {
			readSpc.setStopBits(StopBits.SB_1_5);
		});
	}

	@Test
	public void testStopBits() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testStopBits");
		openDefault();

		readSpc.setStopBits(StopBits.SB_1);
		assertEquals(StopBits.SB_1, readSpc.getStopBits());

		readSpc.setDataBits(DataBits.DB_5);
		readSpc.setStopBits(StopBits.SB_1_5);
		assertEquals(StopBits.SB_1_5, readSpc.getStopBits());
		readSpc.setStopBits(StopBits.SB_1);
		assertEquals(StopBits.SB_1, readSpc.getStopBits());

		readSpc.setDataBits(DataBits.DB_6);
		readSpc.setStopBits(StopBits.SB_2);
		assertEquals(StopBits.SB_2, readSpc.getStopBits());
		readSpc.setStopBits(StopBits.SB_1);
		assertEquals(StopBits.SB_1, readSpc.getStopBits());

		readSpc.setDataBits(DataBits.DB_7);
		readSpc.setStopBits(StopBits.SB_2);
		assertEquals(StopBits.SB_2, readSpc.getStopBits());
		readSpc.setStopBits(StopBits.SB_1);
		assertEquals(StopBits.SB_1, readSpc.getStopBits());

		readSpc.setDataBits(DataBits.DB_8);
		readSpc.setStopBits(StopBits.SB_2);
		assertEquals(StopBits.SB_2, readSpc.getStopBits());
		readSpc.setStopBits(StopBits.SB_1);
		assertEquals(StopBits.SB_1, readSpc.getStopBits());
	}

	@Test
	public void testDataBits() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testDataBits");
		openDefault();

		for (DataBits db : DataBits.values()) {
			try {
				readSpc.setDataBits(db);
			} catch (Exception e) {
				fail(e.getMessage() + "dataBits: " + db);
			}
			assertEquals(db, readSpc.getDatatBits(), db.toString() + "Failed");
		}
	}

	@Test
	public void testBaudrate() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testBaudrate");
		openDefault();

		for (Baudrate b : Baudrate.values()) {
			try {
				readSpc.setBaudrate(b);
				assertEquals(b, readSpc.getBaudrate(), "testBaudrate");
			} catch (IllegalArgumentException iae) {
				// This is Hardware dependent watch for logs ...
				LOG.log(Level.WARNING, "Error setBaudrate " + b, iae);
			}
		}
	}

	/**
	 * Write byte[1024] blocks with set RTS/CTS so the port will actually block The
	 * logs give information about the actual behavior. If the port does not support
	 * RTS/CTS (like MCS7820 on linux TODO BUG?)it will caught by the timeout.
	 * 
	 *
	 * @throws Exception
	 */
	@Test
	public void testWriteBytesTimeout() throws Exception {
		assumeWTest();
		assumeTrue(HARDWARE_SUPPORTS_RTS_CTS);
		LOG.log(Level.INFO, "run testWriteBytesTimeout");

		// Set a high baudrate to speed up things
		open(Baudrate.B115200, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_RTS_CTS());
		// Disabling timeout on the reading side - so the writing side has a chance to
		// fill the buffer...
		readSpc.setFlowControl(FlowControl.getFC_NONE());
		readSpc.setRTS(false);
		Thread.sleep(100);
		assertFalse(writeSpc.isCTS(), "CTS is true; No chance to ever fill the buffer");
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
			assertTrue(100 > round, "Rounds exceed maximum of " + 100);
		} while (dataWritten > 0);

		LOG.log(Level.INFO, "Wrote: " + overallDataWritten + " in " + round + " rounds; OutBuf:  "
				+ writeSpc.getOutBufferBytesCount());
		LOG.log(Level.INFO, "disable flow control to sped up closing");
		writeSpc.setFlowControl(FlowControl.getFC_NONE());
		LOG.log(Level.INFO, "will close port");
		writeSpc.close();
		assertTrue(writeSpc.isClosed());
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
		assumeWTest();
		assumeTrue(HARDWARE_SUPPORTS_RTS_CTS);
		LOG.log(Level.INFO, "run testWriteSingleByteTimeout");

		// Set a high baudrate to speed up things
		open(Baudrate.B115200, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_RTS_CTS());
		// Disabling timeout on the reading side - so the writing side has a chance to
		// fill the buffer...
		readSpc.setFlowControl(FlowControl.getFC_NONE());
		readSpc.setRTS(false);
		Thread.sleep(100);
		assertFalse(writeSpc.isCTS(), "CTS is true; No chance to ever fill the buffer");
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
		assertTrue(writeSpc.isClosed());
		LOG.log(Level.INFO, "port closed");
	}

	
	private final static int _16MB = 1024 * 1024 * 16;
	@Test
	public void testWrite16MBChunkInfiniteWrite() throws Exception {
		testWrite16MBChunk(0);
	}

	@Test
	public void testWrite16MBChunk() throws Exception {
		testWrite16MBChunk( 1000 + 2 * SerialPortSocket.calculateMillisForBytes(_16MB, Baudrate.B1000000, DataBits.DB_8, StopBits.SB_1, Parity.NONE));
	}

	public void testWrite16MBChunk(int writeTimeout) throws Exception {
		assumeWTest();
		LOG.log(Level.INFO, "run testWriteBytesTimeout");
		if (writeTimeout == -1) {
			LOG.log(Level.INFO, "infinite timeout");
		} else {
			LOG.log(Level.INFO, "timeout in ms:" + writeTimeout);
		}

		// Set a high baudrate to speed up things
		open(Baudrate.B1000000, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_NONE());
		setTimeouts(100, 1000, writeTimeout);

		byte[] data = new byte[_16MB];
		int dataWritten = 0;
		try {
			writeSpc.getOutputStream().write(data);
			dataWritten = data.length;
		} catch (TimeoutIOException e) {
			dataWritten = e.bytesTransferred;
			LOG.log(Level.SEVERE, "Timeout: " + dataWritten + " bytes of: " + data.length + " written; OutBuf:  "
					+ writeSpc.getOutBufferBytesCount() + " EX: " + e);
			fail("Timeout only " + e.bytesTransferred + " bytes sent");
	} catch (InterruptedIOException iio) {
		dataWritten = iio.bytesTransferred;
		LOG.log(Level.SEVERE, "Timeout: " + dataWritten + " bytes of: " + data.length + " written; OutBuf:  "
				+ writeSpc.getOutBufferBytesCount() + " EX: " + iio);
		fail("Timeout only " + iio.bytesTransferred + " bytes sent");
	}
		try {
			writeSpc.getOutputStream().flush();
			// TODO NOT on winfail();
		} catch (TimeoutIOException e) {
			LOG.log(Level.SEVERE, "Timeoutt on Flush; OutBuf:  " + writeSpc.getOutBufferBytesCount());
			assertTrue(true);
		}

		LOG.log(Level.INFO, "Wrote: " + dataWritten + " OutBuf:  " + writeSpc.getOutBufferBytesCount());
		LOG.log(Level.INFO, "will close port");
		writeSpc.close();
		assertTrue(writeSpc.isClosed());
		LOG.log(Level.INFO, "port closed");
	}

}
