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
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.api.TimeoutIOException;
import de.ibapl.spsw.tests.tags.BaselineTest;
import de.ibapl.spsw.tests.tags.DtrDsrTest;
import de.ibapl.spsw.tests.tags.RtsCtsTest;

/**
 * Unit test for simple App. Use @Ignore if your hardware can't handle higer
 * speeds.
 * 
 */
public abstract class AbstractReadWriteRtsCtsTests extends AbstractReadWriteTest {

	public Iterator<PortConfiguration> getBaselinePortConfigurations() {
		return new PortConfigurationFactory().setFlowControl(FlowControl.getFC_RTS_CTS()).getBaudrateIterator(Baudrate.B1200, Baudrate.B115200);
	}

	@RtsCtsTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_WriteBytes_ReadBytes(PortConfiguration pc) throws Exception {
		writeBytes_ReadBytes(pc);
	}
	
	@RtsCtsTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_WriteBytes_ReadSingle(PortConfiguration pc) throws Exception {
		writeBytes_ReadSingle(pc);
	}
	
	@RtsCtsTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_WriteSingle_ReadBytes(PortConfiguration pc) throws Exception {
		writeSingle_ReadBytes(pc);
	}

	@RtsCtsTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_WriteSingle_ReadSingle(PortConfiguration pc) throws Exception {
		writeSingle_ReadSingle(pc);
	}

	@RtsCtsTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_WriteBytes_ReadBytes_Threaded(PortConfiguration pc) throws Exception {
		writeBytes_ReadBytes_Threaded(pc);
	}

	@RtsCtsTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_WriteBytes_ReadSingle_Threaded(PortConfiguration pc) throws Exception {
		writeBytes_ReadSingle_Threaded(pc);
	}

	@RtsCtsTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_WriteSingle_ReadBytes_Threaded(PortConfiguration pc) throws Exception {
		writeSingle_ReadBytes_Threaded(pc);
	}

	@RtsCtsTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_WriteSingle_ReadSingle_Threaded(PortConfiguration pc) throws Exception {
		writeSingle_ReadSingle_Threaded(pc);
	}
	
	// TODO does not work as expected Linux Kernel fills in buffer size?
	@Disabled
	@Test
	public void testWriteBufferFull() throws Exception {
		assumeRWTest();
		LOG.log(Level.INFO, "run testWriteSingleByteTimeout");
		// Set a high baudrate to speed up things
		open(Baudrate.B115200, DataBits.DB_8, StopBits.SB_1, Parity.EVEN, FlowControl.getFC_RTS_CTS());
		setTimeouts(100, 1000, 10000);

		final byte[] b = new byte[1024 * 1024];
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) i;
		}
		int round = 1;
		int dataWritten;
		int overallDataWritten = 0;

		do {
			try {
				writeSpc.getOutputStream().write(0xFF);
				dataWritten = 1; // b.length;
			} catch (TimeoutIOException e) {
				dataWritten = e.bytesTransferred;
			}
			try {
				writeSpc.getOutputStream().flush();
			} catch (TimeoutIOException e) {
			}
			round++;
			overallDataWritten += dataWritten;
			LOG.log(Level.INFO,
					"Wrote: " + dataWritten + " (" + overallDataWritten + ") in " + round + " rounds; OutBuf:  "
							+ writeSpc.getOutBufferBytesCount() + "inBuffer: " + readSpc.getInBufferBytesCount());
		} while (dataWritten > 0);

		LOG.log(Level.INFO, "Wrote: " + overallDataWritten + " in " + round + " rounds; OutBuf:  "
				+ writeSpc.getOutBufferBytesCount());
		LOG.log(Level.INFO, "disable flow control to sped up closing");
		byte[] read = new byte[256];
		assertEquals(256, readSpc.getInputStream().read(read));
		new Thread(() -> {
			try {
				TimeoutIOException tIoException = assertThrows(TimeoutIOException.class, () -> {
					writeSpc.getOutputStream().write(b, 0, 1024);
				});
				assertEquals(512, tIoException.bytesTransferred);
			} catch (Throwable t) {
				fail(t.getMessage());
			}
		}).start();

		Thread.sleep(2000);

		assertEquals(256, readSpc.getInputStream().read(read));

		writeSpc.setBaudrate(Baudrate.B500000);
		writeSpc.setFlowControl(FlowControl.getFC_NONE());
	}

	/**
	 * The receiving (spc[1] should stop transfer via RTS/CTS ...
	 * 
	 * @throws Exception
	 */
	// TODO Does not work as expected ....
	@Disabled
	@Test
	public void testFillInbuffer() throws Exception {
		assumeRWTest();
		LOG.log(Level.INFO, "run testWriteSingleByteTimeout");
		// Set a high baudrate to speed up things
		open(Baudrate.B115200, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_RTS_CTS());
		setTimeouts(100, 1000, 10000);

		final byte[] b = new byte[1024];
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) i;
		}
		int round = 0;
		int dataWritten;
		int overallDataWritten = 0;

		do {
			try {
				writeSpc.getOutputStream().write(b);
				dataWritten = b.length;
			} catch (TimeoutIOException e) {
				dataWritten = e.bytesTransferred;
			}
			try {
				writeSpc.getOutputStream().flush();
			} catch (TimeoutIOException e) {
			}
			round++;
			overallDataWritten += dataWritten;
			LOG.log(Level.INFO,
					"Wrote: " + dataWritten + " (" + overallDataWritten + ") in " + round + " rounds; OutBuf:  "
							+ writeSpc.getOutBufferBytesCount() + "inBuffer: " + readSpc.getInBufferBytesCount());
			if (round == 1024) {
				break;
			}
		} while (dataWritten > 0);

		LOG.log(Level.INFO, "Wrote: " + overallDataWritten + " in " + round + " rounds; OutBuf:  "
				+ writeSpc.getOutBufferBytesCount());
		LOG.log(Level.INFO, "disable flow control to sped up closing");
		int dataread = 0;
		int readTotal = 0;

		do {
			try {
				dataread = readSpc.getInputStream().read(b);
				LOG.log(Level.INFO, "Read: " + dataread);
			} catch (TimeoutIOException e) {
				dataread = e.bytesTransferred;
				LOG.log(Level.INFO, "Got timeout");
			}
			readTotal += dataread;
		} while (dataread > 0);
		assertEquals(overallDataWritten, readTotal);

		writeSpc.setBaudrate(Baudrate.B500000);
		writeSpc.setFlowControl(FlowControl.getFC_NONE());
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
		assumeRWTest();

		open(Baudrate.B9600, DataBits.DB_8, StopBits.SB_1, Parity.EVEN, FlowControl.getFC_RTS_CTS());
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

	@RtsCtsTest
	@Test
	public void testInfiniteTimeout() throws Exception {
		assumeRWTest();

		open(Baudrate.B9600, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_RTS_CTS());
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
	final static int WAIT_TIME = 1;
	
	@RtsCtsTest
	@Test
	public void testManualRTS_CTS() throws Exception {
		assumeRWTest();
		open(Baudrate.B9600, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_NONE());
		
		readSpc.setRTS(false);
		Thread.sleep(WAIT_TIME);
		assertFalse(writeSpc.isCTS());
		readSpc.setRTS(true);
		Thread.sleep(WAIT_TIME);
		assertTrue(writeSpc.isCTS());

		writeSpc.setRTS(false);
		Thread.sleep(WAIT_TIME);
		assertFalse(readSpc.isCTS());
		writeSpc.setRTS(true);
		Thread.sleep(WAIT_TIME);
		assertTrue(readSpc.isCTS());
	}

	@DtrDsrTest
	@Test
	public void testManualDTR_DSR() throws Exception {
		assumeRWTest();
		open(Baudrate.B9600, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_NONE());
		
		readSpc.setDTR(false);
		Thread.sleep(WAIT_TIME);
		assertFalse(writeSpc.isDSR());
		readSpc.setDTR(true);
		Thread.sleep(WAIT_TIME);
		assertTrue(writeSpc.isDSR());

		writeSpc.setDTR(false);
		Thread.sleep(WAIT_TIME);
		assertFalse(readSpc.isDSR());
		//If this fails maybe DSR and DCD are not shortend?
		assertFalse(readSpc.isDCD());
		writeSpc.setDTR(true);
		Thread.sleep(WAIT_TIME);
		assertTrue(readSpc.isDSR());
		//If this fails maybe DSR and DCD are not shortend?
		assertTrue(readSpc.isDCD());
	}

}