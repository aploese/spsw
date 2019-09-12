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

public abstract class AbstractSerialPortSocket<T extends AbstractSerialPortSocket<T>> implements SerialPortSocket {

    protected class SerialInputStream extends InputStream {

        private ByteBuffer singleReadBuffer;

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
            if (singleReadBuffer == null) {
                singleReadBuffer = ByteBuffer.allocateDirect(1);
            } else {
                singleReadBuffer.clear();
            }
            try {
                int result = AbstractSerialPortSocket.this.read(singleReadBuffer);
                if (result == 1) {
                    singleReadBuffer.flip();
                    return singleReadBuffer.get() & 0xff;
                } else {
                    return result;
                }
            } catch (AsynchronousCloseException ace) {
                return -1;
            }
        }

        @Override
        public int read(byte b[]) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            } else if (b.length == 0) {
                return 0;
            }
            ByteBuffer buf = ByteBuffer.allocateDirect(b.length);
            try {
                int result = AbstractSerialPortSocket.this.read(buf);
                buf.flip();
                buf.get(b, 0, result);
                return result;
            } catch (AsynchronousCloseException ace) {
                return -1;
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
            ByteBuffer buf = ByteBuffer.allocateDirect(len);

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

    protected class SerialOutputStream extends OutputStream {

        private ByteBuffer singleWriteBuffer;

        @Override
        public void close() throws IOException {
            AbstractSerialPortSocket.this.close();
        }

        @Override
        public void flush() throws IOException {
            AbstractSerialPortSocket.this.drainOutputBuffer();
        }

        @Override
        public void write(byte b[]) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            } else if (b.length == 0) {
                return;
            }
            ByteBuffer buf = ByteBuffer.allocateDirect(b.length);
            buf.put(b);
            buf.flip();
            AbstractSerialPortSocket.this.write(buf);
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
            ByteBuffer buf = ByteBuffer.allocateDirect(len);
            buf.put(b, off, len);
            buf.flip();
            AbstractSerialPortSocket.this.write(buf);
        }

        @Override
        public void write(int b) throws IOException {
            if (singleWriteBuffer == null) {
                singleWriteBuffer = ByteBuffer.allocateDirect(1);
            } else {
                singleWriteBuffer.clear();
            }
            singleWriteBuffer.put((byte) b);
            singleWriteBuffer.flip();
            AbstractSerialPortSocket.this.write(singleWriteBuffer);
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
    public synchronized void close() throws IOException {
        is = null;
        os = null;
    }

    /**
     * writes all data in the output buffer
     *
     * @throws IOException
     */
    protected abstract void drainOutputBuffer() throws IOException;

    
    //TODO JAVA 9 finalze
    @Deprecated
    @Override
    protected final void finalize() throws Throwable {
        try {
            if (isOpen()) {
                close();
            }
        } catch (Exception ex) {
            // This should always work
            System.err.println("SerialPortSocket " + getPortName() + " finalize() exception: " + ex);
        } catch (Error err) {
            // Leave a trace what hit us...
            System.err.println("SerialPortSocket " + getPortName() + " finalize() error: " + err);
            throw err;
        } finally {
            super.finalize();
        }
    }

    @Override
    public synchronized InputStream getInputStream() throws IOException {
        if (!isOpen()) {
            throw new IOException(PORT_IS_CLOSED);
        }
        if (is == null) {
            is = new SerialInputStream();
        }
        return is;
    }

    @Override
    public synchronized OutputStream getOutputStream() throws IOException {
        if (!isOpen()) {
            throw new IOException(PORT_IS_CLOSED);
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
    public String toString() {
        try {
            return String.format("[portname=%s, speed= %s, dataBits= %s, stopBits= %s, parity= %s, flowControl= %s]",
                    getPortName(), getSpeed(), getDatatBits(), getStopBits(), getParity(), getFlowControl());
        } catch (IOException e) {
            return "Internal Error " + e;
        }
    }

}
