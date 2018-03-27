/*-
 * #%L
 * SPSW Provider
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
