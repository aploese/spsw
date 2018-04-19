package de.ibapl.jnrheader.posix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.Wrapper;

public abstract class JNRHeaderBase {

	protected <T extends JnrHeader> void testDefines(Class<T> clazz) throws Exception {
		Map<String, Object> valueMap = new HashMap<>();
		Set<String> nullSet = new HashSet<>();

		JnrHeader instance = JnrHeader.getInstance(clazz);

		File out_file = File.createTempFile(clazz.getSimpleName(), "_Defines");
		out_file.deleteOnExit();	
		File c_source = new File(out_file.getAbsolutePath() + ".c");
		c_source.deleteOnExit();
		try (Writer w = new FileWriter(c_source)) {
			w.write("#include <stdio.h>\n");
			w.write("#include <");
			Wrapper wrapper = clazz.getAnnotation(Wrapper.class);
			w.write(wrapper.value());
			w.write(">\n");
			w.write("\n");
			w.write("int main() {\n");
			for (Field f : clazz.getFields()) {
				w.append("\tprintf(\"#expect ").append(f.getName()).append("=").append(String.valueOf(f.get(instance)))
						.append("\\n\");\n");
				w.append("\t#ifdef ").append(f.getName()).append("\n");
				if (boolean.class.equals(f.getType())) {
					w.append("\tprintf(\"").append(f.getName()).append("=%d\\n\",").append(f.getName()).append(");\n");
					valueMap.put(f.getName(), f.getBoolean(instance) ? "1" : "0");
				} else if (Boolean.class.equals(f.getType())) {
					if (f.get(instance) == null) {
						nullSet.add(f.getName());
					} else {
						w.append("\tprintf(\"").append(f.getName()).append("=%d\\n\",").append(f.getName())
								.append(");\n");
						valueMap.put(f.getName(), f.getBoolean(instance) ? "1" : "0");
					}
				} else if (byte.class.equals(f.getType())) {
					w.append("\tprintf(\"").append(f.getName()).append("=%d\\n\",").append(f.getName()).append(");\n");
					valueMap.put(f.getName(), f.getByte(instance));
				} else if (Byte.class.equals(f.getType())) {
					if (f.get(instance) == null) {
						nullSet.add(f.getName());
					} else {
						w.append("\tprintf(\"").append(f.getName()).append("=%d\\n\",").append(f.getName())
								.append(");\n");
						valueMap.put(f.getName(), f.get(instance));
					}
				} else if (short.class.equals(f.getType())) {
					w.append("\tprintf(\"").append(f.getName()).append("=%d\\n\",").append(f.getName()).append(");\n");
					valueMap.put(f.getName(), f.getShort(instance));
				} else if (Short.class.equals(f.getType())) {
					if (f.get(instance) == null) {
						nullSet.add(f.getName());
					} else {
						w.append("\tprintf(\"").append(f.getName()).append("=%d\\n\",").append(f.getName())
								.append(");\n");
						valueMap.put(f.getName(), f.get(instance));
					}
				} else if (int.class.equals(f.getType())) {
					w.append("\tprintf(\"").append(f.getName()).append("=%d\\n\",").append(f.getName()).append(");\n");
					valueMap.put(f.getName(), f.getInt(instance));
				} else if (Integer.class.equals(f.getType())) {
					if (f.get(instance) == null) {
						nullSet.add(f.getName());
					} else {
						w.append("\tprintf(\"").append(f.getName()).append("=%d\\n\",").append(f.getName())
								.append(");\n");
						valueMap.put(f.getName(), f.get(instance));
					}
				} else if (long.class.equals(f.getType())) {
					w.append("\tprintf(\"").append(f.getName()).append("=%ld\\n\",").append(f.getName()).append(");\n");
					valueMap.put(f.getName(), f.getLong(instance));
				} else if (Long.class.equals(f.getType())) {
					if (f.get(instance) == null) {
						nullSet.add(f.getName());
					} else {
						w.append("\tprintf(\"").append(f.getName()).append("=%ld\\n\",").append(f.getName())
								.append(");\n");
						valueMap.put(f.getName(), f.get(instance));
					}
				}
				w.append("\t#else\n");
				w.append("\tprintf(\"#").append(f.getName()).append(" is null \\n\");\n");
				w.append("\t#endif\n");
			}
			w.write("\treturn 0;\n");
			w.write("}\n");
			w.flush();
		}
		//Run gcc
		Process gcc = Runtime.getRuntime().exec(new String[] { "gcc", c_source.getName(), "-o", out_file.getAbsolutePath() },
				null, c_source.getParentFile());
		int res = gcc.waitFor();
		
		if (res == 0) {
			//OK compiled now execute the binary
			Process exec = Runtime.getRuntime().exec(out_file.getAbsolutePath());
			InputStream is = exec.getInputStream();
			Properties props = new Properties();
			//Load the output of the execute it is expected that that the layout is that of a property file
			props.load(is);
			
			//First find the undefined values
			for (String key : nullSet) {
				Object value = props.getProperty(key);
				assertNull(value, "Value for key mismatch " + key);
				props.remove(key);
			}

			//Then find the defined values and check the value
			for (String key : valueMap.keySet()) {
				Object value = props.getProperty(key);
				// We expect the same value by gcc
				assertEquals(value, String.valueOf(valueMap.get(key)), "Value for key mismatch " + key);
				props.remove(key);
			}
			assertTrue(props.isEmpty());
		} else {
			//Compile error
			StringBuffer sb = new StringBuffer();
			try (BufferedReader r = new BufferedReader(new InputStreamReader(gcc.getErrorStream()))) {
				while (r.ready()) {
					sb.append(r.readLine());
				}
				throw new RuntimeException("Could not compile Err:\n" + sb.toString());
			}
		}
	}

}
