/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.atmodem4j.spsw.logging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 *
 * @author aploese
 */
public abstract class LogReader {

    private final InputStream is;
    private final BufferedReader br;
    private final InputStreamReader isr;
    private final SimpleDateFormat dateFormat;
    private String currentLine;
    private int posInLine;
    
    
    public LogReader(InputStream is) {
        this.is = is;
        isr = new InputStreamReader(is);
        br = new BufferedReader(isr);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    }

    protected void read() throws IOException {
        while (true) {
            
            posInLine = 0;
            currentLine = br.readLine();
            if (currentLine == null) {
                break;
            }
            long ts;
            switch (readLineStart()) {
                case "OP":
                    ts = readTimeStamp();
                    String mode = currentLine.substring(24);
                    logOpend(ts, mode);
                    break;
                case "RS":
                    ts = readTimeStamp();
                    logReadStart(ts);
                    break;
                case "RX":
                    ts = readTimeStamp();
                    readDuration();
                    if (peekDataArray()) {
                        logReadEnd(ts, readArray());
                    } else {
                        logReadEnd(ts, readSingle());
                    }
                    break;
                case "TS":
                    ts = readTimeStamp();
                    logWriteStart(ts);
                    break;
                case "TX":
                    ts = readTimeStamp();
                    readDuration();
                    if (peekDataArray()) {
                        logWriteEnd(ts, readArray());
                    } else {
                        logWriteEnd(ts, readSingle());
                    }
                    break;
                case "FL":
                    ts = readTimeStamp();
                    logFlushed(ts);
                    break;
                case "CL":
                    ts = readTimeStamp();
                    logClosed(ts);
                    break;
                default:
                    throw new RuntimeException("Cant read log");

            }
        }
    }

    private String readLineStart() {
        final String result = currentLine.substring(posInLine, posInLine + 2);
        posInLine += result.length();
        return result;
    }
    
    private long readTimeStamp() {
        try {
            final int start = currentLine.indexOf("@", posInLine);
            final long result = dateFormat.parse(currentLine.substring(start + 1, start + 24)).getTime();
            posInLine += 24;
            return result;
        } catch (ParseException pe) {
            throw new RuntimeException(pe);
        }
    }

    private long readDuration() {
            final int start = currentLine.indexOf("(", posInLine);
            final int end = currentLine.indexOf(")", start);
            final long result = Long.parseLong(currentLine.substring(start + 1, end).trim());
            posInLine = end;
            return result;
    }
    
    private int readSingle() {
        final String str = currentLine.substring(posInLine + 1);
        posInLine = currentLine.length();
        return Integer.parseInt(str.trim(), 16);
    }

    private byte[] readArray() {
        final int start = currentLine.indexOf("[", posInLine) +1;
        final int end = currentLine.indexOf("]", start);
        final byte[] result = new byte[(end - start) / 3];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte)Integer.parseInt(currentLine.substring(start + i * 3 +1, start + i * 3 + 3), 16);
        }
        posInLine = end;
        return result;
    }
    
    private boolean peekDataArray() {
        return currentLine.indexOf("[", posInLine) > 0;
    }

    protected abstract void logOpend(long ts, String mode);

    protected abstract void logReadStart(long ts);

    protected abstract void logReadEnd(long ts, byte[] b);

    protected abstract void logReadEnd(long ts, int i);

    protected abstract void logWriteStart(long ts);

    protected abstract void logWriteEnd(long ts, byte[] b);

    protected abstract void logWriteEnd(long ts, int i);

    protected abstract void logFlushed(long ts);

    protected abstract void logClosed(long ts);

}
