package de.ibapl.spsw.api;

import org.osgi.annotation.versioning.ProviderType;

/*
 * #%L
 * SPSW Java
 * %%
 * Copyright (C) 2009 - 2014 atmodem4j
 * %%
 * atmodem4j - A serial port socket wrapper- http://atmodem4j.sourceforge.net/
 * Copyright (C) 2009-2014, atmodem4j.sf.net, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
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
 * #L%
 */
/**
 *
 * @author aploese
 */
@ProviderType
public enum DataBits {

    DB_5(5),
    DB_6(6),
    DB_7(7),
    DB_8(8);

    public static DataBits fromNative(int nativeValue) {
        switch (nativeValue) {
            case 5:
                return DB_5;
            case 6:
                return DB_6;
            case 7:
                return DB_7;
            case 8:
                return DB_8;
            default:
                throw new RuntimeException("Cant handle native value: " + nativeValue);

        }
    }
    public final int value;

    private DataBits(int dataBits) {
        this.value = dataBits;
    }

}
