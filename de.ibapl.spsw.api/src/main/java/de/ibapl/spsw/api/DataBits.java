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
