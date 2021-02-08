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
import static de.ibapl.spsw.spi.SerialInputStream.DEFAULT_BUFFER_SIZE;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author aploese
 */
public class SerialOutputStream<T extends SerialPortSocket> extends OutputStream {

    private final ByteBuffer writeBuffer;
    public final int BUFFER_SIZE;
    protected final T serialPortSocket;
    private final Lock writeBufferLock = new ReentrantLock(true);

    public SerialOutputStream(final T serialPortSocket, int bufferSize) {
        this.serialPortSocket = serialPortSocket;
        BUFFER_SIZE = bufferSize;
        writeBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    }

    public SerialOutputStream(final T serialPortSocket) {
        this(serialPortSocket, DEFAULT_BUFFER_SIZE);
    }

    @Override
    public void close() throws IOException {
        serialPortSocket.close();
    }

    @Override
    public void flush() throws IOException {
        serialPortSocket.drainOutputBuffer();
    }

    @Override
    public void write(final byte[] b) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        final int len = b.length;
        final boolean useWriteBuffer = (len <= BUFFER_SIZE) ? writeBufferLock.tryLock() : false;
        try {
            ByteBuffer buf = useWriteBuffer ? writeBuffer : ByteBuffer.allocateDirect(len);
            buf.clear().put(b, 0, len).flip();
            serialPortSocket.write(buf);
        } finally {
            if (useWriteBuffer) {
                writeBufferLock.unlock();
            }
        }
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        }
        final boolean useWriteBuffer = (len <= BUFFER_SIZE) ? writeBufferLock.tryLock() : false;
        try {
            ByteBuffer buf = useWriteBuffer ? writeBuffer : ByteBuffer.allocateDirect(len);
            buf.clear().put(b, off, len).flip();
            serialPortSocket.write(buf);
        } finally {
            if (useWriteBuffer) {
                writeBufferLock.unlock();
            }
        }
    }

    @Override
    public void write(final int b) throws IOException {
        final boolean useWriteBuffer = writeBufferLock.tryLock();
        try {
            final ByteBuffer buf = useWriteBuffer ? writeBuffer : ByteBuffer.allocateDirect(1);
            buf.clear().put((byte) b).flip();
            serialPortSocket.write(buf);
        } finally {
            if (useWriteBuffer) {
                writeBufferLock.unlock();
            }
        }
    }

}
