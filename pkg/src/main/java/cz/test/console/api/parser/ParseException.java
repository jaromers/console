package cz.test.console.api.parser;

public class ParseException extends RuntimeException {
	
	public ParseException() {}
	
	public ParseException(String message) {
		super(message);
	}
	
	public ParseException(Throwable e) {
		super(e);
	}
	
	public ParseException(String message, Throwable e) {
		super(message, e);
	}
}
