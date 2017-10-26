package de.ibapl.spsw.provider;

/*-
 * #%L
 * SPSW Provider
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

import de.ibapl.spsw.spi.AbstractSerialPortSocket;
import java.io.IOException;

/**
 *
 * @author scream3r
 */
public class GenericTermiosSerialPortSocket extends AbstractSerialPortSocket {

    /**
     * The file descriptor or handle for this Port
     */
    private int fd = -1;
    
    /**
     * used in native code
     */
    private int pollReadTimeout = -1;
    private int pollWriteTimeout = -1;

    public GenericTermiosSerialPortSocket(String portName) {
        super(portName);
    }

    @Override
    protected native void open(String portName, int type) throws IOException;

    /**
     * Close port
     */
    @Override
    protected native void close0() throws IOException;

    @Override
    public native boolean isCTS();

    public native boolean isRTS();

    @Override
    public native boolean isDSR();

    public native boolean isDTR();

    @Override
    public native boolean isIncommingRI();

    @Override
    public native void setRTS(boolean value) throws IOException;

    @Override
    public native void setDTR(boolean value) throws IOException;

    @Override
    public native void setXONChar(char c) throws IOException;

    @Override
    public native void setXOFFChar(char c) throws IOException;

    @Override
    protected native int readSingle() throws IOException;

    /**
     * Read data from port
     *
     * @param b the data to be written
     * @param off the start offset in the data
     * @param len the number of bytes that are written
     * @exception IOException If an I/O error has occurred.
     */
    @Override
    protected native int readBytes(byte[] b, int off, int len) throws IOException;

    @Override
    protected native void writeSingle(int b) throws IOException;

    /**
     * Write data to port
     *
     * @param off the start offset in the data.
     * @param len the number of bytes to write.
     *
     */
    @Override
    protected native void writeBytes(byte[] b, int off, int len) throws IOException;

    @Override
    public native int getInBufferBytesCount() throws IOException;

    @Override
    public native int getOutBufferBytesCount() throws IOException;
    
    @Override
    public native void drainOutputBuffer() throws IOException;
    
    @Override
    protected native void setFlowControl(int mask) throws IOException;

    @Override
    protected native int getFlowControl0() throws IOException;

    @Override
    public native void setBreak(boolean value) throws IOException;

    @Override
    public native void sendBreak(int duration) throws IOException;
    
    @Override
    protected native void setBaudrate(int baudRate) throws IOException;

    @Override
    protected native void setDataBits(int value) throws IOException;

    @Override
    protected native void setStopBits(int value) throws IOException;

    @Override
    protected native void setParity(int parity) throws IOException;

    @Override
    protected native int getBaudrate0() throws IOException;

    @Override
    protected native int getDataBits0() throws IOException;

    @Override
    protected native int getStopBits0() throws IOException;

    @Override
    protected native int getParity0() throws IOException;

    @Override
    public native char getXONChar() throws IOException;

    @Override
    public native char getXOFFChar() throws IOException;

    @Override
    public native void sendXON() throws IOException;

    @Override
    public native void sendXOFF() throws IOException;

    @Override
    public native int getOverallReadTimeout() throws IOException;

    @Override
    public native int getOverallWriteTimeout() throws IOException;

    @Override
    public native void setTimeouts(int interByteReadTimeout, int overallReadTimeout, int overallWriteTimeout) throws IOException;
    
    @Override
    public native int getInterByteReadTimeout() throws IOException;

}
