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

import java.util.Iterator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.tests.tags.BaselineTest;
import de.ibapl.spsw.tests.tags.ByteChannelTest;
import de.ibapl.spsw.tests.tags.HighSpeedTest;
import de.ibapl.spsw.tests.tags.IOStreamTest;
import de.ibapl.spsw.tests.tags.LowSpeedTest;

/**
 * @author Arne Plöse
 */
public abstract class AbstractReadWriteAllSpeedsTest extends AbstractReadWriteTest {

	public Iterator<PortConfiguration> getLowSpeedPortConfigurations() {
		return new PortConfigurationFactory().setBuffersize(64).getSpeedIterator(Speed._50_BPS, Speed._600_BPS);
	}

	public Iterator<PortConfiguration> getHighSpeedPortConfigurations() {
		return new PortConfigurationFactory().setBuffersize(64).getSpeedIterator(Speed._19200_BPS, Speed._4000000_BPS);
	}

	public Iterator<PortConfiguration> getBaselinePortConfigurations() {
		return new PortConfigurationFactory().setBuffersize(64).getSpeedIterator(Speed._1200_BPS, Speed._9600_BPS);
	}

	@IOStreamTest
	@LowSpeedTest
	@ParameterizedTest
	@MethodSource({ "getLowSpeedPortConfigurations" })
	public void test_WriteBytes_ReadBytes_LowSpeed(PortConfiguration pc) throws Exception {
		write_Read_nonThreaded(SocketIoType.STREAM, SocketIoType.STREAM, pc);
	}

	@IOStreamTest
	@LowSpeedTest
	@ParameterizedTest
	@MethodSource({ "getLowSpeedPortConfigurations" })
	public void test_WriteBytes_ReadSingle_LowSpeed(PortConfiguration pc) throws Exception {
		write_Read_nonThreaded(SocketIoType.STREAM, SocketIoType.SINGLE_BYTE, pc);
	}

	@IOStreamTest
	@LowSpeedTest
	@ParameterizedTest
	@MethodSource({ "getLowSpeedPortConfigurations" })
	public void test_WriteSingle_ReadBytes_LowSpeed(PortConfiguration pc) throws Exception {
		write_Read_nonThreaded(SocketIoType.SINGLE_BYTE, SocketIoType.STREAM, pc);
	}

	@LowSpeedTest
	@ParameterizedTest
	@MethodSource({ "getLowSpeedPortConfigurations" })
	public void test_WriteSingle_ReadSingle_LowSpeed(PortConfiguration pc) throws Exception {
		write_Read_nonThreaded(SocketIoType.SINGLE_BYTE, SocketIoType.SINGLE_BYTE, pc);
	}

	@IOStreamTest
	@LowSpeedTest
	@ParameterizedTest
	@MethodSource({ "getLowSpeedPortConfigurations" })
	public void test_WriteBytes_ReadBytes_Threaded_LowSpeed(PortConfiguration pc) throws Exception {
		write_Read_Threaded(SocketIoType.STREAM, SocketIoType.STREAM, pc);
	}

	@IOStreamTest
	@LowSpeedTest
	@ParameterizedTest
	@MethodSource({ "getLowSpeedPortConfigurations" })
	public void test_WriteBytes_ReadSingle_Threaded_LowSpeed(PortConfiguration pc) throws Exception {
		write_Read_Threaded(SocketIoType.STREAM, SocketIoType.SINGLE_BYTE, pc);
	}

	@IOStreamTest
	@LowSpeedTest
	@ParameterizedTest
	@MethodSource({ "getLowSpeedPortConfigurations" })
	public void test_WriteSingle_ReadBytes_Threaded_LowSpeed(PortConfiguration pc) throws Exception {
		write_Read_Threaded(SocketIoType.SINGLE_BYTE, SocketIoType.STREAM, pc);
	}

	@LowSpeedTest
	@ParameterizedTest
	@MethodSource({ "getLowSpeedPortConfigurations" })
	public void test_WriteSingle_ReadSingle_Threaded_LowSpeed(PortConfiguration pc) throws Exception {
		write_Read_Threaded(SocketIoType.SINGLE_BYTE, SocketIoType.SINGLE_BYTE, pc);
	}

	@ByteChannelTest
	@LowSpeedTest
	@ParameterizedTest
	@MethodSource({ "getLowSpeedPortConfigurations" })
	public void test_Channel_WriteBytes_ReadBytes_LowSpeed(PortConfiguration pc) throws Exception {
		write_Read_nonThreaded(SocketIoType.CHANNEL, SocketIoType.CHANNEL, pc);
	}

	@ByteChannelTest
	@LowSpeedTest
	@ParameterizedTest
	@MethodSource({ "getLowSpeedPortConfigurations" })
	public void test_Channel_WriteBytes_ReadSingle_LowSpeed(PortConfiguration pc) throws Exception {
		write_Read_nonThreaded(SocketIoType.CHANNEL, SocketIoType.SINGLE_BYTE, pc);
	}

	@ByteChannelTest
	@LowSpeedTest
	@ParameterizedTest
	@MethodSource({ "getLowSpeedPortConfigurations" })
	public void test_Channel_WriteSingle_ReadBytes_LowSpeed(PortConfiguration pc) throws Exception {
		write_Read_nonThreaded(SocketIoType.SINGLE_BYTE, SocketIoType.CHANNEL, pc);
	}

	@ByteChannelTest
	@LowSpeedTest
	@ParameterizedTest
	@MethodSource({ "getLowSpeedPortConfigurations" })
	public void test_Channel_WriteBytes_ReadBytes_Threaded_LowSpeed(PortConfiguration pc) throws Exception {
		write_Read_Threaded(SocketIoType.CHANNEL, SocketIoType.CHANNEL, pc);
	}

	@ByteChannelTest
	@LowSpeedTest
	@ParameterizedTest
	@MethodSource({ "getLowSpeedPortConfigurations" })
	public void test_Channel_WriteBytes_ReadSingle_Threaded_LowSpeed(PortConfiguration pc) throws Exception {
		write_Read_Threaded(SocketIoType.CHANNEL, SocketIoType.SINGLE_BYTE, pc);
	}

	@ByteChannelTest
	@LowSpeedTest
	@ParameterizedTest
	@MethodSource({ "getLowSpeedPortConfigurations" })
	public void test_Channel_WriteSingle_ReadBytes_Threaded_LowSpeed(PortConfiguration pc) throws Exception {
		write_Read_Threaded(SocketIoType.SINGLE_BYTE, SocketIoType.CHANNEL, pc);
	}

	@IOStreamTest
	@HighSpeedTest
	@ParameterizedTest
	@MethodSource({ "getHighSpeedPortConfigurations" })
	public void test_WriteBytes_ReadBytes_HighSpeed(PortConfiguration pc) throws Exception {
		write_Read_nonThreaded(SocketIoType.STREAM, SocketIoType.STREAM, pc);
	}

	@IOStreamTest
	@HighSpeedTest
	@ParameterizedTest
	@MethodSource({ "getHighSpeedPortConfigurations" })
	public void test_WriteBytes_ReadSingle_HighSpeed(PortConfiguration pc) throws Exception {
		write_Read_nonThreaded(SocketIoType.STREAM, SocketIoType.SINGLE_BYTE, pc);
	}

	@IOStreamTest
	@HighSpeedTest
	@ParameterizedTest
	@MethodSource({ "getHighSpeedPortConfigurations" })
	public void test_WriteSingle_ReadBytes_HighSpeed(PortConfiguration pc) throws Exception {
		write_Read_nonThreaded(SocketIoType.SINGLE_BYTE, SocketIoType.STREAM, pc);
	}

	@HighSpeedTest
	@ParameterizedTest
	@MethodSource({ "getHighSpeedPortConfigurations" })
	public void test_WriteSingle_ReadSingle_HighSpeed(PortConfiguration pc) throws Exception {
		write_Read_nonThreaded(SocketIoType.SINGLE_BYTE, SocketIoType.SINGLE_BYTE, pc);
	}

	@IOStreamTest
	@HighSpeedTest
	@ParameterizedTest
	@MethodSource({ "getHighSpeedPortConfigurations" })
	public void test_WriteBytes_ReadBytes_Threaded_HighSpeed(PortConfiguration pc) throws Exception {
		write_Read_Threaded(SocketIoType.STREAM, SocketIoType.STREAM, pc);
	}

	@IOStreamTest
	@HighSpeedTest
	@ParameterizedTest
	@MethodSource({ "getHighSpeedPortConfigurations" })
	public void test_WriteBytes_ReadSingle_Threaded_HighSpeed(PortConfiguration pc) throws Exception {
		write_Read_Threaded(SocketIoType.STREAM, SocketIoType.SINGLE_BYTE, pc);
	}

	@IOStreamTest
	@HighSpeedTest
	@ParameterizedTest
	@MethodSource({ "getHighSpeedPortConfigurations" })
	public void test_WriteSingle_ReadBytes_Threaded_HighSpeed(PortConfiguration pc) throws Exception {
		write_Read_Threaded(SocketIoType.SINGLE_BYTE, SocketIoType.STREAM, pc);
	}

	@HighSpeedTest
	@ParameterizedTest
	@MethodSource({ "getHighSpeedPortConfigurations" })
	public void test_WriteSingle_ReadSingle_Threaded_HighSpeed(PortConfiguration pc) throws Exception {
		write_Read_Threaded(SocketIoType.SINGLE_BYTE, SocketIoType.SINGLE_BYTE, pc);
	}

	@ByteChannelTest
	@HighSpeedTest
	@ParameterizedTest
	@MethodSource({ "getHighSpeedPortConfigurations" })
	public void test_Channel_WriteBytes_ReadBytes_HighSpeed(PortConfiguration pc) throws Exception {
		write_Read_nonThreaded(SocketIoType.CHANNEL, SocketIoType.CHANNEL, pc);
	}

	@ByteChannelTest
	@HighSpeedTest
	@ParameterizedTest
	@MethodSource({ "getHighSpeedPortConfigurations" })
	public void test_Channel_WriteBytes_ReadSingle_HighSpeed(PortConfiguration pc) throws Exception {
		write_Read_nonThreaded(SocketIoType.CHANNEL, SocketIoType.SINGLE_BYTE, pc);
	}

	@ByteChannelTest
	@HighSpeedTest
	@ParameterizedTest
	@MethodSource({ "getHighSpeedPortConfigurations" })
	public void test_Channel_WriteSingle_ReadBytes_HighSpeed(PortConfiguration pc) throws Exception {
		write_Read_nonThreaded(SocketIoType.SINGLE_BYTE, SocketIoType.CHANNEL, pc);
	}

	@ByteChannelTest
	@HighSpeedTest
	@ParameterizedTest
	@MethodSource({ "getHighSpeedPortConfigurations" })
	public void test_Channel_WriteBytes_ReadBytes_Threaded_HighSpeed(PortConfiguration pc) throws Exception {
		write_Read_Threaded(SocketIoType.CHANNEL, SocketIoType.CHANNEL, pc);
	}

	@ByteChannelTest
	@HighSpeedTest
	@ParameterizedTest
	@MethodSource({ "getHighSpeedPortConfigurations" })
	public void test_Channel_WriteBytes_ReadSingle_Threaded_HighSpeed(PortConfiguration pc) throws Exception {
		write_Read_Threaded(SocketIoType.CHANNEL, SocketIoType.SINGLE_BYTE, pc);
	}

	@ByteChannelTest
	@HighSpeedTest
	@ParameterizedTest
	@MethodSource({ "getHighSpeedPortConfigurations" })
	public void test_Channel_WriteSingle_ReadBytes_Threaded_HighSpeed(PortConfiguration pc) throws Exception {
		write_Read_Threaded(SocketIoType.SINGLE_BYTE, SocketIoType.CHANNEL, pc);
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