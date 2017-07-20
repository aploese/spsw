
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

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
import org.osgi.annotation.versioning.ProviderType;

/**
 *
 * @author aploese
 */
@ProviderType
public class LogWriter {

    private final PrintStream log;
    private long readStartTS;
    private long writeStartTS;
    private final boolean ascii;

    public LogWriter(OutputStream logOS, boolean ascii) {
        this.log = new PrintStream(logOS);
        this.ascii = ascii;
    }

    void close() {
        log.close();
    }

    void logWriteStart() {
        logWriteStart(System.currentTimeMillis());
    }

    void logWriteStart(long ts) {
        writeStartTS = ts;
        log.format("TS @%1$tF %1$tT.%1$tL\n", new Date(writeStartTS));
        log.flush();
    }

    void logWriteEnd(int b) {
        logWriteEnd(System.currentTimeMillis(), b);
    }

    void logWriteEnd(final long writeEndTS, int b) {
        if (ascii) {
            log.format("TX @%1$tF %1$tT.%1$tL (%2$6d) %3$02x\n", new Date(writeEndTS), writeEndTS - writeStartTS, (byte) b);
        } else {
            log.format("TX @%1$tF %1$tT.%1$tL (%2$6d) %3$s\n", new Date(writeEndTS), writeEndTS - writeStartTS, (char) b);
        }
        log.flush();
        writeStartTS = -1;
    }

    void logWriteEnd(byte[] b, int off, int len) {
        logWriteEnd(System.currentTimeMillis(), b, off, len);
    }

    void logWriteEnd(final long writeEndTS, byte[] b, int off, int len) {
        log.format("TX @%1$tF %1$tT.%1$tL (%2$6d) [", new Date(writeEndTS), writeEndTS - writeStartTS);
        for (int i = off; i < len; i++) {
            if (ascii) {
                log.append((char)b[i]);
            } else {
                log.format(" %02x", b[i]);
            }
        }
        log.append("]\n");
        log.flush();
        writeStartTS = -1;
    }

    void logReadStart() {
        logReadStart(System.currentTimeMillis());
    }

    void logReadStart(long ts) {
        readStartTS = ts;
        log.format("RS @%1$tF %1$tT.%1$tL\n", new Date(readStartTS));
        log.flush();
    }

    void logReadEnd(int b) {
        logReadEnd(System.currentTimeMillis(), b);
    }

    void logReadEnd(final long readEndTS, int b) {
        if (b < 0) {
            log.format("RX @%1$tF %1$tT.%1$tL (%2$6d) %3$d\n", new Date(readEndTS), readEndTS - readStartTS, b);
        } else {
            if (ascii) {
                log.format("RX @%1$tF %1$tT.%1$tL (%2$6d) %3$s\n", new Date(readEndTS), readEndTS - readStartTS, (char) b);
            } else {
                log.format("RX @%1$tF %1$tT.%1$tL (%2$6d) %3$02x\n", new Date(readEndTS), readEndTS - readStartTS, (byte) b);
            }
        }
        log.flush();
        readStartTS = -1;
    }

    void logReadEnd(int readLength, byte[] b, int off) {
        logReadEnd(System.currentTimeMillis(), readLength, b, off);
    }

    void logReadEnd(final long readEndTS, int readLength, byte[] b, int off) {
        final int len = off + readLength;
        log.format("RX @%1$tF %1$tT.%1$tL (%2$6d) [", new Date(readEndTS), readEndTS - readStartTS);
        for (int i = off; i < len; i++) {
            if (ascii) {
                log.append((char)b[i]);
            } else {
            log.format(" %02x", b[i]);
            }
        }
        log.append("]\n");
        log.flush();
        readStartTS = -1;
    }

    void logFlushed() {
        logFlushed(System.currentTimeMillis());
    }

    void logFlushed(final long flushTS) {
        log.format("FL @%1$tF %1$tT.%1$tL\n", new Date(flushTS));
        log.flush();
    }

    void logOpend(String type) {
        logOpend(System.currentTimeMillis(), type);
    }

    void logOpend(final long ts, String type) {
        readStartTS = -1;
        writeStartTS = -1;
        log.format("OP @%1$tF %1$tT.%1$tL %2$s\n", new Date(ts), type);
        log.flush();
    }

    void logClosed() {
        logClosed(System.currentTimeMillis());
    }

    void logClosed(final long ts) {
        log.format("CL @%1$tF %1$tT.%1$tL\n", new Date(ts));
        log.flush();
    }

}
