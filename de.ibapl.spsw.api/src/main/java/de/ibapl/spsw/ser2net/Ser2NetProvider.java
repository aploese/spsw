package de.ibapl.spsw.ser2net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Set;

import javax.net.SocketFactory;

import org.osgi.annotation.versioning.ProviderType;

import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.StopBits;

@ProviderType
public class Ser2NetProvider implements SerialPortSocket {

	private Socket dataSocket;
	private Socket controlSocket;
	private String host;
	private int dataPort;
	private int controlPort;
	
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
	public boolean isIncommingRI() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return dataSocket.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return dataSocket.getOutputStream();
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
	public void openAsIs() throws IOException {
		dataSocket = SocketFactory.getDefault().createSocket(host, dataPort);
		if (controlPort != -1) {
			controlSocket = SocketFactory.getDefault().createSocket(host, controlPort);
		}
	}

	@Override
	public void openRaw() throws IOException {
		dataSocket = SocketFactory.getDefault().createSocket(host, dataPort);
		if (controlPort != -1) {
			controlSocket = SocketFactory.getDefault().createSocket(host, controlPort);
		}
	}

	@Override
	public void openTerminal() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void openModem() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void openRaw(Baudrate baudRate, DataBits dataBits, StopBits stopBits, Parity parity,
			Set<FlowControl> flowControls) throws IOException {
		dataSocket = SocketFactory.getDefault().createSocket(host, dataPort);
		if (controlPort != -1) {
			controlSocket = SocketFactory.getDefault().createSocket(host, controlPort);
			//TODO Set port settings
		} else {
			//TODO print warning
		}
	}

	@Override
	public void close() throws IOException {
		dataSocket.close();
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
		// TODO Auto-generated method stub
		return 0;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBaudrate(Baudrate baudrate) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDataBits(DataBits dataBits) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStopBits(StopBits stopBits) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setParity(Parity parity) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Baudrate getBaudrate() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataBits getDatatBits() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StopBits getStopBits() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Parity getParity() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<FlowControl> getFlowControl() throws IOException {
		// TODO Auto-generated method stub
		return null;
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
	public void setReadTimeouts(int interByteTimeout, int overallTimeout) throws IOException {
		dataSocket.setSoTimeout(overallTimeout);
	}


}
