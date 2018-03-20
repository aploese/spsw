/*-
 * #%L
 * SPSW Provider
 * %%
 * Copyright (C) 2009 - 2017 Arne Plöse
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

import java.util.Iterator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.tests.tags.BaselineTest;
import de.ibapl.spsw.tests.tags.HighSpeedTest;
import de.ibapl.spsw.tests.tags.LowSpeedTest;

/**
 * Unit test for simple App. Timeout is computed 8 data bits + 2 stop bits +
 * parity bit + start bit == 12
 */
public abstract class AbstractReadWriteAllBaudratesTest extends AbstractReadWriteTest {

	public Iterator<PortConfiguration> getLowSpeedPortConfigurations() {
		return new PortConfigurationFactory().setBuffersize(64).getBaudrateIterator(Baudrate.B50, Baudrate.B600);
	}

	public Iterator<PortConfiguration> getHighSpeedPortConfigurations() {
		return new PortConfigurationFactory().setBuffersize(64).getBaudrateIterator(Baudrate.B460800,
				Baudrate.B4000000);
	}

	public Iterator<PortConfiguration> getBaselinePortConfigurations() {
		return new PortConfigurationFactory().setBuffersize(64).getBaudrateIterator(Baudrate.B1200, Baudrate.B230400);
	}

	@LowSpeedTest
	@ParameterizedTest
	@MethodSource({ "getLowSpeedPortConfigurations" })
	public void test_WriteBytes_ReadBytes_LowSpeed(PortConfiguration pc) throws Exception {
		writeBytes_ReadBytes(pc);
	}

	@LowSpeedTest
	@ParameterizedTest
	@MethodSource({ "getLowSpeedPortConfigurations" })
	public void test_WriteBytes_ReadSingle_LowSpeed(PortConfiguration pc) throws Exception {
		writeBytes_ReadSingle(pc);
	}

	@LowSpeedTest
	@ParameterizedTest
	@MethodSource({ "getLowSpeedPortConfigurations" })
	public void test_WriteSingle_ReadBytes_LowSpeed(PortConfiguration pc) throws Exception {
		writeSingle_ReadBytes(pc);
	}

	@LowSpeedTest
	@ParameterizedTest
	@MethodSource({ "getLowSpeedPortConfigurations" })
	public void test_WriteSingle_ReadSingle_LowSpeed(PortConfiguration pc) throws Exception {
		writeSingle_ReadSingle(pc);
	}

	@LowSpeedTest
	@ParameterizedTest
	@MethodSource({ "getLowSpeedPortConfigurations" })
	public void test_WriteBytes_ReadBytes_Threaded_LowSpeed(PortConfiguration pc) throws Exception {
		writeBytes_ReadBytes_Threaded(pc);
	}

	@LowSpeedTest
	@ParameterizedTest
	@MethodSource({ "getLowSpeedPortConfigurations" })
	public void test_WriteBytes_ReadSingle_Threaded_LowSpeed(PortConfiguration pc) throws Exception {
		writeBytes_ReadSingle_Threaded(pc);
	}

	@LowSpeedTest
	@ParameterizedTest
	@MethodSource({ "getLowSpeedPortConfigurations" })
	public void test_WriteSingle_ReadBytes_Threaded_LowSpeed(PortConfiguration pc) throws Exception {
		writeSingle_ReadBytes_Threaded(pc);
	}

	@LowSpeedTest
	@ParameterizedTest
	@MethodSource({ "getLowSpeedPortConfigurations" })
	public void test_WriteSingle_ReadSingle_Threaded_LowSpeed(PortConfiguration pc) throws Exception {
		writeSingle_ReadSingle_Threaded(pc);
	}

	@HighSpeedTest
	@ParameterizedTest
	@MethodSource({ "getHighSpeedPortConfigurations" })
	public void test_WriteBytes_ReadBytes_HighSpeed(PortConfiguration pc) throws Exception {
		writeBytes_ReadBytes(pc);
	}

	@HighSpeedTest
	@ParameterizedTest
	@MethodSource({ "getHighSpeedPortConfigurations" })
	public void test_WriteBytes_ReadSingle_TestHighSpeed(PortConfiguration pc) throws Exception {
		writeBytes_ReadSingle(pc);
	}

	@HighSpeedTest
	@ParameterizedTest
	@MethodSource({ "getHighSpeedPortConfigurations" })
	public void test_WriteSingle_ReadBytes_HighSpeed(PortConfiguration pc) throws Exception {
		writeSingle_ReadBytes(pc);
	}

	@HighSpeedTest
	@ParameterizedTest
	@MethodSource({ "getHighSpeedPortConfigurations" })
	public void test_WriteSingle_ReadSingle_HighSpeed(PortConfiguration pc) throws Exception {
		writeSingle_ReadSingle(pc);
	}

	@HighSpeedTest
	@ParameterizedTest
	@MethodSource({ "getHighSpeedPortConfigurations" })
	public void test_WriteBytes_ReadBytes_Threaded_HighSpeed(PortConfiguration pc) throws Exception {
		writeBytes_ReadBytes_Threaded(pc);
	}

	@HighSpeedTest
	@ParameterizedTest
	@MethodSource({ "getHighSpeedPortConfigurations" })
	public void test_WriteBytes_ReadSingle_Threaded_HighSpeed(PortConfiguration pc) throws Exception {
		writeBytes_ReadSingle_Threaded(pc);
	}

	@HighSpeedTest
	@ParameterizedTest
	@MethodSource({ "getHighSpeedPortConfigurations" })
	public void test_WriteSingle_ReadBytes_Threaded_HighSpeed(PortConfiguration pc) throws Exception {
		writeSingle_ReadBytes_Threaded(pc);
	}

	@HighSpeedTest
	@ParameterizedTest
	@MethodSource({ "getHighSpeedPortConfigurations" })
	public void test_WriteSingle_ReadSingle_Threaded_HighSpeed(PortConfiguration pc) throws Exception {
		writeSingle_ReadSingle_Threaded(pc);
	}

	@BaselineTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_WriteBytes_ReadBytes_(PortConfiguration pc) throws Exception {
		writeBytes_ReadBytes(pc);
	}
	
	@BaselineTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_WriteBytes_ReadSingle(PortConfiguration pc) throws Exception {
		writeBytes_ReadSingle(pc);
	}
	
	@BaselineTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_WriteSingle_ReadBytes(PortConfiguration pc) throws Exception {
		writeSingle_ReadBytes(pc);
	}

	@BaselineTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_WriteSingle_ReadSingle(PortConfiguration pc) throws Exception {
		writeSingle_ReadSingle(pc);
	}

	@BaselineTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_WriteBytes_ReadBytes_Threaded(PortConfiguration pc) throws Exception {
		writeBytes_ReadBytes_Threaded(pc);
	}

	@BaselineTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_WriteBytes_ReadSingle_Threaded(PortConfiguration pc) throws Exception {
		writeBytes_ReadSingle_Threaded(pc);
	}

	@BaselineTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_WriteSingle_ReadBytes_Threaded(PortConfiguration pc) throws Exception {
		writeSingle_ReadBytes_Threaded(pc);
	}

	@BaselineTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_WriteSingle_ReadSingle_Threaded(PortConfiguration pc) throws Exception {
		writeSingle_ReadSingle_Threaded(pc);
	}


}