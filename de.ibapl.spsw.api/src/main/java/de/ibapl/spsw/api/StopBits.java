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
 * The number of stopbits to use. Valid combinations are one stopbit for any
 * number of databits. One and a halve stopbit for 5 databits. And two stop bits
 * for 6,7 and 8 databits.
 * 
 * @author Arne Plöse
 */
@ProviderType
public enum StopBits {

	/**
	 * One stop bit. Valid for 5,6,7,8 data bits.
	 */
	SB_1(1),
	/**
	 * One and a half stop bit. Only valid for 5 data bits.
	 */
	SB_1_5(1.5f),
	/**
	 * Two stop bits. Only valid for 6,7,8 data bits.
	 */
	SB_2(2);

	/**
	 * The number of bits.
	 */
	public final float value;

	private StopBits(float value) {
		this.value = value;
	}
    /**
     *
     * @param stopbits
     */
    public static StopBits fromNative(float stopbits) {
        if (stopbits == 1.0) {
            return SB_1;
        } else if (stopbits == 1.5) {
            return SB_1_5;
        } else if (stopbits == 2.0) {
            return SB_2;
        } else throw  new IllegalArgumentException("Unrecocnized number of stopbits: " + stopbits);
    }

}
