/*
 * SPSW - Drivers for the serial port, https://github.com/aploese/spsw/
 * Copyright (C) 2009-2019, Arne Plöse and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
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
 */
package de.ibapl.spsw.jniprovider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Native;
import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.Set;

import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.spi.AbstractInterruptibleChannel;

/**
 * Base class for {@linkplain GenericTermiosSerialPortSocket} and
 * {@linkplain GenericWinSerialPortSocket} with common JNI bindings.
 *
 *
 * @author scream3r
 * @author Arne Plöse
 */
public abstract class AbstractSerialPortSocket<T extends AbstractSerialPortSocket<T>> extends AbstractInterruptibleChannel implements SerialPortSocket {

    private native int read_ArgsOK(ByteBuffer dst, int pos, int len) throws IOException;

    private native int readBytes(byte[] b) throws IOException;

    private native int write_ArgsOK(ByteBuffer src, int pos, int len) throws IOException;

    private native void writeBytes(byte[] b) throws IOException;

    /**
     * Read data from port
     *
     * @param b the data to be written
     * @param off the start offset in the data
     * @param len the number of bytes that are written
     * @return the readed bytes
     * @exception IOException If an I/O error has occurred.
     */
    private native int readBytes_ArgsOK(byte[] b, int off, int len) throws IOException;

    private native int readSingle() throws IOException;

    private native void sendBreak0(int duration) throws IOException;

    private native void setBreak0(boolean value) throws IOException;

    @Override
    public native char getXOFFChar() throws IOException;

    @Override
    public native char getXONChar() throws IOException;

    @Override
    public native boolean isCTS() throws IOException;

    @Override
    public native boolean isDSR() throws IOException;

    @Override
    public native boolean isDCD() throws IOException;

    @Override
    public native boolean isRI() throws IOException;

    /**
     * Returns the parameters as masked bit set.
     *
     * @return the parameters as masked bit set.
     *
     * @throws IOException
     */
    protected native int getParameters(int parameterBitSetMask) throws IOException;

    @Override
    public native int getInBufferBytesCount() throws IOException;

    @Override
    public native int getOutBufferBytesCount() throws IOException;

    @Override
    public native void setDTR(boolean value) throws IOException;

    /**
     * Set the parameters that are set in the parameterBitSet.
     *
     * @param parameterBitSet the parameters to set in a masked bit set.
     * @throws IOException
     */
    protected native void setParameters(int parameterBitSet) throws IOException;

    @Override
    public native void setXOFFChar(char c) throws IOException;

    @Override
    public native void setXONChar(char c) throws IOException;

    /**
     * Write data to port
     *
     * @param b
     * @param off the start offset in the data.
     * @param len the number of bytes to write.
     * @throws java.io.IOException
     *
     */
    protected native void writeBytes_ParamsOK(byte[] b, int off, int len) throws IOException;

    protected native void writeSingle(int b) throws IOException;

    @Override
    public int read(ByteBuffer dst) throws IOException {
        if (dst.isReadOnly()) {
            throw new IllegalArgumentException("Read-only buffer");
        }
            if (!dst.hasRemaining()) {
                //nothing to read
                return 0;
            }
        synchronized (readLock) {
            // Substitute a native buffer
            //make this blocking IO interruptable
            boolean completed = false;
            int result = 0;
            try {
                begin();
                result = read_ArgsOK(dst, dst.position(), dst.remaining());
                completed = true;
            } catch (IOException e) {
                completed = true;
                throw e;
            } finally {
                end(completed);
            }

            if (result > 0) {
                dst.position(dst.position() + result);
            }
            return result;
        }
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
            if (!src.hasRemaining()) {
                //nothing to write
                return 0;
            }
        synchronized (writeLock) {
            //make this blocking IO interruptable
            boolean completed = false;
            int result = 0;
            try {
                begin();
                result = write_ArgsOK(src, src.position(), src.remaining());
                completed = true;
            } catch (IOException e) {
                completed = true;
                throw e;
            } finally {
                end(completed);
            }
            // now update src
            src.position(src.position() + result);
            return result;
        }
    }

    protected class SerialInputStream extends InputStream {

        @Override
        public int available() throws IOException {
            return AbstractSerialPortSocket.this.getInBufferBytesCount();
        }

        @Override
        public void close() throws IOException {
            AbstractSerialPortSocket.this.close();
        }

        @Override
        public int read() throws IOException {
            synchronized (readLock) {
                //make this blocking IO interruptable
                boolean completed = false;
                try {
                    begin();
                    final int result = AbstractSerialPortSocket.this.readSingle();
                    completed = true;
                    return result;
                } catch (AsynchronousCloseException ace) {
                    completed = true;
                    return -1;
                } catch (IOException e) {
                    completed = true;
                    throw e;
                } finally {
                    end(completed);
                }
            }
        }

        @Override
        public int read(byte b[]) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            } else if (b.length == 0) {
                return 0;
            }

            synchronized (readLock) {
                //make this blocking IO interruptable
                boolean completed = false;
                try {
                    begin();
                    final int result = AbstractSerialPortSocket.this.readBytes(b);
                    completed = true;
                    return result;
                } catch (AsynchronousCloseException ace) {
                    completed = true;
                    return -1;
                } catch (IOException e) {
                    completed = true;
                    throw e;
                } finally {
                    end(completed);
                }
            }
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
            synchronized (readLock) {
                //make this blocking IO interruptable
                boolean completed = false;
                try {
                    begin();
                    final int result = AbstractSerialPortSocket.this.readBytes_ArgsOK(b, off, len);
                    completed = true;
                    return result;
                } catch (AsynchronousCloseException ace) {
                    completed = true;
                    return -1;
                } catch (IOException e) {
                    completed = true;
                    throw e;
                } finally {
                    end(completed);
                }
            }
        }
    }

    protected class SerialOutputStream extends OutputStream {

        @Override
        public void close() throws IOException {
            AbstractSerialPortSocket.this.close();
        }

        @Override
        public void flush() throws IOException {
            synchronized (writeLock) {
                //make this blocking IO interruptable
                boolean completed = false;
                try {
                    begin();
                    AbstractSerialPortSocket.this.drainOutputBuffer();
                    completed = true;
                } catch (IOException e) {
                    completed = true;
                    throw e;
                } finally {
                    end(completed);
                }
            }
        }

        @Override
        public void write(byte b[]) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            } else if (b.length == 0) {
                return;
            }

            synchronized (writeLock) {
                //make this blocking IO interruptable
                boolean completed = false;
                try {
                    begin();
                    AbstractSerialPortSocket.this.writeBytes(b);
                    completed = true;
                } catch (IOException e) {
                    completed = true;
                    throw e;
                } finally {
                    end(completed);
                }
            }
        }

        @Override
        public void write(byte b[], int off, int len) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            } else if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return;
            }

            synchronized (writeLock) {
                //make this blocking IO interruptable
                boolean completed = false;
                try {
                    begin();
                    AbstractSerialPortSocket.this.writeBytes_ParamsOK(b, off, len);
                    completed = true;
                } catch (IOException e) {
                    completed = true;
                    throw e;
                } finally {
                    end(completed);
                }
            }
        }

        @Override
        public void write(int b) throws IOException {
            synchronized (writeLock) {
                //make this blocking IO interruptable
                boolean completed = false;
                try {
                    begin();
                    AbstractSerialPortSocket.this.writeSingle(b);
                    completed = true;
                } catch (IOException e) {
                    completed = true;
                    throw e;
                } finally {
                    end(completed);
                }
            }
        }

    }

    // Layout of the 8 nibbles
    // parity,stopBits,flowControl,flowControl,dataBits,free,speed,speed
    @Native
    static final int SPEED_0_BPS = 0x00000001;
    @Native
    static final int SPEED_50_BPS = 0x00000002;
    @Native
    static final int SPEED_75_BPS = 0x00000003;
    @Native
    static final int SPEED_110_BPS = 0x00000004;
    @Native
    static final int SPEED_134_BPS = 0x00000005;
    @Native
    static final int SPEED_150_BPS = 0x00000006;
    @Native
    static final int SPEED_200_BPS = 0x00000007;
    @Native
    static final int SPEED_300_BPS = 0x00000008;
    @Native
    static final int SPEED_600_BPS = 0x00000009;
    @Native
    static final int SPEED_1200_BPS = 0x0000000A;
    @Native
    static final int SPEED_1800_BPS = 0x0000000B;
    @Native
    static final int SPEED_2400_BPS = 0x0000000C;
    @Native
    static final int SPEED_4800_BPS = 0x0000000D;
    @Native
    static final int SPEED_9600_BPS = 0x0000000E;
    @Native
    static final int SPEED_19200_BPS = 0x0000000F;
    @Native
    static final int SPEED_38400_BPS = 0x00000010;
    @Native
    static final int SPEED_57600_BPS = 0x00000011;
    @Native
    static final int SPEED_115200_BPS = 0x00000012;
    @Native
    static final int SPEED_230400_BPS = 0x00000013;
    @Native
    static final int SPEED_460800_BPS = 0x00000014;
    @Native
    static final int SPEED_500000_BPS = 0x00000015;
    @Native
    static final int SPEED_576000_BPS = 0x00000016;
    @Native
    static final int SPEED_921600_BPS = 0x00000017;
    @Native
    static final int SPEED_1000000_BPS = 0x00000018;
    @Native
    static final int SPEED_1152000_BPS = 0x00000019;
    @Native
    static final int SPEED_1500000_BPS = 0x0000001A;
    @Native
    static final int SPEED_2000000_BPS = 0x0000001B;
    @Native
    static final int SPEED_2500000_BPS = 0x0000001C;
    @Native
    static final int SPEED_3000000_BPS = 0x0000001D;
    @Native
    static final int SPEED_3500000_BPS = 0x0000001E;
    @Native
    static final int SPEED_4000000_BPS = 0x0000001F;
    @Native
    static final int SPEED_MASK = 0x000000FF;

    @Native
    static final int DATA_BITS_DB5 = 0x00001000;
    @Native
    static final int DATA_BITS_DB6 = 0x00002000;
    @Native
    static final int DATA_BITS_DB7 = 0x00003000;
    @Native
    static final int DATA_BITS_DB8 = 0x00004000;
    @Native
    static final int DATA_BITS_MASK = 0x0000F000;

    @Native
    static final int FLOW_CONTROL_NONE = 0x00010000;
    @Native
    static final int FLOW_CONTROL_RTS_CTS_IN = 0x00100000;
    @Native
    static final int FLOW_CONTROL_RTS_CTS_OUT = 0x00200000;
    @Native
    static final int FLOW_CONTROL_XON_XOFF_IN = 0x00400000;
    @Native
    static final int FLOW_CONTROL_XON_XOFF_OUT = 0x00800000;
    @Native
    static final int FLOW_CONTROL_MASK = 0x00FF0000;

    @Native
    static final int STOP_BITS_1 = 0x01000000;
    @Native
    static final int STOP_BITS_1_5 = 0x02000000;
    @Native
    static final int STOP_BITS_2 = 0x03000000;
    @Native
    static final int STOP_BITS_MASK = 0x0F000000;

    @Native
    static final int PARITY_NONE = 0x10000000;
    @Native
    static final int PARITY_ODD = 0x20000000;
    @Native
    static final int PARITY_EVEN = 0x30000000;
    @Native
    static final int PARITY_MARK = 0x40000000;
    @Native
    static final int PARITY_SPACE = 0x50000000;
    @Native
    static final int PARITY_MASK = 0xF0000000;

    @Native
    static final int NO_PARAMS_TO_SET = 0x00000000;

    static Speed speedFromBitSet(int bitset) {
        switch (bitset & SPEED_MASK) {
            case SPEED_0_BPS:
                return Speed._0_BPS;
            case SPEED_50_BPS:
                return Speed._50_BPS;
            case SPEED_75_BPS:
                return Speed._75_BPS;
            case SPEED_110_BPS:
                return Speed._110_BPS;
            case SPEED_134_BPS:
                return Speed._134_BPS;
            case SPEED_150_BPS:
                return Speed._150_BPS;
            case SPEED_200_BPS:
                return Speed._200_BPS;
            case SPEED_300_BPS:
                return Speed._300_BPS;
            case SPEED_600_BPS:
                return Speed._600_BPS;
            case SPEED_1200_BPS:
                return Speed._1200_BPS;
            case SPEED_1800_BPS:
                return Speed._1800_BPS;
            case SPEED_2400_BPS:
                return Speed._2400_BPS;
            case SPEED_4800_BPS:
                return Speed._4800_BPS;
            case SPEED_9600_BPS:
                return Speed._9600_BPS;
            case SPEED_19200_BPS:
                return Speed._19200_BPS;
            case SPEED_38400_BPS:
                return Speed._38400_BPS;
            case SPEED_57600_BPS:
                return Speed._57600_BPS;
            case SPEED_115200_BPS:
                return Speed._115200_BPS;
            case SPEED_230400_BPS:
                return Speed._230400_BPS;
            case SPEED_460800_BPS:
                return Speed._460800_BPS;
            case SPEED_500000_BPS:
                return Speed._500000_BPS;
            case SPEED_576000_BPS:
                return Speed._576000_BPS;
            case SPEED_921600_BPS:
                return Speed._921600_BPS;
            case SPEED_1000000_BPS:
                return Speed._1000000_BPS;
            case SPEED_1152000_BPS:
                return Speed._1152000_BPS;
            case SPEED_1500000_BPS:
                return Speed._1500000_BPS;
            case SPEED_2000000_BPS:
                return Speed._2000000_BPS;
            case SPEED_2500000_BPS:
                return Speed._2500000_BPS;
            case SPEED_3000000_BPS:
                return Speed._3000000_BPS;
            case SPEED_3500000_BPS:
                return Speed._3500000_BPS;
            case SPEED_4000000_BPS:
                return Speed._4000000_BPS;
            default:
                throw new IllegalArgumentException(String.format("Unknown speed in bitset: %8x", bitset));
        }
    }

    static DataBits dataBitsFromBitSet(int bitset) {
        switch (bitset & DATA_BITS_MASK) {
            case DATA_BITS_DB5:
                return DataBits.DB_5;
            case DATA_BITS_DB6:
                return DataBits.DB_6;
            case DATA_BITS_DB7:
                return DataBits.DB_7;
            case DATA_BITS_DB8:
                return DataBits.DB_8;
            default:
                throw new IllegalArgumentException(String.format("Unknown dataBits in bitset: %8x", bitset));
        }
    }

    static Set<FlowControl> flowControlFromBitSet(int bitset) {
        Set<FlowControl> s = EnumSet.noneOf(FlowControl.class);
        if ((bitset & FLOW_CONTROL_MASK) == FLOW_CONTROL_NONE) {
            return s;
        }
        if ((bitset & FLOW_CONTROL_RTS_CTS_IN) == FLOW_CONTROL_RTS_CTS_IN) {
            s.add(FlowControl.RTS_CTS_IN);
        }
        if ((bitset & FLOW_CONTROL_RTS_CTS_OUT) == FLOW_CONTROL_RTS_CTS_OUT) {
            s.add(FlowControl.RTS_CTS_OUT);
        }
        if ((bitset & FLOW_CONTROL_XON_XOFF_IN) == FLOW_CONTROL_XON_XOFF_IN) {
            s.add(FlowControl.XON_XOFF_IN);
        }
        if ((bitset & FLOW_CONTROL_XON_XOFF_OUT) == FLOW_CONTROL_XON_XOFF_OUT) {
            s.add(FlowControl.XON_XOFF_OUT);
        }
        return s;
    }

    static Parity parityFromBitSet(int bitset) {
        switch (bitset & PARITY_MASK) {
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
                throw new IllegalArgumentException(String.format("Unknown stopBits in bitset: %8x", bitset));
        }
    }

    static StopBits stopBitsFromBitSet(int bitset) {
        switch (bitset & STOP_BITS_MASK) {
            case STOP_BITS_1:
                return StopBits.SB_1;
            case STOP_BITS_1_5:
                return StopBits.SB_1_5;
            case STOP_BITS_2:
                return StopBits.SB_2;
            default:
                throw new IllegalArgumentException(String.format("Unknown stopBits in bitset: %8x", bitset));
        }
    }

    static int toBitSet(Speed speed) {
        switch (speed) {
            case _0_BPS:
                return SPEED_0_BPS;
            case _50_BPS:
                return SPEED_50_BPS;
            case _75_BPS:
                return SPEED_75_BPS;
            case _110_BPS:
                return SPEED_110_BPS;
            case _134_BPS:
                return SPEED_134_BPS;
            case _150_BPS:
                return SPEED_150_BPS;
            case _200_BPS:
                return SPEED_200_BPS;
            case _300_BPS:
                return SPEED_300_BPS;
            case _600_BPS:
                return SPEED_600_BPS;
            case _1200_BPS:
                return SPEED_1200_BPS;
            case _1800_BPS:
                return SPEED_1800_BPS;
            case _2400_BPS:
                return SPEED_2400_BPS;
            case _4800_BPS:
                return SPEED_4800_BPS;
            case _9600_BPS:
                return SPEED_9600_BPS;
            case _19200_BPS:
                return SPEED_19200_BPS;
            case _38400_BPS:
                return SPEED_38400_BPS;
            case _57600_BPS:
                return SPEED_57600_BPS;
            case _115200_BPS:
                return SPEED_115200_BPS;
            case _230400_BPS:
                return SPEED_230400_BPS;
            case _460800_BPS:
                return SPEED_460800_BPS;
            case _500000_BPS:
                return SPEED_500000_BPS;
            case _576000_BPS:
                return SPEED_576000_BPS;
            case _921600_BPS:
                return SPEED_921600_BPS;
            case _1000000_BPS:
                return SPEED_1000000_BPS;
            case _1152000_BPS:
                return SPEED_1152000_BPS;
            case _1500000_BPS:
                return SPEED_1500000_BPS;
            case _2000000_BPS:
                return SPEED_2000000_BPS;
            case _2500000_BPS:
                return SPEED_2500000_BPS;
            case _3000000_BPS:
                return SPEED_3000000_BPS;
            case _3500000_BPS:
                return SPEED_3500000_BPS;
            case _4000000_BPS:
                return SPEED_4000000_BPS;
            default:
                throw new IllegalArgumentException("Unknown speed: " + speed);
        }
    }

    static int toBitSet(DataBits dataBits) {
        switch (dataBits) {
            case DB_5:
                return DATA_BITS_DB5;
            case DB_6:
                return DATA_BITS_DB6;
            case DB_7:
                return DATA_BITS_DB7;
            case DB_8:
                return DATA_BITS_DB8;
            default:
                throw new IllegalArgumentException("Unknown dataBits: " + dataBits);
        }
    }

    static int toBitSet(Parity parity) {
        switch (parity) {
            case EVEN:
                return PARITY_EVEN;
            case MARK:
                return PARITY_MARK;
            case NONE:
                return PARITY_NONE;
            case ODD:
                return PARITY_ODD;
            case SPACE:
                return PARITY_SPACE;
            default:
                throw new IllegalArgumentException("Unknown parity: " + parity);
        }
    }

    static int toBitSet(Set<FlowControl> flowControls) {
        if (flowControls.isEmpty()) {
            return FLOW_CONTROL_NONE;
        }
        int bitSet = 0;
        for (FlowControl fc : flowControls) {
            switch (fc) {
                case RTS_CTS_IN:
                    bitSet |= FLOW_CONTROL_RTS_CTS_IN;
                    break;
                case RTS_CTS_OUT:
                    bitSet |= FLOW_CONTROL_RTS_CTS_OUT;
                    break;
                case XON_XOFF_IN:
                    bitSet |= FLOW_CONTROL_XON_XOFF_IN;
                    break;
                case XON_XOFF_OUT:
                    bitSet |= FLOW_CONTROL_XON_XOFF_OUT;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown flowcontrol:" + fc);
            }
        }
        return bitSet;
    }

    static int toBitSet(StopBits stopBits) {
        switch (stopBits) {
            case SB_1:
                return STOP_BITS_1;
            case SB_1_5:
                return STOP_BITS_1_5;
            case SB_2:
                return STOP_BITS_2;
            default:
                throw new IllegalArgumentException("Unknown stopBits: " + stopBits);
        }
    }

    protected SerialInputStream is;
    protected SerialOutputStream os;

    private final String portName;
    private final Object readLock = new Object();
    private final Object writeLock = new Object();

    /**
     * Creates a new Instance and checks read/write permissions with the
     * System.getSecurityManager().
     *
     * @param portName the name of the port.
     *
     * @see SecurityManager#checkRead(String)
     * @see SecurityManager#checkWrite(String)
     * @see java.io.FileOutputStream#FileOutputStream(String)
     * @see java.io.FileInputStream#FileInputStream(String).
     *
     */
    public AbstractSerialPortSocket(String portName) {
        if (portName == null) {
            throw new IllegalArgumentException("portname must not null!");
        }
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkRead(portName);
            security.checkWrite(portName);
        }
        this.portName = portName;
    }

    @Override
    protected void implCloseChannel() throws IOException {
        is = null;
        os = null;
    }

    /**
     * writes all data in the output buffer
     *
     * @throws IOException
     */
    protected abstract void drainOutputBuffer() throws IOException;

    @Override
    public Speed getSpeed() throws IOException {
        return speedFromBitSet(getParameters(SPEED_MASK));
    }

    @Override
    public DataBits getDatatBits() throws IOException {
        return dataBitsFromBitSet(getParameters(DATA_BITS_MASK));
    }

    @Override
    public Set<FlowControl> getFlowControl() throws IOException {
        return flowControlFromBitSet(getParameters(FLOW_CONTROL_MASK));
    }

    @Override
    public synchronized InputStream getInputStream() throws IOException {
        ensureOpen();
        if (is == null) {
            is = new SerialInputStream();
        }
        return is;
    }

    @Override
    public synchronized OutputStream getOutputStream() throws IOException {
        ensureOpen();
        if (os == null) {
            os = new SerialOutputStream();
        }
        return os;
    }

    @Override
    public Parity getParity() throws IOException {
        return parityFromBitSet(getParameters(PARITY_MASK));
    }

    @Override
    public String getPortName() {
        return portName;
    }

    @Override
    public StopBits getStopBits() throws IOException {
        return stopBitsFromBitSet(getParameters(STOP_BITS_MASK));
    }

    public void ensureOpen() throws IOException {
        if (!isOpen()) {
            throw new IOException(PORT_IS_CLOSED);
        }
    }

    protected void open(Speed speed, DataBits dataBits, StopBits stopBits, Parity parity,
            Set<FlowControl> flowControls) throws IOException {
        int bitset = toBitSet(speed);
        bitset |= toBitSet(dataBits);
        bitset |= toBitSet(stopBits);
        bitset |= toBitSet(parity);
        bitset |= toBitSet(flowControls);
        open(portName, bitset);
    }

    /**
     * Open port
     *
     * @param portName the name of port for opening
     * @param paramBitSet the parameters as masked bit set.
     * @throws java.io.IOException
     *
     */
    protected abstract void open(String portName, int paramBitSet) throws IOException;

    @Override
    public void sendBreak(int duration) throws IOException {
        synchronized (writeLock) {
            //make this blocking IO interruptable
            boolean completed = false;
            try {
                begin();
                sendBreak0(duration);
                completed = true;
            } catch (IOException e) {
                completed = true;
                throw e;
            } finally {
                end(completed);
            }
        }
    }

    @Override
    public void setSpeed(Speed speed) throws IOException {
        try {
            setParameters(toBitSet(speed));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Can't set speed " + speed + " on port: " + getPortName(), ex);
        }
    }

    @Override
    public void setBreak(boolean value) throws IOException {
        synchronized (writeLock) {
            setBreak0(value);
        }
    }

    @Override
    public void setDataBits(DataBits dataBits) throws IOException {
        try {
            setParameters(toBitSet(dataBits));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Can't set dataBits " + dataBits + " on port: " + getPortName() + " value is:" + getDatatBits(),
                    ex);
        }
    }

    @Override
    public void setFlowControl(Set<FlowControl> flowControls) throws IOException {
        setParameters(toBitSet(flowControls));
    }

    @Override
    public void setParity(Parity parity) throws IOException {
        setParameters(toBitSet(parity));
    }

    @Override
    public native void setRTS(boolean value) throws IOException;

    @Override
    public void setStopBits(StopBits stopBits) throws IOException {
        try {
            setParameters(toBitSet(stopBits));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Can't set stopBits " + stopBits + " on port: " + getPortName(), ex);
        }
    }

    @Override
    public String toString() {
        try {
            return String.format("[portname=%s, speed= %s, dataBits= %s, stopBits= %s, parity= %s, flowControl= %s]",
                    getPortName(), getSpeed(), getDatatBits(), getStopBits(), getParity(), getFlowControl());
        } catch (IOException e) {
            return "Internal Error " + e;
        }
    }

}
