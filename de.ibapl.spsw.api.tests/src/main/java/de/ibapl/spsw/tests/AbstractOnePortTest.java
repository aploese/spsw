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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.ref.WeakReference;
import java.time.Duration;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.api.TimeoutIOException;
import de.ibapl.spsw.tests.tags.BaselineTest;
import de.ibapl.spsw.tests.tags.NotSupportedByAllDevices;
import de.ibapl.spsw.tests.tags.RtsCtsTest;
import de.ibapl.spsw.tests.tags.SlowTest;

/**
 * Unit test for simple App.
 */
public abstract class AbstractOnePortTest extends AbstractPortTest {

	private void testFlowControl(Set<FlowControl> expected) throws Exception {
		writeSpc.setFlowControl(expected);
		Set<FlowControl> result = writeSpc.getFlowControl();
		assertEquals(expected, result);
	}

	@BaselineTest
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

		testFlowControl(EnumSet.of(FlowControl.XON_XOFF_IN));
		testFlowControl(EnumSet.of(FlowControl.XON_XOFF_OUT));
	}

	@BaselineTest
	@Test
	@EnabledOnOs({ OS.LINUX })
	public void testFlowControl_LINUX() throws Exception {
		assumeWTest();
		LOG.log(Level.INFO, "run testFlowControl");
		openDefault();

		IllegalArgumentException iae = assertThrows(IllegalArgumentException.class, () -> {
			testFlowControl(EnumSet.of(FlowControl.RTS_CTS_IN));
		});
		assertEquals("Can only set RTS/CTS for both in and out", iae.getMessage());
		iae = assertThrows(IllegalArgumentException.class, () -> {
			testFlowControl(EnumSet.of(FlowControl.RTS_CTS_OUT));
		});
		assertEquals("Can only set RTS/CTS for both in and out", iae.getMessage());
	}

	@BaselineTest
	@Test
	@EnabledOnOs({ OS.WINDOWS })
	public void testFlowControl_WINDOWS() throws Exception {
		assumeWTest();
		LOG.log(Level.INFO, "run testFlowControl");
		openDefault();

		testFlowControl(EnumSet.of(FlowControl.RTS_CTS_IN));
		testFlowControl(EnumSet.of(FlowControl.RTS_CTS_OUT));
	}

	@BaselineTest
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

	@BaselineTest
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

	@BaselineTest
	@Test
	public void testRI() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testIncommingRI");
		readSpc.open();

		readSpc.isRI();
	}

	@BaselineTest
	@Test
	public void testCTS() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testDataBits");
		openDefault();

		readSpc.isCTS();
	}

	@BaselineTest
	@Test
	public void testDSR() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testDSR");
		openDefault();

		readSpc.isDSR();
	}

	@BaselineTest
	@Test
	public void testDCD() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testDCD");
		openDefault();

		readSpc.isDCD();
	}

	@BaselineTest
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

	@BaselineTest
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

	/**
	 * The FTDI driver fails to set 5 data bits ...
	 * 
	 * @throws Exception
	 */
	@NotSupportedByAllDevices
	@Test()
	public void test_StopBits_5_DataBits() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testStopBits");
		openDefault();

		readSpc.setStopBits(StopBits.SB_1);
		assertEquals(StopBits.SB_1, readSpc.getStopBits());

		readSpc.setDataBits(DataBits.DB_5);

		readSpc.setStopBits(StopBits.SB_1_5);
		assertEquals(StopBits.SB_1_5, readSpc.getStopBits());

		assertThrows(IllegalArgumentException.class, () -> {
			readSpc.setStopBits(StopBits.SB_2);
		});

		// Now test set 8 data bits with 2 stop bits and switching to 5 data bits
		readSpc.setDataBits(DataBits.DB_8);
		readSpc.setStopBits(StopBits.SB_2);
		assertEquals(StopBits.SB_2, readSpc.getStopBits());
		readSpc.setDataBits(DataBits.DB_5);
		assertEquals(StopBits.SB_1_5, readSpc.getStopBits());
	}

	/**
	 * The FTDI driver fails to set 6 data bits ...
	 * 
	 * @throws Exception
	 */
	@NotSupportedByAllDevices
	@Test()
	public void test_StopBits_6_DataBits() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testStopBits");
		openDefault();

		readSpc.setStopBits(StopBits.SB_1);
		assertEquals(StopBits.SB_1, readSpc.getStopBits());

		readSpc.setDataBits(DataBits.DB_6);

		readSpc.setStopBits(StopBits.SB_2);
		assertEquals(StopBits.SB_2, readSpc.getStopBits());

		assertThrows(IllegalArgumentException.class, () -> {
			readSpc.setStopBits(StopBits.SB_1_5);
		});
	}

	@NotSupportedByAllDevices
	@Test()
	public void test_switch_5_To_6_DataBits() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testStopBits");
		openDefault();

		readSpc.setDataBits(DataBits.DB_6);
		assertEquals(StopBits.SB_2, readSpc.getStopBits());

		// Now test set 5 data bits with 1.5 stop bits and switching to 6 data bits
		readSpc.setDataBits(DataBits.DB_5);
		assertEquals(StopBits.SB_1_5, readSpc.getStopBits());
		readSpc.setDataBits(DataBits.DB_6);
		assertEquals(StopBits.SB_2, readSpc.getStopBits());
	}

	@BaselineTest
	@Test()
	public void test_StopBits_7_DataBits() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testStopBits");
		openDefault();

		readSpc.setStopBits(StopBits.SB_1);
		assertEquals(StopBits.SB_1, readSpc.getStopBits());

		readSpc.setDataBits(DataBits.DB_7);

		readSpc.setStopBits(StopBits.SB_2);
		assertEquals(StopBits.SB_2, readSpc.getStopBits());

		assertThrows(IllegalArgumentException.class, () -> {
			readSpc.setStopBits(StopBits.SB_1_5);
		});
	}

	@NotSupportedByAllDevices
	@Test()
	public void test_switch_5_To_7_DataBits() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testStopBits");
		openDefault();

		readSpc.setDataBits(DataBits.DB_7);
		assertEquals(StopBits.SB_2, readSpc.getStopBits());

		// Now test set 5 data bits with 1.5 stop bits and switching to 7 data bits
		readSpc.setDataBits(DataBits.DB_5);
		assertEquals(StopBits.SB_1_5, readSpc.getStopBits());
		readSpc.setDataBits(DataBits.DB_7);
		assertEquals(StopBits.SB_2, readSpc.getStopBits());
	}

	@BaselineTest
	@Test()
	public void test_StopBits_8_DataBits() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testStopBits");
		openDefault();

		readSpc.setStopBits(StopBits.SB_1);
		assertEquals(StopBits.SB_1, readSpc.getStopBits());

		readSpc.setDataBits(DataBits.DB_8);

		readSpc.setStopBits(StopBits.SB_2);
		assertEquals(StopBits.SB_2, readSpc.getStopBits());

		assertThrows(IllegalArgumentException.class, () -> {
			readSpc.setStopBits(StopBits.SB_1_5);
		});
	}

	@NotSupportedByAllDevices
	@Test()
	public void test_switch_5_To_8_DataBits() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testStopBits");
		openDefault();
		assertEquals(StopBits.SB_2, readSpc.getStopBits());

		// Now test set 5 data bits with 1.5 stop bits and switching to 8 data bits
		readSpc.setDataBits(DataBits.DB_5);
		assertEquals(StopBits.SB_1_5, readSpc.getStopBits());
		readSpc.setDataBits(DataBits.DB_8);
		assertEquals(StopBits.SB_2, readSpc.getStopBits());
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

	/**
	 * Write byte[1024] blocks with set RTS/CTS so the port will actually block The
	 * logs give information about the actual behavior. If the port does not support
	 * RTS/CTS (like MCS7820 on linux TODO BUG?)it will caught by the timeout.
	 * 
	 *
	 * @throws Exception
	 */
	@RtsCtsTest
	@Test
	public void testWriteBytesTimeout() throws Exception {
		assumeWTest();
		// We set FlowControl on the reading end ant RTS false so the writing end can't
		// send at some point
		assumeTrue(readSpc != writeSpc);
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
	@RtsCtsTest
	@Test
	public void testWriteSingleByteTimeout() throws Exception {
		assumeWTest();
		// We set FlowControl on the reading end ant RTS false so the writing end can't
		// send at some point
		assumeTrue(readSpc != writeSpc);
		LOG.log(Level.INFO, "run testWriteSingleByteTimeout");

		// Set a high baudrate to speed up things
		open(Baudrate.B115200, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_RTS_CTS());
		// Disabling timeout on the reading side - so the writing side has a chance to
		// fill the buffer...
		readSpc.setFlowControl(FlowControl.getFC_NONE());
		readSpc.setRTS(false);
		Thread.sleep(10);
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
	private final static int _1MB = 1024 * 1024; //Too much for FTDI on Windows there is nothing sent...
	private final static int _256kB = 1024 * 256;

	@BaselineTest
	@Test
	public void testWrite256kBChunkInfiniteWrite() throws Exception {
		writeMBChunk(_256kB, 0);
	}

	@BaselineTest
	@Test
	public void Write256kBChunk() throws Exception {
		writeMBChunk(_256kB, 1000 + 2 * SerialPortSocket.calculateMillisForBytes(_256kB, Baudrate.B1000000, DataBits.DB_8,
				StopBits.SB_1, Parity.NONE));
	}

	
	@NotSupportedByAllDevices
	@Test
	public void testWrite1MBChunkInfiniteWrite() throws Exception {
		writeMBChunk(_1MB, 0);
	}

	@NotSupportedByAllDevices
	@Test
	public void Write1MBChunk() throws Exception {
		writeMBChunk(_1MB, 1000 + 2 * SerialPortSocket.calculateMillisForBytes(_1MB, Baudrate.B1000000, DataBits.DB_8,
				StopBits.SB_1, Parity.NONE));
	}
	/**
	 * Some devices namely Silicon Labs CP210x can't handle this on windows.They do
	 * not even sent a single byte... port native win error: 87
	 * 
	 * @throws Exception
	 */
	@NotSupportedByAllDevices
	@Test
	public void testWrite16MBChunkInfiniteWrite() throws Exception {
		writeMBChunk(_16MB, 0);
	}

	/**
	 * Some devices namely Silicon Labs CP210x can't handle this on windows. They do
	 * not even sent a single byte... port native win error: 87
	 * 
	 * @throws Exception
	 */
	@NotSupportedByAllDevices
	@Test
	public void Write16MBChunk() throws Exception {
		writeMBChunk(_16MB, 1000 + 2 * SerialPortSocket.calculateMillisForBytes(_16MB, Baudrate.B1000000, DataBits.DB_8,
				StopBits.SB_1, Parity.NONE));
	}

	public void writeMBChunk(int chunksize, int writeTimeout) throws Exception {
		assumeWTest();
		LOG.log(Level.INFO, "run testWriteBytesTimeout writeTO:" + writeTimeout);
		if (writeTimeout == -1) {
			LOG.log(Level.INFO, "infinite timeout");
		} else {
			LOG.log(Level.INFO, "timeout in ms:" + writeTimeout);
		}

		// Set a high baudrate to speed up things
		open(Baudrate.B1000000, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_NONE());
		setTimeouts(100, 1000, writeTimeout);

		byte[] data = new byte[chunksize];
		int dataWritten = 0;
		try {
			assertTimeoutPreemptively(Duration.ofMillis(writeSpc.calculateMillisForBytes(data.length * 2)), () -> {
				writeSpc.getOutputStream().write(data);
			});
			dataWritten = data.length;
		} catch (TimeoutIOException e) {
			dataWritten = e.bytesTransferred;
			String msg = "Timeout: " + dataWritten + " bytes of: " + data.length + " written; OutBuf:  "
					+ writeSpc.getOutBufferBytesCount() + " EX: " + e;
			LOG.log(Level.SEVERE, msg);
			fail(msg);
		} catch (InterruptedIOException iio) {
			dataWritten = iio.bytesTransferred;
			String msg = "Interrupted: " + dataWritten + " bytes of: " + data.length + " written; OutBuf:  "
					+ writeSpc.getOutBufferBytesCount() + " EX: " + iio;
			LOG.log(Level.SEVERE, msg);
			fail(msg);
		}
		try {
			writeSpc.getOutputStream().flush();
			// TODO NOT on win fail();
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

	@BaselineTest
	@Test
	public void testOpenClose() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testOpenClose");

		readSpc.open();
		assertTrue(readSpc.isOpen());
		readSpc.close();
		assertTrue(readSpc.isClosed());
	}

	@BaselineTest
	@Test
	public void testOpenCloseWithParams() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testOpenCloseParams");

		readSpc.open(Baudrate.B9600, DataBits.DB_8, StopBits.SB_1, Parity.EVEN, FlowControl.getFC_NONE());
		assertTrue(readSpc.isOpen());
		assertEquals(Baudrate.B9600, readSpc.getBaudrate());
		assertEquals(DataBits.DB_8, readSpc.getDatatBits());
		assertEquals(StopBits.SB_1, readSpc.getStopBits());
		assertEquals(Parity.EVEN, readSpc.getParity());
		assertEquals(FlowControl.getFC_NONE(), readSpc.getFlowControl());
		readSpc.close();
		assertTrue(readSpc.isClosed());
	}

	@BaselineTest
	@Test
	public void testIlleagalStateExceptions() throws Exception {
		assumeRTest();
		IOException ioe = null;
		// Make sure port is closed
		readSpc.close();

		ioe = assertThrows(IOException.class, () -> {
			readSpc.setBaudrate(Baudrate.B9600);
		});
		assertEquals(ioe.getMessage(), SerialPortSocket.PORT_IS_CLOSED);

		ioe = assertThrows(IOException.class, () -> {
			readSpc.getBaudrate();
		});
		assertEquals(ioe.getMessage(), SerialPortSocket.PORT_IS_CLOSED);

		ioe = assertThrows(IOException.class, () -> {
			readSpc.getInputStream();
		});
		assertEquals(ioe.getMessage(), SerialPortSocket.PORT_IS_CLOSED);

		ioe = assertThrows(IOException.class, () -> {
			readSpc.getOutputStream();
		});
		assertEquals(ioe.getMessage(), SerialPortSocket.PORT_IS_CLOSED);

		readSpc.open();

		ioe = assertThrows(IOException.class, () -> {
			readSpc.open();
		});
		assertEquals(ioe.getMessage(), SerialPortSocket.PORT_IS_OPEN);

	}

	@BaselineTest
	@Test
	public void testSetTimeouts() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testSetTimeOuts");

		openDefault();

		readSpc.setTimeouts(100, 1000, 2000);
		assertEquals(100, readSpc.getInterByteReadTimeout());
		assertEquals(1000, readSpc.getOverallReadTimeout());
		assertEquals(2000, readSpc.getOverallWriteTimeout());

		readSpc.setTimeouts(0, 2222, 3333);
		assertEquals(0, readSpc.getInterByteReadTimeout());
		assertEquals(2222, readSpc.getOverallReadTimeout());
		assertEquals(3333, readSpc.getOverallWriteTimeout());

		readSpc.setTimeouts(0, 2222, 0);
		assertEquals(0, readSpc.getInterByteReadTimeout());
		assertEquals(2222, readSpc.getOverallReadTimeout());
		assertEquals(0, readSpc.getOverallWriteTimeout());

		readSpc.setTimeouts(0, 0, 3333);
		assertEquals(0, readSpc.getInterByteReadTimeout());
		assertEquals(0, readSpc.getOverallReadTimeout());
		assertEquals(3333, readSpc.getOverallWriteTimeout());

		readSpc.setTimeouts(100, 0, 3333);
		assertEquals(100, readSpc.getInterByteReadTimeout());
		assertEquals(0, readSpc.getOverallReadTimeout());
		assertEquals(3333, readSpc.getOverallWriteTimeout());

		readSpc.setTimeouts(100, 0, 0);
		assertEquals(100, readSpc.getInterByteReadTimeout());
		assertEquals(0, readSpc.getOverallReadTimeout());
		assertEquals(0, readSpc.getOverallWriteTimeout());

	}

	@BaselineTest
	@Test
	public void testOverallTimeoutBlocking() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testOverallTimeoutBlocking");
		openDefault();
		setTimeouts(100, 1000, 2000);

		assertTimeoutPreemptively(Duration.ofMillis(1500), () -> {
			final long start = System.currentTimeMillis();
			try {
				int i = readSpc.getInputStream().read();
				fail("No timeout Exception result of Read: " + i);
			} catch (TimeoutIOException tioe) {
				final long time = System.currentTimeMillis() - start;
				LOG.log(Level.INFO, "Timeout: 1000ms and it took: " + time + "ms");
				assertEquals(1000.0, time, 100.0); // We tolerate 5% difference
			}
		});

		readSpc.setTimeouts(0, readSpc.getOverallReadTimeout(), readSpc.getOverallWriteTimeout());

		assertTimeoutPreemptively(Duration.ofMillis(1500), () -> {
			final long start = System.currentTimeMillis();
			try {
				int i = readSpc.getInputStream().read();
				fail("No timeout Exception result of Read: " + i);
			} catch (TimeoutIOException tioe) {
				final long time = System.currentTimeMillis() - start;
				LOG.log(Level.INFO, "Timeout: 1000ms and it took: " + time + "ms");
				assertEquals(1000.0, time, 100.0); // We tolerate 5% difference
			}
		});

		readSpc.close();
		assertTrue(readSpc.isClosed());
	}

	@BaselineTest
	@Test
	public void testOpenTempDir() throws Exception {
		LOG.log(Level.INFO, "run testOpenTempDir");

		File tmpFile = File.createTempFile("serial", "native");
		tmpFile.deleteOnExit();
		SerialPortSocket sp = getSerialPortSocketFactory().createSerialPortSocket(tmpFile.getAbsolutePath());
		IOException ioe = assertThrows(IOException.class, () -> {
			sp.open();
		});
		assertEquals(String.format("Not a serial port: (%s)", sp.getPortName()), ioe.getMessage());
	}

	@BaselineTest
	@Test
	public void testBreak() throws Exception {
		assumeWTest();
		LOG.log(Level.INFO, "run testBreak");
		openDefault();

		writeSpc.setBreak(true);
		writeSpc.setBreak(false);
	}

	@BaselineTest
	@Test
	public void testParity() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testParity");
		openDefault();

		for (Parity p : Parity.values()) {
			readSpc.setParity(p);
			assertEquals(p, readSpc.getParity());
		}
	}

	@BaselineTest
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
				switch (b) {
				case B0:
				case B50:
				case B75:
				case B110:
				case B134:
				case B150:
				case B200:
				case B2000000:
				case B2500000:
				case B3000000:
				case B3500000:
				case B4000000:
					if (b != readSpc.getBaudrate()) {
						LOG.warning("Can't set Baudrate to " + b);
					}
					break;
				default:
					fail("Ex @" + b + "Msg: " + iae);
				}
			}
		}
	}

	@BaselineTest
	@Test()
	public void testOpenTwice() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testOpen2");
		openDefault();

		SerialPortSocket spc1 = getSerialPortSocketFactory().createSerialPortSocket(readSpc.getPortName());
		IOException ioe = assertThrows(IOException.class, () -> {
			spc1.open();
		});
		assertEquals(String.format("Port is busy: (%s)", spc1.getPortName()), ioe.getMessage());

		// try to use the "first" port and if its working ... so we call
		// getInBufferBytesCount()
		readSpc.getInBufferBytesCount();
	}

	@BaselineTest
	@Test
	public void testWriteSingle() throws Exception {
		assumeWTest();
		LOG.log(Level.INFO, "run testWrite");
		openDefault();
		writeSpc.setFlowControl(FlowControl.getFC_NONE());

		writeSpc.getOutputStream().write('a');
		writeSpc.getOutputStream().write('A');
		writeSpc.getOutputStream().write('1');
		writeSpc.getOutputStream().write(1);
	}

	@BaselineTest
	@Test
	public void testWriteBytes() throws Exception {
		assumeWTest();
		LOG.log(Level.INFO, "run testWriteBytes");
		openDefault();
		writeSpc.setFlowControl(FlowControl.getFC_NONE());
		writeSpc.getOutputStream().write("abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ\n\r".getBytes());
	}

	// TODO replace with Sender
	class TestRead implements Runnable {

		boolean done = false;
		final Object lock = new Object();

		@Override
		public void run() {
			while (true) {
				try {
					LOG.info("Start Read");
					int data = readSpc.getInputStream().read();
					LOG.info("Read done: " + data);
					if (data == -1) {
						LOG.log(Level.INFO, "Received -1");
						if (readSpc.isClosed()) {
							LOG.log(Level.INFO, "Received -1 and port is closed");
							synchronized (lock) {
								done = true;
								lock.notifyAll();
							}
							LOG.log(Level.INFO, "Received -1 and port is closed - so we notified all");
						} else {
							LOG.log(Level.SEVERE, "Received -1 but port not closed?");
						}
						break;
					} else {
						LOG.info(String.format("DATA: %x", data));
					}
				} catch (TimeoutIOException tioe) {
					LOG.log(Level.INFO, "Caught Timeout: ", tioe);
				} catch (IOException e) {
					LOG.log(Level.SEVERE, "Caught Exception: ", e);
				}
			}
		}
	}

	@BaselineTest
	@Test
	public void testCloseIn() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testCloseIn");

		for (int loopIndex = 0; loopIndex < 1; loopIndex++) {
			open(Baudrate.B2400, DataBits.DB_8, StopBits.SB_1, Parity.EVEN, FlowControl.getFC_NONE());
			// spc.setTimeouts(100, 1000, 1000);
			printPorts();
			final TestRead tr = new TestRead();
			Thread t = new Thread(tr);
			t.setDaemon(false);
			LOG.info("Start Thread");
			t.start();
			Thread.sleep(100);
			LOG.info("Thread started");

			LOG.info("Close Port");
			Thread.sleep(100);
			readSpc.close();
			assertTrue(readSpc.isClosed());
			LOG.info("Port closed");

			synchronized (tr.lock) {
				if (!tr.done) {
					LOG.info("Will Wait");
					tr.lock.wait(5000); // Allow 1s to close the port and unlock the rad/write Threads.
					if (!tr.done) {
						fail("Port not closed in loopIndex = " + loopIndex);
					}
				}

			}
		}
		LOG.info("OK Finish");

		assertTrue(readSpc.isClosed());
	}

	@BaselineTest
	@Test
	public void testWriteBytesNPE() throws Exception {
		assumeWTest();
		LOG.log(Level.INFO, "run testWriteBytesNPE");
		openDefault();
		assertThrows(NullPointerException.class, () -> {
			writeSpc.getOutputStream().write(null);
		});
	}

	@BaselineTest
	@Test
	public void testWriteBytesWrongLengthAndOrOffset() throws Exception {
		assumeWTest();
		LOG.log(Level.INFO, "run testWriteBytesWrongLengthAndOrOffset");
		openDefault();
		byte[] b = new byte[16];
		assertThrows(IndexOutOfBoundsException.class, () -> {
			writeSpc.getOutputStream().write(b, 0, b.length * 2);
		});
		assertThrows(IndexOutOfBoundsException.class, () -> {
			writeSpc.getOutputStream().write(b, b.length * 2, b.length * 4);
		});
		assertThrows(IndexOutOfBoundsException.class, () -> {
			writeSpc.getOutputStream().write(b, b.length * 4, b.length * 2);
		});
	}

	@BaselineTest
	@Test
	public void testReadBytesNPE() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testReadBytesNPE");
		openDefault();
		assertThrows(NullPointerException.class, () -> {
			readSpc.getInputStream().read(null);
		});
	}

	@BaselineTest
	@Test
	public void testReadBytesWrongLengthAndOrOffset() throws Exception {
		assumeWTest();
		LOG.log(Level.INFO, "run testReadBytesWrongLengthAndOrOffset");
		openDefault();
		byte[] b = new byte[16];
		assertThrows(IndexOutOfBoundsException.class, () -> {
			readSpc.getInputStream().read(b, 0, b.length * 2);
		});
		assertThrows(IndexOutOfBoundsException.class, () -> {
			readSpc.getInputStream().read(b, b.length * 2, b.length * 4);
		});
		assertThrows(IndexOutOfBoundsException.class, () -> {
			readSpc.getInputStream().read(b, b.length * 4, b.length * 2);
		});
	}

	@SlowTest
	@Test
	public void testAllSettings() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testAllSettings");
		openDefault();

		for (Baudrate br : Baudrate.values()) {
			try {
				readSpc.setBaudrate(br);
			} catch (IllegalArgumentException iae) {
				// Some HW supports this, some not ...
				// This is logged in testSetBaudrate so ignore it here.
				continue;
			}
			for (DataBits db : DataBits.values()) {
				readSpc.setDataBits(db);
				for (Parity p : Parity.values()) {
					readSpc.setParity(p);
					for (StopBits sp : StopBits.values()) {
						switch (db) {
						case DB_5:
							if (sp == StopBits.SB_2) {
								assertThrows(IllegalArgumentException.class, () -> {
									readSpc.setStopBits(sp);
								});
							} else {
								readSpc.setStopBits(sp);
								assertEquals(sp, readSpc.getStopBits());
							}
							break;
						case DB_6:
						case DB_7:
						case DB_8:
							if (sp == StopBits.SB_1_5) {
								assertThrows(IllegalArgumentException.class, () -> {
									readSpc.setStopBits(sp);
								});
							} else {
								readSpc.setStopBits(sp);
								assertEquals(sp, readSpc.getStopBits());
							}
							break;
						default:
							throw new RuntimeException("Should never happen");
						}
						assertEquals(br, readSpc.getBaudrate());
						assertEquals(db, readSpc.getDatatBits());
						assertEquals(p, readSpc.getParity());
					}
				}
			}
		}
	}

	public Iterator<PortConfiguration> getTestAllSettings() {
		LinkedList<PortConfiguration> result = new LinkedList<>();
		PortConfigurationFactory portConfigurationFactory = new PortConfigurationFactory();
		for (Baudrate br : Baudrate.values()) {
			portConfigurationFactory.setBaudrate(br);
			for (DataBits db : DataBits.values()) {
				portConfigurationFactory.setDataBits(db);
				for (Parity p : Parity.values()) {
					portConfigurationFactory.setParity(p);
					for (StopBits sp : StopBits.values()) {
						switch (db) {
						case DB_5:
							if (sp == StopBits.SB_2) {
								continue;
							} else {
								portConfigurationFactory.setStopBits(sp);
							}
							break;
						case DB_6:
						case DB_7:
						case DB_8:
							if (sp == StopBits.SB_1_5) {
								continue;
							} else {
								portConfigurationFactory.setStopBits(sp);
							}
							break;
						default:
							throw new RuntimeException("Should never happen");
						}
						result.add(portConfigurationFactory.ofCurrent());
					}
				}
			}
		}
		return result.iterator();
	}

	@SlowTest
	@ParameterizedTest
	@MethodSource({ "getTestAllSettings" })
	public void testAllSettingsOnOpening(PortConfiguration portConfiguration) throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testAllSettingsOnOpening");
		open(portConfiguration);
	}

	@BaselineTest
	@Test
	public void testCloseDuringSingleRead() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testCloseDuringSingleRead");
		open(Baudrate.B2400, DataBits.DB_8, StopBits.SB_1, Parity.EVEN, FlowControl.getFC_NONE());

		new Thread(() -> {
			try {
				Thread.sleep(100);
				readSpc.close();
			} catch (InterruptedException | IOException e) {
				fail("Exception occured");
			}
		}).start();

		assertTrue(readSpc.isOpen());
		int result = assertTimeoutPreemptively(Duration.ofMillis(5000), () -> {
			return readSpc.getInputStream().read();
		});
		assertEquals(-1, result);
		assertTrue(readSpc.isClosed());
		// Allow 50ms to recover -on win the next executed test may fail wit port buy
		// otherwise
		Thread.sleep(100);
	}

	@BaselineTest
	@Test
	public void testCloseDuringBytesRead() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testCloseDuringBytesRead");
		open(Baudrate.B2400, DataBits.DB_8, StopBits.SB_1, Parity.EVEN, FlowControl.getFC_NONE());

		new Thread(() -> {
			try {
				Thread.sleep(100);
				readSpc.close();
			} catch (InterruptedException | IOException e) {
				fail("Exception occured");
			}
		}).start();

		byte b[] = new byte[255];
		assertTrue(readSpc.isOpen());

		int result = assertTimeoutPreemptively(Duration.ofMillis(5000), () -> {
			return readSpc.getInputStream().read(b);
		});
		assertEquals(-1, result);

		assertTrue(readSpc.isClosed());
		// Allow 200ms to recover -on win the next executed test may fail with port busy
		// otherwise (FTDI on win)
		Thread.sleep(200);
	}

	/**
	 * If these test fails, make sure the outputbuffer is really full and must be
	 * written out before more bytes can be wriiten into it. The test relays onto
	 * the fact that all data can't be written in one go.
	 * 
	 * @throws Exception
	 */
	@BaselineTest
	@Test
	public void testCloseDuringBytesWrite() throws Exception {
		assumeWTest();
		LOG.log(Level.INFO, "run testCloseDuringBytesRead");
		open(Baudrate.B1000000, DataBits.DB_8, StopBits.SB_1, Parity.EVEN, FlowControl.getFC_NONE());
		int len = (int) Math.ceil(1000.0 / readSpc.calculateMillisPerByte());
		LOG.log(Level.INFO, "Bytes: " + len);

		byte b[] = new byte[len];
		assertTrue(writeSpc.isOpen());

		new Thread(() -> {
			try {
				Thread.sleep(100);
				writeSpc.close();
			} catch (InterruptedException | IOException e) {
				fail("Exception occured");
			}
		}).start();

		assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
			InterruptedIOException iioe = assertThrows(InterruptedIOException.class, () -> {
				writeSpc.getOutputStream().write(b);
			});
			LOG.log(Level.INFO,
					"Bytes: " + iioe.bytesTransferred + " of: " + len + " written MSG: " + iioe.getMessage());
		});

		assertTrue(readSpc.isClosed());
		// Allow 200ms to recover -on win the next executed test may fail with port busy
		// otherwise (FTDI on win)
		Thread.sleep(200);
	}

	@BaselineTest
	@Test
	public void testSendBreakBlocking() throws Exception {
		assumeWTest();
		LOG.log(Level.INFO, "run testSendBreakBlocking");

		open(Baudrate.B2400, DataBits.DB_8, StopBits.SB_1, Parity.EVEN, FlowControl.getFC_NONE());
		final long start = System.currentTimeMillis();
		writeSpc.sendBreak(500);
		final long end = System.currentTimeMillis();
		assertEquals(500, end - start, 50);
	}

	@BaselineTest
	@Test
	public void testDefaultTimeouts() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "ruf testDefaultTimeouts");
		openDefault();

		assertEquals(100, readSpc.getInterByteReadTimeout());
		assertEquals(0, readSpc.getOverallReadTimeout());
		assertEquals(0, readSpc.getOverallWriteTimeout());
	}

	@BaselineTest
	@Test
	public void testOpen2Times() throws IOException {
		assumeRTest();
		LOG.log(Level.INFO, "run testDefaultTimeouts");

		readSpc.open();
		SerialPortSocket spc2 = getSerialPortSocketFactory().createSerialPortSocket(readSpc.getPortName());
		IOException ioe = assertThrows(IOException.class, () -> {
			spc2.open();
		});
		assertEquals(String.format("Port is busy: (%s)", spc2.getPortName()), ioe.getMessage());
		assertFalse(spc2.isOpen());
	}

	@BaselineTest
	@Test
	public void testOpenSamePort2Times() throws IOException {
		assumeRTest();
		LOG.log(Level.INFO, "run testOpenSamePort2Times");

		readSpc.open();
		IOException ioe = assertThrows(IOException.class, () -> {
			readSpc.open();
		});
		assertEquals(SerialPortSocket.PORT_IS_OPEN, ioe.getMessage());
		assertTrue(readSpc.isOpen());

	}

	/**
	 * Test the finalization by the garbage collector
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@BaselineTest
	@Test
	public void testFinalize() throws IOException, InterruptedException {
		assumeRTest();
		LOG.log(Level.INFO, "run testFinalize on " + readSpc.getPortName());

		final String serialPortName = readSpc.getPortName();

		readSpc.open();
		WeakReference<SerialPortSocket> refSpc = new WeakReference<>(readSpc);
		if (readSpc == writeSpc) {
			writeSpc = null;
		}
		readSpc = null;

		// Order matters! First garbage collect then finalize....
		Runtime.getRuntime().gc();
		Runtime.getRuntime().runFinalization();

		assertNull(refSpc.get());

		// termios has a 10ms waittime during close
		if (System.getProperty("os.name").startsWith("Linux")) {
			Thread.sleep(10);
		}

		// On Windows the GC needs some time - I don't know why...
		if (System.getProperty("os.name").startsWith("Windows")) {
			Thread.sleep(100);
		}

		readSpc = getSerialPortSocketFactory().createSerialPortSocket(serialPortName);
		readSpc.open();
		readSpc.close();
	}

	/**
	 * Test SerialPortSocketFactory's getPortNames() methods.
	 * 
	 * @throws Exception
	 */
	@BaselineTest
	@Test
	public void testSerialPortSocketFactory_getPortNames() throws Exception {
		assumeRTest();
		LOG.info("Iterating serial ports");
		readSpc.open();
		final List<String> ports = getSerialPortSocketFactory().getPortNames(true);
		final List<String> allPorts = getSerialPortSocketFactory().getPortNames(false);
		final List<String> portsincludingReadScp = getSerialPortSocketFactory().getPortNames(readSpc.getPortName(),
				true);

		assertFalse(ports.contains(readSpc.getPortName()), "Open port in filtered portnames found");
		assertTrue(allPorts.contains(readSpc.getPortName()), "Open port not in unfiltered portnames found");
		assertTrue(portsincludingReadScp.contains(readSpc.getPortName()), "Open port not found");

		getSerialPortSocketFactory().getPortNames((portname, busy) -> {
			LOG.log(Level.INFO, "Found port: {0} busy: {1}", new Object[] { portname, busy });
			if (busy) {
				assertTrue(allPorts.contains(portname));
				assertFalse(ports.contains(portname));
			} else {
				assertTrue(ports.contains(portname));
				assertTrue(allPorts.contains(portname));
			}
		});
	}

}
