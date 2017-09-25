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
    private final static String ACION_CALL = "call";
    private final static String ACION_RETURN = "return";

    public LogWriter(OutputStream logStream, boolean ascii) {
        dateTimeFormatter = DateTimeFormatter.ISO_INSTANT;
        this.ascii = ascii;
        this.log = new PrintStream(logStream, false);
    }

    public void close() {
        log.close();
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
        log.append("OS ").append(action).append(" write @").append(dateTimeFormatter.format(ts)).append(": \"");
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
        Duration d = Duration.between(writeStartTS, ts);
        log.append("OS ").append(ACION_RETURN).append(" write @").append(dateTimeFormatter.format(ts));
        if (d.isZero()) {
        } else {
            log.append(": duration: ").append(d.toString());
        }
        log.println();
        log.flush();
    }

    public void beforeRead(Instant ts) {
        readStartTS = ts;
        log.append("IS ").append(ACION_CALL).append(" read @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    public void afterRead(final Instant ts, int b) {
        log.append("IS ").append(ACION_RETURN).append(" read @").append(dateTimeFormatter.format(ts)).append(": \"");
        Duration d = Duration.between(readStartTS, ts);
        if (b >= 0) {
            appendByte((byte) b);
        }
        if (d.isZero()) {
            log.append('\"');
        } else {
            log.append("\" duration: ").append(d.toString());
        }
        log.println();
        log.flush();
    }

    public void afterRead(final Instant ts, int readLength, byte[] b, int off) {
        log.append("IS ").append(ACION_RETURN).append(" read @").append(dateTimeFormatter.format(ts)).append(": \"");
        Duration d = Duration.between(readStartTS, ts);
        for (int i = 0; i < readLength; i++) {
            appendByte(b[off + i]);
        }
        if (d.isZero()) {
            log.append('\"');
        } else {
            log.append("\" duration: ").append(d.toString());
        }
        log.println();
        log.flush();
    }

    void afterRead(final Instant ts, IOException e) {
        log.append("IS ").append(ACION_RETURN).append(" read @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void afterWrite(Instant ts, IOException e) {
        log.append("OS ").append(ACION_RETURN).append(" write @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    public void beforeFlush(final Instant ts) {
        log.append("OS ").append(ACION_CALL).append(" flush @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    public void afterFlush(final Instant ts) {
        log.append("OS ").append(ACION_RETURN).append(" flush @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    public void afterFlush(final Instant ts, IOException e) {
        log.append("OS ").append(ACION_RETURN).append(" flush @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    public void beforeSpOpen(final Instant ts, String type) {
        readStartTS = null;
        writeStartTS = null;
        log.append("SP ").append(ACION_CALL).append(" open @").append(dateTimeFormatter.format(ts)).append(": ").println(type);
        log.flush();
    }

    public void afterSpOpen(final Instant ts, String type) {
        readStartTS = null;
        writeStartTS = null;
        log.append("SP ").append(ACION_RETURN).append(" open @").append(dateTimeFormatter.format(ts)).append(": ").println(type);
        log.flush();
    }

    public void afterSpOpen(final Instant ts, String type, IOException e) {
        readStartTS = null;
        writeStartTS = null;
        log.append("SP ").append(ACION_RETURN).append(" open @").append(dateTimeFormatter.format(ts)).append(": ").append(type).append(' ').println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    public void beforeSpClose(final Instant ts) {
        log.append("SP ").append(ACION_CALL).append(" close @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    public void afterSpClose(final Instant ts) {
        log.append("SP ").append(ACION_RETURN).append(" close @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    public void afterSpClose(final Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" close @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    public void beforeOsClose(Instant ts) {
        log.append("OS ").append(ACION_CALL).append(" close @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    public void afterOsClose(Instant ts) {
        log.append("OS ").append(ACION_RETURN).append(" close @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    public void afterOsClose(Instant ts, IOException e) {
        log.append("OS ").append(ACION_RETURN).append(" close @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    public void beforeIsClose(Instant ts) {
        log.append("IS ").append(ACION_CALL).append(" close @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    public void afterIsClose(Instant ts) {
        log.append("IS ").append(ACION_RETURN).append(" close @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    public void afterIsClose(Instant ts, IOException e) {
        log.append("IS ").append(ACION_RETURN).append(" close @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    public void beforeAvailable(Instant ts) {
        log.append("IS ").append(ACION_CALL).append(" available @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    public void afterAvailable(Instant ts, int available) {
        log.append("IS ").append(ACION_RETURN).append(" available @").append(dateTimeFormatter.format(ts)).append(": ").println(available);
        log.flush();
    }

    public void afterAvailable(Instant ts, IOException e) {
        log.append("IS ").append(ACION_RETURN).append(" available @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeIsCTS(Instant ts) {
        log.append("SP ").append(ACION_CALL).append(" isCTS @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterIsCTS(Instant ts, boolean result) {
        log.append("SP ").append(ACION_RETURN).append(" isCTS @").append(dateTimeFormatter.format(ts)).append(": ").println(result);
        log.flush();
    }

    void afterIsCTS(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" isCTS @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        log.flush();
        e.printStackTrace(log);
    }

    void beforeIsDSR(Instant ts) {
        log.append("SP ").append(ACION_CALL).append(" isDSR @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterIsDSR(Instant ts, boolean result) {
        log.append("SP ").append(ACION_RETURN).append(" isDSR @").append(dateTimeFormatter.format(ts)).append(": ").println(result);
        log.flush();
    }

    void afterIsDSR(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" isDSR @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeIsIncommingRI(Instant ts) {
        log.append("SP ").append(ACION_CALL).append(" isIncommingRI @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterIsIncommingRI(Instant ts, boolean result) {
        log.append("SP ").append(ACION_RETURN).append(" isIncommingRI @").append(dateTimeFormatter.format(ts)).append(": ").println(result);
        log.flush();
    }

    void afterIsIncommingRI(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" isIncommingRI @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeSetRTS(Instant ts, boolean value) {
        log.append("SP ").append(ACION_CALL).append(" setRTS @").append(dateTimeFormatter.format(ts)).append(": ").println(value);
        log.flush();
    }

    void afterSetRTS(Instant ts) {
        log.append("SP ").append(ACION_RETURN).append(" setRTS @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterSetRTS(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" setRTS @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeSetDTR(Instant ts, boolean value) {
        log.append("SP ").append(ACION_CALL).append(" setDTR @").append(dateTimeFormatter.format(ts)).append(": ").println(value);
        log.flush();
    }

    void afterSetDTR(Instant ts) {
        log.append("SP ").append(ACION_RETURN).append(" setDTR @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterSetDTR(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" setDTR @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeSetXONChar(Instant ts, char value) {
        log.append("SP ").append(ACION_CALL).append(" setXONChar @").append(dateTimeFormatter.format(ts)).append(": ").println(value);
        log.flush();
    }

    void afterSetXONChar(Instant ts) {
        log.append("SP ").append(ACION_RETURN).append(" setXONChar @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterSetXONChar(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" setXONChar @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeSetXOFFChar(Instant ts, char value) {
        log.append("SP ").append(ACION_CALL).append(" setXOFFChar @").append(dateTimeFormatter.format(ts)).append(": ").println(value);
        log.flush();
    }

    void afterSetXOFFChar(Instant ts) {
        log.append("SP ").append(ACION_RETURN).append(" setXOFFChar @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterSetXOFFChar(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" setXOFFChar @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeGetXONChar(Instant ts) {
        log.append("SP ").append(ACION_CALL).append(" getXONChar @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterGetXONChar(Instant ts, char result) {
        log.append("SP ").append(ACION_RETURN).append(" getXONChar @").append(dateTimeFormatter.format(ts)).append(": ").println(result);
        log.flush();
    }

    void afterGetXONChar(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" getXONChar @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeGetXOFFChar(Instant ts) {
        log.append("SP ").append(ACION_CALL).append(" getXOFFChar @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterGetXOFFChar(Instant ts, char result) {
        log.append("SP ").append(ACION_RETURN).append(" getXOFFChar @").append(dateTimeFormatter.format(ts)).append(": ").println(result);
        log.flush();
    }

    void afterGetXOFFChar(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" getXOFFChar @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeSendXON(Instant ts) {
        log.append("SP ").append(ACION_CALL).append(" sendXON @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterSendXON(Instant ts) {
        log.append("SP ").append(ACION_RETURN).append(" sendXON @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterSendXON(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" sendXON @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeSendXOFF(Instant ts) {
        log.append("SP ").append(ACION_CALL).append(" sendXOFF @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterSendXOFF(Instant ts) {
        log.append("SP ").append(ACION_RETURN).append(" sendXOFF @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterSendXOFF(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" sendXOFF @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeGetInBufferBytesCount(Instant ts) {
        log.append("SP ").append(ACION_CALL).append(" getInBufferBytesCount @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterGetInBufferBytesCount(Instant ts, int result) {
        log.append("SP ").append(ACION_RETURN).append(" getInBufferBytesCount @").append(dateTimeFormatter.format(ts)).append(": ").println(result);
        log.flush();
    }

    void afterGetInBufferBytesCount(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" getInBufferBytesCount @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeGetOutBufferBytesCount(Instant ts) {
        log.append("SP ").append(ACION_CALL).append(" getOutBufferBytesCount @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterGetOutBufferBytesCount(Instant ts, int result) {
        log.append("SP ").append(ACION_RETURN).append(" getOutBufferBytesCount @").append(dateTimeFormatter.format(ts)).append(": ").println(result);
        log.flush();
    }

    void afterGetOutBufferBytesCount(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" getOutBufferBytesCount @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeSendBreak(Instant ts, int duration) {
        log.append("SP ").append(ACION_CALL).append(" sendBreak @").append(dateTimeFormatter.format(ts)).append(": ").println(duration);
        log.flush();
    }

    void afterSendBreak(Instant ts) {
        log.append("SP ").append(ACION_RETURN).append(" sendBreak @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterSendBreak(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" sendBreak @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeSetBreak(Instant ts, boolean value) {
        log.append("SP ").append(ACION_CALL).append(" setBreak @").append(dateTimeFormatter.format(ts)).append(": ").println(value);
        log.flush();
    }

    void afterSetBreak(Instant ts) {
        log.append("SP ").append(ACION_RETURN).append(" setBreak @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterSetBreak(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" setBreak @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeSetFlowControl(Instant ts, Set<FlowControl> value) {
        log.append("SP ").append(ACION_CALL).append(" setFlowControl @").append(dateTimeFormatter.format(ts)).append(": ").println(value);
        log.flush();
    }

    void afterSetFlowControl(Instant ts) {
        log.append("SP ").append(ACION_RETURN).append(" setFlowControl @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterSetFlowControl(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" setFlowControl @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeSetBaudrate(Instant ts, Baudrate value) {
        log.append("SP ").append(ACION_CALL).append(" setBaudrate @").append(dateTimeFormatter.format(ts)).append(": ").println(value);
        log.flush();
    }

    void afterSetBaudrate(Instant ts) {
        log.append("SP ").append(ACION_RETURN).append(" setBaudrate @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterSetBaudrate(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" setBaudrate @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeSetDataBits(Instant ts, DataBits value) {
        log.append("SP ").append(ACION_CALL).append(" setDataBits @").append(dateTimeFormatter.format(ts)).append(": ").println(value);
        log.flush();
    }

    void afterSetDataBits(Instant ts) {
        log.append("SP ").append(ACION_RETURN).append(" setDataBits @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterSetDataBits(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" setDataBits @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeSetStopBits(Instant ts, StopBits value) {
        log.append("SP ").append(ACION_CALL).append(" setStopBits @").append(dateTimeFormatter.format(ts)).append(": ").println(value);
        log.flush();
    }

    void afterSetStopBits(Instant ts) {
        log.append("SP ").append(ACION_RETURN).append(" setStopBits @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterSetStopBits(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" setStopBits @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeSetParity(Instant ts, Parity value) {
        log.append("SP ").append(ACION_CALL).append(" setParity @").append(dateTimeFormatter.format(ts)).append(": ").println(value);
        log.flush();
    }

    void afterSetParity(Instant ts) {
        log.append("SP ").append(ACION_RETURN).append(" setParity @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterSetParity(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" setParity @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeSetTimeout(Instant ts, int value) {
        log.append("SP ").append(ACION_CALL).append(" setTimeout @").append(dateTimeFormatter.format(ts)).append(": ").println(value);
        log.flush();
    }

    void afterSetTimeout(Instant ts, int result) {
        log.append("SP ").append(ACION_RETURN).append(" setTimeout @").append(dateTimeFormatter.format(ts)).append(": ").println(result);
        log.flush();
    }

    void afterSetTimeout(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" setTimeout @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeGetBaudrate(Instant ts) {
        log.append("SP ").append(ACION_CALL).append(" getBaudrate @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterGetBaudrate(Instant ts, Baudrate result) {
        log.append("SP ").append(ACION_RETURN).append(" getBaudrate @").append(dateTimeFormatter.format(ts)).append(": ").println(result);
        log.flush();
    }

    void afterGetBaudrate(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" getBaudrate @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeGetDatatBits(Instant ts) {
        log.append("SP ").append(ACION_CALL).append(" getDatatBits @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterGetDatatBits(Instant ts, DataBits result) {
        log.append("SP ").append(ACION_RETURN).append(" getDatatBits @").append(dateTimeFormatter.format(ts)).append(": ").println(result);
        log.flush();
    }

    void afterGetDatatBits(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" getDatatBits @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeGetStopBits(Instant ts) {
        log.append("SP ").append(ACION_CALL).append(" getStopBits @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterGetStopBits(Instant ts, StopBits result) {
        log.append("SP ").append(ACION_RETURN).append(" getStopBits @").append(dateTimeFormatter.format(ts)).append(": ").println(result);
        log.flush();
    }

    void afterGetStopBits(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" getStopBits @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeGetParity(Instant ts) {
        log.append("SP ").append(ACION_CALL).append(" getParity @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterGetParity(Instant ts, Parity result) {
        log.append("SP ").append(ACION_RETURN).append(" getParity @").append(dateTimeFormatter.format(ts)).append(": ").println(result);
        log.flush();
    }

    void afterGetParity(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" getParity @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeGetTimeout(Instant ts) {
        log.append("SP ").append(ACION_CALL).append(" getTimeout @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterGetTimeout(Instant ts, int result) {
        log.append("SP ").append(ACION_RETURN).append(" getTimeout @").append(dateTimeFormatter.format(ts)).append(": ").println(result);
        log.flush();
    }

    void afterGetTimeout(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" getTimeout @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeGetFlowControl(Instant ts) {
        log.append("SP ").append(ACION_CALL).append(" getFlowControl @").println(dateTimeFormatter.format(ts));
        log.flush();
    }

    void afterGetFlowControl(Instant ts, Set<FlowControl> result) {
        log.append("SP ").append(ACION_RETURN).append(" getFlowControl @").append(dateTimeFormatter.format(ts)).append(": ").println(result);
        log.flush();
    }

    void afterGetFlowControl(Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" getFlowControl @").append(dateTimeFormatter.format(ts)).append(": ").println(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

}
