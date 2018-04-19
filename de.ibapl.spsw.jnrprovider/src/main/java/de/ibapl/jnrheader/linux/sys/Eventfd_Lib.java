package de.ibapl.jnrheader.linux.sys;

import de.ibapl.jnrheader.NativeDataType;
import jnr.ffi.LibraryLoader;

public abstract class Eventfd_Lib extends Eventfd_H {

	public static final int EFD_CLOEXEC = 02000000;
	public static final int EFD_NONBLOCK = 00004000;
	public static final int EFD_SEMAPHORE = 00000001;
	
	protected interface NativeFunctions {
		@NativeDataType("int")
		int eventfd(@NativeDataType("unsigned int")int count, @NativeDataType("int")int flags);

		@NativeDataType("int")
		int eventfd_read(@NativeDataType("int")int fd, @NativeDataType("enentfd_t")long value);

		@NativeDataType("int")
		int eventfd_write(@NativeDataType("int")int fd, @NativeDataType("enentfd_t")long value);
	}


	@Override
	protected int EFD_CLOEXEC() {
		return Eventfd_Lib.EFD_CLOEXEC;
	}

	@Override
	protected int EFD_NONBLOCK() {
		return Eventfd_Lib.EFD_NONBLOCK;
	}

	@Override
	protected int EFD_SEMAPHORE() {
		return Eventfd_Lib.EFD_SEMAPHORE;
	}

	final private NativeFunctions nativeFunctions;

	public Eventfd_Lib() {
		nativeFunctions = LibraryLoader.create(NativeFunctions.class).load("c");
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
