package de.ibapl.jnrheader.linux;

import de.ibapl.jnrheader.NativeDataType;
import de.ibapl.jnrheader.NativeFunction;
import de.ibapl.jnrheader.NativeStruct;
import de.ibapl.jnrheader.posix.Poll_H;
import jnr.ffi.LibraryLoader;
import jnr.ffi.Runtime;
import jnr.ffi.Struct;
import jnr.ffi.annotations.Out;

public abstract class Poll_Lib extends Poll_H {

	@de.ibapl.jnrheader.NativeFunctions
	protected  interface NativeFunctions {
		@NativeFunction("int")
		int poll(@Out PollFdImpl[] fds, @NativeDataType("nfds_t") long nfds, @NativeDataType("int") int timeout);
	}

	@NativeStruct()
	private class PollFdImpl extends Struct {
		@NativeDataType("int")
		private final int32_t fd = new int32_t();
		@NativeDataType("short int")
		private final int16_t events = new int16_t();
		@NativeDataType("short int")
		private final int16_t revents = new int16_t();

		protected PollFdImpl(Runtime runtime) {
			super(runtime);
		}
	}

	public static final short POLLERR = 0x008;
	public static final short POLLHUP = 0x010;
	public static final short POLLIN = 0x001;
	public static final short POLLNVAL = 0x020;
	public static final short POLLOUT = 0x004;
	public static final short POLLPRI = 0x002;
	public static final short POLLRDBAND = 0x080;
	public static final short POLLRDNORM = 0x040;
	public static final short POLLWRBAND = 0x200;
	public static final short POLLWRNORM = 0x100;

	final private NativeFunctions nativeFunctions;

	public Poll_Lib() {
		nativeFunctions = LibraryLoader.create(NativeFunctions.class).load("c");
	}

	private PollFdImpl[] wrap(PollFd[] fds) {
		PollFdImpl[] result = new PollFdImpl[fds.length];
		for (int i = 0 ; i < fds.length; i++) {
			result[i] = wrap(fds[i]);
		}
		return result;
	}

	private PollFdImpl wrap(PollFd pollFd) {
		PollFdImpl result = new PollFdImpl(Runtime.getRuntime(nativeFunctions));
		result.events.set(pollFd.events);
		result.revents.set(pollFd.revents);
		result.fd.set(pollFd.fd);
		return result;
	}

	@Override
	public int poll(PollFd[] fds, long nfds, int timeout) {
		PollFdImpl[] fdsImpl = wrap(fds);
		int result = nativeFunctions.poll(fdsImpl, nfds, timeout);
		unwrap(fds, fdsImpl);
		return result;
	}

	private void unwrap(PollFd[] fds, PollFdImpl[] fdsImpl) {
		for (int i = 0; i < fds.length; i++) {
			unwrap(fds[i], fdsImpl[i]);
		}
	}

	private void unwrap(PollFd pollFd, PollFdImpl pollFdImpl) {
		pollFd.events = pollFdImpl.events.shortValue(); 
		pollFd.revents = pollFdImpl.revents.shortValue(); 
		pollFd.fd = pollFdImpl.fd.intValue(); 
	}

	@Override
	protected short POLLERR() {
		return Poll_Lib.POLLERR;
	}

	@Override
	protected short POLLHUP() {
		return Poll_Lib.POLLHUP;
	}

	@Override
	protected short POLLIN() {
		return Poll_Lib.POLLIN;
	}

	@Override
	protected short POLLNVAL() {
		return Poll_Lib.POLLNVAL;
	}

	@Override
	protected short POLLOUT() {
		return Poll_Lib.POLLOUT;
	}

	@Override
	protected short POLLPRI() {
		return Poll_Lib.POLLPRI;
	}

	@Override
	protected short POLLRDBAND() {
		return Poll_Lib.POLLRDBAND;
	}

	@Override
	protected short POLLRDNORM() {
		return Poll_Lib.POLLRDNORM;
	}

	@Override
	protected short POLLWRBAND() {
		return Poll_Lib.POLLWRBAND;
	}

	@Override
	protected short POLLWRNORM() {
		return Poll_Lib.POLLWRNORM;
	}

	@Override
	public PollFd createPollFd() {
		return new PollFd();
	}
}