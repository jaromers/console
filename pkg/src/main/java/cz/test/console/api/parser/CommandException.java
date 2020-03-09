package cz.test.console.api.parser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommandException extends RuntimeException {
	
	public static enum Command {
		QUIT;
	}
	
	private Command command;
	
	public CommandException(Command command) {
		super();
		this.command = command;
	}
		
}
