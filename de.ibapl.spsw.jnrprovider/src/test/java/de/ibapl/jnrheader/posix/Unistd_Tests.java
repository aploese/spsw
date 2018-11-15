package de.ibapl.jnrheader.posix;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.ibapl.jnrheader.JNRHeaderBase;
import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.isoc.Errno_H;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.function.Supplier;

class Unistd_Tests extends JNRHeaderBase {

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
    void testDefines() throws Exception {
        testDefines(Unistd_H.class);
    }

//	@Test
    void testOpen() throws Exception {
        Errno_H errno_H = JnrHeader.getInstance(Errno_H.class);
        Unistd_H unistd_H = JnrHeader.getInstance(Unistd_H.class);
        File f = File.createTempFile("Unist_Test", "data");

//		int fd = unistd_H.close(fd)(f.getAbsolutePath(), uni);
        int errno = errno_H.errno();
        errno = errno_H.errno();
        assertEquals(0, errno);
    }

    @Test
    void testReadWriteByteArray() throws Exception {
        Errno_H errno_H = JnrHeader.getInstance(Errno_H.class);
        Unistd_H unistd_H = JnrHeader.getInstance(Unistd_H.class);
        Fcntl_H fcntl_H = JnrHeader.getInstance(Fcntl_H.class);
        File f = File.createTempFile("Unist_Test_RW", ".data");
        f.deleteOnExit();
        
        int fd = fcntl_H.open(f.getAbsolutePath(), fcntl_H.O_RDWR);
        byte[] dataOut = "Hello".getBytes();
        unistd_H.write(fd, dataOut, dataOut.length);
        long currPos = unistd_H.lseek(fd, 0, unistd_H.SEEK_SET);
        
        assertEquals(0, currPos);
        byte[] dataIn = new byte[1024];
        long dataRead = unistd_H.read(fd, dataIn, dataOut.length);
        assertEquals(dataOut.length, dataRead);
        assertArrayEquals(dataOut, Arrays.copyOf(dataIn, dataOut.length));

        int closeResult =  unistd_H.close(fd);
        assertEquals(0, closeResult, () -> "Errorcode is: " + errno_H.errno());
        closeResult =  unistd_H.close(fd);
        assertEquals(-1, closeResult, () -> "Errorcode is: " + errno_H.errno());
        assertEquals(errno_H.EBADF, errno_H.errno());
    }

        @Test
    void testReadWriteByteBuffer() throws Exception {
        byte[] data = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".getBytes();    
        ByteBuffer bb = ByteBuffer.allocateDirect(1024);
            assertEquals(0, bb.position());
            assertEquals(1024, bb.limit());
            bb.put(data);
            int dataLength = bb.position();
            assertEquals(dataLength, bb.position());
            assertEquals(1024, bb.limit());
            bb.flip();
            assertEquals(0, bb.position());
            assertEquals(dataLength, bb.limit());
        File f = File.createTempFile("Unist_Test_RW_ByteBuffer", ".data");
        f.deleteOnExit();
            
        /*
            FileChannel fc = FileChannel.open(f.toPath(), StandardOpenOption.WRITE, StandardOpenOption.READ);
            int written = fc.write(bb);
            
            assertEquals(dataLength, written);
            assertEquals(dataLength, bb.position());
            assertEquals(dataLength, bb.limit());
            bb.clear();
            bb.limit(8);
            
            fc.position(0);

            int read = fc.read(bb);
            bb.limit(bb.capacity() - 1);
            assertEquals(8, read);
            read = fc.read(bb);
            assertEquals(15, read);
            
            byte[] dataIn = new byte[data.length];
            bb.flip();
            bb.get(dataIn);
            assertArrayEquals(data, dataIn);
            fc.close();
            
            
            fail("testReadWriteByteBuffer");
*/
        Errno_H errno_H = JnrHeader.getInstance(Errno_H.class);
        Unistd_H unistd_H = JnrHeader.getInstance(Unistd_H.class);
        Fcntl_H fcntl_H = JnrHeader.getInstance(Fcntl_H.class);

        
        int fd = fcntl_H.open(f.getAbsolutePath(), fcntl_H.O_RDWR);
        bb.position(dataLength / 2);
        unistd_H.write(fd, bb);
        long currPos = unistd_H.lseek(fd, 0, unistd_H.SEEK_SET);
        
        assertEquals(0, currPos);
        byte[] dataIn = new byte[1024];
        bb.clear();
        long readed = unistd_H.read(fd, bb);
        assertEquals(dataLength / 2, readed);
        assertEquals('a', bb.get());
        assertEquals('b', bb.get());
            
        int closeResult =  unistd_H.close(fd);
        assertEquals(0, closeResult, () -> "Errorcode is: " + errno_H.errno());
        closeResult =  unistd_H.close(fd);
        assertEquals(-1, closeResult, () -> "Errorcode is: " + errno_H.errno());
        assertEquals(errno_H.EBADF, errno_H.errno());
    }

}
