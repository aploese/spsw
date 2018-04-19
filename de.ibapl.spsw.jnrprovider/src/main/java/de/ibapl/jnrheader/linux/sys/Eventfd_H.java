package de.ibapl.jnrheader.linux.sys;

import de.ibapl.jnrheader.Define;
import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.Wrapper;
import jnr.ffi.Platform;

@Wrapper("sys/eventfd.h")
public abstract class Eventfd_H implements JnrHeader {

	@Define({Platform.OS.LINUX})
	public final int EFD_CLOEXEC = EFD_CLOEXEC();
	@Define({Platform.OS.LINUX})
	public final int EFD_NONBLOCK = EFD_NONBLOCK();
	@Define({Platform.OS.LINUX})
	public final int EFD_SEMAPHORE = EFD_SEMAPHORE();

	@Define({Platform.OS.LINUX})
	protected abstract int EFD_CLOEXEC();
	@Define({Platform.OS.LINUX})
	protected abstract int EFD_NONBLOCK();
	@Define({Platform.OS.LINUX})
	protected abstract int EFD_SEMAPHORE();

	public abstract int eventfd(int count, int flags);

	public abstract int eventfd_read(int fd, long value);

	public abstract int eventfd_write(int fd, long value);

}
