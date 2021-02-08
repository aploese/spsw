/*
 * SPSW - Drivers for the serial port, https://github.com/aploese/spsw/
 * Copyright (C) 2009-2021, Arne Plöse and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
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
 */
package de.ibapl.spsw.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 *
 * @author Arne Plöse
 *
 */
public class SerialPortSocketTest {

    @Test
    public void testCalculateMillisForCharacters() {
        int result = SerialPortConfiguration.calculateMillisForCharacters(1024, Speed._50_BPS, DataBits.DB_8, StopBits.SB_2,
                Parity.EVEN);
        assertEquals(245760, result);
        result = SerialPortConfiguration.calculateMillisForCharacters(1024, Speed._50_BPS, DataBits.DB_8, StopBits.SB_1,
                Parity.EVEN);
        assertEquals(225280, result);
        result = SerialPortConfiguration.calculateMillisForCharacters(1024, Speed._50_BPS, DataBits.DB_8, StopBits.SB_2,
                Parity.EVEN);
        assertEquals(245760, result);
        result = SerialPortConfiguration.calculateMillisForCharacters(1024, Speed._9600_BPS, DataBits.DB_8, StopBits.SB_1,
                Parity.EVEN);
        assertEquals(1174, result);
        result = SerialPortConfiguration.calculateMillisForCharacters(1024, Speed._4000000_BPS, DataBits.DB_8, StopBits.SB_1,
                Parity.EVEN);
        assertEquals(3, result);
        result = SerialPortConfiguration.calculateMillisForCharacters(1024, Speed._4000000_BPS, DataBits.DB_8, StopBits.SB_2,
                Parity.EVEN);
        assertEquals(4, result);
        result = SerialPortConfiguration.calculateMillisForCharacters(1024, Speed._4000000_BPS, DataBits.DB_8, StopBits.SB_1,
                Parity.NONE);
        assertEquals(3, result);
        result = SerialPortConfiguration.calculateMillisForCharacters(1024, Speed._4000000_BPS, DataBits.DB_5, StopBits.SB_1,
                Parity.NONE);
        assertEquals(2, result);

        // This should sum up to a second. 10 Bit per character to transfer and 5
        // characters to transfer.
        result = SerialPortConfiguration.calculateMillisForCharacters(5, Speed._50_BPS, DataBits.DB_8, StopBits.SB_1,
                Parity.NONE);
        assertEquals(1000, result);

    }

    @Test
    public void testCalculateMillisPerCharacter() {
        // Longest Time
        double result = SerialPortConfiguration.calculateMillisPerCharacter(Speed._50_BPS, DataBits.DB_8, StopBits.SB_2,
                Parity.EVEN);
        assertEquals(240.0, result);
        result = SerialPortConfiguration.calculateMillisPerCharacter(Speed._50_BPS, DataBits.DB_8, StopBits.SB_1, Parity.EVEN);
        assertEquals(220.0, result);
        result = SerialPortConfiguration.calculateMillisPerCharacter(Speed._50_BPS, DataBits.DB_8, StopBits.SB_2, Parity.EVEN);
        assertEquals(240.0, result);
        result = SerialPortConfiguration.calculateMillisPerCharacter(Speed._9600_BPS, DataBits.DB_8, StopBits.SB_1,
                Parity.EVEN);
        assertEquals(1.1458333333333333, result);
        result = SerialPortConfiguration.calculateMillisPerCharacter(Speed._4000000_BPS, DataBits.DB_8, StopBits.SB_1,
                Parity.EVEN);
        assertEquals(0.00275, result);
        result = SerialPortConfiguration.calculateMillisPerCharacter(Speed._4000000_BPS, DataBits.DB_8, StopBits.SB_2,
                Parity.EVEN);
        assertEquals(0.003, result);
        result = SerialPortConfiguration.calculateMillisPerCharacter(Speed._4000000_BPS, DataBits.DB_8, StopBits.SB_1,
                Parity.NONE);
        assertEquals(0.0025, result);
        // Shortest Time
        result = SerialPortConfiguration.calculateMillisPerCharacter(Speed._4000000_BPS, DataBits.DB_5, StopBits.SB_1,
                Parity.NONE);
        assertEquals(0.00175, result);
    }

    @Test
    public void calculateSpeedInCharactersPerSecond() {
        // Lowest speed
        double result = SerialPortConfiguration.calculateSpeedInCharactersPerSecond(Speed._50_BPS, DataBits.DB_8,
                StopBits.SB_2, Parity.EVEN);
        assertEquals(50.0 / 12.0, result);
        assertEquals(4.166666666666667, result);
        result = SerialPortConfiguration.calculateSpeedInCharactersPerSecond(Speed._50_BPS, DataBits.DB_8, StopBits.SB_1,
                Parity.EVEN);
        assertEquals(50.0 / 11.0, result);
        assertEquals(4.545454545454546, result);
        result = SerialPortConfiguration.calculateSpeedInCharactersPerSecond(Speed._50_BPS, DataBits.DB_8, StopBits.SB_2,
                Parity.EVEN);
        assertEquals(50.0 / 12.0, result);
        assertEquals(4.166666666666667, result);
        result = SerialPortConfiguration.calculateSpeedInCharactersPerSecond(Speed._9600_BPS, DataBits.DB_8, StopBits.SB_1,
                Parity.EVEN);
        assertEquals(9600.0 / 11.0, result);
        assertEquals(872.7272727272727, result);
        result = SerialPortConfiguration.calculateSpeedInCharactersPerSecond(Speed._4000000_BPS, DataBits.DB_8, StopBits.SB_1,
                Parity.EVEN);
        assertEquals(4000000.0 / 11.0, result);
        assertEquals(363636.36363636365, result);
        result = SerialPortConfiguration.calculateSpeedInCharactersPerSecond(Speed._4000000_BPS, DataBits.DB_8, StopBits.SB_2,
                Parity.EVEN);
        assertEquals(4000000.0 / 12.0, result);
        assertEquals(333333.3333333333, result);
        result = SerialPortConfiguration.calculateSpeedInCharactersPerSecond(Speed._4000000_BPS, DataBits.DB_8, StopBits.SB_1,
                Parity.NONE);
        assertEquals(4000000.0 / 10.0, result);
        assertEquals(400000, result);
        // Highest speed
        result = SerialPortConfiguration.calculateSpeedInCharactersPerSecond(Speed._4000000_BPS, DataBits.DB_5, StopBits.SB_1,
                Parity.NONE);
        assertEquals(4000000.0 / 7.0, result);
        assertEquals(571428.5714285715, result);
    }

}
