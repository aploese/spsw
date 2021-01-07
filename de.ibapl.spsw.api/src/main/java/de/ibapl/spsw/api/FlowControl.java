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
package de.ibapl.spsw.api;

import java.util.EnumSet;
import java.util.Set;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The flow control encoded as enum constants. Not all OS (POSIX) support
 * separate in/out, whereas other do (Windows). So its recommended to set set
 * both directions.
 * 
 * @author Arne Plöse
 */
@ProviderType
public enum FlowControl {

	/**
	 * RTS/CTS handshaking in bound. not all OS (POSIX) support separate in/out,
	 * whereas other do (Windows).
	 */
	RTS_CTS_IN,
	/**
	 * RTS/CTS handshaking out bound.
	 */
	RTS_CTS_OUT,
	/**
	 * XON/XOFF handshaking in bound.
	 */
	XON_XOFF_IN,
	/**
	 * XON/XOFF handshaking out bound.
	 */
	XON_XOFF_OUT;

	/**
	 * Returns an empty set for no flow control.
	 * 
	 * @return an empty set.
	 */
	public static Set<FlowControl> getFC_NONE() {
		return EnumSet.noneOf(FlowControl.class);
	}

	/**
	 * Returns a set for RTS/CTS handshaking for both directions.
	 * 
	 * @return a set with RTS_CTS_IN, RTS_CTS_OUT set.
	 */
	public static Set<FlowControl> getFC_RTS_CTS() {
		return EnumSet.of(RTS_CTS_IN, RTS_CTS_OUT);
	}

	/**
	 * Returns a set for in bound XON/XOFF handshaking.
	 * 
	 * @return a set with XON_XOFF_IN set.
	 */
	public static Set<FlowControl> getFC_XON_XOFF_IN() {
		return EnumSet.of(XON_XOFF_IN);
	}

	/**
	 * Returns a set for out bound XON/XOFF handshaking.
	 * 
	 * @return a set with XON_XOFF_OUT set.
	 */
	public static Set<FlowControl> getFC__XON_XOFF_OUT() {
		return EnumSet.of(XON_XOFF_OUT);
	}

	/**
	 * Returns a set for XON/XOFF handshaking in both directions.
	 * 
	 * @return a set with XON_XOFF_IN, XON_XOFF_OUT set.
	 */
	public static Set<FlowControl> getFC_XON_XOFF() {
		return EnumSet.of(XON_XOFF_IN, XON_XOFF_OUT);
	}

	/**
	 * Returns a set for RTS/CTS and XON/XOFF handshaking in both directions.
	 * 
	 * @return a set with RTS_CTS_IN, RTS_CTS_OUT, XON_XOFF_IN, XON_XOFF_OUT set.
	 */
	public static Set<FlowControl> getFC_RTS_CTS_XON_XOFF() {
		return EnumSet.of(RTS_CTS_IN, RTS_CTS_OUT, XON_XOFF_IN, XON_XOFF_OUT);
	}

}
