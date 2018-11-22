package de.ibapl.jnrheader.spi.linux.sys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.ibapl.jnrheader.NativeDataType;
import de.ibapl.jnrheader.api.linux.sys.Eventfd_H;
import jnr.ffi.LibraryLoader;
import jnr.ffi.TypeAlias;
import jnr.ffi.annotations.TypeDefinition;
import jnr.ffi.types.int32_t;
import jnr.ffi.types.u_int32_t;

public abstract class Eventfd_Lib extends Eventfd_H {

	public static final int EFD_CLOEXEC = 02000000;
	public static final int EFD_NONBLOCK = 00004000;
	public static final int EFD_SEMAPHORE = 00000001;
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = { ElementType.PARAMETER, ElementType.METHOD })
	@TypeDefinition(alias = TypeAlias.u_int64_t)
	@NativeDataType("uint64_t")
	public @interface eventfd_t {
		
	}

	
	@de.ibapl.jnrheader.NativeFunctions
	protected interface NativeFunctions {
		@int32_t int eventfd(@u_int32_t int count, @int32_t int flags);

		@int32_t int eventfd_read(@int32_t int fd, @eventfd_t long value);

		@int32_t int eventfd_write(@int32_t int fd, @eventfd_t long value);
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
