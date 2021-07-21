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
package de.ibapl.spsw.ser2net;

import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.api.TimeoutIOException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.util.Set;
import javax.net.SocketFactory;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Accesses a remote serial device on a differrent machine over
 * <a href="http://ser2net.sourceforge.net/">ser2net</a> connection. Currently
 * set or change of port parameters, dending BREAK or acess or set of line
 * status (RTS/CTS...) is not supported.
 *
 * @author Arne Plöse
 *
 */
@ProviderType
public class Ser2NetProvider extends AbstractInterruptibleChannel implements SerialPortSocket {

    protected class InputStreamWrapper extends InputStream {

        private InputStreamWrapper() throws IOException {
            origin = dataSocket.getInputStream();
        }

        private final InputStream origin;

        @Override
        public void close() throws IOException {
            Ser2NetProvider.this.close();
        }

        @Override
        public int read() throws IOException {
            try {
                return origin.read();
            } catch (SocketTimeoutException e) {
                TimeoutIOException timeoutIOException = new TimeoutIOException(e.getMessage());
                timeoutIOException.bytesTransferred = e.bytesTransferred;
                timeoutIOException.initCause(e);
                throw timeoutIOException;
            }
        }

        @Override
        public int read(byte b[]) throws IOException {
            try {
                return origin.read(b);
            } catch (SocketTimeoutException e) {
                TimeoutIOException timeoutIOException = new TimeoutIOException(e.getMessage());
                timeoutIOException.bytesTransferred = e.bytesTransferred;
                timeoutIOException.initCause(e);
                throw timeoutIOException;
            }
        }

        @Override
        public int read(byte b[], int off, int len) throws IOException {
            try {
                return origin.read(b, off, len);
            } catch (SocketTimeoutException e) {
                TimeoutIOException timeoutIOException = new TimeoutIOException(e.getMessage());
                timeoutIOException.bytesTransferred = e.bytesTransferred;
                timeoutIOException.initCause(e);
                throw timeoutIOException;
            }
        }

        @Override
        public int available() throws IOException {
            return origin.available();
        }
    }

    protected class OutputStreamWrapper extends OutputStream {

        private OutputStreamWrapper() throws IOException {
            origin = dataSocket.getOutputStream();

        }

        private final OutputStream origin;

        @Override
        public void close() throws IOException {
            Ser2NetProvider.this.close();
        }

        @Override
        public void write(int b) throws IOException {
            origin.write(b);
        }

        @Override
        public void write(byte b[]) throws IOException {
            origin.write(b);
        }

        @Override
        public void write(byte b[], int off, int len) throws IOException {
            origin.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            origin.flush();
        }

    }

    private Socket dataSocket;
    private Socket controlSocket;
    private String host;
    private int dataPort;
    private int controlPort;
    private Speed speed;
    private Parity parity;
    private StopBits stopBits;
    private DataBits dataBits;
    private Set<FlowControl> flowControl;
    private InputStreamWrapper is;
    private OutputStreamWrapper os;
    private ByteChannel channel;

    public Ser2NetProvider(String host, int dataPort, int controlPort) throws IOException {
        this.host = host;
        this.dataPort = dataPort;
        this.controlPort = controlPort;
        dataSocket = SocketFactory.getDefault().createSocket(host, dataPort);
        if (controlPort != -1) {
            controlSocket = SocketFactory.getDefault().createSocket(host, controlPort);
        }
        channel = dataSocket.getChannel();
    }

    public Ser2NetProvider(String host, int dataPort) throws IOException {
        this(host, dataPort, -1);
    }

    public Ser2NetProvider(String host, int dataPort, Speed speed, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls)
            throws IOException {
        this(host, dataPort);
        this.speed = speed;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;
        this.flowControl = flowControls;
    }

    protected void ensureOpen() throws IOException {
        if (!isOpen()) {
            throw new IOException(PORT_IS_CLOSED);
        }
    }

    @Override
    public void drainOutputBuffer() throws IOException {
    }

    @Override
    public boolean isCTS() throws IOException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isDSR() throws IOException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isDCD() throws IOException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isRI() throws IOException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public synchronized InputStream getInputStream() throws IOException {
        ensureOpen();
        if (is == null) {
            is = new InputStreamWrapper();
        }
        return is;
    }

    @Override
    public synchronized OutputStream getOutputStream() throws IOException {
        ensureOpen();
        if (os == null) {
            os = new OutputStreamWrapper();
        }
        return os;
    }

    @Override
    public String getPortName() {
        return host + ":" + dataPort;
    }

    @Override
    protected synchronized void implCloseChannel() throws IOException {

        final Socket s = dataSocket;
        dataSocket = null;
        is = null;
        os = null;
        channel = null;

        s.close();
        if (controlSocket != null) {
            controlSocket.close();
        }
    }

    @Override
    public void setRTS(boolean value) throws IOException {
        // TODO Auto-generated method stub
        ensureOpen();
    }

    @Override
    public void setDTR(boolean value) throws IOException {
        // TODO Auto-generated method stub
        ensureOpen();
    }

    @Override
    public void setXONChar(char c) throws IOException {
        // TODO Auto-generated method stub
        ensureOpen();
    }

    @Override
    public void setXOFFChar(char c) throws IOException {
        // TODO Auto-generated method stub
        ensureOpen();
    }

    @Override
    public char getXONChar() throws IOException {
        // TODO Auto-generated method stub
        ensureOpen();
        return 0;
    }

    @Override
    public char getXOFFChar() throws IOException {
        // TODO Auto-generated method stub
        ensureOpen();
        return 0;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        ensureOpen();
        return channel.read(dst);
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        ensureOpen();
        return channel.write(src);
    }

    @Override
    public void sendBreak(int duration) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendXON() throws IOException {
        // TODO Auto-generated method stub
        ensureOpen();
    }

    @Override
    public void sendXOFF() throws IOException {
        // TODO Auto-generated method stub
        ensureOpen();
    }

    @Override
    public int getInBufferBytesCount() throws IOException {
        ensureOpen();
        return dataSocket.getInputStream().available();
    }

    @Override
    public int getOutBufferBytesCount() throws IOException {
        // TODO Auto-generated method stub
        ensureOpen();
        return 0;
    }

    @Override
    public void setBreak(boolean value) throws IOException {
        // TODO Auto-generated method stub
        ensureOpen();
    }

    @Override
    public void setFlowControl(Set<FlowControl> flowControls) throws IOException {
        ensureOpen();
        this.flowControl = flowControls;
    }

    @Override
    public void setSpeed(Speed speed) throws IOException {
        ensureOpen();
        this.speed = speed;
    }

    @Override
    public void setDataBits(DataBits dataBits) throws IOException {
        ensureOpen();
        this.dataBits = dataBits;
    }

    @Override
    public void setStopBits(StopBits stopBits) throws IOException {
        ensureOpen();
        this.stopBits = stopBits;
    }

    @Override
    public void setParity(Parity parity) throws IOException {
        ensureOpen();
        this.parity = parity;
    }

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
    public StopBits getStopBits() throws IOException {
        ensureOpen();
        return stopBits;
    }

    @Override
    public Parity getParity() throws IOException {
        ensureOpen();
        return parity;
    }

    @Override
    public Set<FlowControl> getFlowControl() throws IOException {
        ensureOpen();
        return flowControl;
    }

    @Override
    public int getOverallReadTimeout() throws IOException {
        ensureOpen();
        return dataSocket.getSoTimeout();
    }

    @Override
    public int getInterByteReadTimeout() throws IOException {
        // TODO Auto-generated method stub
        ensureOpen();
        return 0;
    }

    @Override
    public int getOverallWriteTimeout() throws IOException {
        // TODO Auto-generated method stub
        ensureOpen();
        return 0;
    }

    @Override
    public void setTimeouts(int interByteReadTimeout, int overallReadTimeout, int overallWriteTimeout)
            throws IOException {
        ensureOpen();
        dataSocket.setSoTimeout(overallReadTimeout);
        // TODO Output Timeout??
    }

}
