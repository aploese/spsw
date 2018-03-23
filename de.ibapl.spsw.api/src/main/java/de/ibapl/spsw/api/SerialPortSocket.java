/*-
 * #%L
 * SPSW API
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
package de.ibapl.spsw.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Native;
import java.util.Set;
import org.osgi.annotation.versioning.ProviderType;

/**
 * The interface for accessing a serial port.
 * 
 * 
 * 
 * There are two general cable configurations used with the RS-232C
 * Communications Standard:
 * <li>
 * Data Terminal Equipment (DTE): IBM PC's, printers, plotters, etc <br>
 * </li>
 * <li>
 * Data Communication Equipment (DCE): modems, multiplexors, etc
 * </li>
 * @see <a href="https://www.wikipedia.org/wiki/Serial_port">www.wikipedia.org/wiki/Serial_port</a>
 */
@ProviderType
public interface SerialPortSocket extends AutoCloseable {

	@Native
	public final static String PORT_IS_CLOSED = "Port is closed";
	@Native
	public final static String PORT_IS_OPEN = "Port is open";

	/**
	 * Calculate the transfer time for given size and port parameters.
	 *  
	 * @param len the length of the byte array to transfer
	 * @param baudrate the used Baudrate
	 * @param dataBits the used DataBits
	 * @param stopBits the used Stopbits
	 * @param parity the used Parity
	 * @return the rounded up tranfer time in ms.
	 */
	static int calculateMillisForBytes(int len, Baudrate baudrate, DataBits dataBits, StopBits stopBits,
			Parity parity) {
		return (int) Math.ceil((len * (1 + dataBits.value + (parity == Parity.NONE ? 0 : 1) + stopBits.value) * 1000.0)
				/ baudrate.value);
	}

	static double calculateMillisPerByte(Baudrate baudrate, DataBits dataBits, StopBits stopBits, Parity parity) {
		return ((1 + dataBits.value + (parity == Parity.NONE ? 0 : 1) + stopBits.value) * 1000.0)
				/ baudrate.value;
	}

	default int calculateMillisForBytes(int len) throws IOException {
		return calculateMillisForBytes(len, getBaudrate(), getDatatBits(), getStopBits(), getParity());
	}

	default double calculateMillisPerByte() throws IOException {
		return calculateMillisPerByte(getBaudrate(), getDatatBits(), getStopBits(), getParity());
	}

	// Not supported under Win boolean isRTS() throws IOException;

	// Not supported under Win boolean isDTR() throws IOException;

	/**
	 * Close port. This method deletes event listener first, then closes the port
	 *
	 * @throws java.io.IOException
	 */
	@Override
	void close() throws IOException;

	/**
	 * @return the current set baudrate.
	 * @throws IOException
	 *             if port is closed
	 */
	Baudrate getBaudrate() throws IOException;

	/**
	 * @return the current set data bits.
	 * @throws IOException
	 *             if port is closed
	 */
	DataBits getDatatBits() throws IOException;

	/**
	 * @return the current set flow control.
	 * @throws IOException
	 *             if port is closed
	 */
	Set<FlowControl> getFlowControl() throws IOException;

	/**
	 * Get bytes count in in buffer of port
	 * The actual size of the in buffer is unknown.
	 *
	 * @return byte count in buffer.
	 * @throws java.io.IOException
	 *             if port is closed
	 *
	 */
	int getInBufferBytesCount() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 *             if port is closed
	 */
	InputStream getInputStream() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 *             if port is closed
	 */
	int getInterByteReadTimeout() throws IOException;

	/**
	 * Get bytes count in out buffer of port
	 * The actual size of the out buffer is unknown.
	 *
	 * @return byte count in out buffer.
	 * @throws java.io.IOException
	 *             if port is closed
	 *
	 */
	int getOutBufferBytesCount() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 *             if port is closed
	 */
	OutputStream getOutputStream() throws IOException;

	/**
	 * Returns setting for the timeout in ms. 0 returns implies that the option is
	 * disabled (i.e., timeout of infinity).
	 *
	 * @return the timeout
	 *
	 * @see #setOverallTimeout(int)
	 */
	int getOverallReadTimeout() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 *             if port is closed
	 */
	int getOverallWriteTimeout() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 *             if port is closed
	 */
	Parity getParity() throws IOException;

	/**
	 * Getting port name to use.
	 *
	 * @return the port name.
	 */
	String getPortName();

	/**
	 * @return 
	 * @throws IOException
	 *             if port is closed
	 */
	StopBits getStopBits() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 *             if port is closed
	 */
	char getXOFFChar() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 *             if port is closed
	 */
	char getXONChar() throws IOException;

	boolean isClosed();

	/**
	 * Clear To Send (CTS) DTE input, DCE is ready to transmit
	 * 
	 * @return
	 * @throws IOException
	 *             if port is closed
	 */
	boolean isCTS() throws IOException;

	/**
	 * Data Carrier Detect (DCD) DTE input, data link established, also known as
	 * Receive Line Signal Detect (RLSD)
	 * 
	 * @return DCD
	 * @throws IOException
	 *             if port is closed
	 */
	boolean isDCD() throws IOException;

	/**
	 * Data Set Ready (DSR) DTE input, DCE is ready to communicate
	 * 
	 * @return DSR
	 * @throws IOException
	 *             if port is closed
	 */
	boolean isDSR() throws IOException;

	/**
	 * Getting port state
	 *
	 * @return true if port is open, otherwise false
	 */
	boolean isOpen();

	/**
	 * Ring Indicator (RI) DTE input, announces incoming call
	 * 
	 * @return
	 * @throws IOException
	 *             if port is closed
	 */
	boolean isRI() throws IOException;

	/**
	 * Port opening <br>
	 * <br>
	 * <b>Note: </b>If port busy <b>TYPE_PORT_BUSY</b> exception will be thrown. If
	 * port not found <b>TYPE_PORT_NOT_FOUND</b> exception will be thrown.
	 *
	 * if port busy is thrown one can unlock SerialPortSockets with calls (in this
	 * order) Runtime.getRuntime().gc(); Runtime.getRuntime().runFinalization();
	 * 
	 * @throws SerialPortException
	 * @throws IllegalStateException
	 *             if port is closed
	 */
	void open() throws IOException;

	/**
	 * Setting the parameters of port
	 *
	 * if port busy is thrown one can unlock SerialPortSockets with calls (in this
	 * order) Runtime.getRuntime().gc(); Runtime.getRuntime().runFinalization();
	 * 
	 * @param baudRate
	 *            data transfer rate
	 * @param dataBits
	 *            number of data bits
	 * @param stopBits
	 *            number of stop bits
	 * @param parity
	 *            parity
	 * @param flowControls
	 * @throws java.io.IOException
	 * @throws IllegalStateException
	 *             if port is closed
	 */
	void open(Baudrate baudRate, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls)
			throws IOException;

	/**
	 * 
	 * @param duration
	 *            the duration in ms.
	 * @throws IOException
	 */
	void sendBreak(int duration) throws IOException;

	/**
	 * @return
	 * @throws IOException
	 *             if port is closed
	 */
	void sendXOFF() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 *             if port is closed
	 */
	void sendXON() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 *             if port is closed
	 */
	void setBaudrate(Baudrate baudrate) throws IOException;

	/**
	 * Set Break singnal
	 *
	 * @param value
	 *            the value
	 * @throws java.io.IOException
	 */
	void setBreak(boolean value) throws IOException;

	/**
	 * @return
	 * @throws IOException
	 *             if port is closed
	 */
	void setDataBits(DataBits dataBits) throws IOException;

	/**
	 * Data Terminal Ready (DTR) DTE output, device ready Change DTR line state
	 *
	 * @param value
	 *            <b>true - ON</b>, <b>false - OFF</b>
	 * @throws java.io.IOException
	 *
	 */
	void setDTR(boolean value) throws IOException;

	/**
	 * @return
	 * @throws IOException
	 *             if port is closed
	 */
	void setFlowControl(Set<FlowControl> flowControls) throws IOException;

	/**
	 * @return
	 * @throws IOException
	 *             if port is closed
	 */
	void setParity(Parity parity) throws IOException;

	/**
	 * Request To Send (RTS) DTE output, DTE would like to transmit Change RTS line
	 * state
	 *
	 * @param value
	 *            <b>true - ON</b>, <b>false - OFF</b>
	 * @throws java.io.IOException
	 *
	 */
	void setRTS(boolean value) throws IOException;

	/**
	 * 
	 * @param stopBits
	 *            The stopbits to set.
	 * @throws IOException
	 * @throws IllegalArgumentException
	 *             if the hardware does not support the new value.
	 */
	void setStopBits(StopBits stopBits) throws IOException;

	/**
	 * Enable/disable the timeout, in milliseconds. With this option set to a
	 * non-zero timeout, a read() call on the InputStream associated with this
	 * Socket will block for only this amount of time. If the timeout expires, a
	 * <B>java.net.SocketTimeoutException</B> is raised, though the Socket is still
	 * valid. The option <B>must</B> be enabled prior to entering the blocking
	 * operation to have effect. The timeout must be {@code > 0}. A timeout of zero
	 * is interpreted as an infinite timeout. {@link SocketOptions#SO_TIMEOUT
	 * SO_TIMEOUT} If a timeout value can't be set (I.E: resolution is a tenth of a
	 * second for posix termios) the next smaller value will be used an returned.
	 * Except if its to small to set. In this case the smallest value will be used
	 * and returned.
	 * 
	 *
	 * A overallReadTimeout and of zero is interpreted as an infinite read timeout.
	 * A overallWriteTimeout and of zero is interpreted as an infinite write
	 * timeout.
	 * 
	 * @param timeout
	 *            the specified timeout, in milliseconds.
	 * @exception SocketException
	 *                if there is an error in the underlying protocol, such as a TCP
	 *                error.
	 * @see #getOverallTimeout()
	 * @throws IllegalStateException
	 *             if port is closed
	 */
	void setTimeouts(int interByteReadTimeout, int overallReadTimeout, int overallWriteTimeout) throws IOException;

	/**
	 * @return
	 * @throws IOException
	 *             if port is closed
	 */
	void setXOFFChar(char c) throws IOException;

	/**
	 * @return
	 * @throws IOException
	 *             if port is closed
	 */
	void setXONChar(char c) throws IOException;

}
