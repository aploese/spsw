package de.ibapl.spsw.tests;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.provider.SerialPortSocketFactoryImpl;

public class PrintPortCapabillitiesMain {

	public static void main(String[] args) throws Exception {
		SerialPortSocket serialPortSocket = SerialPortSocketFactoryImpl.singleton()
				.createSerialPortSocket("/dev/ttyUSB0");
		serialPortSocket.openRaw();
        System.out.println("Use device: " + serialPortSocket.getPortName());
        System.out.println(String.format("%-20s%-20s%-20s%-20s", "Baudrate", "DataBits", "StopBits", "Parity"));

		for (Baudrate br : Baudrate.values()) {
			for (DataBits db : DataBits.values()) {
				for (StopBits sb : StopBits.values()) {
					for (Parity p : Parity.values()) {
						try {
							serialPortSocket.setBaudrate(br);
						} catch (IllegalArgumentException e) {
							System.err.println("Cant set Baudrate to: " + br);
						}
						try {
							serialPortSocket.setDataBits(db);
						} catch (IllegalArgumentException e) {
							System.err.println("Cant set DataBits to: " + db);
						}
						try {
							serialPortSocket.setStopBits(sb);
						} catch (IllegalArgumentException e) {
							System.err.println("Cant set StopBits to: " + sb);
						}
						try {
							serialPortSocket.setParity(p);
						} catch (IllegalArgumentException e) {
							System.err.println("Cant set Parity to: " + p);
						}
						printPort(serialPortSocket);
					}
				}
			}
		}
		serialPortSocket.close();
	}

	    private static void printPort(SerialPortSocket sPort) throws IOException {
	        System.out.println(String.format("%-20d%-20d%-20f%-20s", sPort.getBaudrate().value, sPort.getDatatBits().value, sPort.getStopBits().value, sPort.getParity().name()));
	}

}
