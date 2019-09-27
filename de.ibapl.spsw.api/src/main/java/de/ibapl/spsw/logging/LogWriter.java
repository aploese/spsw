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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import org.osgi.annotation.versioning.ProviderType;

import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;

/**
 * Utility class to write to a OutputStream. This class handles the actual
 * formatting and writing to the {@link OutputStream}. It measures the time for
 * read and write calls and prints the duration if desired.
 * 
 * @author Arne Plöse
 */
@ProviderType
public class LogWriter {

	private final static String ACION_CALL = "\tcall";
	private final static String ACION_RETURN = "\treturn";
	private final boolean ascii;
	private Instant baseTimeStamp;
	private final DateTimeFormatter dateTimeFormatter;
	private final PrintStream log;
	private Instant isReadStartTS;
	private Instant channelReadStartTS;
	private final TimeStampLogging timeStampLogging;
	private final boolean verbose;
	private Instant osWriteStartTS;
	private Instant channelWriteStartTS;

	/**
	 * 
	 * @param logStream
	 *            the {@link OutputStream} to write to.
	 * @param ascii
	 *            If true write bytes in their ASCII representation otherwise format
	 *            hexadecimal.
	 * @param timeStampLogging
	 *            How to log timestamps.
	 * @param verbose
	 *            If true be more verbose.
	 */
	public LogWriter(OutputStream logStream, boolean ascii, TimeStampLogging timeStampLogging, boolean verbose) {
		dateTimeFormatter = DateTimeFormatter.ISO_INSTANT;
		this.ascii = ascii;
		this.log = new PrintStream(logStream, false);
		this.timeStampLogging = timeStampLogging;
		this.verbose = verbose;
		this.baseTimeStamp = Instant.now();
	}

	public void afterAvailable(Instant ts, int available) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("IS").append(ACION_RETURN).append(" available:\t").println(available);
		log.flush();
	}

	public void afterAvailable(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("IS").append(ACION_RETURN).append(" available:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	public void afterFlush(final Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("OS").append(ACION_RETURN).append(" flush").println(formatTs(ts));
		log.flush();
	}

	public void afterFlush(final Instant ts, IOException e) {
		log.append(formatTs(ts)).append("OS").append(ACION_RETURN).append(" flush:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterGetSpeed(Instant ts, Speed result) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getSpeed:\t").println(result);
		log.flush();
	}

	void afterGetSpeed(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getSpeed:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterGetDatatBits(Instant ts, DataBits result) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getDatatBits:\t").println(result);
		log.flush();
	}

	void afterGetDatatBits(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getDatatBits:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterGetFlowControl(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getFlowControl:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterGetFlowControl(Instant ts, Set<FlowControl> result) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getFlowControl:\t").println(result);
		log.flush();
	}

	void afterGetInBufferBytesCount(Instant ts, int result) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getInBufferBytesCount:\t").println(result);
		log.flush();
	}

	void afterGetInBufferBytesCount(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getInBufferBytesCount:\t")
				.println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterGetInterByteReadTimeout(Instant ts, int result) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getInterByteReadTimeout:\t")
				.println(result);
		log.flush();
	}

	void afterGetInterByteReadTimeout(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getInterByteReadTimeout:\t")
				.println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterGetOutBufferBytesCount(Instant ts, int result) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getOutBufferBytesCount:\t").println(result);
		log.flush();
	}

	void afterGetOutBufferBytesCount(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getOutBufferBytesCount:\t")
				.println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterGetOverallReadTimeout(Instant ts, int result) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getOverallReadTimeout:\t").println(result);
		log.flush();
	}

	void afterGetOverallReadTimeout(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getOverallReadTimeout:\t")
				.println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterGetOverallWriteTimeout(Instant ts, int result) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getOverallWriteTimeout:\t").println(result);
		log.flush();
	}

	void afterGetOverallWriteTimeout(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getOverallWriteTimeout:\t")
				.println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterGetParity(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getParity:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterGetParity(Instant ts, Parity result) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).println(" getParity:\t");
		log.flush();
	}

	void afterGetStopBits(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getStopBits:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterGetStopBits(Instant ts, StopBits result) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getStopBits:\t").println(result);
		log.flush();
	}

	void afterGetXOFFChar(Instant ts, char result) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getXOFFChar:\t").println(result);
		log.flush();
	}

	void afterGetXOFFChar(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getXOFFChar:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterGetXONChar(Instant ts, char result) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getXONChar:\t").println(result);
		log.flush();
	}

	void afterGetXONChar(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getXONChar:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	public void afterIsClose(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("IS").append(ACION_RETURN).println(" close");
		log.flush();
	}

	public void afterIsClose(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("IS").append(ACION_RETURN).append(" close:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterIsCTS(Instant ts, boolean result) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" isCTS:\t").println(result);
		log.flush();
	}

	void afterIsCTS(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" isCTS:\t").println(e.toString());
		log.flush();
		e.printStackTrace(log);
	}

	void afterIsDCD(Instant ts, boolean result) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" isDCD:\t").println(result);
		log.flush();
	}

	void afterIsDCD(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" isDCD:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterIsDSR(Instant ts, boolean result) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" isDSR:\t").println(result);
		log.flush();
	}

	void afterIsDSR(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" isDSR:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterIsRI(Instant ts, boolean result) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" isRI:\t").println(result);
		log.flush();
	}

	void afterIsRI(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" isRI:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	public void afterOsClose(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("OS").append(ACION_RETURN).println(" close");
		log.flush();
	}

	public void afterOsClose(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("OS").append(ACION_RETURN).append(" close:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	public void afterChannelClose(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("CH").append(ACION_RETURN).println(" close");
		log.flush();
	}

	public void afterChannelClose(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("CH").append(ACION_RETURN).append(" close:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	public void afterIsRead(final Instant ts, int b) {
		log.append(formatTs(ts)).append("IS").append(ACION_RETURN).append(" read:\t\"");
		if (b >= 0) {
			appendByte((byte) b);
		}
		if (timeStampLogging != TimeStampLogging.NONE) {
			final Duration d = Duration.between(isReadStartTS, ts);
			if (d.isZero()) {
				log.append("\"");
			} else {
				log.append("\"\tduration: ").println(d.toString());
			}
		} else {
			log.append("\"");
		}
		log.println();
		log.flush();
	}

	public void afterIsRead(final Instant ts, int readLength, byte[] b, int off) {
		log.append(formatTs(ts)).append("IS").append(ACION_RETURN).append(" read:\t\"");
		for (int i = 0; i < readLength; i++) {
			appendByte(b[off + i]);
		}
		if (timeStampLogging != TimeStampLogging.NONE) {
			final Duration d = Duration.between(isReadStartTS, ts);
			if (d.isZero()) {
				log.append("\"");
			} else {
				log.append("\"\tduration: ").println(d.toString());
			}
		} else {
			log.append("\"");
		}
		log.println();
		log.flush();
	}

	void afterIsRead(final Instant ts, IOException e) {
		log.append(formatTs(ts)).append("IS").append(ACION_RETURN).append(" read:\t").print(e.toString());
		if (timeStampLogging != TimeStampLogging.NONE) {
			final Duration d = Duration.between(isReadStartTS, ts);
			if (d.isZero()) {
				log.println();
			} else {
				log.append("\"\tduration: ").println(d.toString());
			}
		} else {
			log.println();
		}

		e.printStackTrace(log);
		log.flush();
	}

	public void afterChannelRead(final Instant ts, ByteBuffer buffer, int position) {
		log.append(formatTs(ts)).append("CH").append(ACION_RETURN).append(" read:\t\"");
		for (int i = position; i < buffer.position(); i++) {
			appendByte(buffer.get(i));
		}
		if (timeStampLogging != TimeStampLogging.NONE) {
			final Duration d = Duration.between(channelReadStartTS, ts);
			if (d.isZero()) {
				log.append("\"");
			} else {
				log.append("\"\tduration: ").append(d.toString());
			}
		} else {
			log.append("\"");
		}
		log.println();
		log.flush();
	}

	void afterChannelRead(final Instant ts, ByteBuffer buff, IOException e) {
		log.append(formatTs(ts)).append("CH").append(ACION_RETURN).format(" read: buff.position=%d, buff.remaining=%d\t", buff.position(), buff.remaining()).print(e.toString());
		if (timeStampLogging != TimeStampLogging.NONE) {
			final Duration d = Duration.between(channelReadStartTS, ts);
			if (d.isZero()) {
				log.println();
			} else {
				log.append("\"\tduration: ").println(d.toString());
			}
		} else {
			log.println();
		}

		e.printStackTrace(log);
		log.flush();
	}

	void afterSendBreak(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).println(" sendBreak");
		log.flush();
	}

	void afterSendBreak(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" sendBreak:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterSendXOFF(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).println(" sendXOFF");
		log.flush();
	}

	void afterSendXOFF(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" sendXOFF:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterSendXON(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).println(" sendXON");
		log.flush();
	}

	void afterSendXON(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" sendXON:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterSetSpeed(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).println(" setSpeed");
		log.flush();
	}

	void afterSetSpeed(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" setSpeed:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterSetBreak(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).println(" setBreak");
		log.flush();
	}

	void afterSetBreak(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" setBreak:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterSetDataBits(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).println(" setDataBits");
		log.flush();
	}

	void afterSetDataBits(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" setDataBits:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterSetDTR(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).println(" setDTR ");
		log.flush();
	}

	void afterSetDTR(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" setDTR:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterSetFlowControl(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).println(" setFlowControl");
		log.flush();
	}

	void afterSetFlowControl(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" setFlowControl:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterSetParity(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).println(" setParity");
		log.flush();
	}

	void afterSetParity(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" setParity:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterSetRTS(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).println(" setRTS");
		log.flush();
	}

	void afterSetRTS(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" setRTS:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterSetStopBits(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).println(" setStopBits");
		log.flush();
	}

	void afterSetStopBits(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" setStopBits:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterSetTimeouts(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).println(" setTimeouts");
		log.flush();
	}

	void afterSetTimeouts(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" setTimeouts:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterSetXOFFChar(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).println(" setXOFFChar");
		log.flush();
	}

	void afterSetXOFFChar(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" setXOFFChar:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterSetXONChar(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).println(" setXONChar ");
		log.flush();
	}

	void afterSetXONChar(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" setXONChar:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	public void afterSpClose(final Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).println(" close");
		log.flush();
	}

	public void afterSpClose(final Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" close:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	public void afterOsWrite(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("OS").append(ACION_RETURN).println(" write");
		if (timeStampLogging != TimeStampLogging.NONE) {
			final Duration d = Duration.between(osWriteStartTS, ts);
			if (d.isZero()) {
			} else {
				log.append("\tduration: ").append(d.toString());
			}
		}
		log.println();
		log.flush();
	}

	void afterOsWrite(Instant ts, IOException e) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("OS").append(ACION_RETURN).append(" write:\t").print(e.toString());
		if (timeStampLogging != TimeStampLogging.NONE) {
			final Duration d = Duration.between(osWriteStartTS, ts);
			if (d.isZero()) {
				log.println();
			} else {
				log.append("\"\tduration: ").println(d.toString());
			}
		} else {
			log.println();
		}
		e.printStackTrace(log);
		log.flush();
	}

	public void afterChannelWrite(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("CH").append(ACION_RETURN).println(" write");
		if (timeStampLogging != TimeStampLogging.NONE) {
			final Duration d = Duration.between(channelWriteStartTS, ts);
			if (d.isZero()) {
			} else {
				log.append("\tduration: ").append(d.toString());
			}
		}
		log.println();
		log.flush();
	}

	void afterChannelWrite(Instant ts, ByteBuffer buff, IOException e) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("CH").append(ACION_RETURN).format(" write: buff.position=%d, buff.remaining=%d\t", buff.position(), buff.remaining()).print(e.toString());
		if (timeStampLogging != TimeStampLogging.NONE) {
			final Duration d = Duration.between(channelWriteStartTS, ts);
			if (d.isZero()) {
				log.println();
			} else {
				log.append("\"\tduration: ").println(d.toString());
			}
		} else {
			log.println();
		}
		e.printStackTrace(log);
		log.flush();
	}

	private void appendByte(byte b) {
		if (ascii) {
			switch ((char) b) {
			case '\f':
				log.append("\\f");
				break;
			case '\b':
				log.append("\\n");
				break;
			case '\n':
				log.append("\\n");
				break;
			case '\r':
				log.append("\\r");
				break;
			case '\t':
				log.append("\\t");
				break;
			case '\'':
				log.append("\\\'");
				break;
			case '\"':
				log.append("\\\"");
				break;
			case '\\':
				log.append("\\");
				break;
			default:
				log.write((char) b);
			}
		} else {
			log.format("%02x", b);
		}
	}

	private void appendOsWritePrefix(Instant ts, String action) {
		osWriteStartTS = ts;
		log.append(formatTs(ts)).append("OS").append(action).append(" write:\t\"");
	}

	private void appendChannelWritePrefix(Instant ts, String action) {
		osWriteStartTS = ts;
		log.append(formatTs(ts)).append("CH").append(action).append(" write:\t\"");
	}

	public void beforeAvailable(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("IS").append(ACION_CALL).println(" available");
		log.flush();
	}

	public void beforeFlush(final Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("OS").append(ACION_CALL).println(" flush");
		log.flush();
	}

	void beforeGetSpeed(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" getSpeed");
		log.flush();
	}

	void beforeGetDatatBits(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" getDatatBits");
		log.flush();
	}

	void beforeGetFlowControl(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" getFlowControl");
		log.flush();
	}

	void beforeGetInBufferBytesCount(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" getInBufferBytesCount");
		log.flush();
	}

	void beforeGetInterByteReadTimeout(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" getInterByteReadTimeout");
		log.flush();
	}

	void beforeGetOutBufferBytesCount(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" getOutBufferBytesCount");
		log.flush();
	}

	void beforeGetOverallReadTimeout(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" getOverallReadTimeout");
		log.flush();
	}

	void beforeGetOverallWriteTimeout(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" getOverallWriteTimeout");
		log.flush();
	}

	void beforeGetParity(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" getParity");
		log.flush();
	}

	void beforeGetStopBits(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" getStopBits");
		log.flush();
	}

	void beforeGetXOFFChar(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" getXOFFChar");
		log.flush();
	}

	void beforeGetXONChar(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" getXONChar");
		log.flush();
	}

	public void beforeIsClose(Instant ts) {
		log.append(formatTs(ts)).append("IS").append(ACION_CALL).println(" close");
		log.flush();
	}

	void beforeIsCTS(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" isCTS ");
		log.flush();
	}

	void beforeIsDCD(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" isDCD");
		log.flush();
	}

	void beforeIsDSR(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" isDSR");
		log.flush();
	}

	void beforeIsRI(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" isRI");
		log.flush();
	}

	public void beforeOsClose(Instant ts) {
		log.append(formatTs(ts)).append("OS").append(ACION_CALL).println(" close");
		log.flush();
	}

	public void beforeChannelClose(Instant ts) {
		log.append(formatTs(ts)).append("CH").append(ACION_CALL).println(" close");
		log.flush();
	}

	public void beforeIsRead(Instant ts) {
		isReadStartTS = ts;
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("IS").append(ACION_CALL).println(" read");
		log.flush();
	}

	public void beforeChannelRead(Instant ts) {
		channelReadStartTS = ts;
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("IS").append(ACION_CALL).println(" read");
		log.flush();
	}

	void beforeSendBreak(Instant ts, int duration) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).append(" sendBreak:\t").println(duration);
		log.flush();
	}

	void beforeSendXOFF(Instant ts) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" sendXOFF");
		log.flush();
	}

	void beforeSendXON(Instant ts) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" sendXON");
		log.flush();
	}

	void beforeSetSpeed(Instant ts, Speed value) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).append(" setSpeed:\t").println(value);
		log.flush();
	}

	void beforeSetBreak(Instant ts, boolean value) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).append(" setBreak:\t").println(value);
		log.flush();
	}

	void beforeSetDataBits(Instant ts, DataBits value) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).append(" setDataBits:\t").println(value);
		log.flush();
	}

	void beforeSetDTR(Instant ts, boolean value) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).append(" setDTR:\t").println(value);
		log.flush();
	}

	void beforeSetFlowControl(Instant ts, Set<FlowControl> value) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).append(" setFlowControl:\t").println(value);
		log.flush();
	}

	void beforeSetParity(Instant ts, Parity value) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).append(" setParity:\t").println(value);
		log.flush();
	}

	void beforeSetRTS(Instant ts, boolean value) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).append(" setRTS:\t").println(value);
		log.flush();
	}

	void beforeSetStopBits(Instant ts, StopBits value) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).append(" setStopBits:\t").println(value);
		log.flush();
	}

	void beforeSetTimeouts(Instant ts, int interByteReadTimeout, int overallReadTimeout, int overallWriteTimeout) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).append(" setTimeouts:\t(interByteReadTimeout=")
				.print(interByteReadTimeout);
		log.append(", overallReadTimeout=").print(overallReadTimeout);
		log.append(", overallWriteTimeout=").print(overallWriteTimeout);
		log.println(")");
		log.flush();
	}

	void beforeSetXOFFChar(Instant ts, char value) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).append(" setXOFFChar:\t").println(value);
		log.flush();
	}

	void beforeSetXONChar(Instant ts, char value) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).append(" setXONChar:\t").println(value);
		log.flush();
	}

	public void beforeSpClose(final Instant ts) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" close");
		log.flush();
	}

	public void spOpend(final Instant ts, String portname, String args) {
		isReadStartTS = null;
		channelReadStartTS = null;
		osWriteStartTS = null;
		channelWriteStartTS = null;
		baseTimeStamp = Instant.now();
		log.append("@").append(dateTimeFormatter.format(ts)).append("\tSP").append(ACION_CALL).append(" opend:\t\"")
				.append(portname).append("\" (").append(args).println(")");
		log.flush();
	}

	public void beforeOsWrite(Instant ts, byte b) {
		appendOsWritePrefix(ts, ACION_CALL);
		appendByte(b);
		log.println("\"");
		log.flush();
	}

	public void beforeOsWrite(Instant ts, byte[] b) {
		appendOsWritePrefix(ts, ACION_CALL);
		for (byte b0 : b) {
			appendByte(b0);
		}
		log.println("\"");
		log.flush();
	}

	public void beforeOsWrite(Instant ts, byte[] b, int offset, int len) {
		appendOsWritePrefix(ts, ACION_CALL);
		for (int i = 0; i < len; i++) {
			appendByte(b[offset + i]);
		}
		log.println("\"");
		log.flush();
	}

	public void beforeChannelWrite(Instant ts, ByteBuffer buffer) {
		appendChannelWritePrefix(ts, ACION_CALL);
		for (int i = buffer.position(); i < buffer.limit(); i++) {
			appendByte(buffer.get(i));
		}
		log.println("\"");
		log.flush();
	}

	public void close() {
		log.close();
	}

	private String formatTs(Instant ts) {
		switch (timeStampLogging) {
		case NONE:
			return "";
		case FROM_OPEN:
			return '@' + Duration.between(baseTimeStamp, ts).toString() + '\t';
		case UTC:
			return '@' + dateTimeFormatter.format(ts) + '\t';
		default:
			throw new RuntimeException("Unknown timestamp logging: " + timeStampLogging);
		}
	}

	public void beforeChannelIsOpen(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("CH").append(ACION_CALL).println(" isOpen");
		log.flush();
	}

	public void afterChannelIsOpen(Instant ts, boolean result) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("CH").append(ACION_RETURN).append(" isOpen:\t").println(result);
		log.flush();
	}

	public void afterChannelIsOpen(Instant ts, Throwable t) {
		log.append(formatTs(ts)).append("Ch").append(ACION_RETURN).append(" isOpen:\t").println(t.toString());
		t.printStackTrace(log);
		log.flush();
	}

}
