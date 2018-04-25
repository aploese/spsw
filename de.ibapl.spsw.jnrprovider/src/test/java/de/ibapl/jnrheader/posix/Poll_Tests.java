package de.ibapl.jnrheader.posix;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.ibapl.jnrheader.JNRHeaderBase;
import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.posix.Poll_H.PollFd;

class Poll_Tests extends JNRHeaderBase {

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
		testDefines(Poll_H.class);
	}

	@Test
	void testErrorRead() throws Exception {
		File tmpFile = File.createTempFile("Poll_H", ".txt");
		tmpFile.delete();
		Poll_H poll_H = JnrHeader.getInstance(Poll_H.class);
		Fcntl_H fcntl_H = JnrHeader.getInstance(Fcntl_H.class);
		Unistd_H unistd_H = JnrHeader.getInstance(Unistd_H.class);
		final int fd = fcntl_H.open(tmpFile.getAbsolutePath(), fcntl_H.O_CREAT | fcntl_H.O_RDWR | fcntl_H.O_NONBLOCK);
		unistd_H.close(fd);
		try {
			PollFd[] pollFd = poll_H.createPollFd(2);
			pollFd[0].fd = fd;
			pollFd[0].events = poll_H.POLLOUT;
			pollFd[1].fd = -1;
			int poll_result = poll_H.poll(pollFd, pollFd.length, 1000);
			assertEquals(1, poll_result);
			assertEquals(poll_H.POLLNVAL, pollFd[0].revents);
		} finally {
		}
	}

	@Test
	void testRead() throws Exception {
		File tmpFile = File.createTempFile("Poll_H", ".txt");
		tmpFile.delete();
		Poll_H poll_H = JnrHeader.getInstance(Poll_H.class);
		Fcntl_H fcntl_H = JnrHeader.getInstance(Fcntl_H.class);
		Unistd_H unistd_H = JnrHeader.getInstance(Unistd_H.class);
		final int fd = fcntl_H.open(tmpFile.getAbsolutePath(), fcntl_H.O_CREAT | fcntl_H.O_RDWR | fcntl_H.O_NONBLOCK);
		try {
			PollFd[] pollFd = poll_H.createPollFd(1);
			pollFd[0].fd = fd;
			pollFd[0].events = poll_H.POLLOUT;
			int poll_result = poll_H.poll(pollFd, pollFd.length, 1000);
			assertEquals(1, poll_result);
			assertEquals(poll_H.POLLOUT, pollFd[0].revents);

		} finally {
			unistd_H.close(fd);
		}
	}

}
