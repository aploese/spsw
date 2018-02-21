package de.ibapl.spsw.tests;

import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.provider.SerialPortSocketFactoryImpl;

public class SendDataSingleMain {

	public static void main(String[] args) throws Exception {
		SerialPortSocket serialPortSocket = SerialPortSocketFactoryImpl.singleton()
				.createSerialPortSocket("/dev/ttyUSB0");
		serialPortSocket.openRaw(Baudrate.B300, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_NONE());
		serialPortSocket.setTimeouts(1000, 0, 0);
		Thread t = new Thread(() -> {
			final DateTimeFormatter dtf = DateTimeFormatter.ISO_INSTANT;
			try {
				while (true) {
					int received = serialPortSocket.getInputStream().read();
					char data = (char)received;
					if (received > 0) {
						System.out.println(dtf.format(Instant.now()) + " DataReceived: \"" + (data == '\\' ? "\\\\" : data) + "\"");
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
			byte[] data = s.getBytes();
			Thread.sleep(1000);
			for (int j = 0; j < data.length; j++)
			serialPortSocket.getOutputStream().write(data[j]);
			System.out.println("DataSend: " + s);
		}
		// serialPortSocket.close();
	}

}
