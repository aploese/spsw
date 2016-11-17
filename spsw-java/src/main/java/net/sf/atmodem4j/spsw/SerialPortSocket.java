package net.sf.atmodem4j.spsw;

/*
 * #%L
 * SPSW Java
 * %%
 * Copyright (C) 2009 - 2014 atmodem4j
 * %%
 * atmodem4j - A serial port socket wrapper- http://atmodem4j.sourceforge.net/
 * Copyright (C) 2009-2014, atmodem4j.sf.net, and individual contributors as indicated
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

/**
 *
 * @author scream3r
 */
public interface SerialPortSocket extends Closeable {

    public class SerialPortSocketFactory {

        public SerialPortSocket createSerialPortSocket(String portName) {
            switch (System.getProperty("os.name")) {
                case "Linux":
                    return new GenericTermiosSerialPortSocket(portName);
                case "Mac OS":
                    throw new UnsupportedOperationException("Mac OS is currently not supported yet");
                case "Mac OS X":
                    throw new UnsupportedOperationException("Mac OS X is currently not supported yet");
                case "Windows 95":
                    return new GenericWinSerialPortSocket(portName);
                case "Windows 98":
                    return new GenericWinSerialPortSocket(portName);
                case "Windows Me":
                    return new GenericWinSerialPortSocket(portName);
                case "Windows NT":
                    return new GenericWinSerialPortSocket(portName);
                case "Windows 2000":
                    return new GenericWinSerialPortSocket(portName);
                case "Windows XP":
                    return new GenericWinSerialPortSocket(portName);
                case "Windows 2003":
                    return new GenericWinSerialPortSocket(portName);
                case "Windows Vista":
                    return new GenericWinSerialPortSocket(portName);
                case "Windows 2008":
                    return new GenericWinSerialPortSocket(portName);
                case "Windows 7":
                    return new GenericWinSerialPortSocket(portName);
                case "Windows 8":
                    return new GenericWinSerialPortSocket(portName);
                case "Windows 2012":
                    return new GenericWinSerialPortSocket(portName);
                case "Windows CE":
                    throw new UnsupportedOperationException("Windows CE is currently not supported yet");
                case "OS/2":
                    throw new UnsupportedOperationException("OS/2 is currently not supported yet");
                case "MPE/iX":
                    throw new UnsupportedOperationException("MPE/iX is currently not supported yet");
                case "HP-UX":
                    throw new UnsupportedOperationException("HP-UX is currently not supported yet");
                case "AIX":
                    throw new UnsupportedOperationException("AIX is currently not supported yet");
                case "OS/390":
                    throw new UnsupportedOperationException("OS/390 is currently not supported yet");
                case "FreeBSD":
                    throw new UnsupportedOperationException("FreeBSD is currently not supported yet");
                case "Irix":
                    throw new UnsupportedOperationException("Irix is currently not supported yet");
                case "Digital Unix":
                    throw new UnsupportedOperationException("Digital Unix is currently not supported yet");
                case "NetWare 4.11":
                    throw new UnsupportedOperationException("NetWare 4.11 is currently not supported yet");
                case "OSF1":
                    throw new UnsupportedOperationException("OSF1 is currently not supported yet");
                case "OpenVMS":
                    throw new UnsupportedOperationException("OpenVMS is currently not supported yet");
                default:
                    throw new RuntimeException("Can't figure out OS: " + System.getProperty("os.name"));
            }
        }
    }

    static SerialPortSocketFactory FACTORY = new SerialPortSocketFactory();

    boolean isClosed();

    boolean isCTS();

    boolean isDSR();

    boolean isIncommingRI();
    
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

}
