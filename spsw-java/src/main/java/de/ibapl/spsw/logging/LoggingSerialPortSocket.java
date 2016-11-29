/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ibapl.spsw.logging;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.Set;
import de.ibapl.spsw.Baudrate;
import de.ibapl.spsw.DataBits;
import de.ibapl.spsw.FlowControl;
import de.ibapl.spsw.Parity;
import de.ibapl.spsw.SerialPortSocket;
import de.ibapl.spsw.StopBits;

/**
 *
 * @author aploese
 */
public class LoggingSerialPortSocket implements SerialPortSocket {

    private class LOS extends OutputStream {

        private final OutputStream os;

        LOS(OutputStream os) {
            this.os = os;
        }

        @Override
        public void write(byte b[]) throws IOException {
            logWriter.logWriteStart();
            os.write(b);
            logWriter.logWriteEnd(b, 0, b.length);
        }

        @Override
        public void write(byte b[], int off, int len) throws IOException {
            logWriter.logWriteStart();
            os.write(b, off, len);
            logWriter.logWriteEnd(b, off, len);
        }

        @Override
        public void write(int b) throws IOException {
            logWriter.logWriteStart();
            os.write(b);
            logWriter.logWriteEnd(b);
        }

        @Override
        public void flush() throws IOException {
            os.flush();
            logWriter.logFlushed();
        }

        @Override
        public void close() throws IOException {
            os.close();
            logWriter.logClosed();
        }

    }

    private class LIS extends InputStream {

        private final InputStream is;

        LIS(InputStream is) {
            this.is = is;
        }

        @Override
        public int read() throws IOException {
            logWriter.logReadStart();
            final int result = is.read();
            logWriter.logReadEnd(result);
            return result;
        }

        @Override
        public int read(byte b[]) throws IOException {
            logWriter.logReadStart();
            final int result = is.read(b);
            logWriter.logReadEnd(result, b, 0);
            return result;
        }

        @Override
        public int read(byte b[], int off, int len) throws IOException {
            logWriter.logReadStart();
            final int result = is.read(b, off, len);
            logWriter.logReadEnd(result, b, off);
            return result;
        }

        @Override
        public int available() throws IOException {
            return is.available();
        }

    }

    final private SerialPortSocket serialPortSocket;
    private LOS los;
    private LIS lis;
    private final LogWriter logWriter; 

    public LoggingSerialPortSocket(SerialPortSocket serialPortSocket, OutputStream logOS) throws FileNotFoundException {
        this.serialPortSocket = serialPortSocket;
        this.logWriter =  new LogWriter(logOS);
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
    public boolean isCTS() {
        return serialPortSocket.isCTS();
    }

    @Override
    public boolean isDSR() {
        return serialPortSocket.isDSR();
    }

    @Override
    public boolean isIncommingRI() {
        return serialPortSocket.isIncommingRI();
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
        serialPortSocket.openAsIs();
        logWriter.logOpend("AsIs");
    }

    @Override
    public void openRaw() throws IOException {
        serialPortSocket.openRaw();
        logWriter.logOpend("Raw");
    }

    @Override
    public void openTerminal() throws IOException {
        serialPortSocket.openTerminal();
        logWriter.logOpend("Terminal");
    }

    @Override
    public void openModem() throws IOException {
        serialPortSocket.openModem();
        logWriter.logOpend("Modem");
    }

    @Override
    public void openRaw(Baudrate baudRate, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls) throws IOException {
        serialPortSocket.openRaw(baudRate, dataBits, stopBits, parity, flowControls);
        logWriter.logOpend("Raw()");
    }

    @Override
    public void close() throws IOException {
        los = null;
        lis = null;
        serialPortSocket.close();
        logWriter.logClosed();
    }

    @Override
    public void setRTS(boolean value) throws IOException {
        serialPortSocket.setRTS(value);
    }

    @Override
    public void setDTR(boolean value) throws IOException {
        serialPortSocket.setDTR(value);
    }

    @Override
    public void setXONChar(char c) throws IOException {
        serialPortSocket.setXONChar(c);
    }

    @Override
    public void setXOFFChar(char c) throws IOException {
        serialPortSocket.setXOFFChar(c);
    }

    @Override
    public char getXONChar() throws IOException {
        return serialPortSocket.getXONChar();
    }

    @Override
    public char getXOFFChar() throws IOException {
        return serialPortSocket.getXOFFChar();
    }

    @Override
    public void sendXON() throws IOException {
        serialPortSocket.sendXON();
    }

    @Override
    public void sendXOFF() throws IOException {
        serialPortSocket.sendXOFF();
    }

    @Override
    public int getInBufferBytesCount() throws IOException {
        return serialPortSocket.getInBufferBytesCount();
    }

    @Override
    public int getOutBufferBytesCount() throws IOException {
        return serialPortSocket.getOutBufferBytesCount();
    }

    @Override
    public void setBreak(boolean value) throws IOException {
        serialPortSocket.setBreak(value);
    }

    @Override
    public void setFlowControl(Set<FlowControl> flowControls) throws IOException {
        serialPortSocket.setFlowControl(flowControls);
    }

    @Override
    public void setBaudrate(Baudrate baudrate) throws IOException {
        serialPortSocket.setBaudrate(baudrate);
    }

    @Override
    public void setDataBits(DataBits dataBits) throws IOException {
        serialPortSocket.setDataBits(dataBits);
    }

    @Override
    public void setStopBits(StopBits stopBits) throws IOException {
        serialPortSocket.setStopBits(stopBits);
    }

    @Override
    public void setParity(Parity parity) throws IOException {
        serialPortSocket.setParity(parity);
    }

    @Override
    public Baudrate getBaudrate() throws IOException {
        return serialPortSocket.getBaudrate();
    }

    @Override
    public DataBits getDatatBits() throws IOException {
        return serialPortSocket.getDatatBits();
    }

    @Override
    public StopBits getStopBits() throws IOException {
        return serialPortSocket.getStopBits();
    }

    @Override
    public Parity getParity() throws IOException {
        return serialPortSocket.getParity();
    }

    @Override
    public Set<FlowControl> getFlowControl() throws IOException {
        return serialPortSocket.getFlowControl();
    }

}
