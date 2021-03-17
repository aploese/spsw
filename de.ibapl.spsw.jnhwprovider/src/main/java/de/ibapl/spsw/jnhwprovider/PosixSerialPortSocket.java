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

import de.ibapl.jnhw.common.exception.NativeErrorException;
import de.ibapl.jnhw.common.memory.AbstractNativeMemory;
import static de.ibapl.jnhw.common.memory.AbstractNativeMemory.MEM_UNINITIALIZED;
import de.ibapl.jnhw.common.memory.Memory32Heap;
import de.ibapl.jnhw.common.memory.layout.Alignment;
import de.ibapl.jnhw.common.references.IntRef;
import de.ibapl.jnhw.libloader.LoadState;
import de.ibapl.jnhw.linux.sys.Eventfd;
import static de.ibapl.jnhw.linux.sys.Eventfd.EFD_NONBLOCK;
import static de.ibapl.jnhw.linux.sys.Eventfd.eventfd;
import de.ibapl.jnhw.posix.Errno;
import static de.ibapl.jnhw.posix.Errno.EAGAIN;
import static de.ibapl.jnhw.posix.Errno.EBADF;
import de.ibapl.jnhw.posix.Fcntl;
import static de.ibapl.jnhw.posix.Fcntl.F_GETFL;
import static de.ibapl.jnhw.posix.Fcntl.F_SETFL;
import static de.ibapl.jnhw.posix.Fcntl.O_NONBLOCK;
import de.ibapl.jnhw.posix.Poll;
import static de.ibapl.jnhw.posix.Poll.POLLHUP;
import static de.ibapl.jnhw.posix.Poll.POLLIN;
import static de.ibapl.jnhw.posix.Poll.POLLNVAL;
import static de.ibapl.jnhw.posix.Poll.POLLOUT;
import de.ibapl.jnhw.posix.Poll.PollFds;
import static de.ibapl.jnhw.posix.Poll.poll;
import static de.ibapl.jnhw.posix.Termios.TCIOFLUSH;
import static de.ibapl.jnhw.posix.Termios.tcdrain;
import static de.ibapl.jnhw.posix.Termios.tcflush;
import de.ibapl.jnhw.posix.Time;
import de.ibapl.jnhw.posix.Unistd;
import static de.ibapl.jnhw.unix.sys.Ioctl.TIOCCBRK;
import static de.ibapl.jnhw.unix.sys.Ioctl.TIOCSBRK;
import static de.ibapl.jnhw.unix.sys.Ioctl.ioctl;
import de.ibapl.jnhw.util.posix.LibJnhwPosixLoader;
import de.ibapl.spsw.api.AsynchronousCancelException;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.api.TimeoutIOException;
import static de.ibapl.spsw.jnhwprovider.PosixConfiguration.INVALID_FD;
import de.ibapl.spsw.spi.SerialInputStream;
import de.ibapl.spsw.spi.SerialOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.lang.ref.Cleaner;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PosixSerialPortSocket extends StreamSerialPortSocket<PosixSerialPortSocket> {

    private final static int POLL_INFINITE_TIMEOUT = -1;

    public final static Cleaner CLEANER = Cleaner.create();

    /**
     * Cleanup whats left over if the port was not closed properly
     */
    static class FdCleaner implements Runnable {

        int fd = INVALID_FD;
        int cancel_read_event__write_fd = INVALID_FD;
        int cancel_read_event__read_fd = INVALID_FD;
        int cancel_write_event__write_fd = INVALID_FD;
        int cancel_write_event__read_fd = INVALID_FD;

        @Override
        public void run() {
            if (cancel_read_event__write_fd != INVALID_FD) {
                ByteBuffer evt_buff = ByteBuffer.allocateDirect(8);
                evt_buff.putLong(1);
                evt_buff.flip();
                try {
                    //Eventfd.eventfd_write(cancel_read_event__write_fd, 0x0000000000000001L);
                    Unistd.write(cancel_read_event__write_fd, evt_buff);
                    Unistd.usleep(1000); // 1ms
                } catch (NativeErrorException nee) {
                    LOG.log(Level.SEVERE, "Error writing to cancel_read_event__write_fd error: " + Errno.getErrnoSymbol(nee.errno), nee);
                }
            }
            if (cancel_write_event__write_fd != INVALID_FD) {
                ByteBuffer evt_buff = ByteBuffer.allocateDirect(8);
                evt_buff.putLong(1);
                evt_buff.flip();
                try {
                    //Eventfd.eventfd_write(cancel_write_event__write_fd, 0x0000000000000001L);
                    Unistd.write(cancel_write_event__write_fd, evt_buff);
                    Unistd.usleep(1000); // 1ms
                } catch (NativeErrorException nee) {
                    LOG.log(Level.SEVERE, "Error writing to cancel_write_event__write_fd error: " + Errno.getErrnoSymbol(nee.errno), nee);
                }
            }

            if (fd != INVALID_FD) {
                try {
                    Unistd.close(fd);
                    fd = INVALID_FD;
                } catch (NativeErrorException ex) {
                    LOG.severe("can't clean fd");
                }
            }

            if (cancel_read_event__write_fd != INVALID_FD) {
                try {
                    Unistd.close(cancel_read_event__write_fd);
                } catch (NativeErrorException ex) {
                    LOG.severe("can't clean cancel_read_event__write_fd");
                }
            }
            if ((cancel_read_event__read_fd != INVALID_FD) && (cancel_read_event__read_fd != cancel_read_event__write_fd)) {
                try {
                    Unistd.close(cancel_read_event__read_fd);
                } catch (NativeErrorException ex) {
                    LOG.severe("can't clean cancel_read_event__read_fd");
                }
            }
            cancel_read_event__read_fd = INVALID_FD;
            cancel_read_event__write_fd = INVALID_FD;

            if (cancel_write_event__write_fd != INVALID_FD) {
                try {
                    Unistd.close(cancel_write_event__write_fd);
                } catch (NativeErrorException ex) {
                    LOG.severe("can't clean cancel_write_event__write_fd");
                }
            }
            if ((cancel_write_event__read_fd != INVALID_FD) && (cancel_write_event__read_fd != cancel_write_event__write_fd)) {
                try {
                    Unistd.close(cancel_write_event__read_fd);
                } catch (NativeErrorException ex) {
                    LOG.severe("can't clean close_event_read_fd");
                }
            }
            cancel_write_event__read_fd = INVALID_FD;
            cancel_write_event__write_fd = INVALID_FD;

        }

    }

    private final static Logger LOG = Logger.getLogger("d.i.s.jnhwprovider.PosixPosixSerialPortSocket");

    private static final int POLL_TIMEOUT_INFINITE = -1;
    private static final int PORT_FD_IDX = 0;
    private static final int CANCEL_FD_IDX = 1;
    private volatile int fd = INVALID_FD;
    private final PosixConfiguration posixConfiguration;

    private volatile int cancel_read_event__write_fd = INVALID_FD;
    private volatile int cancel_read_event__read_fd = INVALID_FD;
    private volatile int cancel_write_event__write_fd = INVALID_FD;
    private volatile int cancel_write_event__read_fd = INVALID_FD;

    private int interByteReadTimeout = 100;
    private int pollReadTimeout = -1;
    private int pollWriteTimeout = -1;
    private final FdCleaner fdCleaner = new FdCleaner();
    private final Object readLock = new Object();
    private final Object writeLock = new Object();
    /**
     * Cached pollfds to avoid getting native mem for each read/write operation
     */
    private final Memory32Heap nativeMemoryBlock = new Memory32Heap(null, 0L, 1024, AbstractNativeMemory.SET_MEM_TO_0);
    private final PollFds readPollFds;
    private final PollFds writePollFds;
    private final Time.Timespec readTimeout;
    private final Time.Timespec writeTimeout;
    private final Time.Timespec currentReadTime;
    private final Time.Timespec currentWriteTime;

    {
        long offset = 0;
        readPollFds = new PollFds(nativeMemoryBlock, offset, 2);
        offset = nativeMemoryBlock.nextOffset(readPollFds, Poll.PollFd.LAYOUT.alignment);
        writePollFds = new PollFds(nativeMemoryBlock, offset, 2);
        offset = nativeMemoryBlock.nextOffset(writePollFds, Poll.PollFd.LAYOUT.alignment);
        readTimeout = new Time.Timespec(nativeMemoryBlock, offset, MEM_UNINITIALIZED);
        offset = nativeMemoryBlock.nextOffset(readTimeout, Poll.PollFd.LAYOUT.alignment);
        writeTimeout = new Time.Timespec(nativeMemoryBlock, offset, MEM_UNINITIALIZED);
        offset = nativeMemoryBlock.nextOffset(writeTimeout, Poll.PollFd.LAYOUT.alignment);
        currentReadTime = new Time.Timespec(nativeMemoryBlock, offset, MEM_UNINITIALIZED);
        offset = nativeMemoryBlock.nextOffset(currentReadTime, Poll.PollFd.LAYOUT.alignment);
        currentWriteTime = new Time.Timespec(nativeMemoryBlock, offset, MEM_UNINITIALIZED);
        offset = nativeMemoryBlock.nextOffset(currentWriteTime, Alignment.AT_1);

    }

    private static final boolean JNHW_HAVE_SYS_EVENTFD_H = Eventfd.HAVE_SYS_EVENTFD_H;

    PosixSerialPortSocket(String portName) throws IOException {
        this(portName, null, null, null, null, null);
    }

    PosixSerialPortSocket(String portName, Speed speed, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls) throws IOException {
        super(portName);
        posixConfiguration = new PosixConfiguration(portName);
        //Check that the libs are loaded
        if (LoadState.SUCCESS != LibJnhwPosixLoader.touch()) {
            throw new RuntimeException("Could not load native lib: ", LibJnhwPosixLoader.getLoadResult().loadError);
        }
        open(speed, dataBits, stopBits, parity, flowControls);
    }

    @Override
    protected void implCloseChannel() throws IOException {
        super.implCloseChannel();
        if (fd != INVALID_FD) {
            // Mark port as closed...
            int tempFd = fd;
            fd = INVALID_FD;
            posixConfiguration.setFd(fd);
            ByteBuffer evt_buff = ByteBuffer.allocateDirect(8);

            evt_buff.putLong(1);
            evt_buff.flip();
            try {
                Unistd.write(cancel_write_event__write_fd, evt_buff);
            } catch (NativeErrorException nee) {
                LOG.log(Level.SEVERE, "Error writing to cancel_write_event__write_fd error: " + Errno.getErrnoSymbol(nee.errno), nee);
            }
            evt_buff.clear();

            evt_buff.putLong(1);
            evt_buff.flip();
            try {
                Unistd.write(cancel_read_event__write_fd, evt_buff);
            } catch (NativeErrorException nee) {
                LOG.log(Level.SEVERE, "Error writing to cancel_read_event__write_fd error: " + Errno.getErrnoSymbol(nee.errno), nee);
            }

            try {
                tcflush(tempFd, TCIOFLUSH);
            } catch (NativeErrorException nee) {
                LOG.log(Level.SEVERE, "Native Error flushing " + Errno.getErrnoSymbol(nee.errno), nee);
            } catch (Throwable t) {
                LOG.log(Level.SEVERE, "unknown Error flushing ", t);
            }

            try {
                Unistd.close(tempFd);
                fdCleaner.fd = INVALID_FD;
                //leave the close_event_write_fd and close_event_read_fd open for now. So poll can digest the events...
                //closing close_event_write_fd and close_event_read_fd will be don by fdCleaner
                cancel_read_event__read_fd = INVALID_FD;
                cancel_read_event__write_fd = INVALID_FD;
                cancel_write_event__read_fd = INVALID_FD;
                cancel_write_event__write_fd = INVALID_FD;
            } catch (NativeErrorException nee) {
                //TODO after poll POLLHUP ???
                // fd = tempFd;
                //posixConfiguration.setFd(fd);
                LOG.log(Level.SEVERE, "unknown Error during closing " + Errno.getErrnoSymbol(nee.errno), nee);
            }
        }
    }

    @Override
    public DataBits getDatatBits() throws IOException {
        return posixConfiguration.getDatatBits();
    }

    @Override
    public Set<FlowControl> getFlowControl() throws IOException {
        return posixConfiguration.getFlowControl();
    }

    @Override
    public int getInBufferBytesCount() throws IOException {
        return posixConfiguration.getInBufferBytesCount();
    }

    @Override
    public int getInterByteReadTimeout() throws IOException {
        return interByteReadTimeout;
    }

    @Override
    public int getOutBufferBytesCount() throws IOException {
        return posixConfiguration.getOutBufferBytesCount();
    }

    @Override
    public int getOverallReadTimeout() throws IOException {
        return pollReadTimeout == POLL_INFINITE_TIMEOUT ? INFINITE_TIMEOUT : pollReadTimeout;
    }

    @Override
    public int getOverallWriteTimeout() throws IOException {
        return pollWriteTimeout == POLL_INFINITE_TIMEOUT ? INFINITE_TIMEOUT : pollWriteTimeout;
    }

    @Override
    public Parity getParity() throws IOException {
        return posixConfiguration.getParity();
    }

    @Override
    public Speed getSpeed() throws IOException {
        return posixConfiguration.getSpeed();
    }

    @Override
    public StopBits getStopBits() throws IOException {
        return posixConfiguration.getStopBits();
    }

    @Override
    public char getXOFFChar() throws IOException {
        return posixConfiguration.getXOFFChar();
    }

    @Override
    public char getXONChar() throws IOException {
        return posixConfiguration.getXONChar();
    }

    @Override
    public boolean isCTS() throws IOException {
        return posixConfiguration.isCTS();
    }

    @Override
    public boolean isDCD() throws IOException {
        return posixConfiguration.isDCD();
    }

    @Override
    public boolean isDSR() throws IOException {
        return posixConfiguration.isDSR();
    }

    private boolean isFdValid() {
        try {
            return Fcntl.fcntl(fd, F_GETFL) != INVALID_FD;
        } catch (NativeErrorException nee) {
            if (nee.errno == EBADF) {
                LOG.log(Level.SEVERE, "Port {0} has invalid file descriptor", portName);
                return false;
            } else {
                LOG.log(Level.SEVERE, "file descriptor of port " + portName + " not valid unknown Native exception " + Errno.getErrnoSymbol(nee.errno), nee);
                return false;
            }
        }
    }

    @Override
    public boolean isRI() throws IOException {
        return posixConfiguration.isRI();
    }

    private synchronized void open(Speed speed, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls) throws IOException {

        fd = posixConfiguration.open(speed, dataBits, stopBits, parity, flowControls);

        // on linux to avoid read/close problem maybe this helps?
        try {
            if (JNHW_HAVE_SYS_EVENTFD_H) {
                //Create EventFD
                cancel_read_event__read_fd = eventfd(0, EFD_NONBLOCK);// counter is zero so nothing to read is available
                cancel_read_event__write_fd = cancel_read_event__read_fd;
                cancel_write_event__read_fd = eventfd(0, EFD_NONBLOCK);// counter is zero so nothing to read is available
                cancel_write_event__write_fd = cancel_write_event__read_fd;
            } else {
                //Create pipe
                IntRef read_fd = new IntRef();
                IntRef write_fd = new IntRef();
                //read
                Unistd.pipe(read_fd, write_fd);
                cancel_read_event__read_fd = read_fd.value;
                cancel_read_event__write_fd = write_fd.value;
                Fcntl.fcntl(cancel_read_event__read_fd, F_SETFL, O_NONBLOCK);
                Fcntl.fcntl(cancel_read_event__write_fd, F_SETFL, O_NONBLOCK);
                //write
                Unistd.pipe(read_fd, write_fd);
                cancel_write_event__read_fd = read_fd.value;
                cancel_write_event__write_fd = write_fd.value;
                Fcntl.fcntl(cancel_write_event__read_fd, F_SETFL, O_NONBLOCK);
                Fcntl.fcntl(cancel_write_event__write_fd, F_SETFL, O_NONBLOCK);
            }
        } catch (NativeErrorException nee) {
            try {
                Unistd.close(fd);
                fd = INVALID_FD;
                posixConfiguration.setFd(fd);
            } catch (NativeErrorException nee1) {
            }
            throw new IOException("Can't create close_event_fd");
        }
        fdCleaner.fd = fd;
        fdCleaner.cancel_read_event__read_fd = cancel_read_event__read_fd;
        fdCleaner.cancel_read_event__write_fd = cancel_read_event__write_fd;
        fdCleaner.cancel_write_event__read_fd = cancel_write_event__read_fd;
        fdCleaner.cancel_write_event__write_fd = cancel_write_event__write_fd;
        CLEANER.register(this, fdCleaner);
        writePollFds.get(PORT_FD_IDX).fd(fd);
        writePollFds.get(PORT_FD_IDX).events(POLLOUT);
        writePollFds.get(CANCEL_FD_IDX).fd(cancel_write_event__read_fd);
        writePollFds.get(CANCEL_FD_IDX).events(POLLIN);
        readPollFds.get(PORT_FD_IDX).fd(fd);
        readPollFds.get(PORT_FD_IDX).events(POLLIN);
        readPollFds.get(CANCEL_FD_IDX).fd(cancel_read_event__read_fd);
        readPollFds.get(CANCEL_FD_IDX).events(POLLIN);
    }

    @Override
    public void sendBreak(int duration) throws IOException {
        synchronized (writeLock) {
            if (duration <= 0) {
                throw new IllegalArgumentException("sendBreak duration must be greater than 0)");
            }
            //make this blocking IO interruptable
            boolean completed = false;
            try {
                begin();
                try {
                    ioctl(fd, TIOCSBRK);
                } catch (NativeErrorException nee) {
                    completed = true;
                    throw new IOException(formatMsg(nee, "Can't set Break "));
                }
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException ie) {
                    try {
                        ioctl(fd, TIOCCBRK);
                    } catch (NativeErrorException nee) {
                        completed = true;
                        throw new IOException(formatMsg(nee, "Can't clear Break after aborted wait"), ie);
                    }
                    completed = true;
                    throw new RuntimeException("Wait interrupted", ie);
                }
                try {
                    ioctl(fd, TIOCCBRK);
                } catch (NativeErrorException nee) {
                    completed = true;
                    throw new IOException(formatMsg(nee, "Can't clear Break "));
                }
                completed = true;
            } finally {
                end(completed);
            }
        }
    }

    @Override
    public void sendXOFF() throws IOException {
        throw new IllegalArgumentException("sendXOFF not implemented yet");
    }

    @Override
    public void sendXON() throws IOException {
        throw new IllegalArgumentException("sendXON not implemented yet");
    }

    @Override
    public void setBreak(boolean enabled) throws IOException {
        synchronized (writeLock) {
            int arg;
            if (enabled) {
                arg = TIOCSBRK;
            } else {
                arg = TIOCCBRK;
            }
            try {
                ioctl(fd, arg);
            } catch (NativeErrorException nee) {
                throw new IOException(formatMsg(nee, "Can't set Break "));
            }
        }
    }

    @Override
    public void setDataBits(DataBits dataBits) throws IOException {
        posixConfiguration.setParams(null, dataBits, null, null, null);
    }

    @Override
    public void setDTR(boolean enabled) throws IOException {
        posixConfiguration.setDTR(enabled);
    }

    @Override
    public void setFlowControl(Set<FlowControl> flowControls) throws IOException {
        posixConfiguration.setParams(null, null, null, null, flowControls);
    }

    @Override
    public void setParity(Parity parity) throws IOException {
        posixConfiguration.setParams(null, null, null, parity, null);
    }

    @Override
    public void setRTS(boolean enabled) throws IOException {
        posixConfiguration.setRTS(enabled);
    }

    @Override
    public void setSpeed(Speed speed) throws IOException {
        posixConfiguration.setParams(speed, null, null, null, null);
    }

    @Override
    public void setStopBits(StopBits stopBits) throws IOException {
        posixConfiguration.setParams(null, null, stopBits, null, null);
    }

    @Override
    public void setTimeouts(int interByteReadTimeout, int overallReadTimeout, int overallWriteTimeout)
            throws IOException {
        if (overallWriteTimeout < 0) {
            throw new IllegalArgumentException("setTimeouts: overallWriteTimeout must >= 0");
        }

        if (overallReadTimeout < 0) {
            throw new IllegalArgumentException("setTimeouts: overallReadTimeout must >= 0");
        }

        if (interByteReadTimeout < 0) {
            throw new IllegalArgumentException("setReadTimeouts: interByteReadTimeout must >= 0");
        }

        this.interByteReadTimeout = interByteReadTimeout;
        this.pollReadTimeout = overallReadTimeout == INFINITE_TIMEOUT ? POLL_INFINITE_TIMEOUT : overallReadTimeout;
        this.pollWriteTimeout = overallWriteTimeout == INFINITE_TIMEOUT ? POLL_INFINITE_TIMEOUT : overallWriteTimeout;
    }

    @Override
    public void setXOFFChar(char c) throws IOException {
        posixConfiguration.setXOFFChar(c);
    }

    @Override
    public void setXONChar(char c) throws IOException {
        posixConfiguration.setXONChar(c);
    }

    public String termiosToString() throws IOException {
        return posixConfiguration.termiosToString();
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        synchronized (writeLock) {
            if (!src.hasRemaining()) {
                return 0;
            }

            int written;

            try {
                written = Unistd.write(fd, src);
                if (!src.hasRemaining()) {
                    // all was written
                    return written;
                }
            } catch (NativeErrorException nee) {
                if (nee.errno == EAGAIN) {
                    written = 0;
                } else if (fd == INVALID_FD) {
                    throw new AsynchronousCloseException();
                } else if (nee.errno == EBADF) {
                    throw new InterruptedIOException(PORT_FD_INVALID);
                } else {
                    throw new InterruptedIOException(formatMsg(nee, "unknown port error write "));
                }
            }
            //calc endTime only if write all to buff failed.
            final Time.Timespec endTime = pollWriteTimeout != POLL_INFINITE_TIMEOUT ? writeTimeout : null;
            //endTime holds the now the start time, the real end time will be calculated if needed
            try {
                if (pollWriteTimeout != POLL_INFINITE_TIMEOUT) {
                    Time.clock_gettime(Time.CLOCK_MONOTONIC, endTime);
                    endTime.tv_sec(endTime.tv_sec() + pollWriteTimeout / 1000); //full seconds
                    endTime.tv_nsec(endTime.tv_nsec() + (pollWriteTimeout % 1000) * 1000000); // reminder goes to nanos
                    if (endTime.tv_nsec() > 1000000000) {
                        //Overflow occured
                        endTime.tv_sec(endTime.tv_sec() + 1);
                        endTime.tv_nsec(endTime.tv_nsec() - 1000000000);
                    }
                }
            } catch (NativeErrorException ex) {
                throw new RuntimeException(ex);
            }

            //make this blocking IO interruptable
            boolean completed = false;
            try {
                begin();

                int offset = written;
                // calculate the endtime...
                do {
                    try {
                        int poll_result;

                        if (pollWriteTimeout == POLL_INFINITE_TIMEOUT) {
                            poll_result = poll(writePollFds, POLL_TIMEOUT_INFINITE);
                        } else {
                            Time.clock_gettime(Time.CLOCK_MONOTONIC, currentWriteTime);
                            final int remainingTimeOut = (int) (endTime.tv_sec() - currentWriteTime.tv_sec()) * 1000 + (int) ((endTime.tv_nsec() - currentWriteTime.tv_nsec()) / 1000000L);
                            if (remainingTimeOut < 0) {
                                throw new TimeoutIOException();
                            }
                            poll_result = poll(writePollFds, remainingTimeOut);
                        }

                        if (poll_result == 0) {
                            // Timeout occured
                            TimeoutIOException tioe = new TimeoutIOException();
                            tioe.bytesTransferred = written;
                            completed = true;
                            throw tioe;
                        } else {
                            if (writePollFds.get(CANCEL_FD_IDX).revents() == POLLIN) {
                                // we can read from close_event_fd => port is closing
                                completed = true;
                                if (fd == INVALID_FD) {
                                    throw new AsynchronousCloseException();
                                } else {
                                    throw new AsynchronousCancelException();
                                }
                            } else if (writePollFds.get(PORT_FD_IDX).revents() == POLLOUT) {
                                // Happy path all is right...
                            } else if ((writePollFds.get(PORT_FD_IDX).revents() & POLLHUP) == POLLHUP) {
                                //i.e. happens when the USB to serial adapter is removed
                                completed = true;
                                throw new InterruptedIOException(PORT_FD_INVALID);
                            } else if ((writePollFds.get(PORT_FD_IDX).revents() & POLLNVAL) == POLLNVAL) {
                                completed = true;
                                if (fd == INVALID_FD) {
                                    throw new AsynchronousCloseException();
                                } else {
                                    throw new AsynchronousCancelException();
                                }
                            } else {
                                InterruptedIOException iioe = new InterruptedIOException(
                                        "poll returned with poll event write ");
                                iioe.bytesTransferred = (int) offset;
                                completed = true;
                                throw iioe;
                            }
                        }
                    } catch (NativeErrorException nee) {
                        InterruptedIOException iioe = new InterruptedIOException(formatMsg(nee, "poll timeout with error in write!"));
                        iioe.initCause(nee);
                        iioe.bytesTransferred = (int) offset;
                        completed = true;
                        throw iioe;
                    }

                    try {
                        offset += Unistd.write(fd, src);
                    } catch (NativeErrorException nee) {
                        if (fd == INVALID_FD) {
                            completed = true;
                            if (fd == INVALID_FD) {
                                throw new AsynchronousCloseException();
                            } else {
                                throw new AsynchronousCancelException();
                            }
                        } else if (nee.errno == EBADF) {
                            completed = true;
                            throw new InterruptedIOException(PORT_FD_INVALID);
                        } else {
                            InterruptedIOException iioe = new InterruptedIOException(formatMsg(nee, "error during Unistd.write"));
                            iioe.bytesTransferred = (int) offset;
                            completed = true;
                            throw iioe;
                        }
                    }

                } while (src.hasRemaining());
                completed = true;
                return (int) offset;
            } finally {
                end(completed);
            }
        }
    }

    //TODO move to Interface?
    public int write(byte b) throws IOException {
        synchronized (writeLock) {

            try {
                final int written = Unistd.write(fd, b);
                if (written == 1) {
                    return written;
                }
            } catch (NativeErrorException nee) {
                if (nee.errno == EAGAIN) {
                } else if (fd == INVALID_FD) {
                    throw new AsynchronousCloseException();
                } else if (nee.errno == EBADF) {
                    throw new InterruptedIOException(PORT_FD_INVALID);
                } else {
                    throw new InterruptedIOException(formatMsg(nee, "unknown port error write "));
                }
            }

            //make this blocking IO interruptable
            boolean completed = false;
            try {
                begin();

                try {
                    int poll_result;

                    if (pollWriteTimeout == POLL_INFINITE_TIMEOUT) {
                        poll_result = poll(writePollFds, POLL_TIMEOUT_INFINITE);
                    } else {
                        poll_result = poll(writePollFds, pollWriteTimeout);
                    }

                    if (poll_result == 0) {
                        // Timeout occured
                        TimeoutIOException tioe = new TimeoutIOException();
                        tioe.bytesTransferred = 0;
                        completed = true;
                        throw tioe;
                    } else {
                        if (writePollFds.get(CANCEL_FD_IDX).revents() == POLLIN) {
                            // we can read from close_event_fd => port is closing
                            completed = true;
                            if (fd == INVALID_FD) {
                                throw new AsynchronousCloseException();
                            } else {
                                throw new AsynchronousCancelException();
                            }
                        } else if (writePollFds.get(PORT_FD_IDX).revents() == POLLOUT) {
                            // Happy path all is right...
                        } else if ((writePollFds.get(PORT_FD_IDX).revents() & POLLHUP) == POLLHUP) {
                            //i.e. happens when the USB to serial adapter is removed
                            completed = true;
                            throw new InterruptedIOException(PORT_FD_INVALID);
                        } else if ((writePollFds.get(PORT_FD_IDX).revents() & POLLNVAL) == POLLNVAL) {
                            completed = true;
                            if (fd == INVALID_FD) {
                                throw new AsynchronousCloseException();
                            } else {
                                throw new AsynchronousCancelException();
                            }
                        } else {
                            InterruptedIOException iioe = new InterruptedIOException(
                                    "poll returned with poll event write ");
                            iioe.bytesTransferred = 0;
                            completed = true;
                            throw iioe;
                        }
                    }
                } catch (NativeErrorException nee) {
                    InterruptedIOException iioe = new InterruptedIOException(formatMsg(nee, "poll timeout with error in write!"));
                    iioe.initCause(nee);
                    iioe.bytesTransferred = 0;
                    completed = true;
                    throw iioe;
                }

                try {
                    final int written = Unistd.write(fd, b);
                    if (written == 1) {
                        completed = true;
                        return written;
                    } else {
                        completed = true;
                        throw new RuntimeException("Should never ever happen!");
                    }
                } catch (NativeErrorException nee) {
                    if (fd == INVALID_FD) {
                        completed = true;
                        if (fd == INVALID_FD) {
                            throw new AsynchronousCloseException();
                        } else {
                            throw new AsynchronousCancelException();
                        }
                    } else if (nee.errno == EBADF) {
                        completed = true;
                        throw new InterruptedIOException(PORT_FD_INVALID);
                    } else {
                        InterruptedIOException iioe = new InterruptedIOException(formatMsg(nee, "error during Unistd.write"));
                        iioe.bytesTransferred = 0;
                        completed = true;
                        throw iioe;
                    }
                }

            } finally {
                end(completed);
            }
        }
    }

    private String formatMsg(NativeErrorException nee, String formatString, Object... args) {
        if (fd == INVALID_FD) {
            return PORT_IS_CLOSED;
        } else if (isFdValid()) {
            return String.format("Native port error on %s, \"%s\" %s", portName, Errno.getErrnoSymbol(nee.errno),
                    String.format(formatString, args));
        } else {
            return PORT_FD_INVALID;
        }
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        synchronized (readLock) {
            if (!dst.hasRemaining()) {
                //nothing to read
                return 0;
            }

            int nread;
            try {
                //non blocking read
                nread = Unistd.read(fd, dst);
                if (!dst.hasRemaining()) {
                    return nread;
                }
            } catch (NativeErrorException nee) {
                if (fd == INVALID_FD) {
                    throw new AsynchronousCloseException();
                } else if (nee.errno == EAGAIN) {
                    nread = 0;
                } else if (nee.errno == EBADF) {
                    throw new InterruptedIOException(PORT_FD_INVALID);
                } else {
                    throw new InterruptedIOException(
                            "read: read error during first invocation of read() " + Errno.getErrnoSymbol(nee.errno));
                }
            }
            //read from buffer did not read all, so the overall time out may be needed
            final Time.Timespec endTime = pollReadTimeout != POLL_INFINITE_TIMEOUT ? readTimeout : null;
            try {
                if (pollReadTimeout != POLL_INFINITE_TIMEOUT) {
                    Time.clock_gettime(Time.CLOCK_MONOTONIC, endTime);
                    endTime.tv_sec(endTime.tv_sec() + pollReadTimeout / 1000); //full seconds
                    endTime.tv_nsec(endTime.tv_nsec() + (pollReadTimeout % 1000) * 1_000_000); // reminder goes to nanos
                    if (endTime.tv_nsec() > 1_000_000_000) {
                        //Overflow occured
                        endTime.tv_sec(endTime.tv_sec() + 1);
                        endTime.tv_nsec(endTime.tv_nsec() - 1_000_000_000);
                    }
                }
            } catch (NativeErrorException ex) {
                throw new RuntimeException(ex);
            }
            //make this blocking IO interruptable
            boolean completed = false;
            try {
                begin();
                // TODO honor overall read timeout

                if (nread == 0) {
                    // Nothing read yet

                    try {

                        final int poll_result = poll(readPollFds, pollReadTimeout);

                        if (poll_result == 0) {
                            // Timeout
                            completed = true;
                            throw new TimeoutIOException();
                        } else {
                            if (readPollFds.get(CANCEL_FD_IDX).revents() == POLLIN) {
                                // we can read from close_event_fd => port is closing
                                completed = true;
                                if (fd == INVALID_FD) {
                                    throw new AsynchronousCloseException();
                                } else {
                                    throw new AsynchronousCancelException();
                                }
                            } else if (readPollFds.get(PORT_FD_IDX).revents() == POLLIN) {
                                // Happy path just check if its the right event...
                                try {
                                    nread = Unistd.read(fd, dst);
                                    if (!dst.hasRemaining()) {
                                        completed = true;
                                        return nread;
                                    }
                                } catch (NativeErrorException nee) {
                                    if (fd == INVALID_FD) {
                                        completed = true;
                                        if (fd == INVALID_FD) {
                                            throw new AsynchronousCloseException();
                                        } else {
                                            throw new AsynchronousCancelException();
                                        }
                                    } else if (nee.errno == EBADF) {
                                        completed = true;
                                        throw new InterruptedIOException(PORT_FD_INVALID);
                                    } else {
                                        completed = true;
                                        throw new IOException("Readed " + nread + " bytes.  Unknown Error: " + Errno.getErrnoSymbol(nee.errno));
                                    }
                                }
                            } else if ((readPollFds.get(PORT_FD_IDX).revents() & POLLHUP) == POLLHUP) {
                                //i.e. happens when the USB to serial adapter is removed
                                completed = true;
                                throw new InterruptedIOException(PORT_FD_INVALID);
                            } else if ((readPollFds.get(PORT_FD_IDX).revents() & POLLNVAL) == POLLNVAL) {
                                completed = true;
                                if (fd == INVALID_FD) {
                                    throw new AsynchronousCloseException();
                                } else {
                                    throw new AsynchronousCancelException();
                                }
                            } else {
                                completed = true;
                                throw new InterruptedIOException("read poll: received poll event fds:\n" + readPollFds.toString());
                            }
                        }
                    } catch (NativeErrorException nee) {
                        completed = true;
                        throw new InterruptedIOException("read poll: Error during poll errno: " + Errno.getErrnoSymbol(nee.errno));
                    }
                }

                int overallRead = nread;

                // Loop over poll and read to aquire as much bytes as possible either
                // a poll timeout, a full read buffer or an error
                // breaks the loop
                do {
                    try {
                        final int poll_result;
                        if (pollReadTimeout == POLL_TIMEOUT_INFINITE) {
                            poll_result = poll(readPollFds, interByteReadTimeout);
                        } else {
                            Time.clock_gettime(Time.CLOCK_MONOTONIC, currentReadTime);
                            final int remainingTimeOut = (int) (endTime.tv_sec() - currentReadTime.tv_sec()) * 1000 + (int) ((endTime.tv_nsec() - currentReadTime.tv_nsec()) / 1_000_000L);
                            if (remainingTimeOut < 0) {
                                //interbyte timeout or overalll timeout, something was read - do return whats read
                                completed = true;
                                return overallRead; // TODO overflow???
                            }
                            poll_result = poll(readPollFds, interByteReadTimeout < remainingTimeOut ? interByteReadTimeout : remainingTimeOut);
                        }

                        if (poll_result == 0) {
                            // This is the interbyte timeout or the overall timeout with read bytes - We are done
                            completed = true;
                            return overallRead; // TODO overflow???
                        } else {
                            if (readPollFds.get(CANCEL_FD_IDX).revents() == POLLIN) {
                                // we can read from close_event_fd => port is closing
                                completed = true;
                                return -1;
                            } else if (readPollFds.get(PORT_FD_IDX).revents() == POLLIN) {
                                // Happy path
                            } else if ((readPollFds.get(PORT_FD_IDX).revents() & POLLHUP) == POLLHUP) {
                                //i.e. happens when the USB to serial adapter is removed
                                completed = true;
                                throw new InterruptedIOException(PORT_FD_INVALID);
                            } else if ((readPollFds.get(PORT_FD_IDX).revents() & POLLNVAL) == POLLNVAL) {
                                completed = true;
                                if (fd == INVALID_FD) {
                                    throw new AsynchronousCloseException();
                                } else {
                                    throw new AsynchronousCancelException();
                                }
                            } else {
                                completed = true;
                                throw new InterruptedIOException("read poll: received poll event fds:\n" + readPollFds.toString());
                            }
                        }
                    } catch (NativeErrorException nee) {
                        completed = true;
                        throw new InterruptedIOException("read poll: Error during poll " + Errno.getErrnoSymbol(nee.errno));
                    }
                    // OK No timeout and no error, we should read at least one byte without
                    // blocking.
                    try {
                        overallRead += Unistd.read(fd, dst);
                    } catch (NativeErrorException nee) {
                        if (fd == INVALID_FD) {
                            completed = true;
                            if (fd == INVALID_FD) {
                                throw new AsynchronousCloseException();
                            } else {
                                throw new AsynchronousCancelException();
                            }
                        } else if (nee.errno == EBADF) {
                            completed = true;
                            throw new InterruptedIOException(PORT_FD_INVALID);
                        } else {
                            completed = true;
                            throw new IOException("Readed " + overallRead + " bytes.  Unknown Error: " + Errno.getErrnoSymbol(nee.errno));
                        }
                    }
                } while (dst.hasRemaining());
                // We reached this, because the read buffer is full.
                completed = true;
                return overallRead;
            } catch (IOException ioe) {
                //In the case of an interruption we won't see this exception so log it here.
                LOG.log(Level.SEVERE, "IO ex for: " + portName, ioe);
                throw ioe;
            } finally {
                end(completed);
            }
        }
    }

    public int read() throws IOException {
        synchronized (readLock) {

            try {
                //non blocking read
                final short nread = Unistd.read(fd);

                if (Unistd.jnhwIsSingeByteRead(nread)) {
                    return Unistd.jnhwSingeByteReadToInt(nread);
                }
            } catch (NativeErrorException nee) {
                if (fd == INVALID_FD) {
                    throw new AsynchronousCloseException();
                } else if (nee.errno == EAGAIN) {
                } else if (nee.errno == EBADF) {
                    throw new InterruptedIOException(PORT_FD_INVALID);
                } else {
                    throw new InterruptedIOException(
                            "read: read error during first invocation of read() " + Errno.getErrnoSymbol(nee.errno));
                }
            }
            //make this blocking IO interruptable
            boolean completed = false;
            try {
                begin();
                try {

                    final int poll_result = poll(readPollFds, pollReadTimeout);

                    if (poll_result == 0) {
                        // Timeout
                        completed = true;
                        throw new TimeoutIOException();
                    } else {
                        if (readPollFds.get(CANCEL_FD_IDX).revents() == POLLIN) {
                            // we can read from close_event_fd => port is closing
                            completed = true;
                            if (fd == INVALID_FD) {
                                throw new AsynchronousCloseException();
                            } else {
                                throw new AsynchronousCancelException();
                            }
                        } else if (readPollFds.get(PORT_FD_IDX).revents() == POLLIN) {
                            // Happy path just check if its the right event...
                            try {
                                final short nread = Unistd.read(fd);
                                if (Unistd.jnhwIsSingeByteRead(nread)) {
                                    completed = true;
                                    return Unistd.jnhwSingeByteReadToInt(nread);
                                } else {
                                    completed = true;
                                    throw new RuntimeException("Should never ever happen!");
                                }
                            } catch (NativeErrorException nee) {
                                if (fd == INVALID_FD) {
                                    completed = true;
                                    if (fd == INVALID_FD) {
                                        throw new AsynchronousCloseException();
                                    } else {
                                        throw new AsynchronousCancelException();
                                    }
                                } else if (nee.errno == EBADF) {
                                    completed = true;
                                    throw new InterruptedIOException(PORT_FD_INVALID);
                                } else {
                                    completed = true;
                                    throw new IOException("read single byte - Unknown Error: " + Errno.getErrnoSymbol(nee.errno));
                                }
                            }
                        } else if ((readPollFds.get(PORT_FD_IDX).revents() & POLLHUP) == POLLHUP) {
                            //i.e. happens when the USB to serial adapter is removed
                            completed = true;
                            throw new InterruptedIOException(PORT_FD_INVALID);
                        } else if ((readPollFds.get(PORT_FD_IDX).revents() & POLLNVAL) == POLLNVAL) {
                            completed = true;
                            if (fd == INVALID_FD) {
                                throw new AsynchronousCloseException();
                            } else {
                                throw new AsynchronousCancelException();
                            }
                        } else {
                            completed = true;
                            throw new InterruptedIOException("read poll: received poll event fds:\n" + readPollFds.toString());
                        }
                    }
                } catch (NativeErrorException nee) {
                    completed = true;
                    throw new InterruptedIOException("read poll: Error during poll errno: " + Errno.getErrnoSymbol(nee.errno));
                }
            } catch (IOException ioe) {
                //In the case of an interruption we won't see this exception so log it here.
                LOG.log(Level.SEVERE, "IO ex for: " + portName, ioe);
                completed = true;
                throw ioe;
            } finally {
                end(completed);
            }
        }
    }

    @Override
    public void drainOutputBuffer() throws IOException {
        synchronized (writeLock) {
            //make this blocking IO interruptable
            boolean completed = false;
            try {
                begin();

                try {
                    final int poll_result = poll(writePollFds, pollWriteTimeout);

                    if (poll_result == 0) {
                        // Timeout
                        throw new TimeoutIOException();
                    } else {
                        if (writePollFds.get(CANCEL_FD_IDX).revents() == POLLIN) {
                            // we can read from close_event_ds => port is closing
                            throw new IOException(PORT_IS_CLOSED);
                        } else if (writePollFds.get(PORT_FD_IDX).revents() == POLLOUT) {
                            // Happy path all is right... no-op
                        } else if ((writePollFds.get(PORT_FD_IDX).revents() & POLLHUP) == POLLHUP) {
                            //i.e. happens when the USB to serial adapter is removed
                            throw new IOException(PORT_FD_INVALID);
                        } else if ((writePollFds.get(PORT_FD_IDX).revents() & POLLNVAL) == POLLNVAL) {
                            completed = true;
                            if (fd == INVALID_FD) {
                                throw new AsynchronousCloseException();
                            } else {
                                throw new AsynchronousCancelException();
                            }
                        } else {
                            if (fd == INVALID_FD) {
                                throw new IOException(PORT_IS_CLOSED);
                            } else {
                                throw new IOException("drainOutputBuffer poll => : received unexpected event and port not closed");
                            }
                        }
                    }
                } catch (NativeErrorException nee) {
                    throw new IOException(formatMsg(nee, "drainOutputBuffer poll: Error during poll "));
                }

                try {
                    tcdrain(fd);
                    completed = true;
                } catch (NativeErrorException nee) {
                    completed = true;
                    throw new IOException(formatMsg(nee, "Can't drain the output buffer "));
                }
            } finally {
                end(completed);
            }
        }
    }

    public boolean isDTR() throws IOException {
        return posixConfiguration.isDTR();
    }

    public boolean isRTS() throws IOException {
        return posixConfiguration.isRTS();
    }

    @Override
    public synchronized InputStream getInputStream() throws IOException {
        ensureOpen();
        if (is == null) {
            is = new SerialInputStream<PosixSerialPortSocket>(this) {
                @Override
                public int read() throws IOException {
                    try {
                        return serialPortSocket.read();
                    } catch (AsynchronousCloseException ace) {
                        return -1;
                    }
                }
            };
        }
        return is;
    }

    @Override
    public synchronized OutputStream getOutputStream() throws IOException {
        ensureOpen();
        if (os == null) {
            os = new SerialOutputStream<PosixSerialPortSocket>(this) {
                @Override
                public void write(int b) throws IOException {
                    serialPortSocket.write((byte) b);
                }
            };
        }
        return os;
    }

}
