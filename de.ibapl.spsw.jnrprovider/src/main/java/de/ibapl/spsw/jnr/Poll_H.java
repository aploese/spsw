package de.ibapl.spsw.jnr;

import jnr.ffi.LibraryLoader;
import jnr.ffi.Runtime;
import jnr.ffi.Struct;
import jnr.ffi.annotations.Out;
import jnr.ffi.annotations.Transient;

@Wrapper("poll.h")
public class Poll_H {

	public interface NativeFunctions {
		int poll(@Out @Transient PollFd[] fds, long nfds, int timeout);
	}

	@Defines("poll.h")
	public interface PollDefines extends JnrDefines {
		@Mandatory
		short POLLERR();

		@Mandatory
		short POLLHUP();

		@Mandatory
		short POLLIN();

		@Mandatory
		short POLLNVAL();

		@Mandatory
		short POLLOUT();

		@Mandatory
		short POLLPRI();

		@Mandatory
		short POLLRDBAND();

		@Mandatory
		short POLLRDNORM();

		@Mandatory
		short POLLWRBAND();

		@Mandatory
		short POLLWRNORM();
	}

	public final class PollFd extends Struct {
		u_int64_t fd;
		u_int64_t events;
		u_int64_t revents;

		private PollFd(Runtime runtime) {
			super(runtime);
		}

	}

	final private NativeFunctions nativeFunctions;

	public final short POLLIN;
	public final short POLLPRI;
	public final short POLLOUT;
	public final short POLLRDNORM;
	public final short POLLRDBAND;
	public final short POLLWRNORM;
	public final short POLLWRBAND;
	public final short POLLERR;
	public final short POLLHUP;
	public final short POLLNVAL;

	public Poll_H() {
		nativeFunctions = LibraryLoader.create(NativeFunctions.class).load("c");
		final PollDefines pollDefines = JnrDefines.getInstance(PollDefines.class);
		POLLIN = pollDefines.POLLIN();
		POLLPRI = pollDefines.POLLPRI();
		POLLOUT = pollDefines.POLLOUT();
		POLLRDNORM = pollDefines.POLLRDNORM();
		POLLRDBAND = pollDefines.POLLRDBAND();
		POLLWRNORM = pollDefines.POLLWRNORM();
		POLLWRBAND = pollDefines.POLLWRBAND();
		POLLERR = pollDefines.POLLERR();
		POLLHUP = pollDefines.POLLHUP();
		POLLNVAL = pollDefines.POLLNVAL();
	};

	int poll(PollFd[] fds, long nfds, int timeout) {
		if (fds == null) {
			throw new NullPointerException("fds is null");
		}
		if (nfds > fds.length) {
			throw new IllegalArgumentException("nfds > fds.length");
		}
		return nativeFunctions.poll(fds, nfds, timeout);
	}

}