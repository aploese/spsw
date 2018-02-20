package de.ibapl.spsw.tests;

import java.util.logging.Level;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import de.ibapl.spsw.api.SerialPortSocketFactory;
import de.ibapl.spsw.provider.GenericTermiosSerialPortSocket;
import de.ibapl.spsw.provider.SerialPortSocketFactoryImpl;

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
