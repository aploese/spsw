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
package de.ibapl.nativeutils.mips;

import de.ibapl.nativeutils.EFlags;

/**
 * 
 * @author Arne Plöse
 *
 */
public enum MipsEFlags implements EFlags {

	EF_MIPS_ARCH_1, EF_MIPS_ARCH_2, EF_MIPS_ARCH_3, EF_MIPS_ARCH_4, EF_MIPS_ARCH_5, EF_MIPS_ARCH_32, EF_MIPS_ARCH_64, EF_MIPS_ARCH_32R2, EF_MIPS_ARCH_64R2,

	EF_MIPS_ABI_O32, EF_MIPS_ABI_O64, EF_MIPS_NOREORDER, EF_MIPS_PIC, EF_MIPS_CPIC, EF_MIPS_ABI2, EF_MIPS_OPTIONS_FIRST, EF_MIPS_32BITMODE, EF_MIPS_FP64, EF_MIPS_NAN2008;
}
