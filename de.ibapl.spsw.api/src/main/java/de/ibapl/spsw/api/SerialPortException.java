package de.ibapl.spsw.api;

/*
 * #%L
 * SPSW Java
 * %%
 * Copyright (C) 2009 - 2014 atmodem4j
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
import java.io.IOException;
import java.lang.annotation.Native;
import org.osgi.annotation.versioning.ProviderType;

/**
 *
 * @author scream3r
 */
@ProviderType
public class SerialPortException extends IOException {

    @Native
    public static final String SERIAL_PORT_SOCKET_CLOSED = "SerialPortSocket closed";
    private static final long serialVersionUID = -8203166218484485637L;

    public SerialPortException(String message) {
        super(message);
    }

}
