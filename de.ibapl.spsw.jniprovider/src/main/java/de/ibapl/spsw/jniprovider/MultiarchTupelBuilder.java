/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ibapl.spsw.jniprovider;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Build the  Multiarch Tuples <a href="https://wiki.debian.org/Multiarch/Tuples">wiki.debian.org/Multiarch/Tuples</a>.
 * Multiarch Tuples are needed to figure out which native lib to load arm soft float or hard float for example.
 * 
 * @author Arne Plöse
 */
public final class MultiarchTupelBuilder {

	// known system properties
	public final String sun_os_patch_level;
	public final String os_arch;
	public final String os_name;
	public final String os_version;
	public final String sun_arch_abi;
	public final String sun_arch_data_model;
	public final String sun_cpu_endian;
	public final String sun_cpu_isalist;

	public MultiarchTupelBuilder() {
		super();
		sun_os_patch_level = System.getProperty("sun.os.patch.level");
		os_arch = System.getProperty("os.arch");
		os_name = System.getProperty("os.name");
		os_version = System.getProperty("os.version");
		sun_arch_abi = System.getProperty("sun.arch.abi");
		sun_arch_data_model = System.getProperty("sun.arch.data.model");
		sun_cpu_endian = System.getProperty("sun.cpu.endian");
		sun_cpu_isalist = System.getProperty("sun.cpu.isalist");
	}

	/**
	 * Do not use this, its just for testing to fill in the right values...
	 *
	 * @param sun_os_patch_level
	 * @param os_arch
	 * @param os_name
	 * @param os_version
	 * @param sun_arch_abi
	 * @param sun_cpu_endian
	 * @param sun_cpu_isalist
	 */
	MultiarchTupelBuilder(String sun_os_patch_level, String os_arch, String os_name, String os_version,
			String sun_arch_abi, String sun_arch_data_model, String sun_cpu_endian, String sun_cpu_isalist) {
		super();
		this.sun_os_patch_level = sun_os_patch_level;
		this.os_arch = os_arch;
		this.os_name = os_name;
		this.os_version = os_version;
		this.sun_arch_abi = sun_arch_abi;
		this.sun_arch_data_model = sun_arch_data_model;
		this.sun_cpu_endian = sun_cpu_endian;
		this.sun_cpu_isalist = sun_cpu_isalist;
	}

	public Collection<String> getMultiarchTupels() {
		LinkedList<String> result = new LinkedList<>();
		switch (getSimpleOsName()) {
		case "linux":
			switch (os_arch) {
			case "amd64":
				result.add("x86_64-linux-gnu");
				return result;
			case "i386":
				result.add("i386-linux-gnu");
				return result;
			case "arm":
				switch (sun_arch_data_model) {
				case "32":
					if (sun_arch_abi == null || sun_arch_abi.isEmpty()) {
						// info of hard or soft float is missing
						result.add("arm-linux-gnueabihf");
						result.add("arm-linux-gnueabi");
					} else {
						result.add("arm-linux-" + sun_arch_abi);
					}
					return result;
				case "64":
					result.add("aarch64-linux-" + sun_arch_abi);
					return result;
				default:
					throw new UnsupportedOperationException(
							"Can't handle sun.arch.data.model of arm linux\n" + listSystemProperties());
				}
			case "mips":
				switch (sun_arch_data_model) {
				case "32":
					switch (sun_cpu_endian) {
					case "little":
						result.add("mipsel-linux-" + sun_arch_abi);
						return result;
					case "big":
						result.add("mips-linux-" + sun_arch_abi);
						return result;
					default:
						throw new UnsupportedOperationException(
								"Can't handle sun_cpu_endian of mips 32 linux\n" + listSystemProperties());
					}
				case "64":
					switch (sun_cpu_endian) {
					case "little":
						result.add("mips64el-linux-" + sun_arch_abi);
						return result;
					case "big":
						result.add("mips64-linux-" + sun_arch_abi);
						return result;
					default:
						throw new UnsupportedOperationException(
								"Can't handle sun_cpu_endian of mips 32 linux\n" + listSystemProperties());
					}
				default:
					throw new UnsupportedOperationException(
							"Can't handle sun.arch.data.model of mips linux\n" + listSystemProperties());
				}

			default:
				throw new UnsupportedOperationException("Can't handle os.arch of linux\n" + listSystemProperties());
			}
		case "windows":
			switch (os_arch) {
			case "amd64":
				result.add("x86_64-windows-pe32+");
				return result;
			case "x86":
				result.add("x86-windows-pe32");
				return result;
			default:
				throw new UnsupportedOperationException("Cant handle Windows architecture: " + os_arch);
			}
		default:
			throw new UnsupportedOperationException("Cant handle " + os_name + " architecture: " + os_arch);
		}

	}

	public String getSimpleOsName() {
		if (os_name.startsWith("Windows")) {
			return "windows";
		} else if (os_name.equals("Linux")) {
			return "linux";
		} else {
			return os_name;
		}
	}

	public String listSystemProperties() {
		StringBuilder sb = new StringBuilder();
		for (String name : System.getProperties().stringPropertyNames()) {
			sb.append("\t").append(name).append(" = ").append(System.getProperty(name)).append("\n");
		}
		return sb.toString();
	}

}
