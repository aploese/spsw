package de.ibapl.spsw.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import de.ibapl.spsw.tests.tags.BaselineTest;
import de.ibapl.spsw.tests.tags.ByteChannelTest;

public abstract class AbstractChannelTests extends AbstractReadWriteTest {

	@BaselineTest
	@ByteChannelTest
	@Test
	public void test_Channel_Write_Read_Position() throws Exception {
		PortConfiguration pc = new PortConfigurationFactory().ofCurrent();
		open(pc);
		ByteBuffer sendBuffer = ByteBuffer.allocateDirect(64);
		sendBuffer.put("abcdefghijklmnopqrstuvwxyz".getBytes());
		sendBuffer.flip();
		final int writeStart = 2;
		sendBuffer.position(writeStart);

		long written = writeSpc.getChannel().write(sendBuffer);
		assertEquals(24, written);
		assertEquals(sendBuffer.position(), sendBuffer.limit());

		
		ByteBuffer recBuffer = ByteBuffer.allocateDirect(64);
		recBuffer.position(0);
		recBuffer.limit(2);

		long read = readSpc.getChannel().read(recBuffer);
		assertEquals(2, read);
		assertEquals(recBuffer.position(), recBuffer.limit());
		recBuffer.flip();
		assertEquals('c', (char)recBuffer.get());
		assertEquals('d', (char)recBuffer.get());
		recBuffer.limit(recBuffer.limit() + 5);
		read = readSpc.getChannel().read(recBuffer);
		assertEquals(5, read);
		assertEquals(recBuffer.position(), recBuffer.limit());
		recBuffer.flip();
		assertEquals('c', (char)recBuffer.get());
		assertEquals('d', (char)recBuffer.get());
		assertEquals('e', (char)recBuffer.get());
		assertEquals('f', (char)recBuffer.get());
		assertEquals('g', (char)recBuffer.get());
		assertEquals('h', (char)recBuffer.get());
		assertEquals('i', (char)recBuffer.get());
		
	}

}
