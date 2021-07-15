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
import de.ibapl.jnhw.common.memory.AbstractNativeMemory.SetMem;
import de.ibapl.jnhw.common.memory.Int32_t;
import de.ibapl.jnhw.libloader.LoadState;
import de.ibapl.jnhw.linux.sys.Eventfd;
import static de.ibapl.jnhw.linux.sys.Eventfd.EFD_NONBLOCK;
import static de.ibapl.jnhw.linux.sys.Eventfd.eventfd;
import de.ibapl.jnhw.posix.Errno;
import static de.ibapl.jnhw.posix.Errno.EAGAIN;
import static de.ibapl.jnhw.posix.Errno.EBADF;
import de.ibapl.jnhw.posix.Fcntl;
import static de.ibapl.jnhw.posix.Fcntl.F_SETFL;
import static de.ibapl.jnhw.posix.Fcntl.O_NONBLOCK;
import static de.ibapl.jnhw.posix.Poll.POLLHUP;
import static de.ibapl.jnhw.posix.Poll.POLLIN;
import static de.ibapl.jnhw.posix.Poll.POLLNVAL;
import static de.ibapl.jnhw.posix.Poll.POLLOUT;
import de.ibapl.jnhw.posix.Poll.PollFds;
import static de.ibapl.jnhw.posix.Poll.poll;
import static de.ibapl.jnhw.posix.Termios.*;
import de.ibapl.jnhw.posix.Time;
import de.ibapl.jnhw.posix.Unistd;
import static de.ibapl.jnhw.unix.sys.Ioctl.TIOCCBRK;
import static de.ibapl.jnhw.unix.sys.Ioctl.TIOCSBRK;
import static de.ibapl.jnhw.unix.sys.Ioctl.ioctl;
import de.ibapl.jnhw.util.posix.LibJnhwPosixLoader;
import de.ibapl.spsw.api.AsyncSerialPortSocket;
import de.ibapl.spsw.api.AsynchronousCancelException;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import static de.ibapl.spsw.api.SerialPortConfiguration.PORT_FD_INVALID;
import static de.ibapl.spsw.api.SerialPortConfiguration.PORT_IS_CLOSED;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.api.TimeoutIOException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.ref.Cleaner;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PosixAsyncSerialPortSocket extends AbstractSerialPortSocket<PosixAsyncSerialPortSocket> implements AsyncSerialPortSocket {

    private static class FutureDone<V extends Object> implements Future<V> {

        private final Object outcome;

        FutureDone(V outcome) {
            this.outcome = outcome;
        }

        FutureDone(Exception outcome) {
            this.outcome = outcome;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public V get() throws InterruptedException, ExecutionException {
            if (outcome instanceof Exception) {
                throw new ExecutionException((Exception) outcome);
            } else {
                return (V) outcome;
            }
        }

        @Override
        public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            if (unit == null) {
                throw new NullPointerException();
            }
            return get();
        }
    }

    private final static int POLL_INFINITE_TIMEOUT = -1;

    //TODO nix zu schreiben oder zu lesen, aber blockiert
    @Override
    public void readAsync(ByteBuffer dst, Consumer<ByteBuffer> callbackOnSuccess, Consumer<IOException> callbackOnFailure) {
        int nread;
        final boolean readSemaphoreAquired = readSemaphore.tryAcquire();
        if (readSemaphoreAquired) {
            try {
                nread = startRead(dst);
                if (!dst.hasRemaining()) {
                    readSemaphore.release();
                    callbackOnSuccess.accept(dst);
                    return;
                }
            } catch (IOException ioe) {
                readSemaphore.release();
                callbackOnFailure.accept(ioe);
                return;
            } catch (Exception e) {
                readSemaphore.release();
                callbackOnFailure.accept(new IOException(e));
                return;
            }
        } else {
            nread = 0;
        }

        final int start = nread;
        Runnable runnable = () -> {
            try {
                int nRead;
                if (readSemaphoreAquired) {
                    // we may have read something
                    nRead = start;
                } else {
                    try {
                        readSemaphore.acquire();
                    } catch (InterruptedException ex) {
                        callbackOnFailure.accept(new IOException(ex));
                        return;
                    }
                    nRead = startRead(dst);
                }
                if (!dst.hasRemaining()) {
                    readSemaphore.release();
                    callbackOnSuccess.accept(dst);
                    return;
                }
                int read = continueRead(dst, nRead);
                readSemaphore.release();
                callbackOnSuccess.accept(dst);
                return;
            } catch (IOException ioe) {
                readSemaphore.release();
                callbackOnFailure.accept(ioe);
                return;
            } catch (Exception e) {
                readSemaphore.release();
                callbackOnFailure.accept(new IOException(e));
                return;
            }
        };

        if (executor == null) {
            new Thread(runnable).start();
        } else {
            executor.execute(runnable);
        }
    }

    @Override
    public void readAsync(ByteBuffer dst, BiConsumer<ByteBuffer, IOException> callback) {
        readAsync(dst, (bb) -> {
            callback.accept(bb, null);
        }, (ioEx) -> {
            callback.accept(null, ioEx);
        });
    }

    @Override
    public Future<ByteBuffer> readAsync(ByteBuffer dst) {
        int nread;
        final boolean readSemaphoreAquired = readSemaphore.tryAcquire();
        if (readSemaphoreAquired) {
            try {
                nread = startRead(dst);
                if (!dst.hasRemaining()) {
                    return new FutureDone<>(dst);
                }
            } catch (Exception e) {
                return new FutureDone<>(e);
            } finally {
                readSemaphore.release();
            }
        } else {
            nread = 0;
        }

        final int start = nread;
        FutureTask<ByteBuffer> result = new FutureTask<>((Callable<ByteBuffer>) () -> {
            int nread1;
            if (readSemaphoreAquired) {
                nread1 = start;
            } else {
                readSemaphore.acquire();
                try {
                    nread1 = startRead(dst);
                    if (!dst.hasRemaining()) {
                        readSemaphore.release();
                        return dst;
                    }
                } catch (Exception e) {
                    readSemaphore.release();
                    throw e;
                }
            }
            try {
                continueRead(dst, nread1);
                return dst;
            } finally {
                readSemaphore.release();
            }
        }) {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                //TODO cancel read fd
                return super.cancel(false);
            }
        };

        if (executor == null) {
            new Thread(result).start();
        } else {
            executor.submit(result);
        }
        return result;
    }

    @Override
    public void writeAsync(ByteBuffer src, Consumer<ByteBuffer> callbackOnSuccess, Consumer<IOException> callbackOnFailure) {
        int written;
        final boolean writeSemaphoreAquired = writeSemaphore.tryAcquire();
        if (writeSemaphoreAquired) {
            try {
                written = startWrite(src);
                if (!src.hasRemaining()) {
                    writeSemaphore.release();
                    callbackOnSuccess.accept(src);
                    return;
                }
            } catch (IOException ioe) {
                writeSemaphore.release();
                callbackOnFailure.accept(ioe);
                return;
            } catch (Exception e) {
                writeSemaphore.release();
                callbackOnFailure.accept(new IOException(e));
                return;
            }
        } else {
            written = 0;
        }

        final int start = written;
        Runnable runnable = () -> {
            try {
                int written1;
                if (writeSemaphoreAquired) {
                    // we may have writtenh something
                    written1 = start;
                } else {
                    try {
                        writeSemaphore.acquire();
                    } catch (InterruptedException ex) {
                        callbackOnFailure.accept(new IOException(ex));
                        return;
                    }
                    written1 = startWrite(src);
                    if (!src.hasRemaining()) {
                        writeSemaphore.release();
                        callbackOnSuccess.accept(src);
                        return;
                    }
                }
                int wrote = continueWrite(src, written1);
                callbackOnSuccess.accept(src);
                return;
            } catch (IOException ioe) {
                writeSemaphore.release();
                callbackOnFailure.accept(ioe);
                return;
            } catch (Exception e) {
                writeSemaphore.release();
                callbackOnFailure.accept(new IOException(e));
                return;
            }
        };
        if (executor == null) {
            new Thread(runnable).start();
        } else {
            executor.execute(runnable);
        }
    }

    @Override
    public void writeAsync(ByteBuffer src, BiConsumer<ByteBuffer, IOException> callback) {
        writeAsync(src, (bb) -> {
            callback.accept(bb, null);
        }, (ioEx) -> {
            callback.accept(null, ioEx);
        });
    }

    @Override
    public Future<ByteBuffer> writeAsync(ByteBuffer src) {
        int written;
        final boolean writeSemaphoreAquired = writeSemaphore.tryAcquire();
        if (writeSemaphoreAquired) {
            try {
                written = startWrite(src);
                if (!src.hasRemaining()) {
                    return new FutureDone<>(src);
                }
            } catch (Exception e) {
                return new FutureDone<>(e);
            } finally {
                writeSemaphore.release();
            }
        } else {
            written = 0;
        }

        final int start = written;
        FutureTask<ByteBuffer> result = new FutureTask<>((Callable<ByteBuffer>) () -> {
            int written1;
            if (writeSemaphoreAquired) {
                written1 = start;
            } else {
                writeSemaphore.acquire();
                try {
                    written1 = startWrite(src);
                    if (!src.hasRemaining()) {
                        writeSemaphore.release();
                        return src;
                    }
                } catch (Exception e) {
                    writeSemaphore.release();
                    throw e;
                }
            }
            try {
                continueWrite(src, written1);
                return src;
            } finally {
                writeSemaphore.release();
            }
        }) {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                //TODO cancel read fd
                return super.cancel(false);
            }
        };

        if (executor == null) {
            new Thread(result).start();
        } else {
            executor.submit(result);
        }
        return result;
    }

    @Override
    public void drainOutputBuffer() throws IOException {
        try {
            writeSemaphore.acquire();
        } catch (InterruptedException ie) {
            throw new IOException(ie);
        }
        try {
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
        } finally {
            writeSemaphore.release();
        }
    }

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
            if (cancel_read_event__write_fd != PosixAsyncSerialPortSocket.INVALID_FD) {
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
            if (cancel_write_event__write_fd != PosixAsyncSerialPortSocket.INVALID_FD) {
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

            if (fd != PosixAsyncSerialPortSocket.INVALID_FD) {
                try {
                    Unistd.close(fd);
                    fd = PosixAsyncSerialPortSocket.INVALID_FD;
                } catch (NativeErrorException ex) {
                    LOG.severe("can't clean fd");
                }
            }

            if (cancel_read_event__write_fd != PosixAsyncSerialPortSocket.INVALID_FD) {
                try {
                    Unistd.close(cancel_read_event__write_fd);
                } catch (NativeErrorException ex) {
                    LOG.severe("can't clean cancel_read_event__write_fd");
                }
            }
            if ((cancel_read_event__read_fd != PosixAsyncSerialPortSocket.INVALID_FD) && (cancel_read_event__read_fd != cancel_read_event__write_fd)) {
                try {
                    Unistd.close(cancel_read_event__read_fd);
                } catch (NativeErrorException ex) {
                    LOG.severe("can't clean cancel_read_event__read_fd");
                }
            }
            cancel_read_event__read_fd = PosixAsyncSerialPortSocket.INVALID_FD;
            cancel_read_event__write_fd = PosixAsyncSerialPortSocket.INVALID_FD;

            if (cancel_write_event__write_fd != PosixAsyncSerialPortSocket.INVALID_FD) {
                try {
                    Unistd.close(cancel_write_event__write_fd);
                } catch (NativeErrorException ex) {
                    LOG.severe("can't clean cancel_write_event__write_fd");
                }
            }
            if ((cancel_write_event__read_fd != PosixAsyncSerialPortSocket.INVALID_FD) && (cancel_write_event__read_fd != cancel_write_event__write_fd)) {
                try {
                    Unistd.close(cancel_write_event__read_fd);
                } catch (NativeErrorException ex) {
                    LOG.severe("can't clean close_event_read_fd");
                }
            }
            cancel_write_event__read_fd = PosixAsyncSerialPortSocket.INVALID_FD;
            cancel_write_event__write_fd = PosixAsyncSerialPortSocket.INVALID_FD;

        }

    }

    private final static Logger LOG = Logger.getLogger(PosixAsyncSerialPortSocket.class.getCanonicalName());

    private static final int POLL_TIMEOUT_INFINITE = -1;
    private static final int INVALID_FD = -1;
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
    private ExecutorService executor;
    private final FdCleaner fdCleaner = new FdCleaner();
    private final Semaphore readSemaphore = new Semaphore(1, true);
    private final Semaphore writeSemaphore = new Semaphore(1, true);
    /**
     * Cached pollfds to avoid getting native mem for each read/write operation
     */
    private final PollFds readPollFds = new PollFds(2);
    private final PollFds writePollFds = new PollFds(2);

    private static final boolean JNHW_HAVE_SYS_EVENTFD_H = Eventfd.HAVE_SYS_EVENTFD_H;

    PosixAsyncSerialPortSocket(String portName) throws IOException {
        this(portName, null, null, null, null, null);
    }

    PosixAsyncSerialPortSocket(String portName, ExecutorService executor) throws IOException {
        this(portName, null, null, null, null, null);
        this.executor = executor;
    }

    PosixAsyncSerialPortSocket(String portName, Speed speed, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls) throws IOException {
        super(portName);
        posixConfiguration = new PosixConfiguration(portName);
        //Check that the libs are loaded
        if (LoadState.SUCCESS != LibJnhwPosixLoader.touch()) {
            throw new RuntimeException("Could not load native lib: ", LibJnhwPosixLoader.getLoadResult().loadError);
        }
        open(speed, dataBits, stopBits, parity, flowControls);
    }

    PosixAsyncSerialPortSocket(String portName, Speed speed, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls, ExecutorService executor) throws IOException {
        this(portName, speed, dataBits, stopBits, parity, flowControls);
        this.executor = executor;
    }

    @Override
    protected void implCloseChannel() throws IOException {
        if (fd != INVALID_FD) {
            // Mark port as closed...
            int tempFd = fd;
            fd = INVALID_FD;
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
            return Fcntl.fcntl(fd, Fcntl.F_GETFL) != INVALID_FD;
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
                Int32_t read_fd = new Int32_t();
                Int32_t write_fd = new Int32_t();
                //read
                Unistd.pipe(read_fd, write_fd);
                cancel_read_event__read_fd = read_fd.int32_t();
                cancel_read_event__write_fd = write_fd.int32_t();
                Fcntl.fcntl(cancel_read_event__read_fd, F_SETFL, O_NONBLOCK);
                Fcntl.fcntl(cancel_read_event__write_fd, F_SETFL, O_NONBLOCK);
                //write
                Unistd.pipe(read_fd, write_fd);
                cancel_write_event__read_fd = read_fd.int32_t();
                cancel_write_event__write_fd = write_fd.int32_t();
                Fcntl.fcntl(cancel_write_event__read_fd, F_SETFL, O_NONBLOCK);
                Fcntl.fcntl(cancel_write_event__write_fd, F_SETFL, O_NONBLOCK);
            }
        } catch (NativeErrorException nee) {
            try {
                Unistd.close(fd);
                fd = INVALID_FD;
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
        try {
            writeSemaphore.acquire();
        } catch (InterruptedException ie) {
            throw new IOException(ie);
        }
        try {
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
        } finally {
            writeSemaphore.release();
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
        try {
            writeSemaphore.acquire();
        } catch (InterruptedException ie) {
            throw new IOException(ie);
        }
        try {
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
        } finally {
            writeSemaphore.release();
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
    public void setXONChar(char c) throws IOException {
        posixConfiguration.setXONChar(c);
    }

    public String termiosToString() throws IOException {
        return posixConfiguration.termiosToString();
    }

    private int startWrite(ByteBuffer src) throws IOException {
        if (!src.hasRemaining()) {
            return 0;
        }
        try {
            return Unistd.write(fd, src);
        } catch (NativeErrorException nee) {
            if (nee.errno == EAGAIN) {
                return 0;
            } else if (fd == INVALID_FD) {
                throw new AsynchronousCloseException();
            } else if (nee.errno == EBADF) {
                throw new InterruptedIOException(PORT_FD_INVALID);
            } else {
                throw new InterruptedIOException(formatMsg(nee, "unknown port error write "));
            }
        }
    }

    private int continueWrite(ByteBuffer src, int written) throws IOException {
        //calc endTime only if write all to buff failed.
        final Time.Timespec endTime = pollWriteTimeout != POLL_INFINITE_TIMEOUT ? new Time.Timespec(SetMem.DO_NOT_SET) : null;
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
                        final Time.Timespec currentTime = new Time.Timespec(SetMem.DO_NOT_SET);
                        Time.clock_gettime(Time.CLOCK_MONOTONIC, currentTime);
                        final int remainingTimeOut = (int) (endTime.tv_sec() - currentTime.tv_sec()) * 1000 + (int) ((endTime.tv_nsec() - currentTime.tv_nsec()) / 1000000L);
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

    private int startRead(ByteBuffer dst) throws IOException {
        if (!dst.hasRemaining()) {
            //nothing to read
            return 0;
        }
        try {
            //non blocking read
            return Unistd.read(fd, dst);
        } catch (NativeErrorException nee) {
            if (fd == INVALID_FD) {
                throw new AsynchronousCloseException();
            } else if (nee.errno == EAGAIN) {
                return 0;
            } else if (nee.errno == EBADF) {
                throw new InterruptedIOException(PORT_FD_INVALID);
            } else {
                throw new InterruptedIOException(
                        "read: read error during first invocation of read() " + Errno.getErrnoSymbol(nee.errno));
            }
        }
    }

    /**
     * Must only be called from readAsync.
     *
     * @param dst
     * @param nread
     * @return
     * @throws IOException
     */
    private int continueRead(ByteBuffer dst, int nread) throws IOException {
        //read from buffer did not read all, so the overall time out may be needed
        final Time.Timespec endTime = pollReadTimeout != POLL_INFINITE_TIMEOUT ? new Time.Timespec(SetMem.DO_NOT_SET) : null;
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
                        final Time.Timespec currentTime = new Time.Timespec(SetMem.DO_NOT_SET);
                        Time.clock_gettime(Time.CLOCK_MONOTONIC, currentTime);
                        final int remainingTimeOut = (int) (endTime.tv_sec() - currentTime.tv_sec()) * 1000 + (int) ((endTime.tv_nsec() - currentTime.tv_nsec()) / 1_000_000L);
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

    public boolean isDTR() throws IOException {
        return posixConfiguration.isDTR();
    }

    public boolean isRTS() throws IOException {
        return posixConfiguration.isRTS();
    }

}
