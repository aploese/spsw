package de.ibapl.spsw.tests;

import java.util.Iterator;
import java.util.Set;

import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.StopBits;

public class PortConfigurationFactory {
	
	
	class PortConfigurationImpl implements PortConfiguration, Cloneable {
		private int bufferSize = 1024;
		private Set<FlowControl> flowControl = FlowControl.getFC_NONE(); // getFC_RTS_CTS();
		private Parity parity = Parity.NONE;
		private StopBits stopBits = StopBits.SB_1;
		private DataBits dataBits = DataBits.DB_8;
		private Baudrate baudrate = Baudrate.B9600;
		private int interByteReadTimeout = 100;
		private int overallReadTimeout = 2000;
		private int overallWriteTimeout = 2000;
		
		@Override
		public PortConfigurationImpl clone() {
			try {
				return (PortConfigurationImpl)super.clone();
			}catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		public Baudrate getBaudrate() {
			return baudrate;
		}

		@Override
		public DataBits getDataBits() {
			return dataBits;
		}

		@Override
		public Parity getParity() {
			return parity;
		}

		@Override
		public StopBits getStopBits() {
			return stopBits;
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
		public int getOverallWriteTimeout() {
			return overallWriteTimeout;
		}

		@Override
		public int getOverallReadTimeout() {
			return overallReadTimeout;
		}

		@Override
		public int getBufferSize() {
			return bufferSize;
		}
		
		@Override
		public String toString() {
			return String.format("Port Configuration %s, %s, %s, %s, fC: %s, iBTO: %d, oRTO: %d, oWTO: %d, bS: %d", baudrate, dataBits, stopBits, parity, flowControl, interByteReadTimeout, overallReadTimeout, overallWriteTimeout, bufferSize);
		}

	}
	
	//TODO change this to first|current|last|values of each enum ...
	private PortConfigurationImpl portConfigurationImpl = new PortConfigurationImpl();

	public PortConfiguration of(Baudrate b) {
		PortConfigurationImpl result = portConfigurationImpl.clone();
		result.baudrate = b;
		return result;
	}
	
	public PortConfiguration of(Parity parity) {
		PortConfigurationImpl result = portConfigurationImpl.clone();
		result.parity = parity;
		return result;
	}
	
	public PortConfiguration ofBuffersize(int bufferSize, boolean adjustTimeouts) {
		PortConfigurationImpl result = portConfigurationImpl.clone();
		result.bufferSize = bufferSize;
		result.overallReadTimeout = result.calcMaxTransferTime();
		result.overallWriteTimeout = result.overallReadTimeout;
		return result;
	}

	public Iterator<PortConfiguration> getBaudrateIterator(final Baudrate first, final Baudrate last) {
		if (last.ordinal() < first.ordinal()) {
        	throw new IllegalArgumentException("Last must be greater than first baudrate");
        }
		return new Iterator<PortConfiguration>() {

        	int currentIndex = first.ordinal();
        	Baudrate baudrates[] = Baudrate.values();
        	int lastIndex = last.ordinal();
        	
            @Override
            public boolean hasNext() {
                return currentIndex <= lastIndex;
            }

            @Override
            public PortConfiguration next() {
            	return of(baudrates[currentIndex++]);
            }
        };

	}

	public PortConfigurationFactory setBuffersize(int bufferSize) {
		portConfigurationImpl.bufferSize = bufferSize;
		return this;
	}

	public PortConfigurationFactory setBaudrate(Baudrate baudrate) {
		portConfigurationImpl.baudrate = baudrate;
		return this;
	}

	public PortConfigurationFactory setFlowControl(Set<FlowControl> flowControl) {
		portConfigurationImpl.flowControl = flowControl;
		return this;
	}

	public Iterator<PortConfiguration> getParityIterator() {
		return new Iterator<PortConfiguration>() {

			int currentIndex = 0;
        	Parity parities[] = Parity.values();
        	
            @Override
            public boolean hasNext() {
                return currentIndex < parities.length;
            }

            @Override
            public PortConfiguration next() {
            	return of(parities[currentIndex++]);
            }

        };
	}

}
