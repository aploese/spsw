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
package de.ibapl.spsw.jnhwprovider;

import de.ibapl.spsw.spi.SerialOutputStream;
import de.ibapl.spsw.spi.SerialInputStream;
import de.ibapl.spsw.api.SerialPortSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Currently its more effective to buffer arrays in a ByteBuffer instead
 * of unwrap each array in the native part - we do not know how much we can
 * write or read so the whole array is copied for each call for instahnce
 * (POSIX) Unistd.write.
 *
 * @author aploese
 * @param <T>
 */
public abstract class StreamSerialPortSocket<T extends StreamSerialPortSocket<T>> extends AbstractSerialPortSocket<T> implements SerialPortSocket {

    protected SerialInputStream<T> is;
    protected SerialOutputStream<T> os;


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
    public StreamSerialPortSocket(String portName) {
        super(portName);
    }

    @Override
    protected void implCloseChannel() throws IOException {
        is = null;
        os = null;
    }

    @Override
    public synchronized InputStream getInputStream() throws IOException {
        ensureOpen();
        if (is == null) {
            is = new SerialInputStream(this);
        }
        return is;
    }

    @Override
    public synchronized OutputStream getOutputStream() throws IOException {
        ensureOpen();
        if (os == null) {
            os = new SerialOutputStream(this);
        }
        return os;
    }

}
