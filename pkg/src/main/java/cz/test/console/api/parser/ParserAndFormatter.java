package cz.test.console.api.parser;

import cz.test.console.model.FeeEntry;
import cz.test.console.model.InputEntry;
import cz.test.console.model.OutputEntry;

public interface ParserAndFormatter {
	
	public InputEntry parseInputEntry(String line) throws ParseException, CommandException;
	
	public FeeEntry parseFeeEntry(String line) throws ParseException;
	
	public String outputEntryWithFee(OutputEntry outputEntry);
	
	public String outputEntryWithoutFee(OutputEntry outputEntry);
	
}
