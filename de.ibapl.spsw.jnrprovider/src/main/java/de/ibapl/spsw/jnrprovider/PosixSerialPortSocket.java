/*-
 * #%L
 * SPSW API
 * %%
 * Copyright (C) 2009 - 2018 Arne Plöse
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */
package de.ibapl.spsw.jnrprovider;
import static de.ibapl.jnrheader.Defined.isDefined;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.api.linux.sys.Eventfd_H;
import de.ibapl.jnrheader.api.isoc.Errno_H;
import de.ibapl.jnrheader.api.posix.Fcntl_H;
import de.ibapl.jnrheader.api.posix.Poll_H;
import de.ibapl.jnrheader.api.posix.Termios_H;
import de.ibapl.jnrheader.api.posix.Termios_H.Termios;
import de.ibapl.jnrheader.api.posix.Termios_H.cfsetspeed;
import de.ibapl.jnrheader.api.posix.Unistd_H;
import de.ibapl.jnrheader.api.unix.sys.Ioctl_H;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.api.TimeoutIOException;
import jnr.ffi.byref.IntByReference;

public class PosixSerialPortSocket extends AbstractSerialPortSocket<PosixSerialPortSocket> {

	public static final int INVALID_FD = -1;
	private volatile int fd = INVALID_FD;
	private volatile int close_event_fd = INVALID_FD;
	private Termios_H termios_H;
	private Errno_H errno_H;
	private Fcntl_H fcntl_H;
	private Ioctl_H ioctl_H;
	private Unistd_H unistd_H;
	private Eventfd_H eventfd_H;
	private Poll_H poll_H;
	private int interByteReadTimeout = 100;
	private int pollReadTimeout = -1;
	private int pollWriteTimeout = -1;

	public PosixSerialPortSocket(String portName) {
		super(portName);
		termios_H = JnrHeader.getInstance(Termios_H.class);
		errno_H = JnrHeader.getInstance(Errno_H.class);
		fcntl_H = JnrHeader.getInstance(Fcntl_H.class);
		ioctl_H = JnrHeader.getInstance(Ioctl_H.class);
		unistd_H = JnrHeader.getInstance(Unistd_H.class);
		eventfd_H = JnrHeader.getInstance(Eventfd_H.class);
		poll_H = JnrHeader.getInstance(Poll_H.class);
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
			unistd_H.write(close_event_fd, evt_buff, 8);

			unistd_H.usleep(1000); // 1ms
			if (termios_H.tcflush(fd, termios_H.TCIOFLUSH) != 0) {
				// perror("NATIVE Error Close - tcflush");
			}

			int err = unistd_H.close(tempFd);
			if (err == 0) {
				unistd_H.close(close_event_fd);
				close_event_fd = INVALID_FD;
			} else {
				fd = tempFd;
				throw new IOException("close => POSIX errno: " + errno_H.errno());
			}
		}
	}

	@Override
	public DataBits getDatatBits() throws IOException {
		return getDatatBits(getTermios());
	}

	private DataBits getDatatBits(Termios termios) throws IOException {
		try {
			int masked = termios.c_cflag & termios_H.CSIZE;
			if (masked == termios_H.CS5) {
				return DataBits.DB_5;
			} else if (masked == termios_H.CS6) {
				return DataBits.DB_6;
			} else if (masked == termios_H.CS7) {
				return DataBits.DB_7;
			} else if (masked == termios_H.CS8) {
				return DataBits.DB_8;
			} else {
				throw new IllegalArgumentException("Unknown databits in termios.c_cflag: " + termios.c_cflag);
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

	public Set<FlowControl> getFlowControl(Termios termios) throws IOException {
		Set<FlowControl> result = EnumSet.noneOf(FlowControl.class);
		if ((termios.c_cflag & termios_H.CRTSCTS) == termios_H.CRTSCTS) {
			result.addAll(FlowControl.getFC_RTS_CTS());
		}
		if ((termios.c_iflag & termios_H.IXOFF) == termios_H.IXOFF) {
			result.add(FlowControl.XON_XOFF_IN);
		}
		if ((termios.c_iflag & termios_H.IXON) == termios_H.IXON) {
			result.add(FlowControl.XON_XOFF_OUT);
		}
		return result;
	}

	@Override
	public int getInBufferBytesCount() throws IOException {
		IntByReference returnValueRef = new IntByReference();
		int result = ioctl_H.ioctl(fd, ioctl_H.FIONREAD, returnValueRef);
		if (result != 0) {
			if (fd == INVALID_FD) {
				throw new IOException(PORT_IS_CLOSED);
			} else {
				throw new IOException("Can't read in buffer size " + errno_H.errno());
			}
		}
		return returnValueRef.intValue();
	}

	@Override
	public int getInterByteReadTimeout() throws IOException {
		return interByteReadTimeout;
	}

	@Override
	public int getOutBufferBytesCount() throws IOException {
		IntByReference returnValueRef = new IntByReference();
		int result = ioctl_H.ioctl(fd, ioctl_H.TIOCOUTQ, returnValueRef);
		if (result != 0) {
			if (fd == INVALID_FD) {
				throw new IOException(PORT_IS_CLOSED);
			} else {
				throw new IOException("Can't read out buffer size " + errno_H.errno());
			}
		}
		return returnValueRef.intValue();
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

	private Parity getParity(Termios termios) throws IOException {
		if ((termios.c_cflag & termios_H.PARENB) == 0) {
			return Parity.NONE;
		} else if ((termios.c_cflag & termios_H.PARODD) == 0) {
			if (isDefined(termios_H.PAREXT)) {
				if ((termios.c_cflag & termios_H.PAREXT) == 0) {
					return Parity.EVEN;
				} else {
					return Parity.SPACE;
				}
			} else if (isDefined(termios_H.CMSPAR)) {
				if ((termios.c_cflag & termios_H.CMSPAR) == 0) {
					return Parity.EVEN;
				} else {
					return Parity.SPACE;
				}
			}
		} else {
			// ODD or MARK
			if (isDefined(termios_H.PAREXT)) {
				if ((termios.c_cflag & termios_H.PAREXT) == 0) {
					return Parity.ODD;
				} else {
					return Parity.MARK;
				}
			} else if (isDefined(termios_H.CMSPAR)) {
				if ((termios.c_cflag & termios_H.CMSPAR) == 0) {
					return Parity.ODD;
				} else {
					return Parity.MARK;
				}
			}
		}
		throw new IOException("Could not figure out Parity");

	}

	@Override
	public Speed getSpeed() throws IOException {
		return getSpeed(getTermios());
	}

	private Speed getSpeed(Termios termios) throws IOException {
		int inSpeed = termios_H.cfgetispeed(termios);
		int outSpeed = termios_H.cfgetospeed(termios);
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
		if (speed_t == termios_H.B0) {
			return Speed._0_BPS;
		} else if (speed_t == termios_H.B50) {
			return Speed._50_BPS;
		} else if (speed_t == termios_H.B75) {
			return Speed._75_BPS;
		} else if (speed_t == termios_H.B110) {
			return Speed._110_BPS;
		} else if (speed_t == termios_H.B134) {
			return Speed._134_BPS;
		} else if (speed_t == termios_H.B150) {
			return Speed._150_BPS;
		} else if (speed_t == termios_H.B200) {
			return Speed._200_BPS;
		} else if (speed_t == termios_H.B300) {
			return Speed._300_BPS;
		} else if (speed_t == termios_H.B600) {
			return Speed._600_BPS;
		} else if (speed_t == termios_H.B1200) {
			return Speed._1200_BPS;
		} else if (speed_t == termios_H.B1800) {
			return Speed._1800_BPS;
		} else if (speed_t == termios_H.B2400) {
			return Speed._2400_BPS;
		} else if (speed_t == termios_H.B4800) {
			return Speed._4800_BPS;
		} else if (speed_t == termios_H.B9600) {
			return Speed._9600_BPS;
		} else if (speed_t == termios_H.B19200) {
			return Speed._19200_BPS;
		} else if (speed_t == termios_H.B38400) {
			return Speed._38400_BPS;
		} else if (isDefined(termios_H.B57600) && speed_t == termios_H.B57600) {
			return Speed._57600_BPS;
		} else if (isDefined(termios_H.B115200) && speed_t == termios_H.B115200) {
			return Speed._115200_BPS;
		} else if (isDefined(termios_H.B230400) && speed_t == termios_H.B230400) {
			return Speed._230400_BPS;
		} else if (isDefined(termios_H.B460800) && speed_t == termios_H.B460800) {
			return Speed._460800_BPS;
		} else if (isDefined(termios_H.B500000) && speed_t == termios_H.B500000) {
			return Speed._500000_BPS;
		} else if (isDefined(termios_H.B576000) && speed_t == termios_H.B576000) {
			return Speed._576000_BPS;
		} else if (isDefined(termios_H.B921600) && speed_t == termios_H.B921600) {
			return Speed._921600_BPS;
		} else if (isDefined(termios_H.B1000000) && speed_t == termios_H.B1000000) {
			return Speed._1000000_BPS;
		} else if (isDefined(termios_H.B1152000) && speed_t == termios_H.B1152000) {
			return Speed._1152000_BPS;
		} else if (isDefined(termios_H.B1500000) && speed_t == termios_H.B1500000) {
			return Speed._1500000_BPS;
		} else if (isDefined(termios_H.B2000000) && speed_t == termios_H.B2000000) {
			return Speed._2000000_BPS;
		} else if (isDefined(termios_H.B2500000) && speed_t == termios_H.B2500000) {
			return Speed._2500000_BPS;
		} else if (isDefined(termios_H.B3000000) && speed_t == termios_H.B3000000) {
			return Speed._3000000_BPS;
		} else if (isDefined(termios_H.B3500000) && speed_t == termios_H.B3500000) {
			return Speed._3500000_BPS;
		} else if (isDefined(termios_H.B4000000) && speed_t == termios_H.B4000000) {
			return Speed._4000000_BPS;
		} else {
			throw new IllegalArgumentException("speed not supported: " + speed_t);
		}

	}

	/**
	 * 
	 * @param speed
	 * @return
	 */
	private int speed2speed_t(Speed speed) {
		switch (speed) {
		case _0_BPS:
			return termios_H.B0;
		case _50_BPS:
			return termios_H.B50;
		case _75_BPS:
			return termios_H.B75;
		case _110_BPS:
			return termios_H.B110;
		case _134_BPS:
			return termios_H.B134;
		case _150_BPS:
			return termios_H.B150;
		case _200_BPS:
			return termios_H.B200;
		case _300_BPS:
			return termios_H.B300;
		case _600_BPS:
			return termios_H.B600;
		case _1200_BPS:
			return termios_H.B1200;
		case _1800_BPS:
			return termios_H.B1800;
		case _2400_BPS:
			return termios_H.B2400;
		case _4800_BPS:
			return termios_H.B4800;
		case _9600_BPS:
			return termios_H.B9600;
		case _19200_BPS:
			return termios_H.B19200;
		case _38400_BPS:
			return termios_H.B38400;
		case _57600_BPS:
			if (isDefined(termios_H.B57600)) {
				return termios_H.B57600;
			} else {
				throw new IllegalArgumentException("No defined! termios_H.B57600");
			}
		case _115200_BPS:
			if (isDefined(termios_H.B115200)) {
				return termios_H.B115200;
			} else {
				throw new IllegalArgumentException("No defined! termios_H.B115200");
			}
		case _230400_BPS:
			if (isDefined(termios_H.B230400)) {
				return termios_H.B230400;
			} else {
				throw new IllegalArgumentException("No defined! termios_H.B230400");
			}
		case _460800_BPS:
			if (isDefined(termios_H.B460800)) {
				return termios_H.B460800;
			} else {
				throw new IllegalArgumentException("No defined! termios_H.B460800");
			}
		case _500000_BPS:
			if (isDefined(termios_H.B500000)) {
				return termios_H.B500000;
			} else {
				throw new IllegalArgumentException("No defined! termios_H.B500000");
			}
		case _576000_BPS:
			if (isDefined(termios_H.B576000)) {
				return termios_H.B576000;
			} else {
				throw new IllegalArgumentException("No defined! termios_H.B576000");
			}
		case _921600_BPS:
			if (isDefined(termios_H.B921600)) {
				return termios_H.B921600;
			} else {
				throw new IllegalArgumentException("No defined! termios_H.B921600");
			}
		case _1000000_BPS:
			if (isDefined(termios_H.B1000000)) {
				return termios_H.B1000000;
			} else {
				throw new IllegalArgumentException("No defined! termios_H.B1000000");
			}
		case _1152000_BPS:
			if (isDefined(termios_H.B1152000)) {
				return termios_H.B1152000;
			} else {
				throw new IllegalArgumentException("No defined! termios_H.B1152000");
			}
		case _1500000_BPS:
			if (isDefined(termios_H.B1500000)) {
				return termios_H.B1500000;
			} else {
				throw new IllegalArgumentException("No defined! termios_H.B1500000");
			}
		case _2000000_BPS:
			if (isDefined(termios_H.B2000000)) {
				return termios_H.B2000000;
			} else {
				throw new IllegalArgumentException("No defined! termios_H.B2000000");
			}
		case _2500000_BPS:
			if (isDefined(termios_H.B2500000)) {
				return termios_H.B2500000;
			} else {
				throw new IllegalArgumentException("No defined! termios_H.B2500000");
			}
		case _3000000_BPS:
			if (isDefined(termios_H.B3000000)) {
				return termios_H.B3000000;
			} else {
				throw new IllegalArgumentException("No defined! termios_H.B3000000");
			}
		case _3500000_BPS:
			if (isDefined(termios_H.B3500000)) {
				return termios_H.B3500000;
			} else {
				throw new IllegalArgumentException("No defined! termios_H.B3500000");
			}
		case _4000000_BPS:
			if (isDefined(termios_H.B4000000)) {
				return termios_H.B4000000;
			} else {
				throw new IllegalArgumentException("No defined! termios_H.B4000000");
			}
		default:
			throw new IllegalArgumentException("Speed not supported: " + speed);
		}
	}

	private StopBits getStopBits(Termios termios) throws IOException {
		if ((termios.c_cflag & termios_H.CSTOPB) == 0) {
			return StopBits.SB_1;
		} else if ((termios.c_cflag & termios_H.CSTOPB) == termios_H.CSTOPB) {
			if ((termios.c_cflag & termios_H.CSIZE) == termios_H.CS5) {
				return StopBits.SB_1_5;
			} else {
				return StopBits.SB_2;
			}
		}
		throw new IllegalArgumentException("Can't figure out stop bits!");
	}

	@Override
	public char getXOFFChar() throws IOException {
		Termios termios = getTermios();
		return (char) termios.c_cc[termios_H.VSTOP];
	}

	@Override
	public char getXONChar() throws IOException {
		Termios termios = getTermios();
		return (char) termios.c_cc[termios_H.VSTART];
	}

	@Override
	public synchronized boolean isClosed() {
		return fd == INVALID_FD;
	}

	@Override
	public boolean isCTS() throws IOException {
		return getLineStatus(ioctl_H.TIOCM_CTS);
	}

	@Override
	public boolean isDCD() throws IOException {
		return getLineStatus(ioctl_H.TIOCM_CAR);
	}

	@Override
	public boolean isDSR() throws IOException {
		return getLineStatus(ioctl_H.TIOCM_DSR);
	}

	@Override
	public boolean isOpen() {
		return fd != INVALID_FD;
	}

	@Override
	public boolean isRI() throws IOException {
		return getLineStatus(ioctl_H.TIOCM_RNG);
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

		int tempFd = fcntl_H.open(portName, fcntl_H.O_RDWR | fcntl_H.O_NOCTTY | fcntl_H.O_NONBLOCK);

		if (tempFd < 0) {
			int errno = errno_H.errno();
			if (errno == errno_H.EBUSY) {
				throw new IOException(String.format("Port is busy: (%s)", portName));
			} else if (errno == errno_H.ENOENT) {
				throw new IOException(String.format("Port not found: (%s)", portName));
			} else if (errno == errno_H.EACCES) {
				throw new IOException(String.format("Permission denied: (%s)", portName));
			} else if (errno == errno_H.EIO) {
				throw new IOException(String.format("Not a serial port: (%s)", portName));
			} else {
				throw new IOException(String.format("Native port error \"%d:\" open (%s)", errno, portName));
			}

		} else {
			fd = tempFd;
		}

		Termios_H.Termios termios = termios_H.createTermios();
		if (termios_H.tcgetattr(fd, termios) != 0) {
			unistd_H.close(fd);
			fd = -1;
			if (errno_H.errno() == errno_H.ENOTTY) {
				throw new IOException(String.format("Not a serial port: (%s)", portName));
			} else {
				throw new IOException("open tcgetattr " + errno_H.errno());
			}
		}

		if (ioctl_H.ioctl(fd, ioctl_H.TIOCEXCL) != 0) {
			unistd_H.close(fd);
			fd = INVALID_FD;
			new IOException("Can't set exclusive access error: " + errno_H.errno());
		}

		// TODO Linux?? termios_H.cfmakeraw(termios);

		// set basic settings
		termios.c_cflag |= (termios_H.CREAD | termios_H.CLOCAL);
		termios.c_lflag = 0;
		/* Raw input */
		termios.c_iflag = 0;
		/* Raw output */
		termios.c_oflag = 0;
		termios.c_cc[termios_H.VMIN] = 0; // If there is not anything just pass
		termios.c_cc[termios_H.VTIME] = 0;// No timeout

		setParams(termios, speed, dataBits, stopBits, parity, flowControls);
		termios = getTermios();
		// flush the device
		if (termios_H.tcflush(fd, termios_H.TCIOFLUSH) != 0) {
			unistd_H.close(fd);
			fd = INVALID_FD;
			throw new IOException("Can't flush device errno: " + errno_H.errno());
		}

		// on linux to avoid read/close problem maybe this helps?

		close_event_fd = eventfd_H.eventfd(0, eventfd_H.EFD_NONBLOCK);// counter is zero so nothing to read is available
		if (close_event_fd == INVALID_FD) {
			unistd_H.close(fd);
			throw new IOException("Can't create close_event_fd");
		}
	}

	@Override
	public void sendBreak(int duration) throws IOException {
		if (termios_H.tcsendbreak(fd, duration) != 0) {
			throwClosedOrNativeException("Can't sendBreak");
		}
	}

	@Override
	public void sendXOFF() throws IOException {
		throw new IllegalArgumentException("setXOFF not implemented yet");
	}

	@Override
	public void sendXON() throws IOException {
		throw new IllegalArgumentException("setXON not implemented yet");
	}

	@Override
	public void setBreak(boolean enabled) throws IOException {
		int arg;
		if (enabled) {
			arg = ioctl_H.TIOCSBRK;
		} else {
			arg = ioctl_H.TIOCCBRK;
		}
		if (ioctl_H.ioctl(fd, arg) != 0) {
			if (fd == INVALID_FD) {
				throw new IOException(PORT_IS_CLOSED);
			} else {
				throw new IOException("Can't set Break " + errno_H.errno());
			}
		}
	}

	@Override
	public void setDataBits(DataBits dataBits) throws IOException {
		setParams(getTermios(), null, dataBits, null, null, null);
	}

	@Override
	public void setDTR(boolean enabled) throws IOException {
		setLineStatus(enabled, ioctl_H.TIOCM_DTR);
	}

	@Override
	public void setFlowControl(Set<FlowControl> flowControls) throws IOException {
		setParams(getTermios(), null, null, null, null, flowControls);
	}

	private Termios_H.Termios getTermios() throws IOException {
		Termios_H.Termios termios = termios_H.createTermios();
		if (termios_H.tcgetattr(fd, termios) != 0) {
			throwClosedOrNativeException("Native port error \"%d\" => open tcgetattr (%s)", errno_H.errno(), portName);
		}
		return termios;
	}

	private void setParams(Termios_H.Termios termios, Speed speed, DataBits dataBits, StopBits stopBits, Parity parity,
			Set<FlowControl> flowControls) throws IOException {

		if (speed != null) {
			int speedValue = speed2speed_t(speed);
			// Set standard speed from "termios.h"
			if (termios_H instanceof cfsetspeed) {
				if (((cfsetspeed) termios_H).cfsetspeed(termios, speedValue) < 0) {
					throwClosedOrNativeException("Can't set Speed cfsetspeed(settings, speedValue)");
				}
			} else {
				if (termios_H.cfsetospeed(termios, speedValue) < 0) {
					throwClosedOrNativeException("Can't set Speed cfsetospeed(settings, speedValue)");
				}
				if (termios_H.cfsetispeed(termios, speedValue) < 0) {
					throwClosedOrNativeException("Can't set Speed cfsetispeed(settings, speedValue)");
				}
			}
		}

		if (dataBits != null) {
			termios.c_cflag &= ~termios_H.CSIZE;
			switch (dataBits) {
			case DB_5:
				termios.c_cflag |= termios_H.CS5;
				break;
			case DB_6:
				termios.c_cflag |= termios_H.CS6;
				break;
			case DB_7:
				termios.c_cflag |= termios_H.CS7;
				break;
			case DB_8:
				termios.c_cflag |= termios_H.CS8;
				break;
			default:
				throw new IllegalArgumentException("Wrong databits");
			}

		}

		if (stopBits != null) {
			switch (stopBits) {
			case SB_1:
				// 1 stop bit (for info see ->> MSDN)
				termios.c_cflag &= ~termios_H.CSTOPB;
				break;
			case SB_1_5:
				if ((termios.c_cflag & termios_H.CSIZE) == termios_H.CS5) {
					termios.c_cflag |= termios_H.CSTOPB;
				} else {
					throw new IllegalArgumentException("setStopBits 1.5 stop bits are only valid for 5 DataBits");
				}
				break;
			case SB_2:
				if ((termios.c_cflag & termios_H.CSIZE) == termios_H.CS5) {
					throw new IllegalArgumentException("setStopBits 2 stop bits are only valid for 6,7,8 DataBits");
				} else {
					termios.c_cflag |= termios_H.CSTOPB;
				}
				break;
			default:
				throw new IOException("Unknown stopbits " + stopBits);
			}

		}

		if (parity != null) {
			if (isDefined(termios_H.PAREXT)) {
				termios.c_cflag &= ~(termios_H.PARENB | termios_H.PARODD | termios_H.PAREXT); // Clear parity settings
			} else if (isDefined(termios_H.CMSPAR)) {
				termios.c_cflag &= ~(termios_H.PARENB | termios_H.PARODD | termios_H.CMSPAR); // Clear parity settings
			} else {
				termios.c_cflag &= ~(termios_H.PARENB | termios_H.PARODD); // Clear parity settings
			}
			switch (parity) {
			case NONE:
				termios.c_iflag &= ~termios_H.INPCK; // switch parity input checking off
				break;
			case ODD:
				termios.c_cflag |= (termios_H.PARENB | termios_H.PARODD);
				termios.c_iflag |= termios_H.INPCK; // switch parity input checking On
				break;
			case EVEN:
				termios.c_cflag |= termios_H.PARENB;
				termios.c_iflag |= termios_H.INPCK;
				break;
			case MARK:
				if (isDefined(termios_H.PAREXT)) {
					termios.c_cflag |= (termios_H.PARENB | termios_H.PARODD | termios_H.PAREXT);
					termios.c_iflag |= termios_H.INPCK;
				} else if (isDefined(termios_H.CMSPAR)) {
					termios.c_cflag |= (termios_H.PARENB | termios_H.PARODD | termios_H.CMSPAR);
					termios.c_iflag |= termios_H.INPCK;
				}
				break;
			case SPACE:
				if (isDefined(termios_H.PAREXT)) {
					termios.c_cflag |= (termios_H.PARENB | termios_H.PAREXT);
					termios.c_iflag |= termios_H.INPCK;
				} else if (isDefined(termios_H.CMSPAR)) {
					termios.c_cflag |= (termios_H.PARENB | termios_H.CMSPAR);
					termios.c_iflag |= termios_H.INPCK;
				}
				break;
			default:
				throw new IllegalArgumentException("Wrong parity");
			}
		}

		if (flowControls != null) {
			termios.c_cflag &= ~termios_H.CRTSCTS;
			termios.c_iflag &= ~(termios_H.IXON | termios_H.IXOFF);
			if (flowControls.contains(FlowControl.RTS_CTS_IN)) {
				if (flowControls.contains(FlowControl.RTS_CTS_OUT)) {
					termios.c_cflag |= termios_H.CRTSCTS;
				} else {
					throw new IllegalArgumentException("Can only set RTS/CTS for both in and out");
				}
			} else {
				if (flowControls.contains(FlowControl.RTS_CTS_OUT)) {
					throw new IllegalArgumentException("Can only set RTS/CTS for both in and out");
				}
			}
			if (flowControls.contains(FlowControl.XON_XOFF_IN)) {
				termios.c_iflag |= termios_H.IXOFF;
			}
			if (flowControls.contains(FlowControl.XON_XOFF_OUT)) {
				termios.c_iflag |= termios_H.IXON;
			}
		}
		if (termios_H.tcsetattr(fd, termios_H.TCSANOW, termios) != 0) {
			throw new IOException(
					String.format("Native port error \"%d\" => open tcsetattr (%s)", errno_H.errno(), portName));
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
		setLineStatus(enabled, ioctl_H.TIOCM_RTS);
	}

	private boolean getLineStatus(int bitMask) throws IOException {
		IntByReference lineStatusRef = new IntByReference();
		if (ioctl_H.ioctl(fd, ioctl_H.TIOCMGET, lineStatusRef) != 0) {
			if (fd == INVALID_FD) {
				throw new IOException(PORT_IS_CLOSED);
			} else {
				throw new IOException("Can't get line status " + errno_H.errno());
			}
		}
		return (lineStatusRef.intValue() & bitMask) == bitMask;
	}

	private void setLineStatus(boolean enabled, int bitMask) throws IOException {
		IntByReference lineStatusRef = new IntByReference();
		if (ioctl_H.ioctl(fd, ioctl_H.TIOCMGET, lineStatusRef) != 0) {
			if (fd == INVALID_FD) {
				throw new IOException(PORT_IS_CLOSED);
			} else {
				throw new IOException("Can't get line status " + errno_H.errno());
			}
		}
		int lineStatus = lineStatusRef.intValue();
		if (enabled) {
			lineStatus |= bitMask;
		} else {
			lineStatus &= ~bitMask;
		}
		lineStatusRef = new IntByReference(lineStatus);
		if (ioctl_H.ioctl(fd, ioctl_H.TIOCMSET, lineStatusRef) != 0) {
			if (fd == INVALID_FD) {
				throw new IOException(PORT_IS_CLOSED);
			} else {
				throw new IOException("Can't set line status " + errno_H.errno());
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
		Termios termios = getTermios();
		termios.c_cc[termios_H.VSTOP] = (byte) c;

		if (termios_H.tcsetattr(fd, termios_H.TCSANOW, termios) != 0) {
			throwClosedOrNativeException("setXOFFChar tcsetattr");
		}

		if (getXOFFChar() != c) {
			throw new RuntimeException("Cant't set XOFF char");
		}
	}

	private void throwNativeException(String formatString, Object... args) throws IOException {
		throw new IOException(String.format("Native port error on %s, \"%d\" %s", portName, errno_H.errno(),
				String.format(formatString, args)));
	}

	private void throwClosedOrNativeException(String formatString, Object... args) throws IOException {
		if (fd == INVALID_FD) {
			throw new IOException(PORT_IS_CLOSED);
		} else {
			throw new IOException(String.format("Native port error on %s, \"%d\" %s", portName, errno_H.errno(),
					String.format(formatString, args)));
		}
	}

	@Override
	public void setXONChar(char c) throws IOException {
		Termios termios = getTermios();
		termios.c_cc[termios_H.VSTART] = (byte) c;

		if (termios_H.tcsetattr(fd, termios_H.TCSANOW, termios) != 0) {
			throwClosedOrNativeException("setXONChar tcsetattr");
		}
		if (getXONChar() != c) {
			throw new RuntimeException("Cant't set XON char");
		}
	}

	String printNative() throws IOException {
		return getTermios().toString();
	}

	@Override
	protected int writeBytes(ByteBuffer b) throws IOException {
		// TODO wait infinite...
		// See javaTimeNanos() in file src/os/linux/vm/os_linux.cpp of hotspot sources
		final long endTime = System.nanoTime() + pollWriteTimeout == -1 ? 0 : pollWriteTimeout * 1000000L;

		long written = unistd_H.write(fd, b);

		if (!b.hasRemaining()) {
			// all was written
			return (int)written;
		}

		if (written < 0) {
			if (errno_H.EAGAIN == errno_H.errno()) {
				written = 0;
			} else {
				if (fd == INVALID_FD) {
					throw new IOException(PORT_IS_CLOSED);
				} else {
					throw new InterruptedIOException("unknown port error writeBytes " + errno_H.errno());
				}
			}
		}

		Poll_H.PollFd[] fds = poll_H.createPollFd(2);
		fds[0].fd = fd;
		fds[0].events = poll_H.POLLOUT;
		fds[1].fd = close_event_fd;
		fds[1].events = poll_H.POLLIN;

		long offset = written;
		// calculate the endtime...

		do {
			long currentTime = System.nanoTime();
			final int currentPollTimeout = pollWriteTimeout == -1 ? pollWriteTimeout
					: (int) ((endTime - currentTime) / 1000000L);

			int poll_result = poll_H.poll(fds, 2, currentPollTimeout);

			if (poll_result == 0) {
				// Timeout occured
				TimeoutIOException tioe = new TimeoutIOException();
				tioe.bytesTransferred = (int) written;
				throw tioe;
			} else if ((poll_result < 0)) {
				InterruptedIOException iioe = new InterruptedIOException(
						"poll timeout with error writeBytes " + errno_H.errno());
				iioe.bytesTransferred = (int) offset;
				throw iioe;
			} else {
				if (fds[1].revents == poll_H.POLLIN) {
					// we can read from close_event_fd => port is closing
					InterruptedIOException iioe = new InterruptedIOException(PORT_IS_CLOSED);
					iioe.bytesTransferred = (int) offset;
					throw iioe;
				} else if (fds[0].revents == poll_H.POLLOUT) {
					// Happy path all is right...
				} else {
					InterruptedIOException iioe = new InterruptedIOException(
							"poll returned with poll event writeBytes " + errno_H.errno());
					iioe.bytesTransferred = (int) offset;
					throw iioe;
				}
			}

			written = unistd_H.write(fd, b);

			if (written < 0) {
				if (fd == INVALID_FD) {
					InterruptedIOException iioe = new InterruptedIOException(PORT_IS_CLOSED);
					iioe.bytesTransferred = (int) offset;
					throw iioe;
				} else {
					InterruptedIOException iioe = new InterruptedIOException(
							"poll timeout with error writeBytes " + errno_H.errno());
					iioe.bytesTransferred = (int) offset;
					throw iioe;
				}
			}

			offset += written;

		} while (b.hasRemaining());
		return (int)offset;
	}

	@Override
	protected void writeSingle(int b) throws IOException {
		byte[] lpBuffer = new byte[] { (byte) b };
		long written = unistd_H.write(fd, lpBuffer, 1);

		if (written == 1) {
			return;
		}
		if (written == 0) {
			// No-op do poll
		} else if (written < 0) {
			if (errno_H.EAGAIN == errno_H.errno()) {
				// No-op do poll
				written = 0;
			} else {
				if (fd == INVALID_FD) {
					throw new IOException(PORT_IS_CLOSED);
				} else {
					throw new InterruptedIOException("unknown error writeSingle " + errno_H.errno());
				}
			}
		}

		Poll_H.PollFd[] fds = poll_H.createPollFd(2);
		fds[0].fd = fd;
		fds[0].events = poll_H.POLLOUT;
		fds[1].fd = close_event_fd;
		fds[1].events = poll_H.POLLIN;

		int poll_result = poll_H.poll(fds, 2, pollWriteTimeout);

		if (poll_result == 0) {
			// Timeout - nothing was written
			throw new TimeoutIOException();
		} else if (poll_result < 0) {
			throw new InterruptedIOException("writeSingle poll: Error during poll " + errno_H.errno());
		} else {
			if (fds[1].revents == poll_H.POLLIN) {
				// we can read from close_event_fd => port is closing
				throw new IOException(PORT_IS_CLOSED);
			} else if (fds[0].revents == poll_H.POLLOUT) {
				// Happy path all is right...
			} else {
				throw new InterruptedIOException("writeSingle error during poll " + +errno_H.errno());
			}
		}

		written = unistd_H.write(fd, lpBuffer, 1);
		if (written == 0) {
			throw new TimeoutIOException();
		} else if (written < 0) {
			if (fd == INVALID_FD) {
				throw new IOException(PORT_IS_CLOSED);
			} else {
				throw new InterruptedIOException("writeSingle too few bytes written " + errno_H.errno());
			}
		}
	}

	@Override
	protected int readBytes(ByteBuffer b) throws IOException {
		// TODO hounor overall read timeout
		Poll_H.PollFd[] fds = poll_H.createPollFd(2);
		fds[0].fd = fd;
		fds[0].events = poll_H.POLLIN;
		fds[1].fd = close_event_fd;
		fds[1].events = poll_H.POLLIN;

		long nread = unistd_H.read(fds[0].fd, b);
		if (nread < 0) {
			if (fd == INVALID_FD) {
				return -1;
			} else if (errno_H.EAGAIN == errno_H.errno()) {
				nread = 0;
			} else {
				throw new InterruptedIOException(
						"readBytes: read error during first invocation of read() " + errno_H.errno());
			}
		}
		if (nread == 0) {
			// Nothing read yet

			int poll_result = poll_H.poll(fds, 2, pollReadTimeout);

			if (poll_result == 0) {
				// Timeout
				throw new TimeoutIOException();
			} else if ((poll_result < 0)) {
				throw new InterruptedIOException("readBytes poll: Error during poll " + errno_H.errno());
			} else {
				if (fds[1].revents == poll_H.POLLIN) {
					// we can read from close_event_fd => port is closing
					return -1;
				} else if (fds[0].revents == poll_H.POLLIN) {
					// Happy path just check if its the right event...
					nread = unistd_H.read(fds[0].fd, b);
					if (nread < 0) {
						if (fd == INVALID_FD) {
							return -1;
						} else if (nread == 0) {
							throw new TimeoutIOException(); // Is this right???
						} else {
							throw new InterruptedIOException(
									"readBytes read error: Should never happen " + errno_H.errno());
						}
					}
				} else {
					throw new InterruptedIOException("readBytes poll: received poll event fds: " + Arrays.toString(fds)
							+ " errno: " + errno_H.errno());
				}
			}
		}

		long overallRead = nread;

		// Loop over poll and read to aquire as much bytes as possible either
		// a poll timeout, a full read buffer or an error
		// breaks the loop
		while (b.hasRemaining()) {

			int poll_result = poll_H.poll(fds, 2, interByteReadTimeout);

			if (poll_result == 0) {
				// This is the interbyte timeout - We are done
				return (int) overallRead; // TODO overflow???
			} else if ((poll_result < 0)) {
				throw new InterruptedIOException("readBytes poll: Error during poll " + errno_H.errno());
			} else {
				if (fds[1].revents == poll_H.POLLIN) {
					// we can read from close_event_fd => port is closing
					return -1;
				} else if (fds[0].revents == poll_H.POLLIN) {
					// Happy path
				} else {
					throw new InterruptedIOException("readBytes poll: received poll event fds: " + Arrays.toString(fds)
							+ " errono: " + errno_H.errno());
				}
			}

			// OK No timeout and no error, we should read at least one byte without
			// blocking.

			nread = unistd_H.read(fds[0].fd, b);
			if (nread > 0) {
				overallRead += nread;
			} else {
				if (fd == INVALID_FD) {
					return -1;
				} else if (nread == 0) {
					throw new InterruptedIOException(
							"readBytes: nothing to read after successful polling: Should never happen "
									+ errno_H.errno());
				} else {
					throw new InterruptedIOException("readBytes read error: Should never happen " + errno_H.errno());
				}
			}
		}
		// We reached this, because the read buffer is full.
		return (int) overallRead;
	}

	@Override
	protected int readSingle() throws IOException {
		// port is nonblocking and no hardware interbyte timeout, so we try to reed one
		// byte.
		Poll_H.PollFd[] fds = poll_H.createPollFd(2);
		fds[0].fd = fd;
		fds[0].events = poll_H.POLLIN;
		fds[1].fd = close_event_fd;
		fds[1].events = poll_H.POLLIN;

		byte[] lpBuffer = new byte[1];
		long nread = unistd_H.read(fds[0].fd, lpBuffer, 1);
		if (nread == 1) {
			return lpBuffer[0] & 0xFF;
		} else if (nread < 0) {
			if (fd == INVALID_FD) {
				// Filehandle not valid -> closed.
				return -1;
			} else if (errno_H.EAGAIN != errno_H.errno()) {
				throw new InterruptedIOException(
						"readSingle: read error during first invocation of read() " + errno_H.errno());
			}
		}

		// Nothing was read so use poll to wait...

		int poll_result = poll_H.poll(fds, 2, pollReadTimeout);

		if (poll_result == 0) {
			// Timeout
			throw new TimeoutIOException();
		} else if ((poll_result < 0)) {
			throw new InterruptedIOException("readSingle poll: Error during poll " + errno_H.errno());
		} else {
			if (fds[1].revents == poll_H.POLLIN) {
				// we can read from close_event_fd => port is closing
				return -1;
			} else if (fds[0].revents == poll_H.POLLIN) {
				// Happy path just check if its the right event...
			} else {
				throw new InterruptedIOException("readSingle poll: received poll event " + errno_H.errno());
			}
		}

		// OK polling succeeds we should be able to read the byte.
		nread = unistd_H.read(fds[0].fd, lpBuffer, 1);
		if (nread == 1) {
			return lpBuffer[0] & 0xFF;
		}

		if (fd == INVALID_FD) {
			// Closed no-op
			return -1;
		} else {
			throw new InterruptedIOException(
					"readSingle read nothing read and no timeout => Should never happen " + errno_H.errno());
		}
	}

	@Override
	protected void drainOutputBuffer() throws IOException {
		Poll_H.PollFd[] fds = poll_H.createPollFd(2);
		fds[0].fd = fd;
		fds[0].events = poll_H.POLLOUT;
		fds[1].fd = close_event_fd;
		fds[1].events = poll_H.POLLIN;

		int poll_result = poll_H.poll(fds, 2, pollWriteTimeout);

		if (poll_result == 0) {
			// Timeout
			throw new TimeoutIOException();
		} else if ((poll_result < 0)) {
			throw new IOException("drainOutputBuffer poll: Error during poll " + errno_H.errno());
		} else {
			if (fds[1].revents == poll_H.POLLIN) {
				// we can read from close_event_ds => port is closing
				throw new IOException(PORT_IS_CLOSED);
			} else if (fds[0].revents == poll_H.POLLOUT) {
				// Happy path all is right... no-op
			} else {
				if (fd == INVALID_FD) {
					throw new IOException(PORT_IS_CLOSED);
				} else {
					throw new IOException("drainOutputBuffer poll => : received unexpected event and port not closed "
							+ errno_H.errno());
				}
			}
		}

		int result = termios_H.tcdrain(fd);
		if (result != 0) {
			if (fd == INVALID_FD) {
				throw new IOException(PORT_IS_CLOSED);
			} else {
				throw new IOException("Can't drain the output buffer " + errno_H.errno());
			}
		}
	}

	public boolean isDTR() throws IOException {
		return getLineStatus(ioctl_H.TIOCM_DTR);
	}

	public boolean isRTS() throws IOException {
		return getLineStatus(ioctl_H.TIOCM_RTS);
	}

}
