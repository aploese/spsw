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

import java.util.Set;

import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;

/**
 * Helper class for iterative tests.
 * 
 * @author Arne Plöse
 *
 */
public interface PortConfiguration {
	final static int TEST_TIMEOUT_OFFSET = 1000;
	final static int TEST_TIMEOUT_MULTIPLYER = 20;

	Speed getSpeed();

	DataBits getDataBits();

	Parity getParity();

	StopBits getStopBits();

	Set<FlowControl> getFlowControl();

	int getInterByteReadTimeout();

	int getOverallWriteTimeout();

	int getOverallReadTimeout();

	int getBufferSize();

	default int calcMaxTransferTime() {
		return TEST_TIMEOUT_OFFSET + SerialPortSocket.calculateMillisForCharacters(getBufferSize(), getSpeed(),
				getDataBits(), getStopBits(), getParity());
	}

	default long getTestTimeout() {
		return calcMaxTransferTime() * TEST_TIMEOUT_MULTIPLYER;
	}

}
