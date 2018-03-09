package de.ibapl.spsw.tests;

import java.util.Set;

import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.StopBits;

public interface PortConfiguration {
	final static int TEST_TIMEOUT_OFFSET = 600000;

	Baudrate getBaudrate();

	DataBits getDataBits();

	Parity getParity();

	StopBits getStopBits();

	Set<FlowControl> getFlowControl();

	int getInterByteReadTimeout();

	int getOverallWriteTimeout();

	int getOverallReadTimeout();

	int getBufferSize();

	default int calcBitsToTransfer() {
		return 1 + getDataBits().value + (int) Math.round(Math.ceil(getStopBits().value))
		+ (getParity() == Parity.NONE ? 0 : 1);
	}

	default int calcMaxTransferTime() {
		return TEST_TIMEOUT_OFFSET + (getBufferSize() * calcBitsToTransfer() * 1000) / getBaudrate().value;
	}
	
}
