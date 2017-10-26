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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.StopBits;
import java.time.Instant;
import org.osgi.annotation.versioning.ProviderType;

/**
 *
 * @author aploese
 */
@ProviderType
public class LoggingSerialPortSocket implements SerialPortSocket {

    @Override
    public int getInterByteReadTimeout() throws IOException {
        logWriter.beforeGetInterByteReadTimeout(Instant.now());
        try {
            final int result = serialPortSocket.getOverallReadTimeout();
            logWriter.afterGetInterByteReadTimeout(Instant.now(), result);
            return result;
        } catch (IOException e) {
            logWriter.afterGetInterByteReadTimeout(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public int getOverallWriteTimeout() throws IOException {
        logWriter.beforeGetOverallWriteTimeout(Instant.now());
        try {
            final int result = serialPortSocket.getOverallWriteTimeout();
            logWriter.afterGetOverallWriteTimeout(Instant.now(), result);
            return result;
        } catch (IOException e) {
            logWriter.afterGetOverallWriteTimeout(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public int getOverallReadTimeout() throws IOException {
        logWriter.beforeGetOverallReadTimeout(Instant.now());
        try {
            final int result = serialPortSocket.getOverallReadTimeout();
            logWriter.afterGetOverallReadTimeout(Instant.now(), result);
            return result;
        } catch (IOException e) {
            logWriter.afterGetOverallReadTimeout(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public void setTimeouts(int interByteReadTimeout, int overallReadTimeout, int overallWriteTimeout) throws IOException {
        logWriter.beforeSetTimeouts(Instant.now(), interByteReadTimeout, overallReadTimeout, overallWriteTimeout);
        try {
            serialPortSocket.setTimeouts(interByteReadTimeout, overallReadTimeout, overallWriteTimeout);
            logWriter.afterSetTimeouts(Instant.now());
        } catch (IOException e) {
            logWriter.afterSetTimeouts(Instant.now(), e);
            throw e;
        }
    }

    private class LOS extends OutputStream {

        private final OutputStream os;

        LOS(OutputStream os) {
            this.os = os;
        }

        @Override
        public void write(byte b[]) throws IOException {
            logWriter.beforeWrite(Instant.now(), b);
            try {
                os.write(b);
                logWriter.afterWrite(Instant.now());
            } catch (IOException e) {
                logWriter.afterWrite(Instant.now(), e);
                throw e;
            }
        }

        @Override
        public void write(byte b[], int off, int len) throws IOException {
            logWriter.beforeWrite(Instant.now(), b, off, len);
            try {
                os.write(b, off, len);
                logWriter.afterWrite(Instant.now());
            } catch (IOException e) {
                logWriter.afterWrite(Instant.now(), e);
                throw e;
            }
        }

        @Override
        public void write(int b) throws IOException {
            logWriter.beforeWrite(Instant.now(), (byte) b);
            try {
                os.write(b);
                logWriter.afterWrite(Instant.now());
            } catch (IOException e) {
                logWriter.afterWrite(Instant.now(), e);
                throw e;
            }
        }

        @Override
        public void flush() throws IOException {
            logWriter.beforeFlush(Instant.now());
            try {
                os.flush();
                logWriter.afterFlush(Instant.now());
            } catch (IOException e) {
                logWriter.afterFlush(Instant.now(), e);
                throw e;
            }
        }

        @Override
        public void close() throws IOException {
            logWriter.beforeOsClose(Instant.now());
            try {
                os.close();
                logWriter.afterOsClose(Instant.now());
            } catch (IOException e) {
                logWriter.afterOsClose(Instant.now(), e);
                throw e;
            }
        }

    }

    private class LIS extends InputStream {

        private final InputStream is;

        LIS(InputStream is) {
            this.is = is;
        }

        @Override
        public int read() throws IOException {
            logWriter.beforeRead(Instant.now());
            try {
                final int result = is.read();
                logWriter.afterRead(Instant.now(), result);
                return result;
            } catch (IOException e) {
                logWriter.afterRead(Instant.now(), e);
                throw e;
            }
        }

        @Override
        public int read(byte b[]) throws IOException {
            logWriter.beforeRead(Instant.now());
            try {
                final int result = is.read(b);
                logWriter.afterRead(Instant.now(), result, b, 0);
                return result;
            } catch (IOException e) {
                logWriter.afterRead(Instant.now(), e);
                throw e;
            }
        }

        @Override
        public int read(byte b[], int off, int len) throws IOException {
            logWriter.beforeRead(Instant.now());
            try {
                final int result = is.read(b, off, len);
                logWriter.afterRead(Instant.now(), result, b, off);
                return result;
            } catch (IOException e) {
                logWriter.afterRead(Instant.now(), e);
                throw e;
            }

        }

        @Override
        public int available() throws IOException {
            logWriter.beforeAvailable(Instant.now());
            try {
                final int result = is.available();
                logWriter.afterAvailable(Instant.now(), result);
                return result;
            } catch (IOException e) {
                logWriter.afterAvailable(Instant.now(), e);
                throw e;
            }
        }

        @Override
        public void close() throws IOException {
            logWriter.beforeIsClose(Instant.now());
            try {
                is.close();
                logWriter.afterIsClose(Instant.now());
            } catch (IOException e) {
                logWriter.afterIsClose(Instant.now(), e);
                throw e;
            }
        }

    }

    final private SerialPortSocket serialPortSocket;
    private LOS los;
    private LIS lis;
    private final LogWriter logWriter;

    private LoggingSerialPortSocket(SerialPortSocket serialPortSocket, OutputStream logOs, boolean ascii, boolean verbose, TimeStampLogging timeStampLogging) throws FileNotFoundException {
        this.serialPortSocket = serialPortSocket;
        this.logWriter = new LogWriter(logOs, ascii, timeStampLogging, verbose);
        
    }
    
    public static LoggingSerialPortSocket wrapWithAsciiOutputStream(SerialPortSocket serialPortSocket, OutputStream logOs, boolean verbose, TimeStampLogging timeStampLogging) throws FileNotFoundException  {
    	return new LoggingSerialPortSocket(serialPortSocket, logOs, true, verbose, timeStampLogging);
    }

    public static LoggingSerialPortSocket wrapWithHexOutputStream(SerialPortSocket serialPortSocket, OutputStream logOs, boolean verbose, TimeStampLogging timeStampLogging) throws FileNotFoundException  {
    	return new LoggingSerialPortSocket(serialPortSocket, logOs, false, verbose, timeStampLogging);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (logWriter != null) {
                logWriter.close();
            }
        } finally {
            super.finalize();
        }
    }

    @Override
    public boolean isClosed() {
        return serialPortSocket.isClosed();
    }

    @Override
    public boolean isCTS() throws IOException {
        logWriter.beforeIsCTS(Instant.now());
        try {
            final boolean result = serialPortSocket.isCTS();
            logWriter.afterIsCTS(Instant.now(), result);
            return result;
        } catch (IOException e) {
            logWriter.afterIsCTS(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public boolean isDSR() throws IOException {
        logWriter.beforeIsDSR(Instant.now());
        try {
            final boolean result = serialPortSocket.isDSR();
            logWriter.afterIsDSR(Instant.now(), result);
            return result;
        } catch (IOException e) {
            logWriter.afterIsDSR(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public boolean isIncommingRI() throws IOException {
        logWriter.beforeIsIncommingRI(Instant.now());
        try {
            final boolean result = serialPortSocket.isIncommingRI();
            logWriter.afterIsIncommingRI(Instant.now(), result);
            return result;
        } catch (IOException e) {
            logWriter.afterIsIncommingRI(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        final InputStream ois = serialPortSocket.getInputStream();
        if (ois == null) {
            lis = null;
        } else if (lis == null) {
            lis = new LIS(ois);
        } else if (lis.is != ois) {
            lis = new LIS(ois);
        }
        return lis;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        final OutputStream oos = serialPortSocket.getOutputStream();
        if (oos == null) {
            los = null;
        } else if (los == null) {
            los = new LOS(oos);
        } else if (los.os != oos) {
            los = new LOS(oos);
        }
        return los;
    }

    @Override
    public String getPortName() {
        return serialPortSocket.getPortName();
    }

    @Override
    public boolean isOpen() {
        return serialPortSocket.isOpen();
    }

    @Override
    public void openAsIs() throws IOException {
        logWriter.beforeSpOpen(Instant.now(), serialPortSocket.getPortName(), "AsIs");
        try {
            serialPortSocket.openAsIs();
            logWriter.afterSpOpen(Instant.now(), "AsIs");
        } catch (IOException e) {
            logWriter.afterSpOpen(Instant.now(), "AsIs", e);
            throw e;
        }
    }

    @Override
    public void openRaw() throws IOException {
        logWriter.beforeSpOpen(Instant.now(), serialPortSocket.getPortName(), "Raw");
        try {
            serialPortSocket.openRaw();
            logWriter.afterSpOpen(Instant.now(), "Raw");
        } catch (IOException e) {
            logWriter.afterSpOpen(Instant.now(), "Raw", e);
            throw e;
        }
    }

    @Override
    public void openTerminal() throws IOException {
        logWriter.beforeSpOpen(Instant.now(), serialPortSocket.getPortName(), "Terminal");
        try {
            serialPortSocket.openTerminal();
            logWriter.afterSpOpen(Instant.now(), "Terminal");
        } catch (IOException e) {
            logWriter.afterSpOpen(Instant.now(), "Terminal", e);
            throw e;
        }
    }

    @Override
    public void openModem() throws IOException {
        logWriter.beforeSpOpen(Instant.now(), serialPortSocket.getPortName(), "Modem");
        try {
            serialPortSocket.openModem();
            logWriter.afterSpOpen(Instant.now(), "Modem");
        } catch (IOException e) {
            logWriter.afterSpOpen(Instant.now(), "Modem", e);
            throw e;
        }
    }

    @Override
    public void openRaw(Baudrate baudRate, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls) throws IOException {
        logWriter.beforeSpOpen(Instant.now(), serialPortSocket.getPortName(), "Raw(dataBits=" + dataBits + ", stopBits=" + stopBits + ", partity=" + parity + ", flowControl=" + flowControls + ")");
        try {
            serialPortSocket.openRaw(baudRate, dataBits, stopBits, parity, flowControls);
            logWriter.afterSpOpen(Instant.now(), "Raw()");
        } catch (IOException e) {
            logWriter.afterSpOpen(Instant.now(), "Raw()", e);
            throw e;
        }
    }

    @Override
    public void close() throws IOException {
        los = null;
        lis = null;
        logWriter.beforeSpClose(Instant.now());
        try {
            serialPortSocket.close();
            logWriter.afterSpClose(Instant.now());
        } catch (IOException e) {
            logWriter.afterSpClose(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public void setRTS(boolean value) throws IOException {
        logWriter.beforeSetRTS(Instant.now(), value);
        try {
            serialPortSocket.setRTS(value);
            logWriter.afterSetRTS(Instant.now());
        } catch (IOException e) {
            logWriter.afterSetRTS(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public void setDTR(boolean value) throws IOException {
        logWriter.beforeSetDTR(Instant.now(), value);
        try {
            serialPortSocket.setDTR(value);
            logWriter.afterSetDTR(Instant.now());
        } catch (IOException e) {
            logWriter.afterSetDTR(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public void setXONChar(char c) throws IOException {
        logWriter.beforeSetXONChar(Instant.now(), c);
        try {
            serialPortSocket.setXONChar(c);
            logWriter.afterSetXONChar(Instant.now());
        } catch (IOException e) {
            logWriter.afterSetXONChar(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public void setXOFFChar(char c) throws IOException {
        logWriter.beforeSetXOFFChar(Instant.now(), c);
        try {
            serialPortSocket.setXOFFChar(c);
            logWriter.afterSetXOFFChar(Instant.now());
        } catch (IOException e) {
            logWriter.afterSetXOFFChar(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public char getXONChar() throws IOException {
        logWriter.beforeGetXONChar(Instant.now());
        try {
            final char result = serialPortSocket.getXONChar();
            logWriter.afterGetXONChar(Instant.now(), result);
            return result;
        } catch (IOException e) {
            logWriter.afterGetXONChar(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public char getXOFFChar() throws IOException {
        logWriter.beforeGetXOFFChar(Instant.now());
        try {
            final char result = serialPortSocket.getXOFFChar();
            logWriter.afterGetXOFFChar(Instant.now(), result);
            return result;
        } catch (IOException e) {
            logWriter.afterGetXOFFChar(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public void sendXON() throws IOException {
        logWriter.beforeSendXON(Instant.now());
        try {
            serialPortSocket.sendXON();
            logWriter.afterSendXON(Instant.now());
        } catch (IOException e) {
            logWriter.afterSendXON(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public void sendXOFF() throws IOException {
        logWriter.beforeSendXOFF(Instant.now());
        try {
            serialPortSocket.sendXOFF();
            logWriter.afterSendXOFF(Instant.now());
        } catch (IOException e) {
            logWriter.afterSendXOFF(Instant.now(), e);
            throw e;
        }
        serialPortSocket.sendXOFF();
    }

    @Override
    public int getInBufferBytesCount() throws IOException {
        logWriter.beforeGetInBufferBytesCount(Instant.now());
        try {
            final int result = serialPortSocket.getInBufferBytesCount();
            logWriter.afterGetInBufferBytesCount(Instant.now(), result);
            return result;
        } catch (IOException e) {
            logWriter.afterGetInBufferBytesCount(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public int getOutBufferBytesCount() throws IOException {
        logWriter.beforeGetOutBufferBytesCount(Instant.now());
        try {
            final int result = serialPortSocket.getOutBufferBytesCount();
            logWriter.afterGetOutBufferBytesCount(Instant.now(), result);
            return result;
        } catch (IOException e) {
            logWriter.afterGetOutBufferBytesCount(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public void sendBreak(int duration) throws IOException {
        logWriter.beforeSendBreak(Instant.now(), duration);
        try {
            serialPortSocket.sendBreak(duration);
            logWriter.afterSendBreak(Instant.now());
        } catch (IOException e) {
            logWriter.afterSendBreak(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public void setBreak(boolean value) throws IOException {
        logWriter.beforeSetBreak(Instant.now(), value);
        try {
            serialPortSocket.setBreak(value);
            logWriter.afterSetBreak(Instant.now());
        } catch (IOException e) {
            logWriter.afterSetBreak(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public void setFlowControl(Set<FlowControl> flowControls) throws IOException {
        logWriter.beforeSetFlowControl(Instant.now(), flowControls);
        try {
            serialPortSocket.setFlowControl(flowControls);
            logWriter.afterSetFlowControl(Instant.now());
        } catch (IOException e) {
            logWriter.afterSetFlowControl(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public void setBaudrate(Baudrate baudrate) throws IOException {
        logWriter.beforeSetBaudrate(Instant.now(), baudrate);
        try {
            serialPortSocket.setBaudrate(baudrate);
            logWriter.afterSetBaudrate(Instant.now());
        } catch (IOException e) {
            logWriter.afterSetBaudrate(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public void setDataBits(DataBits dataBits) throws IOException {
        logWriter.beforeSetDataBits(Instant.now(), dataBits);
        try {
            serialPortSocket.setDataBits(dataBits);
            logWriter.afterSetDataBits(Instant.now());
        } catch (IOException e) {
            logWriter.afterSetDataBits(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public void setStopBits(StopBits stopBits) throws IOException {
        logWriter.beforeSetStopBits(Instant.now(), stopBits);
        try {
            serialPortSocket.setStopBits(stopBits);
            logWriter.afterSetStopBits(Instant.now());
        } catch (IOException e) {
            logWriter.afterSetStopBits(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public void setParity(Parity parity) throws IOException {
        logWriter.beforeSetParity(Instant.now(), parity);
        try {
            serialPortSocket.setParity(parity);
            logWriter.afterSetParity(Instant.now());
        } catch (IOException e) {
            logWriter.afterSetParity(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public Baudrate getBaudrate() throws IOException {
        logWriter.beforeGetBaudrate(Instant.now());
        try {
            final Baudrate result = serialPortSocket.getBaudrate();
            logWriter.afterGetBaudrate(Instant.now(), result);
            return result;
        } catch (IOException e) {
            logWriter.afterGetBaudrate(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public DataBits getDatatBits() throws IOException {
        logWriter.beforeGetDatatBits(Instant.now());
        try {
            final DataBits result = serialPortSocket.getDatatBits();
            logWriter.afterGetDatatBits(Instant.now(), result);
            return result;
        } catch (IOException e) {
            logWriter.afterGetDatatBits(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public StopBits getStopBits() throws IOException {
        logWriter.beforeGetStopBits(Instant.now());
        try {
            final StopBits result = serialPortSocket.getStopBits();
            logWriter.afterGetStopBits(Instant.now(), result);
            return result;
        } catch (IOException e) {
            logWriter.afterGetStopBits(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public Parity getParity() throws IOException {
        logWriter.beforeGetParity(Instant.now());
        try {
            final Parity result = serialPortSocket.getParity();
            logWriter.afterGetParity(Instant.now(), result);
            return result;
        } catch (IOException e) {
            logWriter.afterGetParity(Instant.now(), e);
            throw e;
        }
    }

    @Override
    public Set<FlowControl> getFlowControl() throws IOException {
        logWriter.beforeGetFlowControl(Instant.now());
        try {
            final Set<FlowControl> result = serialPortSocket.getFlowControl();
            logWriter.afterGetFlowControl(Instant.now(), result);
            return result;
        } catch (IOException e) {
            logWriter.afterGetFlowControl(Instant.now(), e);
            throw e;
        }
    }

}
