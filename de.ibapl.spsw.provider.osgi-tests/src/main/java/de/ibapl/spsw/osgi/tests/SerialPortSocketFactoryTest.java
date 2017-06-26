package de.ibapl.spsw.osgi.tests;

import de.ibapl.spsw.api.SerialPortSocketFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class SerialPortSocketFactoryTest {

    private final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

    ;
    
    // Why is ref == null ??? 
    @Test
//    @Ignore
    public void testSerialPortSocketFactory() throws Exception {
        System.out.println("RUN de.ibapl.spsw.osgi.tests.SerialPortSocketFactoryTest.testSerialPortSocketFactory()");

        ServiceReference<SerialPortSocketFactory> ref = context.getServiceReference(SerialPortSocketFactory.class);
        assertNotNull("No such service", ref);
        SerialPortSocketFactory serialPortSocketFactory = context.getService(ref);
        assertNotNull("Service object init error", serialPortSocketFactory);
        System.out.println("FINISHED de.ibapl.spsw.osgi.tests.SerialPortSocketFactoryTest.testSerialPortSocketFactory()");
    }
}
