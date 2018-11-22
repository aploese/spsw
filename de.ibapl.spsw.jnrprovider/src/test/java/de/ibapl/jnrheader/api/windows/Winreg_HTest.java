package de.ibapl.jnrheader.api.windows;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.api.windows.Minwindef_H.PHKEY;
import de.ibapl.jnrheader.api.windows.Winreg_H.REGSAM;
import de.ibapl.spsw.jnrprovider.GenericWinSerialPortSocket;

import java.util.List;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

/**
 *
 * @author aploese
 */
@EnabledOnOs({ OS.WINDOWS })
public class Winreg_HTest {

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    
    @Test
    public void testLPWSTR() throws Exception {
    	
    	List<String> l = GenericWinSerialPortSocket.getWindowsBasedPortNames();
    	assertEquals(1, l.size());
    	
		Winreg_H winreg_H = JnrHeader.getInstance(Winreg_H.class);
		Winerr_H winerr_H = JnrHeader.getInstance(Winerr_H.class);
		Winnt_H winnt_H = JnrHeader.getInstance(Winnt_H.class);
		Winbase_H winbase_H = JnrHeader.getInstance(Winbase_H.class);
		
		PHKEY phkResult = new PHKEY();
		REGSAM samDesired = REGSAM.of(Winnt_H.KEY_READ);
		if (winreg_H.RegOpenKeyExW(Winreg_H.HKEY_CURRENT_USER, null, 0, samDesired, phkResult)
				== Winerr_H.ERROR_SUCCESS) {
		} else {
			// Iterate or get Username 
			// "\Volatile Environment\1"
			
	    	fail("Cant open ");
		}
    	fail("Winreg");
    }

}
