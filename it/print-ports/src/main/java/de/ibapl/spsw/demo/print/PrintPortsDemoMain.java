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
package de.ibapl.spsw.demo.print;

import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.SerialPortSocketFactory;
import java.io.IOException;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 *
 * @author aploese
 */
public class PrintPortsDemoMain {

    static private SerialPortSocketFactory getSerialPortSocketFactory() {
        ServiceLoader<SerialPortSocketFactory> serviceLoader = ServiceLoader.load(SerialPortSocketFactory.class);
        Iterator<SerialPortSocketFactory> iterator = serviceLoader.iterator();
        if (!iterator.hasNext()) {
            throw new RuntimeException("No implementation of SerialPortSocket found - Please add an SPSW provider to the class path");
        }
        final SerialPortSocketFactory result = iterator.next();
        if (iterator.hasNext()) {
            throw new RuntimeException("More than one implementation of SerialPortSocket found - Please make sure only one SPSW provider is in the class path");
        }
        return result;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SerialPortSocketFactory spsf = getSerialPortSocketFactory();
        spsf.getPortNames((name, busy) -> {
            System.out.printf("Port found: %s\n", name);
            System.out.printf("\t busy: %s\n", busy);
            if (!busy) {
                try (SerialPortSocket sps = spsf.open(name)) {
                    System.out.printf("\t speed: %s\n", sps.getSpeed());
                    System.out.printf("\t spopbits: %s\n", sps.getStopBits());
                    System.out.printf("\t databits: %s\n", sps.getDatatBits());
                    System.out.printf("\t parity: %s\n", sps.getParity());
                    System.out.printf("\t low control: %s\n", sps.getFlowControl());
                } catch (IOException ioe) {
                    System.out.println("de.ibapl.spsw.demo.ascii.AsciiDemoMain.main(): Ex: "  + ioe);
                }
            }
        });
    }


}
