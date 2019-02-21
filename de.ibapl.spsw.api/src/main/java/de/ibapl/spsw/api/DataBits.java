/*
 * SPSW - Drivers for the serial port, https://github.com/aploese/spsw/
 * Copyright (C) 2009-2019, Arne Plöse and individual contributors as indicated
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
package de.ibapl.spsw.api;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The number of data bits in each character encoded as enum constants.
 * <li>Fife bits in character are for the baudot code.</li>
 * <li>Six bits are uncommon, but some devices supports it.</li>
 * <li>Seven bits are for 7bit ASCII.</li>
 * <li>Eight bits are for bytes.</li>
 * 
 * @author Arne Plöse
 */
@ProviderType
public enum DataBits {

	/**
	 * Fife data bits. Valid stop bits are 1 and 1.5 bit.
	 */
	DB_5(5),
	/**
	 * Six data bits. Valid stop bits are 1 and 2 bit.
	 */
	DB_6(6),
	/**
	 * Seven data bits. Valid stop bits are 1 and 2 bit.
	 */
	DB_7(7),
	/**
	 * Eight data bits. Valid stop bits are 1 and 2 bit.
	 */
	DB_8(8);

	/**
	 * Returns the enum constants of DataBits of the given bits in each character.
	 * 
	 * @param nativeValue
	 *            the number of bits to convert.
	 * @return the converted number of bits.
	 * @throws IllegalArgumentException
	 *             if the number is out of range.
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
	 * The number of data bits as int value.
	 */
	public final int value;

	private DataBits(int dataBits) {
		this.value = dataBits;
	}

}
