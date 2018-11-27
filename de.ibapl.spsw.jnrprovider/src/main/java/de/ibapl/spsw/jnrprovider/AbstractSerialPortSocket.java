package de.ibapl.spsw.jnrprovider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;

import de.ibapl.spsw.api.SerialPortSocket;

public abstract class AbstractSerialPortSocket<T extends AbstractSerialPortSocket<T>> implements SerialPortSocket {

	protected class SerialInputStream extends InputStream {

		private ByteBuffer singleReadBuffer;
		
		@Override
		public int available() throws IOException {
			return AbstractSerialPortSocket.this.getInBufferBytesCount();
		}

		@Override
		public void close() throws IOException {
			AbstractSerialPortSocket.this.close();
		}

		@Override
		public int read() throws IOException {
			if (singleReadBuffer == null) {
				 singleReadBuffer = ByteBuffer.allocateDirect(1);
			} else {
				singleReadBuffer.clear();
			}
			try {
				int result = AbstractSerialPortSocket.this.read(singleReadBuffer);
				if (result == 1) {
					singleReadBuffer.flip();
					return singleReadBuffer.get() & 0xff;
				} else {
					return result;
				}
			} catch (AsynchronousCloseException ace) {
				return -1;
			}
		}

		@Override
		public int read(byte b[]) throws IOException {
			if (b == null) {
				throw new NullPointerException();
			} else if (b.length == 0) {
				return 0;
			}
			ByteBuffer buf = ByteBuffer.allocateDirect(b.length);
			try {
				int result = AbstractSerialPortSocket.this.read(buf);
				buf.flip();
				buf.get(b, 0, result);
				return result;
			} catch (AsynchronousCloseException ace) {
				return -1;
			}
		}

		@Override
		public int read(byte b[], int off, int len) throws IOException {
			if (b == null) {
				throw new NullPointerException();
			} else if (off < 0 || len < 0 || len > b.length - off) {
				throw new IndexOutOfBoundsException();
			} else if (len == 0) {
				return 0;
			}
			ByteBuffer buf = ByteBuffer.allocateDirect(len);

			try {
				int result = AbstractSerialPortSocket.this.read(buf);
				buf.flip();
				buf.get(b, off, result);
				return result;
			} catch (AsynchronousCloseException ace) {
				return -1;
			}
		}

	}

	protected class SerialOutputStream extends OutputStream {

		private ByteBuffer singleWriteBuffer;

		@Override
		public void close() throws IOException {
			AbstractSerialPortSocket.this.close();
		}

		@Override
		public void flush() throws IOException {
			AbstractSerialPortSocket.this.drainOutputBuffer();
		}

		@Override
		public void write(byte b[]) throws IOException {
			if (b == null) {
				throw new NullPointerException();
			} else if (b.length == 0) {
				return;
			}
			ByteBuffer buf = ByteBuffer.allocateDirect(b.length);
			buf.put(b);
			buf.flip();
			AbstractSerialPortSocket.this.write(buf);
		}

		@Override
		public void write(byte b[], int off, int len) throws IOException {
			if (b == null) {
				throw new NullPointerException();
			} else if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0)) {
				throw new IndexOutOfBoundsException();
			} else if (len == 0) {
				return;
			}
			ByteBuffer buf = ByteBuffer.allocateDirect(len);
			buf.put(b, off, len);
			buf.flip();
			AbstractSerialPortSocket.this.write(buf);
		}

		@Override
		public void write(int b) throws IOException {
			if (singleWriteBuffer == null) {
				singleWriteBuffer = ByteBuffer.allocateDirect(1);
			} else {
				singleWriteBuffer.clear();
			}
			singleWriteBuffer.put((byte) b);
			singleWriteBuffer.flip();
			AbstractSerialPortSocket.this.write(singleWriteBuffer);
		}

	}

	protected SerialInputStream is;
	protected SerialOutputStream os;

	protected final String portName;

	/**
	 * Creates a new Instance and checks read/write permissions with the
	 * System.getSecurityManager().
	 * 
	 * @param portName the name of the port.
	 * 
	 * @see SecurityManager#checkRead(String)
	 * @see SecurityManager#checkWrite(String)
	 * @see java.io.FileOutputStream#FileOutputStream(String)
	 * @see java.io.FileInputStream#FileInputStream(String).
	 * 
	 */
	public AbstractSerialPortSocket(String portName) {
		if (portName == null) {
			throw new IllegalArgumentException("portname must not null!");
		}
		SecurityManager security = System.getSecurityManager();
		if (security != null) {
			security.checkRead(portName);
			security.checkWrite(portName);
		}
		this.portName = portName;
	}

	@Override
	public synchronized void close() throws IOException {
		is = null;
		os = null;
	}

	/**
	 * writes all data in the output buffer
	 *
	 * @throws IOException
	 */
	protected abstract void drainOutputBuffer() throws IOException;

	@Override
	protected final void finalize() throws Throwable {
		try {
			if (isOpen()) {
				close();
			}
		} catch (Exception ex) {
			// This should always work
			System.err.println("SerialPortSocket " + getPortName() + " finalize() exception: " + ex);
		} catch (Error err) {
			// Leave a trace what hit us...
			System.err.println("SerialPortSocket " + getPortName() + " finalize() error: " + err);
			throw err;
		} finally {
			super.finalize();
		}
	}

	@Override
	public synchronized InputStream getInputStream() throws IOException {
		if (!isOpen()) {
			throw new IOException(PORT_IS_CLOSED);
		}
		if (is == null) {
			is = new SerialInputStream();
		}
		return is;
	}

	@Override
	public synchronized OutputStream getOutputStream() throws IOException {
		if (!isOpen()) {
			throw new IOException(PORT_IS_CLOSED);
		}
		if (os == null) {
			os = new SerialOutputStream();
		}
		return os;
	}

	@Override
	public String getPortName() {
		return portName;
	}

	@Override
	public String toString() {
		try {
			return String.format("[portname=%s, speed= %s, dataBits= %s, stopBits= %s, parity= %s, flowControl= %s]",
					getPortName(), getSpeed(), getDatatBits(), getStopBits(), getParity(), getFlowControl());
		} catch (IOException e) {
			return "Internal Error " + e;
		}
	}

}
