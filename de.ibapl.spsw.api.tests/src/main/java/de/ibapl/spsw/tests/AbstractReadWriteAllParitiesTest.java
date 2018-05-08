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

import java.util.Iterator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.ibapl.spsw.tests.tags.BaselineTest;
import de.ibapl.spsw.tests.tags.ByteChannelTest;
import de.ibapl.spsw.tests.tags.IOStreamTest;

/**
 * @author Arne Plöse
 */
public abstract class AbstractReadWriteAllParitiesTest extends AbstractReadWriteTest {

	public Iterator<PortConfiguration> getBaselinePortConfigurations() {
		return new PortConfigurationFactory().setBuffersize(64).getParityIterator();
	}

	@IOStreamTest
	@BaselineTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_WriteBytes_ReadBytes(PortConfiguration pc) throws Exception {
		write_Read_nonThreaded(SocketIoType.STREAM, SocketIoType.STREAM, pc);
	}

	@IOStreamTest
	@BaselineTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_WriteBytes_ReadSingle(PortConfiguration pc) throws Exception {
		write_Read_nonThreaded(SocketIoType.STREAM, SocketIoType.SINGLE_BYTE, pc);
	}

	@IOStreamTest
	@BaselineTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_WriteSingle_ReadBytes(PortConfiguration pc) throws Exception {
		write_Read_nonThreaded(SocketIoType.SINGLE_BYTE, SocketIoType.STREAM, pc);
	}

	@BaselineTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_WriteSingle_ReadSingle(PortConfiguration pc) throws Exception {
		write_Read_nonThreaded(SocketIoType.SINGLE_BYTE, SocketIoType.SINGLE_BYTE, pc);
	}

	@IOStreamTest
	@BaselineTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_WriteBytes_ReadBytes_Threaded(PortConfiguration pc) throws Exception {
		write_Read_Threaded(SocketIoType.STREAM, SocketIoType.STREAM, pc);
	}

	@IOStreamTest
	@BaselineTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_WriteBytes_ReadSingle_Threaded(PortConfiguration pc) throws Exception {
		write_Read_Threaded(SocketIoType.STREAM, SocketIoType.SINGLE_BYTE, pc);
	}

	@IOStreamTest
	@BaselineTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_WriteSingle_ReadBytes_Threaded(PortConfiguration pc) throws Exception {
		write_Read_Threaded(SocketIoType.SINGLE_BYTE, SocketIoType.STREAM, pc);
	}

	@BaselineTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_WriteSingle_ReadSingle_Threaded(PortConfiguration pc) throws Exception {
		write_Read_Threaded(SocketIoType.SINGLE_BYTE, SocketIoType.SINGLE_BYTE, pc);
	}

	@ByteChannelTest
	@BaselineTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_Channel_WriteBytes_ReadBytes(PortConfiguration pc) throws Exception {
		write_Read_nonThreaded(SocketIoType.CHANNEL, SocketIoType.CHANNEL, pc);
	}

	@ByteChannelTest
	@BaselineTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_Channel_WriteBytes_ReadSingle(PortConfiguration pc) throws Exception {
		write_Read_nonThreaded(SocketIoType.CHANNEL, SocketIoType.SINGLE_BYTE, pc);
	}

	@ByteChannelTest
	@BaselineTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_Channel_WriteSingle_ReadBytes(PortConfiguration pc) throws Exception {
		write_Read_nonThreaded(SocketIoType.SINGLE_BYTE, SocketIoType.CHANNEL, pc);
	}

	@ByteChannelTest
	@BaselineTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_Channel_WriteBytes_ReadBytes_Threaded(PortConfiguration pc) throws Exception {
		write_Read_Threaded(SocketIoType.CHANNEL, SocketIoType.CHANNEL, pc);
	}

	@ByteChannelTest
	@BaselineTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_Channel_WriteBytes_ReadSingle_Threaded(PortConfiguration pc) throws Exception {
		write_Read_Threaded(SocketIoType.CHANNEL, SocketIoType.SINGLE_BYTE, pc);
	}

	@ByteChannelTest
	@BaselineTest
	@ParameterizedTest
	@MethodSource({ "getBaselinePortConfigurations" })
	public void test_Channel_WriteSingle_ReadBytes_Threaded(PortConfiguration pc) throws Exception {
		write_Read_Threaded(SocketIoType.SINGLE_BYTE, SocketIoType.CHANNEL, pc);
	}

}