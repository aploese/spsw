/*-
 * #%L
 * SPSW Provider
 * %%
 * Copyright (C) 2009 - 2017 Arne Plöse
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
package de.ibapl.spsw.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import de.ibapl.spsw.provider.SerialPortSocketFactoryImpl;

/**
 * Unit test for simple App.
 */
public class IteratePortTest {

	private static final Logger LOG = Logger.getLogger("SerialTests");

	@Test
	public void testLoadNativeLib() throws Exception {
		LOG.info("Load native Lib");
		SerialPortSocketFactoryImpl serialPortSocketFactoryImpl = new SerialPortSocketFactoryImpl();
		serialPortSocketFactoryImpl.loadNativeLib();
		assertTrue(SerialPortSocketFactoryImpl.singleton().isLibLoaded());
	}

	@Test
	public void testList() throws Exception {
		LOG.info("Iterating serial ports");
		Set<String> ports = SerialPortSocketFactoryImpl.singleton().getPortNames(true);
		LOG.info(ports == null ? "null" : ports.size() + " serial ports found");
		for (String port : ports) {
			LOG.log(Level.INFO, "Found port: {0}", port);
		}
	}

}
