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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Collections;
import java.util.LinkedList;

import org.junit.jupiter.api.Test;

/**
 *
 * @author Arne Plöse
 *
 */
public class PortnamesComparatorTest {

    @Test
    public void testCompareLinux() {
        PortnamesComparator pnc = new PortnamesComparator();

        assertEquals(-2, "".compareTo("0a"));

        assertEquals(-2, pnc.compare("", "a0"));
        assertEquals(-1, pnc.compare("", "0"));
        assertEquals(-2, pnc.compare("", "0a"));

        assertEquals(-1, pnc.compare("ttyUSB9", "ttyUSB09"));
        assertEquals("ttyUSB09".compareTo("ttyUSB19"), pnc.compare("ttyUSB9", "ttyUSB19"));
        assertEquals("ttyUSB19".compareTo("ttyUSB09"), pnc.compare("ttyUSB19", "ttyUSB9"));

        assertEquals("ttyUSB09.A".compareTo("ttyUSB29.A"), pnc.compare("ttyUSB9.A", "ttyUSB29.A"));
        assertEquals("ttyUSB29.A".compareTo("ttyUSB09.A"), pnc.compare("ttyUSB29.A", "ttyUSB9.A"));

        assertEquals(1, pnc.compare("ttyUSB09B", "ttyUSB0009A"));
        assertEquals(1, pnc.compare("ttyUSB0009B", "ttyUSB009A"));
        assertEquals(1, pnc.compare("ttyUSB0009B", "ttyUSB9A0"));

        assertEquals(2, pnc.compare("ttyUSB000", "ttyUSB0"));

        assertEquals(1, pnc.compare("ttyUSB0", "ttyUSB"));
    }

    @Test
    public void testCompareLinux1() {
        PortnamesComparator pnc = new PortnamesComparator();

        assertEquals(-1, pnc.compare("ttyUSB08", "ttyUSB008"));

        assertEquals(-1, pnc.compare("ttyUSB7", "ttyUSB08"));
        assertEquals(-1, pnc.compare("ttyUSB8", "ttyUSB08"));
        assertEquals(1, pnc.compare("ttyUSB9", "ttyUSB08"));

        assertEquals(1, pnc.compare("ttyUSB08", "ttyUSB7"));
        assertEquals(1, pnc.compare("ttyUSB08", "ttyUSB8"));
        assertEquals(-1, pnc.compare("ttyUSB08", "ttyUSB9"));

        assertEquals(-1, pnc.compare("ttyUSB07", "ttyUSB008"));
        assertEquals(-1, pnc.compare("ttyUSB08", "ttyUSB008"));
        assertEquals(1, pnc.compare("ttyUSB09", "ttyUSB008"));

        assertEquals(1, pnc.compare("ttyUSB008", "ttyUSB07"));
        assertEquals(1, pnc.compare("ttyUSB008", "ttyUSB08"));
        assertEquals(-1, pnc.compare("ttyUSB008", "ttyUSB09"));
    }

    @Test
    public void testCompareLinux2() {
        PortnamesComparator pnc = new PortnamesComparator();
        LinkedList<String> list = new LinkedList<>();
        list.add("USB");
        list.add("USB0");
        list.add("USB1");
        list.add("USB4");
        list.add("USB10");
        list.add("USB12");
        list.add("USB1.A");
        list.add("USB01.A");
        list.add("USB4.A");
        list.add("USB04.B");
        list.add("USB10.A");
        list.add("USB12.A");
        list.add("USB1.B");
        list.add("USB4.B");
        list.add("USB10.B");
        list.add("USB12.B");

        list.sort(pnc);

        java.util.Iterator<String> iter = list.iterator();
        assertEquals("USB", iter.next());
        assertEquals("USB0", iter.next());
        assertEquals("USB1", iter.next());
        assertEquals("USB1.A", iter.next());
        assertEquals("USB01.A", iter.next());
        assertEquals("USB1.B", iter.next());
        assertEquals("USB4", iter.next());
        assertEquals("USB4.A", iter.next());
        assertEquals("USB4.B", iter.next());
        assertEquals("USB04.B", iter.next());
        assertEquals("USB10", iter.next());
        assertEquals("USB10.A", iter.next());
        assertEquals("USB10.B", iter.next());
        assertEquals("USB12", iter.next());
        assertEquals("USB12.A", iter.next());
        assertEquals("USB12.B", iter.next());
        assertFalse(iter.hasNext());
        Collections.reverse(list);
        list.sort(pnc);

        iter = list.iterator();
        assertEquals("USB", iter.next());
        assertEquals("USB0", iter.next());
        assertEquals("USB1", iter.next());
        assertEquals("USB1.A", iter.next());
        assertEquals("USB01.A", iter.next());
        assertEquals("USB1.B", iter.next());
        assertEquals("USB4", iter.next());
        assertEquals("USB4.A", iter.next());
        assertEquals("USB4.B", iter.next());
        assertEquals("USB04.B", iter.next());
        assertEquals("USB10", iter.next());
        assertEquals("USB10.A", iter.next());
        assertEquals("USB10.B", iter.next());
        assertEquals("USB12", iter.next());
        assertEquals("USB12.A", iter.next());
        assertEquals("USB12.B", iter.next());

        /*
		 * for (String s: list) { System.out.println("assertEquals(\"" + s +
		 * "\", iter.next());"); }
         */
    }

}
