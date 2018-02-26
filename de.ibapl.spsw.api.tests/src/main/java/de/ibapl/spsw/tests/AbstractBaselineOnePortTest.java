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
import java.lang.ref.WeakReference;
import java.util.logging.Level;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.PortBusyException;
import de.ibapl.spsw.api.SerialPortException;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.api.TimeoutIOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit test for simple App. No Handshake (RTS/CTS etc.) Only 1 or 2 Stoppbits
 * Baudrates from 300 up to 115200 Parity 8 Databits
 * 
 */
public abstract class AbstractBaselineOnePortTest extends AbstractPortTest {

	@Test
	public void testOpenClose() throws Exception {
		Assume.assumeNotNull(readSpc);
		LOG.log(Level.INFO, "run testOpenClose");

		readSpc.openAsIs();
		Assert.assertTrue(readSpc.isOpen());
		readSpc.close();
		Assert.assertTrue(readSpc.isClosed());
	}

	@Test(timeout = 5000)
	public void testSetTimeouts() throws Exception {
		Assume.assumeNotNull(readSpc);
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

	@Test(timeout = 5000)
	public void testOverallTimeoutBlocking() throws Exception {
		Assume.assumeNotNull(readSpc);
		LOG.log(Level.INFO, "run testOverallTimeoutBlocking");
		openDefault();
		setTimeouts(100, 1000, 2000);

		long start = System.currentTimeMillis();
		try {
			int i = readSpc.getInputStream().read();
			Assert.fail("No timeout Exception result of Read: " + i);
		} catch (TimeoutIOException tioe) {
			final long time = System.currentTimeMillis() - start;
			LOG.log(Level.INFO, "Timeout: 1000ms and it took: " + time + "ms");
			Assert.assertEquals(1000.0, time, 100.0); // We tolerate 5% difference
		}

		readSpc.setTimeouts(0, readSpc.getOverallReadTimeout(), readSpc.getOverallWriteTimeout());

		start = System.currentTimeMillis();
		try {
			int i = readSpc.getInputStream().read();
			Assert.fail("No timeout Exception result of Read: " + i);
		} catch (TimeoutIOException tioe) {
			final long time = System.currentTimeMillis() - start;
			LOG.log(Level.INFO, "Timeout: 1000ms and it took: " + time + "ms");
			Assert.assertEquals(1000.0, time, 100.0); // We tolerate 5% difference
		}

		readSpc.close();
		Assert.assertTrue(readSpc.isClosed());
	}

	@Test(expected = SerialPortException.class)
	public void testOpenTempDir() throws Exception {
		LOG.log(Level.INFO, "run testOpenTempDir");

		File tmpFile = File.createTempFile("serial", "native");
		tmpFile.deleteOnExit();
		SerialPortSocket sp = getSerialPortSocketFactory().createSerialPortSocket(tmpFile.getAbsolutePath());
		sp.openAsIs();
		Assert.fail("No serial port opend");
	}

	@Test
	public void testBreak() throws Exception {
		Assume.assumeNotNull(writeSpc);
		LOG.log(Level.INFO, "run testBreak");
		openDefault();

		writeSpc.setBreak(true);
		writeSpc.setBreak(false);
	}

	@Test
	public void testParity() throws Exception {
		Assume.assumeNotNull(readSpc);
		LOG.log(Level.INFO, "run testParity");
		openDefault();

		for (Parity p : Parity.values()) {
			readSpc.setParity(p);
			Assert.assertEquals(p, readSpc.getParity());
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
				switch (b) {
				case B0:
				case B50:
				case B75:
				case B110:
				case B134:
				case B150:
				case B200:
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

	@Test(expected = SerialPortException.class)
	public void testOpen2() throws Exception {
		Assume.assumeNotNull(readSpc);
		LOG.log(Level.INFO, "run testOpen2");
		openDefault();

		try (SerialPortSocket spc1 = getSerialPortSocketFactory().createSerialPortSocket(readSpc.getPortName())) {
			spc1.openAsIs();
		}
		Assert.fail();
	}

	@Test
	public void testWriteSingle() throws Exception {
		Assume.assumeNotNull(writeSpc);
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
		Assume.assumeNotNull(writeSpc);
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
		Assume.assumeNotNull(readSpc);
		LOG.log(Level.INFO, "run testCloseIn");

		for (int loopIndex = 0; loopIndex < 1; loopIndex++) {
			openRaw(Baudrate.B2400, DataBits.DB_8, StopBits.SB_1, Parity.EVEN, FlowControl.getFC_NONE());
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
			Assert.assertTrue(readSpc.isClosed());
			LOG.info("Port closed");

			synchronized (tr.lock) {
				if (!tr.done) {
					LOG.info("Will Wait");
					tr.lock.wait(5000); // Allow 1s to close the port and unlock the rad/write Threads.
					if (!tr.done) {
						Assert.fail("Port not closed in loopIndex = " + loopIndex);
					}
				}

			}
		}
		LOG.info("OK Finish");

		Assert.assertTrue(readSpc.isClosed());
	}

	@Test(expected = NullPointerException.class)
	public void testWriteBytesNPE() throws Exception {
		Assume.assumeNotNull(writeSpc);
		LOG.log(Level.INFO, "run testWriteBytesNPE");
		openDefault();
		writeSpc.getOutputStream().write(null);
	}

	// TODO FIX Stop bit handling 5 databis 1 and 1.5 6,7,8 db 1 and 2 db
	@Test
	public void testAllSettings() throws Exception {
		Assume.assumeNotNull(readSpc);
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
					Assert.assertEquals(br, readSpc.getBaudrate());
					Assert.assertEquals(db, readSpc.getDatatBits());
					Assert.assertEquals(p, readSpc.getParity());
				}
			}
		}
	}

	@Test(timeout = 5000)
	public void testCloseDuringSingleRead() throws Exception {
		Assume.assumeNotNull(readSpc);
		LOG.log(Level.INFO, "run testCloseDuringSingleRead");
		openRaw(Baudrate.B2400, DataBits.DB_8, StopBits.SB_1, Parity.EVEN, FlowControl.getFC_NONE());

		new Thread(() -> {
			try {
				Thread.sleep(100);
				readSpc.close();
			} catch (InterruptedException | IOException e) {
				fail("Exception occured");
			}
		}).start();

		assertTrue(readSpc.isOpen());
		int result = readSpc.getInputStream().read();
		assertEquals(-1, result);

		Assert.assertTrue(readSpc.isClosed());
	}

	@Test(timeout = 5000)
	public void testCloseDuringBytesRead() throws Exception {
		Assume.assumeNotNull(readSpc);
		LOG.log(Level.INFO, "run testCloseDuringBytesRead");
		openRaw(Baudrate.B2400, DataBits.DB_8, StopBits.SB_1, Parity.EVEN, FlowControl.getFC_NONE());

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
		int result = readSpc.getInputStream().read(b);
		assertEquals(-1, result);

		Assert.assertTrue(readSpc.isClosed());

		// Make sure it can be opend
		// TODO commenting this out will make the next Test fail on windowx with port
		// busy ??? I have no idea why
		// spc.openRaw();
		// spc.close();
		// Assert.assertTrue(spc.isClosed());
		// Or 40s will also do
		Thread.sleep(40);
	}

	@Test
	public void testSendBreakBlocking() throws Exception {
		Assume.assumeNotNull(writeSpc);
		LOG.log(Level.INFO, "run testSendBreakBlocking");

		openRaw(Baudrate.B2400, DataBits.DB_8, StopBits.SB_1, Parity.EVEN, FlowControl.getFC_NONE());
		final long start = System.currentTimeMillis();
		writeSpc.sendBreak(500);
		final long end = System.currentTimeMillis();
		assertEquals(500, end - start, 50);
	}

	@Test
	public void testDefaultTimeouts() throws Exception {
		Assume.assumeNotNull(readSpc);
		LOG.log(Level.INFO, "ruf testDefaultTimeouts");
		openDefault();

		assertEquals(100, readSpc.getInterByteReadTimeout());
		assertEquals(0, readSpc.getOverallReadTimeout());
		assertEquals(0, readSpc.getOverallWriteTimeout());
	}

	@Test
	public void testOpen2Times() throws IOException {
		Assume.assumeNotNull(readSpc);
		LOG.log(Level.INFO, "run testDefaultTimeouts");

		readSpc.openRaw();
		SerialPortSocket spc2 = getSerialPortSocketFactory().createSerialPortSocket(readSpc.getPortName());
		try {
			spc2.openRaw();
			fail("Port opend twice");
		} catch (PortBusyException busyException) {
			Assert.assertFalse(spc2.isOpen());
		}
	}

	@Test
	public void testOpenSamePort2Times() throws IOException {
		Assume.assumeNotNull(readSpc);
		LOG.log(Level.INFO, "run testOpenSamePort2Times");

		readSpc.openRaw();
		try {
			readSpc.openRaw();
			fail("Same port opend twice");
		} catch (IOException ioe) {
			assertEquals(SerialPortSocket.PORT_IS_OPEN, ioe.getMessage());
			Assert.assertTrue(readSpc.isOpen());
		}
	}

	/**
	 * Test the finalization by the garbage collector
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testFinalize() throws IOException, InterruptedException {
		Assume.assumeNotNull(readSpc);
		LOG.log(Level.INFO, "run testFinalize on " + readSpc.getPortName());

		final String serialPortName = readSpc.getPortName();

		readSpc.openRaw();
		WeakReference<SerialPortSocket> refSpc = new WeakReference<>(readSpc);
		if (readSpc == writeSpc) {
			writeSpc = null;
		}
		readSpc = null;

		// Order matters! First garbage collect then finalize....
		Runtime.getRuntime().gc();
		Runtime.getRuntime().runFinalization();

		Assert.assertNull(refSpc.get());

		// On Windows the GC needs some time - I don't know why...
		if (System.getProperty("os.name").startsWith("Windows")) {
			Thread.sleep(100);
		}

		readSpc = getSerialPortSocketFactory().createSerialPortSocket(serialPortName);
		readSpc.openRaw();
		readSpc.close();
	}

}
