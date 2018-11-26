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
package de.ibapl.spsw.api;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Native;
import java.nio.channels.ByteChannel;
import java.util.Set;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The interface for accessing a serial port.
 * 
 * Port means a serial device like UART, usb to serial converter or even a TCP
 * bridge to an serial device on a different machine. Due to the fact that a
 * character her can have 5,6,7 or 8 data bits, don't confuse characters with
 * bytes, see {@link DataBits} for more details. If you work wit 8 data bytes
 * characters and bytes have the same length.
 * 
 * A implementing class should check permissions with a SecurityManager. It is
 * desired to check fail-fast in the constructor. The {@link FileOutputStream}
 * should be the blueprint.<br>
 * Checking of the closed state should be done lazily to improve performance.
 * The implementations are not required to be thread save.<br>
 * Closing a port with blocked read or write operation should unblock the
 * pending read/write and throw a
 * {@code InterruptedIOException(SerialPortSocket.PORT_IS_CLOSED)} in the Thread
 * of that read/write.<br>
 * It is desired to close any OS resources in an finalizer. This is to to
 * prevent blocked ports in larger systems. A call to the garbage collector can
 * then free the still claimed resources.
 *
 * The meaning of the signal lines (RTS/CTS...) is viewn from this point. There
 * are two general cable configurations used with the RS-232C Communications
 * Standard:
 * <li>Data Terminal Equipment (DTE): IBM PC's, printers, plotters, etc <br>
 * </li>
 * <li>Data Communication Equipment (DCE): modems, multiplexors, etc</li> <br>
 * 
 * @see <a href=
 *      "https://www.wikipedia.org/wiki/Serial_port">www.wikipedia.org/wiki/Serial_port</a>
 * 
 * @author Arne Plöse
 */
//TODO make SerialportSocket implement ByteChannel itself???	
@ProviderType
public interface SerialPortSocket extends AutoCloseable {

	/**
	 * {@value #PORT_IS_CLOSED}. This should be the message of the IOException, if
	 * the operation is not allowed on an closed port.
	 */
	@Native
	public final static String PORT_IS_CLOSED = "Port is closed";
	/**
	 * {@value #PORT_IS_OPEN}. This should be the message of the IOException, if the
	 * operation is not allowed on an open port.
	 */
	@Native
	public final static String PORT_IS_OPEN = "Port is open";

	/**
	 * Calculate the transfer time for given size and port parameters.
	 * 
	 * @param len
	 *            the number of characters to transfer.
	 * @param speed
	 *            the used speed.
	 * @param dataBits
	 *            the used data bits.
	 * @param stopBits
	 *            the used stop bits.
	 * @param parity
	 *            the used parity.
	 * @return the rounded up transfer time in ms.
	 */
	static int calculateMillisForCharacters(int len, Speed speed, DataBits dataBits, StopBits stopBits, Parity parity) {
		return (int) Math.ceil(
				(len * (1 + dataBits.value + (parity == Parity.NONE ? 0 : 1) + stopBits.value) * 1000.0) / speed.value);
	}

	/**
	 * Calculate the transfer time for a single character with the given port
	 * parameters.
	 * 
	 * @param speed
	 *            the used speed.
	 * @param dataBits
	 *            the used data bits.
	 * @param stopBits
	 *            the used stop bits.
	 * @param parity
	 *            the used parity.
	 * @return the transfer time in ms.
	 */
	static double calculateMillisPerCharacter(Speed speed, DataBits dataBits, StopBits stopBits, Parity parity) {
		return ((1 + dataBits.value + (parity == Parity.NONE ? 0 : 1) + stopBits.value) * 1000.0) / speed.value;
	}

	/**
	 * Calculate the speed in charactes/s with the given port parameters. If data
	 * bits == 8 it is Byte/s.
	 * 
	 * @param speed
	 *            the used speed.
	 * @param dataBits
	 *            the used data bits.
	 * @param stopBits
	 *            the used stop bits.
	 * @param parity
	 *            the used parity.
	 * @return the speed in charactes/s.
	 */
	static double calculateSpeedInCharactersPerSecond(Speed speed, DataBits dataBits, StopBits stopBits,
			Parity parity) {
		return (double) speed.value / (1 + dataBits.value + (parity == Parity.NONE ? 0 : 1) + stopBits.value);
	}

	/**
	 * Calculate the transfer time for given size and the current port settings.
	 * 
	 * @param len
	 *            the number of characters to transfer.
	 * @return the rounded up transfer time in ms.
	 */
	default int calculateMillisForCharacters(int len) throws IOException {
		return calculateMillisForCharacters(len, getSpeed(), getDatatBits(), getStopBits(), getParity());
	}

	/**
	 * Calculate the transfer time for a single character with the current port
	 * settings.
	 * 
	 */
	default double calculateMillisPerCharacter() throws IOException {
		return calculateMillisPerCharacter(getSpeed(), getDatatBits(), getStopBits(), getParity());
	}

	/**
	 * Calculate the speed in charactes/s with the current port settings. If data
	 * bits == 8 it is Byte/s.
	 * 
	 * @return the speed in charactes/s.
	 */
	default double calculateSpeedInCharactersPerSecond() throws IOException {
		return calculateSpeedInCharactersPerSecond(getSpeed(), getDatatBits(), getStopBits(), getParity());
	}

	/**
	 * Close port. The port should be reopenable after closure.
	 *
	 * @throws IOException
	 */
	@Override
	void close() throws IOException;

	/**
	 * Returns the ByteChannel for this port.
	 * 
	 * @return the ByteChannel.
	 * @throws IOException
	 *             if port is closed or an error at OS level occurs.
	 */
	ByteChannel getChannel() throws IOException;
	
	/**
	 * Read the number of data bits from the port.
	 * 
	 * @return the current number of data bits in each character.
	 * @throws IOException
	 *             if port is closed or an error at OS level occurs.
	 * @see DataBits
	 */
	DataBits getDatatBits() throws IOException;

	/**
	 * Read the flow control from the port.
	 * 
	 * @return the current flow control.
	 * @throws IOException
	 *             if port is closed or an error at OS level occurs.
	 */
	Set<FlowControl> getFlowControl() throws IOException;

	/**
	 * Read the number of bytes in the in buffer of the port. The actual size of the
	 * in buffer OS dependent.
	 *
	 * @return the number of bytes in the in buffer.
	 * @throws java.io.IOException
	 *             if port is closed or an error at OS level occurs.
	 *
	 */
	int getInBufferBytesCount() throws IOException;

	/**
	 * Returns the InputStream for this port.
	 * 
	 * @return the InputStream.
	 * @throws IOException
	 *             if port is closed or an error at OS level occurs.
	 */
	InputStream getInputStream() throws IOException;

	/**
	 * Returns the inter byte timeout.
	 * 
	 * This timeout will be triggered only if some bytes have been received. If the
	 * time span after receiving the last char is greater than this timeout
	 * {@link InputStream#read(byte[])} or
	 * {@link InputStream#read(byte[], int, int)} will return with what read up to
	 * that point.
	 * 
	 * @return the inter byte timeout in ms.
	 * @throws IOException
	 *             if port is closed or an error at OS level occurs.
	 */
	int getInterByteReadTimeout() throws IOException;

	/**
	 * Returns the number of bytes in the out buffer of port. The actual size of the
	 * out buffer is OS dependent.
	 *
	 * @return the number of bytes in the out buffer.
	 * @throws java.io.IOException
	 *             if port is closed or an error at OS level occurs.
	 *
	 */
	int getOutBufferBytesCount() throws IOException;

	/**
	 * Returns the OutputStream for this port.
	 * 
	 * @return the OutputSTream.
	 * @throws IOException
	 *             if port is closed or an error at OS level occurs.
	 */
	OutputStream getOutputStream() throws IOException;

	/**
	 * Returns setting for the read timeout in ms. 0 returns implies that the option
	 * is disabled (i.e., timeout of infinity). This timeout is the time to wait for
	 * some data to arrive. If this time passes a {@link TimeoutIOException} will be
	 * thrown. {@link TimeoutIOException#bytesTransferred} will always be {@code 0}.
	 *
	 * @return the overall read timeout in ms or {@code 0} for infinity.
	 * @throws IOException
	 *             if port is closed or an error at OS level occurs.
	 *
	 * @see #setTimeouts(int, int, int)
	 */
	int getOverallReadTimeout() throws IOException;

	/**
	 * Returns setting for the write timeout in ms. 0 returns implies that the
	 * option is disabled (i.e., timeout of infinity). This timeout is the time to
	 * wait for some data to write out. If this time passes a
	 * {@link TimeoutIOException} will be thrown.
	 * {@link TimeoutIOException#bytesTransferred} will hold the number of bytes
	 * written to the OS.
	 *
	 * @return the overall read timeout in ms or {@code 0} for infinity.
	 * @throws IOException
	 *             if port is closed or an error at OS level occurs.
	 *
	 * @see #setTimeouts(int, int, int)
	 */
	int getOverallWriteTimeout() throws IOException;

	/**
	 * Read the parity from the port.
	 * 
	 * @return the current parity.
	 * @throws IOException
	 *             if port is closed or an error at OS level occurs.
	 * @see Parity
	 */
	Parity getParity() throws IOException;

	/**
	 * Getting port name to use.
	 *
	 * @return the port name.
	 */
	String getPortName();

	/**
	 * Read the speed in bit/s from the port. the speed of characters/s can be
	 * calculated as follows:
	 * {@code speed / (1 start bit + (5,6,7,8) data bits + (0,1) parity bit + (1,1.5,2) stop bits)}
	 * This is the speed in characters/s and with 8 data bits its byte/s.
	 * 
	 * @return the current speed in bit/s.
	 * @throws IOException
	 *             if port is closed or an error at OS level occurs.
	 */
	Speed getSpeed() throws IOException;

	/**
	 * Read the number of stop bits from the port.
	 * 
	 * @return the current number of stop bits.
	 * @throws IOException
	 *             if port is closed or an error at OS level occurs.
	 * @see StopBits
	 */
	StopBits getStopBits() throws IOException;

	/**
	 * Read the XOFF char from the port.
	 * 
	 * @return the current XOFF char.
	 * @throws IOException
	 *             if port is closed or an error at OS level occurs.
	 */
	char getXOFFChar() throws IOException;

	/**
	 * Read the XON char from the port.
	 * 
	 * @return the XON char
	 * @throws IOException
	 *             if port is closed or an error at OS level occurs.
	 */
	char getXONChar() throws IOException;

	boolean isClosed();

	/**
	 * Read the state of the CTS line from the port. Clear To Send (CTS) DTE input,
	 * DCE is ready to transmit
	 * 
	 * @return {@code true} if CTS is set, otherwise {@code false}.
	 * @throws IOException
	 *             if port is closed or an error at OS level occurs.
	 */
	boolean isCTS() throws IOException;

	/**
	 * Read the state of the DCD line from the port.
	 * 
	 * Data Carrier Detect (DCD) DTE input, data link established, also known as
	 * Receive Line Signal Detect (RLSD)
	 * 
	 * @return {@code true} if DCD is set, otherwise {@code false}.
	 * @throws IOException
	 *             if port is closed or an error at OS level occurs.
	 */
	boolean isDCD() throws IOException;

	/**
	 * Read the state of the DSR line from the port. Data Set Ready (DSR) DTE input,
	 * DCE is ready to communicate
	 * 
	 * @return {@code true} if DSR is set, otherwise {@code false}.
	 * @throws IOException
	 *             if port is closed or an error at OS level occurs.
	 */
	boolean isDSR() throws IOException;

	/**
	 * Returns the open state of the port.
	 *
	 * @return true if port is open, otherwise false.
	 * @see #open()
	 * @see #close()
	 */
	boolean isOpen();

	/**
	 * Read the state of the RI line from the port. Ring Indicator (RI) DTE input,
	 * announces incoming call
	 * 
	 * @return {@code true} if RI is set, otherwise {@code false}.
	 * @throws IOException
	 *             if port is closed or an error at OS level occurs.
	 */
	boolean isRI() throws IOException;

	/**
	 * Open the port.
	 * 
	 * @throws IOException
	 *             if port is already opened or an error at OS level occurs.
	 */
	void open() throws IOException;

	/**
	 * Setting the parameters of port
	 *
	 * @param speed
	 *            the speed in bit/s.
	 * @param dataBits
	 *            the number of data bits.
	 * @param stopBits
	 *            the number of stop bits.
	 * @param parity
	 *            the parity.
	 * @param flowControls
	 *            the flow control.
	 * 
	 * @throws IOException
	 *             if parameters can't be set, port is closed or an error at OS
	 *             level occurred.
	 * @throws IllegalArgumentException
	 *             if one ore more parameters can't be set.
	 */
	void open(Speed speed, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls)
			throws IOException;

	/**
	 * Initiate the port to send break for duration in ms.
	 * 
	 * @param duration
	 *            the duration in ms.
	 * @throws IOException
	 *             if <b>break</b> can't be send, port is closed or an error at OS
	 *             level occurs.
	 */
	void sendBreak(int duration) throws IOException;

	/**
	 * Initiate the port to send the XOFF char.
	 * 
	 * @throws IOException
	 *             if <b>XOFF</b> can't be send, port is closed or an error at OS
	 *             level occurs.
	 */
	void sendXOFF() throws IOException;

	/**
	 * Initiate the port to send the XON char.
	 * 
	 * @throws IOException
	 *             if <b>XON</b> can't be send, port is closed or an error at OS
	 *             level occurs.
	 */
	void sendXON() throws IOException;

	/**
	 * Send the <B>break</B> signal.
	 *
	 * @param value
	 *            if {@code true} send the break signal infinite. otherwise stop
	 *            sending the break signal.
	 * @throws IOException
	 *             if value can't be set, port is closed or an error at OS level
	 *             occurred.
	 */
	void setBreak(boolean value) throws IOException;

	/**
	 * Write the number of data bits to the port. <br>
	 * If the number data bits are not supported by the underlying OS and OEM
	 * drivers one of the following may happen:
	 * <li>The old number data bits are not changed and an IOException or an
	 * IllegalArgumentException is thrown.</li>
	 * <li>The new number data bits are set and an IOException is thrown.</li>
	 * 
	 * @param dataBits
	 *            the new number of data bits.
	 * @throws IOException
	 *             if the number data bits can't be set, port is closed or an error
	 *             at OS level occurred. OR if the hardware does not support the new
	 *             number of data bits.
	 * @throws IllegalArgumentException
	 *             if the hardware does not support the new number of data bits.
	 */
	void setDataBits(DataBits dataBits) throws IOException;

	/**
	 * Set the state of the DTR line of the port.
	 * 
	 * Data Terminal Ready (DTR) DTE output, device ready Change DTR line state.
	 *
	 * @param value
	 *            the new DTR state.
	 * @throws IOException
	 *             if DTR state can't be set, port is closed or an error at OS level
	 *             occurred.
	 */
	void setDTR(boolean value) throws IOException;

	/**
	 * @throws IOException
	 *             if value can't be set, port is closed or an error at OS level
	 *             occurred.
	 * @throws IllegalArgumentException
	 *             if the hardware does not support the new value.
	 */
	void setFlowControl(Set<FlowControl> flowControls) throws IOException;

	/**
	 * Write the parity to the port.
	 * 
	 * @param parity
	 *            the new parity.
	 * 
	 * @throws IOException
	 *             if value can't be set, port is closed or an error at OS level
	 *             occurred.
	 */
	void setParity(Parity parity) throws IOException;

	/**
	 * Set the state of the RTS line of the port.
	 * 
	 * Request To Send (RTS) DTE output, DTE would like to transmit Change RTS line
	 * state
	 *
	 * @param value
	 *            the new RTS state.
	 * @throws IOException
	 *             if value can't be set, port is closed or an error at OS level
	 *             occurred.
	 */
	void setRTS(boolean value) throws IOException;

	/**
	 * Write the speed to the port. <br>
	 * If the speed is not supported by the underlying OS and OEM drivers one of the
	 * following may happen:
	 * <li>The old speed is not changed and an IOException or an
	 * IllegalArgumentException is thrown.</li>
	 * <li>the closest valid speed is set and an IOException or an
	 * IllegalArgumentException is thrown.</li>
	 * <li>The new speed is set and an IOException is thrown.</li>
	 * 
	 * @param speed
	 *            the new speed in bit/s.
	 * @throws IOException
	 *             if speed can't be set, port is closed or an error at OS level
	 *             occurred. OR if the hardware does not support the new speed.
	 * @throws IllegalArgumentException
	 *             if the hardware does not support the new speed.
	 */
	void setSpeed(Speed speed) throws IOException;

	/**
	 * Write the number of stop bits to the port. <br>
	 * If the number stop bits are not supported by the underlying OS and OEM
	 * drivers one of the following may happen:
	 * <li>The old number stop bits are not changed and an IOException or an
	 * IllegalArgumentException is thrown.</li>
	 * <li>The new number stop bits are set and an IOException is thrown.</li>
	 * 
	 * @param stopBits
	 *            the new number of stop bits.
	 * @throws IOException
	 *             if the number stop bits can't be set, port is closed or an error
	 *             at OS level occurred. OR if the hardware does not support the new
	 *             number of stop bits.
	 * @throws IllegalArgumentException
	 *             if the hardware does not support the new number of stop bits.
	 */
	void setStopBits(StopBits stopBits) throws IOException;

	/**
	 * Enable/disable the timeouts, in milliseconds. With the overallReadTimeout
	 * option set to a non-zero timeout, a read() call on the InputStream associated
	 * with this Socket will block for only this amount of time.
	 * 
	 * With the overallWriteTimeout option set to a non-zero timeout, a write() call
	 * on the OutputStream associated with this Socket will block for only this
	 * amount of time.
	 * 
	 * If the overallReadTimeout or overallWriteTimeout expires, a
	 * {@link TimeoutIOException} is raised, though the SerialPortSocket is still
	 * valid. The overallReadTimeout or overallWriteTimeout must be {@code > 0}. A
	 * overallReadTimeout or overallWriteTimeout of zero is interpreted as an
	 * infinite timeout. <br>
	 * With the interbyteReadTimeout set to a non zero value the port will wait for
	 * more incoming bytes within this time span. A value of zero as not to wait for
	 * more bytes. <br>
	 * The options <B>must</B> be enabled prior to entering the blocking operation
	 * to have effect.
	 * 
	 *
	 * @param interByteReadTimeout
	 *            the specified inter byte read timeout, in milliseconds. 0 is not
	 *            to wait.
	 * @param overallReadTimeout
	 *            the overall read timeout, in milliseconds. 0 is wait infinite.
	 * @param overallWriteTimeout
	 *            the overall write timeout, in milliseconds. 0 is wait infinite.
	 * 
	 * @throws IOException
	 *             if value can't be set, port is closed or an error at OS level
	 *             occurred.
	 * 
	 * @see #getInterByteReadTimeout()
	 * @see #getOverallReadTimeout()
	 * @see #getOverallWriteTimeout()
	 */
	void setTimeouts(int interByteReadTimeout, int overallReadTimeout, int overallWriteTimeout) throws IOException;

	/**
	 * Write the XOFF char to the port.
	 * 
	 * @param c
	 *            the <b>XOFF</b> char.
	 * @throws IOException
	 *             if value can't be set, port is closed or an error at OS level
	 *             occurred.
	 */
	void setXOFFChar(char c) throws IOException;

	/**
	 * Write the XON char to the port.
	 * 
	 * @param c
	 *            the <b>XON</b> char.
	 * @throws IOException
	 *             if value can't be set, port is closed or an error at OS level
	 *             occurred.
	 */
	void setXONChar(char c) throws IOException;

}
