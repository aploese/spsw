/*-
 * #%L
 * SPSW API
 * %%
 * Copyright (C) 2009 - 2018 Arne Plöse
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */
package de.ibapl.jnrheader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import de.ibapl.jnrheader.posix.Termios_H;

public class GenerateTemplate {

	private final String BASE_PACKAGE = "de.ibapl.spsw.jnr";
	private final String DEFINE_LINE_START_STRING = "#define ";
	private final int DEFINE_LINE_START_POS = DEFINE_LINE_START_STRING.length();

	private File destDir;
	private Map<String, String> allDefines = new HashMap<>();
	private Map<String, String> allDefineAliases = new HashMap<>();
	private Map<String, String> usedDefineAliases = new HashMap<>();

	public GenerateTemplate(String destDir, String definesFile) throws IOException {
		this.destDir = new File(destDir);
	}

	public static void main(String[] args) throws Exception {
		GenerateTemplate gt = new GenerateTemplate(args[0], args[1]);
		gt.generate(Termios_H.class, "x86_64", "linux", "gnu");
	}

	enum Literal {
		OCTAL, HEXADECIMAL, INTEGER, UNSIGNED_INTEGER, LONG, UNSIGNED_LONG, FLOAT, STRING, BOOLEAN, NOT_DEFINED;

		static Literal of(String value) {
			if (value == null) {
				return Literal.NOT_DEFINED;
			}
			if ("true".equals(value) || "false".equals(value)) {
				return BOOLEAN;
			}
			if (value.startsWith("\"")) {
				return STRING;
			}
			if (value.startsWith("0x")) {
				return HEXADECIMAL;
			}
			if (value.startsWith("0")) {
				if (value.length() == 1) {
					return INTEGER;
				} 
				return OCTAL;
			}
			if (value.endsWith("ul")) {
				return UNSIGNED_LONG;
			}
			if (value.endsWith("l")) {
				return LONG;
			}
			if (value.endsWith("u")) {
				return UNSIGNED_INTEGER;
			}
			if (value.contains("e")) {
				return FLOAT;
			}
			return INTEGER;
		}
	}

	private void writeMethod(Writer w, Class<?> returnType, String name, String value) throws IOException {
		if (allDefineAliases.containsKey(name)) {
			usedDefineAliases.put(name, allDefineAliases.get(name));
			w.append("\t //alias for: ").append(allDefineAliases.get(name)).append("\n");
		}
		long longValue;
		switch (Literal.of(value)) {
		case OCTAL:
			longValue = Long.parseUnsignedLong(value, 8);
			break;
		case HEXADECIMAL:
			longValue = Long.parseUnsignedLong(value, 16);
			break;
		case INTEGER:
			longValue = Integer.parseInt(value);
			break;
		case UNSIGNED_INTEGER:
			longValue = Integer.parseUnsignedInt(value, 10);
			break;
		case LONG:
			longValue = Long.parseLong(value);
			break;
		case UNSIGNED_LONG:
			longValue = Long.parseUnsignedLong(value, 10);
			break;
		case BOOLEAN:
			longValue = 0;
			break;
		case NOT_DEFINED:
			longValue = 0;
			break;
		default:
			throw new IllegalArgumentException();
		}

		w.append("\t// Definition is: " + Literal.of(value)).append("\n");
		w.append("\t@Override\n");
		w.append("\tpublic ").append(returnType.getSimpleName()).append(" ").append(name).append("() { return ");
		if (returnType == byte.class || returnType == Byte.class) {
			if (longValue != (longValue & 0xFF)) {
				throw new IllegalArgumentException("Values mismatch " + name + " is bigger than byte");
			}
			if (longValue > Byte.MAX_VALUE) {
				w.append("(byte) ");
			}
		} else if (returnType == short.class || returnType == Short.class) {
			if (longValue != (longValue & 0xFFFF)) {
				throw new IllegalArgumentException("Values mismatch " + name + " is bigger than short");
			}
			if (longValue > Short.MAX_VALUE) {
				w.append("(short) ");
			}
		}
		if (returnType == int.class || returnType == Integer.class) {
			if (longValue != (longValue & 0xFFFFFFFF)) {
				throw new IllegalArgumentException("Values mismatch " + name + " is bigger than int");
			}
			if (longValue > Integer.MAX_VALUE) {
				w.append("(int) ");
			}
		}

		w.append(value).append("; }\n");
	}

	private void writeUsedAliases(Writer w) throws IOException {

		w.append("\n\t@Override\n");
		w.append("\tpublic void getAliases(BiConsumer<String, String> biConsumer) {\n");
		usedDefineAliases.forEach((name, alias) -> {
			try {
				w.append("\t\tbiConsumer.accept(\"").append(name).append("\", \"").append(alias).append("\");\n");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		w.append("\t}\n");

	}

	private void generate(Class<?> clazz, String arch, String os, String abi) throws IOException {
		File destArch = new File(destDir, arch);
		File destOS = new File(destArch, os);
		File destAbi = new File(destOS, abi);
		destAbi.mkdirs();
		for (Class<?> c : clazz.getClasses()) {
			if (c.getAnnotation(DefinedByOS.class) != null) {
				final String className = c.getSimpleName() + "Impl";
				File dest = new File(destAbi, className + ".java");
				try (FileWriter fw = new FileWriter(dest)) {
					fw.append("package ").append(BASE_PACKAGE).append(".platform.").append(arch).append(".").append(os)
							.append(".").append(abi).append(";\n");
					fw.append("\n");
					fw.append("import java.util.function.BiConsumer;");
					fw.append("import ").append(BASE_PACKAGE).append(".").append(clazz.getSimpleName()).append(".")
							.append(c.getSimpleName()).append(";\n");
					fw.append("\n");

					fw.append("public class ").append(className).append(" implements ").append(c.getSimpleName())
							.append(" {\n");
					
					for (Method m : c.getMethods()) {
						if (m.getAnnotation(DefinedByOS.class) != null) {
							String name = m.getName();
							String value = allDefines.get(name);
							if (value == null) {
								throw new IllegalArgumentException("Mandatory value is null: " + value);
							}
							writeMethod(fw, m.getReturnType(), name, value);
						}
						if (m.getAnnotation(DefinedByOS.class) != null) {
							String name = m.getName();
							String value = allDefines.get(name);
							writeMethod(fw, m.getReturnType(), name, value);
						}
						if (m.getAnnotation(IsDefined.class) != null) {
							String name = m.getName();
							String value = allDefines.get(name);
							if (value != null) {
								fw.append("\t //value in define: ").append(value).append("\n");
							}
							writeMethod(fw, boolean.class, name, value == null ? "false" : "true");
						}
					}

					writeUsedAliases(fw);
					fw.append("}\n");
					fw.flush();
				}
			}
		}
	}

}
