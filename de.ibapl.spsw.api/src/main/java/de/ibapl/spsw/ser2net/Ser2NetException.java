package de.ibapl.spsw.ser2net;

import de.ibapl.spsw.api.SerialPortException;

public class Ser2NetException extends SerialPortException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 392370398223050360L;

	public Ser2NetException(String message) {
		super(message);
	}

}
