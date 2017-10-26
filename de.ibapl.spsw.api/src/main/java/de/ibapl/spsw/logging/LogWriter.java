package de.ibapl.spsw.logging;

/*-
 * #%L
 * SPSW API
 * %%
 * Copyright (C) 2009 - 2017 Arne Plöse
 * %%
 * SPSW - Drivers for the serial port, https://github.com/aploese/spsw/
 * Copyright (C) 2009, 2017, Arne Plöse and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
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
 * #L%
 */
import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.StopBits;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAmount;
import java.util.Set;
import org.osgi.annotation.versioning.ProviderType;

/**
 *
 * @author aploese
 */
@ProviderType
public class LogWriter {

	private final PrintStream log;
	private Instant readStartTS;
	private Instant writeStartTS;
	private final DateTimeFormatter dateTimeFormatter;
	private final boolean ascii;
	private final static String ACION_CALL = "\tcall";
	private final static String ACION_RETURN = "\treturn";
	private Instant baseTimeStamp;
	private final TimeStampLogging timeStampLogging;
	private final boolean verbose;

	public LogWriter(OutputStream logStream, boolean ascii, TimeStampLogging timeStampLogging, boolean verbose) {
		dateTimeFormatter = DateTimeFormatter.ISO_INSTANT;
		this.ascii = ascii;
		this.log = new PrintStream(logStream, false);
		this.timeStampLogging = timeStampLogging;
		this.verbose = verbose;
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
		case UTF:
			return '@' + dateTimeFormatter.format(ts) + '\t';
		default:
			throw new RuntimeException("Unknown timestamp logging: " + timeStampLogging);
		}
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

	private void appendWritePrefix(Instant ts, String action) {
		writeStartTS = ts;
		log.append(formatTs(ts)).append("OS").append(action).append(" write:\t\"");
	}

	public void beforeWrite(Instant ts, byte b) {
		appendWritePrefix(ts, ACION_CALL);
		appendByte(b);
		log.println('\"');
		log.flush();
	}

	public void beforeWrite(Instant ts, byte[] b) {
		appendWritePrefix(ts, ACION_CALL);
		for (byte b0 : b) {
			appendByte(b0);
		}
		log.println('\"');
		log.flush();
	}

	public void beforeWrite(Instant ts, byte[] b, int offset, int len) {
		appendWritePrefix(ts, ACION_CALL);
		for (int i = 0; i < len; i++) {
			appendByte(b[offset + i]);
		}
		log.println('\"');
		log.flush();
	}

	public void afterWrite(Instant ts) {
		if (!verbose) {
			return;
		}
		Duration d = Duration.between(writeStartTS, ts);
		log.append(formatTs(ts)).append("OS").append(ACION_RETURN).println(" write");
		if (d.isZero()) {
		} else if (timeStampLogging != timeStampLogging.NONE) {
			log.append("\tduration: ").append(d.toString());
		} else {
		}
		log.println();
		log.flush();
	}

	public void beforeRead(Instant ts) {
		readStartTS = ts;
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("IS").append(ACION_CALL).println(" read");
		log.flush();
	}

	public void afterRead(final Instant ts, int b) {
		log.append(formatTs(ts)).append("IS").append(ACION_RETURN).append(" read:\t\"");
		Duration d = Duration.between(readStartTS, ts);
		if (b >= 0) {
			appendByte((byte) b);
		}
		if (d.isZero()) {
			log.append('\"');
		} else if (timeStampLogging != timeStampLogging.NONE) {
			log.append("\"\tduration: ").append(d.toString());
		} else {
			log.append('\"');
		}
		log.println();
		log.flush();
	}

	public void afterRead(final Instant ts, int readLength, byte[] b, int off) {
		log.append(formatTs(ts)).append("IS").append(ACION_RETURN).append(" read:\t\"");
		Duration d = Duration.between(readStartTS, ts);
		for (int i = 0; i < readLength; i++) {
			appendByte(b[off + i]);
		}
		if (d.isZero()) {
			log.append('\"');
		} else if (timeStampLogging != timeStampLogging.NONE) {
			log.append("\"\tduration: ").append(d.toString());
		} else {
			log.append('\"');
		}
		log.println();
		log.flush();
	}

	void afterRead(final Instant ts, IOException e) {
		log.append(formatTs(ts)).append("IS").append(ACION_RETURN).append(" read:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void afterWrite(Instant ts, IOException e) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("OS").append(ACION_RETURN).append(" write:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	public void beforeFlush(final Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("OS").append(ACION_CALL).println(" flush");
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

	public void beforeSpOpen(final Instant ts, String portname, String type) {
		readStartTS = null;
		writeStartTS = null;
		baseTimeStamp = Instant.now();
		log.append('@').append(dateTimeFormatter.format(ts)).append("\tSP").append(ACION_CALL).append(" open:\t\"").append(portname).append("\" ").println(type);
		log.flush();
	}

	public void afterSpOpen(final Instant ts, String type) {
		if (!verbose) {
			return;
		}
		readStartTS = null;
		writeStartTS = null;
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" open:\t").println(type);
		log.flush();
	}

	public void afterSpOpen(final Instant ts, String type, IOException e) {
		readStartTS = null;
		writeStartTS = null;
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" open:\t").append(type).append(' ')
				.println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	public void beforeSpClose(final Instant ts) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" close");
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

	public void beforeOsClose(Instant ts) {
		log.append(formatTs(ts)).append("OS").append(ACION_CALL).println(" close");
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

	public void beforeIsClose(Instant ts) {
		log.append(formatTs(ts)).append("IS").append(ACION_CALL).println(" close");
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

	public void beforeAvailable(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("IS").append(ACION_CALL).println(" available");
		log.flush();
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

	void beforeIsCTS(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" isCTS ");
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

	void beforeIsDSR(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" isDSR");
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

	void beforeIsIncommingRI(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" isIncommingRI");
		log.flush();
	}

	void afterIsIncommingRI(Instant ts, boolean result) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" isIncommingRI:\t").println(result);
		log.flush();
	}

	void afterIsIncommingRI(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" isIncommingRI:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void beforeSetRTS(Instant ts, boolean value) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).append(" setRTS:\t").println(value);
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

	void beforeSetDTR(Instant ts, boolean value) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).append(" setDTR:\t").println(value);
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

	void beforeSetXONChar(Instant ts, char value) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).append(" setXONChar:\t").println(value);
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

	void beforeSetXOFFChar(Instant ts, char value) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).append(" setXOFFChar:\t").println(value);
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

	void beforeGetXONChar(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" getXONChar");
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

	void beforeGetXOFFChar(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" getXOFFChar");
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

	void beforeSendXON(Instant ts) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" sendXON");
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

	void beforeSendXOFF(Instant ts) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" sendXOFF");
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

	void beforeGetInBufferBytesCount(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" getInBufferBytesCount");
		log.flush();
	}

	void afterGetInBufferBytesCount(Instant ts, int result) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getInBufferBytesCount:\t")
				.println(result);
		log.flush();
	}

	void afterGetInBufferBytesCount(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getInBufferBytesCount:\t")
				.println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void beforeGetOutBufferBytesCount(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" getOutBufferBytesCount");
		log.flush();
	}

	void afterGetOutBufferBytesCount(Instant ts, int result) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getOutBufferBytesCount:\t")
				.println(result);
		log.flush();
	}

	void afterGetOutBufferBytesCount(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getOutBufferBytesCount:\t")
				.println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void beforeSendBreak(Instant ts, int duration) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).append(" sendBreak:\t").println(duration);
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

	void beforeSetBreak(Instant ts, boolean value) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).append(" setBreak:\t").println(value);
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

	void beforeSetFlowControl(Instant ts, Set<FlowControl> value) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).append(" setFlowControl:\t").println(value);
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

	void beforeSetBaudrate(Instant ts, Baudrate value) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).append(" setBaudrate:\t").println(value);
		log.flush();
	}

	void afterSetBaudrate(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).println(" setBaudrate");
		log.flush();
	}

	void afterSetBaudrate(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" setBaudrate:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void beforeSetDataBits(Instant ts, DataBits value) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).append(" setDataBits:\t").println(value);
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

	void beforeSetStopBits(Instant ts, StopBits value) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).append(" setStopBits:\t").println(value);
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

	void beforeSetParity(Instant ts, Parity value) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).append(" setParity:\t").println(value);
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

	void beforeSetTimeouts(Instant ts, int interByteReadTimeout, int overallReadTimeout, int overallWriteTimeout) {
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).append(" setTimeouts:\t(interByteReadTimeout=").print(interByteReadTimeout);
		log.append(", overallReadTimeout=").print(overallReadTimeout);
		log.append(", overallWriteTimeout=").print(overallWriteTimeout);
		log.println(")");
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
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" setTimeouts:\t")
				.println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void beforeGetBaudrate(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" getBaudrate");
		log.flush();
	}

	void afterGetBaudrate(Instant ts, Baudrate result) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getBaudrate:\t").println(result);
		log.flush();
	}

	void afterGetBaudrate(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getBaudrate:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void beforeGetDatatBits(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" getDatatBits");
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

	void beforeGetStopBits(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" getStopBits");
		log.flush();
	}

	void afterGetStopBits(Instant ts, StopBits result) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getStopBits:\t").println(result);
		log.flush();
	}

	void afterGetStopBits(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getStopBits:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void beforeGetParity(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" getParity");
		log.flush();
	}

	void afterGetParity(Instant ts, Parity result) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).println(" getParity:\t");
		log.flush();
	}

	void afterGetParity(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getParity:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

	void beforeGetInterByteReadTimeout(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" getInterByteReadTimeout");
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

	void beforeGetOverallReadTimeout(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" getOverallReadTimeout");
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

	void beforeGetOverallWriteTimeout(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" getOverallWriteTimeout");
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

	void beforeGetFlowControl(Instant ts) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_CALL).println(" getFlowControl");
		log.flush();
	}

	void afterGetFlowControl(Instant ts, Set<FlowControl> result) {
		if (!verbose) {
			return;
		}
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getFlowControl:\t").println(result);
		log.flush();
	}

	void afterGetFlowControl(Instant ts, IOException e) {
		log.append(formatTs(ts)).append("SP").append(ACION_RETURN).append(" getFlowControl:\t").println(e.toString());
		e.printStackTrace(log);
		log.flush();
	}

}
