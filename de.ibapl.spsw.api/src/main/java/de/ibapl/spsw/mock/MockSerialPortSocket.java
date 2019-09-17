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
package de.ibapl.spsw.mock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Set;

import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;
import java.nio.ByteBuffer;

/**
 * Helper class for test that interact with a SerialPortSocket. The behavior of
 * a SerialPortSocket can be predefined. Example:
 * 
 * <pre>
 * {@code
 * 		mockSerialPortSocket = new MockSerialPortSocket();
		mockSerialPortSocket.expectedWrite("0102");
		mockSerialPortSocket.expectedRead("0201");
		mockSerialPortSocket.expectedWrite("0304");
		mockSerialPortSocket.expectedRead("0403");

		SerialPortSocket serialPortSocket = mockSerialPortSocket;
		
		serialPortSocket.open();
		serialPortSocket.getOutputStream().write(0x01);
		serialPortSocket.getOutputStream().write(0x02);
		assertEquals(0x02, serialPortSocket.getInputStream().read());
		assertEquals(0x01, serialPortSocket.getInputStream().read());
		assertThrows(UnexpectedRequestError.class, () -> {
			serialPortSocket.getInputStream().read(); });
 * 
 * }
 * 
 * <pre>
 * 
 * @author Arne Plöse
 */
public class MockSerialPortSocket implements SerialPortSocket {

    @Override
    public int read(ByteBuffer dst) throws IOException {
        //TODO
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        //TODO
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

	public class DataRequest extends Request<byte[]> {

		DataRequest(byte[] payload, RequestType requestType) {
			super(payload, requestType);
		}
	}

	public class ExceptionRequest extends Request<IOException> {

		ExceptionRequest(IOException payload, RequestType requestType) {
			super(payload, requestType);
		}

	}

	public class MBusTestInputStream extends InputStream {

		int readPtr = 0;

		@Override
		public int available() throws IOException {
			if (!open) {
				throw new IOException();
			}
			if (data.isEmpty()) {
				return 0;
			}
			if (data.getFirst().requestType != RequestType.READ) {
				return 0;
			}
			if (data.getFirst() instanceof ExceptionRequest) {
				return 0;
			}
			if (data.getFirst() instanceof DataRequest) {
				final DataRequest dataRequest = (DataRequest) data.getFirst();
				return dataRequest.payload.length - readPtr;
			} else {
				throw new UnexpectedRequestError("No read data request", data.getFirst().stackException);
			}
		}

		@Override
		public void close() throws IOException {
			MockSerialPortSocket.this.close();
		}

		@Override
		public int read() throws IOException {
			if (!open) {
				throw new IOException(PORT_IS_CLOSED);
			}
			if (data.isEmpty()) {
				throw new UnexpectedRequestError("data is empty");
			}
			if (data.getFirst().requestType != RequestType.READ) {
				throw new UnexpectedRequestError("No Read request", data.getFirst().stackException);
			}
			if (data.getFirst() instanceof ExceptionRequest) {
				final ExceptionRequest exceptionRequest = (ExceptionRequest) data.getFirst();
				data.removeFirst();
				throw exceptionRequest.payload;
			}
			if (data.getFirst() instanceof DataRequest) {
				final DataRequest dataRequest = (DataRequest) data.getFirst();
				int result = dataRequest.payload[readPtr++];
				if (readPtr == dataRequest.payload.length) {
					readPtr = 0;
					data.removeFirst();
				}
				return result;
			} else {
				throw new UnexpectedRequestError("No read data request", data.getFirst().stackException);
			}
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			if (!open) {
				return -1;
			}
			if (data.isEmpty()) {
				throw new UnexpectedRequestError("data is empty");
			}
			if (data.getFirst().requestType != RequestType.READ) {
				throw new UnexpectedRequestError("No Read request", data.getFirst().stackException);
			}
			if (data.getFirst() instanceof ExceptionRequest) {
				final ExceptionRequest exceptionRequest = (ExceptionRequest) data.getFirst();
				data.removeFirst();
				throw exceptionRequest.payload;
			}
			if (data.getFirst() instanceof DataRequest) {
				final DataRequest dataRequest = (DataRequest) data.getFirst();
				int count = dataRequest.payload.length - readPtr;
				if (len < count) {
					count = len;
				}
				System.arraycopy(dataRequest.payload, readPtr, b, off, count);
				readPtr += count;
				if (readPtr == dataRequest.payload.length) {
					readPtr = 0;
					data.removeFirst();
				}
				return count;
			} else {
				throw new UnexpectedRequestError("No read data request", data.getFirst().stackException);
			}
		}

	}

	public class MBusTestOutputStream extends OutputStream {

		private int writePtr = 0;

		@Override
		public void close() throws IOException {
			MockSerialPortSocket.this.close();
		}

		@Override
		public void write(int b) throws IOException {
			if (!open) {
				throw new IOException(PORT_IS_CLOSED);
			}
			if (data.isEmpty()) {
				throw new UnexpectedRequestError("data is empty");
			}
			if (data.getFirst().requestType != RequestType.WRITE) {
				throw new UnexpectedRequestError("No Write request", data.getFirst().stackException);
			}
			if (data.getFirst() instanceof ExceptionRequest) {
				final ExceptionRequest exceptionRequest = (ExceptionRequest) data.getFirst();
				data.removeFirst();
				throw exceptionRequest.payload;
			}
			if (data.getFirst() instanceof DataRequest) {
				final DataRequest dataRequest = (DataRequest) data.getFirst();
				if (b != dataRequest.payload[writePtr++]) {
					throw new UnexpectedRequestError("Not expected write data at " + (writePtr - 1),
							data.getFirst().stackException);
				}
				if (writePtr == dataRequest.payload.length) {
					writePtr = 0;
					data.removeFirst();
				}
			} else {
				throw new UnexpectedRequestError("No read data request", data.getFirst().stackException);
			}
		}

	}

	public class Request<T> {
		public final T payload;
		public final RequestType requestType;
		public final RequestStackException stackException;

		/*
		 * We manipulate the stacktrace for better debugging We now 2 method calls away
		 * from the point we want to show first addRequest() second Data() so remove the
		 * last two we are at the point where addRequest() was called...
		 */
		Request(T payload, RequestType requestType) {
			this.payload = payload;
			this.requestType = requestType;
			this.stackException = new RequestStackException();
			final StackTraceElement[] st = this.stackException.getStackTrace();
			this.stackException.setStackTrace(Arrays.copyOfRange(st, 3, st.length - 3));
		}
	}

	public class RequestStackException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	}

	public enum RequestType {
		READ, WRITE;
	}

	public class UnexpectedRequestError extends Error {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public UnexpectedRequestError(String message) {
			super(message);
		}

		public UnexpectedRequestError(String message, RequestStackException stackException) {
			super(message, stackException);
		}

	}

	public static byte[] ascii2Bytes(String s) {
		byte[] result = new byte[s.length() / 2];

		for (int i = 0; i < (s.length() / 2); i++) {
			result[i] = (byte) Integer.parseInt(s.substring(i * 2, (i * 2) + 2), 16);
		}

		return result;
	}

	public static String bytes2Ascii(byte[] byteArray) {
		StringBuilder sb = new StringBuilder(byteArray.length);

		for (byte b : byteArray) {
			sb.append(String.format("%02x", b));
		}

		return sb.toString();
	}

	private Speed speed;
	LinkedList<Request<?>> data = new LinkedList<>();
	private DataBits dataBits;
	private Set<FlowControl> flowControl;
	private int interByteReadTimeout;
	private MBusTestInputStream is;
	private boolean open;
	private MBusTestOutputStream os;

	private int overallReadTimeout;

	private int overallWriteTimeout;

	private Parity parity;

	private StopBits stopBits;

	private void addRequest(Request<?> d) {
		data.add(d);
	}

	public void addRequest(String writeData, IOException readIOException, int times) {
		DataRequest write = new DataRequest(ascii2Bytes(writeData), RequestType.WRITE);
		ExceptionRequest read = new ExceptionRequest(readIOException, RequestType.READ);
		for (int i = 0; i < times; i++) {
			addRequest(write);
			addRequest(read);
		}
	}

	public void addRequest(String writeData, String readData) {
		addRequest(new DataRequest(ascii2Bytes(writeData), RequestType.WRITE));
		addRequest(new DataRequest(ascii2Bytes(readData), RequestType.READ));
	}

	public boolean allRequestsHandled() {
		return data.isEmpty();
	}

	@Override
	public void close() throws IOException {
		open = false;
	}

	public void expectedRead(IOException ioException) {
		addRequest(new ExceptionRequest(ioException, RequestType.READ));
	}

	public void expectedRead(String data) {
		addRequest(new DataRequest(ascii2Bytes(data), RequestType.READ));
	}

	public void expectedWrite(IOException ioException) {
		addRequest(new ExceptionRequest(ioException, RequestType.WRITE));
	}

	public void expectedWrite(String data) {
		addRequest(new DataRequest(ascii2Bytes(data), RequestType.WRITE));
	}

	public void expectedWrite(String data, int times) {
		DataRequest r = new DataRequest(ascii2Bytes(data), RequestType.WRITE);
		for (int i = 0; i < times; i++) {
			addRequest(r);
		}
	}

	@Override
	public Speed getSpeed() throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		return speed;
	}

	@Override
	public DataBits getDatatBits() throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		return dataBits;
	}

	@Override
	public Set<FlowControl> getFlowControl() throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		return flowControl;
	}

	@Override
	public int getInBufferBytesCount() throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		return is.available();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if (open) {
			return is;
		} else {
			throw new IOException(PORT_IS_CLOSED);
		}
	}

	@Override
	public int getInterByteReadTimeout() throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		return interByteReadTimeout;
	}

	@Override
	public int getOutBufferBytesCount() throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		if (open) {
			return os;
		} else {
			throw new IOException(PORT_IS_CLOSED);
		}
	}

	@Override
	public int getOverallReadTimeout() throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		return overallReadTimeout;
	}

	@Override
	public int getOverallWriteTimeout() throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		return overallWriteTimeout;
	}

	@Override
	public Parity getParity() throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		return parity;
	}

	@Override
	public String getPortName() {
		return this.getClass().getCanonicalName();
	}

	@Override
	public StopBits getStopBits() throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		return stopBits;
	}

	@Override
	public char getXOFFChar() throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public char getXONChar() throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public boolean isClosed() {
		return !open;
	}

	@Override
	public boolean isCTS() throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		return false;
	}

	@Override
	public boolean isDCD() throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public boolean isDSR() throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public boolean isOpen() {
		return open;
	}

	@Override
	public boolean isRI() throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public void open() throws IOException {
		if (open) {
			throw new IOException(PORT_IS_OPEN);
		}
		is = new MBusTestInputStream();
		os = new MBusTestOutputStream();
		open = true;
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
	public void sendBreak(int duration) throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public void sendXOFF() throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public void sendXON() throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public void setSpeed(Speed speed) throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		this.speed = speed;
	}

	@Override
	public void setBreak(boolean value) throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public void setDataBits(DataBits dataBits) throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		this.dataBits = dataBits;
	}

	@Override
	public void setDTR(boolean value) throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public void setFlowControl(Set<FlowControl> flowControls) throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		this.flowControl = flowControls;
	}

	@Override
	public void setParity(Parity parity) throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		this.parity = parity;
	}

	@Override
	public void setRTS(boolean value) throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public void setStopBits(StopBits stopBits) throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		this.stopBits = stopBits;
	}

	@Override
	public void setTimeouts(int interByteReadTimeout, int overallReadTimeout, int overallWriteTimeout)
			throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		this.interByteReadTimeout = interByteReadTimeout;
		this.overallReadTimeout = overallReadTimeout;
		this.overallWriteTimeout = overallWriteTimeout;
	}

	@Override
	public void setXOFFChar(char c) throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public void setXONChar(char c) throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		throw new RuntimeException("Not Implemented");
	}

}
