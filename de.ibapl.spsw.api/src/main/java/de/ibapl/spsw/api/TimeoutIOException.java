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
package de.ibapl.spsw.api;

import java.io.InterruptedIOException;

/**
 * Signals that a timeout has occurred on a {@link SerialPortSocket} read or
 * write.
 * 
 * @author Arne Plöse
 *
 */
public class TimeoutIOException extends InterruptedIOException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new TimeoutIOException with a detail message.
	 * 
	 * @param message
	 *            the detail message
	 */
	public TimeoutIOException(String message) {
		super(message);
	}

	public TimeoutIOException(String message, int bytesTransferred) {
		super(message);
                this.bytesTransferred = bytesTransferred;
	}
	/**
	 * Construct a new TimeoutIOException with no detailed message.
	 */
	public TimeoutIOException() {
	}

}
