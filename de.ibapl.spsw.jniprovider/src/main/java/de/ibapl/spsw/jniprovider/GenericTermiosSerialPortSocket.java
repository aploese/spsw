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
package de.ibapl.spsw.jniprovider;

import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;
import java.io.IOException;
import java.lang.ref.Cleaner;
import java.util.Set;

/**
 * JNI wrapper around the POSIX termios structure.
 *
 * Use serial_struct TIOCGSERIAL to get more infos?
 *
 * @author scream3r
 * @author Arne Plöse
 */
public class GenericTermiosSerialPortSocket extends AbstractSerialPortSocket<GenericTermiosSerialPortSocket> {

    private final static int INVALID_FD = -1;
    public final static Cleaner CLEANER = Cleaner.create();

    static class FdCleaner implements Runnable {

        private native void closeFds(int fd, int close_event_write_fd, int close_event_read_fd);

        int fd = INVALID_FD;
        int close_event_write_fd = INVALID_FD;
        int close_event_read_fd = INVALID_FD;

        @Override
        public void run() {
            closeFds(fd, close_event_write_fd, close_event_read_fd);
            /*
            if (fd != GenericTermiosSerialPortSocket.INVALID_FD) {
                try {
                    Unistd.close(fd);
                } catch (NativeErrorException ex) {
                    LOG.severe("can't clean fd");
                }
            }
            if (close_event_write_fd != PosixSerialPortSocket.INVALID_FD) {
                try {
                    Unistd.close(close_event_write_fd);
                } catch (NativeErrorException ex) {
                    LOG.severe("can't clean close_event_write_fd");
                }
            }
            if (close_event_read_fd != PosixSerialPortSocket.INVALID_FD) {
                try {
                    Unistd.close(close_event_read_fd);
                } catch (NativeErrorException ex) {
                    LOG.severe("can't clean close_event_read_fd");
                }
            }
             */
        }

    }

    /**
     * The file descriptor or handle for this Port
     */
    private volatile int fd = -1;
    /**
     * The close event file descriptor or handle proper multi threaded closing
     * for this Port
     */
    private volatile int closeEventReadFd = -1;
    private volatile int closeEventWriteFd = -1;

    private int outByteTime = -1;
    /**
     * used in native code
     */
    private int interByteReadTimeout = 100;
    private int pollReadTimeout = -1;
    private int pollWriteTimeout = -1;
    private FdCleaner fdCleaner = new FdCleaner();

    public GenericTermiosSerialPortSocket(String portName) throws IOException {
        super(portName);
        open(portName, 0);
    }

    public GenericTermiosSerialPortSocket(String portName, Speed speed, DataBits dataBits, StopBits stopBits, Parity parity,
            Set<FlowControl> flowControls) throws IOException {
        super(portName);
        open(speed, dataBits, stopBits, parity, flowControls);
    }

    @Override
    protected void open(String portName, int paramBitSet) throws IOException {
        open0(portName, paramBitSet);
        CLEANER.register(this, fdCleaner);
        fdCleaner.fd = this.fd;
        fdCleaner.close_event_read_fd = this.closeEventReadFd;
        fdCleaner.close_event_write_fd = this.closeEventWriteFd;
    }

    private native void open0(String portName, int paramBitSet) throws IOException;

    @Override
    protected void implCloseChannel() throws IOException {
        super.implCloseChannel();
        close0();
        fdCleaner.fd = INVALID_FD;
        fdCleaner.close_event_read_fd = INVALID_FD;
        fdCleaner.close_event_write_fd = INVALID_FD;
    }

    /**
     * Close port
     *
     * @throws java.io.IOException
     */
    private native void close0() throws IOException;

    @Override
    public native void drainOutputBuffer() throws IOException;

    @Override
    public int getInterByteReadTimeout() throws IOException {
        return interByteReadTimeout;
    }

    @Override
    public int getOverallReadTimeout() throws IOException {
        return pollReadTimeout == -1 ? 0 : pollReadTimeout;
    }

    @Override
    public int getOverallWriteTimeout() throws IOException {
        return pollWriteTimeout == -1 ? 0 : pollWriteTimeout;
    }

    public native boolean isDTR() throws IOException;

    public native boolean isRTS() throws IOException;

    @Override
    public native void sendXOFF() throws IOException;

    @Override
    public native void sendXON() throws IOException;

    @Override
    public void setTimeouts(int interByteReadTimeout, int overallReadTimeout, int overallWriteTimeout)
            throws IOException {
        this.interByteReadTimeout = interByteReadTimeout;
        this.pollReadTimeout = overallReadTimeout == 0 ? -1 : overallReadTimeout;
        this.pollWriteTimeout = overallWriteTimeout == 0 ? -1 : overallWriteTimeout;
    }

}
