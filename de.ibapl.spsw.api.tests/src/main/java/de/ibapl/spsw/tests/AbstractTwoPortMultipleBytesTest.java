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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.SerialPortSocketFactory;
import de.ibapl.spsw.api.StopBits;
import java.io.OutputStream;

/**
 * Unit test for simple App.
 */
public abstract class AbstractTwoPortMultipleBytesTest extends AbstractPortTest {

	private final static int DEFAULT_TEST_BUFFER_SIZE = 1024 * 2;

	public class ReceiverThread extends Thread {

		boolean done;
		final Object LOCK = new Object();
		byte[] dataIn;
		byte[] dataOut;
		long startTimeStamp;
		Exception ex;
		Error err;

		@Override
		public void run() {
			try {
				int offset = 0;
				while (true) {
					try {
						final int count = readSpc.getInputStream().read(dataIn, offset, dataIn.length - offset);
						if (count > 0) {
							assertArrayEquals(Arrays.copyOfRange(dataOut, offset, offset + count),
									Arrays.copyOfRange(dataIn, offset, offset + count),
									"Error @Offset: " + offset + " @Count: " + count);
							offset += count;
							// LOG.info("DATA Rec: " + offset);
							if (offset == dataOut.length) {
								break;
							}
						}
						LOG.log(Level.FINEST, "Bytes read: {0}", count);
						if (count <= 0) {
							if (offset < dataIn.length) {
								LOG.log(Level.SEVERE, "Bytes missing: {0}", dataIn.length - offset);
								printArrays("Too short");
							}
							break;
						}
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
				LOG.log(Level.INFO, "Byte total read: {0}", offset);
				LOG.log(Level.INFO, "Receive time in millis:: {0}", (System.currentTimeMillis() - startTimeStamp));
				synchronized (LOCK) {
					done = true;
					LOCK.notifyAll();
					LOG.log(Level.INFO, "Send Thread finished");
				}
			} catch (Exception ex) {
				synchronized (LOCK) {
					done = true;
					this.ex = ex;
					LOG.log(Level.SEVERE, "Send Thread Exception", ex);
					LOCK.notifyAll();
				}
			} catch (Error err) {
				synchronized (LOCK) {
					done = true;
					this.err = err;
					printArrays("Error");
					LOG.log(Level.SEVERE, "Send Thread Error", err);
					LOCK.notifyAll();
				}
			}
		}

		private void initBuffers(final int size) {
			dataOut = new byte[size];
			for (int i = 0; i < size; i++) {
				// dataOut[i] = (byte) (i & 0xFF);
				dataOut[i] = (byte) (Math.round(Math.random() * 0xFF) & 0xFF);
			}
			dataIn = new byte[size];
		}

		private void printArrays(String title) {
			System.err.println(title);
			for (int i = 0; i < dataOut.length; i++) {
				System.err.println(i + "\t: " + dataOut[i] + "\t" + dataIn[i]);
			}
		}

	}

	private ReceiverThread receiverThread;

	protected Set<FlowControl> flowControl = FlowControl.getFC_NONE(); // getFC_RTS_CTS();
	protected Parity parity = Parity.EVEN;
	protected StopBits stopBits = StopBits.SB_2;
	protected DataBits dataBits = DataBits.DB_8;

	@BeforeEach
	@Override
	public void setUp() throws Exception {
		super.setUp();
		receiverThread = new ReceiverThread();
	}

	@AfterEach
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		receiverThread = null;
	}

	private void runTest(Baudrate baudrate, int buffersize) throws Exception {
		assumeRWTest();
		;
		LOG.info(MessageFormat.format("do run test @baudrate: {0}, buffer size: {1}", baudrate.value, buffersize));

		receiverThread.initBuffers(buffersize);
		receiverThread.startTimeStamp = System.currentTimeMillis();

		openRaw(baudrate, dataBits, stopBits, parity, flowControl);
		printPorts();

		receiverThread.start();

		final int bitsToTransfer = 1 + dataBits.value + (int) Math.round(Math.ceil(stopBits.value))
				+ (parity == Parity.NONE ? 0 : 1);
		assertTimeoutPreemptively(
				Duration.ofMillis(1000 + (receiverThread.dataOut.length * bitsToTransfer * 1000) / baudrate.value),
				() -> {
					writeSpc.getOutputStream().write(receiverThread.dataOut);

					LOG.log(Level.INFO, "Send time in millis:: {0}",
							(System.currentTimeMillis() - receiverThread.startTimeStamp));
					synchronized (receiverThread.LOCK) {
						while (!receiverThread.done) {
							receiverThread.LOCK.wait();
							if (receiverThread.ex != null) {
								throw receiverThread.ex;
							}
							if (receiverThread.err != null) {
								throw receiverThread.err;
							}
						}
						LOG.log(Level.INFO, "Thread finished received");
					}
				});
		assertArrayEquals(receiverThread.dataOut, receiverThread.dataIn);
	}

	@Test
	public void test_0000300() throws Exception {
		runTest(Baudrate.B300, DEFAULT_TEST_BUFFER_SIZE);
	}

	@Test
	public void test_0001200() throws Exception {
		runTest(Baudrate.B1200, DEFAULT_TEST_BUFFER_SIZE);
	}

	@Test
	public void test_0002400() throws Exception {
		runTest(Baudrate.B2400, DEFAULT_TEST_BUFFER_SIZE);
	}

	@Test
	public void test_0009600() throws Exception {
		runTest(Baudrate.B9600, DEFAULT_TEST_BUFFER_SIZE);
	}

	@Test
	public void test_0019200() throws Exception {
		runTest(Baudrate.B19200, DEFAULT_TEST_BUFFER_SIZE);
	}

	@Test
	public void test_0057600() throws Exception {
		runTest(Baudrate.B57600, DEFAULT_TEST_BUFFER_SIZE);
	}

	@Test
	public void test_0115200() throws Exception {
		runTest(Baudrate.B115200, DEFAULT_TEST_BUFFER_SIZE);
	}

	@Test
	public void test_0230400() throws Exception {
		runTest(Baudrate.B230400, DEFAULT_TEST_BUFFER_SIZE);
	}

	@Test
	public void test_0460800() throws Exception {
		runTest(Baudrate.B460800, DEFAULT_TEST_BUFFER_SIZE);
	}

	@Test
	public void test_0500000() throws Exception {
		runTest(Baudrate.B500000, DEFAULT_TEST_BUFFER_SIZE);
	}

	@Disabled // On win can't handle ...
	@Test
	public void test_0576000() throws Exception {
		runTest(Baudrate.B576000, DEFAULT_TEST_BUFFER_SIZE);
	}

	@Disabled
	@Test
	public void test_1000000() throws Exception {
		runTest(Baudrate.B1000000, DEFAULT_TEST_BUFFER_SIZE);
	}

	@Disabled
	@Test
	public void test_1152000() throws Exception {
		runTest(Baudrate.B1152000, DEFAULT_TEST_BUFFER_SIZE);
	}

	@Disabled
	@Test
	public void test_2000000() throws Exception {
		runTest(Baudrate.B2000000, DEFAULT_TEST_BUFFER_SIZE);
	}

	@Disabled
	@Test
	public void test_3000000() throws Exception {
		runTest(Baudrate.B3000000, DEFAULT_TEST_BUFFER_SIZE);
	}

	@Disabled
	@Test
	public void test_4000000() throws Exception {
		runTest(Baudrate.B4000000, DEFAULT_TEST_BUFFER_SIZE);
	}

	/**
	 * Send 128 bytes out - the outputbuffer will hold this - so write returns
	 * assuming all bytes are written. But we will only read one byte ... because
	 * interbyteRead is 0
	 * 
	 * @throws Exception
	 */
	@Disabled
	@Test
	public void testTimeout() throws Exception {
		assumeTrue(HARDWARE_SUPPORTS_RTS_CTS);
		assumeRWTest();

		openRaw(Baudrate.B9600, DataBits.DB_8, StopBits.SB_1, Parity.EVEN, FlowControl.getFC_RTS_CTS());
		setTimeouts(0, 2000, 2000);

		assertEquals(0, readSpc.getInterByteReadTimeout());
		final InputStream is = readSpc.getInputStream();
		final OutputStream os = writeSpc.getOutputStream();

		final byte[] recBuff = new byte[255];
		Arrays.fill(recBuff, (byte) 0);

		final byte[] sendBuff = new byte[128];
		Arrays.fill(sendBuff, (byte) 0x7F);

		final Object lock = new Object();
		final Thread t = new Thread(() -> {
			try {
				final long start = System.currentTimeMillis();
				final int i = is.read(recBuff);
				final long diff = System.currentTimeMillis() - start;
				assertEquals(1, i, "Only expected 1 byte to read " + diff + "ms ");
				assertEquals(0x7F, recBuff[0], "Error @0");
				assertEquals(0x00, recBuff[1], "Error @1");
				synchronized (lock) {
					lock.notifyAll();
				}
			} catch (IOException ex) {
				fail(ex.getMessage());
			}

		});
		t.start();

		// Quick and dirty time lock to start receiver thread
		Thread.sleep(100);
		assertTimeoutPreemptively(Duration.ofMillis(5000), () -> {
			try {
				os.write(sendBuff);
				os.flush();
			} catch (InterruptedIOException e) {
				LOG.severe("Interrupted IO " + e.bytesTransferred);// TODO: handle exception
			}

			synchronized (lock) {
				lock.wait();
			}

			// Wait for all data to arrive...
			Thread.sleep(200);

			assertEquals(127, is.read(recBuff, 1, 200));
		});

		for (int i = 0; i < 128; i++) {
			assertEquals(sendBuff[i], recBuff[i], "Error @" + i);
		}

	}

	@Test
	public void testInfiniteTimeout() throws Exception {
		assumeTrue(HARDWARE_SUPPORTS_RTS_CTS);
		assumeRWTest();

		openRaw(Baudrate.B9600, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_RTS_CTS());
		final InputStream is = readSpc.getInputStream();
		final OutputStream os = writeSpc.getOutputStream();

		final byte[] recBuff = new byte[255];
		Arrays.fill(recBuff, (byte) 0);

		final byte[] sendBuff = new byte[128];
		Arrays.fill(sendBuff, (byte) 0x7F);

		assertTimeoutPreemptively(Duration.ofMillis(5000), () -> {
			os.write(sendBuff);
			os.flush();

			Thread.sleep(200);
			assertEquals(128, is.available());
			final int bytesread = is.read(recBuff);
			assertEquals(128, bytesread);
		});

		for (int i = 0; i < 128; i++) {
			assertEquals(sendBuff[i], recBuff[i], "Error @" + i);
		}

	}

}
