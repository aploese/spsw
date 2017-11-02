package de.ibapl.spsw.api;

import java.io.InterruptedIOException;

public class TimeoutIOException extends InterruptedIOException {

	public TimeoutIOException(String message) {
		super(message);
	}

	public TimeoutIOException() {
	}

}
