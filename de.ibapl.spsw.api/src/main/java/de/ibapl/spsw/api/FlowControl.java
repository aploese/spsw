package de.ibapl.spsw.api;

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

import java.util.EnumSet;
import java.util.Set;
import org.osgi.annotation.versioning.ProviderType;

/**
 * The flow control encoded as enum constants. Not all OS (POSIX) support
 * separate in/out, whereas other do (Windows). So best set both directions.
 * 
 * @author aploese
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
