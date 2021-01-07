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
package de.ibapl.spsw.api;

import java.nio.channels.InterruptibleChannel;

/**
 * An asynchronous IO socket.
 * This should be an for async IO optimized socket.
 * If the data can be read/written without waiting they should read/write with the calling thread. If not, an internal thread reads/writes the data.
 * All callbacks should be done with the currently used thread.
 * 
 * If a read/write finishes wihtout waiting, the calling thread of the callback is the thread that called read/write.
 * If an internal thread was used that thread calls the callbacks.
 * 
 * So if you just need to "fast" process the read/written data, use the thread that calls the callback. If you need "more time" - use some different thread to do so.
 * 
 * This interface is experimental.
 * 
 * @author aploese
 * 
 */
public interface AsyncSerialPortSocket extends SerialPortConfiguration, AsyncByteChannel, InterruptibleChannel {
    
}
