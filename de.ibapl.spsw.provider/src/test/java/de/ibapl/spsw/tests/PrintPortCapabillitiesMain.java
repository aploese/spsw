package de.ibapl.spsw.tests;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.logging.Level;

import javax.sql.rowset.serial.SerialException;
import javax.swing.text.NavigationFilter;

import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortException;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.provider.SerialPortSocketFactoryImpl;

public class PrintPortCapabillitiesMain {

	public static void main(String[] args) throws Exception {
		String serialPortName;
		try (InputStream is = BaselineOnePortTest.class.getClassLoader()
				.getResourceAsStream("junit-spsw-config.properties")) {
			if (is == null) {
				serialPortName = null;
			} else {
				Properties p = new Properties();
				p.load(is);
				serialPortName = p.getProperty("port0");
			}
		}
		SerialPortSocket serialPortSocket = SerialPortSocketFactoryImpl.singleton().createSerialPortSocket(serialPortName);
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
							System.err.println("Can't set Baudrate to: " + br);
						} catch (SerialPortException spe) {
							System.err.println("Error: set Baudrate to: " + br);
							System.err.println(spe);
						}
						try {
							serialPortSocket.setDataBits(db);
						} catch (IllegalArgumentException e) {
							System.err.println("Can't set DataBits to: " + db);
						} catch (SerialPortException spe) {
							System.err.println("Error: set DataBits to: " + db);
							System.err.println(spe);
						}
						try {
							serialPortSocket.setStopBits(sb);
						} catch (IllegalArgumentException e) {
							System.err.println("Can't set StopBits to: " + sb);
						} catch (SerialPortException spe) {
							System.err.println("Error: set StopBits to: " + sb);
							System.err.println(spe);
						}
						try {
							serialPortSocket.setParity(p);
						} catch (IllegalArgumentException e) {
							System.err.println("Can't set Parity to: " + p);
						} catch (SerialPortException spe) {
							System.err.println("Error: set Parity to: " + p);
							System.err.println(spe);
							Thread.sleep(10);
						}
						printPort(serialPortSocket);
					}
				}
				// @5 Databis 1 or 1.5 Stopbits otherwise only 1 or 2 Stopbits...
				try {
					serialPortSocket.setStopBits(StopBits.SB_1);
				} catch (Exception e) {
					System.err.println("Error: set StopBits to: " + StopBits.SB_1);
					System.err.println(e);
				}
			}
		}
		serialPortSocket.close();
	}

	private static void printPort(SerialPortSocket sPort) throws IOException {
		System.out.println(String.format("%-20d%-20d%-20f%-20s", sPort.getBaudrate().value, sPort.getDatatBits().value,
				sPort.getStopBits().value, sPort.getParity().name()));
	}

}
