/*-
 * #%L
 * SPSW API
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
package de.ibapl.spsw.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.time.Instant;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.ibapl.spsw.api.TimeoutIOException;

/**
 *
 * @author Arne Plöse
 */
@Disabled
public class LogWriterTest {

	public LogWriterTest() {
	}

	private void writeHEX(TimeStampLogging timeStampLogging, boolean verbose, String expected) {
		System.out.println("logWrite");
		Instant ts = Instant.parse("2017-07-25T18:47:02.763Z");
		ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
		LogWriter instance = new LogWriter(os, false, timeStampLogging, verbose);
		instance.beforeSpOpen(ts, "HEX_TEST_PORT", "");
		ts = ts.plusMillis(1);
		instance.afterSpOpen(ts);
		instance.beforeAvailable(ts);
		instance.afterAvailable(ts, 0);
		instance.beforeIsRead(ts);
		instance.beforeOsWrite(ts, (byte) 1);
		instance.afterOsWrite(ts);
		instance.beforeOsWrite(ts, (byte) 2);
		instance.afterOsWrite(ts);
		ts = ts.plusMillis(1);
		instance.beforeOsWrite(ts, (byte) 3);
		instance.afterOsWrite(ts);
		instance.afterIsRead(ts, 1);
		instance.beforeIsRead(ts);
		ts = ts.plusNanos(10);
		instance.afterIsRead(ts, 2);
		instance.beforeIsRead(ts);
		ts = ts.plusNanos(10);
		instance.afterIsRead(ts, 3);
		instance.beforeIsRead(ts);
		ts = ts.plusSeconds(1);
		instance.afterIsRead(ts, new TimeoutIOException());
		instance.beforeIsRead(ts);
		ts = ts.plusSeconds(1);
		instance.afterIsRead(ts, -1);
		instance.beforeSpClose(ts);
		ts = ts.plusSeconds(1);
		instance.afterSpClose(ts);
		assertEquals(expected, os.toString());
	}

	private void writeASCII(TimeStampLogging timeStampLogging, boolean verbose, String expected) {
		System.out.println("logWriteACII");
		Instant ts = Instant.parse("2017-07-25T18:47:02.763Z");
		ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
		LogWriter instance = new LogWriter(os, true, timeStampLogging, verbose);
		instance.beforeSpOpen(ts, "ASCII_TEST_PORT", "");
		ts = ts.plusMillis(1);
		instance.afterSpOpen(ts);
		instance.beforeAvailable(ts);
		instance.afterAvailable(ts, 0);
		instance.beforeIsRead(ts);
		instance.beforeOsWrite(ts, (byte) 'A');
		instance.afterOsWrite(ts);
		instance.beforeOsWrite(ts, (byte) 'B');
		instance.afterOsWrite(ts);
		ts = ts.plusMillis(1);
		instance.beforeOsWrite(ts, (byte) '\n');
		instance.afterOsWrite(ts);
		instance.beforeFlush(ts);
		instance.afterIsRead(ts, 'A');
		instance.beforeIsRead(ts);
		ts = ts.plusNanos(10);
		instance.afterIsRead(ts, 'B');
		instance.beforeIsRead(ts);
		ts = ts.plusNanos(10);
		instance.afterIsRead(ts, '\n');
		instance.beforeIsRead(ts);
		ts = ts.plusSeconds(1);
		instance.afterIsRead(ts, new TimeoutIOException());
		instance.beforeIsRead(ts);
		ts = ts.plusSeconds(1);
		instance.afterIsRead(ts, -1);
		instance.beforeSpClose(ts);
		ts = ts.plusSeconds(1);
		instance.afterSpClose(ts);
		assertEquals(expected, os.toString());
	}

	@Test
	public void testWriteASCII_UTF_Verbose() {
		writeASCII(TimeStampLogging.UTC, true, "");
	}

	@Test
	public void testWriteASCII_FROM_OPEN_Verbose() {
		writeASCII(TimeStampLogging.FROM_OPEN, true, "");
	}

	@Test
	public void testWriteASCII_NONE_Verbose() {
		writeASCII(TimeStampLogging.NONE, true, "");
	}

	@Test
	public void testWriteASCII_UTF_NonVerbose() {
		writeASCII(TimeStampLogging.UTC, false, "");
	}

	@Test
	public void testWriteASCII_FROM_OPEN_NonVerbose() {
		writeASCII(TimeStampLogging.FROM_OPEN, false, "");
	}

	@Test
	public void testWriteASCII_NONE_NonVerbose() {
		writeASCII(TimeStampLogging.NONE, false, "");
	}

	@Test
	public void testWriteHEX_UTF_Verbose() {
		writeHEX(TimeStampLogging.UTC, true, "");
	}

	@Test
	public void testWriteHEX_FROM_OPEN_Verbose() {
		writeHEX(TimeStampLogging.FROM_OPEN, true, "");
	}

	@Test
	public void testWriteHEX_NONE_Verbose() {
		writeHEX(TimeStampLogging.NONE, true, "");
	}

	@Test
	public void testWriteHEX_UTF_NonVerbose() {
		writeHEX(TimeStampLogging.UTC, false, "");
	}

	@Test
	public void testWriteHEX_FROM_OPEN_NonVerbose() {
		writeHEX(TimeStampLogging.FROM_OPEN, false, "");
	}

	@Test
	public void testWriteHEX_NONE_NonVerbose() {
		writeHEX(TimeStampLogging.NONE, false, "");
	}

}
