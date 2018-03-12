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

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.time.Duration;
import java.util.logging.Level;

import javax.swing.text.html.HTMLDocument.HTMLReader.PreAction;

import org.junit.jupiter.api.Test;

import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.PortBusyException;
import de.ibapl.spsw.api.SerialPortException;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.api.TimeoutIOException;

/**
 * Unit test for simple App. No Handshake (RTS/CTS etc.) Only 1 or 2 Stoppbits
 * Baudrates from 300 up to 115200 Parity 8 Databits
 * 
 */
public abstract class AbstractBaselineOnePortTest extends AbstractPortTest {

	@Test
	public void testOpenClose() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testOpenClose");

		readSpc.open();
		assertTrue(readSpc.isOpen());
		readSpc.close();
		assertTrue(readSpc.isClosed());
	}

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

	@Test
	public void testIlleagalStateExceptions() throws Exception {
		assumeRTest();
		IllegalStateException ise = null;
		//Make sure port is closed
		readSpc.close();

		ise = assertThrows(IllegalStateException.class, () -> {
			readSpc.setBaudrate(Baudrate.B9600);
		});
		assertEquals(ise.getMessage(), SerialPortSocket.PORT_NOT_OPEN);

		ise = assertThrows(IllegalStateException.class, () -> {
			readSpc.getBaudrate();
		});
		assertEquals(ise.getMessage(), SerialPortSocket.PORT_NOT_OPEN);

		// ise = assertThrows(IllegalStateException.class, ()->{});
		// assertEquals(ise.getMessage(), SerialPortSocket.PORT_NOT_OPEN);

		readSpc.open();

		ise = assertThrows(IllegalStateException.class, () -> {
			readSpc.open();
		});
		assertEquals(ise.getMessage(), SerialPortSocket.PORT_IS_OPEN);

	}

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

	@Test
	public void testOpenTempDir() throws Exception {
		LOG.log(Level.INFO, "run testOpenTempDir");

		File tmpFile = File.createTempFile("serial", "native");
		tmpFile.deleteOnExit();
		SerialPortSocket sp = getSerialPortSocketFactory().createSerialPortSocket(tmpFile.getAbsolutePath());
		assertThrows(SerialPortException.class, () -> {
			sp.open();
		});
	}

	@Test
	public void testBreak() throws Exception {
		assumeWTest();
		LOG.log(Level.INFO, "run testBreak");
		openDefault();

		writeSpc.setBreak(true);
		writeSpc.setBreak(false);
	}

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

	@Test()
	public void testOpenTwice() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testOpen2");
		openDefault();

		SerialPortSocket spc1 = getSerialPortSocketFactory().createSerialPortSocket(readSpc.getPortName());
		assertThrows(SerialPortException.class, () -> {
			spc1.open();
		});

		// try to use the "first" port and if its working ... so we call
		// getInBufferBytesCount()
		readSpc.getInBufferBytesCount();
	}

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

	@Test
	public void testWriteBytes() throws Exception {
		assumeWTest();
		LOG.log(Level.INFO, "run testWriteBytes");
		openDefault();
		writeSpc.setFlowControl(FlowControl.getFC_NONE());
		writeSpc.getOutputStream().write("abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ\n\r".getBytes());
	}

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

	@Test
	public void testWriteBytesNPE() throws Exception {
		assumeWTest();
		LOG.log(Level.INFO, "run testWriteBytesNPE");
		openDefault();
		assertThrows(NullPointerException.class, () -> {
			writeSpc.getOutputStream().write(null);
		});
	}

	@Test
	public void testSwitchStopBits() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "run testSwitchStopBits");
		openDefault();

		readSpc.setDataBits(DataBits.DB_8);
		readSpc.setStopBits(StopBits.SB_2);
		assertEquals(StopBits.SB_2, readSpc.getStopBits());
		readSpc.setDataBits(DataBits.DB_5);
		assertEquals(StopBits.SB_1_5, readSpc.getStopBits());
		readSpc.setDataBits(DataBits.DB_8);
		assertEquals(StopBits.SB_2, readSpc.getStopBits());
	}

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
	}

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

	}

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

	@Test
	public void testDefaultTimeouts() throws Exception {
		assumeRTest();
		LOG.log(Level.INFO, "ruf testDefaultTimeouts");
		openDefault();

		assertEquals(100, readSpc.getInterByteReadTimeout());
		assertEquals(0, readSpc.getOverallReadTimeout());
		assertEquals(0, readSpc.getOverallWriteTimeout());
	}

	@Test
	public void testOpen2Times() throws IOException {
		assumeRTest();
		LOG.log(Level.INFO, "run testDefaultTimeouts");

		readSpc.open();
		SerialPortSocket spc2 = getSerialPortSocketFactory().createSerialPortSocket(readSpc.getPortName());
		assertThrows(PortBusyException.class, () -> {
			spc2.open();
		});
		assertFalse(spc2.isOpen());
	}

	@Test
	public void testOpenSamePort2Times() throws IOException {
		assumeRTest();
		LOG.log(Level.INFO, "run testOpenSamePort2Times");

		readSpc.open();
		IllegalStateException ioe = assertThrows(IllegalStateException.class, () -> {
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

		// On Windows the GC needs some time - I don't know why...
		if (System.getProperty("os.name").startsWith("Windows")) {
			Thread.sleep(100);
		}

		readSpc = getSerialPortSocketFactory().createSerialPortSocket(serialPortName);
		readSpc.open();
		readSpc.close();
	}

}
