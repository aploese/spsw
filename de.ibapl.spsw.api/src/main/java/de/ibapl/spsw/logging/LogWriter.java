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
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
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
        log.append("\"\n");
        log.flush();
    }

    public void beforeWrite(Instant ts, byte[] b) {
        appendWritePrefix(ts, ACION_CALL);
        for (byte b0 : b) {
            appendByte(b0);
        }
        log.append("\"\n");
        log.flush();
    }

    public void beforeWrite(Instant ts, byte[] b, int offset, int len) {
        appendWritePrefix(ts, ACION_CALL);
        for (int i = 0; i < len; i++) {
            appendByte(b[offset + i]);
        }
        log.append("\"\n");
        log.flush();
    }

    public void afterWrite(Instant ts) {
        Duration d = Duration.between(writeStartTS, ts);
        log.append("OS ").append(ACION_RETURN).append(" write @").append(dateTimeFormatter.format(ts));
        if (d.isZero()) {
        } else {
            log.append(": duration: ").append(d.toString());
        }
        log.append('\n');
        log.flush();
    }

    public void beforeRead(Instant ts) {
        readStartTS = ts;
        log.append("IS ").append(ACION_CALL).append(" read @").append(dateTimeFormatter.format(ts)).append('\n');
        log.flush();
    }

    void afterRead(final Instant ts, int b) {
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
        log.append('\n');
    }

    public void afterRead(final Instant ts, int readLength, byte[] b, int off) {
        log.append("IS ").append(ACION_RETURN).append(" read @").append(dateTimeFormatter.format(ts)).append(": \"");
        Duration d = Duration.between(readStartTS, ts);
        for (int i= 0; i < readLength; i++) {
            appendByte(b[off + i]);
        }
        if (d.isZero()) {
            log.append('\"');
        } else {
            log.append("\" duration: ").append(d.toString());
        }
        log.append('\n');
    }

    void afterRead(final Instant ts, IOException e) {
        log.append("IS ").append(ACION_RETURN).append(" read @").append(dateTimeFormatter.format(ts)).append(": ").append(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void afterWrite(Instant ts, IOException e) {
        log.append("OS ").append(ACION_RETURN).append(" write @").append(dateTimeFormatter.format(ts)).append(": ").append(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    public void beforeFlush(final Instant ts) {
        log.append("OS ").append(ACION_CALL).append(" flush @").append(dateTimeFormatter.format(ts)).append('\n');
        log.flush();
    }

    public void afterFlush(final Instant ts) {
        log.append("OS ").append(ACION_RETURN).append(" flush @").append(dateTimeFormatter.format(ts)).append('\n');
        log.flush();
    }

    public void afterFlush(final Instant ts, IOException e) {
        log.append("OS ").append(ACION_RETURN).append(" flush @").append(dateTimeFormatter.format(ts)).append(": ").append(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    public void beforeSpOpen(final Instant ts, String type) {
        readStartTS = null;
        writeStartTS = null;
        log.append("SP ").append(ACION_CALL).append(" open @").append(dateTimeFormatter.format(ts)).append(": ").append(type).append('\n');
        log.flush();
    }
 
    public void afterSpOpen(final Instant ts, String type) {
        readStartTS = null;
        writeStartTS = null;
        log.append("SP ").append(ACION_RETURN).append(" open @").append(dateTimeFormatter.format(ts)).append(": ").append(type).append('\n');
        log.flush();
    }

    public void afterSpOpen(final Instant ts, String type, IOException e) {
        readStartTS = null;
        writeStartTS = null;
        log.append("SP ").append(ACION_RETURN).append(" open @").append(dateTimeFormatter.format(ts)).append(": ").append(type).append(' ').append(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    public void beforeSpClose(final Instant ts) {
        log.append("SP ").append(ACION_CALL).append(" close @").append(dateTimeFormatter.format(ts)).append('\n');
        log.flush();
    }

    public void afterSpClose(final Instant ts) {
        log.append("SP ").append(ACION_RETURN).append(" close @").append(dateTimeFormatter.format(ts)).append('\n');
        log.flush();
    }

    public void afterSpClose(final Instant ts, IOException e) {
        log.append("SP ").append(ACION_RETURN).append(" close @").append(dateTimeFormatter.format(ts)).append(": ").append(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeOsClose(Instant ts) {
        log.append("OS ").append(ACION_CALL).append(" close @").append(dateTimeFormatter.format(ts)).append('\n');
        log.flush();
    }

    void afterOsClose(Instant ts) {
        log.append("OS ").append(ACION_RETURN).append(" close @").append(dateTimeFormatter.format(ts)).append('\n');
        log.flush();
    }

    void afterOsClose(Instant ts, IOException e) {
        log.append("OS ").append(ACION_RETURN).append(" close @").append(dateTimeFormatter.format(ts)).append(": ").append(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeIsClose(Instant ts) {
        log.append("IS ").append(ACION_CALL).append(" close @").append(dateTimeFormatter.format(ts)).append('\n');
        log.flush();
    }

    void afterIsClose(Instant ts) {
        log.append("IS ").append(ACION_RETURN).append(" close @").append(dateTimeFormatter.format(ts)).append('\n');
        log.flush();
    }

    void afterIsClose(Instant ts, IOException e) {
        log.append("IS ").append(ACION_RETURN).append(" close @").append(dateTimeFormatter.format(ts)).append(": ").append(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

    void beforeAvailable(Instant ts) {
        log.append("IS ").append(ACION_CALL).append(" available @").append(dateTimeFormatter.format(ts)).append('\n');
        log.flush();
    }

    void afterAvailable(Instant ts, int available) {
        log.append("IS ").append(ACION_RETURN).append(" available @").append(dateTimeFormatter.format(ts)).append(": ").print(available);
        log.append('\n');
        log.flush();
    }

    void afterAvailable(Instant ts, IOException e) {
        log.append("IS ").append(ACION_RETURN).append(" available @").append(dateTimeFormatter.format(ts)).append(": ").append(e.toString());
        e.printStackTrace(log);
        log.flush();
    }

}
