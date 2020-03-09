package cz.test.console.impl.processor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import cz.test.console.api.processor.EntryProcessor;
import cz.test.console.impl.processor.EntryProcessorBuilderImpl;
import cz.test.console.model.FeeEntry;
import cz.test.console.model.InputEntry;
import cz.test.console.model.OutputEntry;
import io.reactivex.functions.Consumer;

public class EntryProcessorImplTest {

	@Test
	public void testProcessDataWithoutFees() throws InterruptedException, IOException {
		
		StringBuilder output = new StringBuilder();
		
		EntryProcessor entryProcessor = new EntryProcessorBuilderImpl()
							.setOutputInterval(1000)
							.setOutputProcessor(printOutputWithoutFees(output))
							.build();
		
		entryProcessor.processInputEntry(new InputEntry("12345", 12));
		Thread.sleep(200);
		
		entryProcessor.processInputEntry(new InputEntry("23456", 15.3));
		Thread.sleep(200);
		
		entryProcessor.processInputEntry(new InputEntry("12345", 2.3));
		Thread.sleep(3000);

		entryProcessor.processInputEntry(new InputEntry("23456", 2.5));
		Thread.sleep(2000);
		
		entryProcessor.close();
		
		String result = output.toString();		
		
		String loadedFile = loadFile("/expectedProcessorOutputWithoutFees.txt");
		
		Assert.assertEquals(loadedFile, result);
	}
	
	@Test
	public void testProcessDataWithFees() throws InterruptedException, IOException {
		
		StringBuilder output = new StringBuilder();
		
		List<FeeEntry> fees = Arrays.asList(
					new FeeEntry(10, 5),
					new FeeEntry(5, 2.5),
					new FeeEntry(3, 2),
					new FeeEntry(2, 1.5),
					new FeeEntry(1, 1),
					new FeeEntry(0.5, 0.7),
					new FeeEntry(0.2, 0.5),
					new FeeEntry(0, 0.25)
				);
		EntryProcessor entryProcessor = new EntryProcessorBuilderImpl()
							.setOutputInterval(1000)
							.setFees(fees)
							.setOutputProcessor(printOutputWithFees(output))
							.build();
		
		entryProcessor.processInputEntry(new InputEntry("12345", 12)); 		// fees 5
		Thread.sleep(200);
		
		entryProcessor.processInputEntry(new InputEntry("23456", 15.3));	// fees 5
		Thread.sleep(200);
		
		entryProcessor.processInputEntry(new InputEntry("12345", 2.3));     // fees 1.5
		Thread.sleep(3000);

		entryProcessor.processInputEntry(new InputEntry("23456", 2.5));     // fees 1.5
		Thread.sleep(2000);
		
		entryProcessor.close();
		
		String result = output.toString();
		
		String loadedFile = loadFile("/expectedProcessorOutputWithFees.txt");
				
		Assert.assertEquals(loadedFile, result);
	}

	private Consumer<List<OutputEntry>> printOutputWithoutFees(StringBuilder output) {
		return outputEntries -> {
			output.append("----------------------\n");
			outputEntries.forEach(entry -> output
					.append(entry.getZipCode())
					.append(" ")
					.append(entry.getWeight())
					.append("\n"));
		};
	}
	
	private Consumer<List<OutputEntry>> printOutputWithFees(StringBuilder output) {
		return outputEntries -> {
			output.append("----------------------\n");
			outputEntries.forEach(entry -> output
					.append(entry.getZipCode())
					.append(" ")
					.append(entry.getWeight())
					.append(" ")
					.append(entry.getTotalFee())
					.append("\n"));
		};
	}
	
	private String loadFile(String path) throws IOException {
		return IOUtils.toString(this.getClass().getResourceAsStream(path), "UTF-8");
	}
}
