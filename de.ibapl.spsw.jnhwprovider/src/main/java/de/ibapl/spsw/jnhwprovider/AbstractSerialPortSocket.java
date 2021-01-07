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

import de.ibapl.spsw.api.SerialPortConfiguration;
import java.io.IOException;
import java.nio.channels.spi.AbstractInterruptibleChannel;

/**
 * Currently its more effective to buffer arrays in a ByteBuffer instead
 * of unwrap each array in the native part - we do not know how much we can
 * write or read so the whole array is copied for each call for instahnce
 * (POSIX) Unistd.write.
 *
 * @author aploese
 * @param <T>
 */
public abstract class AbstractSerialPortSocket<T extends AbstractSerialPortSocket<T>> extends AbstractInterruptibleChannel implements SerialPortConfiguration {

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

    protected void ensureOpen() throws IOException {
        if (!isOpen()) {
            throw new IOException(PORT_IS_CLOSED);
        }
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
