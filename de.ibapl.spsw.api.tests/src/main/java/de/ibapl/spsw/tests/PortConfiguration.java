package de.ibapl.spsw.tests;

import java.util.Set;

import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.StopBits;

public interface PortConfiguration {
	final static int TEST_TIMEOUT_OFFSET = 1000;

	Baudrate getBaudrate();

	DataBits getDataBits();

	Parity getParity();

	StopBits getStopBits();

	Set<FlowControl> getFlowControl();

	int getInterByteReadTimeout();

	int getOverallWriteTimeout();

	int getOverallReadTimeout();

	int getBufferSize();

	default int calcMaxTransferTime() {
		return TEST_TIMEOUT_OFFSET + SerialPortSocket.calculateMillisForBytes(getBufferSize(), getBaudrate(), getDataBits(), getStopBits(), getParity());
	}

}
