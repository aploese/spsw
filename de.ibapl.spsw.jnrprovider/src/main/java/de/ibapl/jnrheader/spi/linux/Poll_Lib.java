package de.ibapl.jnrheader.spi.linux;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


import de.ibapl.jnrheader.NativeDataType;
import jnr.ffi.types.int32_t;

import de.ibapl.jnrheader.NativeStruct;
import de.ibapl.jnrheader.api.posix.Poll_H;
import jnr.ffi.LibraryLoader;
import jnr.ffi.Runtime;
import jnr.ffi.Struct;
import jnr.ffi.TypeAlias;
import jnr.ffi.annotations.TypeDefinition;

public abstract class Poll_Lib extends Poll_H {
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = { ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD })
	@TypeDefinition(alias = TypeAlias.int64_t)
	@NativeDataType("unsigned long int")
	public @interface nfds_t {
		
	}

	@de.ibapl.jnrheader.NativeFunctions
	protected  interface NativeFunctions {
		@int32_t int poll(PollFdImpl[] fds, @nfds_t long nfds, @int32_t int timeout);
	}

	@NativeStruct("pollfd")
	protected static class PollFdImpl extends Struct {
		@NativeDataType("int")
		private final int32_t fd = new int32_t();
		@NativeDataType("short int")
		private final int16_t events = new int16_t();
		@NativeDataType("short int")
		private final int16_t revents = new int16_t();

		public PollFdImpl(Runtime runtime) {
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

	private PollFdImpl[] wrap(PollFd[] pollFd) {
		PollFdImpl[] result = Struct.arrayOf(Runtime.getRuntime(nativeFunctions), PollFdImpl.class, pollFd.length);
		for (int i = 0 ; i < pollFd.length; i++) {
			result[i].events.set(pollFd[i].events);
			result[i].revents.set(pollFd[i].revents);
			result[i].fd.set(pollFd[i].fd);
		}
		return result;
	}

	@Override
	public int poll(PollFd[] fds, long nfds, int timeout) {
		PollFdImpl[] fdsImpl = wrap(fds);
		final int result = nativeFunctions.poll(fdsImpl, fds.length, timeout);
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
	public PollFd[] createPollFd(int size) {
		PollFd[] result = new PollFd[size];
		for (int i = 0; i < size; i++) {
			result[i] =  new PollFd();
		}
		return result;
	}
}