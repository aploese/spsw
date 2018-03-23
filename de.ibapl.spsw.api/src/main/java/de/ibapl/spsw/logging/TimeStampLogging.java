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

package de.ibapl.spsw.logging;

/**
 * Specifies the way timestamps should be written to the logfile.
 * 
 * @author aploese
 *
 */
public enum TimeStampLogging {
	/**
	 * No timestamps are written. If timestamps are not important, this will give
	 * the most compact logfiles.
	 */
	NONE,
	/**
	 * All timestamps are the time elapsed since the opening of the port.
	 */
	FROM_OPEN,
	/**
	 * All timestamps are absolute times in UTC.
	 */
	UTC,
}
