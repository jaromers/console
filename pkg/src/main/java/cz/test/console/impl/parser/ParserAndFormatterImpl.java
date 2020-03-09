package cz.test.console.impl.parser;

import java.util.Locale;
import java.util.Scanner;

import cz.test.console.api.parser.CommandException;
import cz.test.console.api.parser.ParseException;
import cz.test.console.api.parser.ParserAndFormatter;
import cz.test.console.model.FeeEntry;
import cz.test.console.model.InputEntry;
import cz.test.console.model.OutputEntry;

public class ParserAndFormatterImpl implements ParserAndFormatter {

	@Override
	public InputEntry parseInputEntry(String line) throws ParseException, CommandException {
		Scanner scanner = new Scanner(line);
		try {
			scanner.useLocale(Locale.US);
			if (scanner.hasNextFloat()) {
				float weight = scanner.nextFloat();
				if (scanner.hasNext("\\d{5}")) {
					String zipCode = scanner.next("\\d{5}");
					if (!scanner.hasNext()) {
						return new InputEntry(zipCode, weight);
					} else {
						throw new ParseException("Unexpected input at the end of input line");
					}
				} else {
					throw new ParseException("Unexpected input following zip code");
				}
			} else if (scanner.hasNext("quit")) {
				throw new CommandException(CommandException.Command.QUIT);
			}
		} finally {
			scanner.close();
		}
		throw new ParseException("Unexpected input at the start of input line");
	}

	@Override
	public FeeEntry parseFeeEntry(String line) throws ParseException {
		Scanner scanner = new Scanner(line);
		try {
			scanner.useLocale(Locale.US);
			if (scanner.hasNextFloat()) {
				float weight = scanner.nextFloat();
				if (scanner.hasNextFloat()) {
					float fee = scanner.nextFloat();
					if (!scanner.hasNext()) {
						return new FeeEntry(weight, fee);
					} else {
						throw new ParseException("Unexpected input at the end of input line");
					}
				} else {
					throw new ParseException("Unexpected input after weight");
				}
			} else {
				throw new ParseException("Unexpected input at the start of input line");
			}
		} finally {
			scanner.close();
		}
	}

	@Override
	public String outputEntryWithoutFee(OutputEntry outputEntry) {
		return String.format(Locale.US, "%-5s %.3f", outputEntry.getZipCode(), outputEntry.getWeight());
	}

	@Override
	public String outputEntryWithFee(OutputEntry outputEntry) {
		return String.format(Locale.US, "%-5s %.3f %.2f", outputEntry.getZipCode(), outputEntry.getWeight(), outputEntry.getTotalFee());
	}

}
