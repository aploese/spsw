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

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;

public interface JnrHeader {

    static <T extends JnrHeader> T getInstance(Class<T> clazz) {
        MultiarchTupelBuilder mtb = new MultiarchTupelBuilder();
        String[] s = mtb.getMultiarchTupels().iterator().next().split("-");
        Class<T> implClass;
        if (!clazz.getSimpleName().endsWith("_H")) {
            throw new IllegalArgumentException("Wrong name, expect to end with _H");
        }
        
        String packageName = clazz.getPackage().getName().replaceFirst("\\.api\\.", ".spi.");
        String classSimpleName = clazz.getSimpleName().substring(0, clazz.getSimpleName().length() - 1);
        
        try {
            implClass = (Class<T>) clazz.getClassLoader().loadClass(String.format("%s.%s.%s.%s.%sImpl", packageName, s[1], s[0], s[2], classSimpleName));
            return (T) implClass.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            //no-op we will try next
        }
        try {
            implClass = (Class<T>) clazz.getClassLoader().loadClass(String.format("%s.%s.%s.%sImpl", packageName, s[1], s[0], classSimpleName));
            return (T) implClass.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e_1) {
            //no-op we will try next
        }
        try {
            implClass = (Class<T>) clazz.getClassLoader().loadClass(String.format("%s.%s.%sImpl", packageName, s[1], classSimpleName));
            return (T) implClass.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e_1) {
            throw new RuntimeException(e_1);
        }
    }

    static boolean isDefined(Byte b) {
        return b != null;
    }

    static boolean isDefined(Short s) {
        return s != null;
    }

    static boolean isDefined(Integer i) {
        return i != null;
    }

    static boolean isDefined(Long l) {
        return l != null;
    }

    static boolean isDefined(Float f) {
        return f != null;
    }

    static boolean isDefined(Double d) {
        return d != null;
    }

    public static final String UTF8_ENCODING = "UTF-8";

    /**
     * Windows wide char encoding
     */
    public static final String UTF16_LE_ENCODING = "UTF-16LE";

    public static final Charset CS_UTF_16LE = Charset.forName(UTF16_LE_ENCODING);

    default int calcBufferWriteBytes(ByteBuffer buffer) {
        if (buffer.isReadOnly()) {
            throw new IllegalArgumentException("Read-only buffer");
        }
        final int remaining = buffer.remaining();
        if (remaining < 0) {
            throw new IllegalArgumentException("buffer.remaining must not < 0");
        }
        return remaining;
    }

    default int calcBufferReadBytes(ByteBuffer buffer) {
        final int remaining = buffer.remaining();
        if (remaining < 0) {
            throw new IllegalArgumentException("buffer.remaining must not < 0");
        }
        return remaining;
    }

    default long fixBufferPos(final ByteBuffer buffer, final long byteTransferred) {
        if (byteTransferred > 0) {
            buffer.position((int) (buffer.position() + byteTransferred));
        }
        return byteTransferred;
    }

}
