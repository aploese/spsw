package de.ibapl.spsw.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.Iterator;
import java.util.logging.Level;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(Lifecycle.PER_CLASS)
public abstract class AbstractReadWriteTest extends AbstractPortTest {

	public class Receiver implements Runnable {

		final boolean readSingle;
		final InputStream is;
		boolean done;
		final Object LOCK = new Object();
		final byte[] recBuffer;
		final byte[] sendBuffer;

		Exception ex;
		Error err;

		public Receiver(boolean readSingle, InputStream is, byte[] sendBuffer) {
			this.readSingle = readSingle;
			this.is = is;
			this.sendBuffer = sendBuffer;
			this.recBuffer = new byte[sendBuffer.length];
		}

		@Override
		public void run() {
			try {
				int offset = 0;
				while (true) {
					if (readSingle) {
						final int data = is.read();
						if (data >= 0) {
							final int pos = offset;
							offset++;
							recBuffer[pos] = (byte) data;
							assertEquals(sendBuffer[pos], recBuffer[pos], () -> {
								return String.format("Arrays differ @%d expected but was %02x", pos, sendBuffer[pos],
										recBuffer[pos]);
							});
						} else {
							throw new RuntimeException("TODO implement me");
						}
						if (offset == recBuffer.length) {
							break;
						}
					} else {
						final int count = is.read(recBuffer, offset, recBuffer.length - offset);
						if (count > 0) {
							for (int i = 0; i < count; i++) {
								final int pos = offset + i;
								assertEquals(sendBuffer[offset], recBuffer[offset], () -> {
									return String.format("Arrays differ @%d expected but was %02x", pos,
											sendBuffer[pos], recBuffer[pos]);
								});
							}
							offset += count;
							if (offset == recBuffer.length) {
								break;
							}
						}
						LOG.log(Level.FINEST, "Bytes read: {0}", count);
						if (count <= 0) {
							if (offset < recBuffer.length) {
								LOG.log(Level.SEVERE, "Bytes missing: {0}", recBuffer.length - offset);
								// TODO printArrays("Too short");
							}
							break;
						}
					}
				}
				LOG.log(Level.INFO, "Byte total read: {0}", offset);
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
					// TODO printArrays("Error");
					LOG.log(Level.SEVERE, "Send Thread Error", err);
					LOCK.notifyAll();
				}
			}
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
	}

	protected byte[] initBuffer(final int size) {
		final byte[] result = new byte[size];
		for (int i = 0; i < size; i++) {
			result[i] = (byte) i;
		}
		return result;
	}

	@BeforeEach
	public void setUp(TestInfo testinfo) throws Exception {
		assumeRWTest();
		LOG.info(MessageFormat.format("do run test : {0}", testinfo.getDisplayName()));
		super.setUp();
	}

	public abstract Iterator<PortConfiguration> getTestPortConfigurations();

	@ParameterizedTest
	@MethodSource({ "getTestPortConfigurations" })
	public void writeBytesReadBytesTest(PortConfiguration pc) throws Exception {
		open(pc);
		final Sender sender = new Sender(true, writeSpc.getOutputStream(), initBuffer(pc.getBufferSize()));
		final Receiver receiver = new Receiver(false, readSpc.getInputStream(), sender.sendBuffer);

		assertTimeoutPreemptively(Duration.ofMillis(pc.calcMaxTransferTime()), () -> {
			sender.run();
			receiver.run();
			assertNull(receiver.err);
			assertNull(receiver.ex);
			assertTrue(receiver.done);
		});
	}

	@ParameterizedTest
	@MethodSource({ "getTestPortConfigurations" })
	public void writeBytesReadSingleTest(PortConfiguration pc) throws Exception {
		open(pc);
		final Sender sender = new Sender(false, writeSpc.getOutputStream(), initBuffer(pc.getBufferSize()));
		final Receiver receiver = new Receiver(true, readSpc.getInputStream(), sender.sendBuffer);

		assertTimeoutPreemptively(Duration.ofMillis(pc.calcMaxTransferTime()), () -> {
			sender.run();
			receiver.run();
			assertNull(receiver.err);
			assertNull(receiver.ex);
			assertTrue(receiver.done);
		});
	}

	@ParameterizedTest
	@MethodSource({ "getTestPortConfigurations" })
	public void writeSingleReadBytesTest(PortConfiguration pc) throws Exception {
		open(pc);
		final Sender sender = new Sender(true, writeSpc.getOutputStream(), initBuffer(pc.getBufferSize()));
		final Receiver receiver = new Receiver(false, readSpc.getInputStream(), sender.sendBuffer);

		assertTimeoutPreemptively(Duration.ofMillis(pc.calcMaxTransferTime()), () -> {
			sender.run();
			receiver.run();
			assertNull(receiver.err);
			assertNull(receiver.ex);
			assertTrue(receiver.done);
		});
	}

	@ParameterizedTest
	@MethodSource({ "getTestPortConfigurations" })
	public void writeSingleReadSingleTest(PortConfiguration pc) throws Exception {
		open(pc);
		final Sender sender = new Sender(true, writeSpc.getOutputStream(), initBuffer(pc.getBufferSize()));
		final Receiver receiver = new Receiver(true, readSpc.getInputStream(), sender.sendBuffer);

		assertTimeoutPreemptively(Duration.ofMillis(pc.calcMaxTransferTime()), () -> {
			sender.run();
			receiver.run();
			assertNull(receiver.err);
			assertNull(receiver.ex);
			assertTrue(receiver.done);
		});
	}

	@ParameterizedTest
	@MethodSource({ "getTestPortConfigurations" })
	public void writeBytesReadBytesTestThreaded(PortConfiguration pc) throws Exception {
		open(pc);
		final Sender sender = new Sender(false, writeSpc.getOutputStream(), initBuffer(pc.getBufferSize()));
		final Receiver receiver = new Receiver(false, readSpc.getInputStream(), sender.sendBuffer);

		assertTimeoutPreemptively(Duration.ofMillis(pc.calcMaxTransferTime()), () -> {
			new Thread(receiver).start();
			new Thread(sender).start();
			synchronized (receiver.LOCK) {
				receiver.LOCK.wait();
				assertNull(receiver.err);
				assertNull(receiver.ex);
				assertTrue(receiver.done);
			}
		});
	}

	@ParameterizedTest
	@MethodSource({ "getTestPortConfigurations" })
	public void writeBytesReadSingleTestThreaded(PortConfiguration pc) throws Exception {
		open(pc);
		final Sender sender = new Sender(false, writeSpc.getOutputStream(), initBuffer(pc.getBufferSize()));
		final Receiver receiver = new Receiver(true, readSpc.getInputStream(), sender.sendBuffer);

		assertTimeoutPreemptively(Duration.ofMillis(pc.calcMaxTransferTime()), () -> {
			new Thread(receiver).start();
			new Thread(sender).start();
			synchronized (receiver.LOCK) {
				receiver.LOCK.wait();
				assertNull(receiver.err);
				assertNull(receiver.ex);
				assertTrue(receiver.done);
			}
		});
	}

	@ParameterizedTest
	@MethodSource({ "getTestPortConfigurations" })
	public void writeSingleReadBytesTestThreaded(PortConfiguration pc) throws Exception {
		open(pc);
		final Sender sender = new Sender(true, writeSpc.getOutputStream(), initBuffer(pc.getBufferSize()));
		final Receiver receiver = new Receiver(false, readSpc.getInputStream(), sender.sendBuffer);

		assertTimeoutPreemptively(Duration.ofMillis(pc.calcMaxTransferTime()), () -> {
			new Thread(receiver).start();
			new Thread(sender).start();
			synchronized (receiver.LOCK) {
				receiver.LOCK.wait();
				assertNull(receiver.err);
				assertNull(receiver.ex);
				assertTrue(receiver.done);
			}
		});
	}

	@ParameterizedTest
	@MethodSource({ "getTestPortConfigurations" })
	public void writeSingleReadSingleTestThreaded(PortConfiguration pc) throws Exception {
		open(pc);
		final Sender sender = new Sender(true, writeSpc.getOutputStream(), initBuffer(pc.getBufferSize()));
		final Receiver receiver = new Receiver(false, readSpc.getInputStream(), sender.sendBuffer);

		assertTimeoutPreemptively(Duration.ofMillis(pc.calcMaxTransferTime()), () -> {
			new Thread(receiver).start();
			new Thread(sender).start();
			synchronized (receiver.LOCK) {
				receiver.LOCK.wait();
				assertNull(receiver.err);
				assertNull(receiver.ex);
				assertTrue(receiver.done);
			}
		});
	}

}
