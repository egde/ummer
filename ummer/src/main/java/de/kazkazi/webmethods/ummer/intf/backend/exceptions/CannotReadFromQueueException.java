package de.kazkazi.webmethods.ummer.intf.backend.exceptions;

public class CannotReadFromQueueException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public CannotReadFromQueueException(Throwable e) {
		super(e);
	}

}
