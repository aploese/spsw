/*-
 * #%L
 * SPSW API
 * %%
 * Copyright (C) 2009 - 2017 Arne Plöse
 * %%
 * SPSW - Drivers for the serial port, https://github.com/aploese/spsw/
 * Copyright (C) 2009-2018, Arne Plöse and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
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
 * #L%
 */
package de.ibapl.spsw.provider;

import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.FlowControl;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Native;
import java.util.EnumSet;
import java.util.Set;

/**
 *
 * @author scream3r
 */
public abstract class AbstractSerialPortSocket<T extends AbstractSerialPortSocket<T>> implements SerialPortSocket {

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

			return AbstractSerialPortSocket.this.readBytes(b, 0, b.length);
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

			return AbstractSerialPortSocket.this.readBytes(b, off, len);
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

			AbstractSerialPortSocket.this.writeBytes(b, 0, b.length);
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

			AbstractSerialPortSocket.this.writeBytes(b, off, len);
		}

		@Override
		public void write(int b) throws IOException {
			AbstractSerialPortSocket.this.writeSingle(b);
		}

	}

	// Layout of the 8 nibbles
	// parity,stopBits,flowControl,flowControl,dataBits,free,baudrate,baudrate
	@Native
	static final int BAUDRATE_B0 = 0x00000001;
	@Native
	static final int BAUDRATE_B50 = 0x00000002;
	@Native
	static final int BAUDRATE_B75 = 0x00000003;
	@Native
	static final int BAUDRATE_B110 = 0x00000004;
	@Native
	static final int BAUDRATE_B134 = 0x00000005;
	@Native
	static final int BAUDRATE_B150 = 0x00000006;
	@Native
	static final int BAUDRATE_B200 = 0x00000007;
	@Native
	static final int BAUDRATE_B300 = 0x00000008;
	@Native
	static final int BAUDRATE_B600 = 0x00000009;
	@Native
	static final int BAUDRATE_B1200 = 0x0000000A;
	@Native
	static final int BAUDRATE_B1800 = 0x0000000B;
	@Native
	static final int BAUDRATE_B2400 = 0x0000000C;
	@Native
	static final int BAUDRATE_B4800 = 0x0000000D;
	@Native
	static final int BAUDRATE_B9600 = 0x0000000E;
	@Native
	static final int BAUDRATE_B19200 = 0x0000000F;
	@Native
	static final int BAUDRATE_B38400 = 0x00000010;
	@Native
	static final int BAUDRATE_B57600 = 0x00000011;
	@Native
	static final int BAUDRATE_B115200 = 0x00000012;
	@Native
	static final int BAUDRATE_B230400 = 0x00000013;
	@Native
	static final int BAUDRATE_B460800 = 0x00000014;
	@Native
	static final int BAUDRATE_B500000 = 0x00000015;
	@Native
	static final int BAUDRATE_B576000 = 0x00000016;
	@Native
	static final int BAUDRATE_B921600 = 0x00000017;
	@Native
	static final int BAUDRATE_B1000000 = 0x00000018;
	@Native
	static final int BAUDRATE_B1152000 = 0x00000019;
	@Native
	static final int BAUDRATE_B1500000 = 0x0000001A;
	@Native
	static final int BAUDRATE_B2000000 = 0x0000001B;
	@Native
	static final int BAUDRATE_B2500000 = 0x0000001C;
	@Native
	static final int BAUDRATE_B3000000 = 0x0000001D;
	@Native
	static final int BAUDRATE_B3500000 = 0x0000001E;
	@Native
	static final int BAUDRATE_B4000000 = 0x0000001F;
	@Native
	static final int BAUDRATE_MASK = 0x000000FF;

	@Native
	static final int DATA_BITS_DB5 = 0x00001000;
	@Native
	static final int DATA_BITS_DB6 = 0x00002000;
	@Native
	static final int DATA_BITS_DB7 = 0x00003000;
	@Native
	static final int DATA_BITS_DB8 = 0x00004000;
	@Native
	static final int DATA_BITS_MASK = 0x0000F000;

	@Native
	static final int FLOW_CONTROL_NONE = 0x00010000;
	@Native
	static final int FLOW_CONTROL_RTS_CTS_IN = 0x00100000;
	@Native
	static final int FLOW_CONTROL_RTS_CTS_OUT = 0x00200000;
	@Native
	static final int FLOW_CONTROL_XON_XOFF_IN = 0x00400000;
	@Native
	static final int FLOW_CONTROL_XON_XOFF_OUT = 0x00800000;
	@Native
	static final int FLOW_CONTROL_MASK = 0x00FF0000;

	@Native
	static final int STOP_BITS_1 = 0x01000000;
	@Native
	static final int STOP_BITS_1_5 = 0x02000000;
	@Native
	static final int STOP_BITS_2 = 0x03000000;
	@Native
	static final int STOP_BITS_MASK = 0x0F000000;

	@Native
	static final int PARITY_NONE = 0x10000000;
	@Native
	static final int PARITY_ODD = 0x20000000;
	@Native
	static final int PARITY_EVEN = 0x30000000;
	@Native
	static final int PARITY_MARK = 0x40000000;
	@Native
	static final int PARITY_SPACE = 0x50000000;
	@Native
	static final int PARITY_MASK = 0xF0000000;

	@Native
	static final int NO_PARAMS_TO_SET = 0x00000000;

	static Baudrate baudrateFromBitSet(int bitset) {
		switch (bitset & BAUDRATE_MASK) {
		case BAUDRATE_B0:
			return Baudrate.B0;
		case BAUDRATE_B50:
			return Baudrate.B50;
		case BAUDRATE_B75:
			return Baudrate.B75;
		case BAUDRATE_B110:
			return Baudrate.B110;
		case BAUDRATE_B134:
			return Baudrate.B134;
		case BAUDRATE_B150:
			return Baudrate.B150;
		case BAUDRATE_B200:
			return Baudrate.B200;
		case BAUDRATE_B300:
			return Baudrate.B300;
		case BAUDRATE_B600:
			return Baudrate.B600;
		case BAUDRATE_B1200:
			return Baudrate.B1200;
		case BAUDRATE_B1800:
			return Baudrate.B1800;
		case BAUDRATE_B2400:
			return Baudrate.B2400;
		case BAUDRATE_B4800:
			return Baudrate.B4800;
		case BAUDRATE_B9600:
			return Baudrate.B9600;
		case BAUDRATE_B19200:
			return Baudrate.B19200;
		case BAUDRATE_B38400:
			return Baudrate.B38400;
		case BAUDRATE_B57600:
			return Baudrate.B57600;
		case BAUDRATE_B115200:
			return Baudrate.B115200;
		case BAUDRATE_B230400:
			return Baudrate.B230400;
		case BAUDRATE_B460800:
			return Baudrate.B460800;
		case BAUDRATE_B500000:
			return Baudrate.B500000;
		case BAUDRATE_B576000:
			return Baudrate.B576000;
		case BAUDRATE_B921600:
			return Baudrate.B921600;
		case BAUDRATE_B1000000:
			return Baudrate.B1000000;
		case BAUDRATE_B1152000:
			return Baudrate.B1152000;
		case BAUDRATE_B1500000:
			return Baudrate.B1500000;
		case BAUDRATE_B2000000:
			return Baudrate.B2000000;
		case BAUDRATE_B2500000:
			return Baudrate.B2500000;
		case BAUDRATE_B3000000:
			return Baudrate.B3000000;
		case BAUDRATE_B3500000:
			return Baudrate.B3500000;
		case BAUDRATE_B4000000:
			return Baudrate.B4000000;
		default:
			throw new IllegalArgumentException(String.format("Unknown baudrate in bitset: %8x", bitset));
		}
	}

	static DataBits dataBitsFromBitSet(int bitset) {
		switch (bitset & DATA_BITS_MASK) {
		case DATA_BITS_DB5:
			return DataBits.DB_5;
		case DATA_BITS_DB6:
			return DataBits.DB_6;
		case DATA_BITS_DB7:
			return DataBits.DB_7;
		case DATA_BITS_DB8:
			return DataBits.DB_8;
		default:
			throw new IllegalArgumentException(String.format("Unknown dataBits in bitset: %8x", bitset));
		}
	}

	static Set<FlowControl> flowControlFromBitSet(int bitset) {
		Set<FlowControl> s = EnumSet.noneOf(FlowControl.class);
		if ((bitset & FLOW_CONTROL_MASK) == FLOW_CONTROL_NONE) {
			return s;
		}
		if ((bitset & FLOW_CONTROL_RTS_CTS_IN) == FLOW_CONTROL_RTS_CTS_IN) {
			s.add(FlowControl.RTS_CTS_IN);
		}
		if ((bitset & FLOW_CONTROL_RTS_CTS_OUT) == FLOW_CONTROL_RTS_CTS_OUT) {
			s.add(FlowControl.RTS_CTS_OUT);
		}
		if ((bitset & FLOW_CONTROL_XON_XOFF_IN) == FLOW_CONTROL_XON_XOFF_IN) {
			s.add(FlowControl.XON_XOFF_IN);
		}
		if ((bitset & FLOW_CONTROL_XON_XOFF_OUT) == FLOW_CONTROL_XON_XOFF_OUT) {
			s.add(FlowControl.XON_XOFF_OUT);
		}
		return s;
	}

	static Parity parityFromBitSet(int bitset) {
		switch (bitset & PARITY_MASK) {
		case PARITY_NONE:
			return Parity.NONE;
		case PARITY_ODD:
			return Parity.ODD;
		case PARITY_EVEN:
			return Parity.EVEN;
		case PARITY_MARK:
			return Parity.MARK;
		case PARITY_SPACE:
			return Parity.SPACE;
		default:
			throw new IllegalArgumentException(String.format("Unknown stopBits in bitset: %8x", bitset));
		}
	}

	static StopBits stopBitsFromBitSet(int bitset) {
		switch (bitset & STOP_BITS_MASK) {
		case STOP_BITS_1:
			return StopBits.SB_1;
		case STOP_BITS_1_5:
			return StopBits.SB_1_5;
		case STOP_BITS_2:
			return StopBits.SB_2;
		default:
			throw new IllegalArgumentException(String.format("Unknown stopBits in bitset: %8x", bitset));
		}
	}

	static int toBitSet(Baudrate baudrate) {
		switch (baudrate) {
		case B0:
			return BAUDRATE_B0;
		case B50:
			return BAUDRATE_B50;
		case B75:
			return BAUDRATE_B75;
		case B110:
			return BAUDRATE_B110;
		case B134:
			return BAUDRATE_B134;
		case B150:
			return BAUDRATE_B150;
		case B200:
			return BAUDRATE_B200;
		case B300:
			return BAUDRATE_B300;
		case B600:
			return BAUDRATE_B600;
		case B1200:
			return BAUDRATE_B1200;
		case B1800:
			return BAUDRATE_B1800;
		case B2400:
			return BAUDRATE_B2400;
		case B4800:
			return BAUDRATE_B4800;
		case B9600:
			return BAUDRATE_B9600;
		case B19200:
			return BAUDRATE_B19200;
		case B38400:
			return BAUDRATE_B38400;
		case B57600:
			return BAUDRATE_B57600;
		case B115200:
			return BAUDRATE_B115200;
		case B230400:
			return BAUDRATE_B230400;
		case B460800:
			return BAUDRATE_B460800;
		case B500000:
			return BAUDRATE_B500000;
		case B576000:
			return BAUDRATE_B576000;
		case B921600:
			return BAUDRATE_B921600;
		case B1000000:
			return BAUDRATE_B1000000;
		case B1152000:
			return BAUDRATE_B1152000;
		case B1500000:
			return BAUDRATE_B1500000;
		case B2000000:
			return BAUDRATE_B2000000;
		case B2500000:
			return BAUDRATE_B2500000;
		case B3000000:
			return BAUDRATE_B3000000;
		case B3500000:
			return BAUDRATE_B3500000;
		case B4000000:
			return BAUDRATE_B4000000;
		default:
			throw new IllegalArgumentException("Unknown baudrate: " + baudrate);
		}
	}

	static int toBitSet(DataBits dataBits) {
		switch (dataBits) {
		case DB_5:
			return DATA_BITS_DB5;
		case DB_6:
			return DATA_BITS_DB6;
		case DB_7:
			return DATA_BITS_DB7;
		case DB_8:
			return DATA_BITS_DB8;
		default:
			throw new IllegalArgumentException("Unknown dataBits: " + dataBits);
		}
	}

	static int toBitSet(Parity parity) {
		switch (parity) {
		case EVEN:
			return PARITY_EVEN;
		case MARK:
			return PARITY_MARK;
		case NONE:
			return PARITY_NONE;
		case ODD:
			return PARITY_ODD;
		case SPACE:
			return PARITY_SPACE;
		default:
			throw new IllegalArgumentException("Unknown parity: " + parity);
		}
	}

	static int toBitSet(Set<FlowControl> flowControls) {
		if (flowControls.isEmpty()) {
			return FLOW_CONTROL_NONE;
		}
		int bitSet = 0;
		for (FlowControl fc : flowControls) {
			switch (fc) {
			case RTS_CTS_IN:
				bitSet |= FLOW_CONTROL_RTS_CTS_IN;
				break;
			case RTS_CTS_OUT:
				bitSet |= FLOW_CONTROL_RTS_CTS_OUT;
				break;
			case XON_XOFF_IN:
				bitSet |= FLOW_CONTROL_XON_XOFF_IN;
				break;
			case XON_XOFF_OUT:
				bitSet |= FLOW_CONTROL_XON_XOFF_OUT;
				break;
			default:
				throw new IllegalArgumentException("Unknown flowcontrol:" + fc);
			}
		}
		return bitSet;
	}

	static int toBitSet(StopBits stopBits) {
		switch (stopBits) {
		case SB_1:
			return STOP_BITS_1;
		case SB_1_5:
			return STOP_BITS_1_5;
		case SB_2:
			return STOP_BITS_2;
		default:
			throw new IllegalArgumentException("Unknown stopBits: " + stopBits);
		}
	}

	protected SerialInputStream is;
	protected SerialOutputStream os;

	private final String portName;

	private volatile boolean open;

	protected AbstractSerialPortSocket(String portName) {
		SecurityManager security = System.getSecurityManager();
		if (security != null) {
			security.checkRead(portName);
			security.checkWrite(portName);
		}
		if (portName == null) {
			throw new IllegalArgumentException("portname must not null!");
		}

		this.portName = portName;
	}

	@Override
	public synchronized void close() throws IOException {
		if (!open) {
			return;
		}
		open = false;
		is = null;
		os = null;
		close0();
	}

	/**
	 * Close port
	 *
	 * @throws java.io.IOException
	 */
	protected native void close0() throws IOException;

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
	public Baudrate getBaudrate() throws IOException {
		return baudrateFromBitSet(getParameters(BAUDRATE_MASK));
	}

	@Override
	public DataBits getDatatBits() throws IOException {
		return dataBitsFromBitSet(getParameters(DATA_BITS_MASK));
	}

	@Override
	public Set<FlowControl> getFlowControl() throws IOException {
		return flowControlFromBitSet(getParameters(FLOW_CONTROL_MASK));
	}

	@Override
	public native int getInBufferBytesCount() throws IOException;

	@Override
	public synchronized InputStream getInputStream() throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		if (is == null) {
			is = new SerialInputStream();
		}
		return is;
	}

	@Override
	public native int getOutBufferBytesCount() throws IOException;

	@Override
	public synchronized OutputStream getOutputStream() throws IOException {
		if (!open) {
			throw new IOException(PORT_IS_CLOSED);
		}
		if (os == null) {
			os = new SerialOutputStream();
		}
		return os;
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	protected native int getParameters(int parameterBitSetMask) throws IOException;

	@Override
	public Parity getParity() throws IOException {
		return parityFromBitSet(getParameters(PARITY_MASK));
	}

	@Override
	public String getPortName() {
		return portName;
	}

	@Override
	public StopBits getStopBits() throws IOException {
		return stopBitsFromBitSet(getParameters(STOP_BITS_MASK));
	}

	@Override
	public native char getXOFFChar() throws IOException;

	@Override
	public native char getXONChar() throws IOException;

	@Override
	public boolean isClosed() {
		return !open;
	}

	@Override
	public native boolean isCTS() throws IOException;

	@Override
	public native boolean isDSR() throws IOException;

	@Override
	public native boolean isIncommingRI() throws IOException;

	@Override
	public boolean isOpen() {
		return open;
	}

	@Override
	public synchronized void open() throws IOException {
		if (open) {
			throw new IOException(PORT_IS_OPEN);
		}
		open(portName, NO_PARAMS_TO_SET);
		open = true;
	}

	@Override
	public synchronized void open(Baudrate baudrate, DataBits dataBits, StopBits stopBits, Parity parity,
			Set<FlowControl> flowControls) throws IOException {
		if (open) {
			throw new IOException(PORT_IS_OPEN);
		}
		int bitset = toBitSet(baudrate);
		bitset |= toBitSet(dataBits);
		bitset |= toBitSet(stopBits);
		bitset |= toBitSet(parity);
		bitset |= toBitSet(flowControls);
		open(portName, bitset);
		open = true;
	}

	/**
	 * Open port
	 *
	 * @param portName
	 *            name of port for opening
	 * @param type
	 * @throws java.io.IOException
	 *
	 */
	protected native void open(String portName, int paramBitSet) throws IOException;

	/**
	 * Read data from port
	 *
	 * @param b
	 *            the data to be written
	 * @param off
	 *            the start offset in the data
	 * @param len
	 *            the number of bytes that are written
	 * @return the readed bytes
	 * @exception IOException
	 *                If an I/O error has occurred.
	 */
	protected native int readBytes(byte[] b, int off, int len) throws IOException;

	protected native int readSingle() throws IOException;

	@Override
	public native void sendBreak(int duration) throws IOException;

	@Override
	public void setBaudrate(Baudrate baudrate) throws IOException {
		try {
			setParameters(toBitSet(baudrate));
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("Can't set baudrate " + baudrate + " on port: " + getPortName(), ex);
		}
	}

	@Override
	public native void setBreak(boolean value) throws IOException;

	@Override
	public void setDataBits(DataBits dataBits) throws IOException {
		try {
			setParameters(toBitSet(dataBits));
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException(
					"Can't set dataBits " + dataBits + " on port: " + getPortName() + "value is:" + getDatatBits(), ex);
		}
	}

	@Override
	public native void setDTR(boolean value) throws IOException;

	@Override
	public void setFlowControl(Set<FlowControl> flowControls) throws IOException {
		setParameters(toBitSet(flowControls));
	}

	/**
	 * 
	 * @param parameterBitSet
	 * @return the parameterBitSet after setting
	 * @throws IOException
	 */
	protected native void setParameters(int parameterBitSet) throws IOException;

	@Override
	public void setParity(Parity parity) throws IOException {
		setParameters(toBitSet(parity));
	}

	@Override
	public native void setRTS(boolean value) throws IOException;

	@Override
	public void setStopBits(StopBits stopBits) throws IOException {
		try {
			setParameters(toBitSet(stopBits));
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("Can't set stopBits " + stopBits + " on port: " + getPortName(), ex);
		}
	}

	@Override
	public native void setXOFFChar(char c) throws IOException;

	@Override
	public native void setXONChar(char c) throws IOException;

	@Override
	public String toString() {
		try {
			return String.format("[portname=%s, baudrate= %s, dataBits= %s, stopBits= %s, parity= %s, flowControl= %s]",
					getPortName(), getBaudrate(), getDatatBits(), getStopBits(), getParity(), getFlowControl());
		} catch (IOException e) {
			return "Internal Error " + e;
		}
	}

	/**
	 * Write data to port
	 *
	 * @param b
	 * @param off
	 *            the start offset in the data.
	 * @param len
	 *            the number of bytes to write.
	 * @throws java.io.IOException
	 *
	 */
	protected native void writeBytes(byte[] b, int off, int len) throws IOException;

	protected native void writeSingle(int b) throws IOException;

}
