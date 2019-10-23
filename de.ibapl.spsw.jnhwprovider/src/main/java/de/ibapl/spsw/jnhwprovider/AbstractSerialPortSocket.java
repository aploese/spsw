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
package de.ibapl.spsw.jnhwprovider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;

import de.ibapl.spsw.api.SerialPortSocket;
import java.nio.channels.spi.AbstractInterruptibleChannel;

public abstract class AbstractSerialPortSocket<T extends AbstractSerialPortSocket<T>> extends AbstractInterruptibleChannel implements SerialPortSocket {

    public final static int BUFFER_SIZE = 1024;

    protected class SerialInputStream extends InputStream {

        private final ByteBuffer readBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

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
            synchronized (readBuffer) {
                readBuffer.clear().limit(1);
                try {
                    int result = AbstractSerialPortSocket.this.read(readBuffer);
                    switch (result) {
                        case 1:
                            readBuffer.flip();
                            return readBuffer.get() & 0xff;
                        case 0:
                            return -1;
                        default:
                            throw new RuntimeException("Should never happen single read returns " + result);
                    }
                } catch (AsynchronousCloseException ace) {
                    return -1;
                }
            }
        }

        @Override
        public int read(final byte b[]) throws IOException {
            return read(b, 0, b.length);
        }

        @Override
        public int read(final byte b[], final int off, final int len) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            } else if (off < 0 || len < 0 || len > b.length - off) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return 0;
            }
            if (len <= BUFFER_SIZE) {
                synchronized (readBuffer) {
                    readBuffer.clear().limit(len);
                    try {
                        int result = AbstractSerialPortSocket.this.read(readBuffer);
                        readBuffer.flip();
                        readBuffer.get(b, off, result);
                        return result;
                    } catch (AsynchronousCloseException ace) {
                        return -1;
                    }
                }
            } else {
                final ByteBuffer buf = ByteBuffer.allocateDirect(len);
                try {
                    int result = AbstractSerialPortSocket.this.read(buf);
                    buf.flip();
                    buf.get(b, off, result);
                    return result;
                } catch (AsynchronousCloseException ace) {
                    return -1;
                }
            }
        }
    }

    protected class SerialOutputStream extends OutputStream {

        private final ByteBuffer writeBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

        @Override
        public void close() throws IOException {
            AbstractSerialPortSocket.this.close();
        }

        @Override
        public void flush() throws IOException {
            AbstractSerialPortSocket.this.drainOutputBuffer();
        }

        @Override
        public void write(final byte b[]) throws IOException {
            write(b, 0, b.length);
        }

        @Override
        public void write(final byte b[], final int off, final int len) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            } else if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return;
            }
            if (len <= BUFFER_SIZE) {
                synchronized (writeBuffer) {
                    writeBuffer.clear().put(b, off, len).flip();
                    AbstractSerialPortSocket.this.write(writeBuffer);
                }
            } else {
                final ByteBuffer buf = ByteBuffer.allocateDirect(len);
                buf.put(b, off, len).flip();
                AbstractSerialPortSocket.this.write(buf);
            }
        }

        @Override
        public void write(final int b) throws IOException {
            synchronized (writeBuffer) {
                writeBuffer.clear().put((byte) b).flip();
                AbstractSerialPortSocket.this.write(writeBuffer);
            }
        }

    }

    protected SerialInputStream is;
    protected SerialOutputStream os;

    protected final String portName;

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

    protected void ensureOpen() throws IOException {
        if (!isOpen()) {
            throw new IOException(PORT_IS_CLOSED);
        }
    }

    /**
     * writes all data in the output buffer
     *
     * @throws IOException
     */
    protected abstract void drainOutputBuffer() throws IOException;

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
    public String getPortName() {
        return portName;
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
