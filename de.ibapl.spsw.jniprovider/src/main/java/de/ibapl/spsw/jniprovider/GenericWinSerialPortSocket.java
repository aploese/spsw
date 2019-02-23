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

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.util.List;

/**
/**
 * JNI wrapper around the Windows DCB structure.
 *
 * @author scream3r
 * @author Arne Plöse
 */
public class GenericWinSerialPortSocket extends AbstractSerialPortSocket<GenericWinSerialPortSocket> {

	static native void getWindowsBasedPortNames(List<String> list);

	/**
	 * The file descriptor or handle for this Port
	 */
	private volatile long fd = -1;

	public GenericWinSerialPortSocket(String portName) {
		super(portName);
	}

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
