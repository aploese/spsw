package de.ibapl.jnrheader.api.windows;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import de.ibapl.jnrheader.api.windows.Minwindef_H.LPWSTR;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

/**
 *
 * @author aploese
 */
@EnabledOnOs({ OS.WINDOWS })
public class Minwindef_HTest {
    private static final String _A2Z_ = "abcdefghijklmnopqrstuvwxyz".toUpperCase(); 

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
        LPWSTR lpwstr = LPWSTR.of(_A2Z_);
        
        assertEquals(_A2Z_, lpwstr.toString());
        assertEquals(_A2Z_, lpwstr.toString());
        final byte[] data = new byte[lpwstr.backingBuffer().remaining()];
        assertEquals(_A2Z_.length() * 2, data.length);
        ((ByteBuffer)lpwstr.backingBuffer()).get(data);
        lpwstr.backingBuffer().position(0);
        
        lpwstr = LPWSTR.allocate(1024);
        ((ByteBuffer)lpwstr.backingBuffer()).put(data);
        lpwstr.backingBuffer().position(0);
        lpwstr.backingBuffer().limit(data.length);
        //data null terminated???

        assertEquals(_A2Z_, lpwstr.toString());
        
        fail("Handle or Check Null termination ???.");
    }

}
