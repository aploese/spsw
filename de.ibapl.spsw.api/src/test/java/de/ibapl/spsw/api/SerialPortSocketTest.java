package de.ibapl.spsw.api;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SerialPortSocketTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testCalculateMillisForBytes() {
		int result = SerialPortSocket.calculateMillisForBytes(1024, Baudrate.B50, DataBits.DB_8, StopBits.SB_2, Parity.EVEN);
		assertEquals(245760, result);		
		result = SerialPortSocket.calculateMillisForBytes(1024, Baudrate.B50, DataBits.DB_8, StopBits.SB_1, Parity.EVEN);
		assertEquals(225280, result);		
		result = SerialPortSocket.calculateMillisForBytes(1024, Baudrate.B50, DataBits.DB_8, StopBits.SB_2, Parity.EVEN);
		assertEquals(245760, result);		
		result = SerialPortSocket.calculateMillisForBytes(1024, Baudrate.B9600, DataBits.DB_8, StopBits.SB_1, Parity.EVEN);
		assertEquals(1174, result);		
		result = SerialPortSocket.calculateMillisForBytes(1024, Baudrate.B4000000, DataBits.DB_8, StopBits.SB_1, Parity.EVEN);
		assertEquals(3, result);		
		result = SerialPortSocket.calculateMillisForBytes(1024, Baudrate.B4000000, DataBits.DB_8, StopBits.SB_2, Parity.EVEN);
		assertEquals(4, result);		
		result = SerialPortSocket.calculateMillisForBytes(1024, Baudrate.B4000000, DataBits.DB_8, StopBits.SB_1, Parity.NONE);
		assertEquals(3, result);		
		result = SerialPortSocket.calculateMillisForBytes(1024, Baudrate.B4000000, DataBits.DB_5, StopBits.SB_1, Parity.NONE);
		assertEquals(2, result);		

		//This should sum up to a second. 10 Bit per byte to transfer and 5 Bytes to transfer.
		result = SerialPortSocket.calculateMillisForBytes(5, Baudrate.B50, DataBits.DB_8, StopBits.SB_1, Parity.NONE);
		assertEquals(1000, result);		
		
	}

	@Test
	void testCalculateMillisPerByte() {
		//Longest Time
		double result = SerialPortSocket.calculateMillisPerByte(Baudrate.B50, DataBits.DB_8, StopBits.SB_2, Parity.EVEN);
		assertEquals(240.0, result);		
		result = SerialPortSocket.calculateMillisPerByte(Baudrate.B50, DataBits.DB_8, StopBits.SB_1, Parity.EVEN);
		assertEquals(220.0, result);		
		result = SerialPortSocket.calculateMillisPerByte(Baudrate.B50, DataBits.DB_8, StopBits.SB_2, Parity.EVEN);
		assertEquals(240.0, result);		
		result = SerialPortSocket.calculateMillisPerByte(Baudrate.B9600, DataBits.DB_8, StopBits.SB_1, Parity.EVEN);
		assertEquals(1.1458333333333333, result);		
		result = SerialPortSocket.calculateMillisPerByte(Baudrate.B4000000, DataBits.DB_8, StopBits.SB_1, Parity.EVEN);
		assertEquals(0.00275, result);		
		result = SerialPortSocket.calculateMillisPerByte(Baudrate.B4000000, DataBits.DB_8, StopBits.SB_2, Parity.EVEN);
		assertEquals(0.003, result);
		result = SerialPortSocket.calculateMillisPerByte(Baudrate.B4000000, DataBits.DB_8, StopBits.SB_1, Parity.NONE);
		assertEquals(0.0025, result);		
		//Shortest Time
		result = SerialPortSocket.calculateMillisPerByte(Baudrate.B4000000, DataBits.DB_5, StopBits.SB_1, Parity.NONE);
		assertEquals(0.00175, result);		
	}

}
