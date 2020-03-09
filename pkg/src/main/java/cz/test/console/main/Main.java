package cz.test.console.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cz.test.console.api.parser.CommandException;
import cz.test.console.api.parser.ParserAndFormatter;
import cz.test.console.api.processor.EntryProcessor;
import cz.test.console.api.processor.EntryProcessorBuilder;
import cz.test.console.impl.parser.ParserAndFormatterImpl;
import cz.test.console.impl.processor.EntryProcessorBuilderImpl;
import cz.test.console.model.FeeEntry;
import cz.test.console.model.InputEntry;

public class Main {
	
	public static void main(String[] args) throws IOException {
		
		Options options = new Options();
		
		Option inputFile = new Option("i", "input", true, "input file path");
		inputFile.setRequired(false);
		options.addOption(inputFile);
		
		Option feesFile = new Option("f", "fees", true, "input fees file path");
		feesFile.setRequired(false);
		options.addOption(feesFile);
		
		CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("java cz.test.console.main.Main ", options);

            System.exit(1);
        }
        
        String inputFileName = cmd.getOptionValue('i');
        String feesFileName = cmd.getOptionValue('f');
        
        setupApp(inputFileName, feesFileName);
	}

	private static void setupApp(String inputFileName, String feesFileName) throws IOException {
		final ParserAndFormatter parserAndFormatter = new ParserAndFormatterImpl();
		List<FeeEntry> feeEntries = null;
		if (feesFileName != null) {
			BufferedReader reader = new BufferedReader(new FileReader(feesFileName)); 
			String line = null;
			feeEntries = new ArrayList<>();
			while((line = reader.readLine()) != null) {
				feeEntries.add(parserAndFormatter.parseFeeEntry(line));
			}
			reader.close();
		}
		final boolean feesPresent = feeEntries != null;
		
		EntryProcessorBuilder builder = new EntryProcessorBuilderImpl();
		if (feeEntries != null) {
			builder.setFees(feeEntries);
		}
		builder.setOutputProcessor(entries -> {
			entries.forEach(entry -> {
				System.out.println(feesPresent ? parserAndFormatter.outputEntryWithFee(entry) : parserAndFormatter.outputEntryWithoutFee(entry));
			});
		});
		EntryProcessor processor = builder.build();
		
		// Process input file if present.
		if (inputFileName != null) {
			BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
			String line = null;
			while ((line = reader.readLine()) != null) {
				processor.processInputEntry(parserAndFormatter.parseInputEntry(line));
			}
		}
		
		// Start processing data from console
		BufferedReader reader =  new BufferedReader(new InputStreamReader(System.in)); 
		String line = null;
		boolean stop = false;
		
		while ((line = reader.readLine()) != null && !stop) {
			try {
				InputEntry inputEntry = parserAndFormatter.parseInputEntry(line);
				processor.processInputEntry(inputEntry);
			} catch (cz.test.console.api.parser.ParseException e) {
				System.out.println(e.getMessage());
			} catch (CommandException e) {
				if(CommandException.Command.QUIT.equals(e.getCommand())) {
					stop = true;
				}
			}
		}
		
		processor.close();
		System.exit(0);
	}

}
