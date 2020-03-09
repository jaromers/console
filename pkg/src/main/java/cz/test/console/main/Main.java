package cz.test.console.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
import cz.test.console.terminal.PkgTerminal;

/**
 * Main class, assemble the application.
 * 
 * @author jarom
 *
 */
public class Main {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		// Parse input
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
        
        // setup the app.
        setupApp(inputFileName, feesFileName);
	}

	private static void setupApp(String inputFileName, String feesFileName) throws IOException, InterruptedException {
		// Prepare parser.
		final ParserAndFormatter parserAndFormatter = new ParserAndFormatterImpl();
		
		// Read fees configuration
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
		
		// Prepare the terminal handling modifications for simultaneous input and output (especially on windows terminals).
		PkgTerminal terminal = new PkgTerminal();
		
		// Configure the items processor
		EntryProcessorBuilder builder = new EntryProcessorBuilderImpl();
		if (feeEntries != null) {
			builder.setFees(feeEntries);
		}
		builder.setOutputInterval(10000);
		
		// How the statistics output should be handled / processed.
		builder.setOutputProcessor(entries -> {
			StringBuffer sb = new StringBuffer();
			if (feesPresent) {
				entries.forEach(entry -> sb.append(parserAndFormatter.outputEntryWithFee(entry)).append("\n"));
			} else {
				entries.forEach(entry -> sb.append(parserAndFormatter.outputEntryWithoutFee(entry)).append("\n"));
			}
			terminal.write("" + System.currentTimeMillis() + "\n" + sb.toString());
		});
		EntryProcessor processor = builder.build();		
		
		// Process input file if present.
		if (inputFileName != null) {
			BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
			try {
				String line = null;
				while ((line = reader.readLine()) != null) {
					processor.processInputEntry(parserAndFormatter.parseInputEntry(line));
				}
			} finally {
				reader.close();
			}
		}
		
		// Start processing data from console
		terminal.setLineConsumer((term, line) -> {
			try {
				InputEntry inputEntry = parserAndFormatter.parseInputEntry(line);
				processor.processInputEntry(inputEntry);
			} catch (cz.test.console.api.parser.ParseException e) {
				System.out.println(e.getMessage());
			} catch (CommandException e) {
				if(CommandException.Command.QUIT.equals(e.getCommand())) {
					terminal.close();
				}
			}
		});
		
		terminal.startReading();
		
		processor.close();
		System.exit(0);
	}

}
