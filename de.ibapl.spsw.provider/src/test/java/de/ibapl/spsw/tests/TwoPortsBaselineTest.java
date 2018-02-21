package de.ibapl.spsw.tests;

import de.ibapl.spsw.api.SerialPortSocketFactory;
import de.ibapl.spsw.provider.SerialPortSocketFactoryImpl;

/**
 * Unit test for simple App. Timeout is computed 8 data bits + 2 stop bits +
 * parity bit + start bit == 12
 */
public class TwoPortsBaselineTest extends AbstractTwoPortsBaselineTest {

    protected SerialPortSocketFactory getSerialPortSocketFactory() {
        return SerialPortSocketFactoryImpl.singleton();
    }
}