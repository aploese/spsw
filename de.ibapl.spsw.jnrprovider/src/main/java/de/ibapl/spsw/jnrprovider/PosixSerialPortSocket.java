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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.Set;

import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.jnrprovider.Termios_H.Termios;
import jnr.constants.platform.Errno;
import jnr.constants.platform.OpenFlags;
import jnr.constants.platform.TermiosFlags;
import jnr.ffi.LibraryLoader;
import jnr.ffi.Runtime;
import jnr.ffi.Struct.cc_t;
import jnr.posix.POSIX;
import jnr.posix.POSIXFactory;

public class PosixSerialPortSocket implements SerialPortSocket {

	private volatile int fd = -1;
	private POSIX posix;
	private final String portname;
	private Termios_H.Functions tf;
	private Runtime runtime;

	public PosixSerialPortSocket(String portname) {
		this.portname = portname;
		posix = POSIXFactory.getPOSIX(); // Was new DummyPOSIXHandler(), true);
		tf = LibraryLoader.create(Termios_H.Functions.class).load("c");
		runtime = Runtime.getRuntime(tf);
	}

	@Override
	public synchronized void close() throws IOException {
		if (fd != -1) {
			int tempFd = fd;
			fd = -1;
			int err = posix.close(tempFd);
			if (err == 0) {
			} else {
				fd = tempFd;
				throw new IOException("close => POSIX errno: " + posix.errno());
			}
		}
	}

	@Override
	public DataBits getDatatBits() throws IOException {
		return getDatatBits(getTermios());
	}

	private DataBits getDatatBits(Termios termios) throws IOException {
		try {
			int v = termios.c_cflag.intValue() & TermiosFlags.CSIZE.intValue();
			if (v == TermiosFlags.CS5.intValue()) {
				return DataBits.DB_5;
			} else if (v == TermiosFlags.CS6.intValue()) {
				return DataBits.DB_6;
			} else if (v == TermiosFlags.CS7.intValue()) {
				return DataBits.DB_7;
			} else if (v == TermiosFlags.CS8.intValue()) {
				return DataBits.DB_8;
			} else {
				throw new IllegalArgumentException(
						"Unknown databits in termios.c_cflag: " + termios.c_cflag.intValue());
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
		if ((termios.c_cflag.intValue() & TermiosFlags.CRTSCTS.intValue()) == TermiosFlags.CRTSCTS.intValue()) {
			result.addAll(FlowControl.getFC_RTS_CTS());
		}
		if ((termios.c_iflag.intValue() & TermiosFlags.IXOFF.intValue()) == TermiosFlags.IXOFF.intValue()) {
			result.add(FlowControl.XON_XOFF_IN);
		}
		if ((termios.c_iflag.intValue() & TermiosFlags.IXON.intValue()) == TermiosFlags.IXON.intValue()) {
			result.add(FlowControl.XON_XOFF_OUT);
		}
		return result;
	}

	@Override
	public int getInBufferBytesCount() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getInterByteReadTimeout() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getOutBufferBytesCount() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getOverallReadTimeout() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getOverallWriteTimeout() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Parity getParity() throws IOException {
		return getParity(getTermios());
	}

	private Parity getParity(Termios termios) throws IOException {

		if ((termios.c_cflag.intValue() & TermiosFlags.PARENB.intValue()) == 0) {
			return Parity.NONE;
		} else if ((termios.c_cflag.intValue() & TermiosFlags.PARODD.intValue()) == 0) {
			if (TermiosFlags.PAREXT.defined()) {
				if ((termios.c_cflag.intValue() & TermiosFlags.PAREXT.intValue()) == 0) {
					return Parity.EVEN;
				} else {
					return Parity.SPACE;
				}
			} else if (TermiosFlags.CMSPAR.defined()) {
				if ((termios.c_cflag.intValue() & TermiosFlags.CMSPAR.intValue()) == 0) {
					return Parity.EVEN;
				} else {
					return Parity.SPACE;
				}
			}
		} else {
			// ODD or MARK
			if (TermiosFlags.PAREXT.defined()) {
				if ((termios.c_cflag.intValue() & TermiosFlags.PAREXT.intValue()) == 0) {
					return Parity.ODD;
				} else {
					return Parity.MARK;
				}
			} else if (TermiosFlags.CMSPAR.defined()) {
				if ((termios.c_cflag.intValue() & TermiosFlags.CMSPAR.intValue()) == 0) {
					return Parity.ODD;
				} else {
					return Parity.MARK;
				}
			}
		}
		throw new IOException("Could not figure out Parity");

	}

	@Override
	public String getPortName() {
		return portname;
	}

	@Override
	public Speed getSpeed() throws IOException {
		return getSpeed(getTermios());
	}

	private Speed getSpeed(Termios termios) throws IOException {
		int inSpeed = tf.cfgetispeed(termios);
		int outSpeed = tf.cfgetospeed(termios);
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
		if (speed_t == TermiosFlags.B0.intValue()) {
			return Speed._0_BPS;
		} else if (speed_t == TermiosFlags.B50.intValue()) {
			return Speed._50_BPS;
		} else if (speed_t == TermiosFlags.B75.intValue()) {
			return Speed._75_BPS;
		} else if (speed_t == TermiosFlags.B110.intValue()) {
			return Speed._110_BPS;
		} else if (speed_t == TermiosFlags.B134.intValue()) {
			return Speed._134_BPS;
		} else if (speed_t == TermiosFlags.B150.intValue()) {
			return Speed._150_BPS;
		} else if (speed_t == TermiosFlags.B200.intValue()) {
			return Speed._200_BPS;
		} else if (speed_t == TermiosFlags.B300.intValue()) {
			return Speed._300_BPS;
		} else if (speed_t == TermiosFlags.B600.intValue()) {
			return Speed._600_BPS;
		} else if (speed_t == TermiosFlags.B1200.intValue()) {
			return Speed._1200_BPS;
		} else if (speed_t == TermiosFlags.B1800.intValue()) {
			return Speed._1800_BPS;
		} else if (speed_t == TermiosFlags.B2400.intValue()) {
			return Speed._2400_BPS;
		} else if (speed_t == TermiosFlags.B4800.intValue()) {
			return Speed._4800_BPS;
		} else if (speed_t == TermiosFlags.B9600.intValue()) {
			return Speed._9600_BPS;
		} else if (speed_t == TermiosFlags.B19200.intValue()) {
			return Speed._19200_BPS;
		} else if (speed_t == TermiosFlags.B38400.intValue()) {
			return Speed._38400_BPS;
		} else if (TermiosFlags.B57600.defined() && speed_t == TermiosFlags.B57600.intValue()) {
			return Speed._57600_BPS;
		} else if (TermiosFlags.B115200.defined() && speed_t == TermiosFlags.B115200.intValue()) {
			return Speed._115200_BPS;
		} else if (TermiosFlags.B230400.defined() && speed_t == TermiosFlags.B230400.intValue()) {
			return Speed._230400_BPS;
		} else if (TermiosFlags.B460800.defined() && speed_t == TermiosFlags.B460800.intValue()) {
			return Speed._460800_BPS;
		} else if (TermiosFlags.B500000.defined() && speed_t == TermiosFlags.B500000.intValue()) {
			return Speed._500000_BPS;
		} else if (TermiosFlags.B576000.defined() && speed_t == TermiosFlags.B576000.intValue()) {
			return Speed._576000_BPS;
		} else if (TermiosFlags.B921600.defined() && speed_t == TermiosFlags.B921600.intValue()) {
			return Speed._921600_BPS;
		} else if (TermiosFlags.B1000000.defined() && speed_t == TermiosFlags.B1000000.intValue()) {
			return Speed._1000000_BPS;
		} else if (TermiosFlags.B1152000.defined() && speed_t == TermiosFlags.B1152000.intValue()) {
			return Speed._1152000_BPS;
		} else if (TermiosFlags.B1500000.defined() && speed_t == TermiosFlags.B1500000.intValue()) {
			return Speed._1500000_BPS;
		} else if (TermiosFlags.B2000000.defined() && speed_t == TermiosFlags.B2000000.intValue()) {
			return Speed._2000000_BPS;
		} else if (TermiosFlags.B2500000.defined() && speed_t == TermiosFlags.B2500000.intValue()) {
			return Speed._2500000_BPS;
		} else if (TermiosFlags.B3000000.defined() && speed_t == TermiosFlags.B3000000.intValue()) {
			return Speed._3000000_BPS;
		} else if (TermiosFlags.B3500000.defined() && speed_t == TermiosFlags.B3500000.intValue()) {
			return Speed._3500000_BPS;
		} else if (TermiosFlags.B4000000.defined() && speed_t == TermiosFlags.B4000000.intValue()) {
			return Speed._4000000_BPS;
		} else {
			throw new IllegalArgumentException("speed not supported: " + speed_t);
		}

	}

	/**
	 * Checks if defined and return the intValue. If not defined a
	 * IllegalArgumentException is thrown.
	 * 
	 * @param termiosFlags
	 * @return the intValue
	 * @throws IllegalArgumentException
	 *             if not defined.
	 */
	private int checkDefined(TermiosFlags termiosFlags) {
		if (termiosFlags.defined()) {
			return termiosFlags.intValue();
		} else {
			throw new IllegalArgumentException("TermiosFlag not defined: " + termiosFlags);
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
			return TermiosFlags.B0.intValue();
		case _50_BPS:
			return TermiosFlags.B50.intValue();
		case _75_BPS:
			return TermiosFlags.B75.intValue();
		case _110_BPS:
			return TermiosFlags.B110.intValue();
		case _134_BPS:
			return TermiosFlags.B134.intValue();
		case _150_BPS:
			return TermiosFlags.B150.intValue();
		case _200_BPS:
			return TermiosFlags.B200.intValue();
		case _300_BPS:
			return TermiosFlags.B300.intValue();
		case _600_BPS:
			return TermiosFlags.B600.intValue();
		case _1200_BPS:
			return TermiosFlags.B1200.intValue();
		case _1800_BPS:
			return TermiosFlags.B1800.intValue();
		case _2400_BPS:
			return TermiosFlags.B2400.intValue();
		case _4800_BPS:
			return TermiosFlags.B4800.intValue();
		case _9600_BPS:
			return TermiosFlags.B9600.intValue();
		case _19200_BPS:
			return TermiosFlags.B19200.intValue();
		case _38400_BPS:
			return TermiosFlags.B38400.intValue();
		case _57600_BPS:
			return checkDefined(TermiosFlags.B57600);
		case _115200_BPS:
			return checkDefined(TermiosFlags.B115200);
		case _230400_BPS:
			return checkDefined(TermiosFlags.B230400);
		case _460800_BPS:
			return checkDefined(TermiosFlags.B460800);
		case _500000_BPS:
			return checkDefined(TermiosFlags.B500000);
		case _576000_BPS:
			return checkDefined(TermiosFlags.B576000);
		case _921600_BPS:
			return checkDefined(TermiosFlags.B921600);
		case _1000000_BPS:
			return checkDefined(TermiosFlags.B1000000);
		case _1152000_BPS:
			return checkDefined(TermiosFlags.B1152000);
		case _1500000_BPS:
			return checkDefined(TermiosFlags.B1500000);
		case _2000000_BPS:
			return checkDefined(TermiosFlags.B2000000);
		case _2500000_BPS:
			return checkDefined(TermiosFlags.B2500000);
		case _3000000_BPS:
			return checkDefined(TermiosFlags.B3000000);
		case _3500000_BPS:
			return checkDefined(TermiosFlags.B3500000);
		case _4000000_BPS:
			return checkDefined(TermiosFlags.B4000000);
		default:
			throw new IllegalArgumentException("Speed not supported: " + speed);
		}
	}

	private StopBits getStopBits(Termios termios) throws IOException {
		if ((termios.c_cflag.intValue() & TermiosFlags.CSTOPB.intValue()) == 0) {
			return StopBits.SB_1;
		} else if ((termios.c_cflag.intValue() & TermiosFlags.CSTOPB.intValue()) == TermiosFlags.CSTOPB.intValue()) {
			if ((termios.c_cflag.intValue() & TermiosFlags.CSIZE.intValue()) == TermiosFlags.CS5.intValue()) {
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
		return (char)termios.c_cc[TermiosFlags.VSTOP.intValue()].byteValue();
	}

	@Override
	public char getXONChar() throws IOException {
		Termios termios = getTermios();
		return (char)termios.c_cc[TermiosFlags.VSTART.intValue()].byteValue();
	}

	@Override
	public synchronized boolean isClosed() {
		return fd == -1;
	}

	@Override
	public boolean isCTS() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDCD() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDSR() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOpen() {
		return fd > -1;
	}

	@Override
	public boolean isRI() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public synchronized void open() throws IOException {
		open(null, null, null, null, null);
	}

	@Override
	public synchronized void open(Speed speed, DataBits dataBits, StopBits stopBits, Parity parity,
			Set<FlowControl> flowControls) throws IOException {
		if (fd != -1) {
			throw new IOException("Port is already opend");
		}
		int tempFd = posix.open(portname,
				OpenFlags.O_RDWR.intValue() | OpenFlags.O_NOCTTY.intValue() | OpenFlags.O_NONBLOCK.intValue(), 0666);

		if (tempFd < 0) {
			if (posix.errno() == Errno.EBUSY.intValue()) {
				throw new IOException(String.format("Port is busy: (%s)", portname));
			} else if (posix.errno() == Errno.ENOENT.intValue()) {
				throw new IOException(String.format("Port not found: (%s)", portname));
			} else if (posix.errno() == Errno.EACCES.intValue()) {
				throw new IOException(String.format("Permission denied: (%s)", portname));
			} else if (posix.errno() == Errno.EIO.intValue()) {
				throw new IOException(String.format("Not a serial port: (%s)", portname));
			} else {
				throw new IOException(String.format("Native port error \"%d:\" open (%s)", posix.errno(), portname));
			}

		} else {
			fd = tempFd;
		}

		Termios_H.Termios termios = new Termios_H.Termios(runtime);
		if (tf.tcgetattr(fd, termios) != 0) {
			posix.close(fd);
			fd = -1;
			if (posix.errno() == Errno.ENOTTY.intValue()) {
				throw new IOException(String.format("Not a serial port: (%s)", portname));
			} else {
				throwClosedOrNativeException("open tcgetattr");
			}
		}

		setParams(termios, speed, dataBits, stopBits, parity, flowControls);

		/*
		 * struct serial_struct { int type; int line; unsigned int port; int irq; int
		 * flags; int xmit_fifo_size; int custom_divisor; int baud_base; unsigned short
		 * close_delay; char io_type; char reserved_char[1]; int hub6; unsigned short
		 * closing_wait; / * time to wait before closing * / unsigned short
		 * closing_wait2; / * no longer used... * / unsigned char *iomem_base; unsigned
		 * short iomem_reg_shift; unsigned int port_high; unsigned long iomap_base; / *
		 * cookie passed into ioremap * / };
		 */
	}

	@Override
	public void sendBreak(int duration) throws IOException {
		if (tf.tcsendbreak(fd, duration) != 0) {
			throwClosedOrNativeException("Can't sendBreak");
		}
	}

	@Override
	public void sendXOFF() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendXON() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBreak(boolean value) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDataBits(DataBits dataBits) throws IOException {
		setParams(getTermios(), null, dataBits, null, null, null);
	}

	@Override
	public void setDTR(boolean value) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFlowControl(Set<FlowControl> flowControls) throws IOException {
		setParams(getTermios(), null, null, null, null, flowControls);
	}

	private Termios_H.Termios getTermios() throws IOException {
		Termios_H.Termios termios = new Termios_H.Termios(runtime);
		if (tf.tcgetattr(fd, termios) != 0) {
			for (Errno e : Errno.values()) {
				if (e.intValue() == posix.errno()) {
					throw new IOException(
							String.format("Native port error \"%s\" => open tcgetattr (%s)", e, portname));
				}
			}
			throw new IOException(
					String.format("Native port error \"%d\" => open tcgetattr (%s)", posix.errno(), portname));
		}
		return termios;
	}

	private void setParams(Termios_H.Termios termios, Speed speed, DataBits dataBits, StopBits stopBits, Parity parity,
			Set<FlowControl> flowControls) throws IOException {

		if (speed != null) {
			int speedValue = speed2speed_t(speed);
			// TODO check what this extra fields in struct termios mean: ...
			// #define _HAVE_STRUCT_TERMIOS_C_ISPEED 1
			// #define _HAVE_STRUCT_TERMIOS_C_OSPEED 1

			// Set standard speed from "termios.h"
			if (tf.cfsetspeed(termios, speedValue) < 0) {
				throwClosedOrNativeException("Can't set Speed cfsetspeed(settings, speedValue)");
			}
		}

		if (dataBits != null) {
			termios.c_cflag.set(termios.c_cflag.intValue() & ~TermiosFlags.CSIZE.intValue());
			switch (dataBits) {
			case DB_5:
				termios.c_cflag.set(termios.c_cflag.intValue() | TermiosFlags.CS5.intValue());
				break;
			case DB_6:
				termios.c_cflag.set(termios.c_cflag.intValue() | TermiosFlags.CS6.intValue());
				break;
			case DB_7:
				termios.c_cflag.set(termios.c_cflag.intValue() | TermiosFlags.CS7.intValue());
				break;
			case DB_8:
				termios.c_cflag.set(termios.c_cflag.intValue() | TermiosFlags.CS8.intValue());
				break;
			default:
				throw new IllegalArgumentException("Wrong databits");
			}

		}

		if (stopBits != null) {
			switch (stopBits) {
			case SB_1:
				// 1 stop bit (for info see ->> MSDN)
				termios.c_cflag.set(termios.c_cflag.intValue() & ~TermiosFlags.CSTOPB.intValue());
				break;
			case SB_1_5:
				if ((termios.c_cflag.intValue() & TermiosFlags.CSIZE.intValue()) == TermiosFlags.CS5.intValue()) {
					termios.c_cflag.set(termios.c_cflag.intValue() | TermiosFlags.CSTOPB.intValue());
				} else {
					throw new IllegalArgumentException("setStopBits 1.5 stop bits are only valid for 5 DataBits");
				}
				break;
			case SB_2:
				if ((termios.c_cflag.intValue() & TermiosFlags.CSIZE.intValue()) == TermiosFlags.CS5.intValue()) {
					throw new IllegalArgumentException("setStopBits 2 stop bits are only valid for 6,7,8 DataBits");
				} else {
					termios.c_cflag.set(termios.c_cflag.intValue() | TermiosFlags.CSTOPB.intValue());
				}
				break;
			default:
				throw new IOException("Unknown stopbits " + stopBits);
			}

		}

		if (parity != null) {
			if (TermiosFlags.PAREXT.defined()) {
				termios.c_cflag.set(termios.c_cflag.intValue() & ~(TermiosFlags.PARENB.intValue()
						| TermiosFlags.PARODD.intValue() | TermiosFlags.PAREXT.intValue())); // Clear parity settings
			} else if (TermiosFlags.CMSPAR.defined()) {
				termios.c_cflag.set(termios.c_cflag.intValue() & ~(TermiosFlags.PARENB.intValue()
						| TermiosFlags.PARODD.intValue() | TermiosFlags.CMSPAR.intValue())); // Clear parity settings
			} else {
				termios.c_cflag.set(termios.c_cflag.intValue()
						& ~(TermiosFlags.PARENB.intValue() | TermiosFlags.PARODD.intValue())); // Clear
																								// parity
																								// settings
			}
			switch (parity) {
			case NONE:
				termios.c_iflag.set(termios.c_iflag.intValue() & ~TermiosFlags.INPCK.intValue()); // switch parity input
																									// checking off
				break;
			case ODD:
				termios.c_cflag.set(
						termios.c_cflag.intValue() | (TermiosFlags.PARENB.intValue() | TermiosFlags.PARODD.intValue()));
				termios.c_iflag.set(termios.c_iflag.intValue() | TermiosFlags.INPCK.intValue()); // switch parity input
																									// checking On
				break;
			case EVEN:
				termios.c_cflag.set(termios.c_cflag.intValue() | TermiosFlags.PARENB.intValue());
				termios.c_iflag.set(termios.c_iflag.intValue() | TermiosFlags.INPCK.intValue());
				break;
			case MARK:
				if (TermiosFlags.PAREXT.defined()) {
					termios.c_cflag.set(termios.c_cflag.intValue() | (TermiosFlags.PARENB.intValue()
							| TermiosFlags.PARODD.intValue() | TermiosFlags.PAREXT.intValue()));
					termios.c_iflag.set(termios.c_iflag.intValue() | TermiosFlags.INPCK.intValue());
				} else if (TermiosFlags.CMSPAR.defined()) {
					termios.c_cflag.set(termios.c_cflag.intValue() | (TermiosFlags.PARENB.intValue()
							| TermiosFlags.PARODD.intValue() | TermiosFlags.CMSPAR.intValue()));
					termios.c_iflag.set(termios.c_iflag.intValue() | TermiosFlags.INPCK.intValue());
				}
				break;
			case SPACE:
				if (TermiosFlags.PAREXT.defined()) {
					termios.c_cflag.set(termios.c_cflag.intValue()
							| (TermiosFlags.PARENB.intValue() | TermiosFlags.PAREXT.intValue()));
					termios.c_iflag.set(termios.c_iflag.intValue() | TermiosFlags.INPCK.intValue());
				} else if (TermiosFlags.CMSPAR.defined()) {
					termios.c_cflag.set(termios.c_cflag.intValue()
							| (TermiosFlags.PARENB.intValue() | TermiosFlags.CMSPAR.intValue()));
					termios.c_iflag.set(termios.c_iflag.intValue() | TermiosFlags.INPCK.intValue());
				}
				break;
			default:
				throw new IllegalArgumentException("Wrong parity");
			}
		}

		if (flowControls != null) {
			termios.c_cflag.set(termios.c_cflag.intValue() & ~TermiosFlags.CRTSCTS.intValue());
			termios.c_iflag
					.set(termios.c_iflag.intValue() & ~(TermiosFlags.IXON.intValue() | TermiosFlags.IXOFF.intValue()));
			if (flowControls.contains(FlowControl.RTS_CTS_IN)) {
				if (flowControls.contains(FlowControl.RTS_CTS_OUT)) {
					termios.c_cflag.set(termios.c_cflag.intValue() | TermiosFlags.CRTSCTS.intValue());
				} else {
					throw new IllegalArgumentException("Can only set RTS/CTS for both in and out");
				}
			} else {
				if (flowControls.contains(FlowControl.RTS_CTS_OUT)) {
					throw new IllegalArgumentException("Can only set RTS/CTS for both in and out");
				}
			}
			if (flowControls.contains(FlowControl.XON_XOFF_IN)) {
				termios.c_iflag.set(termios.c_iflag.intValue() | TermiosFlags.IXOFF.intValue());
			}
			if (flowControls.contains(FlowControl.XON_XOFF_OUT)) {
				termios.c_iflag.set(termios.c_iflag.intValue() | TermiosFlags.IXON.intValue());
			}
		}

		if (tf.tcsetattr(fd, TermiosFlags.TCSANOW.intValue(), termios) != 0) {
			for (Errno e : Errno.values()) {
				if (e.intValue() == posix.errno()) {
					throw new IOException(
							String.format("Native port error \"%s\" => open tcsetattr (%s)", e, portname));
				}
			}
			throw new IOException(
					String.format("Native port error \"%d\" => open tcsetattr (%s)", posix.errno(), portname));
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
	public void setRTS(boolean value) throws IOException {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	public void setXOFFChar(char c) throws IOException {
		Termios termios = getTermios();
		termios.c_cc[TermiosFlags.VSTOP.intValue()].set((byte) c);

		if (tf.tcsetattr(fd, TermiosFlags.TCSANOW.intValue(), termios) != 0) {
			throwClosedOrNativeException("setXOFFChar tcsetattr");
		}

		if (getXOFFChar() != c) {
			throw new RuntimeException("Cant't set XOFF char");
		}
	}

	private void throwClosedOrNativeException(String formatString, Object... args) throws IOException {
		// TODO what does Errno.valueOf( exactly?
		final int errno = posix.errno();
		for (Errno e : Errno.values()) {
			if (e.intValue() == errno) {
				throw new IOException(String.format("Native port error on %s, \"%s\" %s", portname, e,
						String.format(formatString, args)));
			}
		}
		throw new IOException(String.format("Native port error on %s, \"%d\" %s", portname, errno,
				String.format(formatString, args)));
	}

	@Override
	public void setXONChar(char c) throws IOException {
		Termios termios = getTermios();
		termios.c_cc[TermiosFlags.VSTART.intValue()].set((byte) c);

		if (tf.tcsetattr(fd, TermiosFlags.TCSANOW.intValue(), termios) != 0) {
			throwClosedOrNativeException("setXONChar tcsetattr");
		}
		if (getXONChar() != c) {
			throw new RuntimeException("Cant't set XON char");
		}
	}

	@Override
	protected final void finalize() throws Throwable {
		try {
			if (isOpen()) {
				close();
			}
		} catch (Exception ex) {
			// This should always work
			System.err.println("SerialPortSocket " + getPortName() + " finalize() exception: " + ex);
		} catch (Error err) {
			// Leave a trace what hit us...
			System.err.println("SerialPortSocket " + getPortName() + " finalize() error: " + err);
			throw err;
		} finally {
			super.finalize();
		}
	}

	String printNative() throws IOException{
		return getTermios().toString();
	}

}
