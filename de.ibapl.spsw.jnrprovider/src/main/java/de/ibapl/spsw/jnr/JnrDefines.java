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
package de.ibapl.spsw.jnr;

import java.util.function.BiConsumer;

public interface JnrDefines {
	
	static <T extends JnrDefines> T getInstance(Class<T> clazz) {
		MultiarchTupelBuilder mtb = new MultiarchTupelBuilder();
		String[] s = mtb.getMultiarchTupels().iterator().next().split("-");
		Class<T> implClass;
		try {
			implClass = (Class<T>)clazz.getClassLoader().loadClass(String.format("de.ibapl.spsw.jnr.platform.%s.%s.%s.%sImpl", s[0], s[1], s[2], clazz.getSimpleName()));
			return (T)implClass.newInstance(); 
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	void getAliases(BiConsumer<String, String> biConsumer);

}
