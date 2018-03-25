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
 * The speed in bit/s to set.
 * The speed is commonly known as baud rate, but baud rate is the symbol rate and not the speed.
 *  
 * 
 * Here speed means bit/s to transfer a character(5,6,7,8<nb>data<nb>bits) with one
 * start bit, one or none parity bit and one, one and a half or two stop bits.
 * 
 * This enum holds the values which should be supported by POSIX and Windows.
 * 
 * @author aploese
 */
@ProviderType
public enum Speed {

    _0_BPS(0),
    _50_BPS(50),
    _75_BPS(75),
    _110_BPS(110),
    _134_BPS(134),
    _150_BPS(150),
    _200_BPS(200),
    _300_BPS(300),
    _600_BPS(600),
    _1200_BPS(1200),
    _1800_BPS(1800),
    _2400_BPS(2400),
    _4800_BPS(4800),
    _9600_BPS(9600),
    _19200_BPS(19200),
    _38400_BPS(38400),
    _57600_BPS(57600),
    _115200_BPS(115200),
    _230400_BPS(230400),
    _460800_BPS(460800),
    _500000_BPS(500000),
    _576000_BPS(576000),
    _921600_BPS(921600),
    _1000000_BPS(1000000),
    _1152000_BPS(1152000),
    _1500000_BPS(1500000),
    _2000000_BPS(2000000),
    _2500000_BPS(2500000),
    _3000000_BPS(3000000),
    _3500000_BPS(3500000),
    _4000000_BPS(4000000);

    public static Speed fromValue(int baudrate) {
        return fromNative(baudrate);
    }

    public static Speed fromNative(int baudrate) {
        switch (baudrate) {
            case 0:
                return _0_BPS;
            case 50:
                return _50_BPS;
            case 75:
                return _75_BPS;
            case 110:
                return _110_BPS;
            case 134:
                return _134_BPS;
            case 150:
                return _150_BPS;
            case 200:
                return _200_BPS;
            case 300:
                return _300_BPS;
            case 600:
                return _600_BPS;
            case 1200:
                return _1200_BPS;
            case 1800:
                return _1800_BPS;
            case 2400:
                return _2400_BPS;
            case 4800:
                return _4800_BPS;
            case 9600:
                return _9600_BPS;
            case 19200:
                return _19200_BPS;
            case 38400:
                return _38400_BPS;
            case 57600:
                return _57600_BPS;
            case 115200:
                return _115200_BPS;
            case 230400:
                return _230400_BPS;
            case 460800:
                return _460800_BPS;
            case 500000:
                return _500000_BPS;
            case 576000:
                return _576000_BPS;
            case 921600:
                return _921600_BPS;
            case 1000000:
                return _1000000_BPS;
            case 1152000:
                return _1152000_BPS;
            case 1500000:
                return _1500000_BPS;
            case 2000000:
                return _2000000_BPS;
            case 2500000:
                return _2500000_BPS;
            case 3000000:
                return _3000000_BPS;
            case 3500000:
                return _3500000_BPS;
            case 4000000:
                return _4000000_BPS;
            default:
                throw new IllegalArgumentException("Non standard baudrate: " + baudrate);
        }
    }

    /**
     * The speed a numerical value in bit/s.
     */
    public final int value;

    /**
     * 
     * @param speed in bit/s
     */
    private Speed(int speed) {
        this.value = speed;
    }

}
