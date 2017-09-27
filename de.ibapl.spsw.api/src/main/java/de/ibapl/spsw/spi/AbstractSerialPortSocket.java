package de.ibapl.spsw.spi;

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
import de.ibapl.spsw.api.SerialPortException;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.FlowControl;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Native;
import java.util.EnumSet;
import java.util.Set;

/**
 *
 * @author scream3r
 */
public abstract class AbstractSerialPortSocket implements SerialPortSocket {

    @Native
    static final int PORT_MODE_UNCHANGED = 0x0;
    @Native
    static final int PORT_MODE_RAW = 0x01;

    @Native
    static final int FLOW_CONTROL_NONE = 0x0;
    @Native
    static final int FLOW_CONTROL_RTS_CTS_IN = 0x01;
    @Native
    static final int FLOW_CONTROL_RTS_CTS_OUT = 0x02;
    @Native
    static final int FLOW_CONTROL_XON_XOFF_IN = 0x04;
    @Native
    static final int FLOW_CONTROL_XON_XOFF_OUT = 0x08;

    @Native
    static final int STOP_BITS_1 = 0x0;
    @Native
    static final int STOP_BITS_1_5 = 0x01;
    @Native
    static final int STOP_BITS_2 = 0x02;

    @Native
    static final int PARITY_NONE = 0x0;
    @Native
    static final int PARITY_ODD = 0x01;
    @Native
    static final int PARITY_EVEN = 0x02;
    @Native
    static final int PARITY_MARK = 0x03;
    @Native
    static final int PARITY_SPACE = 0x04;

    protected SerialInputStream is;
    protected SerialOutputStream os;

    private final String portName;
    private boolean open;

    protected AbstractSerialPortSocket(String portName) {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkRead(portName);
            security.checkWrite(portName);
        }
        if (portName == null) {
            throw new IllegalArgumentException("portname must not null!");
        }

        this.portName = portName;
    }

    @Override
    public boolean isClosed() {
        return !open;
    }

    @Override
    public synchronized void close() throws IOException {
        open = false;
        //Make streams closed, so that they can not be reopend.
        if (is != null) {
            is = null;
        }
        if (os != null) {
            os = null;
        }
        close0();
    }

    @Override
    public synchronized InputStream getInputStream() throws IOException {
        if (!open) {
            throw new SerialPortException(portName, SERIAL_PORT_CLOSED);
        }
        if (is == null) {
            is = new SerialInputStream();
        }
        return is;
    }

    @Override
    public synchronized OutputStream getOutputStream() throws IOException {
        if (!open) {
            throw new SerialPortException(portName, SERIAL_PORT_CLOSED);
        }
        if (os == null) {
            os = new SerialOutputStream();
        }
        return os;
    }

    @Override
    public String getPortName() {
        return portName;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public synchronized void openAsIs() throws IOException {
        open(portName, PORT_MODE_UNCHANGED);
        open = true;
    }

    @Override
    public synchronized void openRaw() throws IOException {
        open(portName, PORT_MODE_RAW);
        open = true;
    }

    @Override
    public synchronized void openTerminal() throws IOException {
        throw new UnsupportedOperationException("Terminal mode not yet supported");
    }

    @Override
    public synchronized void openModem() throws IOException {
        throw new UnsupportedOperationException("Modem mode not yet supported");
    }

    @Override
    public void openRaw(Baudrate baudRate, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls) throws IOException {
        openRaw();
        setBaudrate(baudRate);
        setDataBits(dataBits);
        setStopBits(stopBits);
        setParity(parity);
        setFlowControl(flowControls);
    }

    @Override
    public void setFlowControl(Set<FlowControl> flowControls) throws IOException {
        int nativeValue = 0;
        for (FlowControl fc : flowControls) {
            switch (fc) {
                case RTS_CTS_IN:
                    nativeValue |= FLOW_CONTROL_RTS_CTS_IN;
                    break;
                case RTS_CTS_OUT:
                    nativeValue |= FLOW_CONTROL_RTS_CTS_OUT;
                    break;
                case XON_XOFF_IN:
                    nativeValue |= FLOW_CONTROL_XON_XOFF_IN;
                    break;
                case XON_XOFF_OUT:
                    nativeValue |= FLOW_CONTROL_XON_XOFF_OUT;
                    break;
                default:
                    throw new RuntimeException("Cant handle Flowcontrol");
            }
        }
        setFlowControl(nativeValue);
    }

    @Override
    public Set<FlowControl> getFlowControl() throws IOException {
        final int flowControl = getFlowControl0();
        Set<FlowControl> s = EnumSet.noneOf(FlowControl.class);
        if ((flowControl & FLOW_CONTROL_RTS_CTS_IN) == FLOW_CONTROL_RTS_CTS_IN) {
            s.add(FlowControl.RTS_CTS_IN);
        }
        if ((flowControl & FLOW_CONTROL_RTS_CTS_OUT) == FLOW_CONTROL_RTS_CTS_OUT) {
            s.add(FlowControl.RTS_CTS_OUT);
        }
        if ((flowControl & FLOW_CONTROL_XON_XOFF_IN) == FLOW_CONTROL_XON_XOFF_IN) {
            s.add(FlowControl.XON_XOFF_IN);
        }
        if ((flowControl & FLOW_CONTROL_XON_XOFF_OUT) == FLOW_CONTROL_XON_XOFF_OUT) {
            s.add(FlowControl.XON_XOFF_OUT);
        }
        return s;
    }

    /**
     * Open port
     *
     * @param portName name of port for opening
     * @param type
     * @throws java.io.IOException
     *
     */
    protected abstract void open(String portName, int type) throws IOException;

    /**
     * Close port
     *
     * @throws java.io.IOException
     */
    protected abstract void close0() throws IOException;

    @Override
    @SuppressWarnings("FinalizeDeclaration")
    protected void finalize() throws Throwable {
        try {
            if (isOpen()) {
                close();
            }
        } catch (IOException e) {

        } finally {
            super.finalize();
        }
    }

    @Override
    public void setBaudrate(Baudrate baudrate) throws IOException {
        setBaudrate(baudrate.value);
    }

    @Override
    public void setDataBits(DataBits dataBits) throws IOException {
        setDataBits(dataBits.value);
    }

    @Override
    public void setStopBits(StopBits stopBits) throws IOException {
        try {
            switch (stopBits) {
                case SB_1:
                    setStopBits(STOP_BITS_1);
                    break;
                case SB_1_5:
                    setStopBits(STOP_BITS_1_5);
                    break;
                case SB_2:
                    setStopBits(STOP_BITS_2);
                    break;
                default:
                    throw new IllegalArgumentException("Cant handle Stopbits");
            }
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Can't set " + stopBits + " on port: " + getPortName(), ex);
        }
    }

    @Override
    public void setParity(Parity parity) throws IOException {
        switch (parity) {
            case EVEN:
                setParity(PARITY_EVEN);
                break;
            case MARK:
                setParity(PARITY_MARK);
                break;
            case NONE:
                setParity(PARITY_NONE);
                break;
            case ODD:
                setParity(PARITY_ODD);
                break;
            case SPACE:
                setParity(PARITY_SPACE);
                break;
            default:
                throw new RuntimeException("cant convert parity");
        }
    }

    @Override
    public Baudrate getBaudrate() throws IOException {
        return Baudrate.fromNative(getBaudrate0());
    }

    @Override
    public DataBits getDatatBits() throws IOException {
        return DataBits.fromNative(getDataBits0());
    }

    @Override
    public StopBits getStopBits() throws IOException {
        switch (getStopBits0()) {
            case STOP_BITS_1:
                return StopBits.SB_1;
            case STOP_BITS_1_5:
                return StopBits.SB_1_5;
            case STOP_BITS_2:
                return StopBits.SB_2;
            default:
                throw new RuntimeException("Cant handle native value: " + getStopBits0());
        }
    }

    @Override
    public Parity getParity() throws IOException {
        switch (getParity0()) {
            case PARITY_NONE:
                return Parity.NONE;
            case PARITY_ODD:
                return Parity.ODD;
            case PARITY_EVEN:
                return Parity.EVEN;
            case PARITY_MARK:
                return Parity.MARK;
            case PARITY_SPACE:
                return Parity.SPACE;
            default:
                throw new RuntimeException("Can't convert native value to parity: " + getParity0());
        }
    }

    protected abstract int readSingle() throws IOException;

    /**
     * Read data from port
     *
     * @param b the data to be written
     * @param off the start offset in the data
     * @param len the number of bytes that are written
     * @return the readed bytes
     * @exception IOException If an I/O error has occurred.
     */
    protected abstract int readBytes(byte[] b, int off, int len) throws IOException;

    protected abstract void writeSingle(int b) throws IOException;

    /**
     * Write data to port
     *
     * @param b
     * @param off the start offset in the data.
     * @param len the number of bytes to write.
     * @throws java.io.IOException
     *
     */
    protected abstract void writeBytes(byte[] b, int off, int len) throws IOException;

    protected abstract void setFlowControl(int mask) throws IOException;

    protected abstract int getFlowControl0() throws IOException;

    protected abstract void setBaudrate(int baudRate) throws IOException;

    protected abstract void setDataBits(int value) throws IOException;

    protected abstract void setStopBits(int value) throws IOException;

    protected abstract void setParity(int parity) throws IOException;

    protected abstract int getBaudrate0() throws IOException;

    protected abstract int getDataBits0() throws IOException;

    protected abstract int getStopBits0() throws IOException;

    protected abstract int getParity0() throws IOException;

    /**
     * writes all data in the output buffer
     *
     * @throws IOException
     */
    protected abstract void drainOutputBuffer() throws IOException;

    protected class SerialInputStream extends InputStream {

        @Override
        public void close() throws IOException {
            AbstractSerialPortSocket.this.close();
        }

        @Override
        public int read() throws IOException {
                return AbstractSerialPortSocket.this.readSingle();
        }

        @Override
        public int read(byte b[]) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            } else if (b.length == 0) {
                return 0;
            }

                return AbstractSerialPortSocket.this.readBytes(b, 0, b.length);
        }

        @Override
        public int read(byte b[], int off, int len) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            } else if (off < 0 || len < 0 || len > b.length - off) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return 0;
            }

                return AbstractSerialPortSocket.this.readBytes(b, off, len);
        }

        @Override
        public int available() throws IOException {
                return AbstractSerialPortSocket.this.getInBufferBytesCount();
        }

    }

    protected class SerialOutputStream extends OutputStream {

        @Override
        public void close() throws IOException {
            AbstractSerialPortSocket.this.close();
        }

        @Override
        public void write(int b) throws IOException {
                AbstractSerialPortSocket.this.writeSingle(b);
        }

        @Override
        public void write(byte b[]) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            } else if (b.length == 0) {
                return;
            }

                AbstractSerialPortSocket.this.writeBytes(b, 0, b.length);
        }

        @Override
        public void write(byte b[], int off, int len) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            } else if ((off < 0) || (off > b.length) || (len < 0)
                    || ((off + len) > b.length) || ((off + len) < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return;
            }

                AbstractSerialPortSocket.this.writeBytes(b, off, len);
        }

        @Override
        public void flush() throws IOException {
                AbstractSerialPortSocket.this.drainOutputBuffer();
        }

    }

    @Override
    public String toString() {
        try {
            return String.format("[portname=%s, baudrate= %s, dataBits= %s, stopBits= %s, parity= %s, flowControl= %s]", getPortName(), getBaudrate(), getDatatBits(), getStopBits(), getParity(), getFlowControl());
        } catch (IOException e) {
            return "Internal Error " + e;
        }
    }

}
