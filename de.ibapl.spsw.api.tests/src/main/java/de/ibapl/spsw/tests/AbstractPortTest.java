package de.ibapl.spsw.tests;

import java.util.logging.Logger;

import org.junit.After;

import de.ibapl.spsw.api.SerialPortSocketFactory;

public abstract class AbstractPortTest {
    protected static final int PORT_RECOVERY_TIME = 0;
	protected static final Logger LOG = Logger.getLogger("SpswTests");
    protected abstract SerialPortSocketFactory getSerialPortSocketFactory();

    @After
    public void tearDown() throws Exception {
        Runtime.getRuntime().gc();
        Runtime.getRuntime().runFinalization();
        if (PORT_RECOVERY_TIME > 0) {
        	Thread.sleep(PORT_RECOVERY_TIME * 1000);
        }
    }

}
