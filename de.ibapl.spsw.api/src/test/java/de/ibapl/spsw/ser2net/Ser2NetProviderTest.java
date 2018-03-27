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
package de.ibapl.spsw.ser2net;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;

/**
 *
 * @author Arne Plöse
 */
public class Ser2NetProviderTest {

	public Ser2NetProviderTest() {
	}

	@BeforeAll
	public static void setUpClass() {
	}

	@AfterAll
	public static void tearDownClass() {
	}

	@BeforeEach
	public void setUp() {
	}

	@AfterEach
	public void tearDown() {
	}

	/**
	 * Test of URI method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testURL() throws Exception {
		URI uri = new URI("rs232+udp", null, "localhost", 4001, "/dev/ttyUSB0",
				"remoteprovider=ser2net&controlport=4000", null);
		System.err.println(uri.toString());
		uri = new URI("rs232+tcp", null, "localhost", 4001, "/dev/ttyUSB0", "remoteprovider=ser2net&controlport=4000",
				null);
		System.err.println(uri.toString());
		uri = new URI("rs232+tcp", null, "localhost", 4001, "/dev/ttyUSB0", null, null);
		System.err.println(uri.toString());
		uri = new URI("rs232+rmi", null, "localhost", 4001, "/dev/ttyUSB0", "provider=spsw", null);
		System.err.println(uri.toString());
		uri = new URI("rs232", null, null, -1, "/dev/ttyUSB0", null, null);
		System.err.println(uri.toString());
		uri = URI.create("file:///dev/ttyUSB0");
		System.err.println(uri.toString());
		uri = URI.create("/dev/ttyUSB0");
		System.err.println(uri.toString());
		fail("Not implemented yet");
	}

	/**
	 * Test of isClosed method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testIsClosed() throws Exception {
		System.out.println("isClosed");
		Ser2NetProvider instance = null;
		boolean expResult = false;
		boolean result = instance.isClosed();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of isCTS method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testIsCTS() throws Exception {
		System.out.println("isCTS");
		Ser2NetProvider instance = null;
		boolean expResult = false;
		boolean result = instance.isCTS();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of isDSR method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testIsDSR() throws Exception {
		System.out.println("isDSR");
		Ser2NetProvider instance = null;
		boolean expResult = false;
		boolean result = instance.isDSR();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of isDCD method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testIsDCD() throws Exception {
		System.out.println("isDCD");
		Ser2NetProvider instance = null;
		boolean expResult = false;
		boolean result = instance.isDCD();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of isIncommingRI method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testIsIncommingRI() throws Exception {
		System.out.println("isIncommingRI");
		Ser2NetProvider instance = null;
		boolean expResult = false;
		boolean result = instance.isRI();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getInputStream method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testGetInputStream() throws Exception {
		System.out.println("getInputStream");
		Ser2NetProvider instance = null;
		InputStream expResult = null;
		InputStream result = instance.getInputStream();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getOutputStream method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testGetOutputStream() throws Exception {
		System.out.println("getOutputStream");
		Ser2NetProvider instance = null;
		OutputStream expResult = null;
		OutputStream result = instance.getOutputStream();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getPortName method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testGetPortName() {
		System.out.println("getPortName");
		Ser2NetProvider instance = null;
		String expResult = "";
		String result = instance.getPortName();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of isOpen method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testIsOpen() {
		System.out.println("isOpen");
		Ser2NetProvider instance = null;
		boolean expResult = false;
		boolean result = instance.isOpen();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of openRaw method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testOpen_0args() throws Exception {
		System.out.println("openRaw");
		Ser2NetProvider instance = null;
		instance.open();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of openRaw method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testOpen_5args() throws Exception {
		System.out.println("openRaw");
		Speed baudRate = null;
		DataBits dataBits = null;
		StopBits stopBits = null;
		Parity parity = null;
		Set<FlowControl> flowControls = null;
		Ser2NetProvider instance = null;
		instance.open(baudRate, dataBits, stopBits, parity, flowControls);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of close method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testClose() throws Exception {
		System.out.println("close");
		Ser2NetProvider instance = null;
		instance.close();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setRTS method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testSetRTS() throws Exception {
		System.out.println("setRTS");
		boolean value = false;
		Ser2NetProvider instance = null;
		instance.setRTS(value);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setDTR method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testSetDTR() throws Exception {
		System.out.println("setDTR");
		boolean value = false;
		Ser2NetProvider instance = null;
		instance.setDTR(value);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setXONChar method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testSetXONChar() throws Exception {
		System.out.println("setXONChar");
		char c = ' ';
		Ser2NetProvider instance = null;
		instance.setXONChar(c);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setXOFFChar method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testSetXOFFChar() throws Exception {
		System.out.println("setXOFFChar");
		char c = ' ';
		Ser2NetProvider instance = null;
		instance.setXOFFChar(c);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getXONChar method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testGetXONChar() throws Exception {
		System.out.println("getXONChar");
		Ser2NetProvider instance = null;
		char expResult = ' ';
		char result = instance.getXONChar();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getXOFFChar method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testGetXOFFChar() throws Exception {
		System.out.println("getXOFFChar");
		Ser2NetProvider instance = null;
		char expResult = ' ';
		char result = instance.getXOFFChar();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of sendBreak method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testSendBreak() throws Exception {
		System.out.println("sendBreak");
		int duration = 0;
		Ser2NetProvider instance = null;
		instance.sendBreak(duration);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of sendXON method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testSendXON() throws Exception {
		System.out.println("sendXON");
		Ser2NetProvider instance = null;
		instance.sendXON();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of sendXOFF method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testSendXOFF() throws Exception {
		System.out.println("sendXOFF");
		Ser2NetProvider instance = null;
		instance.sendXOFF();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getInBufferBytesCount method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testGetInBufferBytesCount() throws Exception {
		System.out.println("getInBufferBytesCount");
		Ser2NetProvider instance = null;
		int expResult = 0;
		int result = instance.getInBufferBytesCount();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getOutBufferBytesCount method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testGetOutBufferBytesCount() throws Exception {
		System.out.println("getOutBufferBytesCount");
		Ser2NetProvider instance = null;
		int expResult = 0;
		int result = instance.getOutBufferBytesCount();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setBreak method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testSetBreak() throws Exception {
		System.out.println("setBreak");
		boolean value = false;
		Ser2NetProvider instance = null;
		instance.setBreak(value);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setFlowControl method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testSetFlowControl() throws Exception {
		System.out.println("setFlowControl");
		Set<FlowControl> flowControls = null;
		Ser2NetProvider instance = null;
		instance.setFlowControl(flowControls);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setSpeed method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testSetSpeed() throws Exception {
		System.out.println("setSpeed");
		Speed speed = null;
		Ser2NetProvider instance = null;
		instance.setSpeed(speed);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setDataBits method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testSetDataBits() throws Exception {
		System.out.println("setDataBits");
		DataBits dataBits = null;
		Ser2NetProvider instance = null;
		instance.setDataBits(dataBits);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setStopBits method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testSetStopBits() throws Exception {
		System.out.println("setStopBits");
		StopBits stopBits = null;
		Ser2NetProvider instance = null;
		instance.setStopBits(stopBits);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setParity method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testSetParity() throws Exception {
		System.out.println("setParity");
		Parity parity = null;
		Ser2NetProvider instance = null;
		instance.setParity(parity);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getSpeed method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testGetSpeed() throws Exception {
		System.out.println("getBaudrate");
		Ser2NetProvider instance = null;
		Speed expResult = null;
		Speed result = instance.getSpeed();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getDatatBits method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testGetDatatBits() throws Exception {
		System.out.println("getDatatBits");
		Ser2NetProvider instance = null;
		DataBits expResult = null;
		DataBits result = instance.getDatatBits();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getStopBits method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testGetStopBits() throws Exception {
		System.out.println("getStopBits");
		Ser2NetProvider instance = null;
		StopBits expResult = null;
		StopBits result = instance.getStopBits();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getParity method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testGetParity() throws Exception {
		System.out.println("getParity");
		Ser2NetProvider instance = null;
		Parity expResult = null;
		Parity result = instance.getParity();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getFlowControl method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testGetFlowControl() throws Exception {
		System.out.println("getFlowControl");
		Ser2NetProvider instance = null;
		Set<FlowControl> expResult = null;
		Set<FlowControl> result = instance.getFlowControl();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getOverallReadTimeout method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testGetOverallReadTimeout() throws Exception {
		System.out.println("getOverallReadTimeout");
		Ser2NetProvider instance = null;
		int expResult = 0;
		int result = instance.getOverallReadTimeout();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getInterByteReadTimeout method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testGetInterByteReadTimeout() throws Exception {
		System.out.println("getInterByteReadTimeout");
		Ser2NetProvider instance = null;
		int expResult = 0;
		int result = instance.getInterByteReadTimeout();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setReadTimeouts method, of class Ser2NetProvider.
	 */
	@Test
	@Disabled
	public void testSetReadTimeouts() throws Exception {
		System.out.println("setReadTimeouts");
		int interByteReadTimeout = 0;
		int overallReadTimeout = 0;
		int overallWriteTimeout = 0;
		Ser2NetProvider instance = null;
		instance.setTimeouts(interByteReadTimeout, overallReadTimeout, overallWriteTimeout);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

}
