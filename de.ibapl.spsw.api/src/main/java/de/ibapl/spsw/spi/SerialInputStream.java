/*
 * SPSW - Drivers for the serial port, https://github.com/aploese/spsw/
 * Copyright (C) 2009-2021, Arne Plöse and individual contributors as indicated
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
package de.ibapl.spsw.spi;

import de.ibapl.spsw.api.SerialPortSocket;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author aploese
 */
public class SerialInputStream<T extends SerialPortSocket> extends InputStream {

    public final static int DEFAULT_BUFFER_SIZE = 1024;

    private final ByteBuffer readBuffer;
    public final int BUFFER_SIZE;
    protected final T serialPortSocket;
    private final Lock readBufferLock = new ReentrantLock(true);

    public SerialInputStream(final T serialPortSocket, int bufferSize) {
        this.serialPortSocket = serialPortSocket;
        BUFFER_SIZE = bufferSize;
        readBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    }

    public SerialInputStream(final T serialPortSocket) {
        this(serialPortSocket, DEFAULT_BUFFER_SIZE);
    }

    @Override
    public int available() throws IOException {
        return serialPortSocket.getInBufferBytesCount();
    }

    @Override
    public void close() throws IOException {
        serialPortSocket.close();
    }

    @Override
    public int read() throws IOException {
        final boolean useReadBuffer = readBufferLock.tryLock();
        try {
            final ByteBuffer buf = useReadBuffer ? readBuffer : ByteBuffer.allocateDirect(1);
            buf.clear().limit(1);
            try {
                int result = serialPortSocket.read(buf);
                switch (result) {
                    case 1:
                        buf.flip();
                        return buf.get() & 0xff;
                    case 0:
                        return -1;
                    default:
                        throw new RuntimeException("Should never happen single read returns " + result);
                }
            } catch (AsynchronousCloseException ace) {
                return -1;
            }
        } finally {
            if (useReadBuffer) {
                readBufferLock.unlock();
            }
        }
    }

    @Override
    public int read(final byte[] b) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        final int len = b.length;
        final boolean useReadBuffer = (len <= BUFFER_SIZE) ? readBufferLock.tryLock() : false;
        try {
            ByteBuffer buf = useReadBuffer ? readBuffer : ByteBuffer.allocateDirect(len);
            buf.clear().limit(len);
            try {
                final int result = serialPortSocket.read(buf);
                buf.flip();
                buf.get(b, 0, result);
                return result;
            } catch (AsynchronousCloseException ace) {
                return -1;
            }
        } finally {
            if (useReadBuffer) {
                readBufferLock.unlock();
            }
        }
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
        final boolean useReadBuffer = (len <= BUFFER_SIZE) ? readBufferLock.tryLock() : false;
        try {
            ByteBuffer buf = useReadBuffer ? readBuffer : ByteBuffer.allocateDirect(len);
            buf.clear().limit(len);
            try {
                final int result = serialPortSocket.read(buf);
                buf.flip();
                buf.get(b, off, result);
                return result;
            } catch (AsynchronousCloseException ace) {
                return -1;
            }
        } finally {
            if (useReadBuffer) {
                readBufferLock.unlock();
            }
        }
    }

}
