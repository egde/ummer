package de.kazkazi.webmethods.ummer.intf.backend.exceptions;

public class CannotReadFromChannelException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public CannotReadFromChannelException(Throwable e) {
		super(e);
	}
}
