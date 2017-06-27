
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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import org.osgi.annotation.versioning.ProviderType;

/**
 *
 * @author aploese
 */
@ProviderType
public class LogFileCompressor extends LogReader {

    private final LogWriter logWriter;
    private byte[] readBuffer;
    private long readTs;
    private long readStartTs;

    public static void compress(InputStream is, OutputStream os) throws IOException {
        new LogFileCompressor(is, os);
    }
    
    private LogFileCompressor(InputStream is, OutputStream os) throws IOException {
        super(is);
        logWriter = new LogWriter(os);
        read();
        logWriter.close();
    }

    @Override
    protected void logOpend(long ts, String mode) {
        logWriter.logOpend(ts, mode);
    }

    @Override
    protected void logReadStart(long ts) {
        if (readBuffer == null) {
            logWriter.logReadStart(ts);
        }
        readStartTs = ts;
    }

    @Override
    protected void logReadEnd(long ts, byte[] b) {
        logWriter.logReadEnd(ts, b.length, b, 0);
    }

    @Override
    protected void logReadEnd(long ts, int i) {
        if (i < 0) {
            // handle close
            flushReadBuffer();
            logWriter.logReadEnd(ts, i);
        } else {
            pushToReadBuffer(i, ts);
        }
    }

    @Override
    protected void logWriteStart(long ts) {
        flushReadBuffer();
        logWriter.logWriteStart(ts);
    }

    @Override
    protected void logWriteEnd(long ts, byte[] b) {
        flushReadBuffer();
        logWriter.logWriteEnd(ts, b, 0, b.length);
    }

    @Override
    protected void logWriteEnd(long ts, int i) {
        flushReadBuffer();
        logWriter.logWriteEnd(ts, i);
    }

    @Override
    protected void logFlushed(long ts) {
        logWriter.logFlushed(ts);
    }

    @Override
    protected void logClosed(long ts) {
        flushReadBuffer();
        logWriter.logClosed(ts);
    }

    private void pushToReadBuffer(int i, long ts) {
        if (readBuffer == null) {
            readBuffer = new byte[]{(byte) i};
        } else {
            readBuffer = Arrays.copyOf(readBuffer, readBuffer.length + 1);
            readBuffer[readBuffer.length - 1] = (byte) i;
        }
        readTs = ts;
    }

    private void flushReadBuffer() {
        if (readBuffer != null) {
            if (readBuffer.length == 1) {
                logWriter.logReadEnd(readTs, readBuffer[0] & 0xFF);
            } else {
                logWriter.logReadEnd(readTs, readBuffer.length, readBuffer, 0);
            }
            readBuffer = null;
            if (readTs < readStartTs) {
                // port is currently open
                logReadStart(readStartTs);
            }
            readTs = -1;
        }
    }

}
