package de.ibapl.jnrheader.posix;

import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.POSIX;
import de.ibapl.jnrheader.Wrapper;

@Wrapper("poll.h")
public abstract class Poll_H implements JnrHeader {

	public class PollFd {
		public short events;

		public int fd;

		public short revents;
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(getClass().getSimpleName()).append(" { \n");
			sb.append("fd = ").append(fd);
			sb.append("\n\tevents = \"");
			event2String(sb, events);
			sb.append("\"\n\trevents = \"");
			event2String(sb, revents);
			sb.append("\"\n");
			sb.append("}\n");
			return sb.toString();
		}

		private void event2String(StringBuilder sb, short event) {
			if ((POLLIN & event) == POLLIN) {
				sb.append("POLLIN ");
				event &= ~POLLIN;
			}
			if ((POLLPRI & event) == POLLPRI) {
				sb.append("POLLPRI ");
				event &= ~POLLPRI;
			}
			if ((POLLOUT & event) == POLLOUT) {
				sb.append("POLLOUT ");
				event &= ~POLLOUT;
			}
			if ((POLLRDNORM & event) == POLLRDNORM) {
				sb.append("POLLRDNORM ");
				event &= ~POLLRDNORM;
			}
			if ((POLLRDBAND & event) == POLLRDBAND) {
				sb.append("POLLRDBAND ");
				event &= ~POLLRDBAND;
			}
			if ((POLLWRNORM & event) == POLLWRNORM) {
				sb.append("POLLWRNORM ");
				event &= ~POLLWRNORM;
			}
			if ((POLLWRBAND & event) == POLLWRBAND) {
				sb.append("POLLWRBAND ");
				event &= ~POLLWRBAND;
			}
			if ((POLLERR & event) == POLLERR) {
				sb.append("POLLERR ");
				event &= ~POLLERR;
			}
			if ((POLLHUP & event) == POLLHUP) {
				sb.append("POLLHUP ");
				event &= ~POLLHUP;
			}
			if ((POLLIN & event) == POLLIN) {
				sb.append("POLLIN ");
				event &= ~POLLIN;
			}
			if ((POLLNVAL & event) == POLLNVAL) {
				sb.append("POLLNVAL ");
				event &= ~POLLNVAL;
			}
			if (event != 0) {
				sb.append(String.format("0x%04x", event));
			}
		}
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
	
	public abstract PollFd[] createPollFd(int size);

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