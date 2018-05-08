package de.ibapl.spsw.jnrprovider;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Set;

import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;

public class GenericWinSerialPortSocket extends AbstractSerialPortSocket<GenericWinSerialPortSocket> {

	public GenericWinSerialPortSocket(String portName) {
		super(portName);
	}

	@Override
	public DataBits getDatatBits() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<FlowControl> getFlowControl() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getInBufferBytesCount() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getInterByteReadTimeout() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getOutBufferBytesCount() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getOverallReadTimeout() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getOverallWriteTimeout() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Parity getParity() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Speed getSpeed() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StopBits getStopBits() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char getXOFFChar() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public char getXONChar() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isClosed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCTS() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDCD() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDSR() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRI() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void open() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void open(Speed speed, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendBreak(int duration) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendXOFF() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendXON() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBreak(boolean value) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDataBits(DataBits dataBits) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDTR(boolean value) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFlowControl(Set<FlowControl> flowControls) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setParity(Parity parity) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRTS(boolean value) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSpeed(Speed speed) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStopBits(StopBits stopBits) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTimeouts(int interByteReadTimeout, int overallReadTimeout, int overallWriteTimeout)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setXOFFChar(char c) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setXONChar(char c) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void drainOutputBuffer() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int readBytes(ByteBuffer b) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int readSingle() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int writeBytes(ByteBuffer b) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void writeSingle(int b) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public static String[] getWindowsBasedPortNames() {
		// TODO Auto-generated method stub
		return null;
	}

}
