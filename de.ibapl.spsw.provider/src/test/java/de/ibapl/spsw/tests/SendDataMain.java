package de.ibapl.spsw.tests;

import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.provider.SerialPortSocketFactoryImpl;

public class SendDataMain {

	public static void main(String[] args) throws Exception {
		SerialPortSocket serialPortSocket = SerialPortSocketFactoryImpl.singleton().createSerialPortSocket("/dev/ttyUSB0");
		serialPortSocket.openRaw(Baudrate.B300, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_NONE());
		Thread t = new Thread(() -> {
			byte data[] = new byte[256];
			try {
				while(true) {
				int received = serialPortSocket.getInputStream().read(data);
				if (received > 0) {
				char chars[] = new char[received];
				for (int i = 0; i < received; i++) {
					chars[i] = (char)data[i];
				}
				System.out.println("DataReceived: " + String.copyValueOf(chars));
				}
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		});
		t.start();
		long i = 0;
		while (true) {
			String s = String.format("%10d The quick brown fox jumps over the lazy dog\n", i++);
			serialPortSocket.getOutputStream().write(s.getBytes());
			System.out.println("DataSend: " + s);
		}
//		serialPortSocket.close();
	}

}
