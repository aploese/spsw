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
import de.ibapl.spsw.api.TimeoutIOException;
import de.ibapl.spsw.provider.SerialPortSocketFactoryImpl;

public class SendDataMain {

	public static void main(String[] args) throws Exception {
		final Object printLock = new Object();
		SerialPortSocket serialPortSocket = SerialPortSocketFactoryImpl.singleton()
				.createSerialPortSocket("/dev/ttyUSB0");
		serialPortSocket.openRaw(Baudrate.B1200, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_NONE());
		serialPortSocket.setTimeouts(200, 1000, 1000);
		Thread t = new Thread(() -> {
			final DateTimeFormatter dtf = DateTimeFormatter.ISO_INSTANT;
			byte data[] = new byte[256];
			try {
				while (true) {
					int received = serialPortSocket.getInputStream().read(data);
					if (received > 0) {
						char chars[] = new char[received];
						for (int i = 0; i < received; i++) {
							chars[i] = (char) data[i];
						}
						synchronized (printLock) {
							System.out.println(dtf.format(Instant.now()) + " DataReceived: >>>\n"
									+ String.copyValueOf(chars).replace("\"", "\\\"") + "\n<<<");
						}
					}
				}
			} catch (Exception e) {
				synchronized (printLock) {
					System.err.println(e.getMessage());
				}
			}
		});
		t.start();
		long i = 0;
		while (true) {
			String s = String.format("%10d The quick brown fox jumps over the lazy dog\n", i++);
			Thread.sleep(100);
			try {
				serialPortSocket.getOutputStream().write(s.getBytes());
			} catch (TimeoutIOException e) {
				synchronized (printLock) {
					System.err.println("DataSend: Could only write " + e.bytesTransferred + " bytes");
				}
				while (serialPortSocket.getOutBufferBytesCount() > 0) {
					Thread.sleep(10);
				}
				serialPortSocket.getOutputStream().write(s.getBytes(), e.bytesTransferred,
						s.length() - e.bytesTransferred);
			}
			synchronized (printLock) {
				System.out.println("DataSend: " + s);
			}
		}
		// serialPortSocket.close();
	}

}
