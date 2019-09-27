/*
 * SPSW - Drivers for the serial port, https://github.com/aploese/spsw/
 * Copyright (C) 2009-2019, Arne Plöse and individual contributors as indicated
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
package de.ibapl.spsw.mock;

import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.SerialPortSocketFactory;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;
import static de.ibapl.spsw.mock.MockSerialPortSocket.ascii2Bytes;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 *
 * @author aploese
 */
public class MockSerialPortFactory implements SerialPortSocketFactory {

    private LinkedList<MockRequest<?>> data = new LinkedList<>();

    @Override
    public SerialPortSocket open(String portName) throws IOException {
        return new MockSerialPortSocket(this, portName);
    }

    @Override
    public SerialPortSocket open(String portName, Speed speed, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void getPortNames(BiConsumer<String, Boolean> portNameConsumer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getPortNames(boolean hideBusyPorts) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getPortNames(String portToInclude, boolean hideBusyPorts) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public boolean allRequestsHandled() {
        return data.isEmpty();
    }

    public void addRequest(MockRequest<?> d) {
        data.add(d);
    }

    public void addRequest(String writeData, IOException readIOException, int times) {
        MockDataRequest write = new MockDataRequest(ascii2Bytes(writeData), MockRequestType.WRITE);
        MockExceptionRequest read = new MockExceptionRequest(readIOException, MockRequestType.READ);
        for (int i = 0; i < times; i++) {
            addRequest(write);
            addRequest(read);
        }
    }

    public void addRequest(String writeData, String readData) {
        addRequest(new MockDataRequest(ascii2Bytes(writeData), MockRequestType.WRITE));
        addRequest(new MockDataRequest(ascii2Bytes(readData), MockRequestType.READ));
    }

    public void expectedRead(IOException ioException) {
        addRequest(new MockExceptionRequest(ioException, MockRequestType.READ));
    }

    public void expectedRead(String data) {
        addRequest(new MockDataRequest(ascii2Bytes(data), MockRequestType.READ));
    }

    public void expectedWrite(IOException ioException) {
        addRequest(new MockExceptionRequest(ioException, MockRequestType.WRITE));
    }

    public void expectedWrite(String data) {
        addRequest(new MockDataRequest(ascii2Bytes(data), MockRequestType.WRITE));
    }

    public void expectedWrite(String data, int times) {
        MockDataRequest r = new MockDataRequest(ascii2Bytes(data), MockRequestType.WRITE);
        for (int i = 0; i < times; i++) {
            addRequest(r);
        }
    }

    boolean isDataEmpty() {
        return data.isEmpty();
    }

    MockRequest<?> getDataFirst() {
        return data.getFirst();
    }

    MockRequest<?>  removeDataFirst() {
        return data.removeFirst();
    }


}
