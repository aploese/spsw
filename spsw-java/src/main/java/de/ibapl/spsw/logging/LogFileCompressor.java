/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.atmodem4j.spsw.logging;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 *
 * @author aploese
 */
public class LogFileCompressor extends LogReader {

    private final LogWriter logWriter;
    private byte[] readBuffer;
    private long readTs;
    private long readStartTs;

    public LogFileCompressor(InputStream is, OutputStream os) throws IOException {
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
