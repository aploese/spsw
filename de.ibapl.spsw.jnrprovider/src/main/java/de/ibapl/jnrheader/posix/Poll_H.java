package de.ibapl.jnrheader.posix;

import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.NativeStruct;
import de.ibapl.jnrheader.POSIX;
import de.ibapl.jnrheader.Wrapper;

@Wrapper("poll.h")
public abstract class Poll_H implements JnrHeader {

	@NativeStruct()
	public class PollFd {
		public short events;

		public int fd;

		public short revents;

	}

	@POSIX
	public final short POLLIN = POLLIN();

	@POSIX
	public final short POLLPRI = POLLPRI();

	@POSIX
	public final short POLLOUT = POLLOUT();

	@POSIX
	public final short POLLRDNORM = POLLRDNORM();

	@POSIX
	public final short POLLRDBAND = POLLRDBAND();

	@POSIX
	public final short POLLWRNORM = POLLWRNORM();

	@POSIX
	public final short POLLWRBAND = POLLWRBAND();

	@POSIX
	public final short POLLERR = POLLERR();

	@POSIX
	public final short POLLHUP = POLLHUP();

	@POSIX
	public final short POLLNVAL = POLLNVAL();

	public abstract int poll(PollFd[] fds, long nfds, int timeout);
	
	public abstract PollFd createPollFd();

	protected abstract short POLLERR();

	protected abstract short POLLHUP();

	protected abstract short POLLIN();

	protected abstract short POLLNVAL();

	protected abstract short POLLOUT();

	protected abstract short POLLPRI();

	protected abstract short POLLRDBAND();

	protected abstract short POLLRDNORM();

	protected abstract short POLLWRBAND();

	protected abstract short POLLWRNORM();

}