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
package de.ibapl.spsw.api;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ByteChannel;
import java.nio.channels.InterruptibleChannel;
import org.osgi.annotation.versioning.ProviderType;

/**
 * The interface for accessing a serial port.
 *
 * Port means a serial device like UART, usb to serial converter or even a TCP
 * bridge to an serial device on a different machine. Due to the fact that a
 * character her can have 5,6,7 or 8 data bits, don't confuse characters with
 * bytes, see {@link DataBits} for more details. If you work wit 8 data bytes
 * characters and bytes have the same length.
 *
 * A implementing class should check permissions with a SecurityManager. It is
 * desired to check fail-fast in the constructor. The {@link FileOutputStream}
 * should be the blueprint.<br>
 * Checking of the closed state should be done lazily to improve performance.
 * The implementations are not required to be thread save.<br>
 * Closing a port with blocked read or write operation should unblock the
 * pending read/write and throw a
 * {@code InterruptedIOException(SerialPortSocket.PORT_IS_CLOSED)} in the Thread
 * of that read/write.<br>
 * It is desired to close any OS resources in an finalizer. This is to to
 * prevent blocked ports in larger systems. A call to the garbage collector can
 * then free the still claimed resources.
 *
 * The meaning of the signal lines (RTS/CTS...) is viewn from this point. There
 * are two general cable configurations used with the RS-232C Communications
 * Standard:
 * <li>Data Terminal Equipment (DTE): IBM PC's, printers, plotters, etc <br>
 * </li>
 * <li>Data Communication Equipment (DCE): modems, multiplexors, etc</li> <br>
 *
 * @see <a href=
 *      "https://www.wikipedia.org/wiki/Serial_port">www.wikipedia.org/wiki/Serial_port</a>
 *
 * @author Arne Plöse
 */
//TODO make SerialportSocket implement ByteChannel itself???	
@ProviderType
public interface SerialPortSocket extends SerialPortConfiguration, ByteChannel, InterruptibleChannel {

    /**
     * Returns the InputStream for this port.
     *
     * @return the InputStream.
     * @throws IOException if port is closed or an error at OS level occurs.
     */
    InputStream getInputStream() throws IOException;

    /**
     * Returns the OutputStream for this port.
     *
     * @return the OutputSTream.
     * @throws IOException if port is closed or an error at OS level occurs.
     */
    OutputStream getOutputStream() throws IOException;

}
