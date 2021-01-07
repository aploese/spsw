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
package de.ibapl.spsw.demo.stram.read;

import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.SerialPortSocketFactory;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.api.TimeoutIOException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.AsynchronousCloseException;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 *
 * @author aploese
 */
public class StreamReadDemoMain {

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
     * @param args the only arg acceptes is the portname
     */
    public static void main(String[] args) {
        final SerialPortSocketFactory spsf = getSerialPortSocketFactory();

        InputStream is;

        try {
            SerialPortSocket sps = spsf.open(args[0], Speed._9600_BPS, DataBits.DB_8, StopBits.SB_1, Parity.NONE, FlowControl.getFC_NONE());
            sps.setTimeouts(100, 0, 0);
            is = sps.getInputStream();
        } catch (IOException ioe) {
            System.out.println("de.ibapl.spsw.demo.asciidemo.StreamReadDemoMain.main() Ex: " + ioe);
            return;
        }

        while (true) {
            try {
                int value = is.read();
                if (value < 0) {
                    //EOF reached so stop receiving
                    break;
                }
                System.out.append((char) value);
                System.out.flush();
            } catch (AsynchronousCloseException ace) {
            } catch (TimeoutIOException tioe) {
            } catch (IOException ioe) {
            }

        }
    }

}
