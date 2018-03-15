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
 *
 * @author scream3r
 */
@ProviderType
public interface SerialPortSocket extends AutoCloseable {

	@Native
	public final static String PORT_IS_OPEN = "Port is open";
	@Native
	public final static String PORT_IS_CLOSED = "Port is closed";

	boolean isClosed();

	/**
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException if port is closed
	 */
	boolean isCTS() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException if port is closed
	 */
	boolean isDSR() throws IOException;

	// Not supported under Win boolean isRTS() throws IOException;

	// Not supported under Win boolean isDTR() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException if port is closed
	 */
	boolean isIncommingRI() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException if port is closed
	 */
	InputStream getInputStream() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException if port is closed
	 */
	OutputStream getOutputStream() throws IOException;

	/**
	 * Getting port name under operation
	 *
	 * @return Method returns port name under operation as a String
	 */
	String getPortName();

	/**
	 * Getting port state
	 *
	 * @return Method returns true if port is open, otherwise false
	 */
	boolean isOpen();

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
	 * @throws IllegalStateException if port is closed
	 */
	void open() throws IOException;
	
	@Deprecated
	default void openRaw() throws IOException {
		open();
	}

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
	 * @throws IllegalStateException if port is closed
	 */
	void open(Baudrate baudRate, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls)
			throws IOException;
	
	@Deprecated
	default void openRaw(Baudrate baudRate, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls)
			throws IOException {
		open(baudRate, dataBits, stopBits, parity, flowControls);
	}

	/**
	 * Close port. This method deletes event listener first, then closes the port
	 *
	 * @throws java.io.IOException
	 */
	@Override
	void close() throws IOException;

	/**
	 * Change RTS line state
	 *
	 * @param value
	 *            <b>true - ON</b>, <b>false - OFF</b>
	 * @throws java.io.IOException
	 *
	 */
	void setRTS(boolean value) throws IOException;

	/**
	 * Change DTR line state
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
	 * @throws IllegalStateException if port is closed
	 */
	void setXONChar(char c) throws IOException;

	/**
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException if port is closed
	 */
	void setXOFFChar(char c) throws IOException;

	/**
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException if port is closed
	 */
	char getXONChar() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException if port is closed
	 */
	char getXOFFChar() throws IOException;

	/**
	 * 
	 * @param duration
	 *            the duratiuon in ms.
	 * @throws IOException
	 */
	void sendBreak(int duration) throws IOException;

	/**
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException if port is closed
	 */
	void sendXON() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException if port is closed
	 */
	void sendXOFF() throws IOException;

	/**
	 * Get bytes count in in buffer of port
	 *
	 * @return Method returns the array that contains info about bytes count in
	 *         buffers:
	 * @throws java.io.IOException
	 *
	 */
	int getInBufferBytesCount() throws IOException;

	/**
	 * Get bytes count out inbuffer of port
	 *
	 * @return Method returns the array that contains info about bytes count in
	 *         buffers:
	 * @throws java.io.IOException
	 *
	 */
	int getOutBufferBytesCount() throws IOException;

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
	 * @throws IllegalStateException if port is closed
	 */
	void setFlowControl(Set<FlowControl> flowControls) throws IOException;

	/**
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException if port is closed
	 */
	void setBaudrate(Baudrate baudrate) throws IOException;

	/**
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException if port is closed
	 */
	void setDataBits(DataBits dataBits) throws IOException;

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
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException if port is closed
	 */
	void setParity(Parity parity) throws IOException;

	/**
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException if port is closed
	 */
	Baudrate getBaudrate() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException if port is closed
	 */
	DataBits getDatatBits() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException if port is closed
	 */
	StopBits getStopBits() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException if port is closed
	 */
	Parity getParity() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException if port is closed
	 */
	Set<FlowControl> getFlowControl() throws IOException;

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
	 * @throws IllegalStateException if port is closed
	 */
	int getInterByteReadTimeout() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException if port is closed
	 */
	int getOverallWriteTimeout() throws IOException;

	static double calculateMillisPerByte(Baudrate baudrate, DataBits dataBits, StopBits stopBits, Parity parity) {
		return ((1 + dataBits.value + (parity == Parity.NONE ? 0 : 1) + stopBits.value) * 1000.0) / (double)baudrate.value; 
	}

	static int calculateMillisForBytes(int len, Baudrate baudrate, DataBits dataBits, StopBits stopBits, Parity parity) {
		return (int)Math.ceil((len * (1 + dataBits.value + (parity == Parity.NONE ? 0 : 1) + stopBits.value) * 1000.0) / baudrate.value); 
	}

	default double calculateMillisPerByte() throws IOException {
		return calculateMillisPerByte(getBaudrate(), getDatatBits(), getStopBits(), getParity()); 
	}

	default int calculateMillisForBytes(int len) throws IOException {
		return calculateMillisForBytes(len, getBaudrate(), getDatatBits(), getStopBits(), getParity()); 
	}
	
	/**
	 * Enable/disable the timeout, in milliseconds. With this option set to a
	 * non-zero timeout, a read() call on the InputStream associated with this
	 * Socket will block for only this amount of time. If the timeout expires, a
	 * <B>java.net.SocketTimeoutException</B> is raised, though the Socket is still
	 * valid. The option <B>must</B> be enabled prior to entering the blocking
	 * operation to have effect. The timeout must be {@code > 0}. A timeout of zero
	 * is interpreted as an infinite timeout. {@link SocketOptions#SO_TIMEOUT SO_TIMEOUT}
	 * If a timeout value can't be set (I.E:
	 * resolution is a tenth of a second for posix termios) the next smaller value
	 * will be used an returned. Except if its to small to set. In this case the
	 * smallest value will be used and returned.
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
	 * @throws IllegalStateException if port is closed
	 */
	void setTimeouts(int interByteReadTimeout, int overallReadTimeout, int overallWriteTimeout) throws IOException;

}
