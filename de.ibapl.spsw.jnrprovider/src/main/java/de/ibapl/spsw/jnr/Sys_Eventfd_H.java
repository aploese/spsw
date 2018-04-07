package de.ibapl.spsw.jnr;

import jnr.ffi.LibraryLoader;
import jnr.ffi.annotations.Out;

@Wrapper("sys/eventfd.h")
public class Sys_Eventfd_H {

	public interface NativeFunctions {
		int eventfd(int count, int flags);

		int eventfd_read(int fd, @Out long value);

		int eventfd_write(int fd, @Out long value);
	}

	@Defines("sys/eventfd.h")
	public interface Sys_EventfdDefines extends JnrDefines {
		@Mandatory()
		short EFD_CLOEXEC();

		@Mandatory()
		short EFD_NONBLOCK();

		@Mandatory()
		short EFD_SEMAPHORE();
	}

	public final short EFD_SEMAPHORE;
	public final short EFD_CLOEXEC;
	public final short EFD_NONBLOCK;

	final private NativeFunctions nativeFunctions;

	public Sys_Eventfd_H() {
		nativeFunctions = LibraryLoader.create(NativeFunctions.class).load("c");
		final Sys_EventfdDefines sys_EventfdDefines = JnrDefines.getInstance(Sys_EventfdDefines.class);
		EFD_SEMAPHORE = sys_EventfdDefines.EFD_SEMAPHORE();
		EFD_CLOEXEC = sys_EventfdDefines.EFD_CLOEXEC();
		EFD_NONBLOCK = sys_EventfdDefines.EFD_NONBLOCK();
	}

	public int eventfd(int count, int flags) {
		return nativeFunctions.eventfd(count, flags);
	}

	public int eventfd_read(int fd, long value) {
		return nativeFunctions.eventfd_read(fd, value);
	}

	public int eventfd_write(int fd, long value) {
		return nativeFunctions.eventfd_write(fd, value);
	}

}
