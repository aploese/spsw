package de.ibapl.spsw.api;

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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import org.osgi.annotation.versioning.ProviderType;

/**
 *
 * @author scream3r
 */
@ProviderType
public interface SerialPortSocket extends Closeable {

    boolean isClosed();

    boolean isCTS() throws IOException;

    boolean isDSR() throws IOException;
    
//Not supported under Win    boolean isRTS() throws IOException;
    
//Not supported under Win    boolean isDTR() throws IOException;

    boolean isIncommingRI() throws IOException;

    InputStream getInputStream() throws IOException;

    OutputStream getOutputStream() throws IOException;

    /**
     * Getting port name under operation
     *
     * @return Method returns port name under operation as a String
     */
    String getPortName();

    /**
     * Getting port state
     *
     * @return Method returns true if port is open, otherwise false
     */
    boolean isOpen();

    /**
     * Port opening
     * <br><br>
     * <b>Note: </b>If port busy <b>TYPE_PORT_BUSY</b> exception will be thrown.
     * If port not found <b>TYPE_PORT_NOT_FOUND</b> exception will be thrown.
     *
     * @throws SerialPortException
     */
    void openAsIs() throws IOException;

    void openRaw() throws IOException;

    void openTerminal() throws IOException;

    void openModem() throws IOException;

    /**
     * Setting the parameters of port
     *
     * @param baudRate data transfer rate
     * @param dataBits number of data bits
     * @param stopBits number of stop bits
     * @param parity parity
     * @param flowControls
     * @throws java.io.IOException
     *
     *
     * @since 0.8
     */
    void openRaw(Baudrate baudRate, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls) throws IOException;

    /**
     * Close port. This method deletes event listener first, then closes the
     * port
     *
     * @throws java.io.IOException
     */
    @Override
    void close() throws IOException;

    /**
     * Change RTS line state
     *
     * @param value <b>true - ON</b>, <b>false - OFF</b>
     * @throws java.io.IOException
     *
     */
    void setRTS(boolean value) throws IOException;

    /**
     * Change DTR line state
     *
     * @param value <b>true - ON</b>, <b>false - OFF</b>
     * @throws java.io.IOException
     *
     */
    void setDTR(boolean value) throws IOException;

    void setXONChar(char c) throws IOException;

    void setXOFFChar(char c) throws IOException;

    char getXONChar() throws IOException;

    char getXOFFChar() throws IOException;

    /**
     * 
     * @param duration the duratiuon in ms.
     * @throws IOException 
     */
    void sendBreak(int duration) throws IOException;
    
    void sendXON() throws IOException;

    void sendXOFF() throws IOException;
    
    /**
     * Get bytes count in in buffer of port
     *
     * @return Method returns the array that contains info about bytes count in
     * buffers:
     * @throws java.io.IOException
     *
     */
    int getInBufferBytesCount() throws IOException;
    
    /**
     * Get bytes count out inbuffer of port
     *
     * @return Method returns the array that contains info about bytes count in
     * buffers:
     * @throws java.io.IOException
     *
     */
    int getOutBufferBytesCount() throws IOException;

    /**
     * Set Break singnal
     *
     * @param value the value
     * @throws java.io.IOException
     */
    void setBreak(boolean value) throws IOException;

    void setFlowControl(Set<FlowControl> flowControls) throws IOException;

    void setBaudrate(Baudrate baudrate) throws IOException;

    void setDataBits(DataBits dataBits) throws IOException;

    void setStopBits(StopBits stopBits) throws IOException;

    void setParity(Parity parity) throws IOException;

    Baudrate getBaudrate() throws IOException;

    DataBits getDatatBits() throws IOException;

    StopBits getStopBits() throws IOException;

    Parity getParity() throws IOException;

    Set<FlowControl> getFlowControl() throws IOException;
    
    /**
     * Returns setting for the timeout in ms.
     * 0 returns implies that the option is disabled (i.e., timeout of infinity).
     *
     * @return the timeout
     *
     * @see #setTimeout(int)
     */
    int getTimeout() throws IOException;
    
    /**
     *  Enable/disable the timeout, in milliseconds. With this option set
     *  to a non-zero timeout, a read() call on the InputStream associated with
     *  this Socket will block for only this amount of time.  If the timeout
     *  expires, a <B>java.net.SocketTimeoutException</B> is raised, though the
     *  Socket is still valid. The option <B>must</B> be enabled
     *  prior to entering the blocking operation to have effect. The
     *  timeout must be {@code > 0}.
     *  A timeout of zero is interpreted as an infinite timeout.
     *
     * @param timeout the specified timeout, in milliseconds.
     * @exception SocketException if there is an error
     * in the underlying protocol, such as a TCP error.
     * @see #getTimeout()
     */
    void setTimeout(int timeout) throws IOException;

}
