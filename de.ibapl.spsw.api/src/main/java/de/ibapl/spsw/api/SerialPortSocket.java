package de.ibapl.spsw.api;

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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import org.osgi.annotation.versioning.ProviderType;

/**
 *
 * @author scream3r
 */
@ProviderType
public interface SerialPortSocket extends Closeable {

	public final static String PORT_IS_OPEN = "Port is open";
	public final static String PORT_NOT_OPEN = "Port not open";

	boolean isClosed();

	boolean isCTS() throws IOException;

	boolean isDSR() throws IOException;

	// Not supported under Win boolean isRTS() throws IOException;

	// Not supported under Win boolean isDTR() throws IOException;

	boolean isIncommingRI() throws IOException;

	InputStream getInputStream() throws IOException;

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
	 */
	void openAsIs() throws IOException;

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
	 */
	void openRaw() throws IOException;

	void openTerminal() throws IOException;

	void openModem() throws IOException;

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
	 *
	 *
	 * @since 0.8
	 */
	void openRaw(Baudrate baudRate, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls)
			throws IOException;

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

	void setXONChar(char c) throws IOException;

	void setXOFFChar(char c) throws IOException;

	char getXONChar() throws IOException;

	char getXOFFChar() throws IOException;

	/**
	 * 
	 * @param duration
	 *            the duratiuon in ms.
	 * @throws IOException
	 */
	void sendBreak(int duration) throws IOException;

	void sendXON() throws IOException;

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

	void setFlowControl(Set<FlowControl> flowControls) throws IOException;

	void setBaudrate(Baudrate baudrate) throws IOException;

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

	void setParity(Parity parity) throws IOException;

	Baudrate getBaudrate() throws IOException;

	DataBits getDatatBits() throws IOException;

	StopBits getStopBits() throws IOException;

	Parity getParity() throws IOException;

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

	int getInterByteReadTimeout() throws IOException;

	int getOverallWriteTimeout() throws IOException;

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
	 */
	void setTimeouts(int interByteReadTimeout, int overallReadTimeout, int overallWriteTimeout) throws IOException;

}
