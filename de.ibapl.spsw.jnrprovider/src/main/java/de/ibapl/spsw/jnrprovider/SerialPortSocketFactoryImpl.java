/*-
 * #%L
 * SPSW API
 * %%
 * Copyright (C) 2009 - 2018 Arne Plöse
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
package de.ibapl.spsw.jnrprovider;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import javax.inject.Singleton;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.SerialPortSocketFactory;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;

/**
 * Impements the {@linkplain SerialPortSocketFactory}.
 * 
 * @author Arne Plöse
 */
@Singleton
@Component(name = "de.ibapl.spsw.jnrprovider", scope = ServiceScope.SINGLETON, immediate = true)
public class SerialPortSocketFactoryImpl implements SerialPortSocketFactory {

	@Override
	public SerialPortSocket createSerialPortSocket(String portName) {
		return new PosixSerialPortSocket(portName);
	}

	@Override
	public void getPortNames(BiConsumer<String, Boolean> portNameConsumer) {
		// TODO Auto-generated method stub
	}

	@Override
	public List<String> getPortNames(boolean hideBusyPorts) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getPortNames(String portToInclude, boolean hideBusyPorts) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SerialPortSocket open(String portName) throws IOException, IllegalStateException {
		SerialPortSocket result = new PosixSerialPortSocket(portName);
		result.open();
		return result;
	}

	@Override
	public SerialPortSocket open(String portName, Speed speed, DataBits dataBits, StopBits stopBits, Parity parity,
			Set<FlowControl> flowControls) throws IOException {
		SerialPortSocket result = new PosixSerialPortSocket(portName);
		result.open(speed, dataBits, stopBits, parity, flowControls);
		return result;
	}

}
