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
import java.util.List;
import java.util.Set;

/**
 * /**
 * JNI wrapper around the Windows DCB structure.
 *
 * @author scream3r
 * @author Arne Plöse
 */
public class GenericWinSerialPortSocket extends AbstractSerialPortSocket<GenericWinSerialPortSocket> {

    static native void getWindowsBasedPortNames(List<String> list);

    public final static Cleaner CLEANER = Cleaner.create();
    private final static long INVALID_FD = -1;

    static class FdCleaner implements Runnable {

        private native void closeFds(long fd);

        long fd = INVALID_FD;

        @Override
        public void run() {
            closeFds(fd);
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

    @Override
    protected void implCloseChannel() throws IOException {
        super.implCloseChannel();
        close0();
        fdCleaner.fd = INVALID_FD;
    }

    /**
     * Close port
     *
     * @throws java.io.IOException
     */
    protected native void close0() throws IOException;

    /**
     * The file descriptor or handle for this Port
     */
    private volatile long fd = -1;
    private FdCleaner fdCleaner = new FdCleaner();

    public GenericWinSerialPortSocket(String portName) throws IOException {
        super(portName);
        open(portName, 0);
    }

    public GenericWinSerialPortSocket(String portName, Speed speed, DataBits dataBits, StopBits stopBits, Parity parity,
            Set<FlowControl> flowControls) throws IOException {
        super(portName);
        open(speed, dataBits, stopBits, parity, flowControls);
    }

    @Override
    protected void open(String portName, int paramBitSet) throws IOException {
        open0(portName, paramBitSet);
        CLEANER.register(this, fdCleaner);
        fdCleaner.fd = this.fd;
    }

    private native void open0(String portName, int paramBitSet) throws IOException;

    @Override
    public void drainOutputBuffer() throws IOException {
        // no-op on overlapped...
    }

    @Override
    public native int getInterByteReadTimeout() throws IOException;

    @Override
    public native int getOverallReadTimeout() throws IOException;

    @Override
    public native int getOverallWriteTimeout() throws IOException;

    @Override
    public native void sendXOFF() throws IOException;

    @Override
    public native void sendXON() throws IOException;

    @Override
    public native void setTimeouts(int interbyteReadTimeout, int overallReadTimeout, int overallWriteTimeout)
            throws IOException;

}
