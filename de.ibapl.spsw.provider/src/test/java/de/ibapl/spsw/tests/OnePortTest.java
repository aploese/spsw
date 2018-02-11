package de.ibapl.spsw.tests;

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

import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.SerialPortSocketFactory;
import de.ibapl.spsw.provider.GenericTermiosSerialPortSocket;
import de.ibapl.spsw.provider.SerialPortSocketFactoryImpl;
import static de.ibapl.spsw.tests.AbstractOnePortTest.serialPortName;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.logging.Level;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

/**
 *
 * @author aploese
 */
public class OnePortTest extends AbstractOnePortTest {
    
    @Override
    protected SerialPortSocketFactory getSerialPortSocketFactory() {
        return SerialPortSocketFactoryImpl.singleton();
    }
    
    	@Test
	public void testRTS() throws Exception {
		Assume.assumeNotNull(spc);
		LOG.log(Level.INFO, "run testRTS");

		spc.openAsIs();

		spc.setRTS(true);
		if (spc instanceof GenericTermiosSerialPortSocket) {
			Assert.assertTrue(((GenericTermiosSerialPortSocket) spc).isRTS());
		}
		spc.setRTS(false);
		if (spc instanceof GenericTermiosSerialPortSocket) {
			Assert.assertFalse(((GenericTermiosSerialPortSocket) spc).isRTS());
		}

		spc.close();
		Assert.assertTrue(spc.isClosed());
	}
        
        	@Test
	public void testDTR() throws Exception {
		Assume.assumeNotNull(spc);
		LOG.log(Level.INFO, "run testDTR");

		spc.openAsIs();

		spc.setDTR(true);
		if (spc instanceof GenericTermiosSerialPortSocket) {
			Assert.assertTrue(((GenericTermiosSerialPortSocket) spc).isDTR());
		}
		spc.setDTR(false);
		if (spc instanceof GenericTermiosSerialPortSocket) {
			Assert.assertFalse(((GenericTermiosSerialPortSocket) spc).isDTR());
		}

		spc.close();
		Assert.assertTrue(spc.isClosed());
	}

}
