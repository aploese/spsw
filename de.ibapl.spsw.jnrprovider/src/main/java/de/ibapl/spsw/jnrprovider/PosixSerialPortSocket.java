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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<FlowControl> getFlowControl() throws IOException {
		// TODO Auto-generated method stub
		return null;
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
		Termios_H.Termios termios = new Termios_H.Termios(runtime);
		if (tf.tcgetattr(fd, termios) != 0) {
			throw new IOException(String.format("%s: Unknown port error %s: open tcgetattr (%s)",
					Errno.valueOf(posix.errno()), portname));
		}

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StopBits getStopBits() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char getXOFFChar() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public char getXONChar() throws IOException {
		// TODO Auto-generated method stub
		return 0;
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
		if (fd != -1) {
			throw new IOException("Port is already opend");
		}
		int tempFd = posix.open(portname,
				OpenFlags.O_RDWR.intValue() | OpenFlags.O_NOCTTY.intValue() | OpenFlags.O_NONBLOCK.intValue(), 0666);

		if (tempFd < 0) {
			switch (Errno.valueOf(posix.errno())) {
			case EBUSY:
				throw new IOException(String.format("Port is busy: (%s)", portname));
			case ENOENT:
				throw new IOException(String.format("Port not found: (%s)", portname));
			case EACCES:
				throw new IOException(String.format("Permission denied: (%s)", portname));
			case EIO:
				throw new IOException(String.format("Not a serial port: (%s)", portname));
			default:
				throw new IOException(
						String.format("%s: Unknown port error %s: open (%s)", Errno.valueOf(posix.errno()), portname));
			}

		} else {
			fd = tempFd;
		}
		Termios_H.Termios termios = new Termios_H.Termios(runtime);
		if (tf.tcgetattr(fd, termios) != 0) {
			posix.close(fd);
			fd = -1;
			switch (Errno.valueOf(posix.errno())) {
			case ENOTTY:
				throw new IOException(String.format("Not a serial port: (%s)", portname));
			default:
				throw new IOException(String.format("%s: Unknown port error %s: open tcgetattr (%s)",
						Errno.valueOf(posix.errno()), portname));
			}
		}

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
	public void open(Speed speed, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls)
			throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendBreak(int duration) throws IOException {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	public void setDTR(boolean value) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFlowControl(Set<FlowControl> flowControls) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setParity(Parity parity) throws IOException {
		Termios_H.Termios termios = new Termios_H.Termios(runtime);
		if (tf.tcgetattr(fd, termios) != 0) {
			throw new IOException(String.format("%s: Unknown port error %s: open tcgetattr (%s)",
					Errno.valueOf(posix.errno()), portname));
		}
		if (TermiosFlags.PAREXT.defined()) {
			termios.c_cflag.set(termios.c_cflag.intValue() & ~(TermiosFlags.PARENB.intValue()
					| TermiosFlags.PARODD.intValue() | TermiosFlags.PAREXT.intValue())); // Clear parity settings
		} else if (TermiosFlags.CMSPAR.defined()) {
			termios.c_cflag.set(termios.c_cflag.intValue() & ~(TermiosFlags.PARENB.intValue()
					| TermiosFlags.PARODD.intValue() | TermiosFlags.CMSPAR.intValue())); // Clear parity settings
		} else {
			termios.c_cflag.set(
					termios.c_cflag.intValue() & ~(TermiosFlags.PARENB.intValue() | TermiosFlags.PARODD.intValue())); // Clear
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
				termios.c_cflag.set(
						termios.c_cflag.intValue() | (TermiosFlags.PARENB.intValue() | TermiosFlags.PAREXT.intValue()));
				termios.c_iflag.set(termios.c_iflag.intValue() | TermiosFlags.INPCK.intValue());
			} else if (TermiosFlags.CMSPAR.defined()) {
				termios.c_cflag.set(
						termios.c_cflag.intValue() | (TermiosFlags.PARENB.intValue() | TermiosFlags.CMSPAR.intValue()));
				termios.c_iflag.set(termios.c_iflag.intValue() | TermiosFlags.INPCK.intValue());
			}
			break;
		default:
			throw new IllegalArgumentException("Wrong parity");
		}

		if (tf.tcsetattr(fd, TermiosFlags.TCSANOW.intValue(), termios) != 0) {
			throw new IOException(String.format("%s: Unknown port error %s: open tcgetattr (%s)",
					Errno.valueOf(posix.errno()), portname));
		}
	}

	@Override
	public void setRTS(boolean value) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSpeed(Speed speed) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStopBits(StopBits stopBits) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTimeouts(int interByteReadTimeout, int overallReadTimeout, int overallWriteTimeout)
			throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setXOFFChar(char c) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setXONChar(char c) throws IOException {
		// TODO Auto-generated method stub

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

}
