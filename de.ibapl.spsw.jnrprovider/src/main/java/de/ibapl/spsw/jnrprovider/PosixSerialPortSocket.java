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
import jnr.constants.platform.Errno;
import jnr.constants.platform.OpenFlags;
import jnr.posix.POSIX;
import jnr.posix.POSIXFactory;

public class PosixSerialPortSocket implements SerialPortSocket {

	private int fd = -1;
	private POSIX posix;
	private final String portname;

	public PosixSerialPortSocket(String portname) {
		this.portname = portname;
		posix = POSIXFactory.getPOSIX(); // Was new DummyPOSIXHandler(), true);
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
		// TODO Auto-generated method stub
		return null;
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
/*TODO We got this far ... her the real work starts....
		struct termios settings;
		if (posix.tcgetattr(fd, settings)) {
			posix.close(fd); //since 2.7.0
			fd = -1;
			switch (Errno.valueOf(posix.errno())) {
				case ENOTTY:
					throw new IOException(String.format("Not a serial port: (%s)", portname));
				default:
					throw new IOException(
							String.format("%s: Unknown port error %s: open tcgetattr (%s)", Errno.valueOf(posix.errno()), portname));
			}
			}
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
		// TODO Auto-generated method stub

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
