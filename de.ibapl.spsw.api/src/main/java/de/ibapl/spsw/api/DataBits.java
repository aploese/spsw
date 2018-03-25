/*-
 * #%L
 * SPSW API
 * %%
 * Copyright (C) 2009 - 2017 Arne Plöse
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
package de.ibapl.spsw.api;


import org.osgi.annotation.versioning.ProviderType;

/**
 * The number of data bits in each character encoded as enum constants.
 * Fife bits in character are for the baudot code.
 * Six bits are uncommon.
 * Seven bits are for 7bit ASCII.
 * Eight bits are for bytes.
 * 
 * @author aploese
 */
@ProviderType
public enum DataBits {

	/**
	 * Fife data bits.
	 * Valid stop bits are 1 and 1.5 bit.
	 */
    DB_5(5),
	/**
	 * Six data bits.
	 * Valid stop bits are 1 and 2 bit.
	 */
    DB_6(6),
	/**
	 * Seven data bits.
	 * Valid stop bits are 1 and 2 bit.
	 */
    DB_7(7),
	/**
	 * Eight data bits.
	 * Valid stop bits are 1 and 2 bit.
	 */
    DB_8(8);

	/**
	 * Returns the enum constants of DataBits of the given bits in each character.
	 * @param nativeValue the number of bits to convert.
	 * @return the converted number of bits.
	 * @throws IllegalArgumentException if the number is out of range.
	 */
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
                throw new IllegalArgumentException("No enum constant for: " + nativeValue + " data bits");

        }
    }
    
    /**
     * The number of data bits.
     */
    public final int value;

    private DataBits(int dataBits) {
        this.value = dataBits;
    }

}
