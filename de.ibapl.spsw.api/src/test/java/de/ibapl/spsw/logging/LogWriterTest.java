/*
 * SPSW - Drivers for the serial port, https://github.com/aploese/spsw/
 * Copyright (C) 2009-2019, Arne Plöse and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
