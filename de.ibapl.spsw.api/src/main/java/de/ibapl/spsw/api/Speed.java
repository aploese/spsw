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

import org.osgi.annotation.versioning.ProviderType;

/**
 * The speed in bit/s to set. The speed is commonly known as baud rate, but baud
 * rate is the symbol rate and not the speed.
 *
 *
 * Here speed means bit/s to transfer a character(5,6,7,8<nb>data<nb>bits) with
 * one start bit, one or none parity bit and one, one and a half or two stop
 * bits.
 *
 * This enum holds the values which should be supported by POSIX and Windows.
 *
 * @author Arne Plöse
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


    /**
     *
     * @param speed in bit/s
     */
    public static Speed fromNative(int speed) {
        switch (speed) {
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
                throw new IllegalArgumentException("Non standard speed: " + speed);
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
