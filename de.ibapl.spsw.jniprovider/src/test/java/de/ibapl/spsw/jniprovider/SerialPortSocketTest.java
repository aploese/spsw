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

/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package de.ibapl.spsw.jniprovider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.util.Iterator;
import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;

import de.ibapl.spsw.api.SerialPortSocketFactory;

/**
 *
 * @author Arne Plöse
 */
public class SerialPortSocketTest {

	// TODO add test methods here.
	// The methods must be annotated with annotation @Test. For example:
	//
	// @Test
	// public void hello() {}
	@Test
	public void testSPSWProps() {
		URL u = AbstractSerialPortSocket.class.getClassLoader()
				.getResource(SerialPortSocketFactoryImpl.SPSW_PROPERTIES);
		assertNotNull(u);
	}

	@Test
	public void testLoadLib() {
		SerialPortSocketFactoryImpl spsf = new SerialPortSocketFactoryImpl();
		spsf.activate();
		assertTrue(SerialPortSocketFactoryImpl.isLibLoaded());
	}

	@Test
	public void testServiceLoader() throws Exception {
		ServiceLoader<SerialPortSocketFactory> spsFactoryLoader = ServiceLoader.load(SerialPortSocketFactory.class);
		Iterator<SerialPortSocketFactory> spsFactoryIterator = spsFactoryLoader.iterator();
		assertTrue(spsFactoryIterator.hasNext());
		SerialPortSocketFactory serialPortSocketFactory = spsFactoryIterator.next();
		assertEquals(SerialPortSocketFactoryImpl.class, serialPortSocketFactory.getClass());
		// We excpect to have ony one implementation hanging around...
		assertFalse(spsFactoryIterator.hasNext());

		// We get the same ServiceLoader Instance, so we get the same
		// SerialPortSocketFactory
		spsFactoryIterator = spsFactoryLoader.iterator();
		assertTrue(spsFactoryIterator.hasNext());
		SerialPortSocketFactory serialPortSocketFactory1 = spsFactoryIterator.next();
		assertEquals(SerialPortSocketFactoryImpl.class, serialPortSocketFactory1.getClass());
		assertTrue(serialPortSocketFactory == serialPortSocketFactory1, "Not the same Instance");
		// We excpect to have ony one implementation hanging around...
		assertFalse(spsFactoryIterator.hasNext());

		// We get a new ServiceLoader Instance, so we get a new SerialPortSocketFactory
		spsFactoryLoader = ServiceLoader.load(SerialPortSocketFactory.class);
		spsFactoryIterator = spsFactoryLoader.iterator();
		assertTrue(spsFactoryIterator.hasNext());
		SerialPortSocketFactory serialPortSocketFactory2 = spsFactoryIterator.next();
		assertEquals(SerialPortSocketFactoryImpl.class, serialPortSocketFactory1.getClass());
		assertTrue(serialPortSocketFactory != serialPortSocketFactory2, "The same Instance");
		// We excpect to have ony one implementation hanging around...
		assertFalse(spsFactoryIterator.hasNext());

	}

}
