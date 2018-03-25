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

import org.osgi.annotation.versioning.ProviderType;

/**
 * The number of stopbits to use.
 * Valid combinations are one stopbit for any number of databits.
 * One and a halve stopbit for 5 databits.
 * And two stop bits for 6,7 and 8 databits.
 * @author aploese
 */
@ProviderType
public enum StopBits {

	/**
	 * One stop bit.
     * Valid for 5,6,7,8 data bits.
	 */
    SB_1(1),
    /**
     * One and a half stop bit.
     * Only valid for 5 data bits.
     */
    SB_1_5(1.5f),
    /**
     * Two stop bits.
     * Only valid for 6,7,8 data bits.
     */
    SB_2(2);

	/**
	 * The number of bits.
	 */
    public final float value;

    private StopBits(float value) {
        this.value = value;
    }

}
