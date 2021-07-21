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

import java.io.IOException;

/**
 *
 * @author aploese
 */
public interface InOutSpeedConfiguration {

    /**
     *
     * @return the input speed
     * @throws IOException
     */
    Speed getInSpeed() throws IOException;

    /**
     *
     * @return the output speed
     * @throws IOException
     */
    Speed getOutSpeed() throws IOException;

    /**
     *
     * @param speed
     * @throws IOException
     * @throws IllegalArgumentException it the input speed can't be set
     * separately.
     */
    void setInSpeed(Speed speed) throws IOException, IllegalArgumentException;

    /**
     *
     * @param speed
     * @throws IOException
     * @throws IllegalArgumentException it the output speed can't be set
     * separately.
     */
    void setOutSpeed(Speed speed) throws IOException, IllegalArgumentException;

}
