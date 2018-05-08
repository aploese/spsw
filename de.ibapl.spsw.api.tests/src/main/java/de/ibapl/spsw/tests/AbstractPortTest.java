/*-
 * #%L
 * SPSW Provider
 * %%
 * Copyright (C) 2009 - 2018 Arne Plöse
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.Iterator;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;

import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.SerialPortSocketFactory;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.api.TimeoutIOException;
import de.ibapl.spsw.logging.LoggingSerialPortSocket;
import de.ibapl.spsw.logging.TimeStampLogging;

/**
 * @author Arne Plöse
 */
@ExtendWith(AbstractPortTest.AfterTestExecution.class)
@TestInstance(Lifecycle.PER_CLASS)
public abstract class AbstractPortTest {

	public class Receiver implements Runnable {

		final SocketIoType socketIoType;
		final SerialPortSocket sps;
		boolean done;
		final Object LOCK = new Object();
		ByteBuffer recBuffer;
		final ByteBuffer sendBuffer;
		int currentRecOffset;

		Exception ex;
		Error err;
		public Receiver(SocketIoType socketIoType, SerialPortSocket sps, ByteBuffer sendBuffer) {
			this(socketIoType, sps, sendBuffer, socketIoType == SocketIoType.CHANNEL ? ByteBuffer.allocateDirect(sendBuffer.capacity()) : ByteBuffer.allocate(sendBuffer.capacity()));
		}

		public Receiver(SocketIoType socketIoType, SerialPortSocket sps, ByteBuffer sendBuffer, ByteBuffer recBuffer) {
			this.socketIoType = socketIoType;
			this.sps = sps;
			this.sendBuffer = sendBuffer;
			this.recBuffer = recBuffer;
		}

		@Override
		public void run() {
			currentRecOffset = 0;
			ex = null;
			err = null;
			done = false;
			try {
				while (sendBuffer.limit() > currentRecOffset) {
					switch (socketIoType) {
					case SINGLE_BYTE: {
						
					final InputStream is = sps.getInputStream();
					final int data = is.read();
						if (data >= 0) {
							recBuffer.put((byte) data);
							assertEquals(sendBuffer.get(currentRecOffset), recBuffer.get(currentRecOffset), () -> {
								return String.format("Arrays differ @%d expected %02x, but was %02x", currentRecOffset, sendBuffer.get(currentRecOffset), recBuffer.get(currentRecOffset));
							});
							currentRecOffset++;
						} else {
							throw new RuntimeException("TODO implement me is.read returns: " + data );
						}
						if (currentRecOffset == recBuffer.capacity()) {
							break;
						}
					} 
					break;
					case STREAM: {
						byte[] buf = new byte[recBuffer.remaining()];
						final int count = sps.getInputStream().read(buf);
						recBuffer.put(buf);
						if (count > 0) {
							for (int i = 0; i < count; i++) {
								final int pos = currentRecOffset + i;
								assertEquals(sendBuffer.get(currentRecOffset), recBuffer.get(currentRecOffset), () -> {
									return String.format("Arrays differ @%d expected %02x, but was %02x", pos,
											sendBuffer.get(pos), recBuffer.get(pos));
								});
							}
							currentRecOffset += count;
							if (currentRecOffset == recBuffer.capacity()) {
								break;
							}
						}
						LOG.log(Level.FINEST, "Bytes read: {0}", count);
						if (count <= 0) {
							if (currentRecOffset < recBuffer.capacity()) {
								LOG.log(Level.SEVERE, "Bytes missing: {0}", recBuffer.capacity() - currentRecOffset);
								// TODO printArrays("Too short");
							}
							break;
						}
					}
						break;
					case CHANNEL: {
						final int count = sps.getChannel().read(recBuffer);
						if (count > 0) {
							for (int i = 0; i < count; i++) {
								final int pos = currentRecOffset + i;
								assertEquals(sendBuffer.get(pos), recBuffer.get(pos), () -> {
									return String.format("Arrays differ @%d expected %02x, but was %02x", pos,
											sendBuffer.get(pos), recBuffer.get(pos));
								});
							}
							currentRecOffset += count;
							if (currentRecOffset == recBuffer.capacity()) {
								break;
							}
						}
						LOG.log(Level.FINEST, "Bytes read: {0}", count);
						if (count <= 0) {
							if (currentRecOffset < recBuffer.capacity()) {
								LOG.log(Level.SEVERE, "Bytes missing: {0}", recBuffer.capacity() - currentRecOffset);
								// TODO printArrays("Too short");
							}
							break;
						}
					}
						break;
						//TODO assert ByteBuffer position limit
						
					}
				}
				
				LOG.log(Level.INFO, "Byte total read: {0}", currentRecOffset);
				synchronized (LOCK) {
					done = true;
					LOCK.notifyAll();
					LOG.log(Level.INFO, "Send Thread finished");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				synchronized (LOCK) {
					done = true;
					this.ex = ex;
					LOG.log(Level.SEVERE, "Send Thread Exception offset: " + currentRecOffset, ex);
					LOCK.notifyAll();
				}
			} catch (Error err) {
				err.printStackTrace();
				synchronized (LOCK) {
					done = true;
					this.err = err;
					// TODO printArrays("Error");
					LOG.log(Level.SEVERE, "Send Thread Error offset: " + currentRecOffset, err);
					LOCK.notifyAll();
				}
			}
		}

		/**
		 * Try to figure out what exactly hit us...
		 * 
		 * At high speeds sometime a byte gets "lost" and therefore its running in an
		 * timeout... The first "missing" byte is where the array differ...? The amount
		 * of "missing" bytes is the difference of sendBuffer.length and
		 * currentRecOffset.
		 * 
		 */
		public void assertStateAfterExecution() {
			assertNull(err, err != null ? err.getMessage() : "");
			assertAll("Receive Exception", () -> {
				// Where is the missing byte
				assertEquals(sendBuffer.position(), recBuffer.position(), "Position");
				assertEquals(sendBuffer.limit(), recBuffer.limit(), "Limit");
			}, () -> {
				// How much bytes are missing
				assertEquals(sendBuffer.limit(), currentRecOffset, "Received not enough");
			}, () -> {
				if (ex instanceof TimeoutIOException) {
					// if bytesTransferred == 0 then in the second attempt nothing was read.
					fail("TimeoutIOException bytes transferred: " + ((TimeoutIOException) ex).bytesTransferred
							+ " MSG: " + ex.getMessage());
				} else if (ex instanceof InterruptedIOException) {
					fail("InterruptedIOException bytes transferred: " + ((TimeoutIOException) ex).bytesTransferred
							+ " MSG: " + ex.getMessage());
				} else if (ex != null) {
					fail(ex.getClass().getSimpleName() + " MSG: " + ex.getMessage());

				}
			}, () -> {
				assertTrue(done, "Receiver has not finished");
			});
		}

	}

	public class Sender implements Runnable {
		final SocketIoType socketIoType;
		boolean done;
		final Object LOCK = new Object();
		final ByteBuffer sendBuffer;
		Exception ex;
		Error err;
		final SerialPortSocket sps;

		public Sender(SocketIoType socketIoType, SerialPortSocket sps, int bufferSize) {
			this(socketIoType, sps, socketIoType == SocketIoType.CHANNEL ? ByteBuffer.allocateDirect(bufferSize) : ByteBuffer.allocate(bufferSize));
		}

		public Sender(SocketIoType socketIoType, SerialPortSocket sps, ByteBuffer sendBuffer) {
			this.socketIoType = socketIoType;
			this.sps = sps;
			this.sendBuffer = sendBuffer;
			for (int i = 0; i < sendBuffer.capacity(); i++) {
				sendBuffer.put((byte) i);
			}
			sendBuffer.flip();
		}

		@Override
		public void run() {
			done = false;
			ex = null;
			err = null;
			int bytesWritten = 0;
			try {
				switch (socketIoType) {
				case SINGLE_BYTE: {
					final OutputStream os = sps.getOutputStream();
					while (sendBuffer.hasRemaining()) {
						os.write(sendBuffer.get());
						bytesWritten = 1;
					}
				}
					break;
				case STREAM:
					byte[] b = new byte[sendBuffer.remaining()];
					sendBuffer.get(b);
					sps.getOutputStream().write(b);
					bytesWritten = b.length;
					break;
				case CHANNEL:
					bytesWritten = sps.getChannel().write(sendBuffer);
				break;

				}
				LOG.log(Level.INFO, "Bytes written: {0}", bytesWritten);
				
				synchronized (LOCK) {
					done = true;
					LOCK.notifyAll();
					LOG.log(Level.INFO, "Send Thread finished");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				synchronized (LOCK) {
					done = true;
					this.ex = ex;
					LOG.log(Level.SEVERE, "Send Thread Exception", ex);
					LOCK.notifyAll();
				}
			} catch (Error err) {
				err.printStackTrace();
				synchronized (LOCK) {
					done = true;
					this.err = err;
					LOG.log(Level.SEVERE, "Send Thread Error", err);
					LOCK.notifyAll();
				}
			}
		}

		public void assertStateAfterExecution() {
			assertNull(err);
			assertAll("Sender Exception", () -> {
				assertTrue(done, "Not all data was sent");
			}, () -> {
				if (ex instanceof TimeoutIOException) {
					// if bytesTransferred == 0 then in the second attempt nothing was read.
					fail("TimeoutIOException bytes transferred: " + ((TimeoutIOException) ex).bytesTransferred
							+ " MSG: " + ex.getMessage());
				} else if (ex instanceof InterruptedIOException) {
					fail("InterruptedIOException bytes transferred: " + ((TimeoutIOException) ex).bytesTransferred
							+ " MSG: " + ex.getMessage());
				} else if (ex != null) {
					fail(ex.getClass().getSimpleName() + " MSG: " + ex.getMessage());
				}
			});
		}
	}

	protected static final int PORT_RECOVERY_TIME_MS = 200;

	protected static final Logger LOG = Logger.getLogger("SpswTests");
	private static String readSerialPortName;
	private static String writeSerialPortName;

	protected SerialPortSocket readSpc;
	protected SerialPortSocket writeSpc;
	protected boolean currentTestFailed;

	protected SerialPortSocketFactory getSerialPortSocketFactory() {
		ServiceLoader<SerialPortSocketFactory> serviceLoader = ServiceLoader.load(SerialPortSocketFactory.class);
		Iterator<SerialPortSocketFactory> iterator = serviceLoader.iterator();
		assertTrue(iterator.hasNext(), "No implementation of SerialPortSocket found - Please fix test setup");
		final SerialPortSocketFactory result = iterator.next();
		assertFalse(iterator.hasNext(),
				"More than one implementation of SerialPortSocket found - Please fix test setup");
		return result;
	}

	public static class AfterTestExecution implements AfterTestExecutionCallback {
		@Override
		public void afterTestExecution(ExtensionContext context) throws Exception {
			((AbstractPortTest) context.getRequiredTestInstance()).currentTestFailed = context.getExecutionException()
					.isPresent();
		}
	}

	protected void setSpeed(Speed speed) throws IOException {
		if (readSpc != null) {
			readSpc.setSpeed(speed);
		}
		if (writeSpc != null && writeSpc != readSpc) {
			writeSpc.setSpeed(speed);
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
		open(Speed._9600_BPS, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_NONE());
	}

	/**
	 * Opens the port and make sure that In and out buffer are empty
	 * 
	 * @param speed
	 * @param dataBits
	 * @param stopBits
	 * @param parity
	 * @param flowControl
	 * @throws Exception
	 */
	protected void open(Speed speed, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControl)
			throws Exception {
		if (readSpc != null) {
			readSpc.open(speed, dataBits, stopBits, parity, flowControl);
			assertEquals(0, readSpc.getOutBufferBytesCount(), "Can't start test: OutBuffer is not empty");
			while (readSpc.getInBufferBytesCount() > 0) {
				readSpc.getInputStream().read(new byte[readSpc.getInBufferBytesCount()]);
				Thread.sleep(100);
			}
			assertEquals(0, readSpc.getInBufferBytesCount(), "Can't start test: InBuffer is not empty");

		}
		if (writeSpc != null && writeSpc != readSpc) {
			writeSpc.open(speed, dataBits, stopBits, parity, flowControl);
			assertEquals(0, writeSpc.getOutBufferBytesCount(), "Can't start test: OutBuffer is not empty");
			while (writeSpc.getInBufferBytesCount() > 0) {
				writeSpc.getInputStream().read(new byte[writeSpc.getInBufferBytesCount()]);
				Thread.sleep(100);
			}
			assertEquals(0, writeSpc.getInBufferBytesCount(), "Can't start test: InBuffer is not empty");
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
			sb.append(String.format("\tSpeed:    %-20d %-20d\n", readSpc.getSpeed().value, writeSpc.getSpeed().value));
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
				sb.append(String.format("\tSpeed:    %-20d\n", spc.getSpeed().value));
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
	public void setUp(TestInfo testInfo) throws Exception {
		LOG.info(MessageFormat.format("do run test : {0}", testInfo.getDisplayName()));
		if (readSerialPortName != null) {
			readSpc = getSerialPortSocketFactory().createSerialPortSocket(readSerialPortName);
//No wrapper			readSpc = LoggingSerialPortSocket.wrapWithHexOutputStream(readSpc, new FileOutputStream("/tmp/test_serial.txt"), true, TimeStampLogging.FROM_OPEN);
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
					assert (writeSpc.isClosed());
				}
			}
			writeSpc = null;
		}
		if (readSpc != null) {
			if (!readSpc.isClosed()) {
				readSpc.close();
				assert (readSpc.isClosed());
			}
			readSpc = null;
		}

		Runtime.getRuntime().gc();
		Runtime.getRuntime().runFinalization();
		if (currentTestFailed) {
			Thread.sleep(PORT_RECOVERY_TIME_MS);
			Runtime.getRuntime().gc();
			Runtime.getRuntime().runFinalization();
		}
	}

	protected void open(PortConfiguration pc) throws Exception {
		open(pc.getSpeed(), pc.getDataBits(), pc.getStopBits(), pc.getParity(), pc.getFlowControl());
		setTimeouts(pc.getInterByteReadTimeout(), pc.getOverallReadTimeout(), pc.getOverallWriteTimeout());
	}

	public void runNonThreaded(Sender sender, Receiver receiver, long timeout) throws Exception {
		assertAll("After ", () -> {
			assertTimeout(Duration.ofMillis(timeout), () -> {
				sender.run();
				receiver.run();
			});
		}, () -> {
			sender.assertStateAfterExecution();
		}, () -> {
			receiver.assertStateAfterExecution();
		});
	}

	public void write_Read_nonThreaded(SocketIoType sendType, SocketIoType receiveType, PortConfiguration pc) throws Exception {
		open(pc);
		final Sender sender = new Sender(sendType, writeSpc, pc.getBufferSize());
		final Receiver receiver = new Receiver(receiveType, readSpc, sender.sendBuffer);

		runNonThreaded(sender, receiver, pc.getTestTimeout());
	}

	private void runThreaded(Sender sender, Receiver receiver, long timeout) throws Exception {
		new Thread(receiver).start();
		new Thread(sender).start();
		assertAll("Treaded Test Run ", () -> {
			assertTimeoutPreemptively(Duration.ofMillis(timeout), () -> {
				// Make sure all was sent
				synchronized (sender.LOCK) {
					while (!sender.done) {
						sender.LOCK.wait(timeout / 10);
					}
				}
			}, "Send Timeout");
		}, () -> {
			assertTimeoutPreemptively(Duration.ofMillis(timeout), () -> {
				// Make sure all was received
				synchronized (receiver.LOCK) {
					while (!receiver.done) {
						receiver.LOCK.wait(timeout / 10);
					}
				}
			}, "Reveive Timeout");
		}, () -> {
			sender.assertStateAfterExecution();
		}, () -> {
			receiver.assertStateAfterExecution();
		});

	}

	public void write_Read_Threaded(SocketIoType sendType, SocketIoType receiveType, PortConfiguration pc) throws Exception {
		open(pc);
		final Sender sender = new Sender(sendType, writeSpc, pc.getBufferSize());
		final Receiver receiver = new Receiver(receiveType, readSpc, sender.sendBuffer);

		runThreaded(sender, receiver, pc.getTestTimeout());
	}
}