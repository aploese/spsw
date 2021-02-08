/*
 * SPSW - Drivers for the serial port, https://github.com/aploese/spsw/
 * Copyright (C) 2009-2021, Arne Plöse and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.ibapl.spsw.mock;

import java.util.Arrays;

public class MockRequest<T> {

    public final T payload;
    public final MockRequestType requestType;
    public final MockRequestStackException stackException;

    /*
     * We manipulate the stacktrace for better debugging We now 2 method calls away
     * from the point we want to show first addRequest() second Data() so remove the
     * last two we are at the point where addRequest() was called...
     */
    MockRequest(T payload, MockRequestType requestType) {
        this.payload = payload;
        this.requestType = requestType;
        this.stackException = new MockRequestStackException();
        final StackTraceElement[] st = this.stackException.getStackTrace();
        this.stackException.setStackTrace(Arrays.copyOfRange(st, 3, st.length - 3));
    }

}
