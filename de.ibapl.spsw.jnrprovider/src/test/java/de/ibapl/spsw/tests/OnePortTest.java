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
package de.ibapl.spsw.tests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.logging.Level;

import org.junit.jupiter.api.Test;

import de.ibapl.spsw.jnrprovider.PosixSerialPortSocket;
import de.ibapl.spsw.tests.tags.DtrDsrTest;
import de.ibapl.spsw.tests.tags.RtsCtsTest;

/**
 *
 * @author Arne Plöse
 */
public class OnePortTest extends AbstractOnePortTest {

	@Override
	@RtsCtsTest
	@Test
	public void testRTS() throws Exception {
		assumeWTest();
		LOG.log(Level.INFO, "run testRTS");
		openDefault();

		writeSpc.setRTS(true);
		if (writeSpc instanceof PosixSerialPortSocket) {
			assertTrue(((PosixSerialPortSocket) writeSpc).isRTS());
		}
		writeSpc.setRTS(false);
		if (writeSpc instanceof PosixSerialPortSocket) {
			assertFalse(((PosixSerialPortSocket) writeSpc).isRTS());
		}
	}

	@Override
	@DtrDsrTest
	@Test
	public void testDTR() throws Exception {
		assumeWTest();
		LOG.log(Level.INFO, "run testDTR");
		openDefault();

		writeSpc.setDTR(true);
		if (writeSpc instanceof PosixSerialPortSocket) {
			assertTrue(((PosixSerialPortSocket) writeSpc).isDTR());
		}
		writeSpc.setDTR(false);
		if (writeSpc instanceof PosixSerialPortSocket) {
			assertFalse(((PosixSerialPortSocket) writeSpc).isDTR());
		}

		writeSpc.close();
		assertTrue(writeSpc.isClosed());
	}

}
