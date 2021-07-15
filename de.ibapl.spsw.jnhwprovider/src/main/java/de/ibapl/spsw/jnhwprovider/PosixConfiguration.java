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
import de.ibapl.jnhw.posix.Errno;
import static de.ibapl.jnhw.posix.Errno.*;
import de.ibapl.jnhw.posix.Fcntl;
import static de.ibapl.jnhw.posix.Fcntl.*;
import static de.ibapl.jnhw.posix.Termios.*;
import de.ibapl.jnhw.posix.Unistd;
import static de.ibapl.jnhw.unix.sys.Ioctl.*;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import static de.ibapl.spsw.api.SerialPortConfiguration.PORT_FD_INVALID;
import static de.ibapl.spsw.api.SerialPortConfiguration.PORT_IS_CLOSED;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aploese
 */
class PosixConfiguration {

    private final static Logger LOG = Logger.getLogger("d.i.s.jnhwprovider.PosixConfiguration");

    private static final int CMSPAR_OR_PAREXT;

    static {
        int value = 0;
        if (CMSPAR.isDefined()) {
            value = CMSPAR.get();
        } else if (PAREXT.isDefined()) {
            value = PAREXT.get();
        } else {
            //This is for FreeBSD No Parity SPACE and MARK
            LOG.warning("Parites SPACE and MARK will not work!");
        }
        CMSPAR_OR_PAREXT = value;
    }

    static final int INVALID_FD = -1;

    private final String portName;
    private int fd = INVALID_FD;

    PosixConfiguration(String portName) {
        this.portName = portName;
    }

    int open(Speed speed, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls) throws IOException {
        try {
            fd = Fcntl.open(portName, O_RDWR | O_NOCTTY | O_NONBLOCK);
        } catch (NativeErrorException nee) {
            if (nee.errno == EBUSY) {
                throw new IOException(String.format("Port is busy: \"%s\"", portName));
            } else if (nee.errno == ENOENT) {
                throw new IOException(String.format("Port not found: \"%s\"", portName));
            } else if (nee.errno == EACCES) {
                throw new IOException(String.format("Permission denied: \"%s\"", portName));
            } else if (nee.errno == EIO) {
                throw new IOException(String.format("Not a serial port: \"%s\"", portName));
            } else {
                throw new IOException(String.format("Native port error \"%s:\" open \"%s\"", Errno.getErrnoSymbol(nee.errno), portName));
            }
        }

        final StructTermios termios = new StructTermios();
        try {
            tcgetattr(fd, termios);
        } catch (NativeErrorException nee) {
            try {
                Unistd.close(fd);
            } catch (NativeErrorException nee1) {
            }

            fd = INVALID_FD;
            //this is from tcgetattr
            if (nee.errno == ENOTTY) {
                throw new IOException(String.format("Not a serial port: \"%s\"", portName));
            } else {
                throw new IOException("open tcgetattr " + Errno.getErrnoSymbol(nee.errno));
            }
        }

        try {
            if (ioctl(fd, TIOCEXCL) != 0) {
                LOG.severe("Unexpected result from ioctl(fd, TIOCEXCL())!");
            }
        } catch (NativeErrorException nee) {
            try {
                Unistd.close(fd);
                fd = INVALID_FD;
            } catch (NativeErrorException nee1) {
            }
            throw new IOException("Can't set exclusive access errno: " + Errno.getErrnoSymbol(nee.errno));
        }

        termios.c_iflag(termios.c_iflag() & ~(IGNBRK | BRKINT | PARMRK | ISTRIP | INLCR | IGNCR | ICRNL | IXON));

        termios.c_oflag(termios.c_oflag() & ~OPOST);
        termios.c_lflag(termios.c_lflag() & ~(ECHO | ECHONL | ICANON | ISIG | IEXTEN));
        //Make sure CLOCAL is set otherwise opening the port later won't work without Fcntl.O_NONBLOCK()
        termios.c_cflag(termios.c_cflag() & ~(CSIZE | PARENB) | CS8 | CREAD | CLOCAL | HUPCL);
        termios.c_cc(VMIN, (byte) 0); // If there is not anything just pass
        termios.c_cc(VTIME, (byte) 0);// No timeout

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

        // flush the device
        try {
            tcflush(fd, TCIOFLUSH);
        } catch (NativeErrorException nee) {
            try {
                Unistd.close(fd);
                fd = INVALID_FD;
            } catch (NativeErrorException nee1) {
            }
            throw new IOException("Can't flush device errno: " + Errno.getErrnoSymbol(nee.errno));
        }
        return fd;
    }

    void setParams(Speed speed, DataBits dataBits, StopBits stopBits, Parity parity,
            Set<FlowControl> flowControls) throws IOException {
        setParams(getTermios(), speed, dataBits, stopBits, parity, flowControls);
    }

    private void setParams(StructTermios termios, Speed speed, DataBits dataBits, StopBits stopBits, Parity parity,
            Set<FlowControl> flowControls) throws IOException {

        if (speed != null) {
            int speedValue = speed2speed_t(speed);
            // Set standard speed from "termios.h"
            try {
                cfsetspeed(termios, speedValue);
            } catch (NativeErrorException nee) {
                throw new IllegalArgumentException(formatMsg(nee, "Can't set Speed cfsetspeed(settings, speedValue)"));
            }
        }

        if (dataBits != null) {
            termios.c_cflag(termios.c_cflag() & ~CSIZE);
            switch (dataBits) {
                case DB_5:
                    termios.c_cflag(termios.c_cflag() | CS5);
                    break;
                case DB_6:
                    termios.c_cflag(termios.c_cflag() | CS6);
                    break;
                case DB_7:
                    termios.c_cflag(termios.c_cflag() | CS7);
                    break;
                case DB_8:
                    termios.c_cflag(termios.c_cflag() | CS8);
                    break;
                default:
                    throw new IllegalArgumentException("Wrong databits");
            }

        }

        if (stopBits != null) {
            switch (stopBits) {
                case SB_1:
                    // 1 stop bit (for info see ->> MSDN)
                    termios.c_cflag(termios.c_cflag() & ~CSTOPB);
                    break;
                case SB_1_5:
                    if ((termios.c_cflag() & CSIZE) == CS5) {
                        termios.c_cflag(termios.c_cflag() | CSTOPB);
                    } else {
                        throw new IllegalArgumentException("setStopBits 1.5 stop bits are only valid for 5 DataBits");
                    }
                    break;
                case SB_2:
                    if ((termios.c_cflag() & CSIZE) == CS5) {
                        throw new IllegalArgumentException("setStopBits 2 stop bits are only valid for 6,7,8 DataBits");
                    } else {
                        termios.c_cflag(termios.c_cflag() | CSTOPB);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown stopbits " + stopBits);
            }

        }

        if (parity != null) {
            termios.c_cflag(termios.c_cflag() & ~(PARENB | PARODD | CMSPAR_OR_PAREXT)); // Clear parity settings
            switch (parity) {
                case NONE:
                    termios.c_iflag(termios.c_iflag() & ~INPCK); // switch parity input checking off
                    break;
                case ODD:
                    termios.c_cflag(termios.c_cflag() | PARENB | PARODD);
                    termios.c_iflag(termios.c_iflag() | INPCK); // switch parity input checking On
                    break;
                case EVEN:
                    termios.c_cflag(termios.c_cflag() | PARENB);
                    termios.c_iflag(termios.c_iflag() | INPCK);
                    break;
                case MARK:
                    termios.c_cflag(termios.c_cflag() | PARENB | PARODD | CMSPAR_OR_PAREXT);
                    termios.c_iflag(termios.c_iflag() | INPCK);
                    break;
                case SPACE:
                    termios.c_cflag(termios.c_cflag() | PARENB | CMSPAR_OR_PAREXT);
                    termios.c_iflag(termios.c_iflag() | INPCK);
                    break;
                default:
                    throw new IllegalArgumentException("Wrong parity");
            }
        }

        if (flowControls != null) {
            termios.c_cflag(termios.c_cflag() & ~CRTSCTS);
            termios.c_iflag(termios.c_iflag() & ~(IXON | IXOFF));
            if (flowControls.contains(FlowControl.RTS_CTS_IN)) {
                if (flowControls.contains(FlowControl.RTS_CTS_OUT)) {
                    termios.c_cflag(termios.c_cflag() | CRTSCTS);
                } else {
                    throw new IllegalArgumentException("Can only set RTS/CTS for both in and out");
                }
            } else {
                if (flowControls.contains(FlowControl.RTS_CTS_OUT)) {
                    throw new IllegalArgumentException("Can only set RTS/CTS for both in and out");
                }
            }
            if (flowControls.contains(FlowControl.XON_XOFF_IN)) {
                termios.c_iflag(termios.c_iflag() | IXOFF);
            }
            if (flowControls.contains(FlowControl.XON_XOFF_OUT)) {
                termios.c_iflag(termios.c_iflag() | IXON);
            }
        }

        try {
            tcsetattr(fd, TCSANOW, termios);
        } catch (NativeErrorException nee) {
            if (nee.errno == de.ibapl.jnhw.isoc.Errno.ERANGE) {
                throw new IllegalArgumentException(
                        String.format("Native port error \"%s\" => setParams tcsetattr portname=%s, speed=%s, dataBits=%s, stopBits=%s, parity=%s, flowControls=%s",
                                Errno.getErrnoSymbol(nee.errno), portName, speed, dataBits, stopBits, parity, flowControls));
            } else {
                throw new IOException(
                        String.format("Native port error \"%s\" => setParams tcsetattr portname=%s, speed=%s, dataBits=%s, stopBits=%s, parity=%s, flowControls=%s",
                                Errno.getErrnoSymbol(nee.errno), portName, speed, dataBits, stopBits, parity, flowControls));
            }
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

    DataBits getDatatBits() throws IOException {
        return getDatatBits(getTermios());
    }

    private DataBits getDatatBits(StructTermios termios) throws IOException {
        try {
            int masked = termios.c_cflag() & CSIZE;
            if (masked == CS5) {
                return DataBits.DB_5;
            } else if (masked == CS6) {
                return DataBits.DB_6;
            } else if (masked == CS7) {
                return DataBits.DB_7;
            } else if (masked == CS8) {
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

    Set<FlowControl> getFlowControl() throws IOException {
        return getFlowControl(getTermios());
    }

    private Set<FlowControl> getFlowControl(StructTermios termios) throws IOException {
        Set<FlowControl> result = EnumSet.noneOf(FlowControl.class
        );
        if ((termios.c_cflag() & CRTSCTS) == CRTSCTS) {
            result.addAll(FlowControl.getFC_RTS_CTS());
        }
        if ((termios.c_iflag() & IXOFF) == IXOFF) {
            result.add(FlowControl.XON_XOFF_IN);
        }
        if ((termios.c_iflag() & IXON) == IXON) {
            result.add(FlowControl.XON_XOFF_OUT);
        }
        return result;
    }

    int getInBufferBytesCount() throws IOException {
        try {
            return ioctl_ReturnValue(fd, FIONREAD, 0);
        } catch (NativeErrorException nee) {
            throw new IOException(formatMsg(nee, "Can't read in buffer size "));
        }
    }

    int getOutBufferBytesCount() throws IOException {
        try {
            return ioctl_ReturnValue(fd, TIOCOUTQ, 0);
        } catch (NativeErrorException nee) {
            throw new IOException(formatMsg(nee, "Can't read out buffer size "));
        }
    }

    Parity getParity() throws IOException {
        return getParity(getTermios());
    }

    private Parity getParity(StructTermios termios) throws IOException {
        if ((termios.c_cflag() & PARENB) == 0) {
            return Parity.NONE;
        } else if ((termios.c_cflag() & PARODD) == 0) {
            //PARODD not set => EVEN or SPACE
            if ((termios.c_cflag() & CMSPAR_OR_PAREXT) == 0) {
                return Parity.EVEN;
            } else {
                return Parity.SPACE;
            }
        } else {
            //PARODD is set => ODD or MARK
            if ((termios.c_cflag() & CMSPAR_OR_PAREXT) == 0) {
                return Parity.ODD;
            } else {
                return Parity.MARK;
            }
        }
    }

    Speed getSpeed() throws IOException {
        return getSpeed(getTermios());
    }

    private Speed getSpeed(StructTermios termios) throws IOException {
        long inSpeed = cfgetispeed(termios);
        long outSpeed = cfgetospeed(termios);
        if (inSpeed != outSpeed) {
            throw new IOException(
                    "In and out speed mismatch In:" + speed_t2speed(inSpeed) + " Out: " + speed_t2speed(outSpeed));
        }
        return speed_t2speed(inSpeed);
    }

    char getXOFFChar() throws IOException {
        final StructTermios termios = getTermios();
        return (char) termios.c_cc(VSTOP);
    }

    static Speed speed_t2speed(long speed_t) {
        if (speed_t == B0) {
            return Speed._0_BPS;
        } else if (speed_t == B50) {
            return Speed._50_BPS;
        } else if (speed_t == B75) {
            return Speed._75_BPS;
        } else if (speed_t == B110) {
            return Speed._110_BPS;
        } else if (speed_t == B134) {
            return Speed._134_BPS;
        } else if (speed_t == B150) {
            return Speed._150_BPS;
        } else if (speed_t == B200) {
            return Speed._200_BPS;
        } else if (speed_t == B300) {
            return Speed._300_BPS;
        } else if (speed_t == B600) {
            return Speed._600_BPS;
        } else if (speed_t == B1200) {
            return Speed._1200_BPS;
        } else if (speed_t == B1800) {
            return Speed._1800_BPS;
        } else if (speed_t == B2400) {
            return Speed._2400_BPS;
        } else if (speed_t == B4800) {
            return Speed._4800_BPS;
        } else if (speed_t == B9600) {
            return Speed._9600_BPS;
        } else if (speed_t == B19200) {
            return Speed._19200_BPS;
        } else if (speed_t == B38400) {
            return Speed._38400_BPS;
        } else if (speed_t == B57600) {
            return Speed._57600_BPS;
        } else if (speed_t == B115200) {
            return Speed._115200_BPS;
        } else if (speed_t == B230400) {
            return Speed._230400_BPS;
        } else if (B460800.isEqualsTo(speed_t)) {
            return Speed._460800_BPS;
        } else if (B500000.isEqualsTo(speed_t)) {
            return Speed._500000_BPS;
        } else if (B576000.isEqualsTo(speed_t)) {
            return Speed._576000_BPS;
        } else if (B921600.isEqualsTo(speed_t)) {
            return Speed._921600_BPS;
        } else if (B1000000.isEqualsTo(speed_t)) {
            return Speed._1000000_BPS;
        } else if (B1152000.isEqualsTo(speed_t)) {
            return Speed._1152000_BPS;
        } else if (B1500000.isEqualsTo(speed_t)) {
            return Speed._1500000_BPS;
        } else if (B2000000.isEqualsTo(speed_t)) {
            return Speed._2000000_BPS;
        } else if (B2500000.isEqualsTo(speed_t)) {
            return Speed._2500000_BPS;
        } else if (B3000000.isEqualsTo(speed_t)) {
            return Speed._3000000_BPS;
        } else if (B3500000.isEqualsTo(speed_t)) {
            return Speed._3500000_BPS;
        } else if (B4000000.isEqualsTo(speed_t)) {
            return Speed._4000000_BPS;
        }
        throw new IllegalArgumentException("speed not supported: " + speed_t);
    }

    /**
     *
     * @param speed
     * @return
     */
    private static int speed2speed_t(Speed speed) {
        switch (speed) {
            case _0_BPS:
                return B0;
            case _50_BPS:
                return B50;
            case _75_BPS:
                return B75;
            case _110_BPS:
                return B110;
            case _134_BPS:
                return B134;
            case _150_BPS:
                return B150;
            case _200_BPS:
                return B200;
            case _300_BPS:
                return B300;
            case _600_BPS:
                return B600;
            case _1200_BPS:
                return B1200;
            case _1800_BPS:
                return B1800;
            case _2400_BPS:
                return B2400;
            case _4800_BPS:
                return B4800;
            case _9600_BPS:
                return B9600;
            case _19200_BPS:
                return B19200;
            case _38400_BPS:
                return B38400;
            case _57600_BPS:
                return B57600;
            case _115200_BPS:
                return B115200;
            case _230400_BPS:
                return B230400;
            case _460800_BPS:
                if (B460800.isDefined()) {
                    return B460800.get();
                } else {
                    throw new IllegalArgumentException("No defined! posix.B460800");
                }
            case _500000_BPS:
                if (B500000.isDefined()) {
                    return B500000.get();
                } else {
                    throw new IllegalArgumentException("No defined! posix.B500000");
                }
            case _576000_BPS:
                if (B576000.isDefined()) {
                    return B576000.get();
                } else {
                    throw new IllegalArgumentException("No defined! posix.B576000");
                }
            case _921600_BPS:
                if (B921600.isDefined()) {
                    return B921600.get();
                } else {
                    throw new IllegalArgumentException("No defined! posix.B921600");
                }
            case _1000000_BPS:
                if (B1000000.isDefined()) {
                    return B1000000.get();
                } else {
                    throw new IllegalArgumentException("No defined! posix.B1000000");
                }
            case _1152000_BPS:
                if (B1152000.isDefined()) {
                    return B1152000.get();
                } else {
                    throw new IllegalArgumentException("No defined! posix.B1152000");
                }
            case _1500000_BPS:
                if (B1500000.isDefined()) {
                    return B1500000.get();
                } else {
                    throw new IllegalArgumentException("No defined! posix.B1500000");
                }
            case _2000000_BPS:
                if (B2000000.isDefined()) {
                    return B2000000.get();
                } else {
                    throw new IllegalArgumentException("No defined! posix.B2000000");
                }
            case _2500000_BPS:
                if (B2500000.isDefined()) {
                    return B2500000.get();
                } else {
                    throw new IllegalArgumentException("No defined! posix.B2500000");
                }
            case _3000000_BPS:
                if (B3000000.isDefined()) {
                    return B3000000.get();
                } else {
                    throw new IllegalArgumentException("No defined! posix.B3000000");
                }
            case _3500000_BPS:
                if (B3500000.isDefined()) {
                    return B3500000.get();
                } else {
                    throw new IllegalArgumentException("No defined! posix.B3500000");
                }
            case _4000000_BPS:
                if (B4000000.isDefined()) {
                    return B4000000.get();
                } else {
                    throw new IllegalArgumentException("No defined! posix.B4000000");
                }
            default:
                throw new IllegalArgumentException("Speed not supported: " + speed);
        }
    }

    StopBits getStopBits() throws IOException {
        return getStopBits(getTermios());
    }

    StopBits getStopBits(StructTermios termios) throws IOException {
        if ((termios.c_cflag() & CSTOPB) == 0) {
            return StopBits.SB_1;
        } else if ((termios.c_cflag() & CSTOPB) == CSTOPB) {
            if ((termios.c_cflag() & CSIZE) == CS5) {
                return StopBits.SB_1_5;
            } else {
                return StopBits.SB_2;
            }
        }
        throw new IllegalArgumentException("Can't figure out stop bits!");
    }

    char getXONChar() throws IOException {
        final StructTermios termios = getTermios();
        return (char) termios.c_cc(VSTART);
    }

    boolean isCTS() throws IOException {
        return getLineStatus(TIOCM_CTS);
    }

    boolean isDCD() throws IOException {
        return getLineStatus(TIOCM_CAR);
    }

    boolean isDSR() throws IOException {
        return getLineStatus(TIOCM_DSR);
    }

    private boolean isFdValid() {
        try {
            return fcntl(fd, F_GETFL) != INVALID_FD;
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

    boolean isRI() throws IOException {
        return getLineStatus(TIOCM_RNG);
    }

    boolean getLineStatus(int bitMask) throws IOException {
        try {
            final int lineStatusRef = ioctl_ReturnValue(fd, TIOCMGET, 0);
            return (lineStatusRef & bitMask) == bitMask;
        } catch (NativeErrorException nee) {
            throw new IOException(formatMsg(nee, "Can't get line status "));
        }
    }

    void setLineStatus(boolean enabled, int bitMask) throws IOException {
        int lineStatusRef;
        try {
            lineStatusRef = ioctl_ReturnValue(fd, TIOCMGET, 0);
        } catch (NativeErrorException nee) {
            throw new IOException(formatMsg(nee, "Can't get line status "));
        }
        if (enabled) {
            lineStatusRef |= bitMask;
        } else {
            lineStatusRef &= ~bitMask;
        }
        try {
            ioctl_ReturnValue(fd, TIOCMSET, lineStatusRef);
        } catch (NativeErrorException nee) {
            throw new IOException(formatMsg(nee, "Can't set line status"));
        }
    }

    void setRTS(boolean enabled) throws IOException {
        setLineStatus(enabled, TIOCM_RTS);
    }

    void setDTR(boolean enabled) throws IOException {
        setLineStatus(enabled, TIOCM_DTR);
    }

    private StructTermios getTermios() throws IOException {
        StructTermios termios = new StructTermios();
        try {
            tcgetattr(fd, termios);
            return termios;
        } catch (NativeErrorException nee) {
            throw new IOException(formatMsg(nee, "Native port error => tcgetattr (%s)", portName));
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

    public void setFd(int fd) {
        this.fd = fd;
    }

    void setXOFFChar(char c) throws IOException {
        StructTermios termios = getTermios();
        termios.c_cc(VSTOP, (byte) c);

        try {
            tcsetattr(fd, TCSANOW, termios);
        } catch (NativeErrorException nee) {
            throw new IOException(formatMsg(nee, "setXOFFChar tcsetattr"));
        }

        if (getXOFFChar() != c) {
            throw new RuntimeException("Cant't set XOFF char");
        }
    }

    void setXONChar(char c) throws IOException {
        StructTermios termios = getTermios();
        termios.c_cc(VSTART, (byte) c);

        try {
            tcsetattr(fd, TCSANOW, termios);
        } catch (NativeErrorException nee) {
            throw new IOException(formatMsg(nee, "setXONChar tcsetattr "));
        }
        if (getXONChar() != c) {
            throw new RuntimeException("Cant't set XON char");
        }
    }

    String termiosToString() throws IOException {
        return getTermios().toString();
    }

    boolean isDTR() throws IOException {
        return getLineStatus(TIOCM_DTR);
    }

    boolean isRTS() throws IOException {
        return getLineStatus(TIOCM_RTS);
    }

}
