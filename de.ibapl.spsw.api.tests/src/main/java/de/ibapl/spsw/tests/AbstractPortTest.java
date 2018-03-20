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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.Properties;
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

import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.SerialPortSocketFactory;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.api.TimeoutIOException;
import de.ibapl.spsw.tests.AbstractPortTest.Receiver;
import de.ibapl.spsw.tests.AbstractPortTest.Sender;

@ExtendWith(AbstractPortTest.AfterTestExecution.class)
@TestInstance(Lifecycle.PER_CLASS)
public abstract class AbstractPortTest {

	public class Receiver implements Runnable {

		final boolean readSingle;
		final InputStream is;
		boolean done;
		final Object LOCK = new Object();
		byte[] recBuffer;
		final byte[] sendBuffer;
		int currentRecOffset;

		Exception ex;
		Error err;

		public Receiver(boolean readSingle, InputStream is, byte[] sendBuffer) {
			this.readSingle = readSingle;
			this.is = is;
			this.sendBuffer = sendBuffer;
		}

		@Override
		public void run() {
			this.recBuffer = new byte[sendBuffer.length];
			currentRecOffset = 0;
			ex = null;
			err = null;
			done = false;
			try {
				while (true) {
					if (readSingle) {
						final int data = is.read();
						if (data >= 0) {
							final int pos = currentRecOffset;
							currentRecOffset++;
							recBuffer[pos] = (byte) data;
							assertEquals(sendBuffer[pos], recBuffer[pos], () -> {
								return String.format("Arrays differ @%d expected but was %02x", pos, sendBuffer[pos],
										recBuffer[pos]);
							});
						} else {
							throw new RuntimeException("TODO implement me");
						}
						if (currentRecOffset == recBuffer.length) {
							break;
						}
					} else {
						final int count = is.read(recBuffer, currentRecOffset, recBuffer.length - currentRecOffset);
						if (count > 0) {
							for (int i = 0; i < count; i++) {
								final int pos = currentRecOffset + i;
								assertEquals(sendBuffer[currentRecOffset], recBuffer[currentRecOffset], () -> {
									return String.format("Arrays differ @%d expected but was %02x", pos,
											sendBuffer[pos], recBuffer[pos]);
								});
							}
							currentRecOffset += count;
							if (currentRecOffset == recBuffer.length) {
								break;
							}
						}
						LOG.log(Level.FINEST, "Bytes read: {0}", count);
						if (count <= 0) {
							if (currentRecOffset < recBuffer.length) {
								LOG.log(Level.SEVERE, "Bytes missing: {0}", recBuffer.length - currentRecOffset);
								// TODO printArrays("Too short");
							}
							break;
						}
					}
				}
				LOG.log(Level.INFO, "Byte total read: {0}", currentRecOffset);
				synchronized (LOCK) {
					done = true;
					LOCK.notifyAll();
					LOG.log(Level.INFO, "Send Thread finished");
				}
			} catch (Exception ex) {
				synchronized (LOCK) {
					done = true;
					this.ex = ex;
					LOG.log(Level.SEVERE, "Send Thread Exception offset: " + currentRecOffset, ex);
					LOCK.notifyAll();
				}
			} catch (Error err) {
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
			assertNull(err);
			assertAll("Receive Exception", () -> {
				// Where is the missing byte
				assertArrayEquals(sendBuffer, recBuffer);
			}, () -> {
				// How much bytes are missing
				assertEquals(sendBuffer.length, currentRecOffset, "Received not enough");
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
		final boolean writeSingle;
		boolean done;
		final Object LOCK = new Object();
		final byte[] sendBuffer;
		Exception ex;
		Error err;
		final OutputStream os;

		public Sender(boolean writeSingle, OutputStream os, byte[] sendBuffer) {
			this.writeSingle = writeSingle;
			this.os = os;
			this.sendBuffer = sendBuffer;
		}

		@Override
		public void run() {
			done = false;
			ex = null;
			err = null;
			try {
				if (writeSingle) {
					for (int i = 0; i < sendBuffer.length; i++) {
						os.write(sendBuffer[i]);
					}
				} else {
					os.write(sendBuffer);
				}
				LOG.log(Level.INFO, "Bytes written: {0}", sendBuffer.length);
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
				}});
		}
	}

	protected byte[] initBuffer(final int size) {
		final byte[] result = new byte[size];
		for (int i = 0; i < size; i++) {
			result[i] = (byte) i;
		}
		return result;
	}


	
	protected static final int PORT_RECOVERY_TIME_MS = 200;

	protected static final Logger LOG = Logger.getLogger("SpswTests");
	private static String readSerialPortName;
	private static String writeSerialPortName;

	protected SerialPortSocket readSpc;
	protected SerialPortSocket writeSpc;
	protected boolean currentTestFailed;

	protected abstract SerialPortSocketFactory getSerialPortSocketFactory();

	public static class AfterTestExecution implements AfterTestExecutionCallback {
	public void afterTestExecution(ExtensionContext context) throws Exception {
			((AbstractPortTest)context.getRequiredTestInstance()).currentTestFailed = context.getExecutionException().isPresent();
	}
	}
	
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
	public void setUp(TestInfo testInfo) throws Exception {
		LOG.info(MessageFormat.format("do run test : {0}", testInfo.getDisplayName()));
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
					assert(writeSpc.isClosed());
				}
			}
			writeSpc = null;
		}
		if (readSpc != null) {
			if (!readSpc.isClosed()) {
				readSpc.close();
				assert(readSpc.isClosed());
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
		open(pc.getBaudrate(), pc.getDataBits(), pc.getStopBits(), pc.getParity(), pc.getFlowControl());
		setTimeouts(pc.getInterByteReadTimeout(), pc.getOverallReadTimeout(), pc.getOverallWriteTimeout());
	}
	
	public void writeBytes_ReadBytes(PortConfiguration pc) throws Exception {
		open(pc);
		final Sender sender = new Sender(true, writeSpc.getOutputStream(), initBuffer(pc.getBufferSize()));
		final Receiver receiver = new Receiver(false, readSpc.getInputStream(), sender.sendBuffer);

		assertAll("After ", () -> {
			assertTimeoutPreemptively(Duration.ofMillis(pc.getTestTimeout()), () -> {
				sender.run();
				receiver.run();
			});
		}, () -> {
			sender.assertStateAfterExecution();
		}, () -> {
			receiver.assertStateAfterExecution();
		});
	}

	public void writeBytes_ReadSingle(PortConfiguration pc) throws Exception {
		open(pc);
		final Sender sender = new Sender(false, writeSpc.getOutputStream(), initBuffer(pc.getBufferSize()));
		final Receiver receiver = new Receiver(true, readSpc.getInputStream(), sender.sendBuffer);

		assertAll("After ", () -> {
			assertTimeoutPreemptively(Duration.ofMillis(pc.getTestTimeout()), () -> {
				sender.run();
				receiver.run();
			});
		}, () -> {
			sender.assertStateAfterExecution();
		}, () -> {
			receiver.assertStateAfterExecution();
		});
	}

	public void writeSingle_ReadBytes(PortConfiguration pc) throws Exception {
		open(pc);
		final Sender sender = new Sender(true, writeSpc.getOutputStream(), initBuffer(pc.getBufferSize()));
		final Receiver receiver = new Receiver(false, readSpc.getInputStream(), sender.sendBuffer);

		assertAll("After ", () -> {
			assertTimeoutPreemptively(Duration.ofMillis(pc.getTestTimeout()), () -> {
				sender.run();
				receiver.run();
			});
		}, () -> {
			sender.assertStateAfterExecution();
		}, () -> {
			receiver.assertStateAfterExecution();
		});
	}

	public void writeSingle_ReadSingle(PortConfiguration pc) throws Exception {
		open(pc);
		final Sender sender = new Sender(true, writeSpc.getOutputStream(), initBuffer(pc.getBufferSize()));
		final Receiver receiver = new Receiver(true, readSpc.getInputStream(), sender.sendBuffer);

		assertAll("After ", () -> {
			assertTimeoutPreemptively(Duration.ofMillis(pc.getTestTimeout()), () -> {
				sender.run();
				receiver.run();
			});
		}, () -> {
			sender.assertStateAfterExecution();
		}, () -> {
			receiver.assertStateAfterExecution();
		});
	}

	public void writeBytes_ReadBytes_Threaded(PortConfiguration pc) throws Exception {
		open(pc);
		final Sender sender = new Sender(false, writeSpc.getOutputStream(), initBuffer(pc.getBufferSize()));
		final Receiver receiver = new Receiver(false, readSpc.getInputStream(), sender.sendBuffer);

		assertAll("After ", () -> {
			assertTimeoutPreemptively(Duration.ofMillis(pc.getTestTimeout()), () -> {
				new Thread(receiver).start();
				new Thread(sender).start();
				synchronized (receiver.LOCK) {
					receiver.LOCK.wait();
				}
			});
		}, () -> {
			sender.assertStateAfterExecution();
		}, () -> {
			receiver.assertStateAfterExecution();
		});
	}

	public void writeBytes_ReadSingle_Threaded(PortConfiguration pc) throws Exception {
		open(pc);
		final Sender sender = new Sender(false, writeSpc.getOutputStream(), initBuffer(pc.getBufferSize()));
		final Receiver receiver = new Receiver(true, readSpc.getInputStream(), sender.sendBuffer);

		assertAll("After ", () -> {
			assertTimeoutPreemptively(Duration.ofMillis(pc.getTestTimeout()), () -> {
				new Thread(receiver).start();
				new Thread(sender).start();
				synchronized (receiver.LOCK) {
					receiver.LOCK.wait();
				}
			});
		}, () -> {
			sender.assertStateAfterExecution();
		}, () -> {
			receiver.assertStateAfterExecution();
		});
	}

	public void writeSingle_ReadBytes_Threaded(PortConfiguration pc) throws Exception {
		open(pc);
		final Sender sender = new Sender(true, writeSpc.getOutputStream(), initBuffer(pc.getBufferSize()));
		final Receiver receiver = new Receiver(false, readSpc.getInputStream(), sender.sendBuffer);
		assertAll("After ", () -> {
			assertTimeoutPreemptively(Duration.ofMillis(pc.getTestTimeout()), () -> {
				new Thread(receiver).start();
				new Thread(sender).start();
				synchronized (receiver.LOCK) {
					receiver.LOCK.wait();
				}
			});
		}, () -> {
			sender.assertStateAfterExecution();
		}, () -> {
			receiver.assertStateAfterExecution();
		});
	}

	public void writeSingle_ReadSingle_Threaded(PortConfiguration pc) throws Exception {
		open(pc);
		final Sender sender = new Sender(true, writeSpc.getOutputStream(), initBuffer(pc.getBufferSize()));
		final Receiver receiver = new Receiver(false, readSpc.getInputStream(), sender.sendBuffer);

		assertAll("After ", () -> {
			assertTimeoutPreemptively(Duration.ofMillis(pc.getTestTimeout()), () -> {
				new Thread(receiver).start();
				new Thread(sender).start();
				synchronized (receiver.LOCK) {
					receiver.LOCK.wait();
				}
			});
		}, () -> {
			sender.assertStateAfterExecution();
		}, () -> {
			receiver.assertStateAfterExecution();
		});
	}



}
