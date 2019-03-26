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
package de.ibapl.spsw.tests;

import de.ibapl.jnhw.libloader.NativeLibResolver;
import de.ibapl.jnhw.libloader.OS;
import java.util.Iterator;
import java.util.Set;

import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;

/**
 * Helper class for iterative tests.
 * 
 * @author Arne Plöse
 *
 */
public class PortConfigurationFactory {

	class PortConfigurationImpl implements PortConfiguration {
		private int bufferSize = 1024;
		private DataBits dataBits = DataBits.DB_8;
		private Set<FlowControl> flowControl = FlowControl.getFC_NONE(); // getFC_RTS_CTS();
		private int interByteReadTimeout = 100;
		private int overallReadTimeout = 2000;
		private int overallWriteTimeout = 2000;
		private Parity parity = Parity.NONE;
		private Speed speed = Speed._9600_BPS;
		private StopBits stopBits = StopBits.SB_1;

		public PortConfigurationImpl() {
		}

		public PortConfigurationImpl(PortConfigurationImpl portConfigurationImpl) {
			this.bufferSize = portConfigurationImpl.bufferSize;
			this.flowControl = portConfigurationImpl.flowControl;
			this.parity = portConfigurationImpl.parity;
			this.stopBits = portConfigurationImpl.stopBits;
			this.dataBits = portConfigurationImpl.dataBits;
			this.speed = portConfigurationImpl.speed;
			this.interByteReadTimeout = portConfigurationImpl.interByteReadTimeout;
			this.overallReadTimeout = portConfigurationImpl.overallReadTimeout;
			this.overallWriteTimeout = portConfigurationImpl.overallWriteTimeout;
		}

		public void adjustTimeouts() {
			overallReadTimeout = calcMaxTransferTime();
			overallWriteTimeout = overallReadTimeout;
		}

		@Override
		public int getBufferSize() {
			return bufferSize;
		}

		@Override
		public DataBits getDataBits() {
			return dataBits;
		}

		@Override
		public Set<FlowControl> getFlowControl() {
			return flowControl;
		}

		@Override
		public int getInterByteReadTimeout() {
			return interByteReadTimeout;
		}

		@Override
		public int getOverallReadTimeout() {
			return overallReadTimeout;
		}

		@Override
		public int getOverallWriteTimeout() {
			return overallWriteTimeout;
		}

		@Override
		public Parity getParity() {
			return parity;
		}

		@Override
		public Speed getSpeed() {
			return speed;
		}

		@Override
		public StopBits getStopBits() {
			return stopBits;
		}

		@Override
		public String toString() {
			return String.format("Port Configuration %s, %s, %s, %s, fC: %s, iBTO: %d, oRTO: %d, oWTO: %d, bS: %d",
					speed, dataBits, stopBits, parity, flowControl, interByteReadTimeout, overallReadTimeout,
					overallWriteTimeout, bufferSize);
		}

	}

	private PortConfigurationImpl portConfigurationImpl = new PortConfigurationImpl();

	public Iterator<PortConfiguration> getParityIterator() {
		return new Iterator<PortConfiguration>() {

			int currentIndex = 0;
			Parity parities[] = Parity.values();

			@Override
			public boolean hasNext() {
                            //FreeBSD and macos have no Parity Mark and Space
                            while (currentIndex < parities.length) {
                                Parity parity = parities[currentIndex];
                                if ((parity == Parity.MARK || parity == Parity.MARK) && 
                                    (NativeLibResolver.getOS() == OS.FREE_BSD || NativeLibResolver.getOS() == OS.MAC_OS_X)) {
                                currentIndex++; 
                            } else {
                                    return true;
                                }
                            }
                            return false;
			}

			@Override
			public PortConfiguration next() {
				return of(parities[currentIndex++]);
			}

		};
	}

	public Iterator<PortConfiguration> getSpeedIterator(final Speed first, final Speed last) {
		if (last.ordinal() < first.ordinal()) {
			throw new IllegalArgumentException("Last must be greater than first speed");
		}
		return new Iterator<PortConfiguration>() {

			int currentIndex = first.ordinal();
			int lastIndex = last.ordinal();
			Speed speeds[] = Speed.values();

			@Override
			public boolean hasNext() {
				return currentIndex <= lastIndex;
			}

			@Override
			public PortConfiguration next() {
				return of(speeds[currentIndex++]);
			}
		};

	}

	public PortConfiguration of(Parity parity) {
		final PortConfigurationImpl result = new PortConfigurationImpl(portConfigurationImpl);
		result.parity = parity;
		result.adjustTimeouts();
		return result;
	}

	public PortConfiguration of(Speed speed) {
		final PortConfigurationImpl result = new PortConfigurationImpl(portConfigurationImpl);
		result.speed = speed;
		result.adjustTimeouts();
		return result;
	}

	public PortConfiguration ofBuffersize(int bufferSize) {
		final PortConfigurationImpl result = new PortConfigurationImpl(portConfigurationImpl);
		result.bufferSize = bufferSize;
		result.adjustTimeouts();
		return result;
	}

	public PortConfiguration ofCurrent() {
		final PortConfigurationImpl result = new PortConfigurationImpl(portConfigurationImpl);
		result.adjustTimeouts();
		return result;
	}

	public PortConfigurationFactory setBuffersize(int bufferSize) {
		portConfigurationImpl.bufferSize = bufferSize;
		return this;
	}

	public PortConfigurationFactory setDataBits(DataBits dataBits) {
		portConfigurationImpl.dataBits = dataBits;
		return this;
	}

	public PortConfigurationFactory setFlowControl(Set<FlowControl> flowControl) {
		portConfigurationImpl.flowControl = flowControl;
		return this;
	}

	public PortConfigurationFactory setParity(Parity parity) {
            if ((parity == Parity.MARK || parity == Parity.MARK) && 
                                    (NativeLibResolver.getOS() == OS.FREE_BSD || NativeLibResolver.getOS() == OS.MAC_OS_X)) {
                throw new IllegalArgumentException("Parity MARK and SPACE are not supported under MAcOS and FreeBSD");
            }
		portConfigurationImpl.parity = parity;
		return this;
	}

	public PortConfigurationFactory setSpeed(Speed speed) {
		portConfigurationImpl.speed = speed;
		return this;
	}

	public PortConfigurationFactory setStopBits(StopBits stopBits) {
		portConfigurationImpl.stopBits = stopBits;
		return this;
	}

}
