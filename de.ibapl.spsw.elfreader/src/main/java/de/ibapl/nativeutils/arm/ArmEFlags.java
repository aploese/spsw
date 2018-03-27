/*-
 * #%L
 * SPSW Provider
 * %%
 * Copyright (C) 2009 - 2018 Arne Plöse
 * %%
 * SPSW - Drivers for the serial port, https://github.com/aploese/spsw/
 * Copyright (C) 2009-2018, Arne Plöse and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
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
 * #L%
 */
package de.ibapl.nativeutils.arm;

import de.ibapl.nativeutils.EFlags;

/**
 *
 * @author Arne Plöse
 */
public enum ArmEFlags implements EFlags {

	EF_ARM_EABI_UNKNOWN, EF_ARM_EABI_VER1, EF_ARM_EABI_VER2, EF_ARM_EABI_VER3, EF_ARM_EABI_VER4, EF_ARM_EABI_VER5, EF_ARM_BE8, EF_ARM_LE8, EF_ARM_MAVERICK_FLOAT, EF_ARM_VFP_FLOAT, EF_ARM_SOFT_FLOAT, EF_ARM_OLD_ABI, EF_ARM_NEW_ABI, EF_ARM_ALIGN8, EF_ARM_PIC, EF_ARM_MAPSYMSFIRST, EF_ARM_APCS_FLOAT, EF_ARM_DYNSYMSUSESEGIDX, EF_ARM_APCS_26, EF_ARM_SYMSARESORTED, EF_ARM_INTERWORK, EF_ARM_HASENTRY, EF_ARM_RELEXEC;

}
