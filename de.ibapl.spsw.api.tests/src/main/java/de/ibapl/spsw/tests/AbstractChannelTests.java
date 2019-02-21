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
package de.ibapl.spsw.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import de.ibapl.spsw.tests.tags.BaselineTest;
import de.ibapl.spsw.tests.tags.ByteChannelTest;

public abstract class AbstractChannelTests extends AbstractReadWriteTest {

	@BaselineTest
	@ByteChannelTest
	@Test
	public void test_Channel_Write_Read_Position() throws Exception {
		PortConfiguration pc = new PortConfigurationFactory().ofCurrent();
		open(pc);
		ByteBuffer sendBuffer = ByteBuffer.allocateDirect(64);
		sendBuffer.put("abcdefghijklmnopqrstuvwxyz".getBytes());
		sendBuffer.flip();
		final int writeStart = 2;
		sendBuffer.position(writeStart);

		long written = writeSpc.write(sendBuffer);
		assertEquals(24, written);
		assertEquals(sendBuffer.position(), sendBuffer.limit());

		
		ByteBuffer recBuffer = ByteBuffer.allocateDirect(64);
		recBuffer.position(0);
		recBuffer.limit(2);

		long read = readSpc.read(recBuffer);
		assertEquals(2, read);
		assertEquals(recBuffer.position(), recBuffer.limit());
		recBuffer.flip();
		assertEquals('c', (char)recBuffer.get());
		assertEquals('d', (char)recBuffer.get());
		recBuffer.limit(recBuffer.limit() + 5);
		read = readSpc.read(recBuffer);
		assertEquals(5, read);
		assertEquals(recBuffer.position(), recBuffer.limit());
		recBuffer.flip();
		assertEquals('c', (char)recBuffer.get());
		assertEquals('d', (char)recBuffer.get());
		assertEquals('e', (char)recBuffer.get());
		assertEquals('f', (char)recBuffer.get());
		assertEquals('g', (char)recBuffer.get());
		assertEquals('h', (char)recBuffer.get());
		assertEquals('i', (char)recBuffer.get());
		
	}

}
