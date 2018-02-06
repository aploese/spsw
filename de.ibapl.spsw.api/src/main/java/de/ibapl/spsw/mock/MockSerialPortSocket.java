package de.ibapl.spsw.mock;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Set;

import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortException;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.StopBits;

/**
 *
 * @author aploese
 */
public class MockSerialPortSocket implements SerialPortSocket {

	public class UnexpectedRequestError extends Error {

		public UnexpectedRequestError(String message, RequestStackException stackException) {
			super(message,stackException);
		}

		public UnexpectedRequestError(String message) {
			super(message);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	}

	public class RequestStackException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	}
	
	public enum RequestType {
		READ,
		WRITE;
	}
	
	public class Request<T> {
		public final RequestStackException stackException;
		public final T payload;
		public final RequestType requestType;
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

	private MBusTestInputStream is;
	private MBusTestOutputStream os;
	private boolean open;
	private Baudrate baudrate;
	private Parity parity;
	private StopBits stopBits;
	private DataBits dataBits;
	private Set<FlowControl> flowControl;
	LinkedList<Request<?>> data = new LinkedList<>();
	private int overallReadTimeout;
	private int interByteReadTimeout;
	private int overallWriteTimeout;



	public class MBusTestInputStream extends InputStream {

		int readPtr = 0;

		@Override
		public void close() throws IOException {
			MockSerialPortSocket.this.close();
		}

		@Override
		public int read(byte[]b, int off, int len) throws IOException {
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
				final ExceptionRequest exceptionRequest = (ExceptionRequest)data.getFirst();
				data.removeFirst();
				throw exceptionRequest.payload;
			}
			if (data.getFirst() instanceof DataRequest) {
				final DataRequest dataRequest = (DataRequest)data.getFirst();
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

		@Override
		public int read() throws IOException {
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
				final ExceptionRequest exceptionRequest = (ExceptionRequest)data.getFirst();
				data.removeFirst();
				throw exceptionRequest.payload;
			}
			if (data.getFirst() instanceof DataRequest) {
				final DataRequest dataRequest = (DataRequest)data.getFirst();
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
				final DataRequest dataRequest = (DataRequest)data.getFirst();
				return dataRequest.payload.length - readPtr;
			} else {
				throw new UnexpectedRequestError("No read data request", data.getFirst().stackException);
			}
		}

	}

	public class MBusTestOutputStream extends OutputStream {

		private int writePtr = 0;

		@Override
		public void write(int b) throws IOException {
			if (!open) {
				throw new IOException("Closed");
			}
			if (data.isEmpty()) {
				throw new UnexpectedRequestError("data is empty");
			}
			if (data.getFirst().requestType != RequestType.WRITE) {
				throw new UnexpectedRequestError("No Write request", data.getFirst().stackException);
			}
			if (data.getFirst() instanceof ExceptionRequest) {
				final ExceptionRequest exceptionRequest = (ExceptionRequest)data.getFirst();
				data.removeFirst();
				throw exceptionRequest.payload;
			}
			if (data.getFirst() instanceof DataRequest) {
				final DataRequest dataRequest = (DataRequest)data.getFirst();
				if (b != dataRequest.payload[writePtr++]) {
					throw new UnexpectedRequestError("Not expected write data at " + (writePtr - 1), data.getFirst().stackException);
				}
				if (writePtr == dataRequest.payload.length) {
					writePtr = 0;
					data.removeFirst();
				}
			} else {
				throw new UnexpectedRequestError("No read data request", data.getFirst().stackException);
			}
		}

		@Override
		public void close() throws IOException {
			MockSerialPortSocket.this.close();
		}

	}

	@Override
	public void close() throws IOException {
		open = false;
	}

	@Override
	public final void openRaw() throws IOException {
		is = new MBusTestInputStream();
		os = new MBusTestOutputStream();
		open = true;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if (open) {
			return is;
		} else {
			throw new SerialPortException(SerialPortException.SERIAL_PORT_SOCKET_CLOSED);
		}
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		if (open) {
			return os;
		} else {
			throw new SerialPortException(SerialPortException.SERIAL_PORT_SOCKET_CLOSED);
		}
	}

	@Override
	public boolean isOpen() {
		return open;
	}

	@Override
	public void openAsIs() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void openModem() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void openTerminal() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void openRaw(Baudrate baudrate, DataBits dataBits, StopBits stopBits, Parity parity,
			Set<FlowControl> flowControls) throws IOException {
		this.baudrate = baudrate;
		this.dataBits = dataBits;
		this.stopBits = stopBits;
		this.parity = parity;
		this.flowControl = flowControls;
		this.openRaw();
	}

	@Override
	public boolean isClosed() {
		return !open;
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
	public boolean isIncommingRI() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getPortName() {
		return this.getClass().getCanonicalName();
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
		return is.available();
	}

	@Override
	public int getOutBufferBytesCount() throws IOException {
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
	public void setBaudrate(Baudrate baudrate) throws IOException {
		this.baudrate = baudrate;
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
	public Baudrate getBaudrate() throws IOException {
		return baudrate;
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
		return overallReadTimeout;
	}

	@Override
	public int getInterByteReadTimeout() throws IOException {
		return interByteReadTimeout;
	}

	@Override
	public void setTimeouts(int interByteReadTimeout, int overallReadTimeout, int overallWriteTimeout)
			throws IOException {
		this.interByteReadTimeout = interByteReadTimeout;
		this.overallReadTimeout = overallReadTimeout;
		this.overallWriteTimeout = overallWriteTimeout;
	}

	private void addRequest(Request<?> d) {
		data.add(d);
	}

	public void expectedRead(String data) {
		addRequest(new DataRequest(ascii2Bytes(data), RequestType.READ));
	}

	public void expectedWrite(String data) {
		addRequest(new DataRequest(ascii2Bytes(data), RequestType.WRITE));
	}

	public void expectedRead(IOException ioException) {
		addRequest(new ExceptionRequest(ioException, RequestType.READ));
	}

	public void expectedWrite(IOException ioException) {
		addRequest(new ExceptionRequest(ioException, RequestType.WRITE));
	}

	public void expectedWrite(String data, int times) {
		DataRequest r = new DataRequest(ascii2Bytes(data), RequestType.WRITE);
		for (int i = 0; i < times; i++) {
			addRequest(r);
		}
	}

	public boolean allRequestsHandled() {
		return data.isEmpty();
	}

	@Override
	public int getOverallWriteTimeout() throws IOException {
		return overallWriteTimeout;
	}

	public void addRequest(String writeData, String readData) {
		addRequest(new DataRequest(ascii2Bytes(writeData), RequestType.WRITE));
		addRequest(new DataRequest(ascii2Bytes(readData), RequestType.READ));
	}

	public void addRequest(String writeData, IOException readIOException, int times) {
		DataRequest write = new DataRequest(ascii2Bytes(writeData), RequestType.WRITE);
		ExceptionRequest read = new ExceptionRequest(readIOException, RequestType.READ);
		for (int i = 0; i < times; i++) {
			addRequest(write);
			addRequest(read);
		}
	}
	
    public static byte[] ascii2Bytes(String s) {
        byte[] result = new byte[s.length() / 2];

        for (int i = 0; i < (s.length() / 2); i++) {
            result[i]
                    = (byte) Integer.parseInt(s.substring(i * 2, (i * 2) + 2),
                            16);
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



}
