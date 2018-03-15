/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ibapl.spsw.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.ServiceLoader;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.SerialPortSocketFactory;

/**
 *
 * @author aploese
 */
public class MultiarchTupelBuilderTest {

	/**
	 * Test of getMultiarchTupel method, of class MultiarchTupelBuilder.
	 */
	@Disabled
	@Test
	public void testGetMultiarchTupel() {
		System.out.println("getMultiarchTupel");
		MultiarchTupelBuilder instance = new MultiarchTupelBuilder();
		String expResult = "x86_64-linux-gnu";
		Collection<String> result = instance.getMultiarchTupels();
		assertEquals(1, result.size());
		assertEquals(expResult, result.iterator().next());
	}

	@Test
	public void testGetMultiarchTupel_x86_64_linux_gnu() {
		System.out.println("getMultiarchTupel x86_64-linux-gnu");
		MultiarchTupelBuilder instance = new MultiarchTupelBuilder("unknown", "amd64", "Linux", "4.14.0-3-amd64", null,
				"64", "little", "");
		Collection<String> result = instance.getMultiarchTupels();
		assertEquals(1, result.size());
		assertEquals("x86_64-linux-gnu", result.iterator().next());
	}

	@Test
	public void testGetMultiarchTupel_arm_linux_gnueabihf() {
		System.out.println("getMultiarchTupel arm-linux-gnueabihf");
		MultiarchTupelBuilder instance = new MultiarchTupelBuilder("unknown", "arm", "Linux", "4.9.67-v7+", "gnueabihf",
				"32", "little", "");
		Collection<String> result = instance.getMultiarchTupels();
		assertEquals(1, result.size());
		assertEquals("arm-linux-gnueabihf", result.iterator().next());
	}

	@Test
	public void testGetMultiarchTupel_arm_linux_gnueabihf_NO_sun_arch_data_model() {
		System.out.println("getMultiarchTupel arm-linux-gnueabihf or arm-linux-gnueabi");
		MultiarchTupelBuilder instance = new MultiarchTupelBuilder("unknown", "arm", "Linux", "4.9.61-odroidxu4", null,
				"32", "little", "");
		Collection<String> result = instance.getMultiarchTupels();
		assertEquals(2, result.size());
		Iterator<String> iter = result.iterator();
		assertEquals("arm-linux-gnueabihf", iter.next());
		assertEquals("arm-linux-gnueabi", iter.next());
	}
	
}
