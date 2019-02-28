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
package de.ibapl.spsw.jnhwprovider;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.util.EnumSet;
import java.util.Set;

import static de.ibapl.jnhw.linux.sys.Eventfd.EFD_NONBLOCK;
import static de.ibapl.jnhw.linux.sys.Eventfd.eventfd;

import static de.ibapl.jnhw.posix.Errno.EACCES;
import static de.ibapl.jnhw.posix.Errno.EAGAIN;
import static de.ibapl.jnhw.posix.Errno.ENOTTY;
import static de.ibapl.jnhw.posix.Errno.EIO;
import static de.ibapl.jnhw.posix.Errno.EBUSY;
import static de.ibapl.jnhw.posix.Errno.ENOENT;

import static de.ibapl.jnhw.posix.Fcntl.O_RDWR;
import static de.ibapl.jnhw.posix.Fcntl.O_NOCTTY;
import static de.ibapl.jnhw.posix.Fcntl.O_NONBLOCK;
import static de.ibapl.jnhw.posix.Fcntl.F_SETFL;

import static de.ibapl.jnhw.posix.Poll.POLLIN;
import static de.ibapl.jnhw.posix.Poll.POLLOUT;
import static de.ibapl.jnhw.posix.Poll.PollFds;
import static de.ibapl.jnhw.posix.Poll.poll;

import static de.ibapl.jnhw.unix.sys.Ioctl.FIONREAD;
import static de.ibapl.jnhw.unix.sys.Ioctl.TIOCM_CTS;
import static de.ibapl.jnhw.unix.sys.Ioctl.TIOCM_DTR;
import static de.ibapl.jnhw.unix.sys.Ioctl.TIOCM_CAR;
import static de.ibapl.jnhw.unix.sys.Ioctl.TIOCM_RTS;
import static de.ibapl.jnhw.unix.sys.Ioctl.TIOCM_RNG;
import static de.ibapl.jnhw.unix.sys.Ioctl.TIOCM_DSR;
import static de.ibapl.jnhw.unix.sys.Ioctl.TIOCEXCL;
import static de.ibapl.jnhw.unix.sys.Ioctl.TIOCSBRK;
import static de.ibapl.jnhw.unix.sys.Ioctl.TIOCCBRK;
import static de.ibapl.jnhw.unix.sys.Ioctl.TIOCMGET;
import static de.ibapl.jnhw.unix.sys.Ioctl.TIOCMSET;
import static de.ibapl.jnhw.unix.sys.Ioctl.TIOCOUTQ;
import static de.ibapl.jnhw.unix.sys.Ioctl.ioctl;

import de.ibapl.jnhw.posix.Errno;
import de.ibapl.jnhw.posix.Fcntl;
import de.ibapl.jnhw.NativeErrorException;
import de.ibapl.jnhw.NotDefinedException;
import de.ibapl.jnhw.posix.Unistd;

import static de.ibapl.jnhw.posix.Termios.tcflush;
import static de.ibapl.jnhw.posix.Termios.CSIZE;
import static de.ibapl.jnhw.posix.Termios.CS5;
import static de.ibapl.jnhw.posix.Termios.CS6;
import static de.ibapl.jnhw.posix.Termios.CS7;
import static de.ibapl.jnhw.posix.Termios.CS8;
import static de.ibapl.jnhw.posix.Termios.CRTSCTS;
import static de.ibapl.jnhw.posix.Termios.B0;
import static de.ibapl.jnhw.posix.Termios.B1000000;
import static de.ibapl.jnhw.posix.Termios.B110;
import static de.ibapl.jnhw.posix.Termios.B115200;
import static de.ibapl.jnhw.posix.Termios.B1152000;
import static de.ibapl.jnhw.posix.Termios.B1200;
import static de.ibapl.jnhw.posix.Termios.B134;
import static de.ibapl.jnhw.posix.Termios.B150;
import static de.ibapl.jnhw.posix.Termios.B1500000;
import static de.ibapl.jnhw.posix.Termios.B1800;
import static de.ibapl.jnhw.posix.Termios.B19200;
import static de.ibapl.jnhw.posix.Termios.B200;
import static de.ibapl.jnhw.posix.Termios.B2000000;
import static de.ibapl.jnhw.posix.Termios.B230400;
import static de.ibapl.jnhw.posix.Termios.B2400;
import static de.ibapl.jnhw.posix.Termios.B2500000;
import static de.ibapl.jnhw.posix.Termios.B300;
import static de.ibapl.jnhw.posix.Termios.B3000000;
import static de.ibapl.jnhw.posix.Termios.B3500000;
import static de.ibapl.jnhw.posix.Termios.B38400;
import static de.ibapl.jnhw.posix.Termios.B4000000;
import static de.ibapl.jnhw.posix.Termios.B460800;
import static de.ibapl.jnhw.posix.Termios.B4800;
import static de.ibapl.jnhw.posix.Termios.B50;
import static de.ibapl.jnhw.posix.Termios.B500000;
import static de.ibapl.jnhw.posix.Termios.B57600;
import static de.ibapl.jnhw.posix.Termios.B576000;
import static de.ibapl.jnhw.posix.Termios.B600;
import static de.ibapl.jnhw.posix.Termios.B75;
import static de.ibapl.jnhw.posix.Termios.B921600;
import static de.ibapl.jnhw.posix.Termios.B9600;
import static de.ibapl.jnhw.posix.Termios.CSTOPB;
import static de.ibapl.jnhw.posix.Termios.PARENB;
import static de.ibapl.jnhw.posix.Termios.VMIN;
import static de.ibapl.jnhw.posix.Termios.PARODD;
import static de.ibapl.jnhw.posix.Termios.TCIOFLUSH;
import static de.ibapl.jnhw.posix.Termios.VTIME;
import static de.ibapl.jnhw.posix.Termios.IXOFF;
import static de.ibapl.jnhw.posix.Termios.IXON;
import static de.ibapl.jnhw.posix.Termios.cfgetispeed;
import static de.ibapl.jnhw.posix.Termios.VSTOP;
import static de.ibapl.jnhw.posix.Termios.VSTART;
import static de.ibapl.jnhw.posix.Termios.CLOCAL;
import static de.ibapl.jnhw.posix.Termios.CREAD;
import static de.ibapl.jnhw.posix.Termios.cfsetspeed;
import static de.ibapl.jnhw.posix.Termios.cfgetospeed;
import static de.ibapl.jnhw.posix.Termios.tcgetattr;
import static de.ibapl.jnhw.posix.Termios.TCSANOW;
import static de.ibapl.jnhw.posix.Termios.StructTermios;
import static de.ibapl.jnhw.posix.Termios.INPCK;

import static de.ibapl.jnhw.posix.Termios.tcsetattr;
import static de.ibapl.jnhw.posix.Termios.tcsendbreak;
import static de.ibapl.jnhw.posix.Termios.tcdrain;

import de.ibapl.jnhw.IntRef;
import de.ibapl.jnhw.linux.sys.Eventfd;
import de.ibapl.jnhw.posix.Termios;
import de.ibapl.jnhw.util.posix.LibJnhwPosixLoader;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.api.TimeoutIOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PosixSerialPortSocket extends AbstractSerialPortSocket<PosixSerialPortSocket> {

    private final static Logger LOG = Logger.getLogger(PosixSerialPortSocket.class.getCanonicalName());

    public static final int INVALID_FD = -1;
    private volatile int fd = INVALID_FD;
    private volatile int close_event_write_fd = INVALID_FD;
    private volatile int close_event_read_fd = INVALID_FD;
    private int interByteReadTimeout = 100;
    private int pollReadTimeout = -1;
    private int pollWriteTimeout = -1;
    private final int CMSPAR_OR_PAREXT;
    private final boolean JNHW_HAVE_SYS_EVENTFD_H;

    public PosixSerialPortSocket(String portName) {
        super(portName);
        //Check that the libs are loaded
        if (!LibJnhwPosixLoader.touch()) {
            throw new RuntimeException("Could not load native lib", LibJnhwPosixLoader.LIB_JNHW_POSIX_LOAD_RESULT.loadError);
        }
        int value = 0;
        try {
            value = Termios.CMSPAR();
        } catch (NotDefinedException nde) {
            try {
                value = Termios.PAREXT();
            } catch (NotDefinedException nde1) {
            }
        }
        CMSPAR_OR_PAREXT = value;
        JNHW_HAVE_SYS_EVENTFD_H = Eventfd.HAVE_SYS_EVENTFD_H();
    }

    @Override
    public synchronized void close() throws IOException {
        super.close();
        if (fd != INVALID_FD) {
            // Mark port as closed...
            int tempFd = fd;
            fd = INVALID_FD;
            byte[] evt_buff = new byte[8];
            evt_buff[5] = 1;
            evt_buff[6] = 1;
            evt_buff[7] = 1;

            try {
                //Eventfd.eventfd_write(close_event_fd, 0x0101010000000000L);
                Unistd.write(close_event_write_fd, evt_buff);
                Unistd.usleep(1000); // 1ms
            } catch (NativeErrorException nee) {
                LOG.log(Level.SEVERE, "Error writing to close_event_fd error: " + Errno.getErrnoSymbol(nee.errno), nee);
            }

            try {
                tcflush(fd, TCIOFLUSH());
            } catch (NativeErrorException nee) {
                //no-op
            }

            try {
                Unistd.close(tempFd);
                try {
                    //eventfd
                    if (close_event_read_fd == close_event_write_fd) {
                        Unistd.close(close_event_read_fd);
                    } else {
                        //pipe
                        Unistd.close(close_event_read_fd);
                        Unistd.close(close_event_write_fd);
                    }
                    close_event_read_fd = INVALID_FD;
                    close_event_write_fd = INVALID_FD;
                } catch (NativeErrorException ex) {

                }
            } catch (NativeErrorException nee) {
                fd = tempFd;
                throw new IOException("close => POSIX errno: " + Errno.getErrnoSymbol(nee.errno));
            }
        }
    }

    @Override
    public DataBits getDatatBits() throws IOException {
        return getDatatBits(getTermios());
    }

    private DataBits getDatatBits(StructTermios termios) throws IOException {
        try {
            int masked = termios.c_cflag() & CSIZE();
            if (masked == CS5()) {
                return DataBits.DB_5;
            } else if (masked == CS6()) {
                return DataBits.DB_6;
            } else if (masked == CS7()) {
                return DataBits.DB_7;
            } else if (masked == CS8()) {
                return DataBits.DB_8;
            } else {
                //TODO throw something other than a IllegalArgumentException
                throw new IllegalArgumentException("Unknown databits in termios.c_cflag: " + termios.c_cflag());
            }
        } catch (IllegalArgumentException iae) {
            throw iae;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public Set<FlowControl> getFlowControl() throws IOException {
        return getFlowControl(getTermios());
    }

    public Set<FlowControl> getFlowControl(StructTermios termios) throws IOException {
        Set<FlowControl> result = EnumSet.noneOf(FlowControl.class);
        if ((termios.c_cflag() & CRTSCTS()) == CRTSCTS()) {
            result.addAll(FlowControl.getFC_RTS_CTS());
        }
        if ((termios.c_iflag() & IXOFF()) == IXOFF()) {
            result.add(FlowControl.XON_XOFF_IN);
        }
        if ((termios.c_iflag() & IXON()) == IXON()) {
            result.add(FlowControl.XON_XOFF_OUT);
        }
        return result;
    }

    @Override
    public int getInBufferBytesCount() throws IOException {
        try {
            IntRef returnValueRef = new IntRef();
            ioctl(fd, FIONREAD(), returnValueRef);
            return returnValueRef.value;
        } catch (NativeErrorException nee) {
            if (fd == INVALID_FD) {
                throw new IOException(PORT_IS_CLOSED);
            } else {
                throw new IOException("Can't read in buffer size " + Errno.getErrnoSymbol(nee.errno));
            }
        }
    }

    @Override
    public int getInterByteReadTimeout() throws IOException {
        return interByteReadTimeout;
    }

    @Override
    public int getOutBufferBytesCount() throws IOException {
        try {
            IntRef returnValueRef = new IntRef();
            ioctl(fd, TIOCOUTQ(), returnValueRef);
            return returnValueRef.value;
        } catch (NativeErrorException nee) {
            if (fd == INVALID_FD) {
                throw new IOException(PORT_IS_CLOSED);
            } else {
                throw new IOException("Can't read out buffer size " + Errno.getErrnoSymbol(nee.errno));
            }
        }
    }

    @Override
    public int getOverallReadTimeout() throws IOException {
        return pollReadTimeout == -1 ? 0 : pollReadTimeout;
    }

    @Override
    public int getOverallWriteTimeout() throws IOException {
        return pollWriteTimeout == -1 ? 0 : pollWriteTimeout;
    }

    @Override
    public Parity getParity() throws IOException {
        return getParity(getTermios());
    }

    private Parity getParity(StructTermios termios) throws IOException {
        if ((termios.c_cflag() & PARENB()) == 0) {
            return Parity.NONE;
        } else if ((termios.c_cflag() & PARODD()) == 0) {
            if ((termios.c_cflag() & CMSPAR_OR_PAREXT) == 0) {
                return Parity.EVEN;
            } else {
                return Parity.SPACE;
            }
        } else {
            // ODD or MARK
            if ((termios.c_cflag() & CMSPAR_OR_PAREXT) == 0) {
                return Parity.ODD;
            } else {
                return Parity.MARK;
            }
        }
    }

    @Override
    public Speed getSpeed() throws IOException {
        return getSpeed(getTermios());
    }

    private Speed getSpeed(StructTermios termios) throws IOException {
        int inSpeed = cfgetispeed(termios);
        int outSpeed = cfgetospeed(termios);
        if (inSpeed != outSpeed) {
            throw new IOException(
                    "In and out speed mismatch In:" + speed_t2speed(inSpeed) + " Out: " + speed_t2speed(outSpeed));
        }
        return speed_t2speed(inSpeed);
    }

    @Override
    public StopBits getStopBits() throws IOException {
        return getStopBits(getTermios());
    }

    private Speed speed_t2speed(int speed_t) {
        if (speed_t == B0()) {
            return Speed._0_BPS;
        } else if (speed_t == B50()) {
            return Speed._50_BPS;
        } else if (speed_t == B75()) {
            return Speed._75_BPS;
        } else if (speed_t == B110()) {
            return Speed._110_BPS;
        } else if (speed_t == B134()) {
            return Speed._134_BPS;
        } else if (speed_t == B150()) {
            return Speed._150_BPS;
        } else if (speed_t == B200()) {
            return Speed._200_BPS;
        } else if (speed_t == B300()) {
            return Speed._300_BPS;
        } else if (speed_t == B600()) {
            return Speed._600_BPS;
        } else if (speed_t == B1200()) {
            return Speed._1200_BPS;
        } else if (speed_t == B1800()) {
            return Speed._1800_BPS;
        } else if (speed_t == B2400()) {
            return Speed._2400_BPS;
        } else if (speed_t == B4800()) {
            return Speed._4800_BPS;
        } else if (speed_t == B9600()) {
            return Speed._9600_BPS;
        } else if (speed_t == B19200()) {
            return Speed._19200_BPS;
        } else if (speed_t == B38400()) {
            return Speed._38400_BPS;
        }
        try {
            if (speed_t == B57600()) {
                return Speed._57600_BPS;
            }
        } catch (NotDefinedException nde) {
        }
        try {
            if (speed_t == B115200()) {
                return Speed._115200_BPS;
            }
        } catch (NotDefinedException nde) {
        }
        try {
            if (speed_t == B230400()) {
                return Speed._230400_BPS;
            }
        } catch (NotDefinedException nde) {
        }
        try {
            if (speed_t == B460800()) {
                return Speed._460800_BPS;
            }
        } catch (NotDefinedException nde) {
        }
        try {
            if (speed_t == B500000()) {
                return Speed._500000_BPS;
            }
        } catch (NotDefinedException nde) {
        }
        try {
            if (speed_t == B576000()) {
                return Speed._576000_BPS;
            }
        } catch (NotDefinedException nde) {
        }
        try {
            if (speed_t == B921600()) {
                return Speed._921600_BPS;
            }
        } catch (NotDefinedException nde) {
        }
        try {
            if (speed_t == B1000000()) {
                return Speed._1000000_BPS;
            }
        } catch (NotDefinedException nde) {
        }
        try {
            if (speed_t == B1152000()) {
                return Speed._1152000_BPS;
            }
        } catch (NotDefinedException nde) {
        }
        try {
            if (speed_t == B1500000()) {
                return Speed._1500000_BPS;
            }
        } catch (NotDefinedException nde) {
        }
        try {
            if (speed_t == B2000000()) {
                return Speed._2000000_BPS;
            }
        } catch (NotDefinedException nde) {
        }
        try {
            if (speed_t == B2500000()) {
                return Speed._2500000_BPS;
            }
        } catch (NotDefinedException nde) {
        }
        try {
            if (speed_t == B3000000()) {
                return Speed._3000000_BPS;
            }
        } catch (NotDefinedException nde) {
        }
        try {
            if (speed_t == B3500000()) {
                return Speed._3500000_BPS;
            }
        } catch (NotDefinedException nde) {
        }
        try {
            if (speed_t == B4000000()) {
                return Speed._4000000_BPS;
            }
        } catch (NotDefinedException nde) {
        }
        throw new IllegalArgumentException("speed not supported: " + speed_t);
    }

    /**
     *
     * @param speed
     * @return
     */
    private int speed2speed_t(Speed speed) {
        switch (speed) {
            case _0_BPS:
                return B0();
            case _50_BPS:
                return B50();
            case _75_BPS:
                return B75();
            case _110_BPS:
                return B110();
            case _134_BPS:
                return B134();
            case _150_BPS:
                return B150();
            case _200_BPS:
                return B200();
            case _300_BPS:
                return B300();
            case _600_BPS:
                return B600();
            case _1200_BPS:
                return B1200();
            case _1800_BPS:
                return B1800();
            case _2400_BPS:
                return B2400();
            case _4800_BPS:
                return B4800();
            case _9600_BPS:
                return B9600();
            case _19200_BPS:
                return B19200();
            case _38400_BPS:
                return B38400();
            case _57600_BPS:
                try {
                    return B57600();
                } catch (NotDefinedException nde) {
                    throw new IllegalArgumentException("No defined! posix.B57600");
                }
            case _115200_BPS:
                try {
                    return B115200();
                } catch (NotDefinedException nde) {
                    throw new IllegalArgumentException("No defined! posix.B115200");
                }
            case _230400_BPS:
                try {
                    return B230400();
                } catch (NotDefinedException nde) {
                    throw new IllegalArgumentException("No defined! posix.B230400");
                }
            case _460800_BPS:
                try {
                    return B460800();
                } catch (NotDefinedException nde) {
                    throw new IllegalArgumentException("No defined! posix.B460800");
                }
            case _500000_BPS:
                try {
                    return B500000();
                } catch (NotDefinedException nde) {
                    throw new IllegalArgumentException("No defined! posix.B500000");
                }
            case _576000_BPS:
                try {
                    return B576000();
                } catch (NotDefinedException nde) {
                    throw new IllegalArgumentException("No defined! posix.B576000");
                }
            case _921600_BPS:
                try {
                    return B921600();
                } catch (NotDefinedException nde) {
                    throw new IllegalArgumentException("No defined! posix.B921600");
                }
            case _1000000_BPS:
                try {
                    return B1000000();
                } catch (NotDefinedException nde) {
                    throw new IllegalArgumentException("No defined! posix.B1000000");
                }
            case _1152000_BPS:
                try {
                    return B1152000();
                } catch (NotDefinedException nde) {
                    throw new IllegalArgumentException("No defined! posix.B1152000");
                }
            case _1500000_BPS:
                try {
                    return B1500000();
                } catch (NotDefinedException nde) {
                    throw new IllegalArgumentException("No defined! posix.B1500000");
                }
            case _2000000_BPS:
                try {
                    return B2000000();
                } catch (NotDefinedException nde) {
                    throw new IllegalArgumentException("No defined! posix.B2000000");
                }
            case _2500000_BPS:
                try {
                    return B2500000();
                } catch (NotDefinedException nde) {
                    throw new IllegalArgumentException("No defined! posix.B2500000");
                }
            case _3000000_BPS:
                try {
                    return B3000000();
                } catch (NotDefinedException nde) {
                    throw new IllegalArgumentException("No defined! posix.B3000000");
                }
            case _3500000_BPS:
                try {
                    return B3500000();
                } catch (NotDefinedException nde) {
                    throw new IllegalArgumentException("No defined! posix.B3500000");
                }
            case _4000000_BPS:
                try {
                    return B4000000();
                } catch (NotDefinedException nde) {
                    throw new IllegalArgumentException("No defined! posix.B4000000");
                }
            default:
                throw new IllegalArgumentException("Speed not supported: " + speed);
        }
    }

    private StopBits getStopBits(StructTermios termios) throws IOException {
        if ((termios.c_cflag() & CSTOPB()) == 0) {
            return StopBits.SB_1;
        } else if ((termios.c_cflag() & CSTOPB()) == CSTOPB()) {
            if ((termios.c_cflag() & CSIZE()) == CS5()) {
                return StopBits.SB_1_5;
            } else {
                return StopBits.SB_2;
            }
        }
        throw new IllegalArgumentException("Can't figure out stop bits!");
    }

    @Override
    public char getXOFFChar() throws IOException {
        StructTermios termios = getTermios();
        return (char) termios.c_cc(VSTOP());
    }

    @Override
    public char getXONChar() throws IOException {
        StructTermios termios = getTermios();
        return (char) termios.c_cc(VSTART());
    }

    @Override
    public synchronized boolean isClosed() {
        return fd == INVALID_FD;
    }

    @Override
    public boolean isCTS() throws IOException {
        return getLineStatus(TIOCM_CTS());
    }

    @Override
    public boolean isDCD() throws IOException {
        return getLineStatus(TIOCM_CAR());
    }

    @Override
    public boolean isDSR() throws IOException {
        return getLineStatus(TIOCM_DSR());
    }

    @Override
    public boolean isOpen() {
        return fd != INVALID_FD;
    }

    @Override
    public boolean isRI() throws IOException {
        return getLineStatus(TIOCM_RNG());
    }

    @Override
    public synchronized void open() throws IOException {
        open(null, null, null, null, null);
    }

    @Override
    public synchronized void open(Speed speed, DataBits dataBits, StopBits stopBits, Parity parity,
            Set<FlowControl> flowControls) throws IOException {
        if (fd != INVALID_FD) {
            throw new IOException(PORT_IS_OPEN);
        }

        try {
            fd = Fcntl.open(portName, O_RDWR() | O_NOCTTY() | O_NONBLOCK());
        } catch (NativeErrorException nee) {
            if (nee.errno == EBUSY()) {
                throw new IOException(String.format("Port is busy: (%s)", portName));
            } else if (nee.errno == ENOENT()) {
                throw new IOException(String.format("Port not found: (%s)", portName));
            } else if (nee.errno == EACCES()) {
                throw new IOException(String.format("Permission denied: (%s)", portName));
            } else if (nee.errno == EIO()) {
                throw new IOException(String.format("Not a serial port: (%s)", portName));
            } else {
                throw new IOException(String.format("Native port error \"%d:\" open (%s)", Errno.getErrnoSymbol(nee.errno), portName));
            }
        }

        StructTermios termios = new StructTermios();
        try {
            tcgetattr(fd, termios);
        } catch (NativeErrorException nee) {
            try {
                Unistd.close(fd);
            } catch (NativeErrorException nee1) {
            }

            fd = INVALID_FD;
            //this is from tcgetattr
            if (nee.errno == ENOTTY()) {
                throw new IOException(String.format("Not a serial port: (%s)", portName));
            } else {
                throw new IOException("open tcgetattr " + Errno.getErrnoSymbol(nee.errno));
            }
        }

        try {
            if (ioctl(fd, TIOCEXCL()) != 0) {
                LOG.severe("Unexpected result from ioctl(fd, TIOCEXCL())!");
            }
        } catch (NativeErrorException nee) {
            try {
                Unistd.close(fd);
                fd = INVALID_FD;
            } catch (NativeErrorException nee1) {
            }
            new IOException("Can't set exclusive access errno: " + Errno.getErrnoSymbol(nee.errno));
        }

        // TODO Linux?? posix.cfmakeraw(termios);
        // set basic settings
        termios.c_cflag(termios.c_cflag() | (CREAD() | CLOCAL()));
        termios.c_lflag(0);
        /* Raw input */
        termios.c_iflag(0);
        /* Raw output */
        termios.c_oflag(0);
        termios.c_cc(VMIN(), (byte) 0); // If there is not anything just pass
        termios.c_cc(VTIME(), (byte) 0);// No timeout

        try {
            setParams(termios, speed, dataBits, stopBits, parity, flowControls);
        } catch (Throwable t) {
            try {
                Unistd.close(fd);
                fd = INVALID_FD;
            } catch (NativeErrorException nee) {
            }
            throw t;
        }

        termios = getTermios();
        // flush the device
        try {
            tcflush(fd, TCIOFLUSH());
        } catch (NativeErrorException nee) {
            try {
                Unistd.close(fd);
                fd = INVALID_FD;
            } catch (NativeErrorException nee1) {
            }
            throw new IOException("Can't flush device errno: " + Errno.getErrnoSymbol(nee.errno));
        }

        // on linux to avoid read/close problem maybe this helps?
        try {
            if (JNHW_HAVE_SYS_EVENTFD_H) {
                //Create EventFD
                close_event_read_fd = eventfd(0, EFD_NONBLOCK());// counter is zero so nothing to read is available
                close_event_write_fd = close_event_read_fd;
            } else {
                //Create pipe
                IntRef read_fd = new IntRef();
                IntRef write_fd = new IntRef();
                Unistd.pipe(read_fd, write_fd);
                close_event_read_fd = read_fd.value;
                close_event_write_fd = write_fd.value;
                Fcntl.fcntl(close_event_read_fd, F_SETFL(), O_NONBLOCK());
                Fcntl.fcntl(close_event_write_fd, F_SETFL(), O_NONBLOCK());
            }
        } catch (NativeErrorException nee) {
            try {
                Unistd.close(fd);
                fd = INVALID_FD;
            } catch (NativeErrorException nee1) {
            }
            throw new IOException("Can't create close_event_fd");
        }
    }

    @Override
    public void sendBreak(int duration) throws IOException {
        if (duration <= 0) {
            throw new IllegalArgumentException("sendBreak duration must be grater than 0)");
        }
        try {
            tcsendbreak(fd, duration);
        } catch (NativeErrorException nee) {
            throw createClosedOrNativeException(nee.errno, "Can't sendBreak");
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
        int arg;
        if (enabled) {
            arg = TIOCSBRK();
        } else {
            arg = TIOCCBRK();
        }
        try {
            ioctl(fd, arg);
        } catch (NativeErrorException nee) {
            if (fd == INVALID_FD) {
                throw new IOException(PORT_IS_CLOSED);
            } else {
                throw new IOException("Can't set Break " + Errno.getErrnoSymbol(nee.errno));
            }
        }
    }

    @Override
    public void setDataBits(DataBits dataBits) throws IOException {
        setParams(getTermios(), null, dataBits, null, null, null);
    }

    @Override
    public void setDTR(boolean enabled) throws IOException {
        setLineStatus(enabled, TIOCM_DTR());
    }

    @Override
    public void setFlowControl(Set<FlowControl> flowControls) throws IOException {
        setParams(getTermios(), null, null, null, null, flowControls);
    }

    private StructTermios getTermios() throws IOException {
        StructTermios termios = new StructTermios();
        try {
            tcgetattr(fd, termios);
            return termios;
        } catch (NativeErrorException nee) {
            throw createClosedOrNativeException(nee.errno, "Native port error => open tcgetattr (%s)", portName);
        }
    }

    private void setParams(StructTermios termios, Speed speed, DataBits dataBits, StopBits stopBits, Parity parity,
            Set<FlowControl> flowControls) throws IOException {

        if (speed != null) {
            int speedValue = speed2speed_t(speed);
            // Set standard speed from "termios.h"
            try {
                cfsetspeed(termios, speedValue);
            } catch (NativeErrorException nee) {
                throw createClosedOrNativeException(nee.errno, "Can't set Speed cfsetspeed(settings, speedValue)");
            }
        }

        if (dataBits != null) {
            termios.c_cflag(termios.c_cflag() & ~CSIZE());
            switch (dataBits) {
                case DB_5:
                    termios.c_cflag(termios.c_cflag() | CS5());
                    break;
                case DB_6:
                    termios.c_cflag(termios.c_cflag() | CS6());
                    break;
                case DB_7:
                    termios.c_cflag(termios.c_cflag() | CS7());
                    break;
                case DB_8:
                    termios.c_cflag(termios.c_cflag() | CS8());
                    break;
                default:
                    throw new IllegalArgumentException("Wrong databits");
            }

        }

        if (stopBits != null) {
            switch (stopBits) {
                case SB_1:
                    // 1 stop bit (for info see ->> MSDN)
                    termios.c_cflag(termios.c_cflag() & ~CSTOPB());
                    break;
                case SB_1_5:
                    if ((termios.c_cflag() & CSIZE()) == CS5()) {
                        termios.c_cflag(termios.c_cflag() | CSTOPB());
                    } else {
                        throw new IllegalArgumentException("setStopBits 1.5 stop bits are only valid for 5 DataBits");
                    }
                    break;
                case SB_2:
                    if ((termios.c_cflag() & CSIZE()) == CS5()) {
                        throw new IllegalArgumentException("setStopBits 2 stop bits are only valid for 6,7,8 DataBits");
                    } else {
                        termios.c_cflag(termios.c_cflag() | CSTOPB());
                    }
                    break;
                default:
                    throw new IOException("Unknown stopbits " + stopBits);
            }

        }

        if (parity != null) {
            termios.c_cflag(termios.c_cflag() & ~(PARENB() | PARODD() | CMSPAR_OR_PAREXT)); // Clear parity settings
            switch (parity) {
                case NONE:
                    termios.c_iflag(termios.c_iflag() & ~INPCK()); // switch parity input checking off
                    break;
                case ODD:
                    termios.c_cflag(termios.c_cflag() | (PARENB() | PARODD()));
                    termios.c_iflag(termios.c_iflag() | INPCK()); // switch parity input checking On
                    break;
                case EVEN:
                    termios.c_cflag(termios.c_cflag() | PARENB());
                    termios.c_iflag(termios.c_iflag() | INPCK());
                    break;
                case MARK:
                    termios.c_cflag(termios.c_cflag() | (PARENB() | PARODD() | CMSPAR_OR_PAREXT));
                    termios.c_iflag(termios.c_iflag() | INPCK());
                    break;
                case SPACE:
                    termios.c_cflag(termios.c_cflag() | (PARENB() | CMSPAR_OR_PAREXT));
                    termios.c_iflag(termios.c_iflag() | INPCK());
                    break;
                default:
                    throw new IllegalArgumentException("Wrong parity");
            }
        }

        if (flowControls != null) {
            termios.c_cflag(termios.c_cflag() & ~CRTSCTS());
            termios.c_iflag(termios.c_iflag() & ~(IXON() | IXOFF()));
            if (flowControls.contains(FlowControl.RTS_CTS_IN)) {
                if (flowControls.contains(FlowControl.RTS_CTS_OUT)) {
                    termios.c_cflag(termios.c_cflag() | CRTSCTS());
                } else {
                    throw new IllegalArgumentException("Can only set RTS/CTS for both in and out");
                }
            } else {
                if (flowControls.contains(FlowControl.RTS_CTS_OUT)) {
                    throw new IllegalArgumentException("Can only set RTS/CTS for both in and out");
                }
            }
            if (flowControls.contains(FlowControl.XON_XOFF_IN)) {
                termios.c_iflag(termios.c_iflag() | IXOFF());
            }
            if (flowControls.contains(FlowControl.XON_XOFF_OUT)) {
                termios.c_iflag(termios.c_iflag() | IXON());
            }
        }

        try {
            tcsetattr(fd, TCSANOW(), termios);
        } catch (NativeErrorException nee) {
            throw new IOException(
                    String.format("Native port error \"%s\" => open tcsetattr (%s)", Errno.getErrnoSymbol(nee.errno), portName));
        }

        StringBuilder sb = null;
        termios = getTermios();
        // Make sure it the right parity if it was set - termios may fail silently
        if (parity != null && getParity(termios) != parity) {
            if (sb == null) {
                sb = new StringBuilder();
            }
            sb.append("Could not set parity to: ").append(parity).append(" instead it is: ").append(getParity(termios));
        }

        // Make sure it the right speed if it was set - termios may fail silently
        if (speed != null && getSpeed(termios) != speed) {
            if (sb == null) {
                sb = new StringBuilder();
            } else {
                sb.append("\n");
            }
            sb.append("Could not set speed to: ").append(speed).append(" instead it is: ").append(getSpeed(termios));
        }

        // Make sure it the right stopBits if it was set - termios may fail silently
        if (stopBits != null && getStopBits(termios) != stopBits) {
            if (sb == null) {
                sb = new StringBuilder();
            } else {
                sb.append("\n");
            }
            sb.append("Could not set stopBits to: ").append(stopBits).append(" instead it is: ")
                    .append(getStopBits(termios));
        }

        // Make sure it the right dataBits if it was set - termios may fail silently
        if (dataBits != null && getDatatBits(termios) != dataBits) {
            if (sb == null) {
                sb = new StringBuilder();
            } else {
                sb.append("\n");
            }
            sb.append("Could not set dataBits to: ").append(dataBits).append(" instead it is: ")
                    .append(getDatatBits(termios));
        }
        if (flowControls != null && !flowControls.equals(getFlowControl(termios))) {
            if (sb == null) {
                sb = new StringBuilder();
            } else {
                sb.append("\n");
            }
            sb.append("Could not set flowContrel to: ").append(flowControls).append(" instead it is: ")
                    .append(getFlowControl(termios));
        }
        if (sb != null) {
            throw new IllegalArgumentException(sb.toString());
        }
    }

    @Override
    public void setParity(Parity parity) throws IOException {
        setParams(getTermios(), null, null, null, parity, null);
    }

    @Override
    public void setRTS(boolean enabled) throws IOException {
        setLineStatus(enabled, TIOCM_RTS());
    }

    private boolean getLineStatus(int bitMask) throws IOException {
        IntRef lineStatusRef = new IntRef();
        try {
            ioctl(fd, TIOCMGET(), lineStatusRef);
        } catch (NativeErrorException nee) {
            if (fd == INVALID_FD) {
                throw new IOException(PORT_IS_CLOSED);
            } else {
                throw new IOException("Can't get line status " + Errno.getErrnoSymbol(nee.errno));
            }
        }
        return (lineStatusRef.value & bitMask) == bitMask;
    }

    private void setLineStatus(boolean enabled, int bitMask) throws IOException {
        IntRef lineStatusRef = new IntRef();
        try {
            ioctl(fd, TIOCMGET(), lineStatusRef);
        } catch (NativeErrorException nee) {
            if (fd == INVALID_FD) {
                throw new IOException(PORT_IS_CLOSED);
            } else {
                throw new IOException("Can't get line status " + Errno.getErrnoSymbol(nee.errno));
            }
        }
        if (enabled) {
            lineStatusRef.value |= bitMask;
        } else {
            lineStatusRef.value &= ~bitMask;
        }
        try {
            ioctl(fd, TIOCMSET(), lineStatusRef);
        } catch (NativeErrorException nee) {
            if (fd == INVALID_FD) {
                throw new IOException(PORT_IS_CLOSED);
            } else {
                throw new IOException("Can't set line status " + Errno.getErrnoSymbol(nee.errno));
            }
        }
    }

    @Override
    public void setSpeed(Speed speed) throws IOException {
        setParams(getTermios(), speed, null, null, null, null);
    }

    @Override
    public void setStopBits(StopBits stopBits) throws IOException {
        setParams(getTermios(), null, null, stopBits, null, null);
    }

    @Override
    public void setTimeouts(int interByteReadTimeout, int overallReadTimeout, int overallWriteTimeout)
            throws IOException {
        this.interByteReadTimeout = interByteReadTimeout;
        this.pollReadTimeout = overallReadTimeout == 0 ? -1 : overallReadTimeout;
        this.pollWriteTimeout = overallWriteTimeout == 0 ? -1 : overallWriteTimeout;
    }

    @Override
    public void setXOFFChar(char c) throws IOException {
        StructTermios termios = getTermios();
        termios.c_cc(VSTOP(), (byte) c);

        try {
            tcsetattr(fd, TCSANOW(), termios);
        } catch (NativeErrorException nee) {
            throw createClosedOrNativeException(nee.errno, "setXOFFChar tcsetattr");
        }

        if (getXOFFChar() != c) {
            throw new RuntimeException("Cant't set XOFF char");
        }
    }

    private void throwNativeException(int errno, String formatString, Object... args) throws IOException {
        throw new IOException(String.format("Native port error on %s, \"%d\" %s", portName, Errno.getErrnoSymbol(errno),
                String.format(formatString, args)));
    }

    private IOException createClosedOrNativeException(int errno, String formatString, Object... args) {
        if (fd == INVALID_FD) {
            return new IOException(PORT_IS_CLOSED);
        } else {
            return new IOException(String.format("Native port error on %s, \"%d\" %s", portName, Errno.getErrnoSymbol(errno),
                    String.format(formatString, args)));
        }
    }

    @Override
    public void setXONChar(char c) throws IOException {
        StructTermios termios = getTermios();
        termios.c_cc(VSTART(), (byte) c);

        try {
            tcsetattr(fd, TCSANOW(), termios);
        } catch (NativeErrorException nee) {
            throw createClosedOrNativeException(nee.errno, "setXONChar tcsetattr ");
        }
        if (getXONChar() != c) {
            throw new RuntimeException("Cant't set XON char");
        }
    }

    String printNative() throws IOException {
        return getTermios().toString();
    }

    @Override
    public int write(ByteBuffer b) throws IOException {
        // TODO wait infinite...
        // See javaTimeNanos() in file src/os/linux/vm/os_linux.cpp of hotspot sources
        final long endTime = System.nanoTime() + pollWriteTimeout == -1 ? 0 : pollWriteTimeout * 1000000L;

        int written;

        try {
            written = Unistd.write(fd, b);
            if (!b.hasRemaining()) {
                // all was written
                return written;
            }
        } catch (NativeErrorException nee) {
            if (nee.errno == EAGAIN()) {
                written = 0;
            } else {
                if (fd == INVALID_FD) {
                    throw new AsynchronousCloseException();
                } else {
                    throw new InterruptedIOException("unknown port error writeBytes " + Errno.getErrnoSymbol(nee.errno));
                }
            }
        }

        PollFds fds = new PollFds(2);
        fds.get(0).fd(fd);
        fds.get(0).events(POLLOUT());
        fds.get(1).fd(close_event_read_fd);
        fds.get(1).events(POLLIN());

        int offset = written;
        // calculate the endtime...
        do {
            long currentTime = System.nanoTime();
            final int currentPollTimeout = pollWriteTimeout == -1 ? pollWriteTimeout
                    : (int) ((endTime - currentTime) / 1000000L);

            try {
                final int poll_result = poll(fds, currentPollTimeout);

                if (poll_result == 0) {
                    // Timeout occured
                    TimeoutIOException tioe = new TimeoutIOException();
                    tioe.bytesTransferred = written;
                    throw tioe;
                } else {
                    if (fds.get(1).revents() == POLLIN()) {
                        // we can read from close_event_fd => port is closing
                        throw new AsynchronousCloseException();
                    } else if (fds.get(0).revents() == POLLOUT()) {
                        // Happy path all is right...
                    } else {
                        InterruptedIOException iioe = new InterruptedIOException(
                                "poll returned with poll event writeBytes ");
                        iioe.bytesTransferred = (int) offset;
                        throw iioe;
                    }
                }
            } catch (NativeErrorException nee) {
                InterruptedIOException iioe = new InterruptedIOException(
                        "poll timeout with error in writeBytes errno: " + Errno.getErrnoSymbol(nee.errno));
                iioe.bytesTransferred = (int) offset;
                throw iioe;
            }

            try {
                offset += Unistd.write(fd, b);
            } catch (NativeErrorException nee) {
                if (fd == INVALID_FD) {
                    throw new AsynchronousCloseException();
                } else {
                    InterruptedIOException iioe = new InterruptedIOException(
                            "poll timeout with error writeBytes " + Errno.getErrnoSymbol(nee.errno));
                    iioe.bytesTransferred = (int) offset;
                    throw iioe;
                }
            }

        } while (b.hasRemaining());
        return (int) offset;
    }

    @Override
    public int read(ByteBuffer b) throws IOException {
        // TODO honor overall read timeout
        PollFds fds = new PollFds(2);
        fds.get(0).fd(fd);
        fds.get(0).events(POLLIN());
        fds.get(1).fd(close_event_read_fd);
        fds.get(1).events(POLLIN());

        int nread;
        try {
            nread = Unistd.read(fds.get(0).fd(), b);
        } catch (NativeErrorException nee) {
            if (fd == INVALID_FD) {
                throw new AsynchronousCloseException();
            } else if (nee.errno == EAGAIN()) {
                nread = 0;
            } else {
                throw new InterruptedIOException(
                        "readBytes: read error during first invocation of read() " + Errno.getErrnoSymbol(nee.errno));
            }
        }

        if (nread == 0) {
            // Nothing read yet

            try {
                final int poll_result = poll(fds, pollReadTimeout);

                if (poll_result == 0) {
                    // Timeout
                    throw new TimeoutIOException();
                } else {
                    if (fds.get(1).revents() == POLLIN()) {
                        // we can read from close_event_fd => port is closing
                        throw new AsynchronousCloseException();
                    } else if (fds.get(0).revents() == POLLIN()) {
                        // Happy path just check if its the right event...
                        try {
                            nread = Unistd.read(fds.get(0).fd(), b);
                        } catch (NativeErrorException nee) {
                            if (fd == INVALID_FD) {
                                throw new AsynchronousCloseException();
                            } else if (nread == 0) {
                                throw new TimeoutIOException(); // Is this right???
                            } else {
                                throw new InterruptedIOException(
                                        "readBytes read error: Should never happen " + Errno.getErrnoSymbol(nee.errno));
                            }
                        }
                    } else {
                        throw new InterruptedIOException("readBytes poll: received poll event fds:\n" + fds.toString());
                    }
                }
            } catch (NativeErrorException nee) {
                throw new InterruptedIOException("readBytes poll: Error during poll errno: " + Errno.getErrnoSymbol(nee.errno));
            }
        }

        long overallRead = nread;

        // Loop over poll and read to aquire as much bytes as possible either
        // a poll timeout, a full read buffer or an error
        // breaks the loop
        while (b.hasRemaining()) {

            try {
                final int poll_result = poll(fds, interByteReadTimeout);

                if (poll_result == 0) {
                    // This is the interbyte timeout - We are done
                    return (int) overallRead; // TODO overflow???
                } else {
                    if (fds.get(1).revents() == POLLIN()) {
                        // we can read from close_event_fd => port is closing
                        return -1;
                    } else if (fds.get(0).revents() == POLLIN()) {
                        // Happy path
                    } else {
                        throw new InterruptedIOException("readBytes poll: received poll event fds:\n" + fds.toString());
                    }
                }
            } catch (NativeErrorException nee) {
                throw new InterruptedIOException("readBytes poll: Error during poll " + Errno.getErrnoSymbol(nee.errno));
            }
            // OK No timeout and no error, we should read at least one byte without
            // blocking.
            try {
                overallRead += Unistd.read(fds.get(0).fd(), b);
            } catch (NativeErrorException nee) {
                if (fd == INVALID_FD) {
                    throw new AsynchronousCloseException();
                } else if (nread == 0) {
                    throw new InterruptedIOException(
                            "readBytes: nothing to read after successful polling: Should never happen "
                            + Errno.getErrnoSymbol(nee.errno));
                } else {
                    throw new InterruptedIOException("readBytes read error: Should never happen " + Errno.getErrnoSymbol(nee.errno));
                }
            }
        }
        // We reached this, because the read buffer is full.
        return (int) overallRead;
    }

    @Override
    protected void drainOutputBuffer() throws IOException {
        PollFds fds = new PollFds(2);
        fds.get(0).fd(fd);
        fds.get(0).events(POLLOUT());
        fds.get(1).fd(close_event_read_fd);
        fds.get(1).events(POLLIN());

        try {
            final int poll_result = poll(fds, pollWriteTimeout);

            if (poll_result == 0) {
                // Timeout
                throw new TimeoutIOException();
            } else {
                if (fds.get(1).revents() == POLLIN()) {
                    // we can read from close_event_ds => port is closing
                    throw new IOException(PORT_IS_CLOSED);
                } else if (fds.get(0).revents() == POLLOUT()) {
                    // Happy path all is right... no-op
                } else {
                    if (fd == INVALID_FD) {
                        throw new IOException(PORT_IS_CLOSED);
                    } else {
                        throw new IOException("drainOutputBuffer poll => : received unexpected event and port not closed");
                    }
                }
            }
        } catch (NativeErrorException nee) {
            throw new IOException("drainOutputBuffer poll: Error during poll " + Errno.getErrnoSymbol(nee.errno));
        }
        try {
            tcdrain(fd);
        } catch (NativeErrorException nee) {
            if (fd == INVALID_FD) {
                throw new IOException(PORT_IS_CLOSED);
            } else {
                throw new IOException("Can't drain the output buffer " + Errno.getErrnoSymbol(nee.errno));
            }
        }
    }

    public boolean isDTR() throws IOException {
        return getLineStatus(TIOCM_DTR());
    }

    public boolean isRTS() throws IOException {
        return getLineStatus(TIOCM_RTS());
    }

}
