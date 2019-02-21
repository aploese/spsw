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
 * The parity encoded as enum constatnts.
 * 
 * @author Arne Plöse
 */
@ProviderType
public enum Parity {

	/**
	 * No parity, no parity bit will be transmitted.
	 */
	NONE,
	/**
	 * Parity is calculated, that the number of 1 data bits and the the parity bit
	 * is odd.
	 */
	ODD,
	/**
	 * Parity is calculated, that the number of 1 data bits and the parity bit is
	 * even.
	 */
	EVEN,
	/**
	 * 
	 * No parity, the parity bit is always the mark signal condition (logical 1).
	 */
	MARK,
	/**
	 * No parity, the parity bit is always the space signal condition (logical 0).
	 */
	SPACE;

}
