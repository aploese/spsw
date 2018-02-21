package de.ibapl.spsw.tests;

import de.ibapl.spsw.api.SerialPortSocketFactory;
import de.ibapl.spsw.provider.SerialPortSocketFactoryImpl;

/**
 * Unit test for simple App. No Handshake (RTS/CTS etc.) Only 1 or 2 Stoppbits
 * Baudrates from 300 up to 115200 Parity 8 Databits
 * 
 */
public class BaselineOnePortTest extends AbstractBaselineOnePortTest {

	protected SerialPortSocketFactory getSerialPortSocketFactory() {
		return SerialPortSocketFactoryImpl.singleton();
	}

}
