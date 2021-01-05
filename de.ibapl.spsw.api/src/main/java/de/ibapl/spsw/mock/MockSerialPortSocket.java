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
package de.ibapl.spsw.mock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;
import java.nio.ByteBuffer;
import java.nio.channels.spi.AbstractInterruptibleChannel;

/**
 * Helper class for test that interact with a SerialPortSocket. The behavior of
 * a SerialPortSocket can be predefined. Example:
 *
 * <pre>
 * {@code
 * 		mockSerialPortSocket = new MockSerialPortSocket();
 * mockSerialPortSocket.expectedWrite("0102");
 * mockSerialPortSocket.expectedRead("0201");
 * mockSerialPortSocket.expectedWrite("0304");
 * mockSerialPortSocket.expectedRead("0403");
 *
 * SerialPortSocket serialPortSocket = mockSerialPortSocket;
 *
 * serialPortSocket.open();
 * serialPortSocket.getOutputStream().write(0x01);
 * serialPortSocket.getOutputStream().write(0x02);
 * assertEquals(0x02, serialPortSocket.getInputStream().read());
 * assertEquals(0x01, serialPortSocket.getInputStream().read());
 * assertThrows(UnexpectedRequestError.class, () -> {
 * serialPortSocket.getInputStream().read(); });
 *
 * }
 *
 * <pre>
 *
 * @author Arne Plöse
 */
public class MockSerialPortSocket extends AbstractInterruptibleChannel implements SerialPortSocket {
    
    @Override
    public int read(ByteBuffer dst) throws IOException {
        //TODO
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        //TODO
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void implCloseChannel() throws IOException {
        //no-op
    }



    public class MockInputStream extends InputStream {

        int readPtr = 0;

        @Override
        public int available() throws IOException {
            ensureOpen();
            if (factory.isDataEmpty()) {
                return 0;
            }
            if (factory.getDataFirst().requestType != MockRequestType.READ) {
                return 0;
            }
            if (factory.getDataFirst() instanceof MockExceptionRequest) {
                return 0;
            }
            if (factory.getDataFirst() instanceof MockDataRequest) {
                final MockDataRequest dataRequest = (MockDataRequest) factory.getDataFirst();
                return dataRequest.payload.length - readPtr;
            } else {
                throw new UnexpectedRequestError("No read data request", factory.getDataFirst().stackException);
            }
        }

        @Override
        public void close() throws IOException {
            MockSerialPortSocket.this.close();
        }

        @Override
        public int read() throws IOException {
            ensureOpen();
            if (factory.isDataEmpty()) {
                throw new UnexpectedRequestError("data is empty");
            }
            if (factory.getDataFirst().requestType != MockRequestType.READ) {
                throw new UnexpectedRequestError("No Read request", factory.getDataFirst().stackException);
            }
            if (factory.getDataFirst() instanceof MockExceptionRequest) {
                final MockExceptionRequest exceptionRequest = (MockExceptionRequest) factory.getDataFirst();
                factory.removeDataFirst();
                throw exceptionRequest.payload;
            }
            if (factory.getDataFirst() instanceof MockDataRequest) {
                final MockDataRequest dataRequest = (MockDataRequest) factory.getDataFirst();
                int result = dataRequest.payload[readPtr++];
                if (readPtr == dataRequest.payload.length) {
                    readPtr = 0;
                    factory.removeDataFirst();
                }
                return result;
            } else {
                throw new UnexpectedRequestError("No read data request", factory.getDataFirst().stackException);
            }
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            ensureOpen();
            if (factory.isDataEmpty()) {
                throw new UnexpectedRequestError("data is empty");
            }
            if (factory.getDataFirst().requestType != MockRequestType.READ) {
                throw new UnexpectedRequestError("No Read request", factory.getDataFirst().stackException);
            }
            if (factory.getDataFirst() instanceof MockExceptionRequest) {
                final MockExceptionRequest exceptionRequest = (MockExceptionRequest) factory.getDataFirst();
                factory.removeDataFirst();
                throw exceptionRequest.payload;
            }
            if (factory.getDataFirst() instanceof MockDataRequest) {
                final MockDataRequest dataRequest = (MockDataRequest) factory.getDataFirst();
                int count = dataRequest.payload.length - readPtr;
                if (len < count) {
                    count = len;
                }
                System.arraycopy(dataRequest.payload, readPtr, b, off, count);
                readPtr += count;
                if (readPtr == dataRequest.payload.length) {
                    readPtr = 0;
                    factory.removeDataFirst();
                }
                return count;
            } else {
                throw new UnexpectedRequestError("No read data request", factory.getDataFirst().stackException);
            }
        }

    }

    public class MockOutputStream extends OutputStream {

        private int writePtr = 0;

        @Override
        public void close() throws IOException {
            MockSerialPortSocket.this.close();
        }

        @Override
        public void write(int b) throws IOException {
            ensureOpen();
            if (factory.isDataEmpty()) {
                throw new UnexpectedRequestError("data is empty");
            }
            if (factory.getDataFirst().requestType != MockRequestType.WRITE) {
                throw new UnexpectedRequestError("No Write request", factory.getDataFirst().stackException);
            }
            if (factory.getDataFirst() instanceof MockExceptionRequest) {
                final MockExceptionRequest exceptionRequest = (MockExceptionRequest) factory.getDataFirst();
                factory.removeDataFirst();
                throw exceptionRequest.payload;
            }
            if (factory.getDataFirst() instanceof MockDataRequest) {
                final MockDataRequest dataRequest = (MockDataRequest) factory.getDataFirst();
                if (b != dataRequest.payload[writePtr++]) {
                    throw new UnexpectedRequestError("Not expected write data at " + (writePtr - 1),
                            factory.getDataFirst().stackException);
                }
                if (writePtr == dataRequest.payload.length) {
                    writePtr = 0;
                    factory.removeDataFirst();
                }
            } else {
                throw new UnexpectedRequestError("No read data request", factory.getDataFirst().stackException);
            }
        }

    }


    public class UnexpectedRequestError extends Error {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public UnexpectedRequestError(String message) {
            super(message);
        }

        public UnexpectedRequestError(String message, MockRequestStackException stackException) {
            super(message, stackException);
        }

    }

    public static byte[] ascii2Bytes(String s) {
        byte[] result = new byte[s.length() / 2];

        for (int i = 0; i < (s.length() / 2); i++) {
            result[i] = (byte) Integer.parseInt(s.substring(i * 2, (i * 2) + 2), 16);
        }

        return result;
    }

    public static String bytes2Ascii(byte[] byteArray) {
        StringBuilder sb = new StringBuilder(byteArray.length);

        for (byte b : byteArray) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    private Speed speed;
    private DataBits dataBits;
    private Set<FlowControl> flowControl;
    private int interByteReadTimeout;
    private MockInputStream is;
    private MockOutputStream os;
    private final MockSerialPortFactory factory;
    private final String portname;
    
    private int overallReadTimeout;

    private int overallWriteTimeout;

    private Parity parity;

    private StopBits stopBits;

    @Override
    public Speed getSpeed() throws IOException {
        ensureOpen();
        return speed;
    }

    @Override
    public DataBits getDatatBits() throws IOException {
        ensureOpen();
        return dataBits;
    }

    @Override
    public Set<FlowControl> getFlowControl() throws IOException {
        ensureOpen();
        return flowControl;
    }

    @Override
    public int getInBufferBytesCount() throws IOException {
        ensureOpen();
        return is.available();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        ensureOpen();
        return is;
    }

    @Override
    public int getInterByteReadTimeout() throws IOException {
        ensureOpen();
        return interByteReadTimeout;
    }

    @Override
    public int getOutBufferBytesCount() throws IOException {
        ensureOpen();
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        ensureOpen();
        return os;
    }

    @Override
    public int getOverallReadTimeout() throws IOException {
        ensureOpen();
        return overallReadTimeout;
    }

    @Override
    public int getOverallWriteTimeout() throws IOException {
        ensureOpen();
        return overallWriteTimeout;
    }

    @Override
    public Parity getParity() throws IOException {
        ensureOpen();
        return parity;
    }

    @Override
    public String getPortName() {
        return this.getClass().getCanonicalName();
    }

    @Override
    public StopBits getStopBits() throws IOException {
        ensureOpen();
        return stopBits;
    }

    @Override
    public char getXOFFChar() throws IOException {
        ensureOpen();
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public char getXONChar() throws IOException {
        ensureOpen();
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public boolean isCTS() throws IOException {
        ensureOpen();
        return false;
    }

    @Override
    public boolean isDCD() throws IOException {
        ensureOpen();
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public boolean isDSR() throws IOException {
        ensureOpen();
        throw new RuntimeException("Not Implemented");
    }

    protected void ensureOpen() throws IOException {
        if (!isOpen()) {
            throw new IOException(PORT_IS_CLOSED);
        }
    }

    @Override
    public boolean isRI() throws IOException {
        ensureOpen();
        throw new RuntimeException("Not Implemented");
    }

    public MockSerialPortSocket(MockSerialPortFactory factory, String portname) throws IOException {
        is = new MockInputStream();
        os = new MockOutputStream();
        this.factory = factory;
        this.portname = portname;
    }

    public MockSerialPortSocket(MockSerialPortFactory factory, String portname, Speed speed, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls)
            throws IOException {
        this(factory, portname);
        this.speed = speed;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;
        this.flowControl = flowControls;
    }

    @Override
    public void sendBreak(int duration) throws IOException {
        ensureOpen();
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void sendXOFF() throws IOException {
        ensureOpen();
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void sendXON() throws IOException {
        ensureOpen();
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void setSpeed(Speed speed) throws IOException {
        ensureOpen();
        this.speed = speed;
    }

    @Override
    public void setBreak(boolean value) throws IOException {
        ensureOpen();
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void setDataBits(DataBits dataBits) throws IOException {
        ensureOpen();
        this.dataBits = dataBits;
    }

    @Override
    public void setDTR(boolean value) throws IOException {
        ensureOpen();
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void setFlowControl(Set<FlowControl> flowControls) throws IOException {
        ensureOpen();
        this.flowControl = flowControls;
    }

    @Override
    public void setParity(Parity parity) throws IOException {
        ensureOpen();
        this.parity = parity;
    }

    @Override
    public void setRTS(boolean value) throws IOException {
        ensureOpen();
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void setStopBits(StopBits stopBits) throws IOException {
        ensureOpen();
        this.stopBits = stopBits;
    }

    @Override
    public void setTimeouts(int interByteReadTimeout, int overallReadTimeout, int overallWriteTimeout)
            throws IOException {
        ensureOpen();
        this.interByteReadTimeout = interByteReadTimeout;
        this.overallReadTimeout = overallReadTimeout;
        this.overallWriteTimeout = overallWriteTimeout;
    }

    @Override
    public void setXOFFChar(char c) throws IOException {
        ensureOpen();
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void setXONChar(char c) throws IOException {
        ensureOpen();
        throw new RuntimeException("Not Implemented");
    }

}
