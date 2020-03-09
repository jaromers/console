package cz.test.console.impl.parser;

import org.junit.Assert;
import org.junit.Test;

import cz.test.console.api.parser.ParseException;
import cz.test.console.api.parser.ParserAndFormatter;
import cz.test.console.model.FeeEntry;
import cz.test.console.model.InputEntry;
import cz.test.console.model.OutputEntry;

public class ParserAndFormatterImplTest {

	private ParserAndFormatter parserFormatter = new ParserAndFormatterImpl();
	
	@Test(expected = ParseException.class)
	public void testParseInputEntry() {
		InputEntry input = parserFormatter.parseInputEntry("12345 3.23");
		Assert.assertEquals(input.getZipCode(), "12345");
		Assert.assertEquals(input.getWeight(), 3.23, 0.001);
		
		input = parserFormatter.parseInputEntry("12345 3.23 d");
	}
	
	@Test(expected = ParseException.class)
	public void testParseFeeEntry() {
		FeeEntry input = parserFormatter.parseFeeEntry("10 2.50");
		Assert.assertEquals(input.getWeight(), 10, 0.001);
		Assert.assertEquals(input.getFee(), 2.5, 0.001);
		input = parserFormatter.parseFeeEntry("12345 ax");
	}
	
	@Test
	public void testOutputEntryWithFee() {
		OutputEntry outputEntry = new OutputEntry("12345", 15.2, 1.5);
		String output = parserFormatter.outputEntryWithFee(outputEntry);
		Assert.assertEquals(output, "12345 15.200 1.50");
	}
	
	@Test
	public void testOutputEntryWithoutFee() {
		OutputEntry outputEntry = new OutputEntry("12345", 15.2, 1.5);
		String output = parserFormatter.outputEntryWithoutFee(outputEntry);
		Assert.assertEquals(output, "12345 15.200");
	}
}
