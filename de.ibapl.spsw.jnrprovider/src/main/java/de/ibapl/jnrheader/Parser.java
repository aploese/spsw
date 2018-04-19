package de.ibapl.jnrheader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Parser {

	public void parse() {
/*		try (FileReader fr = new FileReader(new File(definesFile))) {
			try (BufferedReader br = new BufferedReader(fr)) {
				while (br.ready()) {
					String line = br.readLine();
					if (!line.startsWith(DEFINE_LINE_START_STRING)) {
						throw new IllegalArgumentException("Cant read line >>>" + line + "<<<");
					}
					int valueStart = line.indexOf(" ", DEFINE_LINE_START_POS);
					String defineName = line.substring(DEFINE_LINE_START_POS, valueStart);

					String defineValue = line.substring(valueStart + 1);
					allDefines.put(defineName, defineValue);
				}
				allDefines.forEach((name, value) -> {
					String aliasValue = allDefines.get(value);
					if (aliasValue != null) {
						allDefines.replace(name, value, aliasValue);
						allDefineAliases.put(name, value);
					}
				});
			}
		}
		*/

	}
}
