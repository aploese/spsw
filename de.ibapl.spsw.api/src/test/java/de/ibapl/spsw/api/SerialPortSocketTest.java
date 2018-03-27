/*-
 * #%L
 * SPSW API
 * %%
 * Copyright (C) 2009 - 2018 Arne Plöse
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */
package de.ibapl.spsw.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * 
 * @author Arne Plöse
 *
 */
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
	void testCalculateMillisForCharacters() {
		int result = SerialPortSocket.calculateMillisForCharacters(1024, Speed._50_BPS, DataBits.DB_8, StopBits.SB_2,
				Parity.EVEN);
		assertEquals(245760, result);
		result = SerialPortSocket.calculateMillisForCharacters(1024, Speed._50_BPS, DataBits.DB_8, StopBits.SB_1,
				Parity.EVEN);
		assertEquals(225280, result);
		result = SerialPortSocket.calculateMillisForCharacters(1024, Speed._50_BPS, DataBits.DB_8, StopBits.SB_2,
				Parity.EVEN);
		assertEquals(245760, result);
		result = SerialPortSocket.calculateMillisForCharacters(1024, Speed._9600_BPS, DataBits.DB_8, StopBits.SB_1,
				Parity.EVEN);
		assertEquals(1174, result);
		result = SerialPortSocket.calculateMillisForCharacters(1024, Speed._4000000_BPS, DataBits.DB_8, StopBits.SB_1,
				Parity.EVEN);
		assertEquals(3, result);
		result = SerialPortSocket.calculateMillisForCharacters(1024, Speed._4000000_BPS, DataBits.DB_8, StopBits.SB_2,
				Parity.EVEN);
		assertEquals(4, result);
		result = SerialPortSocket.calculateMillisForCharacters(1024, Speed._4000000_BPS, DataBits.DB_8, StopBits.SB_1,
				Parity.NONE);
		assertEquals(3, result);
		result = SerialPortSocket.calculateMillisForCharacters(1024, Speed._4000000_BPS, DataBits.DB_5, StopBits.SB_1,
				Parity.NONE);
		assertEquals(2, result);

		// This should sum up to a second. 10 Bit per character to transfer and 5
		// characters to transfer.
		result = SerialPortSocket.calculateMillisForCharacters(5, Speed._50_BPS, DataBits.DB_8, StopBits.SB_1,
				Parity.NONE);
		assertEquals(1000, result);

	}

	@Test
	void testCalculateMillisPerCharacter() {
		// Longest Time
		double result = SerialPortSocket.calculateMillisPerCharacter(Speed._50_BPS, DataBits.DB_8, StopBits.SB_2,
				Parity.EVEN);
		assertEquals(240.0, result);
		result = SerialPortSocket.calculateMillisPerCharacter(Speed._50_BPS, DataBits.DB_8, StopBits.SB_1, Parity.EVEN);
		assertEquals(220.0, result);
		result = SerialPortSocket.calculateMillisPerCharacter(Speed._50_BPS, DataBits.DB_8, StopBits.SB_2, Parity.EVEN);
		assertEquals(240.0, result);
		result = SerialPortSocket.calculateMillisPerCharacter(Speed._9600_BPS, DataBits.DB_8, StopBits.SB_1,
				Parity.EVEN);
		assertEquals(1.1458333333333333, result);
		result = SerialPortSocket.calculateMillisPerCharacter(Speed._4000000_BPS, DataBits.DB_8, StopBits.SB_1,
				Parity.EVEN);
		assertEquals(0.00275, result);
		result = SerialPortSocket.calculateMillisPerCharacter(Speed._4000000_BPS, DataBits.DB_8, StopBits.SB_2,
				Parity.EVEN);
		assertEquals(0.003, result);
		result = SerialPortSocket.calculateMillisPerCharacter(Speed._4000000_BPS, DataBits.DB_8, StopBits.SB_1,
				Parity.NONE);
		assertEquals(0.0025, result);
		// Shortest Time
		result = SerialPortSocket.calculateMillisPerCharacter(Speed._4000000_BPS, DataBits.DB_5, StopBits.SB_1,
				Parity.NONE);
		assertEquals(0.00175, result);
	}

	@Test
	void calculateSpeedInCharactersPerSecond() {
		// Lowest speed
		double result = SerialPortSocket.calculateSpeedInCharactersPerSecond(Speed._50_BPS, DataBits.DB_8,
				StopBits.SB_2, Parity.EVEN);
		assertEquals(50.0 / 12.0, result);
		assertEquals(4.166666666666667, result);
		result = SerialPortSocket.calculateSpeedInCharactersPerSecond(Speed._50_BPS, DataBits.DB_8, StopBits.SB_1,
				Parity.EVEN);
		assertEquals(50.0 / 11.0, result);
		assertEquals(4.545454545454546, result);
		result = SerialPortSocket.calculateSpeedInCharactersPerSecond(Speed._50_BPS, DataBits.DB_8, StopBits.SB_2,
				Parity.EVEN);
		assertEquals(50.0 / 12.0, result);
		assertEquals(4.166666666666667, result);
		result = SerialPortSocket.calculateSpeedInCharactersPerSecond(Speed._9600_BPS, DataBits.DB_8, StopBits.SB_1,
				Parity.EVEN);
		assertEquals(9600.0 / 11.0, result);
		assertEquals(872.7272727272727, result);
		result = SerialPortSocket.calculateSpeedInCharactersPerSecond(Speed._4000000_BPS, DataBits.DB_8, StopBits.SB_1,
				Parity.EVEN);
		assertEquals(4000000.0 / 11.0, result);
		assertEquals(363636.36363636365, result);
		result = SerialPortSocket.calculateSpeedInCharactersPerSecond(Speed._4000000_BPS, DataBits.DB_8, StopBits.SB_2,
				Parity.EVEN);
		assertEquals(4000000.0 / 12.0, result);
		assertEquals(333333.3333333333, result);
		result = SerialPortSocket.calculateSpeedInCharactersPerSecond(Speed._4000000_BPS, DataBits.DB_8, StopBits.SB_1,
				Parity.NONE);
		assertEquals(4000000.0 / 10.0, result);
		assertEquals(400000, result);
		// Highest speed
		result = SerialPortSocket.calculateSpeedInCharactersPerSecond(Speed._4000000_BPS, DataBits.DB_5, StopBits.SB_1,
				Parity.NONE);
		assertEquals(4000000.0 / 7.0, result);
		assertEquals(571428.5714285715, result);
	}

}
