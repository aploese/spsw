/*-
 * #%L
 * SPSW Provider
 * %%
 * Copyright (C) 2009 - 2018 Arne Plöse
 * %%
 * SPSW - Drivers for the serial port, https://github.com/aploese/spsw/
 * Copyright (C) 2009-2018, Arne Plöse and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
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
 * #L%
 */
package de.ibapl.spsw.jniprovider;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;
import java.util.Iterator;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Arne Plöse
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
