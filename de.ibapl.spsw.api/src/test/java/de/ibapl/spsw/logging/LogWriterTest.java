/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ibapl.spsw.logging;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author aploese
 */
public class LogWriterTest {
    
    public LogWriterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Ignore
    @Test
    public void testLogWrite() {
        System.out.println("logWrite");
        Instant ts = Instant.parse("2017-07-25T18:47:02.763Z");
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
        LogWriter instance = new LogWriter(os, false);
        instance.beforeRead(ts);
        instance.beforeWrite(ts, (byte)1);
        instance.afterWrite(ts);
        instance.beforeWrite(ts, (byte)2);
        instance.afterWrite(ts);
        ts = ts.plusMillis(1);
        instance.beforeWrite(ts, (byte)3);
        instance.afterWrite(ts);
        instance.afterRead(ts, 1);
        instance.beforeRead(ts);
        ts = ts.plusNanos(10);
        instance.afterRead(ts, 2);
        instance.beforeRead(ts);
        ts = ts.plusNanos(10);
        instance.afterRead(ts, 3);
        instance.beforeRead(ts);
        ts = ts.plusSeconds(1);
        instance.afterRead(ts, -1);
        assertEquals("", os.toString());
        fail();
    }
    
    @Ignore
    @Test
    public void testLogWriteASCII() {
        System.out.println("logWriteACII");
        Instant ts = Instant.parse("2017-07-25T18:47:02.763Z");
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
        LogWriter instance = new LogWriter(os, true);
        instance.beforeRead(ts);
        instance.beforeWrite(ts, (byte)'A');
        instance.afterWrite(ts);
        instance.beforeWrite(ts, (byte)'B');
        instance.afterWrite(ts);
        ts = ts.plusMillis(1);
        instance.beforeWrite(ts, (byte)'\n');
        instance.afterWrite(ts);
        instance.beforeFlush(ts);
        instance.afterRead(ts, 'A');
        instance.beforeRead(ts);
        ts = ts.plusNanos(10);
        instance.afterRead(ts, 'B');
        instance.beforeRead(ts);
        ts = ts.plusNanos(10);
        instance.afterRead(ts, '\n');
        instance.beforeRead(ts);
        ts = ts.plusSeconds(1);
        instance.afterRead(ts, -1);
        assertEquals("", os.toString());
        fail();
    }

}
