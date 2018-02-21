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
		Assume.assumeNotNull(writeSpc);
		LOG.log(Level.INFO, "run testRTS");
		openDefault();
		
		writeSpc.setRTS(true);
		if (writeSpc instanceof GenericTermiosSerialPortSocket) {
			Assert.assertTrue(((GenericTermiosSerialPortSocket) writeSpc).isRTS());
		}
		writeSpc.setRTS(false);
		if (writeSpc instanceof GenericTermiosSerialPortSocket) {
			Assert.assertFalse(((GenericTermiosSerialPortSocket) writeSpc).isRTS());
		}
	}
        
        	@Test
	public void testDTR() throws Exception {
		Assume.assumeNotNull(writeSpc);
		LOG.log(Level.INFO, "run testDTR");
		openDefault();

		writeSpc.setDTR(true);
		if (writeSpc instanceof GenericTermiosSerialPortSocket) {
			Assert.assertTrue(((GenericTermiosSerialPortSocket) writeSpc).isDTR());
		}
		writeSpc.setDTR(false);
		if (writeSpc instanceof GenericTermiosSerialPortSocket) {
			Assert.assertFalse(((GenericTermiosSerialPortSocket) writeSpc).isDTR());
		}

		writeSpc.close();
		Assert.assertTrue(writeSpc.isClosed());
	}

}
