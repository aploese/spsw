package de.ibapl.spsw.jnrprovider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

import de.ibapl.spsw.api.SerialPortSocket;

public abstract class AbstractSerialPortSocket<T extends AbstractSerialPortSocket<T>> implements SerialPortSocket {
	
	protected class SerialChannel implements ByteChannel {
		
		private final Object readLock = new Object();
		private final Object writeLock = new Object();
		

		@Override
		public int read(ByteBuffer dst) throws IOException {
			synchronized (readLock) {
				return AbstractSerialPortSocket.this.readBytes(dst);
			}
		}

		@Override
		public boolean isOpen() {
			return AbstractSerialPortSocket.this.isOpen();
		}

		@Override
		public void close() throws IOException {
			AbstractSerialPortSocket.this.close();
		}

		@Override
		public int write(ByteBuffer src) throws IOException {
			synchronized (writeLock) {
				return AbstractSerialPortSocket.this.writeBytes(src);
			}
		}
		
	}
	
	protected class SerialInputStream extends InputStream {

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
			return AbstractSerialPortSocket.this.readSingle();
		}

		@Override
		public int read(byte b[]) throws IOException {
			if (b == null) {
				throw new NullPointerException();
			} else if (b.length == 0) {
				return 0;
			}
			ByteBuffer buf = ByteBuffer.wrap(b);

			return AbstractSerialPortSocket.this.readBytes(buf);
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
			ByteBuffer buf = ByteBuffer.wrap(b, off, len);

			return AbstractSerialPortSocket.this.readBytes(buf);
		}

	}

	protected class SerialOutputStream extends OutputStream {

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
			ByteBuffer buf = ByteBuffer.wrap(b);

			AbstractSerialPortSocket.this.writeBytes(buf);
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
			ByteBuffer buf = ByteBuffer.wrap(b, off, len);

			AbstractSerialPortSocket.this.writeBytes(buf);
		}

		@Override
		public void write(int b) throws IOException {
			AbstractSerialPortSocket.this.writeSingle(b);
		}

	}

	protected SerialInputStream is;
	protected SerialOutputStream os;
	protected SerialChannel ch;
	

	protected final String portName;

	/**
	 * Creates a new Instance and checks read/write permissions with the
	 * System.getSecurityManager().
	 * 
	 * @param portName
	 *            the name of the port.
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

	/**
	 * Read data from port
	 *
	 * @param b
	 *            the data to be written
	 * @return the readed bytes
	 * @exception IOException
	 *                If an I/O error has occurred.
	 */
	protected abstract int readBytes(ByteBuffer b) throws IOException;

	protected abstract int readSingle() throws IOException;

	@Override
	public String toString() {
		try {
			return String.format("[portname=%s, speed= %s, dataBits= %s, stopBits= %s, parity= %s, flowControl= %s]",
					getPortName(), getSpeed(), getDatatBits(), getStopBits(), getParity(), getFlowControl());
		} catch (IOException e) {
			return "Internal Error " + e;
		}
	}

	/**
	 * Write data to port
	 *
	 * @param b
	 * @throws java.io.IOException
	 *
	 */
	protected abstract int writeBytes(ByteBuffer b) throws IOException;

	protected abstract void writeSingle(int b) throws IOException;
	
	@Override
	public synchronized ByteChannel getChannel() throws IOException {
		if (!isOpen()) {
			throw new IOException(PORT_IS_CLOSED);
		}
		if (ch == null) {
			ch = new SerialChannel();
		}
		return ch;
	}



}
