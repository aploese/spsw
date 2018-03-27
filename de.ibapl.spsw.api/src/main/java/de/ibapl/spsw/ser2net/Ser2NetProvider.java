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
package de.ibapl.spsw.ser2net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Set;

import javax.net.SocketFactory;

import org.osgi.annotation.versioning.ProviderType;

import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.api.TimeoutIOException;

/**
 * Accesses a remote serial device on a differrent machine over
 * <a href="http://ser2net.sourceforge.net/">ser2net</a> connection. Currently
 * set or change of port parameters, dending BREAK or acess or set of line
 * status (RTS/CTS...) is not supported.
 * 
 * @author Arne Plöse
 *
 */
@ProviderType
public class Ser2NetProvider implements SerialPortSocket {

	protected class InputStreamWrapper extends InputStream {

		private InputStreamWrapper() throws IOException {
			origin = dataSocket.getInputStream();
		}

		private final InputStream origin;

		@Override
		public void close() throws IOException {
			Ser2NetProvider.this.close();
		}

		@Override
		public int read() throws IOException {
			try {
				return origin.read();
			} catch (SocketTimeoutException e) {
				TimeoutIOException timeoutIOException = new TimeoutIOException(e.getMessage());
				timeoutIOException.bytesTransferred = e.bytesTransferred;
				timeoutIOException.initCause(e);
				throw timeoutIOException;
			}
		}

		@Override
		public int read(byte b[]) throws IOException {
			try {
				return origin.read(b);
			} catch (SocketTimeoutException e) {
				TimeoutIOException timeoutIOException = new TimeoutIOException(e.getMessage());
				timeoutIOException.bytesTransferred = e.bytesTransferred;
				timeoutIOException.initCause(e);
				throw timeoutIOException;
			}
		}

		@Override
		public int read(byte b[], int off, int len) throws IOException {
			try {
				return origin.read(b, off, len);
			} catch (SocketTimeoutException e) {
				TimeoutIOException timeoutIOException = new TimeoutIOException(e.getMessage());
				timeoutIOException.bytesTransferred = e.bytesTransferred;
				timeoutIOException.initCause(e);
				throw timeoutIOException;
			}
		}

		@Override
		public int available() throws IOException {
			return origin.available();
		}
	}

	protected class OutputStreamWrapper extends OutputStream {

		private OutputStreamWrapper() throws IOException {
			origin = dataSocket.getOutputStream();

		}

		private final OutputStream origin;

		@Override
		public void close() throws IOException {
			Ser2NetProvider.this.close();
		}

		@Override
		public void write(int b) throws IOException {
			origin.write(b);
		}

		@Override
		public void write(byte b[]) throws IOException {
			origin.write(b);
		}

		@Override
		public void write(byte b[], int off, int len) throws IOException {
			origin.write(b, off, len);
		}

		@Override
		public void flush() throws IOException {
			origin.flush();
		}

	}

	private Socket dataSocket;
	private Socket controlSocket;
	private String host;
	private int dataPort;
	private int controlPort;
	private Speed speed;
	private Parity parity;
	private StopBits stopBits;
	private DataBits dataBits;
	private Set<FlowControl> flowControl;
	private InputStreamWrapper is;
	private OutputStreamWrapper os;

	public Ser2NetProvider(String host, int dataPort, int controlPort) {
		this.host = host;
		this.dataPort = dataPort;
		this.controlPort = controlPort;
	}

	public Ser2NetProvider(String host, int dataPort) throws IOException {
		this(host, dataPort, -1);
	}

	@Override
	public boolean isClosed() {
		return dataSocket == null ? true : dataSocket.isClosed();
	}

	@Override
	public boolean isCTS() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDSR() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDCD() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRI() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public synchronized InputStream getInputStream() throws IOException {
		if (isClosed()) {
			throw new IOException(PORT_IS_CLOSED);
		}
		if (is == null) {
			is = new InputStreamWrapper();
		}
		return is;
	}

	@Override
	public synchronized OutputStream getOutputStream() throws IOException {
		if (isClosed()) {
			throw new IOException(PORT_IS_CLOSED);
		}
		if (os == null) {
			os = new OutputStreamWrapper();
		}
		return os;
	}

	@Override
	public String getPortName() {
		return host + ":" + dataPort;
	}

	@Override
	public boolean isOpen() {
		return !isClosed();
	}

	@Override
	public void open() throws IOException {
		if (isOpen()) {
			throw new IOException(PORT_IS_OPEN);
		}
		dataSocket = SocketFactory.getDefault().createSocket(host, dataPort);
		if (controlPort != -1) {
			controlSocket = SocketFactory.getDefault().createSocket(host, controlPort);
		}
	}

	@Override
	public void open(Speed speed, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls)
			throws IOException {
		this.speed = speed;
		this.dataBits = dataBits;
		this.stopBits = stopBits;
		this.parity = parity;
		this.flowControl = flowControls;
		this.open();
	}

	@Override
	public synchronized void close() throws IOException {
		if (isClosed()) {
			return;
		}

		final Socket s = dataSocket;
		dataSocket = null;
		is = null;
		os = null;

		s.close();
		if (controlSocket != null) {
			controlSocket.close();
		}
	}

	@Override
	public void setRTS(boolean value) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDTR(boolean value) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setXONChar(char c) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setXOFFChar(char c) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public char getXONChar() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public char getXOFFChar() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void sendBreak(int duration) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendXON() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendXOFF() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getInBufferBytesCount() throws IOException {
		return dataSocket.getInputStream().available();
	}

	@Override
	public int getOutBufferBytesCount() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setBreak(boolean value) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFlowControl(Set<FlowControl> flowControls) throws IOException {
		this.flowControl = flowControls;
	}

	@Override
	public void setSpeed(Speed speed) throws IOException {
		this.speed = speed;
	}

	@Override
	public void setDataBits(DataBits dataBits) throws IOException {
		this.dataBits = dataBits;
	}

	@Override
	public void setStopBits(StopBits stopBits) throws IOException {
		this.stopBits = stopBits;
	}

	@Override
	public void setParity(Parity parity) throws IOException {
		this.parity = parity;
	}

	@Override
	public Speed getSpeed() throws IOException {
		return speed;
	}

	@Override
	public DataBits getDatatBits() throws IOException {
		return dataBits;
	}

	@Override
	public StopBits getStopBits() throws IOException {
		return stopBits;
	}

	@Override
	public Parity getParity() throws IOException {
		return parity;
	}

	@Override
	public Set<FlowControl> getFlowControl() throws IOException {
		return flowControl;
	}

	@Override
	public int getOverallReadTimeout() throws IOException {
		return dataSocket.getSoTimeout();
	}

	@Override
	public int getInterByteReadTimeout() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getOverallWriteTimeout() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTimeouts(int interByteReadTimeout, int overallReadTimeout, int overallWriteTimeout)
			throws IOException {
		dataSocket.setSoTimeout(overallReadTimeout);
		// TODO Output Timeout??
	}

}
