package de.ibapl.spsw.osgi.tests;

import de.ibapl.spsw.api.SerialPortSocketFactory;
import org.junit.After;
//import de.ibapl.spsw.provider.SerialPortSocketFactoryImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SerialPortSocketFactoryTest {
    

    private final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();;
    
/*    
    @Test
    public void testSerialPortSocketFactoryImpl() throws Exception {
        System.out.println("RUN de.ibapl.spsw.osgi.tests.SerialPortSocketFactoryTest.testSerialPortSocketFactoryImpl()");
        Assert.assertTrue("Native lib not loaded", SerialPortSocketFactoryImpl.singleton().isLibLoaded());
        System.out.println("FINISHED de.ibapl.spsw.osgi.tests.SerialPortSocketFactoryTest.testSerialPortSocketFactoryImpl()");
    }
*/
 
    // Why is ref == null ??? 
    @Test
    @Ignore
    public void testSerialPortSocketFactory() throws Exception {
        System.out.println("RUN de.ibapl.spsw.osgi.tests.SerialPortSocketFactoryTest.testSerialPortSocketFactory()");
        
        ServiceReference<SerialPortSocketFactory> ref = context.getServiceReference(SerialPortSocketFactory.class);
        Assert.assertNotNull("No such service", ref);
        SerialPortSocketFactory serialPortSocket = context.getService(ref);
        Assert.assertNotNull("Service object init error", serialPortSocket);
        try {
            serialPortSocket.createSerialPortSocket(serialPortSocket.getClass().getCanonicalName());
        } catch (Exception e) {
	           System.err.println("ERR de.ibapl.spsw.osgi.tests.SerialPortSocketFactoryTest.testSerialPortSocketFactory() " + e);	
        }
        System.out.println("FINISHED de.ibapl.spsw.osgi.tests.SerialPortSocketFactoryTest.testSerialPortSocketFactory()");
    }
}
