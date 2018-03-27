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
